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

/**
 *
 * @author  chris_00
 *
 * Asks the player to join a CC
 *
 */
public class ExAskJoinMPCC extends L2GameServerPacket
{
	
	private static final String _S__FE_27_EXASKJOINMPCC = "[S] FE:1a ExAskJoinMPCC";
	
	private String _requestorName;
	
	/**
	 * @param requestorName
	 */
	public ExAskJoinMPCC(String requestorName)
	{
		_requestorName = requestorName;
	}
	
	/* (non-Javadoc)
	 * @see l2.brick.gameserver.serverpackets.ServerBasePacket#writeImpl()
	 */
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x1a);
		writeS(_requestorName);  // name of CCLeader
		
	}
	
	/* (non-Javadoc)
	 * @see l2.brick.gameserver.BasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__FE_27_EXASKJOINMPCC;
	}
	
}
