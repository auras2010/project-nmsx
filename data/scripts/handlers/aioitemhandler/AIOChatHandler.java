/**
 * 
 */
package handlers.aioitemhandler;

import java.util.logging.Logger;

import l2.universe.gameserver.cache.HtmCache;
import l2.universe.gameserver.handler.IAIOItemHandler;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;

public class AIOChatHandler implements IAIOItemHandler
{
	private static final Logger _log = Logger.getLogger(AIOChatHandler.class.getName());
	private static final String ROOT = "data/html/";
	
	@Override
	public String getBypass()
	{
		return "chat";
	}

	@Override
	public void onBypassUse(L2PcInstance player, String command)
	{
		final String fileRoot = ROOT + command + ".htm";
		final String html = HtmCache.getInstance().getHtm(null, fileRoot);
		if(html != null)
		{
			NpcHtmlMessage msg = new NpcHtmlMessage(5);
			msg.setHtml(html);
			msg.replace("%name%", player.getName());
			player.sendPacket(msg);
		}
		else
		{
			player.sendMessage("The requested page doesn't exist. Ask Admin to check it");
			_log.warning("AIOChatHandler: Missing HTML page requested, at: "+fileRoot);
		}
	}
}
