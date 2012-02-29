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
package l2.universe.gameserver.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javolution.util.FastMap;
import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.L2PremiumItem;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ExNotifyPremiumItem;
import l2.universe.gameserver.network.serverpackets.InventoryUpdate;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

/**
 * The objective of this is:
 * - Save massive calls to DB when a char loads, there's no need to check a possible empty table each time a player logs in
 * - Have a list of current not delivered items, so when the player is online, its get delivered automatically. So if a player
 * is online, and a item is added on the DB, when the thread executes, the item will be sent with a message automatically
 * Premium items list of PcInstance will be removed, and everything will be kept here for sync issues
 *
 * @author Synerge
 */
public class PremiumItemsTable
{
	public static final Logger _log = Logger.getLogger(PremiumItemsTable.class.getName());
	
	private boolean _isReloading = false;
	//private Future<?> _checkItemsTask = null;
	
	// CharId - Item Id - Premium Item
	private Map<Integer, Map<Integer, L2PremiumItem>> _premiumItems = new FastMap<Integer, Map<Integer, L2PremiumItem>>();
	
	private PremiumItemsTable()
	{
		loadTable();
		
		// Program a thread to check the DB for updates or changes every 5 minutes
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new CheckItemsTask(), 300000, 300000);
	}
	
	// Load all Premium Items of every character
	public void loadTable()
	{
		_isReloading = true;
		
		_premiumItems.clear();
		_premiumItems = new FastMap<Integer, Map<Integer, L2PremiumItem>>();
		
		Connection con = null;
		
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT Id, charId, itemId, itemCount, vitamine, itemSender FROM character_premium_items WHERE charId > 0");
			ResultSet rset = statement.executeQuery();
			
			int Id;
			int charId;
			int itemId;
			long itemCount;
			boolean vitamine;
			String itemSender;
			
			while (rset.next())
			{
				Id = rset.getInt("Id");
				charId = rset.getInt("charId");
				itemId = rset.getInt("itemId");
				itemCount = rset.getLong("itemCount");
				vitamine = rset.getInt("vitamine") != 0;
				itemSender = rset.getString("itemSender");
				final L2PremiumItem item = new L2PremiumItem(itemId, itemCount, itemSender, vitamine, true);
				
				if (_premiumItems.get(charId) == null)
					_premiumItems.put(charId, new FastMap<Integer, L2PremiumItem>());
				_premiumItems.get(charId).put(Id, item);
			}
			
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Could not load premium items:" + e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
		_isReloading = false;
		
		// Check items and send messages to online players
		notifyOnlinePlayers();
	}
	
	/* This function will check all items in DB after a reload, and send a message to a player if is online that he has
	 * a new item waiting for him in the Dimensional Merchant
	 */
	private void notifyOnlinePlayers()
	{
		if (_premiumItems.isEmpty())
			return;
		
		for (Entry<Integer, Map<Integer, L2PremiumItem>> itemList : _premiumItems.entrySet())
		{
			if (itemList == null || itemList.getValue() == null || itemList.getValue().isEmpty())
				continue;
			
			// Get player of the item
			final L2PcInstance player = L2World.getInstance().getPlayer(itemList.getKey());
			if (player == null || player.getClient() == null || player.getClient().isDetached())
				continue;
			
			// If its online, then notify that he has premium items waiting
			player.sendPacket(new ExNotifyPremiumItem());	
		}
	}
	
	public void updatePremiumItem(int itemId, int charId, long newcount)
	{
		Connection con = null;
		
		try
		{
			if (_premiumItems.get(charId) == null)
				_premiumItems.put(charId, new FastMap<Integer, L2PremiumItem>());
			_premiumItems.get(charId).get(itemId).updateCount(newcount);
			
			con = L2DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement statement = con.prepareStatement("UPDATE character_premium_items SET itemCount=? WHERE charId=? AND Id=? ");
			statement.setLong(1, newcount);
			statement.setInt(2, charId);
			statement.setInt(3, itemId);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Could not update premium item:" + e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	public void deletePremiumItem(int itemId, int charId)
	{
		Connection con = null;
		
		try
		{
			if (_premiumItems.get(charId) != null)
				_premiumItems.get(charId).remove(itemId);
			
			con = L2DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement statement = con.prepareStatement("DELETE FROM character_premium_items WHERE charId=? AND Id=? ");
			statement.setInt(1, charId);
			statement.setInt(2, itemId);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Could not delete premium item:" + e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	public void storePremiumItemList(int charId, String cName)
	{
		final Map<Integer, L2PremiumItem> pItems = _premiumItems.get(charId);
    	if (pItems == null || pItems.isEmpty())
    		return;
    	
    	for (L2PremiumItem pitem : pItems.values())
    	{
    		if (pitem == null || pitem.getIsSaved())
    			continue;

			Connection con = null;

			try 
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement("INSERT INTO `character_premium_items` (`charId`, `charName`, `itemId`, `itemCount`, `itemSender`, `vitamine`) VALUES (?, ?, ?, ?, ?, ?);");
				statement.setInt(1, charId);
				statement.setString(2, cName);
				statement.setInt(3, pitem.getItemId());
				statement.setLong(4, pitem.getCount());
				statement.setString(5, "Admin");
				statement.setInt(6, 1);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				_log.warning("Could not save premium items:" + e);
			}
			finally
			{
				L2DatabaseFactory.close(con);
			}
			
			pitem.setIsSaved(true);
    	}
	}
	
	/**
	 * If item is not vitamine, it will be added automaticly to inventory 
	 * on character login, else it will be added to premium list and
	 * informed on login to get items from Premium items Manager
	 */
	public void notifyPremiumItem(L2PcInstance activeChar)
	{
		final Map<Integer, L2PremiumItem> pItems = _premiumItems.get(activeChar.getObjectId());
    	if (pItems == null || pItems.isEmpty())
    		return;
    	
    	for (Integer num : pItems.keySet())
    	{
    		final L2PremiumItem pitem = pItems.get(num);
    		if (pitem.getIsVitamine())
    			continue;

			final L2ItemInstance item = activeChar.getInventory().addItem("Status-Give", pitem.getItemId(), pitem.getCount(), null, null);
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addItem(item);
			activeChar.sendPacket(iu);
			
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_PICKED_UP_S1_S2);
			sm.addItemName(pitem.getItemId());
			sm.addItemNumber(pitem.getCount());
			activeChar.sendPacket(sm);
			sm = null;

			deletePremiumItem(num, activeChar.getObjectId());
    	}

    	if (!pItems.isEmpty())
    		activeChar.sendPacket(new ExNotifyPremiumItem());
	}
	
	// Returns Premium Item list of a player
	public Map<Integer, L2PremiumItem> getPremiumItemListOf(int charId)
	{
		if (_premiumItems.get(charId) == null)
			_premiumItems.put(charId, new FastMap<Integer, L2PremiumItem>());
		return _premiumItems.get(charId);
	}
		
	public boolean isReloading()
	{
		return _isReloading;
	}
	
	// Just a thread to execute reload of premium items
	private final class CheckItemsTask implements Runnable
	{
		public void run()
		{
			if (_isReloading)
				return;
			
			loadTable();
		}
	}
	
	public static PremiumItemsTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final PremiumItemsTable _instance = new PremiumItemsTable();
	}
}
