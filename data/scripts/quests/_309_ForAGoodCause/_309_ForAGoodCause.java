/*
* #Ported from Bloodshed python script.
* @author lordofdest
*/

package quests._309_ForAGoodCause;

import l2.universe.gameserver.model.actor.L2Npc;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.State;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.model.quest.jython.QuestJython;
import l2.universe.util.Rnd;
import l2.universe.Config;

public final class _309_ForAGoodCause extends QuestJython
{

    private static final String QUEST_NAME = "309_ForAGoodCause";
    private static final int QUEST_ID = 309;
    private static final int QUEST_MIN_LEVEL = 82;
    
    //quest npc
    private static final int ATRA = 32647;
    
    //quest monsters
    private static final int[] MUCROKIANS = 
    {
        22650, //MUCROKIAN_FANATIC
        22651, //MUCROKIAN_ASCETIC
        22652, //MUCROKIAN_SAVIOR
        22653, //MUCROKIAN_PREACHER
        22654  //CONTAMINATED_MUCROKIAN
    };
    
    private static final int CHANGED_MUCROKIAN = 22655;

    //quest items
    private static final int MUCROKIAN_HIDE = 14873;
    private static final int FALLEN_MUCROKIAN_HIDE = 14874;
    
    //base drop chance of quest items 
    private static final int MUCROKIAN_HIDE_CHANCE = 100;
    private static final int FALLEN_HIDE_CHANCE = 100;
    
    //rewards
    private static final int EXCHANGE_REC_MOIRAI_MAGE_MUCROKIAN_HIDE_COUNT = 240;
    private static final int REC_MOIRAI_MAGE_REWARD_COUNT = 6;
    private static final int[] REC_MOIRAI_MAGE =
    {
        15777, //REC_MOIRAI_CIRCLET_60
        15780, //REC_MOIRAI_TUNIC_60
        15783, //REC_MOIRAI_HOSE_60
        15786, //REC_MOIRAI_GLOVES_60
        15789, //REC_MOIRAI_SHOES_60
        15790  //REC_MOIRAI_SIGIL_60
    };

    private static final int EXCHANGE_PART_MOIRAI_MAGE_MUCROKIAN_HIDE_COUNT = 180;
    private static final int PART_MOIRAI_MAGE_REWARD_COUNT = 6;
    private static final int PART_MOIRAI_MAGE_MIN_REWARD_ITEM_COUNT = 3;
    private static final int PART_MOIRAI_MAGE_MAX_REWARD_ITEM_COUNT = 9;
    private static final int[] PART_MOIRAI_MAGE =
    {
        15647, //PART_MOIRAI_CIRCLET_
        15650, //PART_MOIRAI_TUNIC_60
        15653, //PART_MOIRAI_HOSE_60
        15656, //PART_MOIRAI_GLOVES_60
        15659, //PART_MOIRAI_SHOES_60
        15692  //PART_MOIRAI_SIGIL_60
    };

    public _309_ForAGoodCause(int questID, String name, String description)
    {
        super(questID, name, description);

        addStartNpc(ATRA);
        addTalkId(ATRA);
        
        for (int currentNPCID : MUCROKIANS)
            addKillId(currentNPCID);

        addKillId(CHANGED_MUCROKIAN);

//        questItemIds = new int[] {MUCROKIAN_HIDE, FALLEN_MUCROKIAN_HIDE};
    }
    
    private String onExchangeRequest(QuestState questState, int exchangeID)
    {
        String resultHtmlText = "32647-13.htm";
        
        long fallenMucrokianHideCount = questState.getQuestItemsCount(FALLEN_MUCROKIAN_HIDE);
        if (fallenMucrokianHideCount > 0)
        {
            questState.takeItems(FALLEN_MUCROKIAN_HIDE, fallenMucrokianHideCount);
            questState.giveItems(MUCROKIAN_HIDE, fallenMucrokianHideCount * 2);
            fallenMucrokianHideCount = 0;
        }
        
        long mucrokianHideCount = questState.getQuestItemsCount(MUCROKIAN_HIDE);
        if (exchangeID == EXCHANGE_REC_MOIRAI_MAGE_MUCROKIAN_HIDE_COUNT && mucrokianHideCount >= EXCHANGE_REC_MOIRAI_MAGE_MUCROKIAN_HIDE_COUNT)
        {
            int currentRecipeIndex = Rnd.get(REC_MOIRAI_MAGE_REWARD_COUNT);
            
            questState.takeItems(MUCROKIAN_HIDE, EXCHANGE_REC_MOIRAI_MAGE_MUCROKIAN_HIDE_COUNT);
            questState.giveItems(REC_MOIRAI_MAGE[currentRecipeIndex], (1 * (int)Config.RATE_QUEST_REWARD_RECIPE));
            questState.playSound("ItemSound.quest_finish");
            
            resultHtmlText = "32647-14.htm";
        }
        else if (exchangeID == EXCHANGE_PART_MOIRAI_MAGE_MUCROKIAN_HIDE_COUNT && mucrokianHideCount >= EXCHANGE_PART_MOIRAI_MAGE_MUCROKIAN_HIDE_COUNT)
        {
            int currentPartIndex = Rnd.get(PART_MOIRAI_MAGE_REWARD_COUNT);
            
            int minCountWithQuestRewardMultiplier = PART_MOIRAI_MAGE_MIN_REWARD_ITEM_COUNT * (int)Config.RATE_QUEST_REWARD_MATERIAL;
            int maxCountWithQuestRewardMultiplier = PART_MOIRAI_MAGE_MAX_REWARD_ITEM_COUNT * (int)Config.RATE_QUEST_REWARD_MATERIAL;
            int currentPartCount = Rnd.get(minCountWithQuestRewardMultiplier, maxCountWithQuestRewardMultiplier);
            
            questState.takeItems(MUCROKIAN_HIDE, EXCHANGE_PART_MOIRAI_MAGE_MUCROKIAN_HIDE_COUNT);
            questState.giveItems(PART_MOIRAI_MAGE[currentPartIndex], currentPartCount);
            questState.playSound("ItemSound.quest_finish");
            
            resultHtmlText = "32647-14.htm";
        }
        
        return resultHtmlText;
    }

    @Override
    public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
    {
        String htmlText = getNoQuestMsg(player);
        QuestState questState = player.getQuestState(QUEST_NAME);
        if (null != questState)
        {
            htmlText = event;
            
            if (event.equalsIgnoreCase("32647-05.htm"))
            {
                questState.set("cond", "1");
                questState.setState(State.STARTED);
                questState.playSound("ItemSound.quest_accept");
            }
            else if (event.equalsIgnoreCase("32647-12.htm") || event.equalsIgnoreCase("32647-07.htm"))
            {
                questState.exitQuest(true);
                questState.playSound("ItemSound.quest_finish");
            }
            else if (event.equalsIgnoreCase("claimreward"))
            {
                htmlText = "32647-09.htm";
            }
            else
            {
                int exchangeID = 0;
                try
                {
                    exchangeID = Integer.parseInt(event);
                }
                catch (Exception e)
                {
                    exchangeID = 0;
                }
                
                if (exchangeID > 0)
                    htmlText = onExchangeRequest(questState, exchangeID);
            }
        }

        return htmlText;
    }

    @Override
    public String onTalk(L2Npc npc, L2PcInstance talker)
    {
        String htmlText = getNoQuestMsg(talker);
        QuestState questState = talker.getQuestState(QUEST_NAME);
        if (questState != null)
        {
            //int npcID = npc.getNpcId();
            int currentQuestCondition = questState.getInt("cond");

            QuestState reedFieldMaintenanceState = talker.getQuestState("308_ReedFieldMaintenance");
            if (reedFieldMaintenanceState != null && reedFieldMaintenanceState.getState() == State.STARTED)
            {
                htmlText = "32647-15.htm";
            }
            else if (currentQuestCondition == 0)
            {
                if (talker.getLevel() < QUEST_MIN_LEVEL)
                {
                    htmlText = "32647-00.htm";
                    questState.exitQuest(true);                    
                }
                else
                {
                    htmlText = "32647-01.htm";
                }
            }
            else if (State.STARTED == questState.getState())
            {
                if (questState.getQuestItemsCount(MUCROKIAN_HIDE) >= 1 || questState.getQuestItemsCount(FALLEN_MUCROKIAN_HIDE) >= 1)
                    htmlText = "32647-08.htm";
                else
                    htmlText = "32647-06.htm";
            }
        }

        return htmlText;
    }

    @Override
    public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
    {
        L2PcInstance partyMember = getRandomPartyMember(player, "1");
        if (null == partyMember)
            return null;
            
        QuestState questState = partyMember.getQuestState(QUEST_NAME);
        if (null == questState)
            return null;

        int killedNPCID = npc.getNpcId();

        int itemIDToGive = 0;
        int itemCountToGive = 1;
        if (CHANGED_MUCROKIAN == killedNPCID && questState.getRandom(100) < (FALLEN_HIDE_CHANCE * Config.RATE_QUEST_DROP))
        {
            itemIDToGive = FALLEN_MUCROKIAN_HIDE;
        }
        else
        {
            boolean containsKilledNPC = false;
            for (int currentNPCID : MUCROKIANS)
            {
                if (currentNPCID == killedNPCID)
                {
                    containsKilledNPC = true;
                    break;
                }
            }

            if (containsKilledNPC && questState.getRandom(100) < (MUCROKIAN_HIDE_CHANCE * Config.RATE_QUEST_DROP))
                itemIDToGive = MUCROKIAN_HIDE;
        }
            
        if (itemIDToGive > 0)
        {
            questState.giveItems(itemIDToGive, itemCountToGive);
            questState.playSound("ItemSound.quest_itemget");
        }

        return null;
    }

    public static void main(String[] args) {
        new _309_ForAGoodCause(QUEST_ID, QUEST_NAME, "For A Good Cause");
    }
}
