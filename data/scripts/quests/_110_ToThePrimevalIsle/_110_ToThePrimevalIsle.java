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
package quests._110_ToThePrimevalIsle;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.State;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.Quest;

/**
 * @author Ethernaly, Ortiva
 */
public class _110_ToThePrimevalIsle extends Quest
{
	// NPCs
	private final static int ANTON   = 31338;
	private final static int MARQUEZ = 32113;
	
	// QUEST ITEM
	private final static int QUEST_ITEM[] = { 8777 };
	
	private final static int PLAYER_MIN_LVL = 75;
	
	public _110_ToThePrimevalIsle(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(ANTON);
		addTalkId(ANTON);
		addTalkId(MARQUEZ);
		questItemIds = QUEST_ITEM;
	}
	
	@Override
	public String onEvent(String event, QuestState st)
	{
		if (st == null)
			return null;
		
		String htmltext = event;		
		if (event.equalsIgnoreCase("1"))
		{
			htmltext = "1.htm";
			st.set("cond","1");
			st.giveItems(QUEST_ITEM[0],1);
			st.setState(State.STARTED);
			st.playSound("ItemSound.quest_accept");
		}
		else if (event.equalsIgnoreCase("2") && st.getQuestItemsCount(QUEST_ITEM[0]) >= 1)
		{
			htmltext = "3.htm";
			st.playSound("ItemSound.quest_finish");
			st.giveReward(57, 169380);
			st.addRewardExpAndSp(251602, 25245);
			st.takeItems(QUEST_ITEM[0], -1);
			st.exitQuest(false);
		}
		return htmltext;
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
				if (st.getPlayer().getLevel() >= PLAYER_MIN_LVL)
					htmltext = "0.htm";
				else
				{
			        st.exitQuest(true);
			        htmltext = msgTooLowLvl(PLAYER_MIN_LVL);
				}
				break;
			case State.STARTED:
				if (npc.getNpcId() == MARQUEZ)
				{
					if (st.getInt("cond") == 1)
					{
						if (st.getQuestItemsCount(QUEST_ITEM[0]) == 0)
							htmltext = "1a.htm";
						else
							htmltext = "2.htm";			
					}
				}
				break;
		}
		return htmltext;
	}
		
	public static void main(String[] args)
	{
		new _110_ToThePrimevalIsle(110, "_110_ToThePrimevalIsle", "To The Primeval Isle");
	}
}
