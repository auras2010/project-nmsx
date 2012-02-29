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
package ai.zones;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.CreatureSay;
import l2.universe.scripts.ai.L2AttackableAIScript;

public class FieldOfWhispersSilence extends L2AttackableAIScript
{
	private static final int BRAZIER_OF_PURITY = 18806;
	private static final int GUARDIAN_SPIRITS_OF_MAGIC_FORCE = 22659;
	
	public FieldOfWhispersSilence(int questId, String name, String descr)
	{
		super(questId, name, descr);

		addAggroRangeEnterId(BRAZIER_OF_PURITY);
		addAggroRangeEnterId(GUARDIAN_SPIRITS_OF_MAGIC_FORCE);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		switch (npc.getNpcId())
		{
			case BRAZIER_OF_PURITY:
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), "The Magic Force is being threatened... Protect the Magic Force, Guardian Spirits...!"));
				break;
			case GUARDIAN_SPIRITS_OF_MAGIC_FORCE:
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), "Magic Force must be protected even this life is sacrif iced in return!"));
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new FieldOfWhispersSilence(-1, "FieldOfWhispersSilence", "ai");
	}
}
