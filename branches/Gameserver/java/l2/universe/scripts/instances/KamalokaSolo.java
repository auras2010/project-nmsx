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
package l2.universe.scripts.instances;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;

import javolution.util.FastMap;
import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.instancemanager.InstanceManager;
import l2.universe.gameserver.instancemanager.InstanceManager.InstanceWorld;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.L2Summon;
import l2.universe.gameserver.model.actor.instance.L2MonsterInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.Instance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.util.Rnd;

/**
 * 
 * @author L2 DC, Synerge
 */

public class KamalokaSolo extends Quest
{
	public KamalokaSolo(int questId, String name, String descr)
	{
		super(questId, name, descr);
	}
	public static void main(String[] args)
	{
		// now call the constructor (starts up the)
		new KamalokaSolo(-1, "qn", "instance");
	}

	// NPC
	protected static int	ENTRANCE		= 32484;
	protected static int	REWARDER		= 32485;

	// ITEMS
	private static int[]	HERBS			= { 8600, 8601, 8602, 8603, 8604, 8605 };
	private static int[]	BATTLEHERBS		= { 8606, 8607, 8608, 8609, 8610 };

	private static boolean	debug			= false;	// if true: no check to enter instance
	private static int		INSTANCE_TIME	= 1800000;	// 30 minutes, in millis 1800000
	private static int		COMBAT_TIME		= 1200000;	// 20 minutes, in millis 1200000
	private static int		REWARD_TIME		= 600000;	// 10 minutes, in millis 600000

	private static int		ClientId		= 46;

	private class teleCoord
	{
		int	instanceId;
		int	x;
		int	y;
		int	z;
	}

	public teleCoord newCoord(int x, int y, int z)
	{
		teleCoord tele = new teleCoord();
		tele.x = x;
		tele.y = y;
		tele.z = z;
		return tele;
	}

	protected class KamaPlayer
	{
		public int		instance	= 0;
		public long		timeStamp	= 0;
		public int		points		= 0;
		public int		count		= 0;
		public int		reward		= 0;
		public boolean	rewarded	= false;
	}

	public class KamaParam
	{
		public String		qn			= "KamalokaSolo";
		public teleCoord	enterCoord	= null;
		public teleCoord	rewPosition	= null;
		public int			minLev		= 0;
		public int			maxLev		= 0;
	}

	protected class KamaWorld extends InstanceWorld
	{
		public FastMap<String, KamaPlayer>	KamalokaPlayers	= new FastMap<String, KamaPlayer>();
		public KamaParam					param			= new KamaParam();

		public KamaWorld()
		{
		}
	}

	protected boolean checkConditions(L2PcInstance player)
	{
		if (debug)
			return true;

		if (player.getParty() != null)
		{
			player.sendPacket(SystemMessage.sendString("You are currently in a party, so you cannot enter."));
			return false;
		}
		
		if (System.currentTimeMillis() < InstanceManager.getInstance().getInstanceTime(player.getObjectId(), ClientId))
		{
			player.sendPacket(SystemMessage.sendString("You can't enter in kamaloka, yet."));
			return false;
		}
		return true;
	}

	private boolean isWithinLevel(L2PcInstance player, int minLev, int maxLev)
	{
		if (debug)
			return true;

		if (player.getLevel() > maxLev)
			return false;
		if (player.getLevel() < minLev)
			return false;
		return true;
	}

	private void teleportplayer(L2PcInstance player, L2Npc entryNpc, teleCoord teleto)
	{
		final int instanceId = teleto.instanceId;
		final Instance instanceObj = InstanceManager.getInstance().getInstance(instanceId);
		
		// Set return teleport based on player location
		instanceObj.setReturnTeleport(player.getX(), player.getY(), player.getZ());
		player.setInstanceId(instanceId);
		player.teleToLocation(teleto.x, teleto.y, teleto.z);
		
		final L2Summon pet = player.getPet();
		if (pet != null)
		{
			pet.setInstanceId(instanceId);
			pet.teleToLocation(teleto.x, teleto.y, teleto.z);
		}
		return;
	}

	protected int enterInstance(L2PcInstance player, L2Npc npc, String template, KamaParam param)
	{
		if (!checkConditions(player))
			return 0;
		
		if (!isWithinLevel(player, param.minLev, param.maxLev))
			return 0;

		final int instanceId = InstanceManager.getInstance().createDynamicInstance(template);
		Instance instanceObj = InstanceManager.getInstance().getInstance(instanceId);

		KamaWorld world = new KamaWorld();
		world.instanceId = instanceId;
		world.templateId = ClientId;

		instanceObj.setDuration(INSTANCE_TIME);
		final long instanceOver = INSTANCE_TIME + System.currentTimeMillis();
		KamaPlayer kp = new KamaPlayer();
		kp.instance = instanceId;
		kp.timeStamp = instanceOver;
		world.param = param;
		world.KamalokaPlayers.put(player.getName(), kp);
		InstanceManager.getInstance().addWorld(world);

		teleCoord teleto = param.enterCoord;
		teleto.instanceId = instanceId;
		teleportplayer(player, npc, teleto);

		// Set reset time to 6:30 of the next day
		Calendar reenter = Calendar.getInstance(); 
		reenter.set(Calendar.HOUR_OF_DAY, 6);
		reenter.set(Calendar.MINUTE, 30);
		
		long reenterDelay = reenter.getTimeInMillis();
		if (reenterDelay < System.currentTimeMillis())
			reenterDelay += 86400000;
		
		InstanceManager.getInstance().setInstanceTime(player.getObjectId(), ClientId, reenterDelay);
		player.setKamalokaId(instanceId);
		startQuestTimer("time", COMBAT_TIME, null, player);

		_log.info("Started " + template + " Instance: " + instanceId + " created by player: " + player.getName());

		return instanceId;
	}

	protected void exitInstance(L2PcInstance player, teleCoord tele)
	{
		player.setInstanceId(0);
		player.teleToLocation(tele.x, tele.y, tele.z);
		player.setKamalokaId(0);
		final L2Summon pet = player.getPet();
		if (pet != null)
		{
			pet.setInstanceId(0);
			pet.teleToLocation(tele.x, tele.y, tele.z);
		}
	}

	public String onAdvEventTo(String event, L2Npc npc, L2PcInstance player, String qn, int[] REW1, int[] REW2)
	{		
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return null;
		
		int instanceId = player.getInstanceId();
		final String playerName = player.getName();

		if (instanceId == 0) // player is temporary outside the instance
			instanceId = player.getKamalokaId();

		Instance instanceObj;
		KamaWorld world;
		teleCoord rewPosition = null;
		if (InstanceManager.getInstance().instanceExist(instanceId))
		{
			instanceObj = InstanceManager.getInstance().getInstance(instanceId);
			world = (KamaWorld) InstanceManager.getInstance().getWorld(instanceId);
			if (world == null)
			{
				_log.info(qn + ": onAdvance - world not found, Ev:" + event + " Player:" + playerName + " Instance:" + instanceId);
				player.setKamalokaId(0);
				return "";
			}
			
			rewPosition = world.param.rewPosition;
		}
		else
		{
			_log.info(qn + ": onAdvance - instance not found, Ev:" + event + " Player:" + playerName + " Instance:"	+ instanceId);
			player.setKamalokaId(0);
			return "";
		}
		
		if (event.equals("time"))
		{
			if (!player.isOnline())
				return null;

			instanceObj.setDuration(REWARD_TIME);
			instanceObj.removeNpcs();
			addSpawn(REWARDER, rewPosition.x, rewPosition.y, rewPosition.z, 0, false, 0, false, instanceId);
			if (!world.KamalokaPlayers.containsKey(playerName))
			{
				_log.info(qn + ": Time - player not found");
				return "";
			}
			
			KamaPlayer kp = world.KamalokaPlayers.get(playerName);
			if (kp == null)
				return null;

			if (kp.count < 10)
				kp.reward = 1;
			else
			{
				kp.reward = (kp.points / kp.count) + 1;
				final int reward = kp.reward;
				final int count = kp.count;
				Connection con = null;
				try
				{
					int KamaCode = world.param.minLev * 100 + world.param.maxLev;
					con = L2DatabaseFactory.getInstance().getConnection();
					PreparedStatement insertion = con.prepareStatement("INSERT INTO kamaloka_results (char_name,Level,Grade,Count) VALUES (?,?,?,?)");
					insertion.setString(1, playerName);
					insertion.setInt(2, KamaCode);
					insertion.setInt(3, reward);
					insertion.setInt(4, count);
					insertion.executeUpdate();
					insertion.close();
				}
				catch (SQLException e)
				{
					_log.log(Level.SEVERE, "Error while inserting Kamaloka data:", e);
				}
				finally
				{
					L2DatabaseFactory.close(con);
				}
			}
		}
		else if (event.equals("Reward"))
		{
			KamaPlayer kp = world.KamalokaPlayers.get(playerName);
			if (kp != null)
			{
				if (!kp.rewarded)
				{
					kp.rewarded = true;
					final int r = kp.reward - 1;

					st.giveItems(REW1[r * 2], REW1[r * 2 + 1]);
					st.giveItems(REW2[r * 2], REW2[r * 2 + 1]);

					String fullPath = "data/html/Kamaloka/" + "1.htm";
					return st.showHtmlFile(fullPath).replace("%kamaloka%", qn);
				}
			}
		}
		else if (event.equals("Exit"))
		{
			instanceObj.removePlayers();
			player.setKamalokaId(0);
			InstanceManager.getInstance().destroyInstance(instanceId);
		}
		
		return null;
	}

	public String onEnterTo(L2Npc npc, L2PcInstance player, KamaParam param)
	{
		//_log.info("onEnterTo " + param.qn + " Player: " + player.getName() + " Npc: " + npc.getName());

		final String playerName = player.getName();
		QuestState st = player.getQuestState(param.qn);
		if (st == null)
			st = newQuestState(player);

		int instanceId = player.getInstanceId();
		if (instanceId == 0)
			instanceId = player.getKamalokaId();
		
		final String template = param.qn + ".xml";
		if (instanceId == 0)
		{
			if (enterInstance(player, npc, template, param) == 0)
			{
				//_log.info("Not Entered " + template + " Instance: " + instanceId + " Player: " + player.getName());
			}
		}
		else
		{
			if (!InstanceManager.getInstance().instanceExist(instanceId))
			{
				player.sendPacket(SystemMessage.sendString("Your current Kamaloka instance has expired!"));
				return "";
			}
			
			if (!InstanceManager.getInstance().getInstance(instanceId).getName().equalsIgnoreCase(param.qn))
			{
				player.sendPacket(SystemMessage.sendString("You are currently in another Kamaloka instance."));
				return "";
			}	
			
			final KamaWorld world = (KamaWorld) InstanceManager.getInstance().getWorld(instanceId);

            if (world == null) 
                throw new RuntimeException("Kamaloka world is null!!!");
            else if (world.param == null) 
                throw new RuntimeException("Kamaloka world.param is null!!!");
            else if (world.param.qn == null)
                throw new RuntimeException("Kamaloka world.param.qn is null!!!");

			if (!world.param.qn.isEmpty() && !world.param.qn.equals(param.qn))
			{
				player.sendPacket(SystemMessage.sendString("You are currently in another Kamaloka instance."));
				return "";
			}
			
			KamaPlayer kp = world.KamalokaPlayers.get(playerName);
			if (kp != null)
			{
				// Already entered
				long currentTime = System.currentTimeMillis();
				if (kp.timeStamp > currentTime)
				{
					final teleCoord tele = param.enterCoord;
					
					// Reenter into opened instance
					player.setInstanceId(instanceId);
					player.teleToLocation(tele.x, tele.y, tele.z);

					//_log.info("Continued " + template + " Instance: " + instanceId + " recovered by player: " + player.getName());
				}
				else
				{
					_log.info("Time Over for " + template + " Instance: " + instanceId + " player: " + player.getName());
					player.sendPacket(SystemMessage.sendString("It's too late, You can't reenter in Kamaloka."));
				}
			}
			else
			{
				_log.info("Not found player into world " + template + " Instance: " + instanceId + " by player: " + player.getName());
				if (enterInstance(player, npc, template, param) == 0)
				{
					//_log.info("Not Entered " + template + " Instance: " + instanceId + " Player: " + player.getName());
				}
			}
		}
		return "";
	}

	public String onTalkTo(L2Npc npc, L2PcInstance player, String qn)
	{		
		QuestState st = player.getQuestState(qn);
		if (st == null)
			st = newQuestState(player);
				
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (!(tmpworld instanceof KamaWorld))
		{
			_log.info(qn + ": onTalk - world not found");
			return "";
		}
		
		final String playerName = player.getName();
		final KamaWorld world = (KamaWorld) tmpworld;
		final KamaPlayer kp = world.KamalokaPlayers.get(playerName);
		if (kp == null)
		{
			_log.info(qn + ": onTalk - player not found");
			return "";
		}

		if (npc.getNpcId() == REWARDER)
		{
			if (!world.KamalokaPlayers.containsKey(playerName))
			{
				_log.info(qn + ": REWARDER - player not found");
				return "";
			}
			
			String msgReward = "0.htm";
			if (!kp.rewarded)
			{
				switch (kp.reward)
				{
					case 1:
						msgReward = "D.htm";
						break;
					case 2:
						msgReward = "C.htm";
						break;
					case 3:
						msgReward = "B.htm";
						break;
					case 4:
						msgReward = "A.htm";
						break;
					case 5:
						msgReward = "S.htm";
						break;
					default:
						msgReward = "1.htm";
						break;
				}
			}
			final String fullPath = "data/html/Kamaloka/" + msgReward;
			return st.showHtmlFile(fullPath).replace("%kamaloka%", qn);
		}
		return null;
	}

	public String onKillTo(L2Npc npc, L2PcInstance player, boolean isPet, String qn, int KANABION, int[] APPEAR, int[] REW)
	{		
		if (player == null)
			return "";
		
		QuestState st = player.getQuestState(qn);
		if (st == null)
			st = newQuestState(player);
		
		final InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (!(tmpworld instanceof KamaWorld))
		{
			_log.info(qn + ": onKill - world not found");
			return "";
		}
		
		final String playerName = player.getName();
		final KamaWorld world = (KamaWorld) tmpworld;

		if (!world.KamalokaPlayers.containsKey(playerName))
		{
			_log.info(qn + ": onKill - player not found");
			return "";
		}
		
		KamaPlayer kp = world.KamalokaPlayers.get(playerName);
		
		/* Synerge - Mobs are not active nor aggresive, but if you kill one of the group, the one that
		 * spawns later aggro on you instantly
		 */
		final int npcId = npc.getNpcId();
		if (npcId == KANABION)
		{
			kp.count += 1;
			if (Rnd.get(100) <= REW[0])
			{
				final L2Attackable newMob = (L2Attackable) addSpawn(APPEAR[0], npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false, npc.getInstanceId());
				if (newMob != null)
				{
					newMob.setRunning();
					newMob.addDamageHate(player, 0, 999);
					newMob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
				}
			}
			
			if (Rnd.get(100) <= REW[1])
				st.dropItem((L2MonsterInstance) npc, player, HERBS[Rnd.get(HERBS.length)], 1);
			if (Rnd.get(10000) <= REW[2])
				st.dropItem((L2MonsterInstance) npc, player, REW[3], 1);
		}
		else if (npcId == APPEAR[0])
		{
			kp.points += 1;
			if (Rnd.get(100) <= REW[4])
			{
				final L2Attackable newMob = (L2Attackable) addSpawn(APPEAR[Rnd.get(APPEAR.length)], npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false, npc.getInstanceId());
				if (newMob != null)
				{
					newMob.setRunning();
					newMob.addDamageHate(player, 0, 999);
					newMob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
				}
			}
			
			if (Rnd.get(100) <= REW[5])
				st.dropItem((L2MonsterInstance) npc, player, HERBS[Rnd.get(HERBS.length)], 1);
			else if (Rnd.get(100) <= REW[6])
				st.dropItem((L2MonsterInstance) npc, player, BATTLEHERBS[Rnd.get(BATTLEHERBS.length)], 1);
			if (Rnd.get(10000) <= REW[7])
				st.dropItem((L2MonsterInstance) npc, player, REW[8], 1);
		}
		else if (npcId == APPEAR[1])
		{
			kp.points += 2;
			if (Rnd.get(100) <= REW[9])
				st.dropItem((L2MonsterInstance) npc, player, HERBS[Rnd.get(HERBS.length)], 1);
			else if (Rnd.get(100) <= REW[10])
				st.dropItem((L2MonsterInstance) npc, player, BATTLEHERBS[Rnd.get(BATTLEHERBS.length)], 1);
			
			if (Rnd.get(100) <= REW[11])
			{
				final L2Attackable newMob = (L2Attackable) addSpawn(APPEAR[1], npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false, npc.getInstanceId());
				if (newMob != null)
				{
					newMob.setRunning();
					newMob.addDamageHate(player, 0, 999);
					newMob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
				}
			}
			
			if (Rnd.get(10000) <= REW[12])
				st.dropItem((L2MonsterInstance) npc, player, REW[13], 1);
		}
		return "";
	}
}
