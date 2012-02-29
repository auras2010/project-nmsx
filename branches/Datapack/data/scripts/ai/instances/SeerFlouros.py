import sys
from l2.universe.gameserver.ai import CtrlIntention
from l2.universe.gameserver.model.actor.instance 	import L2PcInstance
from l2.universe.gameserver.model.quest import State
from l2.universe.gameserver.model.quest import QuestState
from l2.universe.gameserver.model.quest.jython import QuestJython as JQuest

SEER  = 18559
GUARD = 18560

class PyObject:
	pass

class Quest (JQuest) :
	def __init__(self,id,name,descr):
		JQuest.__init__(self,id,name,descr)
    self.isGuardSpawn = 0

 def onAdvEvent (self,event,npc,player):
    npcId = npc.getNpcId()
    x = player.getX()
    y = player.getY()
    if event == "time_to_spawn":
	    if self.isGuardSpawn == 0:
			newNpc = self.addSpawn(GUARD,x,y,npc.getZ(),0,False,0,False,npc.getInstanceId(),0)
			self.isGuardSpawn = 1
	    elif self.isGuardSpawn == 1:
		    newNpc = self.addSpawn(GUARD,x,y,npc.getZ(),0,False,0,False,npc.getInstanceId(),0)
		    self.isGuardSpawn = 2
	    elif self.isGuardSpawn == 2:
		    return
    elif event == "time_to_spawn1":
	    if self.isGuardSpawn == 1:
		    newNpc = self.addSpawn(GUARD,x,y,npc.getZ(),0,False,0,False,npc.getInstanceId(),0)
		    self.isGuardSpawn = 2	
	    elif self.isGuardSpawn == 2:
		    return
    
    return

	def onAttack(self, npc, player, damage, isPet, skill):
		npcId = npc.getNpcId()
    if npcId == SEER:
	    if self.isGuardSpawn == 0:
	        self.startQuestTimer("time_to_spawn",30000,npc,player)
	    elif self.isGuardSpawn == 1:
	        self.startQuestTimer("time_to_spawn1",60000,npc,player)
    return

 def onKill(self,npc,player,isPet):
    npcId = npc.getNpcId()
    if npcId == GUARD:
	    if self.isGuardSpawn == 0:
		    self.startQuestTimer("time_to_spawn",30000,npc,player)
	    elif self.isGuardSpawn == 1:
		    self.startQuestTimer("time_to_spawn1",60000,npc,player)
		    self.isGuardSpawn -=1
	    elif self.isGuardSpawn == 2:
		    self.isGuardSpawn -=1
		    self.startQuestTimer("time_to_spawn1",60000,npc,player)
    elif npcId == SEER:
	    self.cancelQuestTimer("time_to_spawn",npc,player)
	    self.cancelQuestTimer("time_to_spawn1",npc,player)

QUEST = Quest(-1,"Flourous","ai")

QUEST.addAttackId(SEER)
QUEST.addKillId(GUARD)
QUEST.addKillId(SEER)