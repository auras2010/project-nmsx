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
package l2.universe.gameserver.instancemanager;

import java.util.logging.Logger;

import javolution.util.FastMap;

import l2.universe.ExternalConfig;
import l2.universe.gameserver.network.L2IrcClient;

public class IrcManager
{
	private static final Logger _log = Logger.getLogger(IrcManager.class.getName());
	private static IrcManager _instance;
	private static L2IrcClient _ircClient;
	private static boolean _initilized = false;
	private static boolean _shutdown = false;
	protected FastMap<String, String> _authedGms = new FastMap<String, String>().shared();
	
	public static final IrcManager getInstance()
	{
		if (_instance == null)
		{
			_log.info("Initializing IrcManager");
			_instance = new IrcManager();
		}
		return _instance;
	}
	
	public IrcManager()
	{
		reload();
		if (_ircClient != null)
			_ircClient.sendChan("IrcManager Initialized!");
	}
	
	public static boolean isInitialized()
	{
		return _initilized;
	}
	
	public void reload()
	{
		if (_shutdown)
			return;
		
		_authedGms.clear();
		
		if (_ircClient != null)
		{
			_ircClient.disconnect();
			_ircClient = null;
			_initilized = false;
		}
		
		try
		{
			_ircClient = new L2IrcClient(ExternalConfig.IRC_SERVER, ExternalConfig.IRC_PORT, ExternalConfig.IRC_PASS, ExternalConfig.IRC_NICK, ExternalConfig.IRC_USER, ExternalConfig.IRC_NAME, ExternalConfig.IRC_CHANNEL);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		_initilized = true;
	}
	
	public void removeConnection()
	{
		if (_ircClient != null)
		{
			_ircClient.disconnect();
			_ircClient = null;
		}
	}
	
	public void shutdown()
	{
		if (_ircClient != null)
		{
			_ircClient.quitServer("Server Shutdown in progress!");
			_ircClient = null;
		}
		_shutdown = true;
	}
	
	public void sendChan(String text)
	{
		if (_ircClient != null)
			_ircClient.sendChan(text);
	}
	
	public void sendChan(String channel, String text)
	{
		if (_ircClient != null)
			_ircClient.sendChan(channel, text);
	}
	
	public void send(String text)
	{
		if (_ircClient != null)
			_ircClient.send(text);
	}
	
	public void send(String target, String text)
	{
		if (_ircClient != null)
			_ircClient.send(target, text);
	}
	
	public FastMap<String, String> getAuthedGMs()
	{
		return _authedGms;
	}
	
	public void forcedDisconnect()
	{
		if (_ircClient != null)
			_ircClient.forcedDisconnect();
	}
}
