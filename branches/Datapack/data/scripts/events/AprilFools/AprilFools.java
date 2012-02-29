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
package events.AprilFools;

import java.util.Calendar;

import l2.universe.gameserver.datatables.NpcTable;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2MonsterInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.network.serverpackets.ExBrBroadcastEventState;
import l2.universe.gameserver.templates.chars.L2NpcTemplate;
import l2.universe.util.Rnd;

/**
 * @author Gigiikun
 */
public class AprilFools extends Quest
{
	private boolean _isFool = false;
	private int APRIL_FOOLS_DROP_CHANCE = 7;
	
	// Year - Month - Day
	private int EVENT_DATE = 20110401;

	public AprilFools(int questId, String name, String descr)
	{
		super(questId, name, descr);

		Calendar cal = Calendar.getInstance();		
		if (cal.get(Calendar.MONTH) == Calendar.APRIL)
		{
			// On the first 3 days of April drop baguette and vesper herbs
			if (cal.get(Calendar.DAY_OF_MONTH) <= 3)
			{
				_isFool = true;
				setOnEnterWorld(true);
				registerDrops();
				_log.info("Official Events: April Fools is started.");
			}
			// On the rest Saturdays of April drop only baguette
			else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
			{
				registerDrops();
				_log.info("Official Events: April Fools is partially started.");
			}
		}
		else
			_log.info("April Fools Event not started!");
	}

	private final void registerDrops()
	{
		for (int level = 1; level < 100; level++)
		{
			L2NpcTemplate[] templates = NpcTable.getInstance().getAllMonstersOfLevel(level);
			if (templates != null && templates.length > 0)
			{
				for (L2NpcTemplate t : templates)
				{
					addEventId(t.getNpcId(), Quest.QuestEventType.ON_KILL);
				}
			}
		}
	}

	@Override
	public String onEnterWorld(L2PcInstance player)
	{
		player.sendPacket(new ExBrBroadcastEventState(EVENT_DATE, 1));
		return null;
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		int r = Rnd.get(100);
		if (r <= APRIL_FOOLS_DROP_CHANCE && npc instanceof L2MonsterInstance)
		{
			r = Rnd.get(100);
			if (_isFool)
			{
				if (r <= 16)
					((L2MonsterInstance) npc).dropItem(killer, 20272, 1);
				else if (r < 33)
					((L2MonsterInstance) npc).dropItem(killer, 20273, 1);
				else if (r < 50)
					((L2MonsterInstance) npc).dropItem(killer, 20274, 1);
				else if (r < 67)
					((L2MonsterInstance) npc).dropItem(killer, 20923, 1);
				else if (r < 83)
					((L2MonsterInstance) npc).dropItem(killer, 20924, 1);
				else
					((L2MonsterInstance) npc).dropItem(killer, 20925, 1);
			}
			else
			{
				if (r <= 33)
					((L2MonsterInstance) npc).dropItem(killer, 20272, 1);
				else if (r < 67)
					((L2MonsterInstance) npc).dropItem(killer, 20273, 1);
				else
					((L2MonsterInstance) npc).dropItem(killer, 20274, 1);
			}
		}
		return "";
	}

	public static void main(String[] args)
	{
		new AprilFools(9555, "AprilFools", "events");
		_log.info("Official Events: April Fools is loaded.");
	}
}
