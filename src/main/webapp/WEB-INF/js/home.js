
$(document).ready(function(){
	$.ajax({
		url:'/member',
		type:'post',
		success:function(result){
			console.log(result);
		}
	});
	$('.header').load("/resources/header.html");
	$('.checkbtn').click(function(){
		var checkval=/^http(s)?:\/\/(www\.)?[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$/;
		if(checkval.test($('.address').val())){
			var data=$('.address').val();
			$('.check_val').text("Loding...");
			$('.check_val').css({'color':'yellowgreen'});
			$('.check_val').fadeIn('fast');
			$.ajax({
				url:'addresscheck',
				type:'post',
				data:{'url':data},
				success:function(result){
					if(result){
						$('.check_val').fadeIn();
						$('.check_val').text("서버가 정상적입니다! 다운되면 알려드릴까요?");
						$('.check_val').css({'color':'#2C7873'});
					}else{
						$('.check_val').fadeIn();
						$('.check_val').text("URL이 잘못되었거나 서버가 다운되었습니다!");
						$('.check_val').css({'color':'salmon'});
					}
				}
			});
		}else{
		alert("URL을 정확하게 입력해주세요!");
		}
	})
	$('.address').bind('click',function(){
		$('.check_val').hide();
		$('.check_val').text("");
	})
});