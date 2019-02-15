$(document).ready(function(){	
	var tag=$('body').prop('class');
$('#menu_toggle').click(function(){
		var name=$(this).prop('class');
		if(name=='menu_toggle'){
			$(this).attr('class','change');
			$('.'+tag+'_body').css({
				'transform':'rotate(-11deg)',
				'margin-left':'-10%',
				'transition':'0.4s'
			});

		   $('.nav_body').css({
			   'transform':'rotate(-11deg)',
			   'margin-left':'91%',
			   'transition':'0.2s'
			});
		   $('.line').css({
			   'transform':'rotate(-11deg)',
			   'margin-left':'-10%',
			   'transition':'0.5s'
		   })
			$('#menu_toggle div').css({
				'background':'white'
			})
		}else if(name=='change'){
			$(this).attr('class','menu_toggle');
			   $('.'+tag+'_body').css({
				   'transform':'rotate(0deg)',
					'margin-left':'0',
				   'transition':'0.4s'
			   });
			   $('.line').css({
				   'transform':'rotate(0deg)',
				   'margin-left':'0',
				   'transition':'0.5s'
			   })
			$('.nav_body').css({
				   'transform':'rotate(0deg)',
				   'margin-left':'101%',
				   'transition':'0.2s'
				});
			$('#menu_toggle div').css({
				'background':'cadetblue'
			})
		}
			
	});
});