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

import l2.universe.Config;
import l2.universe.gameserver.model.L2Object.InstanceType;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.zone.L2SpawnZone;

/**
 * A Town zone
 *
 * @author  durgus
 */
public class L2TownZone extends L2SpawnZone
{
	private int _townId;
	/**
	 * Town war - don't delete
	*/
	@SuppressWarnings("unused")
	private int _redirectTownId;
	private int _taxById;
	private boolean _isPeaceZone;
	private boolean _isTWZone = false;
	
	public L2TownZone(int id)
	{
		super(id);
		
		_taxById = 0;
		_redirectTownId = 9;
		// Default not peace zone
		_isPeaceZone = false;
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("townId"))
		{
			_townId = Integer.parseInt(value);
		}
		/**
		 * Town war - don't delete
		 */
		else if (name.equals("redirectTownId"))
		{
			_redirectTownId = Integer.parseInt(value);
		}
		else if (name.equals("taxById"))
		{
			_taxById = Integer.parseInt(value);
		}
		else if (name.equals("isPeaceZone"))
		{
			_isPeaceZone = Boolean.parseBoolean(value);
		}
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			// PVP possible during siege, now for siege participants only
			// Could also check if this town is in siege, or if any siege is going on
			if (((L2PcInstance) character).getSiegeState() != 0 && Config.ZONE_TOWN == 1)
				return;
			
			if(Config.ENABLE_VITALITY)
				((L2PcInstance)character).startVitalityTask();
			
			// ((L2PcInstance)character).sendMessage("You entered "+_townName);
		}

		if (_isTWZone)
		{
			character.setInTownWarEvent(true);
			character.sendMessage("You entered a Town War event zone.");
		}
		
		if (_isPeaceZone && Config.ZONE_TOWN != 2)
			character.setInsideZone(L2Character.ZONE_PEACE, true);
		
		character.setInsideZone(L2Character.ZONE_TOWN, true);
		
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (_isTWZone)
		{
			character.setInTownWarEvent(false);
			character.sendMessage("You left a Town War event zone.");
		}
		
		// TODO: there should be no exit if there was possibly no enter
		if (_isPeaceZone)
			character.setInsideZone(L2Character.ZONE_PEACE, false);
			
		if (Config.ENABLE_VITALITY && character.isInstanceType(InstanceType.L2PcInstance))
			((L2PcInstance)character).stopVitalityTask();

		character.setInsideZone(L2Character.ZONE_TOWN, false);
		
		// if (character instanceof L2PcInstance)
		//((L2PcInstance)character).sendMessage("You left "+_townName);
		
	}
	
	public void onUpdate(L2Character character)
	{
		if (_isTWZone)
		{
			character.setInTownWarEvent(true);
			character.sendMessage("You entered a Town War event zone.");
		}
		else
		{
			character.setInTownWarEvent(false);
			character.sendMessage("You left a Town War event zone.");
		}
	}

	public void updateForCharactersInside()
	{
		for (L2Character character : _characterList.values())
		{
			if (character != null)
				onEnter(character);
			onUpdate(character);
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
	
	/**
	 * Returns this zones town id (if any)
	 * @return
	 */
	public int getTownId()
	{
		return _townId;
	}
	
	/**
	 * Returns this town zones castle id
	 * @return
	 */
	public final int getTaxById()
	{
		return _taxById;
	}
	
	public final boolean isPeaceZone()
	{
		return _isPeaceZone;
	}
	
	public final void setIsTWZone(boolean value)
	{
		_isTWZone = value;
	}
}
