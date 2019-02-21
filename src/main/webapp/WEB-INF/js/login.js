	$.ajax({
		url:'sessioncheck',
		type:'post',
		success:function(result){
			if(result){
				alert("이미 로그인 하셨습니다.");
				location.href="/";
			}
		},error:function(){
			alert("세션체크 중 문제가 발생했습니다.");
		}
	})
	
$(document).ready(function(){
	$('.header').load('/resources/header.html');
	
	var re = /[~!@\#$%^&*\()\-=+_.']/gi; //특수문자
	/* 공개키 변수 */
	var RSAModulus = null;
	var RSAExponent = null;
	
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
	
	//유효성 검사
	$('.login_form input').keyup(function(){
		var value=$(this).val();
		value=value.replace(/ /gi, "");
		if(re.test(value)){
			$(this).val(value.replace(re,"")); 
			alert("특수문자는 사용하지말아주세요!");
		}
	});
	
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
			url:'memberlogin',
			type:'post',
			data:{
				'id':securedid,
				'pw':securedpw
			},success:function(result){		
				$('.modal').fadeOut('fast');
				alert(result.msg);
				if(result.msg=="로그인 성공!"){
					login=true;
					location.href="/";
				}
			},error:function(){
				$('.modal').fadeOut('fast');
				alert("로그인 중 문제발생!");
			}
		})
	});
});