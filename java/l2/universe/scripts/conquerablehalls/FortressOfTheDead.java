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

package l2.universe.scripts.conquerablehalls;

import gnu.trove.TIntIntHashMap;

import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.datatables.ClanTable;
import l2.universe.gameserver.instancemanager.CHSiegeManager;
import l2.universe.gameserver.model.L2Clan;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.clanhall.SiegableHall;
import l2.universe.gameserver.model.quest.Quest;

/**
 * @author BiggBoss
 * Fortress of the Dead clan hall siege script
 */
public class FortressOfTheDead extends Quest
{	
	private static class SiegeStart implements Runnable
	{
		@Override
		public void run()
		{
			if(_fortress == null)
				_fortress = CHSiegeManager.getInstance().getSiegableHall(FDEAD);

			_fortress.siegeStarts();
			
			_siegeEnd = ThreadPoolManager.getInstance().scheduleGeneral(new SiegeEnd(false), _fortress.getSiegeLenght());
		}
	}
	
	private static class SiegeEnd implements Runnable
	{		
		private boolean _isKilled;
		
		private SiegeEnd(boolean isKilled)
		{
			_isKilled = isKilled;
		}
		
		@Override
		public void run()
		{
			if(_fortress == null)
				_fortress = CHSiegeManager.getInstance().getSiegableHall(FDEAD);

			if(_isKilled)
			{
				L2Clan winner = getWinner();
				if(winner != null)
					_fortress.setOwner(winner);	
			}
			
			_fortress.siegeEnds();
			
			_nextSiege = ThreadPoolManager.getInstance().scheduleGeneral(new SiegeStart(), _fortress.getNextSiegeTime());
		}
	}
	
	private static final Logger _log = Logger.getLogger(FortressOfTheDead.class.getName());
	private static final String qn = "FortressOfTheDead";
	
	private static final int FDEAD = 64;
	private static final int LIDIA = 35629;
	
	private static SiegableHall _fortress;
	private static TIntIntHashMap _damageToLidia = new TIntIntHashMap();
	private static ScheduledFuture<?> _nextSiege, _siegeEnd;
	
	/**
	 * @param questId
	 * @param name
	 * @param descr
	 */
	public FortressOfTheDead(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addKillId(LIDIA);
		addAttackId(LIDIA);

		_fortress = CHSiegeManager.getInstance().getSiegableHall(FDEAD);
		if(_fortress != null)
		{
			long delay = _fortress.getNextSiegeTime();
			if(delay == -1)
				_log.warning("CHSiegeManager: No date setted for Fortress of the Dead Siege!");
			else
			{
				_nextSiege = ThreadPoolManager.getInstance().scheduleGeneral(new SiegeStart(), delay);
				_fortress.prepareSiege();
			}
		}
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if(!_fortress.isInSiege())
			return null;
		
		if(npc.getNpcId() == LIDIA)
		{
			synchronized(this)
			{
				final L2Clan clan = attacker.getClan();
				
				if(clan != null && _fortress.getAttackers().contains(clan))
				{
					final int id = clan.getClanId();
					if(_damageToLidia.containsKey(id))
					{
						int newDamage = _damageToLidia.get(id);
						newDamage += damage;
						_damageToLidia.put(id, newDamage);
					}
					else
						_damageToLidia.put(id, damage);
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if(!_fortress.isInSiege()) return null;
		
		final int npcId = npc.getNpcId();
		
		if(npcId == LIDIA)
		{
			L2Clan clan = killer.getClan();
			if(clan != null && _fortress.getAttackers().contains(clan))
			{
				synchronized(this)
				{
					if(_siegeEnd != null)
						_siegeEnd.cancel(false);
					ThreadPoolManager.getInstance().executeTask(new SiegeEnd(true));
				}
			}
		}
			
		return super.onKill(npc, killer, isPet);
	}
				
	private static L2Clan getWinner()
	{
		int counter = 0;
		int damagest = 0;
		for(int clan : _damageToLidia.keys())
		{
			final int damage = _damageToLidia.get(clan);
			if(damage > counter)
			{
				counter = damage;
				damagest = clan;
			}
		}
		L2Clan winner = ClanTable.getInstance().getClan(damagest);
		return winner;
	}
	
	public static void launchSiege()
	{
		ThreadPoolManager.getInstance().executeTask(new SiegeStart());
	}
	
	public static void endSiege()
	{
		_siegeEnd.cancel(false);
		ThreadPoolManager.getInstance().executeTask(new SiegeEnd(false));
	}
	
	public static void updateAdminDate(long date)
	{
		_fortress.setNextSiegeDate(date);
		_fortress.prepareSiege();
		_nextSiege.cancel(true);
		_nextSiege = ThreadPoolManager.getInstance().scheduleGeneral(new SiegeStart(), date - System.currentTimeMillis());
	}
	
	public static void main(String[] args)
	{
		new FortressOfTheDead(-1, qn, "conquerablehalls");
	}
}