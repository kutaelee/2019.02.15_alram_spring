package com.mail.member;


import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Memberdao {
	@Autowired
	private SqlSession sqlsession;
	
	public List<HashMap<String,Object>> showmember(Membervo mv){
		return sqlsession.selectList("member.showmember",mv);
	}
	public Membervo idCheck(String id) {
		return sqlsession.selectOne("member.idcheck",id);
	}
	public Membervo emailCheck(String email) {
		return sqlsession.selectOne("member.emailcheck",email);
	}
	public void memberInsert(Membervo mv) {
		sqlsession.insert("member.join",mv);
	}
	public Membervo memberLogin(String id){
		return sqlsession.selectOne("member.idcheck",id);	
	}
	public Membervo memberSelect(String id) {
		return sqlsession.selectOne("member.memberselect",id);	
	}
	public void authUpdate(String id) {
		sqlsession.update("member.authupdate",id);
	}
	public String findId(String email) {
		return sqlsession.selectOne("member.findid",email);
	}
	public String findPw(Membervo mv) {
		return sqlsession.selectOne("member.findpw",mv);
	}
	public void privateKeyChange(String token) {
		sqlsession.update("member.privatekeychange",token);
	}
	public String memberCheck(Membervo mv) {
		return sqlsession.selectOne("member.membercheck",mv);
	}
	public void memberPwUpdate(Membervo mv) {
		sqlsession.update("member.pwupdate",mv);
	}
	public void emailUpdate(Membervo mv) {
		sqlsession.update("member.emailupdate",mv);
	}
	public void tokenUpdate(Membervo mv) {
		sqlsession.update("member.tokenupdate",mv);
	}
	public HashMap<String,String> MemberCheckSeq (int seq){
		return sqlsession.selectOne("member.MemberCheckSeq",seq);
	}
}
