package com.mail.member;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.crypto.Cipher;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Transactional
@Service
public class MemberService {
	@Autowired
	Membervo mv;
	@Autowired
	Memberdao md;
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	@Autowired
	Mailvo mail;
	
	
	
	//멤버 출력
	public List<HashMap<String, Object>> showmember() {
		return md.showmember(mv);
	}
	//로그인
	public Membervo memberLogin(Membervo mv) {
		String userpw=mv.getPassword();
		String id=mv.getId();
		
		if(!ObjectUtils.isEmpty(md.memberLogin(id))){
			mv=md.memberLogin(id);
			if(passwordEncoder.matches(userpw,mv.getPassword())){
				return mv;
			}else {
				return null;
			}
		}else {
			return null;
		}
	
		
	}
	//아이디 중복체크
	public boolean idCheck(String id) {
		// 이미 있는 아이디라면 false
		if (!ObjectUtils.isEmpty(md.idCheck(id))) {
			return false;
		} else {
			return true;
		}
	}
	//이메일 중복체크
	public boolean emailCheck(String email) {
		// 이미 있는 이메일이라면 false
		if (!ObjectUtils.isEmpty(md.emailCheck(email))) {
			return false;
		} else {
			return true;
		}
	}
	//회원등록
	public void memberInsert(Membervo mv) throws AddressException, MessagingException {
		String id = mv.getId();
		// 비밀번호 암호화
		mv.setPassword(passwordEncoder.encode(mv.getPassword()));
		mv.setPrivatekey(hash());
		md.memberInsert(mv);
		mv=md.memberSelect(id);
		authMailSend(mv);
	}
	
	//메일 인증 토큰값확인
	public boolean authUpdate(String pramsid,String pramstoken) {
		if(!ObjectUtils.isEmpty(md.memberSelect(pramsid))) {
			mv=md.memberSelect(pramsid);
			
			if(mv.getAuth().equals("N")) {
				if(mv.getPrivatekey().equals(pramstoken)) {
					md.authUpdate(pramsid);
					return true;
				}else {
					return false;
				}
			}
		}
		return false;
	}
	// 인증메일 전송
	public void authMailSend(Membervo mv) throws AddressException, MessagingException {
		String id =mv.getId();
		String token=mv.getPrivatekey();
		String email=mv.getEmail();
		String subject="가입인증메일";
		String body="서버알림 서비스에서 가입인증메일을 보내드립니다. 본인이 가입신청 하신게 맞다면 http://localhost:58080/auth?token="+token+"&id="+id+" 주소를 클릭해주세요!";		
		transMail(body,email,subject);
	}
	//메일전송 함수
	public void transMail(String body,String email,String subject) throws AddressException, MessagingException {
		Properties props = System.getProperties();

		props.put("mail.smtp.host", mail.getHost()); 
		props.put("mail.smtp.port", mail.getPort()); 
		props.put("mail.smtp.auth", "true"); 
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.trust", mail.getHost());

		//Session 생성 
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() 
		{ 
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() { 
				return new javax.mail.PasswordAuthentication(mail.getAdmin(), mail.getPassword()); 
			} 
		}); session.setDebug(true); //for debug

		Message mimeMessage = new MimeMessage(session);
		mimeMessage.setFrom(new InternetAddress("kutaelee0@gmail.com")); 
		mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
		mimeMessage.setSubject(subject); //제목셋팅 
		mimeMessage.setText(body); //내용셋팅 
		Transport.send(mimeMessage); //javax.mail.Transport.send() 이용
	}

	//인증메일 토큰값 랜덤하게 인코딩
	public String hash() {
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		String hashtoken = random.nextInt(1000000) + mail.getToken();

		return passwordEncoder.encode(hashtoken);
	}

	// 개인키 공개키 생성
	public Map<String, String> Rsacall(HttpServletRequest req, HttpSession session)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		session = req.getSession();

		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

		generator.initialize(1024);

		KeyPair keyPair = generator.genKeyPair();
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		// RSA 개인키
		session.setAttribute("privateKey", privateKey);

		RSAPublicKeySpec publicSpec = (RSAPublicKeySpec) keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);

		String publicKeyModulus = publicSpec.getModulus().toString(16);
		String publicKeyExponent = publicSpec.getPublicExponent().toString(16);
		Map<String, String> map = new HashMap<String, String>();
		map.put("RSAModulus", publicKeyModulus);
		map.put("RSAExponent", publicKeyExponent);
		return map;
	}

	// 복호화
	public String decryptRsa(PrivateKey privateKey, String securedValue) {

		String decryptedValue = "";

		try {
			Cipher cipher = Cipher.getInstance("RSA");

			// 암호화 된 값 : byte 배열
			// 이를 문자열 form으로 전송하기 위해 16진 문자열(hex)로 변경
			// 서버측에서도 값을 받을 때 hex 문자열을 받아 다시 byte 배열로 바꾼 뒤 복호화 과정을 수행
			byte[] encryptedBytes = hexToByteArray(securedValue);

			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

			// 문자 인코딩
			decryptedValue = new String(decryptedBytes, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return decryptedValue;
	}

	// 16진 문자열을 byte 배열로 변환
	public static byte[] hexToByteArray(String hex) {

		if (hex == null || hex.length() % 2 != 0) {
			return new byte[] {};
		}

		byte[] bytes = new byte[hex.length() / 2];

		for (int i = 0; i < hex.length(); i += 2) {
			byte value = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);

			bytes[(int) Math.floor(i / 2)] = value;
		}

		return bytes;
	}
	public String findId(String email) {
		return md.findId(email);
	}
	public boolean findPw(Membervo mv) throws AddressException, MessagingException {
		String email=mv.getEmail();
		String id=md.findPw(mv);
		if(!StringUtils.isEmpty(id)) {
			if(md.memberSelect(id).getAuth().equals("Y")) {
				String token=hash();
				md.privateKeyChange(token);
				
				String subject="비밀번호 변경 메일";
				String body="비밀번호 변경 메일입니다. 본인이 요청한게 맞다면 http://localhost:58080/changepassword?token="+token+"&id="+id+" 주소를 클릭해주세요!";		
				
				transMail(body,email,subject);
				return true;			
			}else {
				return false;
			}			
		}else {
			return false;
		}
	}
	//메일 아이디와 토큰값 확인
	public String memberCheck(Membervo mv) {
		return md.memberCheck(mv);
		
	}
	//비밀번호 변경
	public void memberPwUpdate(String id,String pw) {
		mv.setId(id);
		mv.setPassword(passwordEncoder.encode(pw));
		md.memberPwUpdate(mv);
	}
	

}
