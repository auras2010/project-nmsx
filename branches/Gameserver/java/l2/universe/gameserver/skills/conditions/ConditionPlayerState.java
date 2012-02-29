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
package l2.universe.gameserver.skills.conditions;

import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.base.PlayerState;
import l2.universe.gameserver.skills.Env;

/**
 * The Class ConditionPlayerState.
 *
 * @author mkizub
 */
public class ConditionPlayerState extends Condition
{
	private final PlayerState _check;
	private final boolean _required;
	
	/**
	 * Instantiates a new condition player state.
	 *
	 * @param check the check
	 * @param required the required
	 */
	public ConditionPlayerState(PlayerState check, boolean required)
	{
		_check = check;
		_required = required;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		L2PcInstance player;
		switch (_check)
		{
			case RESTING:
				if (env.player instanceof L2PcInstance)
					return ((L2PcInstance) env.player).isSitting() == _required;
				return !_required;
			case MOVING:
				return env.player.isMoving() == _required;
			case RUNNING:
				return env.player.isMoving() == _required && env.player.isRunning() == _required;
			case FLYING:
				return env.player.isFlying() == _required;
			case BEHIND:
				if (env.target == null)
					return false;
				return env.player.isBehind(env.target) == _required;
			case FRONT:
				if (env.target == null)
					return false;
				return env.player.isInFrontOf(env.target) == _required;
			case CHAOTIC:
				player = env.player.getActingPlayer();
				if (player != null)
					return player.getKarma() > 0 == _required;
				return !_required;
			case OLYMPIAD:
				player = env.player.getActingPlayer();
				if (player != null)
					return player.isInOlympiadMode() == _required;
				return !_required;
			case COMBAT:
				player = env.player.getActingPlayer();
				if (player != null)
					return player.isInCombat() == _required;
				return !_required;
			case FLAG:
				player = env.player.getActingPlayer();
				if (player != null)
					return player.getPvpFlag() > 0 == _required;
				return !_required;
		}
		return !_required;
	}
}
