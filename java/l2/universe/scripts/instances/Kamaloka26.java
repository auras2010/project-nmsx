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


public class Kamaloka26 extends Kamaloka
{
	private KamaParam param = new KamaParam();

	public Kamaloka26(int questId, String name, String descr)
	{
		super(questId, name, descr);
		param.qn = "Kamaloka26";
		param.Template = "Kamaloka_26.xml";
		param.Level = 26;
		param.Npc = 30332;
		param.Mob = 18555;
		param.Minion = 18556;
		param.enterCoord = newCoord(-81890,-220819,-7898);
		param.ClientId = 57;
		
		addStartNpc(param.Npc);
		addTalkId(param.Npc);
		addKillId(param.Mob);
	}

	public static void main(String[] args)
	{
		// now call the constructor (starts up the)
		new Kamaloka26(-1, "Kamaloka26", "Kamaloka26");
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		return onAdvEventTo(event, npc, player);
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		return onTalkTo(npc, player, param);
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		return onKillTo(npc, player, isPet, param);
	}
}
