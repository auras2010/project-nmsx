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
package handlers.admincommandhandlers;

import l2.universe.gameserver.handler.IAdminCommandHandler;
import l2.universe.gameserver.instancemanager.TransformationManager;
import l2.universe.gameserver.model.L2Transformation;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

/**
 * @author 
 */
public class AdminRide implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_ride_horse",
		"admin_ride_bike",
		"admin_ride_wyvern",
		"admin_ride_strider",
		"admin_ride_gstrider",
		"admin_ride_wolf",
		"admin_ride_lion",
		"admin_ride_steam",
		"admin_unride"
	};
		
	private static final int WYRVERN_TRANSFORMATION_ID = 12621;
	private static final int STRIDER_TRANSFORMATION_ID = 12526;
	private static final int GUARDIAN_STRIDER_TRANSFORMATION_ID = 16068;
	private static final int GREAT_SNOW_WOLF_TRANSFORMATION_ID = 16042;
	private int _petRideId;
	
	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar)
	{		
		final L2PcInstance target;
		if (activeChar.getTarget() instanceof L2PcInstance)
			target = (L2PcInstance) activeChar.getTarget();
		else
			target = activeChar;
		
		if (command.startsWith("admin_ride"))
		{
			if (target.isMounted() || target.getPet() != null)
			{
				activeChar.sendMessage("Target already have a pet.");
				return false;
			}
			
			if (command.startsWith("admin_ride_wyvern"))
			{
				_petRideId = WYRVERN_TRANSFORMATION_ID;
			}
			else if (command.startsWith("admin_ride_strider"))
			{
				_petRideId = STRIDER_TRANSFORMATION_ID;
			}
			else if (command.startsWith("admin_ride_gstrider"))
			{
				_petRideId = GUARDIAN_STRIDER_TRANSFORMATION_ID;
			}
			else if (command.startsWith("admin_ride_wolf"))
			{
				_petRideId = GREAT_SNOW_WOLF_TRANSFORMATION_ID;
			}
			else if (command.startsWith("admin_ride_horse")) // handled using transformation
			{
				if (target.isTransformed() || target.isInStance())
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_TRANSFORMED));
				else
					TransformationManager.getInstance().transformPlayer(L2Transformation.PURPLE_MANED_HORSE_TRANSFORMATION_ID, target);
				
				return true;
			}
			else if (command.startsWith("admin_ride_lion")) // handled using transformation
			{
				if (target.isTransformed() || target.isInStance())
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_TRANSFORMED));
				else
					TransformationManager.getInstance().transformPlayer(L2Transformation.TAWNY_MANED_LION_TRANSFORMATION_ID, target);
				
				return true;
			}
			else if (command.startsWith("admin_ride_steam")) // handled using transformation
			{
				if (target.isTransformed() || target.isInStance())
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_TRANSFORMED));
				else
					TransformationManager.getInstance().transformPlayer(L2Transformation.STEAM_BEATLE_TRANSFORMATION_ID, target);
				
				return true;
			}
			else if (command.startsWith("admin_ride_bike")) // handled using transformation
			{
				if (target.isTransformed() || target.isInStance())
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_TRANSFORMED));
				else
					TransformationManager.getInstance().transformPlayer(L2Transformation.JET_BIKE_TRANSFORMATION_ID, target);
				
				return true;
			}
			else
			{
				activeChar.sendMessage("Command '" + command + "' not recognized");
				return false;
			}
			
			target.mount(_petRideId, 0, false);
			return false;
		}
		else if (command.startsWith("admin_unride"))
		{
			switch (target.getTransformationId())
			{
				case L2Transformation.PURPLE_MANED_HORSE_TRANSFORMATION_ID:
				case L2Transformation.JET_BIKE_TRANSFORMATION_ID:
				case L2Transformation.TAWNY_MANED_LION_TRANSFORMATION_ID:
				case L2Transformation.STEAM_BEATLE_TRANSFORMATION_ID:
					target.untransform();
					break;
				default:
					target.dismount();
					break;
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
