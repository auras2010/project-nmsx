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
package l2.universe.scripts.hellbound;

import l2.universe.gameserver.instancemanager.HellboundManager;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;

/**
 * 
 * @author DS, based on theOne's work
 *
 */
public class Hude extends Quest
{
	private static final int HUDE = 32298;
	private static final int BASIC_CERT = 9850;
	private static final int STANDART_CERT = 9851;
	private static final int PREMIUM_CERT = 9852;
	private static final int MARK_OF_BETRAYAL = 9676;
	private static final int LIFE_FORCE = 9681;
	private static final int CONTAINED_LIFE_FORCE = 9682;
	private static final int MAP = 9994;
	private static final int STINGER = 10012;
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if ("scertif".equalsIgnoreCase(event))
		{
			if (HellboundManager.getInstance().getLevel() > 3)
			{
				if (player.getInventory().getInventoryItemCount(MARK_OF_BETRAYAL, -1, false) >= 30
						&& player.getInventory().getInventoryItemCount(STINGER, -1, false) >= 60
						&& player.getInventory().getInventoryItemCount(BASIC_CERT, -1, false) > 0)
				{
					if (player.destroyItemByItemId("Quest", MARK_OF_BETRAYAL, 30, npc, true)
							&& player.destroyItemByItemId("Quest", STINGER, 60, npc, true)
							&& player.destroyItemByItemId("Quest", BASIC_CERT, 1, npc, true))
					{
						player.addItem("Quest", STANDART_CERT, 1, npc, true);
						return "32298-getstandart.htm";
					}
				}
			}
			return "32298-nostandart.htm";
		}
		if ("pcertif".equalsIgnoreCase(event))
		{
			if (HellboundManager.getInstance().getLevel() > 6)
			{
				if (player.getInventory().getInventoryItemCount(LIFE_FORCE, -1, false) >= 56
						&& player.getInventory().getInventoryItemCount(CONTAINED_LIFE_FORCE, -1, false) >= 14
						&& player.getInventory().getInventoryItemCount(STANDART_CERT, -1, false) > 0)
				{
					if (player.destroyItemByItemId("Quest", LIFE_FORCE, 56, npc, true)
							&& player.destroyItemByItemId("Quest", CONTAINED_LIFE_FORCE, 14, npc, true)
							&& player.destroyItemByItemId("Quest", STANDART_CERT, 1, npc, true))
					{
						player.addItem("Quest", PREMIUM_CERT, 1, npc, true);
						player.addItem("Quest", MAP, 1, npc, true);
						return "32298-getpremium.htm";
					}
				}
			}
			return "32298-nopremium.htm";
		}

		return null;
	}

	@Override
	public final String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (player.getQuestState(getName()) == null)
			newQuestState(player);

		switch (HellboundManager.getInstance().getLevel())
		{
			default:
				if (player.getInventory().getInventoryItemCount(PREMIUM_CERT, -1, false) > 0)
					return "32298-premium.htm";
			case 4:
			case 5:
			case 6:
				if (player.getInventory().getInventoryItemCount(STANDART_CERT, -1, false) > 0)
					return "32298-standart.htm";
				if (player.getInventory().getInventoryItemCount(BASIC_CERT, -1, false) > 0)
					return "32298-basic.htm";
			case 1:
			case 2:
			case 3:
				return "32298-no.htm";
		}
	}

	public Hude(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addFirstTalkId(HUDE);
		addStartNpc(HUDE);
		addTalkId(HUDE);
	}

	public static void main(String[] args)
	{
		new Hude(-1, Hude.class.getSimpleName(), "hellbound");
	}
}