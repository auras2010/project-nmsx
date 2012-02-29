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

import l2.universe.gameserver.handler.IItemHandler;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.actor.L2Playable;
import l2.universe.gameserver.model.actor.instance.L2DoorInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ActionFailed;

public class Keys implements IItemHandler
{
	public static final int INTERACTION_DISTANCE = 100;

	public void useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		L2PcInstance activeChar = (L2PcInstance) playable;
		L2Object target = activeChar.getTarget();

		if (target == null || !(target instanceof L2DoorInstance))
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		L2DoorInstance door = (L2DoorInstance) target;

		if (door.getOpen())
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (!(activeChar.isInsideRadius(door, INTERACTION_DISTANCE, false, false)))
		{
			activeChar.sendMessage("Too far.");
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		if (activeChar.getAbnormalEffect() > 0 || activeChar.isInCombat())
		{
			activeChar.sendMessage("You are currently enganged in combat.");
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (!playable.destroyItem("Consume", item.getObjectId(), 1, null, false))
			return;

		switch (item.getItemId())
		{
			case 8056: // Key of Splendor Room
				if ((door.getDoorId() != 23150001)
                        && (door.getDoorId() != 23150002)
						&& (door.getDoorId() != 23150003)
						&& (door.getDoorId() != 23150004))
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
			case 9685: // Gate Key: Darkness
				if (door.getDoorId() != 20260001)
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
			case 9686: // Gate Key: Destruction
				if (door.getDoorId() != 20260006)
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
			case 9687: // Gate Key: Blood
				if ((door.getDoorId() != 20260002) && (door.getDoorId() != 20260005))
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
			case 9694: // Secret Key
				if ((door.getDoorId() != 24220001)
						&& (door.getDoorId() != 24220002)
						&& (door.getDoorId() != 24220003)
						&& (door.getDoorId() != 24220004))
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
			case 9703: // Gate Key: Kamael
				if ((door.getDoorId() != 16200002))
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
			case 9704: // Gate Key: Archives
				if ((door.getDoorId() != 16200005))
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
			case 9705: // Gate Key: Observation
				if ((door.getDoorId() != 16200009))
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
			case 9706: // Gate Key: Spicula
				if ((door.getDoorId() != 16200003))
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
			case 9707: // Gate Key: Harkilgamed
				if ((door.getDoorId() != 16200007))
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
			case 9708: // Gate Key: Rodenpicula
				if ((door.getDoorId() != 16200008))
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
			case 9709: // Gate Key: Arviterre
				if ((door.getDoorId() != 16200010))
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
			case 9710: // Gate Key: Katenar
				if ((door.getDoorId() != 16200006))
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
			case 9711: // Gate Key: Prediction
				if ((door.getDoorId() != 16200011))
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
			case 9712: // Gate Key: Massive Cavern
				if ((door.getDoorId() != 16200012))
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
			case 10015: // Prison Gate Key
				if ((door.getDoorId() != 24220008))
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
			case 13150: // Pailaka's Devil Isle Key
				if ((door.getDoorId() != 22110100)
						&& (door.getDoorId() != 22110101)
						&& (door.getDoorId() != 22110102)
						&& (door.getDoorId() != 22110103)
						&& (door.getDoorId() != 22110104)
						&& (door.getDoorId() != 22110105))
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				door.openMe();
				break;
		}
	}
}
