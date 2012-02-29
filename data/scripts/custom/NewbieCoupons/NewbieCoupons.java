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

package custom.NewbieCoupons;

import l2.universe.gameserver.datatables.MultiSell;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;

/**
 * 
 * @author ButterCup, Synerge
 */
public class NewbieCoupons extends Quest
{
    private static final String qn = "NewbieCoupons";
    private static final String qnTutorial = "999_T1Tutorial";
    
    private static final int COUPON_ONE = 7832;
    private static final int COUPON_TWO = 7833;

    private static final int[] NPCS = {30598, 30599, 30600, 30601, 30602, 30603, 31076, 31077, 32135};

    // Multisell
    private static final int WEAPON_MULTISELL = 305986001;
    private static final int ACCESORIES_MULTISELL = 305986002;

    // Newbie/one time rewards section
    // Any quest should rely on a unique bit, but
    // it could be shared among quests that were mutually
    // exclusive or race restricted.
    // Bit #1 isn't used for backwards compatibility.
    // This script uses 2 bits, one for newbie coupons and another for travelers
    // These 2 bits happen to be the same used by the Miss Queen script
    private static final int NEWBIE_WEAPON = 16;
    private static final int NEWBIE_ACCESORY = 32;

    public NewbieCoupons(int id, String name, String descr)
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
        QuestState st = player.getQuestState(qn);
        QuestState qs = player.getQuestState(qnTutorial);
        
        final int newbie = player.getNewbie();        
        final int level = player.getLevel();
        final int occupation_level = player.getClassId().level();
        final int pkkills = player.getPkKills();
        if (event.equals("newbie_give_weapon_coupon"))
        {
            // @TODO: check if this is the very first character for this account
            // would need a bit of SQL, or a core method to determine it.
            // This condition should be stored by the core in the account_data table
            // upon character creation.
            if (6 <= level && level <= 39 && pkkills == 0 && occupation_level == 0)
            {
                // Check the player state against this quest newbie rewarding mark.
                if (newbie != 0 | NEWBIE_WEAPON != newbie)
                {
                    player.setNewbie(newbie | NEWBIE_WEAPON);
                    st.giveItems(COUPON_ONE, 5);
                    if (qs != null) 
                    	qs.set("level6", "1");
                    return "30598-2.htm"; // here's the coupon you requested
                }
                else
                    return "30598-1.htm"; // you got a coupon already!
            }
            else
                return "30598-3.htm"; // you're not eligible to get a coupon (level caps, pkkills or already changed class)
        }
        else if (event.equals("newbie_give_armor_coupon"))
        {
            if (6 <= level && level <= 39 && pkkills == 0 && occupation_level == 1)
            {
                //  check the player state against this quest newbie rewarding mark.
                if (newbie != 0 | NEWBIE_ACCESORY != newbie)
                {
                    player.setNewbie(newbie | NEWBIE_ACCESORY);
                    st.giveItems(COUPON_TWO, 1);
                    return "30598-5.htm"; // here's the coupon you requested
                }
                else
                    return "30598-4.htm"; // you got a coupon already!
            }
            else
                return "30598-6.htm"; // you're not eligible to get a coupon (level caps, pkkills or didnt change class yet)
        }
        else if (event.equals("newbie_show_weapon"))
        {
            if (6 <= level && level <= 39 && pkkills == 0 && occupation_level == 0)
            	MultiSell.getInstance().separateAndSend(WEAPON_MULTISELL, player, npc, false);
            else
                return "30598-7.htm"; // you're not eligible to use warehouse
        }
        else if (event.equals("newbie_show_armor"))
        {
            if (6 <= level && level <= 39 && pkkills == 0 && occupation_level > 0)
            	MultiSell.getInstance().separateAndSend(ACCESORIES_MULTISELL, player, npc, false);
            else
                return "30598-8.htm"; // you're not eligible to use warehouse
        }
        return null;
    }

    @Override
	public String onTalk(L2Npc npc, L2PcInstance player)
    {
        QuestState st = player.getQuestState(qn);
        if (st == null)
            st = newQuestState(player);
        return "30598.htm";
    }

    
    public static void main(String[] args)
    {
        new NewbieCoupons(-1, qn, "custom");
    }
}
