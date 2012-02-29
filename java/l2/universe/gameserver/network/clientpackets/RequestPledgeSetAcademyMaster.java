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

import l2.universe.gameserver.model.L2Clan;
import l2.universe.gameserver.model.L2ClanMember;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

/**
 * Format: (ch) dSS
 * @author  -Wooden-
 *
 */
public final class RequestPledgeSetAcademyMaster extends L2GameClientPacket
{
    private static final String _C__D0_12_REQUESTSETPLEADGEACADEMYMASTER = "[C] D0:12 RequestPledgeSetAcademyMaster";
    private String _currPlayerName;
    private int _set; // 1 set, 0 delete
    private String _targetPlayerName;

    @Override
	protected void readImpl()
    {
        _set = readD();
        _currPlayerName = readS();
        _targetPlayerName = readS();
    }

    @Override
	protected void runImpl()
    {
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		    return;
		
        L2Clan clan = activeChar.getClan();
        if (clan == null) 
        	return;

        if((activeChar.getClanPrivileges() & L2Clan.CP_CL_APPRENTICE) != L2Clan.CP_CL_APPRENTICE)
        {
        	activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_DISMISS_AN_APPRENTICE));
        	return;
        }

        final L2ClanMember currentMember = clan.getClanMember(_currPlayerName);
        final L2ClanMember targetMember = clan.getClanMember(_targetPlayerName);
        if (currentMember == null || targetMember == null) 
        	return;

        L2ClanMember apprenticeMember, sponsorMember;
        if (currentMember.getPledgeType() == L2Clan.SUBUNIT_ACADEMY)
        {
        	apprenticeMember = currentMember;
        	sponsorMember = targetMember;
        }
        else
        {
        	apprenticeMember = targetMember;
        	sponsorMember = currentMember;
        }

        final L2PcInstance apprentice = apprenticeMember.getPlayerInstance();
        final L2PcInstance sponsor = sponsorMember.getPlayerInstance();

        SystemMessage sm = null;
        if(_set == 0)
        {
        	// test: do we get the current sponsor & apprentice from this packet or no?
        	if (apprentice != null)
        		apprentice.setSponsor(0);
        	else // offline
        		apprenticeMember.initApprenticeAndSponsor(0, 0);

        	if (sponsor != null)
        		sponsor.setApprentice(0);
        	else // offline
        		sponsorMember.initApprenticeAndSponsor(0, 0);

        	apprenticeMember.saveApprenticeAndSponsor(0, 0);
        	sponsorMember.saveApprenticeAndSponsor(0, 0);

        	sm = SystemMessage.getSystemMessage(SystemMessageId.S2_CLAN_MEMBER_C1_APPRENTICE_HAS_BEEN_REMOVED);
        }
        else
        {
        	if (apprenticeMember.getSponsor() != 0 || sponsorMember.getApprentice() != 0
        			|| apprenticeMember.getApprentice() != 0 || sponsorMember.getSponsor() != 0)
        	{
        		activeChar.sendMessage("Remove previous connections first.");
        		return;
        	}
        	
        	if (apprentice != null)
        		apprentice.setSponsor(sponsorMember.getObjectId());
        	else // offline
        		apprenticeMember.initApprenticeAndSponsor(0, sponsorMember.getObjectId());

        	if (sponsor != null)
        		sponsor.setApprentice(apprenticeMember.getObjectId());
        	else // offline
        		sponsorMember.initApprenticeAndSponsor(apprenticeMember.getObjectId(), 0);

        	// saving to database even if online, since both must match
        	apprenticeMember.saveApprenticeAndSponsor(0, sponsorMember.getObjectId());
        	sponsorMember.saveApprenticeAndSponsor(apprenticeMember.getObjectId(), 0);

        	sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HAS_BEEN_DESIGNATED_AS_APPRENTICE_OF_CLAN_MEMBER_S1);
        }
        
        sm.addString(sponsorMember.getName());
    	sm.addString(apprenticeMember.getName());
    	
    	if (sponsor != activeChar && sponsor != apprentice)
    		activeChar.sendPacket(sm);
    	if (sponsor != null)
    		sponsor.sendPacket(sm);
    	if (apprentice != null)
    		apprentice.sendPacket(sm);
    }

    @Override
    public String getType()
    {
        return _C__D0_12_REQUESTSETPLEADGEACADEMYMASTER;
    }
}