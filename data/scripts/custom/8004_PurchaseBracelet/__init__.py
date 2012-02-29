# Created by L2Emu Team
import sys
from l2.universe.gameserver.model.quest        import State
from l2.universe.gameserver.model.quest        import QuestState
from l2.universe.gameserver.model.quest.jython import QuestJython as JQuest

qn = "8004_PurchaseBracelet"

Angel_Bracelet = [10316, 10317, 10318, 10319, 10320, 10408]
Devil_Bracelet = [10322, 10323, 10324, 10325, 10326, 10408]

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onAdvEvent (self,event,npc, player) :
    htmltext = event
    st = player.getQuestState(qn)
    if not st : return
    if st.getQuestItemsCount(6471) >= 20 and st.getQuestItemsCount(5094) >= 50 and st.getQuestItemsCount(9814) >= 4 and st.getQuestItemsCount(9816) >= 5 and st.getQuestItemsCount(9817) >= 5 and st.getQuestItemsCount(9815) >= 3 and st.getQuestItemsCount(57) >= 7500000 :
        st.takeItems(6471,25)
        st.takeItems(5094,50)
        st.takeItems(9814,4)
        st.takeItems(9816,5)
        st.takeItems(9817,5)
        st.takeItems(9815,3)
        st.takeItems(57,7500000)
        htmltext = ""
        if event == "Little_Devil" :
           st.giveItems(Devil_Bracelet[st.getRandom(len(Devil_Bracelet))], 1)
        elif event == "Little_Angel" :
          st.giveItems(Angel_Bracelet[st.getRandom(len(Angel_Bracelet))], 1)
    else :
        htmltext = "30098-no.htm"
    st.exitQuest(1)
    return htmltext

 def onTalk(self,npc,player):
    htmltext = ""
    st = player.getQuestState(qn)
    if not st :
      st = self.newQuestState(player)
    htmltext = "30098.htm"
    return htmltext

QUEST = Quest(-1,qn,"custom")

QUEST.addStartNpc(30098)

QUEST.addTalkId(30098)
