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

package custom.Echo;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.util.Util;

/**
 * 
 * @author ButterCup, Synerge
 */
public class Echo extends Quest
{
    private static final String qn = "Echo";
    
    private static final int[] NPCS = {31042, 31043};
    private static final int ADENA = 57;
    private static final int COST = 200;
    
    private static final String[][] LIST = 
    {
        {"4410", "4411", "01", "02", "03"},
        {"4409", "4412", "04", "05", "06"},
        {"4408", "4413", "07", "08", "09"},
        {"4420", "4414", "10", "11", "12"},
        {"4421", "4415", "13", "14", "15"},
        {"4419", "4417", "16", "05", "06"},
        {"4418", "4416", "17", "05", "06"}
    };

    public Echo(int id, String name, String descr)
    {
        super(id, name, descr);

        for (int i : NPCS)
        {
            addStartNpc(i);
            addTalkId(i);
        }
    }

    @Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
    {
		String htmltext = getNoQuestMsg(player);
		QuestState st = player.getQuestState(getName());
		if (st == null || !Util.isDigit(event))
			return htmltext;
        
		final int npcId = npc.getNpcId();
        final int score = Integer.parseInt(event);
        for (String[] val : LIST)
        {
            if (score != Integer.parseInt(val[0]))
            	continue;

            if (st.getQuestItemsCount(score) == 0)
                htmltext = npcId + "-" + val[4] + ".htm";
            else if (st.getQuestItemsCount(ADENA) < COST)
                htmltext = npcId + "-" + val[3] + ".htm";
            else
            {
                st.takeItems(ADENA, COST);
                st.giveItems(Integer.parseInt(val[1]), 1);
                htmltext = npcId + "-" + val[2] + ".htm";
            }
            break;
        }
        st.exitQuest(true);
        return htmltext;
    }

    @Override
	public String onTalk(L2Npc npc, L2PcInstance player)
    {
        return "1.htm";
    }

    public static void main(String[] args)
    {
        new Echo(-1, qn, "custom");
    }
}
