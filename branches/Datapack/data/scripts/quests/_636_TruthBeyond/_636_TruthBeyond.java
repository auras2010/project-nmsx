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
package quests._636_TruthBeyond;

import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;
import l2.universe.gameserver.model.zone.L2ZoneType;

/**
 * 
 * @author moved to java by DS, jython script by Polo, BiTi and DrLecter, Synerge
 *
 */
public final class _636_TruthBeyond extends Quest
{
	private static final int ELIAH = 31329;
	private static final int FLAURON = 32010;
	private static final int ZONE = 30100;
	private static final int VISITOR_MARK = 8064;
	private static final int FADED_MARK = 8065;
	private static final int MARK = 8067;
	
	public _636_TruthBeyond(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(ELIAH);
		addTalkId(ELIAH);
		addTalkId(FLAURON);
		addEnterZoneId(ZONE);
	}
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null) 
			return event;
		
		if (event.equalsIgnoreCase("31329-04.htm"))
		{
			st.set("cond", "1");
			st.setState(State.STARTED);
			st.playSound("ItemSound.quest_accept");
		}
		else if (event.equalsIgnoreCase("32010-02.htm"))
		{
			st.giveItems(VISITOR_MARK, 1);
			st.playSound("ItemSound.quest_finish");
			st.exitQuest(true);
		}
		return event;
	}
	
	@Override
	public final String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
		if (st.getQuestItemsCount(VISITOR_MARK) > 0
				|| st.getQuestItemsCount(FADED_MARK) > 0
				|| st.getQuestItemsCount(MARK) > 0)
		{
			st.exitQuest(true);
			return "31329-mark.htm";
		}
		
		switch(st.getState())
		{
			case State.COMPLETED :
				htmltext = getAlreadyCompletedMsg(player);
				break;
			case State.CREATED :
				if (player.getLevel() > 72)
					htmltext = "31329-02.htm";
				else
				{
					htmltext = "31329-01.htm";
					st.exitQuest(true);
				}
				break;
			case State.STARTED :
				switch (npc.getNpcId())
				{
					case ELIAH:
						htmltext = "31329-05.htm";
						break;
					default:
						if (Integer.parseInt(st.get("cond")) == 1)
							return "32010-01.htm";
						else
						{
							st.exitQuest(true);
							return "32010-03.htm";
						}
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public final String onEnterZone(L2Character character, L2ZoneType zone)
	{
		// QuestState already null on enter because quest is finished
		if (character instanceof L2PcInstance)
		{
			if (((L2PcInstance)character).destroyItemByItemId("Mark", VISITOR_MARK, 1, character, false))
				((L2PcInstance)character).addItem("Mark", FADED_MARK, 1, character, true);
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		new _636_TruthBeyond(636, "_636_TruthBeyond", "The Truth Beyond the Gate");
	}
}
