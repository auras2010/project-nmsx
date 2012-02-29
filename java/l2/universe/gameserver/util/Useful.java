package l2.universe.gameserver.util;

import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;

/**
 * A class with useful actions that can be used in many scripts/commands/etc.
 * @author l2open-team
 */
public class Useful
{
    public static boolean kickPlayer(L2PcInstance plyr)
    {
        if (plyr != null)
        {
            if (plyr.isOfflineTrade())
                plyr.deleteMe();
            else
                plyr.logout();
        }
        else 
        	return false;
        return true;
    }

    public static boolean kickPlayerName(String player)
    {
        final L2PcInstance plyr = L2World.getInstance().getPlayer(player);
        if (plyr == null) 
        	return false;
        
        return kickPlayer(plyr);
    }
}