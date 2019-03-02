package com.mail.main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
	   @RequestMapping("**/favicon.ico")
	    public String favicon() {
	        return "forward:/resources/img/fav.ico";
	    }
	@GetMapping(value = "/")
	public String home(HttpServletRequest req) {
		//추후에 로그인 ip검증 사용예정
		   String ip = req.getHeader("X-FORWARDED-FOR"); 
	         
	         //proxy 환경일 경우
	         if (ip == null || ip.length() == 0) {
	             ip = req.getHeader("Proxy-Client-IP");
	         }
	 
	         //웹로직 서버일 경우
	         if (ip == null || ip.length() == 0) {
	             ip = req.getHeader("WL-Proxy-Client-IP");
	         }
	 
	         if (ip == null || ip.length() == 0) {
	             ip = req.getRemoteAddr() ;
	         }
		return "home";
	}
	//프로토콜 유효성 검사
	public String urlHeader(String url) {
		if(url.substring(0,7).equals("http://")) {
			return url;
		}else if(url.substring(0,8).equals("https://")) {
			return url;
		}else {
			return url="http://"+url;
		}
	}
	@PostMapping(value = "addresscheck")
	public @ResponseBody boolean addrescheck(HttpServletRequest req) {

		String url = req.getParameter("url");
		if(url.length()<9) {
			return false;
		}
		url=urlHeader(url);
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			if (responseCode != 200) {
				return false;
			} else {
				return true;
			}

		} catch (IOException e) {
			System.out.println(e);
			return false;
		}
	}

}
