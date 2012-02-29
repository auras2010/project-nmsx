package l2.universe.gameserver.network;

import javolution.util.FastMap;
import l2.universe.ExternalConfig;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.NetPing;
import l2.universe.gameserver.util.Useful;
/**
 * @author l2open-team
 */
public class Pinger extends Thread
{
    private static Pinger instance;
    private FastMap<Integer, Integer> pingTimes = new FastMap<Integer, Integer>();
    
    public static Pinger getInstance()
    {
        if (instance == null) instance = new Pinger();
        	return instance;
    }
    
    private Pinger()
    {
    }
        
    public int getPingTimes(int objId)
    {
        Integer times = pingTimes.get(objId);
        if (times == null)
        	return 0;
        else
        	return times;
    }

    public void answerPing(int objId)
    {
        if (!ExternalConfig.PING_ENABLED)
        	return;
        
        synchronized (pingTimes)
        {
            pingTimes.remove(objId);
        }
    }
    
    @Override
    public void run()
    {
        for (; ;)
        {
            try
            {
                Thread.sleep(ExternalConfig.PING_INTERVAL);
            }
            catch (final InterruptedException e){}
            
            try
            {
                FastMap<Integer, Integer> newPingTimes = new FastMap<Integer, Integer>();
                synchronized (pingTimes)
                {
                    for (final L2PcInstance player : L2World.getInstance().getAllPlayers().values())
                    {
                        if (player == null || player.isOfflineTrade() || player.isInStoreMode() || player.isInCraftMode())
                        	continue;
                        int oid = player.getObjectId();
                        final int times = getPingTimes(oid);
                        if (times > ExternalConfig.PING_IGNORED_REQEST_LIMIT - 1)
                        	Useful.kickPlayer(player);
                        else
                        {
                            newPingTimes.put(oid, times + 1);
                            //System.out.println("::::::::::::"+times);
                        }
                        player.sendPacket(new NetPing(player.getObjectId()));
                    }
                }
                pingTimes = newPingTimes;
            } 
            catch (Exception ignored) {};
        }
    }
}