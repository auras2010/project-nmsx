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
package l2.brick.gameserver.model;

/**
 * This interface provides method to handle triggered skills from other objects.<br>
 * For example, other skill, an effect, etc...
 *
 * @author  DrHouse
 */
public interface IChanceSkillTrigger
{
	/**
	 * Just a flag
	 * @return 
	 */
	public boolean triggersChanceSkill();
	
	/**
	 * Triggered Id
	 * @return 
	 */
	public int getTriggeredChanceId();
	
	/**
	 * Triggered level
	 * @return 
	 */
	public int getTriggeredChanceLevel();
	
	/**
	 * Chance condition object
	 * @return 
	 */
	public ChanceCondition getTriggeredChanceCondition();
}