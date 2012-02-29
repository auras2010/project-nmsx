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
package l2.universe.loginserver;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.mmocore.network.IPacketHandler;
import org.mmocore.network.ReceivablePacket;

import l2.universe.Config;
import l2.universe.loginserver.L2LoginClient.LoginClientState;
import l2.universe.loginserver.clientpackets.AuthGameGuard;
import l2.universe.loginserver.clientpackets.RequestAuthLogin;
import l2.universe.loginserver.clientpackets.RequestServerList;
import l2.universe.loginserver.clientpackets.RequestServerLogin;
import l2.universe.loginserver.clientpackets.RequestSubmitCardNo;

/**
 * Handler for packets received by Login Server
 *
 * @author  KenM
 */
public final class L2LoginPacketHandler implements IPacketHandler<L2LoginClient>
{
    protected static final Logger _log = Logger.getLogger(L2LoginPacketHandler.class.getName());
    
	/**
	 * @see l2.universe.mmocore.network.IPacketHandler#handlePacket(java.nio.ByteBuffer, l2.universe.mmocore.interfaces.MMOClient)
	 */
	public ReceivablePacket<L2LoginClient> handlePacket(ByteBuffer buf, L2LoginClient client)
	{
		int opcode = buf.get() & 0xFF;

		ReceivablePacket<L2LoginClient> packet = null;
		LoginClientState state = client.getState();

		switch (state)
		{
			case CONNECTED:
				if (opcode == 0x07)
				{
					packet = new AuthGameGuard();
				}
				else
				{
					debugOpcode(opcode, state);
				}
				break;
			case AUTHED_GG:
				if (opcode == 0x00)
				{
					packet = new RequestAuthLogin();
				}
				else
				{
					debugOpcode(opcode, state);
				}
				break;
			case AUTHED_LOGIN:
				switch (opcode)
				{
					case 0x05:
						packet = new RequestServerList();
						break;
					case 0x02:
						packet = new RequestServerLogin();
						break;
					case 0x06:
						if (Config.SECURITY_CARD_LOGIN)
							packet = new RequestSubmitCardNo();
						break;
					default:
						debugOpcode(opcode, state);
						break;
				}
				break;
		}
		return packet;
	}

	private void debugOpcode(int opcode, LoginClientState state)
	{
		_log.info("Unknown Opcode: "+opcode+" for state: "+state.name());
	}
}
