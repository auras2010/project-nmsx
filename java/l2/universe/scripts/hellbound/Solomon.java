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
package l2.universe.scripts.hellbound;

import l2.universe.gameserver.instancemanager.HellboundManager;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;

public class Solomon extends Quest
{
	private static final int SOLOMON = 32355;

	@Override
	public final String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		switch (HellboundManager.getInstance().getLevel())
		{
			case 5:
				return "32355-01.htm";
			case 6:
			case 7:
			case 8:
				return "32355-02.htm";
			default:
				return "32355-03.htm";
		}
	}

	public Solomon(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addFirstTalkId(SOLOMON);
	}

	public static void main(String[] args)
	{
		new Solomon(-1, Solomon.class.getSimpleName(), "hellbound");
	}
}