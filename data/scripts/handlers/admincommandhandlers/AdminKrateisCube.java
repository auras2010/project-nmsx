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
package handlers.admincommandhandlers;

import java.util.StringTokenizer;

import l2.universe.gameserver.handler.model.AAdminCommandHandler;
import l2.universe.gameserver.instancemanager.KrateisCubeManager;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;

public class AdminKrateisCube extends AAdminCommandHandler
{
	private static int _kills = 0;
	
	private static final String[] ADMIN_COMMANDS = 
	{ 
		"admin_start_krateis_cube", 
		"admin_stop_krateis_cube", 
		"admin_register_krateis_cube", 
		"admin_unregister_krateis_cube", 
		"admin_add_krateis_cube_kills", 
		"admin_remove_krateis_cube_kills", 
		"admin_get_krateis_cube_kills" 
	};
	
	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String cmd = st.nextToken();
		
		if (cmd.equals("admin_start_krateis_cube"))
		{
			if (!KrateisCubeManager.getInstance().teleportToWaitRoom())
				activeChar.sendMessage("Not enough registered to start Krateis Cube.");
			return true;
		}
		/*else if (cmd.equals("stop_krateis_cube"))
		{
			new finishCube();
			return true;
		}*/

		if (activeChar.getTarget() instanceof L2PcInstance)
		{
			final L2PcInstance target = (L2PcInstance) activeChar.getTarget();			
			if (cmd.equals("admin_register_krateis_cube"))
			{
				if (!KrateisCubeManager.getInstance().registerPlayer(target))
					activeChar.sendMessage("This player is already registered.");
				else if (target == activeChar)
					activeChar.sendMessage("You have successfully registered for the next Krateis Cube match.");
				else
					target.sendMessage("An admin registered you for the next Krateis Cube match.");
			}
			else if (cmd.equals("admin_unregister_krateis_cube"))
			{
				if (!KrateisCubeManager.getInstance().removePlayer(target))
					activeChar.sendMessage("This player is not registered.");
				else
					target.sendMessage("An admin removed you from Krateis Cube playerlist.");
			}
			else if (cmd.equals("admin_add_krateis_cube_kills"))
			{
				try
				{
					_kills = Integer.parseInt(st.nextToken());
				}
				catch (final Exception e)
				{
					activeChar.sendMessage("Please specify the kills amount you want to add.");
				}
				
				if (KrateisCubeManager.getInstance().addKills(target, _kills))
				{
					target.sendMessage("An admin added " + _kills + " kills to your Krateis Cube kills.");
					activeChar.sendMessage("Added " + _kills + " kills to the player.");
				}
				else
					activeChar.sendMessage("This player does not exist in Krateis Cube playerlist.");
			}
			else if (cmd.equals("admin_get_krateis_cube_kills"))
			{
				if (!KrateisCubeManager.getInstance().isRegistered(target))
					activeChar.sendMessage("This player is not registered.");
				else
				{
					_kills = KrateisCubeManager.getInstance().getKills(target);
					activeChar.sendMessage("Player Krateis Cube kills: " + _kills + ".");
				}
			}
			return true;
		}
		else
		{
			activeChar.sendMessage("Target must be a player!");
			return false;
		}
	}
		
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
