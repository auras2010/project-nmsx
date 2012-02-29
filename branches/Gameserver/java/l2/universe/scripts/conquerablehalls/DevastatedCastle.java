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
 * Devastated Castle clan hall siege script
 */
public class DevastatedCastle extends Quest
{	
	private static class SiegeStart implements Runnable
	{
		@Override
		public void run()
		{
			if(_devastatedCastle == null)
				_devastatedCastle = CHSiegeManager.getInstance().getSiegableHall(DEVASTATED);
			
			_devastatedCastle.siegeStarts();
			
			_siegeEnd = ThreadPoolManager.getInstance().scheduleGeneral(new SiegeEnd(false), _devastatedCastle.getSiegeLenght());
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
			if(_devastatedCastle == null)
				_devastatedCastle = CHSiegeManager.getInstance().getSiegableHall(DEVASTATED);
			
			if(_isKilled)
			{
				L2Clan winner = getWinner();
				if(winner != null)
					_devastatedCastle.setOwner(winner);
			}
			
			_devastatedCastle.siegeEnds();
			
			_nextSiege = ThreadPoolManager.getInstance().scheduleGeneral(new SiegeStart(), _devastatedCastle.getNextSiegeTime());
		}
	}
	
	private static final Logger _log = Logger.getLogger(DevastatedCastle.class.getName());
	private static final String qn = "DevastatedCastle";
	
	private static final int DEVASTATED = 34;
	private static final int GUSTAV = 35410;
	
	private static SiegableHall _devastatedCastle;
	private static TIntIntHashMap _damageToGustav = new TIntIntHashMap();
	private static ScheduledFuture<?> _nextSiege, _siegeEnd;
	
	/**
	 * @param questId
	 * @param name
	 * @param descr
	 */
	public DevastatedCastle(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addKillId(GUSTAV);
		addAttackId(GUSTAV);
		
		_devastatedCastle = CHSiegeManager.getInstance().getSiegableHall(DEVASTATED);
		
		long delay = _devastatedCastle.getNextSiegeTime();
		if(delay == -1)
			_log.warning("Devastated Castle: No date setted for Devastated Castle Siege!");
		else
		{
			_nextSiege = ThreadPoolManager.getInstance().scheduleGeneral(new SiegeStart(), delay);
			_devastatedCastle.prepareSiege();
		}
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if(!_devastatedCastle.isInSiege())
			return null;
		
		if(npc.getNpcId() == GUSTAV)
		{
			synchronized(this)
			{
				final L2Clan clan = attacker.getClan();
				
				if(clan != null && _devastatedCastle.getAttackers().contains(clan))
				{
					final int id = clan.getClanId();
					if(_damageToGustav.containsKey(id))
					{
						int newDamage = _damageToGustav.get(id);
						newDamage += damage;
						_damageToGustav.put(id, newDamage);
					}
					else
						_damageToGustav.put(id, damage);
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if(!_devastatedCastle.isInSiege()) 
			return null;
		
		final int npcId = npc.getNpcId();
		
		if(npcId == GUSTAV)
		{
			L2Clan clan = killer.getClan();
			if(clan != null && _devastatedCastle.getAttackers().contains(clan))
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
		double counter = 0;
		int damagest = 0;
		for(int clan : _damageToGustav.keys())
		{
			final double damage = _damageToGustav.get(clan);
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
		_devastatedCastle.setNextSiegeDate(date);
		_devastatedCastle.prepareSiege();
		_nextSiege.cancel(true);
		_nextSiege = ThreadPoolManager.getInstance().scheduleGeneral(new SiegeStart(), date - System.currentTimeMillis());
	}
	
	public static void main(String[] args)
	{
		new DevastatedCastle(-1, qn, "conquerablehalls");
	}
}