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
package quests._10283_RequestOfIceMerchant;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 ** @author Gnacik, Synerge
 **
 ** 2010-08-07 Based on Freya PTS
 */
public class _10283_RequestOfIceMerchant extends Quest
{
	// NPC's
	private static final int RAFFORTY = 32020;
	private static final int KIER = 32022;
	private static final int JINIA = 32760;
	
	public _10283_RequestOfIceMerchant(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(RAFFORTY);
		addTalkId(RAFFORTY);
		addTalkId(KIER);
		addFirstTalkId(JINIA);
		addTalkId(JINIA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null) 
			return event;
		
		switch (npc.getNpcId())
		{
			case RAFFORTY:
				if (event.equalsIgnoreCase("32020-03.htm"))
				{
					st.setState(State.STARTED);
					st.set("cond", "1");
					st.playSound("ItemSound.quest_accept");
				}
				else if (event.equalsIgnoreCase("32020-07.htm"))
				{
					st.set("cond", "2");
					st.playSound("ItemSound.quest_middle");
				}
				break;
			case KIER:
				if (event.equalsIgnoreCase("spawn"))
				{
					addSpawn(JINIA, 104322, -107669, -3680, 44954, false, 60000);
					return null;
				}
				break;
			case JINIA:
				if (event.equalsIgnoreCase("32760-04.html"))
				{
					st.giveItems(57, 190000);
					st.addExpAndSp(627000, 50300);
					st.playSound("ItemSound.quest_finish");
					st.exitQuest(false);
					npc.deleteMe();
				}
				break;
		}

		return event;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		QuestState qs = player.getQuestState(getName());
		if (qs == null)
			return htmltext;
		
		final int cond = qs.getInt("cond");
		final int npcId = npc.getNpcId();
		
		switch (qs.getState())
		{
			case State.COMPLETED:
				htmltext = "32020-09.htm";
				break;
			case State.CREATED:
				if (npcId == RAFFORTY)
				{
					final QuestState _prev = player.getQuestState("115_TheOtherSideOfTruth");
					if (_prev != null && _prev.getState() == State.COMPLETED && player.getLevel() >= 82)
						htmltext = "32020-01.htm";
					else
					{
						htmltext = "32020-00.htm";
						qs.exitQuest(true);
					}
				}
				break;
			case State.STARTED:
				switch (npcId)
				{
					case RAFFORTY:
						switch (cond)
						{
							case 1:
								htmltext = "32020-04.htm";
								break;
							case 2:
								htmltext = "32020-08.htm";
								break;
						}
						break;
					case KIER:
						switch (cond)
						{
							case 2:
								htmltext = "32022-01.html";
								break;						
						}
						break;
					case JINIA:
						switch (cond)
						{
							case 2:
								htmltext = "32760-02.html";
								break;
						}
						break;
				}
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
			return null;
		
		if (npc.getNpcId() == JINIA && st.getInt("cond") == 2)
			return "32760-01.html";
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new _10283_RequestOfIceMerchant(10283, "_10283_RequestOfIceMerchant", "Request of Ice Merchant");
	}
}
