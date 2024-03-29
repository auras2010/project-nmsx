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

import l2.brick.gameserver.datatables.HennaTreeTable;
import l2.brick.gameserver.model.actor.instance.L2PcInstance;
import l2.brick.gameserver.model.item.instance.L2HennaInstance;
import l2.brick.gameserver.network.serverpackets.HennaEquipList;

/**
 * @author Tempy
 */
public final class RequestHennaDrawList extends L2GameClientPacket
{
	private static final String _C__C3_REQUESTHENNADRAWLIST = "[C] C3 RequestHennaDrawList";
	
	// This is just a trigger packet...
	@SuppressWarnings("unused")
	private int _unknown;
	
	@Override
	protected void readImpl()
	{
		_unknown = readD(); // ??
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		L2HennaInstance[] henna = HennaTreeTable.getInstance().getAvailableHenna(activeChar.getClassId());
		activeChar.sendPacket(new HennaEquipList(activeChar, henna));
	}
	
	@Override
	public String getType()
	{
		return _C__C3_REQUESTHENNADRAWLIST;
	}
}