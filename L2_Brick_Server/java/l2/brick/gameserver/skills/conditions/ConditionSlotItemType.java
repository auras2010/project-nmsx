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
import l2.brick.gameserver.model.item.instance.L2ItemInstance;
import l2.brick.gameserver.model.itemcontainer.Inventory;
import l2.brick.gameserver.skills.Env;

/**
 * The Class ConditionSlotItemType.
 *
 * @author mkizub
 */
public final class ConditionSlotItemType extends ConditionInventory
{
	
	private final int _mask;
	
	/**
	 * Instantiates a new condition slot item type.
	 *
	 * @param slot the slot
	 * @param mask the mask
	 */
	public ConditionSlotItemType(int slot, int mask)
	{
		super(slot);
		_mask = mask;
	}
	
	/* (non-Javadoc)
	 * @see l2.brick.gameserver.skills.conditions.ConditionInventory#testImpl(l2.brick.gameserver.skills.Env)
	 */
	@Override
	public boolean testImpl(Env env)
	{
		if (!(env.player instanceof L2PcInstance))
			return false;
		Inventory inv = ((L2PcInstance) env.player).getInventory();
		L2ItemInstance item = inv.getPaperdollItem(_slot);
		if (item == null)
			return false;
		return (item.getItem().getItemMask() & _mask) != 0;
	}
}
