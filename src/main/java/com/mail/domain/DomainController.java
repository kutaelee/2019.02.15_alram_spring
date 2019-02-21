package com.mail.domain;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
}
