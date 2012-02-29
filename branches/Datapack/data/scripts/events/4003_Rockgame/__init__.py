### Settings
qn = "4003_Rockgame"
NPC         = [6953]
QuestId     = 4003
QuestName   = "Rockgame"
QuestDesc   = "custom"
InitialHtml = "1.htm"

### Items - Format [name, eventId, giveItemId, giveItemQty, takeItem1Id, takeItem1Qty, takeItem2Id, takeItem2Qty]
Items       = [
["1000_Rock", 1001, 1000, 57, 1000, 57, 1],
["1000_Paper", 1002, 1000, 57, 1000, 57, 1],
["1000_Scissors", 1003, 1000, 57, 1000, 57, 1]
]

### ---------------------------------------------------------------------------
### DO NOT MODIFY BELOW THIS LINE
### ---------------------------------------------------------------------------

print "importing " + QuestDesc + ": " + str(QuestId) + ": " + QuestName + ": " + str(len(Items)) + " item(s)",
import sys
from l2.universe.gameserver.model.quest import State
from l2.universe.gameserver.model.quest import QuestState
from l2.universe.gameserver.model.quest.jython import QuestJython as JQuest

### doRequestedEvent
def do_RequestedEvent(event, st, giveItemId, giveItemQty, takeItem1Id, takeItem1Qty, takeItem2Id, takeItem2Qty) :
    if event == "1001" :
        n = st.getRandom(300)
        if st.getQuestItemsCount(takeItem1Id) >= takeItem1Qty :
            st.takeItems(takeItem1Id, takeItem1Qty)
            if n <= 100 :
                st.giveItems(57,1000)
                return "I chose Rock as well. Looks like a tie! Better Luck next time!"
            if n <= 200 and n > 100 :
                return "I chose Paper. Paper covers Rock, you lose. Better Luck next time!"
            if n > 200 :
                st.giveItems(57,2000)
                return "I chose Scissors. Rock smashes Scissors. You Win! Congrats!"
        else :
            return "You do not have enough adena."
        
    if event == "1002" :
        n = st.getRandom(300)
        if st.getQuestItemsCount(takeItem1Id) >= takeItem1Qty :
            st.takeItems(takeItem1Id, takeItem1Qty)
            if n <= 100 :
                st.giveItems(57,2000)
                return "I chose Rock. Paper covers Rock. You Win! Congrats!"
            if n <= 200 and n > 100 :
                st.giveItems(57,1000)
                return "I chose Paper as well. Looks like a tie! Better Luck next time!"
            if n > 200 :
                return "I chose Scissors. Scissors cut Paper, you lose. Better Luck next time!"
        else :
            return "You do not have enough adena."
        
    if event == "1003" :
        n = st.getRandom(300)
        if st.getQuestItemsCount(takeItem1Id) >= takeItem1Qty :
            st.takeItems(takeItem1Id, takeItem1Qty)
            if n <= 100 :
                return "I chose Rock. Rock smashes Scissors, you lose. Better Luck next time!"
            if n <= 200 and n > 100 :
                st.giveItems(57,2000)
                return "I chose Paper. Scissors cut Paper. You Win! Congrats!"
            if n > 200 :
                st.giveItems(57,1000)
                return "I chose Scissors as well. Looks like a tie! Better Luck next time!"
        else :
            return "You do not have enough adena."
 
### main code
class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event

    if event == "0":
        return InitialHtml

    for item in Items:
        if event == str(item[1]):
            htmltext = do_RequestedEvent(event, st, item[1], item[2], item[3], item[4], item[5], item[6])

    return htmltext

 def onTalk (Self,npc,player):
   htmltext = "<html><head><body>I have nothing to say with you</body></html>"
   return InitialHtml

### Quest class and state definition
QUEST       = Quest(QuestId,qn,QuestDesc)


for item in NPC:
   QUEST.addStartNpc(item)
   QUEST.addTalkId(item)
###   QUEST.addTalkId(npcId)
### Quest NPC initialization
###   STARTED.addTalkId(item)

print "...done"
