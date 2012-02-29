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
package quests._009_IntoTheCityOfHumans;

import l2.universe.Config;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 * @author Evilus, Synerge
 */
public class _009_IntoTheCityOfHumans extends Quest
{
	// Petukai; Tanapi; Tamil
	private final static int QUEST_NPC[] = { 30583, 30571, 30576 };
	// SoE: Giran; Mark of Traveler
	private final static int QUEST_REWARD[] = { 7559, 7570 };

	public _009_IntoTheCityOfHumans(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(QUEST_NPC[0]);
		for (int npcId : QUEST_NPC)
			addTalkId(npcId);
	}

	@Override
	public String onEvent(String event, QuestState qs)
	{
		if (qs == null)
			return null;
		
		if (event.equalsIgnoreCase("30583-03.htm"))
		{
			qs.set("cond", "1");
			qs.setState(State.STARTED);
			qs.playSound("ItemSound.quest_accept");
		}
		else if (event.equalsIgnoreCase("30571-02.htm"))
		{
			qs.set("cond", "2");
			qs.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("30576-02.htm"))
		{
			qs.giveItems(QUEST_REWARD[0], (long) Config.RATE_QUEST_REWARD);
			qs.giveItems(QUEST_REWARD[1], 1);
			qs.unset("cond");
			qs.exitQuest(false);
			qs.playSound("ItemSound.quest_finish");
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
				if (npcId == QUEST_NPC[0])
				{
		            if (player.getRace().ordinal() == 3 && player.getLevel() >= 3)
		                html = "30583-02.htm";
		            else
		            {
		                html = "30583-01.htm";
		                qs.exitQuest(true);
		            }
				}
				break;
			case State.STARTED:
				switch (cond)
				{
					case 1:
						if (npcId == QUEST_NPC[0])
							html = "30583-04.htm";
						else if (npcId == QUEST_NPC[1])
							html = "30571-01.htm";
						break;
					case 2:
						if (npcId == QUEST_NPC[1])
							html = "30571-03.htm";
						else if (npcId == QUEST_NPC[2])
							html = "30576-01.htm";
						break;
				}
				break;
		}
		return html;
	}

	public static void main(String[] args)
	{
		new _009_IntoTheCityOfHumans(9, "_009_IntoTheCityOfHumans", "Into the City of Humans");
	}
}
