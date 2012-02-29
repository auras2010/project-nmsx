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
package l2.universe.scripts.instances;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;

/**
 * 
 * @author L2 DC, Synerge
 */

public class Kamaloka50_60 extends KamalokaSolo 
{
	// MOB
	private static final int KANABION = 22470;
	private static final int[] APPEAR = { 22471, 22472 };

	// REWARDS
	// Example: d,count,c,count,b,count.....
	private static final int[] REW1 = { 13002, 10, 13002, 10, 13002, 10, 13002, 10, 13002, 10 };
	private static final int[] REW2 = { 10851, 1, 10852, 1, 10853, 1, 10854, 1, 12830, 1 };
	private static final int[] REW3 = { 85, 65, 31, 948, 90, 65, 20, 31, 948, 65, 25, 20, 35, 948 };

	private KamaParam param = new KamaParam();

	public Kamaloka50_60(int questId, String name, String descr) 
	{
		super(questId, name, descr);
		param.qn = "Kamaloka50_60";
		param.minLev = 50;
		param.maxLev = 60;
		param.rewPosition = newCoord(9136, -205733, -8007);
		param.enterCoord = newCoord(9139, -205132, -8007);
		
		addStartNpc(ENTRANCE);
		addTalkId(ENTRANCE);
		addTalkId(REWARDER);
		addKillId(KANABION);
		for (int mob : APPEAR)
			addKillId(mob);
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) 
	{
		return onAdvEventTo(event, npc, player, param.qn, REW1, REW2);
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) 
	{
		if (npc.getNpcId() == ENTRANCE)
			return onEnterTo(npc, player, param);
		else
			return onTalkTo(npc, player, param.qn);
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		return onKillTo(npc, player, isPet, param.qn, KANABION, APPEAR, REW3);
	}
	
	public static void main(String[] args) 
	{
		new Kamaloka50_60(-1, "Kamaloka50_60", "Kamaloka50_60");
	}
}
