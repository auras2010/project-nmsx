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
package l2.universe.gameserver.model;

import java.util.Vector;

import l2.universe.gameserver.datatables.ItemTable;
import l2.universe.gameserver.datatables.SkillTable;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;
import l2.universe.gameserver.network.SystemMessageId;
import l2.universe.gameserver.network.serverpackets.SystemMessage;

/**
 * @author ShanSoft<br>
 */
public class L2CoreMessage
{
	private int _mid;
	private String _language,_message,_extra;
	private Vector<String> value = new Vector<String>();
	private Vector<String> extravalue = new Vector<String>();
	
	public L2CoreMessage(int mid,String language, String message, String extra)
	{		
		_mid = mid;
		_language = language;
		_message = message;
		_extra = extra;		
	}
	
	public L2CoreMessage(L2CoreMessage msg)
	{
		_mid = msg._mid;
		_language = msg._language;
		_message = msg._message;
		_extra = msg._extra;
	}
	
	public int getMessageId()
	{
		return _mid;
	}
	
	public String getLanguage()
	{
		return _language;
	}
	
	public String getMessage()
	{
		return _message;
	}
	
	public String getExtra()
	{
		return _extra;
	}
	
	public String getExtra(int num)
	{
		final String[] text = _extra.split(";");
		return text[num-1];
	}
	
	public void addString(String text)
	{
		value.add(text);
	}
	
	public void addSkillName(int id, int level)
	{
		final String text = SkillTable.getInstance().getInfo(id, level).getName();
		value.add(text);
	}
	
	public void addSkillName(int id)
	{
		final String text = SkillTable.getInstance().getInfo(id, 1).getName();
		value.add(text);
	}
	
	public void addSkillName(L2Skill skill)
	{
		value.add(skill.getName());
	}
	
	public void addItemName(int id)
	{
		final String text = ItemTable.getInstance().getTemplate(id).getName();
		value.add(text);
	}	
	
	public void addItemName(L2ItemInstance item)
	{
		value.add(item.getName());
	}
	
	public void addExtra(int num)
	{
		final String[] text = _extra.split(";");
		extravalue.add(text[num-1]);
	}
	
	public void addNumber(double num)
	{
		value.add("" + num);
	}
	
	public void addNumber(long num)
	{
		value.add("" + num);
	}
	
	public void addNumber(int num)
	{
		value.add("" + num);
	}
	
	public String renderMsg()
	{
		int i=0;

		for (String text : extravalue)
		{
			i++;
			_message = _message.replace("$E"+i, text);
		}
		
		i = 0;
		
		for (String text : value)
		{
			i++;
			_message = _message.replace("$"+i, text);
		}
		
		return _message;
	}
	
	public void sendMessage(L2PcInstance player)
	{
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1);
		sm.addString(renderMsg());
		player.sendPacket(sm);
		sm = null;
	}
}
