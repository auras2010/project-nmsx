package l2.universe.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.model.L2Clan;

public class ClanDbTask
{
	private static ClanDbTask task = null;

	private static final Logger _log = Logger.getLogger(ClanDbTask.class.getName());

	public static ClanDbTask getInstance()
	{
		if(task == null)
		{
			task = new ClanDbTask();
		}
		return task;
	}

	public void convertOldPledgeFiles(int newId, L2Clan clan)
	{
		Connection con = null;
		
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET crest_id = ? WHERE clan_id = ?");
			statement.setInt(1, newId);
			statement.setInt(2, clan.getClanId());
			statement.executeUpdate();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "Could not update the crest id:" + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
}