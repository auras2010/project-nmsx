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
import java.util.logging.Logger;

import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.model.L2Clan;
import l2.universe.gameserver.model.entity.clanhall.SiegableHall;
import l2.universe.gameserver.model.zone.type.L2ClanHallZone;
import l2.universe.gameserver.templates.StatsSet;

import javolution.util.FastMap;

/**
 * @author BiggBoss
 *
 */
public final class CHSiegeManager
{
	private static final Logger _log = Logger.getLogger(CHSiegeManager.class.getName());
	private static final String SQL_LOAD_HALLS = "SELECT * FROM siegable_clanhall";
	
	private static final class SingletonHolder
	{
		private static final CHSiegeManager INSTANCE = new CHSiegeManager();
	}
	
	private FastMap<Integer, SiegableHall> _siegableHalls = new FastMap<Integer, SiegableHall>();
	
	private CHSiegeManager()
	{
		_log.info("Initializing CHSiegeManager...");
		loadClanHalls();
	}
	
	public static CHSiegeManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private final void loadClanHalls()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SQL_LOAD_HALLS);
			ResultSet rs = statement.executeQuery();
			
			_siegableHalls.clear();
			
			while(rs.next())
			{
				final int id = rs.getInt("clanHallId");
				
				//TODO: Not done yet
				if(id == 35 || id == 63)
					continue;
				
				StatsSet set = new StatsSet();
				
				set.set("id", id);
				set.set("name", rs.getString("name"));
				set.set("ownerId", rs.getInt("ownerId"));
				set.set("desc", rs.getString("desc"));
				set.set("location", rs.getString("location"));
				set.set("nextSiege", rs.getLong("nextSiege"));
				set.set("siegeInterval", rs.getLong("siegeInterval"));
				set.set("siegeLenght", rs.getLong("siegeLenght"));
				SiegableHall hall = new SiegableHall(set);
				_siegableHalls.put(id, hall);
				ClanHallManager.addClanHall(hall);
			}
			_log.config("CHSiegeManager: Loaded "+_siegableHalls.size()+" conquerable clan halls");
			rs.close();
			statement.close();
		}
		catch(Exception e)
		{
			_log.warning("CHSiegeManager: Could not load siegable clan halls!:");
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	public FastMap<Integer, SiegableHall> getConquerableHalls()
	{
		return _siegableHalls;
	}
	
	
	public SiegableHall getSiegableHall(int clanHall)
	{
		return getConquerableHalls().get(clanHall);
	}
	
	public final SiegableHall getNearbyClanHall(int x, int y, int maxDist)
	{
		L2ClanHallZone zone = null;
		
		for (Map.Entry<Integer, SiegableHall> ch : _siegableHalls.entrySet())
		{
			zone = ch.getValue().getZone();
			if (zone != null && zone.getDistanceToZone(x, y) < maxDist)
				return ch.getValue();
		}
		return null;
	}
	
	public final boolean isClanParticipating(L2Clan clan)
	{
		for(SiegableHall hall : getConquerableHalls().values())
			if(hall.isRegistered(clan))
				return true;
		return false;
	}
	
	public final void onServerShutDown()
	{
		for(SiegableHall hall : getConquerableHalls().values())
		{
			//Rainbow springs has his own attackers table
			if(hall.getId() == 62)
				continue;
			
			hall.saveAttackers();
		}
	}
}