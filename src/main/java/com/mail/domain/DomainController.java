package com.mail.domain;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DomainController {
	@Autowired
	DomainService ds;
	@Autowired
	Domaindao dd;
	
	@GetMapping(value="domainupdateform")
	public String domainUpdateForm(HttpServletRequest req) {
		return "domain";
	}
	@PostMapping(value="domainlist")
	public @ResponseBody List<HashMap<String,Object>> domainList(HttpSession session){
		int seq=(Integer) session.getAttribute("userseq");
		return dd.domainList(seq);
	}
	@PostMapping(value="domaininsert")
	public @ResponseBody HashMap<String,Object> domainInsert(HttpServletRequest req,HttpSession session){
		int seq=(Integer)session.getAttribute("userseq");
		String url=req.getParameter("url");
		if(url.length()<9) {
			return null;
		}
		
		//중복도메인이 아니라면
		if(dupleCheck(url)) {	
			url=urlHeader(url);
			return ds.domainInsert(url,seq);
		}else {
			return null;
		}

	}
	//프로토콜 유효성 검사
	public String urlHeader(String url) {
		
		if(url.substring(0,7).equals("http://")) {
			return url;
		}else if(url.substring(0,8).equals("https://")) {
			return url;
		}else {
			return url+="http://"+url;
		}
	}
	//도메인이 중복이면 false 아니면 true
	public boolean dupleCheck(String url) {
		//빈값인지 검사
		if(StringUtils.isEmpty(url)) {
			return false;
		}
		
		String check=null;
		
		//앞자리 잘라냄
		if(url.substring(0,7).equals("http://")) {
			check=url.substring(7,url.length());
		}else{
			check=url.substring(8,url.length());
		}
	
		//www. 잘라냄
		if(check.substring(0,4).equals("www.")) {
			check=check.substring(4,check.length());
		}
		String duple=dd.domainCheck(check);
		if(StringUtils.isEmpty(duple)) {
			return true;
		}else {
			return false;
		}
	}
	@PostMapping(value="domaindelete")
	public @ResponseBody boolean domainDelete(HttpServletRequest req,HttpSession session) {
		String url=req.getParameter("url");
		if (!ObjectUtils.isEmpty(session.getAttribute("userseq"))) {
			//도메인이 없으면 false 있으면 삭제후 true 리턴
			if(dupleCheck(url)) {
				return false;
			}else {
				int seq=(Integer)session.getAttribute("userseq");
				ds.domainDelete(url,seq);
				return true;
			}
		}else {
			return false;
		}

		
	}
}
