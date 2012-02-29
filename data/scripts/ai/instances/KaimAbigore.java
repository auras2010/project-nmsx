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
package ai.instances;

import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.scripts.ai.L2AttackableAIScript;

public class KaimAbigore extends L2AttackableAIScript
{
	private static final int KAIM = 18566;
	private static final int GUARD = 18567;
	
	private boolean _isAlreadyStarted = false;
	private boolean _isAlreadySpawned = false;
	private int _isLockSpawned = 0;
	
	public KaimAbigore(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addAttackId(KAIM);
		addKillId(GUARD);
		addKillId(KAIM);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("time_to_skill"))
		{
			npc.setTarget(player);
			npc.doCast(SkillTable.getInstance().getInfo(5260, 5));
			_isAlreadyStarted = false;
		}
		else if (event.equalsIgnoreCase("time_to_spawn"))
		{
			final int x = player.getX();
			final int y = player.getY();
			addSpawn(GUARD, x + 100, y + 50, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
			addSpawn(GUARD, x - 100, y - 50, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
			addSpawn(GUARD, x, y - 80, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
			_isAlreadySpawned = false;
			_isLockSpawned = 3;
		}
		
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet, L2Skill skill)
	{
		final int npcId = npc.getNpcId();
		if (npcId == KAIM)
		{
			if (_isAlreadyStarted)
				return super.onAttack(npc, player, damage, isPet);
			
			startQuestTimer("time_to_skill", 45000, npc, player);
			_isAlreadyStarted = true;
			
			if (_isAlreadySpawned)
				return super.onAttack(npc, player, damage, isPet);
			
			switch (_isLockSpawned)
			{
				case 0:
					startQuestTimer("time_to_spawn", 60000, npc, player);
					_isAlreadySpawned = true;
					break;
				case 3:
					return super.onAttack(npc, player, damage, isPet);
			}
		}
		
		return super.onAttack(npc, player, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		switch (npc.getNpcId())
		{
			case GUARD:
				_isLockSpawned = 1;
				break;
			case KAIM:
				cancelQuestTimer("time_to_spawn", npc, player);
				cancelQuestTimer("time_to_skill", npc, player);
				break;
		}
		
		return super.onKill(npc, player, isPet);
	}
	
	public static void main(String[] args)
	{
		new KaimAbigore(-1, "KaimAbigore", "ai");
	}
}
