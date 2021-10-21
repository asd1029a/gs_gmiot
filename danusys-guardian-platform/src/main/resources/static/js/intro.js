/**
 * intro 관련 함수 모음
 * @namespace intro
 * */

/* 부모창이 존재하면 현재창을 닫고 부모창을 새로고침 하고, 부모창이 없으면 현재창을 새로고침 한다. */
var windowList = new Map();

/**
 * 로그아웃 함수
 * @function intro.allLogout
 * */
var allLogout = function() {
	if(confirm("로그아웃 하시겠습니까?")) {
    	document.logoutForm.submit();
	}
}

/**
 * window.open으로 열린 모든 새창을 닫는 함수
 * @function intro.closeWindowList
 * */
var closeWindowList = function() {
	windowList.forEach(function(item) {
		item.close();
	});
	
	window.location.reload();
}

/**
 * menu open 함수
 * @function intro.openMenu
 * @param {string} path - 페이지 경로
 * @param {string} name - 페이지 이름
 * @param {number} height - 페이지 height 값
 * @param {number} widht - 페이지 width 값
 * */
var openMenu = function(path, name) {
	var height = screen.height-110;
	var width = screen.width-10;
//	var url = path.indexOf("http")>-1?path:encodeURI(path + '&title=' + name);
	var url = path.indexOf("http")>-1?path:encodeURI(path);
	const child = window.open(url, name, 'height=' + height + ',width=' + width);
	windowList.set(name, child);
}

/**
 * service menu open 함수
 * @function intro.openService
 * @param {string} path - 페이지 경로
 * @param {string} name - 페이지 이름
 * @param {number} height - 페이지 height 값
 * @param {number} widht - 페이지 width 값
 * */
var openService = function(path, name) {
	var height = screen.height;
	var width = screen.width;
	var url = path;
	const child = window.open(url, name, 'height=' + height + ',width=' + width);
	windowList.set(name, child);
}

/*$(function() {
	getMenu("menu");
	getNoticeList();
});
*/
/*var slideIndex = 0;
showSlides();*/

/**
 * 새로 추가된 슬라이드 메뉴 show 함수
 * @function intro.showSlides
 * */
function showSlides() {
	var i;
	var slides = document.getElementsByClassName("visual-slider");
	var dots = document.getElementsByClassName("dot");
	
	if(slides.length == 0) {
		$('.visual-cont').append('<li class="visual-slider fade"><img src="/file/getImage2?sPath=display&imageUrl=intro_img01.png" style="width:100%"></li>');
		$('.slideshow-button').append('<span class="dot"></span>');
	}
	
	for (i = 0; i < slides.length; i++) {
		slides[i].style.display = "none";  
	}
	slideIndex++;
	if (slideIndex > slides.length) {slideIndex = 1}    
	for (i = 0; i < dots.length; i++) {
		dots[i].className = dots[i].className.replace(" active", "");
	}
	slides[slideIndex-1].style.display = "block";  
	dots[slideIndex-1].className += " active";
	setTimeout(showSlides, 2000); // Change image every 2 seconds
}


/**
 * 공지사항 리스트를 불러오는 함수
 * @function intro.getNoticeList
 * */
function getNoticeList() {
	var jsonObj = {};
	jsonObj.noticeGbn = 'notice';
	$.ajax({
        contentType : "application/json; charset=utf-8",
		type       : "POST",
		url 		: "/select/oprt.getNoticeList/action",
		dataType   : "json",
		data       : JSON.stringify(jsonObj),
		async      : false
	}).done(function(result) {
		const rows = result.rows;
		var itemsOnPage = 6;
		$("#portListPagination").simplePagination({
			items: rows.length,
			itemsOnPage : itemsOnPage,
			displayedPages : 5,
			edges : 0,
			prevText : "<",
			nextText : ">",
			cssStyle: 'custom-theme',
			onInit : function (){
				pageClick(1, undefined, rows, itemsOnPage);
			},
			//클릭할때
			onPageClick :  function (pageNumber , event) {
				pageClick(pageNumber, event, rows, itemsOnPage);
			}
		});
	});
}

/**
 * 공지사항 페이지 이동 함수
 * @function intro.pageClick
 * @param {number} pageNumber - 페이지 번호
 * @param {event} event - event
 * @param {Array} data - 공지사항 리스트 데이터
 * @param {number} itemsOnPage - 페이지 당 표시할 row 개수
 * */
function pageClick(pageNumber, event, data, itemsOnPage){
	const length = data.length;
	var max = pageNumber * itemsOnPage;
	max = max <= length ? max : length;
	var cnt = (pageNumber - 1) * itemsOnPage;

	$('#portList').html('');
	
	for(var i = cnt; i < max; i++) {
		const temp = data[i];
		var inner = document.createElement('li');
		var t = document.createElement('span');
		var d = document.createElement('span');
		var n = document.createElement('span');
		
		t.classList.add('txt');
		d.classList.add('date');
		n.classList.add('name');
		
		t.innerHTML = temp.title;
		d.innerHTML = temp.insertDate;
		n.innerHTML = temp.insertUser;
		
		inner.appendChild(t);
		inner.appendChild(d);
		inner.appendChild(n);
		$(inner).bind('click', function() {
			//var target = $('#noticePopup');
			$('#pNoticeTitle').text(temp.title);
			$('#pNoticeWriter').text(temp.insertUser);
			$('#pNoticeDate').text(temp.insertDate);
			$('#pNoticeContent').text(temp.content);
			var contP = $('#pNoticeContent').text(temp.content.replace(/(<br>|<br\/>|<br \/>)/g, ''));
			contP.html(contP.html().replace(/\n/g, '<br />'));
			//target.dialog({title:temp.title,width:"500px",height:"auto",modal: true});
			//target.html('<p>'+temp.content+'</p><p class="insert_info">작성일자: '+temp.insertDate+"&nbsp;&nbsp;작성자: "+temp.insertUser+"</p>");
			$('#noticePopup').css('display','block');
		});
		
		$('#portList').append(inner);
	}
}

/**
 * 사용자에게 할당된 메뉴 리스트를 불러오는 함수
 * @function intro.getMenu
 * @param {string} menuGbn - menu group id
 * */
function getMenu(menuGbn) {
	const jsonObj = {};
	jsonObj.menuGbn = menuGbn;	//menu, serviece
	jsonObj.userId =  $('#loginId').val();
	
	$.ajax({
	    contentType: "application/json; charset=utf-8",
		type       : "POST",
		url        : "/select/common.getMenu/action",
		dataType   : "json",
		data       : JSON.stringify(jsonObj),
		async      : false
	}).done(function(result) {
		//상단 설정 버튼
		/*if($('#loginId').val()=="admin") {
			$('#sysSetLink').removeClass('disabled');
			$('#sysSetLink').bind('click', function(e) {
				openMenu('/action/page.do?path=oprt/oprtMain', name);
			});
		}*/
		
		const rows = result.rows;
		if(menuGbn=="menu") {
			$.each(rows, function(index, obj) {
				$('#'+obj.menuId+'Link').removeClass('disabled');
				$('#'+obj.menuId+'Link').bind('click', function(e) {
					openMenu(obj.path, name);
				});
			});
		}
		if(menuGbn=="service") {
			$('.link-list').html('');
			$.each(rows, function(index, obj) {
				var inner = document.createElement('li');
				var span1 = document.createElement('span');
				var span2 = document.createElement('span');
				
				span1.classList.add('icon');
				span2.classList.add('txt');
				span2.innerHTML = obj.menuNm;
				
				var img = document.createElement('img');
				img.src = '../images/intro/icon_'+obj.iconNm+'.png';
				
				span1.appendChild(img);
				
				inner.appendChild(span1);
				inner.appendChild(span2);
				
				$('.link-list').append(inner);
				
				$(inner).bind('click', function(e) {
					openMenu(obj.path, name);
				});
			});
		}
	});
}