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

import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Playable;
import l2.universe.gameserver.model.actor.L2Summon;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.Duel;
import l2.universe.gameserver.skills.Stats;
import l2.universe.gameserver.util.Util;

public class SummonStatus extends PlayableStatus
{
	public SummonStatus(L2Summon activeChar)
	{
		super(activeChar);
	}

	@Override
	public void reduceHp(double value, L2Character attacker)
	{
		reduceHp(value, attacker, true, false, false);
	}

	@Override
	public void reduceHp(double value, L2Character attacker, boolean awake, boolean isDOT, boolean isHPConsumption)
	{
		if (getActiveChar().isDead())
			return;
		
		L2PcInstance owner = getActiveChar().getOwner();
		if (owner == null)
			return;

		if (attacker != null)
		{
			final L2PcInstance attackerPlayer = attacker.getActingPlayer();

			/* If a player or a npc hits the npc while in duel, interrupt it */
			if (owner.isInDuel())
			{
				if (attackerPlayer != null)
				{
					switch (owner.getDuelState())
					{
						case Duel.DUELSTATE_DEAD:
						case Duel.DUELSTATE_WINNER:
							return;							
					}
	
					// cancel duel if player got hit by another player, that is not part of the duel
					if (attackerPlayer.getDuelId() != owner.getDuelId())
						owner.setDuelState(Duel.DUELSTATE_INTERRUPTED);
				}
				else if (attacker instanceof L2Attackable)
				{
					switch (owner.getDuelState())
					{
						case Duel.DUELSTATE_DEAD:
						case Duel.DUELSTATE_WINNER:
							return;							
					}
	
					owner.setDuelState(Duel.DUELSTATE_INTERRUPTED);
				}
			}
		}
		
		/**
		 * Synerge - Complete support for Shield of Faith transfer damage and Betrayal Mark, both
		 * for Party, thanks l2dc
		 */
		// Transfer dmg from summon to player
		// checking if summon is under transferring dmg effect and if summon owner party is not null, checking if effect caster is not null.
		// checking if summon is near effect caster. (to avoid exploits)
		// checking if effect caster is not dead.
		// checking if summon owner party contains effect caster. (effect should trasnfer dmg only from party members)
		if (owner.getParty() != null)
		{
			final L2PcInstance caster = (L2PcInstance) getActiveChar().isTransferringDmgTo();
			if (getActiveChar().isTransferringDmg()
					&& caster != null 
					&& !caster.isDead()
					&& Util.checkIfInRange(1000, getActiveChar(), caster, true)					 
					&& owner.getParty().getPartyMembers().contains(caster))
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
						for (L2PcInstance m : owner.getParty().getPartyMembers())
						{
							if (Util.checkIfInRange(1000, getActiveChar(), m, true))
							{
								members++;
								if (m.getPet() != null)
									members++;
							}
						}
						
						transferDmg = transferDmg / (members-1);
						for (L2PcInstance member : getActiveChar().getParty().getPartyMembers())
						{
							if (member.getPet() != null && member.getPet() != getActiveChar())
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
						}
					}
				}
			}
		}
		
		super.reduceHp(value, attacker, awake, isDOT, isHPConsumption);
	}

	@Override
	public L2Summon getActiveChar()
	{
		return (L2Summon)super.getActiveChar();
	}
}
