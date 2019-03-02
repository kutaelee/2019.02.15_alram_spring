$(document).ready(function(){
	$('.header').load("/resources/header.html?ver=16");
	$.ajax({
		url:'errorcode',
		type:'post',
		success:function(result){
			$('.error_msg').text(result);
		},error:function(){
			alert("에러처리중 에러발생!");
		}
	});
});