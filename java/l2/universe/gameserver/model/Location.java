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
package l2.universe.gameserver.model;

import l2.universe.gameserver.model.actor.L2Character;

/**
 * This class ...
 *
 * @version $Revision: 1.1.4.1 $ $Date: 2005/03/27 15:29:33 $
 */
public final class Location
{
	public int _x;
	public int _y;
	public int _z;
	public int _heading;
	public int npcId;


	public Location(int x, int y, int z)
	{
		_x = x;
		_y = y;
		_z = z;
	}

	public Location(L2Object obj)
	{
		_x = obj.getX();
		_y = obj.getY();
		_z = obj.getZ();
	}
	
	public Location(L2Character obj)
	{
		_x = obj.getX();
		_y = obj.getY();
		_z = obj.getZ();
		_heading = obj.getHeading();
	}
	
	
	public Location(int x, int y, int z, int heading)
	{
		_x = x;
		_y = y;
		_z = z;
		_heading = heading;
	}

	public int getX()
	{
		return _x;
	}

	public int getY()
	{
		return _y;
	}

	public int getZ()
	{
		return _z;
	}

	public int getHeading()
	{
		return _heading;
	}
}
