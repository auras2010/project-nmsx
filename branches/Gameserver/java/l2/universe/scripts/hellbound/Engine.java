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

import l2.universe.gameserver.Announcements;
import l2.universe.gameserver.datatables.DoorTable;
import l2.universe.gameserver.instancemanager.HellboundManager;
import l2.universe.gameserver.model.actor.instance.L2DoorInstance;
import l2.universe.gameserver.model.quest.Quest;

public class Engine extends Quest implements Runnable
{
	private static final int UPDATE_INTERVAL = 10000;

	private static final int[][] DOOR_LIST =
	{
		{ 19250001, 5 },
		{ 19250002, 5 },
		{ 20250001, 9 },
		{ 20250002, 7 }
	};

	private static final int[] MAX_TRUST =
	{
		0, 300000, 600000, 1000000, 0
	};

	private static final String ANNOUNCE = "Hellbound now has reached level: %lvl%";

	private int _cachedLevel = -1;

	private final void onLevelChange(int newLevel)
	{
		try
		{
			HellboundManager.getInstance().setMaxTrust(MAX_TRUST[newLevel]);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			HellboundManager.getInstance().setMaxTrust(0);
		}

		HellboundManager.getInstance().doSpawn();

		for (int[] doorData : DOOR_LIST)
		{
			try
			{
				L2DoorInstance door = DoorTable.getInstance().getDoor(doorData[0]);
				if (door.getOpen())
				{
					if (newLevel < doorData[1])
						door.closeMe();
				}
				else
				{
					if (newLevel >= doorData[1])
						door.openMe();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		if (_cachedLevel >= 0)
		{
			Announcements.getInstance().announceToAll(ANNOUNCE.replace("%lvl%", String.valueOf(newLevel)));
			_log.info("HellboundEngine: New Level: " + newLevel);
		}
		_cachedLevel = newLevel;
	}

	public void run()
	{
		int level = HellboundManager.getInstance().getLevel();
		if (level == _cachedLevel)
		{
			boolean nextLevel = false;
			switch(level)
			{
				case 1:
				case 2:
				case 3:
					if (HellboundManager.getInstance().getTrust() == HellboundManager.getInstance().getMaxTrust())
						nextLevel = true;
				default:
			}

			if (nextLevel)
			{
				level++;
				HellboundManager.getInstance().setLevel(level);
				onLevelChange(level);
			}
		}
		else
			onLevelChange(level);  // first run or changed by admin
	}

	public Engine(int questId, String name, String descr)
	{
		super(questId, name, descr);
		HellboundManager.getInstance().registerEngine(this, UPDATE_INTERVAL);

		_log.info("HellboundEngine: Mode: levels 0-3");
		_log.info("HellboundEngine: Level: " + HellboundManager.getInstance().getLevel());
		_log.info("HellboundEngine: Trust: " + HellboundManager.getInstance().getTrust());
		if (HellboundManager.getInstance().isLocked())
			_log.info("HellboundEngine: State: locked");
		else
			_log.info("HellboundEngine: State: unlocked");
	}

	public static void main(String[] args)
	{
		new Engine(-1, Engine.class.getSimpleName(), "hellbound");
	}
}