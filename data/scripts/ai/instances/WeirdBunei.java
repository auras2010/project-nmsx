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

public class WeirdBunei extends L2AttackableAIScript
{
	private static final int WEIRD = 18564;	
	private boolean _isAlreadyStarted = false;
	
	public WeirdBunei(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addAttackId(WEIRD);
		addKillId(WEIRD);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("time_to_skill"))
		{
			if (_isAlreadyStarted)
			{
				_isAlreadyStarted = false;
				npc.setTarget(player);
				npc.doCast(SkillTable.getInstance().getInfo(5625, 1));
			}
		}
		
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.getNpcId() == WEIRD)
		{
			if (!_isAlreadyStarted)
			{
				startQuestTimer("time_to_skill", 30000, npc, player);
				_isAlreadyStarted = true;
			}
		}
		return super.onAttack(npc, player, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (npc.getNpcId() == WEIRD)
			cancelQuestTimer("time_to_skill", npc, player);
		
		return super.onKill(npc, player, isPet);
	}
	
	public static void main(String[] args)
	{
		new WeirdBunei(-1, "WeirdBunei", "ai");
	}
}
