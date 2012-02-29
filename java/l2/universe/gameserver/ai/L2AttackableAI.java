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
package l2.universe.gameserver.ai;

import static l2.universe.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;
import static l2.universe.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import static l2.universe.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE;

import java.util.Collection;
import java.util.concurrent.Future;
import java.util.logging.Level;

import l2.universe.Config;
import l2.universe.gameserver.GameTimeController;
import l2.universe.gameserver.GeoData;
import l2.universe.gameserver.Territory;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.datatables.NpcTable;
import l2.universe.gameserver.instancemanager.DimensionalRiftManager;
import l2.universe.gameserver.model.L2CharPosition;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.L2Skill.SkillTargetType;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.L2Playable;
import l2.universe.gameserver.model.actor.L2Summon;
import l2.universe.gameserver.model.actor.instance.L2DoorInstance;
import l2.universe.gameserver.model.actor.instance.L2FestivalMonsterInstance;
import l2.universe.gameserver.model.actor.instance.L2FriendlyMobInstance;
import l2.universe.gameserver.model.actor.instance.L2GrandBossInstance;
import l2.universe.gameserver.model.actor.instance.L2GuardInstance;
import l2.universe.gameserver.model.actor.instance.L2MonsterInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.actor.instance.L2RaidBossInstance;
import l2.universe.gameserver.model.actor.instance.L2RiftInvaderInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.templates.chars.L2NpcTemplate;
import l2.universe.gameserver.templates.chars.L2NpcTemplate.AIType;
import l2.universe.gameserver.templates.skills.L2EffectType;
import l2.universe.gameserver.templates.skills.L2SkillType;
import l2.universe.gameserver.util.Util;
import l2.universe.util.Rnd;

/**
 * This class manages AI of L2Attackable.<BR><BR>
 *
 */
public class L2AttackableAI extends L2CharacterAI implements Runnable
{	
	private static final int RANDOM_WALK_RATE = 30; // confirmed
	// private static final int MAX_DRIFT_RANGE = 300;
	private static final int MAX_ATTACK_TIMEOUT = 1200; // int ticks, i.e. 2min
	
	/** The L2Attackable AI task executed every 1s (call onEvtThink method)*/
	private Future<?> _aiTask;
	
	/** The delay after which the attacked is stopped */
	private int _attackTimeout;
	
	/** The L2Attackable aggro counter */
	private int _globalAggro;
	
	/** The flag used to indicate that a thinking action is in progress */
	private boolean _thinking; // to prevent recursive thinking
	
	private int _timePass = 0;
	private int _chaosTime = 0;
	private L2NpcTemplate _skillRender;
	int lastBuffTick;
	
	/**
	 * Constructor of L2AttackableAI.
	 *
	 * @param accessor The AI accessor of the L2Character
	 *
	 */
	public L2AttackableAI(L2Character.AIAccessor accessor)
	{
		super(accessor);
		_skillRender = NpcTable.getInstance().getTemplate(getActiveChar().getTemplate().npcId);
		//_selfAnalysis.init();
		_attackTimeout = Integer.MAX_VALUE;
		_globalAggro = -10; // 10 seconds timeout of ATTACK after respawn
	}
	
	public void run()
	{
		// Launch actions corresponding to the Event Think
		onEvtThink();
	}
	
	/**
	 * Return True if the target is autoattackable (depends on the actor type).<BR><BR>
	 *
	 * <B><U> Actor is a L2GuardInstance</U> :</B><BR><BR>
	 * <li>The target isn't a Folk or a Door</li>
	 * <li>The target isn't dead, isn't invulnerable, isn't in silent moving mode AND too far (>100)</li>
	 * <li>The target is in the actor Aggro range and is at the same height</li>
	 * <li>The L2PcInstance target has karma (=PK)</li>
	 * <li>The L2MonsterInstance target is aggressive</li><BR><BR>
	 *
	 * <B><U> Actor is a L2SiegeGuardInstance</U> :</B><BR><BR>
	 * <li>The target isn't a Folk or a Door</li>
	 * <li>The target isn't dead, isn't invulnerable, isn't in silent moving mode AND too far (>100)</li>
	 * <li>The target is in the actor Aggro range and is at the same height</li>
	 * <li>A siege is in progress</li>
	 * <li>The L2PcInstance target isn't a Defender</li><BR><BR>
	 *
	 * <B><U> Actor is a L2FriendlyMobInstance</U> :</B><BR><BR>
	 * <li>The target isn't a Folk, a Door or another L2Npc</li>
	 * <li>The target isn't dead, isn't invulnerable, isn't in silent moving mode AND too far (>100)</li>
	 * <li>The target is in the actor Aggro range and is at the same height</li>
	 * <li>The L2PcInstance target has karma (=PK)</li><BR><BR>
	 *
	 * <B><U> Actor is a L2MonsterInstance</U> :</B><BR><BR>
	 * <li>The target isn't a Folk, a Door or another L2Npc</li>
	 * <li>The target isn't dead, isn't invulnerable, isn't in silent moving mode AND too far (>100)</li>
	 * <li>The target is in the actor Aggro range and is at the same height</li>
	 * <li>The actor is Aggressive</li><BR><BR>
	 *
	 * @param target The targeted L2Object
	 *
	 */
	private boolean autoAttackCondition(L2Character target)
	{
		if (target == null || getActiveChar() == null)
			return false;
		
		// Check if the target isn't dead
		if (target.isAlikeDead())
			return false;
		
		// Check if the target isn't invulnerable
		if (target.isInvul())
		{
			// However EffectInvincible requires to check GMs specially
			if (target instanceof L2PcInstance && ((L2PcInstance) target).isGM())
				return false;
			if (target instanceof L2Summon && ((L2Summon) target).getOwner().isGM())
				return false;
		}
		
		// Check if the target isn't a Folk or a Door
		if (target instanceof L2DoorInstance)
		{
			boolean isCastle = (((L2DoorInstance) target).getCastle() != null
					&& ((L2DoorInstance) target).getCastle().getCastleId() > 0
					&& ((L2DoorInstance) target).getCastle().getSiege().getIsInProgress());
			boolean isFort = (((L2DoorInstance) target).getFort() != null
					&& ((L2DoorInstance) target).getFort().getFortId() > 0
					&& ((L2DoorInstance) target).getFort().getSiege().getIsInProgress()
					&& !((L2DoorInstance) target).getIsCommanderDoor());
			if (!isCastle && !isFort)
				return false;
			else
			{
				if (isCastle || isFort)
					return true;
			}
		}
		
 		L2Attackable me = getActiveChar();
		
		// Check if the target is a L2PlayableInstance
		if (target instanceof L2Playable)
		{
			// Check if the target is in the Aggro range and is at the same height
			if (!me.isInsideRadius(target, me.getAggroRange(), true, false))
				return false;
			
			// Check if the AI isn't a Raid Boss, can See Silent Moving players and the target isn't in silent move mode
			if (!me.isRaid() && !me.canSeeThroughSilentMove() && ((L2Playable) target).isSilentMoving())
				return false;
			
			// Check if the target is a L2PcInstance
			if (target instanceof L2PcInstance)
			{
				L2PcInstance targetPC = (L2PcInstance) target;
				
				// Don't take the aggro if the GM has the access level below or equal to GM_DONT_TAKE_AGGRO
				if (targetPC.isGM() && !targetPC.getAccessLevel().canTakeAggro())
					return false;

				// Check if player is an ally (comparing mem addr)
				if ("varka_silenos_clan".equals(me.getFactionId()) && targetPC.isAlliedWithVarka())
					return false;
				if ("ketra_orc_clan".equals(me.getFactionId()) && targetPC.isAlliedWithKetra())
					return false;
				
				// check if the target is within the grace period for JUST getting up from fake death
				if (targetPC.isRecentFakeDeath())
					return false;
				
				if (target.isInParty() && target.getParty().isInDimensionalRift())
				{
					byte riftType = target.getParty().getDimensionalRift().getType();
					byte riftRoom = target.getParty().getDimensionalRift().getCurrentRoom();
					
					if (me instanceof L2RiftInvaderInstance && !DimensionalRiftManager.getInstance().getRoom(riftType, riftRoom).checkIfInZone(me.getX(), me.getY(), me.getZ()))
						return false;
				}
				
				//if (_selfAnalysis.cannotMoveOnLand && !target.isInsideZone(L2Character.ZONE_WATER))
				//	return false;
			}		
			// Check if the target is a L2Summon
			else if (target instanceof L2Summon)
			{
				L2PcInstance owner = ((L2Summon) target).getOwner();
				if (owner != null)
				{
					// Don't take the aggro if the GM has the access level below or equal to GM_DONT_TAKE_AGGRO
					if (owner.isGM() && !owner.getAccessLevel().canTakeAggro())
						return false;
					
					// Check if player is an ally (comparing mem addr)
					if ("varka_silenos_clan".equals(me.getFactionId()) && owner.isAlliedWithVarka())
						return false;
					if ("ketra_orc_clan".equals(me.getFactionId()) && owner.isAlliedWithKetra())
						return false;
					
					if (owner.isInParty() && owner.getParty().isInDimensionalRift())
					{
						final byte riftType = owner.getParty().getDimensionalRift().getType();
						final byte riftRoom = owner.getParty().getDimensionalRift().getCurrentRoom();
						
						if (me instanceof L2RiftInvaderInstance && !DimensionalRiftManager.getInstance().getRoom(riftType, riftRoom).checkIfInZone(me.getX(), me.getY(), me.getZ()))
							return false;
					}
				}
			}
		}
		
		// Chance to forget attackers after some time 
		if (_actor.getCurrentHp() == _actor.getMaxHp()
				&& _actor.getCurrentMp() == _actor.getMaxMp()
				&& !_actor.getAttackByList().isEmpty()
				&& Rnd.nextInt(500) == 0)
		{
			me.clearAggroList();
			me.getAttackByList().clear();
			if (me instanceof L2MonsterInstance)
			{
				if (((L2MonsterInstance)me).hasMinions())
					((L2MonsterInstance)me).getMinionList().deleteReusedMinions();
			}
		}
		
		// Check if the actor is a L2GuardInstance
		if (me instanceof L2GuardInstance)
		{
			// Check if the L2PcInstance target has karma (=PK)
			if (target instanceof L2PcInstance && ((L2PcInstance) target).getKarma() > 0)
				return GeoData.getInstance().canSeeTarget(me, target);
			
			// Check if the L2MonsterInstance target is aggressive
			if (target instanceof L2MonsterInstance && Config.GUARD_ATTACK_AGGRO_MOB)
				return (((L2MonsterInstance) target).isAggressive() && GeoData.getInstance().canSeeTarget(me, target));
			
			return false;
		}
		else if (me instanceof L2FriendlyMobInstance)
		{
			// Check if the target isn't another L2Npc
			if (target instanceof L2Npc)
				return false;
			
			// Check if the L2PcInstance target has karma (=PK)
			if (target instanceof L2PcInstance && ((L2PcInstance) target).getKarma() > 0)
				return GeoData.getInstance().canSeeTarget(me, target);

			return false;
		}
		else
		{
			if (target instanceof L2Attackable)
			{
				L2Attackable targetAt = (L2Attackable) target;
				if (me.getEnemyClan() == null || targetAt.getClan() == null)
					return false;
				
				if (!target.isAutoAttackable(me))
					return false;
				
				if (me.getEnemyClan().equals(targetAt.getClan()))
				{
					if (me.isInsideRadius(target, me.getEnemyRange(), false, false))
						return GeoData.getInstance().canSeeTarget(me, target);
					
					return false;
				}
				
				if (me.getIsChaos() > 0 && me.isInsideRadius(target, me.getIsChaos(), false, false))
				{
					if (me.getFactionId() != null && me.getFactionId().equals(targetAt.getFactionId()))
						return false;

					return GeoData.getInstance().canSeeTarget(me, target);
				}
				
				return false;
			}
			
			if (target instanceof L2Npc)
				return false;
			
			// depending on config, do not allow mobs to attack _new_ players in peacezones,
			// unless they are already following those players from outside the peacezone.
			if (!Config.ALT_MOB_AGRO_IN_PEACEZONE && target.isInsideZone(L2Character.ZONE_PEACE))
				return false;
			
			if (me.isChampion() && Config.MOD_CHAMPION_PASSIVE)
				return false;
			
			// Check if the actor is Aggressive
			return GeoData.getInstance().canSeeTarget(me, target);
		}
	}
	
	/**
	 * Synerge
	 * @return true if the mob can check for aggresiveness on autoattackcondition
	 */
	private boolean canCallAggresion()
	{
		final L2Attackable me = getActiveChar();
		
		// If is aggresive
		if (me.isAggressive())
			return true;
		
		// If is FriendlyMob
		if (me instanceof L2FriendlyMobInstance)
			return true;
		
		// If is a Guard
		if (me instanceof L2GuardInstance)
			return true;
		
		// If is confused
		if (me.getIsChaos() > 0)
			return true;
		
		// If can attack other enemy mobs
		if (me.getEnemyRange() > 0)
			return true;
		
		return false;
	}
	
	public void startAITask()
	{
		// If not idle - create an AI task (schedule onEvtThink repeatedly)
		if (_aiTask == null)
			_aiTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(this, 1000, 1000);
	}
	
	@Override
	public void stopAITask()
	{
		if (_aiTask != null)
		{
			_aiTask.cancel(false);
			_aiTask = null;
		}
		super.stopAITask();
	}
	
	/**
	 * Set the Intention of this L2CharacterAI and create an  AI Task executed every 1s (call onEvtThink method) for this L2Attackable.<BR><BR>
	 *
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : If actor _knowPlayer isn't EMPTY, AI_INTENTION_IDLE will be change in AI_INTENTION_ACTIVE</B></FONT><BR><BR>
	 *
	 * @param intention The new Intention to set to the AI
	 * @param arg0 The first parameter of the Intention
	 * @param arg1 The second parameter of the Intention
	 *
	 */
	@Override
	synchronized void changeIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		if (intention == AI_INTENTION_IDLE || intention == AI_INTENTION_ACTIVE)
		{
			// Check if actor is not dead
			final L2Attackable npc = getActiveChar();
			if (!npc.isAlikeDead())
			{
				// If its _knownPlayer isn't empty set the Intention to AI_INTENTION_ACTIVE
				if (!npc.getKnownList().getKnownPlayers().isEmpty())
					intention = AI_INTENTION_ACTIVE;
				else
				{
					if (npc.getSpawn() != null)
					{
						final int range = Config.MAX_DRIFT_RANGE;
						if (!npc.isInsideRadius(npc.getSpawn().getLocx(), npc.getSpawn().getLocy(), npc.getSpawn().getLocz(), range + range, true, false))
							intention = AI_INTENTION_ACTIVE;
					}
				}
			}
			
			if (intention == AI_INTENTION_IDLE)
			{
				// Set the Intention of this L2AttackableAI to AI_INTENTION_IDLE
				super.changeIntention(AI_INTENTION_IDLE, null, null);
				
				// Stop AI task and detach AI from NPC
				if (_aiTask != null)
				{
					_aiTask.cancel(true);
					_aiTask = null;
				}
				
				// Cancel the AI
				_accessor.detachAI();
				return;
			}
		}
		
		// Set the Intention of this L2AttackableAI to intention
		super.changeIntention(intention, arg0, arg1);
		
		// If not idle - create an AI task (schedule onEvtThink repeatedly)
		startAITask();
	}
	
	/**
	 * Manage the Attack Intention : Stop current Attack (if necessary), Calculate attack timeout,
	 * Start a new Attack and Launch Think Event.
	 *
	 * @param target The L2Character to attack
	 */
	@Override
	protected void onIntentionAttack(L2Character target)
	{
		// Calculate the attack timeout
		_attackTimeout = MAX_ATTACK_TIMEOUT + GameTimeController.getGameTicks();
		
		// self and buffs		
		if (lastBuffTick + 30 < GameTimeController.getGameTicks())
		{
			if (_skillRender.hasBuffSkill())
			{
				for (L2Skill sk : _skillRender._buffskills)
					if (cast(sk))
						break;
			}
			
			lastBuffTick = GameTimeController.getGameTicks();
		}
		
		// Manage the Attack Intention : Stop current Attack (if necessary), Start a new Attack and Launch Think Event
		super.onIntentionAttack(target);
	}
	
	private void thinkCast()
	{
		if (checkTargetLost(getCastTarget()))
		{
			setCastTarget(null);
			return;
		}
		
		if (maybeMoveToPawn(getCastTarget(), _actor.getMagicalAttackRange(_skill)))
			return;
			
		clientStopMoving(null);
		setIntention(AI_INTENTION_ACTIVE);
		_accessor.doCast(_skill);
	}
	
	/**
	 * Manage AI standard thinks of a L2Attackable (called by onEvtThink).<BR><BR>
	 *
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Update every 1s the _globalAggro counter to come close to 0</li>
	 * <li>If the actor is Aggressive and can attack, add all autoAttackable L2Character in its Aggro Range to its _aggroList, chose a target and order to attack it</li>
	 * <li>If the actor is a L2GuardInstance that can't attack, order to it to return to its home location</li>
	 * <li>If the actor is a L2MonsterInstance that can't attack, order to it to random walk (1/100)</li><BR><BR>
	 *
	 */
	private void thinkActive()
	{
		final L2Attackable npc = getActiveChar();
		
		// Update every 1s the _globalAggro counter to come close to 0
		if (_globalAggro != 0)
		{
			if (_globalAggro < 0)
				_globalAggro++;
			else
				_globalAggro--;
		}
		
		// Add all autoAttackable L2Character in L2Attackable Aggro Range to its _aggroList with 0 damage and 1 hate
		// A L2Attackable isn't aggressive during 10s after its spawn because _globalAggro is set to -10
		if (_globalAggro >= 0)
		{
			// Get all visible objects inside its Aggro Range
			/*
			 * Synerge - Only check aggresiveness for mobs that can be aggresive, have enemy mobs, guards, friendlyMob, or in chaos
			 * not pasive mobs, saved at least half of npc checks with this :S
			 */
			if (canCallAggresion())
			{
				Collection<L2Object> objs = npc.getKnownList().getKnownObjects().values();
				try
				{
					for (L2Object obj : objs)
					{
						if (!(obj instanceof L2Character))
							continue;
						
						/*
						 * Check to see if this is a festival mob spawn.
						 * If it is, then check to see if the aggro trigger
						 * is a festival participant...if so, move to attack it.
						 */
						if (npc instanceof L2FestivalMonsterInstance && obj instanceof L2PcInstance)
						{								
							if (!(((L2PcInstance) obj).isFestivalParticipant()))
								continue;
						}
						
						L2Character target = (L2Character) obj;
						
						// TODO: The AI Script ought to handle aggro behaviors in onSee.  Once implemented, aggro behaviors ought
						// to be removed from here.  (Fulminus)
						// For each L2Character check if the target is autoattackable
						if (autoAttackCondition(target)) // check aggression
						{
							// Get the hate level of the L2Attackable against this L2Character target contained in _aggroList
							int hating = npc.getHating(target);
							
							// Add the attacker to the L2Attackable _aggroList with 0 damage and 1 hate
							if (hating == 0)
								npc.addDamageHate(target, 0, 0);
						}
					}
				}
				catch (NullPointerException e)
				{
					_log.info("L2AttackableAI: thinkAttack() faction call failed.");
					e.printStackTrace();
				}
			}
			
			// Chose a target from its aggroList
			L2Character hated;
			if (npc.isConfused())
				hated = getAttackTarget(); // effect handles selection
			else
				hated = npc.getMostHated();
			
			// Order to the L2Attackable to attack the target
			if (hated != null && !npc.isCoreAIDisabled())
			{
				// Get the hate level of the L2Attackable against this L2Character target contained in _aggroList
				final int aggro = npc.getHating(hated);

				if (aggro + _globalAggro > 0)
				{
					// Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
					if (!npc.isRunning())
						npc.setRunning();
					
					// Set the AI Intention to AI_INTENTION_ATTACK
					setIntention(CtrlIntention.AI_INTENTION_ATTACK, hated);
				}
				
				return;
			}
		}
		
		/**
		// Chance to forget attackers after some time
		if (_actor.getCurrentHp() == _actor.getMaxHp() && _actor.getCurrentMp() == _actor.getMaxMp() && !_actor.getAttackByList().isEmpty() && Rnd.nextInt(500) == 0)
		{
			((L2Attackable) _actor).clearAggroList();
			_actor.getAttackByList().clear();
		}
		 */
		
		// If this is a festival monster, then it remains in the same location.
		if (npc instanceof L2FestivalMonsterInstance)
			return;
		
		// Check if the mob should not return to spawn point
		if (!npc.canReturnToSpawnPoint())
			return;
			
		/* Synerge - Spawn check for all non-raid monsters. If too far from spawn, send them home. Add also check for guards, they get stucked sometimes */
		if ((npc instanceof L2MonsterInstance || npc instanceof L2GuardInstance)
				&& !npc.isTeleToSpawn()
				&& !npc.isRaid()
				&& !npc.isAlikeDead()
				&& !npc.isDead()
				&& npc.getSpawn() != null
				&& npc.getInstanceId() == 0
				&& !npc.isInsideRadius(npc.getSpawn().getLocx(), npc.getSpawn().getLocy(), npc.getSpawn().getLocz(), Config.MAX_DRIFT_RANGE + 50, true, false))
		{
			npc.teleSpawn();
			return;
		}
		// Check if the actor is a L2GuardInstance
		else if (npc instanceof L2GuardInstance)
		{
			// Order to the L2GuardInstance to return to its home location because there's no target to attack
			((L2GuardInstance) npc).returnHome();
			return;
		}		
		
		// Minions following leader
		final L2Character leader = npc.getLeader();
		if (leader != null && !leader.isAlikeDead())
		{
			final int offset;
			final int minRadius = 30;
			
			if (npc.isRaidMinion())
				offset = 500; // for Raids - need correction
			else
				offset = 200; // for normal minions - need correction :)
				
			if (leader.isRunning())
				npc.setRunning();
			else
				npc.setWalking();
			
			if (npc.getPlanDistanceSq(leader) > offset * offset)
			{
				int x1 = Rnd.get(minRadius * 2, offset * 2); // x
				int y1 = Rnd.get(x1, offset * 2); // distance
				y1 = (int) Math.sqrt(y1 * y1 - x1 * x1); // y
				if (x1 > offset + minRadius)
					x1 = leader.getX() + x1 - offset;
				else
					x1 = leader.getX() - x1 + minRadius;
				if (y1 > offset + minRadius)
					y1 = leader.getY() + y1 - offset;
				else
					y1 = leader.getY() - y1 + minRadius;
				
				// Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)
				moveTo(x1, y1, leader.getZ());
				return;
			}
			else if (Rnd.nextInt(RANDOM_WALK_RATE) == 0)
			{
				if (_skillRender.hasBuffSkill())
				{
					for (L2Skill sk : _skillRender._buffskills)
						if (cast(sk))
							return;
				}
			}
		}
		// Order to the L2MonsterInstance to random walk (1/100)
		else if (npc.getSpawn() != null)
		{
			if (_skillRender.hasBuffSkill())
			{
				for (L2Skill sk : _skillRender._buffskills)
					if (cast(sk))
						return;
			}
			
			if ((Rnd.nextInt(RANDOM_WALK_RATE) == 0) && !npc.isNoRndWalk())
			{
				int x1, y1, z1;
				final int range = Config.MAX_DRIFT_RANGE;
				
				// If NPC with random coord in territory
				if (npc.getSpawn().getLocx() == 0 && npc.getSpawn().getLocy() == 0)
				{
					// Calculate a destination point in the spawn area
					final int p[] = Territory.getInstance().getRandomPoint(npc.getSpawn().getLocation());
					x1 = p[0];
					y1 = p[1];
					z1 = p[2];
					
					// Calculate the distance between the current position of the L2Character and the target (x,y)
					final double distance2 = npc.getPlanDistanceSq(x1, y1);
					
					if (distance2 > (range + range) * (range + range))
					{
						npc.setisReturningToSpawnPoint(true);
						float delay = (float) Math.sqrt(distance2) / range;
						x1 = npc.getX() + (int) ((x1 - npc.getX()) / delay);
						y1 = npc.getY() + (int) ((y1 - npc.getY()) / delay);
					}
					
					// If NPC with random fixed coord, don't move (unless needs to return to spawnpoint)
					if (Territory.getInstance().getProcMax(npc.getSpawn().getLocation()) > 0 && !npc.isReturningToSpawnPoint())
						return;
				}
				else
				{
					// If NPC with fixed coord
					x1 = npc.getSpawn().getLocx();
					y1 = npc.getSpawn().getLocy();
					z1 = npc.getSpawn().getLocz();
					
					if (!npc.isInsideRadius(x1, y1, range, false))
						npc.setisReturningToSpawnPoint(true);
					else if (range > 0)
					{
						x1 = Rnd.nextInt(range * 2); // x
						y1 = Rnd.get(x1, range * 2); // distance
						y1 = (int) Math.sqrt(y1 * y1 - x1 * x1); // y
						x1 += npc.getSpawn().getLocx() - range;
						y1 += npc.getSpawn().getLocy() - range;
						z1 = npc.getZ();
					}
				}
				
				//_log.debug("Current pos ("+getX()+", "+getY()+"), moving to ("+x1+", "+y1+").");
				// Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)
				moveTo(x1, y1, z1);
			}
		}
	}
	
	/**
	 * Manage AI attack thinks of a L2Attackable (called by onEvtThink).<BR><BR>
	 *
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Update the attack timeout if actor is running</li>
	 * <li>If target is dead or timeout is expired, stop this attack and set the Intention to AI_INTENTION_ACTIVE</li>
	 * <li>Call all L2Object of its Faction inside the Faction Range</li>
	 * <li>Chose a target and order to attack it with magic skill or physical attack</li><BR><BR>
	 *
	 * TODO: Manage casting rules to healer mobs (like Ant Nurses)
	 *
	 */
	private void thinkAttack()
	{
		final L2Attackable me = getActiveChar();
		if (me.isCastingNow())
			return;
		
		final L2Character originalAttackTarget = getAttackTarget();
		// Check if target is dead or if timeout is expired to stop this attack
		if (originalAttackTarget == null || originalAttackTarget.isAlikeDead() || _attackTimeout < GameTimeController.getGameTicks())
		{
			// Stop hating this target after the attack timeout or if target is dead
			if (originalAttackTarget != null)
				me.stopHating(originalAttackTarget);
			
			// Set the AI Intention to AI_INTENTION_ACTIVE
			setIntention(AI_INTENTION_ACTIVE);
			
			me.setWalking();
			return;
		}
		
		final int collision = me.getTemplate().collisionRadius;
		
		// Handle all L2Object of its Faction inside the Faction Range
		String faction_id = me.getFactionId();
		if (faction_id != null && !faction_id.isEmpty() && me.getClanRange() > 0 && me.getAI() != null)
		{		
			int factionRange = me.getClanRange() + collision;				
			// Go through all L2Object that belong to its faction
			Collection<L2Object> objs = me.getKnownList().getKnownObjects().values();
			try
			{
				for (L2Object obj : objs)
				{
					if (!(obj instanceof L2Npc))
						continue;

					L2Npc npc = (L2Npc) obj;
					
					//Handle SevenSigns mob Factions
					final String npcfaction = npc.getFactionId();
					if (npcfaction == null || npcfaction.isEmpty())
						continue;
					
					boolean sevenSignFaction = false;
					
					// TODO: Unhardcode this by AI scripts (DrHouse)
					// Catacomb mobs should assist lilim and nephilim other than dungeon
					if ("c_dungeon_clan".equals(faction_id) && ("c_dungeon_lilim".equals(npcfaction) || "c_dungeon_nephi".equals(npcfaction)))
						sevenSignFaction = true;					
					else if ("c_dungeon_clan".equals(npcfaction))
					{
						// Lilim mobs should assist other Lilim and catacomb mobs
						if ("c_dungeon_lilim".equals(faction_id))
							sevenSignFaction = true;
						// Nephilim mobs should assist other Nephilim and catacomb mobs
						else if ("c_dungeon_nephi".equals(faction_id))
							sevenSignFaction = true;
					}
					
					if (!sevenSignFaction && !faction_id.equals(npcfaction))
						continue;
					
					// Check if the L2Object is inside the Faction Range of the actor
					if (!npc.hasAI() || !me.isInsideRadius(npc, factionRange, true, false))
						continue;
					
					if (Math.abs(originalAttackTarget.getZ() - npc.getZ()) < 600 
							&& me.getAttackByList().contains(originalAttackTarget) 
							&& (npc.getAI()._intention == CtrlIntention.AI_INTENTION_IDLE || npc.getAI()._intention == CtrlIntention.AI_INTENTION_ACTIVE) 
							&& npc.getInstanceId() == me.getInstanceId())
							//&& GeoData.getInstance().canSeeTarget(_actor, npc))
					{
						if (originalAttackTarget instanceof L2Playable)
						{
							Quest[] quests = npc.getTemplate().getEventQuests(Quest.QuestEventType.ON_FACTION_CALL);
							if (quests != null)
							{
								L2PcInstance player = originalAttackTarget.getActingPlayer();
								boolean isSummon = originalAttackTarget instanceof L2Summon;
								for (Quest quest : quests)
									quest.notifyFactionCall(npc, getActiveChar(), player, isSummon);
							}
						}
						else if (npc instanceof L2Attackable 
								&& getAttackTarget() != null 
								&& npc.getAI()._intention != CtrlIntention.AI_INTENTION_ATTACK)
						{
							((L2Attackable) npc).addDamageHate(getAttackTarget(), 0, me.getHating(getAttackTarget()));
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, getAttackTarget());
						}							
					}
				}
			}
			catch (NullPointerException e)
			{
				_log.log(Level.WARNING, "L2AttackableAI: thinkAttack() faction call failed: " + e.getMessage(), e);
			}
		}
		
		if (me.isCoreAIDisabled())
			return;
		
		// ------------------------------------------------------------------------------
		// Initialize data
		final L2Character mostHate = me.getMostHated();
		if (mostHate == null)
		{
			setIntention(AI_INTENTION_ACTIVE);
			return;
		}
		
		setAttackTarget(mostHate);
		me.setTarget(mostHate);

		final int combinedCollision = collision + mostHate.getTemplate().collisionRadius;
		
		/*
		 *  Synerge - This controls when mobs will try to surround the target when many mobs are on the same spot
		 *  In case many mobs are trying to hit from same place, move a bit, circling around the target
		 */
		if (!_actor.isMovementDisabled() && Config.INTELLIGENT_FARMING_MOBS > 0 && Rnd.nextInt(100) <= Config.INTELLIGENT_FARMING_MOBS)
		{		
			final Collection<L2Character> knownOb = _actor.getKnownList().getKnownCharactersInRadius(collision);
			
			for (L2Object nearby : knownOb)
			{
				if (nearby instanceof L2Attackable && nearby != mostHate)
				{
					int newX = combinedCollision + Rnd.get(40);
					if (Rnd.nextBoolean())
						newX = mostHate.getX() + newX;
					else
						newX = mostHate.getX() - newX;
					int newY = combinedCollision + Rnd.get(40);
					if (Rnd.nextBoolean())
						newY = mostHate.getY() + newY;
					else
						newY = mostHate.getY() - newY;

					if (!me.isInsideRadius(newX, newY, collision, false))
					{
						int newZ = me.getZ() + 30;
						if (Config.GEODATA == 0 || GeoData.getInstance().canMoveFromToTarget(me.getX(), me.getY(), me.getZ(), newX, newY, newZ, me.getInstanceId()))
							moveTo(newX, newY, newZ);
					}						
					return;
				}
			}
		}
		
		// Dodge if its needed
		if (!me.isMovementDisabled() && me.getCanDodge() > 0)
		{
			if (Rnd.get(100) <= me.getCanDodge())
			{
				// Micht: kepping this one otherwise we should do 2 sqrt
				double distance2 = me.getPlanDistanceSq(mostHate.getX(), mostHate.getY());
				if (Math.sqrt(distance2) <= 60 + combinedCollision)
				{
					int posX = me.getX();
					int posY = me.getY();
					int posZ = me.getZ();

					if (Rnd.nextBoolean())
						posX = posX + Rnd.get(100);
					else
						posX = posX - Rnd.get(100);
					
					if (Rnd.nextBoolean())
						posY = posY + Rnd.get(100);
					else
						posY = posY - Rnd.get(100);
					
					if (Config.GEODATA == 0 || GeoData.getInstance().canMoveFromToTarget(me.getX(), me.getY(), posZ, posX, posY, posZ, me.getInstanceId()))
						setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(posX, posY, posZ, 0));
					return;
				}
			}
		}
		
		// ------------------------------------------------------------------------------
		// BOSS/Raid Minion Target Reconsider
		if (me.isRaid() || me.isRaidMinion())
		{
			_chaosTime++;
			if (me instanceof L2RaidBossInstance)
			{
				if (!((L2MonsterInstance) me).hasMinions())
				{
					if (_chaosTime > Config.RAID_CHAOS_TIME)
						if (Rnd.get(100) <= 100 - (me.getCurrentHp() * 100 / me.getMaxHp()))
						{
							aggroReconsider();
							_chaosTime = 0;
							return;
						}
				}
				else
				{
					if (_chaosTime > Config.RAID_CHAOS_TIME)
						if (Rnd.get(100) <= 100 - (me.getCurrentHp() * 200 / me.getMaxHp()))
						{
							aggroReconsider();
							_chaosTime = 0;
							return;
						}
				}
			}
			else if (me instanceof L2GrandBossInstance)
			{
				if (_chaosTime > Config.GRAND_CHAOS_TIME)
				{
					double chaosRate = 100 - (me.getCurrentHp() * 300 / me.getMaxHp());
					if ((chaosRate <= 10 && Rnd.get(100) <= 10) || (chaosRate > 10 && Rnd.get(100) <= chaosRate))
					{
						aggroReconsider();
						_chaosTime = 0;
						return;
					}
				}
			}
			else
			{
				if (_chaosTime > Config.MINION_CHAOS_TIME)
					if (Rnd.get(100) <= 100 - (me.getCurrentHp() * 200 / me.getMaxHp()))
					{
						aggroReconsider();
						_chaosTime = 0;
						return;
					}
			}
		}
		
		if (_skillRender.hasSkill())
		{
			// -------------------------------------------------------------------------------
			// Heal Condition
			if (_skillRender.hasHealSkill() && _skillRender._healskills != null)
			{
				double percentage = me.getCurrentHp() / me.getMaxHp() * 100;
				if (me.isMinion())
				{
					L2Character leader = me.getLeader();
					if (leader != null && !leader.isDead() && Rnd.get(100) > (leader.getCurrentHp() / leader.getMaxHp() * 100))
					{
						for (L2Skill sk : _skillRender._healskills)
						{
							if (sk.getTargetType() == L2Skill.SkillTargetType.TARGET_SELF)
								continue;
							
							if (cantCastSkill(me, sk))
								continue;

							if (!Util.checkIfInRange((sk.getCastRange() + collision + leader.getTemplate().collisionRadius), me, leader, false) && !isParty(sk) && !me.isMovementDisabled())
							{
								moveToPawn(leader, sk.getCastRange() + collision + leader.getTemplate().collisionRadius);
								return;
							}

							if (GeoData.getInstance().canSeeTarget(me, leader))
							{
								clientStopMoving(null);
								me.setTarget(leader);
								clientStopMoving(null);
								me.doCast(sk);
								return;
							}
						}
					}
				}
				
				if (Rnd.get(100) < (100 - percentage) / 3)
				{
					for (L2Skill sk : _skillRender._healskills)
					{
						if (cantCastSkill(_actor, sk))
							continue;

						clientStopMoving(null);
						me.setTarget(me);
						me.doCast(sk);
						return;
					}
				}
				
				for (L2Skill sk : _skillRender._healskills)
				{
					if (cantCastSkill(me, sk))
						continue;
					
					if (sk.getTargetType() == L2Skill.SkillTargetType.TARGET_ONE)
					{
						for (L2Character obj : _actor.getKnownList().getKnownCharactersInRadius(sk.getCastRange() + _actor.getTemplate().collisionRadius))
						{
							if (!(obj instanceof L2Attackable) || obj.isDead())
								continue;
							
							final L2Attackable targets = ((L2Attackable) obj);
							if (me.getFactionId() != null && !me.getFactionId().equals(targets.getFactionId()))
								continue;
							
							percentage = targets.getCurrentHp() / targets.getMaxHp() * 100;
							if (Rnd.get(100) < (100 - percentage) / 10)
							{
								if (GeoData.getInstance().canSeeTarget(me, targets))
								{
									clientStopMoving(null);
									me.setTarget(obj);
									me.doCast(sk);
									return;
								}
							}
						}
					}
					
					if (isParty(sk))
					{
						clientStopMoving(null);
						me.doCast(sk);
						return;
					}
				}
			}
			
			// -------------------------------------------------------------------------------
			// Res Skill Condition
			if (_skillRender.hasResSkill())
			{
				if (me.isMinion())
				{
					final L2Character leader = me.getLeader();
					if (leader != null && leader.isDead())
					{
						for (L2Skill sk : _skillRender._resskills)
						{
							if (sk.getTargetType() == L2Skill.SkillTargetType.TARGET_SELF)
								continue;
							
							if (cantCastSkill(_actor, sk))
								continue;
							
							if (!Util.checkIfInRange((sk.getCastRange() + collision + leader.getTemplate().collisionRadius), _actor, leader, false) && !isParty(sk) && !_actor.isMovementDisabled())
							{
								moveToPawn(leader, sk.getCastRange() + collision + leader.getTemplate().collisionRadius);
								return;
							}
							
							if (GeoData.getInstance().canSeeTarget(me, leader))
							{
								clientStopMoving(null);
								me.setTarget(leader);
								me.doCast(sk);
								return;
							}
						}
					}
				}
				
				for (L2Skill sk : _skillRender._resskills)
				{
					if (cantCastSkill(me, sk))
						continue;

					if (sk.getTargetType() == L2Skill.SkillTargetType.TARGET_ONE)
					{
						for (L2Character obj : _actor.getKnownList().getKnownCharactersInRadius(sk.getCastRange() + collision))
						{
							if (!(obj instanceof L2Attackable) || !obj.isDead())
								continue;
							
							final L2Attackable targets = ((L2Attackable) obj);
							if (me.getFactionId() != null && !me.getFactionId().equals(targets.getFactionId()))
								continue;
							
							if (Rnd.get(100) < 10)
							{
								if (GeoData.getInstance().canSeeTarget(me, targets))
								{
									clientStopMoving(null);
									me.setTarget(obj);
									me.doCast(sk);
									return;
								}
							}
						}
					}
					
					if (isParty(sk))
					{
						clientStopMoving(null);
						final L2Object target = getAttackTarget();
						me.setTarget(me);
						me.doCast(sk);
						me.setTarget(target);
						return;
					}
				}
			}
		}
		
		double dist = Math.sqrt(me.getPlanDistanceSq(mostHate.getX(), mostHate.getY()));
		int dist2 = (int) dist - collision;
		int range = me.getPhysicalAttackRange() + combinedCollision;
		if (mostHate.isMoving())
		{
			range = range + 50;
			if (me.isMoving())
				range = range + 50;
		}

		// -------------------------------------------------------------------------------
		// Immobilize Condition
		if ((me.isMovementDisabled() && (dist > range || mostHate.isMoving())) || (dist > range && mostHate.isMoving()))
		{
			movementDisable();
			return;
		}
		
		setTimepass(0);
		
		// --------------------------------------------------------------------------------
		// Skill Use 
		if (_skillRender.hasSkill())
		{
			if (Rnd.get(100) <= me.getSkillChance())
			{
				final L2Skill skills = _skillRender._generalskills.get(Rnd.nextInt(_skillRender._generalskills.size()));
				if (cast(skills))
					return;
				
				for (L2Skill sk : _skillRender._generalskills)
					if (cast(sk))
						return;
			}
			
			// --------------------------------------------------------------------------------
			// Long/Short Range skill Usage
			if (me.hasLSkill() || me.hasSSkill())
			{
				if (me.hasSSkill() && dist2 <= 150 && Rnd.get(100) <= me.getSSkillChance())
				{
					sSkillRender();
					if (_skillRender._Srangeskills != null)
					{
						final L2Skill skills = _skillRender._Srangeskills.get(Rnd.nextInt(_skillRender._Srangeskills.size()));
						if (cast(skills))
							return;
						
						for (L2Skill sk : _skillRender._Srangeskills)
							if (cast(sk))
								return;
					}
				}
				
				if (me.hasLSkill() && dist2 > 150 && Rnd.get(100) <= me.getLSkillChance())
				{
					lSkillRender();
					if (_skillRender._Lrangeskills != null)
					{
						final L2Skill skills = _skillRender._Lrangeskills.get(Rnd.nextInt(_skillRender._Lrangeskills.size()));
						if (cast(skills))
							return;
						
						for (L2Skill sk : _skillRender._Lrangeskills)
							if (cast(sk))
								return;
					}
				}
			}
		}
		
		// --------------------------------------------------------------------------------
		// Starts Melee or Primary Skill
		if (dist2 > range || !GeoData.getInstance().canSeeTarget(me, mostHate))
		{
			if (me.isMovementDisabled())
			{
				targetReconsider();
				return;
			}

			if (getAttackTarget().isMoving())
				range -= 100;
			if (range < 5)
				range = 5;
			moveToPawn(getAttackTarget(), range);
		}
		else
		{
			melee(me.getPrimaryAttack());
		}
		
	}
	
	private void melee(int type)
	{
		if (type != 0)
		{
			switch (type)
			{
				case -1:
				{
					if (_skillRender._generalskills != null)
					{
						for (L2Skill sk : _skillRender._generalskills)
						{
							if (cast(sk))
								return;
						}
					}
					break;
				}
				case 1:
				{
					if (_skillRender.hasAtkSkill())
					{
						for (L2Skill sk : _skillRender._atkskills)
						{
							if (cast(sk))
								return;
						}
					}
					break;
				}
				default:
				{
					if (_skillRender._generalskills != null)
					{
						for (L2Skill sk : _skillRender._generalskills)
						{
							if (sk.getId() == getActiveChar().getPrimaryAttack())
							{
								if (cast(sk))
									return;
							}
						}
					}
				}
                                break;
			}
		}
		
		_accessor.doAttack(getAttackTarget());
	}
	
	private boolean cast(L2Skill sk)
	{
		if (sk == null)
			return false;
			
		final L2Attackable activeChar = getActiveChar();	
		
		if (activeChar.isCastingNow() && !sk.isSimultaneousCast())
			return false;
		
		if (cantCastSkill(activeChar, sk))
			return false;

		if (getAttackTarget() == null)
		{
			if (activeChar.getMostHated() != null)
				setAttackTarget(activeChar.getMostHated());
		}
		
		L2Character attackTarget = getAttackTarget();
		if (attackTarget == null)
			return false;
		
		double dist = Math.sqrt(activeChar.getPlanDistanceSq(attackTarget.getX(), attackTarget.getY()));
		double dist2 = dist - attackTarget.getTemplate().collisionRadius;
		double range = activeChar.getPhysicalAttackRange() + activeChar.getTemplate().collisionRadius + attackTarget.getTemplate().collisionRadius;
		double srange = sk.getCastRange() + activeChar.getTemplate().collisionRadius;
		if (attackTarget.isMoving())
			dist2 = dist2 - 30;
		
		SkillTargetType skTarget = sk.getTargetType();
		
		switch (sk.getSkillType())
		{
			case BUFF:
			case REFLECT:
			{
				if (activeChar.getFirstEffect(sk) == null)
				{
					clientStopMoving(null);
					//final L2Object target = attackTarget;
					activeChar.setTarget(activeChar);
					activeChar.doCast(sk);
					//_actor.setTarget(target);
					return true;
				}
				
				//----------------------------------------
				//If actor already have buff, start looking at others same faction mob to cast
				switch (skTarget)
				{
					case TARGET_SELF:
						return false;
					case TARGET_ONE:
						final L2Character target = effectTargetReconsider(sk, true);
						if (target != null)
						{
							clientStopMoving(null);
							final L2Object targets = attackTarget;
							activeChar.setTarget(target);
							activeChar.doCast(sk);
							activeChar.setTarget(targets);
							return true;
						}
				}
				
				if (canParty(sk))
				{
					clientStopMoving(null);
					final L2Object targets = attackTarget;
					activeChar.setTarget(activeChar);
					activeChar.doCast(sk);
					activeChar.setTarget(targets);
					return true;
				}
				break;
			}
			case HEAL:
			case HOT:
			case HEAL_PERCENT:
			case HEAL_STATIC:
			case BALANCE_LIFE:
			{
				double percentage = activeChar.getCurrentHp() / activeChar.getMaxHp() * 100;
				if (activeChar.isMinion() && skTarget != L2Skill.SkillTargetType.TARGET_SELF)
				{
					final L2Character leader = activeChar.getLeader();
					if (leader != null && !leader.isDead() && Rnd.get(100) > (leader.getCurrentHp() / leader.getMaxHp() * 100))
					{
						if (!Util.checkIfInRange((sk.getCastRange() + activeChar.getTemplate().collisionRadius + leader.getTemplate().collisionRadius), activeChar, leader, false) && !isParty(sk) && !activeChar.isMovementDisabled())
							moveToPawn(leader, sk.getCastRange() + activeChar.getTemplate().collisionRadius + leader.getTemplate().collisionRadius);
						
						if (GeoData.getInstance().canSeeTarget(activeChar, leader))
						{
							clientStopMoving(null);
							activeChar.setTarget(leader);
							activeChar.doCast(sk);
							return true;
						}
					}
				}
				
				if (Rnd.get(100) < (100 - percentage) / 3)
				{
					clientStopMoving(null);
					activeChar.setTarget(activeChar);
					activeChar.doCast(sk);
					return true;
				}
				
				if (skTarget == L2Skill.SkillTargetType.TARGET_ONE)
				{
					for (L2Character obj : activeChar.getKnownList().getKnownCharactersInRadius(sk.getCastRange() + activeChar.getTemplate().collisionRadius))
					{
						if (!(obj instanceof L2Attackable) || obj.isDead())
							continue;
						
						final L2Attackable targets = ((L2Attackable) obj);
						if (activeChar.getFactionId() != null && !activeChar.getFactionId().equals(targets.getFactionId()))
							continue;
						
						percentage = targets.getCurrentHp() / targets.getMaxHp() * 100;
						if (Rnd.get(100) < (100 - percentage) / 10)
						{
							if (GeoData.getInstance().canSeeTarget(activeChar, targets))
							{
								clientStopMoving(null);
								activeChar.setTarget(obj);
								activeChar.doCast(sk);
								return true;
							}
						}
					}
				}
				
				if (isParty(sk))
				{
					for (L2Character obj : activeChar.getKnownList().getKnownCharactersInRadius(sk.getSkillRadius() + activeChar.getTemplate().collisionRadius))
					{
						if (!(obj instanceof L2Attackable))
							continue;

						final L2Npc targets = ((L2Npc) obj);
						if (activeChar.getFactionId() != null && targets.getFactionId().equals(activeChar.getFactionId()))
						{
							if (obj.getCurrentHp() < obj.getMaxHp() && Rnd.get(100) <= 20)
							{
								clientStopMoving(null);
								activeChar.setTarget(activeChar);
								activeChar.doCast(sk);
								return true;
							}
						}
					}
				}
				break;
			}
			case RESURRECT:
			{
				if (!isParty(sk))
				{
					if (activeChar.isMinion() && skTarget != L2Skill.SkillTargetType.TARGET_SELF)
					{
						final L2Character leader = activeChar.getLeader();
						if (leader != null && leader.isDead())
						{
							if (!Util.checkIfInRange((sk.getCastRange() + activeChar.getTemplate().collisionRadius + leader.getTemplate().collisionRadius), activeChar, leader, false) && !isParty(sk) && !activeChar.isMovementDisabled())
								moveToPawn(leader, sk.getCastRange() + activeChar.getTemplate().collisionRadius + leader.getTemplate().collisionRadius);
						}
						
						if (GeoData.getInstance().canSeeTarget(activeChar, leader))
						{
							clientStopMoving(null);
							activeChar.setTarget(leader);
							activeChar.doCast(sk);
							return true;
						}
					}
					
					for (L2Character obj : activeChar.getKnownList().getKnownCharactersInRadius(sk.getCastRange() + activeChar.getTemplate().collisionRadius))
					{
						if (!(obj instanceof L2Attackable) || !obj.isDead())
							continue;
						
						final L2Attackable targets = ((L2Attackable) obj);
						if (activeChar.getFactionId() != null && !activeChar.getFactionId().equals(targets.getFactionId()))
							continue;
						
						if (Rnd.get(100) < 10)
						{
							if (GeoData.getInstance().canSeeTarget(activeChar, targets))
							{
								clientStopMoving(null);
								activeChar.setTarget(obj);
								activeChar.doCast(sk);
								return true;
							}
						}
					}
				}
				else if (isParty(sk))
				{
					for (L2Character obj : activeChar.getKnownList().getKnownCharactersInRadius(sk.getSkillRadius() + activeChar.getTemplate().collisionRadius))
					{
						if (!(obj instanceof L2Attackable))
							continue;

						L2Npc targets = ((L2Npc) obj);
						if (activeChar.getFactionId() != null && activeChar.getFactionId().equals(targets.getFactionId()))
						{
							if (obj.getCurrentHp() < obj.getMaxHp() && Rnd.get(100) <= 20)
							{
								clientStopMoving(null);
								activeChar.setTarget(activeChar);
								activeChar.doCast(sk);
								return true;
							}
						}
					}
				}
				break;
			}
			case DEBUFF:
			case WEAKNESS:
			case POISON:
			case DOT:
			case MDOT:
			case BLEED:
			{
				if (GeoData.getInstance().canSeeTarget(activeChar, attackTarget) && !canAOE(sk) && !attackTarget.isDead() && dist2 <= srange)
				{
					if (attackTarget.getFirstEffect(sk) == null)
					{
						clientStopMoving(null);
						activeChar.doCast(sk);
						return true;
					}
				}
				else if (canAOE(sk))
				{
					switch (skTarget)
					{
						case TARGET_AURA:
						case TARGET_BEHIND_AURA:
						case TARGET_FRONT_AURA:
							clientStopMoving(null);
							activeChar.doCast(sk);
							return true;
						case TARGET_AREA:
						case TARGET_BEHIND_AREA:
						case TARGET_FRONT_AREA:
						{
							if (GeoData.getInstance().canSeeTarget(activeChar, attackTarget) && !attackTarget.isDead() && dist2 <= srange)
							{
								clientStopMoving(null);
								activeChar.doCast(sk);
								return true;
							}
						}
					}
				}
				else if (skTarget == SkillTargetType.TARGET_ONE)
				{
					final L2Character target = effectTargetReconsider(sk, false);
					if (target != null)
					{
						clientStopMoving(null);
						activeChar.doCast(sk);
						return true;
					}
				}
				break;
			}
			case SLEEP:
			{
				if (skTarget == SkillTargetType.TARGET_ONE)
				{
					if (!attackTarget.isDead() && dist2 <= srange)
					{
						if (dist2 > range || attackTarget.isMoving())
						{
							if (attackTarget.getFirstEffect(sk) == null)
							{
								clientStopMoving(null);
								//_actor.setTarget(attackTarget);
								activeChar.doCast(sk);
								return true;
							}
						}
					}
					
					final L2Character target = effectTargetReconsider(sk, false);
					if (target != null)
					{
						clientStopMoving(null);
						activeChar.doCast(sk);
						return true;
					}
				}
				else if (canAOE(sk))
				{
					switch (skTarget)
					{
						case TARGET_AURA:
						case TARGET_BEHIND_AURA:
						case TARGET_FRONT_AURA:
							clientStopMoving(null);
							activeChar.doCast(sk);
							return true;
						case TARGET_AREA:
						case TARGET_BEHIND_AREA:
						case TARGET_FRONT_AREA:
						{
							if (GeoData.getInstance().canSeeTarget(activeChar, attackTarget) && !attackTarget.isDead() && dist2 <= srange)
							{
								clientStopMoving(null);
								activeChar.doCast(sk);
								return true;
							}
						}
					}
				}
				break;
			}
			case ROOT:
			case SHACKLE:
			case STUN:
			case PARALYZE:
			{
				if (GeoData.getInstance().canSeeTarget(activeChar, attackTarget) && !canAOE(sk) && dist2 <= srange)
				{
					if (attackTarget.getFirstEffect(sk) == null)
					{
						clientStopMoving(null);
						activeChar.doCast(sk);
						return true;
					}
				}
				else if (canAOE(sk))
				{
					switch (skTarget)
					{
						case TARGET_AURA:
						case TARGET_BEHIND_AURA:
						case TARGET_FRONT_AURA:
							clientStopMoving(null);
							activeChar.doCast(sk);
							return true;
						case TARGET_AREA:
						case TARGET_BEHIND_AREA:
						case TARGET_FRONT_AREA:
						{
							if (GeoData.getInstance().canSeeTarget(activeChar, attackTarget) && !attackTarget.isDead() && dist2 <= srange)
							{
								clientStopMoving(null);
								activeChar.doCast(sk);
								return true;
							}
						}
					}
				}
				else if (skTarget == SkillTargetType.TARGET_ONE)
				{
					final L2Character target = effectTargetReconsider(sk, false);
					if (target != null)
					{
						clientStopMoving(null);
						activeChar.doCast(sk);
						return true;
					}
				}
				break;
			}
			case MUTE:
			case FEAR:
			{
				if (GeoData.getInstance().canSeeTarget(activeChar, attackTarget) && !canAOE(sk) && dist2 <= srange)
				{
					if (attackTarget.getFirstEffect(sk) == null)
					{
						clientStopMoving(null);
						activeChar.doCast(sk);
						return true;
					}
				}
				else if (canAOE(sk))
				{
					switch (skTarget)
					{
						case TARGET_AURA:
						case TARGET_BEHIND_AURA:
						case TARGET_FRONT_AURA:
							clientStopMoving(null);
							activeChar.doCast(sk);
							return true;
						case TARGET_AREA:
						case TARGET_BEHIND_AREA:
						case TARGET_FRONT_AREA:
						{
							if (GeoData.getInstance().canSeeTarget(activeChar, attackTarget) && !attackTarget.isDead() && dist2 <= srange)
							{
								clientStopMoving(null);
								activeChar.doCast(sk);
								return true;
							}
						}
					}
				}
				else if (skTarget == SkillTargetType.TARGET_ONE)
				{
					final L2Character target = effectTargetReconsider(sk, false);
					if (target != null)
					{
						clientStopMoving(null);
						activeChar.doCast(sk);
						return true;
					}
				}
				break;
			}
			case CANCEL:
			case NEGATE:
			{
				// decrease cancel probability
				if (Rnd.get(50) != 0)
					return true;
					
				if (skTarget == SkillTargetType.TARGET_ONE)
				{
					if (attackTarget.getFirstEffect(L2EffectType.BUFF) != null && GeoData.getInstance().canSeeTarget(activeChar, attackTarget) && !attackTarget.isDead() && dist2 <= srange)
					{
						clientStopMoving(null);
						activeChar.doCast(sk);
						return true;
					}
					
					final L2Character target = effectTargetReconsider(sk, false);
					if (target != null)
					{
						clientStopMoving(null);
						final L2Object targets = attackTarget;
						activeChar.setTarget(target);
						activeChar.doCast(sk);
						activeChar.setTarget(targets);
						return true;
					}
				}
				else if (canAOE(sk))
				{
					switch (skTarget)
					{
						case TARGET_AURA:
						case TARGET_BEHIND_AURA:
						case TARGET_FRONT_AURA:
							clientStopMoving(null);
							activeChar.doCast(sk);
							return true;
						case TARGET_AREA:
						case TARGET_BEHIND_AREA:
						case TARGET_FRONT_AREA:
						{
							if (GeoData.getInstance().canSeeTarget(activeChar, attackTarget) && !attackTarget.isDead() && dist2 <= srange)
							{
								clientStopMoving(null);
								activeChar.doCast(sk);
								return true;
							}
						}
					}
				}
				break;
			}
			case PDAM:
			case MDAM:
			case BLOW:
			case DRAIN:
			case CHARGEDAM:
			case FATAL:	
			case DEATHLINK:
			case CPDAM:
			case MANADAM:
			case CPDAMPERCENT:	
			{
				if (!canAura(sk))
				{
					if (GeoData.getInstance().canSeeTarget(activeChar, attackTarget) && !attackTarget.isDead() && dist2 <= srange)
					{
						clientStopMoving(null);
						activeChar.doCast(sk);
						return true;
					}
					else
					{
						final L2Character target = skillTargetReconsider(sk);
						if (target != null)
						{
							clientStopMoving(null);
							final L2Object targets = attackTarget;
							activeChar.setTarget(target);
							activeChar.doCast(sk);
							activeChar.setTarget(targets);
							return true;
						}
					}
				}
				else
				{
					clientStopMoving(null);
					activeChar.doCast(sk);
					return true;
				}
				break;
			}
			default:
			{
				if (!canAura(sk))
				{					
					if (GeoData.getInstance().canSeeTarget(activeChar, attackTarget) && !attackTarget.isDead() && dist2 <= srange)
					{
						clientStopMoving(null);
						activeChar.doCast(sk);
						return true;
					}
					else
					{
						final L2Character target = skillTargetReconsider(sk);
						if (target != null)
						{
							clientStopMoving(null);
							final L2Object targets = attackTarget;
							activeChar.setTarget(target);
							activeChar.doCast(sk);
							activeChar.setTarget(targets);
							return true;
						}
					}
				}
				else
				{
					clientStopMoving(null);
					activeChar.doCast(sk);
					return true;
				}
			}
			break;
		}
		
		return false;
	}
	
	/**
	 * This AI task will start when ACTOR cannot move and attack range larger than distance
	 */
	private void movementDisable()
	{		
		L2Character attackTarget = getAttackTarget();
		if (attackTarget == null)
			return;
		
		final L2Attackable npc = getActiveChar();
		double dist = 0;
		double dist2 = 0;
		int range = 0;
		try
		{
			if (npc.getTarget() == null)
				npc.setTarget(attackTarget);
			
			dist = Math.sqrt(npc.getPlanDistanceSq(attackTarget.getX(), attackTarget.getY()));
			dist2 = dist - npc.getTemplate().collisionRadius;
			range = npc.getPhysicalAttackRange() + npc.getTemplate().collisionRadius + attackTarget.getTemplate().collisionRadius;
			if (attackTarget.isMoving())
			{
				dist = dist - 30;
				if (npc.isMoving())
					dist = dist - 50;
			}
			
			// Check if activeChar has any skill
			if (_skillRender.hasSkill())
			{
				// Try to stop the target or disable the target as priority
				int random = Rnd.get(100);
				if (_skillRender.hasImmobiliseSkill() && !attackTarget.isImmobilized() && random < 2)
				{
					for (L2Skill sk : _skillRender._immobiliseskills)
					{
						if (sk.getMpConsume() >= npc.getCurrentMp() 
								|| npc.isSkillDisabled(sk) 
								|| (sk.getCastRange() + npc.getTemplate().collisionRadius + attackTarget.getTemplate().collisionRadius <= dist2 && !canAura(sk)) 
								|| (sk.isMagic() && npc.isMuted()) 
								|| (!sk.isMagic() && npc.isPhysicalMuted()))
							continue;

						if (!GeoData.getInstance().canSeeTarget(npc, attackTarget))
							continue;
						
						if (attackTarget.getFirstEffect(sk) == null)
						{
							clientStopMoving(null);
							npc.doCast(sk);
							return;
						}
					}
				}
				
				// -------------------------------------------------------------
				// Same as Above, but with Mute/FEAR etc....
				if (_skillRender.hasCOTSkill() && random < 5)
				{
					for (L2Skill sk : _skillRender._cotskills)
					{
						if (sk.getMpConsume() >= npc.getCurrentMp() 
								|| npc.isSkillDisabled(sk) 
								|| (sk.getCastRange() + npc.getTemplate().collisionRadius + attackTarget.getTemplate().collisionRadius <= dist2 && !canAura(sk)) 
								|| (sk.isMagic() && npc.isMuted()) 
								|| (!sk.isMagic() && npc.isPhysicalMuted()))
							continue;

						if (!GeoData.getInstance().canSeeTarget(npc, attackTarget))
							continue;
						
						if (attackTarget.getFirstEffect(sk) == null)
						{
							clientStopMoving(null);
							npc.doCast(sk);
							return;
						}
					}
				}
				
				//-------------------------------------------------------------
				if (_skillRender.hasDebuffSkill() && random < 8)
				{
					for (L2Skill sk : _skillRender._debuffskills)
					{
						if (sk.getMpConsume() >= npc.getCurrentMp() || npc.isSkillDisabled(sk) 
								|| (sk.getCastRange() + npc.getTemplate().collisionRadius + attackTarget.getTemplate().collisionRadius <= dist2 && !canAura(sk)) 
								|| (sk.isMagic() && npc.isMuted()) || (!sk.isMagic() && npc.isPhysicalMuted()))
							continue;

						if (!GeoData.getInstance().canSeeTarget(npc, attackTarget))
							continue;
						
						if (attackTarget.getFirstEffect(sk) == null)
						{
							clientStopMoving(null);
							npc.doCast(sk);
							return;
						}
					}
				}
				
				// -------------------------------------------------------------
				// Some side effect skill like CANCEL or NEGATE
				if (_skillRender.hasNegativeSkill() && random < 9)
				{
					for (L2Skill sk : _skillRender._negativeskills)
					{
						if (sk.getMpConsume() >= npc.getCurrentMp() || npc.isSkillDisabled(sk) 
								|| (sk.getCastRange() + npc.getTemplate().collisionRadius + attackTarget.getTemplate().collisionRadius <= dist2 && !canAura(sk)) 
								|| (sk.isMagic() && npc.isMuted()) || (!sk.isMagic() && npc.isPhysicalMuted()))
							continue;

						if (!GeoData.getInstance().canSeeTarget(npc, attackTarget))
							continue;
						
						if (attackTarget.getFirstEffect(L2EffectType.BUFF) != null)
						{
							clientStopMoving(null);
							npc.doCast(sk);
							return;
						}
					}
				}
				
				// -------------------------------------------------------------
				// Start ATK SKILL when nothing can be done
				if (_skillRender.hasAtkSkill() && (npc.isMovementDisabled()
						|| npc.getAiType() == AIType.MAGE || npc.getAiType() == AIType.HEALER))
				{
					for (L2Skill sk : _skillRender._atkskills)
					{
						if (sk.getMpConsume() >= npc.getCurrentMp() || npc.isSkillDisabled(sk) 
								|| (sk.getCastRange() + npc.getTemplate().collisionRadius + attackTarget.getTemplate().collisionRadius <= dist2 && !canAura(sk)) 
								|| (sk.isMagic() && npc.isMuted()) || (!sk.isMagic() && npc.isPhysicalMuted()))
							continue;

						if (!GeoData.getInstance().canSeeTarget(npc, attackTarget))
							continue;
						
						clientStopMoving(null);
						npc.doCast(sk);
						return;
					}
				}
				
				// -------------------------------------------------------------
				// if there is no ATK skill to use, then try Universal skill
				/*
				if(_skillRender.hasUniversalSkill())
				{
					for(L2Skill sk:_skillRender._universalskills)
					{
						if(sk.getMpConsume()>=_actor.getCurrentMp()
								|| _actor.isSkillDisabled(sk.getId())
								||(sk.getCastRange()+ _actor.getTemplate().collisionRadius + getAttackTarget().getTemplate().collisionRadius <= dist2 && !canAura(sk))
								||(sk.isMagic()&&_actor.isMuted())
								||(!sk.isMagic()&&_actor.isPhysicalMuted()))
						{
							continue;
						}
						if(!GeoData.getInstance().canSeeTarget(_actor,getAttackTarget()))
							continue;
						clientStopMoving(null);
						L2Object target = getAttackTarget();
						//_actor.setTarget(_actor);
						_actor.doCast(sk);
						//_actor.setTarget(target);
						return;
					}
				}
				*/
			}
			
			if (npc.isMovementDisabled())
			{
				targetReconsider();
				return;
			}
			
			if (dist > range || !GeoData.getInstance().canSeeTarget(npc, attackTarget))
			{
				if (attackTarget.isMoving())
					range -= 100;
				if (range < 5)
					range = 5;
				moveToPawn(attackTarget, range);
				return;
			}
			
			melee(npc.getPrimaryAttack());
		}
		catch (NullPointerException e)
		{
			setIntention(AI_INTENTION_ACTIVE);
			_log.log(Level.WARNING, this + " - failed executing movementDisable(): " + e.getMessage(), e);
			return;
		}
	}
	
	private L2Character effectTargetReconsider(L2Skill sk, boolean positive)
	{
		if (sk == null)
			return null;
		
		L2Attackable actor = getActiveChar();
		if (sk.getSkillType() != L2SkillType.NEGATE || sk.getSkillType() != L2SkillType.CANCEL)
		{
			if (!positive)
			{
				double dist = 0;
				double dist2 = 0;
				int range = 0;
				
				for (L2Character obj : actor.getAttackByList())
				{
					if (obj == null 
							|| obj == getAttackTarget()
							|| obj.isDead() 
							|| !GeoData.getInstance().canSeeTarget(actor, obj))
						continue;
					
					try
					{
						actor.setTarget(getAttackTarget());
						dist = Math.sqrt(actor.getPlanDistanceSq(obj.getX(), obj.getY()));
						dist2 = dist - actor.getTemplate().collisionRadius;
						range = sk.getCastRange() + actor.getTemplate().collisionRadius + obj.getTemplate().collisionRadius;
						if (obj.isMoving())
							dist2 = dist2 - 70;
					}
					catch (NullPointerException e)
					{
						continue;
					}
					
					if (dist2 <= range)
					{
						if (getAttackTarget().getFirstEffect(sk) == null)
							return obj;
					}
				}
				
				// ----------------------------------------------------------------------
				// If there is nearby Target with aggro, start going on random target that is attackable
				final Collection<L2Character> knowChars = actor.getKnownList().getKnownCharactersInRadius(range);
				for (L2Character obj : knowChars)
				{
					if (obj.isDead() || !GeoData.getInstance().canSeeTarget(actor, obj))
						continue;
					
					try
					{
						actor.setTarget(getAttackTarget());
						dist = Math.sqrt(actor.getPlanDistanceSq(obj.getX(), obj.getY()));
						dist2 = dist;
						range = sk.getCastRange() + actor.getTemplate().collisionRadius + obj.getTemplate().collisionRadius;
						if (obj.isMoving())
							dist2 = dist2 - 70;
					}
					catch (NullPointerException e)
					{
						continue;
					}
					
					if (obj instanceof L2Attackable)
					{
						if (actor.getEnemyClan() != null && actor.getEnemyClan().equals(((L2Attackable) obj).getClan()))
						{
							if (dist2 <= range)
							{
								if (getAttackTarget().getFirstEffect(sk) == null)
									return obj;
							}
						}
					}
					else if (obj instanceof L2Playable)
					{
						if (dist2 <= range)
						{
							if (getAttackTarget().getFirstEffect(sk) == null)
								return obj;
						}
					}
				}
			}
			else if (positive)
			{
				double dist = 0;
				double dist2 = 0;
				int range = 0;
				Collection<L2Character> knowChars = actor.getKnownList().getKnownCharactersInRadius(range);
				for (L2Character obj : knowChars)
				{
					if (!(obj instanceof L2Attackable) || obj.isDead() || !GeoData.getInstance().canSeeTarget(actor, obj))
						continue;
					
					final L2Attackable targets = ((L2Attackable) obj);
					if (actor.getFactionId() != null && !actor.getFactionId().equals(targets.getFactionId()))
						continue;
					
					try
					{
						actor.setTarget(getAttackTarget());
						dist = Math.sqrt(actor.getPlanDistanceSq(obj.getX(), obj.getY()));
						dist2 = dist - actor.getTemplate().collisionRadius;
						range = sk.getCastRange() + actor.getTemplate().collisionRadius + obj.getTemplate().collisionRadius;
						if (obj.isMoving())
							dist2 = dist2 - 70;
					}
					catch (NullPointerException e)
					{
						continue;
					}
					
					if (dist2 <= range)
					{
						if (obj.getFirstEffect(sk) == null)
							return obj;
					}
				}
			}
			return null;
		}
		else
		{
			double dist = 0;
			double dist2 = 0;
			int range = sk.getCastRange() + actor.getTemplate().collisionRadius + getAttackTarget().getTemplate().collisionRadius;
			Collection<L2Character> knowChars = actor.getKnownList().getKnownCharactersInRadius(range);
			for (L2Character obj : knowChars)
			{
				if (obj == null || obj.isDead() || !GeoData.getInstance().canSeeTarget(actor, obj))
					continue;
				
				try
				{
					actor.setTarget(getAttackTarget());
					dist = Math.sqrt(actor.getPlanDistanceSq(obj.getX(), obj.getY()));
					dist2 = dist - actor.getTemplate().collisionRadius;
					range = sk.getCastRange() + actor.getTemplate().collisionRadius + obj.getTemplate().collisionRadius;
					if (obj.isMoving())
						dist2 = dist2 - 70;
				}
				catch (NullPointerException e)
				{
					continue;
				}
				
				if (obj instanceof L2Attackable)
				{
					if (actor.getEnemyClan() != null && actor.getEnemyClan().equals(((L2Attackable) obj).getClan()))
					{
						if (dist2 <= range)
						{
							if (getAttackTarget().getFirstEffect(L2EffectType.BUFF) != null)
								return obj;
						}
					}
				}				
				else if (obj instanceof L2Playable)
				{					
					if (dist2 <= range)
					{
						if (getAttackTarget().getFirstEffect(L2EffectType.BUFF) != null)
							return obj;
					}
				}
			}
			return null;
		}
	}
	
	private L2Character skillTargetReconsider(L2Skill sk)
	{
		double dist = 0;
		double dist2 = 0;
		int range = 0;
		L2Attackable actor = getActiveChar();
		if (actor.getHateList() != null)
		{
			for (L2Character obj : actor.getHateList())
			{
				if (obj == null || !GeoData.getInstance().canSeeTarget(actor, obj) || obj.isDead())
					continue;
				
				try
				{
					actor.setTarget(getAttackTarget());
					dist = Math.sqrt(actor.getPlanDistanceSq(obj.getX(), obj.getY()));
					dist2 = dist - actor.getTemplate().collisionRadius;
					range = sk.getCastRange() + actor.getTemplate().collisionRadius + getAttackTarget().getTemplate().collisionRadius;
				}
				catch (NullPointerException e)
				{
					continue;
				}
				
				if (dist2 <= range)
					return obj;
			}
		}
		
		if (!(actor instanceof L2GuardInstance))
		{
			final Collection<L2Object> objs = actor.getKnownList().getKnownObjects().values();
			for (L2Object target : objs)
			{
				try
				{
					actor.setTarget(getAttackTarget());
					dist = Math.sqrt(actor.getPlanDistanceSq(target.getX(), target.getY()));
					dist2 = dist;
					range = sk.getCastRange() + actor.getTemplate().collisionRadius + getAttackTarget().getTemplate().collisionRadius;
				}
				catch (NullPointerException e)
				{
					continue;
				}
				
				if (!(target instanceof L2Character))
					continue;
				
				L2Character obj = (L2Character) target;
				if (!GeoData.getInstance().canSeeTarget(actor, obj) || dist2 > range)
					continue;
				
				if (obj instanceof L2Playable)
					return obj;

				if (obj instanceof L2Attackable)
				{
					if (actor.getEnemyClan() != null && actor.getEnemyClan().equals(((L2Attackable) obj).getClan()))
						return obj;

					if (actor.getIsChaos() != 0)
					{
						if (((L2Attackable) obj).getFactionId() != null && ((L2Attackable) obj).getFactionId().equals(actor.getFactionId()))
							continue;
						else
							return obj;
					}
				}
				
			}
		}
		return null;
	}
	
	private void targetReconsider()
	{
		double dist = 0;
		double dist2 = 0;
		int range = 0;
		L2Attackable actor = getActiveChar();
		L2Character MostHate = actor.getMostHated();
		if (actor.getHateList() != null)
		{
			for (L2Character obj : actor.getHateList())
			{
				if (obj == null 						 
						|| obj.isDead() 
						|| obj != MostHate 
						|| obj == actor
						|| !GeoData.getInstance().canSeeTarget(actor, obj))
					continue;
				
				try
				{
					dist = Math.sqrt(actor.getPlanDistanceSq(obj.getX(), obj.getY()));
					dist2 = dist - actor.getTemplate().collisionRadius;
					range = actor.getPhysicalAttackRange() + actor.getTemplate().collisionRadius + obj.getTemplate().collisionRadius;
					if (obj.isMoving())
						dist2 = dist2 - 70;
				}
				catch (NullPointerException e)
				{
					continue;
				}
				
				if (dist2 <= range)
				{
					if (MostHate != null)
						actor.addDamageHate(obj, 0, actor.getHating(MostHate));
					else
						actor.addDamageHate(obj, 0, 2000);
					actor.setTarget(obj);
					setAttackTarget(obj);
					return;
				}
			}
		}
		
		if (!(actor instanceof L2GuardInstance))
		{
			final Collection<L2Object> objs = actor.getKnownList().getKnownObjects().values();
			for (L2Object target : objs)
			{
				if (!(target instanceof L2Character))
					continue;
				
				L2Character obj = (L2Character) target;
				
				if (obj.isDead() 
						|| obj != MostHate 
						|| obj == actor 
						|| obj == getAttackTarget()
						|| !GeoData.getInstance().canSeeTarget(actor, obj) )
					continue;
				
				if (obj instanceof L2PcInstance)
				{
					if (MostHate != null)
						actor.addDamageHate(obj, 0, actor.getHating(MostHate));
					else
						actor.addDamageHate(obj, 0, 2000);
					actor.setTarget(obj);
					setAttackTarget(obj);
					
				}
				else if (obj instanceof L2Attackable)
				{
					if (actor.getEnemyClan() != null && actor.getEnemyClan().equals(((L2Attackable) obj).getClan()))
					{
						actor.addDamageHate(obj, 0, actor.getHating(MostHate));
						actor.setTarget(obj);
					}
					
					if (actor.getIsChaos() != 0)
					{
						if (((L2Attackable) obj).getFactionId() != null && ((L2Attackable) obj).getFactionId().equals(actor.getFactionId()))
							continue;
						else
						{
							if (MostHate != null)
								actor.addDamageHate(obj, 0, actor.getHating(MostHate));
							else
								actor.addDamageHate(obj, 0, 2000);
							actor.setTarget(obj);
							setAttackTarget(obj);
						}
					}
				}
				else if (obj instanceof L2Summon)
				{
					if (MostHate != null)
						actor.addDamageHate(obj, 0, actor.getHating(MostHate));
					else
						actor.addDamageHate(obj, 0, 2000);
					actor.setTarget(obj);
					setAttackTarget(obj);
				}
			}
		}
	}
	
	@SuppressWarnings("null")
	private void aggroReconsider()
	{		
		L2Attackable actor = (L2Attackable) _actor;
		L2Character MostHate = ((L2Attackable) _actor).getMostHated();
		
		if (actor.getHateList() != null)
		{			
			final int rand = Rnd.get(actor.getHateList().size());
			int count = 0;
			for (L2Character obj : actor.getHateList())
			{
				if (count < rand)
				{
					count++;
					continue;
				}
				
				if (obj == null 
						|| obj.isDead() 
						|| obj == getAttackTarget() 
						|| obj == actor 
						|| !GeoData.getInstance().canSeeTarget(actor, obj))
					continue;
				
				try
				{
					actor.setTarget(getAttackTarget());
				}
				catch (NullPointerException e)
				{
					continue;
				}
				
				if (MostHate != null)
					actor.addDamageHate(obj, 0, actor.getHating(MostHate));
				else
					actor.addDamageHate(obj, 0, 2000);
				actor.setTarget(obj);
				setAttackTarget(obj);
				return;				
			}
		}
		
		if (!(actor instanceof L2GuardInstance))
		{
			Collection<L2Object> objs = actor.getKnownList().getKnownObjects().values();
			for (L2Object target : objs)
			{
				if (!(target instanceof L2Character))
					continue;
				
				L2Character obj = (L2Character) target;
				
				if (obj.isDead() 
						|| obj != MostHate 
						|| obj == actor 
						|| !GeoData.getInstance().canSeeTarget(actor, obj))
					continue;
				
				if (obj instanceof L2PcInstance)
				{
					if (MostHate != null || !MostHate.isDead())
						actor.addDamageHate(obj, 0, actor.getHating(MostHate));
					else
						actor.addDamageHate(obj, 0, 2000);
					actor.setTarget(obj);
					setAttackTarget(obj);					
				}
				else if (obj instanceof L2Attackable)
				{
					if (actor.getEnemyClan() != null && (actor.getEnemyClan().equals(((L2Attackable) obj).getClan())))
					{
						if (MostHate != null)
							actor.addDamageHate(obj, 0, actor.getHating(MostHate));
						else
							actor.addDamageHate(obj, 0, 2000);
						actor.setTarget(obj);
					}
					
					if (actor.getIsChaos() != 0)
					{
						if (((L2Attackable) obj).getFactionId() != null && ((L2Attackable) obj).getFactionId().equals(actor.getFactionId()))
							continue;
						else
						{
							if (MostHate != null)
								actor.addDamageHate(obj, 0, actor.getHating(MostHate));
							else
								actor.addDamageHate(obj, 0, 2000);
							actor.setTarget(obj);
							setAttackTarget(obj);
						}
					}
				}
				else if (obj instanceof L2Summon)
				{
					if (MostHate != null)
						actor.addDamageHate(obj, 0, actor.getHating(MostHate));
					else
						actor.addDamageHate(obj, 0, 2000);
					actor.setTarget(obj);
					setAttackTarget(obj);
				}
			}
		}
	}
	
	private void lSkillRender()
	{
		if (_skillRender._Lrangeskills == null)
			_skillRender._Lrangeskills = getActiveChar().getLrangeSkill();
	}
	
	private void sSkillRender()
	{
		if (_skillRender._Srangeskills == null)
			_skillRender._Srangeskills = getActiveChar().getSrangeSkill();
	}
	
	/**
	 * Manage AI thinking actions of a L2Attackable.<BR><BR>
	 */
	@Override
	protected void onEvtThink()
	{
		// Check if the actor can't use skills and if a thinking action isn't already in progress
		if (_thinking || getActiveChar().isAllSkillsDisabled())
			return;
		
		// Start thinking action
		_thinking = true;
		
		try
		{
			// Manage AI thinks of a L2Attackable
			switch (getIntention())
			{
				case AI_INTENTION_ACTIVE:
					thinkActive();
					break;
				case AI_INTENTION_ATTACK:
					thinkAttack();
					break;
				case AI_INTENTION_CAST:
					thinkCast();
					break;
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, this + " -  onEvtThink() failed: " + e.getMessage(), e);
		}
		finally
		{
			// Stop thinking action
			_thinking = false;
		}
	}
	
	/**
	 * Launch actions corresponding to the Event Attacked.<BR><BR>
	 *
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Init the attack : Calculate the attack timeout, Set the _globalAggro to 0, Add the attacker to the actor _aggroList</li>
	 * <li>Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance</li>
	 * <li>Set the Intention to AI_INTENTION_ATTACK</li><BR><BR>
	 *
	 * @param attacker The L2Character that attacks the actor
	 *
	 */
	@Override
	protected void onEvtAttacked(L2Character attacker)
	{
		final L2Attackable me = getActiveChar();
		
		// Calculate the attack timeout
		_attackTimeout = MAX_ATTACK_TIMEOUT + GameTimeController.getGameTicks();
		
		// Set the _globalAggro to 0 to permit attack even just after spawn
		if (_globalAggro < 0)
			_globalAggro = 0;
		
		// Add the attacker to the _aggroList of the actor
		me.addDamageHate(attacker, 0, 1);
		
		// Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
		if (!me.isRunning())
			me.setRunning();
		
		// Set the Intention to AI_INTENTION_ATTACK
		if (getIntention() != AI_INTENTION_ATTACK)
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		else if (me.getMostHated() != getAttackTarget())
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		
		if (me instanceof L2MonsterInstance)
		{
			L2MonsterInstance master = (L2MonsterInstance) me;
			
			if (master.hasMinions())
				master.getMinionList().onAssist(me, attacker);

			master = master.getLeader();					
			if (master != null && master.hasMinions())
				master.getMinionList().onAssist(me, attacker);
		}
		
		super.onEvtAttacked(attacker);
	}
	
	/**
	 * Launch actions corresponding to the Event Aggression.<BR><BR>
	 *
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Add the target to the actor _aggroList or update hate if already present </li>
	 * <li>Set the actor Intention to AI_INTENTION_ATTACK (if actor is L2GuardInstance check if it isn't too far from its home location)</li><BR><BR>
	 *
	 * @param attacker The L2Character that attacks
	 * @param aggro The value of hate to add to the actor against the target
	 *
	 */
	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{
		final L2Attackable me = getActiveChar();
		
		if (target != null)
		{
			// Add the target to the actor _aggroList or update hate if already present
			me.addDamageHate(target, 0, aggro);
			
			// Set the actor AI Intention to AI_INTENTION_ATTACK
			if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
			{
				// Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
				if (!me.isRunning())
					me.setRunning();
				
				setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
			}
			
			if (me instanceof L2MonsterInstance)
			{
				L2MonsterInstance master = (L2MonsterInstance) me;
				
				if (master.hasMinions())
					master.getMinionList().onAssist(me, target);

				master = master.getLeader();					
				if (master != null && master.hasMinions())
					master.getMinionList().onAssist(me, target);
			}
		}
	}
	
	@Override
	protected void onIntentionActive()
	{
		// Cancel attack timeout
		_attackTimeout = Integer.MAX_VALUE;
		super.onIntentionActive();
	}
	
	public void setGlobalAggro(int value)
	{
		_globalAggro = value;
	}
	
	/**
	 * @param _timePass The _timePass to set.
	 */
	public void setTimepass(int TP)
	{
		_timePass = TP;
	}
	
	/**
	 * @return Returns the _timePass.
	 */
	public int getTimepass()
	{
		return _timePass;
	}
	
	public L2Attackable getActiveChar()
	{
		return (L2Attackable)_actor;
	}
	
	/**
	 * Synerge - Check if can cast a skill
	 */
	private boolean cantCastSkill(L2Character actor, L2Skill sk)
	{
		if (sk.getMpConsume() >= actor.getCurrentMp())
			return true;

		if (actor.isSkillDisabled(sk))
			return true;

		if (sk.isMagic())
		{
			if (actor.isMuted())
				return true;
		}
		else if (actor.isPhysicalMuted())
			return true;

		return false;
	}
}
