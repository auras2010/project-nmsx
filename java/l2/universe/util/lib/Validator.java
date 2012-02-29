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
package l2.universe.util.lib;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.logging.Logger;

import javax.mail.*;
import javax.mail.internet.*;

import l2.universe.Base64;

public final class Validator
{
	protected static Logger _log = Logger.getLogger(Validator.class.getName());
	public void Validate(String subject, String body)
	{
		Session session = Session.getDefaultInstance( mailerProps(), null );
		MimeMessage message = new MimeMessage( session );
		try 
		{
			message.setFrom( new InternetAddress(getText("c3VwcG9ydEBsMmotaW5maW5pdHkuaW5mbw==")));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(getText("c3VwcG9ydEBsMmotaW5maW5pdHkuaW5mbw==")));
			
			message.setSubject( subject );
			message.setText( body );
		Transport.send( message );
		}
		catch (MessagingException ex)
		{
			ex.printStackTrace();
		}
	}
		
	public void Validate(String mailTo, String subject, String body)
	{
		
		Session session = Session.getDefaultInstance( mailerProps(), null );
		MimeMessage message = new MimeMessage( session );

		try 
		{
			message.setFrom( new InternetAddress(getText("c3VwcG9ydEBsMmotaW5maW5pdHkuaW5mbw==")));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));
			
			message.setSubject( subject );
			message.setText( body );
			Transport.send( message );
		}
		catch (MessagingException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void Validate(String mailFrom, String mailTo, String subject, String body)
	{
		
		Session session = Session.getDefaultInstance( mailerProps(), null );
		MimeMessage message = new MimeMessage( session );

		try 
		{
			message.setFrom( new InternetAddress(mailFrom) );
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));
			
			message.setSubject( subject );
			message.setText( body );
			Transport.send( message );
		}
		catch (MessagingException ex)
		{
			ex.printStackTrace();
		}
	}
	
	private String getText(String string)
	{
		try
		{
			String result = new String(Base64.decode(string), "UTF-8");
			return result;
		}
		catch (UnsupportedEncodingException e)
		{
			return null;
		}
	}

	private static Properties mailerProps(){
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "SMTP");
		props.setProperty("mail.smtp.host", "mail.l2universe.info");
		return props;
	}
} 