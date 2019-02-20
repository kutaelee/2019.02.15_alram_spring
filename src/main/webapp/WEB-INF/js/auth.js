$(document).ready(function() {
	$('.header').load("/resources/header.html");

	$.ajax({
		url : 'authreq',
		type : 'post',
		success : function(result) {
			$('.info_text').text(result);
		},
		error : function() {
			alert("인증 도중 문제발생!");
		}
	})
});