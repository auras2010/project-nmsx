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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is made to create packets with any format
 * @author Maktakien
 *
 */
public class AdminForgePacket extends L2GameServerPacket
{
	private List<Part> _parts = new ArrayList<Part>();

	private static class Part
	{
		public byte b;
		public String str;

		public Part(byte bb, String string)
		{
			b = bb;
			str = string;
		}
	}

	public AdminForgePacket()
	{
	}

	@Override
	protected void writeImpl()
	{
		for(Part p : _parts)
		{
			generate(p.b, p.str);
		}

	}

	@Override
	public String getType()
	{
		return "[S] -1 AdminForge";
	}
	
	/**
	 * @param b
	 * @param string
	 */
	public boolean generate(byte b, String string)
	{
		switch (b)
		{
			case 'C':
			case 'c':
				writeC(Integer.decode(string));
				return true;
			case 'D':
			case 'd':
				writeD(Integer.decode(string));
				return true;
			case 'H':
			case 'h':
				writeH(Integer.decode(string));
				return true;
			case 'F':
			case 'f':
				writeF(Double.parseDouble(string));
				return true;
			case 'S':
			case 's':
				writeS(string);
				return true;
			case 'B':
			case 'b':
			case 'X':
			case 'x':
				writeB(new BigInteger(string).toByteArray());
				return true;
			case 'Q':
			case 'q':
				writeQ(Long.decode(string));
				return true;
		}

		return false;
	}

	public void addPart(byte b, String string)
	{
		_parts.add(new Part(b, string));
	}
}