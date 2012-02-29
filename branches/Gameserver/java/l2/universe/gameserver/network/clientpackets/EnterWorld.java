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
package l2.universe.gameserver.network.clientpackets;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import l2.universe.Base64;
import l2.universe.Config;
import l2.universe.ExternalConfig;
import l2.universe.gameserver.Announcements;
import l2.universe.gameserver.GameTimeController;
import l2.universe.gameserver.GmListTable;
import l2.universe.gameserver.LoginServerThread;
import l2.universe.gameserver.SevenSigns;
import l2.universe.gameserver.TaskPriority;
import l2.universe.gameserver.cache.HtmCache;
import l2.universe.gameserver.communitybbs.Manager.RegionBBSManager;
import l2.universe.gameserver.datatables.AdminCommandAccessRights;
import l2.universe.gameserver.datatables.GMSkillTable;
import l2.universe.gameserver.datatables.MapRegionTable;
import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.handler.ScriptHandler;
import l2.universe.gameserver.handler.ScriptHandler.CallSite;
import l2.universe.gameserver.instancemanager.BotManager;
import l2.universe.gameserver.instancemanager.CHSiegeManager;
import l2.universe.gameserver.instancemanager.CastleManager;
import l2.universe.gameserver.instancemanager.ClanHallManager;
import l2.universe.gameserver.instancemanager.CoupleManager;
import l2.universe.gameserver.instancemanager.CursedWeaponsManager;
import l2.universe.gameserver.instancemanager.DimensionalRiftManager;
import l2.universe.gameserver.instancemanager.FortManager;
import l2.universe.gameserver.instancemanager.FortSiegeManager;
import l2.universe.gameserver.instancemanager.InstanceManager;
import l2.universe.gameserver.instancemanager.KrateisCubeManager;
import l2.universe.gameserver.instancemanager.MailManager;
import l2.universe.gameserver.instancemanager.PetitionManager;
import l2.universe.gameserver.instancemanager.QuestManager;
import l2.universe.gameserver.instancemanager.SiegeManager;
import l2.universe.gameserver.instancemanager.TerritoryWarManager;
import l2.universe.gameserver.model.L2Clan;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.L2World;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.model.actor.instance.L2ClassMasterInstance;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.Couple;
import l2.universe.gameserver.model.entity.Fort;
import l2.universe.gameserver.model.entity.FortSiege;
import l2.universe.gameserver.model.entity.L2Event;
import l2.universe.gameserver.model.entity.Siege;
import l2.universe.gameserver.model.entity.clanhall.AuctionableHall;
import l2.universe.gameserver.model.entity.clanhall.SiegableHall;
import l2.universe.gameserver.model.entity.events.CTF;
import l2.universe.gameserver.model.entity.events.DM;
import l2.universe.gameserver.model.entity.events.TvTEvent;
import l2.universe.gameserver.model.entity.events.TvTRoundEvent;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.communityserver.CommunityServerThread;
import l2.universe.gameserver.network.communityserver.writepackets.WorldInfo;
import l2.universe.gameserver.network.serverpackets.Die;
import l2.universe.gameserver.network.serverpackets.EtcStatusUpdate;
import l2.universe.gameserver.network.serverpackets.ExBasicActionList;
import l2.universe.gameserver.network.serverpackets.ExBirthdayPopup;
import l2.universe.gameserver.network.serverpackets.ExBrPremiumState;
import l2.universe.gameserver.network.serverpackets.ExGetBookMarkInfoPacket;
import l2.universe.gameserver.network.serverpackets.ExKrateiMatchCCRecord;
import l2.universe.gameserver.network.serverpackets.ExNoticePostArrived;
import l2.universe.gameserver.network.serverpackets.ExNotifyPremiumItem;
import l2.universe.gameserver.network.serverpackets.ExShowScreenMessage;
import l2.universe.gameserver.network.serverpackets.ExStorageMaxCount;
import l2.universe.gameserver.network.serverpackets.ExVoteSystemInfo;
import l2.universe.gameserver.network.serverpackets.FriendList;
import l2.universe.gameserver.network.serverpackets.HennaInfo;
import l2.universe.gameserver.network.serverpackets.ItemList;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;
import l2.universe.gameserver.network.serverpackets.PledgeShowMemberListAll;
import l2.universe.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import l2.universe.gameserver.network.serverpackets.PledgeSkillList;
import l2.universe.gameserver.network.serverpackets.PledgeStatusChanged;
import l2.universe.gameserver.network.serverpackets.QuestList;
import l2.universe.gameserver.network.serverpackets.ShortCutInit;
import l2.universe.gameserver.network.serverpackets.SkillCoolTime;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.security.MultiBoxProtection;

/**
 * Enter World Packet Handler<p>
 * <p>
 * 0000: 03 <p>
 * packet format rev87 bddddbdcccccccccccccccccccc
 * <p>
 */
public class EnterWorld extends L2GameClientPacket
{
	private static final String _C__03_ENTERWORLD = "[C] 03 EnterWorld";

	private static Logger _log = Logger.getLogger(EnterWorld.class.getName());
	
	private int[][] tracert = new int[5][4];
	
	public TaskPriority getPriority()
	{
		return TaskPriority.PR_URGENT;
	}
	
	@Override
	protected void readImpl()
	{
		readB(new byte[32]); // Unknown Byte Array
		readD(); // Unknown Value
		readD(); // Unknown Value
		readD(); // Unknown Value
		readD(); // Unknown Value
		readB(new byte[32]); // Unknown Byte Array
		readD(); // Unknown Value
		for (int i = 0; i < 5; i++)
		{
			for (int o = 0; o < 4; o++)
				tracert[i][o] = readC();
		}
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			_log.warning("EnterWorld failed! activeChar returned 'null'.");
			getClient().closeNow();
			return;
		}
		
		String[] adress = new String[5];
		for (int i = 0; i < 5; i++)
			adress[i] = tracert[i][0] + "." + tracert[i][1] + "." + tracert[i][2] + "." + tracert[i][3];
		
		LoginServerThread.getInstance().sendClientTracert(activeChar.getAccountName(), adress);
		
		getClient().setClientTracert(tracert);
		
		// Synerge - Save PcIp on list, for multibox protection, this will avoid continue calls to DB
		MultiBoxProtection.getInstance().registerNewPcIp(activeChar.getAccountName(), adress[0]);
		
		// Synerge - Save connection IP for later use in offline trade check
		activeChar.saveConnectionAddress();
		
		// Synerge - New multibox protection. GMs chars dont count as a box
		if (!MultiBoxProtection.getInstance().checkMultiBox(getClient(), adress[0]))
		{
			//getClient().close(LeaveWorld.STATIC_PACKET);
			activeChar.logout(false);
			return;
		}
		
		// Restore to instanced area if enabled
		if (Config.RESTORE_PLAYER_INSTANCE)
			activeChar.setInstanceId(InstanceManager.getInstance().getPlayerInstance(activeChar.getObjectId()));
		else
		{
			int instanceId = InstanceManager.getInstance().getPlayerInstance(activeChar.getObjectId());
			if (instanceId > 0)
				InstanceManager.getInstance().getInstance(instanceId).removePlayer(activeChar.getObjectId());
		}
		
		if (L2World.getInstance().findObject(activeChar.getObjectId()) != null)
		{
			if (Config.DEBUG)
				_log.warning("User already exists in Object ID map! User " + activeChar.getName() + " is a character clone.");
		}
		
		// Apply special GM properties to the GM when entering
		if (activeChar.isGM())
		{
			if (Config.GM_STARTUP_INVULNERABLE && AdminCommandAccessRights.getInstance().hasAccess("admin_invul", activeChar.getAccessLevel()))
				activeChar.setIsInvul(true);
			
			if (Config.GM_STARTUP_INVISIBLE && AdminCommandAccessRights.getInstance().hasAccess("admin_invisible", activeChar.getAccessLevel()))
				activeChar.getAppearance().setInvisible();
			
			if (Config.GM_STARTUP_SILENCE && AdminCommandAccessRights.getInstance().hasAccess("admin_silence", activeChar.getAccessLevel()))
				activeChar.setSilenceMode(true);
			
			if (Config.GM_STARTUP_DIET_MODE && AdminCommandAccessRights.getInstance().hasAccess("admin_diet", activeChar.getAccessLevel()))
			{
				activeChar.setDietMode(true);
				activeChar.refreshOverloaded();
			}
			
			if (Config.GM_STARTUP_AUTO_LIST && AdminCommandAccessRights.getInstance().hasAccess("admin_gmliston", activeChar.getAccessLevel()))
				GmListTable.getInstance().addGm(activeChar, false);
			else
				GmListTable.getInstance().addGm(activeChar, true);
			
			if (Config.GM_GIVE_SPECIAL_SKILLS)
				GMSkillTable.getInstance().addSkills(activeChar);
		}
		
		activeChar.loadSchemesFromDatabase();

		// Synerge - Apply night/day bonus on skill Shadow Sense
		if (activeChar.getRace().ordinal() == 2)
		{
			final L2Skill skill = SkillTable.getInstance().getInfo(294, 1);
			if (skill != null && activeChar.getSkillLevel(294) == 1)
			{
				if (GameTimeController.getInstance().isNowNight())
				{
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.NIGHT_EFFECT_APPLIES_S1);
					sm.addSkillName(294);
					sendPacket(sm);
				}
				else
				{
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.DAY_EFFECT_DISAPPEARS_S1);
					sm.addSkillName(294);
					sendPacket(sm);
				}
			}
		}
		
		/** Kratei's cube */
		long plX = activeChar.getX();//Used by Tempfix bellow: get player current X position
		long plY = activeChar.getY();//Used by Tempfix bellow: get player current Y position
		long plZ = activeChar.getZ();//Used by Tempfix bellow: get player current Z position
		if (KrateisCubeManager.getInstance().isRegistered(activeChar))
			{
				activeChar.setIsInKrateisCube(true);
				activeChar.sendPacket(new ExKrateiMatchCCRecord(1, KrateisCubeManager.krateisScore));//Score on Click to button
			}
		else if (plZ < -8000)//Tempfix for unregistered player stuck in Kratei's Cube
			{
				if (plZ > -8500)
				{
					if (plX > -91326)
					{
						if (plX < -74008)
						{
							if (plY > -91329)
							{
								if (plY < -74231)
								{
									activeChar.teleToLocation(-70381, -70937, -1428);
								}
							}
						}
					}
				}
			}
		
		/** Bot manager punishment */
		if (ExternalConfig.ENABLE_BOTREPORT)
			BotManager.getInstance().onEnter(activeChar);
		
		// Set dead status if applies
		if (activeChar.getCurrentHp() < 0.5)
			activeChar.setIsDead(true);
		
		boolean showClanNotice = false;
		
		// Clan related checks are here
		L2Clan clan = activeChar.getClan();
		if (clan != null)
		{
			activeChar.sendPacket(new PledgeSkillList(clan));
			
			notifyClanMembers(activeChar);
			
			notifySponsorOrApprentice(activeChar);
			
			// Only show message to clan leader
			AuctionableHall clanHall = ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan());	
			if (clanHall != null && activeChar.isClanLeader())
			{
				if (!clanHall.getPaid())
				{
					//clanHall.updateDb();
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW));
				}
			}
			
			for (Siege siege : SiegeManager.getInstance().getSieges())
			{
				if (!siege.getIsInProgress())
					continue;
				
				if (siege.checkIsAttacker(clan))
				{
					activeChar.setSiegeState((byte) 1);
					activeChar.setSiegeSide(siege.getCastle().getCastleId());
				}
				
				else if (siege.checkIsDefender(clan))
				{
					activeChar.setSiegeState((byte) 2);
					activeChar.setSiegeSide(siege.getCastle().getCastleId());
				}
			}
			
			for (FortSiege siege : FortSiegeManager.getInstance().getSieges())
			{
				if (!siege.getIsInProgress())
					continue;
				
				if (siege.checkIsAttacker(clan))
				{
					activeChar.setSiegeState((byte) 1);
					activeChar.setSiegeSide(siege.getFort().getFortId());
				}
				
				else if (siege.checkIsDefender(clan))
				{
					activeChar.setSiegeState((byte) 2);
					activeChar.setSiegeSide(siege.getFort().getFortId());
				}
			}
			
			for(SiegableHall hall : CHSiegeManager.getInstance().getConquerableHalls().values())
			{
				if(!hall.isInSiege())
					continue;
				
				if(hall.isRegistered(activeChar.getClan()))
				{
					activeChar.setSiegeState((byte)1);
					activeChar.setSiegeSide(hall.getId());
				}
			}
			
			sendPacket(new PledgeShowMemberListAll(clan, activeChar));
			sendPacket(new PledgeStatusChanged(clan));
			
			// Residential skills support
			if (clan.getHasCastle() > 0)
				CastleManager.getInstance().getCastleByOwner(clan).giveResidentialSkills(activeChar);
			
			if (clan.getHasFort() > 0)
				FortManager.getInstance().getFortByOwner(clan).giveResidentialSkills(activeChar);
			
			showClanNotice = clan.isNoticeEnabled();
		}
		
		if (TerritoryWarManager.getInstance().getRegisteredTerritoryId(activeChar) > 0)
		{
			if (TerritoryWarManager.getInstance().isTWInProgress())
				activeChar.setSiegeState((byte) 1);
			activeChar.setSiegeSide(TerritoryWarManager.getInstance().getRegisteredTerritoryId(activeChar));
		}
		
		// Updating Seal of Strife Buff/Debuff 
		if (SevenSigns.getInstance().isSealValidationPeriod() && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) != SevenSigns.CABAL_NULL)
		{
			int cabal = SevenSigns.getInstance().getPlayerCabal(activeChar.getObjectId());
			if (cabal != SevenSigns.CABAL_NULL)
			{
				if (cabal == SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE))
					activeChar.addSkill(SkillTable.FrequentSkill.THE_VICTOR_OF_WAR.getSkill());
				else
					activeChar.addSkill(SkillTable.FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill());
			}
		}
		else
		{
			activeChar.removeSkill(SkillTable.FrequentSkill.THE_VICTOR_OF_WAR.getSkill());
			activeChar.removeSkill(SkillTable.FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill());
		}
		
		if (Config.ENABLE_VITALITY && Config.RECOVER_VITALITY_ON_RECONNECT)
		{
			float points = Config.RATE_RECOVERY_ON_RECONNECT * (System.currentTimeMillis() - activeChar.getLastAccess()) / 60000;
			if (points > 0)
				activeChar.updateVitalityPoints(points, false, true);
		}
		
		activeChar.checkRecoBonusTask();
		
		activeChar.broadcastUserInfo();
		
		// Send Macro List
		activeChar.getMacroses().sendUpdate();
		
		// Send Item List
		sendPacket(new ItemList(activeChar, false));
		
		// Send GG check
		activeChar.queryGameGuard();
		
		// Send Teleport Bookmark List
		sendPacket(new ExGetBookMarkInfoPacket(activeChar));
		
		// Send Shortcuts
		sendPacket(new ShortCutInit(activeChar));
		
		// Premium account state
        sendPacket(new ExBrPremiumState(activeChar, activeChar.getPremiumService()));

        // Send Action list
		activeChar.sendPacket(ExBasicActionList.getStaticPacket(activeChar));
		
		// Send Skill list
		activeChar.sendSkillList();
		
		// Send Dye Information
		activeChar.sendPacket(new HennaInfo(activeChar));
		
		Quest.playerEnter(activeChar);
		
		if (!Config.DISABLE_TUTORIAL)
			loadTutorial(activeChar);
		
		for (Quest quest : QuestManager.getInstance().getAllManagedScripts())
		{
			if (quest != null && quest.getOnEnterWorld())
				quest.notifyEnterWorld(activeChar);
		}
		
		activeChar.sendPacket(new QuestList());
		
		activeChar.updateCLColours();
		
		/* PvP color & title System checks
		   Check if the PvP color & title system is enabled and if so
		   check the character's counters and apply any changes that must be done. =*/
		if (activeChar.getPvpKills() >= (ExternalConfig.PVP_AMOUNT1) && (ExternalConfig.PVP_TITLE_AND_COLOR_SYSTEM_ENABLED))
			activeChar.updatePvPTitleColor(activeChar.getPvpKills());

		if (Config.PLAYER_SPAWN_PROTECTION > 0 && !activeChar.isInsideZone(L2Character.ZONE_PEACE))
			activeChar.setProtection(true);
		
		activeChar.spawnMe(activeChar.getX(), activeChar.getY(), activeChar.getZ());
		
		if (L2Event.connectionLossData.containsKey(activeChar.getName()))
		{
			if (L2Event.active && L2Event.isOnEvent(activeChar))
				L2Event.restoreChar(activeChar);
			else
				L2Event.restoreAndTeleChar(activeChar);
		}
		
		// Wedding Checks
		if (Config.MOD_ALLOW_WEDDING)
		{
			engage(activeChar);
			notifyPartner(activeChar, activeChar.getPartnerId());
		}
		
		if (activeChar.isCursedWeaponEquipped())
		{
			CursedWeaponsManager.getInstance().getCursedWeapon(activeChar.getCursedWeaponEquippedId()).cursedOnLogin();
		}
		
		activeChar.updateEffectIcons();
		
		activeChar.sendPacket(new EtcStatusUpdate(activeChar));
		
		//Expand Skill
		activeChar.sendPacket(new ExStorageMaxCount(activeChar));
		
		activeChar.sendPacket(new FriendList(activeChar));
		
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.FRIEND_S1_HAS_LOGGED_IN);
		sm.addString(activeChar.getName());
		for (int id : activeChar.getFriendList())
		{
			L2Object obj = L2World.getInstance().findObject(id);
			if (obj != null)
				obj.sendPacket(sm);
		}
		sm = null;
		
		sendPacket(SystemMessage.getSystemMessage(SystemMessageId.WELCOME_TO_LINEAGE));
		
		SevenSigns.getInstance().sendCurrentPeriodMsg(activeChar);
		Announcements.getInstance().showAnnouncements(activeChar);
		
		if (showClanNotice)
		{
			NpcHtmlMessage notice = new NpcHtmlMessage(1);
			notice.setFile(activeChar.getHtmlPrefix(), "data/html/clanNotice.htm");
			notice.replace("%clan_name%", activeChar.getClan().getName());
			notice.replace("%notice_text%", activeChar.getClan().getNotice().replaceAll("\r\n", "<br>"));
			notice.disableValidation();
			activeChar.sendPacket(notice);
		}
		else if (Config.SERVER_NEWS)
		{
			String serverNews = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/servnews.htm");
			if (serverNews != null)
				activeChar.sendPacket(new NpcHtmlMessage(1, serverNews));
		}
		
		if (Config.PETITIONING_ALLOWED)
			PetitionManager.getInstance().checkPetitionMessages(activeChar);
		
		if (activeChar.isAlikeDead()) // dead or fake dead
		{
			// no broadcast needed since the player will already spawn dead to others
			activeChar.sendPacket(new Die(activeChar));
		}
		
		if (ExternalConfig.PCB_ENABLE)
			activeChar.showPcBangWindow();
		
		activeChar.onPlayerEnter();
		
		activeChar.sendPacket(new SkillCoolTime(activeChar));
		activeChar.sendPacket(new ExVoteSystemInfo(activeChar));
				
		for (L2ItemInstance i : activeChar.getInventory().getItems())
		{
			if (i.isTimeLimitedItem())
				i.scheduleLifeTimeTask();
			if (i.isShadowItem() && i.isEquipped())
				i.decreaseMana(false);
			
			// Unequip instance items if not in instance
			if (i.isInstanceItem() && i.isEquipped() && !i.checkInstanceForItem(activeChar.getInstanceId()))
				activeChar.getInventory().unEquipItemInSlot(i.getLocationSlot());
		}
		
		for (L2ItemInstance i : activeChar.getWarehouse().getItems())
		{
			if (i.isTimeLimitedItem())
				i.scheduleLifeTimeTask();
		}
		
		if (DimensionalRiftManager.getInstance().checkIfInRiftZone(activeChar.getX(), activeChar.getY(), activeChar.getZ(), false))
			DimensionalRiftManager.getInstance().teleportToWaitingRoom(activeChar);
		
		if (activeChar.getClanJoinExpiryTime() > System.currentTimeMillis())
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBERSHIP_TERMINATED));
		
		// remove combat flag before teleporting
		if (activeChar.getInventory().getItemByItemId(9819) != null)
		{
			Fort fort = FortManager.getInstance().getFort(activeChar);			
			if (fort != null)
				FortSiegeManager.getInstance().dropCombatFlag(activeChar, fort.getFortId());
			else
			{
				int slot = activeChar.getInventory().getSlotFromItem(activeChar.getInventory().getItemByItemId(9819));
				activeChar.getInventory().unEquipItemInBodySlot(slot);
				activeChar.destroyItem("CombatFlag", activeChar.getInventory().getItemByItemId(9819), null, true);
			}
		}
		
		// Attacker or spectator logging in to a siege zone. Actually should be checked for inside castle only?
		if (!activeChar.isGM()
				// inside siege zone
				&& activeChar.isInsideZone(L2Character.ZONE_SIEGE)
				// but non-participant or attacker
				&& (!activeChar.isInSiege() || activeChar.getSiegeState() < 2))
			activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);
		
		if (Config.ALLOW_MAIL)
		{
			if (MailManager.getInstance().hasUnreadPost(activeChar))
				sendPacket(ExNoticePostArrived.valueOf(false));
		}
		
		RegionBBSManager.getInstance().changeCommunityBoard();
		CommunityServerThread.getInstance().sendPacket(new WorldInfo(activeChar, null, WorldInfo.TYPE_UPDATE_PLAYER_STATUS));
		
		TvTEvent.onLogin(activeChar);
		
		if (DM._savePlayers.contains(activeChar.getName()))
			DM.addDisconnectedPlayer(activeChar);
		
		TvTRoundEvent.onLogin(activeChar);
		
		if (Config.WELCOME_MESSAGE_ENABLED)
			activeChar.sendPacket(new ExShowScreenMessage(Config.WELCOME_MESSAGE_TEXT, Config.WELCOME_MESSAGE_TIME));
		
		L2ClassMasterInstance.showQuestionMark(activeChar);
		
		/** Add by CTF */
		if (CTF._savePlayers.contains(activeChar.getName()))
		{
			CTF.addDisconnectedPlayer(activeChar);
		}
		
		int birthday = activeChar.checkBirthDay();
		if (birthday == 0)
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_BIRTHDAY_GIFT_HAS_ARRIVED));
			activeChar.sendPacket(new ExBirthdayPopup());
		}
		else if (birthday != -1)
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S1_DAYS_UNTIL_YOUR_CHARACTERS_BIRTHDAY);
			sm.addString(Integer.toString(birthday));
			activeChar.sendPacket(sm);
			sm = null;
		}
		
		ScriptHandler.getInstance().execute(CallSite.ON_ENTER, activeChar, null);
		
		/** Vitamin Item */
		if (!activeChar.getPremiumItemList().isEmpty())
			activeChar.sendPacket(new ExNotifyPremiumItem());
	}
	
	/**
	* @param activeChar
	*/
	private void engage(L2PcInstance cha)
	{
		int _chaid = cha.getObjectId();
		for (Couple cl : CoupleManager.getInstance().getCouples())
		{
			if (cl.getPlayer1Id() == _chaid || cl.getPlayer2Id() == _chaid)
			{
				if (cl.getMaried())
					cha.setMarried(true);
				
				cha.setCoupleId(cl.getId());
				
				if (cl.getPlayer1Id() == _chaid)
					cha.setPartnerId(cl.getPlayer2Id());
				else
					cha.setPartnerId(cl.getPlayer1Id());
			}
		}
	}
	
	/**
	* @param activeChar partnerid
	*/
	private void notifyPartner(L2PcInstance cha, int partnerId)
	{
		if (cha.getPartnerId() != 0)
		{
			int objId = cha.getPartnerId();
			
			try
			{
				L2PcInstance partner = L2World.getInstance().getPlayer(objId);
				if (partner != null)
					partner.sendMessage("Your Partner has logged in.");
			}
			catch (ClassCastException cce)
			{
				_log.warning("Wedding Error: ID " + objId + " is now owned by a(n) " + L2World.getInstance().findObject(objId).getClass().getSimpleName());
			}
		}
	}
	
	/**
	* @param activeChar
	*/
	private void notifyClanMembers(L2PcInstance activeChar)
	{
		L2Clan clan = activeChar.getClan();
		
		// This null check may not be needed anymore since notifyClanMembers is called from within a null check already. Please remove if we're certain it's ok to do so.
		if (clan != null)
		{
			clan.getClanMember(activeChar.getObjectId()).setPlayerInstance(activeChar);
			SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_LOGGED_IN);
			msg.addString(activeChar.getName());
			clan.broadcastToOtherOnlineMembers(msg, activeChar);
			clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(activeChar), activeChar);
			msg = null;
		}
	}
	
	/**
	* @param activeChar
	*/
	private void notifySponsorOrApprentice(L2PcInstance activeChar)
	{
		if (activeChar.getSponsor() != 0)
		{
			L2PcInstance sponsor = L2World.getInstance().getPlayer(activeChar.getSponsor());			
			if (sponsor != null)
			{
				SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOUR_APPRENTICE_S1_HAS_LOGGED_IN);
				msg.addString(activeChar.getName());
				sponsor.sendPacket(msg);
				msg = null;
			}
		}
		else if (activeChar.getApprentice() != 0)
		{
			L2PcInstance apprentice = L2World.getInstance().getPlayer(activeChar.getApprentice());			
			if (apprentice != null)
			{
				SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SPONSOR_C1_HAS_LOGGED_IN);
				msg.addString(activeChar.getName());
				apprentice.sendPacket(msg);
				msg = null;
			}
		}
	}
	
	/**
	* @param string
	* @return
	* @throws UnsupportedEncodingException
	*/
	private String getText(String string)
	{
		try
		{
			String result = new String(Base64.decode(string), "UTF-8");
			return result;
		}
		catch (UnsupportedEncodingException e)
		{
			return null;
		}
	}
	
	private void loadTutorial(L2PcInstance player)
	{
		QuestState qs = player.getQuestState("255_Tutorial");
		if (qs != null)
			qs.getQuest().notifyEvent("UC", null, player);
	}

	@Override
	public String getType()
	{
		return _C__03_ENTERWORLD;
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}
