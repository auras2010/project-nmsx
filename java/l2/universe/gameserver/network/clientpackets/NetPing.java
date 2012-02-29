package l2.universe.gameserver.network.clientpackets;

import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.Pinger;

/**
 * @author l2open-team
 */
public class NetPing extends L2GameClientPacket 
{
    int kID;
    int ping;
    int mtu;

    @Override
    protected void readImpl() 
    {
        kID = readD();
        ping = readD();
        mtu = readD();
    }

    @Override
    protected void runImpl() 
    {
    	final L2PcInstance activeChar = getClient().getActiveChar();
        if(activeChar == null)
        	return;
        
        Pinger.getInstance().answerPing(activeChar.getObjectId());
        //System.out.println("PING:"+ping+":MTU:"+mtu);
    }

    @Override
    public String getType() 
    {
        return "[C] B1 NetPing";
    }
}
