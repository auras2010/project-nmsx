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

import java.util.Calendar;

import javolution.util.FastMap;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.instancemanager.InstanceManager;
import l2.universe.gameserver.instancemanager.InstanceManager.InstanceWorld;
import l2.universe.gameserver.model.L2Party;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.Instance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

/**
 * 
 * @author L2 DC, Synerge
 */


public class Kamaloka extends Quest
{
	/*
	 * Starttime (for duration calculation)
	 */
	private static long INSTANCEENTERTIME = 0;
	
	public Kamaloka(int questId, String name, String descr)
	{
		super(questId, name, descr);
	}

	public static void main(String[] args)
	{
		// now call the constructor (starts up the)
		new Kamaloka(-1, "qn", "instance");
	}
	
	class teleCoord
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

	public class KamaParam
	{
		public String		qn			= "";
		public String		Template	= "";
		public int 			PartySize	= 6;
		public int			Npc			= 0;
		public int			Mob			= 0;
		public int			Minion		= 0;
		public int			dataIndex	= 0;
		public int			ClientId	= 0;
		public teleCoord	enterCoord	= null;
		public teleCoord	retCoord	= null;
		public int			Level		= 0;
	}

	protected class KamaPlayer
	{
		public int		playerId    = 0;
		public int		instance	= 0;
		public long		timeStamp	= 0;
		public int		points		= 0;
		public int		count		= 0;
		public int		reward		= 0;
		public boolean  rewarded	= false;
	}
	
	protected class KamaWorld extends InstanceWorld
	{
		public FastMap<String, KamaPlayer> KamalokaPlayers = new FastMap<String, KamaPlayer>();
		public KamaParam param = new KamaParam();

		public KamaWorld()
		{
		}
	}

	protected boolean isPartySizeOk(L2PcInstance player, KamaParam param)
	{
		return player.getParty().getMemberCount() <= param.PartySize;
	}
	
	protected boolean isWithinLevel(L2PcInstance player, KamaParam param)
	{
		if ((player.getLevel() > param.Level+5) || (player.getLevel() < param.Level-5)) 
			return false;
		return true;
	}
	
	protected boolean checkPrimaryConditions(L2PcInstance player, KamaParam param)
	{
		if (player.getParty() == null)
		{
			player.sendPacket(SystemMessage.sendString("You are not currently in a party, so you cannot enter."));
			return false;
		}
		else if (!isPartySizeOk(player, param))
		{
			player.sendPacket(SystemMessage.sendString("You cannot enter due to the party having exceeded the limit."));
			return false;
		}
		else if (!isWithinLevel(player, param))
		{
			player.sendPacket(SystemMessage.sendString("You do not meet the level requirement."));
			return false;
		}
		return true;
	}
	
	protected boolean checkNewInstanceConditions(L2PcInstance player, KamaParam param)
	{		
		final L2Party party = player.getParty();
		if (party == null)
			return false;
		
		if (System.currentTimeMillis() < InstanceManager.getInstance().getInstanceTime( player.getObjectId(), param.ClientId))
		{
			player.sendPacket(SystemMessage.sendString("You can't enter in kamaloka."));
			return false;
		}
		
		if (!party.isLeader(player))
		{
			player.sendPacket(SystemMessage.sendString("Only a party leader can try to enter."));
			return false;
		}
		
		for (L2PcInstance partyMember : party.getPartyMembers())
		{
			if (!isWithinLevel(partyMember, param))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(2101);
				sm.addCharName(partyMember);
				player.sendPacket(sm);
				sm = null;
				return false;
			}
			else if (!partyMember.isInsideRadius(player, 500, false, false))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(2101);
				sm.addCharName(partyMember);
				player.sendPacket(sm);
				sm = null;
				return false;
			}
			else if (System.currentTimeMillis() < InstanceManager.getInstance().getInstanceTime(partyMember.getObjectId(), param.ClientId))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(2100);
				sm.addCharName(partyMember);
				player.sendPacket(sm);
				sm = null;
				return false;
			}
		}
		return true;
	}
	
	private void teleportplayer(L2PcInstance player, L2Npc entryNpc, teleCoord teleto, KamaParam param)
	{
		final int instanceId = teleto.instanceId;
		Instance instanceObj = InstanceManager.getInstance().getInstance(instanceId);
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		player.setInstanceId(instanceId);
		player.teleToLocation(teleto.x, teleto.y, teleto.z);
		if (param.retCoord != null)
			instanceObj.setReturnTeleport(param.retCoord.x, param.retCoord.y, param.retCoord.z);
		return;
	}

	protected void exitInstance(L2PcInstance player, teleCoord tele)
	{
		// Calculate Duration of this run
		INSTANCEENTERTIME = (System.currentTimeMillis() - INSTANCEENTERTIME) / 1000;
		final int h = (int) (INSTANCEENTERTIME / 3600);
		final int m = (int) ((INSTANCEENTERTIME % 3600) / 60);
		final int s = (int) (INSTANCEENTERTIME % 60);
		
		// Char Statistics
		player.sendMessage("Your time: " + h + "h. " + m + "min. und " + s + "sec.");
		
		player.setInstanceId(0);
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		player.teleToLocation(tele.x, tele.y, tele.z);
		player.setKamalokaId(0);
	}
	
	public String onAdvEventTo(String event, L2Npc npc, L2PcInstance player)
	{
		return event;
	}
	
	private int findInstance(L2PcInstance player)
	{
		int instanceId = player.getInstanceId();
		final L2Party party = player.getParty();
		if (party != null)
		{
			if (instanceId == 0)
			{
				// player outside Kamaloka, search if was inside before
				for (L2PcInstance partyMember : party.getPartyMembers())
				{
					if (partyMember.getInstanceId() != 0)
					{
						instanceId = partyMember.getInstanceId();
						break;
					}
				}
			}
		}
		return instanceId;
	}
	
	public String onTalkTo(L2Npc npc, L2PcInstance player, KamaParam param)
	{
		if (!checkPrimaryConditions(player, param))
			return "";
		
		int instanceId = findInstance(player);	
		// instanceId is 0 for first time or instanceId of a party member
		String playerName = player.getName();
		KamaWorld world = null;
		if (instanceId == 0)
		{
			// brand new instance
			if (!checkNewInstanceConditions(player, param))
				return "";
			
			QuestState st = player.getQuestState(param.qn);
			if (st == null)
				st = newQuestState(player);
			
			instanceId = InstanceManager.getInstance().createDynamicInstance(param.Template);

			world = new KamaWorld();
			world.instanceId = instanceId;
			world.templateId = param.ClientId;
			world.param = param;
			
			// set entertime for duration calculation
			INSTANCEENTERTIME = System.currentTimeMillis();
			
			L2Party party = player.getParty();
			for (L2PcInstance partyMember : party.getPartyMembers())
			{
				KamaPlayer kp = new KamaPlayer();
				kp.instance = instanceId;
				kp.playerId = partyMember.getObjectId();
				world.KamalokaPlayers.put(partyMember.getName(), kp);
				partyMember.removeActiveBuffForKama();
				teleCoord teleto = param.enterCoord;
				teleto.instanceId = instanceId;
				teleportplayer(partyMember, npc, teleto, param);
				partyMember.setKamalokaId(instanceId);
			}
			
			InstanceManager.getInstance().addWorld(world);

			_log.info("Started " + param.Template + " Instance: " + instanceId + " created by player: " + player.getName());
		}
		else
		{
			// party already in kamaloka
			InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(instanceId);
			KamaPlayer kp = null;
			if (tmpworld instanceof KamaWorld)
			{
				world = (KamaWorld) tmpworld;
				kp = world.KamalokaPlayers.get(playerName);
				
				// now if kp is not null we are sure that players was in party at kamaloka start time
				if (kp == null)
				{
					player.sendPacket(SystemMessage.sendString("You can't join a Party during Kamaloka."));	
					_log.info(param.qn + ": onTalk - player not found in world, id: " + instanceId);
					return "";
				}
				
				final Instance instanceObj = InstanceManager.getInstance().getInstance(instanceId);
				if (instanceObj.getCountPlayers() >= param.PartySize)
				{
					player.sendPacket(SystemMessage.getSystemMessage(2102));
					return "";
				}
				
				teleCoord teleto = param.enterCoord;
				teleto.instanceId = instanceId;
				teleportplayer(player, npc, teleto, param);
			}
			else
			{
				player.sendPacket(SystemMessage.sendString("Your Party Members are in another Instance."));	
				_log.info(param.qn + ": onTalk - world not found, id: " + instanceId);
				return "";
			}
		}
		return "";
	}

	public String onKillTo(L2Npc npc, L2PcInstance player, boolean isPet, KamaParam param)
	{
		if (npc.getNpcId() == param.Mob)
		{
			InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
			if (tmpworld instanceof KamaWorld)
			{
				// Set reset time to 6:30 of the next day
				Calendar reenter = Calendar.getInstance(); 
				reenter.set(Calendar.HOUR_OF_DAY, 6);
				reenter.set(Calendar.MINUTE, 30);
				
				long reenterDelay = reenter.getTimeInMillis();
				if (reenterDelay < System.currentTimeMillis())
					reenterDelay += 86400000;
				
				KamaWorld world = (KamaWorld) tmpworld;
				for (KamaPlayer partyMember : world.KamalokaPlayers.values())
					InstanceManager.getInstance().setInstanceTime(partyMember.playerId, param.ClientId, reenterDelay);
			    Instance instanceObj = InstanceManager.getInstance().getInstance(world.instanceId);
			    instanceObj.setDuration(300000);
			}
		}
	    return "";
	}
}
