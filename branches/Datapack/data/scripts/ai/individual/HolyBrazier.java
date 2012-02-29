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
package ai.individual;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.scripts.ai.L2AttackableAIScript;

/**
 * HOLY_BRAZIER AI
 * 
 * @author Emperorc, Synerge
 * 
 */
public class HolyBrazier extends L2AttackableAIScript
{	
	private static final int HOLY_BRAZIER = 32027;
	private static final int GUARDIAN_OF_THE_GRAIL = 22133;
	
	private L2Npc _guard = null;
	private L2Npc _brazier = null;
	
	public HolyBrazier(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		final int[] mobs = { HOLY_BRAZIER, GUARDIAN_OF_THE_GRAIL };
		registerMobs(mobs, QuestEventType.ON_AGGRO_RANGE_ENTER, QuestEventType.ON_SPAWN, QuestEventType.ON_KILL);
	}
	
	private void spawnGuard(L2Npc npc)
	{
		if (_guard == null && _brazier != null)
		{
			_guard = addSpawn(GUARDIAN_OF_THE_GRAIL, _brazier.getX(), _brazier.getY(), _brazier.getZ(), 0, false, 0);
			_guard.setIsNoRndWalk(true);
		}
		return;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc.getNpcId() == HOLY_BRAZIER)
		{
			_brazier = npc;
			_guard = null;
			npc.setIsNoRndWalk(true);
			spawnGuard(npc);
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (npc.getNpcId() == GUARDIAN_OF_THE_GRAIL && !npc.isInCombat() && npc.getTarget() == null)
			npc.setIsNoRndWalk(true);

		return super.onAggroRangeEnter(npc, player, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		switch (npc.getNpcId())
		{
			case GUARDIAN_OF_THE_GRAIL:
				_guard = null;
				spawnGuard(npc);
				break;
			case HOLY_BRAZIER:
				if (_guard != null)
				{
					_guard.deleteMe();
					_guard = null;					
				}
				_brazier = null;
				break;
		}

		return super.onKill(npc, killer, isPet);
	}
	
	public static void main(String[] args)
	{
		new HolyBrazier(-1, "HolyBrazier", "ai");
	}
}
