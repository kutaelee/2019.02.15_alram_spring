package com.mail.member;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.regex.Pattern;

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
public class MemberController {
	@Autowired
	Membervo mv;
	@Autowired
	MemberService ms;


	// 로그인 세션체크
	@PostMapping(value = "sessioncheck")
	public @ResponseBody boolean sessionCheck(HttpSession session) {
		if (!ObjectUtils.isEmpty(session.getAttribute("userseq"))) {
			return true;
		} else {
			return false;
		}
	}

	// 로그아웃
	@PostMapping(value = "logout")
	public @ResponseBody boolean logout(HttpSession session) {
		session.invalidate();
		return true;
	}

	// 메일 전송완료페이지
	@GetMapping(value = "sendmail")
	public String sendMail(HttpServletRequest req, HttpSession session) {

		if (!ObjectUtils.isEmpty(session.getAttribute("sendmail"))) {
			session.removeAttribute("sendmail");
			return "login";
		} else {
			return "home";
		}
	}

	// 이메일 인증
	@GetMapping(value = "auth")
	public String auth(HttpServletRequest req, HttpSession session) {
		String id = req.getParameter("id");
		String token = req.getParameter("token");
		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(token)) {
			return "home";
		} else {
			session.setAttribute("authid", id);
			session.setAttribute("authtoken", token);
			return "auth";
		}

	}

	@PostMapping(value = "authreq", produces = "aplication/text; charset=utf8")
	public @ResponseBody String authUpdate(HttpServletRequest req, HttpSession session) {
		String id = (String) session.getAttribute("authid");
		String token = (String) session.getAttribute("authtoken");

		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(token)) {
			return "아이디나 토큰값이 잘못되었습니다.";
		} else {
			if (ms.authUpdate(id, token)) {
				session.setAttribute("userid", req.getParameter("id"));
				return "인증이 완료되었습니다!";
			} else {
				return "이미 인증이 완료된 아이디거나 토큰값이 잘못되었습니다.";
			}
		}
	}

	/*
	 * // 멤버확인
	 * 
	 * @PostMapping(value = "member") public @ResponseBody List<HashMap<String,
	 * Object>> showmember() { mv.setId("admin"); return ms.showmember(); }
	 */
	// 암호화키 생성
	@PostMapping(value = "rsacall")
	public @ResponseBody Map<String, String> joinPage(HttpServletRequest req, HttpSession session)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		return ms.Rsacall(req, session);
	}

	// ID찾기
	@PostMapping(value = "findid", produces = "application/text; charset=utf8")
	public @ResponseBody String findId(HttpServletRequest req, HttpSession session) {
		String email = req.getParameter("email");
		PrivateKey privateKey = (PrivateKey) session.getAttribute("privateKey");
		email = ms.decryptRsa(privateKey, email);
		String id = ms.findId(email);
		if (!StringUtils.isEmpty(id)) {
			return id;
		} else {
			return "일치하는 정보가 없습니다";
		}

	}

	// 비밀번호 찾기
	@PostMapping(value = "findpw", produces = "application/text; charset=utf8")
	public @ResponseBody String findPw(HttpServletRequest req, HttpSession session)
			throws AddressException, MessagingException {
		String id = req.getParameter("id");
		String email = req.getParameter("email");
		PrivateKey privateKey = (PrivateKey) session.getAttribute("privateKey");
		id = ms.decryptRsa(privateKey, id);
		email = ms.decryptRsa(privateKey, email);
		mv.setId(id);
		mv.setEmail(email);

		if (ms.findPw(mv)) {
			return "이메일로 비밀번호를 변경할 주소를 보내드렸습니다!";
		} else {
			return "일치하는 정보가 없거나 메일인증을 하지 않은 아이디입니다.";
		}
	}

	// 로그인
	@PostMapping(value = "memberlogin", produces = "application/json; charset=utf8")
	public @ResponseBody String memberLogin(HttpServletRequest req, HttpSession session) {
		String id = req.getParameter("id");
		String pw = req.getParameter("pw");

		PrivateKey privateKey = (PrivateKey) session.getAttribute("privateKey");
		id = ms.decryptRsa(privateKey, id);
		pw = ms.decryptRsa(privateKey, pw);

		mv.setId(id);
		mv.setPassword(pw);

		if (null != ms.memberLogin(mv)) {
			mv = ms.memberLogin(mv);
			if (mv.getAuth().equals("Y")) {
				session.setAttribute("userseq", mv.getSeq());

				return "{\"msg\": \"로그인 성공!\"}";
			} else {
				return "{\"msg\": \"이메일 인증 후 로그인 가능합니다!\"}";
			}
		} else {
			return "{\"msg\": \"일치하는 정보가 없습니다!\"}";
		}

	}

	// 회원가입
	@PostMapping(value = "memberjoin")
	public @ResponseBody boolean memberJoin(HttpServletRequest req)
			throws NoSuchAlgorithmException, InvalidKeySpecException, AddressException, MessagingException {
		String id = req.getParameter("id");
		String pw = req.getParameter("pw");
		String email = req.getParameter("email");

		HttpSession session = req.getSession();
		PrivateKey privateKey = (PrivateKey) session.getAttribute("privateKey");
		id = ms.decryptRsa(privateKey, id);
		pw = ms.decryptRsa(privateKey, pw);
		email = ms.decryptRsa(privateKey, email);

		// 공백제거
		id.replace("\\s", "");
		pw.replace("\\s", "");
		email.replace("\\s", "");

		// 아이디 비밀번호 규격검사
		if (id.length() > 3 && id.length() < 13 && pw.length() > 3 && pw.length() < 13) {
			// 아이디 비밀번호 특수문자 검사
			if (Pattern.matches("^[a-zA-Z0-9]*$", id) && Pattern.matches("^[a-zA-Z0-9]*$", pw)) {
				// 아이디,이메일 중복검사
				if (ms.idCheck(id) && ms.emailCheck(email)) {
					mv.setId(id);
					mv.setEmail(email);
					mv.setPassword(pw);

					ms.memberInsert(mv);

					session.setAttribute("sendmail", true);
					return true;
				}
			}

		}
		return false;
	}

	// 아이디 중복검사
	@PostMapping(value = "idcheck")
	public @ResponseBody boolean idCheck(HttpServletRequest req) {
		return ms.idCheck(req.getParameter("id"));
	}

	// 이메일 중복검사
	@PostMapping(value = "emailcheck")
	public @ResponseBody boolean emailCheck(HttpServletRequest req) {
		return ms.emailCheck(req.getParameter("email"));
	}

	// 회원가입페이지
	@GetMapping(value = "joinpage")
	public String joinPage() {
		return "join";
	}

	// 로그인페이지
	@GetMapping(value = "loginpage")
	public String loginPage() {
		return "login";
	}

	// 개인정보찾기 폼
	@GetMapping(value = "memberfindpage")
	public String memberFindPage(HttpSession session) {
		if (ObjectUtils.isEmpty(session.getAttribute("userseq"))) {
			return "memberfind";
		} else {
			return "home";
		}

	}

	// 비밀번호 변경 폼
	@GetMapping(value = "changepassword")
	public String changePassWordForm(HttpServletRequest req, HttpSession session) {
		String id = req.getParameter("id");
		String token = req.getParameter("token");
		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(token)) {
			return "home";
		} else {
			mv.setId(id);
			mv.setPrivatekey(token);
			id = ms.memberCheck(mv);
			if (null != id) {
				session.setAttribute("changepwtarget", id);
				return "changepw";
			} else {
				return "home";
			}

		}

	}

	// 비밀번호 변경
	@PostMapping(value = "memberpwupdate")
	public @ResponseBody boolean memberPwUpdate(HttpServletRequest req) {
		String pw = req.getParameter("pw");

		HttpSession session = req.getSession();
		PrivateKey privateKey = (PrivateKey) session.getAttribute("privateKey");
		pw = ms.decryptRsa(privateKey, pw);
		pw.replace("\\s", "");
		if (pw.length() > 3 && pw.length() < 13 && Pattern.matches("^[a-zA-Z0-9]*$", pw)) {
			String id = (String) session.getAttribute("changepwtarget");
			if (StringUtils.isEmpty(id)) {
				return false;
			}
			System.out.println(pw);
			ms.memberPwUpdate(id, pw);
			session.removeAttribute("changepwtarget");
			return true;
		} else {
			return false;
		}
	}

	@PostMapping(value = "memberCheck")
	public @ResponseBody boolean memberCheck(HttpServletRequest req, HttpSession session) {
		String id = req.getParameter("id");
		String pw = req.getParameter("pw");

		PrivateKey privateKey = (PrivateKey) session.getAttribute("privateKey");
		id = ms.decryptRsa(privateKey, id);
		pw = ms.decryptRsa(privateKey, pw);

		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(pw)) {
			return false;
		}
		mv.setId(id);
		mv.setPassword(pw);

		if (null != ms.memberLogin(mv)) {
			return true;
		} else {
			return false;
		}

	}

	@PostMapping(value = "emailupdatesend")
	public @ResponseBody boolean emailUpdateSend(HttpServletRequest req, HttpSession session)
			throws AddressException, MessagingException {
		String email = req.getParameter("email");
		String id = null;
		PrivateKey privateKey = (PrivateKey) session.getAttribute("privateKey");
		// 비로그인시 이메일변경
		if (ObjectUtils.isEmpty(session.getAttribute("userseq"))) {
			id = req.getParameter("id");
			id = ms.decryptRsa(privateKey, id);
		} else {
			// 마이페이지에서 이메일변경
			int seq = (Integer) session.getAttribute("userseq");
			id = ms.seqSelectId(seq);
		}

		email = ms.decryptRsa(privateKey, email);

		// 값이 비어있거나 이메일이 중복이라면
		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(email) || !ms.emailCheck(email)) {
			return false;
		} else {
			ms.emailUpdateSend(id, email);
			return true;
		}
	}

	@GetMapping(value = "emailupdateform")
	public String emailUpdateForm(HttpServletRequest req, HttpSession session) {
		String id = req.getParameter("id");
		String token = req.getParameter("token");
		String email = req.getParameter("email");
		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(token) || StringUtils.isEmpty(email)) {
			return "home";
		} else {
			session.setAttribute("emailupdateid", id);
			session.setAttribute("emailupdatetoken", token);
			session.setAttribute("emailupdatetagret", email);
			return "auth";
		}
	}

	@PostMapping(value = "emailupdate", produces = "aplication/text;charset=utf-8")
	public @ResponseBody String emailUpdate(HttpServletRequest req, HttpSession session) {
		String id = (String) session.getAttribute("emailupdateid");
		String token = (String) session.getAttribute("emailupdatetoken");
		String email = (String) session.getAttribute("emailupdatetagret");

		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(token) || StringUtils.isEmpty(email)) {
			return "인증값이 잘못되었습니다.";
		} else {
			if (ms.authUpdate(id, token)) {
				ms.emailUpdate(id, email);
				return "이메일이 정상적으로 변경되었습니다!<br/><br/>가입인증도 아직 하지 않으셨다면 같이 완료되었습니다!";
			} else {
				return "인증값이 잘못되었거나 세션이 만료된 페이지입니다.";
			}
		}
	}

	// 가입인증메일 재전송
	@PostMapping(value = "emailresend")
	public @ResponseBody boolean emailResend(HttpServletRequest req, HttpSession session)
			throws AddressException, MessagingException {
		String id = req.getParameter("id");
		PrivateKey privateKey = (PrivateKey) session.getAttribute("privateKey");
		id = ms.decryptRsa(privateKey, id);

		if (StringUtils.isEmpty(id)) {
			return false;
		} else {
			if (ms.emailResend(id)) {
				return true;
			} else {
				return false;
			}
		}
	}

	@GetMapping(value = "mypage")
	public String myPage(HttpSession session) {
		return "mypage";
	}

	@PostMapping(value = "passwordcheck")
	public @ResponseBody boolean passwordCheck(HttpServletRequest req, HttpSession session) {
		int seq = (Integer) session.getAttribute("userseq");
		String pw = req.getParameter("pw");
		PrivateKey privateKey = (PrivateKey) session.getAttribute("privateKey");
		pw = ms.decryptRsa(privateKey, pw);
		String id = ms.seqSelect(seq, pw);
		if (!StringUtils.isEmpty(id)) {
			session.setAttribute("changepwtarget", id);
			return true;
		} else {
			return false;
		}
	}

	@PostMapping(value = "memberSecession")
	public @ResponseBody boolean memberSecession(HttpServletRequest req, HttpSession session) {
		int seq = (Integer) session.getAttribute("userseq");
		ms.memberSecession(seq);
		session.invalidate();
		return true;
	}

	/*
	 * @PostMapping(value="googleurl") public @ResponseBody String googleUrl() {
	 * 구글code 발행 OAuth2Operations oauthOperations =
	 * googleConnectionFactory.getOAuthOperations(); String url =
	 * oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE,
	 * googleOAuth2Parameters);
	 * 
	 * return url;
	 * 
	 * }
	 * 
	 * 
	 * @GetMapping(value = "/oauth2callback") public String
	 * doSessionAssignActionPage(HttpServletRequest req,HttpSession session)throws
	 * Exception{ String code = req.getParameter("code");
	 * 
	 * OAuth2Operations oauthOperations =
	 * googleConnectionFactory.getOAuthOperations(); AccessGrant accessGrant =
	 * oauthOperations.exchangeForAccess(code ,
	 * googleOAuth2Parameters.getRedirectUri(), null);
	 * 
	 * String accessToken = accessGrant.getAccessToken(); Long expireTime =
	 * accessGrant.getExpireTime(); if (expireTime != null && expireTime <
	 * System.currentTimeMillis()) { accessToken = accessGrant.getRefreshToken(); }
	 * Connection<Google> connection =
	 * googleConnectionFactory.createConnection(accessGrant); Google google =
	 * connection == null ? new GoogleTemplate(accessToken) : connection.getApi();
	 * google PlusOperations plusOperations = google.plusOperations(); Person
	 * profile = plusOperations.getGoogleProfile();
	 * mv.setEmail(profile.getEmailAddresses().toString());
	 * mv.setId("google_"+profile.getId()); String seq=ms.socialCheck(mv);
	 * if(!StringUtils.isEmpty(seq)) { session.setAttribute("userseq", seq); }else {
	 * ms.socialJoin(mv); seq=ms.socialCheck(mv); session.setAttribute("userseq",
	 * seq); }
	 * 
	 * return "home"; }
	 */

	@PostMapping(value = "googlelogin")
	public @ResponseBody boolean googleLogin(HttpServletRequest req, HttpSession session) {
		String email = req.getParameter("email");		
		PrivateKey privateKey = (PrivateKey) session.getAttribute("privateKey");
		email = ms.decryptRsa(privateKey, email);
		String[] id=email.split("@");

		mv.setId("google_"+id[0]);
		mv.setEmail(email);

		
		if (!ObjectUtils.isEmpty(ms.socialCheck(mv))) {
			/*기존회원 로그인*/
			session.setAttribute("userseq", ms.socialCheck(mv));
			session.setAttribute("social",true);
			return true; 
		} else {
			/* 신규가입 */
			if(ms.emailCheck(email)) {
				ms.socialJoin(mv);
				session.setAttribute("userseq", ms.socialCheck(mv));
				session.setAttribute("social",true);
				return true; 
			}else {
				return false;
			}
		
		}
		
	}
	@PostMapping(value="socialusercheck")
	public @ResponseBody boolean socialUserCheck(HttpSession session) {
		if(ObjectUtils.isEmpty(session.getAttribute("social"))){
			return false;
		}else {
			return true;
		}
	}
	

	@PostMapping(value="naverlogin")
	public @ResponseBody boolean naverLogin(HttpServletRequest req,HttpSession session) {
		String email = req.getParameter("email");
		String[] id=email.split("@");
		mv.setId("naver_"+id[0]);
		mv.setEmail(email);

		
		if (!ObjectUtils.isEmpty(ms.socialCheck(mv))) {
			/*기존회원 로그인*/
			session.setAttribute("userseq", ms.socialCheck(mv));
			session.setAttribute("social",true);
			return true; 
		} else {
			/* 신규가입 */
			if(ms.emailCheck(email)) {
				ms.socialJoin(mv);
				session.setAttribute("userseq", ms.socialCheck(mv));
				session.setAttribute("social",true);
				return true; 
			}else {
				return false;
			}
		
		}
		
	}

}