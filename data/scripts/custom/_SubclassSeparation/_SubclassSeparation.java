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

/*
 *
 * @author: Sephiroth
 */

package custom._SubclassSeparation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.logging.Logger;

import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.SubclassSeparationHandler;
import l2.universe.gameserver.datatables.CharTemplateTable;
import l2.universe.gameserver.model.SubclassSeparation;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.base.SubClass;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;

public class _SubclassSeparation extends Quest
{
	private int subclassSeparationNpc = 100101;
	private static final Logger _log = Logger.getLogger(_SubclassSeparation.class.getName());

	public _SubclassSeparation(int id, String name, String descr)
	{
		super(id, name, descr);
		addStartNpc(subclassSeparationNpc);
		addTalkId(subclassSeparationNpc);
		addFirstTalkId(subclassSeparationNpc);
	}

	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmlText = "";
		QuestState st = player.getQuestState(getName());
		if (st == null)
			st = newQuestState(player);

		htmlText = "menu.htm";
		return htmlText;
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmlText = "";
		QuestState st = player.getQuestState(getName());
		if (st == null)
			st = newQuestState(player);
		htmlText = "menu.htm";
		return htmlText;
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmlText = "";
		QuestState st = player.getQuestState(getName());
		if (st == null)
			st = newQuestState(player);

		if (event.equals("info"))
			htmlText = "info.htm";
		else if (event.equals("menu"))
			htmlText = "menu.htm";
		else if (event.equals("info2"))
			htmlText = "info2.htm";
		else if (event.equals("start"))
		{
			player.cancelSubclassSeparation();
			htmlText = getStartSS(player);
		}
		else if (event.equals("cancel"))
		{
			player.cancelSubclassSeparation();
			htmlText = "canceled.htm";
		}
		else if (event.contains("subclass"))
		{
			String[] tempText = event.split(" ");
			player.setSubclassSeparationIndex(Integer.valueOf(tempText[1]));
			htmlText = "define_account.htm";
		}
		else if (event.startsWith("sex"))
		{
			String[] strSex = event.split(" ");
			player.setSubclassSeparationSex(Integer.valueOf(strSex[1]));
			htmlText = showReadyHtml(player);
		}
		else if (event.equals("all_set"))
		{
			SubclassSeparation _ss = new SubclassSeparation(player.getObjectId(),
											player.getSubclassSeparationIndex(),
											player.getAccountName(),
											player.getSubclassSeparationAccount(),
											player.getSubclassSeparationSex());
			SubclassSeparationHandler.getInstance().addFutureSeparation(_ss, player);
			htmlText = "logout_to_complete.htm";
		}
		else if (event.contains("account_name"))
		{
			int charCount = 0;
			int account_exists = 0;
			int charLevel = 0;
			boolean error_ocurred = false;

			if (event.equals("account_name"))
			{
				htmlText = "no_account_informed.htm";
			}
			else
			{
				String[] account_info = event.split(" ");
				String e_query = "SELECT count(login) as accounts, count(char_name) as chars FROM characters right join accounts on characters.account_name = accounts.login WHERE login ='" + account_info[1] + "'";
				Connection con = null;
				try
				{
					con = L2DatabaseFactory.getInstance().getConnection();
					PreparedStatement pst = con.prepareStatement(e_query);
					ResultSet rset = pst.executeQuery();
					while (rset.next())
					{
						account_exists = rset.getInt("accounts");
						charCount = rset.getInt("chars");
					}
					pst.close();
					rset.close();
				}
				catch(Exception e)
				{
					error_ocurred = true;
					_log.warning("Couldn't retrive account information for account +" + e.toString());
				}
				finally
				{
					L2DatabaseFactory.close(con);
				}

				if (error_ocurred)
					return "server_error.htm";
				
				if (account_exists == 0 && charCount == 0)
					htmlText = "account_invalid.htm";

				else if ((charCount == 0 || charCount > 1) && account_exists == 1)
					htmlText = "invalid_chars.htm";
				else if (charCount ==1 && account_exists == 1)
				{
					e_query = "SELECT level FROM characters WHERE account_name ='" + account_info[1] + "'";
					con = null;
					try
					{
						con = L2DatabaseFactory.getInstance().getConnection();
						PreparedStatement pst = con.prepareStatement(e_query);
						ResultSet rset = pst.executeQuery();
						while (rset.next())
						{
							charLevel = rset.getInt("level");
						}
						pst.close();
						rset.close();
					}
					catch(Exception e)
					{
						error_ocurred = true;
						_log.warning("Couldn't retrive account information for account: " + e.toString());
					}
					finally
					{
						L2DatabaseFactory.close(con);
					}

					if (charLevel != 1)
					{
						player.cancelSubclassSeparation();
						htmlText = "invalid_char_level.htm";
					}
					else
					{
						player.setSubclassSeparationAccount(account_info[1]);
						htmlText = "select_sex.htm";
					}
				}
			}
		}
		return htmlText;
	}

	private String getStartSS(L2PcInstance player)
	{
		String html = "<html><body>";
		if (player.isSubClassActive())
		{
			html += "Subclass Separator:<br>Yoy must be at your main class to begin.";
			html += "</body></html>";
			return html;
		}
		else if (player.getTotalSubClasses() == 0)
		{
			html += "Subclass Separator:<br>Yoy have no subclasses. We can't continue.";
			html += "</body></html>";
			return html;
		}
		else if (player.getLevel() < 75)
		{
			html += "Subclass Separator:<br>You must be at level 75 or higher to continue.";
			html += "</body></html>";
			return html;
		}
		else if (player.getTotalSubClasses() >= 1 && player.getLevel() >= 75)
		{
			if (SubclassSeparationHandler.getInstance().canDoSeparation(player.getAccountName()))
			{
				String className = "";
				html += "Subclass Separator:<br>Select a subclass:<br>";
				html += "<center>";
				for (SubClass subClass : player.getSubClasses().values())
      	            {
					if (subClass.getClassIndex() != 0)
					{
						className = CharTemplateTable.getInstance().getClassNameById(subClass.getClassId());
						html += "<a action=\"bypass Quest _SubclassSeparation subclass " + subClass.getClassIndex() + "\">" + className + "</a><br>";
					}
				}
				html += "</center>";
				html += "<br><a action=\"bypass Quest _SubclassSeparation cancel\">I've changed my mind!</a>";
				html += "</body></html>";
			}
			else
				html = "wait.htm";
		}
		
		return html;
	}
	private String showReadyHtml(L2PcInstance player)
	{
		int subclassId = 0;
		subclassId = player.getSubClasses().get(player.getSubclassSeparationIndex()).getClassId();

		String html = "<html><body>Subclass Separator:<br>";
			html += "Here is the information you provided, please confirm them before we start the separation process.";
			html += "<br><center>";
			html += "Subclass Chosen: <font color=\"LEVEL\">" + CharTemplateTable.getInstance().getClassNameById(subclassId) + "</font><br>";
			html += "Account name   : <font color=\"LEVEL\">" + player.getSubclassSeparationAccount() + "</font><br>";
			html += "Sex of new char: <font color=\"LEVEL\">";
			if (player.getSubclassSeparationSex() == 1)
				html += "female";
			else
				html += "male";

			html += "</font></center>";
			html += "<br>Do you confirm this information?<br>";
			html += "<a action=\"bypass -h Quest _SubclassSeparation all_set\">Yes, let me proceed</a><br>";
			html += "<a action=\"bypass -h Quest _SubclassSeparation cancel\">I've changed my mind!</a>";
			html += "</body></html>";
		return html;
	}

	public static void main(String[] args)
	{
		new _SubclassSeparation(-1, "_SubclassSeparation", "custom");
	}
}
