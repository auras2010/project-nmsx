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
package l2.universe.gameserver.skills.conditions;

import java.util.ArrayList;
import java.util.List;

import l2.universe.gameserver.datatables.PetDataTable;
import l2.universe.gameserver.model.actor.L2Summon;
import l2.universe.gameserver.model.actor.instance.L2PetInstance;
import l2.universe.gameserver.skills.Env;
import l2.universe.gameserver.templates.item.L2Item;

/**
 *  The Class ConditionPetType.
 * 
 * @author JIV
 *
 */
public class ConditionPetType extends Condition
{
	private final List<Integer> _petTypes;
 	
	/**
	 * Instantiates a new condition pet type.
	 *
	 * @param petType the pet type list
	 */
	public ConditionPetType(List<Integer> petTypes)
	{
		_petTypes = petTypes;
	}
	
	/**
	 * Instantiates a new condition pet type.
	 *
	 * @param petType the pet type
	 */
	
	public ConditionPetType(int petType)
	{
		_petTypes = new ArrayList<Integer>();
		_petTypes.add(petType);
	}

	@Override
	boolean testImpl(Env env)
	{
		if (!(env.player instanceof L2PetInstance))
			return false;
		
		int npcid = ((L2Summon) env.player).getNpcId();
		
		for (int petType : _petTypes)
		{
			if ((petType & L2Item.WOLF) == L2Item.WOLF && PetDataTable.isWolf(npcid))
				return true;
			if ((petType & L2Item.HATCHLING) == L2Item.HATCHLING && PetDataTable.isHatchling(npcid))
				return true;
			if ((petType & L2Item.STRIDER) == L2Item.STRIDER && PetDataTable.isStrider(npcid))
				return true;
			if ((petType & L2Item.BABY) == L2Item.BABY && PetDataTable.isBaby(npcid))
				return true;
			if ((petType & L2Item.IMPROVED_BABY) == L2Item.IMPROVED_BABY && PetDataTable.isImprovedBaby(npcid))
				return true;
			if ((petType & L2Item.GROWN_WOLF) == L2Item.GROWN_WOLF && PetDataTable.isEvolvedWolf(npcid))
				return true;
		}
		
		return false;
	}
	
}
