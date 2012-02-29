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
package quests._148_PathtoBecomingAnExaltedMercenary;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 ** @author Gnacik, Synerge
 **
 ** 2010-09-30 Based on official server Franz
 */
public class _148_PathtoBecomingAnExaltedMercenary extends Quest
{
	// NPCs
	private static final int[] MERCENARY = { 36481, 36482, 36483, 36484, 36485, 36486, 36487, 36488, 36489 };
	// Items
	private static final int CERT_ELITE = 13767;
	private static final int CERT_TOP_ELITE = 13768;
	
	public _148_PathtoBecomingAnExaltedMercenary(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		for(int _npc : MERCENARY)
		{
			addStartNpc(_npc);
			addTalkId(_npc);
		}
		
		questItemIds = new int[] { CERT_ELITE };
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null) 
			return event;
		
		if (!contains(MERCENARY, npc.getNpcId()))
			return event;
		
		if (event.equalsIgnoreCase("exalted-00b.htm"))
		{
			st.giveItems(CERT_ELITE, 1);
		}
		else if (event.equalsIgnoreCase("exalted-03.htm"))
		{
			st.setState(State.STARTED);
			st.set("cond", "1");
			st.playSound("ItemSound.quest_accept");
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
		
		if (!contains(MERCENARY, npc.getNpcId()))
			return htmltext;
		
		switch (st.getState())
		{
			case State.COMPLETED :
				htmltext = getAlreadyCompletedMsg(player);
				break;
			case State.CREATED :				
				if (player.getClan() != null && player.getClan().getHasCastle() > 0)
					htmltext = "castle.htm";
				else if (st.hasQuestItems(CERT_ELITE))
					htmltext = "exalted-01.htm";
				else
				{
					final QuestState prevQ = player.getQuestState("_147_PathtoBecomingAnEliteMercenary");
					if (prevQ != null && prevQ.getState() == State.COMPLETED)
						htmltext = "exalted-00a.htm";
					else
						htmltext = "exalted-00.htm";
				}
				break;
			case State.STARTED :
				if (st.getInt("cond") < 4)
					htmltext = "elite-04.htm";
				else if (st.getInt("cond") == 4)
				{
					st.unset("cond");
					st.unset("kills");
					st.takeItems(CERT_ELITE, -1);
					st.giveItems(CERT_TOP_ELITE, 1);
					st.exitQuest(false);
					htmltext = "exalted-05.htm";
				}
				break;
		}
		
		return htmltext;
	}
		
	public static void main(String[] args)
	{
		new _148_PathtoBecomingAnExaltedMercenary(148, "_148_PathtoBecomingAnExaltedMercenary", "Path to Becoming an Exalted Mercenary");
	}
}
