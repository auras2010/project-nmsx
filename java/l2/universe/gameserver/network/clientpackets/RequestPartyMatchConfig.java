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
package l2.universe.gameserver.network.clientpackets;

import l2.universe.gameserver.model.PartyMatchRoom;
import l2.universe.gameserver.model.PartyMatchRoomList;
import l2.universe.gameserver.model.PartyMatchWaitingList;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ActionFailed;
import l2.universe.gameserver.network.serverpackets.ExPartyRoomMember;
import l2.universe.gameserver.network.serverpackets.ListPartyWating;
import l2.universe.gameserver.network.serverpackets.PartyMatchDetail;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

/**
 * This class ...
 *
 * @version $Revision: 1.1.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestPartyMatchConfig extends L2GameClientPacket
{
	private static final String _C__7F_REQUESTPARTYMATCHCONFIG = "[C] 7F RequestPartyMatchConfig";

	private int _auto, _loc, _lvl;

	@Override
	protected void readImpl()
	{
		_auto = readD();	//
		_loc = readD();		// Location
		_lvl = readD();		// my level
	}

 	@Override
 	protected void runImpl()
 	{
		L2PcInstance _activeChar = getClient().getActiveChar(); 
		if (_activeChar == null)
			return;

		if( !_activeChar.isInPartyMatchRoom() && _activeChar.getParty() != null && _activeChar.getParty().getLeader() != _activeChar)
		{
			_activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANT_VIEW_PARTY_ROOMS));
			_activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if(_activeChar.isInPartyMatchRoom())
		{
			// If Player is in Room show him room, not list
			PartyMatchRoomList _list = PartyMatchRoomList.getInstance();
			if (_list == null)
				return;
			
			PartyMatchRoom _room = _list.getPlayerRoom(_activeChar);
			if (_room == null)
				return;
			
			_activeChar.sendPacket(new PartyMatchDetail(_activeChar,_room));
			_activeChar.sendPacket(new ExPartyRoomMember(_activeChar, _room, 2));

			_activeChar.setPartyRoom(_room.getId());
			//_activeChar.setPartyMatching(1);
			_activeChar.broadcastUserInfo();
		}
		else
		{
			// Add to waiting list
			PartyMatchWaitingList.getInstance().addPlayer(_activeChar);

			// Send Room list
			_activeChar.sendPacket(new ListPartyWating(_activeChar,_auto,_loc,_lvl));
		}
 	}

	@Override
	public String getType()
	{
		return _C__7F_REQUESTPARTYMATCHCONFIG;
	}
}
