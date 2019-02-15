package com.mail.member;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MemberController {
	@Autowired
	Membervo mv;
	@Autowired
	MemberService ms;
	
	@PostMapping(value="/member")
	public @ResponseBody List<HashMap<String,Object>> showmember(){
		mv.setId("admin");
		return ms.showmember();
	}
}
