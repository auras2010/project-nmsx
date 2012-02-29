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

import java.util.Collection;
import java.util.StringTokenizer;

import l2.universe.gameserver.handler.IAdminCommandHandler;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;

public class AdminKick implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_kick",
		"admin_kick_non_gm"
	};
	
	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar)
	{
		if (command.startsWith("admin_kick_non_gm"))
		{
			int counter = 0;
			final Collection<L2PcInstance> pls = L2World.getInstance().getAllPlayers().values();
			for (final L2PcInstance player : pls)
			{
				if (player != null && !player.isGM())
				{
					counter++;
					player.logout();
				}
			}
			activeChar.sendMessage("Kicked " + counter + " players");
		}
		else if (command.startsWith("admin_kick"))
		{
			final StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				final String player = st.nextToken();
				final L2PcInstance plyr = L2World.getInstance().getPlayer(player);
				if (plyr != null)
				{
					plyr.logout();
					activeChar.sendMessage("You kicked " + plyr.getName() + " from the game.");
				}
			}
		}

		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}