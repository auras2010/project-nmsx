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
package quests._179_IntoTheLargeCavern;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 ** @author Gnacik, Synerge
 **
 ** 2010-10-15 Based on official server Naia
 */
public class _179_IntoTheLargeCavern extends Quest
{
	// NPC's
	private static final int KEKROPUS = 32138;
	private static final int NORNIL = 32258;
	
	public _179_IntoTheLargeCavern(int questId, String name, String descr)
	{
		super(questId, name, descr);

		addStartNpc(KEKROPUS);
		addTalkId(KEKROPUS);
		addTalkId(NORNIL);
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null) 
			return event;

		switch (npc.getNpcId())
		{
			case KEKROPUS:
				if (event.equalsIgnoreCase("32138-03.htm"))
				{
					st.setState(State.STARTED);
					st.set("cond", "1");
					st.playSound("ItemSound.quest_accept");
				}
				break;
			case NORNIL:
				if (event.equalsIgnoreCase("32258-08.htm"))
				{
					st.giveItems(391, 1);
					st.giveItems(413, 1);
					st.playSound("ItemSound.quest_finish");
					st.exitQuest(false);
				}
				else if (event.equalsIgnoreCase("32258-09.htm"))
				{
					st.giveItems(847, 2);
					st.giveItems(890, 2);
					st.giveItems(910, 1);
					st.playSound("ItemSound.quest_finish");
					st.exitQuest(false);
				}
				break;
		}

		return event;
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
		switch(st.getState())
		{
			case State.COMPLETED :
				htmltext = getAlreadyCompletedMsg(player);
				break;
			case State.CREATED :
				final QuestState prevQ = player.getQuestState("178_IconicTrinity");
				if (prevQ == null
						|| prevQ.getState() != State.COMPLETED
						|| player.getLevel() < 17
						|| player.getRace().ordinal() != 5
						|| player.getClassId().level() != 0)
				{
					htmltext = "32138-00.htm";
					st.exitQuest(true);
				}
				else
					htmltext = "32138-01.htm";
				break;
			case State.STARTED :
				switch (npc.getNpcId())
				{
					case KEKROPUS:
						if (st.getInt("cond") == 1)
							htmltext = "32138-03.htm";
						break;
					case NORNIL:
						htmltext = "32258-01.htm";
						break;
				}
				break;
		}

		return htmltext;
	}

	public static void main(String[] args)
	{
		new _179_IntoTheLargeCavern(179, "_179_IntoTheLargeCavern", "Into The Large Cavern");
	}
}
