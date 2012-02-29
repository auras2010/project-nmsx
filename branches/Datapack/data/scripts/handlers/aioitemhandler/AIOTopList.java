/**
 * 
 */
package handlers.aioitemhandler;

import java.util.logging.Logger;

import l2.universe.gameserver.cache.HtmCache;
import l2.universe.gameserver.datatables.AIOItemTable;
import l2.universe.gameserver.handler.IAIOItemHandler;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;

public class AIOTopList implements IAIOItemHandler
{
	private static final Logger _log = Logger.getLogger(AIOTopList.class.getName());
	
	@Override
	public String getBypass()
	{
		return "toplist";
	}

	@Override
	public void onBypassUse(L2PcInstance player, String command)
	{
		if(command.startsWith("pvptop"))
		{
			String html = HtmCache.getInstance().getHtm(null, "data/html/aioitem/pvptoplist.htm");
			if(html != null)
			{
				String list = AIOItemTable.getInstance().getPvpRank();
				if(list == null)
				{
					_log.warning("AIOItemTable: PvP Rank is null!");
					return;
				}
				
				NpcHtmlMessage msg = new NpcHtmlMessage(5);
				msg.setHtml(html);
				msg.replace("%list%", list);
				player.sendPacket(msg);
			}
		}
		else if(command.startsWith("pktop"))
		{
			String html = HtmCache.getInstance().getHtm(null, "data/html/aioitem/pktoplist.htm");
			if(html != null)
			{
				String list = AIOItemTable.getInstance().getPkRank();
				if(list == null)
				{
					_log.warning("AIOItemTable: Pk Rank is null!");
					return;
				}
				
				NpcHtmlMessage msg = new NpcHtmlMessage(5);
				msg.setHtml(html);
				msg.replace("%list%", list);
				player.sendPacket(msg);
			}
		}
	}
}
