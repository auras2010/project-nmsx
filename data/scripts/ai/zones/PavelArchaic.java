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
package ai.zones;

import l2.universe.scripts.ai.L2AttackableAIScript;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.util.Rnd;

public class PavelArchaic extends L2AttackableAIScript
{
	private static final int[] MOBS1 = { 22801, 22804 };
	private static final int[] MOBS2 = { 18917 };
	
	public PavelArchaic(int questId, String name, String descr)
	{
		super(questId, name, descr);
		registerMobs(MOBS1, QuestEventType.ON_KILL);
		registerMobs(MOBS2, QuestEventType.ON_ATTACK);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (!npc.isDead() && contains(MOBS2, npc.getNpcId()))
		{
			npc.doDie(attacker);
			
			if (Rnd.get(100) < 40)
			{
				final L2Attackable _golem1 = (L2Attackable) addSpawn(22801, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0);
				attackPlayer(_golem1, attacker);
				
				final L2Attackable _golem2 = (L2Attackable) addSpawn(22804, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0);
				attackPlayer(_golem2, attacker);
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (contains(MOBS1, npc.getNpcId()))
		{
			final L2Attackable _golem = (L2Attackable) addSpawn(npc.getNpcId() + 1, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0);
			attackPlayer(_golem, killer);
		}
		return super.onKill(npc, killer, isPet);
	}
	
	private void attackPlayer(L2Attackable npc, L2PcInstance player)
	{
		npc.setIsRunning(true);
		npc.addDamageHate(player, 0, 999);
		npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
	}
		
	public static void main(String[] args)
	{
		new PavelArchaic(-1, "PavelArchaic", "ai");
	}
}
