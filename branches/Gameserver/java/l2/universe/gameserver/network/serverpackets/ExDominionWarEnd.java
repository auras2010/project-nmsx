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
package l2.universe.gameserver.network.serverpackets;

/**
 * @author JIV
 * 
 * Possibly trigger packet only, need to be verified.
 * 
 */
public class ExDominionWarEnd extends L2GameServerPacket
{
	private static final String TYPE = "[S] FE:A4 ExDominionWarEnd";
	
	/* (non-Javadoc)
	 * @see l2.universe.gameserver.network.serverpackets.L2GameServerPacket#writeImpl()
	 */
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0xA4);
	}
	
	/* (non-Javadoc)
	 * @see l2.universe.gameserver.network.serverpackets.L2GameServerPacket#getType()
	 */
	@Override
	public String getType()
	{
		return TYPE;
	}
	
}
