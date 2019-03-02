$.ajax({
	url : 'sessioncheck',
	type : 'post',
	success : function(result) {
		if (result) {
			$('.nav_body ul li:nth-child(3)').html(
					'<a href="mypage">마이 페이지</a>');
			$('.nav_body ul li:nth-child(4)').html(
					'<a class="logout_btn">로그아웃</a>');
		}
	},
	error : function() {
		alert("세션체크 중 문제가 발생했습니다.");
	}
});

$(document).ready(function() {
	var tag = $('body').prop('class');
	var width=$( window ).width();
	if(width<=500){
		$('body').attr('id','mobile');
		$('.nav_body').css({
			'width':'110%',
			'height':'120%'
		})
	}else if(width<=1080){
		$('body').attr('id','mideum');
	}
	$(window).scroll(function() {
	    var st = $(this).scrollTop();
	    var menu=$('#menu_toggle').prop('class');
	    if(menu=='change'){
	    	  event.preventDefault();
			  event.stopPropagation();
			  return false;
	    }else{
		    $('.nav_body').offset({'top':st});
	    }

	});
	$('.nav_body').click(function(){
		if($('body').prop('id')=='mobile'){
			$('#menu_toggle').trigger('click');
		}
	});
	//구글 로그아웃 
/*	$.ajax({
		url:'googleusercheck',
		type:'post',
		success:function(result){
			if(result){
				$('.nav_body ul li:nth-child(4) a').attr('class','google_logout');
			}
		}
	});
	$(document).on('click','.google_logout',function(){
		alert("구글로그아웃도 함께 진행합니다!");
		$.ajax({
			url : 'logout',
			type : 'post',
			success : function(result) {
				document.location.href = "https://www.google.com/accounts/Logout?continue=https://appengine.google.com/_ah/logout?continue=http://kutaelee.iptime.org:58080";				 	
			},error : function() {
				alert("로그아웃 중 문제발생!");
			}
		});
		
	});*/
	$(document).on('click', '.logout_btn', function() {
			$.ajax({
				url : 'logout',
				type : 'post',
				success : function(result) {
					if (result) {
						
						alert("로그아웃 완료!");
						location.href = "/";
					}
				},
				error : function() {
					alert("로그아웃 중 문제발생!");
				}
			});
		
	});
	$('.home_icon').click(function() {
		location.href = "/";
	})
	$('#menu_toggle').click(function() {
		var bodyid=$('body').prop('id');
		var name = $(this).prop('class');
		
		var menumargin="91%";
		if(bodyid=='mideum'){
			menumargin="85%";
		}
	
		if(bodyid!='mobile'){
		
		if (name == 'menu_toggle') {			
			$(this).attr('class', 'change');
			$('.' + tag + '_body').css({
				'transform' : 'rotate(-11deg)',
				'margin-left' : '-10%',
				'transition' : '0.2s'
			});

			$('.nav_body').css({
				'transform' : 'rotate(-11deg)',
				'margin-left' : menumargin,
				'transition' : '0.2s'
			});
			$('.line').css({
				'transform' : 'rotate(-11deg)',
				'margin-left' : '-10%',
				'transition' : '0.2s'
			});
			$('#menu_toggle div').css({
				'background' : 'white'
			});
			$('.mypage_title').css({
				'margin-left' : '40%',
				'transition' : '0.2s'
			});
		} else if (name == 'change') {		
			$(this).attr('class', 'menu_toggle');
			$('.' + tag + '_body').css({
				'transform' : 'rotate(0deg)',
				'margin-left' : '0',
				'transition' : '0.2s'
			});
			$('.line').css({
				'transform' : 'rotate(0deg)',
				'margin-left' : '0',
				'transition' : '0.2s'
			});
			$('.nav_body').css({
				'transform' : 'rotate(0deg)',
				'margin-left' : '101%',
				'transition' : '0.2s'
			});
			$('#menu_toggle div').css({
				'background' : 'black'
			});
			$('.mypage_title').css({
				'margin-left' : '45%',
				'transition' : '0.2s'
			});
	
		}
		}else{
			if (name == 'menu_toggle') {			
				$(this).attr('class', 'change');
				$('#menu_toggle div').css({
					'background' : 'white'
				});
				$('.nav_body').stop().fadeIn('fast');
			}else{
				$(this).attr('class', 'menu_toggle');
				$('#menu_toggle div').css({
					'background' : 'black'
				});
				$('.nav_body').stop().fadeOut('fast');
			}
		}

	});
	$('.icon_info').hide();
	$('.line img').mouseout(function(){
		$('.icon_info').stop().fadeOut();
	});
	$('.line div').mouseout(function(){
		$('.icon_info').stop().fadeOut();
	});

	
	$('.line').children().mouseover(function(){
		
		var offset = $(this).offset();
		$('.icon_info').offset({'top':offset.top});
		var index=$(this).index();
		if(index==0){
			$('.icon_info h1').text("클릭하시면 메인화면으로 이동합니다!");
		}else if(index==1){
			$('.icon_info h1').text("클릭하시면 제가 제작한 포트폴리오 사이트를 새창으로 엽니다!");
		}else if(index==2){
			$('.icon_info h1').text("클릭하시면  제가 제작한 호텔예약 사이트 데모를 새창으로 엽니다!");
		}else if(index==3){
			$('.icon_info h1').text("클릭하시면  제가 운영하고있는 이슈모아 사이트를 새창으로 엽니다!");
		}else if(index==4){
			$('.icon_info h1').text("현재 사이트는 구글 로그인을 이용하실수 있습니다!");
		}else if(index==5){
			$('.icon_info h1').text("현재 사이트는 네이버 로그인을 이용하실수 있습니다!");
		}
		$('.icon_info').stop().fadeIn();
		$('.icon_info').css({'display':'inline-block'});
	});
	$('.line').children().click(function(){
		var index=$(this).index();
		if(index==1){
			window.open('http://ec2-13-209-68-44.ap-northeast-2.compute.amazonaws.com/');
		}else if(index==2){
			window.open('http://ec2-13-209-68-44.ap-northeast-2.compute.amazonaws.com:8080/');
		}else if(index==3){
			window.open('http://issuemoa.kr/');
		}else if(index==4){
			location.href='https://www.serverchecker.shop/loginpage';
		}else if(index==5){
			location.href='https://www.serverchecker.shop/loginpage';
		}
	});
});