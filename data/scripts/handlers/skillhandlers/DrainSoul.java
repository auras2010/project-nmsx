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

import java.util.logging.Logger;

import l2.universe.gameserver.handler.ISkillHandler;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.templates.skills.L2SkillType;

public class DrainSoul implements ISkillHandler
{
	private static Logger _log = Logger.getLogger(DrainSoul.class.getName());
	
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.DRAIN_SOUL
	};
	
	@Override
	public void useSkill(final L2Character activeChar, final L2Skill skill, final L2Object[] targets)
	{
		if (!(activeChar instanceof L2PcInstance))
			return;
		
		final L2Object[] targetList = skill.getTargetList(activeChar);
		
		if (targetList == null)
			return;
		
		_log.fine("Soul Crystal casting succeded.");
		
		// This is just a dummy skill handler for the soul crystal skill,
		// since the Soul Crystal item handler already does everything.
		
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}