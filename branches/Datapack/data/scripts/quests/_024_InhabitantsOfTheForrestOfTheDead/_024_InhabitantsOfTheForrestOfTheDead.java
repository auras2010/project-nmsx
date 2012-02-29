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

package quests._024_InhabitantsOfTheForrestOfTheDead;

import javolution.util.FastList;

import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;
import l2.universe.gameserver.network.serverpackets.NpcSay;


/**
 * 
 * @author moved to java by Sephiroth
 *
 */

public class _024_InhabitantsOfTheForrestOfTheDead extends Quest
{
	FastList<Integer> mobs = new FastList<Integer>();

	//Npcs
	int Dorian = 31389;
	int Wizard = 31522;
	int Tombstone = 31531;
	int MaidOfLidia = 31532;
	int DorianRaid = 25332;

	//Items
	int Letter = 7065;
	int Hairpin = 7148;
	int Totem = 7151;
	int Flower = 7152;
	int SilverCross = 7153;
	int BrokenSilverCross = 7154;
	int SuspiciousTotem = 7156;

	public _024_InhabitantsOfTheForrestOfTheDead(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(Dorian);
		addTalkId(Dorian);
		addTalkId(Tombstone);
		addTalkId(MaidOfLidia);
		addTalkId(Wizard);
		addAggroRangeEnterId(DorianRaid);
		AddMobs();

		for (int npcId : mobs)
			addKillId(npcId);
	}

	void AddMobs()
	{
		if (mobs.size() == 0);
		{
			mobs.add(21557);
			mobs.add(21558);
			mobs.add(21560);
			mobs.add(21563);
			mobs.add(21564);
			mobs.add(21565);
			mobs.add(21566);
			mobs.add(21567);
		}
	}
	void AutoChat(L2Npc npc, String text)
	{
		L2PcInstance[] chars = ((L2Character)npc).getKnownList().getKnownPlayers().values().toArray(new L2PcInstance[0]);
		if (chars.length != 0)
		{
			for (int iCharPos = 0; iCharPos < chars.length; iCharPos++)
			{
				L2PcInstance pc = chars[iCharPos];
				pc.sendPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), text));
			}
		}
	}	

	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		String htmlText = event;

		if (st == null)
			return htmlText;
		
		if (event.equals("31389-02.htm"))
		{
	            st.giveItems(Flower,1);
      	      st.set("cond","1");
            	st.playSound("ItemSound.quest_accept");
	            st.setState(State.STARTED);
		}
		else if (event.equals("31389-16.htm"))
			st.playSound("InterfaceSound.charstat_open_01");
		else if (event.equals("31389-17.htm"))
		{
	            st.takeItems(BrokenSilverCross,-1);
            	st.giveItems(Hairpin,1);
      	      st.set("cond","5");
		}
		else if (event.equals("31522-03.htm"))
            	st.takeItems(Totem,-1);
		else if (event.equals("31522-07.htm"))
	            st.set("cond","11");
		else if (event.equals("31522-19.htm"))
		{
	            st.giveItems(SuspiciousTotem,1);
      	      st.addExpAndSp(242105,22529);
            	st.exitQuest(false);
	            st.playSound("ItemSound.quest_finish");
		}
		else if (event.equals("31531-02.htm"))
		{
	            st.playSound("ItemSound.quest_middle");
      	      st.set("cond","2");
            	st.takeItems(Flower,-1);
		}
		else if (event.equals("31532-04.htm"))
		{
	            st.playSound("ItemSound.quest_middle");
      	      st.giveItems(Letter,1);
            	st.set("cond","6");
		}
		else if (event.equals("31532-06.htm"))
		{
	            st.takeItems(Hairpin,-1);
      	      st.takeItems(Letter,-1);
		}
		else if (event.equals("31532-16.htm"))
		{
	            st.playSound("ItemSound.quest_middle");
      	      st.set("cond","9");
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
		byte state = st.getState();
		int cond = st.getInt("cond");

		if (state == State.COMPLETED)
		{
			if (npcId == Wizard)
				htmlText = "31522-20.htm";
			else
				htmlText = Quest.getAlreadyCompletedMsg(player);
		}
		
		if (npcId == Dorian)
		{
			if (state == State.CREATED)
			{
				QuestState st2 = player.getQuestState("_023_LidiasHeart");
				if (st2 != null)
				{
					if (st2.getState() == State.COMPLETED && player.getLevel() >= 65)
						htmlText = "31389-01.htm";
					else
						htmlText = "31389-00.htm";
				}
				else
					htmlText = "31389-00.htm";
			}
			else if (cond == 1)
      	          htmlText = "31389-03.htm";
            	else if (cond == 2)
	                htmlText = "31389-04.htm";
      	      else if (cond == 3)
            	    htmlText = "31389-12.htm";
	            else if (cond == 4)
      	          htmlText = "31389-13.htm";
            	else if (cond == 5)
	                htmlText = "31389-18.htm";
		}
		else if (npcId == Tombstone)
		{
			if (cond == 1)
			{
                		st.playSound("AmdSound.d_wind_loot_02");
                		htmlText = "31531-01.htm";
			}
			else if (cond == 2)
				htmlText = "31531-03.htm";
		}
		else if (npcId == MaidOfLidia)
		{
			if (cond == 5)
				htmlText = "31532-01.htm";
			else if (cond == 6)
			{
				if (st.getQuestItemsCount(Letter) >= 1 && st.getQuestItemsCount(Hairpin) >= 1)
					htmlText = "31532-05.htm";
				else
					htmlText = "31532-07.htm";
			}
			else if (cond == 9)
				htmlText = "31532-16.htm";
		}
		else if (npcId == Wizard)
		{
			if (cond == 10)
				htmlText = "31522-01.htm";
			else if (cond == 11)
				htmlText = "31522-08.htm";
		}
		return htmlText;
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final QuestState st = player.getQuestState(getName());
		
		if (st == null)
			return null;

		if (st.getState() != State.STARTED)
			return null;

		int npcId = npc.getNpcId();

		if (st.getQuestItemsCount(Totem) == 0 && st.getInt("cond") == 9)
		{
			if (mobs.contains(npcId) && st.getRandom(100) <= 30)
			{
	                st.giveItems(Totem,1);
      	          st.set("cond","10");
            	    st.playSound("ItemSound.quest_middle");
			}
		}
		return null;
	}

	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (npc.getNpcId() == DorianRaid)
		{
			if (isPet)
				((L2Attackable)npc).getAggroList().remove(player.getPet());
			else
			{
				((L2Attackable)npc).getAggroList().remove(player);
				QuestState st = player.getQuestState(getName());
				if (st != null)
				{
					if (st.getQuestItemsCount(SilverCross) >= 1)
					{
						st.takeItems(SilverCross,-1);
						st.giveItems(BrokenSilverCross,1);
						st.set("cond","4");
						AutoChat(npc,"That sign!");
					}
				}
			}
		}
		return null;
	}

	public static void main(String[] args)
	{
		new _024_InhabitantsOfTheForrestOfTheDead(24, "_024_InhabitantsOfTheForrestOfTheDead", "Inhabitants Of The Forrest Of The Dead");
	}
}
