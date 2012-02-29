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
package quests._463_IMustBeAGenius;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;
import l2.universe.util.Rnd;

/**
 ** @author Gnacik, Synerge
 **
 ** 2010-08-19 Based on Freya PTS
 */
public class _463_IMustBeAGenius extends Quest
{
	private static final int GUTENHAGEN = 32069;
	private static final int CORPSE_LOG = 15510;
	private static final int COLLECTION = 15511;
	private static final int[] MOBS = { 22801, 22802, 22804, 22805, 22807, 22808, 22809, 22810, 22811, 22812};
	
	public _463_IMustBeAGenius(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(GUTENHAGEN);
		addTalkId(GUTENHAGEN);
		for(int _mob : MOBS)
			addKillId(_mob);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null) 
			return event;
		
		if (npc.getNpcId() != GUTENHAGEN)
			return event;
		
		if (event.equalsIgnoreCase("32069-03.htm"))
		{
			st.playSound("ItemSound.quest_accept");
			st.setState(State.STARTED);
			st.set("cond", "1");
			
			// Generate random daily number for player
			int _number = Rnd.get(500, 600);
			st.set("number", String.valueOf(_number));
			
			// Set drop for mobs
			for(int _mob : MOBS)
			{
				int rand = Rnd.get(-2, 4);
				if (rand == 0)
					rand = 5;
				st.set(String.valueOf(_mob), String.valueOf(rand));
			}
			
			// One with higher chance
			st.set(String.valueOf(MOBS[Rnd.get(0, MOBS.length-1)]), String.valueOf(Rnd.get(1, 100)));
			event = getHtm(st.getPlayer().getHtmlPrefix(), "32069-03.htm");
			event = event.replace("%num%", String.valueOf(_number));
		}
		else if (event.equalsIgnoreCase("32069-05.htm"))
		{
			event = getHtm(st.getPlayer().getHtmlPrefix(), "32069-05.htm");
			event = event.replace("%num%", st.get("number"));
		}
		else if (event.equalsIgnoreCase("32069-07.htm"))
		{
			st.addExpAndSp(317961, 25427);
			st.unset("cond");
			st.unset("number");
			for(int _mob : MOBS)
				st.unset(String.valueOf(_mob));
			st.takeItems(COLLECTION, -1);
			st.playSound("ItemSound.quest_finish");
			st.exitQuest(false);
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
		
		if (npc.getNpcId() != GUTENHAGEN)
			return htmltext;

		switch(st.getState())
		{
			case State.COMPLETED :
				htmltext = "32069-08.htm";
				break;
			case State.CREATED :
				if (player.getLevel() >= 70)
					htmltext = "32069-01.htm";
				else
				{
					htmltext = "32069-00.htm";
					st.exitQuest(true);
				}
				break;
			case State.STARTED :
				switch (st.getInt("cond"))
				{
					case 1:
						htmltext = "32069-04.htm";
						break;
					case 2:
						htmltext = "32069-06.htm";
						break;
				}
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		QuestState st = player.getQuestState(getName());
		if (st == null || st.getState() != State.STARTED)
			return null;
		
		if (!contains(MOBS, npc.getNpcId()))
			return null;

		final int dayNumber = st.getInt("number");
		final int number = st.getInt(String.valueOf(npc.getNpcId()));
		
		if (number > 0)
		{
			st.giveItems(CORPSE_LOG, number);
			st.playSound("ItemSound.quest_itemget");
			npc.broadcastNpcSay("Att... attack... " + player.getName() + "... Ro... rogue... " + number + "..");
		}
		else if (number < 0 && ((st.getQuestItemsCount(CORPSE_LOG) + number) > 0))
		{
			st.takeItems(CORPSE_LOG, Math.abs(number));
			st.playSound("ItemSound.quest_itemget");
			npc.broadcastNpcSay("Att... attack... " + player.getName() + "... Ro... rogue... " + number + "..");
		}
		
		if (st.getQuestItemsCount(CORPSE_LOG) == dayNumber)
		{
			st.takeItems(CORPSE_LOG, -1);
			st.giveItems(COLLECTION, 1);
			st.set("cond", "2");
		}
			
		return super.onKill(npc, player, isPet);
	}
		
	public static void main(String[] args)
	{
		new _463_IMustBeAGenius(463, "_463_IMustBeAGenius", "I Must Be a Genius");
	}
}
