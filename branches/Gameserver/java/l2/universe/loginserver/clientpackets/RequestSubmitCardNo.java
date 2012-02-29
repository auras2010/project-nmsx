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
package l2.universe.loginserver.clientpackets;

import l2.universe.Config;
import l2.universe.loginserver.L2LoginClient;
import l2.universe.loginserver.serverpackets.LoginOk;
import l2.universe.loginserver.serverpackets.ServerList;

/**
 * Analysis left for better times, since anyway it's too easy to counter as
 * a anti-emulator measure.
 * @author savormix
 */
public class RequestSubmitCardNo extends L2LoginClientPacket
{
	//private final byte[] _raw = new byte[128];

	@Override
	public boolean readImpl()
	{
		// always 151 bytes, despite what the input is
		if (super._buf.remaining() == 151)
		{
			//readB(_raw);
			return true;
		}
		else
			return false;
	}

	@Override
	public void run()
	{
		/* This definetly shouldn't be used, since it isn't a RSA crypted block
		byte[] decrypted = null;
		try
		{
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.DECRYPT_MODE, getClient().getRSAPrivateKey());
			decrypted = rsaCipher.doFinal(_raw, 0x00, 0x80);
		}
		catch (GeneralSecurityException e)
		{
			e.printStackTrace();
			return;
		}
		System.err.println(HexUtil.printData(decrypted));
		*/
		L2LoginClient client = getClient();
		client.setCardAuthed(true);
		if (Config.SHOW_LICENCE)
			client.sendPacket(new LoginOk(client.getSessionKey()));
		else
			client.sendPacket(new ServerList(client));
	}
}
