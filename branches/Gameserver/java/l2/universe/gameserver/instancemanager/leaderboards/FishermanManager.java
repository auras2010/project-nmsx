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
package l2.universe.gameserver.instancemanager.leaderboards;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javolution.util.FastMap;
import l2.universe.Config;
import l2.universe.database.ItemsDbTask;
import l2.universe.gameserver.Announcements;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ItemList;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.util.Util;


/**
 * @author  Evilus
 */
public class FishermanManager
{
	private static FishermanManager	_instance;
	protected static final Logger	_log					= Logger.getLogger(FishermanManager.class.getName());
	public Map<Integer, FishRank>	_ranks					= new FastMap<Integer, FishRank>();
	protected Future<?>				_actionTask				= null;
	protected int					TASK_DELAY			= Config.MOD_FISHERMAN_INTERVAL;
	protected Long					nextTimeUpdateReward	= 0L;
	
	public static FishermanManager getInstance()
	{
		if (_instance == null)
			_instance = new FishermanManager();
		
		return _instance;
	}
	
	public FishermanManager()
	{
		engineInit();
	}
	
	public void onCatch(int owner, String name)
	{
		FishRank ar = null;
		if (_ranks.get(owner) == null)
			ar = new FishRank();
		else
			ar = _ranks.get(owner);
		
		ar.cought();
		ar.name = name;
		_ranks.put(owner, ar);
	}
	
	public void onEscape(int owner, String name)
	{
		FishRank ar = null;
		if (_ranks.get(owner) == null)
			ar = new FishRank();
		else
			ar = _ranks.get(owner);
		
		ar.escaped();
		ar.name = name;
		_ranks.put(owner, ar);
	}
	
	public void stopTask()
	{
		if (_actionTask != null)
			_actionTask.cancel(true);
		
		_actionTask = null;
	}
	
	public class FishermanTask implements Runnable
	{
		@Override
		public void run()
		{
			_log.info("FishManager: Autotask init.");
			formRank();
			nextTimeUpdateReward = System.currentTimeMillis() + TASK_DELAY * 60000;
		}
	}
	
	public void startTask()
	{
		if (_actionTask == null)
			_actionTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new FishermanTask(), 1000, TASK_DELAY * 60000);
	}
	
	public void formRank()
	{
		final Map<Integer, Integer> scores = new FastMap<Integer, Integer>();
		for (final int obj : _ranks.keySet())
		{
			final FishRank ar = _ranks.get(obj);
			scores.put(obj, ar.cought - ar.escaped);
		}
		
		int Top = -1;
		int idTop = 0;
		for (final int id : scores.keySet())
			if (scores.get(id) > Top)
			{
				idTop = id;
				Top = scores.get(id);
			}
		
		final FishRank arTop = _ranks.get(idTop);
		
		if (arTop == null)
		{
			Announcements.getInstance().announceToAll("Fisherman: No winners at this time!");
			_ranks.clear();
			return;
		}
		
		final L2PcInstance winner = L2World.getInstance().getPlayer(idTop);
		
		Announcements.getInstance().announceToAll("Attention Fishermans: " + arTop.name + " is the winner for this time with " + arTop.cought + "/" + arTop.escaped + ". Next calculation in " + Config.MOD_FISHERMAN_INTERVAL + " min(s).");
		if (Config.MOD_FISHERMAN_REWARD_ID > 0 && Config.MOD_FISHERMAN_REWARD_COUNT > 0)
			if(winner!=null)
			{
				winner.getInventory().addItem("FishManager", Config.MOD_FISHERMAN_REWARD_ID, Config.MOD_FISHERMAN_REWARD_COUNT, winner, null);
				if (Config.MOD_FISHERMAN_REWARD_COUNT > 1) //You have earned $s1.
					winner.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(Config.MOD_FISHERMAN_REWARD_ID).addNumber(Config.MOD_FISHERMAN_REWARD_COUNT));
				else
					winner.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(Config.MOD_FISHERMAN_REWARD_ID));
				winner.sendPacket(new ItemList(winner, false));
			}
			else
				ItemsDbTask.getInstance().addItemToOfflineChar(arTop.name, idTop, "ArenaManager", Config.MOD_FISHERMAN_REWARD_ID, Config.MOD_FISHERMAN_REWARD_COUNT, true);
		_ranks.clear();
	}
	
	public String showHtm(int owner)
	{
		Map<Integer, Integer> scores = new FastMap<Integer, Integer>();
		for (final int obj : _ranks.keySet())
		{
			final FishRank ar = _ranks.get(obj);
			scores.put(obj, ar.cought - ar.escaped);
		}
		
		scores = Util.sortMap(scores, false);
		
		int counter = 0;
		final int max = 20;
		String pt = "<html><body><center>" + "<font color=\"cc00ad\">TOP " + max + " Fisherman</font><br>";
		
		pt += "<table width=260 border=0 cellspacing=0 cellpadding=0 bgcolor=333333>";
		pt += "<tr> <td align=center>No.</td> <td align=center>Name</td> <td align=center>Cought</td> <td align=center>Escaped</td> </tr>";
		pt += "<tr> <td align=center>&nbsp;</td> <td align=center>&nbsp;</td> <td align=center></td> <td align=center></td> </tr>";
		boolean inTop = false;
		for (final int id : scores.keySet())
			if (counter < max)
			{
				final FishRank ar = _ranks.get(id);
				pt += tx(counter, ar.name, ar.cought, ar.escaped, id == owner);
				if (id == owner)
					inTop = true;
				
				counter++;
			}
			else
				break;
		
		if (!inTop)
		{
			final FishRank arMe = _ranks.get(owner);
			if (arMe != null)
			{
				pt += "<tr> <td align=center>...</td> <td align=center>...</td> <td align=center>...</td> <td align=center>...</td> </tr>";
				int placeMe = 0;
				for (final int idMe : scores.keySet())
				{
					placeMe++;
					if (idMe == owner)
						break;
				}
				pt += tx(placeMe, arMe.name, arMe.cought, arMe.escaped, true);
			}
		}
		
		pt += "</table>";
		pt += "<br><br>";
		if (Config.MOD_FISHERMAN_REWARD_ID > 0 && Config.MOD_FISHERMAN_REWARD_COUNT > 0)
		{
			pt += "Next Reward Time in <font color=\"LEVEL\">" + calcMinTo() + " min(s)</font><br1>";
			pt += "<font color=\"aadd77\">" + Config.MOD_FISHERMAN_REWARD_COUNT + " &#" + Config.MOD_FISHERMAN_REWARD_ID + ";</font>";
		}
		
		pt += "</center></body></html>";
		
		return pt;
	}
	
	private int calcMinTo()
	{
		return ((int) (nextTimeUpdateReward - System.currentTimeMillis())) / 60000;
	}
	
	private String tx(int counter, String name, int kills, int deaths, boolean mi)
	{
		String t = "";
		
		t += "	<tr>" + "<td align=center>" + (mi ? "<font color=\"LEVEL\">" : "") + (counter + 1) + ".</td>" + "<td align=center>" + name + "</td>"
		+ "<td align=center>" + kills + "</td>" + "<td align=center>" + deaths + "" + (mi ? "</font>" : "") + " </td>" + "</tr>";
		
		return t;
	}
	
	public void engineInit()
	{
		_ranks = new FastMap<Integer, FishRank>();
		startTask();
		_log.info(getClass().getSimpleName()+": Initialized");
	}
	
	
	public class FishRank
	{
		public int		cought, escaped;
		public String	name;
		
		public FishRank()
		{
			cought = 0;
			escaped = 0;
		}
		
		public void cought()
		{
			cought++;
		}
		
		public void escaped()
		{
			escaped++;
		}
	}
}