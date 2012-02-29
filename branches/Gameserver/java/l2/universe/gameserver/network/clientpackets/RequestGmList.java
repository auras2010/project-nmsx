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

import l2.universe.gameserver.GmListTable;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class handles RequestGmLista packet triggered by /gmlist command
 *
 * @version $Revision: 1.1.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestGmList extends L2GameClientPacket
{
	private static final String _C__81_REQUESTGMLIST = "[C] 81 RequestGmList";

	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		    return;
		
		GmListTable.getInstance().sendListToPlayer(activeChar);
	}

	@Override
	public String getType()
	{
		return _C__81_REQUESTGMLIST;
	}
}
