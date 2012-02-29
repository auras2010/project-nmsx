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
package quests._147_PathtoBecomingAnEliteMercenary;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 ** @author Gnacik, Synerge
 **
 ** 2010-09-30 Based on official server Franz
 */
public class _147_PathtoBecomingAnEliteMercenary extends Quest
{
	// NPCs
	private static final int[] MERCENARY = { 36481, 36482, 36483, 36484, 36485, 36486, 36487, 36488, 36489 };
	// Items
	private static final int CERT_ORDINARY = 13766;
	private static final int CERT_ELITE = 13767;
	
	public _147_PathtoBecomingAnEliteMercenary(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		for (int npcs : MERCENARY)
		{
			addStartNpc(npcs);
			addTalkId(npcs);
		}
		
		questItemIds = new int[] { CERT_ORDINARY };
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null) 
			return event;
		
		if (!contains(MERCENARY, npc.getNpcId()))
			return event;
		
		if (event.equalsIgnoreCase("elite-02.htm"))
		{
			if (st.hasQuestItems(CERT_ORDINARY))
				return "elite-02a.htm";
			
			st.giveItems(CERT_ORDINARY, 1);
		}
		else if (event.equalsIgnoreCase("elite-04.htm"))
		{
			st.setState(State.STARTED);
			st.set("cond", "1");
			st.playSound("ItemSound.quest_accept");
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
		
		if (!contains(MERCENARY, npc.getNpcId()))
			return htmltext;
		
		switch(st.getState())
		{
			case State.COMPLETED :
				htmltext = getAlreadyCompletedMsg(player);
				break;
			case State.CREATED :
				if (player.getClan() != null && player.getClan().getHasCastle() > 0)
					htmltext = "castle.htm";
				else
					htmltext = "elite-01.htm";
				break;
			case State.STARTED :
				if (st.getInt("cond") < 4)
					htmltext = "elite-05.htm";
				else if (st.getInt("cond") == 4)
				{
					st.unset("cond");
					st.unset("kills");
					st.takeItems(CERT_ORDINARY, -1);
					st.giveItems(CERT_ELITE, 1);
					st.exitQuest(false);
					htmltext = "elite-06.htm";
				}
				break;
		}

		return htmltext;
	}
		
	public static void main(String[] args)
	{
		new _147_PathtoBecomingAnEliteMercenary(147, "_147_PathtoBecomingAnEliteMercenary", "Path to Becoming an Elite Mercenary");
	}
}
