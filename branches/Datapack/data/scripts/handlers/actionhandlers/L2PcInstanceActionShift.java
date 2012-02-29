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
package handlers.actionhandlers;

import l2.universe.gameserver.handler.AdminCommandHandler;
import l2.universe.gameserver.handler.IActionHandler;
import l2.universe.gameserver.handler.IAdminCommandHandler;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Object.InstanceType;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.MyTargetSelected;
import l2.universe.gameserver.network.serverpackets.ValidateLocation;

public class L2PcInstanceActionShift implements IActionHandler
{
	public boolean action(final L2PcInstance activeChar, final L2Object target, final boolean interact)
	{
		if (activeChar.isGM())
		{
			// Check if the gm already target this l2pcinstance
			if (activeChar.getTarget() != target)
			{
				// Set the target of the L2PcInstance activeChar
				activeChar.setTarget(target);
				
				// Send a Server->Client packet MyTargetSelected to the L2PcInstance activeChar
				activeChar.sendPacket(new MyTargetSelected(target.getObjectId(), 0));
			}
			
			// Send a Server->Client packet ValidateLocation to correct the L2PcInstance position and heading on the client
			if (activeChar != target)
				activeChar.sendPacket(new ValidateLocation((L2Character) target));
			
			final IAdminCommandHandler ach = AdminCommandHandler.getInstance().getAdminCommandHandler("admin_character_info");
			if (ach != null)
				ach.useAdminCommand("admin_character_info " + target.getName(), activeChar);
		}
		return true;
	}
	
	public InstanceType getInstanceType()
	{
		return InstanceType.L2PcInstance;
	}
}