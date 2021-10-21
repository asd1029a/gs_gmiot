<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<nav>
	<ul class="mcd_menu">
		<!-- <li>
			<a href="#" onclick="" id="">
				<i class="menu_line"></i>
				<strong></strong>
				<small></small>
			</a>
			<ul>
				<li>
					<a href="#" onclick="logout();" class=""><i class="logout_icon"></i>Logout</a>
				</li>
			</ul>
		</li> -->
		<li class="active" onclick="menuActive(this,'ptzfSet');">
			<a href="#">
				<i class=""></i>
				<strong>프리셋 설정</strong>
				<small>Preset Setting</small>
			</a>
		</li>
		<li onclick="menuActive(this,'circlrSet');">
			<a href="#">
				<i class=""></i>
				<strong>순환감시 설정</strong>
				<small>Circular Setting</small>
			</a>
		</li>
	</ul>
</nav>
<script>
$(document).ready(function(){
	//menuActive($('.sub_active')[0],'west','hardware');
	menuActive($('.active')[0],'ptzfSet');
});

function menuActive(target, pageNm) {
	
	//$('.mcd_menu li ul li a').removeClass("sub_active");
	//서브메뉴 있는 경우
	//if(target.classList.value.indexOf("sub_menu") > -1) {
	//	$(target).addClass("sub_active");
	//	target = target.parentNode.parentNode;
	//}
	//엑티브 함수 적용
	//$('.mcd_menu li a').removeClass("active");
	//$(target).parent().children('a').addClass("active");
	$('.mcd_menu li').removeClass("active");
	$(target).addClass("active");

	$(".content_panel_container").html("<div class='west_panel'></div>");
	
	$(".west_panel").html("");
    $(".west_panel").load("/action/page.do", { path : 'empty/set/'+pageNm }, function() {
    	$('.map_panel_container').css("z-index","1");	    	
    });
}
</script>