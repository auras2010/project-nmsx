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
package l2.universe.gameserver.model.actor.status;

import l2.universe.Config;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.instancemanager.DuelManager;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Playable;
import l2.universe.gameserver.model.actor.L2Summon;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.actor.instance.L2SummonInstance;
import l2.universe.gameserver.model.actor.stat.PcStat;
import l2.universe.gameserver.model.entity.Duel;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ActionFailed;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.skills.Formulas;
import l2.universe.gameserver.skills.Stats;
import l2.universe.gameserver.util.Util;
import l2.universe.util.Rnd;

public class PcStatus extends PlayableStatus
{
	private double _currentCp = 0; //Current CP of the L2PcInstance

	public PcStatus(L2PcInstance activeChar)
	{
		super(activeChar);
	}

	@Override
	public final void reduceCp(int value)
	{
		if (getCurrentCp() > value)
			setCurrentCp(getCurrentCp() - value);
		else
			setCurrentCp(0);
	}

	@Override
	public final void reduceHp(double value, L2Character attacker)
	{
		reduceHp(value, attacker, true, false, false, false);
	}

	@Override
	public final void reduceHp(double value, L2Character attacker, boolean awake, boolean isDOT, boolean isHPConsumption)
	{
		reduceHp(value, attacker, awake, isDOT, isHPConsumption, false);
	}

	public final void reduceHp(double value, L2Character attacker, boolean awake, boolean isDOT, boolean isHPConsumption, boolean ignoreCP)
	{
		if (getActiveChar().isDead())
			return;

		if (getActiveChar().isInvul())
		{
			if (attacker == getActiveChar())
			{
				if (!isDOT && !isHPConsumption)
					return;
			}
			else
				return;
		}

		if (!isHPConsumption)
		{
			getActiveChar().stopEffectsOnDamage(awake);
			if (getActiveChar().isSitting())
				getActiveChar().standUp();
						
			if (!isDOT)
			{
				if (getActiveChar().isStunned() && Rnd.get(10) == 0)
					getActiveChar().stopStunning(true);
			}
			
			/* Synerge - Retail like feature. If you are in a private store, and you are hit, then the
			 * store is canceled. If its in offline, disconnect 
			 */
			if (getActiveChar().isInStoreMode())
            {
				getActiveChar().setPrivateStoreType(L2PcInstance.STORE_PRIVATE_NONE);
				getActiveChar().broadcastUserInfo();
				getActiveChar().standUp();
				
				if (getActiveChar().getClient() == null || getActiveChar().getClient().isDetached())
					getActiveChar().logout();
            } 
			else if (getActiveChar().isInCraftMode())
            {
				getActiveChar().isInCraftMode(false);
				getActiveChar().broadcastUserInfo();
				getActiveChar().standUp();
				
				if (getActiveChar().getClient() == null || getActiveChar().getClient().isDetached())
					getActiveChar().logout();
            }
		}

		int fullValue = (int) value;
		int tDmg = 0;

		if (attacker != null && attacker != getActiveChar())
		{
			final L2PcInstance attackerPlayer = attacker.getActingPlayer();

			if (attackerPlayer != null)
			{
				if (attackerPlayer.isGM() && !attackerPlayer.getAccessLevel().canGiveDamage())
					return;

				if (getActiveChar().isInDuel())
				{
					switch (getActiveChar().getDuelState())
					{
						case Duel.DUELSTATE_DEAD:
						case Duel.DUELSTATE_WINNER:
							return;
					}

					// cancel duel if player got hit by another player, that is not part of the duel
					if (attackerPlayer.getDuelId() != getActiveChar().getDuelId())
						getActiveChar().setDuelState(Duel.DUELSTATE_INTERRUPTED);
				}
			}
			else if (attacker instanceof L2Attackable)
			{
				/* Also cancel the duel if the player was hit by a npc */
				if (getActiveChar().isInDuel())
				{
					switch (getActiveChar().getDuelState())
					{
						case Duel.DUELSTATE_DEAD:
						case Duel.DUELSTATE_WINNER:
							return;							
					}

					getActiveChar().setDuelState(Duel.DUELSTATE_INTERRUPTED);
				}
			}

			// Check and calculate transfered damage
			final L2Summon summon = getActiveChar().getPet();
			//TODO correct range
			if (summon != null && summon instanceof L2SummonInstance
			        && Util.checkIfInRange(900, getActiveChar(), summon, true))
			{
				tDmg = (int)value * (int)getActiveChar().getStat().calcStat(Stats.TRANSFER_DAMAGE_PERCENT, 0, null, null) /100;

				// Only transfer dmg up to current HP, it should not be killed
				tDmg = Math.min((int)summon.getCurrentHp() - 1, tDmg);
				if (tDmg > 0)
				{
					summon.reduceCurrentHp(tDmg, attacker, null);
					value -= tDmg;
					fullValue = (int) value; // reduce the announced value here as player will get a message about summon damage
				}
			}
			
			/**
			 * Synerge - Complete support for Shield of Faith transfer damage and Betrayal Mark, both
			 * for Party, thanks l2dc
			 */
			// Transfer dmg from character to player.
			// checking if activeChar is under transferring dmg effect and if his party is not null, checking if effect caster is not null.
			// checking if activeChar is near effect caster. (to avoid exploits)
			// checking if effect caster is not dead and if activeChar is not a effect caster.
			// checking if activeChar party contains effect caster. (effect should trasnfer dmg only from party members)
			if (getActiveChar().getParty() != null)
			{
				final L2PcInstance caster = (L2PcInstance) getActiveChar().isTransferringDmgTo();
				if (getActiveChar().isTransferringDmg()
						&& caster != null && Util.checkIfInRange(1000, getActiveChar(), caster, true)
						&& !caster.isDead() && getActiveChar() != caster
						&& getActiveChar().getParty().getPartyMembers().contains(caster))
				{
					int transferDmg = 0;
					
					// Only transfer dmg up to current HP, it should not be killed
					transferDmg = (int) value * (int) getActiveChar().getStat().calcStat(Stats.TRANSFER_DAMAGE_PLAYER, 0, null, null) / 100;
					transferDmg = Math.min((int) caster.getCurrentHp() - 1, transferDmg);
					if (transferDmg > 0)
					{
						if (attacker instanceof L2Playable)
						{
							if (caster.getCurrentCp() > 0)
							{
								double cp = caster.getCurrentCp();
								if (cp > transferDmg)
								{
									reduceCp(transferDmg);
								}
								else
								{
									transferDmg = (int) (transferDmg - cp);
									reduceCp((int) cp);
								}
							}
						}
						
						caster.reduceCurrentHp(transferDmg, attacker, null);
						value -= transferDmg;
						fullValue = (int) value; // reduce the announced value here as player will get a message about summon damage
					}
				}
				else if (getActiveChar().isUnderBetrayalMark())
				{
					int transferDmg = 0;
					transferDmg = (int) value * (int) getActiveChar().getStat().calcStat(Stats.TRANSFER_DAMAGE_PLAYER, 0, null, null) / 100;
					if (transferDmg > 0)
					{
						if (attacker instanceof L2Playable)
						{
							int members = 0;
							for (L2PcInstance m : getActiveChar().getParty().getPartyMembers())
							{
								if (Util.checkIfInRange(1000, getActiveChar(), m, true))
								{
									members++;
									if (m.getPet() != null)
										members++;
								}
							}
							
							if(members > 0)
							{
								transferDmg = transferDmg / (members - 1);
								
								for (L2PcInstance member : getActiveChar().getParty().getPartyMembers())
								{
									if (member != getActiveChar())
									{
										if (member.getPet() != null)
										{
											int transferDmgPet = transferDmg;
											transferDmgPet = Math.min((int) member.getPet().getCurrentHp() - 1, transferDmg);
											member.getPet().reduceCurrentHp(transferDmgPet, attacker, null);
										}
										
										transferDmg = Math.min((int) member.getCurrentHp() - 1, transferDmg);
										if (member.getCurrentCp() > 0)
										{
											double cp = member.getCurrentCp();
											if (cp > transferDmg)
											{
												reduceCp(transferDmg);
											}
											else
											{
												transferDmg = (int) (transferDmg - cp);
												reduceCp((int) cp);
											}
										}
										member.reduceCurrentHp(transferDmg, attacker, null);
										value -= transferDmg;
										fullValue = (int) value; // reduce the announced value here as player will get a message about summon damage
									}
								}
							}
						}
					}
				}
			}

			if (attacker instanceof L2Playable)
			{
				if (getCurrentCp() >= value)
				{
					setCurrentCp(getCurrentCp() - value);   // Set Cp to diff of Cp vs value
					value = 0;                              // No need to subtract anything from Hp
				}
				else
				{
					value -= getCurrentCp();                // Get diff from value vs Cp; will apply diff to Hp
					setCurrentCp(0, false);                        // Set Cp to 0
				}
			}

			if (fullValue > 0 && !isDOT)
			{
				SystemMessage smsg;
				// Send a System Message to the L2PcInstance
				smsg = SystemMessage.getSystemMessage(SystemMessageId.C1_RECEIVED_DAMAGE_OF_S3_FROM_C2);
				smsg.addString(getActiveChar().getName());
				smsg.addCharName(attacker);
				smsg.addNumber(fullValue);
				getActiveChar().sendPacket(smsg);

				if (tDmg > 0)
				{
					smsg = SystemMessage.getSystemMessage(SystemMessageId.C1_RECEIVED_DAMAGE_OF_S3_FROM_C2);
					smsg.addString(getActiveChar().getPet().getName());
					smsg.addCharName(attacker);
					smsg.addNumber(tDmg);
					getActiveChar().sendPacket(smsg);

					if (attackerPlayer != null)
					{
						smsg = SystemMessage.getSystemMessage(SystemMessageId.GIVEN_S1_DAMAGE_TO_YOUR_TARGET_AND_S2_DAMAGE_TO_SERVITOR);
						smsg.addNumber(fullValue);
						smsg.addNumber(tDmg);
						attackerPlayer.sendPacket(smsg);
					}
				}
				smsg = null;
			}
		}

		if (value > 0)
		{
			value = getCurrentHp() - value;
			if (value <= 0)
			{
				if (getActiveChar().isInDuel())
				{
					getActiveChar().disableAllSkills();
					stopHpMpRegeneration();
					attacker.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
					attacker.sendPacket(ActionFailed.STATIC_PACKET);

					// let the DuelManager know of his defeat
					DuelManager.getInstance().onPlayerDefeat(getActiveChar());
					value = 1;
				}
				else
					value = 0;
			}
			setCurrentHp(value);
		}

		if (getActiveChar().getCurrentHp() < 0.5)
		{
			getActiveChar().abortAttack();
			getActiveChar().abortCast();

			if (getActiveChar().isInOlympiadMode())
			{
				stopHpMpRegeneration();
				getActiveChar().setIsDead(true);
				getActiveChar().setIsPendingRevive(true);
				if (getActiveChar().getPet() != null)
				{
					getActiveChar().getPet().abortAttack();
					getActiveChar().getPet().getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, null);
				}
				return;
			}

			getActiveChar().doDie(attacker);
			if (!Config.DISABLE_TUTORIAL)
			{
				final QuestState qs = getActiveChar().getQuestState("255_Tutorial");
				if (qs != null)
					qs.getQuest().notifyEvent("CE30", null, getActiveChar());
			}
		}
	}

	@Override
	public final void setCurrentHp(double newHp, boolean broadcastPacket)
	{
		super.setCurrentHp(newHp, broadcastPacket);

		if (!Config.DISABLE_TUTORIAL
				&& getCurrentHp() <= getActiveChar().getStat().getMaxHp() * .3)
		{
			final QuestState qs = getActiveChar().getQuestState("255_Tutorial");
			if (qs != null)
				qs.getQuest().notifyEvent("CE45", null, getActiveChar());
        }
	}

	@Override
	public final double getCurrentCp()
	{
		return _currentCp;
	}

	@Override
	public final void setCurrentCp(double newCp)
	{
		setCurrentCp(newCp, true);
	}

	public final void setCurrentCp(double newCp, boolean broadcastPacket)
	{
		// Get the Max CP of the L2Character
		int maxCp = getActiveChar().getStat().getMaxCp();

		synchronized (this)
		{
			if (getActiveChar().isDead())
				return;
			
			if (newCp < 0)
				newCp = 0;

			if (newCp >= maxCp)
			{
				// Set the RegenActive flag to false
				_currentCp = maxCp;
				_flagsRegenActive &= ~REGEN_FLAG_CP;

				// Stop the HP/MP/CP Regeneration task
				if (_flagsRegenActive == 0)
					stopHpMpRegeneration();
			}
			else
			{
				// Set the RegenActive flag to true
				_currentCp = newCp;
				_flagsRegenActive |= REGEN_FLAG_CP;

				// Start the HP/MP/CP Regeneration task with Medium priority
				startHpMpRegeneration();
			}
		}

		// Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform
		if (broadcastPacket)
			getActiveChar().broadcastStatusUpdate();
	}

	@Override
	protected void doRegeneration()
	{
		final PcStat charstat = getActiveChar().getStat();

		// Modify the current CP of the L2Character and broadcast Server->Client packet StatusUpdate
		if (getCurrentCp() < charstat.getMaxCp())
			setCurrentCp(getCurrentCp() + Formulas.calcCpRegen(getActiveChar()), false);

		// Modify the current HP of the L2Character and broadcast Server->Client packet StatusUpdate
		if (getCurrentHp() < charstat.getMaxHp())
			setCurrentHp(getCurrentHp() + Formulas.calcHpRegen(getActiveChar()), false);

		// Modify the current MP of the L2Character and broadcast Server->Client packet StatusUpdate
		if (getCurrentMp() < charstat.getMaxMp())
			setCurrentMp(getCurrentMp() + Formulas.calcMpRegen(getActiveChar()), false);

		getActiveChar().broadcastStatusUpdate(); //send the StatusUpdate packet
	}

	@Override
	public L2PcInstance getActiveChar()
	{
		return (L2PcInstance)super.getActiveChar();
	}
}
