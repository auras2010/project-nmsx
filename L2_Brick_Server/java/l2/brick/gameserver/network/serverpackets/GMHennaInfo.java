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
package l2.brick.gameserver.network.serverpackets;

import l2.brick.gameserver.model.actor.instance.L2PcInstance;
import l2.brick.gameserver.model.item.instance.L2HennaInstance;

/**
 *
 * @author  KenM
 */
public class GMHennaInfo extends L2GameServerPacket
{
	private final L2PcInstance _activeChar;
	private final L2HennaInstance[] _hennas = new L2HennaInstance[3];
	private int _count;
	
	public GMHennaInfo(L2PcInstance activeChar)
	{
		_activeChar = activeChar;
		
		int j = 0;
		for (int i = 0; i < 3; i++)
		{
			L2HennaInstance h = _activeChar.getHenna(i+1);
			if (h != null)
			{
				_hennas[j++] = h;
			}
		}
		_count = j;
	}
	
	/**
	 * @see l2.brick.gameserver.network.serverpackets.L2GameServerPacket#getType()
	 */
	@Override
	public String getType()
	{
		return "[S] 0xf0 GMHennaInfo";
	}
	
	/**
	 * @see l2.brick.gameserver.network.serverpackets.L2GameServerPacket#writeImpl()
	 */
	@Override
	protected void writeImpl()
	{
		writeC(0xf0);
		
		writeC(_activeChar.getHennaStatINT());
		writeC(_activeChar.getHennaStatSTR());
		writeC(_activeChar.getHennaStatCON());
		writeC(_activeChar.getHennaStatMEN());
		writeC(_activeChar.getHennaStatDEX());
		writeC(_activeChar.getHennaStatWIT());
		writeD(3); // slots?
		writeD(_count); //size
		for (int i = 0; i < _count; i++)
		{
			writeD(_hennas[i].getSymbolId());
			writeD(0x01);
		}
	}
	
}
