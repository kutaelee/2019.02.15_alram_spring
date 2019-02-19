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

$(document).ready(function() {

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
		var id=$('.find_pw_id').val();
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
	
});
