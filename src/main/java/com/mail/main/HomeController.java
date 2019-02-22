package com.mail.main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 변경 테스트
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@GetMapping(value = "/")
	public String home(Locale locale,HttpServletRequest req) {
		logger.info("Welcome home! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);
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
	         
		System.out.println(ip);
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
