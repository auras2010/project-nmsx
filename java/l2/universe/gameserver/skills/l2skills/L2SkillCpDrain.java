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
package l2.universe.gameserver.skills.l2skills;

import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.skills.Formulas;
import l2.universe.gameserver.templates.StatsSet;

/**
 * 
 * @author L2Emu
 */
public class L2SkillCpDrain extends L2Skill
{
	public L2SkillCpDrain(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character caster, L2Object[] targets)
	{
		if (caster.isAlikeDead())
			return;
		
		for (L2Character target: (L2Character[]) targets)
		{
			if (target.isAlikeDead())
				continue;
			
			int _cp = (int) target.getStatus().getCurrentCp();
			final int damage = (int) Math.min(getPower(), _cp);
			
			if (damage > 0)
			{
				double newCp = Math.min(caster.getStatus().getCurrentCp() + damage, caster.getMaxCp());
				caster.getStatus().setCurrentCp(newCp);
				
				// Manage attack or cast break of the target (calculating rate, sending message...)
				if (!target.isRaid() && Formulas.calcAtkBreak(target, damage))
				{
					target.breakAttack();
					target.breakCast();
				}
				
				getEffects(caster, target);
				
				caster.sendDamageMessage(target, damage, false, false, false);
				target.getStatus().setCurrentCp(target.getStatus().getCurrentCp() - damage);
			}
		}
	}
}
