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
package quests._250_WatchWhatYouEat;

import l2.universe.gameserver.instancemanager.QuestManager;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 ** @author Gnacik, Synerge
 **
 ** 2010-08-05 Based on Freya PTS
 */
public class _250_WatchWhatYouEat extends Quest
{
	// NPCs
	private static final int SALLY = 32743;
	// Mobs - Items
	private static final int[][] MOBS = {{ 18864, 15493 }, { 18865, 15494 }, { 18868, 15495 }};
	
	public _250_WatchWhatYouEat(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(SALLY);
		addFirstTalkId(SALLY);
		addTalkId(SALLY);
		for(int i[] : MOBS)
			addKillId(i[0]);
	}
	
	@Override
	public String onAdvEvent (String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null) 
			return event;
		
		if (npc.getNpcId() != SALLY)
			return event;
		
		if (event.equalsIgnoreCase("32743-03.htm"))
		{
			st.setState(State.STARTED);
			st.set("cond", "1");
			st.playSound("ItemSound.quest_accept");
		}
		else if (event.equalsIgnoreCase("32743-end.htm"))
		{
			st.unset("cond");
			st.rewardItems(57,135661);
			st.addExpAndSp(698334,76369);
			st.playSound("ItemSound.quest_finish");
			st.exitQuest(false);
		}
		else if (event.equalsIgnoreCase("32743-22.html") && st.getState() == State.COMPLETED)
		{
			return "32743-23.html";
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
		
		if (npc.getNpcId() != SALLY)
			return htmltext;
		
		switch(st.getState())
		{
			case State.COMPLETED :
				htmltext = "32743-done.htm";
				break;
			case State.CREATED :
				if (player.getLevel() >= 82)
					htmltext = "32743-01.htm";
				else
				{
					htmltext = "32743-00.htm";
					st.exitQuest(true);
				}
				break;
			case State.STARTED :
				switch (st.getInt("cond"))
				{
					case 1:
						htmltext = "32743-04.htm";
						break;
					case 2:
						if(st.hasQuestItems(MOBS[0][1]) && st.hasQuestItems(MOBS[1][1]) && st.hasQuestItems(MOBS[2][1]))
						{
							htmltext = "32743-05.htm";
							for (int items[] : MOBS)
								st.takeItems(items[1], -1);
						}
						else
							htmltext = "32743-06.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null || st.getState() != State.STARTED)
			return null;
		
		for (int mob[] : MOBS)
		{
			if (npc.getNpcId() == mob[0])
			{
				if (!st.hasQuestItems(mob[1]))
				{
					st.giveItems(mob[1], 1);
					st.playSound("ItemSound.quest_itemget");
				}
			}
		}
		
		if (st.hasQuestItems(MOBS[0][1]) && st.hasQuestItems(MOBS[1][1]) && st.hasQuestItems(MOBS[2][1]))
		{
			st.set("cond", "2");
			st.playSound("ItemSound.quest_middle");
		}

		return super.onKill(npc, player, isPet);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			Quest q = QuestManager.getInstance().getQuest(getName());
			st = q.newQuestState(player);
		}
		
		if (npc.getNpcId() == SALLY)
			return "32743-20.html";
		
		return null;
	}
		
	public static void main(String[] args)
	{
		new _250_WatchWhatYouEat(250, "_250_WatchWhatYouEat", "Watch What You Eat");
	}
}
