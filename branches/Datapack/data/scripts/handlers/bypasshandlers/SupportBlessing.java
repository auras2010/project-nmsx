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
package handlers.bypasshandlers;

import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.handler.IBypassHandler;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;

public class SupportBlessing implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"GiveBlessing"
	};
	
	@Override
	public boolean useBypass(final String command, final L2PcInstance activeChar, final L2Character target)
	{
		if (!(target instanceof L2Npc))
			return false;
		
		// Blessing of protection - author kerberos_20. Used codes from Rayan - L2Emu project.
		// Prevent a cursed weapon weilder of being buffed - I think no need of that becouse karma check > 0
		// if (player.isCursedWeaponEquiped())
		//   return;
		
		final int player_level = activeChar.getLevel();
		// Select the player
		((L2Npc) target).setTarget(activeChar);
		// If the player is too high level, display a message and return
		if (player_level > 39 || activeChar.getClassId().level() >= 2)
		{
			final NpcHtmlMessage msg = new NpcHtmlMessage(((L2Npc) target).getObjectId());
			msg.setHtml("<html><body>Newbie Guide:<br>I'm sorry, but you are not eligible to receive the protection blessing.<br1>It can only be bestowed on <font color=\"LEVEL\">characters below level 39 who have not made a seccond transfer.</font></body></html>");
			activeChar.sendPacket(msg);
			return true;
		}
		final L2Skill skill = SkillTable.FrequentSkill.BLESSING_OF_PROTECTION.getSkill();
		((L2Npc) target).doCast(skill);
		
		return false;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}