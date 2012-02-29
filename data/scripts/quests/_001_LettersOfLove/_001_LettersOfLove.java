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
package quests._001_LettersOfLove;

import l2.universe.Config;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 * @author Evilus, Synerge
 */
public class _001_LettersOfLove extends Quest
{
	/* Darin; Roxxy; Baulro */
	private final static int QUEST_NPC[] = { 30048, 30006, 30033 };
	/* Darin's Letter; Roxxy's Kerchief; Darin's Receipt; Baulro's Potion */
	private final static int QUEST_ITEM[] = { 687, 688, 1079, 1080 };
	/* Adena; Necklace of Knowledge */
	private final static int QUEST_REWARD[] = { 57, 906 };

	public _001_LettersOfLove(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(QUEST_NPC[0]);
		for (int npcId : QUEST_NPC)
			addTalkId(npcId);
		questItemIds = QUEST_ITEM;
	}

	public String onAdvEvent(String event, QuestState qs)
	{		
		if (qs == null)
			return null;
		
		if (event.equalsIgnoreCase("30048-05.htm"))
		{
			qs.set("cond", "1");
			qs.setState(State.STARTED);
			qs.playSound("ItemSound.quest_accept");
			qs.giveItems(QUEST_ITEM[0], 1);
		}
		return event;
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState qs = player.getQuestState(getName());
		if (qs == null)
			qs = newQuestState(player);
		
		String html = "";
		final int npcId = npc.getNpcId();
		final int cond = qs.getInt("cond");
		
		switch (qs.getState())
		{
			case State.COMPLETED:
				html = msgQuestCompleted();
				break;
			case State.CREATED:
				if (npcId == QUEST_NPC[0])
				{
					if (player.getLevel() >= 2 && cond == 0)
						html = "30048-02.htm";
					else
					{
						html = "30048-01.htm";
						qs.exitQuest(true);
					}
				}
				break;
			case State.STARTED:
				if (npcId == QUEST_NPC[0])
				{
					switch (cond)
					{
						case 2:
							if (qs.getQuestItemsCount(QUEST_ITEM[1]) > 0)
							{
								html = "30048-07.htm";
								qs.takeItems(QUEST_ITEM[1], -1);
								qs.giveItems(QUEST_ITEM[2], 1);
								qs.set("cond", "3");
								qs.playSound("ItemSound.quest_middle");
							}
							break;
						case 3:
							if (qs.getQuestItemsCount(QUEST_ITEM[2]) > 0)
								html = "30048-08.htm";
							break;
						case 4:
							if (qs.getQuestItemsCount(QUEST_ITEM[3]) > 0)
							{
								html = "30048-09.htm";
								qs.takeItems(QUEST_ITEM[3], -1);
								qs.giveItems(QUEST_REWARD[0], 2466 * Math.round(Config.RATE_QUEST_REWARD_ADENA));
								qs.giveItems(QUEST_REWARD[1], (long) Config.RATE_QUEST_REWARD);
								qs.addRewardExpAndSp(5672, 446);
								qs.exitQuest(false);
								qs.playSound("ItemSound.quest_finish");
							}
							break;
						default:
							html = "30048-06.htm";
							break;
					}
				}
				else if (npcId == QUEST_NPC[1])
				{
					switch (cond)
					{
						case 1:
					         if (qs.getQuestItemsCount(QUEST_ITEM[0]) > 0)
				        	 {
								html = "30006-01.htm";
								qs.takeItems(QUEST_ITEM[0], -1);
								qs.giveItems(QUEST_ITEM[1], 1);
								qs.set("cond", "2");
								qs.playSound("ItemSound.quest_middle");
				        	 }
					         break;
						default:
							if (cond > 1)
								html = "30006-02.htm";
							break;
					}
				}
				else if (npcId == QUEST_NPC[2])
				{
					switch (cond)
					{
						case 3:
					         if (qs.getQuestItemsCount(QUEST_ITEM[2]) > 0)
				        	 {
					        	 html = "30033-01.htm";
					        	 qs.takeItems(QUEST_ITEM[2], -1);
					        	 qs.giveItems(QUEST_ITEM[3], 1);
					        	 qs.set("cond", "4");
					        	 qs.playSound("ItemSound.quest_middle");
				        	 }
					         break;
						default:
							if (cond > 3)
								html = "30033-02.htm";
							break;
					}
				}
				break;
		}
		return html;
	}

	public static void main(String[] args)
	{
		new _001_LettersOfLove(1, "_001_LettersOfLove", "Letters of Love");
	}
}
