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
package l2.universe.gameserver.model.entity.events;

import java.util.logging.Logger;

import l2.universe.ExternalConfig;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.util.Rnd;


/**
 * @author Anumis
 */

public class PcPoint implements Runnable
{
	private static final Logger _log = Logger.getLogger(PcPoint.class.getName());
	private static PcPoint _instance;

	public static PcPoint getInstance()
	{
		if(_instance == null)
		{
			_instance = new PcPoint();
		}

		return _instance;
	}

	private PcPoint()
	{
		_log.info("PcBang point event started.");
	}

	@Override
	public void run()
	{
		int score = 0;
		for(L2PcInstance activeChar: L2World.getInstance().getAllPlayers().values())
		{
			if(activeChar.getLevel() > ExternalConfig.PCB_MIN_LEVEL )
			{
				score = Rnd.get(ExternalConfig.PCB_POINT_MIN, ExternalConfig.PCB_POINT_MAX);

				if(Rnd.get(100) <= ExternalConfig.PCB_CHANCE_DUAL_POINT)
				{
					score *= 2;

					activeChar.addPcBangScore(score);

					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT_DOUBLE);
					sm.addNumber(score);
					activeChar.sendPacket(sm);
					sm = null;

					activeChar.updatePcBangWnd(score, true, true);
				}
				else
				{
					activeChar.addPcBangScore(score);

					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT);
					sm.addNumber(score);
					activeChar.sendPacket(sm);
					sm = null;

					activeChar.updatePcBangWnd(score, true, false);
				}
			}

			activeChar = null;
		} 
	}
}