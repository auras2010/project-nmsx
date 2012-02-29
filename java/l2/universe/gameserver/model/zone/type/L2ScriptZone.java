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
package l2.universe.gameserver.model.zone.type;

import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.model.actor.L2Attackable;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.zone.L2ZoneType;

/**
 * A scripted zone...
 * Creation of such a zone should require somekind
 * of jython script reference which can handle onEnter() / onExit()
 *
 * @author  durgus
 */
public class L2ScriptZone extends L2ZoneType
{
	public L2ScriptZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_SCRIPT, true);
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_SCRIPT, false);
		
		// Synerge - Add support for general zones for noExit function, checking also the current zone ID
		if (character instanceof L2Attackable && !character.isDead())
		{
			if (!character.canExitFromZone() && character.mustRemainInZone() == getId())
			{
				final L2Attackable npc = (L2Attackable) character;
				if (character.isZoneForcedReturnTeleport() && npc.getSpawn() != null)
				{
					npc.clearAggroList();
					npc.teleToLocation(npc.getSpawn().getLocx(), npc.getSpawn().getLocy(), npc.getSpawn().getLocz(), false);
				}
				else
				{
					character.abortAttack();
					character.abortCast();
					character.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
					npc.returnHome();
				}
			}
		}
	}
	
	@Override
	public void onDieInside(L2Character character)
	{
	}
	
	@Override
	public void onReviveInside(L2Character character)
	{
	}
}
