package handlers.aioitemhandler;

import java.util.Map;
import java.util.logging.Logger;

import javolution.util.FastMap;

import l2.universe.ExternalConfig;
import l2.universe.gameserver.cache.HtmCache;
import l2.universe.gameserver.datatables.AIOItemTable;
import l2.universe.gameserver.datatables.AIOItemTable.SpawnPointInfo;
import l2.universe.gameserver.datatables.AIOItemTable.TeleportCategoryHolder;
import l2.universe.gameserver.handler.IAIOItemHandler;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;

public class AIOTeleportHandler implements IAIOItemHandler 
{
	private static final Logger _log = Logger.getLogger(AIOTeleportHandler.class.getName());
	private static final String BYPASS = "teleport";
	
	@Override
	public String getBypass() 
	{
		return BYPASS;
	}

	@Override
	public void onBypassUse(L2PcInstance player, String command) 
	{
		String[] subCommand = command.split(" ");		
		String actualCmd = subCommand[0];

		if(actualCmd.equalsIgnoreCase("main"))
		{
			StringBuilder sb = new StringBuilder();
			
			FastMap<Integer, TeleportCategoryHolder> teleports = AIOItemTable.getInstance().getTeleports();
			for(int id : teleports.keySet())
			{
				Map.Entry<Integer, TeleportCategoryHolder> entry = teleports.getEntry(id);
				sb.append("<a action=\"bypass -h Aioitem_teleport_categorypage "+id+"\">"+entry.getValue().getName()+"</a>");
			}
			
			final String mainFile = HtmCache.getInstance().getHtm(null, "data/html/aioitem/tpmain.htm");
			
			if(mainFile == null)
			{
				_log.severe("The file "+mainFile+" for the AIO Item telport is null or unreadable!");
				return;
			}
			
			NpcHtmlMessage msg = new NpcHtmlMessage(5);
			msg.setHtml(mainFile);
			msg.replace("%list%", sb.toString());
			player.sendPacket(msg);
		}
		else if(actualCmd.equalsIgnoreCase("categorypage"))
		{
			if(subCommand.length < 2)
			{
				_log.warning("AIOTeleportHandler: Wrong category page bypass: "+command);
				return;
			}
			
			int id = 0;
			try
			{
				id = Integer.parseInt(subCommand[1]);
			}
			catch(NumberFormatException e)
			{
				_log.warning("AIOTeleportHandler: Wrong Teleport category id: "+subCommand[1]);
				return;
			}

			TeleportCategoryHolder holder = AIOItemTable.getInstance().getCategoryTeleports(id);
			
			if(holder != null)
			{
				StringBuilder sb = new StringBuilder();
				final FastMap<Integer, SpawnPointInfo> spawns = holder.getCategoryData();
				
				for(int teleId : spawns.keySet())
				{
					Map.Entry<Integer, SpawnPointInfo> entry = spawns.getEntry(teleId);
					sb.append("<a action=\"bypass -h Aioitem_teleport_goto "+id+" "+teleId+"\">"+entry.getValue()._name+"</a><br1>");
				}
				
				final String categoryFile = HtmCache.getInstance().getHtm(null, "data/html/aioitem/tpcategory.htm");
				
				if(categoryFile == null)
				{
					_log.severe("The file "+categoryFile+" does not exist or is html-unreadable!");
					return;
				}
				
				NpcHtmlMessage msg = new NpcHtmlMessage(5);
				msg.setHtml(categoryFile);
				msg.replace("%list%", sb.toString());
				player.sendPacket(msg);
			}
			else
			{
				_log.severe("The AIOItem teleport category '"+subCommand[1]+"' does not exsit!");
				return;
			}
		}
		else if(actualCmd.equalsIgnoreCase("goto"))
		{
			if(subCommand.length < 3)
			{
				_log.severe("Wrong category/spawn point in the AIOItem: "+command);
				return;
			}
			
			if(!paymentDone(player))
			{
				return;
			}
			
			int categoryId = 0;
			int spawnId = 0;
			try
			{
				categoryId = Integer.parseInt(subCommand[1]);
				spawnId = Integer.parseInt(subCommand[2]);
			}
			catch(NumberFormatException e)
			{
				_log.warning("AIOTeleportTable: Wrong teleport bypass (goto): "+command);
				return;
			}
			
			Map.Entry<Integer, TeleportCategoryHolder> entry = AIOItemTable.getInstance().getTeleports().getEntry(categoryId);
			if(entry == null)
				return;
			
			SpawnPointInfo info = entry.getValue().getSpawnInfo(spawnId).getValue();
			
			if(info != null)
			{
				//player.destroyItemByItemId("AIO Item", ExternalConfig.AIOITEM_TPCOIN, ExternalConfig.AIOITEM_TPCOINAMOUNT, player, true);
				player.teleToLocation(info._x, info._y, info._z);
			}
			else
			{
				_log.severe("The spawn "+subCommand[2]+" for the category "+subCommand[1]+" does not exist!");
			}
		}
	}
	
	/**
	 * Will try to make the player payment. If success paying, will
	 * return true, otherwise, will return false
	 * @param player
	 * @return boolean
	 */
	private boolean paymentDone(L2PcInstance player)
	{
		L2ItemInstance coin = null;
		
		if((coin = player.getInventory().getItemByItemId(ExternalConfig.AIOITEM_GK_COIN)) != null)
		{
			if(coin.getCount() >= ExternalConfig.AIOITEM_GK_PRICE)
			{
				player.destroyItemByItemId("AIOItem", coin.getItemId(), ExternalConfig.AIOITEM_GK_PRICE, player, true);
				return true;
			}
			else
			{
				player.sendMessage("Not enough "+coin.getName()+" to travel!");
				return false;
			}
		}
		else
		{
			player.sendMessage("You dont have the required items to travel!");
			return false;
		}
	}
}
