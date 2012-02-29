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
package teleports.StrongholdsTeleports;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;

public class StrongholdsTeleports extends Quest
{
	private static final String qn = "StrongholdsTeleports";

	private final static int[] NPCs =
	{
		32163,32181,32184,32186
	};

	public StrongholdsTeleports(int questId, String name, String descr)
	{
		super(questId, name, descr);
		for (int id : NPCs)
		{
			addStartNpc(id);
			addFirstTalkId(id);
			addTalkId(id);
		}
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		if (player.getLevel() < 20 && npc.getNpcId() == 32163)
			htmltext = "32163-4.htm";
		else
			htmltext = "32163-5.htm";

		return htmltext;
	}

	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		if (player.getLevel() < 20)
			htmltext = npc.getNpcId() + ".htm";
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player);
		return htmltext;
	}

	public static void main(String[] args)
	{
		new StrongholdsTeleports(-1, qn, "teleports");
	}
}