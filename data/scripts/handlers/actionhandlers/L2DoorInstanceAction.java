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

import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.handler.IActionHandler;
import l2.universe.gameserver.model.L2Clan;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Object.InstanceType;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2DoorInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ConfirmDlg;
import l2.universe.gameserver.network.serverpackets.MyTargetSelected;
import l2.universe.gameserver.network.serverpackets.StaticObject;
import l2.universe.gameserver.network.serverpackets.ValidateLocation;

public class L2DoorInstanceAction implements IActionHandler
{
	public boolean action(final L2PcInstance activeChar, final L2Object target, final boolean interact)
	{
		final L2DoorInstance door = (L2DoorInstance)target;
		
		// Check if the L2PcInstance already target the L2NpcInstance
		if (activeChar.getTarget() != target)
		{
			// Set the target of the L2PcInstance activeChar
			activeChar.setTarget(target);
			
			// Send a Server->Client packet MyTargetSelected to the L2PcInstance activeChar
			activeChar.sendPacket(new MyTargetSelected(target.getObjectId(), 0));
			
			final StaticObject su;
			// Send HP amount if doors are inside castle/fortress zone
			// TODO: needed to be added here doors from conquerable clanhalls
			if ((door.getCastle() != null && door.getCastle().getCastleId() > 0)
					|| (door.getFort() != null && door.getFort().getFortId() > 0 && !door.getIsCommanderDoor()))
				su = new StaticObject(door, true);
			else
				su = new StaticObject(door, false);
			
			activeChar.sendPacket(su);
			
			// Send a Server->Client packet ValidateLocation to correct the L2NpcInstance position and heading on the client
			activeChar.sendPacket(new ValidateLocation(door));
		}
		else if (interact)
		{
			if (target.isAutoAttackable(activeChar))
			{
				if (Math.abs(activeChar.getZ() - target.getZ()) < 400) // this max heigth difference might need some tweaking
					activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
			}
			else if (activeChar.getClan() != null)
			{
				if (door.getClanHall() != null)
				{
					if (activeChar.getClanId() == door.getClanHall().getOwnerId())
					{
						if (!door.isInsideRadius(activeChar, L2Npc.INTERACTION_DISTANCE, false, false))
							activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, target);
						else
						{
							// Synerge - Addings right privileges check for door control
							if ((activeChar.getClanPrivileges() & L2Clan.CP_CH_OPEN_DOOR) == L2Clan.CP_CH_OPEN_DOOR)
							{
								activeChar.gatesRequest(door);
								if (!door.getOpen())
									activeChar.sendPacket(new ConfirmDlg(SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId()));
								else
									activeChar.sendPacket(new ConfirmDlg(SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId()));
							}
						}
					}
				}
				else if (door.getFort() != null	&& door.isUnlockable()
						&& activeChar.getClan() == door.getFort().getOwnerClan()
						&& !door.getFort().getSiege().getIsInProgress())
				{
					if (!door.isInsideRadius(activeChar, L2Npc.INTERACTION_DISTANCE, false, false))
						activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, target);						
					else
					{
						// Synerge - Addings right privileges check for door control
						if ((activeChar.getClanPrivileges() & L2Clan.CP_CS_OPEN_DOOR) == L2Clan.CP_CS_OPEN_DOOR)
						{
							activeChar.gatesRequest(door);
							if (!door.getOpen())
								activeChar.sendPacket(new ConfirmDlg(SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId()));
							else
								activeChar.sendPacket(new ConfirmDlg(SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId()));
						}
					}
				}
			}
		}
		return true;
	}
	
	public InstanceType getInstanceType()
	{
		return InstanceType.L2DoorInstance;
	}
}
