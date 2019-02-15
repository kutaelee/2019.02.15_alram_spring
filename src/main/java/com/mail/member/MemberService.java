package com.mail.member;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class MemberService {
	@Autowired
	Membervo mv;
	@Autowired
	Memberdao md;
	
	public List<HashMap<String,Object>> showmember(){
		System.out.println(mv.getId());
		return md.showmember(mv);
	}
}
