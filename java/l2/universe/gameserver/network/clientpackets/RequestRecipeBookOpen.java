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

import java.util.logging.Logger;

import l2.universe.Config;
import l2.universe.gameserver.RecipeController;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;


public final class RequestRecipeBookOpen extends L2GameClientPacket
{
    private static final String _C__AC_REQUESTRECIPEBOOKOPEN = "[C] AC RequestRecipeBookOpen";
	private static Logger _log = Logger.getLogger(RequestRecipeBookOpen.class.getName());

    private boolean _isDwarvenCraft;

	@Override
	protected void readImpl()
	{
        _isDwarvenCraft = (readD() == 0);
        if (Config.DEBUG)
        {
        	_log.info("RequestRecipeBookOpen : " + (_isDwarvenCraft ? "dwarvenCraft" : "commonCraft"));
        }
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		    return;

        if (activeChar.getPrivateStoreType() != 0)
        {
        	activeChar.sendMessage("Cannot use recipe book while trading");
            return;
        }

        RecipeController.getInstance().requestBookOpen(activeChar, _isDwarvenCraft);
	}

    @Override
	public String getType()
    {
        return _C__AC_REQUESTRECIPEBOOKOPEN;
    }
}
