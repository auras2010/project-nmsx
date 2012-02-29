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
package ai.individual.grandboss;

import l2.universe.gameserver.instancemanager.ZoneManager;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.zone.L2ZoneType;
import l2.universe.gameserver.network.serverpackets.ExStartScenePlayer;
import l2.universe.scripts.ai.L2AttackableAIScript;

/**
 * Absolute custom AI for Lindvior, this boss still not exist on game, but there are animations for him, i just
 * thought i could be cool to broadcast lindvior approach to Keucereus Base each some hours
 * 
 * @author Synerge
 */
public class Lindvior extends L2AttackableAIScript
{
	private static L2ZoneType _Zone;
	
	public Lindvior(int id, String name, String descr)
	{
		super(id, name, descr);
		
		// Get zone of the Keucerus Base Town Zone
		_Zone = ZoneManager.getInstance().getZoneById(11040);
		
		// On start program Lindvior on the next hour, and then repeat each 6 hours
		startQuestTimer("lindvior_visit", 3600000, null, null);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("lindvior_visit"))
		{
			if (_Zone == null)
				return null;
			
			for (L2Character visitor : _Zone.getCharactersInside().values())
			{
				if (!(visitor instanceof L2PcInstance))
					continue;
				
				((L2PcInstance)visitor).showQuestMovie(ExStartScenePlayer.LINDVIOR);
			}
			
			// Program next Lindvior Visit in 6 hours
			startQuestTimer("lindvior_visit", 21600000, null, null);
			
			return null;
		}

		return super.onAdvEvent(event, npc, player);
	}	
	
	public static void main(String[] args)
	{
		new Lindvior(-1, "Lindvior", "ai");
	}
}
