//파라미터 태그 리턴 함수
function getParameterByName(name) {
	name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"), results = regex
			.exec(location.search);
	return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g,
			" "));
}
// 메뉴 애니메이션
function slide(menu) {
	$('.menu_tap div').removeAttr('class', 'active_menu');
	$('.menu_tap div').attr('class', 'menu');
	$('#' + menu).attr('class', 'active_menu');
	if (menu == 'menu1') {
		$('.cover').css({
			'margin-left' : '55%',
			'transition' : '0.7s'
		});

		$('.menu_tap').css({
			'margin-left' : '35%',
			'transition' : '0.7s'
		});

	} else {
		$('.cover').css({
			'margin-left' : '35%',
			'transition' : '0.7s'
		});

		$('.menu_tap').css({
			'margin-left' : '60.2%',
			'transition' : '0.7s'
		});
	}
}

function escape(value) {
	var re = /[~!@\#$%^&*\()\-=+_.']/gi; // 특수문자
	value = value.replace(/ /gi, "");
	// 특수문자 제거
	if (re.test(value)) {
		$(this).val(value.replace(re, ""));
	}
	return value;
}
$(document).ready(function() {

	/* 공개키 변수 */
	var RSAModulus = null;
	var RSAExponent = null;
	var targetid = null;

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

	$('.header').load("/resources/header.html");
	var tagval = getParameterByName('tag');

	if (tagval == 'email') {
		slide('menu2');
		tagval == null;
	}
	// 메뉴탭 클릭 기능
	$('.menu_tap div').click(function() {
		var menu = $(this).prop('id');
		slide(menu);
	});

	// id찾기
	$('.find_id_btn').click(function() {
		var email = $('.find_id_email').val();

		// RSA 암호키 생성
		var rsa = new RSAKey();
		rsa.setPublic(RSAModulus, RSAExponent);

		// 암호화
		var securedemail = rsa.encrypt(email);

		$.ajax({
			url : 'findid',
			type : 'post',
			data : {
				'email' : securedemail
			},
			success : function(result) {
				alert(result);
			},
			error : function(e) {
				alert("아이디찾기중 문제발생!");
			}
		});
	});

	// pw변경
	$('.find_pw_btn').click(function() {
		var id = $('.find_pw_id').val();
		var email = $('.find_pw_email').val();

		// RSA 암호키 생성
		var rsa = new RSAKey();
		rsa.setPublic(RSAModulus, RSAExponent);

		// 암호화
		var securedid = rsa.encrypt(id);
		var securedemail = rsa.encrypt(email);

		$.ajax({
			url : 'findpw',
			type : 'post',
			data : {
				'id' : securedid,
				'email' : securedemail
			},
			success : function(result) {
				alert(result);
			},
			error : function(e) {
				alert("비밀번호찾기중 문제발생!");
			}
		});
	});

	// 이메일 변경폼 요청
	$('.mail_form_btn').click(function() {
		targetid = $('.email_form_id').val();
		var pw = $('.email_form_pw').val();
		targetid = escape(targetid);
		pw = escape(pw);

		if (targetid.length > 0 && pw.length > 0) {
			var rsa = new RSAKey();
			rsa.setPublic(RSAModulus, RSAExponent);

			var securedid = rsa.encrypt(targetid);
			var securedpw = rsa.encrypt(pw);
			targetid = securedid;
			$.ajax({
				url : 'memberCheck',
				type : 'post',
				data : {
					'id' : securedid,
					'pw' : securedpw
				},
				success : function(result) {
					if (result) {
						$('.email_div').hide();
						$('.email_change').fadeIn();
					} else {
						alert('일치하는 정보가 없습니다.');
					}
				},
				error : function(e) {
					alert("이메일 요청 처리 중 문제발생!");
				}
			});
		} else {
			alert("일치하는 정보가 없습니다.");
		}

	});
	// 이메일 변경
	$('.email_change_btn').click(function() {
		var exptext = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/; // 이메일
		// 형식
		var email = $('.email_form_email').val();
		var rsa = new RSAKey();
		rsa.setPublic(RSAModulus, RSAExponent);
		if (exptext.test(email)) {
			var securedemail = rsa.encrypt(email);
			$.ajax({
				url : 'emailUpdate',
				type : 'post',
				data : {
					'email' : securedemail,
					'id' : targetid
				},
				success : function(result) {
					if (result) {
						alert("이메일이 정상적으로 변경되었습니다!");
						location.reload();
					} else {
						alert("이미 있는 이메일이거나 입력값에 문제가 있습니다!");
					}
				}

			});
		} else {
			alert("이메일 형식에 맞지 않습니다.");
		}

	});
	//인증메일 재전송
	$('.send_mail_btn').click(function() {
		var id = $('.email_form_id').val();
		var pw = $('.email_form_pw').val();
		id = escape(id);
		pw = escape(pw);
		if (id.length > 0 && pw.length > 0) {

			var rsa = new RSAKey();
			rsa.setPublic(RSAModulus, RSAExponent);

			var securedid = rsa.encrypt(id);
			var securedpw = rsa.encrypt(pw);

			$.ajax({
				url : 'memberCheck',
				type : 'post',
				data : {
					'id' : securedid,
					'pw' : securedpw
				},
				success : function(result) {
					if (result) {
						$.ajax({
							url : 'emailresend',
							type : 'post',
							data : {
								'id' : securedid
							},
							success : function(result) {
								if (result) {
									alert("이메일 재전송 완료!");
									location.reload();
								} else {
									alert("이미 인증이 완료된 아이디 입니다.");
								}
							},
							error : function() {
								alert("메일 재전송 중 문제발생!");
							}
						})
					} else {
						alert("일치하는 정보가 없습니다.");
					}
				},
				error : function() {
					alert("아이디 확인 중 문제발생!");
				}
			});
		}else{
			alert("일치하는 정보가 없습니다!");
		}
	});
});
