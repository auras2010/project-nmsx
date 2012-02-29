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
package quests._011_SecretMeetingWithKetraOrcs;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

/**
 * 
 * @author Synerge
 */
public class _011_SecretMeetingWithKetraOrcs extends Quest
{
	private static final int CADMON = 31296;
	private static final int LEON = 31256;
	private static final int WAHKAN = 31371;

	private static final int MUNITIONS_BOX = 7231;

	public _011_SecretMeetingWithKetraOrcs(int questId, String name, String descr)
	{
		super(questId, name, descr);

		addStartNpc(CADMON);
		addTalkId(CADMON);
		addTalkId(LEON);
		addTalkId(WAHKAN);
		questItemIds = new int[] {MUNITIONS_BOX};
	}

	@Override
	public String onEvent(String event, QuestState st)
	{
		if (st == null)
			return null;
		
		if(event.equalsIgnoreCase("31296-03.htm"))
		{
			st.set("cond", "1");
			st.setState(State.STARTED);
			st.playSound("ItemSound.quest_accept");
		}
		else if(event.equalsIgnoreCase("31256-02.htm"))
		{
			st.giveItems(MUNITIONS_BOX, 1);
			st.set("cond", "2");
			st.playSound("ItemSound.quest_middle");
		}
		else if(event.equalsIgnoreCase("31371-02.htm"))
		{
			st.takeItems(MUNITIONS_BOX, 1);
			st.addRewardExpAndSp(82045, 6047);
			st.playSound("ItemSound.quest_finish");
			st.unset("cond");
			st.setState(State.COMPLETED);
			st.exitQuest(false);
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
				if (npcId == CADMON)
				{
					if (qs.getPlayer().getLevel() >= 74)
						html = "31296-01.htm";
					else
					{
						html = "31296-02.htm";
						qs.exitQuest(true);
					}
				}
				break;
			case State.STARTED:
				switch (npcId)
				{
					case CADMON:
						switch (cond)
						{
							case 1:
								html = "31296-04.htm";
								break;
						}
						break;
					case LEON:
						switch (cond)
						{
							case 1:
								html = "31256-01.htm";
								break;
							case 2:
								html = "31256-03.htm";
								break;							
						}
						break;
					case WAHKAN:
						switch (cond)
						{
							case 2:
								if (qs.getQuestItemsCount(MUNITIONS_BOX) > 0)
									html = "31371-01.htm";
								break;
						}
						break;
				}
				break;
		}
		return html;
	}

	public static void main(String[] args)
	{
		new _011_SecretMeetingWithKetraOrcs(11, "_011_SecretMeetingWithKetraOrcs", "Secret Meeting With Ketra Orcs");    	
	}
}