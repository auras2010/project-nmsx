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

import l2.brick.Config;
import l2.brick.gameserver.model.actor.L2Playable;
import l2.brick.gameserver.model.item.instance.L2ItemInstance;
import l2.brick.gameserver.network.SystemMessageId;

public class ManaPotion extends ItemSkills
{
	/**
	 * 
	 * @see l2.brick.gameserver.handler.IItemHandler#useItem(l2.brick.gameserver.model.actor.L2Playable, l2.brick.gameserver.model.L2ItemInstance, boolean)
	 */
	@Override
	public void useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!Config.L2JMOD_ENABLE_MANA_POTIONS_SUPPORT)
		{
			playable.sendPacket(SystemMessageId.NOTHING_HAPPENED);
			return;
		}
		super.useItem(playable, item, forceUse);
	}
}