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

import l2.universe.gameserver.model.actor.instance.L2FenceInstance;

/**
 * Format: (ch)dddddd
 * d: object id
 * d: type (00 - no fence, 01 - only 4 columns, 02 - columns with fences)
 * d: x coord
 * d: y coord
 * d: z coord
 * d: width
 * d: height
 */
public class ExColosseumFenceInfoPacket extends L2GameServerPacket 
{
	private static final String _S__FE_03_EXCOLOSSEUMFENCEINFOPACKET = "[S] FE:03 ExColosseumFenceInfoPacket";
	private int _type;
	private L2FenceInstance _activeChar;
	private int _width;
	private int _height;

	public ExColosseumFenceInfoPacket(L2FenceInstance activeChar)
	{
		_activeChar = activeChar;
		_type = activeChar.getType();
		_width = activeChar.getWidth();
		_height = activeChar.getHeight();
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x03);
 
		writeD(_activeChar.getObjectId()); // ?
		writeD(_type);
		writeD(_activeChar.getX());
		writeD(_activeChar.getY());
		writeD(_activeChar.getZ());
		writeD(_width);
		writeD(_height);
	}

	@Override
	public String getType()
	{
		return _S__FE_03_EXCOLOSSEUMFENCEINFOPACKET;
	}
}
