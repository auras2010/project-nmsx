import sys
from l2.universe import ExternalConfig
from l2.universe.gameserver.model.quest import State
from l2.universe.gameserver.model.quest import QuestState
from l2.universe.gameserver.model.quest.jython import QuestJython as JQuest

qn = "376_GiantsExploration1"

# NPC
SOBLING	= 31147
# Items
ANCIENT_PARCHMENT = 14841
BOOK1,BOOK2,BOOK3,BOOK4,BOOK5 = [14836,14837,14838,14839,14840]
# Mobs
MOBS = [22670,22671,22672,22673,22674,22675,22676,22677]

class Quest (JQuest) :

   def __init__(self,id,name,descr):
      JQuest.__init__(self,id,name,descr)
      self.questItemIds = [ANCIENT_PARCHMENT]

   def onExchangeRequest(self,event,st,qty,rem) :
      if st.getQuestItemsCount(BOOK1) >= rem and st.getQuestItemsCount(BOOK2) >= rem and st.getQuestItemsCount(BOOK3) >= rem and st.getQuestItemsCount(BOOK4) >= rem and st.getQuestItemsCount(BOOK5) >= rem :
         st.takeItems(BOOK1,rem)
         st.takeItems(BOOK2,rem)
         st.takeItems(BOOK3,rem)
         st.takeItems(BOOK4,rem)
         st.takeItems(BOOK5,rem)
         st.giveItems(int(event),qty)
         st.playSound("ItemSound.quest_finish")
         return "31147-ok.htm"
      else:
         return "31147-no.htm"

   def onAdvEvent (self,event,npc,player) :
      htmltext = event
      st = player.getQuestState(qn)
      if not st : return
      if event == "31147-02.htm" :
         st.set("cond","1")
         st.setState(State.STARTED)
         st.playSound("ItemSound.quest_accept")
      elif event == "31147-quit.htm" :
         st.unset("cond")
         st.exitQuest(1)
         st.playSound("ItemSound.quest_finish")
      elif event.isdigit() :
         if int(event) == 9967 :                                 # Recipe - Dynasty Sword (60%)
            htmltext = self.onExchangeRequest(event,st,1,10)
         elif int(event) == 9968 :                              # Recipe - Dynasty Blade (60%)
            htmltext = self.onExchangeRequest(event,st,1,10)
         elif int(event) == 9969 :                              # Recipe - Dynasty Phantom (60%)
            htmltext = self.onExchangeRequest(event,st,1,10)
         elif int(event) == 9970 :                              # Recipe - Dynasty Bow (60%)
            htmltext = self.onExchangeRequest(event,st,1,10)
         elif int(event) == 9971 :                              # Recipe - Dynasty Knife (60%)
            htmltext = self.onExchangeRequest(event,st,1,10)
         elif int(event) == 9972 :                              # Recipe - Dynasty Halberd (60%)
            htmltext = self.onExchangeRequest(event,st,1,10)
         elif int(event) == 9973 :                              # Recipe - Dynasty Cudgel (60%)
            htmltext = self.onExchangeRequest(event,st,1,10)
         elif int(event) == 9974 :                              # Recipe - Dynasty Mace (60%)
            htmltext = self.onExchangeRequest(event,st,1,10)
         elif int(event) == 9975 :                              # Recipe - Dynasty Bagh-Nakh (60%)
            htmltext = self.onExchangeRequest(event,st,1,10)
         elif int(event) == 10544 :                              # Recipe - Dynasty Staff (60%)
            htmltext = self.onExchangeRequest(event,st,1,10)
         elif int(event) == 10545 :                              # Recipe - Dynasty Crusher (60%)         
            htmltext = self.onExchangeRequest(event,st,1,10)
         elif int(event) == 9628 :                              # Leonard
            htmltext = self.onExchangeRequest(event,st,6,1)
         elif int(event) == 9629 :                              # Adamantine
            htmltext = self.onExchangeRequest(event,st,3,1)
         elif int(event) == 9630 :                              # Orichalcum
            htmltext = self.onExchangeRequest(event,st,4,1)

      return htmltext

   def onTalk (self,npc,player) :
      htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
      st = player.getQuestState(qn)
      if not st : return htmltext

      npcId = npc.getNpcId()
      cond = st.getInt("cond")

      if npcId == SOBLING:
         if st.getState() == State.STARTED :
            if st.getQuestItemsCount(BOOK1) > 0 and st.getQuestItemsCount(BOOK2) > 0 and st.getQuestItemsCount(BOOK3) > 0 and st.getQuestItemsCount(BOOK4) > 0 and st.getQuestItemsCount(BOOK5) > 0 :
               # To do
               htmltext = "31147-03.htm"
            else:
               htmltext = "31147-02a.htm"
         else:
            if player.getLevel() >= 79 :
               htmltext = "31147-01.htm"
            else :
               htmltext = "31147-00.htm"
      return htmltext

   def onKill(self,npc,player,isPet) :
	  partyMember = self.getRandomPartyMemberState(player, State.STARTED)
	  if not partyMember : return
	  st = partyMember.getQuestState(qn)
	  if not st : return
	  if st.getState() != State.STARTED : return

	  npcId = npc.getNpcId()
	  cond = st.getInt("cond")
	  if cond == 1 and npcId in MOBS :
	    chance = ExternalConfig.GiantsExploration1DropChance
	    numItems, chance = divmod(chance,100)
	    if st.getRandom(100) < chance : 
	        numItems += 1
	    if numItems :
	        st.giveItems(ANCIENT_PARCHMENT,int(numItems))
	        st.playSound("ItemSound.quest_itemget")
	  return

QUEST      = Quest(376,qn,"Exploration of the Giants Cave, Part I")

QUEST.addStartNpc(SOBLING)
QUEST.addTalkId(SOBLING)

for i in MOBS :
   QUEST.addKillId(i)