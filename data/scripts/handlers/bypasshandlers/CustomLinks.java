package handlers.bypasshandlers;

import l2.universe.gameserver.cache.HtmCache;
import l2.universe.gameserver.handler.IBypassHandler;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * With this bypass you can add custom links to html files.<br>
 * <a action="bypass -h CustomLinks custom.html">Custom Link</a><br>
 * Root directory data/html/custom
 * @author Zoey76
 */
public class CustomLinks implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"CustomLinks"
	};
	
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		try
		{
			String path = command.substring(11).trim();
			if (path.indexOf("..") != -1)
				return false;
			String filename = "data/html/custom" + path;
	
			String content = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), filename);
			
			if ((content == null) || content.isEmpty())
			{
				_log.warning("[" + getClass().getSimpleName() + "] " + filename + " content is null or empty.");
				return false;
			}
			
			activeChar.sendPacket(new NpcHtmlMessage(0, content));
			return true;
		}
		catch (Exception e)
		{
			_log.warning("[" + getClass().getSimpleName() + "] Exception: " + e);
		}
		return false;
	}
	
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}