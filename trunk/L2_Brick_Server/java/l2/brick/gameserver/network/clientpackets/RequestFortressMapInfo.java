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
package l2.brick.gameserver.network.clientpackets;

import l2.brick.gameserver.instancemanager.FortManager;
import l2.brick.gameserver.model.entity.Fort;
import l2.brick.gameserver.network.serverpackets.ExShowFortressMapInfo;

/**
 * @author  KenM
 */
public class RequestFortressMapInfo extends L2GameClientPacket
{
	private static final String _C_D0_48_REQUESTFORTRESSMAPINFO = "[C] D0:48 RequestFortressMapInfo";
	private int _fortressId;
	
	@Override
	protected void readImpl()
	{
		_fortressId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		Fort fort = FortManager.getInstance().getFortById(_fortressId);
		sendPacket(new ExShowFortressMapInfo(fort));
	}
	
	@Override
	public String getType()
	{
		return _C_D0_48_REQUESTFORTRESSMAPINFO;
	}
}
