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
package handlers.itemhandlers;

import l2.universe.gameserver.handler.IItemHandler;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.actor.L2Playable;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.MagicSkillUse;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.templates.item.L2Item;
import l2.universe.gameserver.templates.item.L2Weapon;
import l2.universe.gameserver.templates.item.L2WeaponType;
import l2.universe.gameserver.util.Broadcast;

/**
 * @author -Nemesiss-
 *
 */
public class FishShots implements IItemHandler
{
	private static final int[] SKILL_IDS =
	{
		2181, 2182, 2183, 2184, 2185, 2186
	};
	
	public void useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		
		final L2PcInstance activeChar = (L2PcInstance) playable;
		final L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
		final L2Weapon weaponItem = activeChar.getActiveWeaponItem();
		
		if (weaponInst == null || weaponItem.getItemType() != L2WeaponType.FISHINGROD)
			return;
		
		if (weaponInst.getChargedFishshot())
			// spirit shot is already active
			return;
		
		final int FishshotId = item.getItemId();
		final int grade = weaponItem.getCrystalType();
		final long count = item.getCount();
		
		if ((grade == L2Item.CRYSTAL_NONE && FishshotId != 6535) || (grade == L2Item.CRYSTAL_D && FishshotId != 6536) || (grade == L2Item.CRYSTAL_C && FishshotId != 6537) || (grade == L2Item.CRYSTAL_B && FishshotId != 6538)
				|| (grade == L2Item.CRYSTAL_A && FishshotId != 6539) || (FishshotId != 6540 && grade == L2Item.CRYSTAL_S ))
		{
			//1479 - This fishing shot is not fit for the fishing pole crystal.
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.WRONG_FISHINGSHOT_GRADE));
			return;
		}
		
		if (count < 1)
			return;
		
		weaponInst.setChargedFishshot(true);
		activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), 1, null, false);
		final L2Object oldTarget = activeChar.getTarget();
		activeChar.setTarget(activeChar);
		
		Broadcast.toSelfAndKnownPlayers(activeChar, new MagicSkillUse(activeChar, SKILL_IDS[grade], 1, 0, 0));
		activeChar.setTarget(oldTarget);
	}
}
