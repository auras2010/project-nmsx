package handlers.itemhandlers;

import l2.universe.gameserver.handler.IItemHandler;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.actor.L2Playable;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.quest.Quest;
import l2.universe.gameserver.model.quest.QuestState;
import l2.universe.gameserver.templates.item.L2Item;

/**
 * @author BiggBoss
 *
 */
public class QuestItems implements IItemHandler
{

	/* (non-Javadoc)
	 * @see l2.universe.gameserver.handler.IItemHandler#useItem(l2.universe.gameserver.model.actor.L2Playable, l2.universe.gameserver.model.L2ItemInstance)
	 */
	@Override
	public void useItem(L2Playable playable, L2ItemInstance item, boolean forceuse)
	{
		if(!(playable instanceof L2PcInstance))
			return;
		
		L2PcInstance player = (L2PcInstance) playable;
		
		if(!player.destroyItem("Item Handler - QuestItems", item, player, true))
			return;
		
		L2Item itm = item.getItem();
		for(Quest quest : itm.getQuestEvents())
		{
			QuestState state = player.getQuestState(quest.getName());
			if(state == null || !state.isStarted())
				continue;
			
			quest.notifyItemUse(itm, player);
		}
	}
}