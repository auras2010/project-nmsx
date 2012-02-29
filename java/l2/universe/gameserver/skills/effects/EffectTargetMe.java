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
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Playable;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.actor.instance.L2SiegeSummonInstance;
import l2.universe.gameserver.network.serverpackets.MyTargetSelected;
import l2.universe.gameserver.skills.Env;
import l2.universe.gameserver.templates.effects.EffectTemplate;
import l2.universe.gameserver.templates.skills.L2EffectType;

/**
 * 
 * @author -Nemesiss-
 */
public class EffectTargetMe extends L2Effect
{
	public EffectTargetMe(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.TARGET_ME;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected() instanceof L2Playable)
		{
			if (getEffected() instanceof L2SiegeSummonInstance)
				return false;

			if (getEffected().getTarget() != getEffector())
			{
				// Target is different
				getEffected().setTarget(getEffector());
				if (getEffected() instanceof L2PcInstance)
					getEffected().sendPacket(new MyTargetSelected(getEffector().getObjectId(), 0));
			}
			((L2Playable)getEffected()).setLockedTarget(getEffector());
			return true;
		}
		else if (getEffected() instanceof L2Attackable && !getEffected().isRaid())
			return true;

		return false;
	}
	
	@Override
	public void onExit()
	{
		if (getEffected() instanceof L2Playable)
			((L2Playable)getEffected()).setLockedTarget(null);
	}

	@Override
	public boolean onActionTime()
	{
		// nothing
		return false;
	}
}
