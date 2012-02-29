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

import l2.universe.gameserver.datatables.DoorTable;
import l2.universe.gameserver.handler.IAdminCommandHandler;
import l2.universe.gameserver.instancemanager.CastleManager;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.actor.instance.L2DoorInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.Castle;

/**
 * This class handles following admin commands:
 * - open1 = open coloseum door 24190001
 * - open2 = open coloseum door 24190002
 * - open3 = open coloseum door 24190003
 * - open4 = open coloseum door 24190004
 * - openall = open all coloseum door
 * - close1 = close coloseum door 24190001
 * - close2 = close coloseum door 24190002
 * - close3 = close coloseum door 24190003
 * - close4 = close coloseum door 24190004
 * - closeall = close all coloseum door
 *
 * - open = open selected door
 * - close = close selected door
 * @version $Revision: 1.2.4.5 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminDoorControl implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_open",
		"admin_close",
		"admin_openall",
		"admin_closeall"
	};
	
	private static DoorTable _doorTable = DoorTable.getInstance();
	
	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar)
	{
		try
		{
			if (command.startsWith("admin_open "))
			{
				final int doorId = Integer.parseInt(command.substring(11));
				if (_doorTable.getDoor(doorId) != null)
					_doorTable.getDoor(doorId).openMe();
				else
				{
					for (final Castle castle : CastleManager.getInstance().getCastles())
					{
						if (castle.getDoor(doorId) != null)
							castle.getDoor(doorId).openMe();
					}
				}
			}
			else if (command.startsWith("admin_close "))
			{
				final int doorId = Integer.parseInt(command.substring(12));
				if (_doorTable.getDoor(doorId) != null)
					_doorTable.getDoor(doorId).closeMe();
				else
				{
					for (final Castle castle : CastleManager.getInstance().getCastles())
					{
						if (castle.getDoor(doorId) != null)
							castle.getDoor(doorId).closeMe();
					}
				}
			}
			else if (command.equals("admin_closeall"))
			{
				for (final L2DoorInstance door : _doorTable.getDoors())
					door.closeMe();
				for (final Castle castle : CastleManager.getInstance().getCastles())
				{
					for (final L2DoorInstance door : castle.getDoors())
						door.closeMe();
				}
			}
			else if (command.equals("admin_openall"))
			{
				for (final L2DoorInstance door : _doorTable.getDoors())
					door.openMe();
				for (final Castle castle : CastleManager.getInstance().getCastles())
				{
					for (final L2DoorInstance door : castle.getDoors())
						door.openMe();
				}
			}
			else if (command.equals("admin_open"))
			{
				final L2Object target = activeChar.getTarget();
				if (target instanceof L2DoorInstance)
					((L2DoorInstance) target).openMe();
				else
					activeChar.sendMessage("Incorrect target.");
			}
			else if (command.equals("admin_close"))
			{
				final L2Object target = activeChar.getTarget();
				if (target instanceof L2DoorInstance)
					((L2DoorInstance) target).closeMe();
				else
					activeChar.sendMessage("Incorrect target.");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}