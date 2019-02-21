$.ajax({
	url:'sessioncheck',
	type:'post',
	success:function(result){
		if(!result){
			alert("로그인 후 이용해주세요!");
			location.href="loginpage";
		}
	}
});
function domainlist(list){
	let stat="";
	let color="";
	for(var i=0;i<list.length;i++){
		if(list[i].stat=="Y"){
			stat="정상";
			color="steelblue";
			button='<button class="domain_delete_btn">삭제</button>';
		}else{
			stat="접속불가";
			color="crimson";
			button='<button class="domain_reload_btn">접속확인</button><button class="domain_delete_btn">삭제</button>';
		}
		$('.address_table').append('<tr class="list'+i+'">');
		$('.list'+i).append('<td class="domain">'+list[i].address+'</td>');
		$('.list'+i).append('<td class="stat"><a style="color:'+color+'">'+stat+'</a></td>');
		$('.list'+i).append('<td>'+button+'</td></tr>');
		
	}

	
}
$(document).ready(function(){
	$('.header').load("/resources/header.html");
	//도메인 리스트
	$.ajax({
		url:'domainlist',
		type:'post',
		success:function(result){
			domainlist(result);
		}
	})
	$('.checkbtn').click(function(){
			var data=$('.address').val();
			$('.modal').fadeIn('fast');
			$.ajax({
				url:'addresscheck',
				type:'post',
				data:{'url':data},
				success:function(result){
					if(result){
						$.ajax({
							url:'addressinsert',
							type:'post',
							data:{'url':data},
							success:function(result){
								$('.modal').fadeOut('fast');
								$('.check_val').fadeIn();
								$('.check_val').html("도메인이 정상적으로 등록되었습니다!<br/>서버가 다운되면 알림을 보내드릴게요!");
								$('.check_val').css({'color':'cornflowerblue'});
							},
							error:function(){
								alert("도메인 등록중 문제가 발생했습니다!");
							}
						});
					}else{
						$('.modal').fadeOut('fast');
						$('.check_val').fadeIn();
						$('.check_val').text("URL이 잘못되었거나 서버가 다운되었습니다! (http와 https도 구분합니다)");
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