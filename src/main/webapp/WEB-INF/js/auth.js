$(document).ready(function() {
	$('.header').load("/resources/header.html");
	$('.modal').fadeIn('fast');
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
	})
});