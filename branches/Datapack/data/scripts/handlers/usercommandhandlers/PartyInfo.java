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
package handlers.usercommandhandlers;

import l2.universe.gameserver.handler.IUserCommandHandler;
import l2.universe.gameserver.model.L2Party;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

/**
 * Support for /partyinfo command
 * Added by Tempy - 28 Jul 05
 */
public class PartyInfo implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		81
	};
	
	@Override
	public boolean useUserCommand(final int id, final L2PcInstance activeChar)
	{
		if (id != COMMAND_IDS[0])
			return false;
		
		if (!activeChar.isInParty())
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PARTY_INFORMATION));
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FRIEND_LIST_FOOTER));
			return false;
		}
		
		final L2Party playerParty = activeChar.getParty();
		final int memberCount = playerParty.getMemberCount();
		final int lootDistribution = playerParty.getLootDistribution();
		final String partyLeader = playerParty.getPartyMembers().get(0).getName();
		
		activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PARTY_INFORMATION));
		
		switch (lootDistribution)
		{
			case L2Party.ITEM_LOOTER:
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.LOOTING_FINDERS_KEEPERS));
				break;
			case L2Party.ITEM_ORDER:
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.LOOTING_BY_TURN));
				break;
			case L2Party.ITEM_ORDER_SPOIL:
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.LOOTING_BY_TURN_INCLUDE_SPOIL));
				break;
			case L2Party.ITEM_RANDOM:
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.LOOTING_RANDOM));
				break;
			case L2Party.ITEM_RANDOM_SPOIL:
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.LOOTING_RANDOM_INCLUDE_SPOIL));
				break;
		}
		
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PARTY_LEADER_C1);
		sm.addString(partyLeader);
		activeChar.sendPacket(sm);
		sm = null;
		
		activeChar.sendMessage("Members: " + memberCount + "/9");
		
		activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FRIEND_LIST_FOOTER));
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}