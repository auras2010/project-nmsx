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

import l2.universe.gameserver.TaskPriority;
import l2.universe.gameserver.model.actor.instance.L2AirShipInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.ActionFailed;
import l2.universe.gameserver.network.serverpackets.ExMoveToLocationInAirShip;
import l2.universe.gameserver.network.serverpackets.StopMoveInVehicle;
import l2.universe.gameserver.templates.item.L2WeaponType;
import l2.universe.util.Point3D;

/**
 * format: ddddddd
 * X:%d Y:%d Z:%d OriginX:%d OriginY:%d OriginZ:%d
 * @author  GodKratos
 */
public class MoveToLocationInAirShip extends L2GameClientPacket
{
	private static final String _C__D0_20_MOVETOLOCATIONINAIRSHIP = "[C] D0:20 MoveToLocationInAirShip";

	private int _shipId;
	private Point3D _pos = new Point3D(0,0,0);
	private Point3D _origin_pos = new Point3D(0,0,0);

	public TaskPriority getPriority() { return TaskPriority.PR_HIGH; }

	@Override
	protected void readImpl()
	{
		_shipId = readD();
		int _x, _y, _z;
		_x = readD();
		_y = readD();
		_z = readD();
		_pos.setXYZ(_x, _y, _z);
		_x = readD();
		_y = readD();
		_z = readD();
		_origin_pos.setXYZ(_x, _y, _z);
	}


	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;

		if (_pos.getX() == _origin_pos.getX() && _pos.getY() == _origin_pos.getY() && _pos.getZ() == _origin_pos.getZ())
		{
			activeChar.sendPacket(new StopMoveInVehicle(activeChar, _shipId));
			return;
		}

		if (activeChar.isAttackingNow()
				&& activeChar.getActiveWeaponItem() != null
				&& (activeChar.getActiveWeaponItem().getItemType() == L2WeaponType.BOW))
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (activeChar.isSitting() || activeChar.isMovementDisabled())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (!activeChar.isInAirShip())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		final L2AirShipInstance airShip = activeChar.getAirShip();
		if (airShip.getObjectId() != _shipId)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		activeChar.setVehicle(airShip);
		activeChar.setInVehiclePosition(_pos);
		activeChar.broadcastPacket(new ExMoveToLocationInAirShip(activeChar));
	}

	@Override
	public String getType()
	{
		return _C__D0_20_MOVETOLOCATIONINAIRSHIP;
	}
}
