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
import java.util.logging.Level;
import java.util.logging.Logger;

import l2.universe.L2DatabaseFactory;

public class RecordTable
{
	private final static Logger _log = Logger.getLogger(RecordTable.class.getName());
	
	private static RecordTable _instance;
	private int _maxPlayer = 0;
	private String _strDateMaxPlayer = null;
	
	public static RecordTable getInstance()
	{
		if (_instance == null)
			_instance = new RecordTable();
		return _instance;
	}
	
	private RecordTable()
	{
		restoreRecordData();
	}
	
	private void restoreRecordData()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement statement = con.prepareStatement("SELECT maxplayer, date FROM record ORDER by maxplayer desc limit 1");
			ResultSet recorddata = statement.executeQuery();
			fillRecordTable(recorddata);
			recorddata.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Error while restoring Record table: " + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	private void fillRecordTable(ResultSet Recorddata) throws Exception
	{
	while (Recorddata.next())
		{
			_maxPlayer = Recorddata.getInt("maxplayer");
			_strDateMaxPlayer = Recorddata.getString("date");
		}
	}
	
	public int getMaxPlayer()
	{
		return _maxPlayer;
	}

	public void setMaxPlayer(int maxPlayer)
	{
		_maxPlayer = maxPlayer;
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement statement = con.prepareStatement("REPLACE INTO record(maxplayer,date) VALUES(?,NOW())");
			statement.setInt(1, maxPlayer);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Error while adding info at Record table: " + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	public String getDateMaxPlayer()
	{
		return _strDateMaxPlayer;
	}
}