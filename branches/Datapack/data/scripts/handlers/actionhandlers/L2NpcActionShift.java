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
package handlers.actionhandlers;

import l2.universe.Config;
import l2.universe.gameserver.datatables.ItemTable;
import l2.universe.gameserver.handler.IActionHandler;
import l2.universe.gameserver.model.Elementals;
import l2.universe.gameserver.model.L2DropCategory;
import l2.universe.gameserver.model.L2DropData;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Object.InstanceType;
import l2.universe.gameserver.model.L2Spawn;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2MerchantInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.serverpackets.MyTargetSelected;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;
import l2.universe.gameserver.network.serverpackets.StatusUpdate;
import l2.universe.gameserver.skills.BaseStats;
import l2.universe.gameserver.skills.Stats;
import l2.universe.gameserver.templates.item.L2Item;
import l2.universe.util.StringUtil;

public class L2NpcActionShift implements IActionHandler
{
	/**
	 * Manage and Display the GM console to modify the L2NpcInstance (GM only).<BR><BR>
	 * 
	 * <B><U> Actions (If the L2PcInstance is a GM only)</U> :</B><BR><BR>
	 * <li>Set the L2NpcInstance as target of the L2PcInstance player (if necessary)</li>
	 * <li>Send a Server->Client packet MyTargetSelected to the L2PcInstance player (display the select window)</li>
	 * <li>If L2NpcInstance is autoAttackable, send a Server->Client packet StatusUpdate to the L2PcInstance in order to update L2NpcInstance HP bar </li>
	 * <li>Send a Server->Client NpcHtmlMessage() containing the GM console about this L2NpcInstance </li><BR><BR>
	 * 
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Each group of Server->Client packet must be terminated by a ActionFailed packet in order to avoid
	 * that client wait an other packet</B></FONT><BR><BR>
	 * 
	 * <B><U> Example of use </U> :</B><BR><BR>
	 * <li> Client packet : Action</li><BR><BR>
	 */
	public boolean action(final L2PcInstance activeChar, final L2Object target, final boolean interact)
	{
		final L2Npc targetChar = (L2Npc) target;
		
		// Check if the L2PcInstance is a GM
		if (activeChar.getAccessLevel().isGm())
		{
			// Set the target of the L2PcInstance activeChar
			activeChar.setTarget(target);
			
			// Send a Server->Client packet MyTargetSelected to the L2PcInstance activeChar
			// The activeChar.getLevel() - getLevel() permit to display the correct color in the select window
			activeChar.sendPacket(new MyTargetSelected(target.getObjectId(), activeChar.getLevel() - targetChar.getLevel()));
			
			// Check if the activeChar is attackable (without a forced attack)
			if (target.isAutoAttackable(activeChar))
			{
				// Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
				final StatusUpdate su = new StatusUpdate(target);
				su.addAttribute(StatusUpdate.CUR_HP, (int) targetChar.getCurrentHp());
				su.addAttribute(StatusUpdate.MAX_HP, targetChar.getMaxHp());
				activeChar.sendPacket(su);
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile(activeChar.getHtmlPrefix(), "data/html/admin/npcinfo.htm");
			html.replace("%objid%", String.valueOf(target.getObjectId()));
			html.replace("%class%", target.getClass().getSimpleName());
			html.replace("%id%", String.valueOf(targetChar.getTemplate().npcId));
			html.replace("%lvl%", String.valueOf(targetChar.getTemplate().level));
			html.replace("%name%", String.valueOf(targetChar.getTemplate().name));
			html.replace("%tmplid%", String.valueOf(targetChar.getTemplate().npcId));
			html.replace("%aggro%", String.valueOf((target instanceof L2Attackable) ? ((L2Attackable) target).getAggroRange() : 0));
			html.replace("%hp%", String.valueOf((int) targetChar.getCurrentHp()));
			html.replace("%hpmax%", String.valueOf(targetChar.getMaxHp()));
			html.replace("%mp%", String.valueOf((int) targetChar.getCurrentMp()));
			html.replace("%mpmax%", String.valueOf(targetChar.getMaxMp()));
			
			html.replace("%patk%", String.valueOf(targetChar.getPAtk(null)));
			html.replace("%matk%", String.valueOf(targetChar.getMAtk(null, null)));
			html.replace("%pdef%", String.valueOf(targetChar.getPDef(null)));
			html.replace("%mdef%", String.valueOf(targetChar.getMDef(null, null)));
			html.replace("%accu%", String.valueOf(targetChar.getAccuracy()));
			html.replace("%evas%", String.valueOf(targetChar.getEvasionRate(null)));
			html.replace("%crit%", String.valueOf(targetChar.getCriticalHit(null, null)));
			html.replace("%rspd%", String.valueOf(targetChar.getRunSpeed()));
			html.replace("%aspd%", String.valueOf(targetChar.getPAtkSpd()));
			html.replace("%cspd%", String.valueOf(targetChar.getMAtkSpd()));
			html.replace("%str%", String.valueOf(targetChar.getSTR()));
			html.replace("%dex%", String.valueOf(targetChar.getDEX()));
			html.replace("%con%", String.valueOf(targetChar.getCON()));
			html.replace("%int%", String.valueOf(targetChar.getINT()));
			html.replace("%wit%", String.valueOf(targetChar.getWIT()));
			html.replace("%men%", String.valueOf(targetChar.getMEN()));
			html.replace("%loc%", String.valueOf(targetChar.getX() + " " + targetChar.getY() + " " + targetChar.getZ()));
			html.replace("%dist%", String.valueOf((int)Math.sqrt(activeChar.getDistanceSq(target))));
			
			byte attackAttribute = ((L2Character)target).getAttackElement();
			html.replace("%ele_atk%", Elementals.getElementName(attackAttribute));
			html.replace("%ele_atk_value%", String.valueOf(targetChar.getAttackElementValue(attackAttribute)));
			html.replace("%ele_dfire%", String.valueOf(targetChar.getDefenseElementValue(Elementals.FIRE)));
			html.replace("%ele_dwater%", String.valueOf(targetChar.getDefenseElementValue(Elementals.WATER)));
			html.replace("%ele_dwind%", String.valueOf(targetChar.getDefenseElementValue(Elementals.WIND)));
			html.replace("%ele_dearth%", String.valueOf(targetChar.getDefenseElementValue(Elementals.EARTH)));
			html.replace("%ele_dholy%", String.valueOf(targetChar.getDefenseElementValue(Elementals.HOLY)));
			html.replace("%ele_ddark%", String.valueOf(targetChar.getDefenseElementValue(Elementals.DARK)));
			
			if (targetChar.getSpawn() != null)
			{
				html.replace("%spawn%", targetChar.getSpawn().getLocx() + " " + targetChar.getSpawn().getLocy() + " " + targetChar.getSpawn().getLocz());
				html.replace("%loc2d%", String.valueOf((int) Math.sqrt(targetChar.getPlanDistanceSq(targetChar.getSpawn().getLocx(), targetChar.getSpawn().getLocy()))));
				html.replace("%loc3d%", String.valueOf((int) Math.sqrt(targetChar.getDistanceSq(targetChar.getSpawn().getLocx(), targetChar.getSpawn().getLocy(), targetChar.getSpawn().getLocz()))));
				html.replace("%resp%", String.valueOf(targetChar.getSpawn().getRespawnDelay() / 1000));
			}
			else
			{
				html.replace("%spawn%", "<font color=FF0000>null</font>");
				html.replace("%loc2d%", "<font color=FF0000>--</font>");
				html.replace("%loc3d%", "<font color=FF0000>--</font>");
				html.replace("%resp%", "<font color=FF0000>--</font>");
			}
			
			if (targetChar.hasAI())
			{
				html.replace("%ai_intention%",  "<tr><td><table width=270 border=0 bgcolor=131210><tr><td width=100><font color=FFAA00>Intention:</font></td><td align=right width=170>"+String.valueOf(targetChar.getAI().getIntention().name())+"</td></tr></table></td></tr>");
				html.replace("%ai%",            "<tr><td><table width=270 border=0><tr><td width=100><font color=FFAA00>AI</font></td><td align=right width=170>"+targetChar.getAI().getClass().getSimpleName()+"</td></tr></table></td></tr>");
				html.replace("%ai_type%",       "<tr><td><table width=270 border=0 bgcolor=131210><tr><td width=100><font color=FFAA00>AIType</font></td><td align=right width=170>"+String.valueOf(targetChar.getAiType())+"</td></tr></table></td></tr>");
				html.replace("%ai_clan%",       "<tr><td><table width=270 border=0><tr><td width=100><font color=FFAA00>Clan & Range:</font></td><td align=right width=170>"+String.valueOf(targetChar.getTemplate().getAIDataStatic().getClan())+" "+String.valueOf(((L2Npc)target).getTemplate().getAIDataStatic().getClanRange())+"</td></tr></table></td></tr>");
				html.replace("%ai_enemy_clan%", "<tr><td><table width=270 border=0 bgcolor=131210><tr><td width=100><font color=FFAA00>Enemy & Range:</font></td><td align=right width=170>"+String.valueOf(targetChar.getTemplate().getAIDataStatic().getEnemyClan())+" "+String.valueOf(targetChar.getTemplate().getAIDataStatic().getEnemyRange())+"</td></tr></table></td></tr>");
			}
			else
			{
				html.replace("%ai_intention%",  "");
				html.replace("%ai%", "");
				html.replace("%ai_type%",       "");
				html.replace("%ai_clan%",       "");
				html.replace("%ai_enemy_clan%", "");
			}
			
			if (target instanceof L2MerchantInstance)
				html.replace("%butt%", "<button value=\"Shop\" action=\"bypass -h admin_showShop " + String.valueOf(targetChar.getTemplate().npcId) + "\" width=60 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			else
				html.replace("%butt%", "");
			
			activeChar.sendPacket(html);
		}
		else if (Config.ALT_GAME_VIEWNPC)
		{
			// Set the target of the L2PcInstance activeChar
			activeChar.setTarget(target);
			
			// Send a Server->Client packet MyTargetSelected to the L2PcInstance activeChar
			// The activeChar.getLevel() - getLevel() permit to display the correct color in the select window
			activeChar.sendPacket(new MyTargetSelected(target.getObjectId(), activeChar.getLevel() - targetChar.getLevel()));
			
			// Check if the activeChar is attackable (without a forced attack)
			if (target.isAutoAttackable(activeChar))
			{
				// Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
				final StatusUpdate su = new StatusUpdate(target);
				su.addAttribute(StatusUpdate.CUR_HP, (int) targetChar.getCurrentHp());
				su.addAttribute(StatusUpdate.MAX_HP, targetChar.getMaxHp());
				activeChar.sendPacket(su);
			}
			
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			int hpMul = Math.round((float)(targetChar.getStat().calcStat(Stats.MAX_HP, 1, targetChar, null) / BaseStats.CON.calcBonus(targetChar)));
			if (hpMul == 0)
				hpMul = 1;
			final StringBuilder html1 = StringUtil.startAppend(
					1000,
					"<html><head><title>"+
					String.valueOf(((L2Character)target).getName()),
					"</title></head><body>" +
					"<br><center><font color=\"LEVEL\">Basic Info</font></center>" +
					"<table border=0 width=\"100%\">" +
					"<tr><td>Name: </td><td align=right>"+
					String.valueOf(((L2Character)target).getName()),
					"</td></tr>" +
					"<tr><td>Level: </td><td align=right>"+
					String.valueOf(((L2Character)target).getLevel()),
					"</td></tr>"+
					"<tr><td>Aggresive: </td><td align=right>"+
					String.valueOf((target instanceof L2Attackable) ? "Yes" : "No"),
					"</td></tr>" +
					"<tr><td>Respawn: </td><td align=right>"+
					GetRespawnTime(target),
					"</td></tr></table>" +
					"<table border=0 width=\"100%\">" +
					"<tr><td>Max.HP</td><td><font color=FF0000>",
					String.valueOf(targetChar.getMaxHp() / hpMul),
					"*",
					String.valueOf(hpMul),
					"</font></td><td>Max.MP</td><td><font color=0099FF>",
					String.valueOf(targetChar.getMaxMp()),
					"</font></td></tr>" +
					"<tr><td>P.Atk.</td><td>",
					String.valueOf(targetChar.getPAtk(null)),
					"</td><td>M.Atk.</td><td>",
					String.valueOf(targetChar.getMAtk(null, null)),
					"</td></tr>" +
					"<tr><td>P.Def.</td><td>",
					String.valueOf(targetChar.getPDef(null)),
					"</td><td>M.Def.</td><td>",
					String.valueOf(targetChar.getMDef(null, null)),
					"</td></tr>" +
					"<tr><td>Accuracy</td><td>",
					String.valueOf(targetChar.getAccuracy()),
					"</td><td>Evasion</td><td>",
					String.valueOf(targetChar.getEvasionRate(null)),
					"</td></tr>" +
					"<tr><td>Critical</td><td>",
					String.valueOf(targetChar.getCriticalHit(null, null)),
					"</td><td>Speed</td><td>",
					String.valueOf(targetChar.getRunSpeed()),
					"</td></tr>" +
					"<tr><td>Atk.Speed</td><td>",
					String.valueOf(targetChar.getPAtkSpd()),
					"</td><td>Cast.Speed</td><td>",
					String.valueOf(targetChar.getMAtkSpd()),
					"</td></tr>" +
					"<tr><td>Race</td><td>",
					targetChar.getTemplate().getRace().toString(),
					"</td><td></td><td></td></tr>" +
					"</table>" 
			);
			
			if (targetChar.getTemplate().getDropData() != null)
			{
				StringUtil.append(html1,
                                                "<br><center><font color=\"LEVEL\">Drop Info</font></center>" +
						"<br>Drop type legend: <font color=\"C12869\">Quest</font> <font color=\"00ff00\">Sweep</font> <font color=\"3BB9FF\">Drop</font>" +
                                                "<table border=0 width=\"100%\">"
                                );
				
				for (final L2DropCategory cat : targetChar.getTemplate().getDropData())
				{
					for (final L2DropData drop : cat.getAllDrops())
					{
						final L2Item item = ItemTable.getInstance().getTemplate(drop.getItemId());
						if (item == null)
							continue;
						
						final String color;
						
						color = (drop.isQuestDrop() ? "C12869" : (cat.isSweep() ? "00ff00" : "3BB9FF"));

								double szansa = ((double)drop.getChance()/10000);
								StringUtil.append(html1,
										"<tr><td><font color=\"",
										color,
										"\">",
										"<img src=" + item.getIcon() + " width=32 height=32></td><td>"+
										item.getName(),
										"</td><td>",
										String.valueOf(szansa),
										"%</font></td></tr>"
						);
					}
				}
				html1.append("</table>");
			}
			html1.append("</body></html>");
			
			html.setHtml(html1.toString());
			activeChar.sendPacket(html);
		}
		return true;
	}
	
	/**
	 * @param Get Respawn time of L2Npc target
	 * @return - if target not have respawn time (minion), or return respawn time as string
	 */
	private String GetRespawnTime(L2Object target)
	{
		if (target != null && target instanceof L2Npc)
		{
			L2Spawn spawn = ((L2Npc)target).getSpawn();
			if (spawn != null)
			{
				return String.valueOf(spawn.getRespawnDelay() / 1000);
			}
		}
		return "-";
	}

	public InstanceType getInstanceType()
	{
		return InstanceType.L2Npc;
	}
}
