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
package l2.universe.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.instancemanager.CastleManager;
import l2.universe.gameserver.model.entity.Castle;

public class CastleFortressDbTask
{
	private static CastleFortressDbTask task = null;
	
	private static final Logger _log = Logger.getLogger(CastleFortressDbTask.class.getName());
	
	public static CastleFortressDbTask getInstance()
	{
		if(task == null)
			task = new CastleFortressDbTask();
		return task;
	}
	
	public void loadCastleInstances(CastleManager manager)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			final PreparedStatement statement = con.prepareStatement("SELECT id FROM castle ORDER BY id");
			final ResultSet rs = statement.executeQuery();
			
			while (rs.next())
				manager.getCastles().add(new Castle(rs.getInt("id")));
			
			rs.close();
			statement.close();
			
			_log.info("Loaded: " + manager.getCastles().size() + " castles");
		}
		catch (final Exception e)
		{
			_log.log(Level.WARNING, "Exception: loadCastleData(): " + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
}