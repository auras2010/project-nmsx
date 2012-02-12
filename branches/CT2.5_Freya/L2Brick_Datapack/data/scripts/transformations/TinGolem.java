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

package transformations;

import l2.brick.gameserver.datatables.SkillTable;
import l2.brick.gameserver.instancemanager.TransformationManager;
import l2.brick.gameserver.model.L2Transformation;

public class TinGolem extends L2Transformation
{
	private static final int[] SKILLS = { 940, 941, 5437, 619 };
	
	public TinGolem()
	{
		// id, colRadius, colHeight
		super(116, 13, 18.5);
	}
	
	@Override
	public void onTransform()
	{
		if (getPlayer().getTransformationId() != 116 || getPlayer().isCursedWeaponEquipped())
			return;
		
		transformedSkills();
	}
	
	public void transformedSkills()
	{
		// Fake Attack
		getPlayer().addSkill(SkillTable.getInstance().getInfo(940, 1), false);
		// Special Motion
		getPlayer().addSkill(SkillTable.getInstance().getInfo(941, 1), false);
		// Dissonance
		getPlayer().addSkill(SkillTable.getInstance().getInfo(5437, 2), false);
		// Transform Dispel
		getPlayer().addSkill(SkillTable.getInstance().getInfo(619, 1), false);
		
		getPlayer().setTransformAllowedSkills(SKILLS);
	}
	
	@Override
	public void onUntransform()
	{
		removeSkills();
	}
	
	public void removeSkills()
	{
		// Fake Attack
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(940, 1), false);
		// Special Motion
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(941, 1), false);
		// Dissonance
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(5437, 2), false);
		// Transform Dispel
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(619, 1), false);
		
		getPlayer().setTransformAllowedSkills(EMPTY_ARRAY);
	}
	
	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new TinGolem());
	}
}