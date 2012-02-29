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
package l2.universe.gameserver.skills.effects;

import java.util.Collection;
import java.util.List;

import javolution.util.FastList;

import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.model.CharEffectList;
import l2.universe.gameserver.model.L2Effect;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.skills.Env;
import l2.universe.gameserver.templates.effects.EffectTemplate;
import l2.universe.gameserver.templates.skills.L2EffectType;
import l2.universe.util.Rnd;

/**
 * @author littlecrow
 * 
 *         Implementation of the Confusion Effect
 */
public class EffectConfuseMob extends L2Effect
{
	public EffectConfuseMob(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.CONFUSE_MOB_ONLY;
	}
	
	@Override
	public boolean onStart()
	{
		getEffected().startConfused();
		onActionTime();
		return true;
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopConfused(this);
	}
	
	@Override
	public boolean onActionTime()
	{
		List<L2Character> targetList = new FastList<L2Character>();
		
		// Getting the possible targets
		final Collection<L2Object> objs = getEffected().getKnownList().getKnownObjects().values();
		// synchronized (getEffected().getKnownList().getKnownObjects())
		{
			for (L2Object obj : objs)
			{
				if ((obj instanceof L2Attackable) && (obj != getEffected()))
					targetList.add((L2Character) obj);
			}
		}
		
		// if there is no target, exit function
		if (targetList.isEmpty())
			return true;
		
		// Choosing randomly a new target
		final int nextTargetIdx = Rnd.nextInt(targetList.size());
		final L2Object target = targetList.get(nextTargetIdx);
		
		// Attacking the target
		getEffected().setTarget(target);
		getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		
		return true;
	}

	@Override
	public int getEffectFlags()
	{
		return CharEffectList.EFFECT_FLAG_CONFUSED;
	}
}
