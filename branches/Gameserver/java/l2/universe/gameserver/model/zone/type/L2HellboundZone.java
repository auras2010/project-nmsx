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
package l2.universe.gameserver.model.zone.type;

import l2.universe.Config;
import l2.universe.gameserver.datatables.MapRegionTable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.zone.L2ZoneType;

public class L2HellboundZone extends L2ZoneType
{
	public L2HellboundZone(int id)
	{
		super(id);
	}

	@Override
	protected void onEnter(L2Character player)
	{
		if (player instanceof L2PcInstance)
		{
			player.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, true);
			player.setInsideZone(L2Character.ZONE_HELLBOUND, true);
			player.setInsideZone(L2Character.ZONE_NOLANDING, true);
			if (!player.isGM() || Config.ENTER_HELLBOUND_WITHOUT_QUEST)
			{
			QuestState st = ((L2PcInstance) player).getQuestState("130_PathToHellbound");
				if (st == null || !st.isCompleted())
				{
					player.teleToLocation(MapRegionTable.TeleportWhereType.Town);
					return;
				}
			}
		}
	}

@Override
	protected void onExit(L2Character player)
	{
		player.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, false);
		player.setInsideZone(L2Character.ZONE_HELLBOUND, false);
		player.setInsideZone(L2Character.ZONE_NOLANDING, false);
	}

	@Override
	public void onDieInside(L2Character player)
	{
	}

	@Override
	public void onReviveInside(L2Character player)
	{
	}
}