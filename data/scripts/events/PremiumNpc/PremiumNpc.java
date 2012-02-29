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

package events.PremiumNpc;

/** @author Ragnarok
 *	L2OpenTeam
 *	@date 16.18 | 10.12.2009
 */

import java.util.Calendar;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.instancemanager.QuestManager;
import l2.universe.L2DatabaseFactory;

public class PremiumNpc extends Quest
{
	private final int PremiumNpcId = 110000;
	private final int ConsumableItemId = 4037;
	private final int Count = 1000;
	private int PremiumService;
	
	public PremiumNpc(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(PremiumNpcId);
		addFirstTalkId(PremiumNpcId);
		addTalkId(PremiumNpcId);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		
		String htmltext = event;
		final QuestState st = player.getQuestState(getName());
		htmltext = event;
		
		if (event.equalsIgnoreCase("getPremium"))
		{
			htmltext = "getpremium.htm";
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("what"))
		{
			htmltext = "aboutpremium.htm";
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("back"))
		{
			htmltext = "start.htm";
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("premium1"))
		{
			getPS(player);
			if (PremiumService == 1)
			{
				htmltext = "ladylike-no.htm";
				return htmltext;
			}
			else if (st.getQuestItemsCount(ConsumableItemId) >= Count * 1)
			{
				st.takeItems(ConsumableItemId, Count * 1);
				addPremiumServices(1, player);
				htmltext = "congratulations1.htm";
				return htmltext;
			}
			else
			{
				htmltext = "sorry.htm";
				return htmltext;
			}
		}
		
		if (event.equalsIgnoreCase("premium2"))
		{
			getPS(player);
			if (PremiumService == 1)
			{
				htmltext = "ladylike-no.htm";
				return htmltext;
			}
			else if (st.getQuestItemsCount(ConsumableItemId) >= Count * 2)
			{
				st.takeItems(ConsumableItemId, Count * 2);
				addPremiumServices(2, player);
				htmltext = "congratulations2.htm";
				return htmltext;
			}
			else
			{
				htmltext = "sorry.htm";
				return htmltext;
			}
		}
		
		if (event.equalsIgnoreCase("premium3"))
		{
			getPS(player);
			if (PremiumService == 1)
			{
				htmltext = "ladylike-no.htm";
				return htmltext;
			}
			else if (st.getQuestItemsCount(ConsumableItemId) >= Count * 3)
			{
				st.takeItems(ConsumableItemId, Count * 3);
				addPremiumServices(3, player);
				htmltext = "congratulations3.htm";
			}
			else
			{
				htmltext = "sorry.htm";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			final Quest q = QuestManager.getInstance().getQuest(getName());
			st = q.newQuestState(player);
		}
		htmltext = "start.htm";
		return htmltext;
	}
	
	public static void main(String[] args)
	{ //Call Constructor
		new PremiumNpc(-1, "PremiumNpc", "events");
	}
	
	//To be Continue...
	private void getPS(L2PcInstance player)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			final PreparedStatement statement = con.prepareStatement("SELECT premium_service FROM account_premium WHERE account_name=?");
			statement.setString(1, player.getAccountName());
			final ResultSet chars = statement.executeQuery();
			PremiumService = chars.getInt("premium_service");
			chars.close();
			statement.close();
		}
		catch (final Exception e)
		{
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	private void addPremiumServices(int Months, L2PcInstance player)
	{
		Connection con = null;
		try
		{
			final Calendar finishtime = Calendar.getInstance();
			finishtime.setTimeInMillis(System.currentTimeMillis());
			finishtime.set(Calendar.SECOND, 0);
			finishtime.add(Calendar.MONTH, Months);
			
			con = L2DatabaseFactory.getInstance().getConnection();
			final PreparedStatement statement = con.prepareStatement("UPDATE account_premium SET premium_service=?,enddate=? WHERE account_name=?");
			statement.setInt(1, 1);
			statement.setLong(2, finishtime.getTimeInMillis());
			statement.setString(3, player.getAccountName());
			statement.execute();
			statement.close();
		}
		catch (final SQLException e)
		{
			_log.info("EventPremiumNpc:  Could not increase data");
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
	}
}
