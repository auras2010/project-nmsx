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


public class Kamaloka35_45 extends KamalokaSolo 
{
	// MOB
	private static final int KANABION = 22461;
	private static final int[] APPEAR = { 22462, 22463 };

	// REWARDS
	// Example: d,count,c,count,b,count.....
	private static final int[] REW1 = { 13002, 5, 13002, 5, 13002, 5, 13002, 5, 13002, 5 };
	private static final int[] REW2 = { 10842, 1, 10843, 1, 10844, 1, 10845, 1, 12827, 1 };
	private static final int[] REW3 = { 85, 65, 220, 956, 90, 65, 20, 220, 956, 65, 25, 20, 240, 956 };

	private KamaParam param = new KamaParam();

	public Kamaloka35_45(int questId, String name, String descr) 
	{
		super(questId, name, descr);
		param.qn = "Kamaloka35_45";
		param.minLev = 35;
		param.maxLev = 45;
		param.rewPosition = newCoord(9290, -212993, -7799);
		param.enterCoord = newCoord(8559, -212987, -7802);
		
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
		new Kamaloka35_45(-1, "Kamaloka35_45", "Kamaloka35_45");
	}
}
