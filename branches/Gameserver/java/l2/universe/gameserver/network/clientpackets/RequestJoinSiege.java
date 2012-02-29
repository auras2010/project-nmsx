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

import l2.universe.Config;
import l2.universe.gameserver.instancemanager.CHSiegeManager;
import l2.universe.gameserver.instancemanager.CastleManager;
import l2.universe.gameserver.model.L2Clan;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.Castle;
import l2.universe.gameserver.model.entity.clanhall.SiegableHall;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SiegeAttackerList;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

/**
 *
 * @author KenM
 */
public final class RequestJoinSiege extends L2GameClientPacket
{
	private static final String _C__A4_RequestJoinSiege = "[C] a4 RequestJoinSiege";
	//private static Logger _log = Logger.getLogger(RequestJoinSiege.class.getName());
	
	private int _castleId;
	private int _isAttacker;
	private int _isJoining;
	
	@Override
	protected void readImpl()
	{
		_castleId = readD();
		_isAttacker = readD();
		_isJoining = readD();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)	return;
		
		if ((activeChar.getClanPrivileges() & L2Clan.CP_CS_MANAGE_SIEGE) != L2Clan.CP_CS_MANAGE_SIEGE)
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT));
			return;
		}
		
		L2Clan clan = activeChar.getClan();
		if (clan == null) return;
		
		Castle castle = CastleManager.getInstance().getCastleById(_castleId);
		if (castle != null)
		{
			if (_isJoining == 1)
			{
				if (System.currentTimeMillis() < clan.getDissolvingExpiryTime())
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.CANT_PARTICIPATE_IN_SIEGE_WHILE_DISSOLUTION_IN_PROGRESS));
					return;
				}
				if (_isAttacker == 1)
					castle.getSiege().registerAttacker(activeChar);
				else
					castle.getSiege().registerDefender(activeChar);
			}
			else
				castle.getSiege().removeSiegeClan(activeChar);
			castle.getSiege().listRegisterClan(activeChar);
			return;
		}
		
		SiegableHall hall = CHSiegeManager.getInstance().getSiegableHall(_castleId);
		if(hall != null)
		{
			if(!hall.isRegistering())
			{
				sendPacket(new SystemMessage(SystemMessageId.NOT_SIEGE_REGISTRATION_TIME2));
				return;
			}
			if (_isJoining == 1)
			{
				if (System.currentTimeMillis() < clan.getDissolvingExpiryTime())
				{
					sendPacket(new SystemMessage(SystemMessageId.CANT_PARTICIPATE_IN_SIEGE_WHILE_DISSOLUTION_IN_PROGRESS));
				}
				else if(activeChar.getClan().getLevel() < Config.CHS_CLAN_MINLEVEL)
				{
					activeChar.sendMessage("Only clans level 4 or above may participate in the siege.");
				}
				else if(clan.getHasCastle() > 0 || clan.getHasFort() > 0 || clan.getHasHideout() > 0)
				{
					sendPacket(new SystemMessage(SystemMessageId.CLAN_THAT_OWNS_CASTLE_CANNOT_PARTICIPATE_OTHER_SIEGE));
				}
				else if(hall.getOwnerId() == clan.getClanId())
				{
					sendPacket(new SystemMessage(SystemMessageId.CLAN_THAT_OWNS_CASTLE_IS_AUTOMATICALLY_REGISTERED_DEFENDING));
				}
				else if(hall.isRegistered(clan))
				{
					sendPacket(new SystemMessage(SystemMessageId.ALREADY_REQUESTED_SIEGE_BATTLE));
				}
				else if(CHSiegeManager.getInstance().isClanParticipating(clan))
				{
					sendPacket(new SystemMessage(SystemMessageId.APPLICATION_DENIED_BECAUSE_ALREADY_SUBMITTED_A_REQUEST_FOR_ANOTHER_SIEGE_BATTLE));
				}
				else if(hall.getAttackers().size() >= Config.CHS_MAX_ATTACKERS)
				{
					sendPacket(new SystemMessage(SystemMessageId.ATTACKER_SIDE_FULL));
				}
				else
				{
					hall.getAttackers().add(clan);
					activeChar.sendPacket(new SiegeAttackerList(hall));
				}
			}
 			else
 				hall.getAttackers().remove(clan);
		}
	}
	
	@Override
	public String getType()
	{
		return _C__A4_RequestJoinSiege;
	}
}