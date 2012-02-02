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

import l2.brick.gameserver.model.actor.instance.L2PcInstance;
import l2.brick.gameserver.network.serverpackets.EnchantResult;

/**
 * @author  KenM
 */
public class RequestExCancelEnchantItem extends L2GameClientPacket
{
	private static final String _C__D0_4E_REQUESTEXCANCELENCHANTITEM = "[C] D0:4E RequestExCancelEnchantItem";
	
	@Override
	protected void readImpl()
	{
		// nothing (trigger)
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = this.getClient().getActiveChar();
		if (activeChar != null)
		{
			activeChar.sendPacket(new EnchantResult(2, 0, 0));
			activeChar.setActiveEnchantItem(null);
		}
	}
	
	@Override
	public String getType()
	{
		return _C__D0_4E_REQUESTEXCANCELENCHANTITEM;
	}
}
