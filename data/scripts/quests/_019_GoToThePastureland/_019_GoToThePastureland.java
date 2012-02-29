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
package quests._019_GoToThePastureland;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 * 
 * @author Synerge
 */
public class _019_GoToThePastureland extends Quest
{
	private final static int VLADIMIR = 31302;
	private final static int TUNATUN = 31537;
	private final static int BEAST_MEAT = 7547;

	public _019_GoToThePastureland(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(VLADIMIR);
		addTalkId(VLADIMIR);
		addTalkId(TUNATUN);

		questItemIds = new int[] {BEAST_MEAT};
	}

	@Override
	public String onEvent(String event, QuestState st)
	{
		if (event.equalsIgnoreCase("31302-1.htm"))
		{
			st.giveItems(BEAST_MEAT, 1);
			st.set("cond", "1");
			st.setState(State.STARTED);
			st.playSound("ItemSound.quest_accept");
		}
		else if(event.equalsIgnoreCase("31537-1.htm"))
		{
			st.takeItems(BEAST_MEAT, 1);
			st.addRewardExpAndSp(136766,12688);
			st.giveReward(57, 50000);
			st.unset("cond");
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
		
		switch (st.getState())
		{
			case State.COMPLETED:
				htmltext = msgQuestCompleted();
				break;
			case State.CREATED:
				if (st.getPlayer().getLevel() >= 63)
					htmltext = "31302-0.htm";
				else
				{
					htmltext = "<html><body>Quest for characters level 63 or above.</body></html>";
					st.exitQuest(true);
				}
				break;
			case State.STARTED:
				switch (npc.getNpcId())
				{
					case VLADIMIR:
						htmltext = "31302-2.htm";
						break;
					case TUNATUN:
						if(st.getQuestItemsCount(BEAST_MEAT) >= 1)
							htmltext = "31537-0.htm";
						else
						{
							htmltext = "31537-1.htm";
							st.exitQuest(true);
						}
						break;
				}
				break;
		}

		return htmltext;
	}

	public static void main(String[] args)
	{
		new _019_GoToThePastureland(19, "_019_GoToThePastureland", "Go To The Pastureland");    	
	}
}
