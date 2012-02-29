package l2.universe.gameserver.model.entity;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import l2.universe.ExternalConfig;
import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.Announcements;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
 
public class AutoVoteRewardHandler
{
    private final String HOPZONE = ExternalConfig.WEBSITE_SERVER_LINK;
    // 60 * 1000(1000milliseconds = 1 second) = 60seconds
    private final int initialCheck = 60 * 1000;
    // 1800 * 1000(1000milliseconds = 1 second) = 1800seconds = 30minutes
    private final int delayForCheck = 1800 * 1000;
    private final int[] itemId = { ExternalConfig.ITEM_ID };
    private final int[] itemCount = { ExternalConfig.ITEM_COUNT };
    private final int[] maxStack = { 5000 };
    private final int votesRequiredForReward = ExternalConfig.REQUIREDVOTES;;
    // do not change
    private int lastVoteCount = 0;
    
    private AutoVoteRewardHandler()
    {
        System.out.println("Vote Reward System Initiated.");
        ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new AutoReward(), initialCheck, delayForCheck);
    }
    
    private class AutoReward implements Runnable
    {
        public void run()
        {
            int votes = getVotes();
            System.out.println("Server Votes: " + votes);
            if (votes != 0 && getLastVoteCount() != 0 && votes >= getLastVoteCount() + votesRequiredForReward)
            {
                Connection con = null;
                try
                {
                    con = L2DatabaseFactory.getInstance().getConnection();
                    PreparedStatement statement = con.prepareStatement("" +
                            "SELECT" +
                            "   c.charId," +
                            "   c.char_name " +
                            "FROM " +
                            "   characters AS c " +
                            "LEFT JOIN " +
                            "   accounts AS a " +
                            "ON " +
                            "   c.account_name = a.login " +
                            "WHERE " +
                            "   c.online > 0 " +
                            "GROUP BY " +
                            "   a.lastIP " +
                            "ORDER BY " +
                            "   c.level " +
                            "DESC");
                    ResultSet rset = statement.executeQuery();
                    L2PcInstance player = null;
                    L2ItemInstance item = null;
                    while (rset.next())
                    {
                        player = L2World.getInstance().getPlayer(rset.getInt("charId"));
                        if (player != null && !player.getClient().isDetached())
                        {
                            for (int i = 0; i < itemId.length; i++)
                            {
                                item = player.getInventory().getItemByItemId(itemId[i]);
                                if (item == null || item.getCount() < maxStack[i])
                                    player.addItem("reward", itemId[i], itemCount[i], player, true);
                            }
                        }
                    }
                    statement.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    L2DatabaseFactory.close(con);
                }
                
                setLastVoteCount(getLastVoteCount() + votesRequiredForReward);
            }
            Announcements.getInstance().announceToAll("Help your server by voting now. We need " + (getLastVoteCount()+votesRequiredForReward) + " votes in HopZone for reward all players. At this moment we have " + votes + " votes.");
            if (getLastVoteCount() == 0)
                setLastVoteCount(votes);
        }
    }
    
    private int getVotes()
    {
        URL url = null;
        InputStreamReader isr = null;
        BufferedReader in = null;
        try
        {
            url = new URL(HOPZONE);
            isr = new InputStreamReader(url.openStream());
            in = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                if (inputLine.contains("moreinfo_total_rank_text"))
                    return Integer.valueOf(inputLine.split(">")[2].replace("</div", ""));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                in.close();
            }
            catch (IOException e)
            {}
            try
            {
                isr.close();
            }
            catch (IOException e)
            {}
        }
        return 0;
    }
    
    private void setLastVoteCount(int voteCount)
    {
        lastVoteCount = voteCount;
    }
    
    private int getLastVoteCount()
    {
        return lastVoteCount;
    }
    
    public static AutoVoteRewardHandler getInstance()
    {
        return SingletonHolder._instance;
    }
    
    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder
    {
        protected static final AutoVoteRewardHandler _instance = new AutoVoteRewardHandler();
    }
}