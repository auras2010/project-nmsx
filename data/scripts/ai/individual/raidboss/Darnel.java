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
package ai.individual.raidboss;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.scripts.ai.L2AttackableAIScript;

public class Darnel extends L2AttackableAIScript
{
	private static final int DARNEL = 25531;
	private static final int ORACLE_GUIDE = 32279;
	
	public Darnel(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addKillId(DARNEL);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{	
		if (npc.getNpcId() == DARNEL)
			addSpawn(ORACLE_GUIDE, 152761, 145950, -12588, 0, false, 0, false, player.getInstanceId());
		
		return super.onKill(npc, player, isPet);
	}
	
	public static void main(String[] args)
	{
		new Darnel(-1, "Darnel", "ai");
	}
}
