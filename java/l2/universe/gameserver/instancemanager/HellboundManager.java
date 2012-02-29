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
package l2.universe.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;
 
import javolution.util.FastMap;

import l2.universe.Config;
import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.datatables.NpcTable;
import l2.universe.gameserver.datatables.SpawnTable;
import l2.universe.gameserver.model.L2Spawn;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.templates.chars.L2NpcTemplate;
import l2.universe.util.Rnd;

public class HellboundManager
{
	private static final Logger _log = Logger.getLogger(HellboundManager.class.getName());

	private static final String LOAD_SPAWNS = "SELECT npc_templateid, locx, locy, locz, heading, " +
		"respawn_min_delay, respawn_max_delay, min_hellbound_level, " +
		"max_hellbound_level, max_trust_level, trust_value " +
		"FROM hellbound_spawnlist ORDER BY npc_templateid";
	private static final String LOAD_VAR = 	"SELECT var,value FROM hellbound_data";
	private static final String SAVE_VAR = 	"INSERT INTO hellbound_data (var,value) VALUES (?,?) ON DUPLICATE KEY UPDATE value=?";
	
	private int _level = 0;
	private int _trust = 0;
	private int _maxTrust = 0;
	
	private ScheduledFuture<?> _engine = null;
	private final Map<HellboundSpawn, L2Npc> _population;
	
	private HellboundManager()
	{
		_population = new FastMap<HellboundSpawn, L2Npc>();
		
		loadVars();
		loadSpawns();
	}
	
	public final int getLevel()
	{
		return _level;
	}
	
	public final synchronized void updateTrust(int t, boolean useRates)
	{	
		if (isLocked())	
			return;
		
		final int trust = Math.max(_trust + (useRates ? (int)(Config.RATE_TRUST * t) : t), 0);
		if (_maxTrust > 0)
			_trust = Math.min(trust, _maxTrust);
		
		else
			_trust = trust;
	}

	public final void setLevel(int lvl)
	{
		_level = lvl;
	}

	public final int getTrust()
	{
		return _trust;
	}

	public final int getMaxTrust()
	{
		return _maxTrust;
	}

	public final void setMaxTrust(int trust)
	{
		_maxTrust = trust;
		if (_maxTrust > 0 && _trust > _maxTrust)
			_trust = _maxTrust;
	}
	
 	/**
 	 * Returns true if Hellbound is locked
 	 */
	public final boolean isLocked()
 	{
		return _level == 0;
 	}
 	
	public final void unlock()
	{
		if (_level == 0)
			setLevel(1);
	}

	public final void registerEngine(Runnable r, int interval)
	{
		if (_engine != null)
			_engine.cancel(false);

		_engine = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(r, interval, interval);
	}

	public final void doSpawn()
	{
		int added = 0;
		int deleted = 0;
		for (HellboundSpawn spawnDat : _population.keySet())
		{
			try
			{
				if (spawnDat == null)
					continue;

				L2Npc npc = _population.get(spawnDat);
				if (_level < spawnDat.getMinLvl() || _level > spawnDat.getMaxLvl())
				{
					// npc should be removed
					spawnDat.stopRespawn();

					if (npc != null && npc.isVisible())
					{
						npc.deleteMe();
						deleted++;
					}
				}
				else
				{
					// npc should be added
					spawnDat.startRespawn();

					if (npc == null)
					{
						npc = spawnDat.doSpawn();
						_population.put(spawnDat, npc);
						added++;
					}
					else
					{
						if (npc.isDecayed())
							npc.setDecayed(false);
						if (npc.isDead())
							npc.doRevive();
						if (!npc.isVisible())
							added++;

						npc.setCurrentHp(npc.getMaxHp());
						npc.setCurrentMp(npc.getMaxMp());
						npc.spawnMe(spawnDat.getLocx(), spawnDat.getLocy(), spawnDat.getLocz());
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		if (added > 0)
			_log.info("HellboundManager: Spawned " + added + " NPCs.");
		if (deleted > 0)
			_log.info("HellboundManager: Removed " + deleted + " NPCs.");
	}

	public final void cleanUp()
	{
		saveVars();

		if (_engine != null)
		{
			_engine.cancel(true);
			_engine = null;
		}

		_population.clear();
	}

	private final void loadVars()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset;
		String var, value;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(LOAD_VAR);

			rset = statement.executeQuery();
			while (rset.next())
			{
				var = rset.getString(1);
				value = rset.getString(2);

				if ("level".equalsIgnoreCase(var))
					_level = Integer.parseInt(value);
				else if ("trust".equalsIgnoreCase(var))
					_trust = Integer.parseInt(value);
			}
		}
		catch (Exception e)
		{
			_log.warning("HellboundManager: problem while loading variables: " + e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	public final void saveVars()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SAVE_VAR);

			statement.setString(1, "level");
			statement.setInt(2, _level);
			statement.setInt(3, _level);
			statement.execute();

			statement.setString(1, "trust");
			statement.setInt(2, _trust);
			statement.setInt(3, _trust);
			statement.execute();

			_log.info("HellboundManager: Database updated.");
		}
		catch (Exception e)
		{
			_log.warning("HellboundManager: problem while saving variables: " + e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	private final void loadSpawns()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(LOAD_SPAWNS);
			ResultSet rset = statement.executeQuery();

			HellboundSpawn spawnDat;
			L2NpcTemplate template;

			while (rset.next())
			{
				template = NpcTable.getInstance().getTemplate(rset.getInt("npc_templateid"));
				if (template != null)
				{
					spawnDat = new HellboundSpawn(template);
					spawnDat.setAmount(1);
					spawnDat.setLocx(rset.getInt("locx"));
					spawnDat.setLocy(rset.getInt("locy"));
					spawnDat.setLocz(rset.getInt("locz"));
					spawnDat.setHeading(rset.getInt("heading"));
					spawnDat.setRespawnMinDelay(rset.getInt("respawn_min_delay"));
					spawnDat.setRespawnMaxDelay(rset.getInt("respawn_max_delay"));
					spawnDat.setMinLvl(rset.getInt("min_hellbound_level"));
					spawnDat.setMaxLvl(rset.getInt("max_hellbound_level"));
					spawnDat.setMaxTrustLvl(rset.getInt("max_trust_level"));
					spawnDat.setTrust(rset.getInt("trust_value"));

					// Random respawn time, if needed
					if (spawnDat.getRespawnMaxDelay() > spawnDat.getRespawnMinDelay())
						spawnDat.setRespawnDelay(Rnd.get(spawnDat.getRespawnMinDelay(), spawnDat.getRespawnMaxDelay()));
					else
						spawnDat.setRespawnDelay(spawnDat.getRespawnMinDelay());

					_population.put(spawnDat, null);
					SpawnTable.getInstance().addNewSpawn(spawnDat, false);
				}
				else
				{
					_log.warning("HellboundManager: Data missing in NPC table for ID: " + rset.getInt("npc_templateid") + ".");
				}
			}
			rset.close();
		}
		catch (Exception e)
		{
			_log.warning("HellboundManager: problem while loading spawns: " + e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}

		_log.config("HellboundManager: Loaded " + _population.size() + " npc spawn locations.");
	}

	public static final class HellboundSpawn extends L2Spawn
	{
		private int _minLvl;
		private int _maxLvl;
		private int _maxTrustLvl;
		private int _trust;

		public HellboundSpawn(L2NpcTemplate mobTemplate) throws SecurityException, ClassNotFoundException, NoSuchMethodException
		{
			super(mobTemplate);
		}

		public final int getMinLvl()
		{
			return _minLvl;
		}

		public final void setMinLvl(int lvl)
		{
			_minLvl = lvl;
		}

		public final int getMaxLvl()
		{
			return _maxLvl;
		}

		public final void setMaxLvl(int lvl)
		{
			_maxLvl = lvl;
		}

		public final void setMaxTrustLvl(int lvl)
		{
			_maxTrustLvl = lvl;
		}

		public final int getTrust()
		{
			return _trust;
		}

		public final void setTrust(int t)
		{
			_trust = t;
		}

		public final void updateTrustOnKill()
		{
			if (_trust != 0)
			{
				if (_maxTrustLvl <= HellboundManager.getInstance().getLevel())
					HellboundManager.getInstance().updateTrust(_trust, true);
			}
		}

		@Override
		public final void decreaseCount(L2Npc oldNpc)
		{
			if (getRespawnMaxDelay() > getRespawnMinDelay())
				setRespawnDelay(Rnd.get(getRespawnMinDelay(), getRespawnMaxDelay()));

			super.decreaseCount(oldNpc);
			updateTrustOnKill();
		}
	}

	public static final HellboundManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final HellboundManager _instance = new HellboundManager();
	}
}