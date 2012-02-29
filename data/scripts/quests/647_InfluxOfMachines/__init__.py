# Fix by Cromir & Black Night for Kilah
# Fix by Crypto for L2LastHope Freya Version
# Quest: Influx of Machines
import sys
from l2.universe import Config
from l2.universe.gameserver.model.quest import State
from l2.universe.gameserver.model.quest import QuestState
from l2.universe.gameserver.model.quest.jython import QuestJython as JQuest

qn = "647_InfluxOfMachines"

#Settings: drop chance in %
DROP_CHANCE=30
#Set this to non-zero to use 100% recipes as reward instead of default 60%
ALT_RP_100=0

DESTROYED_GOLEM_SHARD = 15521
RECIPES_60= [6887, 6881, 6897, 7580, 6883, 6899, 6891, 6885, 6893, 6895]
RECIPES_100= [6888, 6882, 6898, 7581, 6884, 6900, 6892, 6886, 6894, 6896]


class Quest (JQuest) :

 def __init__(self,id,name,descr):
     JQuest.__init__(self,id,name,descr)
     self.questItemIds = [DESTROYED_GOLEM_SHARD]

 def onEvent (self,event,st) :
    htmltext = event
    if event == "32069-02.htm" :
       st.set("cond","1")
       st.setState(State.STARTED)
       st.playSound("ItemSound.quest_accept")
    elif event == "32069-06.htm" :
       if st.getQuestItemsCount(DESTROYED_GOLEM_SHARD) >= 500:
          st.takeItems(DESTROYED_GOLEM_SHARD,500)
          if ALT_RP_100 == 0 :
            item = RECIPES_60[st.getRandom(len(RECIPES_60))]
          else :
            item = RECIPES_100[st.getRandom(len(RECIPES_100))]
          st.giveItems(item,1)
          st.playSound("ItemSound.quest_finish")
          st.exitQuest(1)
       else:
          htmltext = "32069-04.htm"
    return htmltext

 def onTalk (self, npc, player):
    st = player.getQuestState(qn)
    htmltext = Quest.getNoQuestMsg(player)
    if st :
        npcId = npc.getNpcId()
        cond = st.getInt("cond")
        count = st.getQuestItemsCount(DESTROYED_GOLEM_SHARD)
        if cond == 0 :
            if player.getLevel() >= 70 :
                htmltext = "32069-01.htm"
            else:
                htmltext = "32069-03.htm"
                st.exitQuest(1)
        elif st.getState() == State.STARTED :
            if cond==1 or count < 500 :
                htmltext = "32069-04.htm"
            elif cond==2 and count >= 500 :
                htmltext = "32069-05.htm"
    return htmltext

 def onKill(self,npc,player,isPet):
    partyMember = self.getRandomPartyMember(player,"1")
    if not partyMember: return
    st = partyMember.getQuestState(qn)
    if st :
        if st.getState() == State.STARTED :
            npcId = npc.getNpcId()
            cond = st.getInt("cond")
            count = st.getQuestItemsCount(DESTROYED_GOLEM_SHARD)
            if cond == 1 and count < 500:
                chance = DROP_CHANCE*Config.RATE_QUEST_DROP
                numItems, chance = divmod(chance,100)
                if st.getRandom(100) < chance : 
                    numItems += 1
                if numItems :
                    if count + numItems >= 500 :
                        numItems = 500 - count
                        st.playSound("ItemSound.quest_middle")
                        st.set("cond","2")
                    else:
                        st.playSound("ItemSound.quest_itemget")
                    st.giveItems(DESTROYED_GOLEM_SHARD,int(numItems))
    return

QUEST       = Quest(647,qn,"Influx of Machines")

QUEST.addStartNpc(32069)

QUEST.addTalkId(32069)

for i in range(22801,22812):
   QUEST.addKillId(i)