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

import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.scripts.ai.L2AttackableAIScript;
import l2.universe.util.Rnd;

/**
 * Hot Spring Disease AI
 * 
 * @author devO, Synerge
 */
public class HotSpringDisease extends L2AttackableAIScript
{
	private static final int[] DISEASE_MOBS1 = { 21314, 21316, 21317, 21319, 21321, 21322 }; // Monsters which cast Hot Spring Malaria (4554)
	private static final int[] DISEASE_MOBS2 = { 21317, 21322 }; // Monsters which cast Hot Springs Flu (4553)
	private static final int[] DISEASE_MOBS3 = { 21316, 21319 }; // Monsters which cast Hot Springs Cholera (4552)
	private static final int[] DISEASE_MOBS4 = { 21314, 21321 }; // Monsters which cast Hot Springs Rheumatism (4551)
	
	// Chance to get infected by disease
	private static final int DISEASE_CHANCE = 5;
	
	public HotSpringDisease(int questId, String name, String descr)
	{
		super(questId, name, descr);

		registerMobs(DISEASE_MOBS1, QuestEventType.ON_ATTACK);
		registerMobs(DISEASE_MOBS2, QuestEventType.ON_ATTACK);
		registerMobs(DISEASE_MOBS3, QuestEventType.ON_ATTACK);
		registerMobs(DISEASE_MOBS4, QuestEventType.ON_ATTACK);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (npc.isCastingNow())
			return super.onAttack(npc, attacker, damage, isPet);
		
		if (contains(DISEASE_MOBS1, npc.getNpcId()))
		{
			if (Rnd.get(100) < DISEASE_CHANCE)
			{
				npc.setTarget(attacker);
				npc.doCast(SkillTable.getInstance().getInfo(4554, Rnd.get(10) + 1));
				return super.onAttack(npc, attacker, damage, isPet);
			}
		}
		
		if (contains(DISEASE_MOBS2, npc.getNpcId()))
		{
			if (Rnd.get(100) < DISEASE_CHANCE)
			{
				npc.setTarget(attacker);
				npc.doCast(SkillTable.getInstance().getInfo(4553, Rnd.get(10) + 1));
				return super.onAttack(npc, attacker, damage, isPet);
			}
		}
		
		if (contains(DISEASE_MOBS3, npc.getNpcId()))
		{
			if (Rnd.get(100) < DISEASE_CHANCE)
			{
				npc.setTarget(attacker);
				npc.doCast(SkillTable.getInstance().getInfo(4552, Rnd.get(10) + 1));
				return super.onAttack(npc, attacker, damage, isPet);
			}
		}
		
		if (contains(DISEASE_MOBS4, npc.getNpcId()))
		{
			if (Rnd.get(100) < DISEASE_CHANCE)
			{
				npc.setTarget(attacker);
				npc.doCast(SkillTable.getInstance().getInfo(4551, Rnd.get(10) + 1));
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	public static void main(String[] args)
	{
		new HotSpringDisease(-1, "HotSpringDisease", "ai");
	}
}
