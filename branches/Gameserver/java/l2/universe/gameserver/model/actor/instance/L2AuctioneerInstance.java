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
package l2.universe.gameserver.model.actor.instance;

import static l2.universe.gameserver.model.itemcontainer.PcInventory.MAX_ADENA;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javolution.util.FastMap;

import l2.universe.Config;
import l2.universe.gameserver.datatables.MapRegionTable;
import l2.universe.gameserver.instancemanager.AuctionManager;
import l2.universe.gameserver.instancemanager.ClanHallManager;
import l2.universe.gameserver.model.L2Clan;
import l2.universe.gameserver.model.entity.Auction;
import l2.universe.gameserver.model.entity.Auction.Bidder;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.templates.chars.L2NpcTemplate;

public final class L2AuctioneerInstance extends L2NpcInstance
{
	private static final int COND_ALL_FALSE = 0;
	private static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
	private static final int COND_REGULAR = 3;

	private Map<Integer, Auction> _pendingAuctions = new FastMap<Integer, Auction>();

	public L2AuctioneerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		setInstanceType(InstanceType.L2AuctioneerInstance);
	}

	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		// BypassValidation Exploit plug.
		if (player.getLastFolkNPC().getObjectId() != getObjectId())
			return;
	
		int condition = validateCondition(player);
		if (condition <= COND_ALL_FALSE)
		{
			//TODO: html
			player.sendMessage("Wrong conditions.");
			return;
		}
		else if (condition == COND_BUSY_BECAUSE_OF_SIEGE)
		{
			String filename = "data/html/auction/auction-busy.htm";
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getHtmlPrefix(), filename);
			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
			return;
		}
		else if (condition == COND_REGULAR)
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			String actualCommand = st.nextToken(); // Get actual command

			String val = "";
			if (st.countTokens() >= 1)
				val = st.nextToken();

			if (actualCommand.equalsIgnoreCase("auction"))
			{
				if (val.isEmpty())
					return;

				try
				{
					int days = Integer.parseInt(val);
					try
					{
						SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
						long bid = 0;
						if (st.countTokens() >= 1)
							bid = Math.min(Long.parseLong(st.nextToken()), MAX_ADENA);

						Auction a = new Auction(player.getClan().getHasHideout(), player.getClan(), days*86400000L, bid, ClanHallManager.getInstance().getClanHallByOwner(player.getClan()).getName());
						if (_pendingAuctions.get(a.getId()) != null)
							_pendingAuctions.remove(a.getId());

						_pendingAuctions.put(a.getId(), a);

						String filename = "data/html/auction/AgitSale3.htm";
						NpcHtmlMessage html = new NpcHtmlMessage(1);
						html.setFile(player.getHtmlPrefix(), filename);
						html.replace("%x%", val);
						html.replace("%AGIT_AUCTION_END%", String.valueOf(format.format(a.getEndDate())));
						html.replace("%AGIT_AUCTION_MINBID%", String.valueOf(a.getStartingBid()));
						html.replace("%AGIT_AUCTION_MIN%", String.valueOf(a.getStartingBid()));
						html.replace("%AGIT_AUCTION_DESC%", ClanHallManager.getInstance().getClanHallByOwner(player.getClan()).getDesc());
						html.replace("%AGIT_LINK_BACK%", "bypass -h npc_"+getObjectId()+"_sale2");
						html.replace("%objectId%", String.valueOf((getObjectId())));
						player.sendPacket(html);
					}
					catch (Exception e)
					{
						player.sendMessage("Invalid bid!");
					}
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction duration!");
				}
				return;
			}
			else if (actualCommand.equalsIgnoreCase("confirmAuction"))
			{
				try
				{
					Auction a = _pendingAuctions.get(player.getClan().getHasHideout());
					a.createAuction();
					_pendingAuctions.remove(player.getClan().getHasHideout());
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction");
				}
				return;
			}
			else if (actualCommand.equalsIgnoreCase("bidding"))
			{
				if (val.isEmpty())
					return;

				if (Config.DEBUG)
					_log.warning("bidding show successful");

				try
				{
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					int auctionId = Integer.parseInt(val);

					if (Config.DEBUG)
						_log.warning("auction test started");

					String filename = "data/html/auction/AgitAuctionInfo.htm";
					Auction a = AuctionManager.getInstance().getAuction(auctionId);

					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile(player.getHtmlPrefix(), filename);
					if (a != null)
					{
						html.replace("%AGIT_NAME%", a.getItemName());
						html.replace("%OWNER_PLEDGE_NAME%", a.getSellerId() != 0 ? a.getSellerClan().getName() : "");
						html.replace("%OWNER_PLEDGE_MASTER%", a.getSellerId() != 0 ? a.getSellerClan().getLeaderName() : "NPC");
						html.replace("%AGIT_SIZE%", String.valueOf(ClanHallManager.getInstance().getClanHallById(a.getItemId()).getGrade()*10));
						html.replace("%AGIT_LEASE%", String.valueOf(ClanHallManager.getInstance().getClanHallById(a.getItemId()).getLease()));
						html.replace("%AGIT_LOCATION%", ClanHallManager.getInstance().getClanHallById(a.getItemId()).getLocation());
						html.replace("%AGIT_AUCTION_END%", String.valueOf(format.format(a.getEndDate())));
						html.replace("%AGIT_AUCTION_REMAIN%", String.valueOf((a.getEndDate()- System.currentTimeMillis())/3600000)+" hours "+String.valueOf((((a.getEndDate() - System.currentTimeMillis()) / 60000) % 60))+" minutes");
						html.replace("%AGIT_AUCTION_MINBID%", String.valueOf(a.getStartingBid()));
						html.replace("%AGIT_AUCTION_COUNT%", String.valueOf(a.getBidders().size()));
						html.replace("%AGIT_AUCTION_DESC%", ClanHallManager.getInstance().getClanHallById(a.getItemId()).getDesc());
						html.replace("%AGIT_LINK_BACK%", "bypass -h npc_"+getObjectId()+"_list");
						html.replace("%AGIT_LINK_BIDLIST%", "bypass -h npc_"+getObjectId()+"_bidlist "+a.getId());
						html.replace("%AGIT_LINK_RE%", "bypass -h npc_"+getObjectId()+"_bid1 "+a.getId());
					}
					else
						_log.warning("Auctioneer Auction null for AuctionId : "+auctionId);

					player.sendPacket(html);
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction!");
				}
				return;
			}
			else if (actualCommand.equalsIgnoreCase("bid"))
			{
				if (val.isEmpty())
					return;

				try
				{
					int auctionId = Integer.parseInt(val);
					try
					{
						long bid = 0;
						if (st.countTokens() >= 1)
							bid = Math.min(Long.parseLong(st.nextToken()), MAX_ADENA);

						AuctionManager.getInstance().getAuction(auctionId).setBid(player, bid);
					}
					catch (Exception e)
					{
						player.sendMessage("Invalid bid!");
					}
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction!");
				}
				return;
			}
			else if (actualCommand.equalsIgnoreCase("bid1"))
			{
				if (player.getClan() == null || player.getClan().getLevel() < 2)
				{
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AUCTION_ONLY_CLAN_LEVEL_2_HIGHER));
					return;
				}

				if (val.isEmpty())
					return;

				if ((player.getClan().getAuctionBiddedAt() > 0 && player.getClan().getAuctionBiddedAt() != Integer.parseInt(val)) || player.getClan().getHasHideout() > 0)
				{
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ALREADY_SUBMITTED_BID));
					return;
				}

				try
				{
					String filename = "data/html/auction/AgitBid1.htm";

					long minimumBid = AuctionManager.getInstance().getAuction(Integer.parseInt(val)).getHighestBid();
					if (minimumBid == 0)
						minimumBid = AuctionManager.getInstance().getAuction(Integer.parseInt(val)).getStartingBid();

					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile(player.getHtmlPrefix(), filename);
					html.replace("%AGIT_LINK_BACK%", "bypass -h npc_"+getObjectId()+"_bidding "+val);
					html.replace("%PLEDGE_ADENA%", String.valueOf(player.getClan().getWarehouse().getAdena()));
					html.replace("%AGIT_AUCTION_MINBID%", String.valueOf(minimumBid));
					html.replace("npc_%objectId%_bid", "npc_"+getObjectId()+"_bid "+val);
					player.sendPacket(html);
					return;
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction!");
				}
				return;
			}
			else if (actualCommand.equalsIgnoreCase("list"))
			{
				List<Auction> auctions = AuctionManager.getInstance().getAuctions();
				SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd");
				/** Limit for make new page, prevent client crash **/
				int limit = 15;
				int start;
				int i = 1;
				double npage = Math.ceil((float)auctions.size()/limit);

				if (val.isEmpty())
				{
					start = 1;
				}
				else
				{
					start = limit*(Integer.parseInt(val)-1)+1;
					limit *= Integer.parseInt(val);
				}

				if (Config.DEBUG)
					_log.warning("cmd list: auction test started");

				String items = "";
				items += "<table width=280 border=0><tr>";
				for (int j = 1; j <= npage; j++)
					items+= "<td><center><a action=\"bypass -h npc_"+getObjectId()+"_list "+j+"\"> Page "+j+" </a></center></td>";

				items += "</tr></table>" + "<table width=280 border=0>";

				for (Auction a : auctions)
				{
					if (a == null)
						continue;

					if (i > limit)
						break;
					else if (i < start)
					{
						i++;
						continue;
					}
					else
						i++;

					items += "<tr>" +
						"<td>"+ClanHallManager.getInstance().getClanHallById(a.getItemId()).getLocation()+"</td>" +
						"<td><a action=\"bypass -h npc_"+getObjectId()+"_bidding "+a.getId()+"\">"+a.getItemName()+"</a></td>" +
						"<td>"+format.format(a.getEndDate())+"</td>" +
						"<td>"+a.getStartingBid()+"</td>" +
						"</tr>";
				}

				items += "</table>";
				String filename = "data/html/auction/AgitAuctionList.htm";

				NpcHtmlMessage html = new NpcHtmlMessage(1);
				html.setFile(player.getHtmlPrefix(), filename);
				html.replace("%AGIT_LINK_BACK%", "bypass -h npc_"+getObjectId()+"_start");
				html.replace("%itemsField%", items);
				player.sendPacket(html);
				return;
			}
			else if (actualCommand.equalsIgnoreCase("bidlist"))
			{
				int auctionId = 0;
				if (val.isEmpty())
				{
					if (player.getClan().getAuctionBiddedAt() <= 0)
						return;
					else
						auctionId = player.getClan().getAuctionBiddedAt();
				}
				else
					auctionId = Integer.parseInt(val);

				if (Config.DEBUG)
					_log.warning("cmd bidlist: auction test started");

				String biders = "";
				List<Bidder> bidders = AuctionManager.getInstance().getAuction(auctionId).getBidders();
				for (Bidder b : bidders)
				{
					biders += "<tr>" + "<td>" + b.getClan().getName() + "</td><td>" + b.getName()
					        + "</td><td>" + b.getTimeBid().get(Calendar.YEAR) + "/"
					        + (b.getTimeBid().get(Calendar.MONTH) + 1) + "/"
					        + b.getTimeBid().get(Calendar.DATE) + "</td><td>" + b.getBid()
					        + "</td>" + "</tr>";
				}
				String filename = "data/html/auction/AgitBidderList.htm";

				NpcHtmlMessage html = new NpcHtmlMessage(1);
				html.setFile(player.getHtmlPrefix(), filename);
				html.replace("%AGIT_LIST%", biders);
				html.replace("%AGIT_LINK_BACK%", "bypass -h npc_"+getObjectId()+"_selectedItems");
				html.replace("%x%", val);
				html.replace("%objectId%", String.valueOf(getObjectId()));
				player.sendPacket(html);
				return;
			}
			else if (actualCommand.equalsIgnoreCase("selectedItems"))
			{
				if (player.getClan() != null && player.getClan().getHasHideout() == 0
				        && player.getClan().getAuctionBiddedAt() > 0)
				{
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					String filename = "data/html/auction/AgitBidInfo.htm";
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile(player.getHtmlPrefix(), filename);
					Auction a = AuctionManager.getInstance().getAuction(player.getClan().getAuctionBiddedAt());
					if (a != null)
					{
						html.replace("%AGIT_NAME%", a.getItemName());
						html.replace("%OWNER_PLEDGE_NAME%", a.getSellerId() != 0 ? a.getSellerClan().getName() : "");
						html.replace("%OWNER_PLEDGE_MASTER%", a.getSellerId() != 0 ? a.getSellerClan().getLeaderName() : "NPC");
						html.replace("%AGIT_SIZE%", String.valueOf(ClanHallManager.getInstance().getClanHallById(a.getItemId()).getGrade()*10));
						html.replace("%AGIT_LEASE%", String.valueOf(ClanHallManager.getInstance().getClanHallById(a.getItemId()).getLease()));
						html.replace("%AGIT_LOCATION%", ClanHallManager.getInstance().getClanHallById(a.getItemId()).getLocation());
						html.replace("%AGIT_AUCTION_END%", String.valueOf(format.format(a.getEndDate())));
						html.replace("%AGIT_AUCTION_REMAIN%", String.valueOf((a.getEndDate()-System.currentTimeMillis()) / 3600000)+" hours "+String.valueOf((((a.getEndDate()-System.currentTimeMillis()) / 60000) % 60))+" minutes");
						html.replace("%AGIT_AUCTION_MINBID%", String.valueOf(a.getStartingBid()));
						html.replace("%AGIT_AUCTION_MYBID%", String.valueOf(a.getBidder(player.getClan()).getBid()));
						html.replace("%AGIT_AUCTION_DESC%", ClanHallManager.getInstance().getClanHallById(a.getItemId()).getDesc());
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%AGIT_LINK_BACK%", "bypass -h npc_"+getObjectId()+"_start");
					}
					else
						_log.warning("Auctioneer Auction null for AuctionBiddedAt : "+player.getClan().getAuctionBiddedAt());

					player.sendPacket(html);
					return;
				}
				else if (player.getClan() != null
				        && AuctionManager.getInstance().getAuction(player.getClan().getHasHideout()) != null)
				{
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					String filename = "data/html/auction/AgitSaleInfo.htm";
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile(player.getHtmlPrefix(), filename);
					Auction a = AuctionManager.getInstance().getAuction(player.getClan().getHasHideout());
					if (a != null)
					{
						html.replace("%AGIT_NAME%", a.getItemName());
						html.replace("%OWNER_PLEDGE_NAME%", a.getSellerId() != 0 ? a.getSellerClan().getName() : "");
						html.replace("%OWNER_PLEDGE_MASTER%", a.getSellerId() != 0 ? a.getSellerClan().getLeaderName() : "NPC");
						html.replace("%AGIT_SIZE%", String.valueOf(ClanHallManager.getInstance().getClanHallById(a.getItemId()).getGrade()*10));
						html.replace("%AGIT_LEASE%", String.valueOf(ClanHallManager.getInstance().getClanHallById(a.getItemId()).getLease()));
						html.replace("%AGIT_LOCATION%", ClanHallManager.getInstance().getClanHallById(a.getItemId()).getLocation());
						html.replace("%AGIT_AUCTION_END%", String.valueOf(format.format(a.getEndDate())));
						html.replace("%AGIT_AUCTION_REMAIN%", String.valueOf((a.getEndDate()-System.currentTimeMillis()) / 3600000)+" hours "+String.valueOf((((a.getEndDate()-System.currentTimeMillis()) / 60000) % 60))+" minutes");
						html.replace("%AGIT_AUCTION_MINBID%", String.valueOf(a.getStartingBid()));
						html.replace("%AGIT_AUCTION_BIDCOUNT%", String.valueOf(a.getBidders().size()));
						html.replace("%AGIT_AUCTION_DESC%", ClanHallManager.getInstance().getClanHallById(a.getItemId()).getDesc());
						html.replace("%AGIT_LINK_BACK%", "bypass -h npc_"+getObjectId()+"_start");
						html.replace("%id%", String.valueOf(a.getId()));
						html.replace("%objectId%", String.valueOf(getObjectId()));
					}
					else
						_log.warning("Auctioneer Auction null for getHasHideout : "+player.getClan().getHasHideout());

					player.sendPacket(html);
					return;
				}
				else if (player.getClan() != null && player.getClan().getHasHideout() != 0)
				{
					int ItemId = player.getClan().getHasHideout();
					String filename = "data/html/auction/AgitInfo.htm";
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile(player.getHtmlPrefix(), filename);
					if (ClanHallManager.getInstance().getClanHallById(ItemId) != null)
					{
						html.replace("%AGIT_NAME%", ClanHallManager.getInstance().getClanHallById(ItemId).getName());
						html.replace("%AGIT_OWNER_PLEDGE_NAME%", player.getClan().getName());
						html.replace("%OWNER_PLEDGE_MASTER%", player.getClan().getLeaderName());
						html.replace("%AGIT_SIZE%", String.valueOf(ClanHallManager.getInstance().getClanHallById(ItemId).getGrade()*10));
						html.replace("%AGIT_LEASE%", String.valueOf(ClanHallManager.getInstance().getClanHallById(ItemId).getLease()));
						html.replace("%AGIT_LOCATION%", ClanHallManager.getInstance().getClanHallById(ItemId).getLocation());
						html.replace("%AGIT_LINK_BACK%", "bypass -h npc_"+getObjectId()+"_start");
						html.replace("%objectId%", String.valueOf(getObjectId()));
					}
					else
						_log.warning("Clan Hall ID NULL : "+ItemId+" Can be caused by concurent write in ClanHallManager");

					player.sendPacket(html);
					return;
				}
				else if (player.getClan() != null && player.getClan().getHasHideout() == 0)
				{
					player.sendPacket(SystemMessageId.NO_OFFERINGS_OWN_OR_MADE_BID_FOR);
					String filename = "data/html/auction/auction.htm";
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile(player.getHtmlPrefix(), filename);
					player.sendPacket(html);
					return;
				}
				else if (player.getClan() == null)
				{
					player.sendPacket(SystemMessageId.CANNOT_PARTICIPATE_IN_AN_AUCTION);
					String filename = "data/html/auction/auction.htm";
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile(player.getHtmlPrefix(), filename);
					player.sendPacket(html);
					return;
				}
			}
			else if (actualCommand.equalsIgnoreCase("cancelBid"))
			{
				long bid = AuctionManager.getInstance().getAuction(player.getClan().getAuctionBiddedAt()).getBidders().get(player.getClanId()).getBid();
				String filename = "data/html/auction/AgitBidCancel.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(1);
				html.setFile(player.getHtmlPrefix(), filename);
				html.replace("%AGIT_BID%", String.valueOf(bid));
				html.replace("%AGIT_BID_REMAIN%", String.valueOf((long)(bid*0.9)));
				html.replace("%AGIT_LINK_BACK%", "bypass -h npc_"+getObjectId()+"_selectedItems");
				html.replace("%objectId%", String.valueOf(getObjectId()));
				player.sendPacket(html);
				return;
			}
			else if (actualCommand.equalsIgnoreCase("doCancelBid"))
			{
				if (AuctionManager.getInstance().getAuction(player.getClan().getAuctionBiddedAt()) != null)
				{
					AuctionManager.getInstance().getAuction(player.getClan().getAuctionBiddedAt()).cancelBid(player);
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANCELED_BID));
				}
				return;
			}
			else if (actualCommand.equalsIgnoreCase("cancelAuction"))
			{
				if (!((player.getClanPrivileges() & L2Clan.CP_CH_AUCTION) == L2Clan.CP_CH_AUCTION))
				{
					String filename = "data/html/auction/not_authorized.htm";
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile(player.getHtmlPrefix(), filename);
					html.replace("%objectId%", String.valueOf(getObjectId()));
					player.sendPacket(html);
					return;
				}
				String filename = "data/html/auction/AgitSaleCancel.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(1);
				html.setFile(player.getHtmlPrefix(), filename);
				html.replace("%AGIT_DEPOSIT%", String.valueOf(ClanHallManager.getInstance().getClanHallByOwner(player.getClan()).getLease()));
				html.replace("%AGIT_LINK_BACK%", "bypass -h npc_"+getObjectId()+"_selectedItems");
				html.replace("%objectId%", String.valueOf(getObjectId()));
				player.sendPacket(html);
				return;
			}
			else if (actualCommand.equalsIgnoreCase("doCancelAuction"))
			{
				if (AuctionManager.getInstance().getAuction(player.getClan().getHasHideout()) != null)
				{
					//AuctionManager.getInstance().getAuction(player.getClan().getHasHideout()).cancelAuction();
					//ClanHallManager.getInstance().setFree(player.getClan().getHasHideout());
					player.sendMessage("Function disabled.");
				}
				return;
			}
			else if (actualCommand.equalsIgnoreCase("sale2"))
			{
				String filename = "data/html/auction/AgitSale2.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(1);
				html.setFile(player.getHtmlPrefix(), filename);
				html.replace("%AGIT_LAST_PRICE%", String.valueOf(ClanHallManager.getInstance().getClanHallByOwner(player.getClan()).getLease()));
				html.replace("%AGIT_LINK_BACK%", "bypass -h npc_"+getObjectId()+"_sale");
				html.replace("%objectId%", String.valueOf(getObjectId()));
				player.sendPacket(html);
				return;
			}
			else if (actualCommand.equalsIgnoreCase("sale"))
			{
				if (!((player.getClanPrivileges() & L2Clan.CP_CH_AUCTION) == L2Clan.CP_CH_AUCTION))
				{
					String filename = "data/html/auction/not_authorized.htm";
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile(player.getHtmlPrefix(), filename);
					html.replace("%objectId%", String.valueOf(getObjectId()));
					player.sendPacket(html);
					return;
				}
				String filename = "data/html/auction/AgitSale1.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(1);
				html.setFile(player.getHtmlPrefix(), filename);
				html.replace("%AGIT_DEPOSIT%", String.valueOf(ClanHallManager.getInstance().getClanHallByOwner(player.getClan()).getLease()));
				html.replace("%AGIT_PLEDGE_ADENA%", String.valueOf(player.getClan().getWarehouse().getAdena()));
				html.replace("%AGIT_LINK_BACK%", "bypass -h npc_"+getObjectId()+"_selectedItems");
				html.replace("%objectId%", String.valueOf(getObjectId()));
				player.sendPacket(html);
				return;
			}
			else if (actualCommand.equalsIgnoreCase("rebid"))
			{
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				if (!((player.getClanPrivileges() & L2Clan.CP_CH_AUCTION) == L2Clan.CP_CH_AUCTION))
				{
					String filename = "data/html/auction/not_authorized.htm";
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile(player.getHtmlPrefix(), filename);
					html.replace("%objectId%", String.valueOf(getObjectId()));
					player.sendPacket(html);
					return;
				}
				try
				{
					String filename = "data/html/auction/AgitBid2.htm";
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile(player.getHtmlPrefix(), filename);
					Auction a = AuctionManager.getInstance().getAuction(player.getClan().getAuctionBiddedAt());
					if (a != null)
					{
						html.replace("%AGIT_AUCTION_BID%", String.valueOf(a.getBidder(player.getClan()).getBid()));
						html.replace("%AGIT_AUCTION_MINBID%", String.valueOf(a.getStartingBid()));
						html.replace("%AGIT_AUCTION_END%",String.valueOf(format.format(a.getEndDate())));
						html.replace("%AGIT_LINK_BACK%", "bypass -h npc_"+getObjectId()+"_selectedItems");
						html.replace("npc_%objectId%_bid1", "npc_"+getObjectId()+"_bid1 "+a.getId());
					}
					else
						_log.warning("Auctioneer Auction null for AuctionBiddedAt : "+player.getClan().getAuctionBiddedAt());

					player.sendPacket(html);
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction!");
				}
				return;
			}
			else if (actualCommand.equalsIgnoreCase("location"))
			{
				NpcHtmlMessage html = new NpcHtmlMessage(1);
				html.setFile(player.getHtmlPrefix(), "data/html/auction/location.htm");
				html.replace("%location%", MapRegionTable.getInstance().getClosestTownName(player));
				html.replace("%LOCATION%", getPictureName(player));
				html.replace("%AGIT_LINK_BACK%", "bypass -h npc_"+getObjectId()+"_start");
				player.sendPacket(html);
				return;
			}
			else if (actualCommand.equalsIgnoreCase("start"))
			{
				showChatWindow(player);
				return;
			}
		}

		super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(L2PcInstance player)
	{
		String filename = "data/html/auction/auction-no.htm";

		int condition = validateCondition(player);
		if (condition == COND_BUSY_BECAUSE_OF_SIEGE)
			filename = "data/html/auction/auction-busy.htm"; // Busy because of siege
		else
			filename = "data/html/auction/auction.htm";

		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile(player.getHtmlPrefix(), filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcId%", String.valueOf(getNpcId()));
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}

	private int validateCondition(L2PcInstance player)
	{
		if (getCastle() != null && getCastle().getCastleId() > 0)
		{
			if (getCastle().getSiege().getIsInProgress())
				return COND_BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
			else
				return COND_REGULAR;
		}

		return COND_ALL_FALSE;
	}

	private String getPictureName(L2PcInstance plyr)
	{
		int nearestTownId = MapRegionTable.getInstance().getMapRegion(plyr.getX(), plyr.getY());

		switch (nearestTownId)
		{
			case 5:
				return "GLUDIO";
			case 6:
				return "GLUDIN";
			case 7:
				return "DION";
			case 8:
				return "GIRAN";
			case 14:
				return "RUNE";
			case 15:
				return "GODARD";
			case 16:
				return "SCHUTTGART";
			default:
				return "ADEN";
		}
	}
}