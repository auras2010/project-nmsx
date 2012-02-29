package l2.universe.gameserver.skills.effects;

import l2.universe.gameserver.model.L2Effect;
import l2.universe.gameserver.skills.Env;
import l2.universe.gameserver.templates.effects.EffectTemplate;
import l2.universe.gameserver.templates.skills.L2EffectType;

/**
 * 
 * @author Synerge
 */
public class EffectShackle extends L2Effect
{
	public EffectShackle(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.SHACKLE;
	}
	
	@Override
	public boolean onStart()
	{
		getEffected().startRooted();
		return true;
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopRooting(false);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}