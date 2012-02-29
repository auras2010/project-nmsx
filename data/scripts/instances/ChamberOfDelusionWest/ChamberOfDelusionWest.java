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
package instances.ChamberOfDelusionWest;

import java.util.Calendar;

import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.instancemanager.InstanceManager;
import l2.universe.gameserver.instancemanager.InstanceManager.InstanceWorld;
import l2.universe.gameserver.model.L2Party;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.L2Summon;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.util.Util;
import l2.universe.util.Rnd;

/**
 * @author Synerge
 * 
 */
public class ChamberOfDelusionWest extends Quest
{
	
	private class CDWorld extends InstanceWorld
	{
		private L2Npc manager, managera, managerb, managerc, managerd, managere, managerf, managerg, managerh, chesta, chestb, chestc, chestd, _aenkinel;
		
		public CDWorld()
		{
		}
	}
	
	private static final String qn = "ChamberOfDelusionWest";
	private static final int INSTANCEID = 128; // this is the client number
	private static final int RESET_HOUR = 6;
	private static final int RESET_MIN = 30;
	
	// NPCs
	private static final int GKSTART = 32659;
	private static final int GKFINISH = 32665;
	private static final int AENKINEL = 25691;
	private static final int PRIZ = 18820;
	private static final int FAIL1 = 18819;
	private static final int FAIL2 = 18819;
	private static final int FAIL3 = 18819;
	private static final int ROOMRB = 4;
	private int rb = 0;
	private int g = 0;
	private int h = 0;
	private int a;
	public int instId = 0;
	private int b;
	private int c;
	
	private class teleCoord
	{
		int instanceId;
		int x;
		int y;
		int z;
	}
	
	private static final int[][] TELEPORT = 
	{ 
		{ -122368, -152624, -6752 }, 
		{ -122368, -153504, -6752 }, 
		{ -120496, -154304, -6752 }, 
		{ -120496, -155184, -6752 }, 
		{ -121440, -154688, -6752 }, 
		{ -121440, -151328, -6752 }, 
		{ -120496, -153008, -6752 }, 
		{ -122368, -154800, -6752 }, 
		{ -121440, -153008, -6752 } 
	};
	
	private boolean checkConditions(L2PcInstance player)
	{
		final L2Party party = player.getParty();
		if (party == null)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_IN_PARTY_CANT_ENTER));
			return false;
		}
		
		if (party.getLeader() != player)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ONLY_PARTY_LEADER_CAN_ENTER));
			return false;
		}
		
		for (L2PcInstance partyMember : party.getPartyMembers())
		{
			if (partyMember.getLevel() < 80)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_LEVEL_REQUIREMENT_NOT_SUFFICIENT);
				sm.addPcName(partyMember);
				player.sendPacket(sm);
				sm = null;
				return false;
			}
			
			if (!Util.checkIfInRange(1000, player, partyMember, true))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_LOCATION_THAT_CANNOT_BE_ENTERED);
				sm.addPcName(partyMember);
				player.sendPacket(sm);
				sm = null;
				return false;
			}
			
			Long reentertime = InstanceManager.getInstance().getInstanceTime(partyMember.getObjectId(), INSTANCEID);
			if (System.currentTimeMillis() < reentertime)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_MAY_NOT_REENTER_YET);
				sm.addPcName(partyMember);
				player.sendPacket(sm);
				sm = null;
				return false;
			}
		}
		return true;
	}
	
	private void teleportplayer(L2PcInstance player, teleCoord teleto)
	{
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		player.setInstanceId(teleto.instanceId);
		player.teleToLocation(teleto.x, teleto.y, teleto.z);
	}
	
	public void penalty(InstanceWorld world)
	{
		if (world instanceof CDWorld)
		{
			Calendar reenter = Calendar.getInstance();
			reenter.add(Calendar.MINUTE, RESET_MIN);
			reenter.add(Calendar.HOUR_OF_DAY, RESET_HOUR);
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.INSTANT_ZONE_S1_RESTRICTED);
			sm.addString(InstanceManager.getInstance().getInstanceIdName(world.templateId));
			// set instance reenter time for all allowed players
			for (int objectId : world.allowed)
			{
				final L2PcInstance player = L2World.getInstance().getPlayer(objectId);
				if (player != null && player.isOnline())
				{
					InstanceManager.getInstance().setInstanceTime(objectId, world.templateId, reenter.getTimeInMillis());
					player.sendPacket(sm);
				}
			}
			sm = null;
		}
	}
	
	private void teleportrnd(L2PcInstance player, CDWorld world)
	{
		int tp = Rnd.get(TELEPORT.length);
		if (rb == 1 && tp == ROOMRB)
		{
			tp = Rnd.get(TELEPORT.length);
			for (int i = 0; i < TELEPORT.length; i++)
			{
				if (i != tp)
					continue;
				
				for (L2PcInstance partyMember : player.getParty().getPartyMembers())
				{
					partyMember.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
					partyMember.setInstanceId(instId);
					partyMember.teleToLocation(TELEPORT[i][0], TELEPORT[i][1], TELEPORT[i][2]);
				}
			}
			a = player.getX();
			b = player.getY();
			c = player.getZ();
		}
		else
		{
			for (int i = 0; i < TELEPORT.length; i++)
			{
				if (i != tp)
					continue;

				for (L2PcInstance partyMember : player.getParty().getPartyMembers())
				{
					partyMember.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
					partyMember.setInstanceId(instId);
					partyMember.teleToLocation(TELEPORT[i][0], TELEPORT[i][1], TELEPORT[i][2]);
				}
			}
			a = player.getX();
			b = player.getY();
			c = player.getZ();
		}
	}
	
	protected void spawnState(CDWorld world)
	{
		world._aenkinel = addSpawn(AENKINEL, -121463, -155094, -6752, 0, false, 0, false, world.instanceId);
		world._aenkinel.setIsNoRndWalk(false);
		world.manager = addSpawn(32665, -121440, -154688, -6752, 0, false, 0, false, world.instanceId);
		world.manager.setIsNoRndWalk(true);
		world.managerb = addSpawn(32665, -122368, -153504, -6752, 0, false, 0, false, world.instanceId);
		world.managerb.setIsNoRndWalk(true);
		world.managerc = addSpawn(32665, -120496, -154304, -6752, 0, false, 0, false, world.instanceId);
		world.managerc.setIsNoRndWalk(true);
		world.managerd = addSpawn(32665, -120496, -155184, -6752, 0, false, 0, false, world.instanceId);
		world.managerd.setIsNoRndWalk(true);
		world.managere = addSpawn(32665, -121440, -151328, -6752, 0, false, 0, false, world.instanceId);
		world.managere.setIsNoRndWalk(true);
		world.managerf = addSpawn(32665, -120496, -153008, -6752, 0, false, 0, false, world.instanceId);
		world.managerf.setIsNoRndWalk(true);
		world.managerg = addSpawn(32665, -122368, -154800, -6752, 0, false, 0, false, world.instanceId);
		world.managerg.setIsNoRndWalk(true);
		world.managerh = addSpawn(32665, -121440, -153008, -6752, 0, false, 0, false, world.instanceId);
		world.managerh.setIsNoRndWalk(true);
		world.managera = addSpawn(32665, -122368, -152624, -6752, 0, false, 0, false, world.instanceId);
		world.managera.setIsNoRndWalk(true);
	}
	
	protected int enterInstance(L2PcInstance player, String template)
	{
		// Check for existing instances for this player
		InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
		if (world != null)
		{
			if (!(world instanceof CDWorld))
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ALREADY_ENTERED_ANOTHER_INSTANCE_CANT_ENTER));
				return 0;
			}
			
			teleCoord tele = new teleCoord();
			tele.x = a;
			tele.y = b;
			tele.z = c;
			tele.instanceId = world.instanceId;
			teleportplayer(player, tele);
			return tele.instanceId;
		}
		else
		{
			if (!checkConditions(player))
				return 0;
			
			int instanceId = InstanceManager.getInstance().createDynamicInstance(template);
			world = new CDWorld();
			world.instanceId = instanceId;
			world.templateId = INSTANCEID;
			world.status = 0;
			InstanceManager.getInstance().addWorld(world);
			_log.info("Chamber Of Delusion started " + template + " Instance: " + instanceId + " created by player: " + player.getName());
			spawnState((CDWorld) world);
			instId = world.instanceId;
			for (L2PcInstance partyMember : player.getParty().getPartyMembers())
			{
				teleportrnd(partyMember, (CDWorld) world);
				world.allowed.add(partyMember.getObjectId());
			}
			
			return instanceId;
		}
	}
	
	protected void exitInstance(L2PcInstance player, teleCoord tele)
	{
		player.setInstanceId(0);
		player.teleToLocation(tele.x, tele.y, tele.z);
		final L2Summon pet = player.getPet();
		if (pet != null)
		{
			pet.setInstanceId(0);
			pet.teleToLocation(tele.x, tele.y, tele.z);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (player == null)
			return "";
		
		InstanceWorld tmpworld = InstanceManager.getInstance().getPlayerWorld(player);
		if (!(tmpworld instanceof CDWorld))
			return "";
		
		final L2Party party = player.getParty();
		if (party == null)
			return "";

		final CDWorld world = (CDWorld) tmpworld;
		instId = player.getInstanceId();
		if (event.equalsIgnoreCase("tproom"))
		{			
			for (L2PcInstance partyMember : party.getPartyMembers())
			{
				teleportrnd(partyMember, world);
			}			
			startQuestTimer("tproom1", 480000, null, player);
			h++;
		}
		else if (event.equalsIgnoreCase("tproom1"))
		{			
			for (L2PcInstance partyMember : party.getPartyMembers())
			{
				teleportrnd(partyMember, world);
			}			
			startQuestTimer("tproom2", 480000, null, player);
			h++;
		}
		else if (event.equalsIgnoreCase("tproom2"))
		{			
			for (L2PcInstance partyMember : party.getPartyMembers())
			{
				teleportrnd(partyMember, world);
			}			
			startQuestTimer("tproom3", 480000, null, player);
			h++;
		}
		else if (event.equalsIgnoreCase("tproom3"))
		{			
			for (L2PcInstance partyMember : party.getPartyMembers())
			{
				teleportrnd(partyMember, world);
			}			
		}
		else if (event.equalsIgnoreCase("7"))
		{
			if (g != 0)
				return null;

			switch (h)
			{
				case 0:
					cancelQuestTimers("tproom");
					for (L2PcInstance partyMember : party.getPartyMembers())
					{
						teleportrnd(partyMember, world);
					}
					startQuestTimer("tproom1", 480000, null, player);
					g = 1;
					break;
				case 1:
					cancelQuestTimers("tproom1");
					for (L2PcInstance partyMember : party.getPartyMembers())
					{
						teleportrnd(partyMember, world);
					}
					startQuestTimer("tproom2", 480000, null, player);
					g = 1;
					break;
				case 2:
					cancelQuestTimers("tproom2");
					for (L2PcInstance partyMember : party.getPartyMembers())
					{
						teleportrnd(partyMember, world);
					}
					startQuestTimer("tproom3", 480000, null, player);
					g = 1;
					break;
				case 3:
					cancelQuestTimers("tproom3");
					for (L2PcInstance partyMember : party.getPartyMembers())
					{
						teleportrnd(partyMember, world);
					}
					g = 1;
					break;
			}
		}
		return "";
	}
	
	public String onAttack(L2Npc npc, L2PcInstance attacker)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof CDWorld)
		{
			CDWorld world = (CDWorld) tmpworld;
			switch (npc.getNpcId())
			{
				case FAIL1:
				//case FAIL2:
				//case FAIL3:
					world.chesta.deleteMe();
					world.chestb.deleteMe();
					world.chestc.deleteMe();
					world.chestd.deleteMe();
					break;
				case PRIZ:
					world.chestb.deleteMe();
					world.chestc.deleteMe();
					world.chestd.deleteMe();
					break;
			}
		}
		return null;
	}
	
	public String onKill(L2Npc npc, L2PcInstance player)
	{
		if (rb == 0 && npc.getNpcId() == AENKINEL)
		{
			_log.info("kill");
			rb = 1;
			InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
			if (tmpworld instanceof CDWorld)
			{
				CDWorld world = (CDWorld) tmpworld;
				
				world.chesta = addSpawn(PRIZ, -121524, -155073, -6752, 0, false, 0, false, world.instanceId);
				world.chesta.setIsNoRndWalk(true);
				world.chestb = addSpawn(FAIL1, -121486, -155070, -6752, 0, false, 0, false, world.instanceId);
				world.chestb.setIsNoRndWalk(true);
				world.chestc = addSpawn(FAIL2, -121457, -155071, -6752, 0, false, 0, false, world.instanceId);
				world.chestc.setIsNoRndWalk(true);
				world.chestd = addSpawn(FAIL3, -121428, -155070, -6752, 0, false, 0, false, world.instanceId);
				world.chestd.setIsNoRndWalk(true);
				_log.info("spawn");				
			}
		}
		return "";
	}
	
	@Override
	public final String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return npc.getNpcId() + ".htm";
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(qn);
		if (st == null)
			st = newQuestState(player);
		
		switch (npc.getNpcId())
		{
			case GKSTART:
				if (enterInstance(player, "ChamberofDelusionWest.xml") != 0)
					startQuestTimer("tproom", 480000, null, player);
				break;
			case GKFINISH:
				InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
				if (world == null)
					return "";
				
				world.allowed.remove(world.allowed.indexOf(player.getObjectId()));
				teleCoord tele = new teleCoord();
				tele.instanceId = 0;
				tele.x = -114592;
				tele.y = -152509;
				tele.z = -6723;
				cancelQuestTimers("tproom");
				cancelQuestTimers("tproom1");
				cancelQuestTimers("tproom2");
				cancelQuestTimers("tproom3");
				penalty(world);
				for (L2PcInstance partyMember : player.getParty().getPartyMembers())
				{
					exitInstance(partyMember, tele);
				}
				break;
		}
		
		return "";
	}
	
	public ChamberOfDelusionWest(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(GKSTART);
		addTalkId(GKSTART);
		addStartNpc(GKFINISH);
		addFirstTalkId(GKFINISH);
		addTalkId(GKFINISH);
		addKillId(AENKINEL);
		addAttackId(PRIZ);
		addAttackId(FAIL1);
		//addAttackId(FAIL2);
		//addAttackId(FAIL3);
	}
	
	public static void main(String[] args)
	{
		new ChamberOfDelusionWest(-1, qn, "instances");
	}
}
