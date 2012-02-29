import sys
from l2.universe.gameserver.model.quest 		import State
from l2.universe.gameserver.model.quest 		import QuestState
from l2.universe.gameserver.datatables 			import DoorTable
from l2.universe.gameserver.datatables 			import SkillTable
from l2.universe.gameserver.model 				import L2Skill
from l2.universe.gameserver.model.quest.jython 	import QuestJython as JQuest
from l2.universe.gameserver 					import Announcements
from l2.universe import L2DatabaseFactory
from l2.universe.gameserver.ai 					import CtrlIntention
from l2.universe.util 							import Rnd
from java.lang 									import System
from l2.universe.gameserver.model 				import L2World

qn = "CaptureTheBase"
closed=1
res_timer=0
npc1=0
npc2=0
TEAM1 = []
TEAM2 = []
attacked = 0
annom = 1
NAME = "ZAHVAT BAZI"
LOC = "Goddard"
REGISTER = 55558
locr = [147712,-55520,-2733]
PENI = 57
PENI_KOL = 100000000
LEVEL = 76
AFTER_RESTART = 5
TIME_FOR_WAIT = 350
TIME_FOR_REG = 3
ANNOUNCE_INTERVAL = 2
YCH_MIN = 3
YCH_MAX = 20
REWARD =[[9552,3,40],[9553,3,40],[9554,3,40],[9555,3,40],[9556,3,40],[9557,3,40]]
t1 =[81431,-16025,-1855]
t2 =[85162,-16759,-1829]
BASE1 = 55561
BASE2 = 55562
com1 = "Dark power"
com2 = "Light power"
RES_TIME = 8


class Quest (JQuest) :
 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def init_LoadGlobalData(self) :
   self.startQuestTimer("open", AFTER_RESTART *60000, None, None)
   return

 def onTalk (Self,npc,player):
  global TEAM1,TEAM2,npc1,npc2,closed
  st = player.getQuestState(qn)
  npcId = npc.getNpcId()
  if npcId == REGISTER:
   if closed<>1:
    if not player.isInOlympiadMode() :
     if player.getLevel() >= LEVEL:
      if player.getName() not in TEAM1 + TEAM2 :
       if len(TEAM1)>len(TEAM2) :
        kolych = len(TEAM1)
       else:
        kolych = len(TEAM2)
       if kolych <= YCH_MAX :
        if PENI_KOL<>0:   
         if st.getQuestItemsCount(PENI)>PENI_KOL:
           st.takeItems(PENI,PENI_KOL)
           if len(TEAM1)>len(TEAM2):
            TEAM2.append(player.getName())
            return "reg.htm"
           else:
            TEAM1.append(player.getName())
            return "reg.htm"
         else:
           st.exitQuest(1)
           return "nopeni.htm"
        else:
           if len(TEAM1)>len(TEAM2):
            TEAM2.append(player.getName())
            return "reg.htm"
           else:
            TEAM1.append(player.getName())
            return "reg.htm"
       else:		 
         return "max.htm"
      else:
       return "yje.htm"
     else:
      return "lvl.htm"
    else:
     return "You register in olympiad games now"
   else:
    return "noreg.htm"
  return

 def onAdvEvent (self,event,npc,player):
   global TEAM1,TEAM2,npc1,npc2,res_timer,annom,closed
   if event == "open" :
     TEAM1=[]
     TEAM2=[]
     closed=0
     annom=1
     npc=self.addSpawn(REGISTER,locr[0],locr[1],locr[2],30000,False,0)
     self.startQuestTimer("close", TIME_FOR_REG*60000, npc, None)
     self.startQuestTimer("announce", ANNOUNCE_INTERVAL*60000, None, None)
     Announcements.getInstance().announceToAll("Opened registration for "+str(NAME)+" event! You can register in "+str(LOC)+".")
   if event == "close":
    res_timer = 1
    self.startQuestTimer("res", RES_TIME*1000, None, None)
    self.startQuestTimer("open", TIME_FOR_WAIT*60000, None, None)
    for nm in TEAM1:
     i=L2World.getInstance().getPlayer(nm)
     if i<>None:
      if not i.isOnline() or i.isInOlympiadMode():
       TEAM1.remove(nm)
     else:
       TEAM1.remove(nm)
    for nm in TEAM2:
     i=L2World.getInstance().getPlayer(nm)
     if i<>None:
      if not i.isOnline() or i.isInOlympiadMode():
       TEAM2.remove(nm)
     else:
       TEAM2.remove(nm)
    while abs(len(TEAM1)-len(TEAM2))>1:
     if len(TEAM1)>len(TEAM2):
      self.saveGlobalQuestVar(str(TEAM1[0].getObjectId()), "team2")
      TEAM2.append(TEAM1[0])
      TEAM1.remove(TEAM1[0])
     else:
      self.saveGlobalQuestVar(str(TEAM2[0].getObjectId()), "team1")
      TEAM1.append(TEAM2[0])
      TEAM2.remove(TEAM2[0])
    if (len(TEAM1)+len(TEAM2))< 2*YCH_MIN :
      npc.deleteMe()
      closed=1
      Announcements.getInstance().announceToAll("Event "+str(NAME)+" was canceled due lack of participation.") 
    else:
      closed=1
      Announcements.getInstance().announceToAll("Event "+str(NAME)+" has started!")
      npc.deleteMe()
      npc1=self.addSpawn(BASE1,t1[0],t1[1],t1[2],30000,False,0)
      npc2=self.addSpawn(BASE2,t2[0],t2[1],t2[2],30000,False,0)
      for nm in TEAM1 :
       i=L2World.getInstance().getPlayer(nm)
       if i<>None:
        if i.isOnline() :
         i.stopAllEffects()
         i.setTeam(2)
         i.broadcastStatusUpdate()
         i.broadcastUserInfo()
         i.teleToLocation(t1[0]+100,t1[1],t1[2])
      for nm in TEAM2 :
       i=L2World.getInstance().getPlayer(nm)
       if i<>None:
        if i.isOnline() :
         i.stopAllEffects()
         i.setTeam(1)
         i.broadcastStatusUpdate()
         i.broadcastUserInfo()
         i.teleToLocation(t2[0]+100,t2[1],t2[2])		
   if event == "announce" and closed==0 and (TIME_FOR_REG - ANNOUNCE_INTERVAL * annom)>0: 
     Announcements.getInstance().announceToAll(str(TIME_FOR_REG - ANNOUNCE_INTERVAL * annom ) + " minutes until event "+str(NAME)+" will start! You can register in "+str(LOC)+". There are "+str(len(TEAM1))+" Dark warriors and "+str(len(TEAM2))+" Heroes of Light.")
     annom=annom+1
     self.startQuestTimer("announce", ANNOUNCE_INTERVAL*60000, None, None)
   if event == "return_1" :
     res_timer = 0
     for nm in TEAM1 :
      i=L2World.getInstance().getPlayer(nm)
      if i<>None:
       if i.isOnline() :
        i.teleToLocation(locr[0],locr[1],locr[2])
        i.setTeam(0)
        i.broadcastStatusUpdate()
        i.broadcastUserInfo()
     for nm in TEAM2 :
      i=L2World.getInstance().getPlayer(nm)
      if i<>None:
       if i.isOnline() :
        i.teleToLocation(locr[0],locr[1],locr[2])
        i.setTeam(0)
        i.broadcastStatusUpdate()
        i.broadcastUserInfo()
     Announcements.getInstance().announceToAll("Event "+str(NAME)+" has ended. "+str(com1)+" win!")
   if event == "return_2" :
     res_timer = 0
     for nm in TEAM1 :
      i=L2World.getInstance().getPlayer(nm)
      if i<>None:
       if i.isOnline() :
        i.teleToLocation(locr[0],locr[1],locr[2])
        i.setTeam(0)
        i.broadcastStatusUpdate()
        i.broadcastUserInfo()
     for nm in TEAM2 :
      i=L2World.getInstance().getPlayer(nm)
      if i<>None:
       if i.isOnline() :
        i.teleToLocation(locr[0],locr[1],locr[2])
        i.setTeam(0)
        i.broadcastStatusUpdate()
        i.broadcastUserInfo()
     Announcements.getInstance().announceToAll("Event "+str(NAME)+" has ended. "+str(com2)+" win!")
   if event == "exit" :
     if player.getName() in TEAM1:
      TEAM1.remove(player.getName())
     else:
      TEAM2.remove(player.getName())
     return "exit.htm"
   if event == "res" and res_timer==1:
    self.startQuestTimer("res", RES_TIME*1000, None, None)
    for nm in TEAM1:
     i=L2World.getInstance().getPlayer(nm)
     if i<>None:
      if i.isOnline() :
       if i.isDead():
        i.doRevive()
        i.setCurrentCp(i.getMaxCp())
        i.setCurrentHp(i.getMaxHp())
        i.setCurrentMp(i.getMaxMp())
        i.stopAllEffects()
        i.setTeam(0)
        i.setTeam(2)
        i.broadcastStatusUpdate()
        i.broadcastUserInfo()
        i.teleToLocation(t1[0],t1[1],t1[2])
    for nm in TEAM2:
     i=L2World.getInstance().getPlayer(nm)
     if i<>None:
      if i.isOnline() :
       if i.isDead():
        i.doRevive()
        i.setCurrentCp(i.getMaxCp())
        i.setCurrentHp(i.getMaxHp())
        i.setCurrentMp(i.getMaxMp())
        i.stopAllEffects()
        i.setTeam(0)
        i.setTeam(1)
        i.broadcastStatusUpdate()
        i.broadcastUserInfo()
        i.teleToLocation(t2[0],t2[1],t2[2])
   return 

 def onAttack (self,npc,player,damage,isPet,npcId):
  npcId = npc.getNpcId()
  if npcId == BASE2 and player.getName() not in TEAM1 :
     #player.reduceCurrentHp(99999,player)   
      player.teleToLocation(85359,-17786,-1833)
  if npcId == BASE1 and player.getName() not in TEAM2 :
     #player.reduceCurrentHp(99999,player)   
      player.teleToLocation(80153,-16204,-1819)
  return   

 def onSkillSee (self,npc,player,skill,targets,isPet) :
     if player.getTarget() == npc and skill.getId() in [1218,1015,1258,1011,1401,58,1217,329]:
      player.setTeam(0)
      player.broadcastStatusUpdate()
      player.broadcastUserInfo()      
      player.teleToLocation(locr[0],locr[1],locr[2])
      if player.getName() in TEAM1 :
       TEAM1.remove(player.getName())
      elif player.getName() in TEAM2 :
       TEAM2.remove(player.getName())
	  
 def onKill(self,npc,player,isPet):
  global TEAM1,TEAM2,npc1,npc2,res_timer
  npcId = npc.getNpcId()
  if npcId == BASE1:
   res_timer=0
   self.startQuestTimer("return_2", 10000, None, None)  
   npc2.deleteMe()
   for nm in TEAM2 :
    i=L2World.getInstance().getPlayer(nm)
    if i<>None:
     if i.isOnline() :
      for id, count, chance in REWARD :
       if Rnd.get(100)<=chance :
        i.getQuestState(qn).giveItems(id,count)
  if npcId == BASE2:
   res_timer=0
   self.startQuestTimer("return_1", 10000, None, None)
   npc1.deleteMe()
   for nm in TEAM1 :
    i=L2World.getInstance().getPlayer(nm)
    if i<>None:
     if i.isOnline() :
      for id, count, chance in REWARD :
       if Rnd.get(100)<=chance :
        i.getQuestState(qn).giveItems(id,count)
  return

QUEST = Quest(5556, qn, "CaptureTheBase")

QUEST.addKillId(int(BASE1))
QUEST.addAttackId(int(BASE1))
QUEST.addKillId(int(BASE2))
QUEST.addAttackId(int(BASE2))
QUEST.addStartNpc(int(REGISTER))
QUEST.addTalkId(int(REGISTER))
QUEST.addSkillSeeId(int(BASE1))
QUEST.addSkillSeeId(int(BASE2))