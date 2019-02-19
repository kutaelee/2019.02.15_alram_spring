package com.mail.member;


import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Memberdao {
	@Autowired
	private SqlSession sqlssion;
	
	public List<HashMap<String,Object>> showmember(Membervo mv){
		return sqlssion.selectList("member.showmember",mv);
	}
	public Membervo idCheck(String id) {
		return sqlssion.selectOne("member.idcheck",id);
	}
	public Membervo emailCheck(String email) {
		return sqlssion.selectOne("member.emailcheck",email);
	}
	public void memberInsert(Membervo mv) {
		sqlssion.insert("member.join",mv);
	}
	public Membervo memberLogin(String id){
		return sqlssion.selectOne("member.idcheck",id);	
	}
	public Membervo memberSelect(String id) {
		return sqlssion.selectOne("member.memberselect",id);	
	}
	public void authUpdate(String id) {
		sqlssion.update("member.authupdate",id);
	}
}
