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

package ai.fantasy_isle.UndergroundTournament;

import java.text.SimpleDateFormat;
import java.util.List;

import javolution.util.FastList;

import l2.universe.gameserver.Announcements;
import l2.universe.gameserver.GameTimeController;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.datatables.DoorTable;
import l2.universe.gameserver.instancemanager.QuestManager;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;

public class UndergroundTournament extends Quest
{
	private static final String qn = "UndergroundTournament";
	/*
	 * Config Event State false=Off;true=On
	 */
	private boolean TOURNAMENT_STATE = false;
	/*
	 * Npcs List
	 */
	private static final int Kuram = 32377;
	/*
	 * Players List
	 */
	private List<L2PcInstance> registeredPlayers = new FastList<L2PcInstance>();
	private List<L2PcInstance> Arena1Players = new FastList<L2PcInstance>();
	private List<L2PcInstance> Arena2Players = new FastList<L2PcInstance>();
	private List<L2PcInstance> Arena3Players = new FastList<L2PcInstance>();
	private List<L2PcInstance> Arena4Players = new FastList<L2PcInstance>();
	private List<L2PcInstance> Arena5Players = new FastList<L2PcInstance>();
	private List<L2PcInstance> Dead1Players = new FastList<L2PcInstance>();
	private List<L2PcInstance> Dead2Players = new FastList<L2PcInstance>();
	private List<L2PcInstance> Dead3Players = new FastList<L2PcInstance>();
	private List<L2PcInstance> Dead4Players = new FastList<L2PcInstance>();
	private List<L2PcInstance> Dead5Players = new FastList<L2PcInstance>();
	/*
	 * Arena Status
	 */
	private boolean Arena1_Status = false;
	private boolean Arena2_Status = false;
	private boolean Arena3_Status = false;
	private boolean Arena4_Status = false;
	private boolean Arena5_Status = false;
	/*
	 * Arena Doors
	 */
	private static final int Arena1_Doors[] = { 17160017, 17160018, 17160019, 17160020, 17160021, 17160022, 17160023, 17160024 };
	private static final int Arena2_Doors[] = { 17160025, 17160026, 17160027, 17160028, 17160029, 17160030, 17160031, 17160032 };
	private static final int Arena3_Doors[] = { 17160033, 17160034, 17160035, 17160036, 17160037, 17160038, 17160039, 17160040 };
	private static final int Arena4_Doors[] = { 17160001, 17160002, 17160003, 17160004, 17160005, 17160006, 17160007, 17160008 };
	private static final int Arena5_Doors[] = { 17160009, 17160010, 17160011, 17160012, 17160013, 17160014, 17160015, 17160016 };
	/*
	 * Spawns of Arenas
	 */
	private static final int Arena1_Locs[][] = { { -80676, -44285, -11496 }, { -82953, -44341, -11501 }, { -82546, -47027, -11506 }, { -81215, -47069, -11506 } };
	private static final int Arena2_Locs[][] = { { -83720, -47849, -11504 }, { -84121, -49111, -11504 }, { -86816, -48786, -11501 }, { -86251, -46589, -11501 } };
	private static final int Arena3_Locs[][] = { { -83677, -50536, -11504 }, { -82645, -51366, -11506 }, { -83916, -53765, -11496 }, { -85758, -52453, -11501 } };
	private static final int Arena4_Locs[][] = { { -80107, -50553, -11506 }, { -78163, -52434, -11501 }, { -79938, -53847, -11496 }, { -81192, -51299, -11504 } };
	private static final int Arena5_Locs[][] = { { -79638, -49190, -11504 }, { -79633, -49184, -11504 }, { -76837, -48777, -11496 }, { -77627, -46646, -11501 } };
	/*
	 * Back Location
	 */
	private static final int BackLoc[] = { -82096, -48965, -10336 };
	
	/*
	 * Schedule Fantasy Underground Tournament
	 */
	private void scheduleTimer()
	{
		int gameTime = GameTimeController.getInstance().getGameTime();
		int hours = (gameTime / 60) % 24;
		int minutes = gameTime % 60;
		int hourDiff, minDiff;
		hourDiff = (14 - hours);
		if (hourDiff < 0)
			hourDiff = 24 - (hourDiff *= -1);
		minDiff = (0 - minutes);
		if (minDiff < 0)
			minDiff = 60 - (minDiff *= -1);
		long diff;
		hourDiff *= 60 * 60 * 1000;
		minDiff *= 60 * 1000;
		diff = hourDiff + minDiff;
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		_log.info("Fantasy Isle: Underground Tournament starting at " + format.format(System.currentTimeMillis() + diff) + " and is scheduled each next 4 hours.");
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new StartUndergroundTournament(), diff, 14400000L);
	}
	
	public class StartUndergroundTournament implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				QuestManager.getInstance().getQuest("UndergroundTournament").notifyEvent("start", null, null);
			}
			catch (Exception e)
			{
			}
		}
	}
	
	/*
	 * On First Talk Code
	 */
	@Override
	public final String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(qn);
		if (st == null)
		{
			st = newQuestState(player);
		}
		String htmltext = "";
		if (TOURNAMENT_STATE)
		{
			if (player.isGM())
			{
				if (Arena1_Status && Arena2_Status && Arena3_Status && Arena4_Status && Arena5_Status)
				{
					htmltext = "32377-no.htm";
				}
				else
				{
					htmltext = "32377-gm.htm";
				}
			}
			else
			{
				if (registeredPlayers.size() > 3)
				{
					htmltext = "32377-ok.htm";
				}
				else
				{
					if (Arena1_Status && Arena2_Status && Arena3_Status && Arena4_Status && Arena5_Status)
					{
						htmltext = "32377-no.htm";
					}
					else
					{
						//TODO: Create check first day of month to get rewards
						htmltext = "32377.htm";
					}
				}
			}
		}
		else
		{
			if (player.isGM())
			{
				htmltext = "32377-gm.htm";
			}
			else
			{
				htmltext = "32377-disabled.htm";
			}
		}
		return htmltext;
	}
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		/*
		 * Start Event By Script
		 */
		if (event.equalsIgnoreCase("start"))
		{
			if (TOURNAMENT_STATE)
			{
				TOURNAMENT_STATE = false;
				Announcements.getInstance().announceToAll("=======================");
				Announcements.getInstance().announceToAll("Underground Tournament:");
				Announcements.getInstance().announceToAll("Fights ended.");
				Announcements.getInstance().announceToAll("Wait to next Tournament..");
				Announcements.getInstance().announceToAll("=======================");
				cancelQuestTimers("start");
			}
			else
			{
				TOURNAMENT_STATE = true;
				Announcements.getInstance().announceToAll("=======================");
				Announcements.getInstance().announceToAll("Underground Tournament:");
				Announcements.getInstance().announceToAll("Tournament Started.");
				Announcements.getInstance().announceToAll("Only to players with 75+ levels.");
				Announcements.getInstance().announceToAll("Register NPC: Kuram");
				Announcements.getInstance().announceToAll("Location: Underground Coliseum");
				Announcements.getInstance().announceToAll("How to get there:");
				Announcements.getInstance().announceToAll("Gatekeeper - > Fantasy Isle");
				Announcements.getInstance().announceToAll("Fantasy Isle - > Underground Coliseum");
				Announcements.getInstance().announceToAll("=======================");
				startQuestTimer("start", 3600000, npc, player, false);
			}
		}
		String htmltext = "";
		/*
		 * Reward List
		 */
		if (event.equalsIgnoreCase("rewards"))
		{
			htmltext = "32377-rewards.htm";
		}
		/*
		 * Reward List
		 * @param vitality
		 * @param clan points
		 * @param Fantasy Coins
		 */
		if (event.equalsIgnoreCase("getvitality"))
		{
			QuestState st = player.getQuestState(qn);
			if (st == null)
			{
				st = newQuestState(player);
			}
			if (st.getState() != State.STARTED)
			{
				st.setState(State.STARTED);
			}
			if (Integer.valueOf(st.get("points")) >= 10)
			{
				player.setVitalityPoints(3500, true);
				player.sendMessage("Your vitality increased");
				st.set("points", String.valueOf(Integer.valueOf(st.get("points")) - 10));
			}
			else
			{
				player.sendMessage("You don't have enough points");
				player.sendMessage("Have only " + st.get("points") + " - need 10");
			}
			htmltext = "32377-rewards.htm";
		}
		if (event.equalsIgnoreCase("getclanpoints"))
		{
			if (player.getClan() == null)
			{
				player.sendMessage("You cannot aquire clan points.");
				player.sendMessage("You don't have clan.");
			}
			else
			{
				QuestState st = player.getQuestState(qn);
				if (st == null)
				{
					st = newQuestState(player);
				}
				if (st.getState() != State.STARTED)
				{
					st.setState(State.STARTED);
				}
				if (player.getClan().getLevel() >= 5)
				{
					if (Integer.valueOf(st.get("points")) >= 50)
					{
						player.getClan().addReputationScore(200, true);
						player.sendMessage("Clan reputation increased by 200 points");
						st.set("points", String.valueOf(Integer.valueOf(st.get("points")) - 50));
					}
					else
					{
						player.sendMessage("You don't have enough points");
						player.sendMessage("Have only " + st.get("points") + " - need 10");
					}
				}
				else
				{
					player.sendMessage("You cannot aquire clan points.");
					player.sendMessage("Your clan need minimum 5 level.");
				}
			}
			htmltext = "32377-rewards.htm";
		}
		if (event.equalsIgnoreCase("getcoins"))
		{
			QuestState st = player.getQuestState(qn);
			if (st == null)
			{
				st = newQuestState(player);
			}
			if (st.getState() != State.STARTED)
			{
				st.setState(State.STARTED);
			}
			if (Integer.valueOf(st.get("points")) >= 5)
			{
				player.sendMessage("Received 15 Fantasy Coins");
				st.set("points", String.valueOf(Integer.valueOf(st.get("points")) - 5));
				st.giveItems(13067, 15);
			}
			else
			{
				player.sendMessage("You don't have enough points");
				player.sendMessage("Have only " + st.get("points") + " - need 5");
			}
			htmltext = "32377-rewards.htm";
		}
		/*
		 * Register Player
		 */
		if (event.equalsIgnoreCase("register"))
		{
			if (player.getLevel() < 75)
			{
				htmltext = "<html><body>Arena Manager:<BR>";
				htmltext += "You cannot register - you need 75+ level";
				htmltext += "</body></html>";
			}
			else
			{
				for (L2PcInstance plr : registeredPlayers)
				{
					if (!plr.isOnline())
					{
						registeredPlayers.remove(plr);
					}
				}
				if (Arena1_Status && Arena2_Status && Arena3_Status && Arena4_Status && Arena5_Status)
				{
					htmltext = "<html><body>Arena Manager:<BR>";
					htmltext += "You cannot register - all Arena is full";
					htmltext += "</body></html>";
				}
				else
				{
					if (!registeredPlayers.contains(player))
					{
						registeredPlayers.add(player);
					}
					htmltext = "<html><body>Arena Manager:<BR>";
					htmltext += "This is a list of the teams planning to participate in this match. Impressive, aren't they? But don't be intimidated. Knowing your opponents and their capabilities is good strategy in fact.<BR><BR>";
					htmltext += "=== List of Participating Players ===<BR>";
					for (L2PcInstance plr : registeredPlayers)
					{
						htmltext += "(Player: <FONT color=00ffff> " + plr.getName() + " [" + plr.getLevel() + "] {" + plr.getTemplate().className + "} " + "</FONT>)<BR>";
					}
					if (registeredPlayers.contains(player))
					{
						htmltext += "<br><br1><a action=\"bypass -h Quest UndergroundTournament unregister\">Unregister Me</a><br>";
					}
					htmltext += "</body></html>";
				}
				if (registeredPlayers.size() > 3)
				{
					startQuestTimer("fight", 1000, npc, player, false);
				}
			}
		}
		/*
		 * Unregister Player
		 */
		if (event.equalsIgnoreCase("unregister"))
		{
			registeredPlayers.remove(player);
			htmltext = "32377-1.htm";
		}
		/*
		 * List of registered Players
		 */
		if (event.equalsIgnoreCase("list"))
		{
			htmltext = "<html><body>Arena Manager:<BR>";
			htmltext += "This is a list of the teams planning to participate in this match. Impressive, aren't they? But don't be intimidated. Knowing your opponents and their capabilities is good strategy in fact.<BR><BR>";
			htmltext += "=== List of Participating Players ===<BR>";
			for (L2PcInstance plr : registeredPlayers)
			{
				htmltext += "(Player: <FONT color=00ffff> " + plr.getName() + " [" + plr.getLevel() + "] {" + plr.getTemplate().className + "} " + "</FONT>)<BR>";
			}
			if (registeredPlayers.contains(player))
			{
				htmltext += "<br><br1><a action=\"bypass -h Quest UndergroundTournament unregister\">Unregister Me</a><br>";
			}
			htmltext += "</body></html>";
		}
		/*
		 * Arena 1
		 */
		if (event.equalsIgnoreCase("open_doors1"))
		{
			for (int i = 0; i < Arena1_Doors.length; i++)
			{
				DoorTable.getInstance().getDoor(Arena1_Doors[i]).openMe();
			}
		}
		if (event.equalsIgnoreCase("checkArena1p1"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead1Players.equals(player))
				{
					if (Arena1Players.get(0).isDead())
					{
						Dead1Players.add(Arena1Players.get(0));
						Arena1Players.get(0).setInstanceId(0);
						Arena1Players.remove(0);
					}
					if (!Arena1Players.get(0).isOnline())
					{
						Dead1Players.add(Arena1Players.get(0));
						Arena1Players.get(0).setInstanceId(0);
						Arena1Players.remove(0);
					}
					if (Arena1Players.get(0).getInstanceId() != 1)
					{
						Dead1Players.add(Arena1Players.get(0));
						Arena1Players.get(0).setInstanceId(0);
						Arena1Players.remove(0);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena1p2"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead1Players.equals(player))
				{
					if (Arena1Players.get(1).isDead())
					{
						Dead1Players.add(Arena1Players.get(1));
						Arena1Players.get(1).setInstanceId(0);
						Arena1Players.remove(1);
					}
					if (!Arena1Players.get(1).isOnline())
					{
						Dead1Players.add(Arena1Players.get(1));
						Arena1Players.get(1).setInstanceId(0);
						Arena1Players.remove(1);
					}
					if (Arena1Players.get(1).getInstanceId() != 1)
					{
						Dead1Players.add(Arena1Players.get(1));
						Arena1Players.get(1).setInstanceId(0);
						Arena1Players.remove(1);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena1p3"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead1Players.equals(player))
				{
					if (Arena1Players.get(2).isDead())
					{
						Dead1Players.add(Arena1Players.get(2));
						Arena1Players.get(2).setInstanceId(0);
						Arena1Players.remove(2);
					}
					if (!Arena1Players.get(2).isOnline())
					{
						Dead1Players.add(Arena1Players.get(2));
						Arena1Players.get(2).setInstanceId(0);
						Arena1Players.remove(2);
					}
					if (Arena1Players.get(2).getInstanceId() != 1)
					{
						Dead1Players.add(Arena1Players.get(2));
						Arena1Players.get(2).setInstanceId(0);
						Arena1Players.remove(2);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena1p4"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead1Players.equals(player))
				{
					if (Arena1Players.get(3).isDead())
					{
						Dead1Players.add(Arena1Players.get(3));
						Arena1Players.get(3).setInstanceId(0);
						Arena1Players.remove(3);
					}
					if (!Arena1Players.get(3).isOnline())
					{
						Dead1Players.add(Arena1Players.get(3));
						Arena1Players.get(3).setInstanceId(0);
						Arena1Players.remove(3);
					}
					if (Arena1Players.get(3).getInstanceId() != 1)
					{
						Dead1Players.add(Arena1Players.get(3));
						Arena1Players.get(3).setInstanceId(0);
						Arena1Players.remove(3);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena1Size"))
		{
			if (Arena1Players.size() == 1)
			{
				Announcements.getInstance().announceToAll("Tournament Manager: In Arena 1 win a " + Arena1Players.get(0).getName() + " and get 5 points");
				startQuestTimer("teleportback1", 1000, npc, player, false);
				for (int i = 0; i < Arena1_Doors.length; i++)
				{
					DoorTable.getInstance().getDoor(Arena1_Doors[i]).closeMe();
				}
				cancelQuestTimers("checkArena1p1");
				cancelQuestTimers("checkArena1p2");
				cancelQuestTimers("checkArena1p3");
				cancelQuestTimers("checkArena1p4");
				cancelQuestTimers("checkArena1Size");
			}
		}
		if (event.equalsIgnoreCase("teleportback1"))
		{
			for (int i = 0; i < Arena1Players.size(); i++)
			{
				Arena1Players.get(i).teleToLocation(BackLoc[0], BackLoc[1], BackLoc[2]);
				if (Arena1Players.get(i).getQuestState(getName()).get("points") == null)
				{
					Arena1Players.get(i).getQuestState(getName()).set("points", String.valueOf("5"));
					Arena1Players.get(i).setCurrentCp(Arena1Players.get(i).getMaxCp());
					Arena1Players.get(i).setCurrentMp(Arena1Players.get(i).getMaxMp());
					Arena1Players.get(i).setCurrentHp(Arena1Players.get(i).getMaxHp());
					Arena1Players.get(i).setInstanceId(0);
				}
				else
				{
					Arena1Players.get(i).getQuestState(getName()).set("points", String.valueOf(Integer.valueOf(Arena1Players.get(i).getQuestState(getName()).get("points")) + 5));
					Arena1Players.get(i).setCurrentCp(Arena1Players.get(i).getMaxCp());
					Arena1Players.get(i).setCurrentMp(Arena1Players.get(i).getMaxMp());
					Arena1Players.get(i).setCurrentHp(Arena1Players.get(i).getMaxHp());
					Arena1Players.get(i).setInstanceId(0);
				}
			}
			for (int i = 0; i < Dead1Players.size(); i++)
			{
				Dead1Players.get(i).teleToLocation(BackLoc[0], BackLoc[1], BackLoc[2]);
				Dead1Players.get(i).restoreExp(100);
				Dead1Players.get(i).doRevive();
				Dead1Players.get(i).setCurrentCp(Dead1Players.get(i).getMaxCp());
				Dead1Players.get(i).setCurrentMp(Dead1Players.get(i).getMaxMp());
				Dead1Players.get(i).setCurrentHp(Dead1Players.get(i).getMaxHp());
				Dead1Players.get(i).setInstanceId(0);
			}
			Dead1Players.clear();
			Arena1Players.clear();
			Arena1_Status = false;
		}
		/*
		 * Arena 2
		 */
		if (event.equalsIgnoreCase("open_doors2"))
		{
			for (int i = 0; i < Arena2_Doors.length; i++)
			{
				DoorTable.getInstance().getDoor(Arena2_Doors[i]).openMe();
			}
		}
		if (event.equalsIgnoreCase("checkArena2p1"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead2Players.equals(player))
				{
					if (Arena2Players.get(0).isDead())
					{
						Dead2Players.add(Arena2Players.get(0));
						Arena2Players.get(0).setInstanceId(0);
						Arena2Players.remove(0);
					}
					if (!Arena2Players.get(0).isOnline())
					{
						Dead2Players.add(Arena2Players.get(0));
						Arena2Players.get(0).setInstanceId(0);
						Arena2Players.remove(0);
					}
					if (Arena2Players.get(0).getInstanceId() != 2)
					{
						Dead2Players.add(Arena2Players.get(0));
						Arena2Players.get(0).setInstanceId(0);
						Arena2Players.remove(0);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena2p2"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead2Players.equals(player))
				{
					if (Arena2Players.get(1).isDead())
					{
						Dead2Players.add(Arena2Players.get(1));
						Arena2Players.get(1).setInstanceId(0);
						Arena2Players.remove(1);
					}
					if (!Arena2Players.get(1).isOnline())
					{
						Dead2Players.add(Arena2Players.get(1));
						Arena2Players.get(1).setInstanceId(0);
						Arena2Players.remove(1);
					}
					if (Arena2Players.get(1).getInstanceId() != 2)
					{
						Dead2Players.add(Arena2Players.get(1));
						Arena2Players.get(1).setInstanceId(0);
						Arena2Players.remove(1);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena2p3"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead2Players.equals(player))
				{
					if (Arena2Players.get(2).isDead())
					{
						Dead2Players.add(Arena2Players.get(2));
						Arena2Players.get(2).setInstanceId(0);
						Arena2Players.remove(2);
					}
					if (!Arena2Players.get(2).isOnline())
					{
						Dead2Players.add(Arena2Players.get(2));
						Arena2Players.get(2).setInstanceId(0);
						Arena2Players.remove(2);
					}
					if (Arena2Players.get(2).getInstanceId() != 2)
					{
						Dead2Players.add(Arena2Players.get(2));
						Arena2Players.get(2).setInstanceId(0);
						Arena2Players.remove(2);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena2p4"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead2Players.equals(player))
				{
					if (Arena2Players.get(3).isDead())
					{
						Dead2Players.add(Arena2Players.get(3));
						Arena2Players.get(3).setInstanceId(0);
						Arena2Players.remove(3);
					}
					if (!Arena2Players.get(3).isOnline())
					{
						Dead2Players.add(Arena2Players.get(3));
						Arena2Players.get(3).setInstanceId(0);
						Arena2Players.remove(3);
					}
					if (Arena2Players.get(3).getInstanceId() != 2)
					{
						Dead2Players.add(Arena2Players.get(3));
						Arena2Players.get(3).setInstanceId(0);
						Arena2Players.remove(3);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena2Size"))
		{
			if (Arena2Players.size() == 1)
			{
				Announcements.getInstance().announceToAll("Tournament Manager: In Arena 2 win a " + Arena2Players.get(0).getName() + " and get 5 points");
				startQuestTimer("teleportback2", 1000, npc, player, false);
				for (int i = 0; i < Arena2_Doors.length; i++)
				{
					DoorTable.getInstance().getDoor(Arena2_Doors[i]).closeMe();
				}
				cancelQuestTimers("checkArena2p1");
				cancelQuestTimers("checkArena2p2");
				cancelQuestTimers("checkArena2p3");
				cancelQuestTimers("checkArena2p4");
				cancelQuestTimers("checkArena2Size");
			}
		}
		if (event.equalsIgnoreCase("teleportback2"))
		{
			for (int i = 0; i < Arena2Players.size(); i++)
			{
				Arena2Players.get(i).teleToLocation(BackLoc[0], BackLoc[1], BackLoc[2]);
				if (Arena2Players.get(i).getQuestState(getName()).get("points") == null)
				{
					Arena2Players.get(i).getQuestState(getName()).set("points", String.valueOf("5"));
					Arena2Players.get(i).setCurrentCp(Arena2Players.get(i).getMaxCp());
					Arena2Players.get(i).setCurrentMp(Arena2Players.get(i).getMaxMp());
					Arena2Players.get(i).setCurrentHp(Arena2Players.get(i).getMaxHp());
					Arena2Players.get(i).setInstanceId(0);
				}
				else
				{
					Arena2Players.get(i).getQuestState(getName()).set("points", String.valueOf(Integer.valueOf(Arena2Players.get(i).getQuestState(getName()).get("points")) + 5));
					Arena2Players.get(i).setCurrentCp(Arena2Players.get(i).getMaxCp());
					Arena2Players.get(i).setCurrentMp(Arena2Players.get(i).getMaxMp());
					Arena2Players.get(i).setCurrentHp(Arena2Players.get(i).getMaxHp());
					Arena2Players.get(i).setInstanceId(0);
				}
			}
			for (int i = 0; i < Dead2Players.size(); i++)
			{
				Dead2Players.get(i).teleToLocation(BackLoc[0], BackLoc[1], BackLoc[2]);
				Dead2Players.get(i).restoreExp(100);
				Dead2Players.get(i).doRevive();
				Dead2Players.get(i).setCurrentCp(Dead2Players.get(i).getMaxCp());
				Dead2Players.get(i).setCurrentMp(Dead2Players.get(i).getMaxMp());
				Dead2Players.get(i).setCurrentHp(Dead2Players.get(i).getMaxHp());
				Dead2Players.get(i).setInstanceId(0);
			}
			Dead2Players.clear();
			Arena2Players.clear();
			Arena2_Status = false;
		}
		/*
		 * Arena 3
		 */
		if (event.equalsIgnoreCase("open_doors3"))
		{
			for (int i = 0; i < Arena3_Doors.length; i++)
			{
				DoorTable.getInstance().getDoor(Arena3_Doors[i]).openMe();
			}
		}
		if (event.equalsIgnoreCase("checkArena3p1"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead3Players.equals(player))
				{
					if (Arena3Players.get(0).isDead())
					{
						Dead3Players.add(Arena3Players.get(0));
						Arena3Players.get(0).setInstanceId(0);
						Arena3Players.remove(0);
					}
					if (!Arena3Players.get(0).isOnline())
					{
						Dead3Players.add(Arena3Players.get(0));
						Arena3Players.get(0).setInstanceId(0);
						Arena3Players.remove(0);
					}
					if (Arena3Players.get(0).getInstanceId() != 3)
					{
						Dead3Players.add(Arena3Players.get(0));
						Arena3Players.get(0).setInstanceId(0);
						Arena3Players.remove(0);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena3p2"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead3Players.equals(player))
				{
					if (Arena3Players.get(1).isDead())
					{
						Dead3Players.add(Arena3Players.get(1));
						Arena3Players.get(1).setInstanceId(0);
						Arena3Players.remove(1);
					}
					if (!Arena3Players.get(1).isOnline())
					{
						Dead3Players.add(Arena3Players.get(1));
						Arena3Players.get(1).setInstanceId(0);
						Arena3Players.remove(1);
					}
					if (Arena3Players.get(1).getInstanceId() != 3)
					{
						Dead3Players.add(Arena3Players.get(1));
						Arena3Players.get(1).setInstanceId(0);
						Arena3Players.remove(1);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena3p3"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead3Players.equals(player))
				{
					if (Arena3Players.get(2).isDead())
					{
						Dead3Players.add(Arena3Players.get(2));
						Arena3Players.get(2).setInstanceId(0);
						Arena3Players.remove(2);
					}
					if (!Arena3Players.get(2).isOnline())
					{
						Dead3Players.add(Arena3Players.get(2));
						Arena3Players.get(2).setInstanceId(0);
						Arena3Players.remove(2);
					}
					if (Arena3Players.get(2).getInstanceId() != 3)
					{
						Dead3Players.add(Arena3Players.get(2));
						Arena3Players.get(2).setInstanceId(0);
						Arena3Players.remove(2);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena3p4"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead3Players.equals(player))
				{
					if (Arena3Players.get(3).isDead())
					{
						Dead3Players.add(Arena3Players.get(3));
						Arena3Players.get(3).setInstanceId(0);
						Arena3Players.remove(3);
					}
					if (!Arena3Players.get(3).isOnline())
					{
						Dead3Players.add(Arena3Players.get(3));
						Arena3Players.get(3).setInstanceId(0);
						Arena3Players.remove(3);
					}
					if (Arena3Players.get(3).getInstanceId() != 3)
					{
						Dead3Players.add(Arena3Players.get(3));
						Arena3Players.get(3).setInstanceId(0);
						Arena3Players.remove(3);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena3Size"))
		{
			if (Arena3Players.size() == 1)
			{
				Announcements.getInstance().announceToAll("Tournament Manager: In Arena 3 win a " + Arena3Players.get(0).getName() + " and get 5 points");
				startQuestTimer("teleportback3", 1000, npc, player, false);
				for (int i = 0; i < Arena3_Doors.length; i++)
				{
					DoorTable.getInstance().getDoor(Arena3_Doors[i]).closeMe();
				}
				cancelQuestTimers("checkArena3p1");
				cancelQuestTimers("checkArena3p2");
				cancelQuestTimers("checkArena3p3");
				cancelQuestTimers("checkArena3p4");
				cancelQuestTimers("checkArena3Size");
			}
		}
		if (event.equalsIgnoreCase("teleportback3"))
		{
			for (int i = 0; i < Arena3Players.size(); i++)
			{
				Arena3Players.get(i).teleToLocation(BackLoc[0], BackLoc[1], BackLoc[2]);
				if (Arena3Players.get(i).getQuestState(getName()).get("points") == null)
				{
					Arena3Players.get(i).getQuestState(getName()).set("points", String.valueOf("5"));
					Arena3Players.get(i).setCurrentCp(Arena3Players.get(i).getMaxCp());
					Arena3Players.get(i).setCurrentMp(Arena3Players.get(i).getMaxMp());
					Arena3Players.get(i).setCurrentHp(Arena3Players.get(i).getMaxHp());
					Arena3Players.get(i).setInstanceId(0);
				}
				else
				{
					Arena3Players.get(i).getQuestState(getName()).set("points", String.valueOf(Integer.valueOf(Arena3Players.get(i).getQuestState(getName()).get("points")) + 5));
					Arena3Players.get(i).setCurrentCp(Arena3Players.get(i).getMaxCp());
					Arena3Players.get(i).setCurrentMp(Arena3Players.get(i).getMaxMp());
					Arena3Players.get(i).setCurrentHp(Arena3Players.get(i).getMaxHp());
					Arena3Players.get(i).setInstanceId(0);
				}
			}
			for (int i = 0; i < Dead3Players.size(); i++)
			{
				Dead3Players.get(i).teleToLocation(BackLoc[0], BackLoc[1], BackLoc[2]);
				Dead3Players.get(i).restoreExp(100);
				Dead3Players.get(i).doRevive();
				Dead3Players.get(i).setCurrentCp(Dead3Players.get(i).getMaxCp());
				Dead3Players.get(i).setCurrentMp(Dead3Players.get(i).getMaxMp());
				Dead3Players.get(i).setCurrentHp(Dead3Players.get(i).getMaxHp());
				Dead3Players.get(i).setInstanceId(0);
			}
			Dead3Players.clear();
			Arena3Players.clear();
			Arena3_Status = false;
		}
		/*
		 * Arena 4
		 */
		if (event.equalsIgnoreCase("open_doors4"))
		{
			for (int i = 0; i < Arena4_Doors.length; i++)
			{
				DoorTable.getInstance().getDoor(Arena4_Doors[i]).openMe();
			}
		}
		if (event.equalsIgnoreCase("checkArena4p1"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead4Players.equals(player))
				{
					if (Arena4Players.get(0).isDead())
					{
						Dead4Players.add(Arena4Players.get(0));
						Arena4Players.get(0).setInstanceId(0);
						Arena4Players.remove(0);
					}
					if (!Arena4Players.get(0).isOnline())
					{
						Dead4Players.add(Arena4Players.get(0));
						Arena4Players.get(0).setInstanceId(0);
						Arena4Players.remove(0);
					}
					if (Arena4Players.get(0).getInstanceId() != 4)
					{
						Dead4Players.add(Arena4Players.get(0));
						Arena4Players.get(0).setInstanceId(0);
						Arena4Players.remove(0);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena4p2"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead4Players.equals(player))
				{
					if (Arena4Players.get(1).isDead())
					{
						Dead4Players.add(Arena4Players.get(1));
						Arena4Players.get(1).setInstanceId(0);
						Arena4Players.remove(1);
					}
					if (!Arena4Players.get(1).isOnline())
					{
						Dead4Players.add(Arena4Players.get(1));
						Arena4Players.get(1).setInstanceId(0);
						Arena4Players.remove(1);
					}
					if (Arena4Players.get(1).getInstanceId() != 4)
					{
						Dead4Players.add(Arena4Players.get(1));
						Arena4Players.get(1).setInstanceId(0);
						Arena4Players.remove(1);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena4p3"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead4Players.equals(player))
				{
					if (Arena4Players.get(2).isDead())
					{
						Dead4Players.add(Arena4Players.get(2));
						Arena4Players.get(2).setInstanceId(0);
						Arena4Players.remove(2);
					}
					if (!Arena4Players.get(2).isOnline())
					{
						Dead4Players.add(Arena4Players.get(2));
						Arena4Players.get(2).setInstanceId(0);
						Arena4Players.remove(2);
					}
					if (Arena4Players.get(2).getInstanceId() != 4)
					{
						Dead4Players.add(Arena4Players.get(2));
						Arena4Players.get(2).setInstanceId(0);
						Arena4Players.remove(2);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena4p4"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead4Players.equals(player))
				{
					if (Arena4Players.get(3).isDead())
					{
						Dead4Players.add(Arena4Players.get(3));
						Arena4Players.get(3).setInstanceId(0);
						Arena4Players.remove(3);
					}
					if (!Arena4Players.get(3).isOnline())
					{
						Dead4Players.add(Arena4Players.get(3));
						Arena4Players.get(3).setInstanceId(0);
						Arena4Players.remove(3);
					}
					if (Arena4Players.get(3).getInstanceId() != 4)
					{
						Dead4Players.add(Arena4Players.get(3));
						Arena4Players.get(3).setInstanceId(0);
						Arena4Players.remove(3);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena4Size"))
		{
			if (Arena4Players.size() == 1)
			{
				Announcements.getInstance().announceToAll("Tournament Manager: In Arena 4 win a " + Arena3Players.get(0).getName() + " and get 5 points");
				startQuestTimer("teleportback4", 1000, npc, player, false);
				for (int i = 0; i < Arena4_Doors.length; i++)
				{
					DoorTable.getInstance().getDoor(Arena4_Doors[i]).closeMe();
				}
				cancelQuestTimers("checkArena4p1");
				cancelQuestTimers("checkArena4p2");
				cancelQuestTimers("checkArena4p3");
				cancelQuestTimers("checkArena4p4");
				cancelQuestTimers("checkArena4Size");
			}
		}
		if (event.equalsIgnoreCase("teleportback4"))
		{
			for (int i = 0; i < Arena4Players.size(); i++)
			{
				Arena4Players.get(i).teleToLocation(BackLoc[0], BackLoc[1], BackLoc[2]);
				if (Arena4Players.get(i).getQuestState(getName()).get("points") == null)
				{
					Arena4Players.get(i).getQuestState(getName()).set("points", String.valueOf("5"));
					Arena4Players.get(i).setCurrentCp(Arena4Players.get(i).getMaxCp());
					Arena4Players.get(i).setCurrentMp(Arena4Players.get(i).getMaxMp());
					Arena4Players.get(i).setCurrentHp(Arena4Players.get(i).getMaxHp());
					Arena4Players.get(i).setInstanceId(0);
				}
				else
				{
					Arena4Players.get(i).getQuestState(getName()).set("points", String.valueOf(Integer.valueOf(Arena4Players.get(i).getQuestState(getName()).get("points")) + 5));
					Arena4Players.get(i).setCurrentCp(Arena4Players.get(i).getMaxCp());
					Arena4Players.get(i).setCurrentMp(Arena4Players.get(i).getMaxMp());
					Arena4Players.get(i).setCurrentHp(Arena4Players.get(i).getMaxHp());
					Arena4Players.get(i).setInstanceId(0);
				}
			}
			for (int i = 0; i < Dead4Players.size(); i++)
			{
				Dead4Players.get(i).teleToLocation(BackLoc[0], BackLoc[1], BackLoc[2]);
				Dead4Players.get(i).restoreExp(100);
				Dead4Players.get(i).doRevive();
				Dead4Players.get(i).setCurrentCp(Dead4Players.get(i).getMaxCp());
				Dead4Players.get(i).setCurrentMp(Dead4Players.get(i).getMaxMp());
				Dead4Players.get(i).setCurrentHp(Dead4Players.get(i).getMaxHp());
				Dead4Players.get(i).setInstanceId(0);
			}
			Dead4Players.clear();
			Arena4Players.clear();
			Arena4_Status = false;
		}
		/*
		 * Arena 5
		 */
		if (event.equalsIgnoreCase("open_doors5"))
		{
			for (int i = 0; i < Arena5_Doors.length; i++)
			{
				DoorTable.getInstance().getDoor(Arena5_Doors[i]).openMe();
			}
		}
		if (event.equalsIgnoreCase("checkArena5p1"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead5Players.equals(player))
				{
					if (Arena5Players.get(0).isDead())
					{
						Dead5Players.add(Arena5Players.get(0));
						Arena5Players.get(0).setInstanceId(0);
						Arena5Players.remove(0);
					}
					if (!Arena5Players.get(0).isOnline())
					{
						Dead5Players.add(Arena5Players.get(0));
						Arena5Players.get(0).setInstanceId(0);
						Arena5Players.remove(0);
					}
					if (Arena5Players.get(0).getInstanceId() != 5)
					{
						Dead5Players.add(Arena5Players.get(0));
						Arena5Players.get(0).setInstanceId(0);
						Arena5Players.remove(0);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena5p2"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead5Players.equals(player))
				{
					if (Arena5Players.get(1).isDead())
					{
						Dead5Players.add(Arena5Players.get(1));
						Arena5Players.get(1).setInstanceId(0);
						Arena5Players.remove(1);
					}
					if (!Arena5Players.get(1).isOnline())
					{
						Dead5Players.add(Arena5Players.get(1));
						Arena5Players.get(1).setInstanceId(0);
						Arena5Players.remove(1);
					}
					if (Arena5Players.get(1).getInstanceId() != 5)
					{
						Dead5Players.add(Arena5Players.get(1));
						Arena5Players.get(1).setInstanceId(0);
						Arena5Players.remove(1);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena5p3"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead5Players.equals(player))
				{
					if (Arena5Players.get(2).isDead())
					{
						Dead5Players.add(Arena5Players.get(2));
						Arena5Players.get(2).setInstanceId(0);
						Arena5Players.remove(2);
					}
					if (!Arena5Players.get(2).isOnline())
					{
						Dead5Players.add(Arena5Players.get(2));
						Arena5Players.get(2).setInstanceId(0);
						Arena5Players.remove(2);
					}
					if (Arena5Players.get(2).getInstanceId() != 5)
					{
						Dead5Players.add(Arena5Players.get(2));
						Arena5Players.get(2).setInstanceId(0);
						Arena5Players.remove(2);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena5p4"))
		{
			if (player.getParty() != null)
			{
				player.getParty().removePartyMember(player);
			}
			try
			{
				if (!Dead5Players.equals(player))
				{
					if (Arena5Players.get(3).isDead())
					{
						Dead5Players.add(Arena5Players.get(3));
						Arena5Players.get(3).setInstanceId(0);
						Arena5Players.remove(3);
					}
					if (!Arena5Players.get(3).isOnline())
					{
						Dead5Players.add(Arena5Players.get(3));
						Arena5Players.get(3).setInstanceId(0);
						Arena5Players.remove(3);
					}
					if (Arena5Players.get(3).getInstanceId() != 5)
					{
						Dead5Players.add(Arena5Players.get(3));
						Arena5Players.get(3).setInstanceId(0);
						Arena5Players.remove(3);
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		if (event.equalsIgnoreCase("checkArena5Size"))
		{
			if (Arena5Players.size() == 1)
			{
				Announcements.getInstance().announceToAll("Tournament Manager: In Arena 5 win a " + Arena5Players.get(0).getName() + " and get 5 points");
				startQuestTimer("teleportback5", 1000, npc, player, false);
				for (int i = 0; i < Arena5_Doors.length; i++)
				{
					DoorTable.getInstance().getDoor(Arena5_Doors[i]).closeMe();
				}
				cancelQuestTimers("checkArena5p1");
				cancelQuestTimers("checkArena5p2");
				cancelQuestTimers("checkArena5p3");
				cancelQuestTimers("checkArena5p4");
				cancelQuestTimers("checkArena5Size");
			}
		}
		if (event.equalsIgnoreCase("teleportback5"))
		{
			for (int i = 0; i < Arena5Players.size(); i++)
			{
				Arena5Players.get(i).teleToLocation(BackLoc[0], BackLoc[1], BackLoc[2]);
				if (Arena5Players.get(i).getQuestState(getName()).get("points") == null)
				{
					Arena5Players.get(i).getQuestState(getName()).set("points", String.valueOf("5"));
					Arena5Players.get(i).setCurrentCp(Arena5Players.get(i).getMaxCp());
					Arena5Players.get(i).setCurrentMp(Arena5Players.get(i).getMaxMp());
					Arena5Players.get(i).setCurrentHp(Arena5Players.get(i).getMaxHp());
					Arena5Players.get(i).setInstanceId(0);
				}
				else
				{
					Arena5Players.get(i).getQuestState(getName()).set("points", String.valueOf(Integer.valueOf(Arena5Players.get(i).getQuestState(getName()).get("points")) + 5));
					Arena5Players.get(i).setCurrentCp(Arena5Players.get(i).getMaxCp());
					Arena5Players.get(i).setCurrentMp(Arena5Players.get(i).getMaxMp());
					Arena5Players.get(i).setCurrentHp(Arena5Players.get(i).getMaxHp());
					Arena5Players.get(i).setInstanceId(0);
				}
			}
			for (int i = 0; i < Dead5Players.size(); i++)
			{
				Dead5Players.get(i).teleToLocation(BackLoc[0], BackLoc[1], BackLoc[2]);
				Dead5Players.get(i).restoreExp(100);
				Dead5Players.get(i).doRevive();
				Dead5Players.get(i).setCurrentCp(Dead5Players.get(i).getMaxCp());
				Dead5Players.get(i).setCurrentMp(Dead5Players.get(i).getMaxMp());
				Dead5Players.get(i).setCurrentHp(Dead5Players.get(i).getMaxHp());
				Dead5Players.get(i).setInstanceId(0);
			}
			Dead5Players.clear();
			Arena5Players.clear();
			Arena5_Status = false;
		}
		/*
		 * View player points
		 */
		if (event.equalsIgnoreCase("view_points"))
		{
			QuestState st = player.getQuestState(qn);
			if (st == null)
			{
				st = newQuestState(player);
			}
			if (st.getState() != State.STARTED)
			{
				st.setState(State.STARTED);
			}
			if (st.get("points") != null)
			{
				htmltext = "<html><body>Arena Manager:<BR>";
				htmltext += "You have " + st.get("points") + " points.";
				htmltext += "</body></html>";
			}
			else
			{
				htmltext = "<html><body>Arena Manager:<BR>";
				htmltext += "You don't have points.";
				htmltext += "</body></html>";
			}
		}
		/*
		 * View fight rules
		 */
		if (event.equalsIgnoreCase("rules"))
		{
			htmltext = "32377-rules.htm";
		}
		/*
		 * Start Fight
		 */
		if (event.equalsIgnoreCase("fight"))
		{
			if (!Arena1_Status)
			{
				Arena1_Status = true;
				for (int i = 0; i < registeredPlayers.size(); i++)
				{
					registeredPlayers.get(i).teleToLocation(Arena1_Locs[i][0], Arena1_Locs[i][1], Arena1_Locs[i][2]);
					registeredPlayers.get(i).stopAllEffects();
					L2Character l2Character = registeredPlayers.get(i);
					L2Character registeredTarget = l2Character;
					registeredPlayers.get(i).setCurrentHp(registeredTarget.getMaxHp());
					registeredPlayers.get(i).setCurrentMp(registeredTarget.getMaxMp());
					registeredPlayers.get(i).setCurrentCp(registeredTarget.getMaxCp());
					Arena1Players.add(registeredPlayers.get(i));
					Arena1Players.get(i).setInstanceId(1);
				}
				registeredPlayers.clear();
				for (int i = 0; i < Arena1_Doors.length; i++)
				{
					DoorTable.getInstance().getDoor(Arena1_Doors[i]).openMe();
				}
				switch (Arena1Players.size())
				{
					case 1:
						startQuestTimer("checkArena1p1", 1000, npc, player, true);
						startQuestTimer("checkArena1Size", 1000, npc, player, true);
						break;
					case 2:
						startQuestTimer("checkArena1p1", 1000, npc, player, true);
						startQuestTimer("checkArena1p2", 1000, npc, player, true);
						startQuestTimer("checkArena1Size", 1000, npc, player, true);
						break;
					case 3:
						startQuestTimer("checkArena1p1", 1000, npc, player, true);
						startQuestTimer("checkArena1p2", 1000, npc, player, true);
						startQuestTimer("checkArena1p3", 1000, npc, player, true);
						startQuestTimer("checkArena1Size", 1000, npc, player, true);
						break;
					case 4:
						startQuestTimer("checkArena1p1", 1000, npc, player, true);
						startQuestTimer("checkArena1p2", 1000, npc, player, true);
						startQuestTimer("checkArena1p3", 1000, npc, player, true);
						startQuestTimer("checkArena1p4", 1000, npc, player, true);
						startQuestTimer("checkArena1Size", 1000, npc, player, true);
						break;
					default:
						break;
				}
			}
			else
			{
				if (!Arena2_Status)
				{
					Arena2_Status = true;
					for (int i = 0; i < registeredPlayers.size(); i++)
					{
						registeredPlayers.get(i).teleToLocation(Arena2_Locs[i][0], Arena2_Locs[i][1], Arena2_Locs[i][2]);
						registeredPlayers.get(i).stopAllEffects();
						L2Character registeredTarget = registeredPlayers.get(i);
						registeredPlayers.get(i).setCurrentHp(registeredTarget.getMaxHp());
						registeredPlayers.get(i).setCurrentMp(registeredTarget.getMaxMp());
						registeredPlayers.get(i).setCurrentCp(registeredTarget.getMaxCp());
						Arena2Players.add(registeredPlayers.get(i));
						Arena2Players.get(i).setInstanceId(2);
					}
					registeredPlayers.clear();
					for (int i = 0; i < Arena2_Doors.length; i++)
					{
						DoorTable.getInstance().getDoor(Arena2_Doors[i]).openMe();
					}
					switch (Arena2Players.size())
					{
						case 1:
							startQuestTimer("checkArena2p1", 1000, npc, player, true);
							startQuestTimer("checkArena2Size", 1000, npc, player, true);
							break;
						case 2:
							startQuestTimer("checkArena2p1", 1000, npc, player, true);
							startQuestTimer("checkArena2p2", 1000, npc, player, true);
							startQuestTimer("checkArena2Size", 1000, npc, player, true);
							break;
						case 3:
							startQuestTimer("checkArena2p1", 1000, npc, player, true);
							startQuestTimer("checkArena2p2", 1000, npc, player, true);
							startQuestTimer("checkArena2p3", 1000, npc, player, true);
							startQuestTimer("checkArena2Size", 1000, npc, player, true);
							break;
						case 4:
							startQuestTimer("checkArena2p1", 1000, npc, player, true);
							startQuestTimer("checkArena2p2", 1000, npc, player, true);
							startQuestTimer("checkArena2p3", 1000, npc, player, true);
							startQuestTimer("checkArena2p4", 1000, npc, player, true);
							startQuestTimer("checkArena2Size", 1000, npc, player, true);
							break;
						default:
							break;
					}
				}
				else
				{
					if (!Arena3_Status)
					{
						Arena3_Status = true;
						for (int i = 0; i < registeredPlayers.size(); i++)
						{
							registeredPlayers.get(i).teleToLocation(Arena3_Locs[i][0], Arena3_Locs[i][1], Arena3_Locs[i][2]);
							registeredPlayers.get(i).stopAllEffects();
							L2Character registeredTarget = registeredPlayers.get(i);
							registeredPlayers.get(i).setCurrentHp(registeredTarget.getMaxHp());
							registeredPlayers.get(i).setCurrentMp(registeredTarget.getMaxMp());
							registeredPlayers.get(i).setCurrentCp(registeredTarget.getMaxCp());
							Arena3Players.add(registeredPlayers.get(i));
							Arena3Players.get(i).setInstanceId(3);
						}
						registeredPlayers.clear();
						for (int i = 0; i < Arena3_Doors.length; i++)
						{
							DoorTable.getInstance().getDoor(Arena3_Doors[i]).openMe();
						}
						switch (Arena3Players.size())
						{
							case 1:
								startQuestTimer("checkArena3p1", 1000, npc, player, true);
								startQuestTimer("checkArena3Size", 1000, npc, player, true);
								break;
							case 2:
								startQuestTimer("checkArena3p1", 1000, npc, player, true);
								startQuestTimer("checkArena3p2", 1000, npc, player, true);
								startQuestTimer("checkArena3Size", 1000, npc, player, true);
								break;
							case 3:
								startQuestTimer("checkArena3p1", 1000, npc, player, true);
								startQuestTimer("checkArena3p2", 1000, npc, player, true);
								startQuestTimer("checkArena3p3", 1000, npc, player, true);
								startQuestTimer("checkArena3Size", 1000, npc, player, true);
								break;
							case 4:
								startQuestTimer("checkArena3p1", 1000, npc, player, true);
								startQuestTimer("checkArena3p2", 1000, npc, player, true);
								startQuestTimer("checkArena3p3", 1000, npc, player, true);
								startQuestTimer("checkArena3p4", 1000, npc, player, true);
								startQuestTimer("checkArena3Size", 1000, npc, player, true);
								break;
							default:
								break;
						}
					}
					else
					{
						if (!Arena4_Status)
						{
							Arena4_Status = true;
							for (int i = 0; i < registeredPlayers.size(); i++)
							{
								registeredPlayers.get(i).teleToLocation(Arena4_Locs[i][0], Arena4_Locs[i][1], Arena4_Locs[i][2]);
								registeredPlayers.get(i).stopAllEffects();
								L2Character registeredTarget = registeredPlayers.get(i);
								registeredPlayers.get(i).setCurrentHp(registeredTarget.getMaxHp());
								registeredPlayers.get(i).setCurrentMp(registeredTarget.getMaxMp());
								registeredPlayers.get(i).setCurrentCp(registeredTarget.getMaxCp());
								Arena4Players.add(registeredPlayers.get(i));
								Arena4Players.get(i).setInstanceId(4);
							}
							registeredPlayers.clear();
							for (int i = 0; i < Arena4_Doors.length; i++)
							{
								DoorTable.getInstance().getDoor(Arena4_Doors[i]).openMe();
							}
							switch (Arena4Players.size())
							{
								case 1:
									startQuestTimer("checkArena4p1", 1000, npc, player, true);
									startQuestTimer("checkArena4Size", 1000, npc, player, true);
									break;
								case 2:
									startQuestTimer("checkArena4p1", 1000, npc, player, true);
									startQuestTimer("checkArena4p2", 1000, npc, player, true);
									startQuestTimer("checkArena4Size", 1000, npc, player, true);
									break;
								case 3:
									startQuestTimer("checkArena4p1", 1000, npc, player, true);
									startQuestTimer("checkArena4p2", 1000, npc, player, true);
									startQuestTimer("checkArena4p3", 1000, npc, player, true);
									startQuestTimer("checkArena4Size", 1000, npc, player, true);
									break;
								case 4:
									startQuestTimer("checkArena4p1", 1000, npc, player, true);
									startQuestTimer("checkArena4p2", 1000, npc, player, true);
									startQuestTimer("checkArena4p3", 1000, npc, player, true);
									startQuestTimer("checkArena4p4", 1000, npc, player, true);
									startQuestTimer("checkArena4Size", 1000, npc, player, true);
									break;
								default:
									break;
							}
						}
						else
						{
							if (!Arena5_Status)
							{
								Arena5_Status = true;
								for (int i = 0; i < registeredPlayers.size(); i++)
								{
									registeredPlayers.get(i).teleToLocation(Arena5_Locs[i][0], Arena5_Locs[i][1], Arena5_Locs[i][2]);
									registeredPlayers.get(i).stopAllEffects();
									L2Character registeredTarget = registeredPlayers.get(i);
									registeredPlayers.get(i).setCurrentHp(registeredTarget.getMaxHp());
									registeredPlayers.get(i).setCurrentMp(registeredTarget.getMaxMp());
									registeredPlayers.get(i).setCurrentCp(registeredTarget.getMaxCp());
									Arena5Players.add(registeredPlayers.get(i));
									Arena5Players.get(i).setInstanceId(5);
								}
								registeredPlayers.clear();
								for (int i = 0; i < Arena5_Doors.length; i++)
								{
									DoorTable.getInstance().getDoor(Arena5_Doors[i]).openMe();
								}
								switch (Arena5Players.size())
								{
									case 1:
										startQuestTimer("checkArena5p1", 1000, npc, player, true);
										startQuestTimer("checkArena5Size", 1000, npc, player, true);
										break;
									case 2:
										startQuestTimer("checkArena5p1", 1000, npc, player, true);
										startQuestTimer("checkArena5p2", 1000, npc, player, true);
										startQuestTimer("checkArena5Size", 1000, npc, player, true);
										break;
									case 3:
										startQuestTimer("checkArena5p1", 1000, npc, player, true);
										startQuestTimer("checkArena5p2", 1000, npc, player, true);
										startQuestTimer("checkArena5p3", 1000, npc, player, true);
										startQuestTimer("checkArena5Size", 1000, npc, player, true);
										break;
									case 4:
										startQuestTimer("checkArena5p1", 1000, npc, player, true);
										startQuestTimer("checkArena5p2", 1000, npc, player, true);
										startQuestTimer("checkArena5p3", 1000, npc, player, true);
										startQuestTimer("checkArena5p4", 1000, npc, player, true);
										startQuestTimer("checkArena5Size", 1000, npc, player, true);
										break;
									default:
										break;
								}
							}
							else
							{
								htmltext = "Error please report to Developer";
							}
						}
					}
				}
			}
		}
		return htmltext;
	}
	
	public UndergroundTournament(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(Kuram);
		addFirstTalkId(Kuram);
		addTalkId(Kuram);
		scheduleTimer();
	}
	
	public static void main(String[] args)
	{
		// now call the constructor (starts up the)
		new UndergroundTournament(-1, qn, "fantasy");
		_log.warning("Fantasy System: Underground Tournament loaded ...");
	}
}
