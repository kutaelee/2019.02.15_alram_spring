$.ajax({
	url:'sessioncheck',
	type:'post',
	success:function(result){
		if(!result){
			alert("로그인 후 이용해주세요!");
			location.href="loginpage";
		}else{
			//도메인 리스트
			$.ajax({
				url:'domainlist',
				type:'post',
				success:function(result){
					domainlist(result);
				}
			})
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
			button='<button class="domain_reload_btn">갱신</button><button class="domain_delete_btn">삭제</button>';
		}
		$('.address_table').append('<tr class="list'+i+'">');
		$('.list'+i).append('<td class="domain">'+list[i].address+'</td>');
		$('.list'+i).append('<td class="stat"><a style="color:'+color+'">'+stat+'</a></td>');
		$('.list'+i).append('<td>'+button+'</td></tr>');
		
	}	
}
function domainadd(list,j){
	let stat="";
	let color="";
		if(list.stat=="Y"){
			stat="정상";
			color="steelblue";
			button='<button class="domain_delete_btn">삭제</button>';
		}else{
			stat="접속불가";
			color="crimson";
			button='<button class="domain_reload_btn">접속확인</button><button class="domain_delete_btn">삭제</button>';
		}
		$('.address_table').append('<tr class="list'+j+'">');
		$('.list'+j).append('<td class="domain">'+list.address+'</td>');
		$('.list'+j).append('<td class="stat"><a style="color:'+color+'">'+stat+'</a></td>');
		$('.list'+j).append('<td>'+button+'</td></tr>');
		
	
}
$(document).ready(function(){
	$('.header').load("/resources/header.html");
	
	//도메인 등록
	$('.checkbtn').click(function(){
			let data=$('.address').val();
			if(data.substring(0,4)!='http'){
				let protocol='http://';
				protocol+=data;
				data=protocol;
			}
			$('.modal').fadeIn('fast');
			$.ajax({
				url:'addresscheck',
				type:'post',
				data:{'url':data},
				success:function(result){
					if(result){
						$.ajax({
							url:'domaininsert',
							type:'post',
							data:{'url':data},
							success:function(result){
								$('.modal').fadeOut('fast');

								if(result.length!=0){
									$('.check_val').fadeIn();
									$('.check_val').html("도메인이 정상적으로 등록되었습니다!<br/>서버가 다운되면 알림을 보내드릴게요!");
									$('.check_val').css({'color':'cornflowerblue'});
									var j=$('.address_table tr').last().prop('class');
									if(null==j){
										j=-1;
									}
									j=j.substring(j.length-1,j.length);
									j=j*1+1;
									domainadd(result,j);
								}else{
									$('.check_val').fadeIn();
									$('.check_val').text("서버를 5개 이상 등록하셨거나 이미 등록된 도메인 입니다!");
									$('.check_val').css({'color':'crimson'});
								}
							
							},
							error:function(){
								$('.modal').fadeOut('fast');
								alert("도메인 등록중 문제가 발생했습니다!");
							}
						});
					}else{
						$('.modal').fadeOut('fast');
						$('.check_val').fadeIn();
						$('.check_val').text("정상인 서버만 등록이 가능합니다! (http와 https도 구분합니다)");
						$('.check_val').css({'color':'crimson'});
					}
				}
			});
	});
	$('.address').bind('click',function(){
		$('.check_val').hide();
		$('.check_val').text("");
	});
	
	//도메인 삭제
	$(document).on('click','.domain_delete_btn',function(){
		let result = window.confirm('삭제하시면 알림을 더 이상 보내지않아요!\n그래도 삭제하시겠어요?');
		$('.modal').fadeIn('fast');
		if(result){
			let list=$(this).parent().parent().prop('class');
			let data=$(this).parent().siblings('.domain').text();
			$.ajax({
				url:'domaindelete',
				type:'post',
				data:{'url':data},
				success:function(result){
					$('.modal').fadeOut('fast');
					if(result){
						alert("삭제가 완료되었습니다!");
						$('.'+list).remove();
					}else{
						alert("이미 삭제되었거나 잘못된 접근입니다.");
					}
			
				},
				error:function(){
					$('.modal').fadeOut('fast');
					alert("도메인 삭제 중 문제가 발생했습니다!");
				}
			});
	
		}
	});
	
	//도메인 갱신
	$(document).on('click','.domain_reload_btn',function(){
		$('.modal').fadeIn('fast');
		let list=$(this).parent().parent().prop('class');
		let data=$(this).parent().siblings('.domain').text();
		$.ajax({
			url:'domainreload',
			type:'post',
			data:{'url':data},
			success:function(result){
				$('.modal').fadeOut('fast');
				if(result){
					$('.'+list+' .stat').text("정상");
					$('.'+list+' .stat').css({
						'color':'steelblue'
					});
					$('.'+list+' td:last-child').children('.domain_reload_btn').remove();
					alert("갱신이 완료되었습니다!");
				}else{
					alert("서버가 정상일때만 갱신이 가능합니다. 확인후 다시 시도해주세요!");
				}
		
			},
			error:function(){
				$('.modal').fadeOut('fast');
				alert("도메인 갱신 중 문제가 발생했습니다!");
			}
		});
		
	});
	$('.readme_btn').mouseover(function(){
		$('.readme_text').stop().fadeIn('slow');
		$(this).mouseout(function(){
			$('.readme_text').stop().fadeOut();
		})
	})

});