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
package l2.universe.gameserver.network;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l2.universe.Base64;
import l2.universe.ExternalConfig;
import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.handler.IIrcCommandHandler;
import l2.universe.gameserver.handler.IrcCommandHandler;
import l2.universe.gameserver.instancemanager.IrcManager;
import l2.universe.gameserver.network.serverpackets.CreatureSay;
import l2.universe.gameserver.util.Broadcast;

import org.jibble.pircbot.PircBot;

public class L2IrcClient extends PircBot
{
	protected final static Logger _log = Logger.getLogger(L2IrcClient.class.getName());
	
	private String _host;
	private int _port;
	private String _pass;
	private String _nick;
	private String _user;
	private String _name;
	private String _chan;
	private boolean _debug = ExternalConfig.IRC_DEBUG;
	protected boolean forcedDisconnect = false;
	
	public L2IrcClient(String host, int port, String pass, String nick, String user, String name, String chan)
	{
		_host = host;
		_port = port;
		_pass = pass;
		_nick = nick;
		_user = user;
		_name = name;
		_chan = chan;
		setVerbose(_debug);
		setLogin(_user);
		setVersion(_name);
		setName(_nick);
	}
	
	public void connect() throws Exception
	{
		try
		{
			startIdentServer();
		}
		catch (Exception e)
		{
			// Hide this Error?
		}
		connect(_host, _port, _pass);
	}
	
	public void forcedConnect() throws IOException
	{
		if (isConnected())
			quitServer("Restarting!");
		
		IrcManager.getInstance().reload();
	}
	
	public void forcedDisconnect()
	{
		if (isConnected())
		{
			quitServer("Evil Admin killed me.");
			forcedDisconnect = true;
		}
		IrcManager.getInstance().removeConnection();
	}
	
	public void forcedReconnect()
	{
		forcedDisconnect = false;
		IrcManager.getInstance().reload();
	}
	
	public void send(String text)
	{
		if (checkConnection())
			sendMessage(_chan, text);
	}
	
	public void send(String target, String text)
	{
		if (checkConnection())
			sendMessage(target, text);
	}
	
	public void sendChan(String channel, String text)
	{
		if (checkConnection())
			sendMessage(channel, text);
	}
	
	public void sendChan(String text)
	{
		if (checkConnection())
			sendMessage(_chan, text);
	}
	
	public boolean checkConnection()
	{
		if (!isConnected())
		{
			try
			{
				if (!forcedDisconnect)
				{
					disconnect();
					connect();
				}
			}
			catch (Exception exc)
			{
				exc.printStackTrace();
			}
		}
		return isConnected();
	}
	
	@Override
	public void onPart(String channel, String sender, String login, String hostname)
	{
		removeAccount(login, hostname);
	}
	
	@Override
	public void onNickChange(String oldNick, String login, String hostname, String newNick)
	{
		removeAccount(login, hostname);
	}
	
	@Override
	protected void onKick(String channel, String kickerNick, String kickerLogin,
	        String kickerHostname, String recipientNick, String reason)
	{
		removeAccount(recipientNick, "");
	}
	
	@Override
	protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason)
	{
		removeAccount(sourceLogin, sourceHostname);
	}
	
	@Override
	public void onPrivateMessage(String sender, String login, String hostname, String message)
	{
		if (message.startsWith("!"))
		{
			IIrcCommandHandler ircch = IrcCommandHandler.getInstance().getIrcCommandHandler(message);
			
			if (ircch != null)
				ircch.useIrcCommand(message, sender, null, IrcManager.getInstance().getAuthedGMs().containsKey(sender.toLowerCase()));
		}
		else if (message.startsWith("ident"))
		{
			StringTokenizer st = new StringTokenizer(message);
			st.nextToken();
			try
			{
				final String username = st.nextToken();
				final String password = st.nextToken();
				isAccountValid(username, password, login, sender, hostname);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else if (message.startsWith("unident"))
		{
			if (IrcManager.getInstance().getAuthedGMs().containsKey(sender.toLowerCase()))
				removeAccount(sender, hostname);
		}
	}
	
	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message)
	{
		if (message.startsWith("!"))
		{
			IIrcCommandHandler ircch = IrcCommandHandler.getInstance().getIrcCommandHandler(message);			
			if (ircch != null)
				ircch.useIrcCommand(message, sender, channel, IrcManager.getInstance().getAuthedGMs().containsKey(sender.toLowerCase()));
		}
		else if (channel.equalsIgnoreCase(_chan))
		{
			final CreatureSay cs = new CreatureSay(0, 1, "[IRC]" + sender, message);
			Broadcast.toAllOnlinePlayers(cs);
		}
	}
	
	@Override
	protected void onDisconnect()
	{
		_log.info("IRC: Disconnected");
		if (isConnected())
			disconnect();
		if (!forcedDisconnect)
		{
			IrcManager.getInstance().removeConnection();
			IrcManager.getInstance().reload();
		}
	}
	
	@Override
	protected void onConnect()
	{
		_log.info("IRC: Connected");
		if (!ExternalConfig.IRC_LOGIN_COMMAND.trim().equals(""))
			send(ExternalConfig.IRC_LOGIN_COMMAND.trim());
		if (ExternalConfig.IRC_NICKSERV)
			send(ExternalConfig.IRC_NICKSERV_NAME, "ident " + ExternalConfig.IRC_NICKSERV_PASS);
		joinChannel(_chan);
	}
	
	protected void isAccountValid(String account, String password, String ident, String nick, String ip)
	{
		boolean ok = false;
		java.sql.Connection con = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] raw = password.getBytes("UTF-8");
			byte[] hash = md.digest(raw);
			byte[] expected = null;
			int access = 0;
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT password, access_level FROM irc WHERE login=?");
			statement.setString(1, account);
			ResultSet rset = statement.executeQuery();
			if (rset.next())
			{
				expected = Base64.decode(rset.getString("password"));
				access = rset.getInt("access_level");
			}
			rset.close();
			statement.close();
			
			if (expected == null)
				return;
			else
			{
				if (access <= 0)
					return;
				
				ok = true;
				for (int i = 0; i < expected.length; i++)
				{
					if (hash[i] != expected[i])
					{
						ok = false;
						break;
					}
				}
			}
			if (ok)
			{
				if (!IrcManager.getInstance().getAuthedGMs().containsKey(ident.toLowerCase()))
				{
					IrcManager.getInstance().getAuthedGMs().put(ident.toLowerCase(), ip);
					send(nick, "Successfully Authed!");
				}
				else
					send(nick, "Already Authed!");
			}
			else
				send(nick, "Authentication Failed!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	protected void removeAccount(String nick, String ip)
	{
		if (IrcManager.getInstance().getAuthedGMs().containsKey(nick.toLowerCase()))
			IrcManager.getInstance().getAuthedGMs().remove(nick.toLowerCase());
		else if (IrcManager.getInstance().getAuthedGMs().containsValue(ip))
			IrcManager.getInstance().getAuthedGMs().remove(ip);
	}
}
