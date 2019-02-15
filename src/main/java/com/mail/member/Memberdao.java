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
}
