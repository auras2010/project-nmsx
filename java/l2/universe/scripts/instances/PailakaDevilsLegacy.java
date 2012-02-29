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

import java.util.Collections;
import java.util.List;

import javolution.util.FastList;
import l2.universe.ExternalConfig;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.instancemanager.InstanceManager;
import l2.universe.gameserver.instancemanager.InstanceManager.InstanceWorld;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.Location;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.L2Summon;
import l2.universe.gameserver.model.actor.instance.L2MonsterInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.actor.instance.L2PetInstance;
import l2.universe.gameserver.model.entity.Instance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.State;
import l2.universe.gameserver.model.zone.L2ZoneType;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.MagicSkillUse;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.util.Rnd;

/**
 * 
 * @author Synerge
 */
public class PailakaDevilsLegacy extends Quest
{
	private static final String qn = "129_PailakaDevilsLegacy";

	private static final int MIN_LEVEL = 61;
	private static final int MAX_LEVEL = 67;
	private static final int MAX_SUMMON_LEVEL = 70;
	private static final int EXIT_TIME = 5;
	private static final int INSTANCE_ID = 44;
	private static final int[] TELEPORT = { 76438, -219035, -3752 };
	//private static final int ZONE = 20109;

	private static final int SURVIVOR = 32498;
	private static final int SUPORTER = 32501; 
	private static final int DWARF = 32508;
	private static final int DWARF2 = 32511;
	private static final int[] NPCS = { SURVIVOR, SUPORTER, DWARF, DWARF2 };

	private static final int POWER_KEG          = 18622;
	private static final int BEGRUDGED_ARCHER   = 18623;
	private static final int DEADMAN_S_GRUDGE_1 = 18624;
	private static final int DEADMAN_S_GRUDGE_2 = 18625; 
	private static final int DEADMAN_S_GRUDGE_3 = 18626;
	private static final int DEADMAN_S_GRUDGE_4 = 18627;
	private static final int ATAN               = 18628; 
	private static final int KAMS               = 18629;
	private static final int HIKORO             = 18630; 
	private static final int ALKASO             = 18631; 
	private static final int GERBERA            = 18632;
	private static final int LEMATAN            = 18633;
	private static final int LEMATAN_S_FOLLOWER = 18634;
	private static final int TREASURE_BOX       = 32495; 
	
	private static final int[] MONSTERS =
	{ 
		BEGRUDGED_ARCHER, DEADMAN_S_GRUDGE_1, DEADMAN_S_GRUDGE_2, DEADMAN_S_GRUDGE_3, 
		DEADMAN_S_GRUDGE_4,	ATAN, KAMS, HIKORO, ALKASO, GERBERA, LEMATAN 
	};

	private static final int ANCIENT_LEGACY_SWORD           = 13042;
	private static final int ENHANCED_ANCIENT_LEGACY_SWORD  = 13043; 
	private static final int COMPLETE_ANCIENT_LEGACY_SWORD  = 13044;
	private static final int PAILAKA_INSTANT_SHIELD         = 13032;
	private static final int QUICK_HEALING_POTION           = 13033; 
	private static final int PAILAKA_WEAPON_UPGRADE_STAGE_1 = 13046;
	private static final int PAILAKA_WEAPON_UPGRADE_STAGE_2 = 13047;
	private static final int PAILAKA_ANTIDOTE               = 13048; 
	private static final int DIVINE_SOUL                    = 13049; 
	private static final int LONG_RANGEDEFENSE_POTION       = 13059; 
	private static final int PSOE                           = 736; 
	private static final int PAILAKA_ALL_PURPOSE_KEY        = 13150;
	private static final int PAILAKA_BRACELET               = 13295;
	
	private static boolean _isTeleportScheduled = false;
	private static boolean _isOnShip = false;
	private static L2Npc _lematanNpc = null;
	private List<L2Npc> _followerslist;
	
	private static L2Skill ENERGY_SKILL = SkillTable.getInstance().getInfo(5712, 1);
	
	private static final int[] ITEMS = 
	{ 
		ANCIENT_LEGACY_SWORD, ENHANCED_ANCIENT_LEGACY_SWORD, COMPLETE_ANCIENT_LEGACY_SWORD, 
		PAILAKA_INSTANT_SHIELD, QUICK_HEALING_POTION, PAILAKA_WEAPON_UPGRADE_STAGE_1, PAILAKA_ALL_PURPOSE_KEY,
		PAILAKA_WEAPON_UPGRADE_STAGE_2, PAILAKA_ANTIDOTE, DIVINE_SOUL, LONG_RANGEDEFENSE_POTION		 
	};
	
	private static final int[][] FOLLOWERS_SPAWNS = 
	{
		{ 85067, -208943, -3336, 20106, 60 },
		{ 84904, -208944, -3336, 10904, 60 },
		{ 85062, -208538, -3336, 44884, 60 },
		{ 84897, -208542, -3336, 52973, 60 },
		{ 84808, -208633, -3339, 65039, 60 },
		{ 84808, -208856, -3339,     0, 60 },
		{ 85144, -208855, -3341, 33380, 60 },
		{ 85139, -208630, -3339, 31777, 60 }		
	};

	private static final FastList<TreasureDrop> DROPLIST = new FastList<TreasureDrop>();
	static
	{
		DROPLIST.add(new TreasureDrop(PAILAKA_INSTANT_SHIELD, 80));
		DROPLIST.add(new TreasureDrop(QUICK_HEALING_POTION, 60));
		DROPLIST.add(new TreasureDrop(PAILAKA_ANTIDOTE, 40));
		DROPLIST.add(new TreasureDrop(DIVINE_SOUL, 50));
		DROPLIST.add(new TreasureDrop(LONG_RANGEDEFENSE_POTION, 20));
		DROPLIST.add(new TreasureDrop(PAILAKA_ALL_PURPOSE_KEY, 10));
	};

	private static final int[][] HP_HERBS_DROPLIST = 
	{
		// itemId, count, chance
		{ 8602, 1, 10 }, { 8601, 1, 40 }, { 8600, 1, 70 }
	};

	private static final int[][] MP_HERBS_DROPLIST =
	{
		// itemId, count, chance
		{ 8605, 1, 10 }, { 8604, 1, 40 }, { 8603, 1, 70 }
	};

	private static final int[] REWARDS = { PAILAKA_BRACELET, PSOE };

	private static final void dropHerb(L2Npc mob, L2PcInstance player, int[][] drop)
	{
		final int chance = Rnd.get(100);
		for (int i = 0; i < drop.length; i++)
		{
			if (chance < drop[i][2])
			{
				((L2MonsterInstance)mob).dropItem(player, drop[i][0], drop[i][1]);
				return;
			}
		}
	}

	private static final void dropItem(L2Npc mob, L2PcInstance player)
	{
		// To make random drops, we shuffle the droplist every time its used
		Collections.shuffle(DROPLIST);
		for (TreasureDrop td : DROPLIST)
		{
			if (Rnd.get(100) < td.getChance())
			{
				((L2MonsterInstance)mob).dropItem(player, td.getItemID(), Rnd.get(1,6));
				return;
			}
		}
	}

	private static final void teleportPlayer(L2PcInstance player, int[] coords, int instanceId)
	{
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		player.setInstanceId(instanceId);
		player.teleToLocation(coords[0], coords[1], coords[2], true);
	}

	private final synchronized void enterInstance(L2PcInstance player, boolean isNewQuest)
	{
		// check for existing instances for this player
		InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
		if (world != null)
		{
			if (world.templateId != INSTANCE_ID)
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ALREADY_ENTERED_ANOTHER_INSTANCE_CANT_ENTER));
				return;
			}
			
			/* Synerge - If the instances is blocked, it means that the player canceled the quest while the instance was active
			 * He now must wait until the instance finishes to enter again
			 */
			if (world.isLocked)
			{
				player.sendMessage("This instance is blocked because the quest was canceled. You must wait until its time ends");
				return;
			}
			
			Instance inst = InstanceManager.getInstance().getInstance(world.instanceId);
			if (inst != null)
			{
				// Synerge - Check max summon levels
				checkMaxSummonLevel(player);
				
				teleportPlayer(player, TELEPORT, world.instanceId);
			}
		}
		// New instance
		else
		{
			/* Synerge - Bind the instance with the quest. You cant create a new instance if you have still the quest
			 * Request canceling the quest before entering, or do it automatically, and request asking again for the quest
			 */
			if (!isNewQuest)
			{
				final QuestState st = player.getQuestState(qn);
				st.unset("cond");
				st.exitQuest(true);
				player.sendMessage("Your instance has ended so your quest has been canceled. Talk to me again");
				return;
			}
			
			final int instanceId = InstanceManager.getInstance().createDynamicInstance("PailakaDevilsLegacy.xml");

			world = new InstanceWorld();
			world.instanceId = instanceId;
			world.templateId = INSTANCE_ID;
			InstanceManager.getInstance().addWorld(world);
			
			// Synerge - Check max summon levels
			checkMaxSummonLevel(player);

			world.allowed.add(player.getObjectId());
			teleportPlayer(player, TELEPORT, instanceId);
		}
	}
	
	// Synerge - Checks if the summon or pet that the player has can be used
	private final void checkMaxSummonLevel(L2PcInstance player)
	{
		if (!ExternalConfig.LIMIT_SUMMONS_PAILAKA)
			return;
		
		final L2Summon pet = player.getPet();
		if (pet instanceof L2PetInstance)
		{
			if (pet.getLevel() > MAX_SUMMON_LEVEL)
				pet.unSummon(player);
		}
	}
	
	/*
	 * States for this Pailaka: (cond)
	 * 0 - Not started
	 * 1 - Accepted quest
	 * 2 - Entered into pailaka
	 * 3 - Got the weapon
	 * 4 - First weapon upgrade, comes from Kams
	 * 5 - Killed hikoro, neccesary but doesnt do anything
	 * 6 - Killed Alkaso, get upgrade
	 * 7 - Complete spear
	 * 8 - Kill Gerbera
	 * 9 - Kill lematan
	 * 10 - Finish
	 */
	@SuppressWarnings("null")
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (npc.getNpcId() == LEMATAN_S_FOLLOWER && event.equalsIgnoreCase("follower_cast"))
		{
			if (!npc.isCastingNow() && !npc.isDead() && !_lematanNpc.isDead())
			{
				npc.setTarget(_lematanNpc);
				npc.doCast(ENERGY_SKILL);
			}
			startQuestTimer("follower_cast", 10000 + Rnd.get(100, 1000), npc, null);
			return null;
		}
		
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return getNoQuestMsg(player);

		final int cond = st.getInt("cond");
		if (event.equalsIgnoreCase("enter"))
		{
			if (player.getLevel() < MIN_LEVEL)
				return "32498-no.htm";
			if (player.getLevel() > MAX_LEVEL)
				return "32498-no.htm";
			if (cond < 2)
				return "32498-no.htm";
			enterInstance(player, cond == 2);
			return null;
		}
		else if (event.equalsIgnoreCase("32498-02.htm"))
		{
			if (cond == 0)
			{
				st.set("cond","1");
				st.setState(State.STARTED);
				st.playSound("ItemSound.quest_accept");
			}
		}
		else if (event.equalsIgnoreCase("32498-05.htm"))
		{
			if (cond == 1)
			{
				st.set("cond","2");
				st.playSound("ItemSound.quest_accept");
			}
		}
		else if (event.equalsIgnoreCase("32501-03.htm"))
		{
			if (cond == 2)
			{
				st.set("cond","3");
				
				if (!st.hasQuestItems(ANCIENT_LEGACY_SWORD))
				{
					st.giveItems(ANCIENT_LEGACY_SWORD, 1);
					st.playSound("ItemSound.quest_itemget");
				}
				
				// Spawns Kams
				addSpawn(KAMS, 80699, -219268, -3521, 11650, false, 0, false, npc.getInstanceId());
			}
		}
		else if (event.equalsIgnoreCase("lematan_teleport"))
		{
			if (npc.getNpcId() == LEMATAN && !npc.isMovementDisabled() && !_isOnShip)
			{
				// Reduce Hate
				((L2Attackable)npc).reduceHate(player, 9999);
				((L2Attackable)npc).abortAttack();
				((L2Attackable)npc).abortCast();
				
				// Broadcast Escape
				npc.broadcastPacket(new MagicSkillUse(npc, 2100, 1, 1000, 0));
				
				// Schedule telport - when Lematan Finish casting
				startQuestTimer("lematan_finish_teleport", 1500, npc, player);
			}
			else
				_isTeleportScheduled = false;
			
			return null;
		}
		else if (event.equalsIgnoreCase("lematan_finish_teleport"))
		{
			if (npc.getNpcId() == LEMATAN && !_isOnShip)
			{
				// Teleport Lematan
				npc.teleToLocation(84973, -208721, -3340);
				
				// Set onShip
				_isOnShip = true;
				
				// Set Spawn loc to ship. If he loose aggro he should stay on board ;)
				npc.getSpawn().setLocx(84973);
				npc.getSpawn().setLocy(-208721);
				npc.getSpawn().setLocz(-3340);
				
				// He cant get out of the center zone of the boat. Their followers bind him there
				npc.mustRemainInZone(200000, true);
				
				// To be sure, reduce again
				((L2Attackable)npc).reduceHate(player, 9999);
				
				if (player != null && player.getPet() != null)
					player.getPet().abortAttack();
				
				// Spawn followers
				_followerslist = new FastList<L2Npc>();
				for (int i = 0; i < FOLLOWERS_SPAWNS.length; i++)
				{
					final int[] SPAWN = FOLLOWERS_SPAWNS[i];
					L2Npc _follower = addSpawn(LEMATAN_S_FOLLOWER, SPAWN[0], SPAWN[1], SPAWN[2], SPAWN[3], false, 0, true, npc.getInstanceId());
					if (_follower != null)
						_followerslist.add(_follower);
				}
				return null;
			}
		}

		return event;
	}

	@Override
	public final String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return getNoQuestMsg(player);

		final int cond = st.getInt("cond");
		switch (npc.getNpcId())
		{
			case SURVIVOR:
				switch (st.getState())
				{
					case State.CREATED:
						if (player.getLevel() < MIN_LEVEL)
							return "32498-no.htm";
						if (player.getLevel() > MAX_LEVEL)
							return "32498-no.htm";
						return "32498-01.htm";
					case State.STARTED:
						if (player.getLevel() < MIN_LEVEL)
							return "32498-no.htm";
						if (player.getLevel() > MAX_LEVEL)
							return "32498-no.htm";
						if (cond > 1)
							return "32498-06.htm";
					case State.COMPLETED:
						return "32498-completed.htm";
					default:
						return "32498-no.htm";
				}
			case SUPORTER:
				switch (st.getInt("cond"))
				{
					case 1:
					case 2:
						return "32501-01.htm";
					default:
						return "32501-04.htm";
				}
			case DWARF:
				if (player.getPet() != null) 
					return "32508-04.htm";
				
				if (cond == 3 && st.getQuestItemsCount(ANCIENT_LEGACY_SWORD) == 1 && st.getQuestItemsCount(PAILAKA_WEAPON_UPGRADE_STAGE_1) == 1)
				{
					st.playSound("ItemSound.quest_itemget");
					st.takeItems(ANCIENT_LEGACY_SWORD, -1);
					st.takeItems(PAILAKA_WEAPON_UPGRADE_STAGE_1, -1);
					st.giveItems(ENHANCED_ANCIENT_LEGACY_SWORD, 1);
										
					// Spawns Hikoro
					addSpawn(HIKORO, 77981, -205910, -3585, 1186, false, 0, false, npc.getInstanceId());
					
					return "32508-02.htm";
				}
				else if (cond == 3 && st.getQuestItemsCount(ENHANCED_ANCIENT_LEGACY_SWORD) == 1 && st.getQuestItemsCount(PAILAKA_WEAPON_UPGRADE_STAGE_2) == 1)
				{
					st.playSound("ItemSound.quest_itemget");
					st.takeItems(ENHANCED_ANCIENT_LEGACY_SWORD, -1);
					st.takeItems(PAILAKA_WEAPON_UPGRADE_STAGE_2, -1);
					st.giveItems(COMPLETE_ANCIENT_LEGACY_SWORD, 1);
					
					// Spawns Gerbera
					addSpawn(GERBERA, 82387, -217356, -2705, 64839, false, 0, false, npc.getInstanceId());
					
					return "32508-03.htm";
				}
				else if (st.getQuestItemsCount(COMPLETE_ANCIENT_LEGACY_SWORD) == 1)
					return "32508-03.htm";

				return "32508-01.htm";
			case DWARF2:
				if (player.getPet() != null)
					return "32511-03.htm";
				
				if (cond == 4 && st.getQuestItemsCount(COMPLETE_ANCIENT_LEGACY_SWORD) == 1)
				{
					st.unset("cond");
					st.playSound("ItemSound.quest_finish");
					st.exitQuest(false);

					Instance inst = InstanceManager.getInstance().getInstance(npc.getInstanceId());
					inst.setDuration(EXIT_TIME * 60000);
					inst.setEmptyDestroyTime(0);

					//if (inst.containsPlayer(player.getObjectId()))
					{
						player.setVitalityPoints(20000, true);
						st.addExpAndSp(10800000, 950000);
						for (int id : REWARDS)
						{
							st.giveItems(id, 1);
						}
					}
					return "32511-01.htm";				
				}				
				else if (st.getState() == State.COMPLETED)
					return "32511-02.htm";
		}
		return getNoQuestMsg(player);
	}

	@Override
	public final String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		switch (npc.getNpcId())
		{
			case TREASURE_BOX:
				if (!npc.isDead())
					npc.doDie(attacker);
				dropItem(npc, attacker);
				break;
			case POWER_KEG:
				if (!npc.isCastingNow())
					npc.doCast(SkillTable.getInstance().getInfo(5714,1));
				break;
			case LEMATAN:
				if (!_isTeleportScheduled && npc.getCurrentHp() < (npc.getMaxHp() / 2.))
				{
					_isTeleportScheduled = true;
					startQuestTimer("lematan_teleport", 1000, npc, attacker);
				}
				break;
		}
		
		return super.onAttack(npc, attacker, damage, isPet);
	}

	@Override
	public final String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final QuestState st = player.getQuestState(qn);
		if (st == null || st.getState() != State.STARTED)
			return null;

		switch (npc.getNpcId())
		{
			case KAMS:
				if (st.getQuestItemsCount(PAILAKA_WEAPON_UPGRADE_STAGE_1) == 0)
					st.giveItems(PAILAKA_WEAPON_UPGRADE_STAGE_1, 1);

				break;
			case HIKORO:				
				// Spawns Alkaso
				addSpawn(ALKASO, 82629, -209487, -3585, 142, false, 0, false, npc.getInstanceId());
				
				break;
			case ALKASO:
				if (st.getQuestItemsCount(PAILAKA_WEAPON_UPGRADE_STAGE_2) == 0)
					st.giveItems(PAILAKA_WEAPON_UPGRADE_STAGE_2, 1);

				break;
			case GERBERA:				
				// Spawns Lematan
				_lematanNpc = addSpawn(LEMATAN, 87881, -209137, -3747, 32525, false, 0, false, npc.getInstanceId());
				
				break;
			case LEMATAN:
				if (_followerslist != null && !_followerslist.isEmpty())
				{
					for (L2Npc _follower : _followerslist)
						_follower.deleteMe();
					_followerslist.clear();
				}
				
				st.set("cond", "4");
				st.playSound("ItemSound.quest_middle");
				
				// Spawns final Dwarf
				addSpawn(DWARF2, 84990, -208376, -3342, 55000, false, 0, false, npc.getInstanceId());
				break;
			case LEMATAN_S_FOLLOWER:
				_followerslist.remove(npc);
				if (!_lematanNpc.isDead())
					ThreadPoolManager.getInstance().scheduleGeneral(new RespawnFollower(new Location(npc.getX(), npc.getY(), npc.getZ())), 5000);
				
				break;
			default:
				// hardcoded herb drops
				dropHerb(npc, player, HP_HERBS_DROPLIST);
				dropHerb(npc, player, MP_HERBS_DROPLIST);
				break;
		}
		return super.onKill(npc, player, isPet);
	}
	
	public class RespawnFollower implements Runnable
	{
		private Location _loc = null;
		
		public RespawnFollower(Location loc)
		{
			_loc = loc;
		}
		
		@Override
		public void run()
		{
			if (_lematanNpc == null || _lematanNpc.isDead())
				return;
			
			final L2Npc follower = addSpawn(LEMATAN_S_FOLLOWER, _loc.getX(), _loc.getY(), _loc.getZ(), 0, false, 0, true, _lematanNpc.getInstanceId());
			if (follower != null)
				_followerslist.add(follower);
		}
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		/* Deactivate their AI, as they only should cast heal on lematan */
		if (npc.getNpcId() == LEMATAN_S_FOLLOWER)
		{
			npc.disableCoreAI(true);
			startQuestTimer("follower_cast", 1000 + Rnd.get(100, 1000), npc, null);
		}
		
		return super.onSpawn(npc);
	}

	@Override
	public String onExitZone(L2Character character, L2ZoneType zone)
	{
		if (character instanceof L2PcInstance
				&& !character.isDead()
				&& !character.isTeleporting()
				&& ((L2PcInstance)character).isOnline())
		{
			InstanceWorld world = InstanceManager.getInstance().getWorld(character.getInstanceId());
			if (world != null && world.templateId == INSTANCE_ID)
				ThreadPoolManager.getInstance().scheduleGeneral(new Teleport(character, world.instanceId), 1000);
		}
		return super.onExitZone(character,zone);
	}

	static final class Teleport implements Runnable
	{
		private final L2Character _char;
		private final int _instanceId;

		public Teleport(L2Character c, int id)
		{
			_char = c;
			_instanceId = id;
		}

		public void run()
		{
			try
			{
				teleportPlayer((L2PcInstance)_char, TELEPORT, _instanceId);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private static class TreasureDrop
	{
		private final int _itemId;
		private final int _chance;

		public TreasureDrop(int itemId, int chance)
		{
			_itemId = itemId;
			_chance = chance;
		}
		
		public int getItemID()
		{
			return _itemId;
		}
		
		public int getChance()
		{
			return _chance;
		}
	}

	public PailakaDevilsLegacy(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(SURVIVOR);
		for (int npcId : NPCS)
			addTalkId(npcId);
		
		addAttackId(TREASURE_BOX);
		addAttackId(POWER_KEG);
		addAttackId(LEMATAN);
		addSpawnId(LEMATAN_S_FOLLOWER);
		addKillId(LEMATAN_S_FOLLOWER);
		
		for (int mobId : MONSTERS)
		{
			addKillId(mobId);
		}
		
		//addExitZoneId(ZONE);
		questItemIds = ITEMS;
		
		addExitZoneId(33030);
        addExitZoneId(33031);
        addExitZoneId(33032);
        addExitZoneId(33033);
        addExitZoneId(33034);
	}

	public static void main(String[] args)
	{
		new PailakaDevilsLegacy(129, qn, "Pailaka - Devil's Legacy");
	}
}
