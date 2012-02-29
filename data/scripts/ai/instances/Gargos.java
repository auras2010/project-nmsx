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

public class Gargos extends L2AttackableAIScript
{
	private static final int GARGOS = 18607;	
	boolean _isStarted = false;
	
	public Gargos(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addAttackId(GARGOS);
		addKillId(GARGOS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("TimeToFire"))
		{
			_isStarted = false;
			npc.broadcastNpcSay("Oooo... Ooo...");
			npc.doCast(SkillTable.getInstance().getInfo(5705, 1));
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet, L2Skill skill)
	{		
		if (npc.getNpcId() == GARGOS)
		{
			if (!_isStarted)
			{
				startQuestTimer("TimeToFire", 60000, npc, player);
				_isStarted = true;
			}
		}
		return super.onAttack(npc, player, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{	
		if (npc.getNpcId() == GARGOS)
			cancelQuestTimer("TimeToFire", npc, player);
		
		return super.onKill(npc, player, isPet);
	}
	
	public static void main(String[] args)
	{
		new Gargos(-1, "Gargos", "ai");
	}
}
