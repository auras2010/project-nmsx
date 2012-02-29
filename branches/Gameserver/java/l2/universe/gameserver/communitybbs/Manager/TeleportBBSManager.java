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
import java.util.StringTokenizer;
//import java.util.logging.Logger;

import javolution.text.TextBuilder;

import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.cache.HtmCache;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;
import l2.universe.gameserver.network.serverpackets.ShowBoard;

public class TeleportBBSManager extends BaseBBSManager
{	
	//private static Logger _log = Logger.getLogger(TeleportBBSManager.class.getName());
	
	public class CBteleport
	{
		public int TpId = 0;	    // Teport location ID
		public String TpName = "";	// Location name
		public int PlayerId = 0;	// charID
		public int xC = 0;			// Location coords X
		public int yC = 0;			// Location coords Y
		public int zC = 0;			// Location coords Z
	}

	private static TeleportBBSManager _Instance = null;

	public static TeleportBBSManager getInstance()
	{
		if(_Instance == null)
			_Instance = new TeleportBBSManager();
		return _Instance;
	}
	
	public String points[][];

	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
		if(command.equals("_bbsteleport;"))
		{
			showTp(activeChar);
		}
		else if(command.startsWith("_bbsteleport;delete;"))
		{
			final StringTokenizer stDell = new StringTokenizer(command, ";");
			stDell.nextToken();
			stDell.nextToken();
			
			final int TpNameDell = Integer.parseInt(stDell.nextToken());
	        delTp(activeChar, TpNameDell);
			showTp(activeChar);
		}
		else if(command.startsWith("_bbsteleport;save;"))
		{
			final StringTokenizer stAdd = new StringTokenizer(command, ";");
			stAdd.nextToken();
			stAdd.nextToken();
			
			final String TpNameAdd = stAdd.nextToken();
	        AddTp(activeChar, TpNameAdd);
			showTp(activeChar);
		}
        else if(command.startsWith("_bbsteleport;teleport;"))
		{
        	final StringTokenizer stGoTp = new StringTokenizer(command, " ");
			stGoTp.nextToken();
			
			final int xTp = Integer.parseInt(stGoTp.nextToken());
			final int yTp = Integer.parseInt(stGoTp.nextToken());
			final int zTp = Integer.parseInt(stGoTp.nextToken());
			final int priceTp = Integer.parseInt(stGoTp.nextToken());
	        goTp(activeChar, xTp, yTp, zTp, priceTp);
			showTp(activeChar);
		}
		else
		{
			final ShowBoard sb = new ShowBoard("<html><body><br><br><center>the command: " + command
					+ " is not implemented yet</center><br><br></body></html>", "101");
			activeChar.sendPacket(sb);
			activeChar.sendPacket(new ShowBoard(null, "102"));
			activeChar.sendPacket(new ShowBoard(null, "103"));
		}
	}
	
	private void goTp(L2PcInstance activeChar, int xTp, int yTp, int zTp, int priceTp)
	{
        if(activeChar.isInsideZone(L2Character.ZONE_PVP) || activeChar.isInsideZone((byte)0) || activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isInJail() || activeChar.isFlying() || activeChar.getKarma() > 0 || activeChar.isInDuel())
        {
            activeChar.sendMessage("You can't be teleported now");
			return;
        } 
        
		if (priceTp > 0 && activeChar.getAdena() < priceTp)
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
			return;
		}
		else if(activeChar.isInsideZone(L2Character.ZONE_PEACE))
        {
            if (priceTp > 0)
				activeChar.reduceAdena("Teleport", priceTp, activeChar, true);

			activeChar.teleToLocation(xTp,yTp,zTp);
        }
	}
	
	private void showTp(L2PcInstance activeChar)
	{
		CBteleport tp;
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement st = con.prepareStatement("SELECT * FROM comteleport WHERE charId=?;");
			st.setLong(1, activeChar.getObjectId());
			ResultSet rs = st.executeQuery();
			TextBuilder html = new TextBuilder();
			html.append("<table width=220>");
			while(rs.next())
			{						
				tp = new CBteleport();
				tp.TpId = rs.getInt("TpId");
				tp.TpName = rs.getString("name");
				tp.PlayerId = rs.getInt("charId");
				tp.xC = rs.getInt("xPos");
				tp.yC = rs.getInt("yPos");
				tp.zC = rs.getInt("zPos");
                html.append("<tr>");
                html.append("<td>");
                html.append("<button value=\""+ tp.TpName +"\" action=\"bypass -h _bbsteleport;teleport; " + tp.xC + " " + tp.yC + " " + tp.zC + " " + 100000 + "\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
                html.append("</td>");
                html.append("<td>");
                html.append("<button value=\"Delete\" action=\"bypass -h _bbsteleport;delete;" + tp.TpId + "\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
                html.append("</td>");
                html.append("</tr>");
			}
			html.append("</table>");

			String content = HtmCache.getInstance().getHtmForce(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/50.htm");
			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			adminReply.setHtml(content);
			adminReply.replace("%tp%", html.toString());
			separateAndSend(adminReply.getHtm(), activeChar);
			
			rs.close();
			st.close();
		}
		catch (Exception e)	{}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	private void delTp(L2PcInstance activeChar, int TpNameDell)
	{
		Connection conDel = null;
		try
		{
			conDel = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement stDel = conDel.prepareStatement("DELETE FROM comteleport WHERE charId=? AND TpId=?;");
			stDel.setInt(1, activeChar.getObjectId());
			stDel.setInt(2, TpNameDell);
			stDel.execute();
			stDel.close();
		}
		catch (Exception e)	{}
		finally
		{
			L2DatabaseFactory.close(conDel);
		}
	}
	
	private void AddTp(L2PcInstance activeChar, String TpNameAdd)
	{
        if(activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isAttackingNow())
        {
            activeChar.sendMessage("To keep a bookmark in your condition it is impossible");
            return;
        }

        if(activeChar.isInCombat())
        {
            activeChar.sendMessage("To keep a bookmark in a mode of fight it is impossible");
            return;
        }
		
        if(activeChar.isInsideZone((byte)11) || activeChar.isInsideZone((byte)5) || activeChar.isInsideZone((byte)9) || activeChar.isInsideZone((byte)10) || activeChar.isInsideZone((byte)3) || activeChar.isInsideZone((byte)16) || activeChar.isInsideZone((byte)8) || activeChar.isFlying())
        {
            activeChar.sendMessage("You can not save this location");
            return;
        }
        
		if(TpNameAdd.equals("") || TpNameAdd.equals(null))
		{
			activeChar.sendMessage("You have not entered the name of the bookmark");
			return;
		}
		
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
							
			PreparedStatement st = con.prepareStatement("SELECT COUNT(*) FROM comteleport WHERE charId=?;");
			st.setLong(1, activeChar.getObjectId());
			ResultSet rs = st.executeQuery();
			rs.next();
			if (rs.getInt(1) <= 9) 
			{
				PreparedStatement st1 = con.prepareStatement("SELECT COUNT(*) FROM comteleport WHERE charId=? AND name=?;");
				st1.setLong(1, activeChar.getObjectId());
				st1.setString(2, TpNameAdd);
				ResultSet rs1 = st1.executeQuery();
				rs1.next();
				
				if (rs1.getInt(1) == 0) 
				{
					PreparedStatement stAdd = con.prepareStatement("INSERT INTO comteleport (charId,xPos,yPos,zPos,name) VALUES(?,?,?,?,?)");
					stAdd.setInt(1, activeChar.getObjectId());
					stAdd.setInt(2, activeChar.getX());
					stAdd.setInt(3, activeChar.getY());
					stAdd.setInt(4, activeChar.getZ());
					stAdd.setString(5, TpNameAdd);
					stAdd.execute();
				} 
				else 
				{
					PreparedStatement stAdd = con.prepareStatement("UPDATE comteleport SET xPos=?, yPos=?, zPos=? WHERE charId=? AND name=?;");
					stAdd.setInt(1, activeChar.getObjectId());
					stAdd.setInt(2, activeChar.getX());
					stAdd.setInt(3, activeChar.getY());
					stAdd.setInt(4, activeChar.getZ());
					stAdd.setString(5, TpNameAdd);
					stAdd.execute();
				}
				
				st1.close();
				rs1.close();
			} 
			else 
				activeChar.sendMessage("You can not save more than 10 bookmarks");
			
			st.close();
			rs.close();
		}
		catch (Exception e) {}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
	}
}
