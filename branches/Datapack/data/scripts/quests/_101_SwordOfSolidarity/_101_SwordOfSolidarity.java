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
package quests._101_SwordOfSolidarity;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;
import l2.universe.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * 
 * @author Synerge
 */
public class _101_SwordOfSolidarity extends Quest
{
	private static final int ROIENS_LETTER = 796;
	private static final int HOWTOGO_RUINS = 937;
	private static final int BROKEN_SWORD_HANDLE = 739;
	private static final int BROKEN_BLADE_BOTTOM = 740;
	private static final int BROKEN_BLADE_TOP = 741;
	private static final int ALLTRANS_NOTE = 742;
	private static final int SWORD_OF_SOLIDARITY = 738;
	
	private static final int ROIEN = 30008;
	private static final int ALTRAN = 30283;

	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	
	public _101_SwordOfSolidarity(int id, String name, String desc)
	{
		super(id,name,desc);
		questItemIds = new int[] { ROIENS_LETTER, HOWTOGO_RUINS, BROKEN_SWORD_HANDLE, BROKEN_BLADE_BOTTOM,
				BROKEN_BLADE_TOP, ALLTRANS_NOTE };

		addStartNpc(ROIEN);
		addTalkId(ROIEN);
		addTalkId(ALTRAN);

		addKillId(20361);
		addKillId(20362);
	}

	@Override
	public String onAdvEvent (String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null) 
			return event;
		
		if (event.equalsIgnoreCase("30008-04.htm"))
		{
			st.set("cond","1");
			st.setState(State.STARTED);
			st.playSound("ItemSound.quest_accept");
			st.giveItems(ROIENS_LETTER,1);
		}
		else if (event.equalsIgnoreCase("30283-02.htm"))
		{
			st.set("cond","2");
			st.playSound("ItemSound.quest_middle");
			st.takeItems(ROIENS_LETTER,st.getQuestItemsCount(ROIENS_LETTER));
			st.giveItems(HOWTOGO_RUINS,1);
		}
		else if (event.equalsIgnoreCase("30283-07.htm"))
		{
			st.giveReward(57, 10981);
			st.takeItems(BROKEN_SWORD_HANDLE,-1);
			st.giveItems(SWORD_OF_SOLIDARITY,1);
			st.giveReward(1060, 100); // Lesser Healing Potions
			st.giveReward(4412, 10);
			st.giveReward(4413, 10);
			st.giveReward(4414, 10);
			st.giveReward(4415, 10);
			st.giveReward(4416, 10);
			st.giveReward(4417, 10);
			st.addRewardExpAndSp(25747, 2171);
			st.unset("cond");
			st.exitQuest(false);
			st.playSound("ItemSound.quest_finish");
			
			// Check the player state against this quest newbie rewarding mark.
			player = st.getPlayer();
			if (!player.getClassId().isMage())
			{
				st.giveReward(SOULSHOT_FOR_BEGINNERS, 7000);
				st.playTutorialVoice("tutorial_voice_026");
				player.sendPacket(new ExShowScreenMessage("Acquisition of race-specific weapon complete. Go find the Newbie Guide.",3000));
			}
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
		
		final int cond = st.getInt("cond");
		switch(st.getState())
		{
			case State.COMPLETED :
				htmltext = getAlreadyCompletedMsg(player);
				break;
			case State.CREATED :
				if (player.getRace().ordinal() != 0)
					htmltext = "30008-00.htm";
				else if (player.getLevel() >= 9)
				{
					htmltext = "30008-02.htm";
				}
				else
				{
					htmltext = "30008-08.htm";
					st.exitQuest(true);
				}
				break;
			case State.STARTED :
				switch (npc.getNpcId())
				{
					case ROIEN:
						switch (cond)
						{
							case 1:
								if (st.getQuestItemsCount(ROIENS_LETTER) ==1)
									htmltext = "30008-05.htm";
								break;
							case 4:
								 if (st.getQuestItemsCount(ROIENS_LETTER) == 0 && st.getQuestItemsCount(ALLTRANS_NOTE) > 0)
								{
									htmltext = "30008-06.htm";
									st.set("cond","5");
									st.playSound("ItemSound.quest_middle");
									st.takeItems(ALLTRANS_NOTE,st.getQuestItemsCount(ALLTRANS_NOTE));
									st.giveItems(BROKEN_SWORD_HANDLE,1);
								}
								break;
							default:
								if (cond >= 2 && st.getQuestItemsCount(ROIENS_LETTER) == 0 &&	st.getQuestItemsCount(ALLTRANS_NOTE) == 0)
								{
									if (st.getQuestItemsCount(BROKEN_BLADE_TOP) > 0 && st.getQuestItemsCount(BROKEN_BLADE_BOTTOM) > 0)
										htmltext = "30008-12.htm";
									else if (st.getQuestItemsCount(BROKEN_BLADE_TOP) + st.getQuestItemsCount(BROKEN_BLADE_BOTTOM) <= 1)
										htmltext = "30008-11.htm";
									else if (st.getQuestItemsCount(BROKEN_SWORD_HANDLE) > 0)
										htmltext = "30008-07.htm";
									else if (st.getQuestItemsCount(HOWTOGO_RUINS) == 1)
										htmltext = "30008-10.htm";
								}
								break;
						}
						break;
					case ALTRAN:
						switch (cond)
						{
							case 1:
								if (st.getQuestItemsCount(ROIENS_LETTER) > 0)
									htmltext = "30283-01.htm";
								break;
							case 4:
								if (st.getQuestItemsCount(ALLTRANS_NOTE) > 0)
									htmltext = "30283-05.htm";
								break;
							case 5:
								if (st.getQuestItemsCount(BROKEN_SWORD_HANDLE) > 0)
									htmltext = "30283-06.htm";
								break;
							default:
								if (cond >= 2 && st.getQuestItemsCount(ROIENS_LETTER) == 0 && st.getQuestItemsCount(HOWTOGO_RUINS) > 0)
								{
									if (st.getQuestItemsCount(BROKEN_BLADE_TOP) + st.getQuestItemsCount(BROKEN_BLADE_BOTTOM) == 1)
										htmltext = "30283-08.htm";
									else if (st.getQuestItemsCount(BROKEN_BLADE_TOP) + st.getQuestItemsCount(BROKEN_BLADE_BOTTOM) == 0)
										htmltext = "30283-03.htm";
									else if (st.getQuestItemsCount(BROKEN_BLADE_TOP) > 0 && st.getQuestItemsCount(BROKEN_BLADE_BOTTOM) > 0)
									{
										htmltext = "30283-04.htm";
										st.set("cond","4");
										st.playSound("ItemSound.quest_middle");
										st.takeItems(HOWTOGO_RUINS,st.getQuestItemsCount(HOWTOGO_RUINS));
										st.takeItems(BROKEN_BLADE_TOP,st.getQuestItemsCount(BROKEN_BLADE_TOP));
										st.takeItems(BROKEN_BLADE_BOTTOM,st.getQuestItemsCount(BROKEN_BLADE_BOTTOM));
										st.giveItems(ALLTRANS_NOTE,1);
									}
								}
								break;
						}
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
		
		switch (npc.getNpcId())
		{
			case 20361:
			case 20362:
				if (st.getQuestItemsCount(HOWTOGO_RUINS) > 0)
				{
					if (st.getQuestItemsCount(BROKEN_BLADE_TOP) == 0)
					{
						if (st.getRandom(5) == 0)
						{
							st.giveItems(BROKEN_BLADE_TOP,1);
							st.playSound("ItemSound.quest_itemget");
						}
					}
					else if (st.getQuestItemsCount(BROKEN_BLADE_BOTTOM) == 0)
					{
						if (st.getRandom(5) == 0)
						{
							st.giveItems(BROKEN_BLADE_BOTTOM,1);
							st.playSound("ItemSound.quest_itemget");
						}
					}
				}
				if (st.getQuestItemsCount(BROKEN_BLADE_TOP) > 0 && st.getQuestItemsCount(BROKEN_BLADE_BOTTOM) > 0)
				{
					st.set("cond","3");
					st.playSound("ItemSound.quest_middle");
				}
				break;
		}

		return null;
	}

	public static void main(String[] args)
	{
		new _101_SwordOfSolidarity(101, "_101_SwordOfSolidarity", "quests");
	}
}
