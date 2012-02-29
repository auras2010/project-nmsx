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
package l2.universe.gameserver.skills.effects;

import java.util.Collection;

import javolution.util.FastList;
import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.model.L2Effect;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.instance.L2EffectPointInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.MagicSkillUse;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.skills.Env;
import l2.universe.gameserver.skills.l2skills.L2SkillSignet;
import l2.universe.gameserver.skills.l2skills.L2SkillSignetCasttime;
import l2.universe.gameserver.templates.effects.EffectTemplate;
import l2.universe.gameserver.templates.skills.L2EffectType;

/**
 * @authors Forsaiken, Sami
 */
public class EffectSignet extends L2Effect
{
	private L2Skill _skill;
	private L2EffectPointInstance _actor;
	
	public EffectSignet(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.SIGNET_EFFECT;
	}

	@Override
	public boolean onStart()
	{
		if (getSkill() instanceof L2SkillSignet)
			_skill = SkillTable.getInstance().getInfo(((L2SkillSignet) getSkill()).effectId, getLevel());
		else if (getSkill() instanceof L2SkillSignetCasttime)
			_skill = SkillTable.getInstance().getInfo(((L2SkillSignetCasttime) getSkill()).effectId, getLevel());
		_actor = (L2EffectPointInstance) getEffected();
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		if (_skill == null)
			return true;
		
		final int mpConsume = _skill.getMpConsume();		
		if (mpConsume > getEffector().getCurrentMp())
		{
			getEffector().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP));
			return false;
		}
		else
			getEffector().reduceCurrentMp(mpConsume);
		
		final L2PcInstance player = (L2PcInstance) getEffector();
		final Collection<L2Character> knownChars = _actor.getKnownList().getKnownCharactersInRadius(getSkill().getSkillRadius());
		L2PcInstance target;
		FastList<L2Character> targets = FastList.newInstance();
		
		for (L2Character cha : knownChars)
		{
			if (cha == null)
				continue;
			
			target = cha.getActingPlayer();
			
			// Synerge - Check conditions for the Signet, only act on allies, party, clan, etc. This one affects the casting player
			if (target != player && player.isTargetAffectedByAOE(target, cha))
				continue;			
			
			//_skill.getEffects(_actor, cha);
			// there doesn't seem to be a visible effect with MagicSkillLaunched packet...
			_actor.broadcastPacket(new MagicSkillUse(_actor, cha, _skill.getId(), _skill.getLevel(), 0, 0));
			targets.add(cha);
		}
		
		if (!targets.isEmpty())
			getEffector().callSkill(_skill, targets.toArray(new L2Character[targets.size()]));
		FastList.recycle(targets);
		
		return true;
	}
	
	@Override
	public void onExit()
	{
		if (_actor != null)
			_actor.deleteMe();
	}
}
