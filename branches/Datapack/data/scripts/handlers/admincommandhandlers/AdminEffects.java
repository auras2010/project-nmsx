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
package handlers.admincommandhandlers;

import java.util.Collection;
import java.util.StringTokenizer;

import l2.universe.Config;
import l2.universe.gameserver.communitybbs.Manager.RegionBBSManager;
import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.handler.IAdminCommandHandler;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2ChestInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.Earthquake;
import l2.universe.gameserver.network.serverpackets.ExRedSky;
import l2.universe.gameserver.network.serverpackets.L2GameServerPacket;
import l2.universe.gameserver.network.serverpackets.MagicSkillUse;
import l2.universe.gameserver.network.serverpackets.PlaySound;
import l2.universe.gameserver.network.serverpackets.SSQInfo;
import l2.universe.gameserver.network.serverpackets.SocialAction;
import l2.universe.gameserver.network.serverpackets.StopMove;
import l2.universe.gameserver.network.serverpackets.SunRise;
import l2.universe.gameserver.network.serverpackets.SunSet;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.skills.AbnormalEffect;
import l2.universe.gameserver.util.Broadcast;

/**
 * This class handles following admin commands:
 *   <li> invis/invisible/vis/visible = makes yourself invisible or visible
 *   <li> earthquake = causes an earthquake of a given intensity and duration around you
 *   <li> bighead/shrinkhead = changes head size
 *   <li> gmspeed = temporary Super Haste effect.
 *   <li> para/unpara = paralyze/remove paralysis from target
 *   <li> para_all/unpara_all = same as para/unpara, affects the whole world.
 *   <li> polyself/unpolyself = makes you look as a specified mob.
 *   <li> changename = temporary change name
 *   <li> clearteams/setteam_close/setteam = team related commands
 *   <li> social = forces an L2Character instance to broadcast social action packets.
 *   <li> effect = forces an L2Character instance to broadcast MSU packets.
 *   <li> abnormal = force changes over an L2Character instance's abnormal state.
 *   <li> play_sound/play_sounds = Music broadcasting related commands
 *   <li> atmosphere = sky change related commands.
 */
public class AdminEffects implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_invis",
		"admin_invisible",
		"admin_vis",
		"admin_visible",
		"admin_invis_menu",
		"admin_earthquake",
		"admin_earthquake_menu",
		"admin_bighead",
		"admin_shrinkhead",
		"admin_gmspeed",
		"admin_gmspeed_menu",
		"admin_unpara_all",
		"admin_para_all",
		"admin_unpara",
		"admin_para",
		"admin_unpara_all_menu",
		"admin_para_all_menu",
		"admin_unpara_menu",
		"admin_para_menu",
		"admin_polyself",
		"admin_unpolyself",
		"admin_polyself_menu",
		"admin_unpolyself_menu",
		"admin_clearteams",
		"admin_setteam_close",
		"admin_setteam",
		"admin_social",
		"admin_effect",
		"admin_social_menu",
		"admin_special",
		"admin_special_menu",
		"admin_effect_menu",
		"admin_abnormal",
		"admin_abnormal_menu",
		"admin_play_sounds",
		"admin_play_sound",
		"admin_atmosphere",
		"admin_atmosphere_menu",
		"admin_set_displayeffect",
		"admin_set_displayeffect_menu"
	};
	
	public boolean useAdminCommand(String command, final L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		if (command.equals("admin_invis_menu"))
		{
			if (!activeChar.getAppearance().getInvisible())
			{
				activeChar.getAppearance().setInvisible();
				activeChar.broadcastUserInfo();
				activeChar.decayMe();
				activeChar.spawnMe();
			}
			else
			{
				activeChar.getAppearance().setVisible();
				activeChar.broadcastUserInfo();
			}
			RegionBBSManager.getInstance().changeCommunityBoard();
			command = "";
			AdminHelpPage.showHelpPage(activeChar, "gm_menu.htm");
		}
		else if (command.startsWith("admin_invis"))
		{
			activeChar.getAppearance().setInvisible();
			activeChar.broadcastUserInfo();
			activeChar.decayMe();
			activeChar.spawnMe();
			RegionBBSManager.getInstance().changeCommunityBoard();
		}
		
		else if (command.startsWith("admin_vis"))
		{
			activeChar.getAppearance().setVisible();
			activeChar.broadcastUserInfo();
			RegionBBSManager.getInstance().changeCommunityBoard();
		}
		else if (command.startsWith("admin_earthquake"))
		{
			try
			{
				final String val1 = st.nextToken();
				final int intensity = Integer.parseInt(val1);
				final String val2 = st.nextToken();
				final int duration = Integer.parseInt(val2);
				final Earthquake eq = new Earthquake(activeChar.getX(), activeChar.getY(), activeChar.getZ(), intensity, duration);
				activeChar.broadcastPacket(eq);
			}
			catch (final Exception e)
			{
				activeChar.sendMessage("Usage: //earthquake <intensity> <duration>");
			}
		}
		else if (command.startsWith("admin_atmosphere"))
		{
			try
			{
				final String type = st.nextToken();
				final String state = st.nextToken();
				final int duration = Integer.parseInt(st.nextToken());
				adminAtmosphere(type, state, duration, activeChar);
			}
			catch (final Exception ex)
			{
				activeChar.sendMessage("Usage: //atmosphere <signsky dawn|dusk>|<sky day|night|red>");
			}
		}
		else if (command.equals("admin_play_sounds"))
		{
			AdminHelpPage.showHelpPage(activeChar, "songs/songs.htm");
		}
		else if (command.startsWith("admin_play_sounds"))
		{
			try
			{
				AdminHelpPage.showHelpPage(activeChar, "songs/songs" + command.substring(18) + ".htm");
			}
			catch (final StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //play_sounds <pagenumber>");
			}
		}
		else if (command.startsWith("admin_play_sound"))
		{
			try
			{
				playAdminSound(activeChar, command.substring(17));
			}
			catch (final StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //play_sound <soundname>");
			}
		}
		else if (command.equals("admin_para_all"))
		{
			try
			{
				final Collection<L2PcInstance> plrs = activeChar.getKnownList().getKnownPlayers().values();
				for (final L2PcInstance player : plrs)
				{
					if (player.isGM())
						continue;

					player.startAbnormalEffect(AbnormalEffect.HOLD_1);
					player.setIsParalyzed(true);
					final StopMove sm = new StopMove(player);
					player.sendPacket(sm);
					player.broadcastPacket(sm);
				}
			}
			catch (final Exception e) {}
		}
		else if (command.equals("admin_unpara_all"))
		{
			try
			{
				final Collection<L2PcInstance> plrs = activeChar.getKnownList().getKnownPlayers().values();
				for (final L2PcInstance player : plrs)
				{
					player.stopAbnormalEffect(AbnormalEffect.HOLD_1);
					player.setIsParalyzed(false);
				}
			}
			catch (final Exception e) {}
		}
		else if (command.startsWith("admin_para")) // || command.startsWith("admin_para_menu"))
		{
			String type = "1";
			try
			{
				type = st.nextToken();
			}
			catch (final Exception e) {}

			try
			{
				if (activeChar.getTarget() instanceof L2Character)
				{
					final L2Character player = (L2Character) activeChar.getTarget();
					if (type.equals("1"))
						player.startAbnormalEffect(AbnormalEffect.HOLD_1);
					else
						player.startAbnormalEffect(AbnormalEffect.HOLD_2);
					player.setIsParalyzed(true);
					final StopMove sm = new StopMove(player);
					player.sendPacket(sm);
					player.broadcastPacket(sm);
				}
			}
			catch (final Exception e) {}
		}
		else if (command.startsWith("admin_unpara")) // || command.startsWith("admin_unpara_menu"))
		{
			String type = "1";
			try
			{
				type = st.nextToken();
			}
			catch (final Exception e) {}

			try
			{
				if (activeChar.getTarget() instanceof L2Character)
				{
					final L2Character player = (L2Character) activeChar.getTarget();
					if (type.equals("1"))
						player.stopAbnormalEffect(AbnormalEffect.HOLD_1);
					else
						player.stopAbnormalEffect(AbnormalEffect.HOLD_2);
					player.setIsParalyzed(false);
				}
			}
			catch (final Exception e) {}
		}
		else if (command.startsWith("admin_bighead"))
		{
			try
			{
				if (activeChar.getTarget() instanceof L2Character)
				{
					final L2Character player = (L2Character) activeChar.getTarget();
					player.startAbnormalEffect(AbnormalEffect.BIG_HEAD);
				}
			}
			catch (final Exception e) {}
		}
		else if (command.startsWith("admin_shrinkhead"))
		{
			try
			{
				if (activeChar.getTarget() instanceof L2Character)
				{
					final L2Character player = (L2Character) activeChar.getTarget();
					player.stopAbnormalEffect(AbnormalEffect.BIG_HEAD);
				}
			}
			catch (final Exception e) {}
		}
		else if (command.startsWith("admin_gmspeed"))
		{
			try
			{
				final int val = Integer.parseInt(st.nextToken());
				final boolean sendMessage = activeChar.getFirstEffect(7029) != null;
				activeChar.stopSkillEffects(7029);
				if (val == 0 && sendMessage)
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EFFECT_S1_DISAPPEARED).addSkillName(7029));
				else if (val >= 1 && val <= 4)
				{
					final L2Skill gmSpeedSkill = SkillTable.getInstance().getInfo(7029, val);
					activeChar.doSimultaneousCast(gmSpeedSkill);
				}
			}
			catch (final Exception e)
			{
				activeChar.sendMessage("Usage: //gmspeed <value> (0=off...4=max)");
			}
			
			if (command.contains("_menu"))
			{
				command = "";
				AdminHelpPage.showHelpPage(activeChar, "gm_menu.htm");
			}
		}
		else if (command.startsWith("admin_polyself"))
		{
			try
			{
				final String id = st.nextToken();
				activeChar.getPoly().setPolyInfo("npc", id);
				activeChar.teleToLocation(activeChar.getX(), activeChar.getY(), activeChar.getZ(), false);
				activeChar.broadcastUserInfo();
			}
			catch (final Exception e)
			{
				activeChar.sendMessage("Usage: //polyself <npcId>");
			}
		}
		else if (command.startsWith("admin_unpolyself"))
		{
			activeChar.getPoly().setPolyInfo(null, "1");
			activeChar.decayMe();
			activeChar.spawnMe(activeChar.getX(), activeChar.getY(), activeChar.getZ());
			activeChar.broadcastUserInfo();
		}
		else if (command.equals("admin_clearteams"))
		{
			try
			{
				final Collection<L2PcInstance> plrs = activeChar.getKnownList().getKnownPlayers().values();
				for (final L2PcInstance player : plrs)
				{
					player.setTeam(0);
					player.broadcastUserInfo();
				}
			}
			catch (final Exception e) {}
		}
		else if (command.startsWith("admin_setteam_close"))
		{
			try
			{
				final String val = st.nextToken();
				final int teamVal = Integer.parseInt(val);
				final Collection<L2PcInstance> plrs = activeChar.getKnownList().getKnownPlayers().values();
				for (final L2PcInstance player : plrs)
				{
					if (activeChar.isInsideRadius(player, 400, false, true))
					{
						player.setTeam(teamVal);
						if (teamVal != 0)
							player.sendMessage("You have joined team " + teamVal);
						player.broadcastUserInfo();
					}
				}
			}
			catch (final Exception e)
			{
				activeChar.sendMessage("Usage: //setteam_close <teamId>");
			}
		}
		else if (command.startsWith("admin_setteam"))
		{
			try
			{
				if (!(activeChar.getTarget() instanceof L2PcInstance))
					return false;
				
				final L2PcInstance player = (L2PcInstance) activeChar.getTarget();				
				final String val = st.nextToken();
				final int teamVal = Integer.parseInt(val);
				player.setTeam(teamVal);
				if (teamVal != 0)
					player.sendMessage("You have joined team " + teamVal);
				player.broadcastUserInfo();
			}
			catch (final Exception e)
			{
				activeChar.sendMessage("Usage: //setteam <teamId>");
			}
		}
		else if (command.startsWith("admin_social"))
		{
			try
			{
				L2Object obj = activeChar.getTarget();
				switch (st.countTokens())
				{
					case 1:
						int social = Integer.parseInt(st.nextToken());
						if (obj == null)
							obj = activeChar;
						
						if (performSocial(social, obj, activeChar))
							activeChar.sendMessage(obj.getName() + " was affected by your request.");
						else
							activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOTHING_HAPPENED));
						break;
					case 2:
						social = Integer.parseInt(st.nextToken());
						String target = st.nextToken();
						if (target == null)
							break;

						final L2PcInstance player = L2World.getInstance().getPlayer(target);
						if (player != null)
						{
							if (performSocial(social, player, activeChar))
								activeChar.sendMessage(player.getName() + " was affected by your request.");
						}
						else
						{
							try
							{
								final int radius = Integer.parseInt(target);
								final Collection<L2Object> objs = activeChar.getKnownList().getKnownObjects().values();
								for (final L2Object object : objs)
								{
									if (activeChar.isInsideRadius(object, radius, false, false))
										performSocial(social, object, activeChar);
								}
								activeChar.sendMessage(radius + " units radius affected by your request.");
							}
							catch (final NumberFormatException nbe)
							{
								activeChar.sendMessage("Incorrect parameter");
							}
						}
						break;
					default:
						if (!command.contains("menu"))
							activeChar.sendMessage("Usage: //social <social_id> [player_name|radius]");
						break;
				}
			}
			catch (final Exception e)
			{
				if (Config.DEBUG)
					e.printStackTrace();
			}
		}
		else if (command.startsWith("admin_abnormal"))
		{
			try
			{
				L2Object obj = activeChar.getTarget();
				switch (st.countTokens())
				{
					case 1:
						int abnormal = Integer.decode("0x" + st.nextToken());
						if (obj == null)
							obj = activeChar;
						
						if (performAbnormal(abnormal, obj))
							activeChar.sendMessage(obj.getName() + "'s abnormal status was affected by your request.");
						else
							activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOTHING_HAPPENED));
						break;
					case 2:
						final String parm = st.nextToken();
						abnormal = Integer.decode("0x" + parm);
						final String target = st.nextToken();
						if (target != null)
						{
							final L2PcInstance player = L2World.getInstance().getPlayer(target);
							if (player != null)
							{
								if (performAbnormal(abnormal, player))
									activeChar.sendMessage(player.getName() + "'s abnormal status was affected by your request.");
								else
									activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOTHING_HAPPENED));
							}
							else
							{
								try
								{
									final int radius = Integer.parseInt(target);
									final Collection<L2Object> objs = activeChar.getKnownList().getKnownObjects().values();
									for (final L2Object object : objs)
									{
										if (activeChar.isInsideRadius(object, radius, false, false))
											performAbnormal(abnormal, object);
									}
									activeChar.sendMessage(radius + " units radius affected by your request.");
								}
								catch (final NumberFormatException nbe)
								{
									activeChar.sendMessage("Usage: //abnormal <hex_abnormal_mask> [player|radius]");
								}
							}
						}
						break;
					default:
						if (!command.contains("menu"))
							activeChar.sendMessage("Usage: //abnormal <abnormal_mask> [player_name|radius]");
						break;
				}
			}
			catch (final Exception e)
			{
				if (Config.DEBUG)
					e.printStackTrace();
			}
		}
		else if (command.startsWith("admin_special"))
		{
			try
			{
				L2Object obj = activeChar.getTarget();
				switch (st.countTokens())
				{
					case 1:
						int special = Integer.decode("0x" + st.nextToken());
						if (obj == null)
							obj = activeChar;
						
						if (performSpecial(special, obj))
							activeChar.sendMessage(obj.getName() + "'s special status was affected by your request.");
						else
							activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOTHING_HAPPENED));
						break;
					case 2:
						final String parm = st.nextToken();
						special = Integer.decode("0x" + parm);
						final String target = st.nextToken();
						if (target != null)
						{
							final L2PcInstance player = L2World.getInstance().getPlayer(target);
							if (player != null)
							{
								if (performSpecial(special, player))
									activeChar.sendMessage(player.getName() + "'s special status was affected by your request.");
								else
									activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOTHING_HAPPENED));
							}
							else
							{
								try
								{
									final int radius = Integer.parseInt(target);
									final Collection<L2Object> objs = activeChar.getKnownList().getKnownObjects().values();
									for (final L2Object object : objs)
									{
										if (activeChar.isInsideRadius(object, radius, false, false))
											performSpecial(special, object);
									}
									activeChar.sendMessage(radius + " units radius affected by your request.");
								}
								catch (final NumberFormatException nbe)
								{
									activeChar.sendMessage("Usage: //special <hex_special_mask> [player|radius]");
								}
							}
						}
						break;
					default:
						if (!command.contains("menu"))
							activeChar.sendMessage("Usage: //special <special_mask> [player_name|radius]");
						break;
				}
			}
			catch (final Exception e)
			{
				if (Config.DEBUG)
					e.printStackTrace();
			}
		}
		else if (command.startsWith("admin_effect"))
		{
			try
			{
				L2Object obj = activeChar.getTarget();
				if (obj == null)
					obj = activeChar;
				else if (!(obj instanceof L2Character))
				{
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.INCORRECT_TARGET));
					return false;
				}
				
				int level = 1, hittime = 1;
				final int skill = Integer.parseInt(st.nextToken());
				if (st.hasMoreTokens())
					level = Integer.parseInt(st.nextToken());
				if (st.hasMoreTokens())
					hittime = Integer.parseInt(st.nextToken());

				final L2Character target = (L2Character) obj;
				target.broadcastPacket(new MagicSkillUse(target, activeChar, skill, level, hittime, 0));
				activeChar.sendMessage(obj.getName() + " performs MSU " + skill + "/" + level + " by your request.");			
			}
			catch (final Exception e)
			{
				activeChar.sendMessage("Usage: //effect skill [level | level hittime]");
			}
		}
		else if (command.startsWith("admin_set_displayeffect"))
		{
			L2Object target = activeChar.getTarget();
			if (!(target instanceof L2Npc))
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			L2Npc npc = (L2Npc) target;
			try
			{
				String type = st.nextToken();
				int diplayeffect = Integer.parseInt(type);
				npc.setDisplayEffect(diplayeffect);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //set_displayeffect <id>");
			}
		}
		if (command.contains("menu"))
			showMainPage(activeChar, command);
		
		return true;
	}
	
	/**
	 * @param action bitmask that should be applied over target's abnormal
	 * @param target
	 * @return <i>true</i> if target's abnormal state was affected , <i>false</i> otherwise.
	 */
	private boolean performAbnormal(final int action, final L2Object target)
	{
		if (!(target instanceof L2Character))
			return false;
		
		final L2Character character = (L2Character) target;
		if ((character.getAbnormalEffect() & action) == action)
			character.stopAbnormalEffect(action);
		else
			character.startAbnormalEffect(action);
		return true;
	}
	
	private boolean performSpecial(final int action, final L2Object target)
	{
		if (!(target instanceof L2PcInstance))
			return false;
		
		final L2Character character = (L2Character) target;
		if ((character.getSpecialEffect() & action) == action)
			character.stopSpecialEffect(action);
		else
			character.startSpecialEffect(action);
		return true;
	}
	
	private boolean performSocial(final int action, final L2Object target, final L2PcInstance activeChar)
	{
		try
		{
			if (!(target instanceof L2Character))
				return false;
			
			if (target instanceof L2ChestInstance)
			{
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOTHING_HAPPENED));
				return false;
			}
			if (target instanceof L2Npc && (action < 1 || action > 3))
			{
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOTHING_HAPPENED));
				return false;
			}
			if (target instanceof L2PcInstance && (action < 2 || action > 16))
			{
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOTHING_HAPPENED));
				return false;
			}
			
			final L2Character character = (L2Character) target;
			character.broadcastPacket(new SocialAction(target.getObjectId(), action));
		}
		catch (final Exception e) {}

		return true;
	}
	
	/**
	 *
	 * @param type - atmosphere type (signssky,sky)
	 * @param state - atmosphere state(night,day)
	 * @param duration 
	 */
	private void adminAtmosphere(final String type, final String state, final int duration, final L2PcInstance activeChar)
	{
		L2GameServerPacket packet = null;
		
		if (type.equals("signsky"))
		{
			if (state.equals("dawn"))
				packet = new SSQInfo(2);
			else if (state.equals("dusk"))
				packet = new SSQInfo(1);
		}
		else if (type.equals("sky"))
		{
			if (state.equals("night"))
				packet = new SunSet();
			else if (state.equals("day"))
				packet = new SunRise();
			else if (state.equals("red"))
			{
				if (duration != 0)
					packet = new ExRedSky(duration);
				else
					packet = new ExRedSky(10);
			}
		}
		else
			activeChar.sendMessage("Usage: //atmosphere <signsky dawn|dusk>|<sky day|night|red>");
		
		if (packet != null)
			Broadcast.toAllOnlinePlayers(packet);
	}
	
	private void playAdminSound(final L2PcInstance activeChar, final String sound)
	{
		final PlaySound _snd = new PlaySound(1, sound, 0, 0, 0, 0, 0);
		activeChar.sendPacket(_snd);
		activeChar.broadcastPacket(_snd);
		activeChar.sendMessage("Playing " + sound + ".");
	}	
	
	private void showMainPage(final L2PcInstance activeChar, final String command)
	{
		String filename = "effects_menu";
		if (command.contains("abnormal"))
			filename = "abnormal";
		else if (command.contains("special"))
			filename = "special";
		else if (command.contains("social"))
			filename = "social";
		AdminHelpPage.showHelpPage(activeChar, filename + ".htm");
	}
	
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
