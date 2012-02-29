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

import java.util.StringTokenizer;

import l2.universe.gameserver.datatables.SubPledgeSkillTree;
import l2.universe.gameserver.datatables.SubPledgeSkillTree.SubUnitSkill;
import l2.universe.gameserver.model.L2SquadTrainer;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.AcquireSkillList;
import l2.universe.gameserver.network.serverpackets.ActionFailed;
import l2.universe.gameserver.network.serverpackets.NpcHtmlMessage;
import l2.universe.gameserver.network.serverpackets.SystemMessage;
import l2.universe.gameserver.network.serverpackets.AcquireSkillList.SkillType;
import l2.universe.gameserver.templates.chars.L2NpcTemplate;
import l2.universe.util.Rnd;

/**
 * @author Vice
 */
public class L2FortSupportCaptainInstance extends L2MerchantInstance implements L2SquadTrainer
{
	public L2FortSupportCaptainInstance(int objectID, L2NpcTemplate template)
	{
		super(objectID, template);
		setInstanceType(InstanceType.L2FortSupportCaptainInstance);
	}

	private final static int[] TalismanIds =
	{
		9914,9915,9917,9918,9919,9920,9921,9922,9923,9924,
		9926,9927,9928,9930,9931,9932,9933,9934,9935,9936,
		9937,9938,9939,9940,9941,9942,9943,9944,9945,9946,
		9947,9948,9949,9950,9951,9952,9953,9954,9955,9956,
		9957,9958,9959,9960,9961,9962,9963,9964,9965,9966,
		10141,10142,10158
	};

	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		// BypassValidation Exploit plug.
		if (player.getLastFolkNPC().getObjectId() != getObjectId())
			return;

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command

		String par = "";
		if (st.countTokens() >= 1)
			par = st.nextToken();

		if (actualCommand.equalsIgnoreCase("Chat"))
		{
			int val = 0;
			try
			{
				val = Integer.parseInt(par);
			}
			catch (IndexOutOfBoundsException ioobe){}
			catch (NumberFormatException nfe){}

			showMessageWindow(player, val);
		}
		else if (actualCommand.equalsIgnoreCase("ExchangeKE"))
		{
			String filename = null;
			int item = TalismanIds[Rnd.get(TalismanIds.length)];
			
			if (!(player.getWeightPenalty() >= 3 || (player.getInventoryLimit() * 0.8 <= player.getInventory().getSize())))
			{
				if (player.destroyItemByItemId("FortSupportCaptainKE", 9912, 10, this, false))
				{
					SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_DISAPPEARED);
					msg.addItemName(9912);
					msg.addNumber(10);
					player.sendPacket(msg);
					
					player.addItem("FortSupportCaptainKE", item, 1, player, true);
					
					filename = "data/html/fortress/supportunit-talisman.htm";
				}
				else
				{
					filename = "data/html/fortress/supportunit-noepau.htm";
				}
				showChatWindow(player, filename);
			}
			else
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT));
			}
		}
		else if (command.equals("subskills"))
		{
			if (player.getClan() != null)
			{
				if (player.isClanLeader())
				{
					AcquireSkillList skilllist = new AcquireSkillList(SkillType.SubUnit);
					SubUnitSkill[] array = SubPledgeSkillTree.getInstance().getAvailableSkills(player.getClan());
					if (array.length == 0)
					{
						player.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);
						return;
					}
					for (SubUnitSkill sus : array)
					{
						skilllist.addSkill(sus.getSkill().getId(), sus.getSkill().getLevel(), sus.getSkill().getLevel(), sus.getReputation(), 0);
					}
					player.sendPacket(skilllist);
				}
				else
				{
					String filename = "data/html/fortress/supportunit-nosquad.htm";
					showChatWindow(player, filename);
				}
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	public void showChatWindow(L2PcInstance player)
	{
		if (player.getClan() == null || getFort().getOwnerClan() == null || player.getClan() != getFort().getOwnerClan())
		{
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getHtmlPrefix(), "data/html/fortress/supportunit-noclan.htm");
			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
			return;
		}

		showMessageWindow(player, 0);
	}

	private void showMessageWindow(L2PcInstance player, int val)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);

		String filename;

		if (val == 0)
			filename = "data/html/fortress/supportunit.htm";
		else
			filename = "data/html/fortress/supportunit-" + val + ".htm";

		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player.getHtmlPrefix(), filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcId%", String.valueOf(getNpcId()));
		if (getFort().getOwnerClan() != null) 
			html.replace("%clanname%", getFort().getOwnerClan().getName());
		else
			html.replace("%clanname%", "NPC");
		player.sendPacket(html);
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
	
	/* (non-Javadoc)
	 * @see l2.universe.gameserver.model.actor.L2SquadTrainer#showSubUnitSkillList(l2.universe.gameserver.model.actor.instance.L2PcInstance)
	 */
	@Override
	public void showSubUnitSkillList(L2PcInstance player)
	{
		onBypassFeedback(player, "subskills");
	}
}