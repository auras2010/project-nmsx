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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.instancemanager.InstanceManager;
import l2.universe.gameserver.instancemanager.InstanceManager.InstanceWorld;
import l2.universe.gameserver.instancemanager.QuestManager;
import l2.universe.gameserver.model.L2CommandChannel;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Party;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2MonsterInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ExShowScreenMessageNPCString;
import l2.universe.gameserver.network.serverpackets.L2GameServerPacket;
import l2.universe.gameserver.network.serverpackets.PlaySound;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.skills.AbnormalEffect;
import l2.universe.gameserver.util.Util;
import l2.universe.util.Rnd;

/**
 * Cavern of the Pirate Captain (daydream)
 * 
 * @author JOJO
 */
public class ZakenDaydream extends Quest
{
	private static final boolean debug = false;
	
	private static final String qn = "ZakenDaydream";
	private static final String INSTANCE_XML = "ZakenDaydream.xml";

	private static final boolean CONFIG_ALONE_PLAYER = true;
	
	private static final int MAX_PLAYERS = 27;
	
	private static final int[] RESET_DAY_OF_WEEK = { Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY };
	private static final int   RESET_HOUR_OF_DAY = 6;
	private static final int   RESET_MINUTE = 30;
	private static final int EXIT_TIME = 600000;
	
	private static final int MIN_LEVEL_LO = 55;
	private static final int MAX_LEVEL_LO = 65;
	private static final int INSTANCE_ID_LO = 133;
	private static final int ZAKEN_LO = 29176;
	
	private static final int MIN_LEVEL_HI = 78;	//TODO: zone.xml <zone id="12020" type="EffectZone"... >
	private static final int MAX_LEVEL_HI = 85;
	private static final int INSTANCE_ID_HI = 135;
	private static final int ZAKEN_HI = 29181;
	
	private static final int ZAKENS_CANDLE = 32705;
	private static final int PATHFINDER_WORKER = 32713;
	private static final int[] RED_CANDLE_MOBS = { 29023, 29024, 29026, 29027, 21637 };	/*FIXME*/
	
	private static final int NOTSPAWN = 0, SPAWNED = 1, DEAD = 2;
	
	// item
	private static final int CANDLE_FUSE = 15280;
	private static final int CANDLE_BLUE = 15302;
	private static final int CANDLE_RED = 15281;
	
	// coord
	private static final int[] FLOOR_Z = { -3488, -3216, -2944 };
	
	private static final int[] VOID = null;
	private static final int[][][] CANDLE_COORD = {
		{     VOID     ,{54240,217155},     VOID     ,{56288,217155},     VOID     },
		{{53320,218080},    (null)    ,{55265,218170},    (null)    ,{57220,218080}},
		{     VOID     ,{54340,219105},    (null)    ,{56195,219105},     VOID     },
		{{53320,220125},    (null)    ,{55265,220040},    (null)    ,{57210,220125}},
		{     VOID     ,{54240,221050},     VOID     ,{56288,221050},     VOID     },
	};
	
	private static final int[][][] ZAKEN_COORD = {
		{     VOID     ,    (null)    ,     VOID     ,    (null)    ,     VOID     },
		{    (null)    ,{54240,218100},    (null)    ,{56290,218080},    (null)    },
		{     VOID     ,    (null)    ,{55255,219100},    (null)    ,     VOID     },
		{    (null)    ,{54250,220130},    (null)    ,{56280,220115},    (null)    },
		{     VOID     ,    (null)    ,     VOID     ,    (null)    ,     VOID     },
	};
	
	private static final int[][] ZAKEN_ROOM_TO_CANDLE_MATRIX = {
		{1,1},{3,1},{2,2},{3,3},{1,3}
	};
	

	class TheWorld extends InstanceWorld
	{
		final int ZAKEN_ID;
		L2MonsterInstance zaken;
		final int zakenFloor, zakenWE, zakenNS;
		L2Npc[][][] candleMatrix = {
				{new L2Npc[5], new L2Npc[5], new L2Npc[5], new L2Npc[5], new L2Npc[5]},
				{new L2Npc[5], new L2Npc[5], new L2Npc[5], new L2Npc[5], new L2Npc[5]},
				{new L2Npc[5], new L2Npc[5], new L2Npc[5], new L2Npc[5], new L2Npc[5]},
			};
		final L2Npc[] blueCandles;
		final long startTime;
		
		TheWorld(int instanceId, int templateId, int ZAKEN_ID)
		{
			super();
			
			this.instanceId = instanceId;
			this.templateId = templateId;
			this.ZAKEN_ID = ZAKEN_ID;
			
			// spawn candles
			for (int floor = 0; floor < 3; floor++)
			{
				for (int ns = 0; ns < 5; ns++)
				{
					for (int we = 0; we < 5; we++)
					{
						int[]coord = CANDLE_COORD[ns][we];
						if (coord != null)
						{
							int x = coord[0];
							int y = coord[1];
							int z = FLOOR_Z[floor];
							int h = 0;	//TODO:
							candleMatrix[floor][ns][we] = addSpawn(ZAKENS_CANDLE, x, y, z, h, false, 0, false, instanceId);
						}
					}
				}
			}
			zakenFloor = Rnd.get(3);
			int[] coord = ZAKEN_ROOM_TO_CANDLE_MATRIX[Rnd.get(5)];
			zakenWE = coord[0];
			zakenNS = coord[1];
			
			blueCandles = new L2Npc[]{
				candleMatrix[zakenFloor][zakenNS-1][zakenWE  ],
				candleMatrix[zakenFloor][zakenNS  ][zakenWE-1],
				candleMatrix[zakenFloor][zakenNS+1][zakenWE  ],
				candleMatrix[zakenFloor][zakenNS  ][zakenWE+1],
			};
			if (debug) for (L2Npc m : blueCandles) m.setTitle("!");
			
			startTime = System.currentTimeMillis();
		}
		
		//spawn zaken
		void spawnBoss()
		{
			status = SPAWNED;
			int[] coord = ZAKEN_COORD[zakenNS][zakenWE];
			int x = coord[0];
			int y = coord[1];
			int z = FLOOR_Z[zakenFloor];
			zaken = (L2MonsterInstance)addSpawn(ZAKEN_ID, x, y, z, 0, true, 0, false, instanceId);
			broadcastPacket(new PlaySound(1, "BS02_A", 1, zaken.getObjectId(), x, y, z));
		}
		
		void broadcastPacket(L2GameServerPacket packet)
		{
			for (int objectId : allowed)
			{
				L2PcInstance member = L2World.getInstance().getPlayer(objectId);
				if (member != null && member.isOnline())
					member.sendPacket(packet);
			}
		}
		
		void addItemRandom(int chance, int itemId, long count, L2Object reference, boolean announce)
		{
			if (chance == 0)
				return;
			for (int objectId : allowed)
			{
				L2PcInstance member = L2World.getInstance().getPlayer(objectId);
				if (member != null && member.isOnline())
				{
					if (Rnd.get(100) < chance)
					{
						member.addItem("Quest", itemId, 1, reference, !announce);
						if (announce)
							member.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.ANNOUNCEMENT_C1_PICKED_UP_S2).addCharName(member).addItemName(itemId));
					}
				}
			}
		}
		
		void setInstanceTime(long instanceTime)
		{
			SystemMessage zoneRestrictedMessage = SystemMessage.getSystemMessage(SystemMessageId.INSTANT_ZONE_S1_RESTRICTED)
					.addString(InstanceManager.getInstance().getInstanceIdName(templateId));
			for (int objectId : allowed)
			{
				L2PcInstance member = L2World.getInstance().getPlayer(objectId);
				if (member != null && member.isOnline())
				{
					InstanceManager.getInstance().setInstanceTime(member.getObjectId(), templateId, instanceTime);
					member.sendPacket(zoneRestrictedMessage);
				}
			}
		}
		
	}
	
	private void burnFuse(L2Npc candle)
	{
		candle.setRHandId(CANDLE_FUSE);
	}
	
	private void burnBlue(L2Npc candle)
	{
		candle.startAbnormalEffect(AbnormalEffect.IMPRISIONING_1);
		candle.setRHandId(CANDLE_BLUE);
	}
	
	private void burnRed(L2Npc candle)
	{
		candle.startAbnormalEffect(AbnormalEffect.BLEEDING);
		candle.setRHandId(CANDLE_RED);
	}
	
	private boolean isBurning(L2Npc candle)
	{
		return candle.getRightHandItem() != 0;
	}
	
	private boolean isBurningBlue(L2Npc candle)
	{
		return candle.getRightHandItem() == CANDLE_BLUE;
	}
	
	private void enterInstance(L2PcInstance player)
	{
		if (InstanceManager.getInstance().getPlayerWorld(player) != null)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ALREADY_ENTERED_ANOTHER_INSTANCE_CANT_ENTER));
			return;
		}
		
		L2Party party = player.getParty();
		if (party == null)
		{
if (!CONFIG_ALONE_PLAYER) {{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_IN_PARTY_CANT_ENTER));
			return;
}}
		}
		else if (party.getLeader() != player)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ONLY_PARTY_LEADER_CAN_ENTER));
			return;
		}
		
		List<L2PcInstance> partyMembers = getPartyMembers(player);
		
		final int MIN_LEVEL, MAX_LEVEL, INSTANCE_ID, ZAKEN_ID;
		int average = 0;
		for (L2PcInstance member : partyMembers)
			average += member.getLevel();
		average /= partyMembers.size();
		if (average <= MAX_LEVEL_LO) { INSTANCE_ID = INSTANCE_ID_LO; MIN_LEVEL = MIN_LEVEL_LO; MAX_LEVEL = MAX_LEVEL_LO; ZAKEN_ID = ZAKEN_LO; }
		else                         { INSTANCE_ID = INSTANCE_ID_HI; MIN_LEVEL = MIN_LEVEL_HI; MAX_LEVEL = MAX_LEVEL_HI; ZAKEN_ID = ZAKEN_HI; }
		
		if (partyMembers.size() > MAX_PLAYERS)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PARTY_EXCEEDED_THE_LIMIT_CANT_ENTER));
			return;
		}
		
		boolean partyCondition = true;
		for (L2PcInstance member : partyMembers)
		{
			int level;
			if ((level = member.getLevel()) < MIN_LEVEL || level > MAX_LEVEL)
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_LEVEL_REQUIREMENT_NOT_SUFFICIENT)
						.addPcName(member));
				partyCondition = false;
			}
			if (!Util.checkIfInRange(1000, player, member, true))
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_LOCATION_THAT_CANNOT_BE_ENTERED)
						.addPcName(member));
				partyCondition = false;
			}
			if (System.currentTimeMillis() < InstanceManager.getInstance().getInstanceTime(member.getObjectId(), INSTANCE_ID))
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_MAY_NOT_REENTER_YET)
						.addPcName(member));
				partyCondition = false;
			}
		}
		if (!partyCondition)
		{
			return;
		}
		
		int instanceId = InstanceManager.getInstance().createDynamicInstance(INSTANCE_XML);
		TheWorld world = new TheWorld(instanceId, INSTANCE_ID, ZAKEN_ID);
		InstanceManager.getInstance().addWorld(world);
		
		// teleport players
		//long instanceTime = System.currentTimeMillis() + 43200000;	//TODO
		for (L2PcInstance member : partyMembers)
		{
			//InstanceManager.getInstance().setInstanceTime(member.getObjectId(), INSTANCE_ID, instanceTime);	//TODO
			member.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			member.setInstanceId(instanceId);
			member.teleToLocation(52560 + Rnd.get(-100, 100), 219100 + Rnd.get(-50, 50), -3235);
			world.allowed.add(member.getObjectId());
		}
	}
	
	private long calculateNextInstanceTime()
	{
		long now = System.currentTimeMillis();
		GregorianCalendar reset = new GregorianCalendar();
		reset.setTimeInMillis(now);
		reset.set(Calendar.MILLISECOND, 0);
		reset.set(Calendar.SECOND, 0);
		reset.set(Calendar.MINUTE, RESET_MINUTE);
		reset.set(Calendar.HOUR_OF_DAY, RESET_HOUR_OF_DAY);
		while (reset.getTimeInMillis() < now)
			reset.add(Calendar.DATE, 1);
		while (! Util.contains(RESET_DAY_OF_WEEK, reset.get(Calendar.DAY_OF_WEEK)))
			reset.add(Calendar.DATE, 1);
		return reset.getTimeInMillis();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event == "burn")
		{
			TheWorld world = (TheWorld) InstanceManager.getInstance().getWorld(npc.getInstanceId());
			if (Util.contains(world.blueCandles, npc))
			{
				burnBlue(npc);
				int count = world.blueCandles.length;
				for (L2Npc blueCandle : world.blueCandles)
					if (isBurningBlue(blueCandle))
						--count;
				if (count == 0)
				{
					startQuestTimer("spawnBoss", 3000, npc, player);
					startQuestTimer("spawnMobs", 5000, npc, player);
					world.broadcastPacket(new ExShowScreenMessageNPCString(1800867, 10000));
				}
			}
			else
			{
				burnRed(npc);
				if (Rnd.get(100) < 33)
					startQuestTimer("sayHint", 3000, npc, player);
				startQuestTimer("spawnMobs", 5000, npc, player);
			}
		}
		else if (event == "spawnMobs")
		{
			int x = npc.getX();
			int y = npc.getY();
			int z = npc.getZ();
			int instanceId = npc.getInstanceId();
			for (int npcId : RED_CANDLE_MOBS)
			{
				for (int n = Rnd.get(1, 3); --n >= 0; )
				{
					L2MonsterInstance mob = (L2MonsterInstance) addSpawn(npcId, x, y, z, 0, true, 0, false, instanceId);
					mob.setTarget(player);
					mob.addDamageHate(player, 1, 1);
				}
			}
		}
		else if (event == "spawnBoss")
		{
			TheWorld world = (TheWorld) InstanceManager.getInstance().getWorld(npc.getInstanceId());
			world.spawnBoss();
		}
		else if (event == "onKillZakenLO")
		{
			TheWorld world = (TheWorld) InstanceManager.getInstance().getWorld(npc.getInstanceId());
			/*FIXME*/
			world.addItemRandom(2, 6659, 1, npc, true);
			world.addItemRandom(50, 6569, 1, npc, false);
			world.addItemRandom(50, 6570, 1, npc, false);
			world.addItemRandom(40, 729, 1, npc, false);
			world.addItemRandom(40, 730, 1, npc, false);
			
			world.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.RAID_WAS_SUCCESSFUL));
			world.broadcastPacket(new PlaySound(1, "BS01_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
			world.setInstanceTime(calculateNextInstanceTime());
			InstanceManager.getInstance().getInstance(world.instanceId).setDuration(EXIT_TIME);
		}
		else if (event == "onKillZakenHI")
		{
			TheWorld world = (TheWorld) InstanceManager.getInstance().getWorld(npc.getInstanceId());
			/*FIXME*/
			int time, rate;
			time = (int)(System.currentTimeMillis() - world.startTime) / 60000;
			if (time <= 5)
				rate = 100;
			else if (time <= 10)
				rate = 75;
			else if (time <= 15)
				rate = 50;
			else if (time <= 20)
				rate = 25;
			else if (time <= 25)
				rate = 12;
			else
				rate = 6;
			world.addItemRandom(10 * rate / 100, 6659, 1, npc, true);
			world.addItemRandom(50 * rate / 100, 15763, 1, npc, true);
			world.addItemRandom(50 * rate / 100, 15764, 1, npc, true);
			world.addItemRandom(50 * rate / 100, 15765, 1, npc, true);
			
			world.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.RAID_WAS_SUCCESSFUL));
			world.broadcastPacket(new PlaySound(1, "BS01_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
			world.setInstanceTime(calculateNextInstanceTime());
			InstanceManager.getInstance().getInstance(world.instanceId).setDuration(EXIT_TIME);
		}
		else if (event == "sayHint")
		{
			TheWorld world = (TheWorld) InstanceManager.getInstance().getWorld(npc.getInstanceId());
			sayHint(world, npc);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			Quest q = QuestManager.getInstance().getQuest(getName());
			st = q.newQuestState(player);
		}
		switch (npc.getNpcId())
		{
			case PATHFINDER_WORKER:
				return "data/html/default/32713.htm";
			case ZAKENS_CANDLE:
				synchronized (InstanceManager.getInstance().getWorld(npc.getInstanceId()))
				{
					if (!isBurning(npc))
					{
						burnFuse(npc);
						startQuestTimer("burn", 10000, npc, player);
					}
				}
				return null;
		}
		throw new RuntimeException();
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		switch (npc.getNpcId())
		{
			case PATHFINDER_WORKER:
				//32713-2.htm <a action="bypass -h npc_%objectId%_Quest ZakenDaydream"></a>
				enterInstance(talker);
				break;
		}
		return super.onTalk(npc, talker);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		switch (npc.getNpcId())
		{
			case ZAKEN_LO:
			case ZAKEN_HI:
				((L2Attackable)npc).setOnKillDelay(100);	//Default 5000ms.
				break;
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		TheWorld world = (TheWorld) InstanceManager.getInstance().getWorld(npc.getInstanceId());
		switch (npc.getNpcId())
		{
			case ZAKEN_LO:
				world.status = DEAD;
				cancelAllQuestTimers(world.instanceId);
				startQuestTimer("onKillZakenLO", 777, npc, killer);
				break;
			case ZAKEN_HI:
				world.status = DEAD;
				cancelAllQuestTimers(world.instanceId);
				startQuestTimer("onKillZakenHI", 777, npc, killer);
				break;
		}
		return super.onKill(npc, killer, isPet);
	}
	
	private void cancelAllQuestTimers(int instanceId)
	{
	//	cancelQuestTimers("xxx", instanceId);
	//	cancelQuestTimers("yyy", instanceId);
	//	cancelQuestTimers("zzz", instanceId);
	}
	
	private void sayHint(TheWorld world, L2Npc candle)
	{
		if (world.status != NOTSPAWN)
			return;
		for (int floor = 0; floor < 3; floor++)
		{
			for (int ns = 0; ns < 5; ns++)
			{
				for (int we = 0; we < 5; we++)
				{
					if (world.candleMatrix[floor][ns][we] == candle)
					{
						final int say;
						if (floor < world.zakenFloor)
							say = 1800868;
						else if (floor > world.zakenFloor)
							say = 1800870;
						else
							say = 1800869;
						world.broadcastPacket(new ExShowScreenMessageNPCString(say, 10000));
						return;
					}
				}
			}
		}
	}
	
	private List<L2PcInstance> getPartyMembers(L2PcInstance player)
	{
		L2Party party;
		L2CommandChannel commandChannel;
		if ((party = player.getParty()) == null)
		{
			List<L2PcInstance> m = new ArrayList<L2PcInstance>(1);
			m.add(player);
			return m;
		}
		else if ((commandChannel = party.getCommandChannel()) == null)
		{
			return party.getPartyMembers();
		}
		else 
		{
			List<L2PcInstance> m = new ArrayList<L2PcInstance>(27);
			for (L2Party pp : commandChannel.getPartys())
				m.addAll(pp.getPartyMembers());
			return m;
		}
	}
	
	private ZakenDaydream(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addFirstTalkId(PATHFINDER_WORKER);
		addFirstTalkId(ZAKENS_CANDLE);
		addTalkId(PATHFINDER_WORKER);
		addSpawnId(ZAKEN_LO);
		addSpawnId(ZAKEN_HI);
		addKillId(ZAKEN_LO);
		addKillId(ZAKEN_HI);
	}
	
	public static void main(String[] args)
	{
		new ZakenDaydream(-1, qn, "instances");
	}
}
