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
package l2.universe.gameserver.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Logger;

import l2.universe.ExternalConfig;
import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.handler.AIOItemHandler;
import l2.universe.gameserver.handler.IAIOItemHandler;
import l2.universe.gameserver.instancemanager.SiegeManager;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.events.TvTEvent;

import javolution.util.FastMap;

public final class AIOItemTable
{
	/**
	 * Right now, normal buffer and scheme buffer share the
	 * avaliable buffs to get (one to use, other to select for profile)
	 * If you want separate buffs for them, you have to:
	 * 1- Create new table with structure ('category(String)', 'buff_id(int)', 'buff_lvl(int)')
	 * 2- Remove comments tag so you make avaliable SchemeBuffsHolder class
	 * 4- Add a new FastMap<String, SchemesBuffsHolder>
	 * 3- At loadAioItemData(), add a new code piece or method, which load the
	 * scheme data and store it in the fastmap
	 * 4- Create the required methods te get data from that map
	 * 5- Modify AIOSchemeHandler in data/scripots/handlers/aioitemhandler
	 */
	
	private static final Logger _log = Logger.getLogger(AIOItemTable.class.getName());
	
	// Holds each category and his Teleports
	private static FastMap<Integer, TeleportCategoryHolder> _teleports = new FastMap<Integer, TeleportCategoryHolder>();
	// Holds each category and his one-use buffs
	private static FastMap<String, CategoryBuffHolder> _buffs = new FastMap<String, CategoryBuffHolder>();
	// Holds all skills
	private static FastMap<Integer, L2Skill> _allBuffs = new FastMap<Integer,L2Skill>();
	// Holds pvp rank
	private static String _pvpList;
	private static int _minPvp;
	private static boolean _pvpNeedsToUpdate = false;
	// Holds pk rank
	private static String _pkList;
	private static int _minPk;
	private static boolean _pkNeedsToUpdate = false;
	
	private AIOItemTable()
	{
	}
	
	public static AIOItemTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	/**
	 * Each object will hold a category and
	 * a map of strings - integer array to 
	 * hold each teleport and his coords
	 */
	public class TeleportCategoryHolder
	{
		private FastMap<Integer, SpawnPointInfo> _spawnPoints;
		private final String _name;
		
		TeleportCategoryHolder(final String name)
		{
			_name = name;
			_spawnPoints = new FastMap<Integer, SpawnPointInfo>();
		}
		
		void loadMyData()
		{
			Connection con = null;
			
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement("SELECT * FROM aio_teleports WHERE category = ?");
				statement.setString(1, _name);
				
				ResultSet rset = statement.executeQuery();
				while(rset.next())
				{
					final int id = rset.getInt("id");
					SpawnPointInfo info = new SpawnPointInfo();
					info._name = rset.getString("tpname");
					info._x = rset.getInt("x");
					info._y = rset.getInt("y");
					info._z = rset.getInt("z");
					_spawnPoints.put(id, info);
				}
				rset.close();
				statement.close();
			}
			catch(SQLException sqle)
			{
				_log.severe("Problems while loading AIOItem teleports. Check the db table\n"+sqle.getMessage());
			}
			try
			{
				con.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		public String getName()
		{
			return _name;
		}
		
		public FastMap<Integer, SpawnPointInfo> getCategoryData()
		{
			return _spawnPoints;
		}
		
		public Map.Entry<Integer, SpawnPointInfo> getSpawnInfo(int id)
		{
		return _spawnPoints.getEntry(id);
		}
	}
	
	public class SpawnPointInfo
	{
		public String _name;
		public int _x;
		public int _y;
		public int _z;
	}
	
	/**
	 * Each object will hold a buff category
	 * and a list of skill (the ones of that
	 * category)
	 */
	public class CategoryBuffHolder
	{
		FastMap<Integer, L2Skill> _categoryBuffs;
		String _categoryName;
		
		CategoryBuffHolder(String name)
		{
			if(name != null)
			{
				_categoryName = name;
				_categoryBuffs = new FastMap<Integer, L2Skill>();
			}
		}
		
		void loadMyData()
		{
			Connection con = null;
			
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement("SELECT buff_id, buff_lvl FROM aio_buffs WHERE category = ?");
				statement.setString(1, _categoryName);				
				ResultSet rset = statement.executeQuery();
				while(rset.next())
				{
					int id = rset.getInt("buff_id");
					int lvl = rset.getInt("buff_lvl");
					
					L2Skill buff = SkillTable.getInstance().getInfo(id, lvl);
					_categoryBuffs.put(id, buff);
					_allBuffs.put(id, buff);
				}
				rset.close();
				statement.close();
			}
			catch(Exception e)
			{
				_log.severe("Couldnt load buffs table for AIOItem\n"+e.getMessage());
			}
			try
			{
				con.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		public FastMap<Integer, L2Skill> getCategoryBuffs()
		{
			return _categoryBuffs;
		}
	}
	
	/*
	public class SchemeBuffsHolder
	{
		FastList<L2Skill> _categoryBuffs;
		String _categoryName;
		
		SchemeBuffsHolder(String name)
		{
			if(name != null)
			{
				_categoryName = name;
				_categoryBuffs = new FastList<L2Skill>();
			}
		}
		
		void loadMyData()
		{
			Connection con = null;
			
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement("");
				statement.setString(1, _categoryName);
				
				ResultSet rset = statement.executeQuery();
				while(rset.next())
				{
					int id = rset.getInt("");
					int lvl = rset.getInt("");
					
					L2Skill buff = SkillTable.getInstance().getInfo(id, lvl);
					_categoryBuffs.add(buff);
				}
				rset.close();
				statement.close();
			}
			catch(Exception e)
			{
				_log.severe("Couldnt load buffs table for AIOItem\n"+e.getMessage());
			}
			try
			{
				con.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		public FastList<L2Skill> getCategoryBuffs()
		{
			return _categoryBuffs;
		}
	}
	*/
	
	/**
	 * Will fill all maps at server start up
	 */
	public void loadAioItemData()
	{
		if(!ExternalConfig.AIOITEM_ENABLEME)
		{
			_log.config("AIOItem: I'm disabled");
			return;
		}
		
		Connection con = null;
		_log.config("Loading AIOItem Data...");
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			try
			{
				PreparedStatement statement = con.prepareStatement("SELECT category_id, category FROM aio_teleports_categories");			
				ResultSet rset = statement.executeQuery();
				while(rset.next())
				{
					final int id = rset.getInt("category_id");
					final String name = rset.getString("category");
					TeleportCategoryHolder holder = new TeleportCategoryHolder(name);
					holder.loadMyData();
					_teleports.put(id, holder);
				}
				rset.close();
				statement.close();
				_log.config("AIOItemTable: Loaded "+_teleports.size() +" teleport categories!");
			}
			catch(Exception e)
			{
				_log.warning("AIOItemTable: Couldnt load AIO Item teleports: "+e.getMessage());
				e.printStackTrace();
			}
			
			try
			{
				/*
				 * Load buffs
				 */	
				PreparedStatement buffStatement = con.prepareStatement("SELECT category FROM aio_buffs");
				ResultSet buffSet = buffStatement.executeQuery();
				while(buffSet.next())
				{
					final String name = buffSet.getString("category");
					CategoryBuffHolder holder = new CategoryBuffHolder(name);
					holder.loadMyData();
					_buffs.put(name, holder);
				}
				buffSet.close();
				buffStatement.close();			
				_log.config("Loaded "+_buffs.size()+" buffs categories for the AIOItem");
			}
			catch(Exception e)
			{
				_log.warning("AIOItem: Couldnt load AIO Item buffs: "+e.getMessage());
				e.printStackTrace();
			}
			// PvP Rank 
			buildPvpRank();
			// Pk Rank
			buildPkRank();
		}
		catch(Exception e)
		{
			_log.severe("Couldnt load data for the AIOItem\n"+e.getMessage());
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	private void buildPvpRank()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("");
		final String colored = "<table width=270 bgcolor=66CCFF><tr>";
		final String nonColored = "<table width=270><tr>";
		int counter = 1;
		
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT char_name,pvpkills FROM characters WHERE pvpkills>0 and accesslevel=0 order by pvpkills desc limit 25");
			ResultSet rset = statement.executeQuery();
			while(rset.next())
			{
				final String name = rset.getString("char_name");
				_minPvp = rset.getInt("pvpkills");
				
				if(counter == 1)
				
				if(counter % 2 == 0)
					sb.append(colored);
				else
					sb.append(nonColored);
				
				sb.append("<td width=90>"+counter+"/<td>");
				sb.append("<td width=90>"+name+"</td>");
				sb.append("<td width=90>"+_minPvp+"</td>");
				sb.append("</tr></table>");
				
				++counter;
			}
			rset.close();
			statement.close();
		}
		catch(Exception e)
		{
			_log.warning("AIOItemTable: Couldnt gather needed info from database for PvP Top:");
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
		_pvpList = sb.toString();
	}
	
	private void buildPkRank()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("");
		final String colored = "<table width=270 bgcolor=FF9999><tr>";
		final String nonColored = "<table width=270><tr>";
		int counter = 1;
		
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT char_name,pkkills FROM characters WHERE pkkills>0 and accesslevel=0 order by pkkills desc limit 25");
			ResultSet rset = statement.executeQuery();
			while(rset.next())
			{
				final String name = rset.getString("char_name");
				_minPk = rset.getInt("pkkills");
				
				if(counter % 2 == 0)
					sb.append(colored);
				else
					sb.append(nonColored);
				
				sb.append("<td width=90>"+counter+"/<td>");
				sb.append("<td width=90>"+name+"</td>");
				sb.append("<td width=90>"+_minPk+"</td>");
				sb.append("</tr></table>");
				
				++counter;
			}
			rset.close();
			statement.close();
		}
		catch(Exception e)
		{
			_log.warning("AIOItemTable: Couldnt gather needed info from database for Pk Top:");
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
		_pkList = sb.toString();
	}
	
	public String getPvpRank()
	{
		if(_pvpNeedsToUpdate)
		{
			buildPvpRank();
			_pkNeedsToUpdate = false;
		}
		return _pvpList;
	}
	
	public String getPkRank()
	{
		if(_pkNeedsToUpdate)
		{
			buildPkRank();
			_pkNeedsToUpdate = false;
		}
		return _pkList;
	}
	
	public void onPvpIncrease(int newNum)
	{
		if(newNum > _minPvp)
		{
			synchronized(this)
			{
				_pvpNeedsToUpdate = true;
			}
		}
	}
	
	public void onPkIncrease(int newNum)
	{
		if(newNum > _minPk)
		{
			synchronized(this)
			{
				_pkNeedsToUpdate = true;
			}
		}
	}
	
	/**
	 * Will return the map of teleports
	 * @return FastMap<Category Name, Teleport Holder>
	 */
	public FastMap<Integer, TeleportCategoryHolder> getTeleports()
	{
		return _teleports;
	}
	
	/**
	 * Will return the teleport holder of a given category
	 * @param cat [String]
	 * @return TeleportCategoryHolder
	 */
	public TeleportCategoryHolder getCategoryTeleports(int id)
	{
		return _teleports.get(id);
	}
	
	/**
	 * Will return the map of buffs
	 * @return FastMap<Category Name, Buffs Holder>
	 */
	public FastMap<String, CategoryBuffHolder> getBuffs()
	{
		return _buffs;
	}
	
	/**
	 * Will return the holder of buffs of a given category
	 * @param cat [String]
	 * @return CategoryBuffHolder
	 */
	public CategoryBuffHolder getBuffCategory(String cat)
	{
		if(!_buffs.containsKey(cat))
			return null;
		
		return _buffs.get(cat);
	}
	
	/**
	 * Will return a skill which must be contained in the
	 * given category with the given id
	 * @param category
	 * @param id
	 * @return L2Skill
	 */
	public L2Skill getBuff(String category, int id)
	{
		if(getBuffCategory(category) != null)
		{
			L2Skill buff = null;
			if((buff = getBuffCategory(category).getCategoryBuffs().get(id)) != null)
			{
				return buff;
			}
		}
		return null;
	}
	
	/**
	 * Return the skill linked to the given
	 * id
	 * @param id
	 * @return L2Skill
	 */
	public L2Skill getBuff(int id)
	{
		return _allBuffs.get(id);
	}
	
	/**
	 * Check the general requirements for the player to be able to send
	 * a aio item bypass and use it
	 * @param player [L2PcInstance]
	 * @return boolean
	 */
	public boolean checkPlayerConditions(L2PcInstance player)
	{
		if (ExternalConfig.AIOITEM_ONLY_FOR_PREMIUM && !player.isPremium())
       	{
			player.sendMessage("You should buy Premium Account to get it!");
			return false;
       	}
		if(player.getPvpFlag() > 0)
		{
			player.sendMessage("Cannot use AIOItem while flagged!");
			return false;
		}
		if(player.getKarma() > 0 || player.isCursedWeaponEquipped())
		{
			player.sendMessage("Cannot use AIOItem while chaotic!");
			return false;
		}
		if(player.isInOlympiadMode() || TvTEvent.isPlayerParticipant(player.getObjectId()))
		{
			player.sendMessage("Cannot use while in events!");
			return false;
		}
		if(player.isEnchanting())
		{
			player.sendMessage("Cannot use while enchanting!");
			return false;
		}
		if(player.isInJail())
		{
			player.sendMessage("Cannot use while in Jail!");
			return false;
		}
        if (ExternalConfig.AIOITEM_ONLY_IN_TOWN && !player.isInsideZone(L2Character.ZONE_TOWN))
        {
            player.sendMessage("Only can be used in town!");
            return false;
        }
        if (SiegeManager.getInstance().getSiege(player) != null && SiegeManager.getInstance().getSiege(player).getIsInProgress())
        {
            player.sendMessage("You can't use AIO during siege!");
            return false;
        }
		return true;
	}
	
	public void handleBypass(L2PcInstance activeChar, String command)
	{
		if(!ExternalConfig.AIOITEM_ENABLEME)
			return;
		
		if(!AIOItemTable.getInstance().checkPlayerConditions(activeChar))
			return;
		
		activeChar.setTarget(activeChar);
		
		String[] subCmd = command.split("_");
		
		IAIOItemHandler handler = AIOItemHandler.getInstance().getAIOHandler(subCmd[1]);
		
		if(handler != null)
			handler.onBypassUse(activeChar, subCmd[2]);
	}
	
	private static class SingletonHolder
	{
		private static final AIOItemTable _instance = new AIOItemTable();
	}
}
