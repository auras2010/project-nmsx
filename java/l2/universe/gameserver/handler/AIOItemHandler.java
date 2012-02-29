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

/**
 * @author BiggBoss
 */
public class AIOItemHandler
{
	private static FastMap<String, IAIOItemHandler> _aioItemHandlers;
	
	private AIOItemHandler()
	{
		if(_aioItemHandlers == null)
			_aioItemHandlers = new FastMap<String, IAIOItemHandler>();
	}
	
	public static AIOItemHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public void registerAIOItemHandler(IAIOItemHandler handler)
	{
		String handlerBypass = handler.getBypass();
		_aioItemHandlers.put(handlerBypass, handler);
	}
	
	public IAIOItemHandler getAIOHandler(String bypass)
	{
		return _aioItemHandlers.get(bypass);
	}
	
	public int size()
	{
		return _aioItemHandlers.size();
	}
	
	private static final class SingletonHolder
	{
		private static final AIOItemHandler _instance = new AIOItemHandler();
	}
}