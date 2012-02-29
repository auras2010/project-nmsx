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
package l2.universe.gameserver.network.serverpackets;

import l2.universe.gameserver.model.actor.instance.L2PcInstance;

/**
 * Format: ch ddcdc
 * @author  KenM
 */
public class ExPCCafePointInfo extends L2GameServerPacket
{
	private static final String _S__FE_31_EXPCCAFEPOINTINFO = "[S] FE:32 ExPCCafePointInfo";
	private int m_AddPoint, m_PeriodType, RemainTime, PointType;
	private L2PcInstance _character;

	public ExPCCafePointInfo(L2PcInstance user, int modify, boolean add, int hour, boolean _double)
	{
		_character = user;
		m_AddPoint = modify;

		if(add && _double)
		{
			m_PeriodType = 1;
			PointType = 0;
		}
		else if(add)
		{
			m_PeriodType = 1;
			PointType = 1;
		}
		else
		{
			m_PeriodType = 2;
			PointType = 2;
		}
		RemainTime = hour; 
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x32);
		writeD(_character.getPcBangScore()); // num points
		writeD(m_AddPoint); // points inc display
		writeC(m_PeriodType); // period(0=don't show window,1=acquisition,2=use points)
		writeD(RemainTime); // period hours left
		writeC(PointType); // points inc display color(0=yellow,1=cyan-blue,2=red,all other black)
	}

	@Override
	public String getType()
	{
		return _S__FE_31_EXPCCAFEPOINTINFO;
	}
}
