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

import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.handler.IItemHandler;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Playable;
import l2.universe.gameserver.model.actor.instance.L2FeedableBeastInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

public class BeastSpice implements IItemHandler
{
	public void useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		
		if (!(activeChar.getTarget() instanceof L2FeedableBeastInstance))
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}
		
		int skillId = 0;
		switch (item.getItemId())
		{
			case 6643:
				skillId = 2188;
				break;
			case 6644:
				skillId = 2189;
				break;
		}
		L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);
		if (skill != null)
			activeChar.useMagic(skill, false, false);
	}
}