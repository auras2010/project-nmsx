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
package l2.universe.gameserver.model.entity;

import static l2.universe.gameserver.model.itemcontainer.PcInventory.MAX_ADENA;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.datatables.ClanTable;
import l2.universe.gameserver.idfactory.IdFactory;
import l2.universe.gameserver.instancemanager.AuctionManager;
import l2.universe.gameserver.instancemanager.ClanHallManager;
import l2.universe.gameserver.model.L2Clan;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;

/**
 * 
 * @author Synerge
 */
public class Auction
{
	protected static final Logger _log = Logger.getLogger(Auction.class.getName());

	private static final int ADENA_ID = 57;
	private int _id;
	private long _endDate;
	private int _itemId;
	private String _itemName = "";
	private int _itemObjectId;
	private long _itemQuantity;
	private String _itemType = "";
	private L2Clan _sellerClan;
	private long _startingBid;
	
	private final List<Bidder> _bidders = new CopyOnWriteArrayList<Bidder>();
	private ScheduledFuture<AutoEndTask> _autoEndTask;
	
	/**
	 * Creates new instance of Auction. Also loads auction from database and starts task for
	 * finishing auction at _endDate}.
	 * 
	 * @param auctionId _id
	 */
	public Auction(int auctionId)
	{
		_id = auctionId;
		loadAuction();
		startAutoTask();
	}
	
	/**
	 * Creates new instance of Auction. _endDate} is set tu current time plus delay.
	 * 
	 * @param itemId _id
	 * @param clan _sellerClan
	 * @param delay time in milliseconds till the auction end
	 * @param bid _startingBid
	 * @param name _itemName}
	 */
	public Auction(int itemId, L2Clan clan, long delay, long bid, String name)
	{
		_id = itemId;
		_endDate = System.currentTimeMillis() + delay;
		_itemId = itemId;
		_itemName = name;
		_itemType = "ClanHall";
		_sellerClan = clan;
		_startingBid = bid;
	}
	
	/**
	 * Loads auction from database using _id}. Also loads bidders from database.
	 * 
	 * @see #loadBidders()
	 */
	private void loadAuction()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement statement = con.prepareStatement("SELECT * FROM auction WHERE id = ?");
			statement.setInt(1, getId());
			ResultSet rs = statement.executeQuery();
			
			if (rs.next())
			{
				final int sellerClanId = rs.getInt("sellerId");
				final String sellerClanName = rs.getString("sellerClanName");
				_endDate = rs.getLong("endDate");
				_itemId = rs.getInt("itemId");
				_itemName = rs.getString("itemName");
				_itemObjectId = rs.getInt("itemObjectId");
				_itemType = rs.getString("itemType");
				_sellerClan = ClanTable.getInstance().getClan(sellerClanId);
				_startingBid = rs.getLong("startingBid");
				
				// this is fix for auctions that are created with sellerid = 0
				// though the CH is being sold by clan - in such case
				// _sellerClan will be null before this check as sellerId is
				// zero, so we load the clan using the stored clan name
				if (_sellerClan == null && sellerClanName != null && !sellerClanName.isEmpty())
				{
					_sellerClan = ClanTable.getInstance().getClanByName(sellerClanName);
				}
			}
			rs.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Auction: Failed to load auction", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
		loadBidders();
	}
	
	/**
	 * Load bidders for this auction.
	 */
	private void loadBidders()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT bidderId, bidderName, maxBid, clan_name, time_bid " + "FROM auction_bid WHERE auctionId = ? ORDER BY maxBid DESC");
			statement.setInt(1, getId());
			ResultSet rs = statement.executeQuery();
			
			while (rs.next())
			{
				final Bidder bidder = new Bidder(rs.getString("bidderName"), ClanTable.getInstance().getClanByName(rs.getString("clan_name")), rs.getLong("maxBid"), rs.getLong("time_bid"));				
				_bidders.add(bidder);
			}
			rs.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Auction: Failed to load bidders", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Starts AutoEndTask that is run when auction ends. If _endDate is in past
	 * then task is scheduled to be run after week.
	 */
	@SuppressWarnings("unchecked")
	private void startAutoTask()
	{
		final long currentTime = System.currentTimeMillis();
		long taskDelay = 0;
		
		if (_endDate <= currentTime)
		{
			_endDate = currentTime + 7 * 24 * 60 * 60 * 1000;
			saveAuctionEndDate();
		}
		else
		{
			taskDelay = _endDate - currentTime;
		}
		
		_autoEndTask = (ScheduledFuture<AutoEndTask>) ThreadPoolManager.getInstance().scheduleGeneral(new AutoEndTask(), taskDelay);
	}
	
	/**
	 * Saves auction end date.
	 */
	private void saveAuctionEndDate()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE auction SET endDate = ? WHERE id = ?");
			statement.setLong(1, _endDate);
			statement.setInt(2, _id);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Exception: saveAuctionDate()", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Sets a bid. The bid is either new bid or bid increase.
	 * 
	 * @param player player placing the bid
	 * @param bid bid amount
	 */
	public void setBid(L2PcInstance player, long bid)
	{
		long requiredAdena = bid;
		Bidder bidder = getBidder(player.getClan());
		
		// Increasing bid so need only the increased amount of adena
		if (bidder != null)
			requiredAdena = bid - bidder.getBid();
		
		// There are two conditions:
		// - either there is no bidder yet and then the bid must be at least the
		// same as starting bid
		// - there is at least one bidder and then the bud must be greater then
		// the bid of the highest bidder
		// In all cases required adena must be available.
		if (_bidders.isEmpty() && bid < getStartingBid() || !_bidders.isEmpty() && bid < _bidders.get(0).getBid() || !takeItem(player, requiredAdena))
		{
			player.sendPacket(SystemMessageId.BID_PRICE_MUST_BE_HIGHER);			
			return;
		}
		
		final Bidder prevHighestBidder = _bidders.isEmpty() ? null : _bidders.get(0);
		
		if (bidder == null)
		{
			// new bid
			bidder = new Bidder(player.getName(), player.getClan(), bid, System.currentTimeMillis());
			_bidders.add(0, bidder);
			updateInDB(player, bid, true);
			player.getClan().setAuctionBiddedAt(_id, true);
		}
		else
		{
			// update to existing bid
			bidder.setBid(bid);
			bidder.setName(player.getName());
			bidder.setTimeBid(System.currentTimeMillis());
			
			if (_bidders.get(0) != bidder)
			{
				_bidders.remove(bidder);
				_bidders.add(0, bidder);
			}
			
			updateInDB(player, bid, false);
		}
		
		player.sendPacket(SystemMessageId.BID_IN_CLANHALL_AUCTION);
		
		if (prevHighestBidder != bidder && _bidders.size() > 1)
		{
			final L2PcInstance outbiddedPlayer = L2World.getInstance().getPlayer(_bidders.get(1).getName());			
			if (outbiddedPlayer != null)
				outbiddedPlayer.sendMessage("You have been out bidded");
		}
	}
	
	/**
	 * Returns item in cwh.
	 * 
	 * @param clan clan in which cwh the item should be returned
	 * @param quantity quantity that should be returned
	 * @param penalty whether 10% penalty should be applied
	 */
	private void returnItem(L2Clan clan, long quantity, boolean penalty)
	{	
		if (penalty)
			quantity *= 0.9; // take 10% tax fee if needed
		
		final long limit = MAX_ADENA - clan.getWarehouse().getAdena();
		quantity = Math.min(quantity, limit);
		
		clan.getWarehouse().addItem("Outbidded", ADENA_ID, quantity, null, null);
	}
	
	/**
	 * Takes item from cwh. Bidder must have clan and quantity of adena available.
	 * 
	 * @param bidder bidder character
	 * @param quantity quantity that should be taken.
	 * 
	 * @return true if adena was taken successfully, otherwise false
	 */
	private boolean takeItem(L2PcInstance bidder, long quantity)
	{
		if (bidder.getClan() != null && bidder.getClan().getWarehouse().getAdena() >= quantity)
		{
			bidder.getClan().getWarehouse().destroyItemByItemId("Buy", ADENA_ID, quantity, bidder, bidder);
			return true;
		}
		
		bidder.sendPacket(SystemMessageId.NOT_ENOUGH_ADENA_IN_CWH);		
		return false;
	}
	
	/**
	 * Updates auction bid in database.
	 * 
	 * @param bidder bidder
	 * @param bid bid amount
	 * @param newBidder whether this is new bidder
	 */
	private void updateInDB(L2PcInstance bidder, long bid, boolean newBidder)
	{
		Connection con = null;
		PreparedStatement statement = null;
		
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			if (newBidder)
			{
				statement = con.prepareStatement("INSERT INTO auction_bid (id, auctionId, bidderId, " + "bidderName, maxBid, clan_name, time_bid) " + "VALUES (?, ?, ?, ?, ?, ?, ?)");
				statement.setInt(1, IdFactory.getInstance().getNextId());
				statement.setInt(2, getId());
				statement.setInt(3, bidder.getClanId());
				statement.setString(4, bidder.getName());
				statement.setLong(5, bid);
				statement.setString(6, bidder.getClan().getName());
				statement.setLong(7, System.currentTimeMillis());
			}
			else
			{
				statement = con.prepareStatement("UPDATE auction_bid SET bidderId=?, bidderName=?, " + "maxBid=?, time_bid=? WHERE auctionId=? AND bidderId=?");
				statement.setInt(1, bidder.getClanId());
				statement.setString(2, bidder.getClan().getLeaderName());
				statement.setLong(3, bid);
				statement.setLong(4, System.currentTimeMillis());
				statement.setInt(5, getId());
				statement.setInt(6, bidder.getClanId());
			}
			
			statement.execute();
			statement.close();
		}
		catch (final Exception e)
		{
			_log.log(Level.SEVERE, "Auction: Failed to save bid", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Removes all bids from this auction from database and clears list of bidders. If auction was
	 * canceled then all bidders get their money back, otherwise all except the top bidder get their
	 * money back.
	 * 
	 * @param canceled whether the auction was canceled
	 */
	private void removeBids(boolean canceled)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM auction_bid WHERE auctionId=?");
			statement.setInt(1, getId());
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Auction: Failed to delete bids", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
		if (!_bidders.isEmpty())
		{
			final Bidder highestBidder = _bidders.get(0);
			
			for (final Bidder bidder : _bidders)
			{
				if (canceled || bidder != highestBidder)
					returnItem(bidder.getClan(), bidder.getBid(), false);
				
				bidder.getClan().setAuctionBiddedAt(0, true);
			}
			
			_bidders.clear();
			
			if (!canceled)
			{
				final L2PcInstance player = L2World.getInstance().getPlayer(highestBidder.getName());				
				if (player != null)
					player.sendMessage("Congratulation you have won ClanHall!");
			}
		}
	}
	
	/**
	 * Removes auction from database.
	 */
	public void deleteAuctionFromDB()
	{
		AuctionManager.getInstance().getAuctions().remove(this);
		Connection con = null;
		
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM auction WHERE itemId=?");
			statement.setInt(1, _itemId);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Auction: Failed to delete auction", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Ends the auction.
	 */
	public void endAuction()
	{
		if (ClanHallManager.getInstance().loaded())
		{
			if (_bidders.isEmpty())
			{
				if (_sellerClan == null)
					startAutoTask();
				else
				{
					final int aucId = AuctionManager.getInstance().getAuctionIndex(_id);
					AuctionManager.getInstance().getAuctions().remove(aucId);
				}
				return;
			}
			
			final Bidder highestBidder = _bidders.get(0);
			
			if (_sellerClan != null)
			{
				returnItem(_sellerClan, highestBidder.getBid(), true);
				_sellerClan.setHasHideout(0);
			}
			
			deleteAuctionFromDB();
			highestBidder.getClan().setAuctionBiddedAt(0, true);
			removeBids(false);
			ClanHallManager.getInstance().setOwner(_itemId, highestBidder.getClan());
		}
		else
		{
			// Task to end auction in case auction environment is not totally loaded yet
			ThreadPoolManager.getInstance().scheduleGeneral(new AutoEndTask(), 3000);
		}
	}
	
	/**
	 * Cancels bid.
	 * 
	 * @param player player cancelling the bid
	 */
	public void cancelBid(L2PcInstance player)
	{
		final Bidder bidder = getBidder(player.getClan());
		if (bidder == null)
			return;
		
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM auction_bid WHERE auctionId=? AND bidderId=?");
			statement.setInt(1, getId());
			statement.setInt(2, player.getClanId());
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Auction: Failed to cancel bid", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
		returnItem(bidder.getClan(), bidder.getBid(), true);
		bidder.getClan().setAuctionBiddedAt(0, true);
		_bidders.remove(bidder);
	}
	
	/**
	 * Cancels auction. Removes auction from database and removes bids.
	 * 
	 * @see #removeBids(boolean)
	 */
	public void cancelAuction()
	{
		if (_autoEndTask != null)
		{
			_autoEndTask.cancel(true);
			_autoEndTask = null;
		}
		
		deleteAuctionFromDB();
		removeBids(true);
	}
	
	/**
	 * Creates new auction in database.
	 */
	public void createAuction()
	{
		AuctionManager.getInstance().getAuctions().add(this);
		Connection con = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement statement = con.prepareStatement("INSERT INTO auction (id, sellerId, sellerName, " + "sellerClanName, itemType, itemId, itemObjectId, " + "itemName, itemQuantity, startingBid, currentBid, endDate) " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
			statement.setInt(1, getId());
			statement.setInt(2, _sellerClan == null ? 0 : _sellerClan.getClanId());
			statement.setString(3, _sellerClan.getLeaderName());
			statement.setString(4, _sellerClan.getName());
			statement.setString(5, _itemType);
			statement.setInt(6, _itemId);
			statement.setInt(7, _itemObjectId);
			statement.setString(8, _itemName);
			statement.setLong(9, _itemQuantity);
			statement.setLong(10, _startingBid);
			statement.setLong(11, getCurrentBid());
			statement.setLong(12, _endDate);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Auction: Failed to create auction", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Getter for _id.
	 * 
	 * @return _id
	 */
	public final int getId()
	{
		return _id;
	}
	
	/**
	 * If no bid has been done yet then return _startingBid}, else return maximum bid.
	 * 
	 * @return current bid
	 */
	public final long getCurrentBid()
	{
		if (_bidders.isEmpty())
			return _startingBid;
		else
			return _bidders.get(0).getBid();
	}
	
	/**
	 * Getter for _endDate.
	 * 
	 * @return _endDate
	 */
	public final long getEndDate()
	{
		return _endDate;
	}
	
	/**
	 * Getter for _itemId.
	 * 
	 * @return _itemId
	 */
	public final int getItemId()
	{
		return _itemId;
	}
	
	/**
	 * Getter for _itemName.
	 * 
	 * @return _itemName
	 */
	public final String getItemName()
	{
		return _itemName;
	}
	
	/**
	 * Getter for _itemObjectId.
	 * 
	 * @return _itemObjectId
	 */
	public final int getItemObjectId()
	{
		return _itemObjectId;
	}
	
	/**
	 * Getter for _itemQuantity.
	 * 
	 * @return _itemQuantity
	 */
	public final long getItemQuantity()
	{
		return _itemQuantity;
	}
	
	/**
	 * Getter for _itemType.
	 * 
	 * @return _itemType
	 */
	public final String getItemType()
	{
		return _itemType;
	}
	
	/**
	 * Getter for id of _sellerClan.
	 * 
	 * @return id of _sellerClan or zero if _sellerClan is null
	 */
	public final int getSellerId()
	{
		return _sellerClan == null ? 0 : _sellerClan.getClanId();
	}
	
	/**
	 * Getter for _sellerClan.
	 * 
	 * @return _sellerClan
	 */
	public final L2Clan getSellerClan()
	{
		return _sellerClan;
	}
	
	/**
	 * Getter for _startingBid.
	 * 
	 * @return _startingBid
	 */
	public final long getStartingBid()
	{
		return _startingBid;
	}
	
	/**
	 * Getter for _bidders.
	 * 
	 * @return _bidders
	 */
	public final List<Bidder> getBidders()
	{
		return _bidders;
	}
	
	/**
	 * Returns bidder if that clan has bidded, or null of that clan did not do bid.
	 * 
	 * @param clan clan
	 * 
	 * @return bidder or null
	 */
	public Bidder getBidder(L2Clan clan)
	{
		Bidder bidder = null;
		
		for (final Bidder curBidder : _bidders)
		{
			if (curBidder.getClan().getClanId() == clan.getClanId())
			{
				bidder = curBidder;
				break;
			}
		}
		
		return bidder;
	}
	
	/**
	 * Returns highest bid or zero if no bid has been done yet.
	 * 
	 * @return highest bid or zero
	 */
	public long getHighestBid()
	{
		if (_bidders.isEmpty())
			return 0;
		else
			return _bidders.get(0).getBid();
	}
	
	/**
	 * Information about bidder.
	 */
	public class Bidder
	{
		/**
		 * Name of bidder character.
		 */
		private String _name;
		/**
		 * Bidding clan.
		 */
		private final L2Clan _clan;
		/**
		 * Bid.
		 */
		private long _bid;
		/**
		 * Time the last bid of the bidder was accepted.
		 */
		private Calendar _timeBid;
		
		/**
		 * Creates new instance of Bidder.
		 * 
		 * @param name _name
		 * @param clan _clan
		 * @param bid _bid
		 * @param timeBid _timeBid
		 */
		public Bidder(String name, L2Clan clan, long bid, long timeBid)
		{
			_name = name;
			_clan = clan;
			_bid = bid;
			_timeBid = Calendar.getInstance();
			_timeBid.setTimeInMillis(timeBid);
		}
		
		/**
		 * Getter for _name.
		 * 
		 * @return _name
		 */
		public String getName()
		{
			return _name;
		}
		
		/**
		 * Setter for _name.
		 * 
		 * @param name _name
		 */
		public void setName(final String name)
		{
			_name = name;
		}
		
		/**
		 * Getter for _clan.
		 * 
		 * @return _clan
		 */
		public L2Clan getClan()
		{
			return _clan;
		}
		
		/**
		 * Getter for _bid.
		 * 
		 * @return _bid
		 */
		public long getBid()
		{
			return _bid;
		}
		
		/**
		 * Setter for _bid.
		 * 
		 * @param bid _bid
		 */
		public void setBid(long bid)
		{
			_bid = bid;
		}
		
		/**
		 * Getter for _timeBid.
		 * 
		 * @return _timeBid
		 */
		public Calendar getTimeBid()
		{
			return _timeBid;
		}
		
		/**
		 * Setter for _timeBid.
		 * 
		 * @param timeBid _timeBid
		 */
		public void setTimeBid(long timeBid)
		{
			_timeBid.setTimeInMillis(timeBid);
		}
	}
	
	/**
	 * Task for ending auction.
	 * 
	 * @see Auction#endAuction()
	 */
	public class AutoEndTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				endAuction();
			}
			catch (final Exception e)
			{
				_log.log(Level.SEVERE, "AuctionEndTask: problem occured while ending auction", e);
			}
		}
	}
}
