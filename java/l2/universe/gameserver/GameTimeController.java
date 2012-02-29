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
package l2.universe.gameserver;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastMap;

import l2.universe.Config;
import l2.universe.gameserver.ai.CtrlEvent;
import l2.universe.gameserver.ai.L2CharacterAI;
import l2.universe.gameserver.instancemanager.DayNightSpawnManager;
import l2.universe.gameserver.model.actor.L2Character;

/**
 * Controls time of the gameserver. One in-game day is 4 real hours.
 */
public class GameTimeController
{
	/**
	 * Logger.
	 */
	protected static final Logger _log = Logger.getLogger(GameTimeController.class.getName());
	/**
	 * How many gameserver ticks are in one second.
	 */
	public static final int TICKS_PER_SECOND = 10;
	/**
	 * How many milliseconds are in one tick.
	 */
	public static final int MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;
	/**
	 * How many in-game days are in real day.
	 */
	private static final int IG_DAYS_IN_RL_DAY = 6;
	/**
	 * Length of in-game day in milliseconds.
	 */
	private static final int INGAME_DAY_LENGTH = 4 * 60 * 60 * 1000;
	/**
	 * At what hour in-game time starts on server start.
	 */
	private static final int GAME_START_OFFSET = Config.SERVER_START_INGAME_HOUR;
	/**
	 * Time in milliseconds when gameserver started.
	 */
	protected static final long GAME_START_TIME = System.currentTimeMillis();
	/**
	 * Map of moving objects.
	 */
	private static final FastMap<Integer, L2Character> _movingObjects = new FastMap<Integer, L2Character>().shared(); 
	/**
	 * Game ticks counter.
	 */
	protected static volatile int _gameTicks;
	/**
	 * Whether it's night now.
	 */
	protected static volatile boolean _isNight = GAME_START_OFFSET >= 0 && GAME_START_OFFSET < 6;
	/**
	 * Whether game time controller is being stopped.
	 */
	protected static volatile boolean _interruptRequest;
	/**
	 * Timer thread. It takes care of updating gameserver ticks.
	 */
	private final TimerThread _timer = new TimerThread();
	
	/**
	 * Creates new instance of GameTimeController.
	 */
	private GameTimeController()
	{
		init();
	}
	
	/**
	 * Returns instance of GameTimeController.
	 *
	 * @return instance of GameTimeController
	 */
	public static GameTimeController getInstance()
	{
		return SingletonHolder._instance;
	}
	
	/**
	 * Starts game time controller initialization.
	 */
	public void init()
	{
		_timer.start();
		
		// we count the time till next midnight in game, and that of course
		// depends on game start offset
		final long startNightSpawnOffset = (long) (INGAME_DAY_LENGTH * (1.0 - GAME_START_OFFSET / 24.0)) % INGAME_DAY_LENGTH;
		
		// in game day starts at 6am, which is 0.25 of day, but to prevent
		// negative time of first invocation of day switch task, we put one day
		// more, which is in fact 1.25
		final long startDaySpawnOffset = (long) (INGAME_DAY_LENGTH * (1.25 - GAME_START_OFFSET / 24.0)) % INGAME_DAY_LENGTH;
		
		// we never run spawning of mobs in init() because it's too early and
		// npcs are not loaded yet, in fact this is handled in GameServer by
		// call to DayNightSpawnManager.getInstance().notifyChangeMode()
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new DayNightChanger(true), startNightSpawnOffset == 0 ? INGAME_DAY_LENGTH : startNightSpawnOffset, INGAME_DAY_LENGTH);
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new DayNightChanger(false), startDaySpawnOffset == 0 ? INGAME_DAY_LENGTH : startDaySpawnOffset, INGAME_DAY_LENGTH);
	}
	
	/**
	 * Returns true if it's night, otherwise false.
	 *
	 * @return true if it's night, otherwise false
	 */
	public boolean isNowNight()
	{
		return _isNight;
	}
	
	/**
	 * Returns game time in minutes.
	 *
	 * @return game time in minutes
	 */
	public int getGameTime()
	{
		return getInGameHour() * 60 + getInGameMinute();
	}
	
	/**
	 * Returns in-game hour.
	 *
	 * @return in-game hour
	 */
	public int getInGameHour()
	{
		final int inGameMillis = (_gameTicks * MILLIS_IN_TICK) % INGAME_DAY_LENGTH;
		
		return (inGameMillis * IG_DAYS_IN_RL_DAY / (60 * 60 * 1000) + GAME_START_OFFSET) % 24;
	}
	
	/**
	 * Returns in-game minute.
	 *
	 * @return in-game minute
	 */
	public int getInGameMinute()
	{
		final int inGameMillis = (_gameTicks * MILLIS_IN_TICK) % INGAME_DAY_LENGTH;
		
		return inGameMillis * IG_DAYS_IN_RL_DAY / (60 * 1000) % 60;
	}
	
	/**
	 * Returns game ticks since gameserver start.
	 * 
	 * @return game ticks since gameserver start
	 */
	public static int getGameTicks()
	{
		return _gameTicks;
	}
	
	/**
	 * Add a L2Character to movingObjects of GameTimeController.<BR><BR>
	 *
	 * <B><U> Concept</U> :</B><BR><BR>
	 * All L2Character in movement are identified in <B>movingObjects</B> of GameTimeController.<BR><BR>
	 *
	 * @param cha The L2Character to add to movingObjects of GameTimeController
	 *
	 */
	public void registerMovingObject(L2Character cha)
	{
		if (cha == null)
			return;
		
		_movingObjects.putIfAbsent(cha.getObjectId(), cha);
	}
	
	/**
	 * Move all L2Characters contained in movingObjects of GameTimeController.<BR><BR>
	 *
	 * <B><U> Concept</U> :</B><BR><BR>
	 * All L2Character in movement are identified in <B>movingObjects</B> of GameTimeController.<BR><BR>
	 *
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Update the position of each L2Character </li>
	 * <li>If movement is finished, the L2Character is removed from movingObjects </li>
	 * <li>Create a task to update the _knownObject and _knowPlayers of each L2Character that finished its movement and of their already known L2Object then notify AI with EVT_ARRIVED </li><BR><BR>
	 *
	 */
	protected void moveObjects()
	{
		// Go throw the table containing L2Character in movement
		Iterator<Map.Entry<Integer, L2Character>> it = _movingObjects.entrySet().iterator();
		
		while (it.hasNext())
		{
			// If movement is finished, the L2Character is removed from
			// movingObjects and added to the ArrayList ended
			L2Character ch = it.next().getValue();
			if (ch.updatePosition(_gameTicks))
			{
				it.remove();
				ThreadPoolManager.getInstance().executeTask(new MovingObjectArrived(ch));
			}
		}
	}
	
	/**
	 * Stops the timer that updates game ticks.
	 */
	public void stopTimer()
	{
		_interruptRequest = true;
		_timer.interrupt();
	}
	
	/**
	 * Takes care of updating game ticks.
	 */
	class TimerThread extends Thread
	{
		/**
		 * Creates new instance of TimerThread.
		 */
		public TimerThread()
		{
			super("GameTimeController");
			setDaemon(true);
			setPriority(MAX_PRIORITY);
		}
		
		@Override
		public void run()
		{
			for (;;)
			{
				try
				{
					// save old ticks value to avoid moving objects 2x in same tick
					final int oldTicks = _gameTicks;
					
					// from server boot to now
					long runtime = System.currentTimeMillis() - GAME_START_TIME;
					
					// new ticks value (ticks now)
					_gameTicks = (int) (runtime / MILLIS_IN_TICK);
					
					if (oldTicks != _gameTicks)
						moveObjects();
					
					runtime = (System.currentTimeMillis() - GAME_START_TIME) - runtime;
					
					// calculate sleep time... time needed to next tick minus
					// time it takes to call moveObjects()
					final int sleepTime = 1 + MILLIS_IN_TICK - ((int) runtime) % MILLIS_IN_TICK;
					
					if (sleepTime > 0)
						Thread.sleep(sleepTime);
				}
				catch (InterruptedException ie)
				{
					if (_interruptRequest)
						return;
					
					_log.log(Level.SEVERE, "TimerThread interrupted", ie);
				}
				catch (final Exception e)
				{
					_log.log(Level.SEVERE, "Problem in TimerThread", e);
				}
			}
		}
	}
	
	/**
	 * Update the _knownObject and _knowPlayers of each L2Character that
	 * finished its movement and of their already known L2Object then notify AI
	 * with EVT_ARRIVED.
	 */
	class MovingObjectArrived implements Runnable
	{
		/**
		 * Character that ended its move.
		 */
		private final L2Character _ended;
		
		/**
		 * Creates new instance of MovingObjectArrived.
		 *
		 * @param ended character that ended its move
		 */
		MovingObjectArrived(L2Character ended)
		{
			_ended = ended;
		}
		
		@Override
		public void run()
		{
			try
			{
				// AI could be just disabled due to region turn off
				final L2CharacterAI characterAI = _ended.getAI();
				
				if (characterAI != null)
				{
					if (Config.MOVE_BASED_KNOWNLIST)
					{
						_ended.getKnownList().findObjects();
					}
					
					characterAI.notifyEvent(CtrlEvent.EVT_ARRIVED);
				}
			}
			catch (NullPointerException e)
			{
				_log.log(Level.SEVERE, "Error while processing moving object arrived", e);
			}
		}
	}
	
	class DayNightChanger implements Runnable
	{
		private final boolean isNight;
		
		public DayNightChanger(final boolean isNight)
		{
			this.isNight = isNight;
		}
		
		@Override
		public void run()
		{
			if (_isNight != isNight)
			{
				_isNight = isNight;
				DayNightSpawnManager.getInstance().notifyChangeMode();
			}
		}
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final GameTimeController _instance = new GameTimeController();
	}
}
