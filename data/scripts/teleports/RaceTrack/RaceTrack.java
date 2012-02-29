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
package teleports.RaceTrack;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.State;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;

public class RaceTrack extends Quest
{
	private static final String qn = "RaceTrack";

	private final static int RACE_MANAGER = 30995;
	private final static int[] TELEPORT_NPCs =
	{
		30320,30256,30059,30080,30899,30177,
		30848,30233,31320,31275,31964,31210
	};

	private final static int[][] RETURN_LOCS =
	{
		{-80884, 149770, -3040},
		{-12682, 122862, -3112},
		{15744, 142928, -2696},
		{83475, 147966, -3400},
		{111409, 219364, -3545},
		{82971, 53207, -1488},
		{146705, 25840, -2008},
		{116819, 76994, -2714},
		{43835, -47749, -792},
		{147930, -55281, -2728},
		{87386, -143246, -1293},
		{12882, 181053, -3560}
	};

	public RaceTrack(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addTalkId(RACE_MANAGER);
		addStartNpc(RACE_MANAGER);
		for (int id : TELEPORT_NPCs)
		{
			addStartNpc(id);
			addTalkId(id);
		}
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		int npcId = npc.getNpcId();
		int i = containsN(TELEPORT_NPCs, npcId);
		if (i > -1)
		{
			player.teleToLocation(12661, 181687, -3560);
			st.setState(State.STARTED);
			st.set("id", Integer.toString(i));
		}
		else
		{
			int return_id = st.getInt("id");
			if (return_id >= TELEPORT_NPCs.length) return_id = TELEPORT_NPCs.length-1;
			player.teleToLocation(RETURN_LOCS[return_id][0], RETURN_LOCS[return_id][1], RETURN_LOCS[return_id][2]);
		}
		return null;
	}

	private static int containsN(int[] array, int obj)
	{
		for (int i = 0; i < array.length; i++)
		{
			if (array[i] == obj)
				return i;
		}
		return -1;
	}

	public static void main(String[] args)
	{
		new RaceTrack(-1, qn, "teleports");
	}
}