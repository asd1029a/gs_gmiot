<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="cont_area">
	<div id="civil_cmplnt_popup" class="gis_event_popup">
		<div class="list">
			<input id="civil_cmplnt_seq_no" type="hidden">
			<input id="civil_cmplnt_area_cd" type="hidden">
			<div class="list_title"><em>민원인 :</em></div>
			<div class="list_cont"><input id="civil_cmplnt_recv_nm" type="text"></div>
			<div class="list_title"><em>민원인연락처 :</em></div>
			<div class="list_cont"><input id="civil_cmplnt_recv_tel" type="text"></div>
		</div>
		<div class="list harf">
			<div class="list_title"><em>민원접수일시 :</em></div>
			<div class="list_cont"><input id="civil_cmplnt_recv_de" type="text"></div>
		</div>
		<div class="list ful">
			<div class="list_title"><em>민원내용 :</em></div>
			<div class="list_cont"><textarea id="civil_cmplnt_recv_cont"></textarea></div>
		</div>
		<div class="list">
			<div class="list_title"><em>위도 :</em></div>
			<div class="list_cont"><input id="civil_cmplnt_lat" type="text" disabled></div>
			<div class="list_title"><em>경도 :</em></div>
			<div class="list_cont"><input id="civil_cmplnt_lon" type="text" disabled></div>
		</div>
		<div class="list ful">
			<div class="list_title"><em>민원접수주소 :</em></div>
			<div class="list_cont"><input id="civil_cmplnt_place" type="text" disabled></div>
		</div>
		<div id="civil_cmplnt_hndl_area">
			<div class="list harf">
				<div class="list_title"><em>처리자 :</em></div>
				<div class="list_cont"><input id="civil_cmplnt_hndl_nm" type="text"></div>
			</div>
			<div class="list harf">
				<div class="list_title"><em>상태 :</em></div>
				<div class="list_cont">
					<select id="civil_cmplnt_flag">
						<option value="R">접수</option>
						<option value="E">처리완료</option>
					</select>
				</div>
			</div>
			<div class="list harf">
				<div class="list_title"><em>처리일시 :</em></div>
				<div class="list_cont"><input id="civil_cmplnt_hndl_de" type="text"></div>
			</div>
			<div class="list ful">
				<div class="list_title"><em>처리내용 :</em></div>
				<div class="list_cont"><textarea id="civil_cmplnt_hndl_cont"></textarea></div>
			</div>
		</div>
		<div class="btn_wrap btn_area_center">
			<a class="btn btnCancle" href="javascript:closePopup()" style="width: 110px;">취소</a>
			<a class="btn btnInsert" href="javascript:getGisCode()" style="width: 110px;">저장</a>
		</div>
	</div>
</div>
<script>
$(function() {
	if (typeof(selectedData) != 'undefined' && selectedData != '') {
		//setData(selectedData);
		$('#civil_cmplnt_hndl_de').val(moment().format("YYYYMMDDHHmmss"));
	}
});

function setData(data) {
	var row = data[0];
	$('#civil_cmplnt_seq_no').val(row.seqNo);
	$('#civil_cmplnt_recv_nm').val(row.civilCmplntRecvNm);
	$('#civil_cmplnt_recv_tel').val(row.civilCmplntRecvTel);
	$('#civil_cmplnt_recv_de').val(row.civilCmplntRecvDe);
	$('#civil_cmplnt_recv_cont').val(row.civilCmplntRecvCont);
	$('#civil_cmplnt_lat').val(row.lat);
	$('#civil_cmplnt_lon').val(row.lon);
	$('#civil_cmplnt_place').val(row.civilCmplntPlace);
	$('#civil_cmplnt_hndl_nm').val(row.civilCmplntHndlNm);
	$('#civil_cmplnt_hndl_de').val(row.civilCmplntHndlDe);
	$('#civil_cmplnt_hndl_cont').val(row.civilCmplntHndlCont);
	$('#civil_cmplnt_flag').val(row.civilCmplntFlag);
	$('#civil_cmplnt_area_cd').val(row.civilCmplntAreaCd);
}

function getGisCode() {
	const lat = $('#civil_cmplnt_lat').val();
	const lon = $('#civil_cmplnt_lon').val();
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
		if (result == "") {
			alert("저장 실패");
			return;
		}
		saveCivilCmplnt(result);
	});
	
}
function saveCivilCmplnt(areaCd) {
	if(!checkNullFocus("civil_cmplnt_recv_nm","민원인을 입력하세요.")) return;
	if(!checkNullFocus("civil_cmplnt_recv_tel","민원인연락처를 입력하세요.")) return;
	if(!checkNullFocus("civil_cmplnt_recv_de","민원접수일시를 입력하세요.")) return;
	if(!checkNullFocus("civil_cmplnt_recv_cont","민원 내용을 입력하세요.")) return;
	if(!validateNumberCustom("civil_cmplnt_recv_tel","민원인연락처는 숫자만 입력가능합니다.")) return;
	if(!validateNumberCustom("civil_cmplnt_recv_de","민원접수일시는 숫자만 입력가능합니다.")) return;
	if($("#civil_cmplnt_hndl_de").val().length>0 && !validateNumberCustom("civil_cmplnt_hndl_de","처리일시는 숫자만 입력가능합니다.")) return;
	
	const jsonObj = {};
	jsonObj.seqNo = $('#civil_cmplnt_seq_no').val();
	jsonObj.civilCmplntRecvNm = $('#civil_cmplnt_recv_nm').val();
	jsonObj.civilCmplntRecvTel = $('#civil_cmplnt_recv_tel').val();
	jsonObj.civilCmplntRecvDe = $('#civil_cmplnt_recv_de').val();
	jsonObj.civilCmplntRecvCont = $('#civil_cmplnt_recv_cont').val();
	jsonObj.civilCmplntLat = $('#civil_cmplnt_lat').val();
	jsonObj.civilCmplntLon = $('#civil_cmplnt_lon').val();
	jsonObj.civilCmplntPlace = $('#civil_cmplnt_place').val();
	jsonObj.civilCmplntAreaCd = areaCd;
	jsonObj.civilCmplntHndlNm = $('#civil_cmplnt_hndl_nm').val();
	jsonObj.civilCmplntHndlDe = $('#civil_cmplnt_hndl_de').val();
	jsonObj.civilCmplntHndlCont = $('#civil_cmplnt_hndl_cont').val();
	jsonObj.civilCmplntFlag = $('#civil_cmplnt_flag').val();
	
	$.ajax({
		type       : "POST",
		url        : "/ajax/insert/common.saveCivilCmplnt/action.do",
		dataType   : "json",
		data : {"param" : JSON.stringify(jsonObj)},
		async      : false,
		beforeSend : function(xhr) {
			// 전송 전 Code
		}
	}).done(function (result) {
		alert("저장완료");
		closePopup();
		getCivilCmplnt();
	}).fail(function (xhr) {
		alert("저장실패");
	}).always(function() {
		
	});
}

function closePopup() {
	$('#mapPopup').dialog('close');
}
</script>