import sys
from l2.universe import Config
from l2.universe.gameserver.model.quest import State
from l2.universe.gameserver.model.quest import QuestState
from l2.universe.gameserver.model.quest.jython import QuestJython as JQuest
from l2.universe.gameserver.network.serverpackets import MagicSkillUse
from l2.universe.gameserver.network.serverpackets import SystemMessage
from l2.universe.gameserver.util import Util

qn = "641_AttackSailren"

#NPC
STATUE = 32109
VELO = [22196,22197,22198, 22218, 22223]
PTERO = 22199

#QUEST ITEM
GAZKH_FRAGMENT = 8782
GAZKH = 8784

#CHANCE
FRAGMENT_CHANCE = 30 #Guessed


class Quest (JQuest) :

 def __init__(self,id,name,descr):
	JQuest.__init__(self,id,name,descr)
	self.questItemIds = [GAZKH_FRAGMENT]

 def onAdvEvent (self,event,npc, player) :
	htmltext = event
	st = player.getQuestState(qn)
	cond = st.getInt("cond")
	if not st : return
	if event == "32109-1.htm" :
		htmltext = "32109-2.htm"
	elif event == "32109-3.htm" :
		st.setState(State.STARTED)
		st.set("cond","1")
		st.playSound("ItemSound.quest_accept")
	elif event == "32109-4.htm" :
		st.takeItems(GAZKH_FRAGMENT,30)
		st.set("cond","2")
		st.playSound("ItemSound.quest_middle")
	elif event == "32109-5.htm" :
		npc.broadcastPacket(MagicSkillUse(npc,player,5089,1,3000,0))
		sm = SystemMessage(110)
		sm.addString("Shilen's Protection")
		player.sendPacket(sm)
		st.giveItems(GAZKH,1)
		st.set("cond","3")
		st.playSound("ItemSound.quest_finish")
		st.set("cond","0")
		st.exitQuest(True)
	return htmltext

 def onTalk (self,npc,player):
	htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
	npcId = npc.getNpcId()
	if npcId == STATUE :
		st = player.getQuestState(qn)
		if not st : return htmltext
		id = st.getState()
		cond = st.getInt("cond")
		if cond == 0 :
		# Check if player has completed the quest The Name of Evil 2
		#          prevSt = player.getQuestState("126_TheNameOfEvil2") 
		#          prevId = prevSt.getState()
		#          if prevId != State.COMPLETED:
		#          		return htmltext	
			if id == State.COMPLETED and st.getQuestItemsCount(GAZKH) ==1:
				htmltext = "<html><body>This quest has already been completed.</body></html>"
			else : 
				htmltext = "32109-1.htm"
		elif cond == 1:
			if st.getQuestItemsCount(GAZKH_FRAGMENT) >= 30:
				self.startQuestTimer("32109-4.htm",0,npc,player)
				htmltext = "32109-4.htm"
			else :
				htmltext = "<html><body> Please come back once you have 30 Gazkh Fragments. </body></html>"
		elif cond == 2:
			self.startQuestTimer("32109-5.htm",0,npc,player)
			htmltext = "32109-5.htm"
	return htmltext

 def onKill(self,npc,player,isPet):
	st = player.getQuestState(qn)
	if not st : return
	npcId = npc.getNpcId()
	if npcId == PTERO or npcId in VELO:
		chance = st.getRandom(100)
		cond = st.getInt("cond")
		if cond == 1 and FRAGMENT_CHANCE <= chance and st.getQuestItemsCount(GAZKH_FRAGMENT) < 30 :
			st.giveItems(GAZKH_FRAGMENT,1)
			st.playSound("ItemSound.quest_itemget")
	return

QUEST = Quest(641,qn,"Attack Sailren!")

QUEST.addStartNpc(STATUE)
QUEST.addTalkId(STATUE)
QUEST.addKillId(PTERO)
for newNpc in VELO:
	QUEST.addKillId(newNpc)
