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

package custom.FreeNoble;

import l2.universe.gameserver.instancemanager.QuestManager;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;

/**
 * @author LasTravel
 */
public class FreeNoble extends Quest
{
   private static final int NPCID = 10003;
   private static final int LevelMin = 80;
   private static final int ItemId = 7678; //Caradine's Letter 
   private static final int Amount = 1;
   
   private static final String qn = "FreeNoble";
   
   public FreeNoble(int questId, String name, String descr)
   {
       super(questId, name, descr);
       addStartNpc(NPCID);
       addTalkId(NPCID);
   }
   
   @Override
   public String onTalk(L2Npc npc, L2PcInstance player)
   {
       String htmltext ="";
       QuestState st = player.getQuestState(getName());
       if (st == null)
       {
           Quest q = QuestManager.getInstance().getQuest(getName());
           st = q.newQuestState(player);
       }
       if (npc.getNpcId() == NPCID)
           htmltext = "1.htm";
   return htmltext;
   }
   
   private boolean CheckCondition(L2PcInstance player)
   {
       if (player == null)
           return false;
       
       else if (player.isHero() || player.isNoble())
           return false;
       else if (player.getLevel() < LevelMin)
           return false;
       else if (!(player.getQuestState(qn).getQuestItemsCount(ItemId) >= Amount))
           return false;

       else
           return true;
   }
   
   @Override
   public String onAdvEvent (String event, L2Npc npc, L2PcInstance player)
   {
		final QuestState st = player.getQuestState(getName());
		if (st == null) 
			return event;
		
       //Give Noble Status
       if (event.equalsIgnoreCase("getnoble"))
           if (CheckCondition(player))
           {   
               player.setNoble(true);
               st.takeItems(ItemId, Amount);
               player.sendMessage("Congrats you are noble now!");
           }   
           else
               player.sendMessage("You can't use this now, go away!");
   return "";
   }
   
   public static void main(String[] args)
   {
       new FreeNoble(-1, qn, "custom");
   }
}