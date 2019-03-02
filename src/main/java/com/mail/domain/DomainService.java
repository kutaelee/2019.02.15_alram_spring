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
	Domainvo dv;
	@Autowired
	MailService mailservice;
	@Autowired
	Memberdao md;
	
	public HashMap<String, Object> domainInsert(String url, int seq) {
		if (dd.domainCount(seq) < 5) {
			dv.setMaster_seq(seq);
			dv.setAddress(url);
			dd.domainInsert(dv);
			return dd.domainSelect(seq);
		} else {
			return null;
		}

	}

	public void domainDelete(String url, int seq) {
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
		}
	}
}
