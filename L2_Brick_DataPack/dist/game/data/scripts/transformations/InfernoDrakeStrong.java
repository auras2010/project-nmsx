package transformations;

import l2.brick.gameserver.datatables.SkillTable;
import l2.brick.gameserver.instancemanager.TransformationManager;
import l2.brick.gameserver.model.L2Transformation;

public class InfernoDrakeStrong extends L2Transformation
{
	private static final int[] SKILLS = {576,577,578,579,5491,619};
	public InfernoDrakeStrong()
	{
		// id, colRadius, colHeight
		super(213, 15, 24);
	}
	
	@Override
	public void onTransform()
	{
		if (getPlayer().getTransformationId() != 213 || getPlayer().isCursedWeaponEquipped())
			return;
		
		transformedSkills();
	}
	
	public void transformedSkills()
	{
		// Paw Strike (up to 4 levels)
		getPlayer().addSkill(SkillTable.getInstance().getInfo(576, 4), false);
		// Fire Breath (up to 4 levels)
		getPlayer().addSkill(SkillTable.getInstance().getInfo(577, 4), false);
		// Blaze Quake (up to 4 levels)
		getPlayer().addSkill(SkillTable.getInstance().getInfo(578, 4), false);
		// Fire Armor (up to 4 levels)
		getPlayer().addSkill(SkillTable.getInstance().getInfo(579, 4), false);
		// Decrease Bow/Crossbow Attack Speed
		getPlayer().addSkill(SkillTable.getInstance().getInfo(5491, 1), false);
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
		// Paw Strike (up to 4 levels)
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(576, 4), false);
		// Fire Breath (up to 4 levels)
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(577, 4), false);
		// Blaze Quake (up to 4 levels)
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(578, 4), false);
		// Fire Armor (up to 4 levels)
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(579, 4), false, false);
		// Decrease Bow/Crossbow Attack Speed
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(5491, 1), false);
		// Transform Dispel
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(619, 1), false);
		
		getPlayer().setTransformAllowedSkills(EMPTY_ARRAY);
	}
	
	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new InfernoDrakeStrong());
	}
}
