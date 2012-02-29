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
package ai.individual.grandboss;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javolution.util.FastList;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.datatables.NpcTable;
import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.instancemanager.GrandBossManager;
import l2.universe.gameserver.instancemanager.InstanceManager;
import l2.universe.gameserver.instancemanager.InstanceManager.InstanceWorld;
import l2.universe.gameserver.model.L2CharPosition;
import l2.universe.gameserver.model.L2CommandChannel;
import l2.universe.gameserver.model.L2Effect;
import l2.universe.gameserver.model.L2Party;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.L2Spawn;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.Location;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.Instance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.zone.L2ZoneType;
import l2.universe.gameserver.model.zone.type.L2BossZone;
import l2.universe.gameserver.network.serverpackets.ExShowScreenMessage;
import l2.universe.gameserver.taskmanager.DecayTaskManager;
import l2.universe.gameserver.templates.StatsSet;
import l2.universe.gameserver.util.Util;
import l2.universe.util.Rnd;

public class Freya extends Quest
{
	//FreyaStand Status Tracking :
	private static final byte DORMANT = 0; //FreyaStand is spawned and no one has entered yet. Entry is unlocked
	private static final byte WAITING = 1; //FreyaStand is spawend and someone has entered, triggering a 30 minute window for additional people to enter before he unleashes his attack. Entry is unlocked
	@SuppressWarnings("unused")
	private static final byte FIGHTING = 2; //FreyaStand is engaged in battle, annihilating his foes. Entry is locked
	private static final byte DEAD = 3; //FreyaStand has been killed. Entry is locked
	
	@SuppressWarnings("unused")
	private static long _LastAction = 0;
	
	private static L2BossZone _Zone;
	private static List<L2PcInstance> _PlayersInside = new FastList<L2PcInstance>();
	
	//private static final boolean debug = true;
	private static final int FreyaOnThrone = 29177;
	private static final int FreyaStand = 29179;
	private static final int Glacier = 18853;
	private static final int Glakias = 25699;
	private static final int ArcheryKnight = 18855;
	private static final int ArchersBreath = 18854;
	private static final int Kegor = 18846;
	private static final int Jinia = 18850;
	private static final int JiniaStart = 32781;
	private static final int KnightSpawns[][] = { { 113828, -113498, -11172 }, { 113366, -113968, -11172 }, { 115606, -116099, -11172 }, { 116073, -115631, -11172 } };
	private static final int GlacierSpawns[][] = { { 115176, -114614 }, { 114918, -114361 }, { 114539, -114349 }, { 114270, -114613 }, { 114271, -114981 }, { 114526, -115249 }, { 114905, -115252 }, { 115167, -114996 } };
	
	private static class Timer implements Runnable
	{
		@Override
		public void run()
		{
			if (time > 10)
			{
				//world.BossZone.broadcastPacket(new ExShowScreenMessage2((new StringBuilder()).append("00 : ").append(time).toString(), 1500, l2.universe.gameserver.network.serverpackets.ExShowScreenMessage2.ScreenMessageAlign.TOP_CENTER, true, false, -1, false));
				ThreadPoolManager.getInstance().scheduleGeneral(new Timer(time - 1, world), 1000L);
			}
		}
		
		private int time;
		private FreyaWorld world;
		
		public Timer(int _time, FreyaWorld _world)
		{
			time = 0;
			world = null;
			time = _time;
			world = _world;
		}
	}
	
	private class FreyaWorld extends InstanceWorld
	{
		
		private L2Npc Npc_Freya;
		private L2Npc Npc_Kegor;
		private L2Npc Npc_Jinia;
		private L2BossZone BossZone;
		private ArrayList<L2Npc> activeMobs;
		private ArrayList<L2Npc> activeKnights;
		private ArrayList<L2Npc> Glaciers;
		private ScheduledFuture<?> GlacierTimer;
		private ScheduledFuture<?> ArchersTimer;
		
		private FreyaWorld()
		{
			super();
			BossZone = GrandBossManager.getInstance().getZone(114722, -114797, -11203);
			activeMobs = new ArrayList<L2Npc>();
			activeKnights = new ArrayList<L2Npc>();
			Glaciers = new ArrayList<L2Npc>();
			GlacierTimer = null;
			ArchersTimer = null;
		}
	}
	
	private static int checkworld(L2PcInstance player)
	{
		InstanceWorld checkworld = InstanceManager.getInstance().getPlayerWorld(player);
		if (checkworld != null)
			return (checkworld instanceof FreyaWorld) ? 1 : 0;
		else
			return 2;
	}
	
	private synchronized void enterInstance(L2Npc npc, L2PcInstance player)
	{
		_log.info("enterInstance:" + player.getName());
		
		startQuestTimer("close", 0, npc, null);
		startQuestTimer("freya_despawn", 60000, npc, null, true);
		
		_LastAction = System.currentTimeMillis();
		_PlayersInside.clear();
		
		if (player.getParty() != null)
		{
			L2CommandChannel CC = player.getParty().getCommandChannel();
			if (CC != null)
			{
				for (L2Party party : CC.getPartys())
				{
					if (party == null)
						continue;
					for (L2PcInstance member : party.getPartyMembers())
					{
						if (member == null || member.getLevel() < 74)
							continue;
						if (!member.isInsideRadius(npc, 700, false, false))
							continue;
						
						if (_PlayersInside.size() > 45)
						{
							member.sendMessage("The number of challenges have been full (45 players), so can not enter.");
							break;
						}
						_PlayersInside.add(member);
					}
					if (_PlayersInside.size() > 45)
						break;
				}
			}
			else if (player.isGM())
				for (L2PcInstance member : player.getParty().getPartyMembers())
					_PlayersInside.add(member);
		}
		else if (player.isGM())
		{
			player.sendMessage("You go allone, GM...");
			_PlayersInside.add(player);
		}
		
		//
		if (_PlayersInside.size() > 0)
		{
			InstanceWorld world = new FreyaWorld();
			world.instanceId = InstanceManager.getInstance().createDynamicInstance(null);
			world.templateId = 139;
			Instance instance = InstanceManager.getInstance().getInstance(world.instanceId);
			instance.addDoor(23140101, true);
			int returnLoc[] = { player.getX(), player.getY(), player.getZ() };
			instance.setSpawnLoc(returnLoc);
			InstanceManager.getInstance().addWorld(world);
			instance.setName("Ice Queen's Castle");
			world.status = 0;
			_log.info((new StringBuilder()).append("Instance Ice Queen's Castle created with id ").append(world.instanceId).append(" and created by player ").append(player.getName()).toString());
			
			_Zone = GrandBossManager.getInstance().getZone(114712, -114811, -11210);
			for (L2PcInstance member : _PlayersInside)
			{
				_Zone.allowPlayerEntry(member, 300);
				teleportplayer(member, new Location(114025 + Rnd.get(50), -112300 + Rnd.get(50), -11200), (FreyaWorld) world);
			}
		}
		GrandBossManager.getInstance().setBossStatus(FreyaStand, WAITING);
	}
	
	private void teleportplayer(L2PcInstance player, Location loc, FreyaWorld world)
	{
		player.setInstanceId(world.instanceId);
		player.teleToLocation(loc._x, loc._y, loc._z);
		if (!world.allowed.contains(Integer.valueOf(player.getObjectId())))
			world.allowed.add(Integer.valueOf(player.getObjectId()));
	}
	
	private static L2Npc spawn(int npcId, int X, int Y, int Z, int head, FreyaWorld world)
	{
		try
		{
			l2.universe.gameserver.templates.chars.L2NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
			if (template != null)
			{
				L2Spawn spawn = new L2Spawn(template);
				spawn.setHeading(head);
				spawn.setLocx(X);
				spawn.setLocy(Y);
				spawn.setLocz(Z + 20);
				spawn.setInstanceId(world.instanceId);
				spawn.setAmount(spawn.getAmount() + 1);
				spawn.setOnKillDelay(1500);
				return spawn.doSpawn();
			}
		}
		catch (Exception e1)
		{
			_log.info((new StringBuilder()).append("Freya: Could not spawn Npc ").append(npcId).toString());
		}
		return null;
	}
	
	public static void startWave(int stage, FreyaWorld world)
	{
		_log.info("startWave: " + stage);
		final FreyaWorld Wworld = world;
		if (stage == 1)
		{
			world.BossZone.broadcastMovie(15, world.instanceId);
			ThreadPoolManager.getInstance().scheduleGeneral(new Timer(0, Wworld)
			{
				
				@Override
				public void run()
				{
					Wworld.Npc_Freya = Freya.spawn(FreyaOnThrone, 114720, -117068, -11078, 16384, Wworld);
					ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
					{
						
						@Override
						public void run()
						{
							//Wworld.BossZone.broadcastPacket(new ExShowScreenMessage2(1801086, 6000, l2.universe.gameserver.network.serverpackets.ExShowScreenMessage2.ScreenMessageAlign.TOP_CENTER, true, false, -1, true));
							Freya.spawnGlaciers(Wworld);
						}
						
						/*
						final _cls1 this$0;
						
						{
							this$0 = _cls1.this;
							super();
						}
						*/
					}, 1000L);
				}
				
				/*
				final FreyaWorld val$Wworld;
				
				{
					Wworld = freyaworld;
					super();
				}
				*/
			}, 53500L);
		}
		if (stage == 2)
		{
			world.BossZone.broadcastMovie(16, world.instanceId);
			ThreadPoolManager.getInstance().scheduleGeneral(new Timer(0, Wworld)
			{
				@Override
				public void run()
				{
					Wworld.Npc_Freya = Freya.spawn(FreyaOnThrone, 114719, -117450, -10673, 16384, Wworld);
					Wworld.Npc_Freya.setIsOverloaded(true);
					Wworld.Npc_Freya.setIsInvul(true);
					ThreadPoolManager.getInstance().scheduleGeneral(new Timer(60, Wworld), 100L);
					ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
					{
						
						@Override
						public void run()
						{
							//$$$$$ Wworld.BossZone.broadcastPacket(new ExShowScreenMessage2(1801087, 6000, l2.universe.gameserver.network.serverpackets.ExShowScreenMessage2.ScreenMessageAlign.TOP_CENTER, true, false, -1, true));
							Freya.spawnKnights(Wworld);
							Freya.spawnGlaciers(Wworld);
						}
					}, 60100L);
				}
			}, 21100L);
		}
		if (stage == 3)
		{
			world.BossZone.broadcastMovie(17, world.instanceId);
			ThreadPoolManager.getInstance().scheduleGeneral(new Timer(0, Wworld)
			{
				
				@Override
				public void run()
				{
					Wworld.status++;
					Wworld.Npc_Freya.deleteMe();
					Wworld.Npc_Freya = Freya.spawn(FreyaStand, 114720, -117068, -11078, 16384, Wworld);
					Freya.moveTo(Wworld.Npc_Freya, new Location(114722, -114797, -11200));
					Freya.spawnGlaciers(Wworld);
					//$$$$$ Wworld.BossZone.broadcastPacket(new ExShowScreenMessage2(1801088, 6000, l2.universe.gameserver.network.serverpackets.ExShowScreenMessage2.ScreenMessageAlign.TOP_CENTER, true, false, -1, true));
				}
			}, 21600L);
		}
	}
	
	private static void stopAll(FreyaWorld world)
	{
		int players[] = InstanceManager.getInstance().getInstance(world.instanceId).getPlayers().toArray();
		
		for (int i = 0; i < players.length; i++)
		{
			int objId = players[i];
			L2PcInstance plr = (L2PcInstance) L2World.getInstance().findObject(objId);
			plr.abortCast();
			plr.abortAttack();
			plr.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			plr.setIsImmobilized(true);
			plr.setIsInvul(true);
		}
		
		for (L2Npc npc : world.activeMobs)
		{
			npc.setIsInvul(true);
			npc.abortCast();
			npc.abortAttack();
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			npc.setIsImmobilized(true);
		}
		
		for (L2Npc npc : world.Glaciers)
		{
			npc.setIsInvul(true);
			npc.abortCast();
			npc.abortAttack();
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			npc.setIsImmobilized(true);
		}
		
		if (world.Npc_Freya != null)
		{
			world.Npc_Freya.abortCast();
			world.Npc_Freya.abortAttack();
			world.Npc_Freya.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			world.Npc_Freya.setIsImmobilized(true);
			world.Npc_Freya.setIsInvul(true);
		}
		if (world.Glaciers.size() > 0)
		{
			for (int i = 0; i < players.length; i++)
			{
				int objId = players[i];
				L2PcInstance player = L2World.getInstance().getPlayer(objId);
				L2Effect effects[] = player.getAllEffects();
				for (int j = 0; j < effects.length; j++)
				{
					L2Effect e = effects[j];
					if (e.getSkill().getId() == 6437)
						e.exit();
				}
			}
		}
		world.ArchersTimer.cancel(false);
		world.GlacierTimer.cancel(false);
	}
	
	private static void startAll(FreyaWorld world)
	{
		for (L2Npc npc : world.activeMobs)
		{
			npc.setIsInvul(false);
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			npc.setIsImmobilized(false);
		}
		
		for (final L2Npc npc : world.Glaciers)
		{
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			npc.setIsImmobilized(false);
			npc.setIsInvul(false);
			world.ArchersTimer = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Runnable()
			{
				@Override
				public void run()
				{
					FreyaWorld wrld = (FreyaWorld) InstanceManager.getInstance().getWorld(npc.getInstanceId());
					wrld.activeMobs.add(Freya.spawn(ArchersBreath, npc.getX(), npc.getY(), npc.getZ(), 0, wrld));
				}
			}, 10000L, 10000L);
		}
		
		if (world.Npc_Freya != null)
		{
			world.Npc_Freya.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			world.Npc_Freya.setIsImmobilized(false);
			world.Npc_Freya.setIsInvul(false);
		}
		if (world.Glaciers.size() > 0)
		{
			int players[] = InstanceManager.getInstance().getInstance(world.instanceId).getPlayers().toArray();
			for (int i = 0; i < players.length; i++)
			{
				L2PcInstance player = L2World.getInstance().getPlayer(players[i]);
				SkillTable.getInstance().getInfo(6437, world.Glaciers.size()).getEffects(world.Glaciers.get(Rnd.get(world.Glaciers.size())), player);
			}
			
		}
		spawnGlaciers(world);
		int players[] = InstanceManager.getInstance().getInstance(world.instanceId).getPlayers().toArray();
		for (int i = 0; i < players.length; i++)
		{
			L2PcInstance plr = (L2PcInstance) L2World.getInstance().findObject(players[i]);
			plr.setIsImmobilized(false);
			plr.setIsInvul(false);
		}
		
	}
	
	private static void endStage(FreyaWorld world)
	{
		for (L2Npc npc : world.activeMobs)
			npc.deleteMe();
		for (L2Npc npc : world.activeKnights)
			npc.deleteMe();
		
		if (world.Glaciers.size() > 0)
		{
			int players[] = InstanceManager.getInstance().getInstance(world.instanceId).getPlayers().toArray();
			for (int i = 0; i < players.length; i++)
			{
				L2PcInstance player = L2World.getInstance().getPlayer(players[i]);
				L2Effect effects[] = player.getAllEffects();
				for (int j = 0; j < effects.length; j++)
				{
					L2Effect effect = effects[j];
					if (effect.getSkill().getId() == 6437)
						effect.exit();
				}
			}
		}
		for (L2Npc npc : world.Glaciers)
			npc.deleteMe();
		
		if (world.Npc_Freya != null)
			world.Npc_Freya.deleteMe();
		world.ArchersTimer.cancel(false);
		world.GlacierTimer.cancel(false);
	}
	
	@Override
	public String onEnterZone(L2Character character, L2ZoneType zone)
	{
		_log.info("onEnterZone: "+ zone.getId() + "  Zone Name: "+ zone.getName());
		if ((character instanceof L2PcInstance) && (character.getInstanceId() > 0))
		{
			InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(character.getInstanceId());
			if (tmpworld instanceof FreyaWorld)
			{
				final FreyaWorld world = (FreyaWorld) tmpworld;
				_log.info("zone.getId(): " + zone.getId() + "  world.status: " + world.status);
				if (zone.getId() == 12016 && world.status < 1)
				{
					world.status = 1;
					ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
					{
						@Override
						public void run()
						{
							Freya.startWave(1, world);
						}
					}, 20000);
				}
			}
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (npc.getInstanceId() > 0)
		{
			l2.universe.gameserver.instancemanager.InstanceManager.InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
			if (tmpworld instanceof FreyaWorld)
			{
				final FreyaWorld world = (FreyaWorld) tmpworld;
				if (npc.getNpcId() == FreyaOnThrone && world.status == 1)
				{
					endStage(world);
					world.status = 2;
					startWave(2, world);
				}
				else if (npc.getNpcId() == ArcheryKnight)
				{
					if (world.status >= 2 && world.status < 12)
					{
						world.status++;
						if (world.status >= 12)
						{
							world.BossZone.broadcastMovie(23, world.instanceId);
							stopAll(world);
							ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
							{
								@Override
								public void run()
								{
									Freya.spawn(Glakias, 114722, -114797, -11200, 16384, world);
									Freya.startAll(world);
								}
							}, 7000L);
						}
					}
					else
					{
						final int x = npc.getSpawn().getLocx();
						final int y = npc.getSpawn().getLocy();
						final int z = npc.getSpawn().getLocz();
						ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
						{
							@Override
							public void run()
							{
								world.activeKnights.add(Freya.spawn(ArcheryKnight, x, y, z, 0, world));
							}
						}, 15000L);
					}
				}
				else if (npc.getNpcId() == Glakias && world.status == 12)
				{
					world.status++;
					endStage(world);
					ThreadPoolManager.getInstance().scheduleGeneral(new Timer(60, world), 100L);
					ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
					{
						@Override
						public void run()
						{
							Freya.startWave(3, world);
						}
					}, 60100L);
				}
				else if (npc.getNpcId() == FreyaStand && world.status == 15)
				{
					world.status++;
					endStage(world);
					DecayTaskManager.getInstance().cancelDecayTask(npc);
					world.BossZone.broadcastMovie(19, world.instanceId);
					ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
					{
						@Override
						public void run()
						{
							world.status++;
						}
					}, 16000L);
				}
				else if (npc.getNpcId() == Glacier)
				{
					npc.setDisplayEffect(3);
					world.Glaciers.remove(npc);
					if (world.Glaciers.size() > 0)
					{
						L2Skill skill = SkillTable.getInstance().getInfo(6437, world.Glaciers.size());
						int players[] = InstanceManager.getInstance().getInstance(world.instanceId).getPlayers().toArray();
						for (int i = 0; i < players.length; i++)
						{
							int objId = players[i];
							L2PcInstance tmpPlayer = L2World.getInstance().getPlayer(objId);
							skill.getEffects(npc, tmpPlayer);
						}
						
					}
					if (world.GlacierTimer == null)
						world.GlacierTimer = ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
						{
							@Override
							public void run()
							{
								Freya.spawnGlaciers(world);
							}
						}, 15000L);
				}
			}
		}
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet)
	{
		if (npc.getInstanceId() > 0)
		{
			l2.universe.gameserver.instancemanager.InstanceManager.InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
			if (tmpworld instanceof FreyaWorld)
			{
				final FreyaWorld world = (FreyaWorld) tmpworld;
				if (npc.getNpcId() == FreyaStand && npc.getCurrentHp() < npc.getMaxHp() * 0.45000000000000001D && world.status == 14)
				{
					world.status++;
					stopAll(world);
					world.BossZone.broadcastMovie(18, world.instanceId);
					ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
					{
						
						@Override
						public void run()
						{
							Freya.startAll(world);
							world.Npc_Kegor = Freya.spawn(Kegor, 114722, -114797, -11200, 0, world);
							world.Npc_Kegor.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, world.Npc_Freya);
							world.Npc_Kegor.setRunning();
							world.Npc_Jinia = Freya.spawn(Jinia, 114722, -114797, -11200, 0, world);
							world.Npc_Jinia.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, world.Npc_Freya);
							world.Npc_Jinia.setRunning();
							world.BossZone.broadcastPacket(new ExShowScreenMessage(1801089, 6000, ExShowScreenMessage.POSITION_TOP_CENTER, true, false, -1, true));
						}
						
					}, 28000L);
				}
				else if (npc.getNpcId() == ArcheryKnight && npc.isImmobilized())
				{
					npc.setDisplayEffect(2);
					npc.setIsImmobilized(false);
					startQuestTimer("respawnKnight", 6000L, npc, null);
				}
			}
		}
		return super.onAttack(npc, player, damage, isPet);
	}
	
	private static void spawnKnights(FreyaWorld world)
	{
		int arr$[][] = KnightSpawns;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			int spawn[] = arr$[i$];
			L2Npc mob = spawn(ArcheryKnight, spawn[0], spawn[1], spawn[2], 0, world);
			mob.setDisplayEffect(1);
			mob.setIsImmobilized(true);
			world.activeKnights.add(mob);
		}
		
		if (world.status == 2)
		{
			world.activeKnights.add(spawn(ArcheryKnight, 114405, -115555, -11200, 0, world));
			world.activeKnights.add(spawn(ArcheryKnight, 115023, -115555, -11200, 0, world));
			world.activeKnights.add(spawn(ArcheryKnight, 115023, -114058, -11200, 0, world));
			world.activeKnights.add(spawn(ArcheryKnight, 114405, -114058, -11200, 0, world));
			world.activeKnights.add(spawn(ArcheryKnight, 114412, -114053, -11200, 0, world));
			world.activeKnights.add(spawn(ArcheryKnight, 114940, -114053, -11200, 0, world));
			world.activeKnights.add(spawn(ArcheryKnight, 114940, -115325, -11200, 0, world));
			world.activeKnights.add(spawn(ArcheryKnight, 114412, -115325, -11200, 0, world));
		}
	}
	
	private static void spawnGlaciers(FreyaWorld world)
	{
		if (world.Glaciers.size() < 7)
		{
			final FreyaWorld Wworld = world;
			int spawn[] = GlacierSpawns[Rnd.get(GlacierSpawns.length)];
			L2Npc mob = spawn(Glacier, spawn[0], spawn[1], -11200, 0, world);
			mob.getSpawn().setOnKillDelay(0);
			mob.setIsOverloaded(true);
			world.Glaciers.add(mob);
			if (world.ArchersTimer == null)
				world.ArchersTimer = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Runnable()
				{
					@Override
					public void run()
					{
						for (L2Npc glac : Wworld.Glaciers)
							Wworld.activeMobs.add(Freya.spawn(ArchersBreath, glac.getX(), glac.getY(), glac.getZ(), 0, Wworld));
					}
				}, 10000L, 10000L);
			
			int players[] = InstanceManager.getInstance().getInstance(world.instanceId).getPlayers().toArray();
			for (int i = 0; i < players.length; i++)
			{
				int objId = players[i];
				L2PcInstance player = L2World.getInstance().getPlayer(objId);
				SkillTable.getInstance().getInfo(6437, world.Glaciers.size()).getEffects(mob, player);
			}
			
			world.GlacierTimer = ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
			{
				@Override
				public void run()
				{
					Freya.spawnGlaciers(Wworld);
				}
			}, 30000L);
		}
	}
	
	private static int moveTo(L2Npc npc, Location loc)
	{
		int time = 0;
		if (npc != null)
		{
			double distance = Util.calculateDistance(loc._x, loc._y, loc._z, npc.getX(), npc.getY(), npc.getZ(), true);
			int heading = Util.calculateHeadingFrom(npc.getX(), npc.getY(), loc._x, loc._y);
			time = (int) ((distance / npc.getRunSpeed()) * 1000D);
			npc.setRunning();
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(loc._x, loc._y, loc._z, heading));
			npc.getSpawn().setLocx(loc._x);
			npc.getSpawn().setLocy(loc._y);
			npc.getSpawn().setLocz(loc._z);
		}
		return time != 0 ? time : 100;
	}
	
	@Override
	public final String onSpawn(L2Npc npc)
	{
		npc.setDisplayEffect(1);
		startQuestTimer("changeEffect", 1900L, npc, null);
		return super.onSpawn(npc);
	}
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (npc != null && npc.getInstanceId() > 0 && (InstanceManager.getInstance().getWorld(npc.getInstanceId()) instanceof FreyaWorld))
		{
			FreyaWorld world = (FreyaWorld) InstanceManager.getInstance().getWorld(npc.getInstanceId());
			if (event.equalsIgnoreCase("changeEffect"))
				npc.setDisplayEffect(2);
			else if (event.equalsIgnoreCase("respawnKnight"))
			{
				L2Npc knight = addSpawn(ArcheryKnight, npc.getSpawn().getLocx(), npc.getSpawn().getLocy(), npc.getSpawn().getLocz(), npc.getSpawn().getHeading(), false, 0L, true, world.instanceId);
				knight.setDisplayEffect(1);
				knight.setIsImmobilized(true);
				startQuestTimer("startAggro", 90000L, knight, null);
			}
			else if (event.equalsIgnoreCase("startAggro"))
			{
				npc.setDisplayEffect(2);
				npc.setIsImmobilized(false);
				startQuestTimer("respawnKnight", 6000L, npc, null);
			}
		}
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		_log.info("onTalk: " + player.getName());
		String htmltext = "";
		if (npc.getNpcId() == JiniaStart)
		{
			_log.info("onTalk: JiniaStart");
			// If player go out from instance, can go inside again
			InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
			if (checkworld(player) == 1)
			{
				_log.info("Back: " + player.getName());
				teleportplayer(player, new Location(114025, -112300, -11200), (FreyaWorld) world);
				return "";
			}
			//
			if (GrandBossManager.getInstance().getBossStatus(FreyaStand) == DEAD)
			{
				htmltext = "<html><body>There is nothing beyond the Freay Ice Castle. Come back later.<br>(You may not enter because Freya is not inside the Ice Castle.)</body></html>";
			}
			else if (GrandBossManager.getInstance().getBossStatus(FreyaStand) == DORMANT)
			{
				/*
				if ((!player.isInParty() || !player.getParty().isLeader(player))
						|| (player.getParty().getCommandChannel() == null)
						|| (player.getParty().getCommandChannel().getChannelLeader() != player))
				{
					htmltext = "<html><body>No reaction. Contact must be initiated by the Command Channel Leader.</body></html>";
				}
				else if (player.getParty().getCommandChannel().getPartys().size() < 2 || player.getParty().getCommandChannel().getPartys().size() > 5)
				{
					htmltext = "<html><body>Your command channel needs to have at least 2 parties and a maximum of 5.</body></html>";
				}
				else
				{
				*/
				enterInstance(npc, player);
				/*
				}
				*/
			}
		}
		return htmltext;
	}
	
	public Freya(int id, String name, String descr)
	{
		super(id, name, descr);
		addEnterZoneId(12016);
		addStartNpc(JiniaStart);
		addTalkId(JiniaStart);
		addKillId(FreyaOnThrone);
		addKillId(ArcheryKnight);
		addKillId(Glakias);
		addKillId(FreyaStand);
		addKillId(Glacier);
		//addFirstTalkId(Jinia);
		//addFirstTalkId(Kegor);
		addAttackId(FreyaStand);
		addAttackId(ArcheryKnight);
		addSpawnId(Glacier);

		StatsSet info = GrandBossManager.getInstance().getStatsSet(FreyaStand);
		int status = GrandBossManager.getInstance().getBossStatus(FreyaStand);
		if (status == DEAD)
		{
			long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
			if (temp > 0)
				startQuestTimer("frintezza_unlock", temp, null, null);
			else
				GrandBossManager.getInstance().setBossStatus(FreyaStand,DORMANT);
		}
		else if (status != DORMANT)
			GrandBossManager.getInstance().setBossStatus(FreyaStand,DORMANT);
}
	
	public static void main(String args[])
	{
		new Freya(-1, "Freya", "instances");
	}
}
