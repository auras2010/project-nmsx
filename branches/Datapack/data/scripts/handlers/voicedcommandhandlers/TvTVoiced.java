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

import l2.universe.ExternalConfig;
import l2.universe.gameserver.handler.IVoicedCommandHandler;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.events.TvTEvent;

/**
 * Tvt info.
 * 
 * @author denser
 */
public class TvTVoiced implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands = { "tvtjoin", "tvtleave" };
	
	/**
	 * Set this to false and recompile script if you dont want to use string cache.
	 * This will decrease performance but will be more consistent against possible html editions during runtime
	 * Recompiling the script will get the new html would be enough too [DrHouse]
	 */
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if(command.equalsIgnoreCase("tvtjoin"))
		{
			if(ExternalConfig.TVT_ALLOW_REGISTER_VOICED_COMMAND)
				TvTEvent.onBypass("tvt_event_participation", activeChar, true);
			else
				activeChar.sendMessage("Command disabled");
		}
		else if(command.equalsIgnoreCase("tvtleave"))
		{
			if(ExternalConfig.TVT_ALLOW_REGISTER_VOICED_COMMAND)
				TvTEvent.onBypass("tvt_event_remove_participation", activeChar, true);
			else
				activeChar.sendMessage("Command disabled");
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}
