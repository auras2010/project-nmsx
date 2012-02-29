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

import l2.universe.gameserver.datatables.SpawnTable;
import l2.universe.gameserver.model.L2Spawn;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.scripts.ai.L2AttackableAIScript;

/**
 * 
 * @author Synerge
 */
public class SeeThroughSilentMove extends L2AttackableAIScript
{
	private static final int[] MOBS = 
	{
		18001, 18002, 22199, 22215, 22216, 22217, 22327, 22746, 22747, 22748, 22749, 22750, 29011,
		22751, 22752, 22753, 22754, 22755, 22756, 22757, 22758, 22759, 22760, 22761, 22762, 29012,
		22763, 22764, 22765, 22794, 22795, 22796, 22797, 22798, 22799, 22800, 29009, 29010, 29013
	};
	
	public SeeThroughSilentMove(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		for (final L2Spawn npc : SpawnTable.getInstance().getSpawnTable())
		{
			if (npc == null || !(npc.getLastSpawn() instanceof L2Attackable))
				continue;
			
			if (contains(MOBS, npc.getNpcid()))
				((L2Attackable) npc.getLastSpawn()).setSeeThroughSilentMove(true);
		}
		
		registerMobs(MOBS, QuestEventType.ON_SPAWN);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc instanceof L2Attackable)
			((L2Attackable) npc).setSeeThroughSilentMove(true);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new SeeThroughSilentMove(-1, "SeeThroughSilentMove", "ai");
	}
}