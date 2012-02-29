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

/**
 * @author Forsaiken
 */
package l2.universe.gameserver.skills.effects;

import java.util.Collection;

import javolution.util.FastList;
import l2.universe.gameserver.ai.CtrlEvent;
import l2.universe.gameserver.datatables.NpcTable;
import l2.universe.gameserver.idfactory.IdFactory;
import l2.universe.gameserver.model.L2Effect;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Playable;
import l2.universe.gameserver.model.actor.L2Summon;
import l2.universe.gameserver.model.actor.instance.L2EffectPointInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.MagicSkillLaunched;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.skills.Env;
import l2.universe.gameserver.skills.Formulas;
import l2.universe.gameserver.skills.l2skills.L2SkillSignetCasttime;
import l2.universe.gameserver.templates.chars.L2NpcTemplate;
import l2.universe.gameserver.templates.effects.EffectTemplate;
import l2.universe.gameserver.templates.skills.L2EffectType;
import l2.universe.util.Point3D;

public class EffectSignetMDam extends L2Effect
{
	private L2EffectPointInstance _actor;
	
	public EffectSignetMDam(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.SIGNET_GROUND;
	}
	
	@Override
	public boolean onStart()
	{
		L2NpcTemplate template;
		if (getSkill() instanceof L2SkillSignetCasttime)
			template = NpcTable.getInstance().getTemplate(((L2SkillSignetCasttime) getSkill())._effectNpcId);
		else
			return false;
		
		L2EffectPointInstance effectPoint = new L2EffectPointInstance(IdFactory.getInstance().getNextId(), template, getEffector());
		effectPoint.setCurrentHp(effectPoint.getMaxHp());
		effectPoint.setCurrentMp(effectPoint.getMaxMp());
		//L2World.getInstance().storeObject(effectPoint);
		
		int x = getEffector().getX();
		int y = getEffector().getY();
		int z = getEffector().getZ();
		
		if (getEffector() instanceof L2PcInstance
		        && getSkill().getTargetType() == L2Skill.SkillTargetType.TARGET_GROUND)
		{
			final Point3D wordPosition = ((L2PcInstance) getEffector()).getCurrentSkillWorldPosition();			
			if (wordPosition != null)
			{
				x = wordPosition.getX();
				y = wordPosition.getY();
				z = wordPosition.getZ();
			}
		}
		effectPoint.setIsInvul(true);
		effectPoint.spawnMe(x, y, z);
		
		_actor = effectPoint;
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getCount() >= getTotalCount() - 2)
			return true; // do nothing first 2 times
		
		final int mpConsume = getSkill().getMpConsume();
		final L2PcInstance caster = (L2PcInstance) getEffector();
		
		boolean ss = false;
		boolean bss = false;
		
		final L2ItemInstance weaponInst = caster.getActiveWeaponInstance();
		if (weaponInst != null)
		{
			switch (weaponInst.getChargedSpiritshot())
			{
				case L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT:
					weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
					bss = true;
					break;
				case L2ItemInstance.CHARGED_SPIRITSHOT:
					weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
					ss = true;
					break;
			}
		}
		
		FastList<L2Character> targets = new FastList<L2Character>();
		
		final L2PcInstance player = (L2PcInstance) getEffector();
		final Collection<L2Character> knownChars = _actor.getKnownList().getKnownCharactersInRadius(getSkill().getSkillRadius());
		L2PcInstance target;
		
		for (L2Character cha : knownChars)
		{
			if (cha == null || cha == caster)
				continue;
			
			if (cha instanceof L2Attackable
			        || cha instanceof L2Playable)
			{
				if (cha.isAlikeDead())
					continue;
				
				if (mpConsume > caster.getCurrentMp())
				{
					caster.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP));
					return false;
				}
				else
					caster.reduceCurrentMp(mpConsume);
				
				if (cha instanceof L2Playable)
				{
					if (cha instanceof L2Summon && ((L2Summon)cha).getOwner() == caster){}
					else
						caster.updatePvPStatus(cha);
				}
				
				target = cha.getActingPlayer();
				
				// Synerge - Check conditions for the Signet, dont cancel allies, party, clan, etc
				if (!player.isTargetAffectedByAOE(target, cha))
					continue;
				
				targets.add(cha);
			}
		}
		
		if (!targets.isEmpty())
		{
			caster.broadcastPacket(new MagicSkillLaunched(caster, getSkill().getId(), getSkill().getLevel(), targets.toArray(new L2Character[targets.size()])));
			for (L2Character targe : targets)
			{
				final boolean mcrit = Formulas.calcMCrit(caster.getMCriticalHit(targe, getSkill()));
				final byte shld = Formulas.calcShldUse(caster, targe, getSkill());
				final int mdam = (int) Formulas.calcMagicDam(caster, targe, getSkill(), shld, ss, bss, mcrit);
				
				if (targe instanceof L2Summon)
					targe.broadcastStatusUpdate();
				
				if (mdam > 0)
				{
					if (!targe.isRaid() && Formulas.calcAtkBreak(targe, mdam))
					{
						targe.breakAttack();
						targe.breakCast();
					}
					caster.sendDamageMessage(targe, mdam, mcrit, false, false);
					targe.reduceCurrentHp(mdam, caster, getSkill());
				}
				targe.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, caster);
			}
		}
		return true;
	}
	
	@Override
	public void onExit()
	{
		if (_actor != null)
			_actor.deleteMe();
	}
}
