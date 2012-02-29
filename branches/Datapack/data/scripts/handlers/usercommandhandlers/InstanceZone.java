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
package handlers.usercommandhandlers;

import java.util.Map;

import l2.universe.gameserver.handler.IUserCommandHandler;
import l2.universe.gameserver.instancemanager.InstanceManager;
import l2.universe.gameserver.instancemanager.InstanceManager.InstanceWorld;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

public class InstanceZone implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		114
	};
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
	
	@Override
	public boolean useUserCommand(final int id, final L2PcInstance activeChar)
	{
		if (id != COMMAND_IDS[0])
			return false;
		
		final InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(activeChar);
		if (world != null && world.templateId >= 0)
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.INSTANT_ZONE_CURRENTLY_INUSE_S1);
			sm.addString(InstanceManager.getInstance().getInstanceIdName(world.templateId));
			activeChar.sendPacket(sm);
			sm = null;
		}
		
		Map<Integer, Long> instanceTimes = InstanceManager.getInstance().getAllInstanceTimes(activeChar.getObjectId());
		boolean firstMessage = true;
		if (instanceTimes != null)
			for(int instanceId : instanceTimes.keySet())
			{
				final long remainingTime = (instanceTimes.get(instanceId) - System.currentTimeMillis()) / 1000;
				if (remainingTime > 60)
				{
					if (firstMessage)
					{
						firstMessage = false;
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.INSTANCE_ZONE_TIME_LIMIT));
					}
					
					final int hours = (int) (remainingTime / 3600);
					final int minutes = (int) ((remainingTime % 3600) / 60);
                   //tvt round
                   final SystemMessage sm;
                   if (instanceId != 500000)
                   {
                       sm = SystemMessage.getSystemMessage(SystemMessageId.AVAILABLE_AFTER_S1_S2_HOURS_S3_MINUTES);
                       sm.addString(InstanceManager.getInstance().getInstanceIdName(instanceId));
                       sm.addNumber(hours);
                       sm.addNumber(minutes);
                   }
                   else
                       sm = SystemMessage.sendString("Solo Instance will be available to re-use in " + hours + " hours and " + minutes + " minutes.");
					activeChar.sendPacket(sm);
				}
				else
					InstanceManager.getInstance().deleteInstanceTime(activeChar.getObjectId(), instanceId);
			}
		
		if (firstMessage)
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NO_INSTANCEZONE_TIME_LIMIT));
		return true;
	}
}
