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

import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.itemcontainer.Inventory;
import l2.universe.gameserver.network.serverpackets.MagicSkillUse;
import l2.universe.gameserver.network.serverpackets.NpcSay;
import l2.universe.scripts.ai.L2AttackableAIScript;
import l2.universe.util.Rnd;

/**
 * 
 * @author Synerge
 *
 */
public class CursedPigs extends L2AttackableAIScript
{
	private static final int BOOTY = 9144;
	private static final int APIGA = 9142;
	private static final int ITEMI = 9141;
	
	private static final int[] MOBS = 
	{
		13031,13032,13033,13034,13035
	};
	
	private static final int[] SKILLS = { 3261,3262 };
	
	public CursedPigs(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		for (int npc : MOBS)
			addSkillSeeId(npc);
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
	{
		if (!contains(targets, npc))
			return null;
		
		final int skillId = skill.getId();
		if (!SKILLS.equals(skillId))
			return null;
				
		int weaponId = 0;					
		
		try
		{
			// TODO: Need to be tested.
			weaponId = caster.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND).getItemId();
		} 
		catch (Exception e) {}
				
		if (weaponId != ITEMI)
			return null;
		
		final int rnd = Rnd.get(100);
		int npcId = npc.getNpcId();
		
		if (npcId == 13034 && rnd < 20)
			npcAction(caster, npc, 20, 80);
		else if (npcId == 13035 && rnd < 10)
			npcAction(caster, npc, 30, 60);
		else if (MOBS.equals(npcId) && rnd < 40)
			npcAction(caster, npc, 10, 60);
		
		return super.onSkillSee(npc, caster, skill, targets, isPet);
	}
	
	private void npcAction(L2PcInstance player, L2Npc npc, int minRND, int maxRND)
	{
		String text = "";
		
		switch (Rnd.get(3))
		{
			case 0:
				text = "You saved me, thank you, Thanks for your help!";
				break;
			case 1:
				text = "Free! Thank you!";
				break;
			case 2:
				text = "Finally the curse is lifted!";
				break;
			default:
				break;
		}
		
		npc.broadcastPacket(new MagicSkillUse(npc, npc, 5441, 1, 1, 0));
		npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), text));
		npc.reduceCurrentHp(9999999, npc, player.getLastSkillCast());
		
		final int rnd2 = Rnd.get(100);		
		if (rnd2 < minRND)
			player.addItem("Quest Item", BOOTY, 1, npc, false);
		else if (rnd2 <= maxRND)
			player.addItem("Quest Item", APIGA, 1, npc, false);
	}
	
	public static void main(String[] args)
	{
		new CursedPigs(-1, "CursedPigs", "ai");
	}
}
