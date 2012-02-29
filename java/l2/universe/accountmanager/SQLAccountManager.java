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
package l2.universe.accountmanager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import l2.universe.Base64;
import l2.universe.Config;
import l2.universe.L2DatabaseFactory;
import l2.universe.Server;

/**
 * This class SQL Account Manager
 *
 * @author netimperia
 * @version $Revision: 2.3.2.1.2.3 $ $Date: 2005/08/08 22:47:12 $
 */
public class SQLAccountManager
{
	private static String _uname = "";
	private static String _pass = "";
	private static String _level = "";
	private static String _mode = "";
	
	public static void main(String[] args) throws SQLException, IOException, NoSuchAlgorithmException
	{
		Server.serverMode = Server.MODE_LOGINSERVER;
		Config.load();
		while (true)
		{
			System.out.println("Please choose an option:");
			System.out.println("");
			System.out.println("1 - Create new account or update existing one (change pass and access level).");
			System.out.println("2 - Change access level.");
			System.out.println("3 - Delete existing account.");
			System.out.println("4 - List accounts & access levels.");
			System.out.println("5 - Exit.");
			LineNumberReader _in = new LineNumberReader(new InputStreamReader(System.in));
			while (!(_mode.equals("1") || _mode.equals("2") || _mode.equals("3") || _mode.equals("4") || _mode.equals("5")))
			{
				System.out.print("Your choice: ");
				_mode = _in.readLine();
			}
			
			if (_mode.equals("1") || _mode.equals("2") || _mode.equals("3"))
			{
				if (_mode.equals("1") || _mode.equals("2"))
				{
					while (_uname.trim().length() == 0)
					{
						System.out.print("Username: ");
						_uname = _in.readLine().toLowerCase();
					}
				}
				else if (_mode.equals("3"))
				{
					while (_uname.trim().length() == 0)
					{
						System.out.print("Account name: ");
						_uname = _in.readLine().toLowerCase();
					}
				}
				if (_mode.equals("1"))
				{
					while (_pass.trim().length() == 0)
					{
						System.out.print("Password: ");
						_pass = _in.readLine();
					}
				}
				if (_mode.equals("1") || _mode.equals("2"))
				{
					while (_level.trim().length() == 0)
					{
						System.out.print("Access level: ");
						_level = _in.readLine();
					}
				}
			}
			
			if (_mode.equals("1"))
			{
				// Add or Update
				addOrUpdateAccount(_uname.trim(), _pass.trim(), _level.trim());
			}
			else if (_mode.equals("2"))
			{
				// Change Level
				changeAccountLevel(_uname.trim(), _level.trim());
			}
			else if (_mode.equals("3"))
			{
				// Delete
				System.out.print("Do you really want to delete this account ? Y/N : ");
				String yesno = _in.readLine();
				if (yesno.equalsIgnoreCase("Y"))
					deleteAccount(_uname.trim());
				else
					System.out.println("Deletion cancelled");
			}
			else if (_mode.equals("4"))
			{
				// List
				_mode = "";
				System.out.println("");
				System.out.println("Please choose a listing mode:");
				System.out.println("");
				System.out.println("1 - Banned accounts only (accessLevel < 0)");
				System.out.println("2 - GM/privileged accounts (accessLevel > 0)");
				System.out.println("3 - Regular accounts only (accessLevel = 0)");
				System.out.println("4 - List all");
				while (!(_mode.equals("1") || _mode.equals("2") || _mode.equals("3") || _mode.equals("4")))
				{
					System.out.print("Your choice: ");
					_mode = _in.readLine();
				}
				System.out.println("");
				printAccInfo(_mode);
			}
			else if (_mode.equals("5"))
			{
				System.exit(0);
			}
			
			_uname = "";
			_pass = "";
			_level = "";
			_mode = "";
			System.out.println();
		}
	}
	
	private static void printAccInfo(String m) throws SQLException
	{
		int count = 0;
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			CallableStatement cs = con.prepareCall(" { call accessGetList(?) } ");
			cs.setString(1, m);
			ResultSet rset = cs.executeQuery();
			while (rset.next())
			{
				System.out.println(rset.getString("login") + " -> " + rset.getInt("accessLevel"));
				count++;
			}
			rset.close();
			cs.close();
		}
		catch (Exception e)
		{
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		System.out.println("Displayed accounts: " + count + ".");
	}
	
	private static void addOrUpdateAccount(String account, String password, String level) throws IOException, SQLException, NoSuchAlgorithmException
	{
		// Encode Password
		MessageDigest md = MessageDigest.getInstance("SHA");
		byte[] newpass = password.getBytes("UTF-8");
		newpass = md.digest(newpass);
		
		// Add to Base
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			CallableStatement statement = con.prepareCall(" { call createUpdateAccount(?, ?, ?) } ");
			statement.setString(1, account);
			statement.setString(2, Base64.encodeBytes(newpass));
			statement.setString(3, level);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	private static void changeAccountLevel(String account, String level) throws SQLException
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			CallableStatement statement = con.prepareCall(" { call setAccountAccessLvl(?, ?) } ");
            statement.setString(1, account);
			statement.setString(2, level);
			statement.executeQuery();
            statement.close();
			
			System.out.println("Account " + account + " has been updated.");
		}
		catch (Exception e)
		{
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	private static void deleteAccount(String account) throws SQLException
	{
		Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            
            CallableStatement statement = con.prepareCall(" { call deleteAccount(?) } ");
            statement.setString(1, account);
            statement.executeQuery();
            statement.close();
            
            System.out.println("Account " + account + " has been deleted.");
        }
        catch (Exception e)
        {
        }
        finally
        {
            L2DatabaseFactory.close(con);
        }
	}
}
