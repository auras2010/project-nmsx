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
package quests._016_TheComingDarkness;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 * 
 * @author Synerge
 */
public class _016_TheComingDarkness extends Quest
{
	// NPC
	private static final int HIERARCH = 31517;
	// ALTAR_LIST (MOB_ID, cond)
	private static final int[][] ALTAR_LIST = {{31512, 1}, {31513, 2}, {31514, 3}, {31515, 4}, {31516, 5}};
	// ITEMS
	private static final int CRYSTAL_OF_SEAL = 7167;

	public _016_TheComingDarkness(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(HIERARCH);
		addTalkId(HIERARCH);
		for (int[] element : ALTAR_LIST)
			addTalkId(element[0]);

		questItemIds = new int[] {CRYSTAL_OF_SEAL};
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null) 
			return event;

		if (event.equalsIgnoreCase("31517-1.htm"))
			return event;
		else if (event.equalsIgnoreCase("31517-2.htm"))
		{
			st.setState(State.STARTED);
			st.set("cond", "1");
			st.giveItems(CRYSTAL_OF_SEAL, 5);
			st.playSound("ItemSound.quest_accept");
		}
		
		for (int[] element : ALTAR_LIST)
		{
			if (event.equalsIgnoreCase(String.valueOf(element[0]) + "-1.htm"))
			{
				st.takeItems(CRYSTAL_OF_SEAL, 1);
				st.set("cond", String.valueOf(element[1] + 1));
				st.playSound("ItemSound.quest_middle");
			}
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
		
		final int npcId = npc.getNpcId();
		
		if (npcId != HIERARCH)
			return htmltext;
		
		final int cond = st.getInt("cond");		
		
		switch (st.getState())
		{
			case State.COMPLETED:
				htmltext = msgQuestCompleted();
				break;
			case State.CREATED:
				final QuestState st2 = player.getQuestState("_017_LightAndDarkness");
				if (st2 != null && st2.getState() == State.COMPLETED)
				{
					if(st.getPlayer().getLevel() >= 61)
						htmltext = "31517-0.htm";
					else
					{
						htmltext = "<html><body>(Only characters level 62 and above are permitted to undertake this quest.) </body></html>";
						st.exitQuest(true);
					}
				}
				else
				{
					htmltext = "<html><body>Quest Light and Darkness need to be finished first.</body></html>";
					st.exitQuest(true);
				}
				break;
			case State.STARTED:
				if (cond > 0 && cond < 6)
				{
					if (st.getQuestItemsCount(CRYSTAL_OF_SEAL) > 0)
						htmltext = "<html><body>You haven't finished your job yet! Come back when you've broken the seal. I'll be waiting.</body></html>";
					else
					{
						htmltext = "<html><body>Where are the crystals of binding? How could you betray us, your brothers?</body></html>";
						st.exitQuest(true);
						st.playSound("ItemSound.quest_giveup");
					}
				}
				else if (cond > 5 && st.getQuestItemsCount(CRYSTAL_OF_SEAL) < 1)
				{
					htmltext = "31517-3.htm";
					st.addRewardExpAndSp(865187,69172);
					st.playSound("ItemSound.quest_finish");
					st.unset("cond");
					st.setState(State.COMPLETED);
					st.exitQuest(false);
				}
				break;
		}

		for (int[] element : ALTAR_LIST)
		{
			if (npcId == element[0])
			{
				if (cond == element[1])
				{
					if (st.getQuestItemsCount(CRYSTAL_OF_SEAL) > 0)
						htmltext = String.valueOf(element[0]) + "-0.htm";
					else
						htmltext = String.valueOf(element[0]) + "-1.htm";
				}
				else if (cond == element[1] + 1)
					htmltext = String.valueOf(element[0]) + "-1.htm";
			}
		}

		return htmltext;
	}

	public static void main(String[] args)
	{
		new _016_TheComingDarkness(16, "_016_TheComingDarkness", "The Coming Darkness");    	
	}
}
