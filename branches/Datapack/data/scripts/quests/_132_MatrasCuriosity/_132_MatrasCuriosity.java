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
package quests._132_MatrasCuriosity;

import l2.universe.gameserver.model.L2Party;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

public class _132_MatrasCuriosity extends Quest
{
    // NPC
    public final static int MATRAS = 32245;

    // Mobs
    public final static int DEMONPRINCE = 25540;
    public final static int RANKU = 25542;

    // Items
    public final static int RANKUSBLUEPRINT = 9800;
    public final static int PRINCESBLUEPRINT = 9801;

    public final static int ROUGHOREOFFIRE = 10521;
    public final static int ROUGHOREOFWATER = 10522;
    public final static int ROUGHOREOFTHEEARTH = 10523;
    public final static int ROUGHOREOFWIND = 10524;
    public final static int ROUGHOREOFDARKNESS = 10525;
    public final static int ROUGHOREOFDIVINITY = 10526;

    public _132_MatrasCuriosity(int id, String name, String descr)
    {
        super(id, name, descr);

        addStartNpc(MATRAS);
        addTalkId(MATRAS);
        addKillId(DEMONPRINCE);
        addKillId(RANKU);
    }

    @Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player)
    {
		final QuestState st = player.getQuestState(getName());
		if (st == null) 
			return null;
		
		if (event.equals("32245-02.htm"))
        {
			if (st.getPlayer().getLevel() >= 76)
            {
				st.setState(State.STARTED);
				st.set("cond","1");
				st.playSound("ItemSound.quest_accept");
				return "32245-02.htm";
            }
        }
		else if (event.equals("takeitem"))
        {
			if (st.getQuestItemsCount(PRINCESBLUEPRINT) >= 1 && st.getQuestItemsCount(RANKUSBLUEPRINT) >= 1)
            {
				st.takeItems(RANKUSBLUEPRINT,1);
				st.takeItems(PRINCESBLUEPRINT,1);
				st.set("cond","3");
				st.playSound("ItemSound.quest_middle");
				startQuestTimer("waiting_matras",30000,npc,player);
				return "32245-07.htm";
            }
			else
                return "32245-06.htm";
        }
		else if (event.equals("getpriz"))
        {
			st.giveItems(57,31210);
			st.giveItems(ROUGHOREOFFIRE,1);
			st.giveItems(ROUGHOREOFWATER,1);
			st.giveItems(ROUGHOREOFTHEEARTH,1);
			st.giveItems(ROUGHOREOFWIND,1);
			st.giveItems(ROUGHOREOFDARKNESS,1);
			st.giveItems(ROUGHOREOFDIVINITY,1);
			st.exitQuest(false);
			st.playSound("ItemSound.quest_finish");
			return "32245-05.htm";
        }
		else if (event.equals("waiting_matras"))
        {
			st.set("wait","1");
            return null;
        }
		return event;
    }

    @Override
	public String onTalk(final L2Npc npc, final L2PcInstance player)
    {
		String htmltext = msgNotHaveMinimumRequirements();
		final QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
		final int cond = st.getInt("cond");
		
		switch (st.getState())
		{
			case State.COMPLETED:
				htmltext = msgQuestCompleted();
				break;
			case State.CREATED:
				if (player.getLevel() >= 76)
					htmltext = "32245-01.htm";
				else
	            {
					st.exitQuest(true);
					htmltext = "32245-00.htm";
	            }
		        break;
			case State.STARTED:
        		switch (cond)
        		{
        			case 1:
        				htmltext = "32245-06.htm";
        				break;
        			case 2:
        				htmltext = "32245-03.htm";
        				break;
        			case 3:
        				if (st.getInt("wait") == 1)
        					htmltext = "32245-04.htm";
        				else
        					htmltext = "32245-08.htm";
        				break;
        		}
		        break;
		}
		return htmltext;
    }

    @Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
    {
		QuestState st = player.getQuestState(getName());
		if (st == null && player.getParty() == null) 
			return null;
		
        final L2Party party = player.getParty();
        switch (npc.getNpcId())
        {
        	case DEMONPRINCE:
    			if (party != null)
                {
    			    for (L2PcInstance partyMember : party.getPartyMembers())
                    {
    				    st = partyMember.getQuestState(getName());
    					if (st != null && st.getState() == State.STARTED && st.getQuestItemsCount(PRINCESBLUEPRINT) < 1)
                        {
    					    st.giveItems(PRINCESBLUEPRINT, 1);
    					    st.playSound("ItemSound.quest_itemget");
    						if (st.getQuestItemsCount(RANKUSBLUEPRINT) >= 1)
                            {
    						    st.set("cond","2");
    						    st.playSound("ItemSound.quest_middle");
                            }
                        }
                    }
                }
    			else
                {
    				if (st.getState() == State.STARTED && st.getQuestItemsCount(PRINCESBLUEPRINT) < 1)
                    {
    					st.giveItems(PRINCESBLUEPRINT, 1);
    					st.playSound("ItemSound.quest_itemget");
    					if (st.getQuestItemsCount(RANKUSBLUEPRINT) >= 1)
                        {
    						st.set("cond","2");
    						st.playSound("ItemSound.quest_middle");
                        }
                    }
                }
    			break;
        	case RANKU:
    			if (party != null)
                {
    			    for (L2PcInstance partyMember : party.getPartyMembers())
                    {
    					st = partyMember.getQuestState(getName());
    					if (st != null && st.getState() == State.STARTED && st.getQuestItemsCount(RANKUSBLUEPRINT) < 1)
                        {
    						st.giveItems(RANKUSBLUEPRINT, 1);
    						st.playSound("ItemSound.quest_itemget");
    						if (st.getQuestItemsCount(PRINCESBLUEPRINT) >= 1)
                            {
    							st.set("cond","2");
    							st.playSound("ItemSound.quest_middle");
                            }
                        }
                    }
                }
    			else
                {
    				if (st.getState() == State.STARTED && st.getQuestItemsCount(RANKUSBLUEPRINT) < 1)
                    {
    					st.giveItems(RANKUSBLUEPRINT, 1);
    					st.playSound("ItemSound.quest_itemget");
    					if (st.getQuestItemsCount(PRINCESBLUEPRINT) >= 1)
                        {
    						st.set("cond","2");
    						st.playSound("ItemSound.quest_middle");
                        }
                    }
                }
    			break;
        }
        
		return null;
    }

    public static void main(String[] args)
    {
        new _132_MatrasCuriosity(132, "_132_MatrasCuriosity", "Matras Curiosity");
    }
}
