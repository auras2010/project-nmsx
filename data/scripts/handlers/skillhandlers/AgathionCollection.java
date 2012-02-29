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
package handlers.skillhandlers;

import java.util.logging.Logger;

import l2.universe.Config;
import l2.universe.gameserver.handler.ISkillHandler;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.instance.L2NpcInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.templates.skills.L2SkillType;
import l2.universe.gameserver.util.Util;
import l2.universe.util.Rnd;

/**
 * @author  nonom
 */
public class AgathionCollection implements ISkillHandler
{
	private static Logger _log = Logger.getLogger(AgathionCollection.class.getName());
	
	private static int RATE = 1;
	private static int ENERGY_SEEDS[] = { 18680, 18679, 18678, 18681, 18682, 18683 };
	
	private static final L2SkillType[] SKILL_IDS = { L2SkillType.AGATHION_COLLECTION };
	
	@Override
	public void useSkill(final L2Character activeChar, final L2Skill skill, final L2Object[] targets)
	{
		if (!(activeChar instanceof L2PcInstance))
			return;
		
		final L2Object[] targetList = skill.getTargetList(activeChar);
		
		if (targetList == null || targetList.length == 0)
			return;
		
		if (Config.DEBUG)
			_log.info("Casting collector");
		
		L2NpcInstance target;
		final L2PcInstance player = (L2PcInstance) activeChar;
		
		for (final L2Object tgt : targetList)
		{
			if (!(tgt instanceof L2NpcInstance))
				continue;
			
			target = (L2NpcInstance) tgt;
			
			if (skill.getId() == 5780 && Util.contains(ENERGY_SEEDS, target.getNpcId()))
			{
				int itemId = 0;
				switch (target.getNpcId())
				{
					case 18678: //Water
						itemId = 14016; //Water
						break;
					case 18679: //Fire
						itemId = 14015; //Fire
						break;
					case 18680: //Wind
						itemId = 14017; //Wind
						break;
					case 18681: //Earth
						itemId = 14018; //Earth
						break;
					case 18682: //Divinity
						itemId = 14020; //Sacred
						break;
					case 18683: //Darkness
						itemId = 14019; //Darkness
						break;
					default:
						break;
				}
				if (Rnd.get(100) < 33)
				{
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_COLLECTION_HAS_SUCCEEDED));
					final int rate = Rnd.get(RATE + 1, 2 * RATE);
					player.addItem("Loot", itemId, rate, null, true);
				}
				else if ((skill.getLevel() == 1 && Rnd.get(100) < 15) || (skill.getLevel() == 2 && Rnd.get(100) < 50) || (skill.getLevel() == 3 && Rnd.get(100) < 75))
				{
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_COLLECTION_HAS_SUCCEEDED));
					final int rate = Rnd.get(1, RATE);
					player.addItem("Loot", itemId, rate, null, true);
				}
				else
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_COLLECTION_HAS_FAILED));
				target.deleteMe();
			}
		}
		
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
