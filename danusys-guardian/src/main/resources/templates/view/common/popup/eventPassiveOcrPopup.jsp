<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="popup">
	<dl class="popup-tit">
		<dt>수동발생</dt>
		<dd onClick="closePopup();"><img src="../images/icons/icon_closed.png"></dd>
	</dl>
	<div class="occur-cont">
		<ul>
			<li>
				<dl>
					<dd>이벤트 종류</dd>
					<dt>
						<select id="passiveOcrEventKind"></select>
					</dt>
				</dl>
				<dl>
					<dd>발생시간</dd>
					<dt>
						<input type="text" id="passiveOcrTime">
					</dt>
				</dl>
			</li>
			<li>
				<dl>
					<dd>위도</dd>
					<dt>
						<input type="text" id="passiveOcrLat">
					</dt>
				</dl>
				<dl>
					<dd>경도</dd>
					<dt>
						<input type="text" id="passiveOcrLon">
					</dt>
				</dl>
			</li>
			<li>
				<dl>
					<dd>이벤트 주소</dd>
					<dt>
						<input type="text" id="passiveOcrPlace">
					</dt>
				</dl>				
			</li>
			<li>
				<dl>
					<dd>내용</dd>
					<dt>
						<textarea id="passiveOcrContent"></textarea>
					</dt>
				</dl>
			</li>
			<li><input type="checkbox" id="sendTouring"><label for="sendTouring"></label>카메라 투어링 종료</li>
		</ul>
	</div>
	<ul class="right-btn">
		<li onClick="closePopup();">취소</li>
		<li onClick="getGisCode();">전송</li>
	</ul>
</div>
<script>
$(function() {
	common.getSelectBoxItem(
		{
		    "el"                : "passiveOcrEventKind",
		    "sql"               : "common.getEventCode",
		    "val"               : "code",
		    "text"              : "name",
		    "isAllCheckCustom"	: "-선택-",
		    "userId"			: '${admin.id}'
		}
	);
	
	$('#passiveOcrEventKind').bind('change', function() {
		if($(this).val() === 'ACCSEA') {
			$('#passiveOcrLat').prop('disabled', false);
			$('#passiveOcrLon').prop('disabled', false);
		} else {
			$('#passiveOcrLat').prop('disabled', true);
			$('#passiveOcrLon').prop('disabled', true);
		}
	});
});

function getGisCode() {
	const lat = $('#passiveOcrLat').val();
	const lon = $('#passiveOcrLon').val();
	const jsonObj = {};
	
	jsonObj.lat = lat;
	jsonObj.lon = lon;

	$.ajax({
		type       : "POST",
		url        : "/getGisCodeFromFront.do",
		dataType   : "json",
		data : {"param" : JSON.stringify(jsonObj)},
		async      : true,
		beforeSend : function(xhr) {
			// 전송 전 Code
		}
	}).done(function (result) {
		/* if (result == "") {
			alert("저장 실패");
			return;
		} */
		savePassiveEvent(result);
	});
	
}
function savePassiveEvent(areaCd) {
	const evtId = $('#passiveOcrEventKind').val();
	const evtOcrYmdHms = $('#passiveOcrTime').val();
	const evtDtl = $('#passiveOcrContent').val();
	const lat = $('#passiveOcrLat').val();
	const lon = $('#passiveOcrLon').val();
	
	if(!checkNull(evtId,"이벤트 종류를 선택하세요.")) return;
	if(!checkNull(evtDtl,"이벤트 내용을 입력하세요.")) return;
	
	const jsonObj = {};
	jsonObj.evtOcrNo = evtId + evtOcrYmdHms;
	jsonObj.evtId = evtId;
	jsonObj.evtOcrYmdHms = evtOcrYmdHms;
	jsonObj.evtGrad = '10';
	jsonObj.lat = lat;
	jsonObj.lon = lon;
	jsonObj.areaCd = areaCd;
	jsonObj.evtPlace = $('#passiveOcrPlace').val();
	jsonObj.evtDtl = evtDtl;
	
	$.ajax({
		type       : "POST",
		url        : "/ajax/insert/event.insertEvent/action.do",
		dataType   : "json",
		data : {"param" : JSON.stringify(jsonObj)},
		async      : false,
		beforeSend : function(xhr) {
			// 전송 전 Code
		}
	}).done(function (result) {
		if($('#sendTouring').is(':checked')) eventTouringSend(jsonObj, false);
		alert("전송 완료");
		selectEvent();
		closePopup();
	}).fail(function (xhr) {
		alert("저장 실패");
	}).always(function() {
		
	});
}

function closePopup() {
	$('#mapPopup').dialog('close');
}
</script>