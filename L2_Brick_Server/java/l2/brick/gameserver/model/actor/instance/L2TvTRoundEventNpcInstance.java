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
package l2.brick.gameserver.model.actor.instance;

import l2.brick.Config;
import l2.brick.gameserver.cache.HtmCache;
import l2.brick.gameserver.model.actor.L2Npc;
import l2.brick.gameserver.model.entity.TvTRoundEvent;
import l2.brick.gameserver.network.serverpackets.ActionFailed;
import l2.brick.gameserver.network.serverpackets.NpcHtmlMessage;
import l2.brick.gameserver.templates.chars.L2NpcTemplate;

public class L2TvTRoundEventNpcInstance extends L2Npc
{
	private static final String htmlPath="data/html/mods/TvTRoundEvent/";
	
	public L2TvTRoundEventNpcInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		setInstanceType(InstanceType.L2TvTRoundEventNpcInstance);
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance playerInstance, String command)
	{
		TvTRoundEvent.onBypass(command, playerInstance);
	}
	
	@Override
	public void showChatWindow(L2PcInstance playerInstance, int val)
	{
		if (playerInstance == null)
			return;
		
		if (TvTRoundEvent.isParticipating())
		{
			final boolean isParticipant = TvTRoundEvent.isPlayerParticipant(playerInstance.getObjectId());
			final String htmContent;
			
			if (!isParticipant)
				htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "Participation.htm");
			else
				htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "RemoveParticipation.htm");
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = TvTRoundEvent.getTeamsPlayerCounts();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%roundteam1name%", Config.TVT_ROUND_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace("%roundteam1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%roundteam2name%", Config.TVT_ROUND_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace("%roundteam2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%roundplayercount%", String.valueOf(teamsPlayerCounts[0]+teamsPlayerCounts[1]));
				if (!isParticipant)
					npcHtmlMessage.replace("%roundfee%", TvTRoundEvent.getParticipationFee());
				
				playerInstance.sendPacket(npcHtmlMessage);
			}
		}
		else if (TvTRoundEvent.isStarting() || TvTRoundEvent.isStarted())
		{
			final String htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "Status.htm");
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = TvTRoundEvent.getTeamsPlayerCounts();
				int[] teamsPointsCounts = TvTRoundEvent.getTeamsPoints();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				//npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%roundteam1name%", Config.TVT_ROUND_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace("%roundteam1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%roundteam1points%", String.valueOf(teamsPointsCounts[0]));
				npcHtmlMessage.replace("%roundteam2name%", Config.TVT_ROUND_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace("%roundteam2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%roundteam2points%", String.valueOf(teamsPointsCounts[1])); // <---- array index from 0 to 1 thx DaRkRaGe
				playerInstance.sendPacket(npcHtmlMessage);
			}
		}
		
		playerInstance.sendPacket(ActionFailed.STATIC_PACKET);
	}
}
