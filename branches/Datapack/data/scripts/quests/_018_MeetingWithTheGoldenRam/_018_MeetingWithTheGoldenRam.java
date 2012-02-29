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
package quests._018_MeetingWithTheGoldenRam;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 * 
 * @author Synerge
 */
public class _018_MeetingWithTheGoldenRam extends Quest
{
	// NPC
	private static final int DONAL = 31314;
	private static final int DAISY = 31315;
	private static final int ABERCROMBIE = 31555;
	// ITEM
	private static final int SUPPLY_BOX = 7245;

	public _018_MeetingWithTheGoldenRam(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(DONAL);
		addTalkId(DONAL);
		addTalkId(DAISY);
		addTalkId(ABERCROMBIE);
		
		questItemIds = new int[] {SUPPLY_BOX};
	}

	@Override
	public String onEvent(String event, QuestState st)
	{
		if (event.equalsIgnoreCase("31314-03.htm"))
		{
			st.set("cond", "1");
			st.setState(State.STARTED);
			st.playSound("ItemSound.quest_accept");
		}
		else if (event.equalsIgnoreCase("31315-02.htm"))
		{
			st.set("cond", "2");
			st.giveItems(SUPPLY_BOX, 1);
			st.playSound("ItemSound.quest_accept");
		}
		else if (event.equalsIgnoreCase("31555-02.htm"))
		{
			st.takeItems(SUPPLY_BOX, 1);
			st.addRewardExpAndSp(126668,11731);
			st.giveReward(57,40000);
			st.unset("cond");
			st.setState(State.COMPLETED);
			st.playSound("ItemSound.quest_finish");
			st.exitQuest(false);
		}
		return event;
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = msgNotHaveMinimumRequirements();
		final QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
		final int cond = st.getInt("cond");		
		
		switch (st.getState())
		{
			case State.COMPLETED:
				htmltext = msgQuestCompleted();
				break;
			case State.CREATED:
				if (st.getPlayer().getLevel() >= 66)
					htmltext = "31314-01.htm";
				else
				{
					htmltext = "31314-02.htm";
					st.exitQuest(true);
				}
				break;
			case State.STARTED:
				switch (npc.getNpcId())
				{
					case DONAL:
						if (cond == 1)
							htmltext = "31314-04.htm";
						break;
					case DAISY:
						switch (cond)
						{
							case 1:
								htmltext = "31315-01.htm";
								break;
							case 2:
								htmltext = "31315-03.htm";
								break;
						}
						break;
					case ABERCROMBIE:
						if (cond == 2 && st.getQuestItemsCount(SUPPLY_BOX) == 1)
							htmltext = "31555-01.htm";
						break;
				}
				break;
		}

		return htmltext;
	}

	public static void main(String[] args)
	{
		new _018_MeetingWithTheGoldenRam(18, "_018_MeetingWithTheGoldenRam", "Meeting with the Golden Ram");    	
	}
}
