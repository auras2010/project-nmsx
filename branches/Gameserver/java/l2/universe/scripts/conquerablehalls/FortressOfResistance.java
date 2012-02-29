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

import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import l2.universe.gameserver.GameTimeController;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.datatables.ClanTable;
import l2.universe.gameserver.datatables.NpcTable;
import l2.universe.gameserver.datatables.SpawnTable;
import l2.universe.gameserver.instancemanager.CHSiegeManager;
import l2.universe.gameserver.model.L2Clan;
import l2.universe.gameserver.model.L2Spawn;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.clanhall.SiegableHall;
import l2.universe.gameserver.model.quest.Quest;

/**
 * @author BiggBoss
 * Fortress of Resistance clan hall siege Script
 */
public class FortressOfResistance extends Quest
{
	private static final Logger _log = Logger.getLogger(FortressOfResistance.class.getName());
	
	private static class SiegeStart implements Runnable
	{
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			if(_fortress == null)
				_fortress = CHSiegeManager.getInstance().getSiegableHall(FORTRESS);
			
			int hoursLeft = (GameTimeController.getInstance().getGameTime() / 60) % 24;
			
			if(hoursLeft < 0 || hoursLeft > 6)
			{
				long scheduleTime = (24 - hoursLeft) * 10 * 60000;
				ThreadPoolManager.getInstance().scheduleGeneral(new SiegeStart(), scheduleTime);
				return;
			}
						
			if(_bloodyLordNuka == null)
			{
				_log.warning("CHSiegeManager: Raid Boss is null!");
				return;
			}
			_bloodyLordNuka.init();
			_inSiege = true;
			
			_fortress.siegeStarts();
			
			_siegeEnd = ThreadPoolManager.getInstance().scheduleGeneral(new SiegeEnd(null), _fortress.getSiegeLenght());
		}
	}
	
	private static class SiegeEnd implements Runnable
	{
		private L2Clan _winner;
		
		private SiegeEnd(L2Clan winner)
		{
			_winner = winner;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			if(_fortress == null)
				_fortress = CHSiegeManager.getInstance().getSiegableHall(FORTRESS);

			_inSiege = false;
			if(_winner != null)
			{
				int oldOwnerId = _fortress.getOwnerId();
				L2Clan oldOwner = ClanTable.getInstance().getClan(oldOwnerId);
				if(oldOwner != null)
				{
					_fortress.free();
					oldOwner.setHasHideout(0);
					oldOwner.broadcastClanStatus();
				}
				_fortress.setOwner(_winner);
			}
			
			_fortress.siegeEnds();
			_nextSiege = ThreadPoolManager.getInstance().scheduleGeneral(new SiegeStart(), _fortress.getNextSiegeTime());
		}
	}
	
	private static final String qn = "FortressOfResistance";
	private static final int FORTRESS = 21;
	
	private static final int BLOODY_LORD_NURKA = 35375;
	
	private static SiegableHall _fortress;
	private static boolean _inSiege = false;
	private static ScheduledFuture<?> _nextSiege, _siegeEnd;
	private static L2Spawn _bloodyLordNuka;
	
	/**
	 * @param questId
	 * @param name
	 * @param descr
	 */
	public FortressOfResistance(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addKillId(BLOODY_LORD_NURKA);
		_fortress = CHSiegeManager.getInstance().getSiegableHall(FORTRESS);
		long siegeTime = _fortress.getNextSiegeTime();
		if(siegeTime == -1)
		{
			_log.warning("CHSiegeManager: No date setted for Fortress of Ressistance siege!");
		}
		else
		{
			try
			{
				setSpawn();
				_nextSiege = ThreadPoolManager.getInstance().scheduleGeneral(new SiegeStart(), siegeTime);
			}
			catch(Exception e)
			{
				_log.warning("Fortress of Resistance: Couldnt set blody nurka spawn, siege cancelled");
			}
		}
	}
	
	private void setSpawn() throws Exception
	{
		_bloodyLordNuka = new L2Spawn(NpcTable.getInstance().getTemplate(BLOODY_LORD_NURKA));
		_bloodyLordNuka.setLocx(44525);
		_bloodyLordNuka.setLocy(108867);
		_bloodyLordNuka.setLocz(-2020);
		_bloodyLordNuka.setHeading(1);
		_bloodyLordNuka.setAmount(1);
		_bloodyLordNuka.setRespawnDelay(10800000);
		SpawnTable.getInstance().addNewSpawn(_bloodyLordNuka, false);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if(!_inSiege)
			return null;
		
		if(npc.getNpcId() == BLOODY_LORD_NURKA)
		{
			L2Clan clan = killer.getClan();
			if(clan != null && _fortress.getAttackers().contains(clan))
			{
				synchronized(this)
				{
					if(_siegeEnd != null)
						_siegeEnd.cancel(false);
					ThreadPoolManager.getInstance().executeTask(new SiegeEnd(clan));
				}
			}
		}
		return super.onKill(npc, killer, isPet);
	}
	
	public static void launchSiege()
	{
		ThreadPoolManager.getInstance().executeTask(new SiegeStart());
	}
	
	public static void endSiege()
	{
		_siegeEnd.cancel(false);
		ThreadPoolManager.getInstance().executeTask(new SiegeEnd(null));
	}
	
	public static void updateAdminDate(long date)
	{
		_fortress.setNextSiegeDate(date);
		_fortress.prepareSiege();
		_nextSiege.cancel(true);
		_nextSiege = ThreadPoolManager.getInstance().scheduleGeneral(new SiegeStart(), _fortress.getNextSiegeTime());
	}

	public static void main(String[] args)
	{
		new FortressOfResistance(-1, qn, "conquerablehalls");
	}
}