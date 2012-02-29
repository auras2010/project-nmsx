
package l2.universe.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;


import l2.universe.L2DatabaseFactory;


public class ItemsDbTask
{
	private static ItemsDbTask task = null;
	
	private static final Logger _log = Logger.getLogger(ItemsDbTask.class.getName());
	
	public static ItemsDbTask getInstance()
	{
		if(task == null)
			task = new ItemsDbTask();
		return task;
	}
	
	public void addItemToOfflineChar(String name, int ownerId, String Sender, int itemId, int Count, boolean isVitamine)
	{
		if(name == null)
			name = "";
		
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			final PreparedStatement statement = con.prepareStatement("INSERT INTO `character_premium_items` (`charId`, `charName`, `itemId`, `itemCount`, `itemSender`, `vitamine`) VALUES (?, ?, ?, ?, ?, ?);");
			statement.setInt(1, ownerId);
			statement.setString(2, name);
			statement.setInt(3, itemId);
			statement.setInt(4, Count);
			statement.setString(5, Sender);
			statement.setInt(6, isVitamine ? 1 : 0);
			statement.execute();
			statement.close();
		}
		catch (final Exception ex)
		{
			_log.log(Level.SEVERE, "Could not add item with addItemToOfflineChar: " + ex);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
}