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

public class set implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"set name",
		"set home",
		"set group"
	};
	
	@Override
	public boolean useVoicedCommand(final String command, final L2PcInstance activeChar, final String params)
	{
		if (command.startsWith("set privileges") && activeChar.getTarget() instanceof L2PcInstance)
		{
			final L2PcInstance pc = (L2PcInstance) activeChar.getTarget();
			final int n = Integer.parseInt(command.substring(15));
			if (activeChar.getClan().getClanId() == pc.getClan().getClanId() && (activeChar.getClanPrivileges() > n) || activeChar.isClanLeader())
			{
				pc.setClanPrivileges(n);
				activeChar.sendMessage("Your clan privileges have been set to " + n + " by " + activeChar.getName());
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
