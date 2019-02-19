package com.mail.member;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MemberController {
	@Autowired
	Membervo mv;
	@Autowired
	MemberService ms;
	
	//로그인 세션체크
	@GetMapping(value="sessioncheck")
	public @ResponseBody boolean sessioncheck(HttpSession session) {
		if(null!=session.getAttribute("userid")) {
			return true;
		}else {
			return false;
		}
	}
	//로그아웃
	@GetMapping(value="logout")
	public @ResponseBody boolean logout(HttpSession session) {
		session.invalidate();
		return true;
	}
	//메일 전송완료페이지
	@GetMapping(value="sendmail")
	public String sendmail(HttpServletRequest req,HttpSession session) {
		if(null!=session.getAttribute("sendmail")) {
			session.removeAttribute("sendmail");
			return "sendmail";
		}else {
			return "home";
		}
		
	}
	//이메일 인증
	@GetMapping(value="auth")
	public String auth(HttpServletRequest req,HttpSession session) {
		if(ms.authUpdate(req.getParameter("id"),req.getParameter("token"))) {
			session.setAttribute("userid", req.getParameter("id"));
			return "success";
		}
		return "fail";
	}
	//멤버확인
	@PostMapping(value = "member")
	public @ResponseBody List<HashMap<String, Object>> showmember() {
		mv.setId("admin");
		return ms.showmember();
	}

	// 암호화키 생성
	@PostMapping(value = "rsacall")
	public @ResponseBody Map<String, String> joinpage(HttpServletRequest req, HttpSession session) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return ms.Rsacall(req,session);
	}
	//로그인
	@PostMapping(value="memberlogin",produces = "application/json; charset=utf8")
	public @ResponseBody String memberlogin(HttpServletRequest req,HttpSession session) {
		String id = req.getParameter("id");
		String pw = req.getParameter("pw");

		PrivateKey privateKey = (PrivateKey) session.getAttribute("privateKey");
		id=ms.decryptRsa(privateKey, id);
		pw=ms.decryptRsa(privateKey, pw);
		
		mv.setId(id);
		mv.setPassword(pw);

		if(!ObjectUtils.isEmpty(ms.memberLogin(mv))) {
			mv=ms.memberLogin(mv);
			if(mv.getAuth().equals("Y")) {
				session.setAttribute("userid", mv.getId());

				return "{\"msg\": \"로그인 성공!\"}";
			}else {
				return "{\"msg\": \"이메일 인증 후 로그인 가능합니다!\"}";
			}
		}else {
			return "{\"msg\": \"일치하는 정보가 없습니다!\"}";
		}
		
		
	}
	//가입
	@PostMapping(value = "memberjoin")
	public @ResponseBody boolean memberjoin(HttpServletRequest req) throws NoSuchAlgorithmException, InvalidKeySpecException, AddressException, MessagingException {
		String id = req.getParameter("id");
		String pw = req.getParameter("pw");
		String email = req.getParameter("email");
		
		HttpSession session = req.getSession();
		PrivateKey privateKey = (PrivateKey) session.getAttribute("privateKey");
		id=ms.decryptRsa(privateKey, id);
		pw=ms.decryptRsa(privateKey, pw);
		email=ms.decryptRsa(privateKey, email);
		
		//공백제거
		id.replace("\\s", "");
		pw.replace("\\s", "");
		email.replace("\\s", "");
		
		//아이디 비밀번호 규격검사
		if(id.length()>3 && id.length()<13 && pw.length()>3 && pw.length()<13) {
			//아이디 비밀번호 특수문자 검사
			if(Pattern.matches("^[a-zA-Z0-9]*$", id) && Pattern.matches("^[a-zA-Z0-9]*$", pw)) {
				//아이디,이메일 중복검사
				if(ms.idCheck(id)&&ms.emailCheck(email)) {
					mv.setId(id);
					mv.setEmail(email);
					mv.setPassword(pw);
				
					mv=ms.memberInsert(mv);
					ms.emailSend(mv);
					session.setAttribute("sendmail", true);
					return true;
				}
			}
		
		}
		return false;
	}
	//아이디 중복검사
	@PostMapping(value="idcheck")
	public @ResponseBody boolean idcheck(HttpServletRequest req) {
		return 	ms.idCheck(req.getParameter("id"));
	}
	//이메일 중복검사
	@PostMapping(value="emailcheck")
	public @ResponseBody boolean emailcheck(HttpServletRequest req) {
		return 	ms.emailCheck(req.getParameter("email"));
	}
	//회원가입페이지
	@GetMapping(value = "joinpage")
	public String joinpage() {
		return "join";
	}
	//로그인페이지
	@GetMapping(value = "loginpage")
	public String loginpage(HttpSession session) {
		if(null!=session.getAttribute("userid")) {
			return "login";
		}else {
			return "home";
		}
		
	}
}
