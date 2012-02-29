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

import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.model.actor.instance.L2NpcInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.util.Rnd;
import l2.universe.scripts.ai.L2AttackableAIScript;

public class BrekaOrcOverlord extends L2AttackableAIScript
{
	private static final int BREKA_ORC_OVERLORD = 20270;
	
	public BrekaOrcOverlord(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addAttackId(BREKA_ORC_OVERLORD);
	}
	
	public String onAttack(L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (npc.getNpcId() == BREKA_ORC_OVERLORD)
		{
			if (npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
			{
				if (Rnd.get(100) < 70)
					npc.broadcastNpcSay("Extreme strength! ! ! !");
			}
			else if (Rnd.get(100) < 10)
				npc.broadcastNpcSay("Humph, wanted to win me to be also in tender!");
			else if (Rnd.get(100) < 10)
				npc.broadcastNpcSay("Haven't thought to use this unique skill for this small thing!");
		}
		
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	public static void main(String[] args)
	{
		new BrekaOrcOverlord(-1, "BrekaOrcOverlord", "ai");
	}
}
