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
package handlers;

import handlers.actionhandlers.*;
import handlers.admincommandhandlers.*;
import handlers.aioitemhandler.AIOBufferHandler;
import handlers.aioitemhandler.AIOChatHandler;
import handlers.aioitemhandler.AIOSchemeHandler;
import handlers.aioitemhandler.AIOServiceHandler;
import handlers.aioitemhandler.AIOShopHandler;
import handlers.aioitemhandler.AIOTeleportHandler;
import handlers.aioitemhandler.AIOTopList;
import handlers.aioitemhandler.AIOWarehouseHandler;
import handlers.bypasshandlers.*;
import handlers.chathandlers.*;
import handlers.irchandlers.*;
import handlers.itemhandlers.*;
import handlers.scripthandlers.*;
import handlers.skillhandlers.*;
import handlers.usercommandhandlers.*;
import handlers.voicedcommandhandlers.*;

import java.util.logging.Logger;

import l2.universe.Config;
import l2.universe.ExternalConfig;
import l2.universe.gameserver.handler.AIOItemHandler;
import l2.universe.gameserver.handler.ActionHandler;
import l2.universe.gameserver.handler.AdminCommandHandler;
import l2.universe.gameserver.handler.BypassHandler;
import l2.universe.gameserver.handler.ChatHandler;
import l2.universe.gameserver.handler.IrcCommandHandler;
import l2.universe.gameserver.handler.ItemHandler;
import l2.universe.gameserver.handler.ScriptHandler;
import l2.universe.gameserver.handler.SkillHandler;
import l2.universe.gameserver.handler.UserCommandHandler;
import l2.universe.gameserver.handler.VoicedCommandHandler;

/**
 *
 * @author  nBd
 */
public class MasterHandler
{
	private static Logger _log = Logger.getLogger(MasterHandler.class.getName());
	
	private static void loadActionHandlers()
	{
		ActionHandler.getInstance().registerActionHandler(new L2ArtefactInstanceAction());
		ActionHandler.getInstance().registerActionHandler(new L2DecoyAction());
		ActionHandler.getInstance().registerActionHandler(new L2DoorInstanceAction());
		ActionHandler.getInstance().registerActionHandler(new L2ItemInstanceAction());
		ActionHandler.getInstance().registerActionHandler(new L2NpcAction());
		ActionHandler.getInstance().registerActionHandler(new L2PcInstanceAction());
		ActionHandler.getInstance().registerActionHandler(new L2PetInstanceAction());
		ActionHandler.getInstance().registerActionHandler(new L2StaticObjectInstanceAction());
		ActionHandler.getInstance().registerActionHandler(new L2SummonAction());
		ActionHandler.getInstance().registerActionHandler(new L2TrapAction());
		_log.config("Loaded " + ActionHandler.getInstance().size() + " ActionHandlers");
	}
	
	private static void loadActionShiftHandlers()
	{
		ActionHandler.getInstance().registerActionShiftHandler(new L2DoorInstanceActionShift());
		ActionHandler.getInstance().registerActionShiftHandler(new L2ItemInstanceActionShift());
		ActionHandler.getInstance().registerActionShiftHandler(new L2NpcActionShift());
		ActionHandler.getInstance().registerActionShiftHandler(new L2PcInstanceActionShift());
		ActionHandler.getInstance().registerActionShiftHandler(new L2StaticObjectInstanceActionShift());
		ActionHandler.getInstance().registerActionShiftHandler(new L2SummonActionShift());
		_log.config("Loaded " + ActionHandler.getInstance().sizeShift() + " ActionShiftHandlers");
	}
	
	private static void loadAdminHandlers()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminAdmin());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminAnnouncements());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminBan());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminBBS());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminBuffs());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminCache());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminCamera());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminChangeAccessLevel());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminChangeAccessLevel());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminCHSiege());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminCheckBot());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminCreateItem());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminCursedWeapons());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminDebug());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminDelete());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminCTFEngine()); // Add by CTF Event
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminDisconnect());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminDoorControl());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminEditChar());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminEditNpc());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminEffects());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminElement());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminEnchant());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminEventEngine());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminEvents());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminExpSp());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminFence());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminFightCalculator());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminFortSiege());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminGeodata());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminGeoEditor());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminGm());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminGraciaSeeds());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminGmChat());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminHeal());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminHellbound());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminHelpPage());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminInstance());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminInstanceZone());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminInvul());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminKick());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminKill());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminKrateisCube());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminLevel());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminLogin());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminMammon());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminManor());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminMenu());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminMessages());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminMobGroup());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminMonsterRace());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminMonsterRush());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminMovieMaker());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminMultiBoxProtection());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminNpcChat());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminOlympiad());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminPathNode());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminPetition());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminPForge());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminPledge());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminPolymorph());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminPremium());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminQuest());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminRepairChar());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminRes());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminRide());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminShop());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminShowQuests());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminShutdown());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminSiege());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminSkill());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminSpawn());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminSummon());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminTarget());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminTeleport());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminTerritoryWar());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminTownWar());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminTest());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminTvTEvent());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminTvTRoundEvent());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminUnblockIp());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminVitality());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminVitaminItem());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminZone());
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminDMEngine());
		_log.config("Loaded " + AdminCommandHandler.getInstance().size() + " AdminCommandHandlers");
	}
	
	private static void loadAIOItemHandlers()
	{
		AIOItemHandler.getInstance().registerAIOItemHandler(new AIOChatHandler());
		if(ExternalConfig.AIOITEM_ENABLESHOP)
			AIOItemHandler.getInstance().registerAIOItemHandler(new AIOShopHandler());
		if(ExternalConfig.AIOITEM_ENABLEGK)
			AIOItemHandler.getInstance().registerAIOItemHandler(new AIOTeleportHandler());
		if(ExternalConfig.AIOITEM_ENABLEWH)
			AIOItemHandler.getInstance().registerAIOItemHandler(new AIOWarehouseHandler());
		if(ExternalConfig.AIOITEM_ENABLEBUFF)
			AIOItemHandler.getInstance().registerAIOItemHandler(new AIOBufferHandler());
		if(ExternalConfig.AIOITEM_ENABLESERVICES)
			AIOItemHandler.getInstance().registerAIOItemHandler(new AIOServiceHandler());
		if(ExternalConfig.AIOITEM_ENABLESCHEMEBUFF)
			AIOItemHandler.getInstance().registerAIOItemHandler(new AIOSchemeHandler());
		if(ExternalConfig.AIOITEM_ENABLETOPLIST)
			AIOItemHandler.getInstance().registerAIOItemHandler(new AIOTopList());
		_log.config("Loaded " + AIOItemHandler.getInstance().size() +" AIOItem bypass handlers");
	}

	private static void loadBypassHandlers()
	{
		BypassHandler.getInstance().registerBypassHandler(new Augment());
		BypassHandler.getInstance().registerBypassHandler(new BloodAlliance());
		BypassHandler.getInstance().registerBypassHandler(new Buy());
		BypassHandler.getInstance().registerBypassHandler(new BuyShadowItem());
		BypassHandler.getInstance().registerBypassHandler(new ChatLink());
		BypassHandler.getInstance().registerBypassHandler(new ClanWarehouse());
		BypassHandler.getInstance().registerBypassHandler(new CPRecovery());
		BypassHandler.getInstance().registerBypassHandler(new CustomLinks());
		BypassHandler.getInstance().registerBypassHandler(new DrawHenna());
		BypassHandler.getInstance().registerBypassHandler(new Festival());
		BypassHandler.getInstance().registerBypassHandler(new FishSkillList());
		BypassHandler.getInstance().registerBypassHandler(new FortSiege());
		BypassHandler.getInstance().registerBypassHandler(new ItemAuctionLink());
		BypassHandler.getInstance().registerBypassHandler(new Link());
		BypassHandler.getInstance().registerBypassHandler(new Loto());
		BypassHandler.getInstance().registerBypassHandler(new ManorManager());
		BypassHandler.getInstance().registerBypassHandler(new Multisell());
		BypassHandler.getInstance().registerBypassHandler(new Observation());
		BypassHandler.getInstance().registerBypassHandler(new OlympiadObservation());
		BypassHandler.getInstance().registerBypassHandler(new OlympiadManagerLink());
		BypassHandler.getInstance().registerBypassHandler(new QuestLink());
		BypassHandler.getInstance().registerBypassHandler(new PA());
		BypassHandler.getInstance().registerBypassHandler(new PlayerHelp());
		BypassHandler.getInstance().registerBypassHandler(new PrivateWarehouse());
		BypassHandler.getInstance().registerBypassHandler(new QuestList());
		BypassHandler.getInstance().registerBypassHandler(new ReceivePremium());
		BypassHandler.getInstance().registerBypassHandler(new ReleaseAttribute());
		BypassHandler.getInstance().registerBypassHandler(new RemoveDeathPenalty());
		BypassHandler.getInstance().registerBypassHandler(new RemoveHennaList());
		BypassHandler.getInstance().registerBypassHandler(new RentPet());
		BypassHandler.getInstance().registerBypassHandler(new RideWyvern());
		BypassHandler.getInstance().registerBypassHandler(new Rift());
		BypassHandler.getInstance().registerBypassHandler(new SkillList());
		BypassHandler.getInstance().registerBypassHandler(new SupportBlessing());
		BypassHandler.getInstance().registerBypassHandler(new SupportMagic());
		BypassHandler.getInstance().registerBypassHandler(new TerritoryStatus());
		BypassHandler.getInstance().registerBypassHandler(new TerritoryWar());
		BypassHandler.getInstance().registerBypassHandler(new Transform());
		BypassHandler.getInstance().registerBypassHandler(new Wear());
		_log.config("Loaded " + BypassHandler.getInstance().size() + " BypassHandlers");
	}
	
	private static void loadChatHandlers()
	{
		ChatHandler.getInstance().registerChatHandler(new ChatAll());
		ChatHandler.getInstance().registerChatHandler(new ChatAlliance());
		ChatHandler.getInstance().registerChatHandler(new ChatBattlefield());
		ChatHandler.getInstance().registerChatHandler(new ChatClan());
		ChatHandler.getInstance().registerChatHandler(new ChatHeroVoice());
		ChatHandler.getInstance().registerChatHandler(new ChatParty());
		ChatHandler.getInstance().registerChatHandler(new ChatPartyMatchRoom());
		ChatHandler.getInstance().registerChatHandler(new ChatPartyRoomAll());
		ChatHandler.getInstance().registerChatHandler(new ChatPartyRoomCommander());
		ChatHandler.getInstance().registerChatHandler(new ChatPetition());
		ChatHandler.getInstance().registerChatHandler(new ChatShout());
		ChatHandler.getInstance().registerChatHandler(new ChatTell());
		ChatHandler.getInstance().registerChatHandler(new ChatTrade());
		_log.config("Loaded " + ChatHandler.getInstance().size() + " ChatHandlers");
	}
	
	private static void loadItemHandlers()
	{
		ItemHandler.getInstance().registerItemHandler(new ScrollOfResurrection());
		ItemHandler.getInstance().registerItemHandler(new SoulShots());
		ItemHandler.getInstance().registerItemHandler(new SpiritShot());
		ItemHandler.getInstance().registerItemHandler(new BlessedSpiritShot());
		ItemHandler.getInstance().registerItemHandler(new BeastSoulShot());
		ItemHandler.getInstance().registerItemHandler(new BeastSpiritShot());
		ItemHandler.getInstance().registerItemHandler(new PaganKeys());
		ItemHandler.getInstance().registerItemHandler(new Maps());
		ItemHandler.getInstance().registerItemHandler(new NicknameColor());
		ItemHandler.getInstance().registerItemHandler(new Recipes());
		ItemHandler.getInstance().registerItemHandler(new RollingDice());
		ItemHandler.getInstance().registerItemHandler(new EnchantAttribute());
		ItemHandler.getInstance().registerItemHandler(new EnchantScrolls());
		ItemHandler.getInstance().registerItemHandler(new ExtractableItems());
		ItemHandler.getInstance().registerItemHandler(new Book());
		ItemHandler.getInstance().registerItemHandler(new SevenSignsRecord());
		ItemHandler.getInstance().registerItemHandler(new ItemSkills());
		ItemHandler.getInstance().registerItemHandler(new ItemSkillsTemplate());
		ItemHandler.getInstance().registerItemHandler(new Keys());
		ItemHandler.getInstance().registerItemHandler(new Seed());
		ItemHandler.getInstance().registerItemHandler(new Harvester());
		ItemHandler.getInstance().registerItemHandler(new MercTicket());
		ItemHandler.getInstance().registerItemHandler(new FishShots());
		ItemHandler.getInstance().registerItemHandler(new PetFood());
		ItemHandler.getInstance().registerItemHandler(new SpecialXMas());
		ItemHandler.getInstance().registerItemHandler(new SummonItems());
		ItemHandler.getInstance().registerItemHandler(new BeastSpice());
		ItemHandler.getInstance().registerItemHandler(new TeleportBookmark());
		ItemHandler.getInstance().registerItemHandler(new Elixir());
		ItemHandler.getInstance().registerItemHandler(new Disguise());
		ItemHandler.getInstance().registerItemHandler(new ManaPotion());
		ItemHandler.getInstance().registerItemHandler(new EnergyStarStone());
		//ItemHandler.getInstance().registerItemHandler(new QuestItems()); -- what does this?
		if(ExternalConfig.AIOITEM_ENABLEME)
			ItemHandler.getInstance().registerItemHandler(new AIOItem());

		_log.config("Loaded " + ItemHandler.getInstance().size() + " ItemHandlers");
	}
	
	private static void loadSkillHandlers()
	{
		SkillHandler.getInstance().registerSkillHandler(new AgathionCollection());
		SkillHandler.getInstance().registerSkillHandler(new Blow());
		SkillHandler.getInstance().registerSkillHandler(new Pdam());
		SkillHandler.getInstance().registerSkillHandler(new Mdam());
		SkillHandler.getInstance().registerSkillHandler(new CpDam());
		SkillHandler.getInstance().registerSkillHandler(new CpDamPercent());
		SkillHandler.getInstance().registerSkillHandler(new Manadam());
		SkillHandler.getInstance().registerSkillHandler(new Heal());
		SkillHandler.getInstance().registerSkillHandler(new HealPercent());
		SkillHandler.getInstance().registerSkillHandler(new CombatPointHeal());
		SkillHandler.getInstance().registerSkillHandler(new ManaHeal());
		SkillHandler.getInstance().registerSkillHandler(new BalanceLife());
		SkillHandler.getInstance().registerSkillHandler(new Charge());
		SkillHandler.getInstance().registerSkillHandler(new Continuous());
		SkillHandler.getInstance().registerSkillHandler(new Detection());
		SkillHandler.getInstance().registerSkillHandler(new Resurrect());
		SkillHandler.getInstance().registerSkillHandler(new ShiftTarget());
		SkillHandler.getInstance().registerSkillHandler(new Spoil());
		SkillHandler.getInstance().registerSkillHandler(new Sweep());
		SkillHandler.getInstance().registerSkillHandler(new StrSiegeAssault());
		SkillHandler.getInstance().registerSkillHandler(new SummonFriend());
		SkillHandler.getInstance().registerSkillHandler(new Disablers());
		SkillHandler.getInstance().registerSkillHandler(new Cancel());
		SkillHandler.getInstance().registerSkillHandler(new StealBuffs());
		SkillHandler.getInstance().registerSkillHandler(new BallistaBomb());
		SkillHandler.getInstance().registerSkillHandler(new TakeCastle());
		SkillHandler.getInstance().registerSkillHandler(new TakeFort());
		SkillHandler.getInstance().registerSkillHandler(new Unlock());
		SkillHandler.getInstance().registerSkillHandler(new Craft());
		SkillHandler.getInstance().registerSkillHandler(new Fishing());
		SkillHandler.getInstance().registerSkillHandler(new FishingSkill());
		if (ExternalConfig.HELLBOUND_STATUS)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new Hellbound());		
		SkillHandler.getInstance().registerSkillHandler(new BeastSkills());
		SkillHandler.getInstance().registerSkillHandler(new DeluxeKey());
		SkillHandler.getInstance().registerSkillHandler(new Sow());
		SkillHandler.getInstance().registerSkillHandler(new Soul());
		SkillHandler.getInstance().registerSkillHandler(new Harvest());
		SkillHandler.getInstance().registerSkillHandler(new GetPlayer());
		SkillHandler.getInstance().registerSkillHandler(new TransformDispel());
		SkillHandler.getInstance().registerSkillHandler(new Trap());
		SkillHandler.getInstance().registerSkillHandler(new GiveSp());
		SkillHandler.getInstance().registerSkillHandler(new GiveReco());
		SkillHandler.getInstance().registerSkillHandler(new GiveVitality());
		SkillHandler.getInstance().registerSkillHandler(new InstantJump());
		SkillHandler.getInstance().registerSkillHandler(new Dummy());
		SkillHandler.getInstance().registerSkillHandler(new Extractable());
		SkillHandler.getInstance().registerSkillHandler(new RefuelAirShip());
		SkillHandler.getInstance().registerSkillHandler(new NornilsPower());
		_log.config("Loaded " + SkillHandler.getInstance().size() + " SkillHandlers");
	}
	
	private static void loadUserHandlers()
	{
		UserCommandHandler.getInstance().registerUserCommandHandler(new ClanPenalty());
		UserCommandHandler.getInstance().registerUserCommandHandler(new ClanWarsList());
		UserCommandHandler.getInstance().registerUserCommandHandler(new DisMount());
		UserCommandHandler.getInstance().registerUserCommandHandler(new Escape());
		UserCommandHandler.getInstance().registerUserCommandHandler(new InstanceZone());
		UserCommandHandler.getInstance().registerUserCommandHandler(new Loc());
		UserCommandHandler.getInstance().registerUserCommandHandler(new Mount());
		UserCommandHandler.getInstance().registerUserCommandHandler(new PartyInfo());
		UserCommandHandler.getInstance().registerUserCommandHandler(new Time());
		UserCommandHandler.getInstance().registerUserCommandHandler(new OlympiadStat());
		UserCommandHandler.getInstance().registerUserCommandHandler(new ChannelLeave());
		UserCommandHandler.getInstance().registerUserCommandHandler(new ChannelDelete());
		UserCommandHandler.getInstance().registerUserCommandHandler(new ChannelListUpdate());
		UserCommandHandler.getInstance().registerUserCommandHandler(new Birthday());
		_log.config("Loaded " + UserCommandHandler.getInstance().size() + " UserHandlers");
	}
	
	private static void loadVoicedHandlers()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new VoiceInfo());
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new stats());
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new Repair());
		if (ExternalConfig.SHOW_GB_STATUS)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new RaidBoosStatus());
		if (Config.MOD_ALLOW_WEDDING)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new Wedding());
		if (Config.BANKING_SYSTEM_ENABLED)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new Banking());
		if (ExternalConfig.TVT_ALLOW_VOICED_COMMAND)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new TvTVoicedInfo());
		if (ExternalConfig.TVT_ROUND_ALLOW_VOICED_COMMAND)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new TvTRoundVoicedInfo());
		if (Config.MOD_CHAT_ADMIN)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new ChatAdmin());
		if (Config.MOD_MULTILANG_ENABLE && Config.MOD_MULTILANG_VOICED_ALLOW)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new Lang());
		if (Config.MOD_DEBUG_VOICE_COMMAND)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new Debug());
		if (ExternalConfig.ALLOW_CLAN_LIDER_TELEPORT)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new cl());
		// Add by CTF Event
		if (ExternalConfig.CTF_ALLOW_VOICE_COMMAND)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new CTFCmd());
		if (ExternalConfig.ALLOW_VOICE_BUFF)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new VoiceBuff());
		if (ExternalConfig.MR_ENABLED)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new MonsterAss());
		if (ExternalConfig.AUTO_LOOT_INDIVIDUAL)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new VoiceAutoloot());
		if (ExternalConfig.ALLOW_CHANGE_PASSWORD)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new ChangePassword());
		if (ExternalConfig.ALLOW_EXP_GAIN_COMMAND)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new ExpGain());
		if (ExternalConfig.TVT_ALLOW_REGISTER_VOICED_COMMAND)
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new TvTVoiced());
		_log.config("Loaded " + VoicedCommandHandler.getInstance().size() + " VoicedHandlers");
	}
	
	private static void loadScriptHandlers()
	{
		ScriptHandler.getInstance().registerHandler(new EnterWorld());
		ScriptHandler.getInstance().registerHandler(new OnDeath());
		ScriptHandler.getInstance().registerHandler(new OnExit());
		ScriptHandler.getInstance().registerHandler(new OnKill());
		ScriptHandler.getInstance().registerHandler(new OnUseItem());
		ScriptHandler.getInstance().registerHandler(new OnRess());
		_log.config("Loaded " + ScriptHandler.getInstance().size() + " ScriptHandlers");
	}
		
	
	private static void loadIrcHandlers()
	{
		if (ExternalConfig.IRC_ENABLED)
		{
			IrcCommandHandler.getInstance().registerIrcCommandHandler(new IrcOnline());
			IrcCommandHandler.getInstance().registerIrcCommandHandler(new IrcGmList());
			_log.config("Loaded " + IrcCommandHandler.getInstance().size() + " IRCHandlers");
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		_log.config("Loading Handlers...");
		loadActionHandlers();
		loadActionShiftHandlers();
		loadAdminHandlers();
		loadBypassHandlers();
		loadChatHandlers();
		loadItemHandlers();
		loadIrcHandlers();
		loadSkillHandlers();
		loadUserHandlers();
		loadVoicedHandlers();
		loadScriptHandlers();
		if(ExternalConfig.AIOITEM_ENABLEME)
			loadAIOItemHandlers();

		_log.config("Handlers Loaded...");
	}
}