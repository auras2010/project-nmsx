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

import java.util.List;
import java.util.StringTokenizer;

import javolution.text.TextBuilder;
import javolution.util.FastList;

import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.datatables.ItemMarketTable;
import l2.universe.gameserver.datatables.ItemTable;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.L2ItemMarketModel;
import l2.universe.gameserver.network.serverpackets.ActionFailed;
import l2.universe.gameserver.network.serverpackets.MyTargetSelected;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;
import l2.universe.gameserver.network.serverpackets.ValidateLocation;
import l2.universe.gameserver.templates.chars.L2NpcTemplate;
import l2.universe.gameserver.templates.item.L2EtcItemType;
import l2.universe.gameserver.templates.item.L2WeaponType;
import l2.universe.gameserver.util.Util;

public class L2ItemMarketerInstance extends L2NpcInstance
{
	private final int ITEMS_PER_PAGE = 4;
	// Pack Method : pack (L2_Type << 3) | Grade
	// Item L2 Type
	private final int ALL = 0x00;
	private final int WEAPON = 0x01;
	private final int ARMOR = 0x02;
	private final int RECIPE = 0x03;
	private final int SHOTS = 0x04;
	private final int BOOK = 0x05;
	private final int OTHER = 0x06;
	private final int MATERIAL = 0x07;
	// Item Grade
	private final int NO_G = 0x00;
	private final int D_G = 0x01;
	private final int C_G = 0x02;
	private final int B_G = 0x03;
	private final int A_G = 0x04;
	private final int S_G = 0x05;
	private final int S80_G = 0x06;
	private final int ALL_G = 0x07;

	public L2ItemMarketerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onAction(L2PcInstance player, boolean interact)
	{
		if (!canTarget(player))
			return;
		
		player.setLastFolkNPC(this);
		
		// Check if the L2PcInstance already target the L2NpcInstance
		if (this != player.getTarget())
		{
			// Set the target of the L2PcInstance player
			player.setTarget(this);
			
			// Send a Server->Client packet MyTargetSelected to the L2PcInstance player
			player.sendPacket(new MyTargetSelected(getObjectId(), 0));
			
			// Send a Server->Client packet ValidateLocation to correct the
			// L2NpcInstance position and heading on the client
			player.sendPacket(new ValidateLocation(this));
		}
		else if (interact)
		{
			// Calculate the distance between the L2PcInstance and the L2NpcInstance
			if (!canInteract(player)) // Notify the L2PcInstance AI with AI_INTENTION_INTERACT
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
			else
			{
				showMsgWindow(player);

				// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
	}

	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		List<L2ItemMarketModel> list = null;
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();
		if ("Private".equalsIgnoreCase(actualCommand))
		{
			list = getItemList(player);
			int pId = 0;
			if (st.hasMoreTokens())
				pId = new Integer(st.nextToken());
			showPrivateItemList(list, pId, player);
			return;
		}
		else if ("See".equalsIgnoreCase(actualCommand))
		{
			if (st.hasMoreTokens())
			{
				int bitmask = new Integer(st.nextToken());
				int pgId = 0;
				if (st.hasMoreTokens())
					pgId = new Integer(st.nextToken());
				list = ItemMarketTable.getInstance().getAllItems();
				if (list != null)
				{
					list = filterItemType(bitmask, list);
					showItemList(list, pgId, player, bitmask);
					return;
				}
				else
				{
					sendMsg("There are no items for you 1", player);
					return;
				}
			}
		}
		else if ("BuyItem".equalsIgnoreCase(actualCommand))
		{
			if (st.hasMoreTokens())
			{
				int itemObjId = Integer.parseInt(st.nextToken());
				if (st.hasMoreTokens())
				{
					int count = new Integer(st.nextToken());
					if (count <= 0)
					{
						sendMsg("Item count must be a valid value.", player);
						return;
					}
					buyItem(player, itemObjId, count);
					return;
				}
			}
		}
		else if ("AddItem".equalsIgnoreCase(actualCommand))
		{
			if (st.hasMoreTokens())
			{
				int itemObjId = new Integer(st.nextToken());
				if (st.hasMoreTokens())
				{
					int count = new Integer(st.nextToken());
					if (st.hasMoreTokens())
					{
						int price = new Integer(st.nextToken());
						if (price <= 0)
						{
							sendMsg("You can't sell an item for 0 or negative number.", player);
							return;
						}
						L2ItemInstance item = player.getInventory().getItemByObjectId(itemObjId);
						list = getItemList(player);
						
						if (!item.isSellable())
						{
							sendMsg("This item can not be add.", player);
							return;
						}
						if (item.isShadowItem())
						{
							sendMsg("Unable to add shadow item.", player);
							return;
						}
						if (item.isQuestItem()){
							sendMsg("Unable to add quest item.", player);
							return;
						}
						if (canAddItem(item, count, list, player))
						{
							player.destroyItem("Market Add", item.getObjectId(), count, null, true);
							addItem(player, item, count, price);
							return;
						}
						else
							sendMsg("Unable to add item or incorret item count.", player);
							return;
					}
				}
			}
		}
		else if ("ListInv".equalsIgnoreCase(actualCommand))
		{
			int pageId = 0;
			if (st.hasMoreTokens())
				pageId = new Integer(st.nextToken());
			showInvList(player, pageId);
			return;
		}
		else if ("ItemInfo".equalsIgnoreCase(actualCommand))
		{
			if (st.hasMoreTokens())
			{
				int pgId = new Integer(st.nextToken());
				if (st.hasMoreTokens())
				{
					int bitmask = new Integer(st.nextToken());
					if (st.hasMoreTokens())
					{
						int itemObjId = new Integer(st.nextToken());
						L2ItemMarketModel mrktItem = ItemMarketTable.getInstance().getItem(itemObjId);
						if (mrktItem != null)
							showItemInfo(mrktItem, bitmask, pgId, player);
						return;
					}
				}
			}
		}
		else if ("Main".equalsIgnoreCase(actualCommand))
		{
			showMsgWindow(player);
			return;
		}
		else if ("SelectItem".equalsIgnoreCase(actualCommand))
		{
			if (st.hasMoreTokens())
			{
				int itemObjId = new Integer(st.nextToken());
				player.sendPacket(ActionFailed.STATIC_PACKET);
				String filename = "data/html/marketer/addItem.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(1);
				html.setFile(player.getHtmlPrefix(), filename);
				html.replace("%objectId%", String.valueOf(getObjectId()));
				L2ItemInstance item = player.getInventory().getItemByObjectId(itemObjId);
				html.replace("%aval%", "" + item.getCount());
				html.replace("%itemObjId%", String.valueOf(itemObjId));
				player.sendPacket(html);
				return;
			}
		}
		else if ("ItemInfo2".equalsIgnoreCase(actualCommand))
		{
			if (st.hasMoreTokens())
			{
				int pgId = new Integer(st.nextToken());
				if (st.hasMoreTokens())
				{
					int itemObjId = new Integer(st.nextToken());
					L2ItemMarketModel mrktItem = ItemMarketTable.getInstance().getItem(itemObjId);
					if (mrktItem != null)
						showItemInfo2(mrktItem, pgId, player);
					return;
				}
			}
		}
		else if ("TakeItem".equalsIgnoreCase(actualCommand))
		{
			if (st.hasMoreTokens())
			{
				int itemObjId = new Integer(st.nextToken());
				L2ItemMarketModel mrktItem = ItemMarketTable.getInstance().getItem(itemObjId);
				if (mrktItem != null && player.getObjectId() == mrktItem.getOwnerId())
				{
					ItemMarketTable.getInstance().removeItemFromMarket(mrktItem.getOwnerId(), mrktItem.getItemObjId(), mrktItem.getCount());
					L2ItemInstance item = ItemTable.getInstance().createItem("Market Remove", mrktItem.getItemId(), mrktItem.getCount(), player);
					item.setEnchantLevel(mrktItem.getEnchLvl());
					player.getInventory().addItem("Market Buy", item, player, null);
					sendMsg(mrktItem.getItemName() + " removed succesfully.", player);
					return;
				}
			}
		}
		else if ("SeeCash".equalsIgnoreCase(actualCommand))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			String filename = "data/html/marketer/cash.htm";
			NpcHtmlMessage html = new NpcHtmlMessage(1);
			html.setFile(player.getHtmlPrefix(), filename);
			html.replace("%objectId%", String.valueOf(getObjectId()));
			int money = ItemMarketTable.getInstance().getMoney(player.getObjectId());
			html.replace("%money%", Util.formatAdena(money));
			player.sendPacket(html);
			return;
		}
		else if ("Cash".equalsIgnoreCase(actualCommand))
		{
			int amount = ItemMarketTable.getInstance().getMoney(player.getObjectId());
			ItemMarketTable.getInstance().takeMoney(player.getObjectId(), amount);
			player.getInventory().addAdena("Market Cash", amount, player, null);
			sendMsg("You've earned " + Util.formatAdena(amount) + " adena", player);
			return;
		}
		else if ("ComfirmAdd".equalsIgnoreCase(actualCommand))
		{
			if (st.hasMoreTokens())
			{
				int itemObjId = new Integer(st.nextToken());
				L2ItemInstance item = player.getInventory().getItemByObjectId(itemObjId);
				if (item == null)
					return;
				if (st.hasMoreTokens())
				{
					int count = new Integer(st.nextToken());
					if (count <= 0 || item.getCount() < count)
					{
						sendMsg("Item count must be a valid value.", player);
						return;
					}
					if (st.hasMoreTokens())
					{
						int price = new Integer(st.nextToken());
						if (price <= 0)
						{
							sendMsg("Price must be a valid value.", player);
							return;
						}
						player.sendPacket(ActionFailed.STATIC_PACKET);
						String filename = "data/html/marketer/comfirm.htm";
						NpcHtmlMessage html = new NpcHtmlMessage(1);
						html.setFile(player.getHtmlPrefix(), filename);
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%count%", "" + count);
						if (item.getEnchantLevel() > 0)
							html.replace("%itemName%", item.getName() + " +" + item.getEnchantLevel());
						else
							html.replace("%itemName%", item.getName());
						html.replace("%itemIcon%", getItemIcon(item.getItemId()));
						html.replace("%price%", Util.formatAdena(price));
						html.replace("%iprice%", price);
						html.replace("%itemObjId%", "" + itemObjId);
						player.sendPacket(html);
						return;
					}
				}
			}
		}
		super.onBypassFeedback(player, command);
	}

	private void showMsgWindow(L2PcInstance player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/marketer/main.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile(player.getHtmlPrefix(), filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
		return;
	}

	private void addItem(L2PcInstance player, L2ItemInstance item, int count, int price)
	{
		L2ItemMarketModel itemModel = new L2ItemMarketModel();
		itemModel.setOwnerId(player.getObjectId());
		itemModel.setOwnerName(player.getName());
		itemModel.setItemObjId(item.getObjectId());
		itemModel.setItemId(item.getItemId());
		itemModel.setPrice(price);
		itemModel.setCount(count);
		itemModel.setItemType(item.getItem().getItemType().toString());
		itemModel.setEnchLvl(item.getEnchantLevel());
		if (itemModel.getEnchLvl() > 0)
			itemModel.setItemName(item.getItemName() + " +" + itemModel.getEnchLvl());
		else
			itemModel.setItemName(item.getItemName());
		itemModel.setItemGrade(item.getItem().getCrystalType());
		if (item.isWeapon())
		{
			if (item.getItemType() == L2WeaponType.NONE)
				itemModel.setL2Type("Armor");
			else
				itemModel.setL2Type("Weapon");
		}
		else if (item.isArmor())
			itemModel.setL2Type("Armor");
		else
		{
			if (item.getItemType() == L2EtcItemType.MATERIAL)
				itemModel.setL2Type("Material");
			else if (item.getItemType() == L2EtcItemType.RECIPE)
				itemModel.setL2Type("Recipe");
			else if (item.getItemType() == L2EtcItemType.SCROLL)
				itemModel.setL2Type("Scroll");
			else if (item.getItemType() == L2EtcItemType.SHOT)
				itemModel.setL2Type("Shot");
			else
				itemModel.setL2Type("Other");
		}
		ItemMarketTable.getInstance().addItemToMarket(itemModel, player);
		sendMsg("You added " + count + " <font color=\"LEVEL\">" + item.getItemName() + "</font>.", player);
	}

	private boolean canAddItem(L2ItemInstance item, int count, List<L2ItemMarketModel> list, L2PcInstance activeChar)
	{
		if (activeChar != null && activeChar.getActiveTradeList() != null)
			return false;
		if (activeChar != null && activeChar.isProcessingTransaction())
			return false;
		if (list != null && !list.isEmpty())
		{
			for (L2ItemMarketModel model : list)
			{
				if (model != null)
				{
					if (model.getItemObjId() == item.getObjectId())
						return false;
				}
			}
		}
		return (item.getItemType() != L2EtcItemType.HERB 
				&& item.getCount() >= count 
				&& item.getItem().getDuration() == -1 
				&& item.getItemId() != 57 
				&& item.getItemId() != 5588 
				&& item.isTradeable() 
				&& !item.isEquipped() 
				&& !item.isAugmented() 
				&& item.isQuestItem() 
				&& item.isSellable());
	}

	private boolean canAddItem(L2ItemInstance item)
	{
		return canAddItem(item, 0, null, null);
	}

	private String getItemIcon(int itemId)
	{
		return "Icon." + ItemMarketTable.getInstance().getItemIcon(itemId);
	}

	private List<L2ItemMarketModel> getItemList(L2PcInstance player)
	{
		return ItemMarketTable.getInstance().getItemsByOwnerId(player.getObjectId());
	}

	private void buyItem(L2PcInstance player, int itemObjId, int count)
	{
		L2ItemMarketModel mrktItem = ItemMarketTable.getInstance().getItem(itemObjId);
		if (mrktItem != null && mrktItem.getCount() >= count)
		{
			L2ItemInstance adena = player.getInventory().getItemByItemId(57);
			if (adena.getCount() >= (mrktItem.getPrice() * count))
			{
				int itemId = mrktItem.getItemId();
				int price = mrktItem.getPrice() * count;
				ItemMarketTable.getInstance().removeItemFromMarket(mrktItem.getOwnerId(), mrktItem.getItemObjId(), count);
				player.destroyItem("Market Buy", adena.getObjectId(), price, null, true);
				ItemMarketTable.getInstance().addMoney(mrktItem.getOwnerId(), price);
				L2ItemInstance item = ItemTable.getInstance().createItem("Market Buy", itemId, count, player);
				item.setEnchantLevel(mrktItem.getEnchLvl());
				player.getInventory().addItem("Market Buy", item, player, null);
				sendMsg("You bought " + count + " <font color=\"LEVEL\">" + mrktItem.getItemName() + "</font>.", player);
				return;
			}
			sendMsg("Adena is not enough.", player);
			return;
		}
		sendMsg("Incorrect item count.", player);
	}

	private List<L2ItemMarketModel> filterItemType(int mask, List<L2ItemMarketModel> list)
	{
		List<L2ItemMarketModel> mrktList = new FastList<L2ItemMarketModel>();
		int itype = mask >> 3;
		switch (itype)
		{
			case ALL:
				return filterItemGrade(mask, list);
			case WEAPON:
				for (L2ItemMarketModel model : list)
				{
					if (model != null)
					{
						if (model.getL2Type().equalsIgnoreCase("Weapon"))
							mrktList.add(model);
					}
				}
				return filterItemGrade(mask, mrktList);
			case ARMOR:
				for (L2ItemMarketModel model : list)
				{
					if (model != null)
					{
						if (model.getL2Type().equalsIgnoreCase("Armor"))
							mrktList.add(model);
					}
				}
				return filterItemGrade(mask, mrktList);
			case RECIPE:
				for (L2ItemMarketModel model : list)
				{
					if (model != null)
					{
						if (model.getL2Type().equalsIgnoreCase("Recipe"))
							mrktList.add(model);
					}
				}
				return mrktList;
			case BOOK:
				for (L2ItemMarketModel model : list)
				{
					if (model != null)
					{
						if (model.getL2Type().equalsIgnoreCase("Spellbook"))
							mrktList.add(model);
					}
				}
				return mrktList;
			case SHOTS:
				for (L2ItemMarketModel model : list)
				{
					if (model != null)
					{
						if (model.getL2Type().equalsIgnoreCase("Shot"))
							mrktList.add(model);
					}
				}
				return filterItemGrade(mask, mrktList);
			case OTHER:
				for (L2ItemMarketModel model : list)
				{
					if (model != null)
					{
						if (model.getL2Type().equalsIgnoreCase("Other"))
							mrktList.add(model);
					}
				}
				return filterItemGrade(mask, mrktList);
			case MATERIAL:
				for (L2ItemMarketModel model : list)
				{
					if (model != null)
					{
						if (model.getL2Type().equalsIgnoreCase("Material"))
							mrktList.add(model);
					}
				}
				return mrktList;
		}
		return filterItemGrade(mask, list);
	}

	private List<L2ItemMarketModel> filterItemGrade(int mask, List<L2ItemMarketModel> list)
	{
		List<L2ItemMarketModel> mrktList = new FastList<L2ItemMarketModel>();
		int igrade = mask & 7;
		switch (igrade)
		{
			case ALL_G:
				return list;
			case NO_G:
				for (L2ItemMarketModel model : list)
				{
					if (model != null)
					{
						if (model.getItemGrade() == NO_G)
							mrktList.add(model);
					}
				}
				return mrktList;
			case D_G:
				for (L2ItemMarketModel model : list)
				{
					if (model != null)
					{
						if (model.getItemGrade() == D_G)
							mrktList.add(model);
					}
				}
				return mrktList;
			case C_G:
				for (L2ItemMarketModel model : list)
				{
					if (model != null)
					{
						if (model.getItemGrade() == C_G)
							mrktList.add(model);
					}
				}
				return mrktList;
			case B_G:
				for (L2ItemMarketModel model : list)
				{
					if (model != null)
					{
						if (model.getItemGrade() == B_G)
							mrktList.add(model);
					}
				}
				return mrktList;
			case A_G:
				for (L2ItemMarketModel model : list)
				{
					if (model != null)
					{
						if (model.getItemGrade() == A_G)
							mrktList.add(model);
					}
				}
				return mrktList;
			case S_G:
				for (L2ItemMarketModel model : list)
				{
					if (model != null)
					{
						if (model.getItemGrade() == S_G)
							mrktList.add(model);
					}
				}
				return mrktList;
			case S80_G:
				for (L2ItemMarketModel model : list)
				{
					if (model != null)
					{
						if (model.getItemGrade() == S80_G)
							mrktList.add(model);
					}
				}
				return mrktList;
		}
		return list;
	}

	private List<L2ItemInstance> filterInventory(L2ItemInstance[] inv)
	{
		List<L2ItemInstance> filteredInventory = new FastList<L2ItemInstance>();
		for (L2ItemInstance item : inv)
		{
			if (canAddItem(item))
				filteredInventory.add(item);
		}
		return filteredInventory;
	}

	private List<L2ItemMarketModel> filterList(List<L2ItemMarketModel> list, L2PcInstance player)
	{
		List<L2ItemMarketModel> filteredList = new FastList<L2ItemMarketModel>();
		if (!list.isEmpty())
		{
			for (L2ItemMarketModel model : list)
			{
				if (model != null && model.getOwnerId() != player.getObjectId())
					filteredList.add(model);
			}
		}
		return filteredList;
	}

	private void showInvList(L2PcInstance player, int pageId)
	{
		int itemsOnPage = ITEMS_PER_PAGE;
		List<L2ItemInstance> list = filterInventory(player.getInventory().getItems());
		int pages = list.size() / itemsOnPage;
		if (list.isEmpty())
		{
			sendMsg("Your inventory is empty.", player);
			return;
		}
		if (list.size() > pages * itemsOnPage)
			pages++;
		if (pageId > pages)
			pageId = pages;
		int itemStart = pageId * itemsOnPage;
		int itemEnd = list.size();
		if (itemEnd - itemStart > itemsOnPage)
			itemEnd = itemStart + itemsOnPage;
		NpcHtmlMessage npcReply = new NpcHtmlMessage(1);
		TextBuilder reply = new TextBuilder("<html><body>");
		reply.append("<center>Items in Inventory</center>");
		reply.append("<img src=\"L2UI.SquareWhite\" width=270 height=1> <img src=\"L2UI.SquareBlank\" width=1 height=3>");
		reply.append("<table width=270><tr>");
		reply.append("<td width=66><button value=\"Back\" action=\"bypass -h npc_" + getObjectId() + ((pageId == 0) ? "_Main " : "_ListInv ") + (pageId - 1) + "\" width=66 height=16 back=\"L2UI.DefaultButton_click\" fore=\"L2UI.DefaultButton\"></td>");
		reply.append("<td width=138></td>");
		reply.append("<td width=66>" + ((pageId + 1 < pages) ? "<button value=\"Next\" action=\"bypass -h npc_" + getObjectId() + "_ListInv " + (pageId + 1) + "\" width=66 height=16 back=\"L2UI.DefaultButton_click\" fore=\"L2UI.DefaultButton\">" : "") + "</td>");
		reply.append("</tr></table>");
		//reply.append("<br>");
		for (int i = itemStart; i < itemEnd; i++)
		{
			L2ItemInstance item = list.get(i);
			if (item == null)
				continue;
			
			String itemIcon = getItemIcon(item.getItemId());
			reply.append("<br>");
			reply.append("<table width=270><tr>");
			reply.append("<td valign=top width=35><button value=\"\" action=\"bypass -h npc_" + getObjectId() + "_SelectItem " + item.getObjectId() + "\" width=32 height=32 back=\"" + itemIcon + "\" fore=\"" + itemIcon + "\"></td>");
			reply.append("<td valign=top width=235>");
			reply.append("<table border=0 width=100%>");
			if (item.getEnchantLevel() > 0)
				reply.append("<tr><td><font color=\"A2A0A2\">" + item.getItemName() + " +" + item.getEnchantLevel() + "</font></td></tr>");
			else
				reply.append("<tr><td><font color=\"A2A0A2\">" + item.getItemName() + "</font></td></tr>");
			reply.append("<tr><td><font color=\"A2A0A2\">Quantity:</font> <font color=\"B09878\">" + item.getCount() + "</font></td></tr></table></td>");
			reply.append("</tr></table>");
			//reply.append("<br>");
		}
		reply.append("<br>");
		reply.append("</body></html>");
		npcReply.setHtml(reply.toString());
		player.sendPacket(npcReply);
	}

	private void showItemList(List<L2ItemMarketModel> list, int pageId, L2PcInstance player, int mask)
	{
		int itemsOnPage = ITEMS_PER_PAGE;
		list = filterList(list, player);
		if (list.isEmpty())
		{
			sendMsg("There are no items for you 2", player);
			return;
		}
		int pages = list.size() / itemsOnPage;
		if (list.size() > pages * itemsOnPage)
			pages++;
		if (pageId > pages)
			pageId = pages;
		int itemStart = pageId * itemsOnPage;
		int itemEnd = list.size();
		if (itemEnd - itemStart > itemsOnPage)
			itemEnd = itemStart + itemsOnPage;
		NpcHtmlMessage npcReply = new NpcHtmlMessage(1);
		TextBuilder reply = new TextBuilder("<html><body>");
		reply.append("<center>Market</center>");
		reply.append("<img src=\"L2UI.SquareWhite\" width=270 height=1> <img src=\"L2UI.SquareBlank\" width=1 height=3>");
		reply.append("<table width=270><tr>");
		reply.append("<td width=66><button value=\"Back\" action=\"bypass -h npc_" + getObjectId() + ((pageId == 0) ? "_Main " : "_See ") + mask + " " + (pageId - 1) + "\" width=66 height=16 back=\"L2UI.DefaultButton_click\" fore=\"L2UI.DefaultButton\"></td>");
		reply.append("<td width=138></td>");
		reply.append("<td width=66>" + ((pageId + 1 < pages) ? "<button value=\"Next\" action=\"bypass -h npc_" + getObjectId() + "_See " + mask + " " + (pageId + 1) + "\" width=66 height=16 back=\"L2UI.DefaultButton_click\" fore=\"L2UI.DefaultButton\">" : "") + "</td>");
		reply.append("</tr></table>");
		//reply.append("<br>");
		for (int i = itemStart; i < itemEnd; i++)
		{
			L2ItemMarketModel mrktItem = list.get(i);
			if (mrktItem == null)
				continue;
			if (mrktItem.getOwnerId() == player.getObjectId())
				continue;
			int _price = mrktItem.getPrice();
			if (_price == 0)
				continue;
			@SuppressWarnings("unused")
			int _grade = mrktItem.getItemGrade();
			String itemIcon = getItemIcon(mrktItem.getItemId());
			reply.append("<br>");
			reply.append("<table width=270><tr>");
			reply.append("<td valign=top width=35><button value=\"\" action=\"bypass -h npc_" + getObjectId() + "_ItemInfo " + pageId + " " + mask + " " + mrktItem.getItemObjId() + "\" width=32 height=32 back=\"" + itemIcon + "\" fore=\"" + itemIcon + "\"></td>");
			reply.append("<td valign=top width=235>");
			reply.append("<table border=0 width=100%>");
			reply.append("<tr><td><font color=\"A2A0A2\">" + mrktItem.getItemName() + "[" + mrktItem.getCount() + "]" + "</font></td></tr>");
			if ((mask & 7) == ALL_G)
				reply.append("<tr><td><font color=\"A2A0A2\">" + "Grade:" + getGrade(mrktItem.getItemGrade()) + "</font></td></tr>");
			reply.append("<tr><td><font color=\"A2A0A2\">Price:</font> <font color=\"B09878\">" + Util.formatAdena(mrktItem.getPrice()) + "</font></td></tr></table></td>");
			reply.append("</tr></table>");
			//reply.append("<br>");
		}
		reply.append("<br>");
		reply.append("</body></html>");
		npcReply.setHtml(reply.toString());
		player.sendPacket(npcReply);
	}

	private void showItemInfo(L2ItemMarketModel mrktItem, int mask, int pageId, L2PcInstance player)
	{
		NpcHtmlMessage npcReply = new NpcHtmlMessage(1);
		TextBuilder reply = new TextBuilder("<html><body>");
		reply.append("<center>Info</center>");
		reply.append("<img src=\"L2UI.SquareWhite\" width=270 height=1> <img src=\"L2UI.SquareBlank\" width=1 height=3>");
		reply.append("<table width=270><tr>");
		reply.append("<td width=66><button value=\"Back\" action=\"bypass -h npc_" + getObjectId() + "_See " + mask + " " + pageId + "\" width=66 height=16 back=\"L2UI.DefaultButton_click\" fore=\"L2UI.DefaultButton\"></td>");
		reply.append("<td width=138></td>");
		reply.append("</tr></table>");
		reply.append("<br>");
		reply.append("<table width=270><tr>");
		reply.append("<td valign=top width=35><img src=" + getItemIcon(mrktItem.getItemId()) + " width=32 height=32 align=left></td>");
		reply.append("<td valign=top width=235>");
		reply.append("<table border=0 width=100%>");
		reply.append("<tr><td><font color=\"A2A0A2\">Name:</font> <font color=\"B09878\">" + mrktItem.getItemName() + "</font><font color=\"A2A0A2\">[<font color=\"B09878\">" + mrktItem.getCount() + "</font>]</td></tr>");
		reply.append("<tr><td><font color=\"A2A0A2\">Price:</font> <font color=\"B09878\">" + Util.formatAdena(mrktItem.getPrice()) + "</font><font color=\"A2A0A2\"></td></tr>");
		reply.append("<tr><td>Seller: </font><font color=\"B09878\">" + mrktItem.getOwnerName() + "</font></td></tr>");
		reply.append("<tr><td><edit var=\"count\" width=110></td></tr>");
		reply.append("<tr><td><button value=\"Buy\" action=\"bypass -h npc_" + getObjectId() + "_BuyItem " + mrktItem.getItemObjId() + " $count\" width=66 height=16 back=\"L2UI.DefaultButton_click\" fore=\"L2UI.DefaultButton\"></td></tr></table></td>");
		reply.append("</tr></table>");
		reply.append("</body></html>");
		npcReply.setHtml(reply.toString());
		player.sendPacket(npcReply);
	}

	private void showPrivateItemList(List<L2ItemMarketModel> list, int pageId, L2PcInstance player)
	{
		int itemsOnPage = ITEMS_PER_PAGE;
		if (list == null || list.isEmpty())
		{
			sendMsg("There are no items for you 3", player);
			return;
		}
		int pages = list.size() / itemsOnPage;
		if (list.size() > pages * itemsOnPage)
			pages++;
		if (pageId > pages)
			pageId = pages;
		int itemStart = pageId * itemsOnPage;
		int itemEnd = list.size();
		if (itemEnd - itemStart > itemsOnPage)
			itemEnd = itemStart + itemsOnPage;
		NpcHtmlMessage npcReply = new NpcHtmlMessage(1);
		TextBuilder reply = new TextBuilder("<html><body>");
		reply.append("<center>Private</center>");
		reply.append("<img src=\"L2UI.SquareWhite\" width=270 height=1> <img src=\"L2UI.SquareBlank\" width=1 height=3>");
		reply.append("<table width=270><tr>");
		reply.append("<td width=66><button value=\"Back\" action=\"bypass -h npc_" + getObjectId() + ((pageId == 0) ? "_Main " : "_Private ") + (pageId - 1) + "\" width=66 height=16 back=\"L2UI.DefaultButton_click\" fore=\"L2UI.DefaultButton\"></td>");
		reply.append("<td width=138></td>");
		reply.append("<td width=66>" + ((pageId + 1 < pages) ? "<button value=\"Next\" action=\"bypass -h npc_" + getObjectId() + "_Private " + (pageId + 1) + "\" width=66 height=16 back=\"L2UI.DefaultButton_click\" fore=\"L2UI.DefaultButton\">" : "") + "</td>");
		reply.append("</tr></table>");
		//reply.append("<br>");
		for (int i = itemStart; i < itemEnd; i++)
		{
			L2ItemMarketModel mrktItem = list.get(i);
			if (mrktItem == null)
				continue;
			int _price = mrktItem.getPrice();
			if (_price == 0)
				continue;
			String itemIcon = getItemIcon(mrktItem.getItemId());
			reply.append("<br>");
			reply.append("<table width=270><tr>");
			reply.append("<td valign=top width=35><button value=\"\" action=\"bypass -h npc_" + getObjectId() + "_ItemInfo2 " + pageId + " " + mrktItem.getItemObjId() + "\" width=32 height=32 back=\"" + itemIcon + "\" fore=\"" + itemIcon + "\"></td>");
			reply.append("<td valign=top width=235>");
			reply.append("<table border=0 width=100%>");
			reply.append("<tr><td><font color=\"A2A0A2\">" + mrktItem.getItemName() + "[" + mrktItem.getCount() + "]" + "</font></td></tr>");
			reply.append("<tr><td><font color=\"A2A0A2\">Price:</font> <font color=\"B09878\">" + Util.formatAdena(mrktItem.getPrice()) + "</font></td></tr></table></td>");
			reply.append("</tr></table>");
			//reply.append("<br>");
		}
		reply.append("<br>");
		reply.append("</body></html>");
		npcReply.setHtml(reply.toString());
		player.sendPacket(npcReply);
	}

	private void sendMsg(String message, L2PcInstance player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		NpcHtmlMessage npcReply = new NpcHtmlMessage(1);
		TextBuilder reply = new TextBuilder("<html><body>");
		reply.append(message);
		reply.append("<br><a action=\"bypass -h npc_" + getObjectId() + "_Main\">Back</a>");
		reply.append("</body></html>");
		npcReply.setHtml(reply.toString());
		player.sendPacket(npcReply);
	}

	private void showItemInfo2(L2ItemMarketModel mrktItem, int pageId, L2PcInstance player)
	{
		NpcHtmlMessage npcReply = new NpcHtmlMessage(1);
		TextBuilder reply = new TextBuilder("<html><body>");
		reply.append("<center>Info</center>");
		reply.append("<img src=\"L2UI.SquareWhite\" width=270 height=1> <img src=\"L2UI.SquareBlank\" width=1 height=3>");
		reply.append("<table width=270><tr>");
		reply.append("<td width=66><button value=\"Back\" action=\"bypass -h npc_" + getObjectId() + "_Private " + pageId + "\" width=66 height=16 back=\"L2UI.DefaultButton_click\" fore=\"L2UI.DefaultButton\"></td>");
		reply.append("<td width=138></td>");
		reply.append("</tr></table>");
		reply.append("<br>");
		reply.append("<table width=270><tr>");
		reply.append("<td valign=top width=35><img src=" + getItemIcon(mrktItem.getItemId()) + " width=32 height=32 align=left></td>");
		reply.append("<td valign=top width=235>");
		reply.append("<table border=0 width=100%>");
		reply.append("<tr><td><font color=\"A2A0A2\">Name:</font> <font color=\"B09878\">" + mrktItem.getItemName() + "</font><font color=\"A2A0A2\">[<font color=\"B09878\">" + mrktItem.getCount() + "</font>]</td></tr>");
		reply.append("<tr><td><font color=\"A2A0A2\">Price:</font> <font color=\"B09878\">" + Util.formatAdena(mrktItem.getPrice()) + "</font><font color=\"A2A0A2\"></td></tr>");
		reply.append("<tr><td><button value=\"Remove\" action=\"bypass -h npc_" + getObjectId() + "_TakeItem " + mrktItem.getItemObjId() + "\" width=66 height=16 back=\"L2UI.DefaultButton_click\" fore=\"L2UI.DefaultButton\"></td></tr></table></td>");
		reply.append("</tr></table>");
		reply.append("</body></html>");
		npcReply.setHtml(reply.toString());
		player.sendPacket(npcReply);
	}

	private String getGrade(int grade)
	{
		switch (grade)
		{
			case D_G:
				return "D Grade";
			case C_G:
				return "C Grade";
			case B_G:
				return "B Grade";
			case A_G:
				return "A Grade";
			case S_G:
				return "S Grade";
			case S80_G:
				return "S80 Grade";
			default:
				return "No Grade";
		}
	}
}
