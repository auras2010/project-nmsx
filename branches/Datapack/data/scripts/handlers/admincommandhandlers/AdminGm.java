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

import java.util.logging.Logger;

import l2.universe.Config;
import l2.universe.gameserver.GmListTable;
import l2.universe.gameserver.datatables.AccessLevels;
import l2.universe.gameserver.handler.IAdminCommandHandler;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class handles following admin commands:
 * - gm = turns gm mode off
 *
 * @version $Revision: 1.2.4.4 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminGm implements IAdminCommandHandler
{
	private static Logger _log = Logger.getLogger(AdminGm.class.getName());
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_gm"
	};
	
	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar)
	{		
		if (!(activeChar.getTarget() instanceof L2PcInstance))
			return false;
		
		final L2PcInstance target = (L2PcInstance)activeChar.getTarget();
		
		if (command.equals("admin_gm"))
			handleGm(target);
		
		return true;
	}
		
	private void handleGm(final L2PcInstance target)
	{		
		if (target.isGM())
		{
			GmListTable.getInstance().deleteGm(target);
			target.setAccessLevel(AccessLevels._userAccessLevelNum);
			target.sendMessage("You no longer have GM status.");
			
			if (Config.DEBUG)
				_log.fine("GM: " + target.getName() + "(" + target.getObjectId() + ") turned his GM status off");
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
