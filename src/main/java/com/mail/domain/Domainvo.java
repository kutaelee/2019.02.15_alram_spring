package com.mail.domain;

import org.springframework.stereotype.Component;

@Component
public class Domainvo {
	private String address; //서버확인용 도메인주소
	private String stat; //서버 생존 유무
	private int cnt; //서버 다운 후 재확인시 실패한 횟수
	private int master_seq; //도메인 주인의 회원번호
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public int getMaster_seq() {
		return master_seq;
	}

	public void setMaster_seq(int master_seq) {
		this.master_seq = master_seq;
	}
	@Override
	public String toString() {
		return "Domainvo [address=" + address + ", stat=" + stat + ", cnt=" + cnt + ", master_seq=" + master_seq + "]";
	}
	
}
