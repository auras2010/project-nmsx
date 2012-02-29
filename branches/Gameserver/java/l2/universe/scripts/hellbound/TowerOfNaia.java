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
package l2.universe.scripts.hellbound;

import java.util.List;

import javolution.util.FastList;

import l2.universe.gameserver.instancemanager.InstanceManager;
import l2.universe.gameserver.model.L2CommandChannel;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.Instance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.NpcSay;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.util.Util;
import l2.universe.util.Rnd;

public class TowerOfNaia extends Quest
{
	private static boolean DEBUG = false;
	
	private static final int Manager = 32313;
	private static final int CUBIC_TO_BELETH = 32376;
	private static final int ITEM_TO_ENTER = 9701;
	
	private static final int MIN_PLAYERS = 2;
	private static final int MAX_PLAYERS = 45;
	private static final int MIN_LEVEL = 75;
	
	private static final int[] SPAWN_XYZ = {22792, 243865, 11088};
	private static final int[] OUT_XYZ = {10587, 244658, -2012};
	
	//Darion's Faithful Servant, Darion's Executioner, Darion's Enforcer
	private static final int[] DarionGuards = {22405, 22343, 22342};
	private static final int Darion = 25603;
	
	// 0, 1, 2 are _epidos mirrors, 3 is the real _epidos
	private static final int[] _epidos = {25612, 25611, 25610, 25609};
	
	private static final int[] _mobs = 
	//room1, room2, room3, room4, room5, room6, room7, room8
	{25605, 25606, 25607, 25608, 25613, 22412, 22413, 22411, // room mobs
	25604, 25609, 25610, 25611, 25612, 29119,}; // other mobs
	
	private static final int[] _doors = {18250101, 18250102, 18250103, 18250002,
	18250003, 18250004, 18250005, 18250006, 18250007, 18250008, 18250009,
	18250010, 18250011, 18250013, 18250014, 18250015, 18250017, 18250018,
	18250019, 18250021, 18250022, 18250023, 18250024, 18250025};
	
	private static int instanceId = 0;
	private static byte lastMinionsSpawnedAt = 100;
	private static int roomKills = 0;
	private static List<L2PcInstance> _playersInside = new FastList<L2PcInstance>();
	private static List<L2Npc> _spawnedMinions = new FastList<L2Npc>();
	
	public TowerOfNaia(int questId, String name, String descr)
	{
		super(questId, name, descr);

		addStartNpc(Manager);
		addTalkId(Manager);
		for (int mob : _mobs)
			addKillId(mob);
		for (int mob : _epidos)
			addKillId(mob);
		addKillId(Darion);
		addAttackId(Darion);
		
	}
	
	private boolean checkConditions(L2PcInstance player)
	{
		if (DEBUG)
			return true;
		
		if (player.getInventory().getItemsByItemId(ITEM_TO_ENTER) == null)
		{
			player.sendMessage("<html><body>You dont have required item.</body></html>");
			return false;
		}
		
		if (player.getParty() == null)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_IN_PARTY_CANT_ENTER));
			return false;
		}
		
		L2CommandChannel channel = player.getParty().getCommandChannel();
		
		if (channel == null)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_IN_COMMAND_CHANNEL_CANT_ENTER));
			return false;
		}
		else if (channel.getChannelLeader() != player)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ONLY_PARTY_LEADER_CAN_ENTER));
			return false;
		}
		else if (channel.getMemberCount() < MIN_PLAYERS || channel.getMemberCount() > MAX_PLAYERS)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PARTY_EXCEEDED_THE_LIMIT_CANT_ENTER));
			return false;
		}
		
		for (L2PcInstance channelMember : channel.getMembers())
		{
			
			if (channelMember == null)
				continue;
			
			if (channelMember.getLevel() < MIN_LEVEL)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_LEVEL_REQUIREMENT_NOT_SUFFICIENT);
				sm.addPcName(channelMember);
				channel.broadcastToChannelMembers(sm);
				return false;
			}
			if (!Util.checkIfInRange(1000, player, channelMember, true))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_LOCATION_THAT_CANNOT_BE_ENTERED);
				sm.addPcName(channelMember);
				channel.broadcastToChannelMembers(sm);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String onAdvEvent (String event, L2Npc npc, L2PcInstance player)
	{	
		if (event.equalsIgnoreCase("cleanup"))
		{
			for (L2PcInstance member : _playersInside)
			{
				if (member != null)
				{
					member.setInstanceId(0);
					member.teleToLocation(OUT_XYZ[0], OUT_XYZ[1], OUT_XYZ[2], true);
					if (member.getPet() != null)
					{
						member.getPet().setInstanceId(0);
						member.getPet().teleToLocation(OUT_XYZ[0], OUT_XYZ[1], OUT_XYZ[2], true);
					}
				}
			}
			
			_playersInside.clear();
			
		}
		if (event.equalsIgnoreCase("move_players"))
		{
			for (L2PcInstance member : _playersInside)
			{
				if (member != null && member.getInstanceId() == instanceId)
				{
					member.teleToLocation(-46112, 246138, -9127, true);
					if (member.getPet() != null)
					{
						member.getPet().teleToLocation(-46112, 246138, -9127, true);
					}
				}
			}
		}
		if (event.equalsIgnoreCase("spawn_beleth_cubic"))
			addSpawn(CUBIC_TO_BELETH, -44957, 246712, -14179, 0, false, 300000).setInstanceId(instanceId);
		
		return null;
	}
	
	@Override
	public String onAttack (L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (npc != null && npc.getNpcId() == Darion)
		{
			int maxHp = npc.getMaxHp();
			double nowHp = npc.getStatus().getCurrentHp();
			
			if (nowHp < maxHp * 0.9 && lastMinionsSpawnedAt > 90)
			{
				spawnGuards(npc);
				lastMinionsSpawnedAt -= 10;
			}
			else if (nowHp < maxHp * 0.8 && lastMinionsSpawnedAt > 80)
			{
				spawnGuards(npc);
				lastMinionsSpawnedAt -= 10;
			}
			else if (nowHp < maxHp * 0.7 && lastMinionsSpawnedAt > 70)
			{
				spawnGuards(npc);
				lastMinionsSpawnedAt -= 10;
			}
			else if (nowHp < maxHp * 0.6 && lastMinionsSpawnedAt > 60)
			{
				spawnGuards(npc);
				lastMinionsSpawnedAt -= 10;
			}
			else if (nowHp < maxHp * 0.5 && lastMinionsSpawnedAt > 50)
			{
				spawnGuards(npc);
				lastMinionsSpawnedAt -= 10;
			}
			else if (nowHp < maxHp * 0.4 && lastMinionsSpawnedAt > 40)
			{
				spawnGuards(npc);
				lastMinionsSpawnedAt -= 10;
			}
			else if (nowHp < maxHp * 0.3 && lastMinionsSpawnedAt > 30)
			{
				spawnGuards(npc);
				lastMinionsSpawnedAt -= 10;
			}
			else if (nowHp < maxHp * 0.2 && lastMinionsSpawnedAt > 20)
			{
				spawnGuards(npc);
				lastMinionsSpawnedAt -= 10;
			}
			else if (nowHp < maxHp * 0.1 && lastMinionsSpawnedAt > 10)
			{
				spawnGuards(npc);
				lastMinionsSpawnedAt -= 10;
			}
		}
		return "";
	}
	
	@Override
	public String onTalk (L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
	
		if (!checkConditions(player))
			return htmltext;
		else
		{
			try 
			{
				instanceId = InstanceManager.getInstance().createDynamicInstance("TowerOfNaia.xml");
				Instance instance = InstanceManager.getInstance().getInstance(instanceId);
				instance.setAllowSummon(false);
				instance.setPvPInstance(false);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return "I/O Error Please report this error to administrator!";
			}
			
			player.destroyItemByItemId("Naia Tower", ITEM_TO_ENTER, 1, player, true);
			
			if (DEBUG)
			{
				if (player.getParty() != null && player.getParty().getCommandChannel() != null)
				{
					for (L2PcInstance member : player.getParty().getCommandChannel().getMembers())
					{
						member.setInstanceId(instanceId);
						member.teleToLocation(SPAWN_XYZ[0] + Rnd.get(50), SPAWN_XYZ[1] + Rnd.get(50), SPAWN_XYZ[2]);
						_playersInside.add(member);
					}
				}
				else if (player.getParty() != null)
				{
					for (L2PcInstance member : player.getParty().getPartyMembers())
					{
						member.setInstanceId(instanceId);
						member.teleToLocation(SPAWN_XYZ[0] + Rnd.get(50), SPAWN_XYZ[1] + Rnd.get(50), SPAWN_XYZ[2]);
						_playersInside.add(member);
					}
				}
				else
				{
					player.setInstanceId(instanceId);
					player.teleToLocation(SPAWN_XYZ[0] + Rnd.get(50), SPAWN_XYZ[1] + Rnd.get(50), SPAWN_XYZ[2]);
					_playersInside.add(player);
				}
			}
			else
			{
				for (L2PcInstance member : player.getParty().getCommandChannel().getMembers())
				{
					member.setInstanceId(instanceId);
					member.teleToLocation(SPAWN_XYZ[0] + Rnd.get(50), SPAWN_XYZ[1] + Rnd.get(50), SPAWN_XYZ[2]);
					_playersInside.add(member);
				}
			}
		}

		return htmltext;
	}

	@Override
	public String onKill (L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (npc.getNpcId() == Darion)
		{	
			for (L2Npc mob : _spawnedMinions)
				if (mob != null)
					mob.deleteMe();
			
			_spawnedMinions.clear();

			for (L2PcInstance member : _playersInside)
				if (member != null && member.getInstanceId() == instanceId)
					member.sendMessage("Darion was defeated succesfully your party will be moved into Tower of Naia in 60 sec");
			
			startQuestTimer("move_players", 30000, npc, killer);
		}
		if (npc.getNpcId() == _mobs[0])
		{
			roomKills++;
			if (DEBUG)
				System.out.println("Room1: " +roomKills);
			
			if (roomKills >= 17)
			{
				roomKills = 0;
				
				for (L2PcInstance member : _playersInside)
					if (member != null && member.getInstanceId() == instanceId)
						member.sendMessage("Room is clear door is now opening please hurry.");
						
				openDoor(_doors[5], _doors[6]);		
				closeDoor(_doors[3], _doors[4]);
			}
		}
		else if (npc.getNpcId() == _mobs[1])
		{
			roomKills++;
			if (DEBUG)
				System.out.println("Room2: " +roomKills);
			
			if (roomKills >= 17)
			{
				roomKills = 0;
				
				for (L2PcInstance member : _playersInside)
					if (member != null && member.getInstanceId() == instanceId)
						member.sendMessage("Room is clear door is now opening please hurry.");

				openDoor(_doors[7], _doors[8]);
				closeDoor(_doors[5], _doors[6]);
			}
		}
		else if (npc.getNpcId() == _mobs[2])
		{
			roomKills++;
			if (DEBUG)
				System.out.println("Room3: " +roomKills);
			
			if (roomKills >= 19)
			{
				roomKills = 0;
				
				for (L2PcInstance member : _playersInside)
					if (member != null && member.getInstanceId() == instanceId)
						member.sendMessage("Room is clear door is now opening please hurry.");

				openDoor(_doors[9], _doors[10]);
				closeDoor(_doors[7], _doors[8]);
			}
		}
		else if (npc.getNpcId() == _mobs[3])
		{
			roomKills++;
			if (DEBUG)
				System.out.println("Room4: " +roomKills);
			
			if (roomKills >= 15)
			{
				roomKills = 0;
				
				for (L2PcInstance member : _playersInside)
					if (member != null && member.getInstanceId() == instanceId)
						member.sendMessage("Room is clear door is now opening please hurry.");
					
				openDoor(_doors[11], _doors[12], _doors[13]);
				closeDoor(_doors[9], _doors[10]);
			}
		}
		else if (npc.getNpcId() == _mobs[4])
		{
			roomKills++;
			if (DEBUG)
				System.out.println("Room5: " +roomKills);
			
			if (roomKills >= 19)
			{
				roomKills = 0;
				
				for (L2PcInstance member : _playersInside)
					if (member != null && member.getInstanceId() == instanceId)
						member.sendMessage("Room is clear door is now opening please hurry.");

				openDoor(_doors[14], _doors[15], _doors[16]);
				closeDoor(_doors[11], _doors[12], _doors[13]);
			}
		}
		else if (npc.getNpcId() == _mobs[5])
		{
			roomKills++;
			if (DEBUG)
				System.out.println("Room6: " +roomKills);
			
			if (roomKills >= 16)
			{
				roomKills = 0;
				
				for (L2PcInstance member : _playersInside)
					if (member != null && member.getInstanceId() == instanceId)
						member.sendMessage("Room is clear door is now opening please hurry.");
				
				openDoor(_doors[17], _doors[18], _doors[19]);
				closeDoor(_doors[14], _doors[15], _doors[16]);
			}
		}
		else if (npc.getNpcId() == _mobs[6])
		{
			roomKills++;
			if (DEBUG)
				System.out.println("Room7: " +roomKills);
			
			if (roomKills >= 18)
			{
				roomKills = 0;
				
				for (L2PcInstance member : _playersInside)
					if (member != null && member.getInstanceId() == instanceId)
						member.sendMessage("Room is clear door is now opening please hurry.");

				openDoor(_doors[20], _doors[21]);
				closeDoor(_doors[17], _doors[18], _doors[19]);
			}
		}
		else if (npc.getNpcId() == _mobs[7])
		{
			roomKills++;
			if (DEBUG)
				System.out.println("Room8: " +roomKills);
			
			if (roomKills >= 18)
			{
				roomKills = 0;
				
				for (L2PcInstance member : _playersInside)
					if (member != null && member.getInstanceId() == instanceId)
						member.sendMessage("Room is clear door is now opening please hurry.");
						
				openDoor(_doors[22], _doors[23]);
				closeDoor(_doors[20], _doors[21]);
			}
		}
		else if (npc.getNpcId() == _epidos[0])
		{
			if (DEBUG)
				System.out.println("Boss1: " + _epidos[0]);
			
			for (L2PcInstance member : _playersInside)
				if (member != null && member.getInstanceId() == instanceId)
					member.sendMessage("Epidos's Mirror was defeated door is now opening please hurry.");
			
			openDoor(_doors[0]);
		}
		else if (npc.getNpcId() == _epidos[1])
		{
			if (DEBUG)
				System.out.println("Boss2: " + _epidos[1]);
			
			for (L2PcInstance member : _playersInside)
				if (member != null && member.getInstanceId() == instanceId)
					member.sendMessage("Epidos's Mirror was defeated door is now opening please hurry.");


			openDoor(_doors[1], _doors[16]);
			closeDoor(_doors[0]);  
		}
		else if (npc.getNpcId() == _epidos[2])
		{
			if (DEBUG)
				System.out.println("Boss3: " + _epidos[2]);
			
			for (L2PcInstance member : _playersInside)
				if (member != null && member.getInstanceId() == instanceId)
					member.sendMessage("Epidos's Mirror was defeated door is now opening please hurry.");


			openDoor(_doors[2]);
			closeDoor(_doors[1]);
			
		}
		else if (npc.getNpcId() == _epidos[3])
		{
			if (DEBUG)
				System.out.println("_epidos: " + _epidos[3]);
			
			for (L2PcInstance member : _playersInside)
				if (member != null && member.getInstanceId() == instanceId)
					member.sendMessage("Epidos was succesfully defeated!");
			
			startQuestTimer("spawn_beleth_cubic", 10000, npc, killer);
		}
		return null;
	}	

	private void spawnGuards(L2Npc npc) 
	{
		npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), "Minions help me!."));
		
		_spawnedMinions.add(addSpawn(DarionGuards[0],npc.getX() + Rnd.get(250),npc.getY() + Rnd.get(50),npc.getZ(),0,false,0,false, npc.getInstanceId()));
		_spawnedMinions.add(addSpawn(DarionGuards[0],npc.getX() + Rnd.get(250),npc.getY() + Rnd.get(50),npc.getZ(),0,false,0,false, npc.getInstanceId()));
		_spawnedMinions.add(addSpawn(DarionGuards[0],npc.getX() + Rnd.get(250),npc.getY() + Rnd.get(50),npc.getZ(),0,false,0,false, npc.getInstanceId()));
		_spawnedMinions.add(addSpawn(DarionGuards[0],npc.getX() + Rnd.get(250),npc.getY() + Rnd.get(50),npc.getZ(),0,false,0,false, npc.getInstanceId()));
		_spawnedMinions.add(addSpawn(DarionGuards[0],npc.getX() + Rnd.get(250),npc.getY() + Rnd.get(50),npc.getZ(),0,false,0,false, npc.getInstanceId()));
		
		_spawnedMinions.add(addSpawn(DarionGuards[1],npc.getX() + Rnd.get(250),npc.getY() + Rnd.get(50),npc.getZ(),0,false,0,false, npc.getInstanceId()));
		_spawnedMinions.add(addSpawn(DarionGuards[1],npc.getX() + Rnd.get(250),npc.getY() + Rnd.get(50),npc.getZ(),0,false,0,false, npc.getInstanceId()));
		_spawnedMinions.add(addSpawn(DarionGuards[1],npc.getX() + Rnd.get(250),npc.getY() + Rnd.get(50),npc.getZ(),0,false,0,false, npc.getInstanceId()));
		
		_spawnedMinions.add(addSpawn(DarionGuards[2],npc.getX() + Rnd.get(250),npc.getY() + Rnd.get(50),npc.getZ(),0,false,0,false, npc.getInstanceId()));
		_spawnedMinions.add(addSpawn(DarionGuards[2],npc.getX() + Rnd.get(250),npc.getY() + Rnd.get(50),npc.getZ(),0,false,0,false, npc.getInstanceId()));
		_spawnedMinions.add(addSpawn(DarionGuards[2],npc.getX() + Rnd.get(250),npc.getY() + Rnd.get(50),npc.getZ(),0,false,0,false, npc.getInstanceId()));
		_spawnedMinions.add(addSpawn(DarionGuards[2],npc.getX() + Rnd.get(250),npc.getY() + Rnd.get(50),npc.getZ(),0,false,0,false, npc.getInstanceId()));
	}
	
	private static void openDoor(int ... doorId)
	{
		for (int door : doorId)
			InstanceManager.getInstance().getInstance(instanceId).getDoor(door).openMe();
	}
	
	private static void closeDoor(int ... doorId)
	{
		for (int door : doorId)
			InstanceManager.getInstance().getInstance(instanceId).getDoor(door).closeMe();
	}
	
    public static void main(String[] args)
    {
    	new TowerOfNaia(-1,"TowerOfNaia","ai");
    }
}