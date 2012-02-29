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

import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.MagicSkillUse;
import l2.universe.scripts.ai.L2AttackableAIScript;

public class Ranku extends L2AttackableAIScript
{
	private static final int RANKU = 25542;
	private static final int RANKU_SCAGEPOAT = 32305;
	private static final int EIDOLON = 25543;
	
	public Ranku(int id, String name, String descr)
	{
		super(id, name, descr);
		addSpawnId(RANKU);
		addSpawnId(RANKU_SCAGEPOAT);
		addSpawnId(EIDOLON);
		addKillId(RANKU);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (npc == null)
			return null;

		if (event.equalsIgnoreCase("time_to_poison"))
		{
			npc.broadcastPacket(new MagicSkillUse(npc, npc, 2357, 1, 1000, 0)); // Just for animation of poison
			npc.reduceCurrentHp(300, npc, null); // Reduce hp
			if (npc.getCurrentHp() < 300)
			{
				npc.doDie(npc); // Die
				addSpawn(EIDOLON, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false, npc.getInstanceId()); // Spawn Eidolon
				return null;
			}
			startQuestTimer("time_to_poison", 15000, npc, null); //timer for reduce hp
		}
		else if (event.equalsIgnoreCase("time_to_more_eidolon"))
			addSpawn(EIDOLON, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false, npc.getInstanceId()); // One more Eidolon

		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		switch (npc.getNpcId())
		{
			case RANKU:
				for (int i = 0; i < 3; i++) // 3 npc
				{
					final int radius = 300;
					final int x = (int) (radius * Math.cos(i * 0.718));
					final int y = (int) (radius * Math.sin(i * 0.718));
					final L2Npc min = addSpawn(RANKU_SCAGEPOAT, npc.getX() + x, npc.getY() + y, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
					min.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, npc);
				}
				break;
			case RANKU_SCAGEPOAT:
				startQuestTimer("time_to_poison", 15000, npc, null); // Timer for reduce hp
				break;
			case EIDOLON:
				startQuestTimer("time_to_more_eidolon", 30000, npc, null); // Timer for spawn more Edolon
				break;
		}

		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (npc.getNpcId() == RANKU)
		{
			cancelQuestTimers("time_to_more_eidolon");
			cancelQuestTimers("time_to_poison");
		}
		
		return super.onKill(npc, killer, isPet);
	}
	
	public static void main(String[] args)
	{
		new Ranku(-1, "Ranku", "ai");
	}
}
