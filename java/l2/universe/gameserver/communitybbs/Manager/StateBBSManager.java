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
package l2.universe.gameserver.communitybbs.Manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javolution.text.TextBuilder;

import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.cache.HtmCache;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;

public class StateBBSManager extends BaseBBSManager
{
	public class CBStatMan
	{
		public int PlayerId = 0; // 
		public String ChName = ""; // 
		public int ChGameTime = 0; // 
		public int ChPk = 0; //
		public int ChPvP = 0; //
		public int ChPcBangPoint = 0; //
		public String ChClanName = ""; // 
		public int ChClanLevel = 0; //
		public int ChClanRep = 0; //
		public String ChClanAlly = ""; // 
		public int ChOnOff = 0; //
		public int ChSex = 0; //		
	}
	
	public static StateBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	@Override
	public void parsecmd(String command, L2PcInstance player)
	{
		if (command.equals("_bbsstat;"))
		{
			showPvp(player);
		}
		else if (command.startsWith("_bbsstat;pk"))
		{
			showPK(player);
		}
		else if (command.startsWith("_bbsstat;clan"))
		{
			showClan(player);
		}
		else if (command.startsWith("_bbsstat;pcbang"))
		{
			showPcBang(player);
		}
		else
		{
			separateAndSend("<html><body><br><br><center>In bbsstat function: " + command + " is not implemented yet.</center><br><br></body></html>", player);
		}
	}
	
	private void showPvp(L2PcInstance player)
	{
		
		CBStatMan tp;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM characters WHERE accesslevel = '0' ORDER BY pvpkills DESC LIMIT 20;");
			rs = statement.executeQuery();
			
			TextBuilder html = new TextBuilder();
			html.append("<center>Top 20 PVP</center>");
			html.append("<img src=L2UI.SquareWhite width=700 height=1>");
			html.append("<table width=700 bgcolor=CCCCCC>");
			html.append("<tr>");
			html.append("<td width=350>Nick</td>");
			html.append("<td width=100>Sex</td>");
			html.append("<td width=200>Time in Game</td>");
			html.append("<td width=100>PK</td>");
			html.append("<td width=100><font color=00CC00>PVP</font></td>");
			html.append("<td width=200>Status</td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("<img src=L2UI.SquareWhite width=700 height=1>");
			html.append("<table width=700>");
			while (rs.next())
			{
				tp = new CBStatMan();
				tp.PlayerId = rs.getInt("charId");
				tp.ChName = rs.getString("char_name");
				tp.ChSex = rs.getInt("sex");
				tp.ChGameTime = rs.getInt("onlinetime");
				tp.ChPk = rs.getInt("pkkills");
				tp.ChPvP = rs.getInt("pvpkills");
				tp.ChOnOff = rs.getInt("online");
				String OnOff;
				String color;
				String sex;
				sex = tp.ChSex == 1 ? "F" : "M";
				if (tp.ChOnOff == 1)
				{
					OnOff = "Online";
					color = "00CC00";
				}
				else
				{
					OnOff = "Offline";
					color = "D70000";
				}
				html.append("<tr>");
				html.append("<td width=350>" + tp.ChName + "</td>");
				html.append("<td width=100>" + sex + "</td>");
				html.append("<td width=200>" + OnlineTime(tp.ChGameTime) + "</td>");
				html.append("<td width=100>" + tp.ChPk + "</td>");
				html.append("<td width=100><font color=00CC00>" + tp.ChPvP + "</font></td>");
				html.append("<td width=200><font color=" + color + ">" + OnOff + "</font></td>");
				html.append("</tr>");
			}
			html.append("</table>");
			
			String content = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/index.htm");
			content = content.replace("%stat%", html.toString());
			separateAndSend(content, player);
			
			statement.close();
			rs.close();
			
			return;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
	}
	
	private void showPK(L2PcInstance player)
	{
		
		CBStatMan tp;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM characters WHERE accesslevel = '0' ORDER BY pkkills DESC LIMIT 20;");
			rs = statement.executeQuery();
			
			TextBuilder html = new TextBuilder();
			html.append("<center>TOP 20 PK</center>");
			html.append("<img src=L2UI.SquareWhite width=700 height=1>");
			html.append("<table width=700 bgcolor=CCCCCC>");
			html.append("<tr>");
			html.append("<td width=350>Nick</td>");
			html.append("<td width=100>Sex</td>");
			html.append("<td width=200>Time in Game</td>");
			html.append("<td width=100><font color=00CC00>PK</font></td>");
			html.append("<td width=100>PVP</td>");
			html.append("<td width=200>Status</td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("<img src=L2UI.SquareWhite width=700 height=1>");
			html.append("<table width=700>");
			while (rs.next())
			{
				tp = new CBStatMan();
				tp.PlayerId = rs.getInt("charId");
				tp.ChName = rs.getString("char_name");
				tp.ChSex = rs.getInt("sex");
				tp.ChGameTime = rs.getInt("onlinetime");
				tp.ChPk = rs.getInt("pkkills");
				tp.ChPvP = rs.getInt("pvpkills");
				tp.ChOnOff = rs.getInt("online");
				String OnOff;
				String color;
				String sex;
				sex = tp.ChSex == 1 ? "F" : "M";
				if (tp.ChOnOff == 1)
				{
					OnOff = "Online";
					color = "00CC00";
				}
				else
				{
					OnOff = "Offline";
					color = "D70000";
				}
				html.append("<tr>");
				html.append("<td width=350>" + tp.ChName + "</td>");
				html.append("<td width=100>" + sex + "</td>");
				html.append("<td width=200>" + OnlineTime(tp.ChGameTime) + "</td>");
				html.append("<td width=100><font color=00CC00>" + tp.ChPk + "</font></td>");
				html.append("<td width=100>" + tp.ChPvP + "</td>");
				html.append("<td width=200><font color=" + color + ">" + OnOff + "</font></td>");
				html.append("</tr>");
			}
			html.append("</table>");
			
			String content = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/index.htm");
			content = content.replace("%stat%", html.toString());
			separateAndSend(content, player);
			
			statement.close();
			rs.close();
			
			return;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
	}
	private void showPcBang(L2PcInstance player)
	{
		
		CBStatMan tp;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM characters WHERE accesslevel = '0' ORDER BY pc_point DESC LIMIT 20;");
			rs = statement.executeQuery();
			
			TextBuilder html = new TextBuilder();
			html.append("<center>TOP 20 PcBang Point</center>");
			html.append("<img src=L2UI.SquareWhite width=700 height=1>");
			html.append("<table width=700 bgcolor=CCCCCC>");
			html.append("<tr>");
			html.append("<td width=350>Nick</td>");
			html.append("<td width=100>Sex</td>");
			html.append("<td width=200>Time in Game</td>");
			html.append("<td width=100><font color=00CC00>PcBang Point</font></td>");
			html.append("<td width=200>Status</td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("<img src=L2UI.SquareWhite width=700 height=1>");
			html.append("<table width=700>");
			while (rs.next())
			{
				tp = new CBStatMan();
				tp.PlayerId = rs.getInt("charId");
				tp.ChName = rs.getString("char_name");
				tp.ChSex = rs.getInt("sex");
				tp.ChGameTime = rs.getInt("onlinetime");
				tp.ChPcBangPoint = rs.getInt("pc_point");
				tp.ChOnOff = rs.getInt("online");
				String OnOff;
				String color;
				String sex;
				sex = tp.ChSex == 1 ? "F" : "M";
				if (tp.ChOnOff == 1)
				{
					OnOff = "Online";
					color = "00CC00";
				}
				else
				{
					OnOff = "Offline";
					color = "D70000";
				}
				html.append("<tr>");
				html.append("<td width=350>" + tp.ChName + "</td>");
				html.append("<td width=100>" + sex + "</td>");
				html.append("<td width=200>" + OnlineTime(tp.ChGameTime) + "</td>");
				html.append("<td width=100><font color=00CC00>" + tp.ChPcBangPoint + "</font></td>");
				html.append("<td width=200><font color=" + color + ">" + OnOff + "</font></td>");
				html.append("</tr>");
			}
			html.append("</table>");
			
			String content = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/index.htm");
			content = content.replace("%stat%", html.toString());
			separateAndSend(content, player);
			
			statement.close();
			rs.close();
			
			return;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
	}

	private void showClan(L2PcInstance player)
	{
		
		CBStatMan tp;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT clan_name,clan_level,reputation_score,ally_name FROM clan_data WHERE clan_level>0 order by clan_level desc limit 20;");
			rs = statement.executeQuery();
			
			TextBuilder html = new TextBuilder();
			html.append("<center>Top 20 Clan</center>");
			html.append("<img src=L2UI.SquareWhite width=700 height=1>");
			html.append("<table width=700 bgcolor=CCCCCC>");
			html.append("<tr>");
			html.append("<td width=350>Clan Name</td>");
			html.append("<td width=100>Clan Ally</td>");
			html.append("<td width=100>Clan Reputation</td>");
			html.append("<td width=200>Clan Level</td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("<img src=L2UI.SquareWhite width=700 height=1>");
			html.append("<table width=700>");
			while (rs.next())
			{
				tp = new CBStatMan();
				tp.ChClanName = rs.getString("clan_name");
				tp.ChClanAlly = rs.getString("ally_name");
				tp.ChClanRep = rs.getInt("reputation_score");
				tp.ChClanLevel = rs.getInt("clan_level");

				html.append("<tr>");
				html.append("<td width=350>" + tp.ChClanName + "</td>");
				html.append("<td width=100>" + tp.ChClanAlly + "</td>");
				html.append("<td width=100>" + tp.ChClanRep + "</td>");
				html.append("<td width=200>" + tp.ChClanLevel + "</td>");
				html.append("</tr>");
			}
			html.append("</table>");
			
			String content = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/index.htm");
			content = content.replace("%stat%", html.toString());
			separateAndSend(content, player);
			
			statement.close();
			rs.close();
			
			return;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
	}
	String OnlineTime(int time)
	{
		long onlinetimeH;
		int onlinetimeM;
		if (time / 60 / 60 - 0.5 <= 0)
		{
			onlinetimeH = 0;
		}
		else
		{
			onlinetimeH = Math.round((time / 60 / 60) - 0.5);
		}
		onlinetimeM = Math.round(((time / 60 / 60) - onlinetimeH) * 60);
		return "" + onlinetimeH + " h. " + onlinetimeM + " m.";
	}
	
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance player)
	{
		
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final StateBBSManager _instance = new StateBBSManager();
	}
}
