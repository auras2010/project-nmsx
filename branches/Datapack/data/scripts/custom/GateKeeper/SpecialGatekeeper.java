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

package custom.GateKeeper;

import l2.universe.ExternalConfig;
import l2.universe.gameserver.instancemanager.ZoneManager;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.zone.L2ZoneType;

/**
 * @author Matim
 * <br>Special Gatekeeper NPC.
 * <br>Show current players count from zone.
 * <br>This code may be easily edited.
 * <br>By default it has few arenas teleports.
 * 
 * TODO price for teleports, config.
 */
public class SpecialGatekeeper extends Quest
{
	protected static L2PcInstance player;
	
	private final static String qn = "SpecialGatekeeper";
	private final static int NPC = ExternalConfig.SPECIAL_GK_NPC_ID;
	
	private final static int teleportPrice = 1000;
	//private final static int priceItemId = 57;
	
	public SpecialGatekeeper(int questId, String name, String descr) 
	{
		super(questId, name, descr);
		addFirstTalkId(NPC);
		addStartNpc(NPC);
		addTalkId(NPC);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		
		if(event.equalsIgnoreCase("1"))
			player.teleToLocation(73890, 142656, -3778);
		else if (event.equalsIgnoreCase("2"))
			player.teleToLocation(-86979, 142402, -3643);
		else if (event.equalsIgnoreCase("3"))
			player.teleToLocation(147451, 46728, -3410);
		else if (event.equalsIgnoreCase("4"))
			player.teleToLocation(12312, 182752, -3558);
		else	
			htmltext = "Error, check whole GK code!";
		return htmltext;
	}

	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		QuestState qs = player.getQuestState(qn);
		if (qs == null)
			qs = newQuestState(player);

		htmltext = "<html><title>Special Gatekeeper</title><head><body><center>" +
				   "<br><img src=l2ui.bbs_lineage2 height=16 width=80>" +
				   "<font color=AAAAAA>Gatekeeper</font><br>" +
				   "Teleport costs: <font color=\"LEVEL\">" + teleportPrice + "</font> adena" +
                   "<img src=L2UI_CH3.herotower_deco width=256 height=32>" +
                   
                   "Players Inside: <font color=\"LEVEL\">" + getPlayerInsideCount(11600) + "</font>" +
                   "<button value=\"Aden\" action=\"bypass -h Quest SpecialGatekeeper 1\" back=\"L2UI_ch3.bigbutton_over\" fore=\"L2UI_ch3.bigbutton\" width=95 height=21><br>" +
                   
                   "Players Inside: <font color=\"LEVEL\">" + getPlayerInsideCount(10500) + "</font>" +
                   "<button value=\"Giran\" action=\"bypass -h Quest SpecialGatekeeper 2\" back=\"L2UI_ch3.bigbutton_over\" fore=\"L2UI_ch3.bigbutton\" width=95 height=21><br>" +
                   
                   "Players Inside: <font color=\"LEVEL\">" + getPlayerInsideCount(11012) + "</font>" +
                   "<button value=\"MOS\" action=\"bypass -h Quest SpecialGatekeeper 3\" back=\"L2UI_ch3.bigbutton_over\" fore=\"L2UI_ch3.bigbutton\" width=95 height=21><br>" +
                   
                   "Players Inside: <font color=\"LEVEL\">" + getPlayerInsideCount(11013) + "</font>" +
                   "<button value=\"VARKA\" action=\"bypass -h Quest SpecialGatekeeper 4\" back=\"L2UI_ch3.bigbutton_over\" fore=\"L2UI_ch3.bigbutton\" width=95 height=21><br>" +
				   
				   "Players Inside: <font color=\"PI\">" + getPlayerInsideCount(11013) + "</font>" +
                   "<button value=\"VARKA\" action=\"bypass -h Quest SpecialGatekeeper 4\" back=\"L2UI_ch3.bigbutton_over\" fore=\"L2UI_ch3.bigbutton\" width=95 height=21><br>" +

				   "<font color=\"cc9900\"><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32></font><br1></center></body></head></html>";
				   
		return htmltext;
	}
		
	/**
	 * @param zoneId
	 * @return player count from given zone ID
	 */
	@SuppressWarnings("deprecation")
	public static int getPlayerInsideCount(int zoneId)
	{
		int i = 0;
		for (L2ZoneType zone : ZoneManager.getInstance().getAllZones())
			if (zone.getId() == zoneId)
			{
				for (L2Character character : zone.getCharactersInside().values())
					if (character instanceof L2PcInstance)
						i++;
					return i;
			}
		return -1;
	}
	
	public static void main(String[] args)
	{
		new SpecialGatekeeper(-1, qn, "SpecialGatekeeper");
	}
}