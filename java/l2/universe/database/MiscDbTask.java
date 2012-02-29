package l2.universe.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import l2.universe.L2DatabaseFactory;

public class MiscDbTask
{
	private static final Logger _log = Logger.getLogger(MiscDbTask.class.getName());
	private static MiscDbTask task = null;
	
	public static MiscDbTask getInstance()
	{
		if (task == null)
			task = new MiscDbTask();
		return task;
	}
	
	/**
	 * Updating Hellbound stats.
	 * 
	 * @param newTrust Trust points
	 * @param newLvl Hellbound level
	 */
	public void updateHellboundStats(int newTrust, int newLvl)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			final PreparedStatement statement = con.prepareStatement("UPDATE hellbound SET trustLevel=?,zonesLevel=? WHERE name='HellBound'");
			statement.setInt(1, newTrust);
			statement.setInt(2, newLvl);
			statement.execute();
			statement.close();
		}
		catch (final SQLException e)
		{
			_log.warning("HellboundManager: Could not save Hellbound stats");
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Getting Hellbound level and trust points from database.
	 * 
	 * @return int array
	 */
	public int[] getHellboundStats()
	{
		final int[] obj = { 0, 0 };
		
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			final PreparedStatement statement = con.prepareStatement("SELECT trustLevel,zonesLevel FROM hellbound");
			final ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				obj[0] = rset.getInt("trustLevel");
				obj[1] = rset.getInt("zonesLevel");
			}
			rset.close();
			statement.close();
		}
		catch (final SQLException e)
		{
			_log.warning("HellboundManager: Could not load the hellbound table");
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
		return obj;
	}
	
	/**
	 * Unlocking Hellbound
	 */
	public void unlockHellbound()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			final PreparedStatement statement = con.prepareStatement("UPDATE hellbound SET unlocked=? WHERE name='HellBound'");
			statement.setInt(1, 1);
			statement.execute();
			statement.close();
		}
		catch (final SQLException e)
		{
			_log.warning("HellboundManager: Could not open Hellbound");
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Check if Hellbound is lock.
	 * 
	 * @return true if Hellbound is Open
	 */
	public boolean checkHellbound()
	{
		boolean lock = false;
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			final PreparedStatement statement = con.prepareStatement("SELECT unlocked FROM hellbound WHERE name='HellBound'");
			final ResultSet rset = statement.executeQuery();
			
			while (rset.next())
				lock = rset.getBoolean("unlocked");
			rset.close();
			statement.close();
		}
		catch (final SQLException e)
		{
			_log.warning("HellboundManager: Could not open Hellbound");
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
		return lock;
	}
}
