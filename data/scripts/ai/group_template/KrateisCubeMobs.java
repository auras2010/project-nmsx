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
package ai.group_template;

import l2.universe.gameserver.instancemanager.KrateisCubeManager;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.scripts.ai.L2AttackableAIScript;

/**
 * 
 * @author Synerge
 */
public class KrateisCubeMobs extends L2AttackableAIScript
{
	public KrateisCubeMobs(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		for (int i = 18579; i <= 18602; i++)
			addKillId(i);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		KrateisCubeManager.getInstance().addKill(killer);
		return super.onKill(npc, killer, isPet);
	}
	
	public static void main(String[] args)
	{
		new KrateisCubeMobs(-1, "KrateisCubeMobs", "ai");
	}
}
