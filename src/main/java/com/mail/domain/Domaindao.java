package com.mail.domain;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Domaindao {
	@Autowired
	private SqlSession sqlsession;
	
	public List<HashMap<String,Object>> domainList(int master_seq){
		return sqlsession.selectList("domain.domainList",master_seq);
	}

	public void domainInsert(Domainvo dv) {
		sqlsession.insert("domain.domainInsert",dv);
	}

	public int domainCount(int seq) {
		return sqlsession.selectOne("domain.domainCount",seq);
	}
	public HashMap<String,Object> domainSelect(int master_seq){
		return sqlsession.selectOne("domain.domainSelect",master_seq);
	}

	public String domainCheck(String check) {
		return sqlsession.selectOne("domain.domainCheck",check);
	}

	public void domainDelete(Domainvo dv) {
		sqlsession.delete("domain.domainDelete",dv);
	}

	public List<HashMap<String,Object>> serverCheck() {
		return sqlsession.selectList("domain.serverCheck");
	}

	public void statFailUpdate(String address) {
		sqlsession.update("domain.statFailUpdate",address);
	}
	public HashMap<String,Object> getFailServer(String address){
		return sqlsession.selectOne("domain.getFailServer",address);
	}

	public void statSuccessUpdate(String url) {
		sqlsession.update("domain.statSuccessUpdate",url);
	}
}
