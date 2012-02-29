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
package l2.universe.gameserver.model.actor.instance;

import l2.universe.Config;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ActionFailed;
import l2.universe.gameserver.network.serverpackets.MyTargetSelected;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.network.serverpackets.ValidateLocation;
import l2.universe.gameserver.templates.chars.L2NpcTemplate;



public class L2FortBallistaInstance extends L2Npc
{
	public L2FortBallistaInstance(int objectId, L2NpcTemplate template)
    {
        super(objectId, template);
        setInstanceType(InstanceType.L2FortBallistaInstance);
    }
	
    @Override
    public boolean isAutoAttackable(L2Character attacker)
    {
        return true;
    }

    @Override
    public boolean doDie(L2Character killer)
    {
        if (!super.doDie(killer))
            return false;
        
        if (!getFort().getSiege().getIsInProgress())
        	return true;

        if (killer instanceof L2PcInstance)
        {
        	final L2PcInstance player = ((L2PcInstance)killer);
        	if (player.getClan() != null && player.getClan().getLevel() >= 5)
        	{
        		player.getClan().addReputationScore(Config.BALLISTA_POINTS, true);
            	player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.BALLISTA_DESTROYED_CLAN_REPU_INCREASED));
        	}
        }
		
        return true;
    }
    
    @Override
	public void onAction(L2PcInstance player, boolean interact)
    {
        if (!canTarget(player)) 
        	return;
        
        player.setLastFolkNPC(this);
        
        // Check if the L2PcInstance already target the L2NpcInstance
        if (this != player.getTarget())
        {
            // Set the target of the L2PcInstance player
            player.setTarget(this);

            // Send a Server->Client packet MyTargetSelected to the L2PcInstance player
            player.sendPacket(new MyTargetSelected(getObjectId(), 0));

            // Send a Server->Client packet ValidateLocation to correct the L2NpcInstance position and heading on the client
            player.sendPacket(new ValidateLocation(this));
        }
        else if (interact)
        {
			if (isAutoAttackable(player) && !isAlikeDead())
			{
				if (Math.abs(player.getZ() - getZ()) < 600) // this max heigth difference might need some tweaking
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
			}
			else if (!canInteract(player))
            {
                // Notify the L2PcInstance AI with AI_INTENTION_INTERACT
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
            }
			else
			{
				// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
        }
    }

    @Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
}
