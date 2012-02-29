package handlers.scripthandlers;

import l2.universe.gameserver.handler.IScriptHandler;
import l2.universe.gameserver.handler.ScriptHandler.CallSite;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.model.entity.events.TvTEvent;

/**
 * @author BiggBoss
 *
 */
public class OnDeath implements IScriptHandler
{
	@Override
	public boolean execute(L2PcInstance activeChar, L2Object target)
	{
		if (!(target instanceof L2PcInstance))
			return true;
		
		final L2PcInstance killer = (L2PcInstance) target;
				
		TvTEvent.onKill(killer, activeChar);
		
		return true;
	}

	@Override
	public CallSite getCallSite()
	{
		return CallSite.ON_DEATH;
	}	
}
