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

import l2.universe.gameserver.model.L2Effect;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.skills.Env;
import l2.universe.gameserver.templates.effects.EffectTemplate;
import l2.universe.gameserver.templates.skills.L2EffectType;
import l2.universe.gameserver.templates.skills.L2SkillType;

/**
 * 
 * @author Gnat
 */
public class EffectNegate extends L2Effect
{
	public EffectNegate(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.NEGATE;
	}
	
	@Override
	public boolean onStart()
	{
		final L2Skill skill = getSkill();
		for (int negateSkillId : skill.getNegateId())
		{
			if (negateSkillId != 0)
				getEffected().stopSkillEffects(negateSkillId);
		}
		
		for (L2SkillType negateSkillType : skill.getNegateStats())
		{
			if (negateSkillType == null)
				continue;
			
			getEffected().stopSkillEffects(negateSkillType, skill.getNegateLvl());
		}
		
		// Synerge - Support for negate Effects
		for (L2EffectType effectType : skill.getNegateEffects())
		{
			if (effectType == null)
				continue;
			
			if (getEffected().getFirstEffect(4515) == null || effectType != L2EffectType.PETRIFICATION)
				getEffected().stopEffects(effectType);
		}
		
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}