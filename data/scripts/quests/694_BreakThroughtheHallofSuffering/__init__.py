#authoor by d0S

import sys

from l2.universe.gameserver.datatables import SkillTable
from l2.universe.gameserver.model.quest        			import State
from l2.universe.gameserver.model.quest        			import QuestState
from l2.universe.gameserver.model.quest.jython 			import QuestJython as JQuest

qn = "694_BreakThroughtheHallofSuffering"

#NPCs
Keucereus = 32548
Tepios = 32603
Tepiosinst = 32530
Mouthofekimus=32537
Klodekus = 25665
Klanikus = 25666

#items
Mark = 13691

class Quest (JQuest) :
    def __init__(self,id,name,descr):
        JQuest.__init__(self,id,name,descr)
        self.questItemIds = [Mark]

    def onAdvEvent (self,event,npc, player) :
        htmltext = event
        st = player.getQuestState(qn)
        if not st : return
        if event == "32603-02.htm" :
            st.setState(State.STARTED)
            st.playSound("ItemSound.quest_accept")
        return htmltext

    def onTalk (self,npc,player):
        htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
        st = player.getQuestState(qn)
        if not st : return htmltext
        npcId = npc.getNpcId()
        id = st.getState()
	if id == State.COMPLETED :
            htmltext = "32603-03.htm"
        elif id == State.CREATED and npcId == Tepios:
            if player.getLevel() >= 75 and player.getLevel() <= 82:
	       htmltext = "32603-01.htm"	
	    elif player.getLevel() > 82 and st.getQuestItemsCount(Mark) == 0:
		   st.giveItems(13691,1)
		   st.playSound("ItemSound.quest_middle")
		   st.setState(State.COMPLETED)
		   htmltext = "32603-05.htm"
            else :
                htmltext = "32603-00.htm"
        elif id == State.STARTED and npcId == Mouthofekimus:
            htmltext = "32537-01.htm"
	elif id == State.STARTED and npcId == Tepiosinst:
	    htmltext = "32530-1.htm"
	elif id == State.STARTED and npcId == Tepios:
	    htmltext = "32603-04.htm"
	    st.exitQuest(True)
	    if st.getQuestItemsCount(Mark) == 0:
	    	st.giveItems(13691,1)
	    st.giveItems(736,1)	
	    st.playSound("ItemSound.quest_finish")    
	return htmltext
       

QUEST       = Quest(694,qn,"Break Through the Hall of Suffering")

QUEST.addStartNpc(Tepios)
QUEST.addTalkId(Tepios)
QUEST.addStartNpc(Mouthofekimus)
QUEST.addTalkId(Mouthofekimus)