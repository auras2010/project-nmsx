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
package quests._004_LongLiveThePaagrioLord;

import l2.universe.Config;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 * @author Evilus, Synerge
 */
public class _004_LongLiveThePaagrioLord extends Quest
{
	// Tataru Zu Hestui; Varkees; Grookin; Uska; Kunai; Gantaki Zu Urutu
	// Honey Khandar; Bear Fur Cloak; Bloody Axe; Ancestor Skull; Spider Dust; Deep Sea Orb
	private final static int QUEST_GIFTS[][] = 
	{ 
		{ 30585, 30566, 30562, 30560, 30559, 30587 }, 
		{ 1541, 1542, 1543, 1544, 1545, 1546 } 
	};

	private final static int NAKUSIN = 30578;
	
	// Adena; Club
	private final static int QUEST_REWARD[] = { 57, 4 };

	public _004_LongLiveThePaagrioLord(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(NAKUSIN);
		addTalkId(NAKUSIN);
		for (int npcId : QUEST_GIFTS[0])
			addTalkId(npcId);
		questItemIds = QUEST_GIFTS[1];
	}

	@Override
	public String onEvent(String event, QuestState qs)
	{
		if (qs == null)
			return null;
		
		if (event.equalsIgnoreCase("30578-03.htm"))
		{
			qs.set("cond","1");
			qs.set("id","1");
			qs.setState(State.STARTED);
			qs.playSound("ItemSound.quest_accept");
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
		final int cond = qs.getInt("cond");
		final int npcId = npc.getNpcId();
		
		switch (qs.getState())
		{
			case State.COMPLETED:
				html = msgQuestCompleted();
				break;
			case State.CREATED:
				if (npcId == NAKUSIN)
				{
					if (player.getRace().ordinal() != 3)
					{
						html = "30578-00.htm";
						qs.exitQuest(true);
					}
					else if (player.getLevel() >= 2)
					{
						html = "30578-02.htm";
					}
					else
					{
						html = "30578-01.htm";
						qs.exitQuest(true);
					}
				}
				break;
			case State.STARTED:
				switch (cond)
				{
					case 1:
						if (npcId == NAKUSIN)
							html = "30578-04.htm";
						else if (npcId == checkNpc(npcId))
						{
							int index = 0;
							for (int i = 0; i < QUEST_GIFTS[1].length; i++)
							{
								if (QUEST_GIFTS[0][i] == checkNpc(npcId))
								{
									index = i;
									break;
								}
							}
							
							if (qs.getQuestItemsCount(QUEST_GIFTS[1][index]) > 0)
								html = String.valueOf(npcId).concat("-02.htm");
							else
							{
								qs.giveItems(QUEST_GIFTS[1][index], 1);
								html = String.valueOf(npcId).concat("-01.htm");

								int count = 0;
								for (int item : QUEST_GIFTS[1])
								{
									if (qs.getQuestItemsCount(item) > 0)
										count += 1;
								}	
								
								if (count == 6)
								{
									qs.set("cond", "2");
									qs.set("id", "2");
									qs.playSound("ItemSound.quest_middle");
								}
								else
									qs.playSound("ItemSound.quest_itemget");
							}
						}
						break;
					case 2:
						if (npcId == NAKUSIN)
						{
							html = "30578-06.htm";
							qs.giveItems(QUEST_REWARD[1], (long) Config.RATE_QUEST_REWARD);
							qs.giveItems(QUEST_REWARD[0], Math.round(1850 * Config.RATE_QUEST_REWARD_ADENA));
							qs.addRewardExpAndSp(4254,335);
							for (int item : QUEST_GIFTS[1])
								qs.takeItems(item, -1);
							qs.unset("cond");
							qs.exitQuest(false);
							qs.playSound("ItemSound.quest_finish");
						}
						break;
				}
				break;
		}
		return html;		
	}
	
	private int checkNpc(int npcId)
	{
		for (int id : QUEST_GIFTS[0])
		{
			if (id == npcId)
				return id;
		}		
		return 0;
	}
	
	public static void main(String[] args)
	{
		new _004_LongLiveThePaagrioLord(4, "_004_LongLiveThePaagrioLord", "Long Live the Paagrio Lord");
	}
}
