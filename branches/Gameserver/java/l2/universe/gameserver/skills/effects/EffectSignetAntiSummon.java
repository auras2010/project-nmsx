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

import l2.universe.gameserver.ai.CtrlEvent;
import l2.universe.gameserver.model.L2Effect;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Playable;
import l2.universe.gameserver.model.actor.instance.L2EffectPointInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.skills.Env;
import l2.universe.gameserver.templates.effects.EffectTemplate;
import l2.universe.gameserver.templates.skills.L2EffectType;
import l2.universe.util.Rnd;

/**
 * @author Forsaiken
 */
public class EffectSignetAntiSummon extends L2Effect
{
	private L2EffectPointInstance _actor;
	private static final byte ERASE_RATE = 40;
	
	public EffectSignetAntiSummon(Env env, EffectTemplate template)
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
		_actor = (L2EffectPointInstance) getEffected();
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getCount() == getTotalCount() - 1)
			return true; // do nothing first time
		
		final int mpConsume = getSkill().getMpConsume();
		final L2PcInstance player = (L2PcInstance) getEffector();
		final Collection<L2Character> knownChars = _actor.getKnownList().getKnownCharactersInRadius(getSkill().getSkillRadius());
		L2PcInstance target;
		
		for (L2Character cha : knownChars)
		{
			if (!(cha instanceof L2Playable))
				continue;

			if (mpConsume > getEffector().getCurrentMp())
			{
				getEffector().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP));
				return false;
			}
			else
				getEffector().reduceCurrentMp(mpConsume);
			
			target = cha.getActingPlayer();
			
			// Synerge - Check conditions for the Signet, dont cancel allies, party, clan, etc
			if (!player.isTargetAffectedByAOE(target, cha))
				continue;
			
			if (ERASE_RATE > Rnd.get(100))
			{
				target.getPet().unSummon(target);
				target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, getEffector());
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
