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

package handlers.voicedcommandhandlers;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;
import java.util.logging.Level;

import l2.universe.Base64;
import l2.universe.L2DatabaseFactory;
import l2.universe.gameserver.handler.IVoicedCommandHandler;
import l2.universe.gameserver.model.actor.instance.L2PcInstance;

public class ChangePassword implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands = { "changepassword" };
	private static final int MIN = 6; // minimum character lengths
	private static final int MAX = 30; // miaximum character lengths
	
	@Override
	public boolean useVoicedCommand(final String command, final L2PcInstance activeChar, final String target)
	{
		if (command.equalsIgnoreCase("changepassword") && target != null)
		{
			final StringTokenizer st = new StringTokenizer(target);
			try
			{
				String curpass = null, newpass = null, repeatnewpass = null;
				if (st.hasMoreTokens())
					curpass = st.nextToken();
				if (st.hasMoreTokens())
					newpass = st.nextToken();
				if (st.hasMoreTokens())
					repeatnewpass = st.nextToken();
				
				if (!(curpass == null || newpass == null || repeatnewpass == null))
				{
					if (!newpass.equals(repeatnewpass))
					{
						activeChar.sendMessage("Your repeated password must be the same as new password!");
						return false;
					}
					if (newpass.length() < MIN)
					{
						activeChar.sendMessage("Your password must have min: " + MIN + " chars!");
						return false;
					}
					if (newpass.length() > MAX)
					{
						activeChar.sendMessage("Your password can't have more than " + MAX + " chars!");
						return false;
					}
					
					final MessageDigest md = MessageDigest.getInstance("SHA");
					
					byte[] raw = curpass.getBytes("UTF-8");
					raw = md.digest(raw);
					final String curpassEnc = Base64.encodeBytes(raw);
					String pass = null;
					int passUpdated = 0;
					
					Connection con = null;
					
					try
					{
						con = L2DatabaseFactory.getInstance().getConnection();
						final PreparedStatement statement = con.prepareStatement("SELECT password FROM accounts WHERE login=?");
						statement.setString(1, activeChar.getAccountName());
						final ResultSet rset = statement.executeQuery();
						if (rset.next())
							pass = rset.getString("password");
						rset.close();
						statement.close();
						
						if (curpassEnc.equals(pass))
						{
							byte[] password = newpass.getBytes("UTF-8");
							password = md.digest(password);
							
							final PreparedStatement ps = con.prepareStatement("UPDATE accounts SET password=? WHERE login=?");
							ps.setString(1, Base64.encodeBytes(password));
							ps.setString(2, activeChar.getAccountName());
							passUpdated = ps.executeUpdate();
							ps.close();
							_log.info("Character " + activeChar.getName() + " has changed his password from " + curpassEnc + " to " + Base64.encodeBytes(password));
							
							if (passUpdated > 0)
								activeChar.sendMessage("Your password was updated successfully!");
							else
								activeChar.sendMessage("Your password wasn't changed due troubles!");
						}
						else
						{
							L2DatabaseFactory.close(con);
							activeChar.sendMessage("Passess not matches.");
							return false;
						}
					}
					catch (final Exception e)
					{
					}
					finally
					{
						L2DatabaseFactory.close(con);
					}
				}
				else
				{
					activeChar.sendMessage("Wrong command structure! Use: .changepassword currendPassword newPassword retypePassword");
					return false;
				}
			}
			catch (final Exception e)
			{
				activeChar.sendMessage("A problem occured while changing password!");
				_log.log(Level.WARNING, "", e);
			}
		}
		else
		{
			activeChar.sendMessage("Wrong command structure! Use: .changepassword currendPassword newPassword retypePassword");
			return false;
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}
