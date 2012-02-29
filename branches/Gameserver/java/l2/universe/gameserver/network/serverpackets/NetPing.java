package l2.universe.gameserver.network.serverpackets;

/**
 * @author l2open-team
 */
public class NetPing  extends L2GameServerPacket
{
    private int _kID;

    public NetPing(int kID)
    {
        _kID = kID;
    }

    @Override
    protected void writeImpl()
    {
        writeC(0xd9);
		writeD(_kID);
    }
    
    @Override
    public String getType() 
   	{
        return null;
    }
}