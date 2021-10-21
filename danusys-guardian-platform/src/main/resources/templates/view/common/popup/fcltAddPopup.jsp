<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="popup">
	<dl class="popup-tit">
		<dt>시설물 추가</dt>
		<dd onClick="closePopup();"><img src="../images/icons/icon_closed.png"></dd>
	</dl>
	<div class="fclt-cont">
		<ul>
			<li>
				<dl>
					<dd>시설물 아이디</dd>
					<dt>
						<input type="text" id="fcltId">
					</dt>
				</dl>
				<dl>
					<dd>시설물 이름</dd>
					<dt>
						<input type="text" id="fcltNm">
					</dt>
				</dl>
			</li>
			<li>
				<dl>
					<dd>위도</dd>
					<dt>
						<input type="text" id="fcltLat">
					</dt>
				</dl>
				<dl>
					<dd>경도</dd>
					<dt>
						<input type="text" id="fcltLon">
					</dt>
				</dl>
			</li>
			<li>
				<dl>
					<dd>시설물 종류</dd>
					<dt>
						<select id="fcltKind"></select>
					</dt>
				</dl>
				<dl>
					<dd>시설물 용도</dd>
					<dt>
						<select id="fcltPurpose">
							<option value="">-선택-</option>
						</select>
					</dt>
				</dl>
			</li>
			<li>
				<dl>
					<dd>시설물 주소</dd>
					<dt>
						<input type="text" id="fcltPlace">
					</dt>
				</dl>				
			</li>
			<li>
				<dl>
					<dd>관리번호</dd>
					<dt>
						<input type="text" id="fcltMgmtNo">
					</dt>
				</dl>
				<!-- <dl>
					<dd>읍면동</dd>
					<dt>
						<select id="fcltAreaCd">
							<option value="">-선택-</option>
						</select>
					</dt>
				</dl> -->
			</li>
		</ul>
	</div>
	<ul class="right-btn">
		<!-- <li id="moveCoord">좌표이동</li> -->
		<li onClick="closePopup(true);">취소</li>
		<li id="insertFclt">추가</li>
		<li id="updateFclt">수정</li>
	</ul>
</div>

<script>
$(function(){
	fcltKind();
	fcltPurpose();
	$('#insertFclt').bind('click', function(e) {
		getAreaCode(saveFclt);
	});
	
	$('#updateFclt').bind('click', function(e) {
		getAreaCode(updateFclt);
	});
});

function setMooveCoordEvent(data) {
	$('#moveCoord').bind('click', function(e) {
		addDragFeature(data);
		$('#fcltPopup').hide();
	});
}

function fcltKind(){
	common.getSelectBoxItem(
			{
			    "el"                : "fcltKind",
			    "sql"               : "common.getFcltKnd",
			    "val"               : "code",
			    "text"              : "name",
			    "isAllCheckCustom"	: "-선택-",
			    "userId"			: '${admin.id}'
			}
	);
	
	/* $('#fcltKind').bind('change',function(){
		var value = $(this).val();
		if(value != "" && value != "undefined"){
			fcltPurpose(value);
		} else {
			$('#fcltPurpose').html('');
			$('#fcltPurpose').append($('<option></option>').val('').html('-선택-'));
		}
	}); */
}

function fcltPurpose(kind){
	common.getSelectBoxItem(
			{
			    "el"                : 'fcltPurpose',
			    "sql"               : "common.getFcltPurpose",
			    "val"               : "code",
			    "text"              : "name",
			    "isAllCheckCustom"	: "-선택-"
			}
	);
}

function getAreaCode(callback) {
	const lat = $('#fcltLat').val();
	const lon = $('#fcltLon').val();
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
		callback(result);
	});
	
}

function saveFclt(areaCd){
	const jsonObj = {};
	var fcltId = $('#fcltId').val();
	var fcltNm = $('#fcltNm').val();
	var fcltLon = $('#fcltLon').val();
	var fcltLat = $('#fcltLat').val();
	var fcltKind = $('#fcltKind').val();
	var fcltPurpose = $('#fcltPurpose').val();
	var fcltPlace = $('#fcltPlace').val();
	
	if(!checkNull(fcltId,"시설물 아이디를 입력해주세요")) return
	if(!checkNull(fcltNm,"시설물 이름을 입력해주세요")) return
	if(!checkNull(fcltLon,"시설물 위도를 입력해주세요")) return
	if(!checkNull(fcltLat,"시설물 경도를 입력해주세요")) return
	if(!checkNull(fcltKind,"시설물 종류를 입력해주세요")) return
	//if(!checkNull(fcltPurpose,"시설물 용도를 입력해주세요")) return
	if(!checkNull(fcltPlace,"시설물 장소를 입력해주세요")) return
	
	const checkFcltId = (function() {
		let rtnVal;
		const jsonObj = {};
		jsonObj.fcltId = fcltId;
		$.ajax({
			type		: "POST",
			url			: "/select/facility.getCheckFcltId/action.do",
			dataType	: "json",
			data		: {"param" : JSON.stringify(jsonObj)},
			async		: false
		}).done(function(result) {
			const rows = result.rows;
			rtnVal = rows[0];
			
		}).fail(function (xhr) {
			
		}).always(function() {
			
		});
		return rtnVal;
	}());
	
	if(checkFcltId > 0) {
		alert("이미 존재하는 시설물 아이디 입니다.");
		return
	}
	
	jsonObj.fcltId = fcltId;
	jsonObj.fcltNm = fcltNm;
	jsonObj.fcltLon = fcltLon;
	jsonObj.fcltLat = fcltLat;
	jsonObj.fcltKind = fcltKind;
	jsonObj.fcltPurpose = fcltPurpose;
	jsonObj.fcltPlace = fcltPlace;
	jsonObj.areaCd = areaCd;
	jsonObj.fcltMgmtNo = $('#fcltMgmtNo').val();
	
	$.ajax({
		type		: "POST",
		url			: "/ajax/insert/facility.insertFclt/action.do",
		dataType	: "json",
		data 		: {"param" : JSON.stringify(jsonObj)},
		async 		: false,
	}).done(function() {
		alert('저장 완료');
		selectFclt();
		closePopup();
		mapManager.removeOverlayById('fcltSelected');
	}).fail(function(xhr) {
		alert('저장 실패');
	}).always(function() {
		
	});
}

function updateFclt(areaCd){
	const jsonObj = {};
	var fcltId = $('#fcltId').val();
	var fcltNm = $('#fcltNm').val();
	var fcltLon = $('#fcltLon').val();
	var fcltLat = $('#fcltLat').val();
	var fcltKind = $('#fcltKind').val();
	var fcltPurpose = $('#fcltPurpose').val();
	var fcltPlace = $('#fcltPlace').val();
	
	if(!checkNull(fcltId,"시설물 아이디를 입력해주세요")) return;
	if(!checkNull(fcltNm,"시설물 이름을 입력해주세요")) return;
	if(!checkNull(fcltLon,"시설물 위도를 입력해주세요")) return;
	if(!checkNull(fcltLat,"시설물 경도를 입력해주세요")) return;
	if(!checkNull(fcltKind,"시설물 종류를 입력해주세요")) return;
	//if(!checkNull(fcltPurpose,"시설물 용도를 입력해주세요")) return;
	if(!checkNull(fcltPlace,"시설물 장소를 입력해주세요")) return;
	
	jsonObj.fcltId = fcltId;
	jsonObj.fcltNm = fcltNm;
	jsonObj.fcltLon = fcltLon;
	jsonObj.fcltLat = fcltLat;
	jsonObj.fcltKind = fcltKind;
	jsonObj.fcltPurpose = fcltPurpose;
	jsonObj.fcltPlace = fcltPlace;
	jsonObj.areaCd = areaCd;
	jsonObj.fcltMgmtNo = $('#fcltMgmtNo').val();
	
	$.ajax({
		type		: "POST",
		url			: "/ajax/insert/facility.insertFclt/action.do",
		dataType	: "json",
		data 		: {"param" : JSON.stringify(jsonObj)},
		async 		: false,
	}).done(function() {
		alert('저장 완료');
		selectFclt();
		closePopup();
		mapManager.removeOverlayById('fcltSelected');
	}).fail(function(xhr) {
		alert('저장 실패');
	}).always(function() {
		
	});
}

function revertCoordinate() {
	const fcltId = $('#fcltId').val();
	const feature = mapManager.getVectorLayer('fclt').getSource().getFeatureById(fcltId);
	const prop = feature.getProperties();
	const coordinate = new ol.proj.transform([prop.lon, prop.lat],mapManager.properties.pro4j[mapManager.properties.type],mapManager.properties.projection);
	feature.getGeometry().setCoordinates(coordinate);
}

function closePopup(flag) {
	if(flag && $('#fcltId').val() !== '') revertCoordinate();
	$('#fcltPopup').dialog('close');
}

</script>