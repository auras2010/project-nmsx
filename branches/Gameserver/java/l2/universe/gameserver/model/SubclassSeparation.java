/*
 * $Header: Item.java, 2/08/2005 00:49:12 luisantonioa Exp $
 * 
 * $Author: luisantonioa $ $Date: 2/08/2005 00:49:12 $ $Revision: 1 $ $Log:
 * Item.java,v $ Revision 1 2/08/2005 00:49:12 luisantonioa Added copyright
 * notice
 * 
 * 
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

public class SubclassSeparation
{
	private final int _playerId;
	private final int _subclassChosen;
	private final String _newAccountName;
	private final String _accountName;
	private final int _sex;
	public SubclassSeparation(int playerId, int subclassChosen, String accountName, String newAccountName, int sex)
	{
		_playerId = playerId;
		_subclassChosen = subclassChosen;
		_accountName = accountName;
		_newAccountName = newAccountName;
		_sex = sex;
	}
	public int getPlayerId() { return _playerId; }
	public int getSubclassChosen() { return _subclassChosen; }
	public String getAccountName() { return _accountName; }
	public String getNewAccountName() { return _newAccountName; }
	public int getSex() { return _sex; }
}
