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
package ai.individual.monster;

import javolution.util.FastList;
import javolution.util.FastMap;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.scripts.ai.L2AttackableAIScript;

/**
 * 
 * @author Synerge
 */
public class ZombieGatekeepers extends L2AttackableAIScript
{
	private static final int ZOMBIE_GATEKEEPER = 22136;
	
	private static FastMap<Integer, FastList<L2Character>> _attackersList = new FastMap<Integer, FastList<L2Character>>();
	
	public ZombieGatekeepers(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addAttackId(ZOMBIE_GATEKEEPER);
		addAggroRangeEnterId(ZOMBIE_GATEKEEPER);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet, L2Skill skill)
	{
		final int npcObjId = npc.getObjectId();		
		final L2Character target = isPet ? attacker.getPet() : attacker;
		
		if (_attackersList.get(npcObjId) == null)
		{
			FastList<L2Character> player = new FastList<L2Character>();
			player.add(target);
			_attackersList.put(npcObjId, player);
		}
		else if (!_attackersList.get(npcObjId).contains(target))
			_attackersList.get(npcObjId).add(target);
		
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		// The guards attack players with less lvl than 73
		if (player.getLevel() < 73)
		{
			((L2Attackable) npc).addDamageHate(player, 0, 999);
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
			return super.onAggroRangeEnter(npc, player, isPet);
		}
		
		final int npcObjId = npc.getObjectId();		
		final L2Character target = isPet ? player.getPet() : player;
		
		final L2ItemInstance VisitorsMark = player.getInventory().getItemByItemId(8064);
		final L2ItemInstance FadedVisitorsMark = player.getInventory().getItemByItemId(8065);
		final L2ItemInstance PagansMark = player.getInventory().getItemByItemId(8067);
		
		final long mark1 = VisitorsMark == null ? 0 : VisitorsMark.getCount();
		final long mark2 = FadedVisitorsMark == null ? 0 : FadedVisitorsMark.getCount();
		final long mark3 = PagansMark == null ? 0 : PagansMark.getCount();
		
		if (mark1 == 0 && mark2 == 0 && mark3 == 0)
		{
			((L2Attackable) npc).addDamageHate(target, 0, 999);
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		}
		else
		{
			if (_attackersList.get(npcObjId) == null || !_attackersList.get(npcObjId).contains(target))
			{
				((L2Attackable) npc).getAggroList().remove(target);
				return null;
			}
			else
			{
				((L2Attackable) npc).addDamageHate(target, 0, 999);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
			}
		}
		
		return super.onAggroRangeEnter(npc, player, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		final int npcObjId = npc.getObjectId();
		if (_attackersList.get(npcObjId) != null)
			_attackersList.get(npcObjId).clear();
		
		return super.onKill(npc, killer, isPet);
	}
	
	public static void main(String[] args)
	{
		new ZombieGatekeepers(-1, "ZombieGatekeepers", "ai");
	}
}
