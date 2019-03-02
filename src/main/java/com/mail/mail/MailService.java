package com.mail.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	@Autowired
	Mailvo mail;
	
	//메일전송 함수
	public void transMail(String body,String email,String subject) throws AddressException, MessagingException {
		Properties props = System.getProperties();

		props.put("mail.smtp.host", mail.getHost()); 
		props.put("mail.smtp.port", mail.getPort()); 
		props.put("mail.smtp.auth", "true"); 
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.trust", mail.getHost());

		//Session 생성 
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() 
		{ 
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() { 
				return new javax.mail.PasswordAuthentication(mail.getAdmin(), mail.getPassword()); 
			} 
		}); //session.setDebug(true); //for debug

		Message mimeMessage = new MimeMessage(session);
		mimeMessage.setFrom(new InternetAddress("servercheckbot@gmail.com")); 
		mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
		mimeMessage.setSubject(subject); //제목셋팅 
		mimeMessage.setText(body); //내용셋팅 
		Transport.send(mimeMessage); //javax.mail.Transport.send() 이용
	}
}
