package com.mail.domain;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class DomainService {
	@Autowired
	Domaindao dd;
	@Autowired
	Domainvo dv;
	
	public HashMap<String, Object> domainInsert(String url, int seq) {
		if(dd.domainCount(seq)<5) {
			dv.setMaster_seq(seq);
			dv.setAddress(url);
			dd.domainInsert(dv);
			return dd.domainSelect(seq);
		}else {
			return null;
		}
	
	}

	public void domainDelete(String url,int seq) {
		dv.setAddress(url);
		dv.setMaster_seq(seq);
		dd.domainDelete(dv);
	}
	


}
