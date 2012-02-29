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
package quests._008_AnAdventureBegins;

import l2.universe.Config;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 * @author Evilus, Synerge
 */
public class _008_AnAdventureBegins extends Quest
{
	/* Jasmine; Roselyn; Harne */
	private final static int QUEST_NPC[] = { 30134, 30355, 30144 };
	/* Roselyn's Note */
	private final static int QUEST_ITEM[] = { 7573 };
	/* SoE: Giran; Mark of Traveler */
	private final static int QUEST_REWARD[] = { 7559, 7570 };

	public _008_AnAdventureBegins(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(QUEST_NPC[0]);
		for (int npcId : QUEST_NPC)
			addTalkId(npcId);
		questItemIds = QUEST_ITEM;
	}

	@Override
	public String onEvent(String event, QuestState qs)
	{		
		if (qs == null)
			return null;
		
		String html = event;
		
		if (event.equalsIgnoreCase("30134-03.htm"))
		{
			qs.set("cond", "1");
			qs.setState(State.STARTED);
			qs.playSound("ItemSound.quest_accept");
		}
		else if (event.equalsIgnoreCase("30355-02.htm"))
		{
			qs.set("cond", "2");
			qs.giveItems(QUEST_ITEM[0], 1);
			qs.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("30144-02.htm"))
		{
			qs.set("cond", "3");
			qs.takeItems(QUEST_ITEM[0], 1);
			qs.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("30134-06.htm"))
		{
			qs.giveItems(QUEST_REWARD[0], (long) Config.RATE_QUEST_REWARD);
			qs.giveItems(QUEST_REWARD[1], 1);
			qs.unset("cond");
			qs.exitQuest(false);
			qs.playSound("ItemSound.quest_finish");
		}
		return html;
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState qs = player.getQuestState(getName());
		if (qs == null)
			qs = newQuestState(player);
		String html = "";
		final int cond = qs.getInt("cond");
		final int npcId = npc.getNpcId();

		switch (qs.getState())
		{
			case State.COMPLETED:
				html = msgQuestCompleted();
				break;
			case State.CREATED:
				if (npcId == QUEST_NPC[0])
				{
		            if (player.getRace().ordinal() == 2 && player.getLevel() >= 3)
		                html = "30134-02.htm";
		            else
		            {
		                html = "30134-01.htm";
		                qs.exitQuest(true);
		            }
				}
				break;
			case State.STARTED:
				if (npcId == QUEST_NPC[1])
				{
					switch (cond)
					{
						case 1:
					         if (qs.getQuestItemsCount(QUEST_ITEM[0]) == 0)
					        	 html = "30355-01.htm";
					         break;
						case 2:
							html = "30355-03.htm";
							break;
					}
				}
				else if (npcId == QUEST_NPC[0])
				{
					switch (cond)
					{
						case 1:
					         if (qs.getQuestItemsCount(QUEST_ITEM[0]) == 0)
					        	 html = "30355-01.htm";
					         break;
						case 2:
							html = "30355-03.htm";
							break;
					}
				}
				else if (npcId == QUEST_NPC[2])
				{
					if (cond == 2 && qs.getQuestItemsCount(QUEST_ITEM[0]) > 0)
						html = "30144-01.htm";
				}
				break;
		}

		return html;
	}

	public static void main(String[] args)
	{
		new _008_AnAdventureBegins(8, "_008_AnAdventureBegins", "An Adventure Begins");
	}
}
