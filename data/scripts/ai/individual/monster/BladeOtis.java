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

import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.scripts.ai.L2AttackableAIScript;


public class BladeOtis extends L2AttackableAIScript
{
    private final static int BLADEO = 18562;
    private final static int GUARD = 18563;

    private static int isAlreadySpawned = 0;

    public BladeOtis(int id, String name, String descr)
    {
        super(id, name, descr);

        addAttackId(BLADEO);
        addKillId(GUARD);
        addKillId(BLADEO);
    }


    @Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
    {
        if (event.equals("time_to_spawn") && isAlreadySpawned == 0)
        {
		    addSpawn(GUARD,player.getX()+100,player.getY()+100,npc.getZ(),0,false,0,false, npc.getInstanceId());
		    isAlreadySpawned += 1;
        }
        else if (event.equals("time_to_spawn1"))
        {
	        addSpawn(GUARD,player.getX()+50,player.getY()+8,npc.getZ(),0,false,0,false, npc.getInstanceId());
	        isAlreadySpawned += 1;
        }
        else if (event.equals("time_to_spawn2"))
        {
	        addSpawn(GUARD,player.getX()-20,player.getY()+10,npc.getZ(),0,false,0,false, npc.getInstanceId());
	        isAlreadySpawned += 1;
        }
        else if (event.equals("time_to_spawn3"))
        {
	        addSpawn(GUARD,player.getX()+150,player.getY()+100,npc.getZ(),0,false,0,false, npc.getInstanceId());
	        isAlreadySpawned += 1;
        }
        else if (event.equals("time_to_spawn4"))
        {
	        addSpawn(GUARD,player.getX()+10,player.getY()+100,npc.getZ(),0,false,0,false, npc.getInstanceId());
	        isAlreadySpawned += 1;
        }
        else if (event.equals("time_to_spawn5"))
        {
	        addSpawn(GUARD,player.getX()+15,player.getY()-40,npc.getZ(),0,false,0,false, npc.getInstanceId());
	        isAlreadySpawned += 1;
        }
        return null;
    }

    @Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet, L2Skill skill)
    {
        if (npc.getCurrentHp() < npc.getMaxHp()*0.5)
        {
            if (isAlreadySpawned == 0)
                startQuestTimer("time_to_spawn",1, npc, player);
            else if (isAlreadySpawned == 1)
                startQuestTimer("time_to_spawn1",10000, npc, player);
            else if (isAlreadySpawned == 2)
                startQuestTimer("time_to_spawn2",10000, npc, player);
            else if (isAlreadySpawned == 3)
                startQuestTimer("time_to_spawn3",10000, npc, player);
            else if (isAlreadySpawned == 4)
                startQuestTimer("time_to_spawn4",10000, npc, player);
            else if (isAlreadySpawned == 5)
                startQuestTimer("time_to_spawn5",10000, npc, player);
        }
        return super.onAttack(npc, player, damage, isPet, skill);
    }

    @Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
    {
        if (npc.getNpcId() == GUARD)
	        addSpawn(GUARD,player.getX()+150,player.getY()-30,npc.getZ(),0,false,0,false, npc.getInstanceId());
        else if (npc.getNpcId() == BLADEO)
        {
	        cancelQuestTimer("time_to_spawn",npc,player);
	        cancelQuestTimer("time_to_spawn1",npc,player);
	        cancelQuestTimer("time_to_spawn2",npc,player);
	        cancelQuestTimer("time_to_spawn3",npc,player);
	        cancelQuestTimer("time_to_spawn4",npc,player);
	        cancelQuestTimer("time_to_spawn5",npc,player);
        }
        return null;
    }


    public static void main(String[] args)
    {
        new BladeOtis(-1, "BladeOtis", "ai");
    }
}
