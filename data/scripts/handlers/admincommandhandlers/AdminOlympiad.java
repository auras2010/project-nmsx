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
package handlers.admincommandhandlers;

import l2.universe.gameserver.handler.IAdminCommandHandler;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.olympiad.Olympiad;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.templates.StatsSet;

/**
 * This class handles following admin commands: <li>add_exp_sp_to_character
 * <i>shows menu for add or remove</i> <li>add_exp_sp exp sp <i>Adds exp & sp to
 * target, displays menu if a parameter is missing</i> <li>remove_exp_sp exp sp
 * <i>Removes exp & sp from target, displays menu if a parameter is missing</i>
 * 
 * @version $Revision: 1.2.4.6 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminOlympiad implements IAdminCommandHandler
{	
	private static final String[] ADMIN_COMMANDS = 
	{ 
		"admin_addolypoints", 
		"admin_removeolypoints", 
		"admin_setolypoints", 
		"admin_getolypoints" 
	};
	
	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar)
	{
		if (command.startsWith("admin_addolypoints"))
		{
			try
			{
				final String val = command.substring(19);
				final L2Object target = activeChar.getTarget();
				if (target instanceof L2PcInstance)
				{
					final L2PcInstance player = (L2PcInstance) target;
					if (player.isNoble())
					{
						final StatsSet playerStat = Olympiad.getNobleStats(player.getObjectId());
						if (playerStat == null)
						{
							activeChar.sendMessage("This player isnt played on olympiad yet!.");
							return false;
						}
						
						final int oldpoints = Olympiad.getInstance().getNoblePoints(player.getObjectId());
						final int points = oldpoints + Integer.parseInt(val);
						playerStat.set("olympiad_points", points);
						
						activeChar.sendMessage("You have changed nobless points for " + player.getName() + " to: " + points);
					}
					else
					{
						activeChar.sendMessage("This player is not nobless!");
						return false;
					}
				}
			}
			catch (final StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //addolypoints points");
			}
		}
		else if (command.startsWith("admin_removeolypoints"))
		{
			try
			{
				final String val = command.substring(22);
				final L2Object target = activeChar.getTarget();
				if (target instanceof L2PcInstance)
				{
					final L2PcInstance player = (L2PcInstance) target;
					if (player.isNoble())
					{
						final StatsSet playerStat = Olympiad.getNobleStats(player.getObjectId());
						if (playerStat == null)
						{
							activeChar.sendMessage("This player isnt played on olympiad yet!.");
							return false;
						}
						
						final int oldpoints = Olympiad.getInstance().getNoblePoints(player.getObjectId());
						int points = oldpoints - Integer.parseInt(val);
						if (points < 0)
							points = 0;
						
						playerStat.set("olympiad_points", points);
						
						activeChar.sendMessage("You have changed nobless points for " + player.getName() + " to: " + points);
					}
					else
					{
						activeChar.sendMessage("This player is not nobless!");
						return false;
					}
				}
			}
			catch (final StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //removeolypoints points");
			}
		}
		else if (command.startsWith("admin_setolypoints"))
		{
			try
			{
				final String val = command.substring(19);
				final L2Object target = activeChar.getTarget();
				if (target instanceof L2PcInstance)
				{
					final L2PcInstance player = (L2PcInstance) target;
					if (player.isNoble())
					{
						final StatsSet playerStat = Olympiad.getNobleStats(player.getObjectId());
						if (playerStat == null)
						{
							activeChar.sendMessage("This player isnt played on olympiad yet!.");
							return false;
						}
						playerStat.set("olympiad_points", Integer.parseInt(val));
						activeChar.sendMessage("You have changed nobless points for " + player.getName() + " to: " + Integer.parseInt(val));
					}
					else
					{
						activeChar.sendMessage("This player is not nobless!");
						return false;
					}
				}
			}
			catch (final StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //setolypoints points");
			}
		}
		else if (command.startsWith("admin_getolypoints"))
		{
			try
			{
				final L2Object target = activeChar.getTarget();
				if (target instanceof L2PcInstance)
				{
					final L2PcInstance player = (L2PcInstance) target;
					if (player.isNoble())
					{
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_CURRENT_RECORD_FOR_THIS_OLYMPIAD_SESSION_IS_S1_MATCHES_S2_WINS_S3_DEFEATS_YOU_HAVE_EARNED_S4_OLYMPIAD_POINTS);
						sm.addNumber(Olympiad.getInstance().getCompetitionDone(player.getObjectId()));
						sm.addNumber(Olympiad.getInstance().getCompetitionWon(player.getObjectId()));
						sm.addNumber(Olympiad.getInstance().getCompetitionLost(player.getObjectId()));
						sm.addNumber(Olympiad.getInstance().getNoblePoints(player.getObjectId()));
						activeChar.sendPacket(sm);
						sm = null;
					}
					else
					{
						activeChar.sendMessage("This player is not nobless!");
						return false;
					}
				}
			}
			catch (final StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //getolypoints");
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
