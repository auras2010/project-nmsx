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

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import l2.universe.Config;
import l2.universe.gameserver.SevenSignsFestival;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.L2Party;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ActionFailed;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.security.MultiBoxProtection;
import l2.universe.gameserver.taskmanager.AttackStanceTaskManager;

/**
 * This class ...
 *
 * @version $Revision: 1.9.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class Logout extends L2GameClientPacket
{
	private static final String _C__09_LOGOUT = "[C] 09 Logout";
	private static final Logger _log = Logger.getLogger(Logout.class.getName());
	protected static final Logger _logAccounting = Logger.getLogger("accounting");

	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		// Dont allow leaving if player is fighting
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
			return;

		if (player.getActiveEnchantItem() != null || player.getActiveEnchantAttrItem() != null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (player.isLocked())
		{
			_log.warning("Player " + player.getName() + " tried to logout during class change.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
        }

		if (AttackStanceTaskManager.getInstance().getAttackStanceTask(player) && 
				!(player.isGM() && Config.GM_RESTART_FIGHTING))
		{
			if (Config.DEBUG) _log.fine("Player " + player.getName() + " tried to logout while fighting");

			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANT_LOGOUT_WHILE_FIGHTING));
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (player.atEvent)
		{
			player.sendMessage("A superior power doesn't allow you to leave the event");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		// Prevent player from logging out if they are a festival participant
		// and it is in progress, otherwise notify party members that the player
		// is not longer a participant.
		if (player.isFestivalParticipant())
		{
			if (SevenSignsFestival.getInstance().isFestivalInitialized())
			{
				player.sendMessage("You cannot log out while you are a participant in a festival.");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final L2Party playerParty = player.getParty();
			if (playerParty != null)
				player.getParty().broadcastToPartyMembers(SystemMessage.sendString(player.getName() + " has been removed from the upcoming festival."));
		}
		
		// Synerge - Unequip instance items when leaving an instance
		if (player.getInstanceId() > 0)
		{
			for (L2ItemInstance i : player.getInventory().getItems())
			{
				if (i.isInstanceItem() && i.isEquipped())
					player.useEquippableItem(i, true);
			}
		}

		// Synerge - Also check that he can set a Offline Store on multiBox
		if ((player.isInStoreMode() && Config.OFFLINE_TRADE_ENABLE)	
				|| (player.isInCraftMode() && Config.OFFLINE_CRAFT_ENABLE))
		{
			if (MultiBoxProtection.getInstance().checkMultiBoxOfflineStore(getClient(), MultiBoxProtection.getInstance().getLocalIp(player.getAccountName())))
			{
				player.getInventory().updateDatabase();
				player.closeNetConnection(true);
				if (player.getOfflineStartTime() == 0)
					player.setOfflineStartTime(System.currentTimeMillis());
				return;
			}
		}
		
		// Remove player from Boss Zone
		player.removeFromBossZone();
		
		LogRecord record = new LogRecord(Level.INFO, "Disconnected");
		record.setParameters(new Object[]{this.getClient()});
		_logAccounting.log(record);

		player.logout();
	}

	@Override
	public String getType()
	{
		return _C__09_LOGOUT;
	}
}