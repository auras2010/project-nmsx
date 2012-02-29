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

import java.util.Collection;
import java.util.concurrent.Future;

import l2.universe.Config;
import l2.universe.gameserver.datatables.MapRegionTable;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.model.L2Effect;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.zone.L2ZoneType;
import l2.universe.util.Rnd;

public class L2HeatOfDesertZone extends L2ZoneType
{
	private int _skillId;
	private int _chance;
	private int _initialDelay;
	private int _skillLvl;
	private int _reuse;
	private Future<?> _task;
	
	public L2HeatOfDesertZone(int id)
	{
		super(id);
		
		// Setup skill: Heat of Desert
		_skillId = 5399;
		_skillLvl = 1;
		_chance = 100;
		_initialDelay = 0;
		_reuse = 6000;
	}

	@Override
	protected void onEnter(L2Character player)
	{
		if (player instanceof L2PcInstance)
		{
			player.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, true);
			player.setInsideZone(L2Character.ZONE_HEAT_OF_DESERT, true);
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
			if (_task == null)
			{
				_task = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new ApplySkillHoD(this), _initialDelay, _reuse);
			}
		}
	}

	@Override
	protected void onExit(L2Character player)
	{
		if (_characterList.isEmpty() && _task != null)
		{
		_task.cancel(true);
			_task = null;
		}
		player.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, false);
		player.setInsideZone(L2Character.ZONE_HEAT_OF_DESERT, false);
		player.setInsideZone(L2Character.ZONE_NOLANDING, false);
		// if player exit zone and have skill 5399 so this skill should be removed ?? For now disable
		player.stopSkillEffects(_skillId);
	}
	public L2Skill getSkill()
	{
		return SkillTable.getInstance().getInfo(_skillId, _skillLvl);
	}
	
	public int getChance()
	{
		return _chance;
	}
	
	public boolean checkCond(L2Character player)
	{
		for (L2Effect e : player.getAllEffects())
		{
			if (e != null)
			{
				if (e.getSkill().getId() == 2341)
				{
					return false;
				}
			}
		}
		return true;
	}

	
	protected Collection<L2Character> getCharacterList()
	{
		return _characterList.values();
	}
	
	class ApplySkillHoD implements Runnable
	{
		private L2HeatOfDesertZone _HeatOfDesertZone;
		
		ApplySkillHoD(L2HeatOfDesertZone zone)
		{
			_HeatOfDesertZone = zone;
		}
		
		public void run()
		{
			for (L2Character temp : _HeatOfDesertZone.getCharacterList())
			{
				if (temp != null && !temp.isDead() && checkCond(temp))
				{
					if (temp instanceof L2PcInstance && Rnd.get(100) < getChance())
					{
						getSkill().getEffects(temp, temp);
					}
				}
			}
		}
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