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
package l2.universe.gameserver.handler;

public interface IIrcCommandHandler
{
	/**
	 * this is the worker method that is called when someone uses an bypass command.
	 * @param activeChar
	 * @param command
	 * @return command success
	 */
	public boolean useIrcCommand(String command, String gm, String channel, boolean authed);
	
	/**
	 * this method is called at initialization to register all the item ids automatically 
	 * @return all known itemIds
	 */
	public String[] getIrcCommandList();
}