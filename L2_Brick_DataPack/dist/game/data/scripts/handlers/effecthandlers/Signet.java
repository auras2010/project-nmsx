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
package handlers.effecthandlers;

import javolution.util.FastList;

import l2.brick.gameserver.datatables.SkillTable;
import l2.brick.gameserver.model.L2Effect;
import l2.brick.gameserver.model.L2Skill;
import l2.brick.gameserver.model.actor.L2Character;
import l2.brick.gameserver.model.actor.instance.L2EffectPointInstance;
import l2.brick.gameserver.network.SystemMessageId;
import l2.brick.gameserver.network.serverpackets.MagicSkillUse;
import l2.brick.gameserver.skills.Env;
import l2.brick.gameserver.skills.l2skills.L2SkillSignet;
import l2.brick.gameserver.skills.l2skills.L2SkillSignetCasttime;
import l2.brick.gameserver.templates.effects.EffectTemplate;
import l2.brick.gameserver.templates.skills.L2EffectType;

/**
 * @authors Forsaiken, Sami
 */
public class Signet extends L2Effect
{
	private L2Skill _skill;
	private L2EffectPointInstance _actor;
	private boolean _srcInArena;
	
	public Signet(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	/**
	 * 
	 * @see l2.brick.gameserver.model.L2Effect#getEffectType()
	 */
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.SIGNET_EFFECT;
	}
	
	/**
	 * 
	 * @see l2.brick.gameserver.model.L2Effect#onStart()
	 */
	@Override
	public boolean onStart()
	{
		if (getSkill() instanceof L2SkillSignet)
			_skill = SkillTable.getInstance().getInfo(((L2SkillSignet) getSkill()).effectId, getLevel());
		else if (getSkill() instanceof L2SkillSignetCasttime)
			_skill = SkillTable.getInstance().getInfo(((L2SkillSignetCasttime) getSkill()).effectId, getLevel());
		_actor = (L2EffectPointInstance) getEffected();
		_srcInArena = (getEffector().isInsideZone(L2Character.ZONE_PVP) && !getEffector().isInsideZone(L2Character.ZONE_SIEGE));
		return true;
	}
	
	/**
	 * 
	 * @see l2.brick.gameserver.model.L2Effect#onActionTime()
	 */
	@Override
	public boolean onActionTime()
	{
		if (_skill == null)
			return true;
		int mpConsume = _skill.getMpConsume();
		
		if (mpConsume > getEffector().getCurrentMp())
		{
			getEffector().sendPacket(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
			return false;
		}
		
		getEffector().reduceCurrentMp(mpConsume);
		FastList<L2Character> targets = FastList.newInstance();
		for (L2Character cha : _actor.getKnownList().getKnownCharactersInRadius(getSkill().getSkillRadius()))
		{
			if (cha == null)
				continue;
			
			if (_skill.isOffensive() && !L2Skill.checkForAreaOffensiveSkills(getEffector(), cha, _skill, _srcInArena))
				continue;
			
			// there doesn't seem to be a visible effect with MagicSkillLaunched packet...
			_actor.broadcastPacket(new MagicSkillUse(_actor, cha, _skill.getId(), _skill.getLevel(), 0, 0));
			targets.add(cha);
		}
		
		if (!targets.isEmpty())
			getEffector().callSkill(_skill, targets.toArray(new L2Character[targets.size()]));
		FastList.recycle(targets);
		return true;
	}
	
	/**
	 * 
	 * @see l2.brick.gameserver.model.L2Effect#onExit()
	 */
	@Override
	public void onExit()
	{
		if (_actor != null)
			_actor.deleteMe();
	}
}
