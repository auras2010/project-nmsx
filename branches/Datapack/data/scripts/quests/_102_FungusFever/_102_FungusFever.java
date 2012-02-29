package quests._102_FungusFever;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 * 
 * @author Evilus, Synerge
 */
public class _102_FungusFever extends Quest
{
	// All Quest items
	private int ALBERRYUS_LETTER_ID = 964;
	private int EVERGREEN_AMULET_ID = 965;
	private int DRYAD_TEARS_ID = 966;
	private int ALBERRYUS_LIST_ID = 746;
	private int COBS_MEDICINE1_ID = 1130;
	private int COBS_MEDICINE2_ID = 1131;
	private int COBS_MEDICINE3_ID = 1132;
	private int COBS_MEDICINE4_ID = 1133;
	private int COBS_MEDICINE5_ID = 1134;
	private int SWORD_OF_SENTINEL_ID = 743;
	private int STAFF_OF_SENTINEL_ID = 744;
	
	// Quest Npcs
	private final static int QUEST_NPC[] = { 30284, 30284, 30156, 30217, 30219, 30221, 30285 };

	public _102_FungusFever(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(QUEST_NPC[0]);
		for (int npc : QUEST_NPC)
			addTalkId(npc);

		questItemIds = new int[] { ALBERRYUS_LETTER_ID, EVERGREEN_AMULET_ID, DRYAD_TEARS_ID, ALBERRYUS_LIST_ID, COBS_MEDICINE1_ID, COBS_MEDICINE2_ID, COBS_MEDICINE3_ID, 
				COBS_MEDICINE4_ID, COBS_MEDICINE5_ID, SWORD_OF_SENTINEL_ID, STAFF_OF_SENTINEL_ID };
	}

	public String onAdvEvent(String event, QuestState qs)
	{		
		if (qs == null)
			return null;

		String html = event;
		if (event.equalsIgnoreCase("1"))
		{
			html = "30284-02.htm";
			qs.giveItems(ALBERRYUS_LETTER_ID, 1);
			qs.set("cond", "1");
			qs.setState(State.STARTED);
			qs.playSound("ItemSound.quest_accept");
		}

		return html;

	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String html = getNoQuestMsg(player);
		QuestState qs = player.getQuestState(getName());
		if (qs == null)
			return html;
		
		final int cond = qs.getInt("cond");
		
		switch(qs.getState())
		{
			case State.COMPLETED :
				html = getAlreadyCompletedMsg(player);
				break;
			case State.CREATED :
				if (player.getRace().ordinal() != 1)
				{
					html = "30284-00.htm";
					qs.exitQuest(true);
				}
				else if (player.getLevel() >= 12)
				{
					html = "30284-07.htm";
				}
				else
					html = "30284-08.htm";
				break;
			case State.STARTED :
				switch (npc.getNpcId())
				{
					case 30156:
						switch (cond)
						{
							case 1:
								if (qs.getQuestItemsCount(ALBERRYUS_LETTER_ID) == 1)
								{
									qs.giveItems(EVERGREEN_AMULET_ID, 1);
									qs.takeItems(ALBERRYUS_LETTER_ID, 1);
									qs.set("cond", "2");
									qs.playSound("ItemSound.quest_middle");
									html = "30156-03.htm";
								}
								break;
							case 2:
								if (qs.getQuestItemsCount(EVERGREEN_AMULET_ID) > 0 && qs.getQuestItemsCount(DRYAD_TEARS_ID) < 10)
									html = "30156-04.htm";
								break;
							case 3:
								if (qs.getQuestItemsCount(EVERGREEN_AMULET_ID) > 0 && qs.getQuestItemsCount(DRYAD_TEARS_ID) >= 10)
								{
									qs.takeItems(EVERGREEN_AMULET_ID, 1);
									qs.takeItems(DRYAD_TEARS_ID, - 1);
									qs.giveItems(COBS_MEDICINE1_ID, 1);
									qs.giveItems(COBS_MEDICINE2_ID, 1);
									qs.giveItems(COBS_MEDICINE3_ID, 1);
									qs.giveItems(COBS_MEDICINE4_ID, 1);
									qs.giveItems(COBS_MEDICINE5_ID, 1);
									qs.set("cond", "4");
									qs.playSound("ItemSound.quest_middle");
									html = "30156-05.htm";
								}
								break;
							case 4:
								if (qs.getQuestItemsCount(ALBERRYUS_LIST_ID) == 0 && 
										(qs.getQuestItemsCount(COBS_MEDICINE1_ID) == 1 || qs.getQuestItemsCount(COBS_MEDICINE2_ID) == 1 
												|| qs.getQuestItemsCount(COBS_MEDICINE3_ID) == 1 || qs.getQuestItemsCount(COBS_MEDICINE4_ID) == 1 
												|| qs.getQuestItemsCount(COBS_MEDICINE5_ID) == 1))
									html = "30156-06.htm";
								break;
							case 5:
								if (qs.getQuestItemsCount(ALBERRYUS_LIST_ID) > 0)
									html = "30156-07.htm";
								break;
						}
						break;
					case 30284:
						switch (cond)
						{
							case 1:
								if (qs.getQuestItemsCount(ALBERRYUS_LETTER_ID) == 1)
									html = "30284-03.htm";
								else if (qs.getQuestItemsCount(EVERGREEN_AMULET_ID) == 1)
									html = "30284-09.htm";
								break;
							case 4:
								if (qs.getQuestItemsCount(ALBERRYUS_LIST_ID) == 0 && qs.getQuestItemsCount(COBS_MEDICINE1_ID) == 1)
								{
									qs.takeItems(COBS_MEDICINE1_ID, 1);
									qs.giveItems(ALBERRYUS_LIST_ID, 1);
									qs.set("cond", "5");
									qs.playSound("ItemSound.quest_middle");
									html = "30284-04.htm";
								}
								break;
							case 5:
								if (qs.getQuestItemsCount(ALBERRYUS_LIST_ID) == 1 && 
										(qs.getQuestItemsCount(COBS_MEDICINE1_ID) == 1 || qs.getQuestItemsCount(COBS_MEDICINE2_ID) == 1 
												|| qs.getQuestItemsCount(COBS_MEDICINE3_ID) == 1 || qs.getQuestItemsCount(COBS_MEDICINE4_ID) == 1 
												|| qs.getQuestItemsCount(COBS_MEDICINE5_ID) == 1))
									html = "30284-05.htm";
								break;
							case 6:
								if (qs.getQuestItemsCount(ALBERRYUS_LIST_ID) == 1)
								{
									qs.takeItems(ALBERRYUS_LIST_ID, 1);
									qs.set("cond", "0");
									qs.exitQuest(false);
									qs.playSound("ItemSound.quest_finish");
									html = "30284-06.htm";
									qs.giveReward(57, 6331);
									if (player.getClassId().getId() > 18 && player.getClassId().getId() < 25)
									{
										qs.giveItems(SWORD_OF_SENTINEL_ID, 1);
										qs.giveReward(1835, 1000);
									}
									else
									{
										qs.giveItems(STAFF_OF_SENTINEL_ID, 1);
										qs.giveReward(2509, 500);
									}
									qs.giveReward(4412, 100);
									qs.giveReward(4413, 100);
									qs.giveReward(4414, 100);
									qs.giveReward(4415, 100);
									qs.giveReward(4416, 100);
									qs.giveReward(4417, 100);
									qs.giveReward(1060, 100);
									qs.addRewardExpAndSp(30202, 1339);
								}
								break;
						}
						break;
					case 30217:
						switch (cond)
						{
							case 5:
								if (qs.getQuestItemsCount(ALBERRYUS_LIST_ID) == 1 && qs.getQuestItemsCount(COBS_MEDICINE2_ID) == 1)
								{
									qs.takeItems(COBS_MEDICINE2_ID, 1);
									html = "30217-01.htm";
								}
								break;
						}
						break;
					case 30219:
						switch (cond)
						{
							case 5:
								if (qs.getQuestItemsCount(ALBERRYUS_LIST_ID) == 1 && qs.getQuestItemsCount(COBS_MEDICINE3_ID) == 1)
								{
									qs.takeItems(COBS_MEDICINE3_ID, 1);
									html = "30219-01.htm";
								}
								break;
						}
						break;
					case 30221:
						switch (cond)
						{
							case 5:
								if (qs.getQuestItemsCount(ALBERRYUS_LIST_ID) == 1 && qs.getQuestItemsCount(COBS_MEDICINE4_ID) == 1)
								{
									qs.takeItems(COBS_MEDICINE4_ID, 1);
									html = "30221-01.htm";
								}
								break;
						}
						break;
					case 30285:
						switch (cond)
						{
							case 5:
								if (qs.getQuestItemsCount(ALBERRYUS_LIST_ID) == 1 && qs.getQuestItemsCount(COBS_MEDICINE5_ID) == 1)
								{
									qs.takeItems(COBS_MEDICINE5_ID, 1);
									html = "30285-01.htm";
								}
								break;
						}
						break;
				}

				break;
		}
		
		return html;
	}

	public void onKill(QuestState qs, L2Npc npc, L2PcInstance player)
	{
		if (qs == null || qs.getState() != State.STARTED)
			return;

		final int npcId = npc.getNpcId();
		if (npcId <= 20013 && npcId >= 20019)
		{
			if (qs.getQuestItemsCount(EVERGREEN_AMULET_ID) > 0 && qs.getQuestItemsCount(DRYAD_TEARS_ID) < 10)
			{
				if (qs.getRandom(10) < 3)
					qs.giveItems(DRYAD_TEARS_ID, 1);
			}
		}
		
		if (qs.getQuestItemsCount(DRYAD_TEARS_ID) == 10)
		{
			qs.playSound("ItemSound.quest_middle");
			qs.set("cond", "3");
		}
		else
			qs.playSound("ItemSound.quest_itemget");
		
		return;
	}

	public static void main(String[] args)
	{
		new _102_FungusFever(102, "_102_FungusFever", "quests");
	}

}
