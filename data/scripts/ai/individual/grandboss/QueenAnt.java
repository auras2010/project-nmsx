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

import java.util.Collection;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import javolution.util.FastList;
import l2.universe.Config;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.datatables.SpawnTable;
import l2.universe.gameserver.instancemanager.GrandBossManager;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.L2Spawn;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2GrandBossInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.zone.type.L2BossZone;
import l2.universe.gameserver.network.serverpackets.PlaySound;
import l2.universe.gameserver.network.serverpackets.SocialAction;
import l2.universe.gameserver.templates.StatsSet;
import l2.universe.util.Rnd;
import l2.universe.scripts.ai.L2AttackableAIScript;

/**
 * Queen Ant AI
 * 
 * @author Emperorc, Complete rework by Synerge
 *
 */
public class QueenAnt extends L2AttackableAIScript
{
	private static Logger _log = Logger.getLogger(QueenAnt.class.getName());
	
	private static final int QUEEN = 29001;
	private static final int LARVA = 29002;
	private static final int NURSE = 29003;
	private static final int GUARD = 29004;
	private static final int ROYAL = 29005;
	
	// QUEEN Status Tracking :
	private static final byte ALIVE = 0; // Queen Ant is spawned.
	private static final byte DEAD = 1; // Queen Ant has been killed.
	
	private static L2BossZone _Zone;
	protected static L2GrandBossInstance _Queen = null;
	private final FastList<L2Attackable> _ToDespawn = new FastList<L2Attackable>();
	
	protected ScheduledFuture<?> _activityCheckTask = null;
	protected static long _LastAction = 0;
	private static final int INACTIVITY_TIME = 59000;
	
	public QueenAnt(int questId, String name, String descr)
	{
		super(questId, name, descr);
		final int[] mobs = { QUEEN, LARVA, NURSE, GUARD, ROYAL };
		registerMobs(mobs);
		_Zone = GrandBossManager.getInstance().getZone(-21610, 181594, -5734);
		
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(QUEEN);
		switch (GrandBossManager.getInstance().getBossStatus(QUEEN))
		{
			case DEAD:
				// Load the unlock date and time for queen ant from DB
				final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
				// If queen ant is locked until a certain time, mark it so and start the unlock timer
				// the unlock time has not yet expired.
				if (temp > 0)
				{
					startQuestTimer("queen_unlock", temp, null, null);
				}
				else
				{
					// The time has already expired while the server was offline. Immediately spawn queen ant.
					_Queen = (L2GrandBossInstance) addSpawn(QUEEN, -21610, 181594, -5734, 0, false, 0);
					GrandBossManager.getInstance().setBossStatus(QUEEN, ALIVE);
					ThreadPoolManager.getInstance().scheduleGeneral(new SpawnBoss(_Queen), 200);
				}
				break;
			default:
				final int loc_x = info.getInteger("loc_x");
				final int loc_y = info.getInteger("loc_y");
				final int loc_z = info.getInteger("loc_z");
				final int heading = info.getInteger("heading");
				final int hp = info.getInteger("currentHP");
				final int mp = info.getInteger("currentMP");
				_Queen = (L2GrandBossInstance) addSpawn(QUEEN, loc_x, loc_y, loc_z, heading, false, 0);
				_Queen.setCurrentHpMp(hp, mp);
				ThreadPoolManager.getInstance().scheduleGeneral(new SpawnBoss(_Queen), 200);
				break;
		}
		
		// Guard Ants cant exit the main cave zone
		final Collection<L2Spawn> spawns =  SpawnTable.getInstance().getSpawnTable();
		for (L2Spawn npc : spawns)
		{
			if (npc == null)
				continue;
			
			if (npc.getTemplate().npcId == GUARD)
			{
				if (npc.getLastSpawn() != null)
					npc.getLastSpawn().mustRemainInZone(12012, true);
			}
		}
	}
	
	public class SpawnBoss implements Runnable
	{
		private L2GrandBossInstance queenAnt = null;
		
		public SpawnBoss(L2GrandBossInstance npc)
		{
			queenAnt = npc;
		}
		
		@Override
		public void run()
		{
			// Move players in a specific location if inside bosszone when boss spawns
			if (Rnd.get(100) < 33)
				_Zone.movePlayersTo(-19480, 187344, -5600);
			else if (Rnd.get(100) < 50)
				_Zone.movePlayersTo(-17928, 180912, -5520);
			else
				_Zone.movePlayersTo(-23808, 182368, -5600);
			
			GrandBossManager.getInstance().addBoss(queenAnt);
			startQuestTimer("action", 10000, queenAnt, null, true);
			_Zone.broadcastPacket(new PlaySound(1, "BS02_D", 1, queenAnt.getObjectId(), queenAnt.getX(), queenAnt.getY(), queenAnt.getZ()));
			
			// Spawn larva. Immortal and immobile
			final L2Npc Larva = addSpawn(LARVA, -21600, 179482, -5846, Rnd.get(360), false, 0);
			//Larva.setIsRaidMinion(true);
			Larva.setIsMortal(false);
			Larva.setIsImmobilized(true);
			_ToDespawn.add((L2Attackable) Larva);
			
			// 2 nurses behind larva
			final L2Attackable nurse1 = (L2Attackable) addSpawn(NURSE, -22000, 179482, -5846, 0, false, 0);
			final L2Attackable nurse2 = (L2Attackable) addSpawn(NURSE, -21200, 179482, -5846, 0, false, 0);
			nurse1.setIsRaidMinion(true);
			nurse1.mustRemainInZone(12012, false);
			nurse2.setIsRaidMinion(true);
			nurse2.mustRemainInZone(12012, false);
			_ToDespawn.add(nurse1);
			_ToDespawn.add(nurse2);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("action"))
		{
			if (npc != null && Rnd.get(3) == 0)
			{
				npc.broadcastPacket(new SocialAction(npc.getObjectId(), (Rnd.get(2) == 0) ? 3 : 4));
			}
		}
		else if (event.equalsIgnoreCase("queen_unlock"))
		{
			final L2GrandBossInstance queen = (L2GrandBossInstance) addSpawn(QUEEN, -21610, 181594, -5734, 0, false, 0);
			GrandBossManager.getInstance().setBossStatus(QUEEN, ALIVE);
			ThreadPoolManager.getInstance().scheduleGeneral(new SpawnBoss(queen), 200);
		}
		else if (event.equalsIgnoreCase("spawn_royal"))
		{
			if (_Queen == null || _Queen.isDead())
				return null;
			
			_Queen.spawnOneMinion(ROYAL);
		}
		else if (event.equalsIgnoreCase("spawn_nurse"))
		{
			if (_Queen == null || _Queen.isDead())
				return null;
			
			_Queen.spawnOneMinion(NURSE);
		}
		else if (event.equalsIgnoreCase("despawn_minion"))
		{
			for (L2Attackable minion : _ToDespawn)
			{
				if (minion != null)
				{					
					_ToDespawn.remove(minion);
					minion.decayMe();
				}
			}
			_ToDespawn.clear();
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFactionCall(L2Npc npc, L2Npc caller, L2PcInstance attacker, boolean isPet)
	{
		if (caller == null || npc == null)
			return super.onFactionCall(npc, caller, attacker, isPet);
				
		switch (npc.getNpcId())
		{
			// NURSES ARE CALLED
			case NURSE:
				if (npc.isCastingNow())
					return null;
				
				switch (caller.getNpcId())
				{
					// FROM LARVA
					case LARVA:
						// Add chance to heal larva, or nurses will be always on larva and never heal queen
						if (Rnd.get(100) > 50)
						{
							npc.setTarget(caller);
							//npc.doCast(SkillTable.getInstance().getInfo(4020, 1));
							npc.doCast(SkillTable.getInstance().getInfo(4024, 1));
						}
						return null;
					// FROM QUEEN
					case QUEEN:
						// If already healing the larva, return null
						if (npc.getTarget() instanceof L2Npc)
						{
							if (((L2Npc) npc.getTarget()).getNpcId() == LARVA)
							{
								if (Rnd.get(100) > 30)
									return null;
							}
						}
						/**
						if (Rnd.get(100) > 40)
						{
							npc.setTarget(caller);
							npc.doCast(SkillTable.getInstance().getInfo(4020, 1));
						}
						return null;
						*/
						break;
					// FROM OTHERS
					default:
						return null;
				}
				break;
			case LARVA:
				return null;
			// Queen doesnt answer to larva's call
			case QUEEN:
				switch (caller.getNpcId())
				{
					case LARVA:
					case GUARD:
						return null;
				}
				break;
		}

		return super.onFactionCall(npc, caller, attacker, isPet);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (!attacker.isGM() && attacker.getLevel() > Config.Max_Level_Attack_AQ)
		{
			final L2Skill tempSkill = SkillTable.FrequentSkill.RAID_CURSE2.getSkill();
			if (tempSkill != null)
			{
				attacker.abortAttack();
				attacker.abortCast();
				attacker.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				tempSkill.getEffects(attacker, attacker);
			}
			
			/* Synerge - Remove aggression against this attacker to avoid characters to tank raids or mobs
			 * without taking damage, so dont attack petrified players
			 */
			final L2Attackable npcTarget = (L2Attackable) npc;
			npcTarget.reduceHate(attacker, 99999);
			if (npcTarget.getTarget() == attacker)
			{
				npcTarget.abortAttack();
				npcTarget.abortCast();
			}
			
			return null;
		}
		
		switch (npc.getNpcId())
		{
			case QUEEN:
				_LastAction = System.currentTimeMillis();
				if (_activityCheckTask == null)
					_activityCheckTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new CheckQAActivity(), 60000, 60000);
				break;
			case NURSE:
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
				return null;
			case ROYAL:
				return null;
			case LARVA:
				break;
		}
		
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
	{
		switch (npc.getNpcId())
		{
			case NURSE:
			case ROYAL:
			case GUARD:
			case LARVA:
				if (!caster.isGM() && caster.getLevel() > Config.Max_Level_Attack_AQ)
				{
					// Npc cast raid silence on caster
					final L2Skill tempSkill = SkillTable.FrequentSkill.RAID_CURSE.getSkill();
					if (tempSkill != null)
					{
						caster.abortAttack();
						caster.abortCast();
						caster.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
						tempSkill.getEffects(npc, caster);
					}
					else
					{
						_log.warning("Skill 4215 at level 1 is missing in DP.");
					}
					
					/* Synerge - Remove aggression against this attacker to avoid characters to tank raids or mobs
					 * without taking damage, so dont attack petrified players
					 */
					final L2Attackable npcTarget = (L2Attackable) npc;
					npcTarget.reduceHate(caster, 99999);
					if (npcTarget.getTarget() == caster)
					{
						npcTarget.abortAttack();
						npcTarget.abortCast();
					}
				}
				return null;
			case QUEEN:
				returnHome(_Queen, 3000);
				return null;
		}
		return super.onSkillSee(npc, caster, skill, targets, isPet);
	}
		
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		switch (npc.getNpcId())
		{
			case QUEEN:
				_Zone.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
				GrandBossManager.getInstance().setBossStatus(QUEEN, DEAD);
				
				// Time is 36hour +/- 17hour
				final long respawnTime = (Config.Interval_Of_QueenAnt_Spawn + Rnd.get(Config.Random_Of_QueenAnt_Spawn));
				startQuestTimer("queen_unlock", respawnTime, null, null);
				cancelQuestTimer("action", npc, null);
				
				// Also save the respawn time so that the info is maintained past reboots
				final StatsSet info = GrandBossManager.getInstance().getStatsSet(QUEEN);
				info.set("loc_x", -21610);
				info.set("loc_y", 181594);
				info.set("loc_z", -5734);
				info.set("respawn_time", System.currentTimeMillis() + respawnTime);
				GrandBossManager.getInstance().setStatsSet(QUEEN, info);
				
				startQuestTimer("despawn_minion", 20000, null, null);
				if (_activityCheckTask != null)
				{
					_activityCheckTask.cancel(false);
					_activityCheckTask = null;
				}
				break;
			case ROYAL:
				if (GrandBossManager.getInstance().getBossStatus(QUEEN) == ALIVE)
				{
					_ToDespawn.remove(npc);
					startQuestTimer("spawn_royal", (80 + Rnd.get(40)) * 1000, null, null);
				}
				break;
			case NURSE:
				if (GrandBossManager.getInstance().getBossStatus(QUEEN) == ALIVE)
				{
					_ToDespawn.remove(npc);
					startQuestTimer("spawn_nurse", 10000, null, null);
				}
				break;
		}
		return super.onKill(npc, killer, isPet);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		// Ants cant go out of the main zone, and the respawn of royal guards and nurses are really fast
		switch (npc.getNpcId())
		{
			case GUARD:
			case ROYAL:
			case NURSE:							
			case QUEEN:
				npc.mustRemainInZone(12012, true);
				break;
		}

		return super.onSpawn(npc);
	}
	
	private void returnHome(L2Npc npc, int ifrange)
	{
		if (npc == null)
			return;
		
		if (!npc.isInsideRadius(npc.getSpawn().getLocx(), npc.getSpawn().getLocy(), npc.getSpawn().getLocz(), Math.max(ifrange, 100), true, false))
		{
			npc.setCurrentHp(npc.getMaxHp());
			npc.setCurrentMp(npc.getMaxMp());
			((L2Attackable) npc).getAggroList().clear();
			npc.teleToLocation(npc.getSpawn().getLocx(), npc.getSpawn().getLocy(), npc.getSpawn().getLocz(), false);
		}
	}
	
	// At end of activity time.
	private class CheckQAActivity implements Runnable
	{
		@Override
		public void run()
		{
			final Long temp = System.currentTimeMillis() - _LastAction;
			if (temp > INACTIVITY_TIME)
			{
				// After 59 sec of inactivity, QA return to spawn point
				_activityCheckTask.cancel(false);
				_activityCheckTask = null;
				_Queen.returnHome();
			}
		}
	}
	
	public static void main(String[] args)
	{
		new QueenAnt(-1, "queen_ant", "ai");
		_log.info("Level Restrict ANT QUEEN:" + Config.Max_Level_Attack_AQ);
	}
}
