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

import l2.brick.gameserver.model.L2Clan;
import l2.brick.gameserver.model.L2Effect;
import l2.brick.gameserver.model.actor.instance.L2PcInstance;
import l2.brick.gameserver.network.SystemMessageId;
import l2.brick.gameserver.network.serverpackets.SystemMessage;
import l2.brick.gameserver.skills.AbnormalEffect;
import l2.brick.gameserver.skills.Env;
import l2.brick.gameserver.templates.effects.EffectTemplate;
import l2.brick.gameserver.templates.skills.L2EffectType;

/**
 * @author ZaKaX (Ghost @ L2D)
 */
public class ClanGate extends L2Effect
{
	public ClanGate(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	/**
	 * 
	 * @see l2.brick.gameserver.model.L2Effect#onStart()
	 */
	@Override
	public boolean onStart()
	{
		getEffected().startAbnormalEffect(AbnormalEffect.MAGIC_CIRCLE);
		if (getEffected() instanceof L2PcInstance)
		{
			L2Clan clan = ((L2PcInstance) getEffected()).getClan();
			if (clan != null)
			{
				SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.COURT_MAGICIAN_CREATED_PORTAL);
				clan.broadcastToOtherOnlineMembers(msg, ((L2PcInstance) getEffected()));
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * @see l2.brick.gameserver.model.L2Effect#onActionTime()
	 */
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	/**
	 * 
	 * @see l2.brick.gameserver.model.L2Effect#onExit()
	 */
	@Override
	public void onExit()
	{
		getEffected().stopAbnormalEffect(AbnormalEffect.MAGIC_CIRCLE);
	}
	
	/**
	 * 
	 * @see l2.brick.gameserver.model.L2Effect#getEffectType()
	 */
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.CLAN_GATE;
	}
}
