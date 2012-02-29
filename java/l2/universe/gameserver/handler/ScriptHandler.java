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

import javolution.util.FastMap;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author BiggBoss
 *
 */
public class ScriptHandler
{	
	public enum CallSite
	{
		// Entering to server
		ON_ENTER,
		// Killing any L2Character instance
		ON_KILL,
		// Dyeing
		ON_DEATH,
		// When revive
		ON_RESS,
		// Exiting from server
		ON_EXIT,
		// Using a item
		ON_USE_ITEM,
	}
	
	private static FastMap<CallSite, IScriptHandler> _handler;
	
	private ScriptHandler()
	{
		if(_handler == null)
			_handler = new FastMap<CallSite, IScriptHandler>();
	}
	
	public static ScriptHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public void registerHandler(IScriptHandler handler)
	{
		CallSite site = handler.getCallSite();
		_handler.put(site, handler);
	}
	
	public boolean execute(CallSite site, L2PcInstance activeChar, L2Object target)
	{
		IScriptHandler handler = _handler.get(site);
		if(handler != null)
			return handler.execute(activeChar, target);
		return true;
	}
	
	public int size()
	{
		return _handler.size();
	}
	
	private static final class SingletonHolder
	{
		private static final ScriptHandler _instance = new ScriptHandler();
	}
}
