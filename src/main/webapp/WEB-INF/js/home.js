function successcheckval(){
	$('.modal').fadeOut('fast');
	$('.check_val').fadeIn();
	$('.check_val').attr('id','suc');
	$('.check_val').text("서버가 정상적입니다! 등록페이지를 안내해드릴까요?");
}
function failcheckval(){
	$('.modal').fadeOut('fast');
	$('.check_val').fadeIn();
	$('.check_val').attr('id','fail');
	$('.check_val').text("URL이 잘못되었거나 서버가 다운되었습니다! (http와 https도 구분합니다)");
}
$(document).ready(function(){
	$('.header').load("/resources/header.html?ver=0");
	//서버 체크
	$('.checkbtn').click(function(){
			var data=$('.address').val();
			if(data.trim()==''){
				alert("도메인을 입력해주세요!");
				$('.address').focus();
			}else{				
				if(data.substring(0,4)!='http'){
					let protocol='http://';
					protocol+=data;
					data=protocol;
					$('.address').val(data);
				}
				$('.modal').fadeIn('fast');
			 if(data.length<12){
					failcheckval();
				}
			$.ajax({
				url:'addresscheck',
				type:'post',
				data:{'url':data},
				success:function(result){
					if(result){
						successcheckval();
					}else{
						failcheckval();
					}
				}
			});
		}
	})
	
	//메뉴안내
	$(document).on('click','#suc',function(){
		$('.menu_toggle').trigger('click');
		$('.nav_body li:nth-child(2) a').css({'color':'deeppink'});
		$('.nav_body li:nth-child(2) a').css({'color':'deeppink'});
			setTimeout(function(){
				$('.nav_body li:nth-child(2) a').removeAttr('style');
			},1000);
	
	});
	
	//주소창 클릭시 텍스트 숨김
	$('.address').bind('click',function(){
		$('.check_val').hide();
		$('.check_val').text("");
	})
	
	//엔터키로 서버 조회
	$('body').keydown(function(e){
		if(e.keyCode==13){
			$('.checkbtn').trigger('click');
		}
	});
});