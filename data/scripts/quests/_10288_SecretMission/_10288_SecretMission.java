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
package quests._10288_SecretMission;

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
public class _10288_SecretMission extends Quest
{
	// NPC's
	private static final int DOMINIC  = 31350;
	private static final int AQUILANI = 32780;
	private static final int GREYMORE = 32757;
	// Items
	private static final int LETTER = 15529;
	
	public _10288_SecretMission(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(DOMINIC);
		addStartNpc(AQUILANI);
		addTalkId(DOMINIC);
		addTalkId(GREYMORE);
		addTalkId(AQUILANI);
		addFirstTalkId(AQUILANI);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null) 
			return event;
		
		switch (npc.getNpcId())
		{
			case DOMINIC:
				if (event.equalsIgnoreCase("31350-05.htm"))
				{
					st.setState(State.STARTED);
					st.set("cond", "1");
					st.giveItems(LETTER, 1);
					st.playSound("ItemSound.quest_accept");
				}
				break;
			case GREYMORE:
				if (event.equalsIgnoreCase("32757-03.htm"))
				{
					st.unset("cond");
					st.takeItems(LETTER, -1);
					st.giveItems(57, 106583);
					st.addExpAndSp(417788, 46320);
					st.playSound("ItemSound.quest_finish");
					st.exitQuest(false);
				}
				break;
			case AQUILANI:
				switch (st.getState())
				{
					case State.STARTED:
						if (event.equalsIgnoreCase("32780-05.html"))
						{
							st.set("cond", "2");
							st.playSound("ItemSound.quest_middle");
						}
						break;
					case State.COMPLETED:
						if (event.equalsIgnoreCase("teleport"))
						{
							player.teleToLocation(118833, -80589, -2688);
							return null;
						}
						break;
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
			case State.COMPLETED :
				htmltext = "31350-08.htm";
				break;
			case State.CREATED:
				if (npcId == DOMINIC)
				{
					if (player.getLevel() >= 82)
						htmltext = "31350-01.htm";
					else
					{
						htmltext = "31350-00.htm";
						qs.exitQuest(true);
					}
				}
				break;
			case State.STARTED:
				switch (npcId)
				{
					case DOMINIC:
						switch (cond)
						{
							case 1:
								htmltext = "31350-06.htm";
								break;
							case 2:
								htmltext = "31350-07.htm";
								break;
						}
						break;
					case AQUILANI:
						switch (cond)
						{
							case 1:
								htmltext = "32780-03.html";
								break;	
							case 2:
								htmltext = "32780-06.html";
								break;
						}
						break;
					case GREYMORE:
						switch (cond)
						{
							case 2:
								htmltext = "32757-01.htm";
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
		QuestState st = player.getQuestState(getName());
		if (st == null)
			st = newQuestState(player);
		
		if (npc.getNpcId() == AQUILANI)
		{
			if (st.getState() == State.COMPLETED)
				return "32780-01.html";
			else
				return "32780-00.html";
		}
		
		return null;
	}
		
	public static void main(String[] args)
	{
		new _10288_SecretMission(10288, "_10288_SecretMission", "Secret Mission");
	}
}
