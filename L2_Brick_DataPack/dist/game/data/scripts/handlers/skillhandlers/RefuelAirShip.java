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

import l2.brick.gameserver.handler.ISkillHandler;
import l2.brick.gameserver.model.L2Object;
import l2.brick.gameserver.model.L2Skill;
import l2.brick.gameserver.model.actor.L2Character;
import l2.brick.gameserver.model.actor.instance.L2AirShipInstance;
import l2.brick.gameserver.model.actor.instance.L2ControllableAirShipInstance;
import l2.brick.gameserver.model.actor.instance.L2PcInstance;
import l2.brick.gameserver.templates.skills.L2SkillType;

public class RefuelAirShip implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.REFUEL
	};
	
	/**
	 * 
	 * @see l2.brick.gameserver.handler.ISkillHandler#useSkill(l2.brick.gameserver.model.actor.L2Character, l2.brick.gameserver.model.L2Skill, l2.brick.gameserver.model.L2Object[])
	 */
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if (!(activeChar instanceof L2PcInstance))
			return;
		
		final L2AirShipInstance ship = ((L2PcInstance)activeChar).getAirShip();
		if (ship == null
				|| !(ship instanceof L2ControllableAirShipInstance)
				|| ship.getFuel() >= ship.getMaxFuel())
			return;
		
		ship.setFuel(ship.getFuel() + (int)skill.getPower());
		ship.updateAbnormalEffect(); // broadcast new fuel
	}
	
	/**
	 * 
	 * @see l2.brick.gameserver.handler.ISkillHandler#getSkillIds()
	 */
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}