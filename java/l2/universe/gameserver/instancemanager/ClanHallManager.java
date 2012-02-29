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
package l2.universe.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastMap;

import l2.universe.Config;
import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.datatables.ClanTable;
import l2.universe.gameserver.model.L2Clan;
import l2.universe.gameserver.model.entity.Auction;
import l2.universe.gameserver.model.entity.ClanHall;
import l2.universe.gameserver.model.entity.clanhall.AuctionableHall;
import l2.universe.gameserver.model.zone.type.L2ClanHallZone;
import l2.universe.gameserver.templates.StatsSet;

/**
 *
 * @author  Steuf
 */
public class ClanHallManager
{
	protected static final Logger _log = Logger.getLogger(ClanHallManager.class.getName());
	
	private Map<Integer, AuctionableHall> _clanHall;
	private Map<Integer, AuctionableHall> _freeClanHall;
	private Map<Integer, AuctionableHall> _allAuctionableClanHalls;
	
	private static Map<Integer, ClanHall> _allClanHalls = new FastMap<Integer, ClanHall>();
	private boolean _loaded = false;
	
	public static ClanHallManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public boolean loaded()
	{
		return _loaded;
	}
	
	private ClanHallManager()
	{
		_log.info("Initializing ClanHallManager");
		_clanHall = new FastMap<Integer, AuctionableHall>();
		_freeClanHall = new FastMap<Integer, AuctionableHall>();
		_allAuctionableClanHalls = new FastMap<Integer, AuctionableHall>();
		load();
	}
	
	/** Reload All Clan Hall */
	/*	public final void reload() Cant reload atm - would loose zone info
		{
			_clanHall.clear();
			_freeClanHall.clear();
			load();
		}
	 */
	
	/** Load All Clan Hall */
	private final void load()
	{
		Connection con = null;
		try
		{
			int id, ownerId, lease;
			PreparedStatement statement;
			ResultSet rs;
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM clanhall ORDER BY id");
			rs = statement.executeQuery();
			while (rs.next())
			{
				StatsSet set = new StatsSet();
				id = rs.getInt("id");
				ownerId = rs.getInt("ownerId");
				lease = rs.getInt("lease");
				set.set("id", id);
				set.set("name", rs.getString("name"));
				set.set("ownerId", ownerId);
				set.set("lease", lease);
				set.set("desc", rs.getString("desc"));
				set.set("location", rs.getString("location"));
				set.set("paidUntil", rs.getLong("paidUntil"));
				set.set("grade", rs.getInt("Grade"));
				set.set("paid", rs.getBoolean("paid"));
				AuctionableHall ch = new AuctionableHall(set);
				_allAuctionableClanHalls.put(id, ch);
				addClanHall(ch);
				if (ownerId > 0)
				{
					final L2Clan owner = ClanTable.getInstance().getClan(ownerId);
					if (owner != null)
					{
						_clanHall.put(id, ch);
						owner.setHasHideout(id);
						continue;
					}
					else
						ch.free();
				}
				_freeClanHall.put(id, ch);
				
				Auction auc = AuctionManager.getInstance().getAuction(id);
				if (auc == null && lease > 0)
					AuctionManager.getInstance().initNPC(id);
			}
			
			statement.close();
			_log.info("Loaded: " + getClanHalls().size() + " clan halls");
			_log.info("Loaded: " + getFreeClanHalls().size() + " free clan halls");
			_loaded = true;
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception: ClanHallManager.load(): " + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	/** Get Map with all FreeClanHalls */
	public final Map<Integer, AuctionableHall> getFreeClanHalls()
	{
		return _freeClanHall;
	}
	
	public static final Map<Integer, ClanHall> getAllClanHalls()
	{
		return _allClanHalls;
	}
		
	/** Get Map with all ClanHalls that have owner*/
	public final Map<Integer, AuctionableHall> getClanHalls()
	{
		return _clanHall;
	}
	
	/** Get Map with all ClanHalls*/
	public final Map<Integer, AuctionableHall> getAllAuctionableClanHalls()
	{
		return _allAuctionableClanHalls;
	}
	
	public static final void addClanHall(ClanHall hall)
	{
		_allClanHalls.put(hall.getId(), hall);
	}

	/** Check is free ClanHall */
	public final boolean isFree(int chId)
	{
		if (_freeClanHall.containsKey(chId))
			return true;
		return false;
	}
	
	/** Free a ClanHall */
	public final synchronized void setFree(int chId)
	{
		_freeClanHall.put(chId, _clanHall.get(chId));
		ClanTable.getInstance().getClan(_freeClanHall.get(chId).getOwnerId()).setHasHideout(0);
		_freeClanHall.get(chId).free();
		_clanHall.remove(chId);
	}
	
	/** Set ClanHallOwner */
	public final synchronized void setOwner(int chId, L2Clan clan)
	{
		if (!_clanHall.containsKey(chId))
		{
			_clanHall.put(chId, _freeClanHall.get(chId));
			_freeClanHall.remove(chId);
		}
		else
			_clanHall.get(chId).free();
		ClanTable.getInstance().getClan(clan.getClanId()).setHasHideout(chId);
		_clanHall.get(chId).setOwner(clan);
	}
	
	/** Get Clan Hall by Id */
	public final AuctionableHall getClanHallById(int clanHallId)
	{
		if (_clanHall.containsKey(clanHallId))
			return _clanHall.get(clanHallId);
		if (_freeClanHall.containsKey(clanHallId))
			return _freeClanHall.get(clanHallId);
		if(Config.DEBUG)
		{
			if(!CHSiegeManager.getInstance().getConquerableHalls().containsKey(clanHallId))
				_log.warning("Clan hall id " + clanHallId + " not found in clanhall table!");
			else
				_log.warning("ClanHallManager: a Siegable hall (id: "+clanHallId+") was requested at this class!");
		}
		return null;
	}
	
	/** Get Clan Hall by x,y,z */
	/*
		public final ClanHall getClanHall(int x, int y, int z)
		{
			for (Map.Entry<Integer, ClanHall> ch : _clanHall.entrySet())
				if (ch.getValue().getZone().isInsideZone(x, y, z)) return ch.getValue();

			for (Map.Entry<Integer, ClanHall> ch : _freeClanHall.entrySet())
				if (ch.getValue().getZone().isInsideZone(x, y, z)) return ch.getValue();

			return null;
		}*/
	
	public final AuctionableHall getNearbyClanHall(int x, int y, int maxDist)
	{
		L2ClanHallZone zone = null;
		
		for (Map.Entry<Integer, AuctionableHall> ch : _clanHall.entrySet())
		{
			zone = ch.getValue().getZone();
			if (zone != null && zone.getDistanceToZone(x, y) < maxDist)
				return ch.getValue();
		}
		for (Map.Entry<Integer, AuctionableHall> ch : _freeClanHall.entrySet())
		{
			zone = ch.getValue().getZone();
			if (zone != null && zone.getDistanceToZone(x, y) < maxDist)
				return ch.getValue();
		}
		return null;
	}
	
	/** Get Clan Hall by Owner */
	public final AuctionableHall getClanHallByOwner(L2Clan clan)
	{
		for (Map.Entry<Integer, AuctionableHall> ch : _clanHall.entrySet())
		{
			if (clan.getClanId() == ch.getValue().getOwnerId())
				return ch.getValue();
		}
		return null;
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final ClanHallManager _instance = new ClanHallManager();
	}
}