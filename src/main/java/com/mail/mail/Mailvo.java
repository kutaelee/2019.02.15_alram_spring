package com.mail.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Mailvo {
	@Value("#{mail['mail.host']}")
	private String host;
	@Value("#{mail['mail.admin']}")
	private String admin;
	@Value("#{mail['mail.password']}")
	private String password;
	@Value("#{mail['mail.port']}")
	private String port;
	@Value("#{mail['mail.token']}")
	private String token;
	//only getter
	public String getHost() {
		return host;
	}
	public String getAdmin() {
		return admin;
	}
	public String getPassword() {
		return password;
	}
	public String getPort() {
		return port;
	}
	public String getToken() {
		return token;
	}
	

}
