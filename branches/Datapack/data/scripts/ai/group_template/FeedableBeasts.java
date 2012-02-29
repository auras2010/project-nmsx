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
package ai.group_template;

import java.util.Map;

import javolution.util.FastMap;

import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.datatables.NpcTable;
import l2.universe.gameserver.idfactory.IdFactory;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.actor.instance.L2TamedBeastInstance;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.network.serverpackets.SocialAction;
import l2.universe.gameserver.templates.chars.L2NpcTemplate;
import l2.universe.scripts.ai.L2AttackableAIScript;
import l2.universe.util.Rnd;

/**
 * Growth-capable mobs: Polymorphing upon successful feeding. Epilogue Beast Farm
 * @author Fulminus
 */
public class FeedableBeasts extends L2AttackableAIScript
{
	private static final int GOLDEN_SPICE = 6643;
	private static final int CRYSTAL_SPICE = 6644;
	private static final int SKILL_GOLDEN_SPICE = 2188;
	private static final int SKILL_CRYSTAL_SPICE = 2189;
	private static final int[] TAMED_BEASTS = { 16013, 16014, 16015, 16016, 16017, 16018 };
	private static final int FOODSKILLDIFF = GOLDEN_SPICE - SKILL_GOLDEN_SPICE;
	
	// All mobs that can eat...
	private static final int[] FEEDABLE_BEASTS =
	{
		21451, 21452, 21453, 21454,	21455, 21456, 21457, 21458, 21459, 21460, 21461, 21462, 21463,
		21464, 21465, 21466, 21467, 21468, 21469, 21470, 21471, 21472, 21473, 21474, 21475, 21476, 
		21477, 21478, 21479, 21480, 21481, 21482, 21483, 21484, 21485, 21486, 21487, 21488, 21489, 
		21490, 21491, 21492, 21493, 21494, 21495, 21496, 21497, 21498, 21499, 21500, 21501, 21502, 
		21503, 21504, 21505, 21506, 21507, 21824, 21825, 21826, 21827, 21828, 21829, 16013, 16014, 
		16015, 16016, 16017, 16018 
	};

	private static final Map<Integer, Integer> MAD_COW_POLYMORPH = new FastMap<Integer, Integer>();
	static
	{
		MAD_COW_POLYMORPH.put(21824, 21468);
		MAD_COW_POLYMORPH.put(21825, 21469);
		MAD_COW_POLYMORPH.put(21826, 21487);
		MAD_COW_POLYMORPH.put(21827, 21488);
		MAD_COW_POLYMORPH.put(21828, 21506);
		MAD_COW_POLYMORPH.put(21829, 21507);
	}
	
	private static final String[][] TEXT =
	{
		{
			"What did you just do to me?","You want to tame me, huh?",
			"Do not give me this. Perhaps you will be in danger.",
			"Bah bah. What is this unpalatable thing?",
			"My belly has been complaining.  This hit the spot.",
			"What is this? Can I eat it?","You don't need to worry about me.",
			"Delicious food, thanks.","I am starting to like you!","Gulp"
		},
		{
			"I do not think you have given up on the idea of taming me.",
			"That is just food to me.  Perhaps I can eat your hand too.",
			"Will eating this make me fat? Ha ha","Why do you always feed me?",
			"Do not trust me.  I may betray you"
		},
		{
			"Destroy","Look what you have done!",
			"Strange feeling...!  Evil intentions grow in my heart...!",
			"It is happenning!","This is sad...Good is sad...!"
		}
	};
	
	private static final String[] TAMED_TEXT =
	{
		"Refills! Yeah!","I am such a gluttonous beast, it is embarrassing! Ha ha",
		"Your cooperative feeling has been getting better and better.",
		"I will help you!","The weather is really good.  Wanna go for a picnic?",
		"I really like you! This is tasty...",
		"If you do not have to leave this place, then I can help you.",
		"What can I help you with?","I am not here only for food!",
		"Yam, yam, yam, yam, yam!"
	};
	
	private static Map<Integer, Integer> _FeedInfo = new FastMap<Integer, Integer>();
	private static Map<Integer, GrowthCapableMob> _GrowthCapableMobs = new FastMap<Integer, GrowthCapableMob>();
	
	// All mobs that grow by eating
	private class GrowthCapableMob
	{
		private final int _growthLevel;
		private final int _chance;
		
		private final Map<Integer, int[][]> _spiceToMob = new FastMap<Integer, int[][]>();
		
		public GrowthCapableMob(int growthLevel, int chance)
		{
			_growthLevel = growthLevel;
			_chance = chance;
		}
		
		public void addMobs(int spice, int[][] Mobs)
		{
			_spiceToMob.put(spice, Mobs);
		}
		
		public Integer getMob(int spice, int mobType, int classType)
		{
			if (_spiceToMob.containsKey(spice))
			{
				return _spiceToMob.get(spice)[mobType][classType];
			}
			return null;
		}
		
		public Integer getRandomMob(int spice)
		{
			int[][] temp = _spiceToMob.get(spice);
			final int rand = Rnd.get(temp[0].length);
			return temp[0][rand];
		}
		
		public Integer getChance()
		{
			return _chance;
		}
		
		public Integer getGrowthLevel()
		{
			return _growthLevel;
		}
	}
	
	public FeedableBeasts(int questId, String name, String descr)
	{
		super(questId, name, descr);
		registerMobs(FEEDABLE_BEASTS, QuestEventType.ON_KILL, QuestEventType.ON_SKILL_SEE);
		
		GrowthCapableMob temp;
		
		final int[][] Kookabura_0_Gold = { { 21452, 21453, 21454, 21455 } };
		final int[][] Kookabura_0_Crystal = { { 21456, 21457, 21458, 21459 } };
		final int[][] Kookabura_1_Gold_1 = { { 21460, 21462 } };
		final int[][] Kookabura_1_Gold_2 = { { 21461, 21463 } };
		final int[][] Kookabura_1_Crystal_1 = { { 21464, 21466 } };
		final int[][] Kookabura_1_Crystal_2 = { { 21465, 21467 } };
		final int[][] Kookabura_2_1 = { { 21468, 21824 }, { 16017, 16018 } };
		final int[][] Kookabura_2_2 = { { 21469, 21825 }, { 16017, 16018 } };
		
		final int[][] Buffalo_0_Gold = { { 21471, 21472, 21473, 21474 } };
		final int[][] Buffalo_0_Crystal = { { 21475, 21476, 21477, 21478 } };
		final int[][] Buffalo_1_Gold_1 = { { 21479, 21481 } };
		final int[][] Buffalo_1_Gold_2 = { { 21481, 21482 } };
		final int[][] Buffalo_1_Crystal_1 = { { 21483, 21485 } };
		final int[][] Buffalo_1_Crystal_2 = { { 21484, 21486 } };
		final int[][] Buffalo_2_1 = { { 21487, 21826 }, { 16013, 16014 } };
		final int[][] Buffalo_2_2 = { { 21488, 21827 }, { 16013, 16014 } };
		
		final int[][] Cougar_0_Gold = { { 21490, 21491, 21492, 21493 } };
		final int[][] Cougar_0_Crystal = { { 21494, 21495, 21496, 21497 } };
		final int[][] Cougar_1_Gold_1 = { { 21498, 21500 } };
		final int[][] Cougar_1_Gold_2 = { { 21499, 21501 } };
		final int[][] Cougar_1_Crystal_1 = { { 21502, 21504 } };
		final int[][] Cougar_1_Crystal_2 = { { 21503, 21505 } };
		final int[][] Cougar_2_1 = { { 21506, 21828 }, { 16015, 16016 } };
		final int[][] Cougar_2_2 = { { 21507, 21829 }, { 16015, 16016 } };
		
		// Alpen Kookabura
		temp = new GrowthCapableMob(0, 100);
		temp.addMobs(GOLDEN_SPICE, Kookabura_0_Gold);
		temp.addMobs(CRYSTAL_SPICE, Kookabura_0_Crystal);
		_GrowthCapableMobs.put(21451, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Kookabura_1_Gold_1);
		_GrowthCapableMobs.put(21452, temp);
		_GrowthCapableMobs.put(21454, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Kookabura_1_Gold_2);
		_GrowthCapableMobs.put(21453, temp);
		_GrowthCapableMobs.put(21455, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Kookabura_1_Crystal_1);
		_GrowthCapableMobs.put(21456, temp);
		_GrowthCapableMobs.put(21458, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Kookabura_1_Crystal_2);
		_GrowthCapableMobs.put(21457, temp);
		_GrowthCapableMobs.put(21459, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Kookabura_2_1);
		_GrowthCapableMobs.put(21460, temp);
		_GrowthCapableMobs.put(21462, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Kookabura_2_2);
		_GrowthCapableMobs.put(21461, temp);
		_GrowthCapableMobs.put(21463, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Kookabura_2_1);
		_GrowthCapableMobs.put(21464, temp);
		_GrowthCapableMobs.put(21466, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Kookabura_2_2);
		_GrowthCapableMobs.put(21465, temp);
		_GrowthCapableMobs.put(21467, temp);
		
		// Alpen Buffalo
		temp = new GrowthCapableMob(0, 100);
		temp.addMobs(GOLDEN_SPICE, Buffalo_0_Gold);
		temp.addMobs(CRYSTAL_SPICE, Buffalo_0_Crystal);
		_GrowthCapableMobs.put(21470, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Buffalo_1_Gold_1);
		_GrowthCapableMobs.put(21471, temp);
		_GrowthCapableMobs.put(21473, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Buffalo_1_Gold_2);
		_GrowthCapableMobs.put(21472, temp);
		_GrowthCapableMobs.put(21474, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Buffalo_1_Crystal_1);
		_GrowthCapableMobs.put(21475, temp);
		_GrowthCapableMobs.put(21477, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Buffalo_1_Crystal_2);
		_GrowthCapableMobs.put(21476, temp);
		_GrowthCapableMobs.put(21478, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Buffalo_2_1);
		_GrowthCapableMobs.put(21479, temp);
		_GrowthCapableMobs.put(21481, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Buffalo_2_2);
		_GrowthCapableMobs.put(21480, temp);
		_GrowthCapableMobs.put(21482, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Buffalo_2_1);
		_GrowthCapableMobs.put(21483, temp);
		_GrowthCapableMobs.put(21485, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Buffalo_2_2);
		_GrowthCapableMobs.put(21484, temp);
		_GrowthCapableMobs.put(21486, temp);
		
		// Alpen Cougar
		temp = new GrowthCapableMob(0, 100);
		temp.addMobs(GOLDEN_SPICE, Cougar_0_Gold);
		temp.addMobs(CRYSTAL_SPICE, Cougar_0_Crystal);
		_GrowthCapableMobs.put(21489, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Cougar_1_Gold_1);
		_GrowthCapableMobs.put(21490, temp);
		_GrowthCapableMobs.put(21492, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Cougar_1_Gold_2);
		_GrowthCapableMobs.put(21491, temp);
		_GrowthCapableMobs.put(21493, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Cougar_1_Crystal_1);
		_GrowthCapableMobs.put(21494, temp);
		_GrowthCapableMobs.put(21496, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Cougar_1_Crystal_2);
		_GrowthCapableMobs.put(21495, temp);
		_GrowthCapableMobs.put(21497, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Cougar_2_1);
		_GrowthCapableMobs.put(21498, temp);
		_GrowthCapableMobs.put(21500, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Cougar_2_2);
		_GrowthCapableMobs.put(21499, temp);
		_GrowthCapableMobs.put(21501, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Cougar_2_1);
		_GrowthCapableMobs.put(21502, temp);
		_GrowthCapableMobs.put(21504, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Cougar_2_2);
		_GrowthCapableMobs.put(21503, temp);
		_GrowthCapableMobs.put(21505, temp);
	}
	
	public void spawnNext(L2Npc npc, int growthLevel, L2PcInstance player, int food)
	{
		final int npcId = npc.getNpcId();
		int nextNpcId = 0;
		
		// Find the next mob to spawn, based on the current npcId, growthlevel, and food.
		if (growthLevel == 2)
		{
			// If tamed, the mob that will spawn depends on the class type (fighter/mage) of the player!
			if (Rnd.get(2) == 0)
			{
				if (player.getClassId().isMage())
				{
					nextNpcId = _GrowthCapableMobs.get(npcId).getMob(food, 1, 1);
				}
				else
				{
					nextNpcId = _GrowthCapableMobs.get(npcId).getMob(food, 1, 0);
				}
			}
			else
			{
				// Ff not tamed, there is a small chance that have "mad cow" disease.
				// That is a stronger-than-normal animal that attacks its feeder
				if (Rnd.get(5) == 0)
				{
					nextNpcId = _GrowthCapableMobs.get(npcId).getMob(food, 0, 1);
				}
				else
				{
					nextNpcId = _GrowthCapableMobs.get(npcId).getMob(food, 0, 0);
				}
			}
		}
		else
		{
			// All other levels of growth are straight-forward
			nextNpcId = _GrowthCapableMobs.get(npcId).getRandomMob(food);
		}
		
		// Remove the feedinfo of the mob that got despawned, if any
		if (_FeedInfo.containsKey(npc.getObjectId()))
		{
			if (_FeedInfo.get(npc.getObjectId()) == player.getObjectId())
				_FeedInfo.remove(npc.getObjectId());
		}
		
		// Despawn the old mob
		/**
		if (_GrowthCapableMobs.get(npcId).getGrowthLevel() == 0)
			npc.deleteMe();
		else
			npc.deleteMe();
			*/
		npc.deleteMe();
		
		// if this is finally a trained mob, then despawn any other trained mobs that the
		// player might have and initialize the Tamed Beast.
		if (contains(TAMED_BEASTS, nextNpcId))
		{
			if (player.getTrainedBeasts() != null && !player.getTrainedBeasts().isEmpty())
			{
				for (L2TamedBeastInstance oldTrained : player.getTrainedBeasts())
				{
					oldTrained.deleteMe();
				}
			}
			
			final L2NpcTemplate template = NpcTable.getInstance().getTemplate(nextNpcId);
			final L2TamedBeastInstance nextNpc = new L2TamedBeastInstance(IdFactory.getInstance().getNextId(), template, player, food - FOODSKILLDIFF, npc.getX(), npc.getY(), npc.getZ());
			nextNpc.setRunning();
			
			final QuestState st = player.getQuestState("_20_BringUpWithLove");
			if (st != null)
			{
				if (Rnd.get(100) <= 5 && st.getQuestItemsCount(7185) == 0)
				{
					// If player has quest 20 going, give quest item
					// It's easier to hardcode it in here than to try and repeat this stuff in the quest
					st.giveItems(7185, 1);
					st.set("cond", "2");
				}
			}
			
			// Also, perform a rare random chat
			switch (Rnd.get(20))
			{
				case 0:
					npc.broadcastNpcSay(player.getName() + ", will you show me your hideaway?");
					break;
				case 1:
					npc.broadcastNpcSay(player.getName() + ", whenever I look at spice, I think about you.");
					break;
				case 2:
					npc.broadcastNpcSay(player.getName() + ", you do not need to return to the village.  I will give you strength");
					break;
				case 3:
					npc.broadcastNpcSay("Thanks, " + player.getName() + ".  I hope I can help you");
					break;
				case 4:
					npc.broadcastNpcSay(player.getName() + ", what can I do to help you?");
					break;
			}
		}
		else
		{
			// if not trained, the newly spawned mob will automatically be agro against its feeder
			// (what happened to "never bite the hand that feeds you" anyway?!)
			final L2Attackable nextNpc = (L2Attackable) this.addSpawn(nextNpcId, npc);
			
			if (MAD_COW_POLYMORPH.containsKey(nextNpcId))
			{
				startQuestTimer("polymorph_mad_cow", 10000, nextNpc, player);
			}
			
			// Register the player in the feedinfo for the mob that just spawned
			_FeedInfo.put(nextNpc.getObjectId(), player.getObjectId());
			nextNpc.setRunning();
			nextNpc.addDamageHate(player, 0, 99999);
			nextNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("polymorph_mad_cow") && npc != null && player != null)
		{
			if (MAD_COW_POLYMORPH.containsKey(npc.getNpcId()))
			{
				// Remove the feed info from the previous mob
				if (_FeedInfo.get(npc.getObjectId()) == player.getObjectId())
					_FeedInfo.remove(npc.getObjectId());

				// Despawn the mad cow
				npc.deleteMe();
				
				// Spawn the new mob
				final L2Attackable nextNpc = (L2Attackable) addSpawn(MAD_COW_POLYMORPH.get(npc.getNpcId()), npc);
				
				// Register the player in the feedinfo for the mob that just spawned
				_FeedInfo.put(nextNpc.getObjectId(), player.getObjectId());
				nextNpc.setRunning();
				nextNpc.addDamageHate(player, 0, 99999);
				nextNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
	{
		// This behavior is only run when the target of skill is the passed npc (chest)
		// i.e. when the player is attempting to open the chest using a skill
		if (!contains(targets, npc))
			return super.onSkillSee(npc, caster, skill, targets, isPet);

		// Gather some values on local variables
		final int npcId = npc.getNpcId();
		final int skillId = skill.getId();
		
		// Check if the npc and skills used are valid for this script.  Exit if invalid.
		if (!contains(FEEDABLE_BEASTS, npcId) || (skillId != SKILL_GOLDEN_SPICE && skillId != SKILL_CRYSTAL_SPICE))
			return super.onSkillSee(npc, caster, skill, targets, isPet);
		
		// First gather some values on local variables
		final int objectId = npc.getObjectId();
		int growthLevel = 3; // if a mob is in FEEDABLE_BEASTS but not in _GrowthCapableMobs, then it's at max growth (3)
		if (_GrowthCapableMobs.containsKey(npcId))
			growthLevel = _GrowthCapableMobs.get(npcId).getGrowthLevel();
		
		// Prevent exploit which allows 2 players to simultaneously raise the same 0-growth beast
		// If the mob is at 0th level (when it still listens to all feeders) lock it to the first feeder!       
		if (growthLevel == 0 && _FeedInfo.containsKey(objectId))
			return super.onSkillSee(npc, caster, skill, targets, isPet);
		else
			_FeedInfo.put(objectId, caster.getObjectId());
		
		int food = 0;
		switch (skillId)
		{
			case SKILL_GOLDEN_SPICE:
				food = GOLDEN_SPICE;
				break;
			case SKILL_CRYSTAL_SPICE:
				food = CRYSTAL_SPICE;
				break;
		}
		
		// Display the social action of the beast eating the food.
		npc.broadcastPacket(new SocialAction(objectId, 2));
		
		// If this pet can't grow, it's all done.
		if (_GrowthCapableMobs.containsKey(npcId))
		{
			// Do nothing if this mob doesn't eat the specified food (food gets consumed but has no effect).
			if (_GrowthCapableMobs.get(npcId).getMob(food, 0, 0) == null)
				return super.onSkillSee(npc, caster, skill, targets, isPet);
			
			// Rare random talk...
			if (Rnd.get(20) == 0)
				npc.broadcastNpcSay(TEXT[growthLevel][Rnd.get(TEXT[growthLevel].length)]);
			
			if (growthLevel > 0 && _FeedInfo.get(objectId) != caster.getObjectId())
			{
				// Check if this is the same player as the one who raised it from growth 0.
				// If no, then do not allow a chance to raise the pet (food gets consumed but has no effect).
				return super.onSkillSee(npc, caster, skill, targets, isPet);
			}
			
			// Polymorph the mob, with a certain chance, given its current growth level
			if (Rnd.get(100) < _GrowthCapableMobs.get(npcId).getChance())
			{
				spawnNext(npc, growthLevel, caster, food);
			}
		}
		else if (contains(TAMED_BEASTS, npcId) && npc instanceof L2TamedBeastInstance)
		{
			final L2TamedBeastInstance beast = ((L2TamedBeastInstance) npc);
			if (skillId == beast.getFoodType())
			{
				beast.onReceiveFood();
				beast.broadcastNpcSay(TAMED_TEXT[Rnd.get(TAMED_TEXT.length)]);
			}
		}
		return super.onSkillSee(npc, caster, skill, targets, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		// Remove the feedinfo of the mob that got killed, if any
		if (_FeedInfo.containsKey(npc.getObjectId()))
			_FeedInfo.remove(npc.getObjectId());

		return super.onKill(npc, killer, isPet);
	}
	
	public static void main(String[] args)
	{
		new FeedableBeasts(-1, "FeedableBeasts", "ai");
	}
}
