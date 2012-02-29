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
package handlers.skillhandlers;

import l2.universe.gameserver.handler.ISkillHandler;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.instance.L2FortBallistaInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.templates.skills.L2SkillType;
import l2.universe.util.Rnd;

/**
 * 
 * @author Synerge
 */
public class BallistaBomb implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.BALLISTA
	};
	
	@Override
	public void useSkill(final L2Character activeChar, final L2Skill skill, final L2Object[] targets)
	{
		if (!(activeChar instanceof L2PcInstance))
			return;
		
		final L2Object[] targetList = skill.getTargetList(activeChar);
		if (targetList == null || targetList.length == 0)
			return;
		
		final L2Character target = (L2Character) targetList[0];
		if (!(target instanceof L2FortBallistaInstance))
			return;
		
		if (Rnd.get(3) == 0)
		{
			target.setIsInvul(false);
			target.reduceCurrentHp(target.getMaxHp() + 1, activeChar, skill);
		}
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}