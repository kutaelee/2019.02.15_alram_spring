package com.mail.domain;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Domaindao {
	@Autowired
	private SqlSession sqlssion;
	
	public List<HashMap<String,Object>> domainList(int master_seq){
		return sqlssion.selectList("domain.domainlist",master_seq);
	}
}
