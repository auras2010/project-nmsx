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
package l2.brick.gameserver.skills.conditions;

import l2.brick.gameserver.model.actor.instance.L2PcInstance;
import l2.brick.gameserver.model.item.L2Weapon;
import l2.brick.gameserver.skills.Env;

/**
 * The Class ConditionChangeWeapon.
 *
 * @author nBd
 */
public class ConditionChangeWeapon extends Condition
{
	private final boolean _required;
	
	/**
	 * Instantiates a new condition change weapon.
	 *
	 * @param required the required
	 */
	public ConditionChangeWeapon(boolean required)
	{
		_required = required;
	}
	
	/**
	 * Test impl.
	 *
	 * @param env the env
	 * @return true, if successful
	 * @see l2.brick.gameserver.skills.conditions.Condition#testImpl(l2.brick.gameserver.skills.Env)
	 */
	@Override
	public boolean testImpl(Env env)
	{
		if (!(env.player instanceof L2PcInstance))
			return false;
		
		if (_required)
		{
			L2Weapon weaponItem = env.player.getActiveWeaponItem();
			
			if (weaponItem == null)
				return false;
			
			if (weaponItem.getChangeWeaponId() == 0)
				return false;
			
			if (((L2PcInstance)env.player).isEnchanting())
				return false;
		}
		return true;
	}
	
}