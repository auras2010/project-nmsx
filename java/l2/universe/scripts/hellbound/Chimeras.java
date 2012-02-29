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
package l2.universe.scripts.hellbound;

import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.util.Rnd;

import l2.universe.scripts.ai.L2AttackableAIScript;

/**
 * 
 * @author DS, based on theOne's work
 *
 */
public class Chimeras extends L2AttackableAIScript
{
	private static final int[] NPCS =
	{
		22349, 22350, 22351, 22352
	};

	private static final int CELTUS = 22353;
	private static final int[][] LOCATIONS =
	{
		{  4276, 237245, -3310 },
		{ 11437, 236788, -1949 },
		{  7647, 235672, -1977 },
		{  1882, 233520, -3315 }
	};

	private static final int BOTTLE = 2359;

	private static final int DIM_LIFE_FORCE = 9680;
	private static final int LIFE_FORCE = 9681;
	private static final int CONTAINED_LIFE_FORCE = 9682;

	@Override
	public final String onSpawn(L2Npc npc)
	{
		final int[] spawn = LOCATIONS[Rnd.get(LOCATIONS.length)];
		if (!npc.isInsideRadius(spawn[0], spawn[1], spawn[2], 200, false, false))
		{
			npc.getSpawn().setLocx(spawn[0]);
			npc.getSpawn().setLocy(spawn[1]);
			npc.getSpawn().setLocz(spawn[2]);
			ThreadPoolManager.getInstance().scheduleGeneral(new Teleport(npc, spawn), 100);
		}

		return super.onSpawn(npc);
	}

	@Override
	public final String onSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
	{
		if (skill.getId() == BOTTLE)
		{
			if (!npc.isDead())
			{
				if (targets.length > 0 && targets[0] == npc)
				{
					if (npc.getCurrentHp() < npc.getMaxHp() * 0.1)
					{
						npc.setIsDead(true);
						if (npc.getNpcId() == CELTUS)
							((L2Attackable)npc).dropItem(caster, CONTAINED_LIFE_FORCE, 1);
						else
						{
							if (Rnd.get(100) < 10)
								((L2Attackable)npc).dropItem(caster, LIFE_FORCE, 1);
							else
								((L2Attackable)npc).dropItem(caster, DIM_LIFE_FORCE, 1);
						}
						npc.onDecay();
					}
				}
			}
		}

		return super.onSkillSee(npc, caster, skill, targets, isPet);
	}

	private static class Teleport implements Runnable
	{
		private final L2Npc _npc;
		private final int[] _coords;

		public Teleport(L2Npc npc, int[] coords)
		{
			_npc = npc;
			_coords = coords;
		}

		@Override
		public void run()
		{
			_npc.teleToLocation(_coords[0], _coords[1], _coords[2]);
		}
	}

	public Chimeras(int questId, String name, String descr)
	{
		super(questId, name, descr);
		for (int npcId : NPCS)
			addSkillSeeId(npcId);

		addSpawnId(CELTUS);
		addSkillSeeId(CELTUS);
	}

	public static void main(String[] args)
	{
		new Chimeras(-1, Chimeras.class.getSimpleName(), "ai");
	}
}