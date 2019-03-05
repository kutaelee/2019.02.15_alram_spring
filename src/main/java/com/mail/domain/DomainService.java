package com.mail.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mail.mail.MailService;
import com.mail.member.Memberdao;

@Transactional
@Service
public class DomainService {
	@Autowired
	Domaindao dd;
	@Autowired
	MailService mailservice;
	@Autowired
	Memberdao md;
	public static List<HashMap<String,String>> failmember = new ArrayList<HashMap<String,String>>(); //접속실패 서버의 마스터 스레드에서 only add
	public static List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>(); //접속실패 도메인 리스트 스레드 마지막에  only add all
	
	public HashMap<String, Object> domainInsert(String url, int seq) {
			Domainvo dv=new Domainvo();
			dv.setMaster_seq(seq);
			dv.setAddress(url);
			dd.domainInsert(dv);
			return dd.domainSelect(seq);
	}

	public void domainDelete(String url, int seq) {
		Domainvo dv=new Domainvo();
		dv.setAddress(url);
		dv.setMaster_seq(seq);
		dd.domainDelete(dv);
	}



	public void statSuccessUpdate(String url) {
		dd.statSuccessUpdate(url);
	}

	@Scheduled(fixedDelay = 1000*60*10)
	public void serverCheckThread() throws AddressException, MessagingException {
		int listcount=dd.getServerCheckCount(); //서버체크해야하는 도메인리스트 개수
		int threads=6; //할당할 스레드
		int index=0; //스레드에서 가져올 인덱스번호
		int limit=listcount/threads; //스레드에서 몇개를 가져올지 정해주는 변수

		List <ServerCheckThread> threadlist=new ArrayList<ServerCheckThread>();
		
		if(listcount/threads<=0) {
			ServerCheckThread runnable=new ServerCheckThread(index,listcount,dd,mailservice,md);
			runnable.start();
			serverDownMailSend(failmember,list);
			failmember.clear();
			list.clear();
		}else {
			//스레드풀 갯수 제한 6개
			 ExecutorService executorService = Executors.newFixedThreadPool(threads);
			 
			for(int i=0;i<threads;i++) {
				index=limit*i; //인덱스번호를 리미트카운트만큼 루프돌때마다 올림
				threadlist.add(new ServerCheckThread(index,limit,dd,mailservice,md)); //루프를 돌면서 스레드 리스트생성
				executorService.execute(threadlist.get(i)); //스레드실행
				
				//루프가 한번 남았다면
				if(i==threads-2){
					threadlist.add(new ServerCheckThread(index,listcount-5,dd,mailservice,md));//마지막 리스트까지 
					executorService.execute(threadlist.get(i+1));//스레드실행
					break;
				}	
			}
			 executorService.shutdown(); //모든작업 처리 후 스레드풀 종료
			 try{
				 //만약 9분안에 처리를 못하면 강제종료시킴
		            if(!executorService.awaitTermination(9, TimeUnit.MINUTES)){
		                executorService.shutdownNow();
		            }
		        }catch(InterruptedException e){
		            e.printStackTrace();
		            executorService.shutdownNow();
		        }
			 //알림메일발송
				serverDownMailSend(failmember,list);
				failmember.clear();
				list.clear();
		}
	}
	//다운된 서버 알림메일 전송
	public void serverDownMailSend(List<HashMap<String,String>> failmember,List<HashMap<String, Object>> list) {
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
					body.append("갱신은 https://www.serverchecker.shop/domainupdateform 에서 진행하실 수 있습니다!");
					
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
	}
	//메일내용 버퍼 클리어
	public void bufferClear(List<StringBuffer> buffer) {
		for(StringBuffer i:buffer) {
			i.setLength(0);
		}	
	}
}
