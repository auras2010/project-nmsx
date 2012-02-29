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



import l2.universe.gameserver.datatables.ItemTable;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.util.Rnd;

public class BaylorTreasureChest extends Quest
{
	private static int BOX = 29116;

	public BaylorTreasureChest(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addKillId(BOX);
	}
	
	private void dropItem(L2Npc npc,int itemId, int count,L2PcInstance player)
	{
		L2ItemInstance ditem = ItemTable.getInstance().createItem("Loot", itemId, count, player);
		ditem.dropMe(npc, npc.getX(), npc.getY(), npc.getZ()); 
	}
	
	@Override
	public String onKill (L2Npc npc, L2PcInstance player, boolean isPet)
	{
		int chance = Rnd.get(100);
		if (chance <= 1)
			dropItem(npc, 9470, 1, player);
		else if (chance >= 2 && chance <= 32)
			dropItem(npc, 6578, 2, player);
		else
			dropItem(npc, 6704, 10, player);
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		new BaylorTreasureChest(-1, "BaylorTreasureChest", "ai");
	}
}
