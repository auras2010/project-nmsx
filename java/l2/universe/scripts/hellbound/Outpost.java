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

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javolution.util.FastList;

import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.instancemanager.HellboundManager;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;

/**
 * 
 * @author DS, based on theOne's work
 *
 */
public class Outpost extends Quest
{
	private static final int CAPTAIN = 18466;
	private static final int DEFENDER = 22358;

	private static final int TIMEOUT = 60000;

	private static volatile boolean isAttacked = false;
	private static long lastAttack = 0;
	private static L2Npc boss = null;
	private static List<L2Npc> defenders = new FastList<L2Npc>();
	private static ScheduledFuture<?> checkTask = null;

	@Override
	public final String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (!isAttacked)
		{
			isAttacked = true;
			ThreadPoolManager.getInstance().scheduleGeneral(new Attack(player), 5000);
			if (checkTask == null)
				checkTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Check(), TIMEOUT, TIMEOUT);

			return "18466.htm";
		}

		return null;
	}

	@Override
	public final String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		lastAttack = System.currentTimeMillis();

		if (!isAttacked)
		{
			isAttacked = true;
			ThreadPoolManager.getInstance().scheduleGeneral(new Attack(attacker), 100);
			if (checkTask == null)
				checkTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Check(), TIMEOUT, TIMEOUT);
		}

		return super.onAttack(npc, attacker, damage, isPet);
	}

	@Override
	public final String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (checkTask != null)
		{
			checkTask.cancel(false);
			checkTask = null;
		}

		HellboundManager.getInstance().setLevel(9);

		return super.onKill(npc, killer, isPet);
	}

	@Override
	public final String onSpawn(L2Npc npc)
	{
		npc.setIsNoRndWalk(true);

		if (npc.getNpcId() == CAPTAIN)
		{
			boss = npc;
			npc.setAutoAttackable(false);
		}
		else if (!isAttacked)
		{
			if (!defenders.contains(npc))
				defenders.add(npc);

			ThreadPoolManager.getInstance().scheduleGeneral(new Delete(npc), 100);
		}

		return super.onSpawn(npc);
	}

	private class Attack implements Runnable
	{
		final L2PcInstance _player;

		public Attack(L2PcInstance player)
		{
			_player = player;
		}

		@Override
		public void run()
		{
			for (L2Npc npc : defenders)
			{
				try
				{
					npc.getSpawn().startRespawn();
					if (npc.isDecayed())
						npc.setDecayed(false);
					if (npc.isDead())
						npc.doRevive();

					npc.spawnMe(npc.getSpawn().getLocx(), npc.getSpawn().getLocy(), npc.getSpawn().getLocz());

					npc.setIsRunning(true);
					((L2Attackable)npc).addDamageHate(_player, 0, 1);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, _player);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			try
			{
				boss.setAutoAttackable(true);
				boss.setIsRunning(true);
				((L2Attackable)boss).addDamageHate(_player, 0, 1);
				boss.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, _player);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private class Delete implements Runnable
	{
		private final L2Npc _npc;

		public Delete(L2Npc npc)
		{
			_npc = npc;
		}

		@Override
		public void run()
		{
			_npc.getSpawn().stopRespawn();
			_npc.deleteMe();
		}
	}

	private class Check implements Runnable
	{
		@Override
		public void run()
		{
			if (System.currentTimeMillis() - lastAttack > TIMEOUT)
			{
				isAttacked = false;
				for (L2Npc npc : defenders)
				{
					try
					{
						if (npc.isVisible())
						{
							npc.getSpawn().stopRespawn();
							((L2Attackable)npc).clearAggroList();
							npc.onDecay();
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				try
				{
					boss.setAutoAttackable(false);
					((L2Attackable)boss).clearAggroList();
					boss.onDecay();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				checkTask.cancel(false);
				checkTask = null;
			}
		}
	}

	public Outpost(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addFirstTalkId(CAPTAIN);
		addAttackId(CAPTAIN);
		addAttackId(DEFENDER);
		addKillId(CAPTAIN);
		addSpawnId(CAPTAIN);
		addSpawnId(DEFENDER);
	}

	public static void main(String[] args)
	{
		new Outpost(-1, Outpost.class.getSimpleName(), "hellbound");
	}
}