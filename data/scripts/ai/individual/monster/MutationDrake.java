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

import l2.universe.gameserver.GeoData;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.L2Summon;
import l2.universe.gameserver.model.actor.instance.L2DecoyInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.util.Util;
import l2.universe.util.Rnd;

import java.util.ArrayList;

import l2.universe.scripts.ai.L2AttackableAIScript;

public class MutationDrake extends L2AttackableAIScript
{
	private static final int MUTATION_DRAKE = 22552;

	public MutationDrake (int questId, String name, String descr)
	{
		super(questId, name, descr);
		registerMobs(new int[] {MUTATION_DRAKE});
	}

    @Override
	public String onAttack (L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (npc.getCurrentHp() > ((npc.getMaxHp()*3)/4) && Rnd.get(100) < 75)
		    getRandomTarget(npc);
		else if (npc.getCurrentHp() > ((npc.getMaxHp()*2)/4) && Rnd.get(100) < 50)
            getRandomTarget(npc);
		else if (npc.getCurrentHp() > ((npc.getMaxHp())/4) && Rnd.get(100) < 25)
			getRandomTarget(npc);
		return super.onAttack(npc, attacker, damage, isPet);
	}

	public L2Character getRandomTarget(L2Npc npc)
	{
		ArrayList<L2Character> result = new ArrayList<L2Character>();
		{
			for (L2Object obj : npc.getKnownList().getKnownObjects().values())
			{
				if (obj instanceof L2Character)
					if (obj.getZ() < (npc.getZ() - 100) && obj.getZ() > (npc.getZ() + 100) || !(GeoData.getInstance().canSeeTarget(obj.getX(), obj.getY(), obj.getZ(), npc.getX(), npc.getY(), npc.getZ()))||((L2Character) obj).isGM())
						continue;
				if (obj instanceof L2PcInstance || obj instanceof L2Summon || obj instanceof L2DecoyInstance)
					if (Util.checkIfInRange(1000, npc, obj, true) && !((L2Character) obj).isDead())
						result.add((L2Character) obj);
			}
		}
		if (!result.isEmpty() && result.size() != 0)
		{
			Object[] characters = result.toArray();
			return (L2Character) characters[Rnd.get(characters.length)];
		}
		return null;
	}

	public static void main(String[] args)
	{
		new MutationDrake(-1,"MutationDrake", "AI");
	}
}