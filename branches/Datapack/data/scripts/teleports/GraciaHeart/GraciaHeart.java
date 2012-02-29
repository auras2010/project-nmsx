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
package teleports.GraciaHeart;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;

public class GraciaHeart extends Quest
{
	private final static int NPC = 36570;

	public GraciaHeart(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(NPC);
		addTalkId(NPC);
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{		
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return "";
				
		if (npc.getNpcId() != NPC)
			return "";

		String htmltext = "";

		if (player.getLevel() < 75)
			htmltext = "36570-0.htm";
		else
			player.teleToLocation(-204288, 242026, 1744);
		st.exitQuest(true);
		
		return htmltext;
	}

	public static void main(String[] args)
	{
		new GraciaHeart(-1, "GraciaHeart", "teleports");
	}
}