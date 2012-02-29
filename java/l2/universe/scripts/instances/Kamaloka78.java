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

public class Kamaloka78 extends Kamaloka
{
	private KamaParam param = new KamaParam();

	public Kamaloka78(int questId, String name, String descr)
	{
		super(questId, name, descr);
		param.qn = "Kamaloka78";
		param.Template = "Kamaloka_78.xml";
		param.Level = 78;
		param.PartySize = 9;
		param.Npc = 31340;
		param.Mob = 29144;
		param.Minion = 0;
		param.enterCoord = newCoord(-10965,-185532,-10944);
		param.ClientId = 73;
			
		addStartNpc(param.Npc);
		addTalkId(param.Npc);
		addKillId(param.Mob);
	}

	public static void main(String[] args)
	{
		// now call the constructor (starts up the)
		new Kamaloka78(-1, "Kamaloka78", "Kamaloka78");
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
