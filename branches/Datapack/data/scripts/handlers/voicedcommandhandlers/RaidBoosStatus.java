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

package handlers.voicedcommandhandlers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
  
import l2.universe.gameserver.datatables.NpcTable;
import l2.universe.gameserver.handler.IVoicedCommandHandler;
import l2.universe.gameserver.instancemanager.GrandBossManager;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.templates.StatsSet;
 

 public class RaidBoosStatus implements IVoicedCommandHandler
 {
    static final Logger _log = Logger.getLogger(RaidBoosStatus.class.getName());
         private static final String[] _voicedCommands =
         {
                "grandboos"
          };
            
         public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
         {
                if (command.startsWith("grandboos"))
                    return Status(activeChar);
                else 
                    return false;
          }
          
            public boolean Status(L2PcInstance activeChar)
            {
            	   int[] BOSSES = { 29001, 29006, 29014, 29019, 29020, 29022, 29028, 29045 };
                   SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                   activeChar.sendMessage("######## GRANDBOOS ########");
                   for (int boss : BOSSES){
                         String name = NpcTable.getInstance().getTemplate(boss).getName();
                         StatsSet stats = GrandBossManager.getInstance().getStatsSet(boss);
                         if (stats == null){
                            activeChar.sendMessage("Stats for GrandBoss " + boss + " not found!");
                            continue;
                         }
                         if (boss == 29019) {
                                       long dmax = 0;
                                       for (int i = 29066; i <= 29068; i++) {
                                            StatsSet s = GrandBossManager.getInstance().getStatsSet(i);
                                            if (s == null) continue;
                                            long d = s.getLong("respawn_time");
                                            if (d >= dmax) {
                                                 dmax = d;
                                                 stats = s;
                                            }
                                       }
                                   }
                         long delay = stats.getLong("respawn_time");
                         long currentTime = System.currentTimeMillis();
                         if (delay <= currentTime){
                             activeChar.sendMessage("("+name+") > Is Alive");
 
                         }else{
                             activeChar.sendMessage("("+name+") > Is Death ( "+sdf.format(new Date(delay))+" )");
                          }
                         }             
                     
                      return true;
            }
           
               public String[] getVoicedCommandList()
            {
                return _voicedCommands;
            }
} 