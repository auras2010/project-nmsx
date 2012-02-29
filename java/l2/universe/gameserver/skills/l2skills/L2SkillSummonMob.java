 package l2.universe.gameserver.skills.l2skills;

import java.util.List;
import java.util.Random;

import l2.universe.gameserver.datatables.NpcTable;
import l2.universe.gameserver.datatables.SpawnTable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.L2Spawn;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.templates.chars.L2NpcTemplate;
import l2.universe.gameserver.templates.StatsSet;

public class L2SkillSummonMob extends L2Skill
{
	private int     npcId;
    private static final Random _rnd = new Random();
    private List<L2Npc> _mobs;
    final int     power;

	public L2SkillSummonMob(StatsSet set) 
    {
		super(set);

		npcId = set.getInteger("npcId", 0);
		power = set.getInteger("power", 5);
	}

	@Override
	public void useSkill(L2Character caster, L2Object[] targets)
	{
		try
		{
			for (int i = 0; i < this.getPower(); i++)
			{
				L2NpcTemplate npcTemplate = null;
				npcTemplate = NpcTable.getInstance().getTemplate(npcId);
				L2Spawn npcSpawn = new L2Spawn(npcTemplate);

				final int point = _rnd.nextInt(100);
				final int signX = (_rnd.nextInt(2) == 0) ? -1 : 1;
				final int signY = (_rnd.nextInt(2) == 0) ? -1 : 1;

				npcSpawn.setLocx(caster.getX() + (point * signX));
				npcSpawn.setLocy(caster.getY() + (point * signY));
				npcSpawn.setLocz(caster.getZ());
				npcSpawn.stopRespawn();

				SpawnTable.getInstance().addNewSpawn(npcSpawn, false);
				_mobs.add(npcSpawn.doSpawn());
			}
		}
		catch (Exception e)
		{
			_log.warning("RaidEngine: Error while spawning undead: " + e);
		}
	}
}
