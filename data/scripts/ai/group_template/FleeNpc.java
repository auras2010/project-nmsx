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
import l2.universe.gameserver.model.L2CharPosition;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.scripts.ai.L2AttackableAIScript;
import l2.universe.util.Rnd;

/**
 * 
 * @author Synerge
 */
public class FleeNpc extends L2AttackableAIScript
{
	private static final int[] NPCS = { 18150, 18151, 18152, 18153, 18154, 18155, 18156, 18157 };
	
	public FleeNpc(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		registerMobs(NPCS, QuestEventType.ON_ATTACK);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (npc.getNpcId() >= 18150 && npc.getNpcId() <= 18157)
		{
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition((npc.getX() + Rnd.get(-40, 40)), (npc.getY() + Rnd.get(-40, 40)), npc.getZ(), npc.getHeading()));
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
			return null;
		}

		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	public static void main(String[] args)
	{
		new FleeNpc(-1, "FleeNpc", "ai");
	}
}
