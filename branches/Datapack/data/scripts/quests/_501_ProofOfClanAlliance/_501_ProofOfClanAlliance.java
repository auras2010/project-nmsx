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
package quests._501_ProofOfClanAlliance;

import gnu.trove.TIntIntHashMap;

import java.util.logging.Level;
import java.util.logging.Logger;

import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.model.L2Clan;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;
import l2.universe.gameserver.model.quest.QuestTimer;
import l2.universe.gameserver.network.serverpackets.NpcSay;

public class _501_ProofOfClanAlliance extends Quest
{
	private static final Logger _log = Logger.getLogger(_501_ProofOfClanAlliance.class.getName());
	private static final String qn = "_501_ProofOfClanAlliance";
	private static final boolean DEBUG = false; 
	private static final String DELIMITER = ";";
	
	// Quest Npcs
	private static final int SIR_KRISTOF_RODEMAI  = 30756;
	private static final int STATUE_OF_OFFERING   = 30757;
	private static final int WITCH_ATHREA         = 30758;
	private static final int WITCH_KALIS          = 30759;
	
	private static final int POISON_OF_DEATH = 4082;
	private static final int DYING = 4083;
	private static final int FIE = 10000;
	
	// Quest Items
	private static final int HERB_OF_HARIT     = 3832;
	private static final int HERB_OF_VANOR     = 3833;
	private static final int HERB_OF_OEL_MAHUM = 3834;
	private static final int BLOOD_OF_EVA      = 3835;
	private static final int SYMBOL_OF_LOYALTY = 3837;
	private static final int PROOF_OF_ALLIANCE = 3874;
	private static final int VOUCHER_OF_FAITH  = 3873;
	private static final int ANTIDOTE_RECIPE   = 3872;
	private static final int POTION_OF_RECOVERY= 3889;
	
	private static final int CHESTS[] = {27173,27178};
	private static final int CHEST_LOCS[][] = 
		{	{102273,103433,-3512}, {102190,103379,-3524}, {102107,103325,-3533},
			{102024,103271,-3500}, {102327,103350,-3511}, {102244,103296,-3518},
			{102161,103242,-3529}, {102078,103188,-3500}, {102381,103267,-3538},
			{102298,103213,-3532}, {102215,103159,-3520}, {102132,103105,-3513},
			{102435,103184,-3515}, {102352,103130,-3522}, {102269,103076,-3533},
			{102186,103022,-3541}};
	
	private static TIntIntHashMap MOBS = new TIntIntHashMap();
		//{
		//    { 20685, 20644, 20576 },
		//    { HERB_OF_VANOR, HERB_OF_HARIT, HERB_OF_OEL_MAHUM }};
	
	private static boolean isArthea = false;
	private static int chests_kills = 0;
	private static int chests_wins = 0;
	
	/**
	 * If actual player is not Clan Leader we must find Clan Leader for this clan
	 * and then we must check if cl have this quest taken too.
	 * 
	 * @param player - actual player
	 * @return - leader quest state.
	 */
	private QuestState getLeaderQuestState(L2PcInstance player)
	{
		if (player.isClanLeader())
			return player.getQuestState(qn);
		
		L2Clan clan = player.getClan();
		if (clan == null)
			return null;
		
		L2PcInstance leader = clan.getLeader().getPlayerInstance();
		if (leader == null)
			return null;
		
		QuestState leaderst = leader.getQuestState(qn);
		return leaderst;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		QuestState leaderst = null;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return null;
		
		if (event.equals("chest_timer"))
		{
			isArthea = false;
			return "";
		}
		
		if (player.isClanLeader()) 
			leaderst = st;
		else
			leaderst = getLeaderQuestState(player);
		
		if (leaderst == null)
		{
			if (DEBUG) return debugScript("Event can't find leader");
			return null;
		}
		
		if (player.isClanLeader())
		{
			if (event.equalsIgnoreCase("30756-07.htm"))
			{
				st.playSound("ItemSound.quest_accept");
				st.set("cond", "1");
				st.setState(State.STARTED);
				st.set("part", "1");
			}
			else if (event.equalsIgnoreCase("30759-03.htm"))
			{
				st.set("part", "2");
				st.set("cond", "2");
				st.set("dead_list", "");
			}
			else if (event.equalsIgnoreCase("30759-07.htm"))
			{
		        st.takeItems(SYMBOL_OF_LOYALTY,1); // Item is not stackable, thus need to do this for each item
		        st.takeItems(SYMBOL_OF_LOYALTY,1);
		        st.takeItems(SYMBOL_OF_LOYALTY,1);
		        st.giveItems(ANTIDOTE_RECIPE,1);
		        st.set("part","3");
		        st.set("cond","3");
		        st.startQuestTimer("poison_timer", 3600000); // 1000 * 60 * 60 = 1 hour
		        st.addNotifyOfDeath(player);
		        SkillTable.getInstance().getInfo(POISON_OF_DEATH, 1).getEffects(npc,player);
			}
			else if (event.equalsIgnoreCase("poison_timer"))
			{
				st.exitQuest(true);
				if (DEBUG) return debugScript("Times Up! Quest failed!");
			}
		}
		else if (event.equalsIgnoreCase("30757-05.htm"))
		{
			if (player.isClanLeader()) return "Only Clan Members can sacrifice themselves!";
			
			if (st.getRandom(10) > 5)
			{
				st.giveItems(SYMBOL_OF_LOYALTY,1);
				String[] deadlist = leaderst.get("dead_list").split(DELIMITER);
				leaderst.set("dead_list", joinStringArray(setNewValToArray(deadlist, player.getName().toLowerCase()), DELIMITER));
				return "30757-06.htm";
			}
			else
			{
				L2Skill skill = SkillTable.getInstance().getInfo(DYING, 1);
				npc.setTarget(player);
				npc.doCast(skill);
				startQuestTimer(player.getName(), 4000, npc,player, false);
			}
		}
		else if (event.equalsIgnoreCase(player.getName()))
		{
			if (player.isDead())
			{
				st.giveItems(SYMBOL_OF_LOYALTY, 1);
				String[] deadlist = leaderst.get("dead_list").split(DELIMITER);
				leaderst.set("dead_list", joinStringArray(setNewValToArray(deadlist, player.getName().toLowerCase()), DELIMITER));
			}
			else if (DEBUG) return debugScript("player " + player.getName() + " didn't die!");
		}
		else if (event.equalsIgnoreCase("30758-03.htm"))
		{
			if (isArthea)
				return "30758-04.htm";
			
			isArthea = true;
			chests_kills = chests_wins = 0;
			leaderst.set("part","4");
			for (int i = 0; i < CHEST_LOCS.length; i++ )
			{
				int rand = st.getRandom(5);
				addSpawn(CHESTS[0] + rand, CHEST_LOCS[i][0], CHEST_LOCS[i][1], CHEST_LOCS[i][2], 0, false, 300000);
				startQuestTimer("chest_timer", 300000, npc, player, false);
			}
		}
		else if (event.equalsIgnoreCase("30758-07.htm"))
		{
			if (st.getQuestItemsCount(57) >= FIE && !isArthea)
			{
				st.takeItems(57, FIE);
				return "30758-08.htm";
			}
		}
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		String htmltext = getNoQuestMsg(talker);
		QuestState st = talker.getQuestState(qn);
		if (st == null)
			return htmltext;

		
	   int npcId = npc.getNpcId();
	   byte id = st.getState();
	   L2Clan clan = talker.getClan();
	   int part = st.getInt("part");
		   

	   switch (npcId)
	   {
		   case SIR_KRISTOF_RODEMAI:
			   if (id == State.CREATED)
			   {
				   if (!talker.isClanLeader() || clan == null)
					   return returningString("05", npcId);
				   
				   int level = clan.getLevel();
			       if (level <= 2)
			    	   return returningString("01", npcId);
			       else if (level >= 4)
			    	   return returningString("02", npcId);
			       else if (level == 3)
			       {
                       if (st.hasQuestItems(PROOF_OF_ALLIANCE)) // you already have the item, no need to complete the quest!
                    	   return returningString("03", npcId);
                       else
                    	   return returningString("04", npcId);
			       }
			   }
			   else if (id == State.STARTED)
			   {
                   if (!st.hasQuestItems(VOUCHER_OF_FAITH) || part != 6)
                	   return returningString("10", npcId);

                   st.playSound("ItemSound.quest_finish");
                   st.takeItems(VOUCHER_OF_FAITH,1);
                   st.giveItems(PROOF_OF_ALLIANCE,1);
                   st.addExpAndSp(0, 120000);
                   st.exitQuest(false);
                   return returningString("09", npcId);
			   }
			   break;
		
		   case WITCH_KALIS:
		       if (id == State.CREATED) // not a valid leader (has not started the quest yet) or a clan member
		       {
		    	   QuestState leaderst = getLeaderQuestState(talker);
		    	   if (leaderst == null)
		    	   {
		    		   if (DEBUG) return debugScript("Kalis cannot find leader");
		    		   return "";
		    	   }
		    	   
		    	   if (talker.isClanLeader() || leaderst == st)
		    		   return "You must see Rodemai to start the quest! I cannot help you!";
		    	   else if (leaderst.getState() == State.STARTED)
		    		   return returningString("12", npcId);
		    	   else if (DEBUG) return debugScript("Leader needs to start the quest!");
		       }
		       else if (id == State.STARTED)
		       {
		    	   long symbol = st.getQuestItemsCount(SYMBOL_OF_LOYALTY);
		    	   if (part == 1/* && st.hasQuestItems(SYMBOL_OF_LOYALTY)*/)
		    		   return returningString("01", npcId);
		    	   else if (part == 2 && symbol < 3)
		    		   return returningString("05", npcId);
		    	   else if (symbol >= 3 && isAffected(talker, 4082))
		    		   return returningString("06", npcId);
		    	   else if (part == 5 && 
		    			   st.hasQuestItems(HERB_OF_HARIT) && 
		    			   st.hasQuestItems(HERB_OF_VANOR) && 
		    			   st.hasQuestItems(HERB_OF_OEL_MAHUM) && 
		    			   st.hasQuestItems(BLOOD_OF_EVA) && 
		    			   isAffected(talker,4082))
		    	   {
		    		   st.giveItems(VOUCHER_OF_FAITH, 1);
		    	       st.giveItems(POTION_OF_RECOVERY,1);
		    	       
	    			   st.takeItems(HERB_OF_HARIT, -1);
	    			   st.takeItems(HERB_OF_VANOR, -1); 
	    			   st.takeItems(HERB_OF_OEL_MAHUM, -1); 
	    			   st.takeItems(BLOOD_OF_EVA, -1);
		    	       
	                   st.set("part","6");
	                   st.set("cond","4");
	                   QuestTimer timer = st.getQuestTimer("poison_timer");
	                   if (timer != null)
	                	   timer.cancel();
	                   return returningString("08", npcId);
		    	   }
		    	   else if (part == 3 || part == 4 || part == 5)
		    	   {
		    		   if (!isAffected(talker, 4082))
		    		   {
                           st.set("part","1");
                           st.takeItems(ANTIDOTE_RECIPE, -1);
                           return returningString("09", npcId);
		    		   }
		    		   else
		    			   return returningString("10", npcId);
		    	   }
		    	   else if (part == 6)
		    		   return returningString("11", npcId);
		    	   else if (DEBUG) return debugScript("Uhhh....Kalis is confused by player: " + talker.getName());
		       }
	    	   else if (DEBUG) return debugScript("Player has already finished the quest!");
		       break;

		   case STATUE_OF_OFFERING:
	    	   QuestState leaderst = getLeaderQuestState(talker);
	    	   if (leaderst == null)
	    	   {
	    		   if (DEBUG) return debugScript("Statue can't find leader");
	    		   return "";
	    	   }
	    	   
	    	   byte sId = leaderst.getState();
	    	   switch (sId)
	    	   {
	    		   case State.STARTED:
					if (leaderst.getInt("part") != 2)
					{
						if (DEBUG) return debugScript("wrong state for sacrifice");
			    		return "";
					}
					
					if (talker.isClanLeader() || leaderst == st)
						return returningString("02", npcId);
					
					if (talker.getLevel() >= 40)
					{
						String[] dlist = leaderst.get("dead_list").split(DELIMITER);
						if (!contains(dlist, talker.getName()) && dlist.length < 3)
							return returningString("01", npcId);
						else
							return returningString("03", npcId);
					}
					else
						return returningString("04", npcId);
				
	    		   default:
	    			   if (DEBUG) return debugScript("Leader must start the quest or has already finished the quest!");
	    			   return returningString("08", npcId);
	    	   }
		   
		   case WITCH_ATHREA:
	    	   QuestState leader_st = getLeaderQuestState(talker);
	    	   if (leader_st == null)
	    	   {
	    		   if (DEBUG) return debugScript("Athrea can't find your leader!");
	    		   return "";
	    	   }
	    	   
	    	   byte s_Id = leader_st.getState();
	    	   switch (s_Id)
	    	   {
	    		   case State.STARTED:
	    			   int partA = leader_st.getInt("part");
	    			   if (partA == 3 && leader_st.hasQuestItems(ANTIDOTE_RECIPE) && !leader_st.hasQuestItems(BLOOD_OF_EVA))
	    				   return returningString("01", npcId);
	    			   else if (partA == 5)
	    				   return returningString("10", npcId);
	    			   else if (partA == 4)
	    			   {
	    				   if (leader_st.getInt("chest_wins") >= 4)
	    				   {
	    					   st.giveItems(BLOOD_OF_EVA,1);
	    					   leader_st.set("part","5");
	    					   return returningString("09", npcId);
	    				   }
	    				   return returningString("06", npcId);
	    			   }
	    			   else if (DEBUG) return debugScript("You should go seek help elsewhere! I cannot help you in your current state!");
					break;
				
				default:
					if (DEBUG) return debugScript("You must have the quest started!");
					break;
	    	   }
			   break;

		   default:
			   break;
	   }
	   
	   return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		QuestState leaderst = getLeaderQuestState(killer);
		if (leaderst == null)
			return null;
		
		if (leaderst.getState() != State.STARTED)
		{
			if (DEBUG) return debugScript("leader needs to start quest");
			return null;
		}
		
		int part = leaderst.getInt("part");
		int npcId = npc.getNpcId();
		
		if (rangeCompare(npcId) && part == 4)
		{
			if ((chests_kills - chests_wins) == 12 || (chests_wins < 4 && leaderst.getRandom(4) == 0))
			{
				chests_wins++;
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), "###### BINGO! ######"));
				//npc.broadcastPacket(new NpcSay(npc.getObjectId(), type, npc.getNpcId(), text));
			}
			
			chests_kills++;
		}
		else if ( MOBS.containsKey(npcId))
		{
			QuestState st = killer.getQuestState(qn);
			if (st == null)
				st = newQuestState(killer);
			
			if (st == leaderst)
				return null;
			
			if (part >=3 && part < 6)
			{
				if (st.getRandom(10) == 0)
				{
					st.giveItems(MOBS.get(npcId), 1);
	                st.playSound("ItemSound.quest_itemget");
				}
				else if (DEBUG) return debugScript("leader is not correct state");
			}
		}
		
		return null;
	}
	
	@Override
	public String onDeath(L2Character killer, L2Character victim, QuestState qs)
	{
		if (qs.getPlayer().equals(victim))
		{
			QuestTimer tm = qs.getQuestTimer("poison_timer");
	        if (tm != null)
	        	tm.cancel();
	
	        qs.exitQuest(true);

			if (DEBUG) return debugScript("leader died, quest failed");
		}
		
        return null;
	}
	
	/**
	 * Cheching if player is affected by skill.
	 * 
	 * @param player - actual player
	 * @param skillId - skill's id
	 * @return
	 */
	private boolean isAffected(L2PcInstance player, int skillId)
	{
		return player.getFirstEffect(skillId) != null;
	}
	
	/**
	 * Checks, that value is in Range.
	 * 
	 * @param i - value
	 * @return true, if value is in range.
	 */
	private boolean rangeCompare(int i)
	{
		return i >= CHESTS[0] && CHESTS[1] >= i;
	}
	
	/**
	 * Debug mode for script.
	 * 
	 * TODO: DEBUG mode should be solve in other way.
	 * When player take the quest should be saving for this quest
	 * DEBUG mode. If player finishing quest DEBUG mode should be OFF
	 * for this quest and all variables should be cleared. 
	 * Of course debug mode should 
	 * be only for quests that have ONE TIME running. 
	 * Should be global config for DEBUG mode for admin. 
	 * Default should be ON. If admin
	 * TURN OFF DEBUG MODE then will not be saved any variables but 
	 * will be more troubles with solve troubles for quests.
	 * 
	 * @param msg
	 * @return
	 */
	private static String debugScript(String msg)
	{
		_log.log(Level.INFO, msg);
		return msg;
	}
	
	/**
	 * Joining array into simgle string with separator.
	 * 
	 * @param s - string array
	 * @param separator
	 * @return single string.
	 */
	private static String joinStringArray(String[] s, String sep)
	{
		String ts = "";
		for (int i = 0; i < s.length; i++)
		{
			if (i == (s.length - 1))
				ts += s[i];
			else
				ts += s[i] + sep;
		}
		
		return ts;
	}
	
	/**
	 * New array with all previous values and new value.
	 * 
	 * @param s
	 * @param s1
	 * @return
	 */
	public static String[] setNewValToArray(String[] s, String s1)
	{
		String[] ts = new String[s.length+1];
		for (int i = 0; i < s.length; i++)
			ts[i] = s[i];
		ts[s.length] = s1;
		return ts;
	}
	
	/**
	 * 
	 * @param s
	 * @param npcId
	 * @return
	 */
	private static String returningString(String s, int npcId)
	{
		return String.valueOf(npcId) + "-" + s + ".htm";
	}
	
	/**
	 * @param questId
	 * @param name
	 * @param descr
	 */
	public _501_ProofOfClanAlliance(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		MOBS.putIfAbsent(20685, HERB_OF_VANOR);
		MOBS.putIfAbsent(20644, HERB_OF_HARIT);
		MOBS.putIfAbsent(20576, HERB_OF_OEL_MAHUM);
		
		isArthea = false;
		chests_kills = chests_wins = 0;
		questItemIds = new int[] {HERB_OF_VANOR, HERB_OF_HARIT, HERB_OF_OEL_MAHUM, BLOOD_OF_EVA, SYMBOL_OF_LOYALTY, ANTIDOTE_RECIPE, VOUCHER_OF_FAITH, POTION_OF_RECOVERY, ANTIDOTE_RECIPE};
		// TODO Auto-generated constructor stub
	}
	
}