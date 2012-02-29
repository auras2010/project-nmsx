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

import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.scripts.ai.L2AttackableAIScript;

/**
 * 
 * @author Synerge
 */
public class SearchingMaster extends L2AttackableAIScript
{
	private static final int[] MOBS =
	{
		20965, 20966, 20967, 20968, 20969, 20970, 20971, 20972, 20973
	};
	
	public SearchingMaster(int questId, String name, String descr)
	{
		super(questId, name, descr);
		registerMobs(MOBS, QuestEventType.ON_ATTACK);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet)
	{
		if (player == null)
			return null;
		
		final L2Character attacker = isPet ? player.getPet().getOwner() : player;
		
		npc.setIsRunning(true);
		((L2Attackable) npc).addDamageHate(attacker, 0, 999);
		npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		
		return super.onAttack(npc, player, damage, isPet);
	}
	
	public static void main(String[] args)
	{
		new SearchingMaster(-1, "SearchingMaster", "ai");
	}
}
