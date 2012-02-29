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
package handlers.itemhandlers;

import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.actor.L2Playable;
import l2.universe.gameserver.model.actor.instance.L2AirShipInstance;
import l2.universe.gameserver.model.actor.instance.L2ControllableAirShipInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ActionFailed;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

public class EnergyStarStone extends ItemSkills
{
	@Override
	public void useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		
		final L2AirShipInstance ship = ((L2PcInstance) playable).getAirShip();
		if (ship == null
				|| !(ship instanceof L2ControllableAirShipInstance)
				|| ship.getFuel() >= ship.getMaxFuel())
		{
			playable.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addItemName(item));
			playable.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		super.useItem(playable, item, forceUse);
	}
}