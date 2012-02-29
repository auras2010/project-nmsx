package handlers.aioitemhandler;

import java.util.logging.Logger;

import javolution.text.TextBuilder;
import javolution.util.FastMap;

import l2.universe.ExternalConfig;
import l2.universe.gameserver.cache.HtmCache;
import l2.universe.gameserver.datatables.AIOItemTable;
import l2.universe.gameserver.handler.IAIOItemHandler;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;

public class AIOBufferHandler implements IAIOItemHandler 
{
	private static final Logger _log = Logger.getLogger(AIOBufferHandler.class.getName());
	private static final String BYPASS = "buffer";
	
	@Override
	public String getBypass() 
	{
		return BYPASS;
	}

	@Override
	public void onBypassUse(L2PcInstance player, String command) 
	{
		String[] subCommands = command.split(" ");
		final String actualCmd = subCommands[0];
		
		if(actualCmd == null || actualCmd.isEmpty())
		{
			_log.severe("Wrong parameters for the AIOItem buffer: "+command);
			return;
		}
		
		/*
		 * Show all buffs categories
		 */
		if(actualCmd.equalsIgnoreCase("main"))
		{
			StringBuilder sb = new StringBuilder();
			
			for(String cat : AIOItemTable.getInstance().getBuffs().keySet())
			{
				sb.append("<a action=\"bypass -h Aioitem_buffer_category "+cat+"\">"+cat+"</a>");
			}
			
			String html = HtmCache.getInstance().getHtm(null, "data/html/aioitem/buffermain.htm");
			if(html == null)
			{
				_log.severe("The file buffermain.htm does not exist or is corrupted!");
				return;
			}
			
			NpcHtmlMessage msg = new NpcHtmlMessage(5);
			msg.setHtml(html);
			msg.replace("%list%", sb.toString());
			player.sendPacket(msg);
		}
		/*
		 * Show given category buffs
		 */
		else if(actualCmd.equalsIgnoreCase("category"))
		{
			String secondCmd = subCommands[1];
			FastMap<Integer, L2Skill> tempCat = AIOItemTable.getInstance().getBuffCategory(secondCmd).getCategoryBuffs();
			
			if(tempCat != null)
			{
				showBuffCategoryWindow(secondCmd, tempCat, player);
			}
			else
			{
				_log.severe("AIOItem Buffer: Null category: "+secondCmd);
				return;
			}
		}
		/*
		 * Single buff
		 */
		else if(actualCmd.equalsIgnoreCase("buff"))
		{
			/*
			 * Payment
			 */
			if(!paymentDone(player))
			{
				return;
			}
			
			/*
			 * Buff parse
			 */
			int buffId = 0;
			try
			{
				buffId = Integer.parseInt(subCommands[2]);
			}
			catch(NumberFormatException nfe)
			{
				nfe.printStackTrace();
			}
			
			String secondCmd = subCommands[1];

			/*
			 * Get effects
			 */
			AIOItemTable.getInstance().getBuff(secondCmd, buffId).getEffects(player, player);
			showBuffCategoryWindow(secondCmd, AIOItemTable.getInstance().getBuffCategory(secondCmd).getCategoryBuffs(), player);
		}
		/*
		 * Buffer services
		 */
		else if(actualCmd.equalsIgnoreCase("other"))
		{
			if(!paymentDone(player))
			{
				return;
			}
			
			String secondCmd = subCommands[1];
			
			/*
			 * Heal
			 */
			if(secondCmd.equalsIgnoreCase("heal"))
			{
				player.setCurrentCp(player.getMaxCp());
				player.setCurrentMp(player.getMaxMp());
				player.setCurrentHp(player.getMaxHp());
			}
			/*
			 * Cancel
			 */
			else if(secondCmd.equalsIgnoreCase("cancel"))
			{
				player.stopAllEffectsExceptThoseThatLastThroughDeath();
			}
		}
	}

	/**
	 * Will reduce the player items required for the buffer
	 * or will return false, in which case, wont buff/serve
	 * him
	 * @param player
	 * @return boolean
	 */
	private boolean paymentDone(L2PcInstance player)
	{
		L2ItemInstance payment = null;
		if((payment = player.getInventory().getItemByItemId(ExternalConfig.AIOITEM_BUFF_COIN)) != null)
		{
			if(payment.getCount() < ExternalConfig.AIOITEM_BUFF_PRICE)
			{
				player.sendMessage("Not enough "+payment.getName()+" to buy buffs!");
				return false;
			}
			else
			{
				player.destroyItemByItemId("AIO Item", ExternalConfig.AIOITEM_BUFF_COIN, ExternalConfig.AIOITEM_BUFF_PRICE, player, true);
				return true;
			}
		}
		else
		{
			player.sendMessage("You dont have the required items to buy buffs!");
			return false;
		}
	}
	
	/**
	 * Will show the given category with her buffs
	 * @param cat
	 * @param category
	 * @param player
	 */
	private void showBuffCategoryWindow(String cat, FastMap<Integer, L2Skill> category, L2PcInstance player)
	{
		int b = 2;
		
		TextBuilder tb = new TextBuilder();
		tb.append("<html><body><center>");
		tb.append("<br><font color=LEVEL>Choose what you want!</font><br>");
		tb.append("<table width = 240 height = 32>");
		for (int i : category.keySet())
		{
			if (b % 2 == 0)
			{
				tb.append("<tr>");
				tb.append("<td><a action=\"bypass -h Aioitem_buffer_buff "+cat+" "+i+"\">"+category.get(i).getName()+"</a></td>");
			}
			else
			{
				tb.append("<td><a action=\"bypass -h Aioitem_buffer_buff "+cat+" "+i+"\">"+category.get(i).getName()+"</a></td>");
				tb.append("</tr>");
			}
			b++;
		}
		tb.append("</table><br><a action=\"bypass -h Aioitem_buffer_main\"><font color=LEVEL>Main</font></a>");
		tb.append("</center></body></html>");
		
		NpcHtmlMessage msg = new NpcHtmlMessage(5);
		msg.setHtml(tb.toString());
		player.sendPacket(msg);
	}
}
