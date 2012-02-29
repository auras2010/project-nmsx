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
package l2.universe.gameserver.model;

import l2.universe.gameserver.model.actor.instance.L2PcInstance;

/**
 *
 * @author  KenM
 */
public abstract class L2Transformation implements Cloneable, Runnable
{
	private final int _id;
	private final int _graphicalId;
	private final int _mountNpcId;
	private double _collisionRadius;
	private double _collisionHeight;
	private final boolean _isStance;
	private final boolean _isMounting;

	public static final int TRANSFORM_ZARICHE = 301;
	public static final int TRANSFORM_AKAMANAH = 302;
	public static final int PURPLE_MANED_HORSE_TRANSFORMATION_ID = 106;	
	public static final int JET_BIKE_TRANSFORMATION_ID = 20001;
	public static final int TAWNY_MANED_LION_TRANSFORMATION_ID = 109;
	public static final int STEAM_BEATLE_TRANSFORMATION_ID = 110;
	
	protected static final int[] EMPTY_ARRAY = {};

	private L2PcInstance _player;
	
	/**
	 * 
	 * @param id
	 *            Internal id that server will use to associate this transformation
	 * @param graphicalId
	 *            Client visible transformation id
	 * @param collisionRadius
	 *            Collision Radius of the player while transformed
	 * @param collisionHeight
	 *            Collision Height of the player while transformed
	 */
	public L2Transformation(int id, int graphicalId, double collisionRadius, double collisionHeight, int mountNpcId)
	{
		_id = id;
		_graphicalId = graphicalId;
		_mountNpcId = mountNpcId;
		_collisionRadius = collisionRadius;
		_collisionHeight = collisionHeight;
		_isStance = false;
		
		// Synerge - See if is a mounting transformation
		switch (_id)
		{
			case PURPLE_MANED_HORSE_TRANSFORMATION_ID:
			case JET_BIKE_TRANSFORMATION_ID:
			case TAWNY_MANED_LION_TRANSFORMATION_ID:
			case STEAM_BEATLE_TRANSFORMATION_ID:
				_isMounting = true;
				break;
			default:
				_isMounting = false;
		}
	}

	/**
	 * 
	 * @param id
	 *            Internal id(will be used also as client graphical id) that server will use to
	 *            associate this transformation
	 * @param collisionRadius
	 *            Collision Radius of the player while transformed
	 * @param collisionHeight
	 *            Collision Height of the player while transformed
	 */
	public L2Transformation(int id, double collisionRadius, double collisionHeight)
	{
		this(id, id, collisionRadius, collisionHeight, 0);
	}
	
	/**
	 * 
	 * @param id
	 *            Internal id(will be used also as client graphical id) that server will use to
	 *            associate this transformation
	 * @param collisionRadius
	 *            Collision Radius of the player while transformed
	 * @param collisionHeight
	 *            Collision Height of the player while transformed
	 * @param mountNpcId
	 * 			  NpcId for mountable transformation
	 */
	public L2Transformation(int id, double collisionRadius, double collisionHeight, int mountNpcId)
	{
		this(id, id, collisionRadius, collisionHeight, mountNpcId);
	}
	
	/**
	 * 
	 * @param id
	 *            Internal id(will be used also as client graphical id) that server will use to
	 *            associate this transformation Used for stances
	 */
	public L2Transformation(int id)
	{
		_id = id;
		_graphicalId = id;
		_mountNpcId = 0;
		_isStance = true;
		
		// Synerge - See if is a mounting transformation
		switch (_id)
		{
			case PURPLE_MANED_HORSE_TRANSFORMATION_ID:
			case JET_BIKE_TRANSFORMATION_ID:
			case TAWNY_MANED_LION_TRANSFORMATION_ID:
			case STEAM_BEATLE_TRANSFORMATION_ID:
				_isMounting = true;
				break;
			default:
				_isMounting = false;
		}
	}

	/**
	 * @return Returns the id.
	 */
	public int getId()
	{
		return _id;
	}

	/**
	 * @return Returns the graphicalId.
	 */
	public int getGraphicalId()
	{
		return _graphicalId;
	}
	
	/**
	 * @return Returns the id.
	 */
	public int getMountNpcID()
	{
		return _mountNpcId;
	}

	/**
	 * Return true if this is a stance (vanguard/inquisitor)
	 * @return
	 */
	public boolean isStance()
	{
		return _isStance;
	}

	/**
	 * @return Returns the collisionRadius.
	 */
	public double getCollisionRadius()
	{
		if (isStance())
			return _player.getCollisionRadius();
		return _collisionRadius;
	}

	/**
	 * @return Returns the collisionHeight.
	 */
	public double getCollisionHeight()
	{
		if (isStance())
			return _player.getCollisionHeight();
		return _collisionHeight;
	}

	// Scriptable Events
	public abstract void onTransform();

	public abstract void onUntransform();

	/**
	 * @param player The player to set.
	 */
	private void setPlayer(L2PcInstance player)
	{
		_player = player;
	}

	/**
	 * @return Returns the player.
	 */
	public L2PcInstance getPlayer()
	{
		return _player;
	}

	public void start()
	{
		this.resume();
	}

	public void resume()
	{
		this.getPlayer().transform(this);
	}

	public void run()
	{
		this.stop();
	}

	public void stop()
	{
		this.getPlayer().untransform();
	}

	public L2Transformation createTransformationForPlayer(L2PcInstance player)
	{
		try
		{
			L2Transformation transformation = (L2Transformation) this.clone();
			transformation.setPlayer(player);
			return transformation;
		}
		catch (CloneNotSupportedException e)
		{
			// should never happen
			return null;
		}
	}

	// Override if necessary
	public void onLevelUp()
	{
	}

	/**
	 * Returns true if transformation can do melee attack
	 */
	public boolean canDoMeleeAttack()
	{
		return true;
	}

	/**
	 * Returns true if transformation can start follow target when trying to cast an skill out of range
	 */
	public boolean canStartFollowToCast()
	{
		return true;
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName()+" [_id=" + _id + ", _graphicalId=" + _graphicalId + ", _collisionRadius=" + _collisionRadius + ", _collisionHeight=" + _collisionHeight + ", _isStance=" + _isStance + "]";
	}
	
	// Synerge - Returns true if this transformation is a mounting transformation
	public boolean isMountingTransformation()
	{
		return _isMounting;
	}
}
