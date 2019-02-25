/* 세션체크 */
$.ajax({
	url : 'sessioncheck',
	type : 'post',
	success : function(result) {
		if (!result) {
			alert("로그인 후 이용해주세요!");
			location.href = "loginpage";
		} else {

		}
	}
});

/* 폼 로드 */
function passwordform() {
	$('.content_div h1').text("비밀번호 변경");
	$('.content2 label').text("비밀번호");
	$('.content2 input').attr('type', 'password');
	$('.content2 input').attr('placeholder', '●●●●●●');
	$('.content2 input').attr('maxlength', '12');
	$('input').val("");
	$('.content2').show();
	$('.content3').show();
	$('.hr2').show();
	$('.hr3').show();
	$('.content_div button').attr('class', 'password_change_btn');
	$('.content_div button').text('비밀번호 변경');
	$('.content_div').show();
}
function emailform() {
	$('.content_div h1').text("이메일 변경");
	$('.content2 label').text("이메일주소");
	$('.content2 input').attr('type', 'email');
	$('.content2 input').attr('placeholder', 'email');
	$('.content2 input').attr('maxlength', '40');
	$('input').val("");
	$('.content2').show();
	$('.content3').hide();
	$('.hr2').show();
	$('.hr3').hide();
	$('.content_div button').attr('class', 'email_change_btn');
	$('.content_div button').text('이메일 변경');
	$('.content_div').show();
}
function secessionform() {
	$('.content_div h1').text("회원 탈퇴");
	$('.content2').hide();
	$('.content3').hide();
	$('input').val("");
	$('.hr2').hide();
	$('.hr3').hide();
	$('.content_div button').attr('class', 'secession_btn');
	$('.content_div button').text('회원 탈퇴');
	$('.content_div').show();
}
/* 변경할 비밀번호 유효성 검사 */
function passwordtest(re) {
	let pw1 = $('.content2 input').val();
	let pw2 = $('.content3 input').val();

	if (re.test(pw1) || re.test(pw2) || pw1.length < 4 || pw2.length < 4 || pw1 != pw2) {
		return false;
	} else {
		return true;
	}
}

// 현재 비밀번호 확인
function passwordCheckAjax(securedpw){
	return new Promise(function (resolve, reject){
		$.ajax({
			url : 'passwordcheck',
			type : 'post',
			data : {
				'pw' : securedpw
			},success:function(result){
				console.log(result);
				resolve(result);
			},error:function(err){
				reject(err);
			}
		});
	});

}

$(document).ready(function() {

	/* 공개키 변수 */
	let RSAModulus = null;
	let RSAExponent = null;
	const exptext = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/; // 이메일
	const re = /[~!@\#$%^&*\()\-=+_.']/gi; // 특수문자
	let rsa = null;

	/* 공개키 요청 */
	$.ajax({
		url : 'rsacall',
		type : 'post',
		dataType : 'json',
		success : function(result) {
			RSAModulus = result.RSAModulus;
			RSAExponent = result.RSAExponent;
			rsa = new RSAKey();
			rsa.setPublic(RSAModulus, RSAExponent);
		},
		error : function() {
			alert("암호화 에러 발생!");
			location.href = '/';
		}
	});
	$('.header').load("/resources/header.html");

	$('.mypage_menu div').click(function() {
		$('.active_menu').removeAttr('class');
		$(this).attr('class', 'active_menu');
	});

	/* 마이페이지 메뉴 클릭 */
	$('#password').click(function() {
		passwordform();
	})
	$('#email').click(function() {
		emailform();
	})
	$('#member').click(function() {
		secessionform();
	})
	$('.mypage_title').click(function() {
		location.href = "/mypage";
	});
	
	/* 비밀번호 변경 */
	$(document).on('click', '.password_change_btn', function() {
		$('.modal').fadeIn('fast');
		let pwcheck1=passwordtest(re);
		let curpw = $('.content1 input').val();
		const securedcurpw = rsa.encrypt(curpw);	
	
		if(pwcheck1){
			let changepw = $('.content2 input').val();
			const securedchangepw = rsa.encrypt(changepw);	
		passwordCheckAjax(securedcurpw).then(function (resolvedData) {
			if(resolvedData){	
			$.ajax({
					url : 'memberpwupdate',
					type : 'post',
					data : {
						'pw' : securedchangepw
					},
					success : function(result) {
						$('.modal').fadeOut('fast');
						if (result) {
							alert("비밀번호 변경완료!");
							location.reload();
						} else {
							console.log("여기에러");
							alert("변경할 비밀번호가 잘못되었습니다!");
						}
					},
					error : function() {
						$('.modal').fadeOut('fast');
						alert("비밀번호 변경 중 오류발생!");
					}
				});	
			}else{
				alert("현재 비밀번호가 틀립니다!");
			}
		}).catch(function (err) {
			$('.modal').fadeOut('fast');
		 	alert("비밀번호 체크 중 오류발생!");
		 	pwcheck2=false;
		});
		}else{
			$('.modal').fadeOut('fast');
			alert("변경할 비밀번호가 잘못되었습니다!");
		}
		
	});
	/* 이메일 변경 */
	$(document).on('click', '.email_change_btn', function() {
		$('.modal').fadeIn('fast');
		let curpw = $('.content1 input').val();
		const securedcurpw = rsa.encrypt(curpw);	
	passwordCheckAjax(securedcurpw).then(function (resolvedData) {
		if(resolvedData){	
			const email = $('.content2 input').val();
			if (exptext.test(email)) {
				const securedemail = rsa.encrypt(email);
				$.ajax({
					url : 'emailupdatesend',
					type : 'post',
					data : {
						'email' : securedemail
					},
					success : function(result) {
						$('.modal').fadeOut('fast');
						if (result) {
							alert("변경 신청하신 이메일로 인증메일이 발송되었습니다!");
							location.reload();
						} else {
							alert("이미 등록된 이메일이거나 입력값에 문제가 있습니다!");
						}
					}

				});
			} else {
				$('.modal').fadeOut('fast');
				alert("이메일 형식에 맞지 않습니다.");
			}
		}else{
			$('.modal').fadeOut('fast');
			alert("현재 비밀번호가 틀립니다!");
		}
		}).catch(function (err) {
			$('.modal').fadeOut('fast');
			alert("비밀번호 체크 중 오류발생!");
			pwcheck2=false;
		});
	});
	
	/* 회원 탈퇴 */
	$(document).on('click', '.secession_btn', function() {
		if(confirm('회원탈퇴를 하시면 서버가 다운되어도 더 이상 알림을 보내지 않아요!\n회원정보와 등록서버가 모두 삭제되며 복구가 불가능해요!\n그래도 정말 괜찮으시겠어요?')){
			$('.modal').fadeIn('fast');
			let curpw = $('.content1 input').val();
			const securedcurpw = rsa.encrypt(curpw);	
		passwordCheckAjax(securedcurpw).then(function (resolvedData) {
			if(resolvedData){	
				$.ajax({
					url:'memberSecession',
					type:'post',
					success:function(result){
						if(result){
							$('.modal').fadeOut('fast');
							alert("모든 정보를 삭제했습니다.\n그동안 이용해주셔서 감사드립니다!");
							location.href="/";
						}
					},error:function(){
						$('.modal').fadeOut('fast');
						alert("회원탈퇴중 문제발생!");
					}
				})
			}else{
				$('.modal').fadeOut('fast');
				alert("현재 비밀번호가 틀립니다!");
			}
		}).catch(function (err) {
			$('.modal').fadeOut('fast');
			alert("비밀번호 체크 중 오류발생!");
			pwcheck2=false;
		});
		}
	
	});

	/* 유효성 검사 */
	$('.mypage_div input').keyup(function() {
		var value = $(this).val();
		var name = $(this).prop('class');

		/* 공백제거 */
		value = value.replace(/ /gi, "");
		$(this).val(value);
	})
	
		/* 기본으로 첫메뉴선택 */
	$('#password').trigger('click');
});