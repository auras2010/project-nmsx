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
import l2.brick.gameserver.network.L2GameClient;
import l2.brick.gameserver.network.serverpackets.ExShowFortressSiegeInfo;

/**
 * @author  KenM
 */
public class RequestFortressSiegeInfo extends L2GameClientPacket
{
	private static final String _C__D0_3F_REQUESTFORTRESSSIEGEINFO = "[C] D0:3F RequestFortressSiegeInfo";
	
	@Override
	protected void readImpl()
	{
		// trigger
	}
	
	@Override
	protected void runImpl()
	{
		L2GameClient client = this.getClient();
		if (client != null)
		{
			for (Fort fort : FortManager.getInstance().getForts())
			{
				if (fort != null && fort.getSiege().getIsInProgress())
				{
					client.sendPacket(new ExShowFortressSiegeInfo(fort));
				}
			}
		}
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
	
	@Override
	public String getType()
	{
		return _C__D0_3F_REQUESTFORTRESSSIEGEINFO;
	}
}
