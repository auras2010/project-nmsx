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
package l2.brick.gameserver.network.gameserverpackets;

import l2.brick.gameserver.LoginServerThread.SessionKey;
import l2.brick.util.network.BaseSendablePacket;


/**
 * @author -Wooden-
 *
 */
public class PlayerAuthRequest extends BaseSendablePacket
{
	public PlayerAuthRequest(String account, SessionKey key)
	{
		writeC(0x05);
		writeS(account);
		writeD(key.playOkID1);
		writeD(key.playOkID2);
		writeD(key.loginOkID1);
		writeD(key.loginOkID2);
	}
	
	/* (non-Javadoc)
	 * @see l2.brick.gameserver.gameserverpackets.GameServerBasePacket#getContent()
	 */
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
	
}