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
package ai.instances;

import java.util.Collection;

import javolution.util.FastList;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.clientpackets.Say2;
import l2.universe.gameserver.network.serverpackets.CreatureSay;
import l2.universe.util.Rnd;
import l2.universe.scripts.ai.L2AttackableAIScript;

public class FollowerOfMontagnar extends L2AttackableAIScript
{
	private static final int MONTAGNAR = 18568;
	private static final int FOFMONTAGNAR = 18569;
	private FastList<L2Attackable>  _minions = new FastList<L2Attackable>();
	
	public FollowerOfMontagnar(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addKillId(MONTAGNAR);
		addAggroRangeEnterId(MONTAGNAR);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (npc.getNpcId() == MONTAGNAR)
		{
			final L2Attackable follower1 = (L2Attackable) addSpawn(FOFMONTAGNAR, npc.getX()+(Rnd.get(50)), npc.getY()+(Rnd.get(50)), npc.getZ(), 0, false, 0, false, npc.getInstanceId());
			final L2Attackable follower2 = (L2Attackable) addSpawn(FOFMONTAGNAR, npc.getX()+(Rnd.get(50)), npc.getY()+(Rnd.get(50)), npc.getZ(), 0, false, 0,false, npc.getInstanceId());
			_minions.add(follower1);
			_minions.add(follower2);
			follower1.setIsInvul(true);
			follower2.setIsInvul(true);
			npc.broadcastPacket(new CreatureSay(npc.getObjectId(), Say2.ALL, npc.getName(), player.getName()+"! Get him!!!"));
			for (L2Attackable minion : _minions)
			{
				minion.clearAggroList();
				minion.setIsRunning(true);
				minion.addDamageHate(player, 1, 99999);
				minion.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
			}
			startQuestTimer("changetarget", 10000, npc, null, true);
		}
		return super.onAggroRangeEnter(npc, player, isPet);
	}
		
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("changetarget"))
		{
			final Collection<L2Character> knows = npc.getKnownList().getKnownCharactersInRadius(2000);
			if (knows.isEmpty())
			{
				cancelQuestTimers("changetarget");
				return null;
			}
			
			L2PcInstance target = null;
			for (L2Character c : knows)
			{
				if (c instanceof L2PcInstance)
				{
					if (Rnd.get(100) <= 50)
					{
						target = (L2PcInstance) c;
						break;
					}
				}
			}
			
			if (target != null)
			{
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), Say2.ALL, npc.getName(), target.getName()+"! Get him!!!"));
			
				for (L2Attackable minion : _minions)
				{
					minion.clearAggroList();
					minion.setIsRunning(true);
					minion.addDamageHate(target, 1, 99999);
					minion.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		cancelQuestTimers("changetarget");
		for (L2Attackable minion : _minions)
		{
			if (npc.getInstanceId() == minion.getInstanceId())
			{
				minion.decayMe();
				_minions.remove(minion);
			}
		}
		return super.onKill(npc, killer, isPet);
	}
	
	public static void main(String[] args)
	{
		new FollowerOfMontagnar(-1, "FollowerOfMontagnar", "ai");
	}
}