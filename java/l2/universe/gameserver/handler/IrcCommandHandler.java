/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package l2.universe.gameserver.handler;

import java.util.Map;
import java.util.logging.Logger;

import javolution.util.FastMap;
import l2.universe.Config;

public class IrcCommandHandler
{
	private static Logger _log = Logger.getLogger(IrcCommandHandler.class.getName());
	
	private static IrcCommandHandler _instance;	
	private Map<String, IIrcCommandHandler> _datatable;
	
	public static IrcCommandHandler getInstance()
	{
		if (_instance == null)
			_instance = new IrcCommandHandler();

		return _instance;
	}
	
	private IrcCommandHandler()
	{
		_datatable = new FastMap<String, IIrcCommandHandler>();
	}
	
	public void registerIrcCommandHandler(IIrcCommandHandler handler)
	{
		final String[] ids = handler.getIrcCommandList();		
		for (String element : ids)
		{
			if (Config.DEBUG)
				_log.fine("Adding handler for command " + element);
			_datatable.put(element, handler);
		}
	}
	
	public IIrcCommandHandler getIrcCommandHandler(String BypassCommand)
	{
		String command = BypassCommand;		
		if (BypassCommand.indexOf(" ") != -1)
			command = BypassCommand.substring(0, BypassCommand.indexOf(" "));
		
		if (Config.DEBUG)
			_log.fine("getting handler for command: " + command + " -> " + (_datatable.get(command) != null));
		
		return _datatable.get(command);
	}
	
	/**
	 * @return
	 */
	public int size()
	{
		return _datatable.size();
	}
}
