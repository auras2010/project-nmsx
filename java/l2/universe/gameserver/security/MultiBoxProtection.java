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
package l2.universe.gameserver.security;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javolution.util.FastList;
import javolution.util.FastMap;
import l2.universe.Config;
import l2.universe.ExternalConfig;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.L2GameClient;
import l2.universe.util.StringUtil;

/**
 * This module manage advanced multibox protection, considering internal and external IPs
 * Have control over number of normal clients per Pc, vip clients per Pc, and amount of offline stores por Pc
 * Each time a new character logs in, saves the current connections from that pc, to have a fast list
 * of boxes for other uses
 * 
 * @author Synerge
 */
public class MultiBoxProtection
{
	private static final Logger _log = Logger.getLogger(MultiBoxProtection.class.getName());
	
	private Map<String, String> _pcIpMap;
	private Map<IpPack, List<L2PcInstance>> _multiBoxUsersMap;
	
	public static MultiBoxProtection getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public MultiBoxProtection()
	{
		_pcIpMap = new FastMap<String, String>();
		_multiBoxUsersMap = new HashMap<IpPack, List<L2PcInstance>>();
	}
	
	/**
	 *  Synerge - New system for multibox, this check External IP and Internal IP only, internal from the list
	 *  Also save the list of players from same IP into a map for fast get on other functions
	 *  
	 *  Returns: True if can log another box. False if he cant, then he will be kicked
	 */
	public boolean checkMultiBox(L2GameClient client, String userHost)
	{
		if (client == null || userHost == null)
			return false;
		
		if (client.getActiveChar().getAccessLevel().getLevel() > 0 || ExternalConfig.MAX_PLAYERS_FROM_ONE_PC <= 0)
			return true;
		
		final Collection<L2PcInstance> allPlayers = L2World.getInstance().getAllPlayers().values();
		boolean isVIP = (client.getActiveChar().isPremium());
		final String ip = client.getConnection().getInetAddress().getHostAddress();
		int maxClientsAllowed = (isVIP ? ExternalConfig.MAX_PLAYERS_FROM_ONE_PC_VIP : ExternalConfig.MAX_PLAYERS_FROM_ONE_PC);
		
		List<L2PcInstance> ipMap = new FastList<L2PcInstance>();		
		String playerIp;
		String playerLocalIp;
		
		for (L2PcInstance player : allPlayers)
		{
			if (player == null || player.getClient() == null || player.getClient().isDetached())
				continue;

			// Check External IP
			playerIp = player.getConnectionAddress();
			if (ip.equals(playerIp))
			{
				playerLocalIp = getLocalIp(player.getAccountName());
				if (playerLocalIp != null)
				{
					if (playerIp.equals(playerLocalIp))
						continue;
					
					// Check Internal IP
					if (playerLocalIp.equals(userHost))
					{
						// If one account is GM dont check multibox
						if (player.getAccessLevel().getLevel() > 0)
							return true;
						
						// Check again VIP status for that account, its not only for the account that is logging now
						isVIP |= (player.isPremium());
						maxClientsAllowed = (isVIP ? ExternalConfig.MAX_PLAYERS_FROM_ONE_PC_VIP : ExternalConfig.MAX_PLAYERS_FROM_ONE_PC);
						
						ipMap.add(player);
						
						if (ipMap.size() >= maxClientsAllowed)
						{
							if (Config.DEVELOPER)
								_log.warning(StringUtil.concat("Multibox Protection: " + ip + " was trying to use over " + maxClientsAllowed + " clients!"));
							return false;
						}
					}
				}
			}
		}
		
		// Save ips on character, to make fast comparisons
		client.getActiveChar().saveIpPack(new IpPack(ip, userHost));
		
		// Save multiBox info for this client.
		ipMap.add(client.getActiveChar());
		_multiBoxUsersMap.put(new IpPack(ip, userHost), ipMap);
		
		return true;
	}
	
	/**
	 *  Synerge - This will only check for Offline Stores on same Pc set max on config
	 *  
	 *  Returns: True if can set another Offline Store
	 */
	public boolean checkMultiBoxOfflineStore(L2GameClient client, String userHost)
	{
		if (client == null || userHost == null)
			return false;
		
		if (client.getActiveChar().getAccessLevel().getLevel() > 0 || ExternalConfig.MAX_OFFLINE_STORES <= 0)
			return true;
		
		final Collection<L2PcInstance> allPlayers = L2World.getInstance().getAllPlayers().values();
		boolean isVIP = (client.getActiveChar().isPremium());
		final String ip = client.getConnection().getInetAddress().getHostAddress();
		int maxStoresAllowed = (isVIP ? ExternalConfig.MAX_OFFLINE_STORES_VIP : ExternalConfig.MAX_OFFLINE_STORES);
		
		List<Integer> ipMap = new FastList<Integer>();		
		String playerIp;
		String playerLocalIp;
		
		for (L2PcInstance player : allPlayers)
		{
			if (player == null || player.getClient() == null || !player.getClient().isDetached())
				continue;

			playerIp = player.getConnectionAddress();
			if (ip.equals(playerIp))
			{
				playerLocalIp = getLocalIp(player.getAccountName());
				if (playerLocalIp != null)
				{
					if (playerIp.equals(playerLocalIp))
						continue;
					
					if (playerLocalIp.equals(userHost))
					{
						// If one account is GM dont check multibox
						if (player.getAccessLevel().getLevel() > 0)
							return true;
						
						// Check again VIP status for that account, its not only for the account that is logging now
						isVIP |= (player.isPremium());
						maxStoresAllowed = (isVIP ? ExternalConfig.MAX_OFFLINE_STORES_VIP : ExternalConfig.MAX_OFFLINE_STORES);
						
						ipMap.add(player.getObjectId());
						
						if (ipMap.size() >= maxStoresAllowed)
							return false;
					}
				}
			}
		}
		
		return true;
	}
	
	public synchronized void registerNewPcIp(String account, String pcIp)
	{		
		_pcIpMap.put(account, pcIp);
	}
	
	// This will get the internalIp from the map
	public String getLocalIp(String account)
	{		
		final String pcIp = _pcIpMap.get(account);		
		if (pcIp == null)
		{
			_log.warning(StringUtil.concat("Multibox Proteccion. Null PcIp for account " + account + "!"));
			return null;
		}
		return pcIp;
	}	
	
	// Get lists of multiBox from this client
	public List<L2PcInstance> getPlayersFromSameIP(L2PcInstance player)
	{		
		if (player == null)
			return null;
		
		final String pcIp = _pcIpMap.get(player.getAccountName());	
		if (pcIp == null)
			return null;
		
		for (IpPack ips : _multiBoxUsersMap.keySet())
		{
			if (ips != null 
					&& ips.externalIp.equalsIgnoreCase(player.getConnectionAddress()) 
					&& ips.internalIp.equalsIgnoreCase(pcIp))
				return _multiBoxUsersMap.get(ips);
		}
		
		return null;
	}	
	
	// Checks if the two characters are in the same pc. If ips are null, then consider as multibox
	public boolean checkIsPlayerFromSamePc(L2PcInstance player1, L2PcInstance player2)
	{		
		if (player1 == null || player2 == null)
			return true;
		
		final IpPack ipsPlayer1 = player1.getIpPack();
		if (ipsPlayer1 == null)
			return true;
		
		final IpPack ipsPlayer2 = player2.getIpPack();
		if (ipsPlayer2 == null)
			return true;
		
		if (ipsPlayer1.getExternalIP().equalsIgnoreCase(ipsPlayer2.getExternalIP()) 
				&& ipsPlayer1.getInternalIP().equalsIgnoreCase(ipsPlayer2.getInternalIP()) )
			return true;
				
		return false;
	}	
	
	// Just a simple class to save internal and external IP for a map index
	public class IpPack 
	{
		private String internalIp;
		private String externalIp;

		public IpPack(String exIp, String inIp) 
		{
			externalIp = exIp;
			internalIp = inIp;			
		}
		
		public String getInternalIP()
		{
			return internalIp;
		}
		
		public String getExternalIP()
		{
			return externalIp;
		}
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final MultiBoxProtection _instance = new MultiBoxProtection();
	}
}
