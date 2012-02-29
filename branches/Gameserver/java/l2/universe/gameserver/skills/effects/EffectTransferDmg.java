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

import l2.universe.gameserver.model.L2Effect;
import l2.universe.gameserver.model.actor.L2Playable;
import l2.universe.gameserver.skills.Env;
import l2.universe.gameserver.templates.effects.EffectTemplate;
import l2.universe.gameserver.templates.skills.L2EffectType;

/**
 * Support for transfering damage to a party member. This is not Transfer Pain
 *
 * @author Synerge
 */
public class EffectTransferDmg extends L2Effect
{   
   public EffectTransferDmg(Env env, EffectTemplate template)
   {
	   super(env, template);
   }

   public EffectTransferDmg(Env env, L2Effect effect)
   {
	   super(env, effect);
   }

   @Override
   public L2EffectType getEffectType()
   {
	   return L2EffectType.TRANSFERDMG;
   }

   @Override
   public boolean onStart()
   {
	   if (getEffected() instanceof L2Playable)
	   {
		   final L2Playable activeChar = (L2Playable) getEffected();
		   activeChar.setIsTransferringDmg(true);
		   activeChar.setIsTransferringDmgTo((L2Playable) getEffector());
	   }
	   return true;
   }

   @Override
   public void onExit()
   {
		if (getEffected() instanceof L2Playable) 
		{
			final L2Playable activeChar = (L2Playable) getEffected();
			activeChar.setIsTransferringDmg(false);
			activeChar.setIsTransferringDmgTo(null);
		}
   }

   @Override
   public boolean onActionTime()
   {
	   return false;
   }
}
