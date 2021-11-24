<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script src="/js/jquery/jquery-datepicker-ko.js"></script>
<link rel="stylesheet" href="/css/import/datepicker.css">

<div class="popup popup-handling">
		<dl class="popup-tit">
			<dt>상황처리내역</dt>
			<dd onClick="closeLogPopup();"><img src="../images/icons/icon_closed.png"></dd>
		</dl>
		<div class="hand-cont">
			<ul class="tab">
				<li class="active" value="">전체</li>
				<li value="eventM">발생</li>
				<li value="eventE">종료</li>
			</ul>
			<div class="hand-srch-form">
				<ul>
					<li>
						<span>이벤트 유형</span>
						<select id="popupEventSelect"></select>
					</li>
					<!-- <li>
						<span>처리상태</span>
						<select>
							<option selected>대기</option>
							<option>처리 중</option>
							<option>완료</option>
							<option>보류</option>
						</select>
					</li> -->
					<li>
						<span>발생일자</span>
						<dl class="search">
							<dt><input type="text" placeholder="시작일 입력" id="searchDeS"></dt>
						</dl>
						<span>&nbsp;~</span>
						<dl class="search">
							<dt><input type="text" placeholder="종료일 입력" id="searchDeE"></dt>
						</dl>
					</li>
					<li class="button" onClick="selectEventLog();">조회</li>
					<!-- <li class="button">초기화</li> -->
					<!-- <li class="button">신규등록</li> -->
				</ul>
			</div>
			<div class="hand-srch-list">
				<article class="search-list" id="eventLogList"></article>
				<ul id="eventLogListPagination"></ul>
			</div>
		</div>
		<ul class="right-btn">
			<li class="excel btn-excel" onClick="excelDown('eventLogList','selectEventList','event_log_result')">엑셀 다운로드</li>
			<li onClick="closeLogPopup();">닫기</li>
		</ul>
	</div>

<script>
var jsonMObj = {};

$(document).ready(function(){
	$('#searchDeS').datepicker({
		buttonImage: "../images/icons/icon_calendar.png",
		showOn: "both",
        buttonImageOnly: true
	});
	$('#searchDeE').datepicker({
		buttonImage: "../images/icons/icon_calendar.png",
		showOn: "both",
        buttonImageOnly: true
	});
	
	comboEventGbn('popupEventSelect','${admin.id}');
	
	$('.hand-cont > ul li').click(function(){
		$(".hand-cont > ul li").removeClass("active");
		$(this).addClass("active");
		
		selectEventLog();
	});
	
	selectEventLog();
});


function closeLogPopup() {
	$('#eventLogPopup').dialog('close');
}

function selectEventLog() {
	const jsonObj = {};
	jsonMObj = {};
	
	var popupGbnVal;
	if($('#popupEventSelect').val() != "null") {
		popupGbnVal = $('#popupEventSelect').val();
	}
	
	jsonObj.userId = '${admin.id}';
	jsonObj.evtId = popupGbnVal;		//이벤트구분
	jsonObj.eventDeS = $('#searchDeS').val();
	jsonObj.eventDeE = $('#searchDeE').val();
	
	var popupEventFlag = $(".hand-cont ul li.active").attr('value');
	jsonObj.pageKind = popupEventFlag;
	
	jsonMObj = jsonObj;
	
	$.ajax({
		type       : "POST",
		url 		: "/select/event.selectEventList/action.do",
		dataType   : "json",
		data       : {
			"param" : JSON.stringify(jsonObj)
		},
		async      : false
	}).done(function(result) {
		const rows = result.rows;
		var itemsOnPage = 10;
		$("#eventLogListPagination").simplePagination({
			items: rows.length,
			itemsOnPage : itemsOnPage,
			displayedPages : 5,
			edges : 0,
			prevText : "<",
			nextText : ">",
			cssStyle: 'custom-theme',
			onInit : function (){
				eventLogPageClick(1, undefined, rows, itemsOnPage);
			},
			//클릭할때
			onPageClick :  function (pagenumber , event) {
				eventLogPageClick(pagenumber, event, rows, itemsOnPage);
			}
		});
	});
}

function eventLogPageClick(pagenumber, event, data, itemsOnPage) {
	const length = data.length;
	var max = pagenumber * itemsOnPage;
	max = max <= length ? max : length;
	var cnt = (pagenumber - 1) * itemsOnPage;

	$('#eventLogList').html('<ul><li class="thead">'
								+'<span id="evtOcrNo">이벤트 발생번호</span>'
								+'<span id="evtNm">이벤트 명</span>'
								+'<span id="evtDtl">내용</span>'
								+'<span id="evtOcrYmdHms">발생일시</span>'
								+'<span id="evtPrgrsCdKr">상태</span>'
								/* +'<span id="rceptUserId">접수자</span>' */
								+'<span id="evtPlace">발생장소</span>'
							+'</li></ul>');
	
	for(var i = cnt; i < max; i++) {
		const temp = data[i];
		var b_inner = document.createElement('li');
		var s1 = document.createElement('span');
		var s2 = document.createElement('span');
		var s3 = document.createElement('span');
		var s4 = document.createElement('span');
		var s5 = document.createElement('span');
		/* var s6 = document.createElement('span'); */
		var s7 = document.createElement('span');
		
		s1.innerHTML = temp.evtOcrNo;
		s2.innerHTML = temp.evtNm;
		s3.innerHTML = temp.evtDtl;
		s4.innerHTML = temp.evtOcrYmdHms;
		s5.innerHTML = temp.evtPrgrsCdKr;
		/* s6.innerHTML = temp.rceptUserId; */
		s7.innerHTML = temp.evtPlace;
		
		b_inner.appendChild(s1);
		b_inner.appendChild(s2);
		b_inner.appendChild(s3);
		b_inner.appendChild(s4);
		b_inner.appendChild(s5);
		/* b_inner.appendChild(s6); */
		b_inner.appendChild(s7);
		$(b_inner).bind('click', function() {
			
		});
		$('#eventLogList ul').append(b_inner);
	}
}

function excelDown(tableNm, sqlId, fileNm) {
	var path = '/excelDownload/event.'+sqlId+'/action.do';
	excelDownLoadByUlLi('#'+tableNm, path, fileNm, jsonMObj);
}

function excelDownLoadByUlLi(table_obj, path, fileName, data) {
	var url = path;
	var data = data;
	var opts=[];
	var liLen = $(table_obj).find('li.thead > span').length;
	for(var i=0;i<liLen;i++){
		if($(table_obj).find('li.thead span:nth-child('+(i+1)+')').attr('id') != 'undefined' && $(table_obj).find('li.thead span:nth-child('+(i+1)+')').attr('id') != null){
			opts[i] = $(table_obj).find('li.thead span:nth-child('+(i+1)+')').attr('id');
		}
	}
	var cnt = 0;
	for (i = 0; i < opts.length; i++) {
			data["headerField" + cnt] = opts[i];
			data["headerText" + cnt] = $(table_obj).find('li.thead span:nth-child('+(i+1)+')').text();
			cnt++;
	}
	
	data["headerCnt"] = cnt;
	data["fileName"] = fileName;
	
	$.download(url, data, "POST");
}
</script>