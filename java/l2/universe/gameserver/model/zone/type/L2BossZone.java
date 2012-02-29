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
package l2.universe.gameserver.model.zone.type;

import java.util.Map;

import javolution.util.FastMap;

import l2.universe.gameserver.GameServer;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.datatables.MapRegionTable;
import l2.universe.gameserver.instancemanager.GrandBossManager;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.L2Playable;
import l2.universe.gameserver.model.actor.L2Summon;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.zone.L2ZoneType;
import l2.universe.gameserver.network.serverpackets.L2GameServerPacket;
import l2.universe.util.L2FastList;

/**
 * @author DaRkRaGe
 */
public class L2BossZone extends L2ZoneType
{
	private int _timeInvade;
	private boolean _enabled = true; // default value, unless overridden by xml...
	
	// track the times that players got disconnected. Players are allowed
	// to log back into the zone as long as their log-out was within _timeInvade
	// time...
	// <player objectId, expiration time in milliseconds>
	private FastMap<Integer, Long> _playerAllowedReEntryTimes;
	
	// track the players admitted to the zone who should be allowed back in 
	// after reboot/server downtime (outside of their control), within 30 
	// of server restart
	private L2FastList<Integer> _playersAllowed;
	private int[] _oustLoc = { 0, 0, 0 };
	protected L2FastList<L2Character> _raidList = new L2FastList<L2Character>();
	
	public L2BossZone(int id)
	{
		super(id);
		_playerAllowedReEntryTimes = new FastMap<Integer, Long>();
		_playersAllowed = new L2FastList<Integer>();
		_oustLoc = new int[3];
		GrandBossManager.getInstance().addZone(this);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("InvadeTime"))
		{
			_timeInvade = Integer.parseInt(value);
		}
		else if (name.equals("EnabledByDefault"))
		{
			_enabled = Boolean.parseBoolean(value);
		}
		else if (name.equals("oustX"))
		{
			_oustLoc[0] = Integer.parseInt(value);
		}
		else if (name.equals("oustY"))
		{
			_oustLoc[1] = Integer.parseInt(value);
		}
		else if (name.equals("oustZ"))
		{
			_oustLoc[2] = Integer.parseInt(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}
		
	@Override
	/**
	 * Boss zones have special behaviors for player characters. Players are
	 * automatically teleported out when the attempt to enter these zones,
	 * except if the time at which they enter the zone is prior to the entry
	 * expiration time set for that player. Entry expiration times are set by
	 * any one of the following: 1) A player logs out while in a zone
	 * (Expiration gets set to logoutTime + _timeInvade) 2) An external source
	 * (such as a quest or AI of NPC) set up the player for entry.
	 * 
	 * There exists one more case in which the player will be allowed to enter.
	 * That is if the server recently rebooted (boot-up time more recent than
	 * currentTime - _timeInvade) AND the player was in the zone prior to reboot.
	 */
	protected void onEnter(L2Character character)
	{
		if (_enabled)
		{
			if (character instanceof L2PcInstance)
			{
				final L2PcInstance player = (L2PcInstance) character;
				
				player.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, true);
				if (player.isGM())
					return;
					
				// If player has been (previously) cleared by npc/ai for entry and the zone is
				// set to receive players (aka not waiting for boss to respawn)
				if (_playersAllowed.contains(player.getObjectId()))
				{
					// Get the information about this player's last logout-exit from
					// this zone.
					final Long expirationTime = _playerAllowedReEntryTimes.get(player.getObjectId());
					
					// with legal entries, do nothing.
					if (expirationTime == null) // legal null expirationTime entries
					{
						long serverStartTime = GameServer.dateTimeServerStarted.getTimeInMillis();
						if ((serverStartTime > (System.currentTimeMillis() - _timeInvade)))
							return;
					}
					else
					{
						// legal non-null logoutTime entries
						_playerAllowedReEntryTimes.remove(player.getObjectId());
						if (expirationTime.longValue() > System.currentTimeMillis())
							return;
					}
					_playersAllowed.remove(_playersAllowed.indexOf(player.getObjectId()));
				}
				
				// teleport out all players who attempt "illegal" (re-)entry
				if (_oustLoc[0] != 0 && _oustLoc[1] != 0 && _oustLoc[2] != 0)
					player.teleToLocation(_oustLoc[0], _oustLoc[1], _oustLoc[2]);
				else
					player.teleToLocation(MapRegionTable.TeleportWhereType.Town);
			}
			else if (character instanceof L2Summon)
			{
				final L2PcInstance player = ((L2Summon) character).getOwner();
				if (player != null)
				{
					if (_playersAllowed.contains(player.getObjectId()) || player.isGM())
						return;
					
					// Remove summon and teleport out owner who attempt "illegal" (re-)entry
					if (_oustLoc[0] != 0 && _oustLoc[1] != 0 && _oustLoc[2] != 0)
						player.teleToLocation(_oustLoc[0], _oustLoc[1], _oustLoc[2]);
					else
						player.teleToLocation(MapRegionTable.TeleportWhereType.Town);
				}
				((L2Summon) character).unSummon(player);
			}
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (_enabled)
		{
			if (character instanceof L2Playable)
			{
				if (character instanceof L2PcInstance)
				{
					L2PcInstance player = (L2PcInstance) character;
					player.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, false);
					if (player.isGM())
						return;
					
					// if the player just got disconnected/logged out, store the dc time so that
					// decisions can be made later about allowing or not the player to log into the zone
					if (!player.isOnline() && _playersAllowed.contains(player.getObjectId()))
					{
						// mark the time that the player left the zone
						_playerAllowedReEntryTimes.put(player.getObjectId(), System.currentTimeMillis() + _timeInvade);
					}
					else
					{
						if (_playersAllowed.contains(player.getObjectId()))
							_playersAllowed.remove(_playersAllowed.indexOf(player.getObjectId()));
						_playerAllowedReEntryTimes.remove(player.getObjectId());
					}
				}
				
				if (getCharactersInside() != null && !getCharactersInside().isEmpty())
				{
					_raidList.clear();
					int count = 0;
					for (L2Character obj : getCharactersInside().values())
					{
						if (obj instanceof L2Playable)
							count++;
						else if (obj instanceof L2Attackable && obj.isRaid())
							_raidList.add(obj);
					}
					
					// If inside zone isnt any player, force all boss instance return to its spawn points
					if (count == 0 && !_raidList.isEmpty())
					{
						for (int i = 0; i < _raidList.size(); i++)
						{
							L2Attackable raid = (L2Attackable) _raidList.get(i);
							if (raid == null || raid.getSpawn() == null || raid.isDead())
								continue;
							
							if (!raid.isInsideRadius(raid.getSpawn().getLocx(), raid.getSpawn().getLocy(), 150, false))
								raid.returnHome();
						}
					}
				}
			}
		}
		
		// Synerge - Add support for general zones for noExit function, checking also the current zone ID
		if (character instanceof L2Attackable && !character.isDead())
		{
			if (!character.canExitFromZone() && character.mustRemainInZone() == getId())
			{
				L2Attackable npc = (L2Attackable) character;
				if (character.isZoneForcedReturnTeleport() && npc.getSpawn() != null)
				{
					npc.clearAggroList();
					npc.teleToLocation(npc.getSpawn().getLocx(), npc.getSpawn().getLocy(), npc.getSpawn().getLocz(), false);
				}
				else
				{
					character.abortAttack();
					character.abortCast();
					character.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
					npc.returnHome();
				}
			}
		}
	}
	
	public void setZoneEnabled(boolean flag)
	{
		if (_enabled != flag)
			oustAllPlayers();
		
		_enabled = flag;
	}
	
	public int getTimeInvade()
	{
		return _timeInvade;
	}
	
	public void setAllowedPlayers(L2FastList<Integer> players)
	{
		if (players != null)
			_playersAllowed = players;
	}
	
	public L2FastList<Integer> getAllowedPlayers()
	{
		return _playersAllowed;
	}
	
	public boolean isPlayerAllowed(L2PcInstance player)
	{
		if (player.isGM())
			return true;
		
		if (_playersAllowed.contains(player.getObjectId()))
			return true;
		
		if (_oustLoc[0] != 0 && _oustLoc[1] != 0 && _oustLoc[2] != 0)
			player.teleToLocation(_oustLoc[0], _oustLoc[1], _oustLoc[2]);
		else
			player.teleToLocation(MapRegionTable.TeleportWhereType.Town);
		return false;
	}
	
	/**
	 * Some GrandBosses send all players in zone to a specific part of the zone, rather than just
	 * removing them all. If this is the case, this command should be used. If this is no the case,
	 * then use oustAllPlayers().
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void movePlayersTo(int x, int y, int z)
	{
		if (_characterList.isEmpty())
			return;
		
		for (L2Character character : _characterList.values())
		{
			if (character instanceof L2PcInstance)
			{
				L2PcInstance player = (L2PcInstance) character;
				if (player.isOnline())
					player.teleToLocation(x, y, z);
			}
		}
	}
	
	/**
	 * Occasionally, all players need to be sent out of the zone (for example,
	 * if the players are just running around without fighting for too long, or
	 * if all players die, etc). This call sends all online players to town and
	 * marks offline players to be teleported (by clearing their relog
	 * expiration times) when they log back in (no real need for off-line
	 * teleport).
	 */
	public void oustAllPlayers()
	{
		if (_characterList == null || _characterList.isEmpty())
			return;
		
		for (L2Character character : _characterList.values())
		{
			if (!(character instanceof L2PcInstance))
				continue;

			L2PcInstance player = (L2PcInstance) character;
			if (player.isOnline())
			{
				if (_oustLoc[0] != 0 && _oustLoc[1] != 0 && _oustLoc[2] != 0)
					player.teleToLocation(_oustLoc[0], _oustLoc[1], _oustLoc[2]);
				else
					player.teleToLocation(MapRegionTable.TeleportWhereType.Town);
			}
		}
		_playerAllowedReEntryTimes.clear();
		_playersAllowed.clear();
	}
	
	/**
	 * This function is to be used by external sources, such as quests and AI
	 * in order to allow a player for entry into the zone for some time.  Naturally
	 * if the player does not enter within the allowed time, he/she will be
	 * teleported out again... 
	 * @param player: reference to the player we wish to allow 
	 * @param durationInSec: amount of time in seconds during which entry is valid.
	 */
	public void allowPlayerEntry(L2PcInstance player, int durationInSec)
	{
		if (player.isGM())
			return;

		if (!_playersAllowed.contains(player.getObjectId()))
			_playersAllowed.add(player.getObjectId());
		_playerAllowedReEntryTimes.put(player.getObjectId(), System.currentTimeMillis() + durationInSec * 1000);
	}
	
	public void removePlayer(L2PcInstance player)
	{
		if (player.isGM())
			return;

		_playersAllowed.remove(Integer.valueOf(player.getObjectId()));
		_playerAllowedReEntryTimes.remove(player.getObjectId());
	}
	
	@Override
	public void onDieInside(L2Character character)
	{
	}
	
	@Override
	public void onReviveInside(L2Character character)
	{
	}
	
	@Override
	public void broadcastPacket(L2GameServerPacket packet)
	{
		if (_characterList == null || _characterList.isEmpty())
			return;
		
		for (L2Character character : _characterList.values())
		{
			if (!(character instanceof L2PcInstance))
				continue;
			
			L2PcInstance player = (L2PcInstance) character;
			if (player.isOnline())
				player.sendPacket(packet);
		}
	}
    public void broadcastMovie(int id, int instanceId)
	{
		if (_characterList == null || _characterList.isEmpty())
			return;
		for (L2Character character : _characterList.values())
		{
			if (character == null)
				continue;
			if (character instanceof L2PcInstance)
			{
				L2PcInstance player = (L2PcInstance) character;
				if (player.isOnline() && player.getInstanceId() == instanceId)
					player.showMovie(id);
			}
		}
	}
	public void updateKnownList(L2Npc npc)
	{
		if (_characterList == null || _characterList.isEmpty())
			return;
		
		Map<Integer, L2PcInstance> npcKnownPlayers = npc.getKnownList().getKnownPlayers();
		for (L2Character character : _characterList.values())
		{
			if (!(character instanceof L2PcInstance))
				continue;

			L2PcInstance player = (L2PcInstance) character;
			if (player.isOnline())
				npcKnownPlayers.put(player.getObjectId(), player);
		}
	}

	@Override
	public FastMap<Integer, L2Character> getCharactersInside()
	{
		return _characterList;
	}
}