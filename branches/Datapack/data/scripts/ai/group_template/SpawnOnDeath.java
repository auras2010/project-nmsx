package ai.group_template;

import java.util.Map;

import javolution.util.FastMap;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.scripts.ai.L2AttackableAIScript;
import l2.universe.util.Rnd;

/**
 * 
 * @author Synerge
 */
public class SpawnOnDeath extends L2AttackableAIScript
{
	private static final Map<Integer, Integer> MOBSPAWNS5 = new FastMap<Integer, Integer>();
	private static final Map<Integer, Integer> MOBSPAWNS15 = new FastMap<Integer, Integer>();
	private static final Map<Integer, Integer> MOBSPAWNS100 = new FastMap<Integer, Integer>();
	static
	{
		MOBSPAWNS5.put(22705, 22707);
		MOBSPAWNS15.put(22703, 22703);
		MOBSPAWNS15.put(22704, 22704);
		MOBSPAWNS100.put(18812, 18813);
		MOBSPAWNS100.put(18813, 18814);
		MOBSPAWNS100.put(18814, 18812);
	}
	
	public SpawnOnDeath(int questId, String name, String descr)
	{
		super(questId, name, descr);
		int[] temp = { 22703, 22704, 18812, 18813, 18814, 22705, 22707 };
		registerMobs(temp, QuestEventType.ON_KILL);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		final int npcId = npc.getNpcId();
		L2Attackable newNpc = null;
		if (MOBSPAWNS15.containsKey(npcId))
		{
			if (Rnd.get(100) < 15)
			{
				newNpc = (L2Attackable) addSpawn(MOBSPAWNS15.get(npcId), npc);
				newNpc.setRunning();
				newNpc.addDamageHate(killer, 0, 999);
				newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, killer);
			}
		}
		else if (MOBSPAWNS100.containsKey(npcId))
		{
			npc.deleteMe();
			newNpc = (L2Attackable) addSpawn(MOBSPAWNS100.get(npcId), npc);
		}
		else if (MOBSPAWNS5.containsKey(npcId) && Rnd.get(100) < 5)
		{
			newNpc = (L2Attackable) addSpawn(MOBSPAWNS5.get(npcId), npc);
			newNpc.setRunning();
			newNpc.addDamageHate(killer, 0, 999);
			newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, killer);
		}
		return super.onKill(npc, killer, isPet);
	}
	
	public static void main(String[] args)
	{
		new SpawnOnDeath(-1, "SpawnOnDeath", "ai");
	}
}
