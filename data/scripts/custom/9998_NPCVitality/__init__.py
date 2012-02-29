import sys
from l2.universe.gameserver.model.actor.instance import L2PcInstance
from l2.universe.gameserver.model.quest          import State
from l2.universe.gameserver.model.quest          import QuestState
from l2.universe.gameserver.model.quest.jython   import QuestJython as JQuest
 

qn = "9998_NPCVitality"
 

NPC=[5002]
GOLDBAR = 3470
QuestId = 9998
QuestName = "NPCVitality"
QuestDesc = "custom"
InitialHtml = "start.htm"

print "INFO Loaded: 9998_NPCVitality"
 

class Quest (JQuest) :


 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)


 def onEvent(self,event,st) :

        htmltext = event
        count = st.getQuestItemsCount(GOLDBAR)
        if count == 0 :
                return "no.htm"
        else :
            st.getPlayer().setTarget(st.getPlayer())
        if event == "1":
            count = st.getQuestItemsCount(GOLDBAR)
            if count < 1 :
                return "malo.htm"
            else : 
                 st.takeItems(GOLDBAR,1)
    	    st.getPlayer().setVitalityPoints(1999,True)
            st.playSound("ItemSound.quest_finish")
            st.setState(State.COMPLETED)
            st.exitQuest(1)
            return "up.htm"

        if event == "2":
            count = st.getQuestItemsCount(GOLDBAR)
            if count < 2 :
                return "malo.htm"
            else : 
                 st.takeItems(GOLDBAR,2)
            st.getPlayer().setVitalityPoints(12999,True)
            st.playSound("ItemSound.quest_finish")
            st.setState(State.COMPLETED)
            st.exitQuest(1)
            return "up.htm"

        if event == "3":
            count = st.getQuestItemsCount(GOLDBAR)
            if count < 3 :
                return "malo.htm"
            else : 
                 st.takeItems(GOLDBAR,3)
            st.getPlayer().setVitalityPoints(16999,True)
            st.playSound("ItemSound.quest_finish")
            st.setState(State.COMPLETED)
            st.exitQuest(1)
            return "up.htm"

        if event == "4":
            count = st.getQuestItemsCount(GOLDBAR)
            if count < 4 :
                return "malo.htm"
            else : 
                 st.takeItems(GOLDBAR,4)
            st.getPlayer().setVitalityPoints(19999,True)
            st.playSound("ItemSound.quest_finish")
            st.setState(State.COMPLETED)
            st.exitQuest(1)
            return "up.htm"

        if htmltext != event:
             st.setState(State.COMPLETED)
             st.exitQuest(1)
             return htmltext

 def onFirstTalk (self,npc,player):
        st = player.getQuestState(qn)
        if not st :
            st = self.newQuestState(player)
        return InitialHtml
QUEST = Quest(-1,str(QuestId) + "_" + QuestName,QuestDesc)

for npcId in NPC:
 QUEST.addStartNpc(npcId)
 QUEST.addFirstTalkId(npcId)
 QUEST.addTalkId(npcId)