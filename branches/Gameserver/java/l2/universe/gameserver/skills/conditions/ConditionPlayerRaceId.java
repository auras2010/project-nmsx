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

import java.util.ArrayList;
import java.util.List;

import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.skills.Env;


/**
 * The Class ConditionPlayerRaceId.
 *
 * @author janiii
 */

public class ConditionPlayerRaceId extends Condition
{
	private final List<Integer> _raceIds;
	
	/**
	 * Instantiates a new condition player race id.
	 *
	 * @param raceId the race id list
	 */
	public ConditionPlayerRaceId(List<Integer> raceId)
	{
		_raceIds = raceId;
	}
	
	/**
	 * Instantiates a new condition player race id.
	 *
	 * @param raceId the race id
	 */
	public ConditionPlayerRaceId(int raceId)
	{
		_raceIds = new ArrayList<Integer>();
		_raceIds.add(raceId);
	}
	
	/* (non-Javadoc)
	 * @see l2.universe.gameserver.skills.conditions.Condition#testImpl(l2.universe.gameserver.skills.Env)
	 */
	@Override
	public boolean testImpl(Env env)
	{
		if (!(env.player instanceof L2PcInstance))
			return false;
		return _raceIds == null || (_raceIds.contains(((L2PcInstance)env.player).getRace().ordinal()));
	}
}