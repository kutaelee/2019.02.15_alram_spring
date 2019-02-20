
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
			var data=$('.address').val();
			$('.check_val').text("Loding...");
			$('.check_val').css({'color':'teal'});
			$('.check_val').fadeIn('fast');
			$.ajax({
				url:'addresscheck',
				type:'post',
				data:{'url':data},
				success:function(result){
					if(result){
						$('.check_val').fadeIn();
						$('.check_val').text("서버가 정상적입니다! 다운되면 알려드릴까요?");
						$('.check_val').css({'color':'cornflowerblue'});
					}else{
						$('.check_val').fadeIn();
						$('.check_val').text("URL이 잘못되었거나 서버가 다운되었습니다!");
						$('.check_val').css({'color':'crimson'});
					}
				}
			});
	})
	$('.address').bind('click',function(){
		$('.check_val').hide();
		$('.check_val').text("");
	})
});