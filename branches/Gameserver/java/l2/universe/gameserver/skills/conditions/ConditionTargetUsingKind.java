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
package l2.universe.gameserver.skills.conditions;

import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.itemcontainer.Inventory;
import l2.universe.gameserver.skills.Env;
import l2.universe.gameserver.templates.item.L2ArmorType;
import l2.universe.gameserver.templates.item.L2Item;

public class ConditionTargetUsingKind extends Condition
{
	
	private final int _mask;
	
	/**
	 * @param mask
	 */
	public ConditionTargetUsingKind(int mask)
	{
		_mask = mask;
	}
	
	@Override
	boolean testImpl(Env env)
	{
		Inventory inv = env.player.getInventory();
		
		// When target doesn't have an inventory but has an active weapon
		if (inv == null)
		{
			if (env.target.getActiveWeaponItem() == null)
				return false;
			else
				return (env.target.getActiveWeaponItem().getItemType().mask() & _mask) != 0;
		}
		
		// If ConditionUsingItemType is one between Light, Heavy or Magic
		if (_mask == L2ArmorType.LIGHT.mask() || _mask == L2ArmorType.HEAVY.mask() || _mask == L2ArmorType.MAGIC.mask())
		{
			// Get the itemMask of the weared chest (if exists)
			L2ItemInstance chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
			if (chest == null)
				return false;
			int chestMask = chest.getItem().getItemMask();
			
			// If chest armor is different from the condition one return false
			if ((_mask & chestMask) == 0)
				return false;
			
			// So from here, chest armor matches conditions
			
			int chestBodyPart = chest.getItem().getBodyPart();
			// Return True if chest armor is a Full Armor
			if (chestBodyPart == L2Item.SLOT_FULL_ARMOR)
				return true;
			else
			{
				// Check legs armor
				L2ItemInstance legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
				if (legs == null)
					return false;
				int legMask = legs.getItem().getItemMask();
				// Return true if legs armor matches too
				return (_mask & legMask) != 0;
			}
		}
		return (_mask & inv.getWearedMask()) != 0;
	}
}