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


public class Kamaloka25_35 extends KamalokaSolo 
{
	// MOB
	private static final int KANABION = 22455;
	private static final int[] APPEAR = { 22456, 22457 };

	// REWARDS
	// Example: d,count,c,count,b,count.....
	private static final int[] REW1 = { 13002, 3, 13002, 3, 13002, 3, 13002, 3, 13002, 3 };
	private static final int[] REW2 = { 10838, 1, 10837, 1, 10836, 1, 10840, 1, 12825, 1 };
	private static final int[] REW3 = { 85, 65, 128, 956, 90, 65, 20, 128, 956, 65, 25, 20, 141, 956 };

	private KamaParam param = new KamaParam();

	public Kamaloka25_35(int questId, String name, String descr) 
	{
		super(questId, name, descr);
		param.qn = "Kamaloka25_35";
		param.minLev = 25;
		param.maxLev = 35;
		param.rewPosition = newCoord(16301, -219806, -8021);
		param.enterCoord = newCoord(15617, -219883, -8021);
		
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
		new Kamaloka25_35(-1, "Kamaloka25_35", "Kamaloka25_35");
	}
}
