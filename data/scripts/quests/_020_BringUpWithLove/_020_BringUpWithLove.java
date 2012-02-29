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
package quests._020_BringUpWithLove;

import l2.universe.gameserver.instancemanager.QuestManager;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 ** @author Gnacik, Synerge
 **
 ** 2010-09-29 Based on official server Franz
 */
public class _020_BringUpWithLove extends Quest
{
	// NPC
	private static final int TUNATUN = 31537;
	// ITEMS
	private static final int BEAST_WHIP = 15473;
	private static final int CRYSTAL = 9553;
	private static final int JEWEL = 7185;
	
	public _020_BringUpWithLove(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(TUNATUN);
		addTalkId(TUNATUN);
		addFirstTalkId(TUNATUN);
		
		questItemIds = new int[] { JEWEL };
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null) 
			return event;
		
		if (npc.getNpcId() != TUNATUN)
			return event;

		if (event.equalsIgnoreCase("31537-12.htm"))
		{
			st.setState(State.STARTED);
			st.set("cond", "1");
			st.playSound("ItemSound.quest_accept");
		}
		else if (event.equalsIgnoreCase("31537-03.htm"))
		{
			if (st.hasQuestItems(BEAST_WHIP))
				return "31537-03a.htm";
			else
				st.giveItems(BEAST_WHIP, 1);
		}
		else if (event.equalsIgnoreCase("31537-15.htm"))
		{
			st.unset("cond");
			st.takeItems(JEWEL, -1);
			st.giveItems(CRYSTAL, 1);
			st.playSound("ItemSound.quest_finish");
			st.exitQuest(false);
		}
		else if (event.equalsIgnoreCase("31537-21.html"))
		{
			if (player.getLevel() < 82)
				return "31537-23.html";
			if (st.hasQuestItems(BEAST_WHIP))
				return "31537-22.html";
			st.giveItems(BEAST_WHIP, 1);
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
		
		if (npc.getNpcId() != TUNATUN)
			return htmltext;
		
		switch(st.getState())
		{
			case State.COMPLETED :
				htmltext = getAlreadyCompletedMsg(player);
				break;
			case State.CREATED :
				if (player.getLevel() >= 82)
					htmltext = "31537-01.htm";
				else
				{
					htmltext = "31537-00.htm";
					st.exitQuest(true);
				}
				break;
			case State.STARTED :
				switch (st.getInt("cond"))
				{
					case 1:
						htmltext = "31537-13.htm";
						break;
					case 2:
						htmltext = "31537-14.htm";
						break;
				}
				break;
		}
		
		return htmltext;
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
		
		return "31537-20.html";
	}
		
	public static void main(String[] args)
	{
		new _020_BringUpWithLove(20, "_020_BringUpWithLove", "Bring Up With Love");
	}
}
