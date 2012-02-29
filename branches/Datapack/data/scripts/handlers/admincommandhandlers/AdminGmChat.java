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

import l2.universe.gameserver.GmListTable;
import l2.universe.gameserver.handler.IAdminCommandHandler;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.clientpackets.Say2;
import l2.universe.gameserver.network.serverpackets.CreatureSay;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

/**
 * This class handles following admin commands:
 * - gmchat text = sends text to all online GM's
 * - gmchat_menu text = same as gmchat, displays the admin panel after chat
 *
 * @version $Revision: 1.2.4.3 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminGmChat implements IAdminCommandHandler
{
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_gmchat",
		"admin_snoop",
		"admin_gmchat_menu"
	};
	
	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar)
	{
		if (command.startsWith("admin_gmchat"))
			handleGmChat(command, activeChar);
		else if (command.startsWith("admin_snoop"))
			snoop(command, activeChar);
		
		if (command.contains("_menu"))
			AdminHelpPage.showHelpPage(activeChar, "gm_menu.htm");
		
		return true;
	}
	
	/**
	 * @param command
	 * @param activeChar
	 */
	private void snoop(final String command, final L2PcInstance activeChar)
	{
		L2Object target = null;
		if (command.length() > 12)
		{
			target = L2World.getInstance().getPlayer(command.substring(12));
		}
		if (target == null)
			target = activeChar.getTarget();
		
		if (!(target instanceof L2PcInstance))
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}
		
		final L2PcInstance player = (L2PcInstance) target;
		player.addSnooper(activeChar);
		activeChar.addSnooped(player);
	}
		
	/**
	 * @param command
	 * @param activeChar
	 */
	private void handleGmChat(final String command, final L2PcInstance activeChar)
	{
		try
		{
			int offset = 0;
			if (command.startsWith("admin_gmchat_menu"))
				offset = 18;
			else
				offset = 13;
			final String text = command.substring(offset);
			final CreatureSay cs = new CreatureSay(0, Say2.ALLIANCE, activeChar.getName(), text);
			GmListTable.broadcastToGMs(cs);
		}
		catch (final StringIndexOutOfBoundsException e)
		{
			// empty message.. ignore
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
