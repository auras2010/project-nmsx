package transformations;

import l2.brick.gameserver.datatables.SkillTable;
import l2.brick.gameserver.instancemanager.TransformationManager;
import l2.brick.gameserver.model.L2Transformation;

public class DragonBomberWeak extends L2Transformation
{
	private static final int[] SKILLS = {580,581,582,583,5491,619};
	public DragonBomberWeak()
	{
		// id, colRadius, colHeight
		super(218, 16, 24);
	}
	
	@Override
	public void onTransform()
	{
		if (getPlayer().getTransformationId() != 218 || getPlayer().isCursedWeaponEquipped())
			return;
		
		transformedSkills();
	}
	
	public void transformedSkills()
	{
		// Death Blow (up to 4 levels)
		getPlayer().addSkill(SkillTable.getInstance().getInfo(580, 2), false);
		// Sand Cloud (up to 4 levels)
		getPlayer().addSkill(SkillTable.getInstance().getInfo(581, 2), false);
		// Scope Bleed (up to 4 levels)
		getPlayer().addSkill(SkillTable.getInstance().getInfo(582, 2), false);
		// Assimilation (up to 4 levels)
		getPlayer().addSkill(SkillTable.getInstance().getInfo(583, 2), false);
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
		// Death Blow (up to 4 levels)
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(580, 2), false);
		// Sand Cloud (up to 4 levels)
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(581, 2), false);
		// Scope Bleed (up to 4 levels)
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(582, 2), false);
		// Assimilation (up to 4 levels)
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(583, 2), false, false);
		// Decrease Bow/Crossbow Attack Speed
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(5491, 1), false);
		// Transform Dispel
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(619, 1), false);
		
		getPlayer().setTransformAllowedSkills(EMPTY_ARRAY);
	}
	
	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new DragonBomberWeak());
	}
}
