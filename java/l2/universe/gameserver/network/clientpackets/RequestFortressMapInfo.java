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
package l2.universe.gameserver.network.clientpackets;

import l2.universe.gameserver.instancemanager.FortManager;
import l2.universe.gameserver.network.serverpackets.ExShowFortressMapInfo;

/**
 *
 * @author  KenM
 */
public class RequestFortressMapInfo extends L2GameClientPacket
{
    private int _fortressId;
    
    @Override
    protected void readImpl()
    {
        _fortressId = readD();
    }

    @Override
    protected void runImpl()
    {
        sendPacket(new ExShowFortressMapInfo(FortManager.getInstance().getFortById(_fortressId)));
    }
    
    @Override
    public String getType()
    {
        return "[C] D0:4B RequestFortressMapInfo";
    }
}
