/* This program is free software: you can redistribute it and/or modify it under
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
package ai.individual.raidboss;

import l2.universe.scripts.ai.L2AttackableAIScript;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.datatables.DoorTable;
import l2.universe.gameserver.datatables.SpawnTable;
import l2.universe.gameserver.instancemanager.CastleManager;
import l2.universe.gameserver.instancemanager.GrandBossManager;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.L2CharPosition;
import l2.universe.gameserver.model.L2Spawn;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.NpcSay;
import l2.universe.gameserver.network.serverpackets.SocialAction;
import l2.universe.gameserver.network.serverpackets.SpecialCamera;
import l2.universe.util.L2FastList;
import l2.universe.util.Rnd;

public class Benom extends L2AttackableAIScript
{
	private L2Npc _Benom;
	private static final int BENOM_ID = 29054;
	private static final int BENOM_TELEPORT = 13101;
	private static final String[] BenomSpeak = { "You should have finished me when you had the chance!!!", "I will crush all of you!!!", "I am not finished here, come face me!!!", "You cowards!!! I will torture each and everyone of you!!!" };
	private static final int[] WalkInterval = { 18000, 17000, 4500, 16000, 22000, 14000, 10500, 14000, 9500, 12500, 20500, 14500, 17000, 20000, 22000, 11000, 11000, 20000, 8000, 5500, 20000, 18000, 25000, 28000, 25000, 25000, 25000, 25000, 10000, 24000, 7000, 12000, 20000 };
	private static final byte ALIVE = 0;
	private static final byte DEAD = 1;
	private static byte BenomIsSpawned = 0;
	private static int BenomWalkRouteStep = 0;
	private static final int[][] benomWalkRoutes = { { 12565, -49739, -547 }, { 11242, -49689, -33 }, { 10751, -49702, 83 }, { 10824, -50808, 316 }, { 9084, -50786, 972 }, { 9095, -49787, 1252 }, { 8371, -49711, 1252 }, { 8423, -48545, 1252 }, { 9105, -48474, 1252 }, { 9085, -47488, 972 }, { 10858, -47527, 316 }, { 10842, -48626, 75 }, { 12171, -48464, -547 }, { 13565, -49145, -535 }, { 15653, -49159, -1059 }, { 15423, -48402, -839 }, { 15066, -47438, -419 }, { 13990, -46843, -292 }, { 13685, -47371, -163 }, { 13384, -47470, -163 }, { 14609, -48608, 346 }, { 13878, -47449, 747 }, { 12894, -49109, 980 }, { 10135, -49150, 996 }, { 12894, -49109, 980 }, { 13738, -50894, 747 }, { 14579, -49698, 347 }, { 12896, -51135, -166 }, { 12971, -52046, -292, }, { 15140, -50781, -442, }, { 15328, -50406, -603 }, { 15594, -49192, -1059 }, { 13175, -49153, -537 } };
	
	public Benom(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(BENOM_TELEPORT);
		addTalkId(BENOM_TELEPORT);
		addAggroRangeEnterId(BENOM_ID);
		addKillId(BENOM_ID);
		
		final int castleOwner = CastleManager.getInstance().getCastleById(8).getOwnerId();
		final long siegeDate = CastleManager.getInstance().getCastleById(8).getSiegeDate().getTimeInMillis();
		long benomTeleporterSpawn = (siegeDate - System.currentTimeMillis()) - 86400000;
		final long benomRaidRoomSpawn = (siegeDate - System.currentTimeMillis()) - 86400000;
		long benomRaidSiegeSpawn = (siegeDate - System.currentTimeMillis());
		
		if (benomTeleporterSpawn < 0)
			benomTeleporterSpawn = 1;
		if (benomRaidSiegeSpawn < 0)
			benomRaidSiegeSpawn = 1;
		
		if (castleOwner > 0)
		{
			if (benomTeleporterSpawn >= 1)
				startQuestTimer("BenomTeleSpawn", benomTeleporterSpawn, null, null);
		}
		
		if ((siegeDate - System.currentTimeMillis()) > 0)
			startQuestTimer("BenomRaidRoomSpawn", benomRaidRoomSpawn, null, null);
		startQuestTimer("BenomRaidSiegeSpawn", benomRaidSiegeSpawn, null, null);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		final int castleOwner = CastleManager.getInstance().getCastleById(8).getOwnerId();
		final int clanId = player.getClanId();
		if (castleOwner != 0 && clanId != 0)
		{
			if (castleOwner == clanId)
			{
				int X = 12558 + (Rnd.get(200) - 100);
				int Y = -49279 + (Rnd.get(200) - 100);
				player.teleToLocation(X, Y, -3007);
				return htmltext;
			}
			else
				htmltext = "<html><body>Benom's Avatar:<br>Your clan does not own this castle. Only members of this Castle's owning clan can challenge Benom.</body></html>";
		}
		else
			htmltext = "<html><body>Benom's Avatar:<br>Your clan does not own this castle. Only members of this Castle's owning clan can challenge Benom.</body></html>";
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("BenomTeleSpawn"))
		{
			addSpawn(BENOM_TELEPORT, 11013, -49629, -547, 13400, false, 0);
		}
		else if (event.equals("BenomRaidRoomSpawn"))
		{
			if (BenomIsSpawned == 0 && GrandBossManager.getInstance().getBossStatus(BENOM_ID) == 0)
				_Benom = addSpawn(BENOM_ID, 12047, -49211, -3009, 0, false, 0);
			BenomIsSpawned = 1;
		}
		else if (event.equals("BenomRaidSiegeSpawn"))
		{
			if (GrandBossManager.getInstance().getBossStatus(BENOM_ID) == 0)
			{
				switch (BenomIsSpawned)
				{
					case 0:
						_Benom = addSpawn(BENOM_ID, 11025, -49152, -537, 0, false, 0);
						BenomIsSpawned = 1;
						break;
					case 1:
						_Benom.teleToLocation(11025, -49152, -537);
						break;
				}

				startQuestTimer("BenomSpawnEffect", 100, _Benom, null);
				startQuestTimer("BenomBossDespawn", 5400000, _Benom, null);
				cancelQuestTimer("BenomSpawn", _Benom, null);
				unspawnNpc(BENOM_TELEPORT);
			}
		}
		else if (event.equals("BenomSpawnEffect"))
		{
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 200, 0, 150, 0, 5000));
			npc.broadcastPacket(new SocialAction(npc.getObjectId(), 3));
			startQuestTimer("BenomWalk", 5000, npc, null);
			BenomWalkRouteStep = 0;
		}
		else if (event.equals("Attacking"))
		{
			L2FastList<L2PcInstance> NumPlayers = new L2FastList<L2PcInstance>();
			for (L2PcInstance plr : npc.getKnownList().getKnownPlayers().values())
			{
				NumPlayers.add(plr);
			}
			
			if (NumPlayers.size() > 0)
			{
				L2PcInstance target = NumPlayers.get(Rnd.get(NumPlayers.size()));
				((L2Attackable) npc).addDamageHate(target, 0, 999);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
				startQuestTimer("Attacking", 2000, npc, player);
			}
			else if (NumPlayers.size() == 0)
				startQuestTimer("BenomWalkFinish", 2000, npc, null);
		}
		else if (event.equals("BenomWalkFinish"))
		{
			if (npc.getCastle().getSiege().getIsInProgress())
				cancelQuestTimer("Attacking", npc, player);
			final int X = benomWalkRoutes[BenomWalkRouteStep][0];
			final int Y = benomWalkRoutes[BenomWalkRouteStep][1];
			final int Z = benomWalkRoutes[BenomWalkRouteStep][2];
			npc.teleToLocation(X, Y, Z);
			npc.setWalking();
			BenomWalkRouteStep = 0;
			startQuestTimer("BenomWalk", 2200, npc, null);
		}
		else if (event.equals("BenomWalk"))
		{
			if (BenomWalkRouteStep == 33)
			{
				BenomWalkRouteStep = 0;
				startQuestTimer("BenomWalk", 100, npc, null);
			}
			else
			{
				startQuestTimer("Talk", 100, npc, null);
				switch (BenomWalkRouteStep)
				{
					case 14:
						startQuestTimer("DoorOpen", 15000, null, null);
						startQuestTimer("DoorClose", 23000, null, null);
						break;
					case 32:
						startQuestTimer("DoorOpen", 500, null, null);
						startQuestTimer("DoorClose", 4000, null, null);
						break;
				}

				final int Time = WalkInterval[BenomWalkRouteStep];
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				final int X = benomWalkRoutes[BenomWalkRouteStep][0];
				final int Y = benomWalkRoutes[BenomWalkRouteStep][1];
				final int Z = benomWalkRoutes[BenomWalkRouteStep][2];
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(X, Y, Z, 0));
				BenomWalkRouteStep += 1;
				startQuestTimer("BenomWalk", Time, npc, null);
			}
		}
		else if (event.equals("DoorOpen"))
		{
			DoorTable.getInstance().getDoor(20160005).openMe();
		}
		else if (event.equals("DoorClose"))
		{
			DoorTable.getInstance().getDoor(20160005).closeMe();
		}
		else if (event.equals("Talk"))
		{
			if (Rnd.get(100) < 40)
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), BenomSpeak[Rnd.get(4)]));
		}
		else if (event.equals("BenomBossDespawn"))
		{
			GrandBossManager.getInstance().setBossStatus(BENOM_ID, ALIVE);
			BenomIsSpawned = 0;
			unspawnNpc(BENOM_ID);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		cancelQuestTimer("BenomWalk", npc, null);
		cancelQuestTimer("BenomWalkFinish", npc, null);
		startQuestTimer("Attacking", 100, npc, player);
		return super.onAggroRangeEnter(npc, player, isPet);
	}
	
	public String onKill(L2Npc npc, L2PcInstance player, Boolean isPet)
	{
		GrandBossManager.getInstance().setBossStatus(BENOM_ID, DEAD);
		cancelQuestTimer("BenomWalk", npc, null);
		cancelQuestTimer("BenomWalkFinish", npc, null);
		cancelQuestTimer("BenomBossDespawn", npc, null);
		cancelQuestTimer("Talk", npc, null);
		cancelQuestTimer("Attacking", npc, null);
		return super.onKill(npc, player, isPet);
	}
	
	private void unspawnNpc(int npcId)
	{
		for (L2Spawn spawn : SpawnTable.getInstance().getSpawnTable())
		{
			if (spawn.getId() == npcId)
			{
				SpawnTable.getInstance().deleteSpawn(spawn, false);
				L2Npc npc = spawn.getLastSpawn();
				npc.deleteMe();
			}
		}
	}
	
	public static void main(String[] args)
	{
		new Benom(-1, "Benom", "ai");
	}
}
