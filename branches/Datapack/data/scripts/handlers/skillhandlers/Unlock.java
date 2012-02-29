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

import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.handler.ISkillHandler;
import l2.universe.gameserver.instancemanager.InstanceManager;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.instance.L2ChestInstance;
import l2.universe.gameserver.model.actor.instance.L2DoorInstance;
import l2.universe.gameserver.model.entity.Instance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ActionFailed;
import l2.universe.gameserver.network.serverpackets.SocialAction;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.templates.skills.L2SkillType;
import l2.universe.util.Rnd;

public class Unlock implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.UNLOCK,
		L2SkillType.UNLOCK_SPECIAL
	};
	
	@Override
	public void useSkill(final L2Character activeChar, final L2Skill skill, final L2Object[] targets)
	{
		final L2Object[] targetList = skill.getTargetList(activeChar);
		
		if (targetList == null)
			return;
		
		for (final L2Object target : targets)
		{
			if (target instanceof L2DoorInstance)
			{
				L2DoorInstance door = (L2DoorInstance) target;
				// Check if door in the different instance
				if (activeChar.getInstanceId() != door.getInstanceId())
				{
					// Search for the instance
					final Instance inst = InstanceManager.getInstance().getInstance(activeChar.getInstanceId());
					if (inst == null)
					{
						// Instance not found
						activeChar.sendPacket(ActionFailed.STATIC_PACKET);
						return;
					}
					for (final L2DoorInstance instanceDoor : inst.getDoors())
					{
						if (instanceDoor.getDoorId() == door.getDoorId())
						{
							// Door found
							door = instanceDoor;
							break;
						}
					}
					
					// Checking instance again
					if (activeChar.getInstanceId() != door.getInstanceId())
					{
						activeChar.sendPacket(ActionFailed.STATIC_PACKET);
						return;
					}
				}
				
				if ((!door.isUnlockable() && skill.getSkillType() != L2SkillType.UNLOCK_SPECIAL)
						|| door.getFort() != null)
				{
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.UNABLE_TO_UNLOCK_DOOR));
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				if (doorUnlock(skill) && (!door.getOpen()))
				{
					door.openMe();
					if(skill.getAfterEffectId() == 0)
						door.onOpen();
				}
				else
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_UNLOCK_DOOR));
			}
			else if (target instanceof L2ChestInstance)
			{
				L2ChestInstance chest = (L2ChestInstance) target;
				if (chest.getCurrentHp() <= 0
						|| chest.isInteracted()
						|| activeChar.getInstanceId() != chest.getInstanceId())
				{
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				else
				{
					chest.setInteracted();
					if (chestUnlock(skill, chest))
					{
						activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 3));
						chest.setSpecialDrop();
						chest.setMustRewardExpSp(false);
						chest.reduceCurrentHp(99999999, activeChar, skill);
					}
					else
					{
						activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 13));
						chest.addDamageHate(activeChar, 0, 1);
						chest.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, activeChar);
						if (chestTrap(chest))
							chest.chestTrap(activeChar);
					}
				}
			}
		}
	}
	
	private static final boolean doorUnlock(final L2Skill skill)
	{
		if (skill.getSkillType() == L2SkillType.UNLOCK_SPECIAL)
			return Rnd.get(100) < skill.getPower();
		
		switch (skill.getLevel())
		{
			case 0:
				return false;
			case 1:
				return Rnd.get(120) < 30;
			case 2:
				return Rnd.get(120) < 50;
			case 3:
				return Rnd.get(120) < 75;
			default:
				return Rnd.get(120) < 100;
		}
	}
	
	private static final boolean chestUnlock(final L2Skill skill, final L2Character chest)
	{
		/*
		 * Synerge - Retail description says that the skill opens with 90% success chest lower than x lvl
		 * The lvl is the same as magicLvl, so the first skill in 20, with magic lvl 20, opens with 90% success
		 * chests below lvl 20. Way far for the formula below this.
		 * For example taking with this formula the first lvl of unlock: skill.getLevel() * 5 + 35
		 * Is 1 * 5 + 35 = 40, to open a lvl 19 chest, when description says 90%
		 * I would assume that in the best case, when chest is below the magicLvl of the skill, is 80%
		 * 90% seems overpowered, although must consider that this skills uses lots of keys
		 * So chestLvl <= MagicLvl = 80% chances to open it. I would assume also, that for every lvl
		 * that chest is bigger than magicLvl, 8% of chances are lost, so in 10 lvls, chances will be 0
		 * So a lvl 1 unlock skill can open chests lower than 30, when lower than 20 is the best case possible
		 */
		final int chestLvl = chest.getLevel();
		final int minUnlockLvl = skill.getMagicLevel();
		final int chance;
		
		/* If lower than the min lvl of the skill, chances are the max, 80% */
		if (chestLvl <= minUnlockLvl)
			chance = 80;
		/* If bigger, then for each lvl 8% are lost */
		else
		{
			chance = 80 - (chestLvl - minUnlockLvl) * 8;
			if (chance <= 0)
				return false;
		}
		
		return Rnd.get(100) < chance;
	}
	
	private static final boolean chestTrap(final L2Character chest)
	{
		if (chest.getLevel() > 60)
			return Rnd.get(100) < 80;
		if (chest.getLevel() > 40)
			return Rnd.get(100) < 50;
		if (chest.getLevel() > 30)
			return Rnd.get(100) < 30;
		return Rnd.get(100) < 10;
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
