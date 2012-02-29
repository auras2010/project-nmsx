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
package ai.group_template;

import java.util.Map;

import javolution.util.FastMap;

import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.NpcSay;
import l2.universe.scripts.ai.L2AttackableAIScript;
import l2.universe.util.Rnd;

/**
 * 
 * @author Synerge
 */
public class PrisonGuards extends L2AttackableAIScript
{
	private final static int GUARD1 = 18367;
	private final static int GUARD2 = 18368;
	private final static int STAMP = 10013;
	private final static String[] GUARDVARS = { "1st", "2nd", "3rd", "4th" };
	private final static String qn = "IOPRace";
	
	private final static int SILENCE_SKILL = 4098;
	private final static int PETRIFICATION_SKILL = 4578;
	private final static int EVENT_TIMER = 5239;
	
	private boolean _firstAttacked = false;
	
	private final Map<L2Npc, Integer> _guards = new FastMap<L2Npc, Integer>();
	
	public PrisonGuards(int questId, String name, String descr)
	{
		super(questId, name, descr);
		final int[] mob = { GUARD1, GUARD2 };
		registerMobs(mob);
		
		// Place 1
		_guards.put(addSpawn(GUARD2, 160704, 184704, -3704, 49152, false, 0), 0);
		_guards.put(addSpawn(GUARD2, 160384, 184704, -3704, 49152, false, 0), 0);
		_guards.put(addSpawn(GUARD1, 160528, 185216, -3704, 49152, false, 0), 0);
		// Place 2
		_guards.put(addSpawn(GUARD2, 135120, 171856, -3704, 49152, false, 0), 1);
		_guards.put(addSpawn(GUARD2, 134768, 171856, -3704, 49152, false, 0), 1);
		_guards.put(addSpawn(GUARD1, 134928, 172432, -3704, 49152, false, 0), 1);
		// Place 3
		_guards.put(addSpawn(GUARD2, 146880, 151504, -2872, 49152, false, 0), 2);
		_guards.put(addSpawn(GUARD2, 146366, 151506, -2872, 49152, false, 0), 2);
		_guards.put(addSpawn(GUARD1, 146592, 151888, -2872, 49152, false, 0), 2);
		// Place 4
		_guards.put(addSpawn(GUARD2, 155840, 160448, -3352, 0, false, 0), 3);
		_guards.put(addSpawn(GUARD2, 155840, 159936, -3352, 0, false, 0), 3);
		_guards.put(addSpawn(GUARD1, 155578, 160177, -3352, 0, false, 0), 3);
		
		for (final L2Npc npc : _guards.keySet())
		{
			npc.setIsNoRndWalk(true);
			npc.setIsImmobilized(true);
			if (npc.getNpcId() == GUARD1)
			{
				npc.setIsInvul(true);
				npc.disableCoreAI(true);
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("Respawn"))
		{
			final L2Npc newGuard = addSpawn(npc.getNpcId(), npc.getSpawn().getLocx(), npc.getSpawn().getLocy(), npc.getSpawn().getLocz(), npc.getSpawn().getHeading(), false, 0);
			newGuard.setIsNoRndWalk(true);
			newGuard.setIsImmobilized(true);
			if (npc.getNpcId() == GUARD1)
			{
				newGuard.setIsInvul(true);
				newGuard.disableCoreAI(true);
			}
			
			final int place = _guards.get(npc);
			_guards.remove(npc);
			_guards.put(newGuard, place);
		}
		else if (event.equalsIgnoreCase("attackEnd"))
		{
			if (npc.getNpcId() == GUARD2)
			{
				if (npc.getX() != npc.getSpawn().getLocx() || npc.getY() != npc.getSpawn().getLocy())
				{
					npc.teleToLocation(npc.getSpawn().getLocx(), npc.getSpawn().getLocy(), npc.getSpawn().getLocz(), npc.getSpawn().getHeading(), false);
					npc.setIsImmobilized(true);
				}
				((L2Attackable) npc).getAggroList().clear();
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			}
		}
		
		return null;
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance player, L2Skill skill, L2Object[] targets, boolean isPet)
	{
		if (npc.getNpcId() == GUARD2)
		{
			final L2Character caster = isPet ? player.getPet() : player;	
			if (_firstAttacked && caster.getFirstEffect(EVENT_TIMER) == null)
			{
				if (caster.getFirstEffect(SILENCE_SKILL) == null)
					castDebuff(npc, caster, SILENCE_SKILL, isPet, false, true);
			}
		}
		
		return super.onSkillSee(npc, player, skill, targets, isPet);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (npc.getNpcId() == GUARD2)
		{
			final L2Character target = isPet ? player.getPet() : player;
			if (target.getFirstEffect(EVENT_TIMER) != null)
			{
				cancelQuestTimer("attackEnd", null, null);
				startQuestTimer("attackEnd", 180000, npc, null);
				
				npc.setIsImmobilized(false);
				npc.setTarget(target);
				npc.setRunning();
				((L2Attackable) npc).addDamageHate(target, 0, 999);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
			}
			else
			{
				if (npc.getX() != npc.getSpawn().getLocx() || npc.getY() != npc.getSpawn().getLocy())
				{
					npc.teleToLocation(npc.getSpawn().getLocx(), npc.getSpawn().getLocy(), npc.getSpawn().getLocz(), npc.getSpawn().getHeading(), false);
					npc.setIsImmobilized(true);
				}
				((L2Attackable) npc).getAggroList().remove(target);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				return null;
			}
		}
		
		return super.onAggroRangeEnter(npc, player, isPet);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet)
	{
		final L2Character attacker = isPet ? player.getPet() : player;
		
		_firstAttacked = true;
		
		if (attacker.getFirstEffect(EVENT_TIMER) == null)
		{
			if (attacker.getFirstEffect(PETRIFICATION_SKILL) == null)
				castDebuff(npc, attacker, PETRIFICATION_SKILL, isPet, true, false);
			
			npc.setTarget(null);
			((L2Attackable) npc).getAggroList().remove(attacker);
			((L2Attackable) npc).stopHating(attacker);
			((L2Attackable) npc).abortAttack();
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			return null;
		}
		
		switch (npc.getNpcId())
		{
			case GUARD1:
				if (Rnd.get(100) < 5)
				{
					if (player.getQuestState(qn) != null && player.getQuestState(qn).getInt(GUARDVARS[_guards.get(npc)]) != 1)
					{
						player.getQuestState(qn).set(GUARDVARS[_guards.get(npc)], "1");
						player.getQuestState(qn).giveItems(STAMP, 1);
					}
				}
				break;
			case GUARD2:
				cancelQuestTimer("attackEnd", null, null);
				startQuestTimer("attackEnd", 180000, npc, null);
				
				npc.setIsImmobilized(false);
				npc.setTarget(attacker);
				npc.setRunning();
				((L2Attackable) npc).addDamageHate(attacker, 0, 999);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
				break;
		}
		
		return super.onAttack(npc, player, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (_guards.containsKey(npc))
			startQuestTimer("Respawn", 20000, npc, null);
		
		return super.onKill(npc, player, isPet);
	}
	
	private void castDebuff(L2Npc npc, L2Character player, int effectId, boolean isSummon, boolean fromAttack, boolean isSpell)
	{
		if (fromAttack)
		{
			final
            /*
			 * 1800107 It's not easy to obtain.
			 * 1800108 You're out of your mind coming here...
			 */
			int msg = (npc.getNpcId() == GUARD1 ? 1800107 : 1800108);
			npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), msg));
		}
		
		final L2Skill skill = SkillTable.getInstance().getInfo(effectId, isSpell ? 9 : 1);
		if (skill != null)
		{
			npc.setTarget(isSummon ? player.getPet() : player);
			npc.doCast(skill);
		}
	}
	
	public static void main(String[] args)
	{
		new PrisonGuards(-1, "PrisonGuards", "ai");
	}
}
