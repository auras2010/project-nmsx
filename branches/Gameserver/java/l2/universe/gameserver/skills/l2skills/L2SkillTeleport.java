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
package l2.universe.gameserver.skills.l2skills;

import java.util.logging.Level;

import l2.universe.gameserver.datatables.MapRegionTable;
import l2.universe.gameserver.instancemanager.GrandBossManager;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.Location;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.events.TvTEvent;
import l2.universe.gameserver.model.entity.events.TvTRoundEvent;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ActionFailed;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.templates.StatsSet;
import l2.universe.gameserver.templates.skills.L2SkillType;

public class L2SkillTeleport extends L2Skill
{
	private final String _recallType;
	private final Location _loc;

	public L2SkillTeleport(StatsSet set)
	{
		super(set);

		_recallType = set.getString("recallType", "");
		String coords = set.getString("teleCoords", null);
		if (coords != null)
		{
			String[] valuesSplit = coords.split(",");
			_loc = new Location(Integer.parseInt(valuesSplit[0]),
					Integer.parseInt(valuesSplit[1]),
					Integer.parseInt(valuesSplit[2]));
		}
		else
			_loc = null;
	}

	@Override
	public void useSkill(L2Character activeChar, L2Object[] targets)
	{
		if (activeChar instanceof L2PcInstance)
		{
			L2PcInstance activePC = (L2PcInstance) activeChar;

			if (activePC.getQuestState("LastHero") != null)
			{		
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}

			// Thanks nbd
			if (!TvTEvent.onEscapeUse(activePC.getObjectId()))
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}

			if (!TvTRoundEvent.onEscapeUse(((L2PcInstance) activeChar).getObjectId()))
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}

			if (activeChar.isAfraid())
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}

			if (activePC.isCombatFlagEquipped())
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}

			if (activePC.isInOlympiadMode())
			{
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT));
				return;
			}

			if (GrandBossManager.getInstance().getZone(activeChar) != null && !activeChar.isGM())
			{
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION));
				return;
			}

			//Kratei's Cube
			if (((L2PcInstance) activeChar).getIsInKrateisCube())
			{
				activeChar.sendMessage("You may not escape from Kratei's Cube!");
				return;
			}

		}

		try
		{
			for (L2Character target: (L2Character[]) targets)
			{
				L2PcInstance targetChar = null;
				if (target instanceof L2PcInstance)
				{
					targetChar = (L2PcInstance) target;

					// Check to see if the current player target is in a festival.
					if (targetChar.isFestivalParticipant())
					{
						targetChar.sendMessage("You may not use an escape skill in a festival.");
						continue;
					}

					// Check to see if player is in jail
					if (targetChar.isInJail())
					{
						targetChar.sendMessage("You can not escape from jail.");
						continue;
					}

					// Check to see if player is in a duel
					if (targetChar.isInDuel())
					{
						targetChar.sendMessage("You cannot use escape skills during a duel.");
						continue;
					}

					if (targetChar._inEventDM)
					{
						targetChar.sendMessage("You may not use an escape skill in a Event.");
						continue;
					}

					// Check to see if the current player target is in CTF
					if (targetChar._inEventCTF)
					{
						targetChar.sendMessage("You may not use an escape skill in a Event.");
						continue;
					}

					//Kratei's Cube
					if (((L2PcInstance) activeChar).getIsInKrateisCube())
					{
						activeChar.sendMessage("You may not escape from Kratei's Cube!");
						continue;
					}

					if (targetChar != activeChar)
					{
						if (!TvTEvent.onEscapeUse(targetChar.getObjectId()))
							continue;

						if (!TvTRoundEvent.onEscapeUse(targetChar.getObjectId()))
							continue;

						if (targetChar.isInOlympiadMode())
							continue;

						if (GrandBossManager.getInstance().getZone(targetChar) != null)
							continue;

						if (targetChar.isCombatFlagEquipped())
							continue;
						
						if (targetChar.isRooted() || targetChar.isParalyzed())
							continue;
					}
				}
				
				Location loc = null;
				if (getSkillType() == L2SkillType.TELEPORT)
				{
					if (_loc != null)
					{
						// target is not player OR player is not flying or flymounted
						// TODO: add check for gracia continent coords
						if (!(target instanceof L2PcInstance)
								|| !(target.isFlying() || ((L2PcInstance)target).isFlyingMounted()))
							loc = _loc;
					}
				}
				else
				{
					if (_recallType.equalsIgnoreCase("Castle"))
						loc = MapRegionTable.getInstance().getTeleToLocation(target, MapRegionTable.TeleportWhereType.Castle);
					else if (_recallType.equalsIgnoreCase("ClanHall"))
						loc = MapRegionTable.getInstance().getTeleToLocation(target, MapRegionTable.TeleportWhereType.ClanHall);
					else if (_recallType.equalsIgnoreCase("Fortress"))
						loc = MapRegionTable.getInstance().getTeleToLocation(target, MapRegionTable.TeleportWhereType.Fortress);
					else
						loc = MapRegionTable.getInstance().getTeleToLocation(target, MapRegionTable.TeleportWhereType.Town);
				}
				
				if (loc != null)
				{
					// Synerge - Unequip instance items when leaving an instance, and instance pets
					if (targetChar != null && target.getInstanceId() > 0)
					{
						for (L2ItemInstance i : target.getInventory().getItems())
						{
							if (i.isInstanceItem() && i.isEquipped())
								targetChar.useEquippableItem(i, true);
						}
						
						/* When exiting pailaka zone, unsummon pet */
				        if (targetChar.getPet() != null && (targetChar.getPet().getNpcId() == 14916 || targetChar.getPet().getNpcId() == 14917))
				        	targetChar.getPet().unSummon(targetChar);
					}

					target.setInstanceId(0);
					if (target instanceof L2PcInstance)
						((L2PcInstance)target).setIsIn7sDungeon(false);
					target.teleToLocation(loc, true);
				}
			}
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "", e);
		}
	}
}