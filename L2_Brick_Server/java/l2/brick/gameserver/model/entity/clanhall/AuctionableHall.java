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
package l2.brick.gameserver.model.entity.clanhall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Level;

import l2.brick.Config;
import l2.brick.L2DatabaseFactory;
import l2.brick.gameserver.ThreadPoolManager;
import l2.brick.gameserver.datatables.ClanTable;
import l2.brick.gameserver.instancemanager.AuctionManager;
import l2.brick.gameserver.instancemanager.ClanHallManager;
import l2.brick.gameserver.model.L2Clan;
import l2.brick.gameserver.model.StatsSet;
import l2.brick.gameserver.model.entity.ClanHall;
import l2.brick.gameserver.model.itemcontainer.PcInventory;
import l2.brick.gameserver.network.SystemMessageId;
import l2.brick.gameserver.network.serverpackets.SystemMessage;

public final class AuctionableHall extends ClanHall
{
	private long _paidUntil;
	private int _grade;
	private boolean _paid;
	private int _lease;
	
	protected final int _chRate = 604800000;
	
	public AuctionableHall(StatsSet set)
	{
		super(set);
		_paidUntil = set.getLong("paidUntil");
		_grade = set.getInteger("grade");
		_paid = set.getBool("paid");
		_lease = set.getInteger("lease");
		
		if(getOwnerId() != 0)
		{
			_isFree = false;
			initialyzeTask(false);
			loadFunctions();
		}
	}

	/**
	 * @return if clanHall is paid or not
	 */
	public final boolean getPaid()
	{
		return _paid;
	}
	
	/** Return lease*/
	@Override
	public final int getLease()
	{
		return _lease;
	}
	
	/** Return PaidUntil */
	@Override
	public final long getPaidUntil()
	{
		return _paidUntil;
	}
	
	/** Return Grade */
	@Override
	public final int getGrade()
	{
		return _grade;
	}
	
	@Override
	public final void free()
	{
		super.free();
		_paidUntil = 0;
		_paid = false;
	}
	
	@Override
	public final void setOwner(L2Clan clan)
	{
		super.setOwner(clan);
		_paidUntil = System.currentTimeMillis();
		initialyzeTask(true);
	}
	
	/**
	 * Initialize Fee Task 
	 * @param forced
	 */
	private final void initialyzeTask(boolean forced)
	{
		long currentTime = System.currentTimeMillis();
		if (_paidUntil > currentTime)
			ThreadPoolManager.getInstance().scheduleGeneral(new FeeTask(), _paidUntil - currentTime);
		else if (!_paid && !forced)
		{
			if (System.currentTimeMillis() + (1000 * 60 * 60 * 24) <= _paidUntil + _chRate)
				ThreadPoolManager.getInstance().scheduleGeneral(new FeeTask(), System.currentTimeMillis() + (1000 * 60 * 60 * 24));
			else
				ThreadPoolManager.getInstance().scheduleGeneral(new FeeTask(), (_paidUntil + _chRate) - System.currentTimeMillis());
		}
		else
			ThreadPoolManager.getInstance().scheduleGeneral(new FeeTask(), 0);
	}
	
	/** Fee Task */
	private class FeeTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				long _time = System.currentTimeMillis();
				
				if (_isFree)
					return;
				
				if(_paidUntil > _time)
				{
					ThreadPoolManager.getInstance().scheduleGeneral(new FeeTask(), _paidUntil - _time);
					return;
				}
				
				L2Clan Clan = ClanTable.getInstance().getClan(getOwnerId());
				if (ClanTable.getInstance().getClan(getOwnerId()).getWarehouse().getAdena() >= getLease())
				{
					if (_paidUntil != 0)
					{
						while (_paidUntil <= _time)
							_paidUntil += _chRate;
					}
					else
						_paidUntil = _time + _chRate;
					ClanTable.getInstance().getClan(getOwnerId()).getWarehouse().destroyItemByItemId("CH_rental_fee", PcInventory.ADENA_ID, getLease(), null, null);
					if (Config.DEBUG)
						_log.warning("deducted " + getLease() + " adena from " + getName() + " owner's cwh for ClanHall _paidUntil: " + _paidUntil);
					ThreadPoolManager.getInstance().scheduleGeneral(new FeeTask(), _paidUntil - _time);
					_paid = true;
					updateDb();
				}
				else
				{
					_paid = false;
					if (_time > _paidUntil + _chRate)
					{
						if (ClanHallManager.getInstance().loaded())
						{
							AuctionManager.getInstance().initNPC(getId());
							ClanHallManager.getInstance().setFree(getId());
							Clan.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED));
						}
						else
							ThreadPoolManager.getInstance().scheduleGeneral(new FeeTask(), 3000);
					}
					else
					{
						updateDb();
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW);
						sm.addNumber(getLease());
						Clan.broadcastToOnlineMembers(sm);
						if (_time + (1000 * 60 * 60 * 24) <= _paidUntil + _chRate)
							ThreadPoolManager.getInstance().scheduleGeneral(new FeeTask(), _time + (1000 * 60 * 60 * 24));
						else
							ThreadPoolManager.getInstance().scheduleGeneral(new FeeTask(), (_paidUntil + _chRate) - _time);
						
					}
				}
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "", e);
			}
		}
	}
	
	@Override
	public final void updateDb()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement;
			
			statement = con.prepareStatement("UPDATE clanhall SET ownerId=?, paidUntil=?, paid=? WHERE id=?");
			statement.setInt(1, getOwnerId());
			statement.setLong(2, getPaidUntil());
			statement.setInt(3, (getPaid()) ? 1 : 0);
			statement.setInt(4, getId());
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception: updateOwnerInDB(L2Clan clan): " + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
}
