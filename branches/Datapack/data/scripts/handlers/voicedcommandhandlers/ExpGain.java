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
package handlers.voicedcommandhandlers;

import l2.universe.gameserver.handler.IVoicedCommandHandler;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;

public class ExpGain implements IVoicedCommandHandler
{
	private final String[] _voicedCommands = { "expon", "xpon", "expoff", "xpoff" };
	
	@Override
	public boolean useVoicedCommand(final String command, final L2PcInstance activeChar, final String target)
	{
		if (command.equalsIgnoreCase("expon") || command.equalsIgnoreCase("xpon"))
		{
			activeChar.setExpOn(true);
			activeChar.sendMessage("You can gain Experience by killing mobs.");
		}
		else if (command.equalsIgnoreCase("expoff") || command.equalsIgnoreCase("xpoff"))
		{
			activeChar.setExpOn(false);
			activeChar.sendMessage("You can not gain Experience by killing mobs.");
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}
