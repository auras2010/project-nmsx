import sys
from l2.universe.gameserver.model.quest        import State
from l2.universe.gameserver.model.quest        import QuestState
from l2.universe.gameserver.model.quest.jython import QuestJython as JQuest

qn = "8013_Pathfinder"

Pathfinder = 32484
Rewarder   = 32485

class Worker(JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

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
   return
   
 def onFirstTalk(self,npc,player):
   npcId = npc.getNpcId()
   if npcId == Rewarder:
       if npc.isInsideRadius(9261,-219862,-8021,1000,1,0):
           htmltext = "20-30.htm"
       elif npc.isInsideRadius(16301,-219806,-8021,1000,1,0):
           htmltext = "25-35.htm"
       elif npc.isInsideRadius(23478,-220079,-7799,1000,1,0):
           htmltext = "30-40.htm"
       elif npc.isInsideRadius(9290,-212993,-7799,1000,1,0):
           htmltext = "35-45.htm"
       elif npc.isInsideRadius(16598,-212997,-7802,1000,1,0):
           htmltext = "40-50.htm"
       elif npc.isInsideRadius(23650,-213051,-8007,1000,1,0):
           htmltext = "45-55.htm"
       elif npc.isInsideRadius(9136,-205733,-8007,1000,1,0):
           htmltext = "50-60.htm"
       elif npc.isInsideRadius(16508,-205737,-8007,1000,1,0):
           htmltext = "55-65.htm"
       elif npc.isInsideRadius(23229,-206316,-7991,1000,1,0):
           htmltext = "60-70.htm"
       elif npc.isInsideRadius(42638,-219781,-8759,1000,1,0):
           htmltext = "65-75.htm"
       elif npc.isInsideRadius(49014,-219737,-8759,1000,1,0):
           htmltext = "70-80.htm"
       else:
	       return
   return htmltext

QUEST = Worker(-1,qn,"custom")

QUEST.addStartNpc(Pathfinder)
QUEST.addTalkId(Pathfinder)
QUEST.addFirstTalkId(Rewarder)
