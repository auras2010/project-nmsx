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
package l2.universe.gameserver.taskmanager.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.taskmanager.Task;
import l2.universe.gameserver.taskmanager.TaskManager;
import l2.universe.gameserver.taskmanager.TaskTypes;
import l2.universe.gameserver.taskmanager.TaskManager.ExecutedTask;

/**
 * @author BiggBoss
 */
public class TaskReportPointsRestore extends Task
{
	private static final String NAME = "report_points_restore";
	
	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement update = con.prepareStatement("UPDATE characters SET bot_report_points = 7");
			update.execute();
			update.close();
			System.out.println("Sucessfully restored Bot Report Points for all accounts!");
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "00:00:00", "");
	}
}