/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2.universe.gameserver.model.actor.instance;

import java.util.Collection;
import java.util.concurrent.ScheduledFuture;

import l2.universe.gameserver.GeoData;
import l2.universe.gameserver.SevenSigns;
import l2.universe.gameserver.ThreadPoolManager;
import l2.universe.gameserver.ai.CtrlIntention;
import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.model.L2Object;
import l2.universe.gameserver.model.L2Skill;
import l2.universe.gameserver.model.actor.L2Character;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.ActionFailed;
import l2.universe.gameserver.network.serverpackets.MagicSkillUse;
import l2.universe.gameserver.network.serverpackets.MyTargetSelected;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.network.serverpackets.ValidateLocation;
import l2.universe.gameserver.templates.chars.L2NpcTemplate;

/**
 * @author Layane
 *
 */
public class L2CabaleBufferInstance extends L2NpcInstance
{
	private ScheduledFuture<?> _aiTask;
	
	@Override
	public void onAction(L2PcInstance player, boolean interact)
	{
		if (!canTarget(player))
			return;
		
		player.setLastFolkNPC(this);

		if (this != player.getTarget())
		{
			// Set the target of the L2PcInstance player
			player.setTarget(this);

			// Send a Server->Client packet MyTargetSelected to the L2PcInstance player
			// The color to display in the select window is White
			player.sendPacket(new MyTargetSelected(getObjectId(), 0));

			// Send a Server->Client packet ValidateLocation to correct the L2ArtefactInstance position and heading on the client
			player.sendPacket(new ValidateLocation(this));
		}
		else if (interact)
		{
			// Calculate the distance between the L2PcInstance and the L2NpcInstance
			if (!canInteract(player))
			{
				// Notify the L2PcInstance AI with AI_INTENTION_INTERACT
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
			}
			else
			{
				// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
	}

    private class CabalaAI implements Runnable
    {
        private L2CabaleBufferInstance _caster;

        protected CabalaAI(L2CabaleBufferInstance caster)
        {
            _caster = caster;
        }

        public void run()
        {
            boolean isBuffAWinner = false;
            boolean isBuffALoser = false;

            final int winningCabal = SevenSigns.getInstance().getCabalHighestScore();
            int losingCabal = SevenSigns.CABAL_NULL;

            switch (winningCabal)
            {
            	case SevenSigns.CABAL_DAWN:
            		losingCabal = SevenSigns.CABAL_DUSK;
            		break;
            	case SevenSigns.CABAL_DUSK:
            		losingCabal = SevenSigns.CABAL_DAWN;
            		break;
            }

            /**
             * For each known player in range, cast either the positive or negative buff.
             * <BR>
             * The stats affected depend on the player type, either a fighter or a mystic.
             * <BR><BR>
             * Curse of Destruction (Loser)<BR>
             *  - Fighters: -25% Accuracy, -25% Effect Resistance<BR>
             *  - Mystics: -25% Casting Speed, -25% Effect Resistance<BR>
             * <BR><BR>
             * Blessing of Prophecy (Winner)
             *  - Fighters: +25% Max Load, +25% Effect Resistance<BR>
             *  - Mystics: +25% Magic Cancel Resist, +25% Effect Resistance<BR>
             */
            Collection<L2PcInstance> plrs = getKnownList().getKnownPlayers().values();
			for (L2PcInstance player : plrs)
			{
				if (player == null || player.isInvul())
					continue;

				final int playerCabal = SevenSigns.getInstance().getPlayerCabal(player.getObjectId());
				if (playerCabal == SevenSigns.CABAL_NULL)
					continue;
				
				switch (_caster.getNpcId())
				{
					case SevenSigns.ORATOR_NPC_ID:
						if (playerCabal == winningCabal)
						{
							if (!player.isMageClass())
							{
								if (handleCast(player, 4364))
								{
									isBuffAWinner = true;
									continue;
								}
							}
							else
							{
								if (handleCast(player, 4365))
								{
									isBuffAWinner = true;
									continue;
								}
							}
						}
						break;
					case SevenSigns.PREACHER_NPC_ID:
						if (playerCabal == losingCabal)
						{
							if (!player.isMageClass())
							{
								if (handleCast(player, 4361))
								{
									isBuffALoser = true;
									continue;
								}
							}
							else
							{
								if (handleCast(player, 4362))
								{
									isBuffALoser = true;
									continue;
								}
							}
						}
						break;
				}

				if (isBuffAWinner && isBuffALoser)
					break;
			}
        }

        private boolean handleCast(L2PcInstance player, int skillId)
        {
			if (player.isDead() || !player.isVisible() || !isInsideRadius(player, getDistanceToWatchObject(player), false, false))
                return false;
			
			// Synerge - If cant target the player dont debuff, because they will get debuffs even when inside a clan hall
			if (!GeoData.getInstance().canSeeTarget(L2CabaleBufferInstance.this, player))
				return false;

			final int skillLevel = (player.getLevel() > 40) ? 1 : 2;
            final L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
            if (player.getFirstEffect(skill) == null)
            {
                skill.getEffects(_caster, player);
                broadcastPacket(new MagicSkillUse(_caster, player, skill.getId(), skillLevel, skill.getHitTime(), 0));
                SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
                sm.addSkillName(skill);
                player.sendPacket(sm);
                sm = null;
                return true;
            }

            return false;
        }
    }

    public L2CabaleBufferInstance(int objectId, L2NpcTemplate template)
    {
        super(objectId, template);
        setInstanceType(InstanceType.L2CabaleBufferInstance);

        if (_aiTask != null)
        	_aiTask.cancel(true);

        _aiTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new CabalaAI(this), 3000, 3000);
    }

    @Override
	public void deleteMe()
    {
        if (_aiTask != null)
        {
        	_aiTask.cancel(true);
        	_aiTask = null;
        }

        super.deleteMe();
    }

    @Override
	public int getDistanceToWatchObject(L2Object object)
	{
		return 900;
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
    {
        return false;
    }
}
