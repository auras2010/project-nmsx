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
package quests._014_WhereaboutsOfTheArchaeologist;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 * 
 * @author Synerge
 */
public class _014_WhereaboutsOfTheArchaeologist extends Quest
{
	// NPC
	private static final int LIESEL = 31263;
	private static final int GHOST_OF_ADVENTURER = 31538;
	// QUEST ITEM
	private static final int LETTER_TO_ARCHAEOLOGIST = 7253;

	public _014_WhereaboutsOfTheArchaeologist(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(LIESEL);
		addTalkId(LIESEL);
		addTalkId(GHOST_OF_ADVENTURER);
		
		questItemIds = new int[] {LETTER_TO_ARCHAEOLOGIST};
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null) 
			return event;
		
		if (event.equalsIgnoreCase("31263-2.htm"))
		{
			st.set("cond", "1");
			st.giveItems(LETTER_TO_ARCHAEOLOGIST, 1);
			st.setState(State.STARTED);
			st.playSound("ItemSound.quest_accept");
		}
		else if (event.equalsIgnoreCase("31538-1.htm"))
		{
			st.takeItems(LETTER_TO_ARCHAEOLOGIST, 1);
			st.addRewardExpAndSp(325881, 32524);
			st.giveReward(57, 136928);
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
				if(st.getPlayer().getLevel() >= 74)
					htmltext = "31263-0.htm";
				else
				{
					htmltext = "31263-1.htm";
					st.exitQuest(true);
				}
				break;
			case State.STARTED:
				switch (npc.getNpcId())
				{
					case LIESEL:
						if (st.getInt("cond") == 1)
							htmltext = "31263-2.htm";		
						break;
					case GHOST_OF_ADVENTURER:
						if (st.getInt("cond") == 1 && st.getQuestItemsCount(LETTER_TO_ARCHAEOLOGIST) == 1)
							htmltext = "31538-0.htm";
						break;
				}
				break;
		}
		return htmltext;
	}

	public static void main(String[] args)
	{
		new _014_WhereaboutsOfTheArchaeologist(14, "_014_WhereaboutsOfTheArchaeologist", "Where abouts of the Archaeologist");    	
	}
}
