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
 * This class ...
 *
 * @version $Revision: 1.1.6.2 $ $Date: 2005/03/27 15:29:39 $
 */
public class PlaySound extends L2GameServerPacket
{
    private static final String _S__98_PlaySound = "[S] 9e PlaySound";
	private int _mode;
	private String _soundFile;
	private int _unknown1;
	private int _unknown2;
	private int _x;
	private int _y;
	private int _z;
	private int _unknown8;

    public PlaySound(String soundFile)
    {
		_mode = 0;
        _soundFile  = soundFile;
		_unknown1 = 0;
		_unknown2 = 0;
		_x = 0;
		_y = 0;
		_z = 0;
        _unknown8   = 0;
    }
    
    public PlaySound(int mode, String soundFile)
	{
		_mode = mode;
		_soundFile = soundFile;
		_unknown1 = 0;
		_unknown2 = 0;
		_x = 0;
		_y = 0;
		_z = 0;
		_unknown8 = 0;
	}

	public PlaySound(int mode, String soundFile, int unknown1, int unknown2, int x, int y, int z)
    {
		_mode = mode;
		_soundFile = soundFile;
		_unknown1 = 0;
		_unknown2 = 0;
		_x = 0;
		_y = 0;
		_z = 0;
		_unknown8 = 0;
    }


    @Override
	protected final void writeImpl()
    {
		writeC(0x9e);
		writeD(_mode); // 0 for quest sounds, 1 for music
		writeS(_soundFile);
		writeD(_unknown1); // unknown 0 for quest; 1 for ship;
		writeD(_unknown2); // 0 for quest; objectId of ship
		writeD(_x); // x
		writeD(_y); // y
		writeD(_z); // z
		writeD(_unknown8);
    }

    @Override
	public String getType()
    {
        return _S__98_PlaySound;
    }
}
