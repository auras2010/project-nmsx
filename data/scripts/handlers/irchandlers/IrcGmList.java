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
package handlers.irchandlers;

import l2.universe.gameserver.GmListTable;
import l2.universe.gameserver.handler.IIrcCommandHandler;
import l2.universe.gameserver.instancemanager.IrcManager;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;

/**
 *
 * @author nBd
 */
public class IrcGmList implements IIrcCommandHandler
{
	private static final String[] IRC_COMMANDS = { "!gmlist" };
	
	/**
	 * @see net.sf.l2j.gameserver.handler.IIrcCommandHandler#getIrcCommandList()
	 */
	@Override
	public String[] getIrcCommandList()
	{
		
		return IRC_COMMANDS;
	}
	
	/**
	 * @see net.sf.l2j.gameserver.handler.IIrcCommandHandler#useIrcCommand(java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public boolean useIrcCommand(final String command, final String gm, final String channel, final boolean authed)
	{
		if (command.equalsIgnoreCase("!gmlist"))
		{
			final String target = channel == null ? gm : channel;
			if (GmListTable.getInstance().getAllGms(false).size() <= 0)
				IrcManager.getInstance().send(target, "There are not any GMs that are providing customer service currently");
			else
				for (final L2PcInstance player : GmListTable.getInstance().getAllGms(false))
					IrcManager.getInstance().send(target, player.getName());
		}
		return true;
	}
}
