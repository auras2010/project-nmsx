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

import java.util.List;

import javolution.util.FastList;

import l2.brick.gameserver.handler.ISkillHandler;
import l2.brick.gameserver.model.L2Object;
import l2.brick.gameserver.model.L2Skill;
import l2.brick.gameserver.model.actor.L2Character;
import l2.brick.gameserver.model.actor.instance.L2PcInstance;
import l2.brick.gameserver.model.actor.instance.L2PetInstance;
import l2.brick.gameserver.skills.Formulas;
import l2.brick.gameserver.taskmanager.DecayTaskManager;
import l2.brick.gameserver.templates.skills.L2SkillType;
import l2.brick.gameserver.templates.skills.L2TargetType;

public class Resurrect implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.RESURRECT
	};
	
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		L2PcInstance player = null;
		if (activeChar instanceof L2PcInstance)
			player = (L2PcInstance) activeChar;
		
		L2PcInstance targetPlayer;
		List<L2Character> targetToRes = new FastList<L2Character>();
		
		for (L2Character target: (L2Character[]) targets)
		{
			if (target instanceof L2PcInstance)
			{
				targetPlayer = (L2PcInstance) target;
				
				// Check for same party or for same clan, if target is for clan.
				if (skill.getTargetType() == L2TargetType.TARGET_CORPSE_CLAN)
				{
					if (player.getClanId() != targetPlayer.getClanId())
						continue;
				}
			}
			if (target.isVisible())
				targetToRes.add(target);
		}
		
		if (targetToRes.isEmpty())
		{
			activeChar.abortCast();
			return;
		}
		
		for (L2Character cha : targetToRes)
			if (activeChar instanceof L2PcInstance)
			{
				if (cha instanceof L2PcInstance)
					((L2PcInstance) cha).reviveRequest((L2PcInstance) activeChar, skill, false);
				else if (cha instanceof L2PetInstance)
					((L2PetInstance) cha).getOwner().reviveRequest((L2PcInstance) activeChar, skill, true);
			}
			else
			{
				DecayTaskManager.getInstance().cancelDecayTask(cha);
				cha.doRevive(Formulas.calculateSkillResurrectRestorePercent(skill.getPower(), activeChar));
			}
	}
	
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
