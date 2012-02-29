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
package instances.Disciple;

import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.instancemanager.InstanceManager;
import l2.universe.gameserver.instancemanager.InstanceManager.InstanceWorld;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2DoorInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ExStartScenePlayer;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

/**
 * 
 * @author Synerge & knoxville
 * TODO: Lilith and Anakim Attack.
 */
public class Disciple extends Quest
{
	private class DiSWorld extends InstanceWorld
	{
		public           long[] storeTime                                = {0,0}; 
		public DiSWorld()
		{
		}
	}

	private static final String qn = "Disciple";
	private static final int INSTANCEID = 112; 

	private static final int PROMISE = 32585;
	private static final int LEON = 32587;  
	private static final int DOOR = 17240111;
	private static final int GATEKEEPER = 32657; 

	private class teleCoord {int instanceId; int x; int y; int z;}

	private void teleportplayer(L2PcInstance player, teleCoord teleto)
	{
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		player.setInstanceId(teleto.instanceId);
		player.teleToLocation(teleto.x, teleto.y, teleto.z);
	}
	
	protected void exitInstance(L2PcInstance player, teleCoord tele)
	{
		player.setInstanceId(0);
		player.teleToLocation(tele.x, tele.y, tele.z);
	}
	
	protected int enterInstance(L2PcInstance player, String template, teleCoord teleto)
	{
		int instanceId = 0;
		InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
		if (world != null)
		{
			if (!(world instanceof DiSWorld))
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ALREADY_ENTERED_ANOTHER_INSTANCE_CANT_ENTER));
				return 0;
			}
			teleto.instanceId = world.instanceId;
			teleportplayer(player,teleto);
			return instanceId;
		}
		else
		{
			instanceId = InstanceManager.getInstance().createDynamicInstance(template);
			world = new DiSWorld();
			world.instanceId = instanceId;
			world.templateId = INSTANCEID;
			world.status = 0;
			((DiSWorld)world).storeTime[0] = System.currentTimeMillis();
			InstanceManager.getInstance().addWorld(world);
			_log.info("Disciple started " + template + " Instance: " + instanceId + " created by player: " + player.getName());
			teleto.instanceId = instanceId;
			teleportplayer(player,teleto);
			world.allowed.add(player.getObjectId());
			return instanceId;
		}
	}
	protected void openDoor(int doorId,int instanceId)
	{
		for (L2DoorInstance door : InstanceManager.getInstance().getInstance(instanceId).getDoors())
		{
			if (door.getDoorId() == doorId)
				door.openMe();
		}
	} 

	@Override
	public String onTalk ( L2Npc npc, L2PcInstance player)
	{	
		QuestState st = player.getQuestState(qn);
		if (st == null)
			st = newQuestState(player);
		
		switch (npc.getNpcId())
		{
			case PROMISE:
				teleCoord tele = new teleCoord();
				tele.x = -89559;      
				tele.y = 216030;
				tele.z = -7488;
				enterInstance(player, "Disciple.xml", tele);
				break;
			case LEON:
				InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
				world.allowed.remove(world.allowed.indexOf(player.getObjectId()));
				tele = new teleCoord();
				tele.instanceId = 0;
				tele.x = 171782;    
				tele.y = -17612;
				tele.z = -4901;
				exitInstance(player,tele);
				break;
			case GATEKEEPER:
				final InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
				if (tmpworld instanceof DiSWorld)
				{
					openDoor(DOOR,tmpworld.instanceId);
					for (int objId : tmpworld.allowed)
					{
						final L2PcInstance pl = L2World.getInstance().getPlayer(objId);
						if (pl != null)
							pl.showQuestMovie(ExStartScenePlayer.SSQ_SEALING_EMPEROR_1ST);					          
					}		
				}
				break;
		}

		return "";
	}

	public Disciple(int questId, String name, String descr)
	{
		super(questId, name, descr);

		addStartNpc(PROMISE);
		addTalkId(PROMISE);
		addTalkId(LEON);
		addTalkId(GATEKEEPER);
	}

	public static void main(String[] args)
	{
		new Disciple(-1, qn, "instances");
	}
}
