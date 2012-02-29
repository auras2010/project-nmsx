import sys
import time
from l2.universe                               import L2DatabaseFactory
from l2.universe.gameserver.model.quest        import State
from l2.universe.gameserver.model.quest        import QuestState
from l2.universe.gameserver.model.quest.jython import QuestJython as JQuest

qn = "8014_KamaAchievements"

Pathfinder = 32484
Hour = 6
Minutes = 30

class Quest(JQuest) :

 def __init__(self,id,name,descr): 
    JQuest.__init__(self,id,name,descr)
    newMinute = time.strftime("%M")
    newHour = time.strftime("%H")
    Hour1 = (((Hour - int(newHour)) * 60) * 60) * 1000
    Minutes1 = ((Minutes - int(newMinute)) * 60) * 1000
    TimeCheck = Hour1 + Minutes1
    self.startQuestTimer("TimeCheck",TimeCheck,None,None)
	
 def onAdvEvent (self,event,npc,player):
    if event == "TimeCheck":
	    nextCheck = 86400000 #24h
	    self.startQuestTimer("TimeCheck",nextCheck,None,None,True)
	    con=L2DatabaseFactory.getInstance().getConnection()
	    trigger = con.prepareStatement("DELETE FROM kamaloka_results")
	    trigger1 = con.prepareStatement("DELETE FROM character_instance_time")
	    trigger.executeUpdate()
	    trigger1.executeUpdate()
	    trigger.close();
	    trigger1.close();
	    try:
		    con.close()
	    except:
		    pass

 def onTalk (self,npc,player):
   npcId = npc.getNpcId()
   if npcId == Pathfinder:
       if npc.isInsideRadius(-13948,123819,-3112,500,1,0):
	       htmltext = "gludio-list.htm"
       elif npc.isInsideRadius(18228,146030,-3088,500,1,0):
	       htmltext = "dion-list.htm"
       elif npc.isInsideRadius(108384,221614,-3592,500,1,0):
	       htmltext = "heine-list.htm"
       elif npc.isInsideRadius(80960,56455,-1552,500,1,0):
	       htmltext = "oren-list.htm"
       elif npc.isInsideRadius(85894,-142108,-1336,500,1,0):
	       htmltext = "schuttgart-list.htm"
       elif npc.isInsideRadius(42674,-47909,-797,500,1,0):
	       htmltext = "rune-list.htm"
       else:
	       return
   return htmltext
   

QUEST = Quest(8014,qn,"custom")

QUEST.addStartNpc(Pathfinder)
QUEST.addTalkId(Pathfinder)
