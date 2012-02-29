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
package teleports.ElrokiTeleporters;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;

public class ElrokiTeleporters extends Quest
{
	private static final String qn = "ElrokiTeleporters";

	private final static int NPC1 = 32111;
	private final static int NPC2 = 32112;

	public ElrokiTeleporters(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(NPC1);
		addStartNpc(NPC2);
		addTalkId(NPC1);
		addTalkId(NPC2);
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		QuestState st = player.getQuestState(getName());
		int npcId = npc.getNpcId();
		if (npcId == NPC1)
		{
			if (player.isInCombat())
				htmltext = "32111-no.htm";
			else
				player.teleToLocation(4990, -1879, -3178);
		}
		else if (npcId == NPC2)
			player.teleToLocation(7557, -5513, -3221);

		st.exitQuest(true);
		return htmltext;
	}

	public static void main(String[] args)
	{
		new ElrokiTeleporters(-1, qn, "teleports");
	}
}