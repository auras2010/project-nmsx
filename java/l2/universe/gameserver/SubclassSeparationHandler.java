/*
 * $Header: Item.java, 2/08/2005 00:49:12 luisantonioa Exp $
 * 
 * $Author: luisantonioa $ $Date: 2/08/2005 00:49:12 $ $Revision: 1 $ $Log:
 * Item.java,v $ Revision 1 2/08/2005 00:49:12 luisantonioa Added copyright
 * notice
 * 
 * 
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
package l2.universe.gameserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javolution.util.FastList;

import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.model.SubclassSeparation;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;


public class SubclassSeparationHandler
{
	private String SET_ACCOUNT_AS_BLOCED = "UPDATE accounts SET accesslevel = -999 WHERE login =?1";
	private String SET_ACCOUNT_AS_FREE = "UPDATE accounts SET accesslevel = 0 WHERE login =?1 and accesslevel = -999";

	private String LOAD_SEPARATION_DATA = "SELECT * FROM subclass_separation WHERE account_from = ?1 ORDER BY separation_date DESC";
	private String LOAD_SUBCLASS_DATA = "SELECT exp, level, sp, class_id  FROM character_subclasses WHERE charid = ?1 AND class_index = ?2";
	private String LOAD_NEW_CHARACTER_DATA = "SELECT charId FROM characters WHERE account_name = ?1";
	private String LOAD_RACE_DATA = "SELECT class_name FROM class_list WHERE id = ?1";

	private String INSERT_SEPARATION_INFO = "INSERT INTO subclass_separation (account_from, account_to, char_id, subclass_index, separation_date) VALUES(?1, ?2, ?3, ?4, ?5)";

	private String UPDATE_SKILL_INFO = "UPDATE character_skills SET charid = ?1, class_index = 0 where charid = ?2 and class_index = ?3";
	private String UPDATE_HENNA_INFO = "UPDATE character_hennas SET charid = ?1, class_index = 0 where charid = ?2 and class_index = ?3";
	private String UPDATE_CHARACTER_INFO = "UPDATE characters SET ?1 = ?2 WHERE charid = ?3"; 

	private String DELETE_SKILL_DATA = "DELETE FROM character_skills WHERE charid = ?1 and class_index = ?2";
	private String DELETE_SKILL_SAVE_DATA = "DELETE FROM character_skills_save WHERE charid = ?1 and class_index = ?2";
	private String DELETE_QUEST_DATA = "DELETE FROM character_quests where charid = ?1 AND class_index = ?2";
	private String DELETE_SHORTCUT_DATA = "DELETE FROM character_shortcuts where charid = ?1 AND class_index = ?2";
	private String DELETE_RECIPES_DATA = "DELETE FROM character_recipebook where charid = ?1 AND clasSindex = ?2";
	private String DELETE_SUBCLASS = "DELETE FROM character_subclasses where charid = ?1 AND class_index = ?2";

	private static final Logger _log = Logger.getLogger(SubclassSeparationHandler.class.getName());
	private FastList<SubclassSeparation> _separations = new FastList<SubclassSeparation>();	

	public SubclassSeparationHandler()
	{
		_log.info("Initializing Subclass Separation Handler.");
	}

	public static SubclassSeparationHandler getInstance()
	{
		return SingletonHolder._instance;
	}

	public void addFutureSeparation(SubclassSeparation ss, L2PcInstance player)
	{
		_separations.add(ss);
	}
	public boolean canDoSeparation(String accountName)
	{
		boolean canDo = true;
		Connection con = null;
		String e_query = LOAD_SEPARATION_DATA.replace("?1", accountName);
		
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement st = con.prepareStatement(e_query);
			ResultSet rset = st.executeQuery();
			while (rset.next())
			{
				Date last_separation = rset.getDate("separation_date");

				Calendar last_date = Calendar.getInstance();
				last_date.setTime(last_separation);

				Calendar today_date = Calendar.getInstance();

				long diff = today_date.getTimeInMillis() - last_date.getTimeInMillis();
				long days = (diff / (1000L*60L*60L*24L));
				if (days < 7)
					canDo = false;
				else
					canDo = true;
				_log.info("days in diff " + String.valueOf(days));
				break;
			}
		}
		catch(Exception e)
		{
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		return canDo;
	}

	public void doSeparation(int playerId)
	{
		boolean error_ocurred = false;
		long _subclassExperience = 0;
		long _subclassSp = 0;

		int _subclassLevel = 0;
		int _subclassId = 0;
		int _subclassIndex = 0;
		int _subclassSex = 0;

		int newCharId = 0;
		int newCharRaceId = 0;
			
		SubclassSeparation _doSeparation = getSeparationInfo(playerId);
		
		newCharId = getNewCharacterId(_doSeparation.getNewAccountName());
		playerId = _doSeparation.getPlayerId();

		_subclassIndex = _doSeparation.getSubclassChosen();
		_subclassSex = _doSeparation.getSex();

		String e_query = LOAD_SUBCLASS_DATA.replace("?1", String.valueOf(playerId));
		e_query = e_query.replace("?2", String.valueOf(_subclassIndex));
		
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			con.setAutoCommit(false);

			PreparedStatement st = con.prepareStatement(e_query);
			ResultSet rset = st.executeQuery();
			while (rset.next())
			{
				_subclassExperience = rset.getLong("exp");
				_subclassSp = rset.getLong("sp");
				_subclassLevel = rset.getInt("level");
				_subclassId = rset.getInt("class_id");
			}
			rset.close();
		}
		catch(Exception e)
		{
			error_ocurred = true;
			_log.warning("Failed to initiate separation for playerId " + playerId + e.toString());
		}

		if (error_ocurred)
		{
			try
			{
				con.rollback();
			}
			catch(java.sql.SQLException e)
			{
				_log.severe("Couldn't perform rollback.");
			}
			finally
			{
				L2DatabaseFactory.close(con);
			}
			return;
		}
		else
		{
			L2DatabaseFactory.close(con);
		}
		
		e_query = SET_ACCOUNT_AS_BLOCED.replace("?1", String.valueOf(newCharId));
		if (executeQuery(e_query, con))
			return;
		
		e_query = SET_ACCOUNT_AS_BLOCED.replace("?1", String.valueOf(playerId));
		if (executeQuery(e_query, con))
			return;
		
		e_query = UPDATE_CHARACTER_INFO.replace("?1", "face");
		e_query = e_query.replace("?2", "0");
		e_query = e_query.replace("?3",String.valueOf(newCharId));
		if (executeQuery(e_query, con))
			return;

		e_query = UPDATE_CHARACTER_INFO.replace("?1","hairStyle");
		e_query = e_query.replace("?2","0");
		e_query = e_query.replace("?3",String.valueOf(newCharId));
		if (executeQuery(e_query, con))
			return;
		
		e_query = UPDATE_CHARACTER_INFO.replace("?1","hairColor");
		e_query = e_query.replace("?2","0");
		e_query = e_query.replace("?3",String.valueOf(newCharId));
		if (executeQuery(e_query, con))
			return;
		
		e_query = UPDATE_CHARACTER_INFO.replace("?1","exp");
		e_query = e_query.replace("?2",String.valueOf(_subclassExperience));
		e_query = e_query.replace("?3",String.valueOf(newCharId));
		if (executeQuery(e_query, con))
			return;

		e_query = UPDATE_CHARACTER_INFO.replace("?1","sp");
		e_query = e_query.replace("?2",String.valueOf(_subclassSp));
		e_query = e_query.replace("?3",String.valueOf(newCharId));
		if (executeQuery(e_query, con))
			return;

		e_query = UPDATE_CHARACTER_INFO.replace("?1","level");
		e_query = e_query.replace("?2",String.valueOf(_subclassLevel));
		e_query = e_query.replace("?3",String.valueOf(newCharId));
		if (executeQuery(e_query, con))
			return;
	
		e_query = DELETE_SKILL_DATA.replace("?1", String.valueOf(newCharId));
		e_query = e_query.replace("?2", "0");
		if (executeQuery(e_query, con))
			return;
		
		e_query = UPDATE_SKILL_INFO.replace("?1", String.valueOf(newCharId));
		e_query = e_query.replace("?2", String.valueOf(playerId));
		e_query = e_query.replace("?3", String.valueOf(_subclassIndex));
		if (executeQuery(e_query, con))
			return;
		
		e_query = UPDATE_HENNA_INFO.replace("?1", String.valueOf(newCharId));
		e_query = e_query.replace("?2", String.valueOf(playerId));
		e_query = e_query.replace("?3", String.valueOf(_subclassIndex));
		if (executeQuery(e_query, con))
			return;

		newCharRaceId = getRaceByClassId(_subclassId);

		e_query = UPDATE_CHARACTER_INFO.replace("?1","race");
		e_query = e_query.replace("?2",String.valueOf(newCharRaceId));
		e_query = e_query.replace("?3",String.valueOf(newCharId));
		if (executeQuery(e_query, con))
			return;

		e_query = UPDATE_CHARACTER_INFO.replace("?1","base_class");
		e_query = e_query.replace("?2",String.valueOf(_subclassId));
		e_query = e_query.replace("?3",String.valueOf(newCharId));
		if (executeQuery(e_query, con))
			return;

		e_query = UPDATE_CHARACTER_INFO.replace("?1","classId");
		e_query = e_query.replace("?2",String.valueOf(_subclassId));
		e_query = e_query.replace("?3",String.valueOf(newCharId));
		if (executeQuery(e_query, con))
			return;
		
		e_query = UPDATE_CHARACTER_INFO.replace("?1","sex");
		e_query = e_query.replace("?2",String.valueOf(_subclassSex));
		e_query = e_query.replace("?3",String.valueOf(newCharId));
		if (executeQuery(e_query, con))
			return;

		e_query = DELETE_SKILL_DATA.replace("?1", String.valueOf(playerId));
		e_query = e_query.replace("?2", String.valueOf(_subclassIndex));
		if (executeQuery(e_query, con))
			return;
		
		e_query = DELETE_SKILL_SAVE_DATA.replace("?1",String.valueOf(playerId));
		e_query = e_query.replace("?2", String.valueOf(_subclassIndex));
		if (executeQuery(e_query, con))
			return;

		e_query = DELETE_QUEST_DATA.replace("?1", String.valueOf(playerId));
		e_query = e_query.replace("?2", String.valueOf(_subclassIndex));
		if (executeQuery(e_query, con))
			return;

		e_query = DELETE_SHORTCUT_DATA.replace("?1", String.valueOf(playerId));
		e_query = e_query.replace("?2", String.valueOf(_subclassIndex));
		if (executeQuery(e_query, con))
			return;

		e_query = DELETE_RECIPES_DATA.replace("?1", String.valueOf(playerId));
		e_query = e_query.replace("?2", String.valueOf(_subclassIndex));
		if (executeQuery(e_query, con))
			return;

		e_query = DELETE_SUBCLASS.replace("?1", String.valueOf(playerId));
		e_query = e_query.replace("?2", String.valueOf(_subclassIndex));
		if (executeQuery(e_query, con))
			return;

		Calendar c = Calendar.getInstance();
		String fullDate = String.valueOf(c.get(Calendar.YEAR)) + "-" + String.valueOf(c.get(Calendar.MONTH)+1) + "-" + String.valueOf(c.get(Calendar.DATE)) + " " 
					+ String.valueOf(c.get(Calendar.HOUR)) + ":" + String.valueOf(c.get(Calendar.MINUTE)) + ":" + String.valueOf(c.get(Calendar.SECOND));

		e_query = INSERT_SEPARATION_INFO.replace("?1", "'" + _doSeparation.getAccountName() + "'");
		e_query = e_query.replace("?2", "'" + _doSeparation.getNewAccountName() + "'");
		e_query = e_query.replace("?3", String.valueOf(playerId));
		e_query = e_query.replace("?4", String.valueOf(_subclassIndex));
		e_query = e_query.replace("?5", "'" + fullDate + "'");
		if (executeQuery(e_query, con))
			return;
		
		e_query = SET_ACCOUNT_AS_FREE.replace("?1", String.valueOf(newCharId));
		if (executeQuery(e_query, con))
			return;
		
		e_query = SET_ACCOUNT_AS_FREE.replace("?1", String.valueOf(playerId));
		if (executeQuery(e_query, con))
			return;

		_separations.remove(_doSeparation);
	}

	private SubclassSeparation getSeparationInfo(int playerId)
	{
		SubclassSeparation _return = null;
		for (SubclassSeparation _ss : _separations)
		{
			if (_ss.getPlayerId() == playerId)
			{
				_return = _ss;
				break;
			}
		}
		return _return;
	}
	
	private int getNewCharacterId(String accountName)
	{
		Connection con = null;
		int newCharId = 0;
		String e_query = LOAD_NEW_CHARACTER_DATA.replace("?1","'" + accountName + "'");
		boolean error_ocurred = false;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement st = con.prepareStatement(e_query);
			ResultSet rset = st.executeQuery();
			while (rset.next())
			{
				newCharId = rset.getInt("charid");
			}
			rset.close();
		}
		catch(Exception e)
		{
			error_ocurred = true;
			_log.warning("Error while getting new character id from database (subclass split) " + e.toString());
		}
		if (error_ocurred)
		{
			try
			{
				con.rollback();
			}
			catch(java.sql.SQLException e)
			{
				_log.severe("Couldn't perform rollback.");
			}
			finally
			{
				L2DatabaseFactory.close(con);
			}
		}
		else
			L2DatabaseFactory.close(con);
		return newCharId;
	}

	private int getRace(String className)
	{
		String[] _fullClass = className.split("_");
		String _className = _fullClass[0];
		int raceId = 0;
		if (_className.equals("H"))
			raceId = 0;
		else if (_className.equals("E"))
			raceId = 1;
		else if (_className.equals("DE"))
			raceId = 2;
		else if (_className.equals("O"))
			raceId = 3;
		else if (_className.equals("D"))
			raceId = 4;
		return raceId;
	}

	private int getRaceByClassId(int classId)
	{
		Connection con = null;
		String class_name =  "";
		int raceId = -1;
		String e_query = LOAD_RACE_DATA.replace("?1", String.valueOf(classId));
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement st = con.prepareStatement(e_query);
			ResultSet rset = st.executeQuery();
			while (rset.next())
			{
				class_name = rset.getString("class_name");
			}
			rset.close();
			st.close();
		}
		catch(Exception e)
		{
			_log.warning("Error getting class_name by id of subclass " + e.toString());
		}
		finally
		{	
			L2DatabaseFactory.close(con);
		}
		raceId = getRace(class_name);
		return raceId;
	}
	private boolean executeQuery(String parQuery, Connection con)
	{
		boolean error_ocurred = false;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement st = con.prepareStatement(parQuery);
			st.execute();
			st.close();
		}
		catch(java.sql.SQLException e)
		{
			error_ocurred = true;
			_log.warning("Failed running executeQuery for query " + parQuery + "\n" + e.toString());
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		return error_ocurred;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final SubclassSeparationHandler _instance = new SubclassSeparationHandler();
	}
}
