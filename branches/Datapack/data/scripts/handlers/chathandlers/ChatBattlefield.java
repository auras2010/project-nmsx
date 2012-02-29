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
package handlers.chathandlers;

import java.util.Collection;

import l2.universe.gameserver.handler.IChatHandler;
import l2.universe.gameserver.instancemanager.TerritoryWarManager;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.CreatureSay;

/**
 * A chat handler
 *
 * @author  Gigiikun
 */
public class ChatBattlefield implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		20
	};
	
	/**
	 * Handle chat type 'battlefield'
	 */
	public void handleChat(int type, L2PcInstance activeChar, String target, String text)
	{
		if (TerritoryWarManager.getInstance().isTWChannelOpen() && activeChar.getSiegeSide() > 0)
		{
			final CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
			final Collection<L2PcInstance> pls = L2World.getInstance().getAllPlayers().values();
			if (pls != null)
			{
				for (L2PcInstance player : pls)
				{
					if (player.getSiegeSide() == activeChar.getSiegeSide())
						player.sendPacket(cs);
				}
			}
		}
	}
	
	/**
	 * Returns the chat types registered to this handler
	 */
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}
