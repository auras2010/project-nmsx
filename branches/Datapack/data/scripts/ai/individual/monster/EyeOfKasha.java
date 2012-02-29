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
package ai.individual.monster;

import java.util.Collection;
import java.util.Map;

import javolution.util.FastMap;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.instancemanager.ZoneManager;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2MonsterInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.zone.L2ZoneType;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.util.Rnd;
import l2.universe.scripts.ai.L2AttackableAIScript;

/**
 *
 * Eye of Kasha AI
 *
 * @author InsOmnia, Synerge
 */
public class EyeOfKasha extends L2AttackableAIScript
{
	private static final int CURSE_MOBS = 18812; // Monsters which cast Kasha's Curse
	private static final int YEARNING_MOBS = 18813; // Monsters which cast Kasha's Yearning
	private static final int DESPAIR_MOBS = 18814; // Monsters which cast Kasha's Despair

	private static final int DENOFEVILZONE = 200201;
	private static final int[] CampZONE =
	{ 	
		70000, 70001, 70002, 70003, 70004, 70005, 70006, 70007, 70008, 70009, 70010 
	};

	private static final int[] KashaZONE =
	{ 	
		200208, 200209 ,200210, 200211, 200212, 200213, 200214, 200215, 200216, 200217, 200218, 200219, 200220, 200221, 200222, 200223, 
		200224, 200225, 200226, 200227, 200228, 200229, 200230, 200231, 200232, 200233, 200234, 200235, 200236, 200237, 200238, 200239, 
		200240, 200241, 200242, 200243, 200244, 200245, 200246, 200247, 200248, 200249, 200250, 200251, 200252,	
	};

	private static final Map<Integer, Integer> KASHARESPAWN = new FastMap<Integer, Integer>();
	static
	{
		KASHARESPAWN.put(18812, 18813);
		KASHARESPAWN.put(18813, 18814);
		KASHARESPAWN.put(18814, 18812);
	}

	private static final int[] MOBS = { 18812, 18813, 18814 };

	public EyeOfKasha(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addExitZoneId(DENOFEVILZONE);

		for (int i : KashaZONE)
		{
			addEnterZoneId(i);
		}

		for (int i = 0; i < CampZONE.length; i++)
		{
			final int random = Rnd.get(60*1000*1,60*1000*7);
			int message;
			ThreadPoolManager.getInstance().scheduleGeneral(new handleCampDestroy(ZoneManager.getInstance().getZoneById(CampZONE[i])), random);

			if (random > 5*60000)
			{
				message = random - (5*60000);
				ThreadPoolManager.getInstance().scheduleGeneral(new handleCampMessage(0, ZoneManager.getInstance().getZoneById(CampZONE[i])), message);
			}
			else if (random > 3*60000)
			{
				message = random - (3*60000);
				ThreadPoolManager.getInstance().scheduleGeneral(new handleCampMessage(0, ZoneManager.getInstance().getZoneById(CampZONE[i])), message);
			}
			else if (random > 60000)
			{
				message = random - 60000;
				ThreadPoolManager.getInstance().scheduleGeneral(new handleCampMessage(0, ZoneManager.getInstance().getZoneById(CampZONE[i])), message);
			}
			else if (random > 15000)
			{
				message = random - 15000;
				ThreadPoolManager.getInstance().scheduleGeneral(new handleCampMessage(1, ZoneManager.getInstance().getZoneById(CampZONE[i])), message);
			}
		}
	}

	@Override
	public String onEnterZone(L2Character character, L2ZoneType zone)
	{
		if (character instanceof L2PcInstance)
		{
			if (isKashaRange(zone) && isKashaInZone(zone))
			{
				ThreadPoolManager.getInstance().scheduleGeneral(new handleCast(character, zone), Rnd.get(2000, 10000));
			}
		}
		return super.onEnterZone(character, zone);
	}

	@Override
	public String onExitZone(L2Character character, L2ZoneType zone)
	{
		if (character instanceof L2PcInstance)
		{
			if (character.getFirstEffect(6150) != null)
			{
				character.stopSkillEffects(6150);
			}
			if (character.getFirstEffect(6152) != null)
			{
				character.stopSkillEffects(6152);
			}
			if (character.getFirstEffect(6154) != null)
			{
				character.stopSkillEffects(6154);
			}
		}
		return super.onExitZone(character, zone);
	}

	/**
	 *
	 * @param character - L2Character player inside Camp
	 * @return L2ZoneType campZone where character is
	 */
	private L2ZoneType getCamp(L2Character character)
	{
		for (int z : CampZONE)
		{
			L2ZoneType zone = ZoneManager.getInstance().getZoneById(z);
			if (zone.isCharacterInZone(character))
			{
				return zone;
			}
		}
		return null;
	}

	/**
	 *
	 * @param zone - L2ZoneType circle arround the KashaEye
	 * @return true if this is Kasha Eye zone
	 */
	private boolean isKashaRange(L2ZoneType zone)
	{
		for (int z : KashaZONE)
		{
			if (zone.getId() == z)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param zone - L2ZoneType circle arround Kasha Eye
	 * @return L2Npc Kasha Eye inside the circle
	 */
	private L2Npc getKasha(L2ZoneType zone)
	{
		final Collection<L2Character> chars = zone.getCharactersInside().values();
		for (L2Character c : chars)
		{
			if (c instanceof L2MonsterInstance)
			{
				for (int k : MOBS)
				{
					if (k == ((L2MonsterInstance) c).getNpcId())
					{
						return (L2MonsterInstance) c;
					}
				}
			}
		}
		return null;
	}

	/**
	 *
	 * @param zone - L2ZoneType circle arround Kasha Eye
	 * @return true if Kasha Eye is inside the circle
	 */
	private boolean isKashaInZone(L2ZoneType zone)
	{
		final Collection<L2Character> chars = zone.getCharactersInside().values();
		for (L2Character c : chars)
		{
			if (c instanceof L2MonsterInstance)
			{
				for (int k : MOBS)
				{
					if (k == ((L2MonsterInstance) c).getNpcId())
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 *
	 * @param zone - L2ZoneType circle arround Kasha Eye
	 * @return true if character is still inside the circle
	 */
	private boolean isCharacterInZone(L2Character character, L2ZoneType zone)
	{
		final Collection<L2Character> chars = zone.getCharactersInside().values();
		for (L2Character c : chars)
		{
			if (c == character)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param campZone - L2ZoneType camp zone
	 */
	private void destroyKashaInCamp(L2ZoneType campZone)
	{
		final L2Skill skill = SkillTable.getInstance().getInfo(6149, 1);
		final Collection<L2Character> chars = campZone.getCharactersInside().values();
		for (L2Character c : chars)
		{
			if (c instanceof L2MonsterInstance)
			{
				for (int m : MOBS)
				{
					if (m == ((L2MonsterInstance) c).getNpcId())
					{
						if (!c.isDead())
						{
							c.doCast(skill);
							((L2Attackable) c).getSpawn().stopRespawn();
							c.doDie(c);
							ThreadPoolManager.getInstance().scheduleGeneral(new handleRespawn(c), 40000);
						}
					}
				}
			}
		}
	}

	/**
	 *
	 * @param message - 0 or 1
	 * 0 - I can feel that the energy being flown in the Kasha's eye is getting stronger rapidly.
	 * 1 - Kasha's eye pitches and tosses like it's about to explode.
	 */
	private void broadcastKashaMessage(int message, L2ZoneType campZone)
	{
		for (L2Character c : campZone.getCharactersInside().values())
		{
			if (c instanceof L2PcInstance)
			{
				switch (message)
				{
					case 0:
						c.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.I_CAN_FEEL_ENERGY_KASHA_EYE_GETTING_STRONGER_RAPIDLY));
						break;
					case 1:
						c.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.KASHA_EYE_PITCHES_TOSSES_EXPLODE));
						break;
				}
			}
		}
	}

	/**
	 *
	 * @param c - L2Character Kasha Eye Respawn Task
	 *
	 */
	private class handleRespawn implements Runnable
	{
		private L2Character _c;

		public handleRespawn(L2Character c)
		{
			_c = c;
		}

		@Override
		public void run()
		{
			final L2MonsterInstance npc = ((L2MonsterInstance) _c);
			final int npcId = npc.getNpcId();
			if (KASHARESPAWN.containsKey(npcId))
			{
				npc.deleteMe();
				addSpawn(KASHARESPAWN.get(npcId), npc);
			}
		}
	}

	/**
	 *
	 * @param character - L2PcInstance player to cast Kasha buffs
	 * @param kashaZone circle arround Kasha Eye
	 *
	 */
	private class handleCast implements Runnable
	{
		private L2Character _character;
		private L2ZoneType _kashaZone;

		public handleCast(L2Character character, L2ZoneType kashaZone)
		{
			_character = character;
			_kashaZone = kashaZone;
		}

		@Override
		public void run()
		{
			if (getCamp(_character) != null && isCharacterInZone(_character, _kashaZone))
			{
				final L2ZoneType campZone = getCamp(_character);
				int curseLvl = 0;
				int yearningLvl = 0;
				int despairLvl = 0;
				final Collection<L2Character> chars = campZone.getCharactersInside().values();
				for (L2Character c : chars)
				{
					if (c instanceof L2MonsterInstance)
					{
						switch (((L2MonsterInstance) c).getNpcId())
						{
							case CURSE_MOBS:
								curseLvl++;
								break;
							case YEARNING_MOBS:
								yearningLvl++;
								break;
							case DESPAIR_MOBS:
								despairLvl++;
								break;
						}
					}
				}

				if (getKasha(_kashaZone) != null)
				{
					L2Skill curse = null;
					L2Skill yearning = null;
					L2Skill despair = null;
					L2Npc npc = getKasha(_kashaZone);
					boolean casted = false;
					if (curseLvl > 0)
					{
						if (_character.getFirstEffect(6150) != null)
						{
							_character.stopSkillEffects(6150);
						}
						curse = SkillTable.getInstance().getInfo(6150, curseLvl);
						curse.getEffects(npc, _character);
						casted = true;
					}

					if (yearningLvl > 0)
					{
						if (_character.getFirstEffect(6152) != null)
						{
							_character.stopSkillEffects(6152);
						}
						yearning = SkillTable.getInstance().getInfo(6152, yearningLvl);
						yearning.getEffects(npc, _character);
						casted = true;
					}

					if (despairLvl > 0)
					{
						if (_character.getFirstEffect(6154) != null)
						{
							_character.stopSkillEffects(6154);
						}
						despair = SkillTable.getInstance().getInfo(6154, despairLvl);
						despair.getEffects(npc, _character);
						casted = true;
					}

					if (casted && Rnd.get(100) <= 20)
					{
						_character.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.KASHA_EYE_GIVES_STRANGE_FEELING));
					}
				}
			}
		}
	}

	private class handleCampDestroy implements Runnable
	{
		private L2ZoneType _zone;

		public handleCampDestroy(L2ZoneType campZone)
		{
			_zone = campZone;
		}

		@Override
		public void run()
		{
			destroyKashaInCamp(_zone);
			ThreadPoolManager.getInstance().scheduleGeneral(new handleCampDestroy(_zone), (7*60000)+40000);
			ThreadPoolManager.getInstance().scheduleGeneral(new handleCampMessage(0, _zone), (2*60000)+40000);
			ThreadPoolManager.getInstance().scheduleGeneral(new handleCampMessage(0, _zone), (4*60000)+40000);
			ThreadPoolManager.getInstance().scheduleGeneral(new handleCampMessage(0, _zone), (6*60000)+40000);
			ThreadPoolManager.getInstance().scheduleGeneral(new handleCampMessage(1, _zone), (7*60000)+25000);
		}
	}

	private class handleCampMessage implements Runnable
	{
		private int _message;
		private L2ZoneType _campZone;

		public handleCampMessage(int message, L2ZoneType campZone)
		{
			_message = message;
			_campZone = campZone;
		}

		@Override
		public void run()
		{
			broadcastKashaMessage(_message, _campZone);
		}
	}

	public static void main(String[] args)
	{
		new EyeOfKasha(-1, "EyeOfKasha", "ai");
	}
}
