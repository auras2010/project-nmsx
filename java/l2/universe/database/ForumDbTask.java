package l2.universe.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.communitybbs.BB.Forum;
import l2.universe.gameserver.communitybbs.BB.Topic;
import l2.universe.gameserver.communitybbs.Manager.ForumsBBSManager;
import l2.universe.gameserver.communitybbs.Manager.TopicBBSManager;

public class ForumDbTask
{
	private static final Logger _log = Logger.getLogger(ForumDbTask.class.getName());

	private static ForumDbTask task = null;

	public static ForumDbTask getInstance()
	{
		if(task == null)
		{
			task = new ForumDbTask();
		}
		return task;
	}

	public void loadForum(Forum forum)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM forums WHERE forum_id=?");
			statement.setInt(1, forum.getForumId());
			ResultSet result = statement.executeQuery();
			
			if (result.next())
			{
				forum.setForumName(result.getString("forum_name"));
				//_ForumParent = result.getInt("forum_parent");
				// _ForumParent = result.getInt("forum_parent");
				forum.setForumPost(result.getInt("forum_post"));
				forum.setForumType(result.getInt("forum_type"));
				forum.setForumPerm(result.getInt("forum_perm"));
				forum.setOwnerID(result.getInt("forum_owner_id"));
			}
			result.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Data error on Forum " + forum.getForumId() + " : " + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM topic WHERE topic_forum_id=? ORDER BY topic_id DESC");
			statement.setInt(1, forum.getForumId());
			ResultSet result = statement.executeQuery();
			
			while (result.next())
			{
				Topic t = new Topic(Topic.ConstructorType.RESTORE, result.getInt("topic_id"), result.getInt("topic_forum_id"), result.getString("topic_name"), result.getLong("topic_date"), result.getString("topic_ownername"), result.getInt("topic_ownerid"), result.getInt("topic_type"), result.getInt("topic_reply"));
				forum.getTopic().put(t.getID(), t);
				if (t.getID() > TopicBBSManager.getInstance().getMaxID(forum))
				{
					TopicBBSManager.getInstance().setMaxID(t.getID(), forum);
				}
			}
			result.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Data error on Forum " + forum.getForumId() + " : " + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	public void getForumChildren(Forum forum)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT forum_id FROM forums WHERE forum_parent=?");
			statement.setInt(1, forum.getForumId());
			ResultSet result = statement.executeQuery();
			
			while (result.next())
			{
				Forum f = new Forum(result.getInt("forum_id"), forum);
				forum.getForumChildren().add(f);
				ForumsBBSManager.getInstance().addForum(f);
			}
			result.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Data error on Forum (children): " + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	public void insertForumIntoDb(Forum forum)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("INSERT INTO forums (forum_id,forum_name,forum_parent,forum_post,forum_type,forum_perm,forum_owner_id) VALUES (?,?,?,?,?,?,?)");
			statement.setInt(1, forum.getForumId());
			statement.setString(2, forum.getForumName());
			statement.setInt(3, forum.getFParent().getID());
			statement.setInt(4, forum.getForumPost());
			statement.setInt(5, forum.getForumType());
			statement.setInt(6, forum.getForumPerm());
			statement.setInt(7, forum.getOwnerID());
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Error while saving new Forum to db " + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
}