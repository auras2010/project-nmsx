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
package l2.brick.gameserver.model.zone.type;

import l2.brick.Config;
import l2.brick.gameserver.ThreadPoolManager;
import l2.brick.gameserver.model.actor.L2Character;
import l2.brick.gameserver.model.actor.instance.L2PcInstance;
import l2.brick.gameserver.model.zone.L2ZoneType;
import l2.brick.gameserver.network.SystemMessageId;

/**
 * A jail zone
 * @author durgus
 */
public class L2JailZone extends L2ZoneType
{
	public L2JailZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			character.setInsideZone(L2Character.ZONE_JAIL, true);
			if (Config.JAIL_IS_PVP)
			{
				character.setInsideZone(L2Character.ZONE_PVP, true);
				character.sendPacket(SystemMessageId.ENTERED_COMBAT_ZONE);
			}
			if (Config.JAIL_DISABLE_TRANSACTION)
			{
				character.setInsideZone(L2Character.ZONE_NOSTORE, true);
			}
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			character.setInsideZone(L2Character.ZONE_JAIL, false);
			if (Config.JAIL_IS_PVP)
			{
				character.setInsideZone(L2Character.ZONE_PVP, false);
				character.sendPacket(SystemMessageId.LEFT_COMBAT_ZONE);
			}
			if (((L2PcInstance) character).isInJail())
			{
				// when a player wants to exit jail even if he is still jailed, teleport him back to jail
				ThreadPoolManager.getInstance().scheduleGeneral(new BackToJail(character), 2000);
				character.sendMessage("You cannot cheat your way out of here. You must wait until your jail time is over.");
			}
			if (Config.JAIL_DISABLE_TRANSACTION)
			{
				character.setInsideZone(L2Character.ZONE_NOSTORE, false);
			}
		}
	}
	
	@Override
	public void onDieInside(L2Character character)
	{
	}
	
	@Override
	public void onReviveInside(L2Character character)
	{
	}
	
	static class BackToJail implements Runnable
	{
		private final L2PcInstance _activeChar;
		
		BackToJail(L2Character character)
		{
			_activeChar = (L2PcInstance) character;
		}
		
		@Override
		public void run()
		{
			_activeChar.teleToLocation(-114356, -249645, -2984); // Jail
		}
	}
}
