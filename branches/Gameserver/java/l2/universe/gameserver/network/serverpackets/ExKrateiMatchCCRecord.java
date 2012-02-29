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

import l2.universe.gameserver.instancemanager.KrateisCubeManager;
import l2.universe.gameserver.instancemanager.KrateisCubeManager.CCPlayer;
/**
 * Sent at the end of Krateis Cube OR when you click the "Match results" icon during
 * the match.
 * @author savormix
 * @Recode Johan
 * @Recode Xmen57
 * @Recode Willow
 */
public class ExKrateiMatchCCRecord extends L2GameServerPacket
{
	private static final String _S__FE_89_EXPVPMATCHCCRECORD = "[S] FE:89 ExPVPMatchCCRecord";

	private final int			_state;
	private final CCPlayer[]	_players;
	private String				playername;
	private Integer 			kills;
	private static String[]		SCBnames;
	private static Integer[][]	SCBkills;


	public ExKrateiMatchCCRecord(int state, CCPlayer[] players)
	{
		_state = state;
		_players = players;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x89);

		writeD(_state); // 0x01 - in progress, 0x02 - finished
		writeD(_players.length);

		//Sorting the scores
		SCBnames = KrateisCubeManager.scoreboardnames;
		SCBkills = KrateisCubeManager.scoreboardkills;
		for (int i = 0; i <= 23; i++)
		{
			if (SCBkills[i][1] < SCBkills[i+1][1])
			{
				playername = SCBnames[i];
				SCBnames[i] = SCBnames[i+1];
				SCBnames[i+1] = playername;
				kills = SCBkills[i][1];
				SCBkills[i][1] = SCBkills[i+1][1];
				SCBkills[i+1][1] = kills;
				kills = SCBkills[i][0];
				SCBkills[i][0] = SCBkills[i+1][0];
				SCBkills[i+1][0] = kills;
				i=0;
			}
		}
		
		//Printing the scoreboard
		for (int i = 0; i <= 24; i++)
		{
			if (SCBkills[i][0] > 0)
			{
				playername = SCBnames[i];
				kills = SCBkills[i][1];

				writeS(playername);
				writeD(kills);
			}
		}

	}

	@Override
	public String getType()
	{
		return _S__FE_89_EXPVPMATCHCCRECORD;
	}
}
