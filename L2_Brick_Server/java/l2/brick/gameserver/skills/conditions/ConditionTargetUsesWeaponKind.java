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

import l2.brick.gameserver.model.item.L2Weapon;
import l2.brick.gameserver.skills.Env;

/**
 * The Class ConditionTargetUsesWeaponKind.
 *
 * @author mkizub
 */
public class ConditionTargetUsesWeaponKind extends Condition
{
	
	private final int _weaponMask;
	
	/**
	 * Instantiates a new condition target uses weapon kind.
	 *
	 * @param weaponMask the weapon mask
	 */
	public ConditionTargetUsesWeaponKind(int weaponMask)
	{
		_weaponMask = weaponMask;
	}
	
	/* (non-Javadoc)
	 * @see l2.brick.gameserver.skills.conditions.Condition#testImpl(l2.brick.gameserver.skills.Env)
	 */
	@Override
	public boolean testImpl(Env env)
	{
		
		if (env.target == null)
			return false;
		
		L2Weapon item = env.target.getActiveWeaponItem();
		
		if (item == null)
			return false;
		
		return (item.getItemType().mask() & _weaponMask) != 0;
	}
}
