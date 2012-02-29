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

import java.util.Collection;

import javolution.util.FastList;

import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.L2Playable;
import l2.universe.gameserver.model.actor.L2Summon;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.actor.instance.L2PetInstance;
import l2.universe.gameserver.network.serverpackets.NpcSay;
import l2.universe.gameserver.templates.skills.L2SkillType;
import l2.universe.gameserver.util.Util;
import l2.universe.scripts.ai.L2AttackableAIScript;
import l2.universe.util.Rnd;

/**
 * 
 * @author Synerge
 */
public class Monastery extends L2AttackableAIScript
{
	private static final int[] MOBS1 = { 22124, 22125, 22126, 22127, 22129 };
	private static final int[] MOBS2 = { 22134, 22135 };
	
	static final int[] messages = 
	{ 
	1121006, // You cannot carry a weapon without authorization!
	10077, // $s1, why would you choose the path of darkness?!
	10078 // $s1! How dare you defy the will of Einhasad!
	};
	
	public Monastery(int questId, String name, String descr)
	{
		super(questId, name, descr);
		registerMobs(MOBS1, QuestEventType.ON_AGGRO_RANGE_ENTER, QuestEventType.ON_SPAWN, QuestEventType.ON_SPELL_FINISHED);
		registerMobs(MOBS2, QuestEventType.ON_SKILL_SEE);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (contains(MOBS1, npc.getNpcId()) && !npc.isInCombat() && npc.getTarget() == null)
		{
			if (player.getActiveWeaponInstance() != null)
			{
				npc.setTarget(player);
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), messages[0]));
				switch (npc.getNpcId())
				{
					case 22124:
					case 22126:
					{
						final L2Skill skill = SkillTable.getInstance().getInfo(4589, 8);
						npc.doCast(skill);
						break;
					}
					default:
					{
						npc.setIsRunning(true);
						((L2Attackable) npc).addDamageHate(player, 0, 999);
						npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
						break;
					}
				}
			}
			else if (((L2Attackable) npc).getMostHated() == null)
				return null;
		}
		return super.onAggroRangeEnter(npc, player, isPet);
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
	{
		if (contains(MOBS2, npc.getNpcId()))
		{
			if (skill.getSkillType() == L2SkillType.AGGDAMAGE && targets.length != 0)
			{
				for (final L2Object obj : targets)
				{
					if (obj.equals(npc))
					{
						NpcSay packet = new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), messages[Rnd.get(2) + 1]);
						packet.addStringParameter(caster.getName());
						npc.broadcastPacket(packet);
						((L2Attackable) npc).addDamageHate(caster, 0, 999);
						npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, caster);
						break;
					}
				}
			}
		}
		return super.onSkillSee(npc, caster, skill, targets, isPet);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (!contains(MOBS1, npc.getNpcId()))
			return super.onSpawn(npc);
		
		final FastList<L2Playable> result = new FastList<L2Playable>();
		final Collection<L2Object> objs = npc.getKnownList().getKnownObjects().values();
		for (final L2Object obj : objs)
		{
			if (obj instanceof L2PcInstance || obj instanceof L2PetInstance)
			{
				if (Util.checkIfInRange(npc.getAggroRange(), npc, obj, true) && !((L2Character) obj).isDead())
					result.add((L2Playable) obj);
			}
		}
		
		if (!result.isEmpty() && result.size() != 0)
		{
			final Object[] characters = result.toArray();
			for (final Object obj : characters)
			{
				final L2Playable target = (L2Playable) (obj instanceof L2PcInstance ? obj : ((L2Summon) obj).getOwner());
				if (target.getActiveWeaponInstance() != null && !npc.isInCombat() && npc.getTarget() == null)
				{
					npc.setTarget(target);
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), messages[0]));
					switch (npc.getNpcId())
					{
						case 22124:
						case 22126:
						case 22127:
						{
							final L2Skill skill = SkillTable.getInstance().getInfo(4589, 8);
							npc.doCast(skill);
							break;
						}
						default:
						{
							npc.setIsRunning(true);
							((L2Attackable) npc).addDamageHate(target, 0, 999);
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
							break;
						}
					}
				}
			}
		}

		return super.onSpawn(npc);
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		if (contains(MOBS1, npc.getNpcId()) && skill.getId() == 4589)
		{
			npc.setIsRunning(true);
			((L2Attackable) npc).addDamageHate(player, 0, 999);
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	public static void main(String[] args)
	{
		new Monastery(-1, "Monastery", "ai");
	}
}
