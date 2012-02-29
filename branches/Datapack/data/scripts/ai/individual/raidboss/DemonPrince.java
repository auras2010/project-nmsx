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

import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.scripts.ai.L2AttackableAIScript;

/**
 * @author RosT, Synerge, ButterCup
 */
public class DemonPrince extends L2AttackableAIScript
{
	private static final int PRINCE = 25540;
	private static final int PRINCE_MIN = 25541; // Don't attacks players if they didn't attacked him. After 20 sec - BOOM!
	
	public int princestatus;
	
	public DemonPrince(int id, String name, String descr)
	{
		super(id, name, descr);
		addSpawnId(PRINCE);
		addSpawnId(PRINCE_MIN);
		addAttackId(PRINCE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (npc == null)
			return null;

		if (event.equalsIgnoreCase("time_to_suicide"))
		{
			npc.doCast(SkillTable.getInstance().getInfo(4529, 1)); //Use BOOM skill
			startQuestTimer("suicide", 1700, npc, null);
		}
		else if (event.equalsIgnoreCase("suicide"))
			npc.doDie(npc); //die
		
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		switch (npc.getNpcId())
		{
			case PRINCE_MIN:
				startQuestTimer("time_to_suicide", 20000, npc, null); // Timer for kamikaze
				break;
			case PRINCE:
				princestatus = 0;
				break;
		}
		
		return super.onSpawn(npc);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (npc.getNpcId() == PRINCE)
		{
			final int maxHp = npc.getMaxHp();
			final double nowHp = npc.getStatus().getCurrentHp();
			
			// When 55%, 35%, 15%, 5% hp, use Ultimate Defense and spawns Suicides mobs (boom after 20 sec)
			switch (princestatus)
			{
				case 0:
					if (nowHp < maxHp * 0.55)
					{
						princestatus = 1;
						npc.doCast(SkillTable.getInstance().getInfo(5044, 2));
						for (int i = 0; i < 3; i++) //3 mobs
						{
							final int radius = 300;
							final int x = (int) (radius * Math.cos(i * 0.518));
							final int y = (int) (radius * Math.sin(i * 0.518));
							addSpawn(PRINCE_MIN, npc.getX() + x, npc.getY() + y, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
						}
					}
					break;
				case 1:
					if (nowHp < maxHp * 0.35)
					{
						princestatus = 2;
						npc.doCast(SkillTable.getInstance().getInfo(5044, 2));
						for (int i = 0; i < 4; i++) //4 mobs
						{
							final int radius = 300;
							final int x = (int) (radius * Math.cos(i * 0.718));
							final int y = (int) (radius * Math.sin(i * 0.718));
							addSpawn(PRINCE_MIN, npc.getX() + x, npc.getY() + y, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
						}
					}
					break;
				case 2:
					if (nowHp < maxHp * 0.15)
					{
						princestatus = 3;
						npc.doCast(SkillTable.getInstance().getInfo(5044, 2));
						for (int i = 0; i < 5; i++) //5 mobs
						{
							final int radius = 300;
							final int x = (int) (radius * Math.cos(i * 0.918));
							final int y = (int) (radius * Math.sin(i * 0.918));
							addSpawn(PRINCE_MIN, npc.getX() + x, npc.getY() + y, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
						}
					}
					break;
				case 3:
					if (nowHp < maxHp * 0.05)
					{
						princestatus = 4;
						npc.doCast(SkillTable.getInstance().getInfo(5044, 2));
						for (int i = 0; i < 6; i++) //6 mobs
						{
							final int radius = 300;
							final int x = (int) (radius * Math.cos(i * 0.918));
							final int y = (int) (radius * Math.sin(i * 0.918));
							addSpawn(PRINCE_MIN, npc.getX() + x, npc.getY() + y, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
						}
					}
					break;
			}
		}
		
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	public static void main(String[] args)
	{
		new DemonPrince(-1, "DemonPrince", "ai");
	}
}
