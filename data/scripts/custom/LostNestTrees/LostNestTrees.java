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

package custom.LostNestTrees;

import java.util.Collection;
import java.util.concurrent.ScheduledFuture;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.zone.L2ZoneType;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ExSetCompassZoneCode;
import l2.universe.gameserver.network.serverpackets.StatusUpdate;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

/*
 * source: http://v3.elliebelly.net/index.php?option=com_kunena&Itemid=55&func=view&catid=18&id=13304
*/

public class LostNestTrees extends Quest
{
	private static final String qn = "LostNestTrees";
	private static final double mpBonus = 36;
	private static final int[] ZONES = { 12203, 12204 };
	protected ScheduledFuture<?> _mpTask = null;
	
	@Override
	public String onEnterZone(L2Character character, L2ZoneType zone)
	{
		if (character instanceof L2PcInstance)
		{
			character.sendPacket(new ExSetCompassZoneCode(ExSetCompassZoneCode.ALTEREDZONE));
			if (!checkIfPc(zone) && _mpTask == null)
				_mpTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new giveMp(zone), 3000, 3000);
		}
		return super.onEnterZone(character, zone);
	}
	
	@Override
	public String onExitZone(L2Character character, L2ZoneType zone)
	{
		if (character instanceof L2PcInstance)
		{
			character.sendPacket(new ExSetCompassZoneCode(ExSetCompassZoneCode.GENERALZONE));
			if (howManyPc(zone) == 1 && _mpTask != null)
			{
				_mpTask.cancel(true);
				_mpTask = null;
			}
		}
		return super.onExitZone(character, zone);
	}
	
	private boolean checkIfPc(L2ZoneType zone)
	{
		Collection<L2Character> inside = zone.getCharactersInside().values();
		for (L2Character c : inside)
		{
			if (c instanceof L2PcInstance)
				return true;
		}
		return false;
	}
	
	private int howManyPc(L2ZoneType zone)
	{
		int count = 0;
		Collection<L2Character> inside = zone.getCharactersInside().values();
		for (L2Character c : inside)
		{
			if (c instanceof L2PcInstance)
				count++;
		}
		return count;
	}
	
	private void updateMp(L2Character player)
	{
		double currentMp = player.getCurrentMp();
		double maxMp = player.getMaxMp();
		if (currentMp != maxMp)
		{
			double newMp = 0;
			if ((currentMp + mpBonus) >= maxMp)
				newMp = maxMp;
			else
				newMp = currentMp + mpBonus;
			player.setCurrentMp(newMp);
			StatusUpdate sump = new StatusUpdate(player.getObjectId());
			sump.addAttribute(StatusUpdate.CUR_MP, (int) newMp);
			player.sendPacket(sump);
			// system message
			SystemMessage smp = SystemMessage.getSystemMessage(SystemMessageId.S1_MP_RESTORED);
			smp.addNumber((int) mpBonus);
			player.sendPacket(smp);
		}
	}
	
	private class giveMp implements Runnable
	{
		private L2ZoneType _zone;
		
		public giveMp(L2ZoneType zone)
		{
			_zone = zone;
		}
		
		public void run()
		{
			if (howManyPc(_zone) > 0)
			{
				Collection<L2Character> inside = _zone.getCharactersInside().values();
				for (L2Character c : inside)
				{
					if (c instanceof L2PcInstance)
						updateMp(c);
				}
			}
			else if (_mpTask != null)
			{
				_mpTask.cancel(true);
				_mpTask = null;
			}
		}
	}
	
	public LostNestTrees(int questId, String name, String descr)
	{
		super(questId, name, descr);
		for (int zones : ZONES)
		{
			addEnterZoneId(zones);
			addExitZoneId(zones);
		}
	}
	
	public static void main(String[] args)
	{
		new LostNestTrees(-1, qn, "custom");
	}
}