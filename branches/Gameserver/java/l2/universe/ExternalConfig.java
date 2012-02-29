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
package l2.universe;

import gnu.trove.TIntIntHashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javolution.text.TypeFormat;
import l2.universe.util.L2Properties;
import l2.universe.util.StringUtil;

public final class ExternalConfig
{
	protected static final Logger _log = Logger.getLogger(ExternalConfig.class.getName());
	
	//--------------------------------------------------\\
	//					Custom							\\
	//--------------------------------------------------\\
	public static final String CUSTOM_FILE = "./config/Custom.properties";
	public static final String EVENTS_SETTINGS = "./config/event/EventsSettings.properties";
	public static final String DM_FILES = "./config/event/DM.properties";
	public static final String PK_PROTECTION = "./config/LowLevelPkProtection.properties";
	public static final String CTF_CONFIG_FILE = "./config/event/CTF.properties";
	public static final String TVT_CONFIG_FILE = "./config/event/TVT.properties";
	public static final String EVENT_FILES = "./config/event/HideAndSeek.properties";
	public static final String MR_FILES = "./config/event/MonsterRush.properties";
	public static final String PC_BANG_POINT_FILE = "./config/event/pcBang.properties";
	public static final String PREMIUM_FILES = "./config/event/PremiumAccount.properties";
	public static final String TOWNWAR_FILES = "./config/event/TownWar.properties";
	public static final String BLOCK_CHECKER_EVENT = "./config/event/HandyBlockChecker.properties";
	public static final String IRC_CONFIG_FILE = "./config/settings/irc.properties";
	public static final String QUEST_RATE_FILE = "./config/scripts/QuestRate.properties";
	public static final String DATABASE_SETTINGS = "./config/settings/DatabaseSettings.properties";
	public static final String CHAR_COMMAND = "./config/CharacterVoiceCommand.properties";
	private static final String AIO_CONFIG_FILE = "./config/AioConfig.properties";

	public static final int MRUSH_REWARD_ID = 0;

	public static boolean CUSTOM_SCRIPTS;
	public static boolean ALL_SUBCLASS_AVAILABLE;
	public static int DROP_PROTECTED_TIME;
	public static boolean HALLOFSUFFERING_REMOVEBUFF;
	public static int REQUIREDVOTES;
	public static String WEBSITE_SERVER_LINK;
	public static int ITEM_ID;
	public static int ITEM_COUNT;
	public static int RENAME_NPC_ID;
	public static int RENAME_NPC_MIN_LEVEL;
	public static String RENAME_NPC_FEE;
	
	public static int DELEVEL_NPC_ID;
	public static boolean DELEVEL_NPC_ENABLE;
	
	public static boolean VOTESYSTEMENABLE;
	public static boolean REMOVE_DANCES_ON_RESTART;
	public static boolean HELLBOUND_STATUS;

	/** ****************************************************/
	/** AIO Item configuration parameters				  **/
	/** ****************************************************/
	public static boolean AIOITEM_ENABLEME;
	public static boolean AIOITEM_ONLY_FOR_PREMIUM;
	public static boolean AIOITEM_ONLY_IN_TOWN;
	public static boolean AIOITEM_ENABLESHOP;
	public static boolean AIOITEM_ENABLEGK;
	public static boolean AIOITEM_ENABLEWH;
	public static boolean AIOITEM_ENABLEBUFF;
	public static boolean AIOITEM_ENABLESCHEMEBUFF;
	public static boolean AIOITEM_ENABLESERVICES;
	public static boolean AIOITEM_ENABLESUBCLASS;
	public static boolean AIOITEM_ENABLETOPLIST;
	public static int AIOITEM_GK_COIN;
	public static int AIOITEM_GK_PRICE;
	public static int AIOITEM_BUFF_COIN;
	public static int AIOITEM_BUFF_PRICE;
	public static int AIOITEM_SCHEME_COIN;
	public static int AIOITEM_SCHEME_PRICE;
	public static int AIOITEM_SCHEME_PROFILE_PRICE;
	public static int AIOITEM_SCHEME_MAX_PROFILES;
	public static int AIOITEM_SCHEME_MAX_PROFILE_BUFFS;

	/** ************************************************** **/
	/** Events Settings									   **/
	/** ************************************************** **/
	public static int PARTY_MEMBER_COUNT;
	
	/** ************************************************** **/
	/** Bot detection system							   **/
	/** ************************************************** **/
	public static boolean ENABLE_BOTREPORT;
	
	/** ************************************************** **/
	/** IRC Settings -Begin                          	   **/
	/** ************************************************** **/
	public static boolean IRC_ENABLED;
	public static String IRC_SERVER;
	public static int IRC_PORT;
	public static String IRC_PASS;
	public static String IRC_NICK;
	public static String IRC_USER;
	public static String IRC_NAME;
	public static boolean IRC_NICKSERV;
	public static String IRC_NICKSERV_NAME;
	public static String IRC_NICKSERV_PASS;
	public static String IRC_LOGIN_COMMAND;
	public static String IRC_CHANNEL;
	public static boolean IRC_DEBUG;
	
	public static boolean AUTO_LOOT_INDIVIDUAL;
	
	public static int[] BBS_ALLOWED_MULTISELLS = new int[2];
	public static boolean ENABLE_BLOCK_CHECKER_EVENT;
	public static int MIN_BLOCK_CHECKER_TEAM_MEMBERS;
	public static boolean HBCE_FAIR_PLAY;
	
	public static boolean DEVASTATED_CASTLE_ENABLED;
	public static boolean FORTRESS_OF_THE_DEAD_ENABLED;
	public static boolean USE_CUSTOM_CLANHALLS;
	public static boolean ALLOW_EXP_GAIN_COMMAND;
	
	public static String SERVER_NAME;
	public static int SERVERINFO_NPC_ID;
	public static String[] SERVERINFO_NPC_ADM;
	public static String[] SERVERINFO_NPC_GM;
	public static String SERVERINFO_NPC_DESCRIPTION;
	public static String SERVERINFO_NPC_EMAIL;
	public static String SERVERINFO_NPC_PHONE;
	public static String[] SERVERINFO_NPC_CUSTOM;
	public static String[] SERVERINFO_NPC_DISABLE_PAGE;
	
	/** ************************************************** **/
	/** Custom Start Location                          	   **/
	/** ************************************************** **/
	public static boolean SPAWN_CHAR;
	public static int SPAWN_X;
	public static int SPAWN_Y;
	public static int SPAWN_Z;
	
	public static boolean CONSUME_SPIRIT_SOUL_SHOTS;
	public static boolean ALT_QUEST_RECIPE_REWARD;
	
	/** ************************************************** **/
	/**  Hide and seek event settings                  	   **/
	/** ************************************************** **/
	public static boolean HAS_ENABLED;
	public static String[] HAS_EVENT_INTERVAL;
	public static int HAS_REG_MINS_DURATION;
	public static int HAS_EVENT_MINS_DURATION;
	public static boolean HAS_PK_PLAYER_CAN_JOIN;
	public static boolean HAS_SEQUENCE_NPC;
	
	/** ************************************************** **/
	/**  Monster Rush event settings                  	   **/
	/** ************************************************** **/
	public static boolean MR_ENABLED;
	public static String[] MR_EVENT_INTERVAL;
	public static int MR_PARTICIPATION_TIME;
	public static int MR_RUNNING_TIME;
	public static int MRUSH_REWARD_AMOUNT;
	public static int MRUSH_REWARD_ITEM;
	
	/** ************************************************** **/
	/**  PC Bang Point event settings                  	   **/
	/** ************************************************** **/
	public static boolean PCB_ENABLE;
	public static int PCB_MIN_LEVEL;
	public static int PCB_POINT_MIN;
	public static int PCB_POINT_MAX;
	public static int PCB_CHANCE_DUAL_POINT;
	public static int PCB_INTERVAL;
	
	/** ************************************************** **/
	/**  Custom Title Settings		                  	   **/
	/** ************************************************** **/
	public static boolean CHAR_TITLE;
	// This is the new players title.
	public static String ADD_CHAR_TITLE;
	
	/** ************************************************** **/
	/**  Premium Service			                  	   **/
	/** ************************************************** **/
	public static boolean USE_PREMIUMSERVICE;
	public static float PREMIUM_RATE_XP;
	public static float PREMIUM_RATE_SP;
	public static float PREMIUM_RATE_DROP_ADENA;
	public static float PREMIUM_DROP_ADENA_MULTIPLIER;
	public static float PREMIUM_RATE_DROP_SPOIL;
	public static float PREMIUM_RATE_DROP_ITEMS;
	public static boolean PREMIUM_ENCH_BONUS;
	public static int PREMIUM_ENCH_CHANCE_BONUS;
	public static boolean VOICED_BUFF_ONLY_PREMIUM;
	public static String VOICED_BUFF_NOTPREMIUM_MESSAGE;
	
	/** ************************************************** **/
	/**  Clan Leader Color			                  	   **/
	/** ************************************************** **/
	public static boolean CLAN_LEADER_COLOR_ENABLED;
	public static int CLAN_LEADER_COLOR;
	public static int CLAN_LEADER_COLOR_CLAN_LEVEL;
	
	/** ************************************************** **/
	/**  Config options allowing the PvP name color Engine **/
	/** ************************************************** **/
	public static boolean PVP_TITLE_AND_COLOR_SYSTEM_ENABLED;
	public static int PVP_AMOUNT1;
	public static int TITLE_COLOR_FOR_PVP_AMOUNT1;
	public static String PVP1_TITLE;
	public static int PVP_AMOUNT2;
	public static int TITLE_COLOR_FOR_PVP_AMOUNT2;
	public static String PVP2_TITLE;
	public static int PVP_AMOUNT3;
	public static int TITLE_COLOR_FOR_PVP_AMOUNT3;
	public static String PVP3_TITLE;
	public static int PVP_AMOUNT4;
	public static int TITLE_COLOR_FOR_PVP_AMOUNT4;
	public static String PVP4_TITLE;
	public static int PVP_AMOUNT5;
	public static int TITLE_COLOR_FOR_PVP_AMOUNT5;
	public static String PVP5_TITLE;
	public static int PVP_AMOUNT6;
	public static int TITLE_COLOR_FOR_PVP_AMOUNT6;
	public static String PVP6_TITLE;
	public static int PVP_AMOUNT7;
	public static int TITLE_COLOR_FOR_PVP_AMOUNT7;
	public static String PVP7_TITLE;
	public static int PVP_AMOUNT8;
	public static int TITLE_COLOR_FOR_PVP_AMOUNT8;
	public static String PVP8_TITLE;
	public static int PVP_AMOUNT9;
	public static int TITLE_COLOR_FOR_PVP_AMOUNT9;
	public static String PVP9_TITLE;
	public static int PVP_AMOUNT10;
	public static int TITLE_COLOR_FOR_PVP_AMOUNT10;
	public static String PVP10_TITLE;
	public static int PVP_AMOUNT11;
	
	/** ************************************************** **/
	/**  Custom enchant value 							   **/
	/** ************************************************** **/
	public static boolean ALLOW_CUSTOM_ENCHANT_VALUE;
	public static int CUSTOM_ENCHANT_VALUE;
	
	/** ************************************************** **/
	/**  Town war 										   **/
	/** ************************************************** **/
	public static int TW_TOWN_ID;
	public static String TW_TOWN_NAME;
	public static boolean TW_ALL_TOWNS;
	public static boolean TW_AUTO_EVENT;
	public static String[] TW_INTERVAL;
	public static int TW_TIME_BEFORE_START;
	public static int TW_RUNNING_TIME;
	public static int TW_ITEM_ID;
	public static int TW_ITEM_AMOUNT;
	public static boolean TW_GIVE_PVP_AND_PK_POINTS;
	public static boolean TW_ALLOW_KARMA;
	public static boolean TW_DISABLE_GK;
	public static boolean TW_RESS_ON_DEATH;
	public static boolean TW_LOSE_BUFFS_ON_DEATH;
	public static int PING_INTERVAL;
	public static boolean PING_ENABLED;
	public static int PING_IGNORED_REQEST_LIMIT;
	
	/** ************************************************** **/
	/**  Multi-Language Selection						   **/
	/** ************************************************** **/
	public static String LANGUAGE;
	
	/** ************************************************** **/
	/**  Allow Console to show player chat				   **/
	/** ************************************************** **/
	public static boolean ALT_SHOW_CHAT;
	
	/** ************************************************** **/
	/**  Allow keyboard movement  						   **/
	/** ************************************************** **/
	public static boolean ALLOW_KEYBOARD_MOVEMENT;
	
	/** ************************************************** **/
	/**  Allow Mail Settings    						   **/
	/** ************************************************** **/
	public static boolean ENABLE_MAIL;
	
	/** ************************************************** **/
	/**  Custom Settings	    						   **/
	/** ************************************************** **/
	public static int STORE_TITLE_SIZE;
	public static int JUMP_OUT_GRAND_BOSS_ZONE_TIME;
	public static int ITEM_ID_CLAN_LIDER_TELEPORT_TO;
	public static int COUNT_ITEM_CLAN_LIDER_TELEPORT_TO;
	public static boolean ALLOW_CLAN_LIDER_TELEPORT;
	public static int ITEM_ID_CLAN_LIDER_TELEPORT;
	public static int COUNT_ITEM_CLAN_LIDER_TELEPORT;
	public static int MAX_PLAYERS_FROM_ONE_PC;
	public static int MAX_PLAYERS_FROM_ONE_PC_VIP;
	public static int MAX_OFFLINE_STORES;
	public static int MAX_OFFLINE_STORES_VIP;
	public static boolean ALLOW_REPAIR_CHAR;
	public static boolean ALLOW_CHANGE_PASSWORD;
	public static boolean ALLOW_VOICE_BUFF;
	public static boolean SHOW_GB_STATUS;
	public static boolean OFFLINE_COLOR_IN_CB;
	public static boolean LIMIT_SUMMONS_PAILAKA;
	public static int SHOP_MIN_RANGE_FROM_NPC;
	public static int SHOP_MIN_RANGE_FROM_PLAYER;
	public static int SPECIAL_GK_NPC_ID;
	public static boolean UNIVERSE_ADVNPC;

	/** ************************************************** **/
	/**  TVT Settings		    						   **/
	/** ************************************************** **/
	public static boolean TVT_EVENT_ENABLED;
	public static boolean TVT_EVENT_IN_INSTANCE;
	public static String TVT_EVENT_INSTANCE_FILE;
	public static String[] TVT_EVENT_INTERVAL;
	public static int TVT_EVENT_PARTICIPATION_TIME;
	public static int TVT_EVENT_RUNNING_TIME;
	public static int TVT_EVENT_PARTICIPATION_NPC_ID;
	public static int[] TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
	public static int[] TVT_EVENT_PARTICIPATION_FEE = new int[2];
	public static int TVT_EVENT_MIN_PLAYERS_IN_TEAMS;
	public static int TVT_EVENT_MAX_PLAYERS_IN_TEAMS;
	public static int TVT_EVENT_RESPAWN_TELEPORT_DELAY;
	public static int TVT_EVENT_START_LEAVE_TELEPORT_DELAY;
	public static String TVT_EVENT_TEAM_1_NAME;
	public static int[] TVT_EVENT_TEAM_1_COORDINATES = new int[3];
	public static String TVT_EVENT_TEAM_2_NAME;
	public static int[] TVT_EVENT_TEAM_2_COORDINATES = new int[3];
	public static List<int[]> TVT_EVENT_REWARDS;
	public static boolean TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED;
	public static boolean TVT_EVENT_SCROLL_ALLOWED;
	public static boolean TVT_EVENT_POTIONS_ALLOWED;
	public static boolean TVT_EVENT_SUMMON_BY_ITEM_ALLOWED;
	public static List<Integer> TVT_DOORS_IDS_TO_OPEN;
	public static List<Integer> TVT_DOORS_IDS_TO_CLOSE;
	public static boolean TVT_REWARD_TEAM_TIE;
	public static byte TVT_EVENT_MIN_LVL;
	public static byte TVT_EVENT_MAX_LVL;
	public static int TVT_EVENT_EFFECTS_REMOVAL;
	public static TIntIntHashMap TVT_EVENT_FIGHTER_BUFFS;
	public static TIntIntHashMap TVT_EVENT_MAGE_BUFFS;
	public static boolean TVT_ALLOW_VOICED_COMMAND;
	public static boolean TVT_ALLOW_REGISTER_VOICED_COMMAND;

	/**
	 * Tvt Round
	 * **/
	public static boolean TVT_ROUND_EVENT_ENABLED;
	public static boolean TVT_ROUND_EVENT_IN_INSTANCE;
	public static String TVT_ROUND_EVENT_INSTANCE_FILE;
	public static String[] TVT_ROUND_EVENT_INTERVAL;
	public static int TVT_ROUND_EVENT_PARTICIPATION_TIME;
	public static int TVT_ROUND_EVENT_FIRST_FIGHT_RUNNING_TIME;
	public static int TVT_ROUND_EVENT_SECOND_FIGHT_RUNNING_TIME;
	public static int TVT_ROUND_EVENT_THIRD_FIGHT_RUNNING_TIME;
	public static int TVT_ROUND_EVENT_PARTICIPATION_NPC_ID;
	public static int[] TVT_ROUND_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
	public static int[] TVT_ROUND_EVENT_PARTICIPATION_FEE = new int[2];
	public static int TVT_ROUND_EVENT_MIN_PLAYERS_IN_TEAMS;
	public static int TVT_ROUND_EVENT_MAX_PLAYERS_IN_TEAMS;
	public static boolean TVT_ROUND_EVENT_ON_DIE;
	public static int TVT_ROUND_EVENT_START_RESPAWN_LEAVE_TELEPORT_DELAY;
	public static String TVT_ROUND_EVENT_TEAM_1_NAME;
	public static int[] TVT_ROUND_EVENT_TEAM_1_COORDINATES = new int[3];
	public static String TVT_ROUND_EVENT_TEAM_2_NAME;
	public static int[] TVT_ROUND_EVENT_TEAM_2_COORDINATES = new int[3];
	public static List<int[]> TVT_ROUND_EVENT_REWARDS;
	public static boolean TVT_ROUND_EVENT_TARGET_TEAM_MEMBERS_ALLOWED;
	public static boolean TVT_ROUND_EVENT_SCROLL_ALLOWED;
	public static boolean TVT_ROUND_EVENT_POTIONS_ALLOWED;
	public static boolean TVT_ROUND_EVENT_SUMMON_BY_ITEM_ALLOWED;
	public static List<Integer> TVT_ROUND_DOORS_IDS_TO_OPEN;
	public static List<Integer> TVT_ROUND_DOORS_IDS_TO_CLOSE;
	public static List<Integer> TVT_ROUND_ANTEROOM_DOORS_IDS_TO_OPEN_CLOSE;
	public static int TVT_ROUND_EVENT_WAIT_OPEN_ANTEROOM_DOORS;
	public static int TVT_ROUND_EVENT_WAIT_CLOSE_ANTEROOM_DOORS;
	public static boolean TVT_ROUND_EVENT_STOP_ON_TIE;
	public static int TVT_ROUND_EVENT_MINIMUM_TIE;
	public static boolean TVT_ROUND_GIVE_POINT_TEAM_TIE;
	public static boolean TVT_ROUND_REWARD_TEAM_TIE;
	public static boolean TVT_ROUND_EVENT_REWARD_ON_SECOND_FIGHT_END;
	public static byte TVT_ROUND_EVENT_MIN_LVL;
	public static byte TVT_ROUND_EVENT_MAX_LVL;
	public static int TVT_ROUND_EVENT_EFFECTS_REMOVAL;
	public static TIntIntHashMap TVT_ROUND_EVENT_FIGHTER_BUFFS;
	public static TIntIntHashMap TVT_ROUND_EVENT_MAGE_BUFFS;
	public static int TVT_ROUND_EVENT_MAX_PARTICIPANTS_PER_IP;
	public static boolean TVT_ROUND_ALLOW_VOICED_COMMAND;
	
	/** ************************************************** **/
	/**  CTF Settings		    						   **/
	/** ************************************************** **/
	public static boolean DM_ALLOW_INTERFERENCE;
	public static boolean DM_ALLOW_POTIONS;
	public static boolean DM_ALLOW_SUMMON;
	public static boolean DM_ON_START_REMOVE_ALL_EFFECTS;
	public static boolean DM_ON_START_UNSUMMON_PET;
	public static long DM_REVIVE_DELAY;
	
	/** ************************************************** **/
	/**  CTF Settings		    						   **/
	/** ************************************************** **/
	public static boolean CTF_EVENT_ENABLED;
	public static String[] CTF_EVENT_INTERVAL;
	public static String CTF_EVEN_TEAMS;
	public static boolean CTF_ALLOW_VOICE_COMMAND;
	public static boolean CTF_ALLOW_INTERFERENCE;
	public static boolean CTF_ALLOW_POTIONS;
	public static boolean CTF_ALLOW_SUMMON;
	public static boolean CTF_ON_START_REMOVE_ALL_EFFECTS;
	public static boolean CTF_ON_START_UNSUMMON_PET;
	public static boolean CTF_ANNOUNCE_TEAM_STATS;
	public static boolean CTF_ANNOUNCE_REWARD;
	public static boolean CTF_JOIN_CURSED;
	public static boolean CTF_REVIVE_RECOVERY;
	public static long CTF_REVIVE_DELAY;
	public static boolean CTF_BUFFS_AFTER_DIE;
	public static TIntIntHashMap CTF_EVENT_FIGHTER_BUFFS;
	public static TIntIntHashMap CTF_EVENT_MAGE_BUFFS;
	
	/** ************************************************** **/
	/**  Quest Rate					    				   **/
	/** ************************************************** **/
    public static int RiseandFalloftheElrokiTribeDropChance;
    public static int RiseandFalloftheElrokiTribeReward;
    public static int GiantsExploration1DropChance;
    public static int APowerfulPrimevalCreatureEggDropChance;
    public static int APowerfulPrimevalCreatureTissueDropChance;
	public static int RagnaOrcAmuletDropChance;
	public static int JudeRequestDropChance;
	public static int SuccesFailureOfBusinessDropChance;
	public static int WontYouJoinUsDropChance;
	public static int MucrokianHideDropChance;
	public static int AwakenedMucrokianHideDropChance;
	public static int ContaminatedMucrokianHideDropChance;
	public static int MineralFragmentDropChance;
	public static int CursedBurialDropChance;	

	/** ************************************************** **/
	/**  Pk Protection					 				   **/
	/** ************************************************** **/
    public static int DISABLE_ATTACK_IF_LVL_DIFFERENCE_OVER;
    public static int PUNISH_PK_PLAYER_IF_PKS_OVER;
    public static long PK_MONITOR_PERIOD;
    public static String PK_PUNISHMENT_TYPE;
    public static long PK_PUNISHMENT_PERIOD;
    
	/** ************************************************** **/
    /**  Killer's stats config.                            **/
	/** ************************************************** **/
    public static boolean ENABLED_KILLERS_STATS;

	/** ************************************************** **/
    /**  Database config.                            **/
	/** ************************************************** **/
    public static boolean DATABASE_BACKUP_MAKE_BACKUP_ON_STARTUP;
    public static boolean DATABASE_BACKUP_MAKE_BACKUP_ON_SHUTDOWN;
    public static String DATABASE_BACKUP_DATABASE_NAME;
    public static String DATABASE_BACKUP_SAVE_PATH;
    public static boolean DATABASE_BACKUP_COMPRESSION;
    public static String DATABASE_BACKUP_MYSQLDUMP_PATH;

    /**
	 * This class initializes all global variables for configuration.<br>
	 * If the key doesn't appear in properties file, a default value is set by this class.
	 * @see CONFIGURATION_FILE (properties file) for configuring your server.
	 */
	public static void loadconfig()
	{
		if (Server.serverMode == Server.MODE_GAMESERVER)
		{
			_log.info("Loading Custom GameServer Configuration Files...");
			InputStream is = null;
			try
			{
				try
				{
					L2Properties aioSettings = new L2Properties();
					is = new FileInputStream(new File(AIO_CONFIG_FILE));
					aioSettings.load(is);
					
					AIOITEM_ENABLEME = Boolean.parseBoolean(aioSettings.getProperty("EnableAIOItem", "false"));
					AIOITEM_ONLY_FOR_PREMIUM = Boolean.parseBoolean(aioSettings.getProperty("AIOItemOnlyForPremium", "false"));
					AIOITEM_ONLY_IN_TOWN = Boolean.parseBoolean(aioSettings.getProperty("AIOItemOnlyInTown", "false"));
					AIOITEM_ENABLESHOP = Boolean.parseBoolean(aioSettings.getProperty("EnableGMShop", "false"));
					AIOITEM_ENABLEGK = Boolean.parseBoolean(aioSettings.getProperty("EnableGk", "false"));
					AIOITEM_ENABLEWH = Boolean.parseBoolean(aioSettings.getProperty("EnableWh", "false"));
					AIOITEM_ENABLEBUFF = Boolean.parseBoolean(aioSettings.getProperty("EnableBuffer", "false"));
					AIOITEM_ENABLESCHEMEBUFF = Boolean.parseBoolean(aioSettings.getProperty("EnableSchemeBuffer", "false"));
					AIOITEM_ENABLESERVICES = Boolean.parseBoolean(aioSettings.getProperty("EnableServices", "false"));
					AIOITEM_ENABLESUBCLASS = Boolean.parseBoolean(aioSettings.getProperty("EnableSubclassManager", "false"));
					AIOITEM_ENABLETOPLIST = Boolean.parseBoolean(aioSettings.getProperty("EnableTopListManager", "false"));
					AIOITEM_GK_COIN = Integer.parseInt(aioSettings.getProperty("GkCoin", "57"));
					AIOITEM_GK_PRICE = Integer.parseInt(aioSettings.getProperty("GkPrice", "100"));
					AIOITEM_BUFF_COIN = Integer.parseInt(aioSettings.getProperty("BufferCoin", "57"));
					AIOITEM_BUFF_PRICE = Integer.parseInt(aioSettings.getProperty("BufferPrice", "100"));
					AIOITEM_SCHEME_COIN = Integer.parseInt(aioSettings.getProperty("SchemeCoin", "57"));
					AIOITEM_SCHEME_PRICE = Integer.parseInt(aioSettings.getProperty("SchemePrice", "100"));
					AIOITEM_SCHEME_PROFILE_PRICE = Integer.parseInt(aioSettings.getProperty("SchemeProfileCreationPrice", "1000"));
					AIOITEM_SCHEME_MAX_PROFILES = Integer.parseInt(aioSettings.getProperty("SchemeMaxProfiles", "4"));
					AIOITEM_SCHEME_MAX_PROFILE_BUFFS = Integer.parseInt(aioSettings.getProperty("SchemeMaxProfileBuffs", "24"));
				}
				catch(Exception e)
				{
					_log.warning("CustomConfig.load(): Couldn't load AIO Item settings. Reason:");
					e.printStackTrace();
				}
				/** ************************************************** **/
				/**  Load IRC Properties 							   **/
				/** ************************************************** **/
				try
				{
					_log.info("Loading " + IRC_CONFIG_FILE.replaceAll("./config/", ""));
					L2Properties ircSettings = new L2Properties();
					is = new FileInputStream(new File(IRC_CONFIG_FILE));
					ircSettings.load(is);
					
					IRC_ENABLED = Boolean.parseBoolean(ircSettings.getProperty("Enable", "false"));
					IRC_SERVER = ircSettings.getProperty("Server", "localhost");
					IRC_PORT = TypeFormat.parseInt(ircSettings.getProperty("Port", "6667"));
					IRC_PASS = ircSettings.getProperty("Password", "localhost");
					IRC_NICK = ircSettings.getProperty("Nick", "l2bot");
					IRC_USER = ircSettings.getProperty("User", "l2");
					IRC_NAME = ircSettings.getProperty("Name", "l2");
					IRC_NICKSERV = Boolean.parseBoolean(ircSettings.getProperty("NickServ", "false"));
					IRC_NICKSERV_NAME = ircSettings.getProperty("NickservName", "nickserv");
					IRC_NICKSERV_PASS = ircSettings.getProperty("NickservPassword", "");
					IRC_LOGIN_COMMAND = ircSettings.getProperty("LoginCommand", "");
					IRC_CHANNEL = ircSettings.getProperty("Channel", "#mychan");
					IRC_DEBUG = Boolean.parseBoolean(ircSettings.getProperty("Debug", "false"));
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new Error("Failed to Load " + IRC_CONFIG_FILE + " File.");
				}
				
				/** ************************************************** **/
				/**  Load TVT Properties 							   **/
				/** ************************************************** **/
				try
				{
					L2Properties TVTSettings = new L2Properties();
					is = new FileInputStream(new File(TVT_CONFIG_FILE));
					TVTSettings.load(is);
					
					TVT_EVENT_ENABLED = Boolean.parseBoolean(TVTSettings.getProperty("TvTEventEnabled", "false"));
					TVT_EVENT_IN_INSTANCE = Boolean.parseBoolean(TVTSettings.getProperty("TvTEventInInstance", "false"));
					TVT_EVENT_INSTANCE_FILE = TVTSettings.getProperty("TvTEventInstanceFile", "coliseum.xml");
					TVT_EVENT_INTERVAL = TVTSettings.getProperty("TvTEventInterval", "20:00").split(",");
					TVT_EVENT_PARTICIPATION_TIME = Integer.parseInt(TVTSettings.getProperty("TvTEventParticipationTime", "3600"));
					TVT_EVENT_RUNNING_TIME = Integer.parseInt(TVTSettings.getProperty("TvTEventRunningTime", "1800"));
					TVT_ALLOW_REGISTER_VOICED_COMMAND = Boolean.parseBoolean(TVTSettings.getProperty("TvTAllowRegisterVoicedCommand", "false"));
					TVT_EVENT_PARTICIPATION_NPC_ID = Integer.parseInt(TVTSettings.getProperty("TvTEventParticipationNpcId", "0"));
					
					/**Tvt Round**/
					TVT_ROUND_EVENT_ENABLED = Boolean.parseBoolean(TVTSettings.getProperty("TvTRoundEventEnabled", "false"));
					TVT_ROUND_EVENT_IN_INSTANCE = Boolean.parseBoolean(TVTSettings.getProperty("TvTRoundEventInInstance", "false"));
					TVT_ROUND_EVENT_INSTANCE_FILE = TVTSettings.getProperty("TvTRoundEventInstanceFile", "coliseum.xml");
					TVT_ROUND_EVENT_INTERVAL = TVTSettings.getProperty("TvTRoundEventInterval", "20:00").split(",");
					TVT_ROUND_EVENT_PARTICIPATION_TIME = Integer.parseInt(TVTSettings.getProperty("TvTRoundEventParticipationTime", "3600"));
					TVT_ROUND_EVENT_FIRST_FIGHT_RUNNING_TIME = Integer.parseInt(TVTSettings.getProperty("TvTRoundEventFirstFightRunningTime", "1800"));
					TVT_ROUND_EVENT_SECOND_FIGHT_RUNNING_TIME = Integer.parseInt(TVTSettings.getProperty("TvTRoundEventSecondFightRunningTime", "1800"));
					TVT_ROUND_EVENT_THIRD_FIGHT_RUNNING_TIME = Integer.parseInt(TVTSettings.getProperty("TvTRoundEventThirdFightRunningTime", "1800"));
					TVT_ROUND_EVENT_PARTICIPATION_NPC_ID = Integer.parseInt(TVTSettings.getProperty("TvTRoundEventParticipationNpcId", "0"));
					TVT_ROUND_EVENT_ON_DIE = Boolean.parseBoolean(TVTSettings.getProperty("TvTRoundEventOnDie", "true"));

					if (TVT_EVENT_PARTICIPATION_NPC_ID == 0)
					{
						TVT_EVENT_ENABLED = false;
						_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationNpcId");
					}
					else
					{
						String[] propertySplit = TVTSettings.getProperty("TvTEventParticipationNpcCoordinates", "0,0,0").split(",");
						if (propertySplit.length < 3)
						{
							TVT_EVENT_ENABLED = false;
							_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationNpcCoordinates");
						}
						else
						{
							TVT_EVENT_REWARDS = new ArrayList<int[]>();
							TVT_DOORS_IDS_TO_OPEN = new ArrayList<Integer>();
							TVT_DOORS_IDS_TO_CLOSE = new ArrayList<Integer>();
							TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
							TVT_EVENT_TEAM_1_COORDINATES = new int[3];
							TVT_EVENT_TEAM_2_COORDINATES = new int[3];
							TVT_EVENT_PARTICIPATION_NPC_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
							TVT_EVENT_PARTICIPATION_NPC_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
							TVT_EVENT_PARTICIPATION_NPC_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
							if (propertySplit.length == 4)
								TVT_EVENT_PARTICIPATION_NPC_COORDINATES[3] = Integer.parseInt(propertySplit[3]);
							TVT_EVENT_MIN_PLAYERS_IN_TEAMS = Integer.parseInt(TVTSettings.getProperty("TvTEventMinPlayersInTeams", "1"));
							TVT_EVENT_MAX_PLAYERS_IN_TEAMS = Integer.parseInt(TVTSettings.getProperty("TvTEventMaxPlayersInTeams", "20"));
							TVT_EVENT_MIN_LVL = (byte) Integer.parseInt(TVTSettings.getProperty("TvTEventMinPlayerLevel", "1"));
							TVT_EVENT_MAX_LVL = (byte) Integer.parseInt(TVTSettings.getProperty("TvTEventMaxPlayerLevel", "80"));
							TVT_EVENT_RESPAWN_TELEPORT_DELAY = Integer.parseInt(TVTSettings.getProperty("TvTEventRespawnTeleportDelay", "20"));
							TVT_EVENT_START_LEAVE_TELEPORT_DELAY = Integer.parseInt(TVTSettings.getProperty("TvTEventStartLeaveTeleportDelay", "20"));
							TVT_EVENT_EFFECTS_REMOVAL = Integer.parseInt(TVTSettings.getProperty("TvTEventEffectsRemoval", "0"));
							TVT_ALLOW_VOICED_COMMAND = Boolean.parseBoolean(TVTSettings.getProperty("TvTAllowVoicedInfoCommand", "false"));
							TVT_EVENT_TEAM_1_NAME = TVTSettings.getProperty("TvTEventTeam1Name", "Team1");
							propertySplit = TVTSettings.getProperty("TvTEventTeam1Coordinates", "0,0,0").split(",");
							if (propertySplit.length < 3)
							{
								TVT_EVENT_ENABLED = false;
								_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventTeam1Coordinates");
							}
							else
							{
								TVT_EVENT_TEAM_1_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
								TVT_EVENT_TEAM_1_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
								TVT_EVENT_TEAM_1_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
								TVT_EVENT_TEAM_2_NAME = TVTSettings.getProperty("TvTEventTeam2Name", "Team2");
								propertySplit = TVTSettings.getProperty("TvTEventTeam2Coordinates", "0,0,0").split(",");
								if (propertySplit.length < 3)
								{
									TVT_EVENT_ENABLED = false;
									_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventTeam2Coordinates");
								}
								else
								{
									TVT_EVENT_TEAM_2_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
									TVT_EVENT_TEAM_2_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
									TVT_EVENT_TEAM_2_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
									propertySplit = TVTSettings.getProperty("TvTEventParticipationFee", "0,0").split(",");
									try
									{
										TVT_EVENT_PARTICIPATION_FEE[0] = Integer.parseInt(propertySplit[0]);
										TVT_EVENT_PARTICIPATION_FEE[1] = Integer.parseInt(propertySplit[1]);
									}
									catch (NumberFormatException nfe)
									{
										if (propertySplit.length > 0)
											_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationFee");
									}
									propertySplit = TVTSettings.getProperty("TvTEventReward", "57,100000").split(";");
									for (String reward : propertySplit)
									{
										String[] rewardSplit = reward.split(",");
										if (rewardSplit.length != 2)
											_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventReward \"", reward, "\""));
										else
										{
											try
											{
												TVT_EVENT_REWARDS.add(new int[] { Integer.parseInt(rewardSplit[0]), Integer.parseInt(rewardSplit[1]) });
											}
											catch (NumberFormatException nfe)
											{
												if (!reward.isEmpty())
													_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventReward \"", reward, "\""));
											}
										}
									}
									
									TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED = Boolean.parseBoolean(TVTSettings.getProperty("TvTEventTargetTeamMembersAllowed", "true"));
									TVT_EVENT_SCROLL_ALLOWED = Boolean.parseBoolean(TVTSettings.getProperty("TvTEventScrollsAllowed", "false"));
									TVT_EVENT_POTIONS_ALLOWED = Boolean.parseBoolean(TVTSettings.getProperty("TvTEventPotionsAllowed", "false"));
									TVT_EVENT_SUMMON_BY_ITEM_ALLOWED = Boolean.parseBoolean(TVTSettings.getProperty("TvTEventSummonByItemAllowed", "false"));
									TVT_REWARD_TEAM_TIE = Boolean.parseBoolean(TVTSettings.getProperty("TvTRewardTeamTie", "false"));
									propertySplit = TVTSettings.getProperty("TvTDoorsToOpen", "").split(";");
									for (String door : propertySplit)
									{
										try
										{
											TVT_DOORS_IDS_TO_OPEN.add(Integer.parseInt(door));
										}
										catch (NumberFormatException nfe)
										{
											if (!door.isEmpty())
												_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTDoorsToOpen \"", door, "\""));
										}
									}
									
									propertySplit = TVTSettings.getProperty("TvTDoorsToClose", "").split(";");
									for (String door : propertySplit)
									{
										try
										{
											TVT_DOORS_IDS_TO_CLOSE.add(Integer.parseInt(door));
										}
										catch (NumberFormatException nfe)
										{
											if (!door.isEmpty())
												_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTDoorsToClose \"", door, "\""));
										}
									}
									
									propertySplit = TVTSettings.getProperty("TvTEventFighterBuffs", "").split(";");
									if (!propertySplit[0].isEmpty())
									{
										TVT_EVENT_FIGHTER_BUFFS = new TIntIntHashMap(propertySplit.length);
										for (String skill : propertySplit)
										{
											String[] skillSplit = skill.split(",");
											if (skillSplit.length != 2)
												_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventFighterBuffs \"", skill, "\""));
											else
											{
												try
												{
													TVT_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
												}
												catch (NumberFormatException nfe)
												{
													if (!skill.isEmpty())
														_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventFighterBuffs \"", skill, "\""));
												}
											}
										}
									}
									
									propertySplit = TVTSettings.getProperty("TvTEventMageBuffs", "").split(";");
									if (!propertySplit[0].isEmpty())
									{
										TVT_EVENT_MAGE_BUFFS = new TIntIntHashMap(propertySplit.length);
										for (String skill : propertySplit)
										{
											String[] skillSplit = skill.split(",");
											if (skillSplit.length != 2)
												_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventMageBuffs \"", skill, "\""));
											else
											{
												try
												{
													TVT_EVENT_MAGE_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
												}
												catch (NumberFormatException nfe)
												{
													if (!skill.isEmpty())
														_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventMageBuffs \"", skill, "\""));
												}
											}
										}
									}
								}
							}
						}
					}
					if (TVT_ROUND_EVENT_PARTICIPATION_NPC_ID == 0)
					{
						TVT_ROUND_EVENT_ENABLED = false;
						_log.warning("TvTRoundEventEngine[Config.load()]: invalid config property -> TvTRoundEventParticipationNpcId");
					}
					else
					{
						String[] propertySplit = TVTSettings.getProperty("TvTRoundEventParticipationNpcCoordinates", "0,0,0").split(",");
						if (propertySplit.length < 3)
						{
							TVT_ROUND_EVENT_ENABLED = false;
							_log.warning("TvTRoundEventEngine[Config.load()]: invalid config property -> TvTRoundEventParticipationNpcCoordinates");
						}
						else
						{
							TVT_ROUND_EVENT_REWARDS = new ArrayList<int[]>();
							TVT_ROUND_DOORS_IDS_TO_OPEN = new ArrayList<Integer>();
							TVT_ROUND_DOORS_IDS_TO_CLOSE = new ArrayList<Integer>();
							TVT_ROUND_ANTEROOM_DOORS_IDS_TO_OPEN_CLOSE = new ArrayList<Integer>();
							TVT_ROUND_EVENT_WAIT_OPEN_ANTEROOM_DOORS = Integer.parseInt(TVTSettings.getProperty("TvTRoundEventWaitOpenAnteroomDoors", "30"));
							TVT_ROUND_EVENT_WAIT_CLOSE_ANTEROOM_DOORS = Integer.parseInt(TVTSettings.getProperty("TvTRoundEventWaitCloseAnteroomDoors", "15"));
							TVT_ROUND_EVENT_STOP_ON_TIE = Boolean.parseBoolean(TVTSettings.getProperty("TvTRoundEventStopOnTie", "false"));
							TVT_ROUND_EVENT_MINIMUM_TIE = Integer.parseInt(TVTSettings.getProperty("TvTRoundEventMinimumTie", "1"));
							if (TVT_ROUND_EVENT_MINIMUM_TIE != 1 && TVT_ROUND_EVENT_MINIMUM_TIE != 2 && TVT_ROUND_EVENT_MINIMUM_TIE != 3) TVT_ROUND_EVENT_MINIMUM_TIE = 1;
							TVT_ROUND_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
							TVT_ROUND_EVENT_TEAM_1_COORDINATES = new int[3];
							TVT_ROUND_EVENT_TEAM_2_COORDINATES = new int[3];
							TVT_ROUND_EVENT_PARTICIPATION_NPC_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
							TVT_ROUND_EVENT_PARTICIPATION_NPC_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
							TVT_ROUND_EVENT_PARTICIPATION_NPC_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
							if (propertySplit.length == 4)
								TVT_ROUND_EVENT_PARTICIPATION_NPC_COORDINATES[3] = Integer.parseInt(propertySplit[3]);
							TVT_ROUND_EVENT_MIN_PLAYERS_IN_TEAMS = Integer.parseInt(TVTSettings.getProperty("TvTRoundEventMinPlayersInTeams", "1"));
							TVT_ROUND_EVENT_MAX_PLAYERS_IN_TEAMS = Integer.parseInt(TVTSettings.getProperty("TvTRoundEventMaxPlayersInTeams", "20"));
							TVT_ROUND_EVENT_MIN_LVL = (byte)Integer.parseInt(TVTSettings.getProperty("TvTRoundEventMinPlayerLevel", "1"));
							TVT_ROUND_EVENT_MAX_LVL = (byte)Integer.parseInt(TVTSettings.getProperty("TvTRoundEventMaxPlayerLevel", "80"));
							TVT_ROUND_EVENT_START_RESPAWN_LEAVE_TELEPORT_DELAY = Integer.parseInt(TVTSettings.getProperty("TvTRoundEventStartRespawnLeaveTeleportDelay", "10"));
							TVT_ROUND_EVENT_EFFECTS_REMOVAL = Integer.parseInt(TVTSettings.getProperty("TvTRoundEventEffectsRemoval", "0"));
							TVT_ROUND_EVENT_MAX_PARTICIPANTS_PER_IP = Integer.parseInt(TVTSettings.getProperty("TvTRoundEventMaxParticipantsPerIP", "0"));
							TVT_ROUND_ALLOW_VOICED_COMMAND = Boolean.parseBoolean(TVTSettings.getProperty("TvTRoundAllowVoicedInfoCommand", "false"));
							TVT_ROUND_EVENT_TEAM_1_NAME = TVTSettings.getProperty("TvTRoundEventTeam1Name", "Team1");
							propertySplit = TVTSettings.getProperty("TvTRoundEventTeam1Coordinates", "0,0,0").split(",");
							if (propertySplit.length < 3)
							{
								TVT_ROUND_EVENT_ENABLED = false;
								_log.warning("TvTRoundEventEngine[Config.load()]: invalid config property -> TvTRoundEventTeam1Coordinates");
							}
							else
							{
								TVT_ROUND_EVENT_TEAM_1_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
								TVT_ROUND_EVENT_TEAM_1_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
								TVT_ROUND_EVENT_TEAM_1_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
								TVT_ROUND_EVENT_TEAM_2_NAME = TVTSettings.getProperty("TvTRoundEventTeam2Name", "Team2");
								propertySplit = TVTSettings.getProperty("TvTRoundEventTeam2Coordinates", "0,0,0").split(",");
								if (propertySplit.length < 3)
								{
									TVT_ROUND_EVENT_ENABLED= false;
									_log.warning("TvTRoundEventEngine[Config.load()]: invalid config property -> TvTRoundEventTeam2Coordinates");
								}
								else
								{
									TVT_ROUND_EVENT_TEAM_2_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
									TVT_ROUND_EVENT_TEAM_2_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
									TVT_ROUND_EVENT_TEAM_2_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
									propertySplit = TVTSettings.getProperty("TvTRoundEventParticipationFee", "0,0").split(",");
									try
									{
										TVT_ROUND_EVENT_PARTICIPATION_FEE[0] = Integer.parseInt(propertySplit[0]);
										TVT_ROUND_EVENT_PARTICIPATION_FEE[1] = Integer.parseInt(propertySplit[1]);
									}
									catch (NumberFormatException nfe)
									{
										if (propertySplit.length > 0)
											_log.warning("TvTRoundEventEngine[Config.load()]: invalid config property -> TvTRoundEventParticipationFee");
									}
									propertySplit = TVTSettings.getProperty("TvTRoundEventReward", "57,100000").split(";");
									for (String reward : propertySplit)
									{
										String[] rewardSplit = reward.split(",");
										if (rewardSplit.length != 2)
											_log.warning(StringUtil.concat("TvTRoundEventEngine[Config.load()]: invalid config property -> TvTRoundEventReward \"", reward, "\""));
										else
										{
											try
											{
												TVT_ROUND_EVENT_REWARDS.add(new int[]{Integer.parseInt(rewardSplit[0]), Integer.parseInt(rewardSplit[1])});
											}
											catch (NumberFormatException nfe)
											{
												if (!reward.isEmpty())
													_log.warning(StringUtil.concat("TvTRoundEventEngine[Config.load()]: invalid config property -> TvTRoundEventReward \"", reward, "\""));
											}
										}
									}
									
									TVT_ROUND_EVENT_TARGET_TEAM_MEMBERS_ALLOWED = Boolean.parseBoolean(TVTSettings.getProperty("TvTRoundEventTargetTeamMembersAllowed", "true"));
									TVT_ROUND_EVENT_SCROLL_ALLOWED = Boolean.parseBoolean(TVTSettings.getProperty("TvTRoundEventScrollsAllowed", "false"));
									TVT_ROUND_EVENT_POTIONS_ALLOWED = Boolean.parseBoolean(TVTSettings.getProperty("TvTRoundEventPotionsAllowed", "false"));
									TVT_ROUND_EVENT_SUMMON_BY_ITEM_ALLOWED = Boolean.parseBoolean(TVTSettings.getProperty("TvTRoundEventSummonByItemAllowed", "false"));
									TVT_ROUND_GIVE_POINT_TEAM_TIE = Boolean.parseBoolean(TVTSettings.getProperty("TvTRoundGivePointTeamTie", "false"));
									TVT_ROUND_REWARD_TEAM_TIE = Boolean.parseBoolean(TVTSettings.getProperty("TvTRoundRewardTeamTie", "false"));
									TVT_ROUND_EVENT_REWARD_ON_SECOND_FIGHT_END = Boolean.parseBoolean(TVTSettings.getProperty("TvTRoundEventRewardOnSecondFightEnd", "false"));
									propertySplit = TVTSettings.getProperty("TvTRoundDoorsToOpen", "").split(";");
									for (String door : propertySplit)
									{
										try
										{
											TVT_ROUND_DOORS_IDS_TO_OPEN.add(Integer.parseInt(door));
										}
										catch (NumberFormatException nfe)
										{
											if (!door.isEmpty())
												_log.warning(StringUtil.concat("TvTRoundEventEngine[Config.load()]: invalid config property -> TvTRoundDoorsToOpen \"", door, "\""));
										}
									}
									
									propertySplit = TVTSettings.getProperty("TvTRoundDoorsToClose", "").split(";");
									for (String door : propertySplit)
									{
										try
										{
											TVT_ROUND_DOORS_IDS_TO_CLOSE.add(Integer.parseInt(door));
										}
										catch (NumberFormatException nfe)
										{
											if (!door.isEmpty())
												_log.warning(StringUtil.concat("TvTRoundEventEngine[Config.load()]: invalid config property -> TvTRoundDoorsToClose \"", door, "\""));
										}
									}
									
									propertySplit = TVTSettings.getProperty("TvTRoundAnteroomDoorsToOpenClose", "").split(";");
									for (String door : propertySplit)
									{
										try
										{
											TVT_ROUND_ANTEROOM_DOORS_IDS_TO_OPEN_CLOSE.add(Integer.parseInt(door));
										}
										catch (NumberFormatException nfe)
										{
											if (!door.isEmpty())
												_log.warning(StringUtil.concat("TvTRoundEventEngine[Config.load()]: invalid config property -> TvTRoundAnteroomDoorsToOpenClose \"", door, "\""));
										}
									}
									
									propertySplit = TVTSettings.getProperty("TvTRoundEventFighterBuffs", "").split(";");
									if (!propertySplit[0].isEmpty())
									{
										TVT_ROUND_EVENT_FIGHTER_BUFFS = new TIntIntHashMap(propertySplit.length);
										for (String skill : propertySplit)
										{
											String[] skillSplit = skill.split(",");
											if (skillSplit.length != 2)
												_log.warning(StringUtil.concat("TvTRoundEventEngine[Config.load()]: invalid config property -> TvTRoundEventFighterBuffs \"", skill, "\""));
											else
											{
												try
												{
													TVT_ROUND_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
												}
												catch (NumberFormatException nfe)
												{
													if (!skill.isEmpty())
														_log.warning(StringUtil.concat("TvTRoundEventEngine[Config.load()]: invalid config property -> TvTRoundEventFighterBuffs \"", skill, "\""));
												}
											}
										}
									}
									
									propertySplit = TVTSettings.getProperty("TvTRoundEventMageBuffs", "").split(";");
									if (!propertySplit[0].isEmpty())
									{
										TVT_ROUND_EVENT_MAGE_BUFFS = new TIntIntHashMap(propertySplit.length);
										for (String skill : propertySplit)
										{
											String[] skillSplit = skill.split(",");
											if (skillSplit.length != 2)
												_log.warning(StringUtil.concat("TvTRoundEventEngine[Config.load()]: invalid config property -> TvTRoundEventMageBuffs \"", skill, "\""));
											else
											{
												try
												{
													TVT_ROUND_EVENT_MAGE_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
												}
												catch (NumberFormatException nfe)
												{
													if (!skill.isEmpty())
														_log.warning(StringUtil.concat("TvTRoundEventEngine[Config.load()]: invalid config property -> TvTRoundEventMageBuffs \"", skill, "\""));
												}
											}
										}
									}
								}
							}
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new Error("Failed to Load " + TVT_CONFIG_FILE + " File.");
				}
				
				/** ************************************************** **/
				/**  Load CTF Properties 							   **/
				/** ************************************************** **/
				try
				{
					L2Properties CtfSettings = new L2Properties();
					is = new FileInputStream(new File(CTF_CONFIG_FILE));
					CtfSettings.load(is);
					
					CTF_EVENT_ENABLED = Boolean.parseBoolean(CtfSettings.getProperty("CTFEventEnabled", "false"));
					CTF_EVENT_INTERVAL = CtfSettings.getProperty("CTFEventInterval", "20:00").split(",");
					CTF_EVEN_TEAMS = CtfSettings.getProperty("CTFEvenTeams", "BALANCE");
					CTF_ALLOW_VOICE_COMMAND = Boolean.parseBoolean(CtfSettings.getProperty("CTFAllowVoiceCommand", "false"));
					CTF_ALLOW_INTERFERENCE = Boolean.parseBoolean(CtfSettings.getProperty("CTFAllowInterference", "false"));
					CTF_ALLOW_POTIONS = Boolean.parseBoolean(CtfSettings.getProperty("CTFAllowPotions", "false"));
					CTF_ALLOW_SUMMON = Boolean.parseBoolean(CtfSettings.getProperty("CTFAllowSummon", "false"));
					CTF_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(CtfSettings.getProperty("CTFOnStartRemoveAllEffects", "true"));
					CTF_ON_START_UNSUMMON_PET = Boolean.parseBoolean(CtfSettings.getProperty("CTFOnStartUnsummonPet", "true"));
					CTF_ANNOUNCE_TEAM_STATS = Boolean.parseBoolean(CtfSettings.getProperty("CTFAnnounceTeamStats", "false"));
					CTF_ANNOUNCE_REWARD = Boolean.parseBoolean(CtfSettings.getProperty("CTFAnnounceReward", "false"));
					CTF_JOIN_CURSED = Boolean.parseBoolean(CtfSettings.getProperty("CTFJoinWithCursedWeapon", "true"));
					CTF_REVIVE_RECOVERY = Boolean.parseBoolean(CtfSettings.getProperty("CTFReviveRecovery", "false"));
					CTF_REVIVE_DELAY = Long.parseLong(CtfSettings.getProperty("CTFReviveDelay", "20000"));
					CTF_BUFFS_AFTER_DIE = Boolean.parseBoolean(CtfSettings.getProperty("CTFBuffsAfterDie", "false"));
					
					if (CTF_REVIVE_DELAY < 1000)
						CTF_REVIVE_DELAY = 1000; //can't be set less then 1 second	
						
					String[] propertySplit = CtfSettings.getProperty("CtfEventFighterBuffs", "").split(";");
					if (!propertySplit[0].isEmpty())
					{
						CTF_EVENT_FIGHTER_BUFFS = new TIntIntHashMap(propertySplit.length);
						for (String skill : propertySplit)
						{
							String[] skillSplit = skill.split(",");
							if (skillSplit.length != 2)
								_log.warning(StringUtil.concat("CtfEventEngine[Config.load()]: invalid config property -> CtfEventFighterBuffs \"", skill, "\""));
							else
							{
								try
								{
									CTF_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
								}
								catch (NumberFormatException nfe)
								{
									if (!skill.isEmpty())
										_log.warning(StringUtil.concat("CtfEventEngine[Config.load()]: invalid config property -> CtfEventFighterBuffs \"", skill, "\""));
								}
							}
						}
					}
					
					propertySplit = CtfSettings.getProperty("CtfEventMageBuffs", "").split(";");
					if (!propertySplit[0].isEmpty())
					{
						CTF_EVENT_MAGE_BUFFS = new TIntIntHashMap(propertySplit.length);
						for (String skill : propertySplit)
						{
							String[] skillSplit = skill.split(",");
							if (skillSplit.length != 2)
								_log.warning(StringUtil.concat("CtfEventEngine[Config.load()]: invalid config property -> CtfEventMageBuffs \"", skill, "\""));
							else
							{
								try
								{
									CTF_EVENT_MAGE_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
								}
								catch (NumberFormatException nfe)
								{
									if (!skill.isEmpty())
										_log.warning(StringUtil.concat("CtfEventEngine[Config.load()]: invalid config property -> CtfEventMageBuffs \"", skill, "\""));
								}
							}
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new Error("Failed to Load " + CTF_CONFIG_FILE + " File.");
				}
				
				/** ************************************************** **/
				/**  Load Hide And Seek Properties 							   **/
				/** ************************************************** **/
				try
				{
					L2Properties EventFiles = new L2Properties();
					is = new FileInputStream(new File(EVENT_FILES));
					EventFiles.load(is);
					//hide and seek
					HAS_ENABLED = Boolean.parseBoolean(EventFiles.getProperty("EnableHideAndSeek", "True"));
					HAS_EVENT_INTERVAL = EventFiles.getProperty("HideAndSeekEventInterval", "21:00").split(",");
					HAS_REG_MINS_DURATION = Integer.parseInt(EventFiles.getProperty("HideAndSeekRegMinDuration", "3"));
					HAS_EVENT_MINS_DURATION = Integer.parseInt(EventFiles.getProperty("HideAndSeekEventDuration", "10"));
					HAS_PK_PLAYER_CAN_JOIN = Boolean.parseBoolean(EventFiles.getProperty("HideAndSeekPKCanJoin", "False"));
					HAS_SEQUENCE_NPC = Boolean.parseBoolean(EventFiles.getProperty("HideAndSeekSeqNpc", "True"));
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new Error("Failed to Load " + EVENT_FILES + " File.");
				}
				
				/** ************************************************** **/
				/**  Load Monster Rush Properties 					   **/
				/** ************************************************** **/
				try
				{
					L2Properties MREventFiles = new L2Properties();
					is = new FileInputStream(new File(MR_FILES));
					MREventFiles.load(is);
					MR_ENABLED = Boolean.parseBoolean(MREventFiles.getProperty("EnableMonsterRush", "True"));
					MR_EVENT_INTERVAL = MREventFiles.getProperty("MREventInterval", "18:00").split(",");
					MR_PARTICIPATION_TIME = Integer.parseInt(MREventFiles.getProperty("MREventParticipationTime", "3600"));
					MR_RUNNING_TIME = Integer.parseInt(MREventFiles.getProperty("MREventRunningTime", "1800"));
					MRUSH_REWARD_AMOUNT = Integer.parseInt(MREventFiles.getProperty("MrEventRewardAmount", "1"));
					MRUSH_REWARD_ITEM = Integer.parseInt(MREventFiles.getProperty("MrEventRewardItem", "3481"));
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new Error("Failed to Load " + MR_FILES + " File.");
				}
				
				/** ************************************************** **/
				/**  PC Bang Points Event Properties 				   **/
				/** ************************************************** **/
				try
				{
					L2Properties PCBEventFiles = new L2Properties();
					is = new FileInputStream(new File(PC_BANG_POINT_FILE));
					PCBEventFiles.load(is);
					PCB_ENABLE = Boolean.parseBoolean(PCBEventFiles.getProperty("PcBangPointEnable", "true"));
					PCB_MIN_LEVEL = Integer.parseInt(PCBEventFiles.getProperty("PcBangPointMinLevel", "20"));
					PCB_POINT_MIN = Integer.parseInt(PCBEventFiles.getProperty("PcBangPointMinCount", "20"));
					PCB_POINT_MAX = Integer.parseInt(PCBEventFiles.getProperty("PcBangPointMaxCount", "1000000"));
					
					if (PCB_POINT_MAX < 1)
					{
						PCB_POINT_MAX = Integer.MAX_VALUE;
					}
					
					PCB_CHANCE_DUAL_POINT = Integer.parseInt(PCBEventFiles.getProperty("PcBangPointDualChance", "20"));
					PCB_INTERVAL = Integer.parseInt(PCBEventFiles.getProperty("PcBangPointTimeStamp", "900"));
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new Error("Failed to Load " + PC_BANG_POINT_FILE + " File.");
				}
				
				/** ************************************************** **/
				/**  Load PREMIUM SERVICE Properties 				   **/
				/** ************************************************** **/
				try
				{
					L2Properties PremiumFiles = new L2Properties();
					is = new FileInputStream(new File(PREMIUM_FILES));
					PremiumFiles.load(is);
					
					// Premium service start
					USE_PREMIUMSERVICE = Boolean.parseBoolean(PremiumFiles.getProperty("UsePremiumServices", "False"));
					PREMIUM_RATE_XP = Float.parseFloat(PremiumFiles.getProperty("PremiumRateXp", "2"));
					PREMIUM_RATE_SP = Float.parseFloat(PremiumFiles.getProperty("PremiumRateSp", "2"));
					PREMIUM_RATE_DROP_ADENA = Float.parseFloat(PremiumFiles.getProperty("PremiumRateDropAdena", "2"));
					PREMIUM_DROP_ADENA_MULTIPLIER = Float.parseFloat(PremiumFiles.getProperty("PremiumDropAdenaMultiplier","1.5"));
					PREMIUM_RATE_DROP_SPOIL = Float.parseFloat(PremiumFiles.getProperty("PremiumRateDropSpoil", "2"));
					PREMIUM_RATE_DROP_ITEMS = Float.parseFloat(PremiumFiles.getProperty("PremiumRateDropItems", "2"));
					
					VOICED_BUFF_ONLY_PREMIUM = (USE_PREMIUMSERVICE ? Boolean.parseBoolean(PremiumFiles.getProperty("VoicedBuffOnlyPremium", "false")) : false);
					VOICED_BUFF_NOTPREMIUM_MESSAGE = PremiumFiles.getProperty("VoicedBuffErrorMessage", "Only premium account players can use this command");
					PREMIUM_ENCH_BONUS = Boolean.parseBoolean(PremiumFiles.getProperty("PremiumEnchBonus", "False"));
					if (PREMIUM_ENCH_BONUS)
						PREMIUM_ENCH_CHANCE_BONUS = Integer.parseInt(PremiumFiles.getProperty("PremiumEnchantChanceBonus", "0"));
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new Error("Failed to Load " + PREMIUM_FILES + " File.");
				}
				
				/** ************************************************** **/
				/**  Load TW Properties 							   **/
				/** ************************************************** **/
				try
				{
					L2Properties TownWarFiles = new L2Properties();
					is = new FileInputStream(new File(TOWNWAR_FILES));
					TownWarFiles.load(is);
					//town war
					TW_TOWN_ID = Integer.parseInt(TownWarFiles.getProperty("TownWarTownId", "9"));
					TW_TOWN_NAME = TownWarFiles.getProperty("TownWarTownName", "Giran Town");
					TW_ALL_TOWNS = Boolean.parseBoolean(TownWarFiles.getProperty("TownWarAllTowns", "False"));
					TW_AUTO_EVENT = Boolean.parseBoolean(TownWarFiles.getProperty("TownWarAutoEvent", "false"));
					TW_INTERVAL = TownWarFiles.getProperty("TownWarInterval", "20:00").split(",");
					TW_TIME_BEFORE_START = Integer.parseInt(TownWarFiles.getProperty("TownWarTimeBeforeStart", "3600"));
					TW_RUNNING_TIME = Integer.parseInt(TownWarFiles.getProperty("TownWarRunningTime", "1800"));
					TW_ITEM_ID = Integer.parseInt(TownWarFiles.getProperty("TownWarItemId", "57"));
					TW_ITEM_AMOUNT = Integer.parseInt(TownWarFiles.getProperty("TownWarItemAmount", "5000"));
					TW_GIVE_PVP_AND_PK_POINTS = Boolean.parseBoolean(TownWarFiles.getProperty("TownWarGivePvPAndPkPoints", "False"));
					TW_ALLOW_KARMA = Boolean.parseBoolean(TownWarFiles.getProperty("TownWarAllowKarma", "False"));
					TW_DISABLE_GK = Boolean.parseBoolean(TownWarFiles.getProperty("TownWarDisableGK", "True"));
					TW_RESS_ON_DEATH = Boolean.parseBoolean(TownWarFiles.getProperty("TownWarRessOnDeath", "True"));
					TW_LOSE_BUFFS_ON_DEATH = Boolean.parseBoolean(TownWarFiles.getProperty("TownWarLoseBuffsOnDeath", "False"));
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new Error("Failed to Load " + TOWNWAR_FILES + " File.");
				}
				
				/** ************************************************** **/
				/**  Load DM Properties 							   **/
				/** ************************************************** **/
				try
				{
					L2Properties DMFiles = new L2Properties();
					is = new FileInputStream(new File(DM_FILES));
					DMFiles.load(is);

					DM_ALLOW_INTERFERENCE = Boolean.parseBoolean(DMFiles.getProperty("DMAllowInterference", "false"));
					DM_ALLOW_POTIONS = Boolean.parseBoolean(DMFiles.getProperty("DMAllowPotions", "false"));
					DM_ALLOW_SUMMON = Boolean.parseBoolean(DMFiles.getProperty("DMAllowSummon", "false"));
					DM_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(DMFiles.getProperty("DMOnStartRemoveAllEffects", "true"));
					DM_ON_START_UNSUMMON_PET = Boolean.parseBoolean(DMFiles.getProperty("DMOnStartUnsummonPet", "true"));
					DM_REVIVE_DELAY = Long.parseLong(DMFiles.getProperty("DMReviveDelay", "20000"));
					if (DM_REVIVE_DELAY < 1000)
						DM_REVIVE_DELAY = 1000; //can't be set less then 1 second

				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new Error("Failed to Load " + DM_FILES + " File.");
				}
				
				/** ************************************************** **/
				/**  Load Block Cheker Properties 					   **/
				/** ************************************************** **/
				try
				{
					L2Properties HandyBlockFiles = new L2Properties();
					is = new FileInputStream(new File(BLOCK_CHECKER_EVENT));
					HandyBlockFiles.load(is);
					//Block Cheker
					ENABLE_BLOCK_CHECKER_EVENT = Boolean.valueOf(HandyBlockFiles.getProperty("EnableBlockCheckerEvent", "false"));
					HBCE_FAIR_PLAY = Boolean.parseBoolean(HandyBlockFiles.getProperty("HBCEFairPlay", "false"));
					MIN_BLOCK_CHECKER_TEAM_MEMBERS = Integer.valueOf(HandyBlockFiles.getProperty("BlockCheckerMinTeamMembers", "2"));
					if (MIN_BLOCK_CHECKER_TEAM_MEMBERS < 1)
						MIN_BLOCK_CHECKER_TEAM_MEMBERS = 1;
					else if (MIN_BLOCK_CHECKER_TEAM_MEMBERS > 6)
						MIN_BLOCK_CHECKER_TEAM_MEMBERS = 6;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new Error("Failed to Load " + BLOCK_CHECKER_EVENT + " File.");
				}
				
				/** ************************************************** **/
				/**  Load Quest Rate Properties 				   **/
				/** ************************************************** **/
				try
				{
					L2Properties QuestRateFiles = new L2Properties();
					is = new FileInputStream(new File(QUEST_RATE_FILE));
					QuestRateFiles.load(is);

					RiseandFalloftheElrokiTribeDropChance = Integer.valueOf(QuestRateFiles.getProperty("RiseandFalloftheElrokiTribeDropChance", "75"));
					RiseandFalloftheElrokiTribeReward = Integer.valueOf(QuestRateFiles.getProperty("RiseandFalloftheElrokiTribeReward", "1"));
					GiantsExploration1DropChance = Integer.valueOf(QuestRateFiles.getProperty("GiantsExploration1DropChance", "20"));
					APowerfulPrimevalCreatureEggDropChance = Integer.valueOf(QuestRateFiles.getProperty("APowerfulPrimevalCreatureEggDropChance", "1"));
					APowerfulPrimevalCreatureTissueDropChance = Integer.valueOf(QuestRateFiles.getProperty("APowerfulPrimevalCreatureTissueDropChance", "33"));
					RagnaOrcAmuletDropChance = Integer.valueOf(QuestRateFiles.getProperty("RagnaOrcAmuletDropChance", "40"));
					JudeRequestDropChance = Integer.valueOf(QuestRateFiles.getProperty("EvilWeaponDropChance", "55"));
					SuccesFailureOfBusinessDropChance = Integer.valueOf(QuestRateFiles.getProperty("SpiritFragmentDropChance", "80"));
					WontYouJoinUsDropChance = Integer.valueOf(QuestRateFiles.getProperty("EnchantedGolemFragmentDropChance", "80"));
					MucrokianHideDropChance = Integer.valueOf(QuestRateFiles.getProperty("MucrokianHideDropChance", "50"));
					AwakenedMucrokianHideDropChance = Integer.valueOf(QuestRateFiles.getProperty("AwakenedMucrokianHideDropChance", "50"));
					ContaminatedMucrokianHideDropChance = Integer.valueOf(QuestRateFiles.getProperty("ContaminatedMucrokianHideDropChance", "10"));
					MineralFragmentDropChance = Integer.valueOf(QuestRateFiles.getProperty("MineralFragmentDropChance", "20"));
					CursedBurialDropChance = Integer.valueOf(QuestRateFiles.getProperty("CursedBurialDropChance", "35"));	
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new Error("Failed to Load " + QUEST_RATE_FILE + " File.");
				}

				/** ************************************************** **/
				/**  Pk Protection  				 				   **/
				/** ************************************************** **/
				try
				{
					L2Properties EventsSettingsFiles = new L2Properties();
					is = new FileInputStream(new File(EVENTS_SETTINGS));
					EventsSettingsFiles.load(is);
					//BloodShed Party
					PARTY_MEMBER_COUNT = Integer.parseInt(EventsSettingsFiles.getProperty("MinPlayersInParty", "5"));
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new Error("Failed to Load " + EVENTS_SETTINGS + " File.");
				}

				/** ************************************************** **/
				/**  Events Settings  				 				   **/
				/** ************************************************** **/
				try
				{
					L2Properties PkProtectionFiles = new L2Properties();
					is = new FileInputStream(new File(PK_PROTECTION));
					PkProtectionFiles.load(is);
					  //Block Cheker
					DISABLE_ATTACK_IF_LVL_DIFFERENCE_OVER = Integer.parseInt(PkProtectionFiles.getProperty("DisableAttackIfLvlDifferenceOver", "0"));
					PUNISH_PK_PLAYER_IF_PKS_OVER = Integer.parseInt(PkProtectionFiles.getProperty("PunishPKPlayerIfPKsOver", "0"));
					PK_MONITOR_PERIOD = Long.parseLong(PkProtectionFiles.getProperty("PKMonitorPeriod", "3600"));
					PK_PUNISHMENT_TYPE = PkProtectionFiles.getProperty("PKPunishmentType", "jail");
					PK_PUNISHMENT_PERIOD = Long.parseLong(PkProtectionFiles.getProperty("PKPunishmentPeriod", "3600"));
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new Error("Failed to Load " + PK_PROTECTION + " File.");
				}

				/** ************************************************** **/
				/**  Load CUSTOM FILE Properties 					   **/
				/** ************************************************** **/
				try
				{
					L2Properties Custom = new L2Properties();
					is = new FileInputStream(new File(CUSTOM_FILE));
					Custom.load(is);
					
					CUSTOM_SCRIPTS = true;
					ALL_SUBCLASS_AVAILABLE = Boolean.parseBoolean(Custom.getProperty("AllSubClassAvailable", "false"));
					DROP_PROTECTED_TIME = Integer.parseInt(Custom.getProperty("DropProtectedTime", "5"));
					// 	PVP Title & Color System configs - Start
					PVP_TITLE_AND_COLOR_SYSTEM_ENABLED = Boolean.parseBoolean(Custom.getProperty("EnablePvPColorSystem", "false"));
					PVP_AMOUNT1 = Integer.parseInt(Custom.getProperty("PvpAmount1", "20"));
					TITLE_COLOR_FOR_PVP_AMOUNT1 = Integer.decode("0x" + Custom.getProperty("ColorForAmount1", "00FF00"));
					PVP1_TITLE = Custom.getProperty("TitleForAmount1", "Warrior");
					PVP_AMOUNT2 = Integer.parseInt(Custom.getProperty("PvpAmount2", "80"));
					TITLE_COLOR_FOR_PVP_AMOUNT2 = Integer.decode("0x" + Custom.getProperty("ColorForAmount2", "00FF00"));
					PVP2_TITLE = Custom.getProperty("TitleForAmount2", "Soldier");
					PVP_AMOUNT3 = Integer.parseInt(Custom.getProperty("PvpAmount3", "140"));
					TITLE_COLOR_FOR_PVP_AMOUNT3 = Integer.decode("0x" + Custom.getProperty("ColorForAmount3", "00FF00"));
					PVP3_TITLE = Custom.getProperty("TitleForAmount3", "Elite Soldier");
					PVP_AMOUNT4 = Integer.parseInt(Custom.getProperty("PvpAmount4", "190"));
					TITLE_COLOR_FOR_PVP_AMOUNT4 = Integer.decode("0x" + Custom.getProperty("ColorForAmount4", "00FF00"));
					PVP4_TITLE = Custom.getProperty("TitleForAmount4", "Gladiator");
					PVP_AMOUNT5 = Integer.parseInt(Custom.getProperty("PvpAmount5", "290"));
					TITLE_COLOR_FOR_PVP_AMOUNT5 = Integer.decode("0x" + Custom.getProperty("ColorForAmount5", "00FF00"));
					PVP5_TITLE = Custom.getProperty("TitleForAmount5", "Veteran");
					PVP_AMOUNT6 = Integer.parseInt(Custom.getProperty("PvpAmount6", "390"));
					TITLE_COLOR_FOR_PVP_AMOUNT6 = Integer.decode("0x" + Custom.getProperty("ColorForAmount6", "00FF00"));
					PVP6_TITLE = Custom.getProperty("TitleForAmount6", "Champion");
					PVP_AMOUNT7 = Integer.parseInt(Custom.getProperty("PvpAmount7", "500"));
					TITLE_COLOR_FOR_PVP_AMOUNT7 = Integer.decode("0x" + Custom.getProperty("ColorForAmount7", "00FF00"));
					PVP7_TITLE = Custom.getProperty("TitleForAmount7", "Commander");
					PVP_AMOUNT8 = Integer.parseInt(Custom.getProperty("PvpAmount8", "650"));
					TITLE_COLOR_FOR_PVP_AMOUNT8 = Integer.decode("0x" + Custom.getProperty("ColorForAmount8", "00FF00"));
					PVP8_TITLE = Custom.getProperty("TitleForAmount8", "Master");
					PVP_AMOUNT9 = Integer.parseInt(Custom.getProperty("PvpAmount9", "800"));
					TITLE_COLOR_FOR_PVP_AMOUNT9 = Integer.decode("0x" + Custom.getProperty("ColorForAmount9", "00FF00"));
					PVP9_TITLE = Custom.getProperty("TitleForAmount9", "Assassin");
					PVP_AMOUNT10 = Integer.parseInt(Custom.getProperty("PvpAmount10", "1000"));
					TITLE_COLOR_FOR_PVP_AMOUNT10 = Integer.decode("0x" + Custom.getProperty("ColorForAmount10", "00FF00"));
					PVP10_TITLE = Custom.getProperty("TitleForAmount10", "Hero");
					
					RENAME_NPC_ID = Integer.parseInt(Custom.getProperty("RenameNpcID", "50024"));
					RENAME_NPC_MIN_LEVEL = Integer.parseInt(Custom.getProperty("RenameNpcMinLevel", "40"));
					RENAME_NPC_FEE = Custom.getProperty("RenameNpcFee", "57,250000");
					
					DELEVEL_NPC_ID = Integer.parseInt(Custom.getProperty("DelevelNpcID", "77778"));
					DELEVEL_NPC_ENABLE = Boolean.parseBoolean(Custom.getProperty("AllowDelevelNPC", "False"));

					SPAWN_CHAR = Boolean.parseBoolean(Custom.getProperty("CustomSpawn", "false"));
					SPAWN_X = Integer.parseInt(Custom.getProperty("SpawnX", ""));
					SPAWN_Y = Integer.parseInt(Custom.getProperty("SpawnY", ""));
					SPAWN_Z = Integer.parseInt(Custom.getProperty("SpawnZ", ""));
					CONSUME_SPIRIT_SOUL_SHOTS = Boolean.parseBoolean(Custom.getProperty("ConsumeSpiritSoulShots", "True"));
					ALLOW_CUSTOM_ENCHANT_VALUE = Boolean.parseBoolean(Custom.getProperty("AlowCustomEnchantValue", "False"));
					CUSTOM_ENCHANT_VALUE = Integer.parseInt(Custom.getProperty("CustomEnchantValue", "25"));
					
					AUTO_LOOT_INDIVIDUAL = Boolean.parseBoolean(Custom.getProperty("AutoLootIndividual", "False"));
					
					String[] temp = Custom.getProperty("AllowedMultisells", "1:999999").split(":");
					if (temp.length == 0)
					{
						BBS_ALLOWED_MULTISELLS[0] = 1;
						BBS_ALLOWED_MULTISELLS[1] = 999999;
					}
					else if (temp.length == 1)
					{
						BBS_ALLOWED_MULTISELLS[0] = Integer.valueOf(temp[0]);
						BBS_ALLOWED_MULTISELLS[1] = 0;
					}
					else
					{
						BBS_ALLOWED_MULTISELLS[0] = Integer.valueOf(temp[0]);
						BBS_ALLOWED_MULTISELLS[1] = Integer.valueOf(temp[1]);
					}
					
					CLAN_LEADER_COLOR_ENABLED = Boolean.parseBoolean(Custom.getProperty("ClanLeaderNameColorEnabled", "True"));
					CLAN_LEADER_COLOR = Integer.decode("0x" + Custom.getProperty("ClanLeaderColor", "00FFFF"));
					CLAN_LEADER_COLOR_CLAN_LEVEL = Integer.parseInt(Custom.getProperty("ClanLeaderColorAtClanLevel", "1"));
					
					CHAR_TITLE = Boolean.parseBoolean(Custom.getProperty("CharTitle", "False"));
					ADD_CHAR_TITLE = Custom.getProperty("CharAddTitle", "Welcome");
					
					// 	Mutli-Language settings
					//LANGUAGE = MLS.getProperty("Language", "en");
					
					OFFLINE_COLOR_IN_CB = Boolean.parseBoolean(Custom.getProperty("ColorInCbForOffline", "False"));
					LIMIT_SUMMONS_PAILAKA = Boolean.parseBoolean(Custom.getProperty("LimitSummonsPailaka", "False"));
					
					ALT_SHOW_CHAT = Boolean.parseBoolean(Custom.getProperty("AltShowChat", "False"));
					ALLOW_KEYBOARD_MOVEMENT = Boolean.parseBoolean(Custom.getProperty("AllowKeyboardMovement", "False"));
					STORE_TITLE_SIZE = Integer.parseInt(Custom.getProperty("StoreTitleSize", "29"));
					JUMP_OUT_GRAND_BOSS_ZONE_TIME = Integer.parseInt(Custom.getProperty("JumpOutGrandBossZoneTime", "10"));
					
					PING_INTERVAL = Integer.parseInt(Custom.getProperty("PingInterval", "30000"));
					PING_ENABLED = Boolean.parseBoolean(Custom.getProperty("PingEnabled", "false"));
					PING_IGNORED_REQEST_LIMIT = Integer.parseInt(Custom.getProperty("PingIgnoredRequestLimit", "2"));
					
					/**Hopzone vote reward**/
					VOTESYSTEMENABLE = Boolean.parseBoolean(Custom.getProperty("EnableHopzoneVoteReard", "false"));
					WEBSITE_SERVER_LINK = Custom.getProperty("WebsiteServerLink", "link");
					REQUIREDVOTES = Integer.parseInt(Custom.getProperty("RequiredVotesForReward", "100"));
					ITEM_ID = Integer.parseInt(Custom.getProperty("ItemID", "20392"));
					ITEM_COUNT = Integer.parseInt(Custom.getProperty("ItemCount", "5"));
					
					/** Bot report */
					ENABLE_BOTREPORT = Boolean.valueOf(Custom.getProperty("EnableBotReport", "false"));
					MAX_PLAYERS_FROM_ONE_PC = Integer.parseInt(Custom.getProperty("MaxBoxFromSamePc", "2"));
					MAX_PLAYERS_FROM_ONE_PC_VIP = Integer.parseInt(Custom.getProperty("MaxBoxFromSamePcVip", "2"));
					HALLOFSUFFERING_REMOVEBUFF = Boolean.valueOf(Custom.getProperty("HallOfSufferingBuffRemove", "False"));
					MAX_OFFLINE_STORES = Integer.parseInt(Custom.getProperty("MaxOfflineStores", "1"));	
					MAX_OFFLINE_STORES_VIP = Integer.parseInt(Custom.getProperty("MaxOfflineStoresVip", "1"));	
					
					/** Server info*/
					SERVER_NAME = Custom.getProperty("ServerName", "L2 Universe");
					SERVERINFO_NPC_ID = Integer.parseInt(Custom.getProperty("ServerInfoNpcID", "50026"));
					SERVERINFO_NPC_ADM = Custom.getProperty("ServerInfoNpcAdm", "AdmServer").split("\\;");
					SERVERINFO_NPC_GM = Custom.getProperty("ServerInfoNpcGm", "GmServer 01;GmServer 02").split("\\;");
					SERVERINFO_NPC_DESCRIPTION = Custom.getProperty("ServerInfoNpcDescription", "Server description.");
					SERVERINFO_NPC_EMAIL = Custom.getProperty("ServerInfoNpcEmail", "user@user.com");
					SERVERINFO_NPC_PHONE = Custom.getProperty("ServerInfoNpcPhone", "0");
					SERVERINFO_NPC_CUSTOM = Custom.getProperty("ServerInfoNpcCustom", "ame 01;Name 02;Name 03").split("\\;");
					SERVERINFO_NPC_DISABLE_PAGE = Custom.getProperty("ServerInfoNpcDisablePage", "0").split("\\;");
					ENABLED_KILLERS_STATS = Boolean.valueOf(Custom.getProperty("EnableKillerStats", "False"));
					ALT_QUEST_RECIPE_REWARD = Boolean.parseBoolean(Custom.getProperty("AltQuestRecipeReward", "False"));
					
					REMOVE_DANCES_ON_RESTART = Boolean.parseBoolean(Custom.getProperty("RemoveDancesOnRestart", "True"));
					SHOP_MIN_RANGE_FROM_NPC = Integer.parseInt(Custom.getProperty("MinShopRangeFromNpc", "0"));
					SHOP_MIN_RANGE_FROM_PLAYER = Integer.parseInt(Custom.getProperty("MinShopRangeFromPlayer", "0"));
					SPECIAL_GK_NPC_ID = Integer.parseInt(Custom.getProperty("SpecialGkID", "50001"));
					UNIVERSE_ADVNPC = Boolean.parseBoolean(Custom.getProperty("AdvancedAdventureNpc", "false"));
					
					ENABLE_MAIL = Boolean.parseBoolean(Custom.getProperty("EnableMail", "true"));

				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new Error("Failed to Load " + CUSTOM_FILE + " File.");
				}
				
				/** ************************************************** **/
				/**  voice commands					  				  **/
				/** ************************************************** **/
				try
				{
					L2Properties CharacterVoiceCommand = new L2Properties();
					is = new FileInputStream(new File(CHAR_COMMAND));
					CharacterVoiceCommand.load(is);
					ALLOW_EXP_GAIN_COMMAND = Boolean.parseBoolean(CharacterVoiceCommand.getProperty("AllowExpGainCommand", "False"));
					ALLOW_CLAN_LIDER_TELEPORT = Boolean.parseBoolean(CharacterVoiceCommand.getProperty("AllowClanLiderTP", "true"));
					ITEM_ID_CLAN_LIDER_TELEPORT = Integer.parseInt(CharacterVoiceCommand.getProperty("ItemIdClTeleport", "57"));
					COUNT_ITEM_CLAN_LIDER_TELEPORT = Integer.parseInt(CharacterVoiceCommand.getProperty("CountItemClTeleport", "500000"));
					ITEM_ID_CLAN_LIDER_TELEPORT_TO = Integer.parseInt(CharacterVoiceCommand.getProperty("ItemIdToClTeleport", "57"));
					COUNT_ITEM_CLAN_LIDER_TELEPORT_TO = Integer.parseInt(CharacterVoiceCommand.getProperty("CountItemToClTeleport", "50000"));
					ALLOW_CHANGE_PASSWORD = Boolean.parseBoolean(CharacterVoiceCommand.getProperty("AllowChangePassword", "False"));
					ALLOW_REPAIR_CHAR = Boolean.parseBoolean(CharacterVoiceCommand.getProperty("AllowRepairChar", "False"));
					ALLOW_VOICE_BUFF = Boolean.parseBoolean(CharacterVoiceCommand.getProperty("AllowVoiceBuff", "False"));
					SHOW_GB_STATUS = Boolean.parseBoolean(CharacterVoiceCommand.getProperty("ShowGBStatus", "False"));
					HELLBOUND_STATUS = Boolean.parseBoolean(CharacterVoiceCommand.getProperty("HellboundStatus", "false"));
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new Error("Failed to Load " + CHAR_COMMAND + " File.");
				}
					
				/** ************************************************** **/
				/**  Database Properties 				   **/
				/** ************************************************** **/
				try
				{
					L2Properties DatabaseSettingsFiles = new L2Properties();
					is = new FileInputStream(new File(DATABASE_SETTINGS));
					DatabaseSettingsFiles.load(is);
                   	DATABASE_BACKUP_MAKE_BACKUP_ON_STARTUP = Boolean.parseBoolean(DatabaseSettingsFiles.getProperty("DatabaseBackupMakeBackupOnStartup", "False"));
                   	DATABASE_BACKUP_MAKE_BACKUP_ON_SHUTDOWN = Boolean.parseBoolean(DatabaseSettingsFiles.getProperty("DatabaseBackupMakeBackupOnShutdown", "False"));
                   	DATABASE_BACKUP_DATABASE_NAME = DatabaseSettingsFiles.getProperty("DatabaseBackupDatabaseName", "l2jdb");
                   	DATABASE_BACKUP_SAVE_PATH = DatabaseSettingsFiles.getProperty("DatabaseBackupSavePath", "/backup/database/");
                   	DATABASE_BACKUP_COMPRESSION = Boolean.parseBoolean(DatabaseSettingsFiles.getProperty("DatabaseBackupCompression", "True"));
                   	DATABASE_BACKUP_MYSQLDUMP_PATH = DatabaseSettingsFiles.getProperty("DatabaseBackupMysqldumpPath", ".");
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new Error("Failed to Load " + DATABASE_SETTINGS + " File.");
				}
			}
			finally
			{
				try
				{
					is.close();
				}
				catch (Exception e) {}
			}
		}
		else
		{
			_log.severe("Could not Load ExternalConfig: server mode was not set");
		}
	}
	
	/**
	 * Set a new value to a game parameter from the admin console.
	 * @param pName (String) : name of the parameter to change
	 * @param pValue (String) : new value of the parameter
	 * @return boolean : true if modification has been made
	 * @link useAdminCommand
	 */
	public static boolean setParameterValue(String pName, String pValue)
	{
		//	premium service start
		if (pName.equalsIgnoreCase("PremiumRateXp"))
			PREMIUM_RATE_XP = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("PremiumRateSp"))
			PREMIUM_RATE_SP = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("PremiumRateDropAdena"))
			PREMIUM_RATE_DROP_ADENA = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("PremiumRateDropSpoil"))
			PREMIUM_RATE_DROP_SPOIL = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("PremiumRateDropItems"))
			PREMIUM_RATE_DROP_ITEMS = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("AutoLootIndividual"))
			AUTO_LOOT_INDIVIDUAL = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("EnableBotReport"))
			ENABLE_BOTREPORT = Boolean.parseBoolean(pValue);
		//CTF Event
		else if (pName.equalsIgnoreCase("CTFEventEnabled"))
			CTF_EVENT_ENABLED = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("CTFEventInterval"))
			CTF_EVENT_INTERVAL = pValue.split(",");
		else if (pName.equalsIgnoreCase("CTFEvenTeams"))
			CTF_EVEN_TEAMS = pValue;
		else if (pName.equalsIgnoreCase("CTFAllowVoiceCommand"))
			CTF_ALLOW_VOICE_COMMAND = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("CTFAllowInterference"))
			CTF_ALLOW_INTERFERENCE = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("CTFAllowPotions"))
			CTF_ALLOW_POTIONS = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("CTFAllowSummon"))
			CTF_ALLOW_SUMMON = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("CTFOnStartRemoveAllEffects"))
			CTF_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("CTFOnStartUnsummonPet"))
			CTF_ON_START_UNSUMMON_PET = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("CTFReviveDelay"))
			CTF_REVIVE_DELAY = Long.parseLong(pValue);
		//tvt Event
		else if (pName.equalsIgnoreCase("TvTEventEnabled"))
			TVT_EVENT_ENABLED = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("TvTEventInterval"))
			TVT_EVENT_INTERVAL = pValue.split(",");
		else if (pName.equalsIgnoreCase("TvTEventParticipationTime"))
			TVT_EVENT_PARTICIPATION_TIME = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("TvTEventRunningTime"))
			TVT_EVENT_RUNNING_TIME = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("TvTEventParticipationNpcId"))
			TVT_EVENT_PARTICIPATION_NPC_ID = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("MultiBoxesPerPC"))
			MAX_PLAYERS_FROM_ONE_PC = Integer.parseInt(pValue);

		else if (pName.equalsIgnoreCase("DMAllowInterference"))
			DM_ALLOW_INTERFERENCE = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("DMAllowPotions"))
			DM_ALLOW_POTIONS = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("DMAllowSummon"))
			DM_ALLOW_SUMMON = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("DMOnStartRemoveAllEffects"))
			DM_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("DMOnStartUnsummonPet"))
			DM_ON_START_UNSUMMON_PET = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("DMReviveDelay"))
			DM_REVIVE_DELAY = Long.parseLong(pValue);
		
		// Disable PK'ing Low Lvls
		else if (pName.equalsIgnoreCase("DisableAttackIfLvlDifferenceOver")) 
			DISABLE_ATTACK_IF_LVL_DIFFERENCE_OVER = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("PunishPKPlayerIfPKsOver")) 
			PUNISH_PK_PLAYER_IF_PKS_OVER = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("PKMonitorPeriod")) 
			PK_MONITOR_PERIOD = Long.parseLong(pValue);
		else if (pName.equalsIgnoreCase("PKPunishmentType")) 
			PK_PUNISHMENT_TYPE = pValue;
		else if (pName.equalsIgnoreCase("PKPunishmentPeriod")) 
			PK_PUNISHMENT_PERIOD = Long.parseLong(pValue);
		else if (pName.equalsIgnoreCase("ItemID"))
			ITEM_ID = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("ItemCount"))
			ITEM_COUNT = Integer.parseInt(pValue);
		else
			return false;
		return true;
	}
	
	private ExternalConfig()
	{
	}
	
}
