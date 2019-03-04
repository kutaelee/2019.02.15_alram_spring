/* 공개키 변수 */
var RSAModulus = null;
var RSAExponent = null;
var googlesw=false;
$.ajax({
	url : 'sessioncheck',
	type : 'post',
	success : function(result) {
		if (result) {
			alert("이미 로그인 하셨습니다.");
			location.href = "/";
		}
	},
	error : function() {
		alert("세션체크 중 문제가 발생했습니다.");
	}
})

function onSignIn(googleUser) {
		if(googlesw){	
		let profile = googleUser.getBasicProfile();
			let email = profile.getEmail();
			let rsa = new RSAKey();
			rsa.setPublic(RSAModulus, RSAExponent);
			securedemail=rsa.encrypt(email);
		
				$.ajax({
					url : 'googlelogin',
					type : 'post',
					data : {
						'email' : securedemail
					},
					success : function(result) {
						if (result) {
							alert("로그인 성공!");
							location.href="/";
						} else {
							alert("이미 중복된 이메일이 있어서 이용이 불가능합니다.\n구글 로그아웃을 진행한 후에 다른 아이디로 로그인해주세요!");
							location.href = "https://www.google.com/accounts/Logout?continue=https://appengine.google.com/_ah/logout?continue=https://www.serverchkecer.shop","_blank";			
						}
					},
					error : function() {
						alert("이미 중복된 이메일이 있어서 이용이 불가능합니다.\n구글 로그아웃을 진행한 후에 다른 아이디로 로그인해주세요!");
						location.href = "https://www.google.com/accounts/Logout?continue=https://appengine.google.com/_ah/logout?continue=https://www.serverchkecer.shop","_blank";
					}
				});
		
		}
}

$(document).ready(function() {
	$('.header').load('/resources/header.html');

	var re = /[~!@\#$%^&*\()\-=+_.']/gi; // 특수문자
	$('.g-signin2').click(function(){
		googlesw=true;
	});
	// 공개키 요청
	$.ajax({
		url : 'rsacall',
		type : 'post',
		dataType : 'json',
		success : function(result) {
			RSAModulus = result.RSAModulus;
			RSAExponent = result.RSAExponent;
		},
		error : function() {
			alert("암호화 에러 발생!");
			location.href = '/';
		}
	});

	// 유효성 검사
	$('.login_form input').keyup(function() {
		var value = $(this).val();
		value = value.replace(/ /gi, "");
		if (re.test(value)) {
			$(this).val(value.replace(re, ""));
			alert("특수문자는 사용하지말아주세요!");
		}
	});
	
	//엔터 키 입력 
	$('.login_id').keyup(function(e){
		if(e.keyCode==13){
				$('.login_pw').focus();
			}
	})
	$('.login_pw').keyup(function(e){
		if(e.keyCode==13){
				$('.login_btn').trigger('click');
			}
	})
	$('.login_btn').click(function() {
		$('.modal').fadeIn('fast');
		var id = $('.login_id').val();
		var pw = $('.login_pw').val();

		// RSA 암호키 생성
		var rsa = new RSAKey();
		rsa.setPublic(RSAModulus, RSAExponent);

		// 계정 정보 암호화
		var securedid = rsa.encrypt(id);
		var securedpw = rsa.encrypt(pw);

		$.ajax({
			url : 'memberlogin',
			type : 'post',
			data : {
				'id' : securedid,
				'pw' : securedpw
			},
			success : function(result) {
				$('.modal').fadeOut('fast');
				alert(result.msg);
				if (result.msg == "로그인 성공!") {
					login = true;
					location.href = "/";
				}
			},
			error : function() {
				$('.modal').fadeOut('fast');
				alert("로그인 중 문제발생!");
			}
		})
	});
	
	
	// naver
	$('.naverlogin').click(function(){
		var naverLogin = new naver.LoginWithNaverId(
				{
					clientId: "Es8i1j9wAKGHOocdaIBd",
					callbackUrl: "http://www.serverchecker.shop/loginpage",
					isPopup: true
				}
		);
		naverLogin.init();
		window.addEventListener('load', function () {
			naverLogin.getLoginStatus(function (status) {
				if (status) {
					/* (5) 필수적으로 받아야하는 프로필 정보가 있다면 callback처리 시점에 체크 */
					var email = naverLogin.user.getEmail();
					if( email == undefined || email == null) {
						alert("이메일은 필수정보입니다. 정보제공을 동의해주세요.");
						/* (5-1) 사용자 정보 재동의를 위하여 다시 네아로 동의페이지로 이동함 */
						naverLogin.reprompt();
						return;
					}

					window.location.replace("http://www.serverchecker.shop/loginpage");
				} else {
					console.log("callback 처리에 실패하였습니다.");
				}
			});
		});
		naverLogin.getLoginStatus(function (status) {
			if (status) {
				let email = naverLogin.user.getEmail();	

				$.ajax({
					url:'naverlogin',
					type:'post',
					data:{'email':email},
					success:function(result){
						if(result){
							alert("네이버 로그인성공!");
							location.href="/";
						}else{
							alert("소셜로그인이 아닌 아이디에 존재하는 이메일입니다.\n홈페이지 로그인을 통해 이용해주세요!");
							location.href="/loginpage";
						}
					
					}
				});
			} else {
				console.log("AccessToken이 올바르지 않습니다.");
			}
		});


	});
});