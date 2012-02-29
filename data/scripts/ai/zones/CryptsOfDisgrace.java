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
package ai.zones;

import l2.universe.scripts.ai.L2AttackableAIScript;
import l2.universe.gameserver.datatables.NpcTable;
import l2.universe.gameserver.datatables.SpawnTable;
import l2.universe.gameserver.model.L2Spawn;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.templates.chars.L2NpcTemplate;

public class CryptsOfDisgrace extends L2AttackableAIScript
{
	// Multiplier to compute the quadrant of the crypt
	private static final int[] MUL = { 1, -1 };
	
	// ID of Mobs
	private static final int MOREK_WARRIOR = 22703;
	private static final int BATUR_WARRIOR = 22704;
	private static final int BATUR_COMMANDER = 22705;
	private static final int TURKA_FOLLOWER = 22706;
	private static final int TURKA_COMMANDER = 22707;
	
	// used when Turka's get hitted
	// to prevent warnings until this part be used 
	/*private static final String[] GUARD_SPEAK = { // Message will be -->  "Guard of the Grave: + GUARD_SPEAK[index]"
		"I am tired! Do not wake me up again!", "Those who are in front of my eyes! will be destroyed!"
	};
	
	private static final String[] GHOST_SPEAK = { // Message will be -->  "Turka Commander's Ghost: + GHOST_SPEAK[index]"
		"Who has awakened us from our slumber?", "All will pay a severe price to me and these here",
		"All is vanity! but this cannot be the end!"
	};*/

	/**
	 * Coordinates of the locations of Morek's Spawns (got with /loc in retail in only Moreks location.
	 * places with batur nears the locs are estimated
	 * TODO: This should be stored in spawnlist?
	 */
	private static final int[][] MOREK_LOCS = 
	{ 
		{ 49532, -116144, -3808 }, 
		{ 49232, -115230, -3800 }, 
		{ 49422, -115711, -3808 }, 
		{ 50052, -115261, -3680 }, 
		{ 49865, -115068, -3680 }, 
		{ 50210, -114905, -3616 }, 
		{ 50499, -115362, -3640 }, 
		{ 50229, -115899, -3736 }, 
		{ 50583, -114999, -3608 }, 
		{ 50959, -155409, -3592 }, 
		{ 51145, -115176, -3584 }, 
		{ 51573, -115236, -3536 }, 
		{ 51817, -114970, -3480 }, 
		{ 52064, -115199, -3464 }, 
		{ 52107, -115017, -3448 }, 
		{ 52410, -115055, -3376 }, 
		{ 52471, -115480, -3272 }, 
		{ 53900, -116160, -3376 }, 
		{ 54436, -115416, -3408 }, 
		{ 55249, -115585, -3504 }, 
		{ 54438, -116433, -3432 }, 
		{ 53446, -119254, -3944 }, 
		{ 53137, -119466, -4040 }, 
		{ 53031, -119255, -4048 }, 
		{ 52897, -119473, -4104 }, 
		{ 52783, -119172, -4128 }, 
		{ 45903, -115654, -3584 }, 
		{ 46026, -115493, -3624 }, 
		{ 46152, -115790, -3664 }, 
		{ 46270, -115470, -3720 }, 
		{ 46434, -116017, -3752 }, 
		{ 46679, -116073, -3736 }, 
		{ 46905, -115987, -3808 }, 
		{ 46768, -115544, -3816 }, 
		{ 46360, -115607, -3752 }, 
		{ 53902, -120699, -3968 }, 
		{ 54367, -120232, -3968 }, 
		{ 54719, -120344, -3968 }, 
		{ 54711, -120871, -3808 }, 
		{ 54123, -121040, -3984 }, 
		{ 54959, -122148, -4176 }, 
		{ 53777, -122322, -4080 }, 
		{ 53977, -122661, -4048 }, 
		{ 53939, -122967, -4056 }, 
		{ 53547, -122774, -4024 }, 
		{ 53973, -123340, -4016 }, 
		{ 53818, -123237, -4048 }, 
		{ 53220, -123297, -4048 }, 
		{ 55070, -123076, -4048 }, 
		{ 56086, -124473, -4160 }, 
		{ 56517, -123752, -4176 },
		{ 55948, -123064, -4184 }, 
		{ 55506, -125174, -3976 }, 
		{ 55191, -124877, -3800 }, 
		{ 54913, -125185, -3648 }, 
		{ 52237, -126715, -3984 }, 
		{ 51812, -125437, -4056 }, 
		{ 52764, -125587, -3808 }, 
		{ 52177, -125705, -4016 }, 
		{ 46831, -119639, -3936 }, 
		{ 47165, -120943, -4048 }, 
		{ 46930, -121387, -4032 }, 
		{ 46051, -120756, -3824 }, 
		{ 45387, -119857, -3544 }, 
		{ 44986, -119919, -3488 }, 
		{ 44852, -120168, -3496 }, 
		{ 45126, -120707, -3542 }, 
		{ 44877, -120839, -3507 }, 
		{ 44871, -121169, -3525 }, 
		{ 44148, -119760, -3404 }, 
		{ 44047, -120062, -3431 }, 
		{ 43628, -119925, -3383 }, 
		{ 43954, -121129, -3404 }, 
		{ 43905, -120836, -3396 }, 
		{ 42975, -121102, -3380 }, 
		{ 43274, -121231, -3370 }, 
		{ 43290, -120829, -3380 }, 
		{ 43182, -119427, -3382 }, 
		{ 42980, -119785, -3382 }, 
		{ 43191, -120078, -3382 }, 
		{ 43338, -120704, -3387 }, 
		{ 44124, -120329, -3445 }, 
		{ 46787, -122772, -4006 }, 
		{ 46997, -123342, -3916 }, 
		{ 46212, -123737, -3665 }, 
		{ 47203, -123713, -3818 }, 
		{ 47543, -123431, -3946 }, 
		{ 48414, -123244, -3880 }, 
		{ 48729, -124006, -3960 }, 
		{ 49087, -124295, -3741 }, 
		{ 48030, -124916, -3792 }, 
		{ 49311, -125880, -3753 }, 
		{ 50555, -125895, -3701 }, 
		{ 51294, -127343, -3823 }, 
		{ 51967, -127241, -3883 }, 
		{ 52997, -126537, -4040 }, 
		{ 52190, -124602, -4022 }, 
		{ 52488, -123563, -4045 }, 
		{ 52940, -124343, -3665 }, 
		{ 51709, -123125, -3882 }, 
		{ 50117, -122347, -4130 }, 
		{ 50283, -123609, -3799 }, 
		{ 49573, -122021, -3984 }, 
		{ 50456, -117816, -4263 }, 
		{ 48685, -119880, -4302 }, 
		{ 48250, -120598, -4059 }, 
		{ 49169, -121237, -3958 }, 
		{ 50849, -121699, -4119 }, 
		{ 50603, -123717, -3784 }, 
		{ 49055, -123049, -3937 }, 
		{ 49516, -121839, -3983 }, 
		{ 51229, -124032, -3842 }, 
		{ 51125, -121702, -3925 } 
	};
	
	// to prevent warnings until this part be used
	// Coordinates of the center of each crypt
	/*private static final int[][] CRYPTS = {
		{50156, -124909, -3242},
		{46527, -124915, -3234},
		{53886, -124920, -3202},
		{52221, -122019, -3441},
		{50159, -119119, -3730},
		{48142, -122015, -3440}
	};*/

	// Base Radius for calc (estimated) to prevent warnings until this part be used
	// private static final int R = 1000;
	
	// To prevent warnings until this part be used
	// private int nearby = 0;
	
	public CryptsOfDisgrace(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addKillId(MOREK_WARRIOR);
		addKillId(BATUR_WARRIOR);
		addKillId(BATUR_COMMANDER);
		addKillId(TURKA_FOLLOWER);
		addKillId(TURKA_COMMANDER);
		
		// Spawn Moreks
		for (int i = 0; i < MOREK_LOCS.length; i++)
		{
			final int[] loc = MOREK_LOCS[i];
			addSpawn(MOREK_WARRIOR, loc[0] + (50 + (int) Math.round(Math.random() * 50)) * MUL[(int) Math.round(Math.random() * 1)], loc[1] + (50 + (int) Math.round(Math.random() * 50)) * MUL[(int) Math.round(Math.random() * 1)], loc[2]);
		}
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		// To prevent warnings until this part be used
		//int npcId = npc.getNpcId();
		return super.onKill(npc, player, isPet);
	}
	
	/**
	 * parameters: Loc's from L2Spawn getLocx(), getLocy() or L2Npc getX(), getY()
	 * Will be used to spawn a Turka when a mob from Contamined series had been killed.
	 * return the index in int[][] CRYPTS
	 */
	// to prevent warnings until this part be used
	/*
	private int getNearbyCrypt(int x, int y)
	{
		int t = R;
		int p = R;
		for (int i = 0; i < CRYPTS.length; i++)
		{
			int[] pos = CRYPTS[i];
			// Simple formula: Math.abs(x - pos[0]) + Math.abs(y - pos[1]);
			t = (int) Math.round(Math.sqrt((Math.pow(Math.abs(Math.abs(x) - Math.abs(pos[0])), 2)) + (Math.pow(Math.abs(Math.abs(y) - Math.abs(pos[1])), 2))));
			if (t < p)
			{
			nearby = i;
				p = t;
			}
		}
		return nearby;
	}*/

	public void addSpawn(int mobId, int x, int y, int z)
	{
		L2NpcTemplate template1;
		template1 = NpcTable.getInstance().getTemplate(mobId);
		L2Spawn spawn = null;
		try
		{
			spawn = new L2Spawn(template1);
			spawn.setLocx(x);
			spawn.setLocy(y);
			spawn.setLocz(z);
			spawn.setAmount(1);
			spawn.setHeading(-1);
			spawn.setInstanceId(0);
			spawn.setRespawnDelay(40);
			SpawnTable.getInstance().addNewSpawn(spawn, false);
			spawn.init();
			spawn.startRespawn();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		new CryptsOfDisgrace(-1, "CryptsOfDisgrace", "ai");
	}
}
