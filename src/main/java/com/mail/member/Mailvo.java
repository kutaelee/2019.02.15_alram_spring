package com.mail.member;

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
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getAdmin() {
		return admin;
	}
	public void setAdmin(String admin) {
		this.admin = admin;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
