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
package l2.universe.gameserver;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastList;

import l2.universe.Config;
import l2.universe.ExternalConfig;
import l2.universe.gameserver.cache.HtmCache;
import l2.universe.gameserver.instancemanager.IrcManager;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.clientpackets.Say2;
import l2.universe.gameserver.network.serverpackets.CreatureSay;
import l2.universe.gameserver.network.serverpackets.L2GameServerPacket;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.script.DateRange;
import l2.universe.gameserver.util.Broadcast;
import l2.universe.util.StringUtil;

/**
 * This class ...
 * 
 * @version $Revision: 1.5.2.1.2.7 $ $Date: 2005/03/29 23:15:14 $
 */
public class Announcements
{
	private static Logger _log = Logger.getLogger(Announcements.class.getName());
	
	private static List<String> _announcements = new FastList<String>();
	private static List<AnnouncementEvent> _eventAnnouncements = new FastList<AnnouncementEvent>();
	
	private Announcements()
	{
		loadAnnouncements();
	}
	
	public static Announcements getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public void loadAnnouncements()
	{
		_announcements.clear();
		File file = new File(Config.DATAPACK_ROOT, "data/announcements.txt");
		if (file.exists())
		{
			readFromDisk(file);
		}
		else
		{
			_log.warning("data/announcements.txt doesn't exist");
		}
	}
	
	public void showAnnouncements(L2PcInstance activeChar)
	{
		for (String announcement : _announcements)
		{
			CreatureSay cs = new CreatureSay(0, Say2.ANNOUNCEMENT, activeChar.getName(), announcement);
			activeChar.sendPacket(cs);
		}
		
		for (final AnnouncementEvent event : _eventAnnouncements)
		{
			Date currentDate = new Date();

			if (event.validDateRange.isWithinRange(currentDate))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1);
				for (String string : event.msg)
				{
					sm.addString(string.replace("\\n", "\n"));
				}
				
				activeChar.sendPacket(sm);
				sm = null;
			}

		}
	}
	
	public void addEventAnnouncement(DateRange validDateRange, String[] msg)
	{
		AnnouncementEvent event = new AnnouncementEvent();
		event.validDateRange = validDateRange;
		event.msg = msg;
		_eventAnnouncements.add(event);
	}
	
	public void listAnnouncements(L2PcInstance activeChar)
	{
		String content = HtmCache.getInstance().getHtmForce(activeChar.getHtmlPrefix(), "data/html/admin/announce.htm");
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setHtml(content);
		final StringBuilder replyMSG = StringUtil.startAppend(500, "<br>");
		for (int i = 0; i < _announcements.size(); i++)
		{
			StringUtil.append(replyMSG, "<table width=260><tr><td width=220>", _announcements.get(i), "</td><td width=40>"
					+ "<button value=\"Delete\" action=\"bypass -h admin_del_announcement ", String.valueOf(i), "\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table>");
		}
		adminReply.replace("%announces%", replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
	
	public void addAnnouncement(String text)
	{
		_announcements.add(text);
		saveToDisk();
	}
	
	public void delAnnouncement(int line)
	{
		_announcements.remove(line);
		saveToDisk();
	}
	
	private void readFromDisk(File file)
	{
		LineNumberReader lnr = null;
		try
		{
			int i = 0;
			String line = null;
			lnr = new LineNumberReader(new FileReader(file));
			while ((line = lnr.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line, "\n\r");
				if (st.hasMoreTokens())
				{
					String announcement = st.nextToken();
					_announcements.add(announcement);
					
					i++;
				}
			}
			
			if (Config.DEBUG)
				_log.info("Announcements: Loaded " + i + " Announcements.");
		}
		catch (IOException e1)
		{
			_log.log(Level.SEVERE, "Error reading announcements: ", e1);
		}
		finally
		{
			try
			{
				lnr.close();
			}
			catch (Exception e2) {}
		}
	}
	
	private void saveToDisk()
	{
		File file = new File("data/announcements.txt");
		FileWriter save = null;
		
		try
		{
			save = new FileWriter(file);
			for (String announcement : _announcements)
			{
				save.write(announcement);
				save.write("\r\n");
			}
		}
		catch (IOException e)
		{
			_log.log(Level.SEVERE, "Saving to the announcements file has failed: ", e);
		}
		finally
		{
			try
			{
				save.close();
			}
			catch (Exception e) {}
		}
	}
	
	public void announceToAll(String text)
	{
		if (ExternalConfig.IRC_ENABLED)
			IrcManager.getInstance().sendChan("Announce:" + text);

		Broadcast.announceToOnlinePlayers(text);
	}
	
	public void announceToAll(SystemMessage sm)
	{
		Broadcast.toAllOnlinePlayers(sm);
	}
	
	public void announceToAll(L2GameServerPacket gsp)
	{
		Broadcast.toAllOnlinePlayers(gsp);
	}

	public void announceToInstance(SystemMessage sm, int instanceId)
	{
		Broadcast.toPlayersInInstance(sm, instanceId);
	}
	
	// Method for handling announcements from admin
	public void handleAnnounce(String command, int lengthToTrim)
	{
		try
		{
			// Announce string to everyone on server
			String text = command.substring(lengthToTrim);
			SingletonHolder._instance.announceToAll(text);
		}
		catch (StringIndexOutOfBoundsException e) {}
	}
	
	protected class AnnouncementEvent
	{
		protected DateRange validDateRange;
		protected String[] msg;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final Announcements _instance = new Announcements();
	}
}
