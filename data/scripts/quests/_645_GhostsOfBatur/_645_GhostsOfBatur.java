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
package quests._645_GhostsOfBatur;

import l2.universe.ExternalConfig;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 * 
 * @author Synerge
 */
public class _645_GhostsOfBatur extends Quest
{
	private static final int KARUDA = 32017;
	private static final int CURSED_BURIAL = 14861;
	private static final int[] MOBS = { 22703, 22704, 22705 };
	
	public _645_GhostsOfBatur(int id, String name, String descr)
	{
		super(id, name, descr);
		
		addStartNpc(KARUDA);
		addTalkId(KARUDA);
		
		for (final int i : MOBS)
			addKillId(i);	
		
		questItemIds = new int[] { CURSED_BURIAL };
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		// The values of quantity for materials was calculated by 500/necessary items due to lack of information
		// If player can reach the exchange html without get 500 items again this need to be updated.
		final QuestState st = player.getQuestState(getName());
		if (st == null)
			return null;

		String htmltext = "";

		if (event.equalsIgnoreCase("32017-03.htm"))
		{
			if (player.getLevel() < 80)
			{
				htmltext = "32017-02.htm";
				st.exitQuest(true);
			}
			else
			{
				st.set("cond", "1");
				st.setState(State.STARTED);
				st.playSound("ItemSound.quest_accept");
			}
		}
		else if (event.equalsIgnoreCase("Reward"))
		{
			htmltext = "32017-06.htm";
		}
		else if (event.equalsIgnoreCase("RDB"))
		{
			if (st.getQuestItemsCount(CURSED_BURIAL) >= 500)
			{
				st.takeItems(CURSED_BURIAL, 500);
				st.rewardItems(9968, 1);
				st.playSound("ItemSound.quest_finish");
				st.exitQuest(true);
				htmltext = "32017-07.htm";
			}
			else
				htmltext = "32017-04.htm";
		}
		else if (event.equalsIgnoreCase("RDP"))
		{
			if (st.getQuestItemsCount(CURSED_BURIAL) >= 500)
			{
				st.takeItems(CURSED_BURIAL, 500);
				st.rewardItems(9969, 1);
				st.playSound("ItemSound.quest_finish");
				st.exitQuest(true);
				htmltext = "32017-07.htm";
			}
			else
				htmltext = "32017-04.htm";
		}
		else if (event.equalsIgnoreCase("RDW"))
		{
			if (st.getQuestItemsCount(CURSED_BURIAL) >= 500)
			{
				st.takeItems(CURSED_BURIAL, 500);
				st.rewardItems(9970, 1);
				st.playSound("ItemSound.quest_finish");
				st.exitQuest(true);
				htmltext = "32017-07.htm";
			}
			else
				htmltext = "32017-04.htm";
		}
		else if (event.equalsIgnoreCase("RDK"))
		{
			if (st.getQuestItemsCount(CURSED_BURIAL) >= 500)
			{
				st.takeItems(CURSED_BURIAL, 500);
				st.rewardItems(9971, 1);
				st.playSound("ItemSound.quest_finish");
				st.exitQuest(true);
				htmltext = "32017-07.htm";
			}
			else
				htmltext = "32017-04.htm";
		}
		else if (event.equalsIgnoreCase("RDH"))
		{
			if (st.getQuestItemsCount(CURSED_BURIAL) >= 500)
			{
				st.takeItems(CURSED_BURIAL, 500);
				st.rewardItems(9972, 1);
				st.playSound("ItemSound.quest_finish");
				st.exitQuest(true);
				htmltext = "32017-07.htm";
			}
			else
				htmltext = "32017-04.htm";
		}
		else if (event.equalsIgnoreCase("RDC"))
		{
			if (st.getQuestItemsCount(CURSED_BURIAL) >= 500)
			{
				st.takeItems(CURSED_BURIAL, 500);
				st.rewardItems(9973, 1);
				st.playSound("ItemSound.quest_finish");
				st.exitQuest(true);
				htmltext = "32017-07.htm";
			}
			else
				htmltext = "32017-04.htm";
		}
		else if (event.equalsIgnoreCase("RDM"))
		{
			if (st.getQuestItemsCount(CURSED_BURIAL) >= 500)
			{
				st.takeItems(CURSED_BURIAL, 500);
				st.rewardItems(9974, 1);
				st.playSound("ItemSound.quest_finish");
				st.exitQuest(true);
				htmltext = "32017-07.htm";
			}
			else
				htmltext = "32017-04.htm";
		}
		else if (event.equalsIgnoreCase("RDN"))
		{
			if (st.getQuestItemsCount(CURSED_BURIAL) >= 500)
			{
				st.takeItems(CURSED_BURIAL, 500);
				st.rewardItems(9975, 1);
				st.playSound("ItemSound.quest_finish");
				st.exitQuest(true);
				htmltext = "32017-07.htm";
			}
			else
				htmltext = "32017-04.htm";
		}
		else if (event.equalsIgnoreCase("LEO"))
		{
			if (st.getQuestItemsCount(CURSED_BURIAL) >= 500)
			{
				st.takeItems(CURSED_BURIAL, 500);
				st.rewardItems(9628, 62);
				st.playSound("ItemSound.quest_finish");
				st.exitQuest(true);
				htmltext = "32017-07.htm";
			}
			else
				htmltext = "32017-04.htm";
		}
		else if (event.equalsIgnoreCase("ADA"))
		{
			if (st.getQuestItemsCount(CURSED_BURIAL) >= 500)
			{
				st.takeItems(CURSED_BURIAL, 500);
				st.rewardItems(9629, 33);
				st.playSound("ItemSound.quest_finish");
				st.exitQuest(true);
				htmltext = "32017-07.htm";
			}
			else
				htmltext = "32017-04.htm";
		}
		else if (event.equalsIgnoreCase("ORI"))
		{
			if (st.getQuestItemsCount(CURSED_BURIAL) >= 500)
			{
				st.takeItems(CURSED_BURIAL, 500);
				st.rewardItems(9630, 41);
				st.playSound("ItemSound.quest_finish");
				st.exitQuest(true);
				htmltext = "32017-07.htm";
			}
			else
				htmltext = "32017-04.htm";
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
			case State.COMPLETED :
				htmltext = getAlreadyCompletedMsg(player);
				break;
			case State.CREATED :
				htmltext = "32017-01.htm";
				break;
			case State.STARTED :
				switch (st.getInt("cond"))
				{
					case 0:
						htmltext = "32017-01.htm";
						break;
					case 1:
						htmltext = "32017-04.htm";
						break;
					case 2:
						if (st.getQuestItemsCount(CURSED_BURIAL) >= 500)
							htmltext = "32017-05.htm";
						else
							htmltext = "32017-01.htm";
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
		if (st == null || st.getInt("cond") != 1)
			return null;
		
		final int npcId = npc.getNpcId();				
		if (contains(MOBS, npcId))
		{
			long chance = ExternalConfig.CursedBurialDropChance;
			if (npcId == 22705)
				chance += 5;
			
			if (st.getRandom(100) <= chance)
			{
				st.giveItems(CURSED_BURIAL, 1);
				
				if (st.getQuestItemsCount(CURSED_BURIAL) >= 500)
				{
					st.playSound("ItemSound.quest_middle");
					st.set("cond", "2");
				}
				else
					st.playSound("ItemSound.quest_itemget");
			}
		}

		return super.onKill(npc, player, isPet);
	}
	
	public static void main(String[] args)
	{
		new _645_GhostsOfBatur(645, "_645_GhostsOfBatur", "Ghosts of Batur");
	}
}
