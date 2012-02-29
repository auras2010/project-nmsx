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

import java.util.logging.Level;

import l2.universe.Config;
import l2.universe.gameserver.GameTimeController;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.datatables.MapRegionTable;
import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.handler.IUserCommandHandler;
import l2.universe.gameserver.instancemanager.GrandBossManager;
import l2.universe.gameserver.model.L2Effect;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.events.TvTEvent;
import l2.universe.gameserver.model.entity.events.TvTRoundEvent;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ActionFailed;
import l2.universe.gameserver.network.serverpackets.MagicSkillUse;
import l2.universe.gameserver.network.serverpackets.SetupGauge;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.util.Broadcast;

public class Escape implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		52
	};
	
	@Override
	public boolean useUserCommand(final int id, final L2PcInstance activeChar)
	{
		// Thanks nbd
		if (!TvTEvent.onEscapeUse(activeChar.getObjectId()))
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		// Check to see if the player is in a festival.
		if (activeChar.isFestivalParticipant())
		{
			activeChar.sendMessage("You may not use an escape command in a festival.");
			return false;
		}
		
		// Add by CTF Event
		if (activeChar.isInFunEvent())
		{
			activeChar.sendMessage("You may not escape from an Event.");
			return false;
		}
		
		// Check to see if player is in jail
		if (activeChar.isInJail())
		{
			activeChar.sendMessage("You can not escape from jail.");
			return false;
		}
		
		if (GrandBossManager.getInstance().getZone(activeChar) != null && !activeChar.isGM())
		{
			activeChar.sendMessage("You may not use an escape command in a Boss Zone.");
			return false;
		}
		
		if (!TvTRoundEvent.onEscapeUse(activeChar.getObjectId()))
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}

		
		boolean fearActive = false;
		for (L2Effect effect : activeChar.getAllEffects())
		{
			switch (effect.getEffectType())
			{
				case FEAR:
				{
					fearActive = true;
					break;
				}
			}
			if (fearActive)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.NO_UNSTUCK_PLEASE_SEND_PETITION);
				activeChar.sendPacket(sm);
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
		}

		final int unstuckTimer = (activeChar.getAccessLevel().isGm() ? 1000 : Config.UNSTUCK_INTERVAL * 1000);
		
		if (activeChar.isCastingNow() || activeChar.isMovementDisabled() || activeChar.isMuted()
				|| activeChar.isAlikeDead() || activeChar.isInOlympiadMode() || activeChar.inObserverMode() || activeChar.isCombatFlagEquipped())
			return false;
			
		activeChar.forceIsCasting(GameTimeController.getGameTicks() + unstuckTimer / GameTimeController.MILLIS_IN_TICK);
		
		final L2Skill escape = SkillTable.getInstance().getInfo(2099, 1); // 5 minutes escape
		final L2Skill GM_escape = SkillTable.getInstance().getInfo(2100, 1); // 1 second escape
		if (activeChar.getAccessLevel().isGm())
		{
			if (GM_escape != null)
			{
				activeChar.doCast(GM_escape);
				return true;
			}
			activeChar.sendMessage("You use Escape: 1 second.");
		}
		else if (Config.UNSTUCK_INTERVAL == 300 && escape != null)
		{
			activeChar.doCast(escape);
			return true;
		}
		else if (Config.UNSTUCK_INTERVAL > 100)
			activeChar.sendMessage("You use Escape: " + unstuckTimer / 60000 + " minutes.");
		else
			activeChar.sendMessage("You use Escape: " + unstuckTimer / 1000 + " seconds.");
		activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		
		//SoE Animation section
		activeChar.setTarget(activeChar);
		activeChar.disableAllSkills();
		
		final MagicSkillUse msk = new MagicSkillUse(activeChar, 1050, 1, unstuckTimer, 0);
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, msk, 810000/*900*/);
		activeChar.sendPacket(new SetupGauge(0, unstuckTimer));
		//End SoE Animation section
		
		// continue execution later
		activeChar.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(new EscapeFinalizer(activeChar), unstuckTimer));
		
		return true;
	}
	
	static class EscapeFinalizer implements Runnable
	{
		private final L2PcInstance _activeChar;
		
		EscapeFinalizer(final L2PcInstance activeChar)
		{
			_activeChar = activeChar;
		}
		
		@Override
		public void run()
		{
			if (_activeChar.isDead())
				return;
			
			_activeChar.setIsIn7sDungeon(false);
			_activeChar.enableAllSkills();
			_activeChar.setIsCastingNow(false);
			_activeChar.setInstanceId(0);
			
			try
			{
				_activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);
			}
			catch (final Exception e)
			{
				_log.log(Level.SEVERE, "", e);
			}
		}
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}