/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2.universe.gameserver.model.entity.clanhall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;

import javolution.util.FastList;
import javolution.util.FastMap;

import l2.universe.Config;
import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.Announcements;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.datatables.ClanTable;
import l2.universe.gameserver.datatables.DoorTable;
import l2.universe.gameserver.datatables.NpcTable;
import l2.universe.gameserver.model.L2Clan;
import l2.universe.gameserver.model.L2Spawn;
import l2.universe.gameserver.model.actor.instance.L2DoorInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.ClanHall;
import l2.universe.gameserver.model.zone.type.L2SiegeZone;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SiegeInfo;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.templates.StatsSet;
import l2.universe.gameserver.templates.chars.L2NpcTemplate;

/**
 * @author BiggBoss
 *
 */
public final class SiegableHall extends ClanHall
{
	private class PrepareOwner implements Runnable
	{
		@Override
		public void run()
		{
			free();
			SystemMessage msg = new SystemMessage(SystemMessageId.REGISTRATION_TERM_FOR_S1_ENDED);
			msg.addString(getName());
			Announcements.getInstance().announceToAll(msg);
			updateSiegeStatus(SiegeStatus.WAITING_BATTLE);
		}
	}
	
	private static final String SQL_LOAD_ATTACKERS = "SELECT attacker_id FROM clanhall_siege_attackers WHERE clanhall_id = ?";
	private static final String SQL_SAVE_ATTACKERS = "INSERT INTO clanhall_siege_attackers VALUES (?,?)";
	private static final String SQL_LOAD_GUARDS = "SELECT * FROM clanhall_siege_guards WHERE clanHallId = ?";
	private static final String SQL_SAVE = "UPDATE siegable_clanhall SET ownerId=?, nextSiege=? WHERE clanHallId=?";
		
	private long _nextSiege;
	private long _siegeInterval;
	private long _siegeLength;
	private ScheduledFuture<?> _prepareTask;
	
	private SiegeStatus _status = SiegeStatus.REGISTERING;
	private L2SiegeZone _siegeZone;
	
	private FastList<L2Clan> _attackers = new FastList<L2Clan>();
	private FastList<L2Spawn> _guards;
	private FastMap<L2Clan, Integer> _flagControl = new FastMap<L2Clan, Integer>();
	
	public SiegableHall(StatsSet set)
	{
		super(set);
		_nextSiege = set.getLong("nextSiege") - System.currentTimeMillis();
		if(_nextSiege < 0)
			updateNextSiege();
		
		_log.config(getName()+" siege scheduled for: "+getNextSiegeDate());
		
		_siegeInterval = set.getLong("siegeInterval");
		_siegeLength = set.getLong("siegeLenght");
		loadAttackers();
	}
	
	private final void loadAttackers()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SQL_LOAD_ATTACKERS);
			statement.setInt(1, getId());
			ResultSet rset = statement.executeQuery();
			while(rset.next())
			{
				L2Clan clan = ClanTable.getInstance().getClan(rset.getInt("attacker_id"));
				if(clan != null)
					_attackers.add(clan);
			}
			rset.close();
			statement.close();
		}
		catch(Exception e)
		{
			_log.warning(getName()+": Could not load siege attackers!:");
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	public final void loadGuards()
	{
		if(_guards == null)
		{
			_guards = new FastList<L2Spawn>();
		
			Connection con = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement(SQL_LOAD_GUARDS);
				statement.setInt(1, getId());
				ResultSet rset = statement.executeQuery();
				while(rset.next())
				{
					final int npcId = rset.getInt("npcId");
					final L2NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
					L2Spawn spawn = new L2Spawn(template);
					spawn.setLocx(rset.getInt("x"));
					spawn.setLocy(rset.getInt("y"));
					spawn.setLocz(rset.getInt("z"));
					spawn.setHeading(rset.getInt("heading"));
					spawn.setRespawnDelay(rset.getInt("respawnDelay"));
					spawn.setAmount(1);
					_guards.add(spawn);
				}
				rset.close();
				statement.close();
			}
			catch(Exception e)
			{
				_log.warning(getName()+": Couldnt load siege guards!:");
				e.printStackTrace();
			}
			finally
			{
				L2DatabaseFactory.close(con);
			}
		}
	}
	
	public final void loadDoor()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("Select * from castle_door where castleId = ?");
			statement.setInt(1, getId());
			ResultSet rs = statement.executeQuery();
			
			while (rs.next())
			{
				// Create list of the door default for use when respawning dead doors
				_doorDefault.add(rs.getString("name") + ";" + rs.getInt("id") + ";" + rs.getInt("x") + ";" + rs.getInt("y") + ";"
						+ rs.getInt("z") + ";" + rs.getInt("range_xmin") + ";" + rs.getInt("range_ymin") + ";" + rs.getInt("range_zmin")
					+ ";" + rs.getInt("range_xmax") + ";" + rs.getInt("range_ymax") + ";" + rs.getInt("range_zmax") + ";"
						+ rs.getInt("hp") + ";" + rs.getInt("pDef") + ";" + rs.getInt("mDef") + ";0");
			
				L2DoorInstance door = DoorTable.parseList(_doorDefault.get(_doorDefault.size() - 1), false);
				door.setIsWall(rs.getBoolean("isWall"));
				door.setClanHall(this);
				getDoors().add(door);
				door.spawnMe(door.getX(), door.getY(), door.getZ());
			}
			rs.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception: SiegableHall.loadDoor(): " + e.getMessage(),e );
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	public void restoreDoors()
	{
		for(L2DoorInstance door : getDoors())
			door.deleteMe();
		spawnDoor(false);
	}
	
	public final void saveAttackers()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement delStatement = con.prepareStatement("DELETE FROM clanhall_siege_attackers WHERE clanhall_id = ?");
			delStatement.setInt(1, getId());
			delStatement.execute();
			delStatement.close();
			
			if(getAttackers().size() > 0)
			{
				for(L2Clan clan : getAttackers())
				{
					PreparedStatement insert = con.prepareStatement(SQL_SAVE_ATTACKERS);
					insert.setInt(1, getId());
					insert.setInt(2, clan.getClanId());
					insert.execute();
					insert.close();
				}
			}
			_log.config(getName()+": Sucessfully saved attackers down to database!");
		}
		catch(Exception e)
		{
			_log.warning(getName()+": Couldnt save attacker list!");
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	public long getNextSiegeTime()
	{
		return _nextSiege;
	}
	
	public long getSiegeLenght()
	{
		return _siegeLength;
	}
	
	public final FastList<L2Clan> getAttackers()
	{
		return _attackers;
	}
	
	public final boolean isRegistered(L2Clan clan)
	{
		if(clan == null || !getAttackers().contains(clan))
			return false;
		return true;
	}
	
	public final FastMap<L2Clan, Integer> getFlagControl()
	{
		return _flagControl;
	}
	
	public final int getClanFlagCount(L2Clan clan)
	{
		return getFlagControl().get(clan);
	}
	
	public final boolean isRegistering() 
	{ 
		return _status == SiegeStatus.REGISTERING; 
	}
	
	public final boolean isInSiege() 
	{ 
		return _status == SiegeStatus.RUNNING; 
	}
	
	public final boolean isWaitingBattle() 
	{ 
		return _status == SiegeStatus.WAITING_BATTLE; 
	}
	
	public final L2SiegeZone getSiegeZone()
	{
		return _siegeZone;
	}
	
	public final Date getNextSiegeDate()
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(getNextSiegeTime() + System.currentTimeMillis());
		return c.getTime();
	}
	
	public final void setNextSiegeDate(long date)
	{
		if(date > System.currentTimeMillis())
			date -= System.currentTimeMillis();
		_nextSiege = date;
	}
	
	public final void updateNextSiege()
	{
		setNextSiegeDate(_siegeInterval - _siegeLength);
		_log.config(getName()+" siege re-scheduled for "+getNextSiegeDate());
		updateDb();
		prepareSiege();
		updateSiegeStatus(SiegeStatus.REGISTERING);
	}
	
	public final void setSiegeZone(L2SiegeZone zone)
	{
		_siegeZone = zone;
	}
	
	public final void updateSiegeStatus(SiegeStatus status)
	{
		_status = status;
	}
	
	public final void updateSiegeZone(boolean active)
	{
		_siegeZone.setIsActive(active);
	}
	
	public final void showSiegeInfo(L2PcInstance player)
	{
		player.sendPacket(new SiegeInfo(this));
	}
	
	public final void spawnSiegeGuards()
	{
		for(L2Spawn guard : _guards)
			if(guard != null)
				guard.init();
	}
	
	public final void unSpawnSiegeGuards()
	{
		for(L2Spawn guard : _guards)
		{
			if(guard != null)
			{
				guard.stopRespawn();
				guard.getLastSpawn().deleteMe();
			}
		}			
	}
	
	public final void prepareSiege()
	{
		if(_prepareTask != null)
			_prepareTask.cancel(true);
		_prepareTask = ThreadPoolManager.getInstance().scheduleGeneral(new PrepareOwner(), getNextSiegeTime() - 3600);
	}
	
	public final void siegeStarts()
	{
		banishForeigners();
		spawnDoor();
		loadGuards();
		spawnSiegeGuards();
		updateSiegeZone(true);
		
		final byte state = 1;
		for(L2Clan clan : getAttackers())
		{
			for(L2PcInstance pc : clan.getOnlineMembers(0))
			{
				if(pc != null)
				{
					pc.setSiegeState(state);
					pc.broadcastUserInfo();
				}
			}
		}
		
		updateSiegeStatus(SiegeStatus.RUNNING);
		Announcements.getInstance().announceToAll("The Siege of "+getName()+" has begun!");
	}
	
	public final void siegeEnds()
	{
		updateSiegeZone(false);
		updateNextSiege();
		restoreDoors();
		unSpawnSiegeGuards();
		banishForeigners();
		
		final byte state = 0;
		for(L2Clan clan : getAttackers())
		{
			for(L2PcInstance player : clan.getOnlineMembers(0))
			{
				player.setSiegeState(state);
				player.broadcastUserInfo();
			}
		}
		
		getAttackers().clear();
		
		Announcements.getInstance().announceToAll("The "+getName()+" siege is over!");
	}
	
	public final synchronized boolean addSiegeFlag(L2Clan clan)
	{
		Map.Entry<L2Clan, Integer> entry = getFlagControl().getEntry(clan);
		boolean canAdd = true;
		
		if(entry != null)
		{
			if(entry.getValue() >= Config.CHS_MAX_FLAGS_PER_CLAN)
				canAdd = false;
			else
			{
				final int newVal = entry.getValue() + 1;
				getFlagControl().put(entry.getKey(), newVal);
			}
		}
		else
			getFlagControl().put(clan, 1);
		
		return canAdd;
	}
	
	public final void removeSiegeFlag(L2Clan clan)
	{
		if(getFlagControl().containsKey(clan))
		{
			final int newVal = getClanFlagCount(clan) - 1;
			getFlagControl().put(clan, newVal);
		}
	}
	
	@Override
	public final void updateDb()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement;
			
			statement = con.prepareStatement(SQL_SAVE);
			statement.setInt(1, getOwnerId());
			statement.setLong(2, getNextSiegeTime() + System.currentTimeMillis());
			statement.setInt(3, getId());
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception: updateOwnerInDB(L2Clan clan): " + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
}