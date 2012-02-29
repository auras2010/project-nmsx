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

public class VoiceAutoloot implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands = { "autoloot", "autolootherbs" };
	
	@Override
	public boolean useVoicedCommand(final String command, final L2PcInstance activeChar, final String target)
	{
		if (!ExternalConfig.AUTO_LOOT_INDIVIDUAL)
			return false;
		
		if (command.equalsIgnoreCase("autoloot"))
		{
			if (activeChar._useAutoLoot)
			{
				activeChar._useAutoLoot = false;
				activeChar.sendMessage("AutoLoot is desactivated.");
			}
			else
			{
				activeChar._useAutoLoot = true;
				activeChar.sendMessage("AutoLoot is activated.");
			}
		}
		else if (command.equalsIgnoreCase("autolootherbs"))
			if (activeChar._useAutoLootHerbs)
			{
				activeChar._useAutoLootHerbs = false;
				activeChar.sendMessage("AutoLoot herb is desactivated.");
			}
			else
			{
				activeChar._useAutoLootHerbs = true;
				activeChar.sendMessage("AutoLoot Herb is activated.");
			}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}
