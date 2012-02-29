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
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import l2.universe.Config;
import l2.universe.gameserver.model.L2Effect;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.L2Playable;
import l2.universe.gameserver.model.actor.L2Summon;
import l2.universe.gameserver.model.actor.instance.L2CubicInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.StatusUpdate;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.skills.Formulas;
import l2.universe.gameserver.templates.StatsSet;

public class L2SkillDrain extends L2Skill
{
	private static final Logger _logDamage = Logger.getLogger("damage");

	private float _absorbPart;
	private int   _absorbAbs;

	public L2SkillDrain(StatsSet set)
    {
		super(set);

		_absorbPart = set.getFloat ("absorbPart", 0.f);
		_absorbAbs  = set.getInteger("absorbAbs", 0);
	}

	@Override
	public void useSkill(L2Character activeChar, L2Object[] targets)
    {
		if (activeChar.isAlikeDead())
			return;

		boolean ss = false;
		boolean bss = false;
		
		L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
		if (weaponInst != null)
		{
		    if (weaponInst.getChargedSpiritshot() == L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT)
		    {
		        bss = true;
		        weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
		    }
		    else if (weaponInst.getChargedSpiritshot() == L2ItemInstance.CHARGED_SPIRITSHOT)
		    {
		        ss = true;
		        weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
		    }
        }
        // If there is no weapon equipped, check for an active summon.
        else if (activeChar instanceof L2Summon)
        {
            final L2Summon activeSummon = (L2Summon)activeChar;

            if (activeSummon.getChargedSpiritShot() == L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT)
            {
                bss = true;
                activeSummon.setChargedSpiritShot(L2ItemInstance.CHARGED_NONE);
            }
            else if (activeSummon.getChargedSpiritShot() == L2ItemInstance.CHARGED_SPIRITSHOT)
            {
                ss = true;
                activeSummon.setChargedSpiritShot(L2ItemInstance.CHARGED_NONE);
            }
        }

        for(L2Character target: (L2Character[]) targets)
        {
			if (target.isAlikeDead() && getTargetType() != SkillTargetType.TARGET_CORPSE_MOB)
				continue;

            if (activeChar != target && target.isInvul())
                continue; // No effect on invulnerable chars unless they cast it themselves.

			final boolean mcrit = Formulas.calcMCrit(activeChar.getMCriticalHit(target, this));
			final byte shld = Formulas.calcShldUse(activeChar, target, this);
			int damage = (int)Formulas.calcMagicDam(activeChar, target, this, shld, ss, bss, mcrit);
			
			int _drain = 0;
			int _cp = (int)target.getCurrentCp();
			int _hp = (int)target.getCurrentHp();
			
			if (_cp > 0)
			{
				if (damage < _cp)
					_drain = 0;
				else
					_drain = damage - _cp;
			}
			else if (damage > _hp)
				_drain = _hp;
			else
				_drain = damage;

			double hpAdd = _absorbAbs + _absorbPart * _drain;
			double hp = ((activeChar.getCurrentHp() + hpAdd) > activeChar.getMaxHp() ? activeChar.getMaxHp() : (activeChar.getCurrentHp() + hpAdd));

            activeChar.setCurrentHp(hp);

			StatusUpdate suhp = new StatusUpdate(activeChar);
			suhp.addAttribute(StatusUpdate.CUR_HP, (int)hp);
			activeChar.sendPacket(suhp);

            // Check to see if we should damage the target
            if (damage > 0 && (!target.isDead() || getTargetType() != SkillTargetType.TARGET_CORPSE_MOB))
            {
                // Manage attack or cast break of the target (calculating rate, sending message...)
                if (!target.isRaid() && Formulas.calcAtkBreak(target, damage))
                {
                    target.breakAttack();
                    target.breakCast();
                }

            	activeChar.sendDamageMessage(target, damage, mcrit, false, false);

            	if (Config.LOG_GAME_DAMAGE
            			&& activeChar instanceof L2Playable
            			&& damage > Config.LOG_GAME_DAMAGE_THRESHOLD)
            	{
            		LogRecord record = new LogRecord(Level.INFO, "");
            		record.setParameters(new Object[]{activeChar, " did damage ", (int)damage, this, " to ", target});
            		record.setLoggerName("mdam");
            		_logDamage.log(record);
            	}
            	
            	byte reflect = Formulas.calcSkillReflect(target, this);

            	if (hasEffects() && getTargetType() != SkillTargetType.TARGET_CORPSE_MOB)
                {
                	// ignoring vengance-like reflections
                	if ((reflect & Formulas.SKILL_REFLECT_SUCCEED) > 0)
                	{
                		activeChar.stopSkillEffects(getId());
    					getEffects(target,activeChar);
    					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
						sm.addSkillName(getId());
						activeChar.sendPacket(sm);
						sm = null;
                	}
                	else
                	{
                		// activate attacked effects, if any
                        target.stopSkillEffects(getId());
                        if (Formulas.calcSkillSuccess(activeChar, target, this, shld, false, ss, bss))
                            getEffects(activeChar, target);
                        else
                        {
                            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_RESISTED_YOUR_S2);
                            sm.addCharName(target);
                            sm.addSkillName(this);
                            activeChar.sendPacket(sm);
                            sm = null;
                        }
                	}
                }
            	
				// Vengeance reflected damage
				// DS: because only skill using vengeanceMdam is Shield Deflect Magic
				// and for this skill no damage should pass to target, just hardcode it for now
				if ((reflect & Formulas.SKILL_REFLECT_VENGEANCE) != 0)
					activeChar.reduceCurrentHp(damage, target, this);
				else
					target.reduceCurrentHp(damage, activeChar, this);
            }

            // Check to see if we should do the decay right after the cast
            if (target.isDead() && getTargetType() == SkillTargetType.TARGET_CORPSE_MOB && target instanceof L2Npc) {
                ((L2Npc)target).endDecayTask();
            }
		}
        
		// effect self :]
		if (hasSelfEffects())
		{
	        L2Effect effect = activeChar.getFirstEffect(getId());
	        if (effect != null && effect.isSelfEffect())
	        {
	            //Replace old effect with new one.
	            effect.exit();
	        }
	        // cast self effect if any
	        getEffectsSelf(activeChar);
		}
	}
	
	public void useCubicSkill(L2CubicInstance activeCubic, L2Object[] targets)
    {
		if (Config.DEBUG)
			_log.info("L2SkillDrain: useCubicSkill()");
		
        for(L2Character target: (L2Character[]) targets)
        {
			if (target.isAlikeDead() && getTargetType() != SkillTargetType.TARGET_CORPSE_MOB)
				continue;

			final boolean mcrit = Formulas.calcMCrit(activeCubic.getMCriticalHit(target, this));
			final byte shld = Formulas.calcShldUse(activeCubic.getOwner(), target, this);
			
			int damage = (int)Formulas.calcMagicDam(activeCubic, target, this, mcrit, shld);
			if (Config.DEBUG)
    			_log.info("L2SkillDrain: useCubicSkill() -> damage = " + damage);
			
			final double hpAdd = _absorbAbs + _absorbPart * damage;
			final L2PcInstance owner = activeCubic.getOwner();
			final double hp = ((owner.getCurrentHp() + hpAdd) > owner.getMaxHp() ? owner.getMaxHp() : (owner.getCurrentHp() + hpAdd));

            owner.setCurrentHp(hp);
            
			StatusUpdate suhp = new StatusUpdate(owner);
			suhp.addAttribute(StatusUpdate.CUR_HP, (int)hp);
			owner.sendPacket(suhp);
			
            // Check to see if we should damage the target
            if (damage > 0 && (!target.isDead() || getTargetType() != SkillTargetType.TARGET_CORPSE_MOB))
            {
    			target.reduceCurrentHp(damage, activeCubic.getOwner(), this);
                
                // Manage attack or cast break of the target (calculating rate, sending message...)
                if (!target.isRaid() && Formulas.calcAtkBreak(target, damage))
                {
                    target.breakAttack();
                    target.breakCast();
                }
            	owner.sendDamageMessage(target, damage, mcrit, false, false);
            }
		}
	}
}
