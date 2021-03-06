package com.mail.domain;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
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
	public static final String USER_AGENT="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.119 Safari/537.36";

	@GetMapping(value="domainupdateform")
	public String domainUpdateForm(HttpServletRequest req) throws AddressException, MessagingException {
		return "domain";
	}
	@PostMapping(value="domainlist")
	public @ResponseBody List<HashMap<String,Object>> domainList(HttpSession session){
		int seq=(Integer) session.getAttribute("userseq");
		return dd.domainList(seq);
	}
	@GetMapping(value="domainlistcount")
	public @ResponseBody boolean domainListCount(HttpSession session) {
		int seq=(Integer)session.getAttribute("userseq");
		if (dd.domainCount(seq) < 5) {
			return true;
		}else {
			return false;
		}
	}
	@PostMapping(value="domaininsert")
	public @ResponseBody HashMap<String,Object> domainInsert(HttpServletRequest req,HttpSession session) {
		int seq=(Integer)session.getAttribute("userseq");
		String url=req.getParameter("url");

		//유효성 체크
		if(url.length()<9) {
			return null;
		}
		url = urlHeader(url);

		//커넥션 체크
		try {
			HttpURLConnection connection=connectionSet(url);
			if(!urlConnection(connection)) {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//중복도메인이 아니라면
		if(dupleCheck(url)) {	
			url=urlHeader(url);
			return ds.domainInsert(url,seq);
		}else {
			return null;
		}

	}
	//커넥션 세팅
	public static HttpURLConnection connectionSet(String url) throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestProperty("User-Agent",DomainController.USER_AGENT);
		connection.setRequestMethod("HEAD");
		return connection;
	}
	//프로토콜 유효성 검사
	public static String urlHeader(String url) {
		
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
		String duple=dd.domainDupleCheck(check);

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
	
	//서버 접속 가능한지 확인 후 갱신
	@PostMapping(value="domainreload")
	public @ResponseBody boolean domainReload(HttpServletRequest req) {
		String url=req.getParameter("url");
		
		if(url.length()<9) {
			return false;
		}
		url=urlHeader(url);
		HttpURLConnection connection=null;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestProperty("User-Agent",USER_AGENT);
			connection.setRequestMethod("HEAD");
	
			if (!urlConnection(connection)) {
				return false;
			} else {
				ds.statSuccessUpdate(url);
				return true;
			}

		} catch (IOException e) {
			return false;
		} finally {
			connection.disconnect();
		}
		
	}

	//커넥션을 세팅하고 넘겨주면 상태코드를 확인하고 서버의 생존유무를 리턴
		public static boolean urlConnection(HttpURLConnection connection) {
			try {
				int responseCode = connection.getResponseCode();

				//200코드가 아닐 경우
				if (responseCode != 200) {
					
					for (int i = 0; i < 5; i++) {
						// 만약 301,302,307 코드와 같이 url과 uri가 변경된다면
						if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
								|| responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
							URL redirectUrl = new URL(connection.getHeaderField("Location")); //로케이션 값을 구해서 다시연결
							connection = (HttpURLConnection) redirectUrl.openConnection();
							responseCode = connection.getResponseCode();
							if (responseCode == 200) {
								return true; //200코드가 나오면 true 리턴
							}
						}else {
							break; //성공 코드 또는 리다이렉트 코드가 아니면 false 리턴
						}
					}
					return false; //5번 이상 리다이렉트를 한다면 서버다운으로 간주하고 false 리턴
				} else {
					return true; //성공코드이므로 true 리턴
				}

			} catch (IOException e) {
				return false; //잘못된 도메인 false 리턴
			} 
		}
}
