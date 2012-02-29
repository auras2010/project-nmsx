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
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Object.InstanceType;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.L2Event;
import l2.universe.gameserver.model.entity.events.CTF;
import l2.universe.gameserver.model.entity.events.DM;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.network.serverpackets.ActionFailed;
import l2.universe.gameserver.network.serverpackets.MyTargetSelected;
import l2.universe.gameserver.network.serverpackets.StatusUpdate;
import l2.universe.gameserver.network.serverpackets.ValidateLocation;
import l2.universe.util.Rnd;

public class L2NpcAction implements IActionHandler
{
	/**
	 * Manage actions when a player click on the L2NpcInstance.<BR><BR>
	 *
	 * <B><U> Actions on first click on the L2NpcInstance (Select it)</U> :</B><BR><BR>
	 * <li>Set the L2NpcInstance as target of the L2PcInstance player (if necessary)</li>
	 * <li>Send a Server->Client packet MyTargetSelected to the L2PcInstance player (display the select window)</li>
	 * <li>If L2NpcInstance is autoAttackable, send a Server->Client packet StatusUpdate to the L2PcInstance in order to update L2NpcInstance HP bar </li>
	 * <li>Send a Server->Client packet ValidateLocation to correct the L2NpcInstance position and heading on the client </li><BR><BR>
	 *
	 * <B><U> Actions on second click on the L2NpcInstance (Attack it/Intercat with it)</U> :</B><BR><BR>
	 * <li>Send a Server->Client packet MyTargetSelected to the L2PcInstance player (display the select window)</li>
	 * <li>If L2NpcInstance is autoAttackable, notify the L2PcInstance AI with AI_INTENTION_ATTACK (after a height verification)</li>
	 * <li>If L2NpcInstance is NOT autoAttackable, notify the L2PcInstance AI with AI_INTENTION_INTERACT (after a distance verification) and show message</li><BR><BR>
	 *
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Each group of Server->Client packet must be terminated by a ActionFailed packet in order to avoid
	 * that client wait an other packet</B></FONT><BR><BR>
	 *
	 * <B><U> Example of use </U> :</B><BR><BR>
	 * <li> Client packet : Action, AttackRequest</li><BR><BR>
	 *
	 * @param activeChar The L2PcInstance that start an action on the L2NpcInstance
	 *
	 */
	public boolean action(final L2PcInstance activeChar, final L2Object target, final boolean interact)
	{
		final L2Npc targetNpc = (L2Npc) target;
		if (!targetNpc.canTarget(activeChar))
			return false;
		
		activeChar.setLastFolkNPC(targetNpc);
		
		// Check if the L2PcInstance already target the L2NpcInstance
		if (target != activeChar.getTarget())
		{
			// Set the target of the L2PcInstance activeChar
			activeChar.setTarget(target);
			
			// Check if the activeChar is attackable (without a forced attack)
			if (target.isAutoAttackable(activeChar))
			{
				// Send a Server->Client packet MyTargetSelected to the L2PcInstance activeChar
				// The activeChar.getLevel() - getLevel() permit to display the correct color in the select window
				activeChar.sendPacket(new MyTargetSelected(target.getObjectId(), activeChar.getLevel() - targetNpc.getLevel()));
				
				// Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
				final StatusUpdate su = new StatusUpdate(target);
				su.addAttribute(StatusUpdate.CUR_HP, (int) targetNpc.getCurrentHp());
				su.addAttribute(StatusUpdate.MAX_HP, targetNpc.getMaxHp());
				activeChar.sendPacket(su);
			}
			else
				// Send a Server->Client packet MyTargetSelected to the L2PcInstance activeChar
				activeChar.sendPacket(new MyTargetSelected(target.getObjectId(), 0));
			
			// Send a Server->Client packet ValidateLocation to correct the L2NpcInstance position and heading on the client
			activeChar.sendPacket(new ValidateLocation(targetNpc));
		}
		else if (interact)
		{
			activeChar.sendPacket(new ValidateLocation(targetNpc));
			
			// Check if the activeChar is attackable (without a forced attack) and isn't dead
			if (target.isAutoAttackable(activeChar))
			{
				if (!targetNpc.isAlikeDead())
				{
					// Check the height difference
					if (Math.abs(activeChar.getZ() - target.getZ()) < 400)
						// Set the L2PcInstance Intention to AI_INTENTION_ATTACK
						activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
					// activeChar.startAttack(this);
					else
						// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
						activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				}
			}
			else // Calculate the distance between the L2PcInstance and the L2NpcInstance
			{
			if (!targetNpc.canInteract(activeChar))
				// Notify the L2PcInstance AI with AI_INTENTION_INTERACT
				activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, target);
			else
			{
				if (targetNpc.hasRandomAnimation())
					targetNpc.onRandomAnimation(Rnd.get(8));
					
					// Open a chat window on client with the text of the L2NpcInstance
					if (targetNpc.isEventMob)
						L2Event.showEventHtml(activeChar, String.valueOf(target.getObjectId()));
					// CTF MOD - start
					else if (targetNpc._isEventMobCTF)
						CTF.showEventHtml(activeChar, String.valueOf(target.getObjectId()));
					else if (targetNpc._isCTF_Flag && activeChar._inEventCTF)
						CTF.showFlagHtml(activeChar, String.valueOf(target.getObjectId()), targetNpc._CTF_FlagTeamName);
					else if (targetNpc._isCTF_throneSpawn)
						CTF.CheckRestoreFlags();
					//CTF MOD - end
					else if (targetNpc._isEventMobDM)
						DM.showEventHtml(activeChar, String.valueOf(target.getObjectId()));
					else
				{
					final Quest[] qlsa = targetNpc.getTemplate().getEventQuests(Quest.QuestEventType.QUEST_START);
					if (qlsa != null && qlsa.length > 0)
						activeChar.setLastQuestNpcObject(target.getObjectId());
					final Quest[] qlst = targetNpc.getTemplate().getEventQuests(Quest.QuestEventType.ON_FIRST_TALK);
					if (qlst != null && qlst.length == 1)
						qlst[0].notifyFirstTalk(targetNpc, activeChar);
					else
						targetNpc.showChatWindow(activeChar);
				}
			}
			}
		}
		return true;
	}
	
	public InstanceType getInstanceType()
	{
		return InstanceType.L2Npc;
	}
}
