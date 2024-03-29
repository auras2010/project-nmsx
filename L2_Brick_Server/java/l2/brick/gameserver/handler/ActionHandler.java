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
package l2.brick.gameserver.handler;

import java.util.Map;

import javolution.util.FastMap;

import l2.brick.gameserver.model.L2Object.InstanceType;

public class ActionHandler
{
	private final Map<InstanceType, IActionHandler> _actions;
	
	public static ActionHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private ActionHandler()
	{
		_actions = new FastMap<InstanceType, IActionHandler>();
	}
	
	public void registerHandler(IActionHandler handler)
	{
		_actions.put(handler.getInstanceType(), handler);
	}
	
	public IActionHandler getHandler(InstanceType iType)
	{
		IActionHandler result = null;
		for (InstanceType t = iType; t != null; t = t.getParent())
		{
			result = _actions.get(t);
			if (result != null)
				break;
		}
		return result;
	}
	
	public int size()
	{
		return _actions.size();
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final ActionHandler _instance = new ActionHandler();
	}
}