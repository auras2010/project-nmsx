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

import l2.universe.gameserver.network.serverpackets.ExReplyDominionInfo;
import l2.universe.gameserver.network.serverpackets.ExShowOwnthingPos;

/**
 *
 * @author  JIV
 */
public class RequestDominionInfo extends L2GameClientPacket
{
	@Override
	public String getType()
	{
		return "[C] D0:58 RequestDominionInfo";
	}

	@Override
	protected void readImpl()
	{
		// nothing
	}

	@Override
	protected void runImpl()
	{
		sendPacket(new ExReplyDominionInfo());
		sendPacket(new ExShowOwnthingPos());
	}

    @Override
	protected boolean triggersOnActionRequest()
    {
    	return false;
    }
}