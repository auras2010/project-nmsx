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
package teleports.KamalokaEscape;

import l2.universe.gameserver.instancemanager.InstanceManager;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.Instance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;

/**
 * 
 * @author angkor
 */
public class KamalokaEscape extends Quest
{
	private final static int NPC = 32496;

	public KamalokaEscape(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(NPC);
		addTalkId(NPC);
	}

	private boolean checkPrimaryConditions(L2PcInstance player)
	{
		if (player.getParty() == null)
			return false;
		if (!player.getParty().isLeader(player))
			return false;

		return true;
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return "";
				
		if (npc.getNpcId() != NPC)
			return "";

		String htmltext = "";

		if (checkPrimaryConditions(player))
		{
			final Instance instance = InstanceManager.getInstance().getInstance(player.getInstanceId());
			if (instance != null)
				instance.removePlayers();
		}
		else
			htmltext = "32496-1.htm";

		st.exitQuest(true);
		return htmltext;
	}

	public static void main(String[] args)
	{
		new KamalokaEscape(-1, "KamalokaEscape", "teleports");
	}
}
