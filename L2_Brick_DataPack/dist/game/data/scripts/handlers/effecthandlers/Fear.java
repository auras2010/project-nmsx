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
package handlers.effecthandlers;

import l2.brick.Config;
import l2.brick.gameserver.GeoData;
import l2.brick.gameserver.ai.CtrlIntention;
import l2.brick.gameserver.model.CharEffectList;
import l2.brick.gameserver.model.L2CharPosition;
import l2.brick.gameserver.model.L2Effect;
import l2.brick.gameserver.model.Location;
import l2.brick.gameserver.model.actor.instance.L2DefenderInstance;
import l2.brick.gameserver.model.actor.instance.L2FortCommanderInstance;
import l2.brick.gameserver.model.actor.instance.L2NpcInstance;
import l2.brick.gameserver.model.actor.instance.L2PcInstance;
import l2.brick.gameserver.model.actor.instance.L2PetInstance;
import l2.brick.gameserver.model.actor.instance.L2SiegeFlagInstance;
import l2.brick.gameserver.model.actor.instance.L2SiegeSummonInstance;
import l2.brick.gameserver.skills.Env;
import l2.brick.gameserver.templates.effects.EffectTemplate;
import l2.brick.gameserver.templates.skills.L2EffectType;

/**
 * @author littlecrow
 * 
 *         Implementation of the Fear Effect
 */
public class Fear extends L2Effect
{
	public static final int FEAR_RANGE = 500;
	
	private int _dX = -1;
	private int _dY = -1;
	
	public Fear(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	/**
	 * 
	 * @see l2.brick.gameserver.model.L2Effect#getEffectType()
	 */
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.FEAR;
	}
	
	/**
	 * 
	 * @see l2.brick.gameserver.model.L2Effect#onStart()
	 */
	@Override
	public boolean onStart()
	{
		// Fear skills cannot be used l2pcinstance to l2pcinstance. Heroic
		// Dread, Curse: Fear, Fear, Horror, Sword Symphony, Word of Fear, Hell Scream and
		// Mass Curse Fear are the exceptions.
		if (getEffected() instanceof L2PcInstance
				&& getEffector() instanceof L2PcInstance)
		{
			switch (getSkill().getId())
			{
				case 1376:
				case 1169:
				case 65:
				case 1092:
				case 98:
				case 1272:
				case 1381:
				case 763:
					break;
				default:
					return false;
			}
		}
		
		if (getEffected() instanceof L2NpcInstance
				|| getEffected() instanceof L2DefenderInstance
				|| getEffected() instanceof L2FortCommanderInstance
				|| getEffected() instanceof L2SiegeFlagInstance
				|| getEffected() instanceof L2SiegeSummonInstance)
			return false;
		
		if (!getEffected().isAfraid())
		{
			if(getEffected().isCastingNow() && getEffected().canAbortCast())
				getEffected().abortCast();

			if (getEffected().getX() > getEffector().getX())
				_dX = 1;
			if (getEffected().getY() > getEffector().getY())
				_dY = 1;
			
			getEffected().startFear();
			onActionTime();
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @see l2.brick.gameserver.model.L2Effect#onExit()
	 */
	@Override
	public void onExit()
	{
		getEffected().stopFear(false);
	}
	
	/**
	 * 
	 * @see l2.brick.gameserver.model.L2Effect#onActionTime()
	 */
	@Override
	public boolean onActionTime()
	{
		int posX = getEffected().getX();
		int posY = getEffected().getY();
		int posZ = getEffected().getZ();
		
		if (getEffected().getX() > getEffector().getX())
			_dX = 1;
		if (getEffected().getY() > getEffector().getY())
			_dY = 1;
		
		posX += _dX * FEAR_RANGE;
		posY += _dY * FEAR_RANGE;
		
		if (Config.GEODATA > 0)
		{
			Location destiny = GeoData.getInstance().moveCheck(getEffected().getX(), getEffected().getY(), getEffected().getZ(), posX, posY, posZ, getEffected().getInstanceId());
			posX = destiny.getX();
			posY = destiny.getY();
		}
		
		if (!(getEffected() instanceof L2PetInstance))
			getEffected().setRunning();
		
		getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(posX, posY, posZ, 0));
		return true;
	}

	/* (non-Javadoc)
	 * @see l2.brick.gameserver.model.L2Effect#getEffectFlags()
	 */
	@Override
	public int getEffectFlags()
	{
		return CharEffectList.EFFECT_FLAG_FEAR;
	}
}
