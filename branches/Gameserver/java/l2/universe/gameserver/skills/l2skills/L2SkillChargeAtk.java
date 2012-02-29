package l2.universe.gameserver.skills.l2skills;

import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.L2Effect;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.templates.StatsSet;
import l2.universe.gameserver.skills.Formulas;

public class L2SkillChargeAtk extends L2Skill
{
	final int num_charges;
	
	public L2SkillChargeAtk(StatsSet set) 
    {
		super(set);
		num_charges = set.getInteger("num_charges", getLevel());
	}

	@Override
	public void useSkill(L2Character caster, L2Object[] targets)
	{
		if (caster.isAlikeDead())
			return;
		
		@SuppressWarnings("unused")
		boolean ss = false;
        
        for(int index = 0;index < targets.length;index++)
        {
        	final L2Character target = (L2Character)targets[index];
            if (target.isAlikeDead())
            	continue;

            // TODO: should we use dual or not?
            // because if so, damage are lowered but we dont do anything special with dual then
            // like in doAttackHitByDual which in fact does the calcPhysDam call twice
            final boolean dual = caster.isUsingDualWeapon();
            
            final byte shld = Formulas.calcShldUse(caster, target);
            //boolean crit = Formulas.getInstance().calcCrit(caster.getCriticalHit(target, this));
            final L2ItemInstance weapon = caster.getActiveWeaponInstance();
            final boolean soul = (weapon != null && weapon.getChargedSoulshot() == L2ItemInstance.CHARGED_SOULSHOT);

            int damage = (int) Formulas.calcPhysDam(caster, target, this, shld, false, dual, soul);
            if (damage > 0)
            {
                target.reduceCurrentHp(damage, caster,this);
                SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_DID_S1_DMG);
                sm.addNumber(damage);
                caster.sendPacket(sm);
                sm = null;
                
                if (soul && weapon!= null)
                    weapon.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);
            }
            else
            {
                caster.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.MISSED_TARGET));
            }
        }

		// self Effect :]
		L2Effect effect = caster.getFirstEffect(this.getId());
		if (effect != null && effect.isSelfEffect())
		{
			//Replace old effect with new one.
			effect.exit();
		}
		
		this.getEffectsSelf(caster);
		getEffects(caster, caster);
		getEffectsSelf(caster);
	}
}
