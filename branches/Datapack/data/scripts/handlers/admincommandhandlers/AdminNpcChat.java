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
 * 
 * 
 */
package handlers.admincommandhandlers;

import javolution.text.TextBuilder;
import l2.universe.gameserver.handler.IAdminCommandHandler;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.CreatureSay;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

/**
 * 
 * @author Gabo
 */
public class AdminNpcChat implements IAdminCommandHandler 
{
	private static final String[] ADMIN_COMMANDS = 
	{ 
		"admin_npcchat",
		"admin_npcchat_menu" 
	};

	public boolean useAdminCommand(String command, L2PcInstance activeChar) 
	{
		if (command.startsWith("admin_npcchat"))
			handleNPChat(command, activeChar);
		if (command.startsWith("admin_npcchat_menu"))
			showNpcMenu(activeChar);
		
		return true;
	}

	private void handleNPChat(String command, L2PcInstance activeChar) 
	{
		try 
		{
			if (activeChar.getTarget() instanceof L2Npc) 
			{
				final L2Npc npc = (L2Npc)activeChar.getTarget();

				int offset = 0;
				if (command.startsWith("admin_npcchat_menu"))
					offset = 19;
				else
					offset = 14;
				final String text = command.substring(offset);
				final CreatureSay cs = new CreatureSay(npc.getObjectId(), 0, npc.getTemplate().getName(), text);
				npc.broadcastPacket(cs);
			} 
			else 
			{
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.INCORRECT_TARGET));
				return;
			}
		} 
		catch (Exception e) {}
	}
	
	public void showNpcMenu(final L2PcInstance activeChar)
	{
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		final TextBuilder replyMSG = new TextBuilder("<html><title>Npc Says</title><body>");
		replyMSG.append("Instructions:<br>Target the NPC and write whatever you want to make the NPC say.<br>");
		replyMSG.append("<center>Text:<multiedit var=\"text\" width=250 height=50></center>");
		replyMSG.append("<table width=\"160\">");
		replyMSG.append("<tr><td><center><button value=\"Say\" action=\"bypass -h admin_npcchat_menu $text\" width=80 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></td></tr>");
		replyMSG.append("</table></body></html>");
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
	
	public String[] getAdminCommandList() 
	{
		return ADMIN_COMMANDS;
	}
}
