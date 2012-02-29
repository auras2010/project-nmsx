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

/*
 * By Sephiroth - 2011, May 6
 *
 */

package quests._025_HidingBehindTheTruth;

import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;
import l2.universe.gameserver.network.serverpackets.NpcSay;

public class _025_HidingBehindTheTruth extends Quest
{
	//Npcs
	int Agripel = 31348;
	int Benedict = 31349;
	int Wizard = 31522;
	int Tombstone = 31531;
	int Lidia = 31532;
	int Bookshelf = 31533;
	int Bookshelf2 = 31534;
	int Bookshelf3 = 31535;
	int Coffin = 31536;
	int Triol = 27218;

	//Items
	int Contract = 7066;
	int Dress = 7155;
	int SuspiciousTotem = 7156;
	int GemstoneKey = 7157;
	int TotemDoll = 7158;

	public _025_HidingBehindTheTruth(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(Benedict);
		addTalkId(Agripel);
		addTalkId(Benedict);
		addTalkId(Bookshelf);
		addTalkId(Bookshelf2);
		addTalkId(Bookshelf3);
		addTalkId(Wizard);
		addTalkId(Lidia);
		addTalkId(Tombstone);
		addTalkId(Coffin);
		addKillId(Triol);
	}

	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		String htmlText = event;

		if (st == null)
			return htmlText;

		if (event.equals("31349-02.htm"))
		{
			st.playSound("ItemSound.quest_accept");
			st.set("cond","1");
			st.setState(State.STARTED);
		}
		else if (event.equals("31349-03.htm"))
		{
			if (st.getQuestItemsCount(SuspiciousTotem) >= 1)
				htmlText = "31349-05.htm";
			else
			{
				st.playSound("ItemSound.quest_middle");
				st.set("cond","2");
			}
		}
		else if (event.equals("31349-10.htm"))
		{
			st.playSound("ItemSound.quest_middle");
			st.set("cond","4");
		}
		else if (event.equals("31348-02.htm"))
			st.takeItems(SuspiciousTotem,-1);
		else if (event.equals("31348-07.htm"))
		{
			st.playSound("ItemSound.quest_middle");
			st.set("cond","5");
			st.giveItems(GemstoneKey,1);
		}
		else if (event.equals("31522-04.htm"))
		{
			st.playSound("ItemSound.quest_middle");
			st.set("cond","6");
		}
		else if (event.equals("31535-03.htm"))
		{
			if (st.getInt("step") == 0)
			{
				st.set("step","1");
				L2Npc triol = st.addSpawn(27218,59712,-47568,-2712,0,false,300000,true);
				triol.broadcastPacket(new NpcSay(triol.getObjectId(), 0, triol.getNpcId(), "That box was sealed by my master. Don't touch it!"));
				triol.setRunning();
				((L2Attackable)triol).addDamageHate(player,0,999);
				triol.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
				st.playSound("ItemSound.quest_middle");
				st.set("cond","7");
			}
			else if (st.getInt("step") == 2)
				htmlText = "31535-04.htm";
		}
		else if (event.equals("31535-05.htm"))
		{
			st.giveItems(Contract,1);
			st.takeItems(GemstoneKey,-1);
			st.playSound("ItemSound.quest_middle");
			st.set("cond","9");
		}
		else if (event.equals("31532-02.htm"))
			st.takeItems(Contract,-1);
		else if (event.equals("31532-06.htm"))
		{
			st.playSound("ItemSound.quest_middle");
			st.set("cond","11");
		}
		else if (event.equals("31531-02.htm"))
		{
			st.playSound("ItemSound.quest_middle");
			st.set("cond","12");
			st.addSpawn(Coffin,60104,-35820,-664,0,false,20000,true);
		}
		else if (event.equals("31532-18.htm"))
		{
			st.playSound("ItemSound.quest_middle");
			st.set("cond","15");
		}
		else if (event.equals("31522-12.htm"))
		{
			st.playSound("ItemSound.quest_middle");
			st.set("cond","16");
		}
		else if (event.equals("31348-10.htm"))
			st.takeItems(TotemDoll,-1);
		else if (event.equals("31348-15.htm"))
		{
			st.playSound("ItemSound.quest_middle");
			st.set("cond","17");
		}
		else if (event.equals("31348-16.htm"))
		{
			st.playSound("ItemSound.quest_middle");
			st.set("cond","18");
		}
		else if (event.equals("31532-20.htm"))
		{
			st.giveItems(905,2);
			st.giveItems(874,1);
			st.takeItems(7063,-1);
			st.addExpAndSp(572277,53750);
			st.unset("cond");
			st.exitQuest(false);
			st.playSound("ItemSound.quest_finish");
		}
		else if (event.equals("31522-15.htm"))
		{
			st.giveItems(936,1);
			st.giveItems(874,1);
			st.takeItems(7063,-1);
			st.addExpAndSp(572277,53750);
			st.unset("cond");
			st.exitQuest(false);
			st.playSound("ItemSound.quest_finish");
		}
		return htmlText;
	}
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		String htmlText = Quest.getNoQuestMsg(player);

		if (st == null)
			return htmlText;
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		byte id = st.getState();

		if (id == State.COMPLETED)
			htmlText = Quest.getAlreadyCompletedMsg(player);
		else if (id == State.CREATED)
		{
			if (npcId == Benedict)
			{
				QuestState st2 = player.getQuestState("_024_InhabitantsOfTheForrestOfTheDead");
				if (st2 != null) 
					if (st2.getState() == State.COMPLETED && player.getLevel() >= 66)
						htmlText = "31349-01.htm";
					else
						htmlText = "31349-00.htm";
				else
					htmlText = "31349-00.htm";
			}
		}
		else if (id == State.STARTED)
		{
			if (npcId == Benedict)
			{
				if (cond == 1)
					htmlText = "31349-02.htm";
				else if (cond == 2 || cond == 3)
					htmlText = "31349-04.htm";
				else if (cond == 4)
					htmlText = "31349-10.htm";
			}
			else if (npcId == Wizard)
			{
				if (cond == 2)
				{
					htmlText = "31522-01.htm";
					st.playSound("ItemSound.quest_middle");
					st.set("cond","3");
					st.giveItems(SuspiciousTotem,1);
				}
				else if (cond == 3)
					htmlText = "31522-02.htm";
				else if (cond == 5)
					htmlText = "31522-03.htm";
				else if (cond == 6)
					htmlText = "31522-04.htm";
				else if (cond == 9)
				{
					htmlText = "31522-05.htm";
					st.playSound("ItemSound.quest_middle");
					st.set("cond","10");
				}
				else if (cond == 10)
					htmlText = "31522-05.htm";
				else if (cond == 15)
					htmlText = "31522-06.htm";
				else if (cond == 16)
					htmlText = "31522-13.htm";
				else if (cond == 17)
					htmlText = "31522-16.htm";
				else if (cond == 18)
					htmlText = "31522-14.htm";
			}
			else if (npcId == Agripel)
			{
				if (cond == 4)
					htmlText = "31348-01.htm";
				else if (cond == 5)
					htmlText = "31348-08.htm";
				else if (cond == 16)
					htmlText = "31348-09.htm";
				else if (cond == 17)
					htmlText = "31348-17.htm";
				else if (cond == 18)
					htmlText = "31348-18.htm";
			}
			else if (npcId == Bookshelf)
				if (cond == 6)
					htmlText = "31533-01.htm";
			else if (npcId == Bookshelf2)
				if (cond == 6)
					htmlText = "31534-01.htm";
			else if (npcId == Bookshelf3)
			{
				if (cond >= 6 && cond <= 8)
					htmlText = "31535-01.htm";
				else if (cond == 9)
					htmlText = "31535-06.htm";
			}
			else if (npcId == Lidia)
			{
				if (cond == 10)
					htmlText = "31532-01.htm";
				else if (cond == 11 || cond == 12)
					htmlText = "31532-06.htm";
				else if (cond == 13)
				{
					htmlText = "31532-07.htm";
					st.set("cond","14");
					st.takeItems(Dress,-1);
				}
				else if (cond == 14)
					htmlText = "31532-08.htm";
				else if (cond == 15)
					htmlText = "31532-18.htm";
				else if (cond == 17)
					htmlText = "31532-19.htm";
				else if (cond == 18)
					htmlText = "31532-21.htm";
			}
			else if (npcId == Tombstone)
				if (cond == 11 || cond == 12)
					htmlText = "31531-01.htm";
				else if (cond == 13)
					htmlText = "31531-03.htm";
			else if (npcId == Coffin)
			{
				if (cond == 12)
				{
					htmlText = "31536-01.htm";
					st.giveItems(Dress,1);
					st.playSound("ItemSound.quest_middle");
					st.set("cond","13");
					npc.deleteMe();
				}
			}
		}
		return htmlText;
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
			return "";
		if (st.getState() != State.STARTED)
			return "";
		if (st.getInt("cond") == 7)
		{
			st.playSound("ItemSound.quest_itemget");
			st.set("cond","8");
			npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), "You've ended my immortal life! You've protected by the feudal lord, aren't you?"));
			st.giveItems(TotemDoll,1);
			st.set("step","2");
		}
		return "";
	}

	public static void main(String[] args)
	{
		new _025_HidingBehindTheTruth(25, "_025_HidingBehindTheTruth", "Hiding Behind the Truth");
	}

}