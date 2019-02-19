$(document).ready(function() {
	var pwcheck=false;
	var pw2check=false;
	/* 공개키 변수 */
	var RSAModulus = null;
	var RSAExponent = null;
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

	$('.header').load("/resources/header.html");
	
	//유효성 검사
	$('.changepw_form input').keyup(function(){
		var value=$(this).val();
		value=value.replace(/ /gi, "");
		if(re.test(value)){
			$(this).val(value.replace(re,"")); 
			alert("특수문자는 사용하지말아주세요!");
		}
	});
	
	$('.password1').keyup(function() {
		var pw = $(this).val();
		var pw2 = $('.password2').val();
		if (pw.length > 3 && pw.length < 13) {
			$('.pw1_info').css({
				'color' : 'white'
			});
			$('.pw1_info').text("사용해도 좋은 비밀번호 입니다");
			pwcheck = true;
		} else {
			$('.pw1_info').css({
				'color' : 'firebrick'
			});
			$('.pw1_info').text("비밀번호는 4자리 이상 12자리이하 입니다");
			pwcheck = false;
		}
		if (pw == pw2) {
			$('.pw2_info').css({
				'color' : 'white'
			});
			$('.pw2_info').text("비밀번호가 서로 같습니다");
			pw2check = true;
		} else {
			$('.pw2_info').css({
				'color' : 'firebrick'
			});
			$('.pw2_info').text("비밀번호가 서로 다릅니다");
			pw2check = false;
		}
	});
	$('.password2').keyup(function() {
		var pw = $('.password1').val();
		var pw2 = $(this).val();
		if (pw == pw2) {
			$('.pw2_info').css({
				'color' : 'white'
			});
			$('.pw2_info').text("비밀번호가 서로 같습니다");
			pw2check = true;
		} else {
			$('.pw2_info').css({
				'color' : 'firebrick'
			});
			$('.pw2_info').text("비밀번호가 서로 다릅니다");
			pw2check = false;
		}
	});
	
	//비밀번호 변경 요청
	$('.changepw_btn').click(function() {
		if(pwcheck&&pw2check){
			var pw = $('.password1').val();
			var rsa = new RSAKey();
			rsa.setPublic(RSAModulus, RSAExponent);

			var securedpw = rsa.encrypt(pw);
			$.ajax({
				url : 'memberpwupdate',
				type : 'post',
				data : {
					'pw' : securedpw
				},
				success : function(result) {
					if(result){
						alert("비밀번호가 변경되었습니다!");
						location.href="/loginpage";
					}else{
						alert("입력값이 잘못되었거나 만료된 페이지 입니다.");
					}
				},
				error : function() {
					alert("비밀번호 변경중 에러발생!");
				}
			});
		}else{
			alert("입력값이 잘못되었습니다.");
		}

	});
});