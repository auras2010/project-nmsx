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
package teleports.StakatoNest;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

public class StakatoNest extends Quest
{
	private final static int NPC = 32640;
	
	private final static int[][] DATA =
	{
		{80456, -52322, -5640},
		{88718, -46214, -4640},
		{87464, -54221, -5120},
		{80848, -49426, -5128},
		{87682, -43291, -4128}
	};

	public StakatoNest(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(NPC);
		addTalkId(NPC);
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		QuestState st = player.getQuestState(getName());
		if (st == null)
			st = newQuestState(player);

		final int loc = Integer.parseInt(event) - 1;
		
		if (DATA.length > loc)
		{
			int x = DATA[loc][0];
			int y = DATA[loc][1];
			int z = DATA[loc][2];

			if (player.getParty() != null)
			{
				for (L2PcInstance partyMember : player.getParty().getPartyMembers())
				{
					if (partyMember.isInsideRadius(player, 1000, true, true))
						partyMember.teleToLocation(x, y, z);
				}
			}
			player.teleToLocation(x, y, z);
			st.exitQuest(true);
		}

		return htmltext;
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		final QuestState accessQuest = player.getQuestState("_240_ImTheOnlyOneYouCanTrust");
		if (accessQuest != null && accessQuest.getState() == State.COMPLETED)
			htmltext = "32640.htm";
		else
			htmltext = "32640-no.htm";

		return htmltext;
	}

	public static void main(String[] args)
	{
		new StakatoNest(-1, "StakatoNest", "teleports");
	}
}