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
package ai.group_template;

import gnu.trove.TIntHashSet;
import gnu.trove.TIntObjectHashMap;
import javolution.util.FastList;
import javolution.util.FastMap;

import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.NpcSay;
import l2.universe.scripts.ai.L2AttackableAIScript;
import l2.universe.util.Rnd;

/**
 * 
 * @author Synerge
 */
public class SummonMinions extends L2AttackableAIScript
{
	private static int HasSpawned;
	private static TIntHashSet myTrackingSet = new TIntHashSet(); //Used to track instances of npcs
	private final FastMap<Integer, FastList<L2PcInstance>> _attackersList = new FastMap<Integer, FastList<L2PcInstance>>().shared();
	
	private static final TIntObjectHashMap<int[]> MINIONS = new TIntObjectHashMap<int[]>();	
	static
	{
		MINIONS.put(20767, new int[] { 20768, 20769, 20770 }); //Timak Orc Troop
		// MINIONS.put(22030,new int[]{22045,22047,22048}); //Ragna Orc Shaman
		// MINIONS.put(22032,new int[]{22036}); //Ragna Orc Warrior - summons shaman but not 22030 ><
		// MINIONS.put(22038,new int[]{22037}); //Ragna Orc Hero
		MINIONS.put(21524, new int[] { 21525 }); //Blade of Splendor
		MINIONS.put(21531, new int[] { 21658 }); //Punishment of Splendor
		MINIONS.put(21539, new int[] { 21540 }); //Wailing of Splendor
		MINIONS.put(22080, new int[] { 22079, 22079, 22079, 22079, 22079, 22079 }); // Massive Bandersnatch
		MINIONS.put(22084, new int[] { 22083, 22083, 22083, 22083, 22083, 22083 }); // Panthera
		MINIONS.put(22088, new int[] { 22087, 22087, 22087, 22087, 22087, 22087 }); // Pronghorn
		MINIONS.put(22092, new int[] { 22091, 22091, 22091, 22091, 22091, 22091 }); // frost Golem
		MINIONS.put(22094, new int[] { 22093, 22093, 22093, 22093, 22093, 22093 }); // Frost Buffalo
		MINIONS.put(22096, new int[] { 22095, 22095, 22095, 22095, 22095, 22095 }); // Ursus
		MINIONS.put(22257, new int[] { 18364, 18364 }); // Island Guardian
		MINIONS.put(22258, new int[] { 18364, 18364 }); // White Sand Mirage
		MINIONS.put(22259, new int[] { 18364, 18364 }); // Muddy Coral
		MINIONS.put(22260, new int[] { 18364, 18364 }); // Kleopora
		MINIONS.put(22261, new int[] { 18365, 18365 }); // Seychelles
		MINIONS.put(22262, new int[] { 18365, 18365 }); // Naiad
		MINIONS.put(22263, new int[] { 18365, 18365 }); // Sonneratia
		MINIONS.put(22264, new int[] { 18366, 18366 }); // Castalia
		MINIONS.put(22265, new int[] { 18366, 18366 }); // Chrysocolla
		MINIONS.put(22266, new int[] { 18366, 18366 }); // Pythia
		MINIONS.put(22774, new int[] { 22768, 22768 }); // Tanta Lizardman Summoner
	}
	
	public SummonMinions(int questId, String name, String descr)
	{
		super(questId, name, descr);

		for (Integer id : MINIONS.keys())
		{
			addAttackId(id);
			addKillId(id);
		}
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		final int npcId = npc.getNpcId();
		if (!MINIONS.containsKey(npcId))
			return super.onAttack(npc, attacker, damage, isPet);
		
		final int npcObjId = npc.getObjectId();
		if (!myTrackingSet.contains(npcObjId)) //this allows to handle multiple instances of npc
		{
			synchronized (myTrackingSet)
			{
				myTrackingSet.add(npcObjId);
			}
			
			HasSpawned = npcObjId;
		}
		
		if (HasSpawned == npcObjId)
		{
			switch (npcId)
			{
				case 22030: //mobs that summon minions only on certain hp
				case 22032:
				case 22038:
					if (npc.getCurrentHp() < (npc.getMaxHp() / 2.0))
					{
						HasSpawned = 0;
						if (Rnd.get(100) < 33) //mobs that summon minions only on certain chance
						{
							int[] minions = MINIONS.get(npcId);
							for (final int val : minions)
							{
								final L2Attackable newNpc = (L2Attackable) addSpawn(val, (npc.getX() + Rnd.get(-150, 150)), (npc.getY() + Rnd.get(-150, 150)), npc.getZ(), 0, false, 0);
								newNpc.setRunning();
								newNpc.addDamageHate(attacker, 0, 999);
								newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
							}
							minions = null;
						}
					}
					break;
				case 22257:
				case 22258:
				case 22259:
				case 22260:
				case 22261:
				case 22262:
				case 22263:
				case 22264:
				case 22265:
				case 22266:
					if (isPet)
						attacker = (attacker).getPet().getOwner();
					if (attacker.getParty() != null)
					{
						for (final L2PcInstance member : attacker.getParty().getPartyMembers())
						{
							if (_attackersList.get(npcObjId) == null)
							{
								final FastList<L2PcInstance> player = new FastList<L2PcInstance>();
								player.add(member);
								_attackersList.put(npcObjId, player);
							}
							else if (!_attackersList.get(npcObjId).contains(member))
								_attackersList.get(npcObjId).add(member);
						}
					}
					else
					{
						if (_attackersList.get(npcObjId) == null)
						{
							final FastList<L2PcInstance> player = new FastList<L2PcInstance>();
							player.add(attacker);
							_attackersList.put(npcObjId, player);
						}
						else if (!_attackersList.get(npcObjId).contains(attacker))
							_attackersList.get(npcObjId).add(attacker);
					}
					if ((attacker.getParty() != null && attacker.getParty().getMemberCount() > 2) || _attackersList.get(npcObjId).size() > 2) //Just to make sure..
					{
						HasSpawned = 0;
						for (final int val : MINIONS.get(npcId))
						{
							final L2Attackable newNpc = (L2Attackable) addSpawn(val, npc.getX() + Rnd.get(-150, 150), npc.getY() + Rnd.get(-150, 150), npc.getZ(), 0, false, 0);
							newNpc.setRunning();
							newNpc.addDamageHate(attacker, 0, 999);
							newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
						}
					}
					break;
				default: //mobs without special conditions
					HasSpawned = 0;
					final int[] minions = MINIONS.get(npcId);
					if (npcId != 20767)
					{
						for (int val : minions)
						{
							final L2Attackable newNpc = (L2Attackable) addSpawn(val, npc.getX() + Rnd.get(-150, 150), npc.getY() + Rnd.get(-150, 150), npc.getZ(), 0, false, 0);
							if (newNpc != null)
							{
								newNpc.setRunning();
								newNpc.addDamageHate(attacker, 0, 999);
								newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
							}
						}
					}
					else
					{
						for (int val : minions)
						{
							addSpawn(val, (npc.getX() + Rnd.get(-100, 100)), (npc.getY() + Rnd.get(-100, 100)), npc.getZ(), 0, false, 0);
						}
						
						npc.broadcastPacket(new NpcSay(npcObjId, 0, npcId, 1000294)); // Come out, you children of darkness!
					}
					
					break;
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		final int npcId = npc.getNpcId();
		final int npcObjId = npc.getObjectId();
		if (MINIONS.containsKey(npcId))
		{
			synchronized (myTrackingSet)
			{
				myTrackingSet.remove(npcObjId);
			}
		}
		
		if (_attackersList.get(npcObjId) != null)
		{
			_attackersList.get(npcObjId).clear();
		}

		return super.onKill(npc, killer, isPet);
	}
	
	public static void main(String[] args)
	{
		new SummonMinions(-1, "SummonMinions", "ai");
	}
}