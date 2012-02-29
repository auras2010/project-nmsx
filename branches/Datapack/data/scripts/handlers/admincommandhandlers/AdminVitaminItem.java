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
package handlers.admincommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javolution.text.TextBuilder;
import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.handler.IAdminCommandHandler;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminVitaminItem implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS = 
	{ 
		"admin_vitaminitem", 
		"admin_sendvitamin"
	};
	
	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar)
	{
		if (command.equals("admin_vitaminitem"))
		{
			main_txt(activeChar);
		}
		else if (command.startsWith("admin_sendvitamin"))
		{
			final String[] args = command.split(" ");
			final int itemId = Integer.parseInt(args[1]);
			final long itemcount = Long.parseLong(args[2]);
			int online = 0;
			if (args[3].equals("online"))
				online = 1;
			
			add_vit_item(itemId, itemcount, online, activeChar);
			main_txt(activeChar);
		}
		return true;
	}
	
	public void main_txt(final L2PcInstance player)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(5);
		final TextBuilder sb = new TextBuilder();
		sb.append("<html><title>Vitamin item sender</title><body>");
		sb.append("<table width=270>");
		sb.append("<tr><td>Add Vitamin item:</td></tr>");
		sb.append("<tr><td>Item Id: </td></tr>");
		sb.append("<tr><td><td><edit width=120 var=\"tsId\"></td></tr>");
		sb.append("<tr><td>Item Count: </td></tr>");
		sb.append("<tr><td><td><edit width=120 var=\"tsCnt\"></td></tr>");
		sb.append("<tr><td>To: </td></tr>");
		sb.append("<tr><td><td><combobox width=75 var=tsPpl list=all;online> Players</td></tr>");
		sb.append("</table>");
		sb.append("<table width=270>");
		sb.append("<tr>");
		sb.append("<td><button value=\"Send item\" width=80 action=\"bypass -h admin_sendvitamin $tsId $tsCnt $tsPpl\" height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		sb.append("</tr>");
		sb.append("</table></body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	public void add_vit_item(final int id, final long count, final int online, final L2PcInstance player)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement;
			if (online == 1)
				statement = con.prepareStatement("SELECT charId FROM characters WHERE online = 1 AND accesslevel > -1 AND deletetime = 0");
			else
				statement = con.prepareStatement("SELECT charId FROM characters WHERE accesslevel > -1 AND deletetime = 0");
			
			final PreparedStatement statement2 = con.prepareStatement("SELECT Id FROM character_premium_items WHERE charId=? ORDER BY Id DESC");
			final PreparedStatement statement3 = con.prepareStatement("INSERT INTO character_premium_items (charId, Id, itemId, itemCount, itemSender) VALUES (?,?,?,?,?)");
			
			final ResultSet set = statement.executeQuery();
			
			while (set.next())
			{
				final int charid = set.getInt("charId");
				final int lastnum = getlastnum(statement2, charid);
				
				statement3.setInt(1, charid);
				statement3.setInt(2, lastnum + 1);
				statement3.setInt(3, id);
				statement3.setLong(4, count);
				statement3.setString(5, "Server");
				statement3.execute();
			}
			set.close();
			statement.close();
			statement2.close();
			statement3.close();
			
			player.sendMessage("Items added to players");
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	public int getlastnum(final PreparedStatement statement, final int charid)
	{
		int i = 0;
		
		try
		{
			statement.setInt(1, charid);
			final ResultSet set = statement.executeQuery();
			if (set.next())
				i = set.getInt("charId");
			set.close();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		
		return i;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
