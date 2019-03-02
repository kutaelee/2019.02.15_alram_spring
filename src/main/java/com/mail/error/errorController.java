package com.mail.error;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class errorController {
	  
	  @PostMapping(value="errorcode",produces = "aplication/text;charset=utf-8")
	  public @ResponseBody String errorCode(HttpSession session) {
		  String err=(String) session.getAttribute("error");
		  return err;
	  }
}
