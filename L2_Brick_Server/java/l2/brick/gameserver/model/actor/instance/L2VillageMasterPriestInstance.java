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
package l2.brick.gameserver.model.actor.instance;

import l2.brick.gameserver.model.base.ClassType;
import l2.brick.gameserver.model.base.PlayerClass;
import l2.brick.gameserver.model.base.Race;
import l2.brick.gameserver.templates.chars.L2NpcTemplate;

public final class L2VillageMasterPriestInstance extends L2VillageMasterInstance
{
	public L2VillageMasterPriestInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	protected final boolean checkVillageMasterRace(PlayerClass pclass)
	{
		if (pclass == null)
			return false;
		
		return pclass.isOfRace(Race.Human) || pclass.isOfRace(Race.Elf);
	}
	
	@Override
	protected final boolean checkVillageMasterTeachType(PlayerClass pclass)
	{
		if (pclass == null)
			return false;
		
		return pclass.isOfType(ClassType.Priest);
	}
}