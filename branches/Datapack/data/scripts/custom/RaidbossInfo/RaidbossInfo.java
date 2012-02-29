/*
 * This program is free software you can redistribute it and/or modify it under
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
 * this program. If not, see <http//www.gnu.org/licenses/>.
 */

/*
 * 
 * @author Sephiroth
 */

package custom.RaidbossInfo;

import javolution.util.FastList;

import l2.universe.ExternalConfig;
import l2.universe.gameserver.util.Util;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;

public class RaidbossInfo extends Quest
{
	private FastList<Raidboss> _raidList = new FastList<Raidboss>();

	private final static int[] npcIds =
	{
		31729,31730,31731,31732,31733,31734,31735,31736,31737,31738,31739,
		31740,31741,31742,31743,31744,31745,31746,31747,31748,31749,31750,
		31751,31752,31753,31754,31755,31756,31757,31758,31759,31760,31761,
		31762,31763,31764,31765,31766,31767,31768,31769,31770,31771,31772,
		31773,31774,31775,31776,31777,31778,31779,31780,31781,31782,31783,
		31784,31785,31786,31787,31788,31789,31790,31791,31792,31793,31794,
		31795,31796,31797,31798,31799,31800,31801,31802,31803,31804,31805,
		31806,31807,31808,31809,31810,31811,31812,31813,31814,31815,31816,
		31817,31818,31819,31820,31821,31822,31823,31824,31825,31826,31827,
		31828,31829,31830,31831,31832,31833,31834,31835,31836,31837,31838,
		31839,31840,31841,31842,32337,32338,32339,32340
	};

	public RaidbossInfo(int id, String name, String descr)
	{
		super(id,name,descr);

		for (int i : npcIds)
		{
			addStartNpc(i);
			addTalkId(i);
		}

		init();
	}

	class Raidboss
	{
		private final int _bossId;
		private final int _posX;
		private final int _posY;
		private final int _posZ;
		private String _bossName;

		public Raidboss(int bossId, int posX, int posY, int posZ, String bossName)
		{
			_bossId = bossId;
			_posX = posX;
			_posY = posY;
			_posZ = posZ;
			_bossName = bossName;

		}
		
		public int getBossId() { return _bossId; }
		public int getPosX() { return _posX; }
		public int getPosY() { return _posY; }
		public int getPosZ() { return _posZ; }
		public String getBossName() { return _bossName; }
	}
	
	void init()
	{
		addBoss(25001,-54464,146572,-2400,"Greyclaw Kutus (lv23)");
		addBoss(25019,7352,169433,-3172,"Pan Dryad (lv25)");
		addBoss(25038,-57366,186276,-4804,"Tirak (lv28)");
		addBoss(25060,-60427,188266,-4352,"Unrequited Kael (lv24)");
		addBoss(25076,-61041,127347,-2512,"Princess Molrang (lv25)");
		addBoss(25095,-37799,198120,-2200,"Elf Renoa (lv29)");
		addBoss(25127,-47634,219274,-1936,"Langk Matriarch Rashkos (lv24)");
		addBoss(25146,-13698,213796,-3300,"Evil spirit Bifrons (lv21)");
		addBoss(25149,-12652,138200,-3120,"Zombie Lord Crowl (lv25)");
		addBoss(25166,-21778,152065,-2636,"Ikuntai (lv25)");
		addBoss(25272,49194,127999,-3161,"Partisan Leader Talakin (lv28)");
		addBoss(25357,-3451,112819,-3032,"Sukar Wererat Chief (lv21)");
		addBoss(25360,29064,179362,-3128,"Tiger Hornet (lv26)");
		addBoss(25362,-55791,186903,-2856,"Tracker Leader Sharuk (lv23)");
		addBoss(25365,-62171,190489,-3160,"Patriarch Kuroboros (lv26)");
		addBoss(25366,-62342,179572,-3088,"Kuroboros' Priest (lv23)");
		addBoss(25369,-45713,111186,-3280,"Soul Scavenger (lv25)");
		addBoss(25372,-114915,233080,-1504,"Discarded Guardian (lv20)");
		addBoss(25373,9661,76976,-3652,"Malex, Herald of Dagoniel (lv21)");
		addBoss(25375,22523,80431,-2772,"Zombie Lord Farakelsus (lv20)");
		addBoss(25378,-53970,84334,-3048,"Madness Beast (lv20)");
		addBoss(25380,-47412,51647,-5659,"Kaysha, Herald of Icarus (lv21)");
		addBoss(25426,-18053,-101274,-1580, "Freki, Betrayer of Urutu (lv25)");
		addBoss(25429,172122,-214776,-3064,"Mammon's Collector Talloth (lv25)");
		
		//lvl30 list
		addBoss(25004,-94101,100238,-3012,"Turek Mercenary Captain (lv30)");
		addBoss(25020,90365,125716,-1632,"Breka Warlock Pastu (lv34)");
		addBoss(25023,27181,101830,-3192,"Swamp Stakato Queen Zyrnna (lv34)");
		addBoss(25041,10525,126890,-3132,"Remmel (lv35)");
		addBoss(25063,-91009,116339,-2908,"Chertuba of Great Soul (lv35)");
		addBoss(25079,53794,102660,-529,"Cat's Eye (lv30)");
		addBoss(25082,88554,140646,-2960,"Leader of Cat Gang (lv39)");
		addBoss(25098,123570,133506,-3156,"Sejarr's Servitor (lv35)");
		addBoss(25112,116219,139458,-3124,"Meana, Agent of Beres (lv30)");
		addBoss(25118,50883,146764,-3077,"Guilotine, Warden of the Execution Grounds (lv35)");
		addBoss(25128,17671,179134,-3016,"Vuku Grand Seer Gharmash (lv33)");
		addBoss(25152,43787,124067,-2512,"Flame Lord Shadar (lv35)");
		addBoss(25169,-54517,170321,-2700,"Ragraman (lv30)");
		addBoss(25170,26108,122256,-3488,"Lizardmen Leader Hellion (lv38)");
		addBoss(25185,88143,166365,-3388,"Tasaba Patriarch Hellena (lv35)");
		addBoss(25188,88102,176262,-3012,"Apepi (lv30)");
		addBoss(25189,68677,203149,-3192,"Cronos's Servitor Mumu (lv34)");
		addBoss(25211,76461,193228,-3208,"Sebek (lv36)");
		addBoss(25223,43062,152492,-2294,"Soul Collector Acheron (lv35)");
		addBoss(25352,-16843,174890,-2984,"Giant Wastelands Basilisk (lv30)");
		addBoss(25354,-16089,184295,-3364,"Gargoyle Lord Sirocco (lv35)");
		addBoss(25383,51405,153984,-3008,"Ghost of Sir Calibus (lv34)");
		addBoss(25385,53418,143534,-3332,"Evil Spirit Tempest (lv36)");
		addBoss(25388,40074,102019,-790,"Red Eye Captain Trakia (lv35)");
		addBoss(25391,45620,120710,-2158,"Nurka's Messenger (lv33)");
		addBoss(25392,29891,107201,-3572,"Captain of Queen's Royal Guards (lv32)");
		addBoss(25394,101806,200394,-3180,"Premo Prime (lv38)");
		addBoss(25398,5000,189000,-3728,"Eye of Beleth (lv35)");
		addBoss(25401,117812,102948,-3140,"Skyla (lv32)");
		addBoss(25404,36048,191352,-2524,"Corsair Captain Kylon (lv33)");
		addBoss(25501,48693,-106508,-1247,"Grave Robber Boss Akata (30)");
		addBoss(25504,122771,-141022,-1016,"Nellis' Vengeful Spirit (39)");
		addBoss(25506,127856,-160639,-1080,"Rayito The Looter (37)");
		
		//lvl40 list
		addBoss(25007,124240,75376,-2800,"Retreat Spider Cletu (lv42)");
		addBoss(25026,92976,7920,-3914,"Katu Van Leader Atui (lv49)");
		addBoss(25044,107792,27728,-3488,"Barion (lv47)");
		addBoss(25047,116352,27648,-3319,"Karte (lv49)");
		addBoss(25057,107056,168176,-3456,"Biconne of Blue Sky (lv45)");
		addBoss(25064,92528,84752,-3703,"Mystic of Storm Teruk (lv40)");
		addBoss(25085,66944,67504,-3704,"Timak Orc Chief Ranger (lv44)");
		addBoss(25088,90848,16368,-5296,"Crazy Mechanic Golem (lv43)");
		addBoss(25099,64048,16048,-3536,"Rotten Tree Repiro (lv44)");
		addBoss(25102,113840,84256,-2480,"Shacram (lv45)");
		addBoss(25115,94000,197500,-3300,"Icarus Sample 1 (lv40)");
		addBoss(25134,87536,75872,-3591,"Leto Chief Talkin (lv40)");
		addBoss(25155,73520,66912,-3728,"Shaman King Selu (lv40)");
		addBoss(25158,77104,5408,-3088,"King Tarlk (lv48)");
		addBoss(25173,75968,110784,-2512,"Tiger King Karuta (lv45)");
		addBoss(25192,125920,190208,-3291,"Earth Protector Panathen (lv43)");
		addBoss(25208,73776,201552,-3760,"Water Couatle Ateka (lv40)");
		addBoss(25214,112112,209936,-3616,"Fafurion's Page Sika (lv40)");
		addBoss(25260,93120,19440,-3607,"Iron Giant Totem (lv45)");
		addBoss(25395,15000,119000,-11900,"Archon Suscepter (lv45)");
		addBoss(25410,72192,125424,-3657,"Road Scavenger Leader (lv40)");
		addBoss(25412,81920,113136,-3056,"Necrosentinel Royal Guard (lv47)");
		addBoss(25415,128352,138464,-3467,"Nakondas (lvl40)");
		addBoss(25418,62416,8096,-3376,"Dread Avenger Kraven (lv44)");
		addBoss(25420,42032,24128,-4704,"Orfen's Handmaiden (lv48)");
		addBoss(25431,79648,18320,-5232,"Flame Stone Golem (lv44)");
		addBoss(25437,67296,64128,-3723,"Timak Orc Gosmos (lv45)");
		addBoss(25438,107000,92000,-2272,"Thief Kelbar (lv44)");
		addBoss(25441,111440,82912,-2912,"Evil Spirit Cyrion (lv45)");
		addBoss(25456,133632,87072,-3623,"Mirror of Oblivion (lv49)");
		addBoss(25487,83056,183232,-3616,"Water Spirit Lian (lv40)");
		addBoss(25490,86528,216864,-3584,"Gwindorr (lv40)");
		addBoss(25498,126624,174448,-3056,"Fafurion's Henchman Istary (lv45)");
		
		//lvl50 list
		addBoss(25010,113920,52960,-3735,"Furious Thieles (lv55)");
		addBoss(25013,169744,11920,-2732,"Spiteful Soul of Peasant Leader (lv50)");
		addBoss(25029,54941,206705,-3728,"Atraiban (lv53)");
		addBoss(25032,88532,245798,-10376,"Eva's Guardian Millenu (58)");
		addBoss(25050,125520,27216,-3632,"Verfa (lv51)");
		addBoss(25067,94992,-23168,-2176,"Shaka, Captain of the Red Flag (lv52)");
		addBoss(25070,125600,50100,-3600,"Enchanted Forest Watcher Ruell (lv55)");
		addBoss(25089,165424,93776,-2992,"Soulless Wild Boar (lv59)");
		addBoss(25103,135872,94592,-3735,"Sorcerer Isirr (lv55)");
		addBoss(25119,121872,64032,-3536,"Berun, Messenger of the Fairy Queen (lv50)");
		addBoss(25122,86300,-8200,-3000,"Hopeful Refugee Leo (lv56)");
		addBoss(25131,75488,-9360,-2720,"Carnage Lord Gato (lv50)");
		addBoss(25137,125280,102576,-3305,"Sephia, Seer of Bereth (lv55)");
		addBoss(25159,124984,43200,-3625,"Unicorn Paniel (lv54)");
		addBoss(25176,92544,115232,-3200,"Black Lily (55)");
		addBoss(25182,41966,215417,-3728,"Demon Kurikups (59)");
		addBoss(25217,89904,105712,-3292,"Cursed Clara (lv50)");
		addBoss(25230,66672,46704,-3920,"Ragoth, Seer of Timak (lv57)");
		addBoss(25238,155000,85400,-3200,"Abyss Brukunt (59)");
		addBoss(25241,165984,88048,-2384,"Harit Hero Tamash (lv55)");
		addBoss(25259,42050,208107,-3752,"Zaken's Butcher Krantz (lv55)");
		addBoss(25273,23800,119500,-8976,"Carnamakos (50)");
		addBoss(25277,54651,180269,-4976,"Lilith's Witch Marilion (lv50)");
		addBoss(25280,85622,88766,-5120,"Pagan Watcher Cerberon (lv55)");
		addBoss(25434,104096,-16896,-1803,"Bandit Leader Barda (lv55)");
		addBoss(25460,150304,67776,-3688,"Deadman Ereve (lv51)");
		addBoss(25463,166288,68096,-3264,"Harit Guardian Garangky (lv56)");
		addBoss(25473,175712,29856,-3776,"Grave Robber Kim (lv52)");
		addBoss(25475,183568,24560,-3184,"Ghost Knight Kabed (lv55)");
		addBoss(25481,53517,205413,-3728,"Magus Kenishee (lv53)");
		addBoss(25484,3160,220463,-3680,"Zaken's Mate Tillion (lv50)");
		addBoss(25493,3174,254428,-10873,"Eva's Spirit Niniel (lv55)");
		addBoss(25496,8300,258000,-10200,"Fafurion's Envoy Pingolpin (lv52)");
		//25509(,,,),				 //Dark Shaman Varangka (53) - not spawned yet
		//25512(,,,),				 //Gigantic Chaos Golem (52) - not spawned yet
		//29060(,,,),				 //Captain Of The Ice Queen's Royal Guard (59) - not spawned yet
		
		//lvl60 list
		addBoss(25016,6787,245775,-10376,"The 3rd Underwater Guardian (lv60)");
		addBoss(25051,7760,-9072,-3264,"Rahha (lv65)");
		addBoss(25073,43265,110044,-3944,"Bloody Priest Rudelto (lv69)");
		addBoss(25106,73880,-11412,-2880,"Lidia, Ghost of the Well (lv60)");
		addBoss(25125,70656,85184,-2000,"Fierce Tiger King Angel (lv65)");
		addBoss(25140,191975,56959,-7616,"Hekaton Prime (lv65)");
		addBoss(25162,194107,53884,-4368,"Giant Marpanak (lv60)");
		addBoss(25179,181814,52379,-4344,"Karum, Guardian Of The Statue Of the Giant (60)");
		addBoss(25226,104240,-3664,-3392,"Roaring Lord Kastor (lv62)");
		addBoss(25233,185800,-26500,-2000,"Spiteful Soul of Andras the Betrayer (lv69)");
		addBoss(25234,120080,111248,-3047,"Ancient Weird Drake (lv65)");
		addBoss(25255,170048,-24896,-3440,"Gargoyle Lord Tiphon (lv65)");
		addBoss(25256,170320,42640,-4832,"Taik High Prefect Arak (lv60)");
		addBoss(25263,144400,-28192,-1920,"Kernon's Faithful Servant Kelone (67)");
		addBoss(25322,93296,-75104,-1824,"Demon's Agent Falston (lv66)");
		addBoss(25407,115072,112272,-3018,"Lord Ishka (lv60)");
		addBoss(25423,113600,47120,-4640,"Fairy Queen Timiniel (61)");
		addBoss(25444,13232,17456,-4384,"Enmity Ghost Ramdal (lv65)");
		addBoss(254671,6192,61472,-4160,"Gorgolos (lv64)");
		addBoss(25470,86896,56276,-4576,"Utenus, the Last Titan (lv66)");
		addBoss(25478,68288,28368,-3632,"Hisilrome, Priest of Shilen (lv65)");
		//29056(,,,),				 //Ice Fairy Sirra (60) - not spawned yet
		
		//lvl70 list
		addBoss(25035,180968,12035,-2720,"Shilen's Messenger Cabrio (lv70)");
		addBoss(25054,113432,16403,3960,"Kernon (lv75)");
		addBoss(25092,116151,16227,1944,"Korim (lv70)");
		addBoss(25109,152660,110387,-5520,"Cloe, Priest of Antharas (lv74)");
		addBoss(25126,116263,15916,6992,"Longhorn Golkonda (lv79)");
		addBoss(25143,113102,16002,6992,"Shuriel, Fire of Wrath (lv78)");
		addBoss(25163,130500,59098,3584,"Roaring Skylancer (lv70)");
		addBoss(25198,102656,157424,-3735,"Fafurion's Messenger Loch Ness (lv70)");
		addBoss(25199,108096,157408,-3688,"Fafurion's Seer Sheshark (lv72)");
		addBoss(25202,119760,157392,-3744,"Crokian Padisha Sobekk (lv74)");
		addBoss(25205,123808,153408,-3671,"Ocean's Flame Ashakiel (lv76)");
		addBoss(25220,113551,17083,-2120,"Death Lord Hallate (lv73)");
		addBoss(25229,137568,-19488,-3552,"Storm Winged Naga (lv75)");
		addBoss(25235,116400,-62528,-3264,"Vanor Chief Kandra (lv72)");
		addBoss(25244,187360,45840,-5856,"Last Lesser Giant Olkuth (lv75)");
		addBoss(25245,172000,55000,-5400,"Last Lesser Giant Glaki (lv78)");
		addBoss(25248,127903,-13399,-3720,"Doom Blade Tanatos (lv72)");
		addBoss(25249,147104,-20560,-3377,"Palatanos of the Fearsome Power (lv75)");
		addBoss(25252,192376,22087,-3608,"Palibati Queen Themis (lv70)");
		addBoss(25266,188983,13647,-2672,"Bloody Empress Decarbia (lv75)");
		addBoss(25269,123504,-23696,-3481,"Beast Lord Behemoth (lv70)");
		addBoss(25276,154088,-14116,-3736,"Death Lord Ipos (lv75)");
		addBoss(25281,151053,88124,-5424,"Anakim's Nemesis Zakaron (lv70)");
		addBoss(25282,179311,-7632,-4896,"Death Lord Shax (lv75)");
		addBoss(25293,134672,-115600,-1216,"Hestia, Guardian Deity of the Hot Springs (lv78)");
		addBoss(25325,91008,-85904,-2736,"Barakiel, the Flame of Splendor (lv70)");
		addBoss(25328,59331,-42403,-3003,"Eilhalder Von Hellman (lv71)");
		addBoss(25447,113200,17552,-1424,"Immortal Savior Mardil (lv71)");
		addBoss(25450,113600,15104,9559,"Cherub Galaxia (lv79)");
		addBoss(25453,156704,-6096,-4185,"Minas Anor (lv70)");

		//25523(,,,),				 //Plague Golem (lvl73) - not spawned yet

		addBoss(25524,144143,-5731,-4722,"Flamestone Giant (lvl76)");

		//25296(,,,),				 //Icicle Emperor Bumpalump (lvl74) - quest spawn - not spawned yet
		//25290(,,,),				 //Daimon The White-Eyed (lvl78) - quest spawn - not spawned yet
		
		//lvl80 list
		addBoss(25283,185060,-9622,-5104,"Lilith (lvl80)");
		addBoss(25286,185065,-12612,-5104,"Anakim (lvl80)");
		addBoss(25299,148154,-73782,-4364,"Ketra's Hero Hekaton (lvl80)");
		addBoss(25302,145553,-81651,-5464,"Ketra's Commander Tayr (lvl80)");
		addBoss(25305,144997,-84948,-5712,"Ketra's Chief Brakki (lvl80)");
		addBoss(25306,142368,-82512,-6487,"Soul of Fire Nastron (lvl80)");
		addBoss(25309,115537,-39046,-1940,"Varka's Hero Shadith (lvl80)");
		addBoss(25312,109296,-36103,-648,"Varks's Commander Mos (lvl80)");
		addBoss(25315,105654,-42995,-1240,"Varka's Chief Horus (lvl80)");
		addBoss(25316,105452,-36775,-1050,"Soul of Water Ashutar (lvl80)");
		addBoss(25319,185700,-106066,-6184,"Ember (lvl80)");
		addBoss(25514,79635,-55612,-5980,"Queen Shyeed (lvl80)");
		addBoss(25517,112793,-76080,286,"Unkown (lvl80)");
		addBoss(25527,3776,-6768,-3276,"Uruka (lvl80");
		addBoss(25539,-17475,253163,-3432,"Typhoon (lvl81)");
		addBoss(25623,192361,254528,1598,"Valdstone (lvl80)");
		addBoss(25624,-174600,219711,4424,"Rok (lvl80)");
		addBoss(25625,-181989,208968,4424,"Enira (lvl(80)");
		//addBoss(25626,-252898,235845,5343,"Dius (lvl80)"); // Review this one, table shows he is a L2Monster...
		addBoss(29062,-16373,-53562,-10197,"Andreas Van Halter (lvl80)");
		addBoss(29065,26528,-8244,-2007,"Sailren (lvl80)");
	}

	private void addBoss(int bossId, int posX, int posY, int posZ, String bossName)
	{
		Raidboss rb = new Raidboss(bossId, posX, posY, posZ, bossName);
		_raidList.add(rb);
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		if (ExternalConfig.UNIVERSE_ADVNPC || player.isGM())
		{
			if (player.isNoble() || player.isGM())
				return "info-noble.htm";
			else
				return "info.htm";
		}
		else
			return "info.htm";
	}

	@Override
	public String onAdvEvent (String event, L2Npc npc, L2PcInstance player)
	{
		Raidboss rb = null;
		if (event.startsWith("level"))
			return event;

		if (Util.isDigit(event))
		{
			rb = getRaidBoss(Integer.valueOf(event));
			player.getRadar().addMarker(rb.getPosX(), rb.getPosY(), rb.getPosZ());
		}
		else
		{
			String[] temp = event.split(" ");
			rb = getRaidBoss(Integer.valueOf(temp[0]));
			player.enterObserverMode(rb.getPosX(), rb.getPosY(), rb.getPosZ());
			event = "Peeking " + rb.getBossName();
		}
		return super.onAdvEvent(event, npc, player);
	}
	private Raidboss getRaidBoss(int bossId)
	{
		for (Raidboss _rb : _raidList)
		{
			if (_rb.getBossId() == bossId)
				return _rb;
		}
		return null;
	}

	public static void main(String[] args)
	{
		new RaidbossInfo(-1, "RaidbossInfo", "custom");
	}
}
