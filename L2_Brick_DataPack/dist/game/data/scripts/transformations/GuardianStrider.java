package transformations;

import l2.brick.gameserver.datatables.SkillTable;
import l2.brick.gameserver.instancemanager.TransformationManager;
import l2.brick.gameserver.model.L2Transformation;

public class GuardianStrider extends L2Transformation
{
	private static final int[] SKILLS = {839};
	public GuardianStrider()
	{
		// id, colRadius, colHeight
		super(123, 13, 40);
	}
	
	@Override
	public void onTransform()
	{
		if (getPlayer().getTransformationId() != 123 || getPlayer().isCursedWeaponEquipped())
			return;
		
		transformedSkills();
	}
	
	public void transformedSkills()
	{
		// Dismount
		getPlayer().addSkill(SkillTable.getInstance().getInfo(839, 1), false);
		
		getPlayer().setTransformAllowedSkills(SKILLS);
	}
	
	@Override
	public void onUntransform()
	{
		removeSkills();
	}
	
	public void removeSkills()
	{
		// Dismount
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(839, 1), false);
		
		getPlayer().setTransformAllowedSkills(EMPTY_ARRAY);
	}
	
	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new GuardianStrider());
	}
}
