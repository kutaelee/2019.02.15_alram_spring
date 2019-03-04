package com.mail.domain;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.mail.mail.MailService;
import com.mail.member.Memberdao;

public class ServerCheckThread extends Thread{

	Domaindao dd;
	MailService mailservice;
	Memberdao md;
	HashMap<String,Integer> map;
	
	public ServerCheckThread(int index,int limit,Domaindao dd,MailService mailservice,Memberdao md) {
		this.dd=dd;
		this.mailservice=mailservice;
		this.md=md;	
		map=new HashMap<String,Integer>();
		map.put("index", index);
		map.put("limit", limit);
	}
	
	//serverCheck
	public void run() {
		//long start = System.currentTimeMillis();
		
		List<HashMap<String, Object>> list = dd.serverCheck(map); // 등록된 서버중 상태가 정상인 서버리스트 모두 가져옴
		
		StringBuffer url = new StringBuffer(); // 접속 url 버퍼

		List<String> failserver = new ArrayList<String>();// 접속 실패한 서버 리스트
		HttpURLConnection connection = null;
		//리스트를 돌며서버체크
		for (HashMap<String, Object> i : list) {
			
			try {
				url.append(i.get("address"));
				connection=DomainController.connectionSet(url.toString());
				if(!DomainController.urlConnection(connection)) { // urlConnection 함수는 상태코드판별 후 200이면 true 리턴
					failserver.add(url.toString());
				}
			} catch (IOException e) {
				failserver.add(url.toString());

			} finally {
				url.delete(0, url.length());
			}
		}
		connection.disconnect(); //접속확인 종료 후 소켓을 닫음
		
		//실패한 url은 3번 더 체크하며 성공할 경우 리스트에서 삭제 (서버체크 중 많은 실패 시 스레드 시간 문제로 검토 후 사용예정)
/*		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < failserver.size(); j++) {
				try {
					url.append(failserver.get(j));
					HttpURLConnection connection = (HttpURLConnection) new URL(url.toString()).openConnection();
					connection.setRequestMethod("HEAD");
					int responseCode = connection.getResponseCode();
					if (responseCode == 200) {
						failserver.remove(j);
					}
				} catch (IOException e) {

				} finally {
					url.delete(0, url.length());
				}
			}
		}*/
		list.clear();
		for(String address:failserver) {
			dd.statFailUpdate(address); //서버 접속불가 처리
			list.add(dd.getFailServer(address));
		}
		
		
		//접속 불가 서버를 가진 멤버를 리스트에 추가
		List<HashMap<String,String>> failmember = DomainService.failmember;
		for(HashMap<String, Object> i : list) {
			int seq=(Integer) i.get("master_seq");
			failmember.add(md.MemberCheckSeq(seq));	
		}
		
		DomainService.list.addAll(list);
		//long end = System.currentTimeMillis();
	}
	
}
