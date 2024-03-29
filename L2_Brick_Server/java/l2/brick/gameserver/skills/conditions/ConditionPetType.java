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
package l2.brick.gameserver.skills.conditions;

import l2.brick.gameserver.datatables.PetDataTable;
import l2.brick.gameserver.model.actor.L2Summon;
import l2.brick.gameserver.model.actor.instance.L2PetInstance;
import l2.brick.gameserver.model.item.L2Item;
import l2.brick.gameserver.skills.Env;

/**
 * @author JIV
 *
 */
public class ConditionPetType extends Condition
{
	private int petType;
	
	public ConditionPetType(int petType)
	{
		this.petType = petType;
	}

	/* (non-Javadoc)
	 * @see l2.brick.gameserver.skills.conditions.Condition#testImpl(l2.brick.gameserver.skills.Env)
	 */
	@Override
	public boolean testImpl(Env env)
	{
		if (!(env.player instanceof L2PetInstance))
			return false;
		
		/*if ((petType & L2Item.ANY_PET) == L2Item.ANY_PET)
			return true;*/
		
		int npcid = ((L2Summon) env.player).getNpcId();
		
		if (PetDataTable.isHatchling(npcid) && (petType & L2Item.HATCHLING) == L2Item.HATCHLING)
			return true;
		else if (PetDataTable.isWolf(npcid) && (petType & L2Item.WOLF) == L2Item.WOLF)
			return true;
		else if (PetDataTable.isEvolvedWolf(npcid) && (petType & L2Item.GROWN_WOLF) == L2Item.GROWN_WOLF)
			return true;
		else if (PetDataTable.isStrider(npcid) && (petType & L2Item.STRIDER) == L2Item.STRIDER)
			return true;
		else if (PetDataTable.isBaby(npcid) && (petType & L2Item.BABY) == L2Item.BABY)
			return true;
		else if (PetDataTable.isImprovedBaby(npcid) && (petType & L2Item.IMPROVED_BABY) == L2Item.IMPROVED_BABY)
			return true;
		
		return false;
	}
	
}
