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

package custom.NpcLocationInfo;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import javolution.util.FastList;
import javolution.util.FastMap;

public class NpcLocationInfo extends Quest
{
  @SuppressWarnings({ "unchecked", "rawtypes" })
private static FastMap<Integer, Location> locations = new FastMap();

  @SuppressWarnings({ "rawtypes", "unchecked" })
private FastList<Integer> npcIds = new FastList();

  @Override
public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
  {
    if (event.endsWith(".htm"))
      return event;
    String htmltext = "";
    QuestState st = player.getQuestState("NpcLocationInfo");
    if (st == null)
      return "";
    int npcId = Integer.parseInt(event);
    if (locations.keySet().contains(Integer.valueOf(npcId)))
    {
    @SuppressWarnings("cast")
	Location loc = (Location)locations.get(Integer.valueOf(npcId));
      st.addRadar(loc.x, loc.y, loc.z);
      htmltext = "MoveToLoc.htm";
      st.exitQuest(true);
    }
    return htmltext;
  }

  @Override
public String onTalk(L2Npc npc, L2PcInstance player)
  {
    String htmltext = "";
    int npcId = npc.getNpcId();
    if (this.npcIds.contains(Integer.valueOf(npcId)))
      htmltext = String.valueOf(npcId) + ".htm";
    return htmltext;
  }

  public NpcLocationInfo(int id, String name, String desc)
  {
    super(id, name, desc);
    int[] NPC_IDS = { 30598, 30599, 30600, 30601, 30602, 32135 };
    for (int i : NPC_IDS)
    {
      addStartNpc(i);
      addTalkId(i);
      this.npcIds.add(Integer.valueOf(i));
    }
  }

  public static void main(String[] args)
  {
    new NpcLocationInfo(-1, "NpcLocationInfo", "custom");
  }

  static
  {
    locations.put(Integer.valueOf(30006), new Location(-84108, 244604, -3729));
    locations.put(Integer.valueOf(30039), new Location(-82236, 241573, -3728));
    locations.put(Integer.valueOf(30040), new Location(-82515, 241221, -3728));
    locations.put(Integer.valueOf(30041), new Location(-82319, 244709, -3727));
    locations.put(Integer.valueOf(30042), new Location(-82659, 244992, -3717));
    locations.put(Integer.valueOf(30043), new Location(-86114, 244682, -3727));
    locations.put(Integer.valueOf(30044), new Location(-86328, 244448, -3724));
    locations.put(Integer.valueOf(30045), new Location(-86322, 241215, -3727));
    locations.put(Integer.valueOf(30046), new Location(-85964, 240947, -3727));
    locations.put(Integer.valueOf(30283), new Location(-85026, 242689, -3729));
    locations.put(Integer.valueOf(30003), new Location(-83789, 240799, -3717));
    locations.put(Integer.valueOf(30004), new Location(-84204, 240403, -3717));
    locations.put(Integer.valueOf(30001), new Location(-86385, 243267, -3717));
    locations.put(Integer.valueOf(30002), new Location(-86733, 242918, -3717));
    locations.put(Integer.valueOf(30031), new Location(-84516, 245449, -3714));
    locations.put(Integer.valueOf(30033), new Location(-84729, 245001, -3726));
    locations.put(Integer.valueOf(30035), new Location(-84965, 245222, -3726));
    locations.put(Integer.valueOf(30032), new Location(-84981, 244764, -3726));
    locations.put(Integer.valueOf(30036), new Location(-85186, 245001, -3726));
    locations.put(Integer.valueOf(30026), new Location(-83326, 242964, -3718));
    locations.put(Integer.valueOf(30027), new Location(-83020, 242553, -3718));
    locations.put(Integer.valueOf(30029), new Location(-83175, 243065, -3718));
    locations.put(Integer.valueOf(30028), new Location(-82809, 242751, -3718));
    locations.put(Integer.valueOf(30054), new Location(-81895, 243917, -3721));
    locations.put(Integer.valueOf(30055), new Location(-81840, 243534, -3721));
    locations.put(Integer.valueOf(30005), new Location(-81512, 243424, -3720));
    locations.put(Integer.valueOf(30048), new Location(-84436, 242793, -3729));
    locations.put(Integer.valueOf(30312), new Location(-78939, 240305, -3443));
    locations.put(Integer.valueOf(30368), new Location(-85301, 244587, -3725));
    locations.put(Integer.valueOf(30049), new Location(-83163, 243560, -3728));
    locations.put(Integer.valueOf(30047), new Location(-97131, 258946, -3622));
    locations.put(Integer.valueOf(30497), new Location(-114685, 222291, -2925));
    locations.put(Integer.valueOf(30050), new Location(-84057, 242832, -3729));
    locations.put(Integer.valueOf(30311), new Location(-100332, 238019, -3573));
    locations.put(Integer.valueOf(30051), new Location(-82041, 242718, -3725));

    locations.put(Integer.valueOf(30134), new Location(9670, 15537, -4499));
    locations.put(Integer.valueOf(30224), new Location(15120, 15656, -4301));
    locations.put(Integer.valueOf(30348), new Location(17306, 13592, -3649));
    locations.put(Integer.valueOf(30355), new Location(15272, 16310, -4302));
    locations.put(Integer.valueOf(30347), new Location(6449, 19619, -3619));
    locations.put(Integer.valueOf(30432), new Location(-15404, 71131, -3370));
    locations.put(Integer.valueOf(30356), new Location(7490, 17397, -4378));
    locations.put(Integer.valueOf(30349), new Location(17102, 13002, -3668));
    locations.put(Integer.valueOf(30346), new Location(6532, 19903, -3618));
    locations.put(Integer.valueOf(30433), new Location(-15648, 71405, -3376));
    locations.put(Integer.valueOf(30357), new Location(7634, 18047, -4378));
    locations.put(Integer.valueOf(30431), new Location(-1301, 75883, -3491));
    locations.put(Integer.valueOf(30430), new Location(-1152, 76125, -3491));
    locations.put(Integer.valueOf(30307), new Location(10584, 17574, -4557));
    locations.put(Integer.valueOf(30138), new Location(12009, 15704, -4555));
    locations.put(Integer.valueOf(30137), new Location(11951, 15661, -4555));
    locations.put(Integer.valueOf(30135), new Location(10761, 17970, -4558));
    locations.put(Integer.valueOf(30136), new Location(10823, 18013, -4558));
    locations.put(Integer.valueOf(30143), new Location(11283, 14226, -4167));
    locations.put(Integer.valueOf(30360), new Location(10447, 14620, -4167));
    locations.put(Integer.valueOf(30145), new Location(11258, 14431, -4167));
    locations.put(Integer.valueOf(30135), new Location(10761, 17970, -4558));
    locations.put(Integer.valueOf(30144), new Location(10344, 14445, -4167));
    locations.put(Integer.valueOf(30358), new Location(10775, 14190, -4167));
    locations.put(Integer.valueOf(30359), new Location(11235, 14078, -4167));
    locations.put(Integer.valueOf(30141), new Location(11012, 14128, -4167));
    locations.put(Integer.valueOf(30139), new Location(13380, 17430, -4544));
    locations.put(Integer.valueOf(30140), new Location(13464, 17751, -4544));
    locations.put(Integer.valueOf(30350), new Location(13763, 17501, -4544));
    locations.put(Integer.valueOf(30421), new Location(-44225, 79721, -3577));
    locations.put(Integer.valueOf(30419), new Location(-44015, 79683, -3577));
    locations.put(Integer.valueOf(30130), new Location(25856, 10832, -3649));
    locations.put(Integer.valueOf(30351), new Location(12328, 14947, -4499));
    locations.put(Integer.valueOf(30353), new Location(13081, 18444, -4498));
    locations.put(Integer.valueOf(30354), new Location(12311, 17470, -4499));

    locations.put(Integer.valueOf(30146), new Location(46926, 51511, -2977));
    locations.put(Integer.valueOf(30285), new Location(44995, 51706, -2803));
    locations.put(Integer.valueOf(30284), new Location(45727, 51721, -2803));
    locations.put(Integer.valueOf(30221), new Location(42812, 51138, -2996));
    locations.put(Integer.valueOf(30217), new Location(45487, 46511, -2996));
    locations.put(Integer.valueOf(30219), new Location(47401, 51764, -2996));
    locations.put(Integer.valueOf(30220), new Location(42971, 51372, -2996));
    locations.put(Integer.valueOf(30218), new Location(47595, 51569, -2996));
    locations.put(Integer.valueOf(30216), new Location(45778, 46534, -2996));
    locations.put(Integer.valueOf(30363), new Location(44476, 47153, -2984));
    locations.put(Integer.valueOf(30149), new Location(42700, 50057, -2984));
    locations.put(Integer.valueOf(30150), new Location(42766, 50037, -2984));
    locations.put(Integer.valueOf(30148), new Location(44683, 46952, -2981));
    locations.put(Integer.valueOf(30147), new Location(44667, 46896, -2982));
    locations.put(Integer.valueOf(30155), new Location(45725, 52105, -2795));
    locations.put(Integer.valueOf(30156), new Location(44823, 52414, -2795));
    locations.put(Integer.valueOf(30157), new Location(45000, 52101, -2795));
    locations.put(Integer.valueOf(30158), new Location(45919, 52414, -2795));
    locations.put(Integer.valueOf(30154), new Location(44692, 52261, -2795));
    locations.put(Integer.valueOf(30153), new Location(47780, 49568, -2983));
    locations.put(Integer.valueOf(30152), new Location(47912, 50170, -2983));
    locations.put(Integer.valueOf(30151), new Location(47868, 50167, -2983));
    locations.put(Integer.valueOf(30423), new Location(28928, 74248, -3773));
    locations.put(Integer.valueOf(30414), new Location(43673, 49683, -3046));
    locations.put(Integer.valueOf(31853), new Location(50592, 54896, -3376));
    locations.put(Integer.valueOf(30223), new Location(42978, 49115, -2994));
    locations.put(Integer.valueOf(30362), new Location(46475, 50495, -3058));
    locations.put(Integer.valueOf(30222), new Location(45859, 50827, -3058));
    locations.put(Integer.valueOf(30371), new Location(51210, 82474, -3283));
    locations.put(Integer.valueOf(31852), new Location(49262, 53607, -3216));

    locations.put(Integer.valueOf(30540), new Location(115072, -178176, -906));
    locations.put(Integer.valueOf(30541), new Location(117847, -182339, -1537));
    locations.put(Integer.valueOf(30542), new Location(116617, -184308, -1569));
    locations.put(Integer.valueOf(30543), new Location(117826, -182576, -1537));
    locations.put(Integer.valueOf(30544), new Location(116378, -184308, -1571));
    locations.put(Integer.valueOf(30545), new Location(115183, -176728, -791));
    locations.put(Integer.valueOf(30546), new Location(114969, -176752, -790));
    locations.put(Integer.valueOf(30547), new Location(117366, -178725, -1118));
    locations.put(Integer.valueOf(30548), new Location(117378, -178914, -1120));
    locations.put(Integer.valueOf(30531), new Location(116226, -178529, -948));
    locations.put(Integer.valueOf(30532), new Location(116190, -178441, -948));
    locations.put(Integer.valueOf(30533), new Location(116016, -178615, -948));
    locations.put(Integer.valueOf(30534), new Location(116190, -178615, -948));
    locations.put(Integer.valueOf(30535), new Location(116103, -178407, -948));
    locations.put(Integer.valueOf(30536), new Location(116103, -178653, -948));
    locations.put(Integer.valueOf(30525), new Location(115468, -182446, -1434));
    locations.put(Integer.valueOf(30526), new Location(115315, -182155, -1444));
    locations.put(Integer.valueOf(30527), new Location(115271, -182692, -1445));
    locations.put(Integer.valueOf(30518), new Location(115900, -177316, -915));
    locations.put(Integer.valueOf(30519), new Location(116268, -177524, -914));
    locations.put(Integer.valueOf(30516), new Location(115741, -181645, -1344));
    locations.put(Integer.valueOf(30517), new Location(116192, -181072, -1344));
    locations.put(Integer.valueOf(30520), new Location(115205, -180024, -870));
    locations.put(Integer.valueOf(30521), new Location(114716, -180018, -871));
    locations.put(Integer.valueOf(30522), new Location(114832, -179520, -871));
    locations.put(Integer.valueOf(30523), new Location(115717, -183488, -1483));
    locations.put(Integer.valueOf(30524), new Location(115618, -183265, -1483));
    locations.put(Integer.valueOf(30537), new Location(114348, -178537, -813));
    locations.put(Integer.valueOf(30650), new Location(114990, -177294, -854));
    locations.put(Integer.valueOf(30538), new Location(114426, -178672, -812));
    locations.put(Integer.valueOf(30539), new Location(114409, -178415, -812));
    locations.put(Integer.valueOf(30671), new Location(117061, -181867, -1413));
    locations.put(Integer.valueOf(30651), new Location(116164, -184029, -1507));
    locations.put(Integer.valueOf(30550), new Location(115563, -182923, -1448));
    locations.put(Integer.valueOf(30554), new Location(112656, -174864, -611));
    locations.put(Integer.valueOf(30553), new Location(116852, -183595, -1566));

    locations.put(Integer.valueOf(30576), new Location(-45264, -112512, -235));
    locations.put(Integer.valueOf(30577), new Location(-46576, -117311, -242));
    locations.put(Integer.valueOf(30578), new Location(-47360, -113791, -237));
    locations.put(Integer.valueOf(30579), new Location(-47360, -113424, -235));
    locations.put(Integer.valueOf(30580), new Location(-45744, -117165, -236));
    locations.put(Integer.valueOf(30581), new Location(-46528, -109968, -250));
    locations.put(Integer.valueOf(30582), new Location(-45808, -110055, -255));
    locations.put(Integer.valueOf(30583), new Location(-45731, -113844, -237));
    locations.put(Integer.valueOf(30584), new Location(-45728, -113360, -237));
    locations.put(Integer.valueOf(30569), new Location(-45952, -114784, -199));
    locations.put(Integer.valueOf(30570), new Location(-45952, -114496, -199));
    locations.put(Integer.valueOf(30571), new Location(-45863, -112621, -200));
    locations.put(Integer.valueOf(30572), new Location(-45864, -112540, -199));
    locations.put(Integer.valueOf(30564), new Location(-43264, -112532, -220));
    locations.put(Integer.valueOf(30560), new Location(-43910, -115518, -194));
    locations.put(Integer.valueOf(30561), new Location(-43950, -115457, -194));
    locations.put(Integer.valueOf(30558), new Location(-44416, -111486, -222));
    locations.put(Integer.valueOf(30559), new Location(-43926, -111794, -222));
    locations.put(Integer.valueOf(30562), new Location(-43109, -113770, -221));
    locations.put(Integer.valueOf(30563), new Location(-43114, -113404, -221));
    locations.put(Integer.valueOf(30565), new Location(-46768, -113610, -3));
    locations.put(Integer.valueOf(30566), new Location(-46802, -114011, -112));
    locations.put(Integer.valueOf(30567), new Location(-46247, -113866, -21));
    locations.put(Integer.valueOf(30568), new Location(-46808, -113184, -112));
    locations.put(Integer.valueOf(30585), new Location(-45328, -114736, -237));
    locations.put(Integer.valueOf(30587), new Location(-44624, -111873, -238));

    locations.put(Integer.valueOf(32163), new Location(-116879, 46591, 360));
    locations.put(Integer.valueOf(32173), new Location(-119378, 49242, 8));
    locations.put(Integer.valueOf(32174), new Location(-119774, 49245, 8));
    locations.put(Integer.valueOf(32175), new Location(-119830, 51860, -792));
    locations.put(Integer.valueOf(32176), new Location(-119362, 51862, -792));
    locations.put(Integer.valueOf(32177), new Location(-112872, 46850, 48));
    locations.put(Integer.valueOf(32178), new Location(-112352, 47392, 48));
    locations.put(Integer.valueOf(32179), new Location(-110544, 49040, -1128));
    locations.put(Integer.valueOf(32180), new Location(-110536, 45162, -1128));
    locations.put(Integer.valueOf(32164), new Location(-115888, 43568, 524));
    locations.put(Integer.valueOf(32165), new Location(-115486, 43567, 525));
    locations.put(Integer.valueOf(32168), new Location(-116920, 47792, 456));
    locations.put(Integer.valueOf(32166), new Location(-116749, 48077, 462));
    locations.put(Integer.valueOf(32167), new Location(-117153, 48075, 456));
    locations.put(Integer.valueOf(32141), new Location(-119104, 43280, 544));
    locations.put(Integer.valueOf(32142), new Location(-119104, 43152, 544));
    locations.put(Integer.valueOf(32143), new Location(-117056, 43168, 544));
    locations.put(Integer.valueOf(32144), new Location(-117060, 43296, 544));
    locations.put(Integer.valueOf(32145), new Location(-118192, 42384, 824));
    locations.put(Integer.valueOf(32146), new Location(-117968, 42384, 824));
    locations.put(Integer.valueOf(32139), new Location(-118132, 42788, 712));
    locations.put(Integer.valueOf(32140), new Location(-118028, 42778, 712));
    locations.put(Integer.valueOf(32138), new Location(-118080, 42835, 712));
    locations.put(Integer.valueOf(32171), new Location(-114802, 44821, 524));
    locations.put(Integer.valueOf(32170), new Location(-114975, 44658, 512));
    locations.put(Integer.valueOf(32172), new Location(-114801, 45031, 525));
    locations.put(Integer.valueOf(32153), new Location(-120432, 45296, 408));
    locations.put(Integer.valueOf(32154), new Location(-120706, 45079, 408));
    locations.put(Integer.valueOf(32155), new Location(-120356, 45293, 408));
    locations.put(Integer.valueOf(32156), new Location(-120604, 44960, 408));
    locations.put(Integer.valueOf(32150), new Location(-120294, 46013, 384));
    locations.put(Integer.valueOf(32151), new Location(-120157, 45813, 344));
    locations.put(Integer.valueOf(32152), new Location(-120158, 46221, 344));
    locations.put(Integer.valueOf(32147), new Location(-120400, 46921, 400));
    locations.put(Integer.valueOf(32148), new Location(-120407, 46755, 408));
    locations.put(Integer.valueOf(32149), new Location(-120442, 47125, 408));
    locations.put(Integer.valueOf(32160), new Location(-118720, 48062, 464));
    locations.put(Integer.valueOf(32162), new Location(-118918, 47956, 464));
    locations.put(Integer.valueOf(32161), new Location(-118527, 47955, 464));
    locations.put(Integer.valueOf(32158), new Location(-117605, 48079, 456));
    locations.put(Integer.valueOf(32157), new Location(-117824, 48080, 464));
    locations.put(Integer.valueOf(32159), new Location(-118030, 47930, 456));
    locations.put(Integer.valueOf(32169), new Location(-119237, 46587, 360));
  }

  private static class Location
  {
    public final int x;
    public final int y;
    public final int z;

    public Location(int _x, int _y, int _z)
    {
      this.x = _x;
      this.y = _y;
      this.z = _z;
    }
  }
}