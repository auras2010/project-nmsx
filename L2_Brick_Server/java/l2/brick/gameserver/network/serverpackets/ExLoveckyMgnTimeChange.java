package l2.brick.gameserver.network.serverpackets;

public class ExLoveckyMgnTimeChange extends L2GameServerPacket
{
	private int _cas;
	private boolean _aktivni;
	
	public ExLoveckyMgnTimeChange(int cas, boolean aktivni)
	{
		_cas = cas;
		_aktivni = aktivni;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0xE1);
		writeC(_aktivni ? 1 : 0);
		writeD(_cas);
	}
	
	@Override
	public String getType()
	{
		return "[S] FE:E1 ExLoveckyMgnTimeChange".intern();
	}
}