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
package quests._691_MatrasSuspiciousRequest;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 * 
 * @author Synerge
 */
public class _691_MatrasSuspiciousRequest extends Quest
{
	// NPCs
	private static final int MATRAS = 32245;
	
	// MOBs
	private static final int[] MOBS = {	22363, 22364, 22365, 22366,	22367, 22368, 22370, 22371, 22372 };
	
	// ITEMs
	private static final int DYNASTIC_ESSENCE_II = 10413;
	private static final int RED_STONE = 10372;
	
	public _691_MatrasSuspiciousRequest(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(MATRAS);
		addTalkId(MATRAS);
		
		for (final int mob : MOBS)
			addKillId(mob);
		
		questItemIds = new int[] { RED_STONE };
	}
		
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = player.getQuestState(getName());
		if (qs == null)
			return event;

		if (event.equalsIgnoreCase("32245-03.htm"))
		{
			qs.set("cond", "1");
			qs.setState(State.STARTED);
			qs.playSound("ItemSound.quest_accept");
		}
		else if (event.equalsIgnoreCase("32245-05.htm"))
		{
			qs.playSound("ItemSound.quest_finish");
			qs.takeItems(RED_STONE, 744);
			qs.giveItems(DYNASTIC_ESSENCE_II, 1);
			qs.exitQuest(true);
		}
		
		return event;
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
			case State.COMPLETED:
				htmltext = msgQuestCompleted();
				break;
			case State.CREATED:
				if (st.getPlayer().getLevel() >= 76)
					htmltext = "32245-01.htm";
				else
				{
					st.exitQuest(true);
				}
				break;
			case State.STARTED:
				if (npc.getNpcId() == MATRAS)
				{
					switch (st.getInt("cond"))
					{
						case 1:
							htmltext = "32245-03.htm";
							break;							
						case 2:
							htmltext = "32245-04.htm";
							break;							
						default:
							break;
					}
				}
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (!contains(MOBS, npc.getNpcId()))
			return null;
		
		final L2PcInstance partyMember = getRandomPartyMemberState(player, State.STARTED);
		if (partyMember == null)
			return null;
		
		final QuestState st = partyMember.getQuestState(getName());
		if (st == null || st.getInt("cond") != 1)
			return null;
		
		if (st.dropQuestItems(RED_STONE, 1, 744, 30, true))
		{
			st.playSound("ItemSound.quest_middle");
			st.set("cond", "2");
		}
		
		return super.onKill(npc, player, isPet);
	}	
	
	public static void main(String[] args)
	{
		new _691_MatrasSuspiciousRequest(691, "_691_MatrasSuspiciousRequest", "Matras Suspicious Request!");
	}
}
