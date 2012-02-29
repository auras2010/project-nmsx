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
package l2.universe.gameserver.network.clientpackets;

import java.util.Collection;

import l2.universe.Config;
import l2.universe.gameserver.datatables.ClanTable;
import l2.universe.gameserver.model.L2Clan;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ActionFailed;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

public final class RequestStartPledgeWar extends L2GameClientPacket
{
    private static final String _C__4D_REQUESTSTARTPLEDGEWAR = "[C] 4D RequestStartPledgewar";
    //private static Logger _log = Logger.getLogger(RequestStartPledgeWar.class.getName());

    private String _pledgeName;

    @Override
	protected void readImpl()
    {
        _pledgeName = readS();
    }

    @Override
	protected void runImpl()
    {
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		    return;
		
        L2Clan _clan = player.getClan();
        if (_clan == null) 
        	return;

        if (_clan.getLevel() < 3 || _clan.getMembersCount() < Config.ALT_CLAN_MEMBERS_FOR_WAR)
        {
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_WAR_DECLARED_IF_CLAN_LVL3_OR_15_MEMBER);
            player.sendPacket(sm);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            sm = null;
            return;
        }
        else if ((player.getClanPrivileges() & L2Clan.CP_CL_PLEDGE_WAR) != L2Clan.CP_CL_PLEDGE_WAR )
        {
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT));
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        L2Clan clan = ClanTable.getInstance().getClanByName(_pledgeName);
        if (clan == null)
        {
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CLAN_WAR_CANNOT_DECLARED_CLAN_NOT_EXIST));
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        else if (_clan.getAllyId() == clan.getAllyId() && _clan.getAllyId() != 0)
        {
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CLAN_WAR_AGAINST_A_ALLIED_CLAN_NOT_WORK));
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        //else if(clan.getLevel() < 3)
        else if (clan.getLevel() < 3 || clan.getMembersCount() < Config.ALT_CLAN_MEMBERS_FOR_WAR)
        {
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CLAN_WAR_DECLARED_IF_CLAN_LVL3_OR_15_MEMBER));
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        else if (_clan.isAtWarWith(clan.getClanId()))
        {
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.ALREADY_AT_WAR_WITH_S1_WAIT_5_DAYS); //msg id 628
            sm.addString(clan.getName());
            player.sendPacket(sm);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            sm = null;
            return;
        }

        //_log.warning("RequestStartPledgeWar, leader: " + clan.getLeaderName() + " clan: "+ _clan.getName());

        //        L2PcInstance leader = L2World.getInstance().getPlayer(clan.getLeaderName());

        //        if(leader == null)
        //            return;

        //        if(leader != null && leader.isOnline() == 0)
        //        {
        //            player.sendMessage("Clan leader isn't online.");
        //            player.sendPacket(ActionFailed.STATIC_PACKET);
        //            return;
        //        }

        //        if (leader.isProcessingRequest())
        //        {
        //            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessage.S1_IS_BUSY_TRY_LATER);
        //            sm.addString(leader.getName());
        //            player.sendPacket(sm);
        //            return;
        //        }

        //        if (leader.isTransactionInProgress())
        //        {
        //            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessage.S1_IS_BUSY_TRY_LATER);
        //            sm.addString(leader.getName());
        //            player.sendPacket(sm);
        //            return;
        //        }

        //        leader.setTransactionRequester(player);
        //        player.setTransactionRequester(leader);
        //        leader.sendPacket(new StartPledgeWar(_clan.getName(),player.getName()));

        ClanTable.getInstance().storeclanswars(player.getClanId(), clan.getClanId());
        Collection<L2PcInstance> pls = L2World.getInstance().getAllPlayers().values();
        //synchronized (L2World.getInstance().getAllPlayers())
        {
        	for (L2PcInstance cha : pls)
        		if (cha.getClan() == player.getClan() || cha.getClan() == clan)
        			cha.broadcastUserInfo();
        }
    }

    @Override
	public String getType()
    {
        return _C__4D_REQUESTSTARTPLEDGEWAR;
    }
}
