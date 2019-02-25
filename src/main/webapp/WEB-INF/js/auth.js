//파라미터 태그 리턴 함수
function getParameterByName(name) {
	name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"), results = regex.exec(location.search);
	return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g," "));
}

$(document).ready(function() {
	$('.header').load("/resources/header.html");
	$('.modal').fadeIn('fast');

	/* 가입 인증 메일일 경우 이메일파라미터값이 없음*/
	if(!getParameterByName("email")){
	$.ajax({
		url : 'authreq',
		type : 'post',
		success : function(result) {
			$('.modal').fadeOut('fast');
			$('.info_text').text(result);
		},
		error : function() {
			alert("인증 도중 문제발생!");
		}
	});
	}else{
		/* 이메일 인증 메일일 경우 이메일파라미터값 있음*/
		$.ajax({
			url : 'emailupdate',
			type : 'post',
			success : function(result) {
				$('.modal').fadeOut('fast');
				$('.info_text').html(result);
			},
			error : function() {
				alert("인증 도중 문제발생!");
			}
		});
	}
});