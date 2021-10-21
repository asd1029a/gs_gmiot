<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<nav>
	<ul class="mcd_menu">
		<li class="active" onclick="menuActive(this,'fcltMain');">
			<a href="#">
				<i class=""></i>
				<strong>시설물</strong>
				<small>Facility</small>
			</a>
		</li>
		<li onclick="menuActive(this,'civilCmplnt');">
			<a href="#">
				<i class=""></i>
				<strong>민원 관리</strong>
				<small>Civil Complain</small>
			</a>
		</li>
	</ul>
</nav>
<script>
$(document).ready(function(){
	//menuActive($('.sub_active')[0],'west','hardware');
	menuActive($('.active')[0],'fcltMain');
});

function menuActive(target, pageNm) {
	//엑티브 함수 적용
	$('.mcd_menu li').removeClass("active");
	$(target).addClass("active");

	$(".content_panel_container").html("<div class='west_panel'></div>");
	
	$(".west_panel").html("");
    $(".west_panel").load("/action/page.do", { path : 'empty/fclt/'+pageNm }, function() {
    	$('.map_panel_container').css("z-index","1");	    	
    });
}
</script>