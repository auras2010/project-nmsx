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
package quests._249_PoisonedPlainsOfTheLizardmen;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 ** @author Gnacik, Synerge
 **
 ** 2010-08-04 Based on Freya PTS
 */
public class _249_PoisonedPlainsOfTheLizardmen extends Quest
{
	private static final int MOUEN = 30196;
	private static final int JHONNY = 32744;
	
	public _249_PoisonedPlainsOfTheLizardmen(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(MOUEN);
		addTalkId(MOUEN);
		addTalkId(JHONNY);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null) 
			return event;
		
		switch (npc.getNpcId())
		{
			case MOUEN:
				if (event.equalsIgnoreCase("30196-03.htm"))
				{
					st.setState(State.STARTED);
					st.set("cond", "1");
					st.playSound("ItemSound.quest_accept");
				}
				break;
			case JHONNY:
				if (event.equalsIgnoreCase("32744-03.htm"))
				{
					st.unset("cond");
					st.giveItems(57, 83056);
					st.addExpAndSp(477496, 58743);
					st.playSound("ItemSound.quest_finish");
					st.exitQuest(false);
				}
				break;
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
		
		switch(st.getState())
		{
			case State.COMPLETED :
				switch (npc.getNpcId())
				{
					case MOUEN:
						htmltext = "30196-05.htm";
						break;
					case JHONNY:
						htmltext = "32744-04.htm";
						break;
				}
				break;
			case State.CREATED :
				if (player.getLevel() >= 82)
					htmltext = "30196-01.htm";
				else
				{
					htmltext = "30196-00.htm";
					st.exitQuest(true);
				}
				break;
			case State.STARTED :
				switch (npc.getNpcId())
				{
					case MOUEN:
						if (st.getInt("cond") == 1)
							htmltext = "30196-04.htm";
						break;
					case JHONNY:
						if (st.getInt("cond") == 1)
							htmltext = "32744-01.htm";
						break;
				}
				break;
		}

		return htmltext;
	}
		
	public static void main(String[] args)
	{
		new _249_PoisonedPlainsOfTheLizardmen(249, "_249_PoisonedPlainsOfTheLizardmen", "Poisoned Plains of the Lizardmen");
	}
}
