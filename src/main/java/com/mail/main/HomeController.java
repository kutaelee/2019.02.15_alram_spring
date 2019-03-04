package com.mail.main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mail.domain.DomainController;

@Controller
public class HomeController {
	@RequestMapping("**/favicon.ico")
	public String favicon() {
		return "forward:/resources/img/fav.ico";
	}

	@GetMapping(value = "/")
	public String home(HttpServletRequest req) {
		// 추후에 로그인 ip검증 사용예정
		String ip = req.getHeader("X-FORWARDED-FOR");

		// proxy 환경일 경우
		if (ip == null || ip.length() == 0) {
			ip = req.getHeader("Proxy-Client-IP");
		}

		// 웹로직 서버일 경우
		if (ip == null || ip.length() == 0) {
			ip = req.getHeader("WL-Proxy-Client-IP");
		}

		if (ip == null || ip.length() == 0) {
			ip = req.getRemoteAddr();
		}
		return "home";
	}


	
	
	@PostMapping(value = "addresscheck")
	public @ResponseBody boolean addrescheck(HttpServletRequest req) {
		String url = req.getParameter("url");
		HttpURLConnection connection = null;
		HttpURLConnection.setFollowRedirects(true);
		
		if (url.length() < 9) {
		
			return false;
		}
		url = DomainController.urlHeader(url);
		try {
			connection=DomainController.connectionSet(url);
				return DomainController.urlConnection(connection);
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		} finally {
			connection.disconnect();
		}
	

	}

}
