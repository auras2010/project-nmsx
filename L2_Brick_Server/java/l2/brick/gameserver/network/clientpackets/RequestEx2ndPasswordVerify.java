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
package l2.brick.gameserver.network.clientpackets;

import l2.brick.Config;

/**
 * Format: (ch)S
 * S: numerical password
 * 
 * @author mrTJO
 */
public class RequestEx2ndPasswordVerify extends L2GameClientPacket
{
	private static final String _C__D0_AE_REQUESTEX2NDPASSWORDVERIFY = "[C] D0:AE RequestEx2ndPasswordVerify";
	
	//private static Logger _log = Logger.getLogger(RequestEx2ndPasswordVerify.class.getName());
	String _password;
	
	@Override
	protected void readImpl()
	{
		_password = readS();
	}
	
	@Override
	protected void runImpl()
	{
		if (!Config.SECOND_AUTH_ENABLED)
			return;
		
		getClient().getSecondaryAuth().checkPassword(_password, false);
	}
	
	/* (non-Javadoc)
	 * @see l2.brick.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__D0_AE_REQUESTEX2NDPASSWORDVERIFY;
	}
}
