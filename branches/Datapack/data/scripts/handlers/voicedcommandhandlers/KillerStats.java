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
package handlers.voicedcommandhandlers;

import l2.universe.gameserver.handler.IVoicedCommandHandler;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;
import l2.universe.util.StringUtil;


public class KillerStats implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS = { "killerStats" };

	
	/* (non-Javadoc)
	 * @see l2.universe.gameserver.handler.IVoicedCommandHandler#useVoicedCommand(java.lang.String, l2.universe.gameserver.model.actor.instance.L2PcInstance, java.lang.String)
	 */
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		if (command.equalsIgnoreCase("killerStats"))
		{
			if (activeChar.getKiller() == null)
				return false;

			final NpcHtmlMessage kStats = new NpcHtmlMessage(5);
			final StringBuilder replyMSG = StringUtil.startAppend(800, "<html><body><center><font color=\"LEVEL\">[ KILLER'S STATS ]</font></center><br><br>Statistics for killer: <font color=\"LEVEL\">", activeChar.getKiller().getName(), "</font><br>Current HP: [ <font color=\"FF0000\">", String.valueOf(activeChar.getKiller().getCurrentHp()), "</font> ]<br>	Current MP: [ <font color=\"FF0000\">", String.valueOf(activeChar.getKiller().getCurrentMp()), "</font> ]<br>Current CP: [ <font color=\"FF0000\">", String.valueOf(activeChar.getKiller().getCurrentCp()), "</font> ]<br>");

			replyMSG.append("</body></html>");
			kStats.setHtml(replyMSG.toString());
			activeChar.sendPacket(kStats);
			return true;
		}

		return false;
	}
	
	/* (non-Javadoc)
	 * @see l2.universe.gameserver.handler.IVoicedCommandHandler#getVoicedCommandList()
	 */
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
