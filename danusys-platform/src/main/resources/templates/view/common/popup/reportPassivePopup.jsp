<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>통합플랫폼 업무일지 이상징후 전송</title>
<link rel="shortcut icon" href="/favicon.ico" type="image/x-icon">

<link rel="stylesheet" type="text/css" href="/css/common.css">
<link rel="stylesheet" type="text/css" href="/css/jquery/jquery-ui.css">
<link rel="stylesheet" type="text/css" href="/css/reportPassivePopup.css">

<script src="/js/jquery/jquery.min.js"></script>
<script src="/js/jquery/jquery-ui.js"></script>
<script src="/js/moment.min.js"></script>
<script src="/js/common.js"></script>

<script type="text/javascript">
$(function() {
	$('#tabs').tabs({
		select : function(e, ui) {
			selectedTitle = $(ui.tab).text();
		}
	});
	$('input:radio[name=event_code]').bind('click', function() {
		const id = $(this).attr('id');
		if(id == 'eventEtc') $('#eventEtcText').show();
		else $('#eventEtcText').hide();
	});
	$('input:radio[name=disorder_code]').bind('click', function() {
		const id = $(this).attr('id');
		if(id == 'disorderEtc') $('#disorderEtcText').show();
		else $('#disorderEtcText').hide();
	});
	
	setReportData();
});

function setReportData() {
	const fcltName = '(${mgmtNo}) ${fcltNm}';
	const fcltPurposeNm = '${fcltPurposeNm}';
	const userId = '${userId}';
	const imgUrl = '${imgUrl}';
	const ocrYmdHm = moment().format('YYYY년 MM월 DD일 hh시 mm분');
	
	if(fcltName != '') $('#fcltName').text(fcltName);
	if(imgUrl != '') $('#fcltImg').attr('src', '/file/getImage2.do?sPath=report&imageUrl='+imgUrl);
	if(fcltPurposeNm != '') $('#fcltPurposeNm').text(fcltPurposeNm);
	if(userId != '') $('#userId').text(userId);
	$('#ocrYmdHm').text(ocrYmdHm);
}

function checkId() {
	const jsonObj = {};
	jsonObj.ssoLoginId = '${userId}';
	
	$.ajax({
		type       	: "POST",
		url        	: "/login/checkSsoLoginId.do",
		dataType   	: "json",
		contentType : "application/json; charset=utf-8",
		data : JSON.stringify(jsonObj),
		async      : true
	}).done(function (result) {
		if(result.result == 'Y') {
			saveReport();
		} else if(result.result == 'N') {
			alert('통합플랫폼에 ${userId}와(과) 동일한 사용자 아이디가 없습니다.\n통합플랫폼 관리자에게 문의 하십시오.')
		}
	}).fail(function (xhr) {
		
	});
}

function saveReport() {
	const flag = $('#tabs').tabs('option', 'active');
	const content = $('#content').val();
	const fcltId = '${fcltId}';
	const ocrYmdHm = moment($('#ocrYmdHm').text(), 'YYYYMMDDhhmmss').format('YYYYMMDDhhmmss');
	var type = '', etc = '', etcId = '';
	
	if(flag == 0) {
		type = $('input:radio[name=event_code]:checked').val();
	} else if(flag == 1) {
		type = $('input:radio[name=event_code]:checked').val();
	}
	
	if(type.indexOf('Etc') > -1) etcId = flag == 0 ? 'eventEtcText' : 'disorderEtcText'; 
	
	if(!checkNullFocus('content',"내용을 입력하세요.")) return;
	
	if(type.indexOf('Etc') > -1) if(!checkNullFocus(etcId,"기타 내용을 입력하세요.")) return;
	
	etc = $('#'+etcId).val();
	
	const jsonObj = {};
	jsonObj.division = 'VMS' + $($("#tabs li")[flag]).text();
	jsonObj.type = $('#fcltPurposeNm').text();
	jsonObj.subject = etc !== '' && typeof etc !== 'undefined' ? etc : $('label[for='+type+']').text();
	jsonObj.content = content;
	jsonObj.fcltId = fcltId;
	jsonObj.fcltNm = '${fcltNm}';
	jsonObj.fcltPrps = '${fcltPurposeCd}';
	jsonObj.userId = '${userId}';
	jsonObj.eventDate = ocrYmdHm;
	jsonObj.img = '${imgUrl}';
	jsonObj.endYN = 'Y';
	
	$.ajax({
		type       : "POST",
		url        : "/ajax/insert/report.saveReportTask/action.do",
		dataType   : "json",
		data : {"param" : JSON.stringify(jsonObj)},
		async      : false,
		beforeSend : function(xhr) {
			// 전송 전 Code
		}
	}).done(function (result) {
		alert("전송 완료");
		window.close();
	}).fail(function (xhr) {
		alert("전송 실패");
	}).always(function() {
		
	});
}
</script>
</head>
<body>
	<div id="tabs" class="container">
		<ul>
			<li><a href="#event">이벤트</a></li>
			<li><a href="#disorder">장애</a></li>
		</ul>
		<div class="tabs_wrap" id="event">
			<div class="radio_wrap">
				<input type="radio" name="event_code" id="crime" value="crime" checked/>
				<label for="crime">방범</label>
			</div>
			<div class="radio_wrap">
				<input type="radio" name="event_code" id="112" value="112"/>
				<label for="112">방범(112신고)</label>
			</div>
			<div class="radio_wrap">
				<input type="radio" name="event_code" id="emergency" value="emergency"/>
				<label for="emergency">긴급상황</label>
			</div>
			<div class="radio_wrap">
				<input type="radio" name="event_code" id="dumping" value="dumping"/>
				<label for="dumping">무단투기</label>
			</div>
			<div class="radio_wrap">
				<input type="radio" name="event_code" id="eventEtc" value="eventEtc"/>
				<label for="eventEtc">기타</label>
				<input type="text" id="eventEtcText" class="hidden" placeholder="">
			</div>
		</div>
		<div class="tabs_wrap" id="disorder">
			<div class="radio_wrap">
				<input type="radio" name="disorder_code" id="declaration" value="declaration" checked/>
				<label for="declaration">장애신고</label>
			</div>
			<div class="radio_wrap">
				<input type="radio" name="disorder_code" id="pruning" value="pruning"/>
				<label for="pruning">작업요청(가지치기)</label>
			</div>
			<div class="radio_wrap">
				<input type="radio" name="disorder_code" id="disorderEtc" value="disorderEtc"/>
				<label for="disorderEtc">기타</label>
				<input type="text" id="disorderEtcText" class="hidden" placeholder="">
			</div>
		</div>
	</div>
	<div class="container">
		<ul>
			<li>
				<div class="title">
					<span>작성자 명</span>
				</div>
				<div class="cont">
					<span id="userId"></span>
				</div>
			</li>
			<li>
				<div class="title">
					<span>내용</span>
				</div>
				<div class="cont">
					<textarea id="content" rows="5"></textarea>
				</div>
			</li>
			<li>
				<div class="title">
					<span>카메라 이름</span>
				</div>
				<div class="cont">
					<span id="fcltName">(관리번호) 카메라명</span>
				</div>
			</li>
			<li>
				<div class="title">
					<span>카메라 종류</span>
				</div>
				<div class="cont">
					<span id="fcltPurposeNm">방범</span>
				</div>
			</li>
			<li>
				<div class="title">
					<span>발생일시</span>
				</div>
				<div class="cont">
					<span id="ocrYmdHm">2020년 07월 13일 11시 43분 00초</span>
				</div>
			</li>
		</ul>
	</div>
	<div class="btn_container">
		<input type="button" class="btn" onclick="checkId()" value="전송"/>
		<input type="button" class="btn" onclick="window.close()" value="취소"/>
	</div>
	<div>
		<div class="title">
			<span>사진</span>
		</div>
		<div class="cont">
			<img src="/images/report/no_image.png" id="fcltImg" onerror="this.src='/images/report/no_image.png'">
			<!-- <div id="fcltImg" style="width: 100%; height: 100%;"></div> -->
		</div>
	</div>
</body>
</html>