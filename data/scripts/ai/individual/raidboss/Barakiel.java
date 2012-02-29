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

package ai.individual.raidboss;

import l2.universe.gameserver.model.actor.instance.L2NpcInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.scripts.ai.L2AttackableAIScript;

public class Barakiel extends L2AttackableAIScript
{
	// Barakiel NpcID
	private static final int BARAKIEL = 25325;
	// Barakiel Z coords
	private static final int x1 = 89800;
	private static final int x2 = 93200;
	private static final int y1 = -87038;

	public Barakiel (int questId, String name, String descr)
	{
		super(questId,name,descr);
		int[] mobs = {BARAKIEL};
		registerMobs(mobs);
	}

	public String onAttack (L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		int npcId = npc.getNpcId();
		if (npcId == BARAKIEL)
		{
			int x = npc.getX();
			int y = npc.getY();
			if (x < x1 || x > x2 || y < y1)
			{
				npc.teleToLocation(91008,-85904,-2736);
				npc.getStatus().setCurrentHp(npc.getMaxHp());
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}

	public static void main(String[] args)
	{
		new Barakiel(-1, "Barakiel", "ai");
	}
}