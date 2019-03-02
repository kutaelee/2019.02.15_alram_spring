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

		//리스트를 돌며서버체크
		for (HashMap<String, Object> i : list) {
			
			try {
				url.append(i.get("address"));
				HttpURLConnection connection = (HttpURLConnection) new URL(url.toString()).openConnection();
				connection.setRequestMethod("HEAD");
				int responseCode = connection.getResponseCode();
				if (responseCode != 200) {
					failserver.add(url.toString());
				}

			} catch (IOException e) {
				failserver.add(url.toString());

			} finally {

				url.delete(0, url.length());
			}
		}
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
			dd.statFailUpdate(address);
			list.add(dd.getFailServer(address));
		}
		
		
		//접속 불가 서버를 가진 멤버를 리스트에 추가
		List<HashMap<String,String>> failmember = new ArrayList<HashMap<String,String>>();
		for(HashMap<String, Object> i : list) {
			int seq=(Integer) i.get("master_seq");
			failmember.add(md.MemberCheckSeq(seq));	
		}
		
		StringBuffer id=new StringBuffer(); //서버다운된 도메인 마스터의 아이디
		StringBuffer email=new StringBuffer(); //메일 수신자
		StringBuffer subject=new StringBuffer(); //제목
		StringBuffer body=new StringBuffer(); //내용
		
		//버퍼 클리어를 위해 리스트를 만듬
		List<StringBuffer> bufferlist= new ArrayList<StringBuffer>();

		bufferlist.add(id);
		bufferlist.add(email);
		bufferlist.add(subject);
		bufferlist.add(body);
		
		//메일전송 반복
		
		List<String> mailsendlist=new ArrayList<String>(); //메일 보낸 회원 리스트
		boolean sendsw=true;
		for(HashMap<String, String> i : failmember) {
			id.append(i.get("id"));
			email.append(i.get("email"));
			subject.append("서버알림서비스 - 서버가 다운되었습니다!");
			body.append(id).append("님 서버가 다운되었습니다!\n");
			

			//서버 도메인 정보 바디에 추가
			for(int j=0;j<list.size();j++) {
			
				if(list.get(j).get("master_seq").equals(i.get("seq"))) {
					body.append("다운된 서버의 도메인:");
					body.append(list.get(j).get("address")).append("\n");
					list.remove(j); //바디에 주소 추가 후 리스트에서 삭제
					j--; //리스트 사이즈에 맞춰 j를 하나 줄임
					
				}
			}
			body.append("서버를 정상으로 만드신 후에 도메인관리에서 갱신버튼을 눌러주세요!\n");	
			body.append("갱신하지 않으시면 알림을 다시 보내드리지 않습니다!\n");
			body.append("갱신은 http://www.serverchecker.shop/mypage 에서 진행하실 수 있습니다!");
			
			/* 만약 보낸사람 리스트에 이미 있으면 sw를 false로 만들어서 중복메일을 방지 */
			for(String x:mailsendlist) {
				if(x.equals(String.valueOf(i.get("seq")))) {
					sendsw=false;
				}
			}
			
			if(sendsw) {
				try {
					mailservice.transMail(body.toString(), email.toString(), subject.toString());
				} catch (AddressException e) {
					e.printStackTrace();
				} catch (MessagingException e) {
					e.printStackTrace();
				}//메일전송
				mailsendlist.add(String.valueOf(i.get("seq")));//보낸사람목록에 추가
				sendsw=true;
			}
		
			
			/*버퍼 초기화*/
			bufferClear(bufferlist);
		}
		
		
		//long end = System.currentTimeMillis();
	}
	public void bufferClear(List<StringBuffer> buffer) {
		for(StringBuffer i:buffer) {
			i.setLength(0);
		}	
	}
}
