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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2.universe.gameserver.handler.IAdminCommandHandler;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.L2GameClient;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;
import l2.universe.util.StringUtil;

/**
 * This class is for admins to control characters ips, dualboxs, internal and external ips
 */
public class AdminMultiBoxProtection implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS = 
	{ 
		"admin_find_ip", // find all the player connections from a given IPv4 number
		"admin_find_dualbox", //list all the IPs with more than 1 char logged in (dualbox)
		"admin_strict_find_dualbox", 
		"admin_tracert"
	};
	
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar)
	{
		if (command.startsWith("admin_find_ip"))
		{
			try
			{
				final String val = command.substring(14);
				findCharactersPerIp(activeChar, val);
			}
			catch (Exception e)
			{ //Case of empty or malformed IP number
				activeChar.sendMessage("Usage: //find_ip <www.xxx.yyy.zzz>");
				listCharacters(activeChar, 0);
			}
		}
		else if (command.startsWith("admin_find_dualbox"))
		{
			int multibox = 2;
			try
			{
				final String val = command.substring(19);
				multibox = Integer.parseInt(val);
				if (multibox < 1)
				{
					activeChar.sendMessage("Usage: //find_dualbox [number > 0]");
					return false;
				}
			}
			catch (Exception e) {}
			findDualbox(activeChar, multibox);
		}
		else if (command.startsWith("admin_strict_find_dualbox"))
		{
			int multibox = 2;
			try
			{
				final String val = command.substring(26);
				multibox = Integer.parseInt(val);
				if (multibox < 1)
				{
					activeChar.sendMessage("Usage: //strict_find_dualbox [number > 0]");
					return false;
				}
			}
			catch (final Exception e) {}
			findDualboxStrict(activeChar, multibox);
		}
		else if (command.startsWith("admin_tracert"))
		{
			final L2Object target = activeChar.getTarget();
			if (target instanceof L2PcInstance)
			{
				final L2PcInstance pl = (L2PcInstance) target;
				if (pl.getClient() == null)
				{
					activeChar.sendMessage("Client is null.");
					return false;
				}
				
				String ip;
				final int[][] trace = pl.getClient().getTrace();
				for (int i = 0; i < trace.length; i++)
				{
					ip = "";
					for (int o = 0; o < trace[0].length; o++)
					{
						ip = ip + trace[i][o];
						if (o != trace[0].length - 1)
							ip = ip + ".";
					}
					activeChar.sendMessage("Hop" + i + ": " + ip);
				}
			}
			else
				activeChar.sendMessage("Invalid target.");
		}

		return true;
	}
		
	private void listCharacters(final L2PcInstance activeChar, int page)
	{
		final Collection<L2PcInstance> allPlayers = L2World.getInstance().getAllPlayers().values();
		final L2PcInstance[] players = allPlayers.toArray(new L2PcInstance[allPlayers.size()]);
		
		final int maxCharactersPerPage = 20;		
		int maxPages = players.length / maxCharactersPerPage;
		
		if (players.length > maxCharactersPerPage * maxPages)
			maxPages++;
		
		//Check if number of users changed
		if (page > maxPages)
			page = maxPages;
		
		final int charactersStart = maxCharactersPerPage * page;
		int charactersEnd = players.length;
		if (charactersEnd - charactersStart > maxCharactersPerPage)
			charactersEnd = charactersStart + maxCharactersPerPage;
		
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile(activeChar.getHtmlPrefix(), "data/html/admin/charlist.htm");
		
		final StringBuilder replyMSG = new StringBuilder(1000);
		
		for (int x = 0; x < maxPages; x++)
		{
			final int pagenr = x + 1;
			StringUtil.append(replyMSG, "<center><a action=\"bypass -h admin_show_characters ", String.valueOf(x), "\">Page ", String.valueOf(pagenr), "</a></center>");
		}
		
		adminReply.replace("%pages%", replyMSG.toString());
		replyMSG.setLength(0);
		
		//Add player info into new Table row
		for (int i = charactersStart; i < charactersEnd; i++)			
			StringUtil.append(replyMSG, "<tr><td width=80><a action=\"bypass -h admin_character_info ", players[i].getName(), "\">", players[i].getName(), "</a></td><td width=110>", players[i].getTemplate().className, "</td><td width=40>", String.valueOf(players[i].getLevel()), "</td></tr>");
		
		adminReply.replace("%players%", replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
			
	/**
	 * @param activeChar
	 * @param IpAdress
	 * @throws IllegalArgumentException
	 */
	private void findCharactersPerIp(final L2PcInstance activeChar, final String IpAdress) throws IllegalArgumentException
	{
		boolean findDisconnected = false;
		
		if (IpAdress.equals("disconnected"))
			findDisconnected = true;
		else if (!IpAdress.matches("^(?:(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2(?:[0-4][0-9]|5[0-5]))\\.){3}(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2(?:[0-4][0-9]|5[0-5]))$"))
			throw new IllegalArgumentException("Malformed IPv4 number");
		
		final Collection<L2PcInstance> allPlayers = L2World.getInstance().getAllPlayers().values();
		final L2PcInstance[] players = allPlayers.toArray(new L2PcInstance[allPlayers.size()]);
		int CharactersFound = 0;
		L2GameClient client;
		String name, ip = "0.0.0.0";
		final StringBuilder replyMSG = new StringBuilder(1000);
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		
		adminReply.setFile(activeChar.getHtmlPrefix(), "data/html/admin/ipfind.htm");
		for (final L2PcInstance player : players)
		{
			if (player == null)
				continue;
			
			client = player.getClient();
			if (client == null)
				continue;
			
			if (client.isDetached())
			{
				if (!findDisconnected)
					continue;
			}
			else if (findDisconnected)
				continue;
			
			ip = client.getConnection().getInetAddress().getHostAddress();
			if (!ip.equals(IpAdress))
				continue;
			
			name = player.getName();
			CharactersFound = CharactersFound + 1;
			StringUtil.append(replyMSG, "<tr><td width=80><a action=\"bypass -h admin_character_list ", name, "\">", name, "</a></td><td width=110>", player.getTemplate().className, "</td><td width=40>", String.valueOf(player.getLevel()), "</td></tr>");
			
			if (CharactersFound > 20)
				break;
		}
		adminReply.replace("%results%", replyMSG.toString());
		
		final String replyMSG2;
		
		if (CharactersFound == 0)
			replyMSG2 = "s. Maybe they got d/c? :)";
		else if (CharactersFound > 20)
		{
			adminReply.replace("%number%", " more than " + String.valueOf(CharactersFound));
			replyMSG2 = "s.<br>In order to avoid you a client crash I won't <br1>display results beyond the 20th character.";
		}
		else if (CharactersFound == 1)
			replyMSG2 = ".";
		else
			replyMSG2 = "s.";
		adminReply.replace("%ip%", IpAdress);
		adminReply.replace("%number%", String.valueOf(CharactersFound));
		adminReply.replace("%end%", replyMSG2);
		activeChar.sendPacket(adminReply);
	}

	/**
	* @param activeChar
	*/
	private void findDualbox(final L2PcInstance activeChar, final int multibox)
	{
		final Collection<L2PcInstance> allPlayers = L2World.getInstance().getAllPlayers().values();
		final L2PcInstance[] players = allPlayers.toArray(new L2PcInstance[allPlayers.size()]);
		
		final Map<String, List<L2PcInstance>> ipMap = new HashMap<String, List<L2PcInstance>>();
		
		String ip = "0.0.0.0";
		L2GameClient client;
		
		final Map<String, Integer> dualboxIPs = new HashMap<String, Integer>();
		
		for (final L2PcInstance player : players)
		{
			client = player.getClient();
			if (client == null || client.isDetached())
				continue;

			ip = client.getConnection().getInetAddress().getHostAddress();
			if (ipMap.get(ip) == null)
				ipMap.put(ip, new ArrayList<L2PcInstance>());
			ipMap.get(ip).add(player);
			
			if (ipMap.get(ip).size() >= multibox)
			{
				final Integer count = dualboxIPs.get(ip);
				if (count == null)
					dualboxIPs.put(ip, multibox);
				else
					dualboxIPs.put(ip, count + 1);
			}
		}
		
		final List<String> keys = new ArrayList<String>(dualboxIPs.keySet());
		Collections.sort(keys, new Comparator<String>()
		{
			public int compare(final String left, final String right)
			{
				return dualboxIPs.get(left).compareTo(dualboxIPs.get(right));
			}
		});
		Collections.reverse(keys);
		
		final StringBuilder results = new StringBuilder();
		for (final String dualboxIP : keys)
			StringUtil.append(results, "<a action=\"bypass -h admin_find_ip " + dualboxIP + "\">" + dualboxIP + " (" + dualboxIPs.get(dualboxIP) + ")</a><br1>");
		
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile(activeChar.getHtmlPrefix(), "data/html/admin/dualbox.htm");
		adminReply.replace("%multibox%", String.valueOf(multibox));
		adminReply.replace("%results%", results.toString());
		adminReply.replace("%strict%", "");
		activeChar.sendPacket(adminReply);
	}
	
	private void findDualboxStrict(final L2PcInstance activeChar, final int multibox)
	{
		final Collection<L2PcInstance> allPlayers = L2World.getInstance().getAllPlayers().values();
		final L2PcInstance[] players = allPlayers.toArray(new L2PcInstance[allPlayers.size()]);
		
		final Map<IpTrace, List<L2PcInstance>> ipMap = new HashMap<IpTrace, List<L2PcInstance>>();
		
		L2GameClient client;
		
		final Map<IpTrace, Integer> dualboxIPs = new HashMap<IpTrace, Integer>();
		
		for (final L2PcInstance player : players)
		{
			client = player.getClient();
			if (client == null || client.isDetached())
				continue;
			
			final IpTrace pack = new IpTrace(client.getConnection().getInetAddress().getHostAddress(), client.getTrace());
			if (ipMap.get(pack) == null)
				ipMap.put(pack, new ArrayList<L2PcInstance>());
			ipMap.get(pack).add(player);
			
			if (ipMap.get(pack).size() >= multibox)
			{
				final Integer count = dualboxIPs.get(pack);
				if (count == null)
					dualboxIPs.put(pack, multibox);
				else
					dualboxIPs.put(pack, count + 1);
			}
		}
		
		final List<IpTrace> keys = new ArrayList<IpTrace>(dualboxIPs.keySet());
		Collections.sort(keys, new Comparator<IpTrace>()
		{
			public int compare(final IpTrace left, final IpTrace right)
			{
				return dualboxIPs.get(left).compareTo(dualboxIPs.get(right));
			}
		});
		Collections.reverse(keys);
		
		final StringBuilder results = new StringBuilder();
		for (final IpTrace dualboxIP : keys)
			StringUtil.append(results, "<a action=\"bypass -h admin_find_ip " + dualboxIP.ip + "\">" + dualboxIP.ip + " (" + dualboxIPs.get(dualboxIP) + ")</a><br1>");
		
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile(activeChar.getHtmlPrefix(), "data/html/admin/dualbox.htm");
		adminReply.replace("%multibox%", String.valueOf(multibox));
		adminReply.replace("%results%", results.toString());
		adminReply.replace("%strict%", "strict_");
		activeChar.sendPacket(adminReply);
	}
		
	private final class IpTrace
	{
		String ip;
		int[][] tracert;
		
		public IpTrace(final String ip, final int[][] tracert)
		{
			this.ip = ip;
			this.tracert = tracert;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((ip == null) ? 0 : ip.hashCode());
			for (final int[] array : tracert)
				result = prime * result + Arrays.hashCode(array);
			return result;
		}
		
		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final IpTrace other = (IpTrace) obj;
			if (!getOuterType().equals(other.getOuterType()))				
				return false;
			
			if (ip == null)
			{
				if (other.ip != null)
					return false;
			}
			else if (!ip.equals(other.ip))
				return false;
			
			for (int i = 0; i < tracert.length; i++)
			{
				for (int o = 0; o < tracert[0].length; o++)
					if (tracert[i][o] != other.tracert[i][o])
						return false;
			}
			return true;
		}
		
		private AdminMultiBoxProtection getOuterType()
		{
			return AdminMultiBoxProtection.this;
		}
	}
	
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
