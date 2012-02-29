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

import l2.universe.gameserver.model.CharEffectList;
import l2.universe.gameserver.model.L2Effect;
import l2.universe.gameserver.model.actor.L2Playable;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.skills.Env;
import l2.universe.gameserver.templates.effects.EffectTemplate;
import l2.universe.gameserver.templates.skills.L2EffectType;
import l2.universe.gameserver.templates.skills.L2SkillType;

public class EffectSilentMove extends L2Effect
{
	public EffectSilentMove(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	/**
 	 * 
	 * @see l2.universe.gameserver.model.L2Effect#effectCanBeStolen()
	 */
	@Override
	protected boolean effectCanBeStolen()
	{
		return true;
	}

	// Special constructor to steal this effect
	public EffectSilentMove(Env env, L2Effect effect)
	{
		super(env, effect);
	}
	
	@Override
	public boolean onStart()
	{
		super.onStart();
		
		if (getEffected() instanceof L2Playable)
		{
			L2Playable effected = (L2Playable)getEffected();
			
			// Synerge - Increase number of silent move effects
			effected.setNSilentEffects(effected.getNSilentEffects() + 1);
		}
		return true;
	}
	
	@Override
	public void onExit()
	{
		// Synerge - Check number of active silent move effects, if its 0, then remove silent effect
		if (getEffected() instanceof L2Playable)
		{
			L2Playable effected = (L2Playable)getEffected();
			
			effected.setNSilentEffects(effected.getNSilentEffects() - 1);
			
			if (effected.getNSilentEffects() == 0)
				super.onExit();
			else
				super.onStart(); // Will this work? oO
		}
		else
			super.onExit();
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.SILENT_MOVE;
	}
	
	@Override
	public boolean onActionTime()
	{
		// Only cont skills shouldn't end
		if (getSkill().getSkillType() != L2SkillType.CONT)
			return false;
		
		if (getEffected().isDead())
			return false;
		
		double manaDam = calc();
		if (manaDam > getEffected().getCurrentMp())
		{
			getEffected().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP));
			return false;
		}
		
		getEffected().reduceCurrentMp(manaDam);
		return true;
	}
	
	@Override
	public int getEffectFlags()
	{
		return CharEffectList.EFFECT_FLAG_SILENT_MOVE;
	}
}