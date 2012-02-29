# Sorrowful Sound of Flute Written By Elektra
# Fixed by mr
# Revised to match retail May 27 2010 by Elektra
import sys
from l2.universe.gameserver.model.quest import State
from l2.universe.gameserver.model.quest import QuestState
from l2.universe.gameserver.model.quest.jython import QuestJython as JQuest

qn = "363_SorrowfulSoundofFlute"

class Quest (JQuest) :

 def __init__(self,id,name,descr):
     JQuest.__init__(self,id,name,descr)
     self.questItemIds = [4319]

 def onEvent (self,event,st) :
    htmltext = event
    player = st.getPlayer()
    if event == "1" :
      if player.getLevel() >= 15 :
        htmltext = "30956_2.htm"
        st.set("cond","1")
        st.setState(State.STARTED)
        st.playSound("ItemSound.quest_accept")
      else :
        htmltext = "30956_0.htm"
        st.exitQuest(1)
    elif event == "OUTFIT" :
        st.giveItems(4318,1)
        htmltext = "30956_outfit.htm"
    elif event == "FLUTE" :
        st.giveItems(4319,1)
        htmltext = "30956_flute.htm"
    elif event == "BEER" :
        st.giveItems(4320,1)
        htmltext = "30956_beer.htm"
    return htmltext


 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != 30956 and id != State.STARTED : return htmltext

   if id == State.CREATED :
     st.set("cond","0")
   if npcId == 30956 and st.getInt("cond") == 0 :
        htmltext = "30956_1.htm"
   elif npcId == 30956 and st.getInt("cond") == 1 :
        htmltext = "30956_2.htm"
   elif npcId == 30595 and st.getInt("cond") >= 1 and npcId == 30595 and st.getInt("cond") <= 2:
        if st.getInt("cond") != 2:
            st.set("cond","2")
        htmltext = "30595_1.htm"
   elif npcId == 30458 and st.getInt("cond") >= 1 and npcId == 30458 and st.getInt("cond") <= 2:
        if st.getInt("cond") != 2:
            st.set("cond","2")
        htmltext = "30458_1.htm"
   elif npcId == 30058 and st.getInt("cond") >= 1 and npcId == 30058 and st.getInt("cond") <= 2:
        if st.getInt("cond") != 2:
            st.set("cond","2")
        htmltext = "30058_1.htm"
   elif npcId == 30057 and st.getInt("cond") >= 1 and npcId == 30057 and st.getInt("cond") <= 2:
        if st.getInt("cond") != 2:
            st.set("cond","2")
        htmltext = "30057_1.htm"
   elif npcId == 30594 and st.getInt("cond") >= 1 and npcId == 30594 and st.getInt("cond") <= 2:
        if st.getInt("cond") != 2:
            st.set("cond","2")
        htmltext = "30594_1.htm"
   elif npcId == 30956 and st.getInt("cond") == 2 :
        st.set("cond","3")
        htmltext = "30956_3.htm"
   elif npcId == 30956 and st.getInt("cond") == 3 :
        htmltext = "<html><body>Nanarin:<br><br>Please hurry and give Barbado his gift...</body></html>"
   elif npcId == 30959 and st.getInt("cond") == 3 :
        if st.getQuestItemsCount(4318) >= 1:
            st.takeItems(4318,-1)
            st.set("cond","5")
            htmltext = "30959_2.htm"
        if st.getQuestItemsCount(4319) >= 1:
            st.takeItems(4319,-1)
            st.set("cond","4")
            htmltext = "30959_1.htm"
        if st.getQuestItemsCount(4320) >= 1:
            st.takeItems(4320,-1)
            st.set("cond","5")
            htmltext = "30959_2.htm"
   elif npcId == 30959 and st.getInt("cond") == 4 :
        htmltext = "<html><body>Musician Barbado:<br><br>I have nothing more to say to you...</body></html>"
   elif npcId == 30956 and st.getInt("cond") == 4 :
        htmltext = "30956_4.htm"
        st.giveReward(4420, 1)
        st.playSound("ItemSound.quest_finish")
        st.exitQuest(1)
   elif npcId == 30956 and st.getInt("cond") == 5 :
        htmltext = "30956_5.htm"
        st.playSound("ItemSound.quest_finish")
        st.exitQuest(1)
   return htmltext


QUEST       = Quest(363,qn,"Sorrowful Sound of Flute")

QUEST.addStartNpc(30956)

QUEST.addTalkId(30956)
QUEST.addTalkId(30458)
QUEST.addTalkId(30595)
QUEST.addTalkId(30959)
QUEST.addTalkId(30594)
QUEST.addTalkId(30057)
QUEST.addTalkId(30058)
