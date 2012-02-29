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

import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.scripts.ai.L2AttackableAIScript;

import java.util.ArrayList;


public class Hellenark extends L2AttackableAIScript
{
	private static final int HELLENARK = 22326;
	private static final int NAIA = 18484;
	
	private int status = 0;
	public ArrayList<L2Npc> spawnNaia = new ArrayList<L2Npc>();
	
	private static final int[][] NAIA_LOC = 
	{
        {-24542, 245792, -3133, 19078},
        {-23839, 246056, -3133, 17772},
        {-23713, 244358, -3133, 53369},
        {-23224, 244524, -3133, 57472},
        {-24709, 245186, -3133, 63974},
        {-24394, 244379, -3133, 5923}
	};

	public Hellenark(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addAttackId(HELLENARK);
		addTalkId(NAIA);
		addFirstTalkId(NAIA);
		addStartNpc(NAIA);
	}

	@Override
    public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.getNpcId() == HELLENARK)
		{
			if (status == 0)
			{
				startQuestTimer("spawn", 20000, npc, null, false);
				status = 1;
			}
		}
		return null;
	}

	@Override
    public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return null;
	}

	@Override
    public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("spawn"))
		{
			if (status == 1)
				status = 3;
			startQuestTimer("check", 30000, npc, null, false);
			for (int i=0;i<6;i++)
			{
				L2Npc mob = addSpawn(NAIA, NAIA_LOC[i][0], NAIA_LOC[i][1], NAIA_LOC[i][2], NAIA_LOC[i][3], false, 0);
				spawnNaia.add(mob);
                mob.setIsInvul(true);
                mob.setIsImmobilized(true);
                mob.setIsOverloaded(true);
			}
			startQuestTimer("cast", 5000, npc, null, false);
		}
		else if (event.equalsIgnoreCase("check"))
		{
			if (status == 1)
				startQuestTimer("check", 180000, npc, null, false);
			if (status == 3)
				startQuestTimer("desp", 180000, npc, null, false);
			status = 3;
		}
		else if (event.equalsIgnoreCase("desp"))
		{
			cancelQuestTimers("cast");
			for (L2Npc npc1 : spawnNaia)
				npc1.deleteMe();
			status = 0;
		}
		else if (event.equalsIgnoreCase("cast"))
		{
			for (L2Npc npc1 : spawnNaia)
			{
				npc1.setTarget(player);
				npc1.doCast(SkillTable.getInstance().getInfo(5765, 1));    // TODO: unknown debuff
			}
			startQuestTimer("cast", 5000, npc, null, false);
		}
		return "";
	}

	public static void main(String[] args)
	{
		new Hellenark(-1, "Hellenark", "ai");
	}
}
