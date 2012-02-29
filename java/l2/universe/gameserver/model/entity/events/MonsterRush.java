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
package l2.universe.gameserver.model.entity.events;

import java.util.logging.Logger;

import javolution.util.FastSet;
import l2.universe.ExternalConfig;
import l2.universe.gameserver.Announcements;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.datatables.NpcTable;
import l2.universe.gameserver.model.L2Spawn;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.templates.chars.L2NpcTemplate;

public class MonsterRush
{
	protected static final Logger _log = Logger.getLogger(MonsterRush.class.getName());
	
	enum Status
	{
		INACTIVE, STARTED, REGISTER, TELEPORT, REWARDING
	}
	
	public static FastSet<L2PcInstance> _participants = new FastSet<L2PcInstance>();
	public static FastSet<L2Npc> _monsters = new FastSet<L2Npc>();
	public static Status _status = Status.INACTIVE;
	public static int[] _miniIntervals = { 80000, 160000, 240000, 360000 };
	
	public static int[] _CoordsX = { 17918, 19769, 19157, 17900 };
	public static int[] _CoordsY = { 146271, 145728, 143732, 145674 };
	public static int[] _CoordsZ = { -3110, -3120, -3066, -3103 };
	
	public static int[][] _wave1Mons = { { 50000, 50001 }, { 1, 10 } };
	public static int[][] _wave2Mons = { { 50002, 50003, 50004 }, { 10, 10, 10 } };
	public static int[][] _wave3Mons = { { 50005, 50006, 50007, 50008 }, { 5, 5, 5, 1 } };
	
	protected static L2Npc _lord = null;
	public static int _wave = 1;
	public static int X = 18864;
	public static int Y = 145216;
	public static int Z = -3132;
	
	public static int getParticipatingPlayers()
	{
		return _participants.size();
	}
	
	protected static void monWave(final int _waveNum)
	{
		if (_status == Status.INACTIVE)
			return;
		
		if (_waveNum == 2)
			Announcements.getInstance().announceToAll("First wave has ended. Prepare for second wave!");
		else if (_waveNum == 3)
			Announcements.getInstance().announceToAll("Second wave has ended. Prepare for last wave!");
		
		L2Npc mobas = null;
		int[][] wave = _wave1Mons;
		if (_waveNum == 2)
			wave = _wave2Mons;
		else if (_waveNum == 3)
			wave = _wave3Mons;
		
		for (int i = 0; i <= wave[0].length - 1; i++)
			for (int a = 1; a <= wave[1][i]; a++)
				for (int r = 0; r <= _CoordsX.length - 1; r++)
				{
					mobas = addSpawn(wave[0][i], _CoordsX[r], _CoordsY[r], _CoordsZ[r]);
					mobas.getKnownList().addKnownObject(_lord);
					_monsters.add(mobas);
				}
		for (L2Npc monster : _monsters)
		{
			((L2Attackable) monster).addDamageHate(_lord, 9000, 9000);
			monster.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, _lord, null);
		}
		
		for (int i = 0; i <= _miniIntervals.length - 1; i++)
			ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
			{
				public void run()
				{
					miniWave(_waveNum);
				}
			}, _miniIntervals[i]);
		
	}
	
	public static void abortEvent()
	{
		_status = Status.INACTIVE;
		synchronized (_participants)
		{
			for (L2PcInstance player : _participants)
			{
				player.teleToLocation(X, Y, Z, true);
				player.setTeam(0);
				player.setIsInMonsterRush(false);
			}
		}
		for (L2Npc monster : _monsters)
		{
			monster.onDecay();
		}
		_monsters.clear();
		_participants.clear();
		Announcements.getInstance().announceToAll("Monster Rush: Event was aborted.");
	}
	
	public static void endByLordDeath()
	{
		endAndReward();
	}
	
	protected static void endAndReward()
	{
		if (_status == Status.INACTIVE)
			return;
		
		_status = Status.REWARDING;
		for (L2Npc monster : _monsters)
		{
			monster.onDecay();
		}
		_monsters.clear();
		if (L2World.getInstance().findObject(_lord.getObjectId()) == null)
		{
			Announcements.getInstance().announceToAll("Monster Rush: Lord was not protected!");
			Announcements.getInstance().announceToAll("Monster Rush: Teleporting players back to town.");
			Announcements.getInstance().announceToAll("Monster Rush: Event has ended.");
			synchronized (_participants)
			{
				for (L2PcInstance player : _participants)
				{
					player.teleToLocation(X, Y, Z, true);
					player.setTeam(0);
					player.setIsInMonsterRush(false);
				}
			}
		}
		else
		{
			Announcements.getInstance().announceToAll("Monster Rush: Lord was protected!");
			Announcements.getInstance().announceToAll("Monster Rush: Teleporting players back to town.");
			Announcements.getInstance().announceToAll("Monster Rush: Event has ended.");
			_lord.deleteMe();
			synchronized (_participants)
			{
				for (L2PcInstance player : _participants)
				{
					player.sendMessage("Darion: Thanks you for help.");
					player.sendMessage("Darion: For your courage i will give you a reward, check your inventory.");
					
					player.getInventory().addItem("MonsterRush Event", ExternalConfig.MRUSH_REWARD_ITEM, ExternalConfig.MRUSH_REWARD_AMOUNT, player, null);
					player.getInventory().updateDatabase();
					player.teleToLocation(X, Y, Z, true);
					player.setTeam(0);
					player.setIsInMonsterRush(false);
				}
			}
		}
		_participants.clear();
		_status = Status.INACTIVE;
	}
	
	protected static void miniWave(int _waveNum)
	{
		if (_status == Status.INACTIVE)
			return;
		
		int[][] wave = _wave1Mons;
		if (_waveNum == 2)
			wave = _wave2Mons;
		else if (_waveNum == 3)
			wave = _wave3Mons;
		
		L2Npc mobas = null;
		for (int i = 0; i <= wave[0].length - 1; i++)
			for (int a = 1; a <= Math.round(wave[1][i] * 0.65); a++)
				for (int r = 0; r <= _CoordsX.length - 1; r++)
				{
					mobas = addSpawn(wave[0][i], _CoordsX[r], _CoordsY[r], _CoordsZ[r]);
					_monsters.add(mobas);
				}
		
		for (L2Npc monster : _monsters)
		{
			((L2Attackable) monster).addDamageHate(_lord, 7000, 7000);
			monster.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, _lord, null);
		}
	}
	
	public static L2Npc addSpawn(int npcId, int x, int y, int z)
	{
		L2Npc result = null;
		try
		{
			L2NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
			if (template != null)
			{
				L2Spawn spawn = new L2Spawn(template);
				spawn.setInstanceId(0);
				spawn.setHeading(1);
				spawn.setLocx(x);
				spawn.setLocy(y);
				spawn.setLocz(z);
				spawn.stopRespawn();
				result = spawn.spawnOne(true);
				
				return result;
			}
		}
		catch (Exception e1){}
		
		return null;
	}
	
	public static void doUnReg(L2PcInstance player)
	{
		if (_status == Status.REGISTER)
		{
			if (_participants.contains(player))
			{
				_participants.remove(player);
				player.sendMessage("You have succesfully unregistered from Monster Rush event.");
			}
			else
				player.sendMessage("You aren't registered in this event.");
		}
		else
			player.sendMessage("Event is inactive.");
	}
	
	public static void doReg(L2PcInstance player)
	{
		if (_status == Status.REGISTER)
		{
			if (!_participants.contains(player))
			{
				_participants.add(player);
				player.sendMessage("You have succesfully registered to Monster Rush event.");
			}
			else
				player.sendMessage("You have already registered for this event.");
		}
		else
			player.sendMessage("You cannot register now.");
	}
	
	public static void startRegister()
	{
		_status = Status.REGISTER;
		_participants.clear();
		Announcements.getInstance().announceToAll("Monster Rush: Registration is open.");
		Announcements.getInstance().announceToAll("For registration write .mrjoin command.");
	}
	
	public static void startEvent()
	{
		_status = Status.STARTED;
		Announcements.getInstance().announceToAll("Registration is over. Teleporting players to town center in 20 seconds.");
		ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
		{
			public void run()
			{
				beginTeleport();
			}
		}, 20000);
	}
	
	/**
	 * Is Monster Rush Event inactive?<br><br>
	 *
	 * @return boolean: true if event is inactive(waiting for next event cycle), otherwise false<br>
	 */
	public static boolean isInactive()
	{
		boolean isInactive;
		
		synchronized (_status)
		{
			isInactive = _status == Status.INACTIVE;
		}
		
		return isInactive;
	}
	
	/**
	 * Is Monster Rush Event in participation?<br><br>
	 *
	 * @return boolean: true if event is in participation progress, otherwise false<br>
	 */
	public static boolean isParticipating()
	{
		boolean isParticipating;
		
		synchronized (_status)
		{
			isParticipating = _status == Status.REGISTER;
		}
		
		return isParticipating;
	}
	
	/**
	 * Is Monster Rush Event started?<br><br>
	 *
	 * @return boolean: true if event is started, otherwise false<br>
	 */
	public static boolean isStarted()
	{
		boolean isStarted;
		
		synchronized (_status)
		{
			isStarted = _status == Status.STARTED;
		}
		
		return isStarted;
	}
	
	/**
	 * Send a SystemMessage to all participated players<br>
	 * 1. Send the message to all players of team number one<br>
	 * 2. Send the message to all players of team number two<br><br>
	 *
	 * @param message as String<br>
	 */
	public static void sysMsgToAllParticipants(String message)
	{
		for (final L2PcInstance playerInstance : _participants)
		{
			if (playerInstance != null)
				playerInstance.sendMessage(message);
		}
	}
	
	protected static void beginTeleport()
	{
		_status = Status.TELEPORT;
		_lord = addSpawn(40030, X, Y, Z);
		_lord.setIsParalyzed(true);
		synchronized (_participants)
		{
			for (L2PcInstance player : _participants)
			{
				if (player.isInOlympiadMode() || TvTEvent.isPlayerParticipant(player.getObjectId()))
				{
					_participants.remove(player);
					return;
				}
				player.teleToLocation(X, Y, Z, true);
				player.setTeam(2);
				player.setIsInMonsterRush(true);
			}
			Announcements.getInstance().announceToAll("Teleportation done. First monster wave will approach town in 1 minute!");
			ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
			{
				public void run()
				{
					monWave(1);
				}
			}, 180000);
			ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
			{
				public void run()
				{
					monWave(2);
				}
			}, 360000);
			ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
			{
				public void run()
				{
					monWave(3);
				}
			}, 720000);
			ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
			{
				public void run()
				{
					endAndReward();
				}
			}, 1220000);
		}
	}
}
