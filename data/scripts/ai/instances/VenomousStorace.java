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

import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.scripts.ai.L2AttackableAIScript;


public class VenomousStorace extends L2AttackableAIScript
{
	private static final int VENOMOUS = 18571;
	private static final int GUARD = 18572;
	
	private boolean _isAlreadySpawned = false;
	private int _isLockSpawned = 0;
	
	public VenomousStorace(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addAttackId(VENOMOUS);
		addKillId(GUARD);
		addKillId(VENOMOUS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("time_to_spawn"))
		{
			final int x = player.getX();
			final int y = player.getY();
			addSpawn(GUARD, x + 100, y + 50, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
			addSpawn(GUARD, x - 100, y - 50, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
			_isAlreadySpawned = false;
			_isLockSpawned = 2;
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.getNpcId() == VENOMOUS)
		{
			switch (_isLockSpawned)
			{
				case 0:
					if (!_isAlreadySpawned)
					{
						startQuestTimer("time_to_spawn", 20000, npc, player);
						_isAlreadySpawned = true;
					}
					break;
				case 2:
				default:
					return null;
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
			case VENOMOUS:
				cancelQuestTimer("time_to_spawn", npc, player);
				break;
		}
		
		return super.onKill(npc, player, isPet);
	}
	
	public static void main(String[] args)
	{
		new VenomousStorace(-1, "VenomousStorace", "ai");
	}
}
