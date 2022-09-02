$(document).ready(function($){
	$(window).scroll(function(){
		if ($(this).scrollTop() > 300) {
			$('#cd-top').addClass('cd-is-visible');
		} else {
			$('#cd-top').removeClass('cd-is-visible cd-fade-out');
		}s
		if( $(this).scrollTop() > 1200 ) {
			$('#cd-top').addClass('cd-fade-out');
		}
	});
	//www.sucaijiayuan.com
	//smooth scroll to top
	$('#cd-top').on('click', function(event){
		event.preventDefault();
		$('body,html').animate({
				scrollTop: 0 ,
			}, 700
		);
	});
});