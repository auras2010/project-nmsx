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

package custom.ShadowWeapons;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;

/**
 * 
 * @author ButterCup, Synerge
 */
public class ShadowWeapons extends Quest
{
    private static final String qn = "ShadowWeapons";

    private static final int[] NPC = 
    {
            30037, 30066, 30070, 30109, 30115, 30120, 30174, 30175, 30176, 30187, 30191, 30195,
            30288, 30289, 30290, 30297, 30373, 30462, 30474, 30498, 30499, 30500, 30503, 30504,
            30505, 30511, 30512, 30513, 30595, 30676, 30677, 30681, 30685, 30687, 30689, 30694,
            30699, 30704, 30845, 30847, 30849, 30854, 30857, 30862, 30865, 30894, 30897, 30900,
            30905, 30910, 30913, 31269, 31272, 31288, 31314, 31317, 31321, 31324, 31326, 31328,
            31331, 31334, 31336, 31965, 31974, 31276, 31285, 31958, 31961, 31996, 31968, 31977,
            32092, 32093, 32094, 32095, 32096, 32097, 32098, 32193, 32196, 32199, 32202, 32205,
            32206, 32213, 32214, 32221, 32222, 32229, 32230, 32233, 32234
    };
    
    // ItemId for shadow weapon coupons, it's not used more than once but increases readability
    private static final int D_COUPON = 8869;
    private static final int C_COUPON = 8870;

    public ShadowWeapons(int id, String name, String descr)
    {
        super(id, name, descr);

        for (int npcId : NPC)
        {
            addStartNpc(npcId);
            addTalkId(npcId);
        }
    }

    @Override
	public String onTalk(L2Npc npc, L2PcInstance player)
    {
		String htmltext = getNoQuestMsg(player);
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
        if  (st.getQuestItemsCount(D_COUPON) != 0)
        {
            final int multisell = 306893001;
            htmltext = st.showHtmlFile("exchange.htm").replace("%msid%", "" + multisell);
        }
        else if  (st.getQuestItemsCount(C_COUPON) != 0)
        {
            final int multisell = 306893002;
            htmltext = st.showHtmlFile("exchange.htm").replace("%msid%", "" + multisell);
        }
        else
            htmltext = "exchange-no.htm";
		
        st.exitQuest(true);
        return htmltext;
    }

    public static void main(String[] args)
    {
        new ShadowWeapons(-1, qn, "custom");
    }
}
