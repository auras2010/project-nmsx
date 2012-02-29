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
package quests._104_SpiritOfMirrors;

import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

/**
 *
 * @author Evilus, Synerge
 */
public class _104_SpiritOfMirrors extends Quest
{
	// QUEST ITEMS
	private final static int GALLINT_OAK_WAND = 748;
	private final static int WAND_SPIRITBOUND1 = 1135;
	private final static int WAND_SPIRITBOUND2 = 1136;
	private final static int WAND_SPIRITBOUND3 = 1137;
	
	// REWARDS
	private final static int LONG_SWORD = 2;
	private final static int WAND_OF_ADEPT = 747;
	private final static int SPIRITSHOT_NO_GRADE_FOR_BEGINNERS = 5790;
	private final static int SOULSHOT_NO_GRADE_FOR_BEGINNERS = 5789;
	private final static int SPIRITSHOT_NO_GRADE = 2509;
	private final static int SOULSHOT_NO_GRADE = 1835;
	private final static int LESSER_HEALING_POT = 1060;
	private final static int ADENA = 57;
	
	// NPC
	private final static int GALLINT = 30017;
	private final static int ARNOLD = 30041;
	private final static int JOHNSTONE = 30043;
	private final static int KENYOS = 30045;
	
	// MOBS
	private final static int SPIRIT_OF_MIRROR1 = 27003;
	private final static int SPIRIT_OF_MIRROR2 = 27004;
	private final static int SPIRIT_OF_MIRROR3 = 27005;

	private final int[] TALK_NPC = {30017,30041,30043,30045,};

	private static final int[][] DROPLIST_COND = 
	{
		{SPIRIT_OF_MIRROR1, WAND_SPIRITBOUND1},
		{SPIRIT_OF_MIRROR2, WAND_SPIRITBOUND2},
		{SPIRIT_OF_MIRROR3, WAND_SPIRITBOUND3}
	};

	public _104_SpiritOfMirrors(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(GALLINT);
		for (int npcId : TALK_NPC)
			addTalkId(npcId);

		for (int i = 0; i < DROPLIST_COND.length; i++)
			addKillId(DROPLIST_COND[i][0]);
		
		questItemIds = new int[] {WAND_SPIRITBOUND1, WAND_SPIRITBOUND2, WAND_SPIRITBOUND3, GALLINT_OAK_WAND};
	}

	@Override
	public String onEvent(String event, QuestState st)
	{
		if (st == null)
			return event;
		
		if (event.equalsIgnoreCase("30017-03.htm"))
		{
			st.set("cond", "1");
			st.setState(State.STARTED);
			st.playSound("ItemSound.quest_accept");
			st.giveItems(GALLINT_OAK_WAND, 3);
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
		final int npcId = npc.getNpcId();
		
		switch(st.getState())
		{
			case State.COMPLETED :
				htmltext = getAlreadyCompletedMsg(player);
				break;
			case State.CREATED :
				if (st.getPlayer().getRace().ordinal() != 0)
				{
					htmltext = "30017-00.htm";
					st.exitQuest(true);
				}
				else if (st.getPlayer().getLevel() >= 10)
				{
					htmltext = "30017-02.htm";
				}
				else
				{
					htmltext = "30017-06.htm";
					st.exitQuest(true);
				}
				break;
			case State.STARTED :
				switch (npcId)
				{
					case GALLINT:
						switch (cond)
						{
							case 1:
								if(st.getQuestItemsCount(GALLINT_OAK_WAND) >= 1
										&& st.getQuestItemsCount(WAND_SPIRITBOUND1) == 0
										|| st.getQuestItemsCount(WAND_SPIRITBOUND2) == 0
										|| st.getQuestItemsCount(WAND_SPIRITBOUND3) == 0)
									htmltext = "30017-04.htm";
								break;
							case 3:
								if (st.getQuestItemsCount(WAND_SPIRITBOUND1) == 1
										&& st.getQuestItemsCount(WAND_SPIRITBOUND2) == 1
										&& st.getQuestItemsCount(WAND_SPIRITBOUND3) == 1)
								{
									st.takeItems(WAND_SPIRITBOUND1, 1);
									st.takeItems(WAND_SPIRITBOUND2, 1);
									st.takeItems(WAND_SPIRITBOUND3, 1);
									st.giveItems(LESSER_HEALING_POT, 100);
									
									for (int ECHO_CHRYSTAL = 4412; ECHO_CHRYSTAL <= 4416; ECHO_CHRYSTAL++)
										st.giveItems(ECHO_CHRYSTAL, 10);
									
									if (st.getPlayer().getClassId().isMage())
									{
										st.giveItems(SPIRITSHOT_NO_GRADE_FOR_BEGINNERS, 3000);
										st.giveItems(SPIRITSHOT_NO_GRADE, 500);
										st.giveItems(WAND_OF_ADEPT, 1);
									}
									else
									{
										st.giveItems(SOULSHOT_NO_GRADE_FOR_BEGINNERS, 6000);
										st.giveItems(SOULSHOT_NO_GRADE, 1000);
										st.giveItems(LONG_SWORD, 1);
									}
									st.addRewardExpAndSp(39750, 3407);
									st.giveItems(ADENA, 16866);
									htmltext = "30017-05.htm";
									st.unset("cond");
									st.exitQuest(false);
									st.playSound("ItemSound.quest_finish");
								}
								break;
						}
						break;		
					case ARNOLD:
					case JOHNSTONE:
					case KENYOS:
						if (cond >= 1 )
						{
							st.set("cond","2");
							st.playSound("ItemSound.quest_middle");
							htmltext = npcId + "-01.htm";
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

		if (st.getPlayer().getActiveWeaponInstance() != null && st.getPlayer().getActiveWeaponInstance().getItemId() == GALLINT_OAK_WAND)
		{
			final int npcId = npc.getNpcId();
			final L2ItemInstance weapon = st.getPlayer().getActiveWeaponInstance();
			for (int i = 0; i < DROPLIST_COND.length; i++)
			{
				if (st.getItemEquipped(5) == GALLINT_OAK_WAND && npcId == DROPLIST_COND[i][0] && st.getQuestItemsCount(DROPLIST_COND[i][1]) == 0)
				{
					st.getPlayer().getInventory().destroyItem("weapon", weapon, 1, player, npc);
					st.getPlayer().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED).addItemName(GALLINT_OAK_WAND));
					st.giveItems(DROPLIST_COND[i][1], 1);
					st.getPlayer().getInventory().reloadEquippedItems();

					final long HaveAllQuestItems = st.getQuestItemsCount(WAND_SPIRITBOUND1) + st.getQuestItemsCount(WAND_SPIRITBOUND2) + st.getQuestItemsCount(WAND_SPIRITBOUND3);
					if(HaveAllQuestItems == 3)
					{
						st.set("cond", "3");
						st.playSound("ItemSound.quest_middle");
					}
					else
						st.playSound("ItemSound.quest_itemget");
				}
			}
		}
		
		return null;
	}

	public static void main(String[] args)
	{
		new _104_SpiritOfMirrors(104, "_104_SpiritOfMirrors", "Spirit Of Mirrors");
	}
}
