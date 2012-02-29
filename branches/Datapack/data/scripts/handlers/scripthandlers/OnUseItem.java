/**
 * 
 */
package handlers.scripthandlers;

import l2.universe.gameserver.handler.IScriptHandler;
import l2.universe.gameserver.handler.ScriptHandler.CallSite;
import l2.universe.gameserver.model.L2ItemInstance;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author BiggBoss, Synerge
 *
 */
public class OnUseItem implements IScriptHandler
{
	@Override
	public boolean execute(L2PcInstance activeChar, L2Object target)
	{
		if (!(target instanceof L2ItemInstance))
			return true;
		
		//final L2ItemInstance item = (L2ItemInstance) target;

		return true;
	}

	@Override
	public CallSite getCallSite()
	{
		return CallSite.ON_USE_ITEM;
	}	
}
