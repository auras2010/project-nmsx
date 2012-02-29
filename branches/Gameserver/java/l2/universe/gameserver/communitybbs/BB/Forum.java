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
package l2.universe.gameserver.communitybbs.BB;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import l2.universe.database.ForumDbTask;
import l2.universe.gameserver.communitybbs.Manager.ForumsBBSManager;

public class Forum
{
	//type
	public static final int ROOT = 0;
	public static final int NORMAL = 1;
	public static final int CLAN = 2;
	public static final int MEMO = 3;
	public static final int MAIL = 4;
	//perm
	public static final int INVISIBLE = 0;
	public static final int ALL = 1;
	public static final int CLANMEMBERONLY = 2;
	public static final int OWNERONLY = 3;
	
	//private static Logger _log = Logger.getLogger(Forum.class.getName());
	private List<Forum> _children;
	private Map<Integer, Topic> _topic;
	private int _forumId;
	private String _forumName;
	//private int _ForumParent;
	private int _forumType;
	private int _forumPost;
	private int _forumPerm;
	private Forum _fParent;
	private int _ownerID;
	private boolean _loaded = false;
	
	/**
	 * Creates new instance of Forum. When you create new forum, use
	 * {@link l2.universe.gameserver.communitybbs.Manager.ForumsBBSManager#
	 * addForum(l2.universe.gameserver.communitybbs.BB.Forum)} to add forum
	 * to the forums manager.
	 *
	 * @param i
	 */
	public Forum(int Forumid, Forum FParent)
	{
		setForumId(Forumid);
		setFParent(FParent);
		setChildren(new FastList<Forum>());
		setTopic(new FastMap<Integer, Topic>());
		
		/*load();
		getChildren();	*/
	}
	
	/**
	 * @param name
	 * @param parent
	 * @param type
	 * @param perm
	 */
	public Forum(String name, Forum parent, int type, int perm, int OwnerID)
	{
		setForumName(name);
		setForumId(ForumsBBSManager.getInstance().getANewID());
		// _ForumParent = parent.getID();
		setForumType(type);
		setForumPost(0);
		setForumPerm(perm);
		setFParent(parent);
		setOwnerID(OwnerID);
		setChildren(new FastList<Forum>());
		setTopic(new FastMap<Integer, Topic>());
		parent.getForumChildren().add(this);
		ForumsBBSManager.getInstance().addForum(this);
		_loaded = true;
	}
	
	/**
	 *
	 */
	private void load()
	{
		ForumDbTask.getInstance().loadForum(this);
	}
	
	/**
	 *
	 */
	public void getChildren()
	{
		ForumDbTask.getInstance().getForumChildren(this);
	}
	
	public int getTopicSize()
	{
		vload();
		return getTopic().size();
	}
	
	public Topic getTopic(int j)
	{
		vload();
		return _topic.get(j);
	}
	
	public void addTopic(Topic t)
	{
		vload();
		getTopic().put(t.getID(), t);
	}
	
	/**
	* @return
	*/
	public int getID()
	{
		return getForumId();
	}
	
	public String getName()
	{
		vload();
		return getForumName();
	}
	
	public int getType()
	{
		vload();
		return getForumType();
	}
	
	/**
	 * @param name
	 * @return
	 */
	public Forum getChildByName(String name)
	{
		vload();
		for (Forum f : getForumChildren())
		{
			if (f.getName().equals(name))
			{
				return f;
			}
		}
		return null;
	}
	
	/**
	 * @param id
	 */
	public void rmTopicByID(int id)
	{
		getTopic().remove(id);
		
	}
	
	/**
	 *
	 */
	public void insertIntoDb()
	{
		ForumDbTask.getInstance().insertForumIntoDb(this);
	}
	
	/**
	 *
	 */
	public void vload()
	{
		if (_loaded == false)
		{
			load();
			getChildren();
			_loaded = true;
		}
	}

	/**
	 * @param forumId the forumId to set
	 */
	public void setForumId(int forumId)
	{
		_forumId = forumId;
	}

	/**
	 * @return the forumId
	 */
	public int getForumId()
	{
		return _forumId;
	}

	/**
	 * @param forumName the forumName to set
	 */
	public void setForumName(String forumName)
	{
		_forumName = forumName;
	}

	/**
	 * @return the forumName
	 */
	public String getForumName()
	{
		return _forumName;
	}

	/**
	 * @param forumPost the forumPost to set
	 */
	public void setForumPost(int forumPost)
	{
		_forumPost = forumPost;
	}

	/**
	 * @return the forumPost
	 */
	public int getForumPost()
	{
		return _forumPost;
	}

	/**
	 * @param forumType the forumType to set
	 */
	public void setForumType(int forumType)
	{
		_forumType = forumType;
	}

	/**
	 * @return the forumType
	 */
	public int getForumType()
	{
		return _forumType;
	}

	/**
	 * @param forumPerm the forumPerm to set
	 */
	public void setForumPerm(int forumPerm)
	{
		_forumPerm = forumPerm;
	}

	/**
	 * @return the forumPerm
	 */
	public int getForumPerm()
	{
		return _forumPerm;
	}

	/**
	 * @param ownerID the ownerID to set
	 */
	public void setOwnerID(int ownerID)
	{
		_ownerID = ownerID;
	}

	/**
	 * @return the ownerID
	 */
	public int getOwnerID()
	{
		return _ownerID;
	}

	/**
	 * @param topic the topic to set
	 */
	public void setTopic(Map<Integer, Topic> topic)
	{
		_topic = topic;
	}

	/**
	 * @return the topic
	 */
	public Map<Integer, Topic> getTopic()
	{
		return _topic;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<Forum> children)
	{
		_children = children;
	}

	/**
	 * @return the children
	 */
	public List<Forum> getForumChildren()
	{
		return _children;
	}

	/**
	 * @param fParent the fParent to set
	 */
	public void setFParent(Forum fParent)
	{
		_fParent = fParent;
	}

	/**
	 * @return the fParent
	 */
	public Forum getFParent()
	{
		return _fParent;
	}
}