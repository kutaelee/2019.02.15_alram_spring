package com.mail.member;

import org.springframework.stereotype.Component;

@Component
public class Membervo {
	private int seq; //회원번호
	private String id;//회원아이디 외래키
	private String email;//회원 이메일 알림보낼때 사용
	private String password;//회원비밀번호
	private String stat;//회원상태 등록한 모든 도메인이 연속적으로 접속불가할때 N으로 바꿈
	private String privatekey;//회원 등록할때 인증메일 파라미터 개인키
	private String auth; //이메일 인증 여부
	
	
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public String getPrivatekey() {
		return privatekey;
	}
	public void setPrivatekey(String privatekey) {
		this.privatekey = privatekey;
	}

	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getStat() {
		return stat;
	}
	
	public void setStat(String stat) {
		this.stat = stat;
	}
	@Override
	public String toString() {
		return "Membervo [seq=" + seq + ", id=" + id + ", email=" + email + ", password=" + password + ", stat=" + stat
				+ ", privatekey=" + privatekey + ", auth=" + auth + "]";
	}
	
}
