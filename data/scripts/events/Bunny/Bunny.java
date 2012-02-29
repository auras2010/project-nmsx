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
package events.Bunny;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;

import javolution.util.FastList;
import l2.universe.Config;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.datatables.NpcTable;
import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.datatables.SpawnTable;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.L2Spawn;
import l2.universe.gameserver.model.actor.instance.L2MonsterInstance;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ItemList;
import l2.universe.gameserver.network.serverpackets.NpcSay;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.templates.chars.L2NpcTemplate;
import l2.universe.util.Rnd;

public class Bunny extends Quest
{
	public static final String SPAWNLIST_FILE = "/data/scripts/events/Bunny/spawnlist.csv";
	public static final String CONFIG_FILE = "/data/scripts/events/bunny.cfg";
	
	private static final String[] Text = { "I am telling the truth.", "A relaxing feeling is moving through my stomach.", "I am nothing.", "Boo-hoo... I hate...", "You will regret this.", "see you later.", "You've made a great choice.", "Did you see that Firecracker explode?", "If you need to go to Fantasy Isle, come see me.", "All of Fantasy Isle is a Peace Zone.", "If you collect 50 individual Treasure Sack Pieces, you can exchange them for a Treasure Sack.", "Startled", "Bumps" };
	
	private static FastList<L2Spawn> spawns = new FastList<L2Spawn>();
	private static String START_YEAR, END_YEAR, START_MONTH, END_MONTH, START_DAY, END_DAY, START_HOUR, END_HOUR;
	private static int DROP_CHANCE;
	
	protected static boolean isEventTime = false;
	protected static final DateFormat format = new SimpleDateFormat("yyyy MMM dd HH", Locale.US);
	
	public Bunny(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addEventId(32365, Quest.QuestEventType.ON_TALK);
		addEventId(32365, Quest.QuestEventType.ON_FIRST_TALK);
		addEventId(32365, Quest.QuestEventType.QUEST_START);
		addEventId(13097, Quest.QuestEventType.ON_SKILL_SEE);
		addEventId(13097, Quest.QuestEventType.ON_SPAWN);
		addEventId(13097, Quest.QuestEventType.ON_FIRST_TALK);
		addEventId(13098, Quest.QuestEventType.ON_SKILL_SEE);
		addEventId(13098, Quest.QuestEventType.ON_SPAWN);
		addEventId(13098, Quest.QuestEventType.ON_FIRST_TALK);
		loadConfig();
		load();
		timerCheck();
	}
	
	private void loadConfig()
	{
		InputStream is = null;
		try
		{
			try
			{
				final Properties bunnySettings = new Properties();
				is = new FileInputStream(new File(Config.DATAPACK_ROOT + CONFIG_FILE));
				bunnySettings.load(is);
				START_YEAR = bunnySettings.getProperty("StartYear", "0");
				END_YEAR = bunnySettings.getProperty("EndYear", "0");
				START_MONTH = bunnySettings.getProperty("StartMonth", "0");
				END_MONTH = bunnySettings.getProperty("EndMonth", "0");
				START_DAY = bunnySettings.getProperty("StartDay", "0");
				END_DAY = bunnySettings.getProperty("EndDay", "0");
				START_HOUR = bunnySettings.getProperty("StartHour", "0");
				END_HOUR = bunnySettings.getProperty("EndHour", "0");
				DROP_CHANCE = Integer.parseInt(bunnySettings.getProperty("DropChance", "0"));
			}
			catch (final Exception e)
			{
				e.printStackTrace();
				throw new Error("Failed to Load " + CONFIG_FILE + " File.");
			}
		}
		finally
		{
			try
			{
				is.close();
			}
			catch (final Exception e)
			{
			}
		}
	}
	
	private void timerCheck()
	{
		try
		{
			final Date start = format.parse("" + START_YEAR + " " + START_MONTH + " " + START_DAY + " " + START_HOUR);
			final Date end = format.parse("" + END_YEAR + " " + END_MONTH + " " + END_DAY + " " + END_HOUR);
			final long startTime = start.getTime();
			final long endTime = end.getTime();
			final long current = System.currentTimeMillis();
			if (startTime > endTime)
			{
				_log.warning("Bunny Event: Wrong time settings... Ignored.");
				return;
			}
			if (current >= startTime && current <= endTime)
			{
				registerDrops();
				doSpawns();
				isEventTime = true;
				ThreadPoolManager.getInstance().scheduleGeneral(new EndEvent(1, 604800000), endTime - current);
				_log.info("Bunny Event: Event started, Event will end at " + format.format(current + endTime - current));
				return;
			}
			else
			{
				if (current < startTime)
				{
					ThreadPoolManager.getInstance().scheduleGeneral(new EndEvent(2, endTime - current), startTime - current);
					_log.info("Bunny Event: Event will start at " + format.format(current + startTime - current));
				}
				else if (current <= endTime + 604800000)
				{
					doSpawns();
					ThreadPoolManager.getInstance().scheduleGeneral(new EndEvent(1, 604800000), (endTime + 604800000) - current);
					_log.info("Bunny Event: Event is over, npcs will be despawned at " + format.format(current + (endTime + 604800000) - current));
				}
				else
				{
					_log.warning("Bunny Event has passed... Ignored.");
					return;
				}
			}
			
		}
		catch (final ParseException e)
		{
			e.printStackTrace();
		}
	}
	
	protected class EndEvent implements Runnable
	{
		private final int _type;
		private final long _delay;
		
		public EndEvent(int type, long nextDelay)
		{
			_type = type;
			_delay = nextDelay;
		}
		
		@Override
		public void run()
		{
			switch (_type)
			{
				case 0: // 1 week after event ended
					deleteSpawns();
					_log.info("Bunny Event: Event is over.");
					break;
				case 1: // event end timer
					isEventTime = false;
					ThreadPoolManager.getInstance().scheduleGeneral(new EndEvent(0, 0), _delay);
					_log.info("Bunny Event: Event is over, npcs will be despawned at " + format.format(_delay));
					break;
				case 2: // start event
					registerDrops();
					doSpawns();
					isEventTime = true;
					ThreadPoolManager.getInstance().scheduleGeneral(new EndEvent(1, 604800000), _delay);
					_log.info("Bunny Event: Event started, Event will end at " + format.format(_delay));
					break;
			}
		}
	}
	
	protected void registerDrops()
	{
		for (int level = 1; level < 100; level++)
		{
			final L2NpcTemplate[] templates = NpcTable.getInstance().getAllOfLevel(level);
			if ((templates != null) && (templates.length > 0))
			{
				for (final L2NpcTemplate t : templates)
				{
					try
					{
						if (L2Attackable.class.isAssignableFrom(Class.forName("l2.universe.gameserver.model.actor.instance." + t.type + "Instance")))
						{
							addEventId(t.npcId, Quest.QuestEventType.ON_KILL);
						}
					}
					catch (final ClassNotFoundException ex)
					{
						System.out.println("Class not found " + t.type + "Instance");
					}
				}
			}
		}
		
	}
	
	protected void doSpawns()
	{
		if (spawns.isEmpty() || spawns.size() == 0)
			return;
		for (final L2Spawn spawn : spawns)
		{
			if (spawn == null)
				continue;
			spawn.doSpawn();
			spawn.startRespawn();
		}
	}
	
	protected void deleteSpawns()
	{
		if (spawns.isEmpty() || spawns.size() == 0)
			return;
		for (final L2Spawn spawn : spawns)
		{
			if (spawn == null)
				continue;
			spawn.stopRespawn();
			spawn.getLastSpawn().doDie(spawn.getLastSpawn());
		}
	}
	
	private void load()
	{
		Scanner s;
		try
		{
			s = new Scanner(new File(Config.DATAPACK_ROOT + SPAWNLIST_FILE));
		}
		catch (final Exception e)
		{
			_log.warning("Bunny Event: Can not find '" + Config.DATAPACK_ROOT + SPAWNLIST_FILE);
			return;
		}
		int lineCount = 0;
		spawns.clear();
		while (s.hasNextLine())
		{
			lineCount++;
			final String line = s.nextLine();
			
			if (line.startsWith("#"))
				continue;
			else if (line.equals(""))
				continue;
			
			final String[] lineSplit = line.split(";");
			
			boolean ok = true;
			int npcID = 0;
			
			try
			{
				npcID = Integer.parseInt(lineSplit[0]);
			}
			catch (final Exception e)
			{
				_log.warning("Bunny Event: Error in line " + lineCount + " -> invalid npc id or wrong seperator after npc id!");
				_log.warning("		" + line);
				ok = false;
			}
			final L2NpcTemplate template = NpcTable.getInstance().getTemplate(npcID);
			if (template == null)
			{
				_log.warning("Bunny Event: NPC Id " + npcID + " not found!");
				continue;
			}
			if (!ok)
				continue;
			
			final String[] lineSplit2 = lineSplit[1].split(",");
			
			int x = 0, y = 0, z = 0, heading = 0, respawn = 0;
			
			try
			{
				x = Integer.parseInt(lineSplit2[0]);
				y = Integer.parseInt(lineSplit2[1]);
				z = Integer.parseInt(lineSplit2[2]);
				heading = Integer.parseInt(lineSplit2[3]);
				respawn = Integer.parseInt(lineSplit2[4]);
			}
			
			catch (final Exception e)
			{
				_log.warning("Bunny Event: Error in line " + lineCount + " -> incomplete/invalid data or wrong seperator!");
				_log.warning("		" + line);
				ok = false;
			}
			
			if (!ok)
				continue;
			try
			{
				final L2Spawn spawnDat = new L2Spawn(template);
				spawnDat.setAmount(1);
				spawnDat.setLocx(x);
				spawnDat.setLocy(y);
				spawnDat.setLocz(z);
				spawnDat.setHeading(heading);
				spawnDat.setRespawnDelay(respawn);
				SpawnTable.getInstance().addNewSpawn(spawnDat, false);
				spawns.add(spawnDat);
				//spawnDat.doSpawn();
				//spawnDat.startRespawn();
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
		}
		s.close();
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
	{
		if (npc.isInsideRadius(caster, 100, false, false) && npc.getNpcId() == 13097 && skill.getId() == 629)
			spawnChests(npc);
		if (caster.getTarget() == npc && npc.getNpcId() == 13098 && skill.getId() == 630)
			dropReward(npc, caster);
		return super.onSkillSee(npc, caster, skill, targets, isPet);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (isdigit(event) && npc != null)
		{
			switch (Integer.valueOf(event))
			{
				case 0:
					if (Rnd.get(5) == 0)
						shout(npc, Rnd.get(3));
					break;
				case 1:
					cancelQuestTimer("0", npc, null);
					shout(npc, Rnd.get(3, 5));
					npc.deleteMe();
					break;
				case 2:
					cancelQuestTimer("0", npc, null);
					shout(npc, 6);
					break;
				case 3:
					shout(npc, 7);
					break;
				case 4:
					if (Rnd.get(3) == 0)
						shout(npc, Rnd.get(8, 10));
					break;
				case 5:
					if (Rnd.get(100) == 0)
					{
						if (Rnd.get(2) == 0)
							shout(npc, 12);
						else
						{
							shout(npc, 11);
						}
					}
					break;
			}
			return "";
		}
		else if (player != null)
		{
			final QuestState st = player.getQuestState("Bunny");
			if (st != null)
			{
				if (event.equalsIgnoreCase("sacks"))
				{
					if (st.getQuestItemsCount(10272) < 50)
						return "32365-04.htm";
					final int chance = Rnd.get(100);
					if (chance < 2)
						st.giveItems(10254, 1);
					else if (chance < 6)
						st.giveItems(10255, 1);
					else if (chance < 12)
						st.giveItems(10256, 1);
					else if (chance < 25)
						st.giveItems(10257, 1);
					else if (chance < 50)
						st.giveItems(10258, 1);
					else
						st.giveItems(10259, 1);
					st.takeItems(10272, 50);
					
					return "32365-01.htm";
				}
				else if (event.equalsIgnoreCase("scroll"))
				{
					if (!isEventTime)
						return "32365-06.htm";
					final String time = loadGlobalQuestVar(player.getAccountName());
					if (time == "")
					{
						if (st.getQuestItemsCount(57) < 500)
							return "32365-05.htm";
						st.takeItems(57, 500);
						st.giveItems(10274, 1);
						saveGlobalQuestVar(player.getAccountName(), String.valueOf(System.currentTimeMillis() + 43200000));
					}
					else
					{
						final long remain = Long.valueOf(time) - System.currentTimeMillis();
						if (remain <= 0)
						{
							if (st.getQuestItemsCount(57) < 500)
								return "32365-05.htm";
							st.takeItems(57, 500);
							st.giveItems(10274, 1);
							saveGlobalQuestVar(player.getAccountName(), String.valueOf(System.currentTimeMillis() + 43200000));
						}
						else
						{
							final int hours = (int) remain / 1000 / 60 / 60;
							final int minutes = (int) remain / 1000 / 60 % 60;
							SystemMessage sm;
							if (hours > 0)
							{
								sm = SystemMessage.getSystemMessage(SystemMessageId.ITEM_PURCHASABLE_IN_S1_HOURS_S2_MINUTES);
								sm.addNumber(hours);
								sm.addNumber(minutes);
							}
							else
							{
								sm = SystemMessage.getSystemMessage(SystemMessageId.ITEM_PURCHASABLE_IN_S1_MINUTES);
								sm.addNumber(minutes);
							}
							player.sendPacket(sm);
						}
					}
					return "32365-01.htm";
				}
			}
		}
		return event;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		switch (npc.getNpcId())
		{
			case 32365:
				startQuestTimer("4", 60000, npc, null, true);
				break;
			case 13097:
				startQuestTimer("5", 5000, npc, null, true);
				npc.disableCoreAI(true);
				break;
			case 13098:
				if (Rnd.get(100) < 5)
				{
					npc.setTarget(npc);
					npc.doCast(SkillTable.getInstance().getInfo(3156, 1));
					startQuestTimer("3", 2000, npc, null);
					npc.setIsInvul(true);
				}
				else
					startQuestTimer("0", 2000, npc, null, true);
				startQuestTimer("1", 18000, npc, null, false);
				npc.disableCoreAI(true);
				break;
			
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		switch (npc.getNpcId())
		{
			case 32365:
				QuestState st = player.getQuestState("Bunny");
				if (st == null)
					st = newQuestState(player);
				player.setLastQuestNpcObject(npc.getObjectId());
				return "32365-01.htm";
			case 13097:
				break;
			case 13098:
				break;
			
		}
		return null;
	}
	
	private void dropReward(L2Npc npc, L2PcInstance player)
	{
		if (npc.isInvul())
		{
			int itemId = 10272;
			int count = 1;
			npc.setIsInvul(false);
			if (Rnd.get(100) < 50)
			{
				final int chance = Rnd.get(100);
				if (chance < 5)
					itemId = 10254;
				else if (chance < 10)
					itemId = 10255;
				else if (chance < 15)
					itemId = 10256;
				else if (chance < 30)
					itemId = 10257;
				else if (chance < 55)
					itemId = 10258;
				else
					itemId = 10259;
			}
			else
				count += Rnd.get(7, 12);
			final L2ItemInstance item = player.getInventory().addItem("event", itemId, count, player, npc);
			if (item != null)
			{
				if (count > 1)
				{
					final SystemMessage smsg = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S);
					smsg.addItemName(item);
					smsg.addNumber(count);
					player.sendPacket(smsg);
				}
				else
				{
					final SystemMessage smsg = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1);
					smsg.addItemName(item);
					player.sendPacket(smsg);
				}
				player.sendPacket(new ItemList(player, false));
			}
		}
		((L2MonsterInstance) npc).dropItem(player, 10272, Rnd.get(3, 7));
		cancelQuestTimer("0", npc, null);
		cancelQuestTimer("1", npc, null);
		startQuestTimer("2", 10, npc, null, false);
		npc.reduceCurrentHp(9999, npc, null);
	}
	
	private void spawnChests(L2Npc npc)
	{
		int x = 0, y = 0;
		final int z = npc.getZ();
		for (int i = 0; i < 3; i++)
		{
			switch (i)
			{
				case 0:
					x = npc.getX() + 150;
					y = npc.getY() + 120;
					addSpawn(13098, x, y, z, 0, false, 0);
					break;
				case 1:
					x = npc.getX() - 150;
					y = npc.getY() + 120;
					addSpawn(13098, x, y, z, 0, false, 0);
					break;
				case 2:
					x = npc.getX();
					y = npc.getY() - 130;
					addSpawn(13098, x, y, z, 0, false, 0);
					break;
			}
		}
		cancelQuestTimer("5", npc, null);
		npc.reduceCurrentHp(9999, npc, null);
	}
	
	private void shout(L2Npc npc, int id)
	{
		npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), Text[id]));
	}
	
	public boolean isdigit(String str)
	{
		for (int i = 0; i < str.length(); i++)
		{
			if (!Character.isDigit(str.charAt(i)))
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (Rnd.get(100) < DROP_CHANCE && npc instanceof L2MonsterInstance && isEventTime)
			((L2MonsterInstance) npc).dropItem(killer, 10272, 1);
		return super.onKill(npc, killer, isPet);
	}
	
	@Override
	public boolean unload()
	{
		deleteSpawns();
		return super.unload();
	}
	
	public static void main(String[] args)
	{
		new Bunny(-1, "Bunny", "events");
	}
}
