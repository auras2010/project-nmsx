/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package custom.GrandWedding;

import java.util.Collection;
import javolution.util.FastList;

import l2.universe.gameserver.Announcements;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.instancemanager.CoupleManager;
import l2.universe.gameserver.model.L2CharPosition;
import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.Couple;
import l2.universe.gameserver.model.itemcontainer.Inventory;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.network.serverpackets.CreatureSay;
import l2.universe.gameserver.network.serverpackets.PlaySound;
import l2.universe.gameserver.network.serverpackets.SpecialCamera;
import l2.universe.gameserver.network.serverpackets.SocialAction;
import l2.universe.util.Rnd;

public class GrandWedding extends Quest
{
	private static int GIFT = 889; // Reward ID: tateossian ring
	private static int NEEDED_ADENA = 100000000; // need 100.000.000 Adena
	private static int REQUIRED = 5283; // ID of required item (rice cake)
	private static int QTY_REQUIRED = 20; // Qty of required items
	
	private static int[] Gourd = { 102504, 102513 };
	private static int[] entertainmentId = { 102501, 102511, 102512 };
	private static int pixyId = 102500;
	private static int[] specialGuests = { 102517, 102518, 102519, 102520, 102521, 102522 };
	private static int[] NPCS = { 102502, 102509, 102510 };
	
	private static int Point1X = -51480;
	private static int Point1Y = -54091;
	private static int Point1Z = -2808;
	private static int head1 = 15308;
	private static int Point2X = -51480;
	private static int Point2Y = -54242;
	private static int head2 = 48643;
	
	private static int[] numberGuards = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	
	private static int WeddingLocked = 0;
	private static int WeddingStep = 0;
	private static L2Npc giftBox;
	private static L2PcInstance husband;
	private static L2PcInstance wife;
	private static boolean husbandOk = false;
	private static boolean wifeOk = false;
	private static int HusbandCoupleId = 0;
	private static int WifeCoupleId = 0;
	private static L2Npc Anakim;
	private static FastList<L2Npc> WeddingManagers = new FastList<L2Npc>();
	private static Collection<L2PcInstance> players = null;
	private static FastList<L2Npc> guards = new FastList<L2Npc>();
	private static FastList<L2PcInstance> WeddingList = new FastList<L2PcInstance>();
	private static FastList<L2Npc> guests = new FastList<L2Npc>();
	private static FastList<L2Npc> pixies = new FastList<L2Npc>();
	private static FastList<L2Npc> entertainment = new FastList<L2Npc>();
	private static FastList<L2Npc> entertainment2 = new FastList<L2Npc>();
	private static FastList<L2Npc> gourds = new FastList<L2Npc>();
	private static L2Npc pet1;
	private static L2Npc pet2;
	
	private static String htmltext_01 = "<html><body>Good luck!<br><br>And congratulations again from the married couple!!!</body></html>";
	private static String htmltext_02 = "<html><body>Only the married couple can get the reward!!!</body></html>";
	private static String htmltext_03 = "<html><body>You need to be engaged to get married!</body></html>";
	private static String htmltext_04 = "<html><body>You need to be wearing formal wear to get married!</body></html>";
	private static String htmltext_05 = "<html><body>You need to be level 60 or more to use this service.</body></html>";
	private static String htmltext_06 = "<html><body>You are late, please be quiet during the ceremony.</body></html>";
	private static String htmltext_07 = "<html><body>It is not your turn to speak.</body></html>";
	private static String htmltext_08 = "<html><body>What do you think you are doing interfering in the wedding?</body></html>";
	private static String htmltext_09 = "<html><body>You do not have enough items.</body></html>";
	private static String htmltext_10 = "<html><body>Another Wedding request is already in progress, you need to wait until either that wedding is completed or until the 2 minutes allowed for the answer is over.</body></html>";
	private static String htmltext_11 = "<html><body>You are already married!  What are you doing talking to me?</body></html>";
	private static String htmltext_12 = "<html><body>You are now on the Wedding guest list, you will be teleported 1 minute before the beginning of the ceremony.</body></html>";
	private static String htmltext_13 = "<html><body>You are the one getting married!!! You cannot be a guest too! ;)</body></html>";
	private static String htmltext_14 = "<html><body>You are already on the Wedding guest list.</body></html>";
	
	private static String text_01 = "Your bride has 2 minutes to give an answer or the ceremony will be cancelled.";
	private static String text_02 = "I will now announce your wedding so that guests can register to come see it.  Congratulations!  The wedding ceremony will start in 5 minutes.";
	private static String text_03 = "Your groom has 2 minutes to give an answer or the ceremony will be cancelled.";
	private static String text_04 = "The wedding request is cancelled.  Another couple may now ask to get married.";
	private static String text_05 = "The Grand Wedding to celebrate ";
	private static String text_06 = " and ";
	private static String text_07 = "'s union will start in 5 minutes.  All those who want to come to see this great ceremony, please register with a Grand Wedding Manager in Giran, Aden or Goddard.";
	private static String text_08 = "Players on the Wedding guest list will now be teleported to the Ceremony's location.  Once there the ceremony will start in 1 minute.";
	private static String text_09 = "You will now be paralyzed until we are ready for your vows.  This is a necessary mesure :)";
	private static String text_10 = "Please stay quiet during the preparation of the ceremony";
	private static String text_11 = "So... where are you my lovely pets?...";
	private static String text_12 = "Oh, here you are!";
	private static String text_13 = "Hmmm... I think we are missing some guests...";
	private static String text_14 = "Oh, I see them coming! ^^";
	private static String text_15 = "Anyone else coming?...";
	private static String text_16 = "The entertainment staff were supposed to be here too...";
	private static String text_17 = "Ok, now we can start this wedding";
	private static String text_18 = "But some more guests were supposed to be here too... oh, I see them now!";
	private static String text_19 = "Could ";
	private static String text_20 = " please come to me so we can start the ceremony.";
	private static String text_21 = "Now lets start this ceremony...  ";
	private static String text_22 = " do you take ";
	private static String text_23 = " as your game wife and promise to protect her from PKers and to help her in game whenever necessary?";
	private static String text_24 = "And you, ";
	private static String text_25 = ", do you promise to help and protect ";
	private static String text_26 = " when necessary, and to be there by his side when he needs you?";
	private static String text_27 = "By the powers given to me, I now pronounce you two married.";
	private static String text_28 = "And now a wedding present from the Server Team, enjoy!!";
	
	public GrandWedding(int questId, String name, String descr)
	{
		super(questId, name, descr);
		WeddingManagers.add(addSpawn(102510, 146364, 27322, -2205, 62980, false, 0));
		WeddingManagers.add(addSpawn(102510, 147982, -56568, -2781, 20998, false, 0));
		WeddingManagers.add(addSpawn(102510, 83486, 149328, -3405, 47599, false, 0));
		for (final int i : NPCS)
		{
			addStartNpc(i);
			addTalkId(i);
		}
	}
	
	public static void main(String[] args)
	{
		// now call the constructor (starts up the)
		new GrandWedding(-1, "GrandWedding", "custom");
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final int npcId = npc.getNpcId();
		String htmltext = "<html><body>:(((</body></html>";
		
		if (player.getQuestState("GrandWedding") == null)
			return "";
		
		if (npcId == 102502)
		{
			if (player == husband || player == wife)
			{
				htmltext = htmltext_01;
				husband.getQuestState("GrandWedding").giveItems(GIFT, 1);
				wife.getQuestState("GrandWedding").giveItems(GIFT, 1);
				// L2Npc lilith = addSpawn(25283, -51772, -54523, -2825, 0,
				// false, 0);
				husband = null;
				wife = null;
				giftBox.deleteMe();
			}
			else
				htmltext = htmltext_02;
		}
		else if (npcId == 102510)
		{
			final int level = player.getLevel();
			switch (WeddingLocked)
			{
				case 0:
					if (level >= 60)
					{
                        if (player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST) != null)
                        {
                            final int items = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST).getItemId();
                            if (player.getCoupleId() == 0)
                                htmltext = htmltext_03;
						                             else
                            {
                                if (items == 6408)
                                    htmltext = "EngageRequest.htm";
                                else
                                    htmltext = htmltext_04;
                            }
                        }
                        else
                            htmltext = htmltext_04;
					}
					else
						htmltext = htmltext_05;
					break;
				case 1:
					htmltext = "WeddingList.htm";
					break;
				case 2:
					player.teleToLocation(-51848, -54165, -2826);
					htmltext = htmltext_06;
					break;
			}
		}
		else if (npcId == 102509)
		{
			if (player == husband || player.getName().equals(husband.getName()))
			{
				if (WeddingStep == 1)
					htmltext = "1.htm";
				else
					htmltext = htmltext_07;
			}
			else if (player == wife || player.getName().equals(wife.getName()))
			{
				if (WeddingStep == 2)
					htmltext = "2.htm";
				else
					htmltext = htmltext_07;
			}
			else
				htmltext = htmltext_08;
		}
		
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		int xx;
		int yy;
		int zz;
		int x1;
		int y1;
		int x2;
		int y2;
		
		if (event.equals("EngageRequest"))
		{
			final boolean sex = player.getAppearance().getSex();
			final boolean married = player.isMarried();
			if (!married)
			{
				if (sex == false)
				{
					if (husbandOk == false)
					{
						if (HusbandCoupleId == 0 && WifeCoupleId == 0)
						{
							if (player.getQuestState("GrandWedding").getQuestItemsCount(REQUIRED) >= QTY_REQUIRED && player.getQuestState("GrandWedding").getQuestItemsCount(57) >= NEEDED_ADENA)
							{
								WeddingList.clear();
								player.getQuestState("GrandWedding").takeItems(REQUIRED, QTY_REQUIRED);
								player.getQuestState("GrandWedding").takeItems(57, NEEDED_ADENA);
								husband = player;
								husbandOk = true;
								HusbandCoupleId = player.getCoupleId();
								startQuestTimer("WeddingAnswer", 120000, null, null);
								for (final L2Npc i : WeddingManagers)
								{
									players = i.getKnownList().getKnownPlayers().values();
									if (players.contains(husband))
										i.broadcastPacket(new CreatureSay(i.getNpcId(), 0, "Wedding Manager", text_01));
								}
							}
							else
								return htmltext_09;
						}
						if (WifeCoupleId == player.getCoupleId())
						{
							husband = player;
							husbandOk = true;
							HusbandCoupleId = player.getCoupleId();
							WeddingLocked = 1;
							cancelQuestTimer("WeddingAnswer", null, null);
							startQuestTimer("WeddingAnnounce", 10000, null, null);
							for (final L2Npc i : WeddingManagers)
							{
								players = i.getKnownList().getKnownPlayers().values();
								if (players.contains(husband))
									i.broadcastPacket(new CreatureSay(i.getNpcId(), 0, "Wedding Manager", text_02));
							}
						}
						if (husband != player)
							return htmltext_10;
					}
					else
						return htmltext_10;
				}
				else if (sex == true)
				{
					if (wifeOk == false)
					{
						if (HusbandCoupleId == 0 && WifeCoupleId == 0)
						{
							if (player.getQuestState("GrandWedding").getQuestItemsCount(REQUIRED) >= QTY_REQUIRED && player.getQuestState("GrandWedding").getQuestItemsCount(57) >= NEEDED_ADENA)
							{
								WeddingList.clear();
								player.getQuestState("GrandWedding").takeItems(REQUIRED, QTY_REQUIRED);
								player.getQuestState("GrandWedding").takeItems(57, NEEDED_ADENA);
								wife = player;
								wifeOk = true;
								WifeCoupleId = player.getCoupleId();
								cancelQuestTimer("WeddingAnswer", null, null);
								startQuestTimer("WeddingAnswer", 120000, null, null);
								for (final L2Npc i : WeddingManagers)
								{
									players = i.getKnownList().getKnownPlayers().values();
									if (players.contains(wife))
										i.broadcastPacket(new CreatureSay(i.getNpcId(), 0, "Wedding Manager", text_03));
								}
							}
							else
								return htmltext_09;
						}
						if (HusbandCoupleId == player.getCoupleId())
						{
							wife = player;
							wifeOk = true;
							WifeCoupleId = player.getCoupleId();
							WeddingLocked = 1;
							cancelQuestTimer("WeddingAnswer", null, null);
							startQuestTimer("WeddingAnnounce", 10000, null, null);
							for (final L2Npc i : WeddingManagers)
							{
								players = i.getKnownList().getKnownPlayers().values();
								if (players.contains(wife))
									i.broadcastPacket(new CreatureSay(i.getNpcId(), 0, "Wedding Manager", text_02));
							}
						}
						if (wife != player)
							return htmltext_10;
						
					}
					else
						return htmltext_10;
				}
			}
			else
				return htmltext_11;
		}
		if (event.equals("WeddingAnswer"))
		{
			husbandOk = false;
			wifeOk = false;
			husband = null;
			wife = null;
			HusbandCoupleId = 0;
			WifeCoupleId = 0;
			WeddingLocked = 0;
			WeddingStep = 0;
			for (final L2Npc i : WeddingManagers)
				i.broadcastPacket(new CreatureSay(i.getNpcId(), 0, "Wedding Manager", text_04));
		}
		if (event.equals("WeddingAnnounce"))
		{
			final String Announcestart = text_05 + husband.getName() + text_06 + wife.getName() + text_07;
			Announcements.getInstance().announceToAll(Announcestart);
			startQuestTimer("WeddingTeleportAnnounce", 285000, null, null);
			startQuestTimer("WeddingTeleport", 300000, null, null);
		}
		if (event.equals("WeddingList"))
		{
			if (!WeddingList.contains(player))
			{
				if (player != wife && player != husband)
				{
					WeddingList.add(player);
					return htmltext_12;
				}
				else
					return htmltext_13;
			}
			else
				return htmltext_14;
		}
		if (event.equals("WeddingTeleportAnnounce"))
		{
			Announcements.getInstance().announceToAll(text_08);
		}
		if (event.equals("WeddingTeleport"))
		{
			xx = 0;
			yy = 0;
			
			WeddingLocked = 2;
			husband.teleToLocation(-51659, -54137, -2820);
			husband.sendMessage(text_09);
			husband.setIsParalyzed(true);
			wife.teleToLocation(-51659, -54194, -2819);
			wife.sendMessage(text_09);
			wife.setIsParalyzed(true);
			if (WeddingList.size() > 0)
			{
				for (final L2PcInstance i : WeddingList)
				{
					xx = -51848 + (Rnd.get(100) - 50);
					yy = -54165 + (Rnd.get(100) - 50);
					i.teleToLocation(xx, yy, -2826);
				}
			}
			startQuestTimer("WeddingGuardsSpawn", 60000, null, null);
		}
		if (event.equals("WeddingGuardsSpawn"))
		{
			L2Npc guard = null;
			int val = 1;
			guards.clear();
			y1 = Point1Y;
			y2 = Point2Y;
			x1 = Point1X;
			x2 = Point2X;
			for (int i = 0; i < numberGuards.length; i++)
			{
				x1 = x1 + val;
				x2 = x2 + val;
				guard = addSpawn(102503, x1, y1, Point1Z, head1, false, 0);
				guards.add(guard);
				guard = addSpawn(102503, x2, y2, Point1Z, head2, false, 0);
				guards.add(guard);
				val = 80;
			}
			startQuestTimer("guardsPart2", 6000, null, null);
		}
		if (event.equals("guardsPart2"))
		{
			zz = guards.get(0).getZ();
			for (int i = 0; i < guards.size(); i += 2)
			{
				final int xx1 = guards.get(i).getX();
				final int yy1 = guards.get(i).getY() - 30;
				final int xx2 = guards.get(i + 1).getX();
				final int yy2 = guards.get(i + 1).getY() + 30;
				guards.get(i).getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(xx1, yy1, zz, 0));
				guards.get(i + 1).getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(xx2, yy2, zz, 0));
			}
			startQuestTimer("guardsPart3", 2500, null, null);
		}
		if (event.equals("guardsPart3"))
		{
			for (final L2Npc i : guards)
				i.broadcastPacket(new SocialAction(i.getObjectId(), 2));
			
			startQuestTimer("AnakimSpawn", 2000, null, null);
		}
		if (event.equals("AnakimSpawn"))
		{
			Anakim = addSpawn(102509, -52241, -54176, -2827, 0, false, 0);
			startQuestTimer("AnakimSpeak", 100, null, null);
		}
		if (event.equals("AnakimSpeak"))
		{
			Anakim.broadcastPacket(new CreatureSay(Anakim.getNpcId(), 0, "Anakim", text_10));
			Anakim.broadcastPacket(new SpecialCamera(Anakim.getObjectId(), 200, 0, 150, 0, 5000));
			startQuestTimer("AnakimAnim", 1000, null, null);
			startQuestTimer("AnakimPets", 8000, null, null);
		}
		if (event.equals("AnakimAnim"))
		{
			Anakim.broadcastPacket(new SocialAction(Anakim.getObjectId(), 2));
		}
		if (event.equals("AnakimPets"))
		{
			Anakim.broadcastPacket(new CreatureSay(Anakim.getNpcId(), 0, "Anakim", text_11));
			Anakim.broadcastPacket(new CreatureSay(Anakim.getNpcId(), 0, "Anakim", text_12));
			pet1 = addSpawn(102514, -52241, -54146, -2827, 0, false, 0);
			pet2 = addSpawn(102514, -52241, -54206, -2827, 0, false, 0);
			startQuestTimer("AnakimWalk", 3000, null, null);
		}
		if (event.equals("AnakimWalk"))
		{
			Anakim.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(-49877, -54168, -2688, 0));
			startQuestTimer("petsWalk", 1500, null, null);
		}
		if (event.equals("petsWalk"))
		{
			pet1.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(-49896, -54116, -2688, 0));
			pet2.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(-49896, -54220, -2688, 0));
			startQuestTimer("AnakimHeading", 27000, null, null);
			Anakim.broadcastPacket(new SpecialCamera(Anakim.getObjectId(), 400, 180, 150, 0, 31500));
		}
		if (event.equals("AnakimHeading"))
		{
			Anakim.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(-49984, -54168, -2688, 0));
			pet2.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(-49976, -54241, -2688, 0));
			startQuestTimer("petsHeading", 100, null, null);
			startQuestTimer("witnessSpawn", 500, null, null);
			startQuestTimer("AnakimSpeak2", 3000, null, null);
		}
		if (event.equals("petsHeading"))
		{
			pet1.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(-49976, -54104, -2688, 0));
		}
		if (event.equals("witnessSpawn"))
		{
			L2Npc witness = addSpawn(102508, -50034, -54068, -2688, head2, false, 0);
			guards.add(witness);
			witness = addSpawn(102507, -50034, -54268, -2688, head1, false, 0);
			guards.add(witness);
		}
		if (event.equals("AnakimSpeak2"))
		{
			Anakim.broadcastPacket(new CreatureSay(Anakim.getNpcId(), 0, "Anakim", text_13));
			startQuestTimer("AnakimSpeak3", 2000, null, null);
		}
		if (event.equals("AnakimSpeak3"))
		{
			Anakim.broadcastPacket(new CreatureSay(Anakim.getNpcId(), 0, "Anakim", text_14));
			startQuestTimer("PixiesSpawn", 1000, null, null);
			startQuestTimer("PixiesCamera", 10500, null, null);
		}
		if (event.equals("PixiesSpawn"))
		{
			for (int i = 0; i < 45; i++)
			{
				xx = -51910 + (Rnd.get(120) - 60);
				yy = -54985 + (Rnd.get(120) - 60);
				final L2Npc pixy = addSpawn(pixyId, xx, yy, -2824, 0, false, 0);
				pixy.setRunning();
				pixies.add(pixy);
			}
			startQuestTimer("pixiesMove1", 9000, null, null);
		}
		if (event.equals("PixiesCamera"))
		{
			pixies.get(0).broadcastPacket(new SpecialCamera(pixies.get(0).getObjectId(), 400, 180, 150, 0, 14000));
		}
		if (event.equals("pixiesMove1"))
		{
			for (final L2Npc i : pixies)
			{
				xx = -51433 + (Rnd.get(250) - 125);
				yy = -54725 + (Rnd.get(250) - 125);
				i.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(xx, yy, -2827, 0));
			}
			startQuestTimer("pixiesMove2", 3000, null, null);
		}
		if (event.equals("pixiesMove2"))
		{
			for (final L2Npc i : pixies)
			{
				xx = -51848 + (Rnd.get(60) - 30);
				yy = -54165 + (Rnd.get(60) - 30);
				i.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(xx, yy, -2826, 0));
			}
			startQuestTimer("pixiesMove3", 2500, null, null);
		}
		if (event.equals("pixiesMove3"))
		{
			for (final L2Npc i : pixies)
			{
				xx = -51228 + (Rnd.get(1200) - 600);
				yy = -54178 + (Rnd.get(1200) - 600);
				i.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(xx, yy, -2809, 0));
			}
			startQuestTimer("AnakimSpeak4", 5000, null, null);
		}
		if (event.equals("AnakimSpeak4"))
		{
			Anakim.broadcastPacket(new CreatureSay(Anakim.getNpcId(), 0, "Anakim", text_15));
			Anakim.broadcastPacket(new CreatureSay(Anakim.getNpcId(), 0, "Anakim", text_16));
			startQuestTimer("entertainmentSpawn", 1000, null, null);
		}
		if (event.equals("entertainmentSpawn"))
		{
			for (int i = 0; i < 24; i++)
			{
				final int rr = Rnd.get(3);
				xx = -53714 + (Rnd.get(150) - 75);
				yy = -54142 + (Rnd.get(150) - 75);
				final L2Npc show = addSpawn(entertainmentId[rr], xx, yy, -2674, 0, false, 0);
				show.setRunning();
				entertainment.add(show);
			}
			startQuestTimer("entertainmentMove", 4000, null, null);
		}
		if (event.equals("showCamera"))
		{
			entertainment.get(0).broadcastPacket(new SpecialCamera(entertainment.get(0).getObjectId(), 400, 180, 150, 0, 20000));
		}
		if (event.equals("entertainmentMove"))
		{
			for (final L2Npc i : entertainment)
			{
				xx = -52083 + (Rnd.get(100) - 50);
				yy = -54117 + (Rnd.get(100) - 50);
				i.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(xx, yy, -2826, 0));
			}
			startQuestTimer("entertainmentMove2", 10500, null, null);
		}
		if (event.equals("entertainmentMove2"))
		{
			for (final L2Npc i : entertainment)
			{
				xx = -51770 + (Rnd.get(220) - 110);
				yy = -54863 + (Rnd.get(220) - 110);
				i.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(xx, yy, -2825, 0));
			}
			for (final L2Npc show : entertainment)
				show.setWalking();
			
			startQuestTimer("showCamera", 100, null, null);
			startQuestTimer("entertainmentMove3", 10500, null, null);
		}
		if (event.equals("entertainmentMove3"))
		{
			for (final L2Npc i : entertainment)
			{
				xx = -51150 + (Rnd.get(200) - 100);
				yy = -54511 + (Rnd.get(200) - 100);
				i.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(xx, yy, -2825, 0));
			}
			startQuestTimer("AnakimSpeak5", 10000, null, null);
		}
		if (event.equals("AnakimSpeak5"))
		{
			Anakim.broadcastPacket(new CreatureSay(Anakim.getNpcId(), 0, "Anakim", text_17));
			Anakim.broadcastPacket(new CreatureSay(Anakim.getNpcId(), 0, "Anakim", text_18));
			startQuestTimer("SpecialGuestsSpawn", 4000, null, null);
		}
		if (event.equals("SpecialGuestsSpawn"))
		{
			for (int i = 0; i < specialGuests.length; i++)
			{
				x1 = -51311 + (Rnd.get(500) - 250);
				y1 = -53695 + (Rnd.get(500) - 250);
				final L2Npc guard = addSpawn(specialGuests[i], x1, y1, Point1Z, 58609, false, 0);
				guests.add(guard);
			}
			startQuestTimer("GuestCamera", 100, null, null);
			startQuestTimer("AnakimSpeak6", 8500, null, null);
			startQuestTimer("CoupleMarch", 10000, null, null);
		}
		if (event.equals("GuestCamera"))
		{
			guests.get(0).broadcastPacket(new SpecialCamera(guests.get(0).getObjectId(), 1000, 180, 150, 0, 6000));
		}
		if (event.equals("AnakimSpeak6"))
		{
			final String AnakimTalk = text_19 + wife.getName() + text_06 + husband.getName() + text_20;
			Anakim.broadcastPacket(new CreatureSay(Anakim.getNpcId(), 0, "Anakim", AnakimTalk));
			startQuestTimer("AnakimSpeak7", 24000, null, null);
		}
		if (event.equals("CoupleMarch"))
		{
			for (final L2PcInstance i : WeddingList)
				i.sendPacket(new PlaySound(1, "ns23_f", 0, 0, i.getX(), i.getY(), i.getZ()));
			
			wife.sendPacket(new PlaySound(1, "ns23_f", 0, 0, wife.getX(), wife.getY(), wife.getZ()));
			husband.sendPacket(new PlaySound(1, "ns23_f", 0, 0, husband.getX(), husband.getY(), husband.getZ()));
			husband.setIsParalyzed(false);
			wife.setIsParalyzed(false);
			husband.setWalking();
			wife.setWalking();
			husband.broadcastPacket(new SpecialCamera(husband.getObjectId(), 700, 180, 140, 0, 20000));
			wife.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(-50042, -54178, -2688, 0));
			husband.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(-50042, -54147, -2688, 0));
		}
		if (event.equals("AnakimSpeak7"))
		{
			final String AnakimTalk = text_21 + husband.getName() + text_22 + wife.getName() + text_23;
			Anakim.broadcastPacket(new CreatureSay(Anakim.getNpcId(), 0, "Anakim", AnakimTalk));
			WeddingStep = 1;
		}
		if (event.equals("AnakimSpeak8"))
		{
			final String AnakimTalk = text_24 + wife.getName() + text_25 + husband.getName() + text_26;
			Anakim.broadcastPacket(new CreatureSay(Anakim.getNpcId(), 0, "Anakim", AnakimTalk));
			WeddingStep = 2;
		}
		if (event.equals("AnakimSpeak9"))
		{
			final String AnakimTalk = text_27;
			Anakim.broadcastPacket(new CreatureSay(Anakim.getNpcId(), 0, "Anakim", AnakimTalk));
			WeddingStep = 0;
			husband.setPartnerId(wife.getObjectId());
			husband.setMarryAccepted(true);
			husband.setMarried(true);
			wife.setPartnerId(husband.getObjectId());
			wife.setMarryAccepted(true);
			wife.setMarried(true);
			husband.setRunning();
			wife.setRunning();
			final Couple couple = CoupleManager.getInstance().getCouple(HusbandCoupleId);
			couple.marry();
			Anakim.doCast(SkillTable.getInstance().getInfo(2025, 1));
			for (final L2Npc i : guards)
			{
				final int rr = Rnd.get(1);
				if (rr == 0)
					i.doCast(SkillTable.getInstance().getInfo(2024, 1));
				if (rr == 1)
					i.doCast(SkillTable.getInstance().getInfo(2023, 1));
			}
			startQuestTimer("WeddingFinale", 3000, null, null);
		}
		if (event.equals("WeddingFinale"))
		{
			for (final L2Npc i : pixies)
			{
				xx = -51228 + (Rnd.get(1200) - 600);
				yy = -54178 + (Rnd.get(1200) - 600);
				i.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(xx, yy, -2809, 0));
			}
			for (int i = 0; i < 25; i++)
			{
				final int rr = Rnd.get(2);
				xx = -51228 + (Rnd.get(1200) - 600);
				yy = -54178 + (Rnd.get(1200) - 600);
				final L2Npc gourd = addSpawn(Gourd[rr], xx, yy, -2824, 0, false, 0);
				gourds.add(gourd);
			}
			startQuestTimer("WeddingFinale2", 4000, null, null);
		}
		if (event.equals("WeddingFinale2"))
		{
			for (final L2Npc i : guests)
				i.deleteMe();
			
			for (final L2Npc i : guards)
				i.deleteMe();
			
			for (final L2Npc i : entertainment)
			{
				xx = -51862 + (Rnd.get(50) - 25);
				yy = -54451 + (Rnd.get(50) - 25);
				i.setRunning();
				i.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(xx, yy, -2825, 0));
			}
			for (int n = 0; n < 25; n++)
				gourds.get(n).reduceCurrentHp(999999, gourds.get(n), null);
			
			startQuestTimer("WeddingFinale3", 6000, null, null);
		}
		if (event.equals("WeddingFinale3"))
		{
			for (int i = 0; i < 12; i++)
			{
				entertainment2.add(entertainment.get(i));
				entertainment.remove(i);
			}
			
			for (final L2Npc i : entertainment)
			{
				xx = -51867;
				yy = -54209;
				i.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(xx, yy, -2825, 0));
			}
			for (final L2Npc i : entertainment2)
			{
				xx = -51867;
				yy = -54120;
				i.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(xx, yy, -2825, 0));
			}
			startQuestTimer("WeddingFinale4", 6000, null, null);
		}
		if (event.equals("WeddingFinale4"))
		{
			int val = 1;
			x1 = Point1X + 30;
			x2 = Point2X - 30;
			for (int i = 0; i < entertainment.size(); i++)
			{
				x1 = x1 + val;
				x2 = x2 + val;
				yy = -54209;
				entertainment.get(i).getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(x1, yy, Point1Z, 0));
				entertainment2.get(i).getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(x2, yy, Point1Z, 0));
				val = 80;
			}
			startQuestTimer("WeddingFinale5", 6000, null, null);
		}
		if (event.equals("WeddingFinale5"))
		{
			entertainment2.get(0).broadcastPacket(new CreatureSay(entertainment2.get(0).getNpcId(), 0, "Ceremony Staff", text_28));
			startQuestTimer("WeddingFinale6", 4000, null, null);
		}
		if (event.equals("WeddingFinale6"))
		{
			giftBox = addSpawn(102502, husband.getX() + 20, husband.getY() + 20, husband.getZ(), 0, false, 0);
			startQuestTimer("weddingDespawn", 5000, null, null);
		}
		if (event.equals("weddingDespawn"))
		{
			husbandOk = false;
			wifeOk = false;
			HusbandCoupleId = 0;
			WifeCoupleId = 0;
			WeddingLocked = 0;
			WeddingStep = 0;
			Anakim.deleteMe();
			pet1.deleteMe();
			pet2.deleteMe();
			for (final L2Npc s : entertainment2)
				s.deleteMe();
			for (final L2Npc s : entertainment)
				s.deleteMe();
			for (final L2Npc n : pixies)
				n.deleteMe();
			for (final L2Npc i : guards)
				i.deleteMe();
		}
		return "";
	}
}
