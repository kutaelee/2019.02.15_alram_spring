	$.ajax({
		url:'sessioncheck',
		type:'post',
		success:function(result){
			if(result){
				alert("새로운 가입은 로그아웃 후 진행 해 주세요!");
				location.href="/";
			}
		},error:function(){
			alert("세션체크 중 문제가 발생했습니다.");
		}
	})
$(document).ready(function() {
	$('.header').load('/resources/header.html');


	
	/* 공개키 변수 */
	var RSAModulus = null;
	var RSAExponent = null;

	/* 유효성 체크 변수 */
	var idcheck = false;
	var emailcheck = false;
	var pwcheck = false;
	var pw2check = false;
	var exptext = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/; // 이메일 형식
	var re = /[~!@\#$%^&*\()\-=+_.']/gi; //특수문자


	// 공개키 요청
	$.ajax({
		url : 'rsacall',
		type : 'post',
		dataType : 'json',
		success : function(result) {
			RSAModulus = result.RSAModulus;
			RSAExponent = result.RSAExponent;
		}
	});

	/* 유효성 검사 */
	$('.join_form input').keyup(function(){
		var value=$(this).val();
		var name=$(this).prop('class');
		
		//공백제거
		value=value.replace(/ /gi, "");
		$(this).val(value);
		if(name!="join_email"){
			//특수문자 제거
			if(re.test(value)){
				$(this).val(value.replace(re,"")); 
				alert("특수문자는 사용하지말아주세요!");
			}
		}

	})
	$('.join_id').keyup(function() {
		var id = $(this).val();
		if (id.length > 3 && id.length < 13) {
			$.ajax({
				url : 'idcheck',
				type : 'post',
				data : {
					'id' : id
				},
				success : function(result) {
					if (result) {
						$('.id_info').css({
							'color' : 'cornflowerblue'
						});
						$('.id_info').text("사용해도 좋은 아이디 입니다");
						idcheck = true;
					} else {
						$('.id_info').css({
							'color' : 'salmon'
						});
						$('.id_info').text("이미 존재하는 아이디 입니다");
						idcheck = false;
					}
				}
			});
		} else {
			$('.id_info').css({
				'color' : 'salmon'
			});
			$('.id_info').text("아이디는 4자리 이상 입력해주세요");
			idcheck = false
		}
	});
	$('.join_email').keyup(function() {
		var email = $(this).val();
		if (exptext.test(email)) {
			$.ajax({
				url : 'emailcheck',
				type : 'post',
				data : {
					'email' : email
				},
				success : function(result) {
					if (result) {
						$('.email_info').css({
							'color' : 'cornflowerblue'
						});
						$('.email_info').text("사용해도 좋은 이메일 입니다");
						emailcheck = true;
					} else {
						$('.email_info').css({
							'color' : 'salmon'
						});
						$('.email_info').text("이미 존재하는 이메일 입니다");
						emailcheck = false;
					}
				}
			});
		} else {
			$('.email_info').css({
				'color' : 'salmon'
			});
			$('.email_info').text("이메일 형식에 맞춰주세요! 인증에 사용됩니다!");
			emailcheck = false;
		}
	});
	$('.join_pw').keyup(function() {
		var pw = $(this).val();
		var pw2 = $('.join_pw2').val();
		if (pw.length > 3 && pw.length < 13) {
			$('.pw_info').css({
				'color' : 'cornflowerblue'
			});
			$('.pw_info').text("사용해도 좋은 비밀번호 입니다");
			pwcheck = true;
		} else {
			$('.pw_info').css({
				'color' : 'salmon'
			});
			$('.pw_info').text("비밀번호는 4자리 이상 12자리이하 입니다");
			pwcheck = false;
		}
		if (pw == pw2) {
			$('.pw2_info').css({
				'color' : 'cornflowerblue'
			});
			$('.pw2_info').text("비밀번호가 서로 같습니다");
			pw2check = true;
		} else {
			$('.pw2_info').css({
				'color' : 'salmon'
			});
			$('.pw2_info').text("비밀번호가 서로 다릅니다");
			pw2check = false;
		}
	});
	$('.join_pw2').keyup(function() {
		var pw = $('.join_pw').val();
		var pw2 = $(this).val();
		if (pw == pw2) {
			$('.pw2_info').css({
				'color' : 'cornflowerblue'
			});
			$('.pw2_info').text("비밀번호가 서로 같습니다");
			pw2check = true;
		} else {
			$('.pw2_info').css({
				'color' : 'salmon'
			});
			$('.pw2_info').text("비밀번호가 서로 다릅니다");
			pw2check = false;
		}
	});
	// 회원가입 버튼클릭
	$('.join_btn').click(function() {
		if (idcheck && pwcheck && pw2check && emailcheck) {

			var id = $('.join_id').val();
			var email = $('.join_email').val();
			var pw = $('.join_pw').val();

			// RSA 암호키 생성
			var rsa = new RSAKey();
			rsa.setPublic(RSAModulus, RSAExponent);

			// 계정 정보 암호화
			var securedid = rsa.encrypt(id);
			var securedemail = rsa.encrypt(email);
			var securedpw = rsa.encrypt(pw);

			// 회원가입
			$.ajax({
				type : "post",
				url : "memberjoin",
				data : {
					'id' : securedid,
					'pw' : securedpw,
					'email' : securedemail
				},
				success : function(result) {
					if(result){
						alert("가입신청완료!");
						location.href="/sendmail";
					}else{
						alert("입력하신 값에 문제가 있습니다.");
					}
					
				},
				error:function(){
					alert("가입 도중 문제가 발생했습니다!");
				}
			});
		}else{
			alert("입력한 값중에 잘못된 값이 있습니다");
		}
	});
});