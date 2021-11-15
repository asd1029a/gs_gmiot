<!-- 시설물현황조회 -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" type="text/css" href="/css/import/easyui_datagrid.css">
<div class="cont_area" style="height:100%; background-color: #444; color: #fff;">
	<div class="list">
		<em>검색 조건 : </em>
		<input type="radio" name="facility_state" id="fcltStateTot" checked value=""/>
		<label for="fcltStateTot">전체</label>
		<input type="radio" name="facility_state" id="fcltStateNotCoord" value="0"/>
		<label for="fcltStateNotCoord">좌표없음</label>
	</div>
	
	<table id="facilityListTable" style="height:88%;"></table>
	
	<div class="btn_wrap btn_area_center">
		<a class="btn btnExcel" id="fcltListExcelBtn">엑셀저장</a>
		<a class="btn btnCancle" onclick="javascript:closePopup()">닫기</a>
	</div>
</div>

<script>
$(document).ready(function(){
	$facilityState = $('input[name="facility_state"]');
	$facilityState.on('click', function() {
		selectFacilityList();
	})
	
	selectFacilityList();
});

function selectFacilityList() {
	const stateCd = $('input[name="facility_state"]:checked').val();
	
	const jsonObj = {};
	jsonObj.pageKind = 'manage';
	if (stateCd == '0') jsonObj.lonDefind = 'lonDefind';
	
	$('#fcltListExcelBtn').on('click', function() {
		const url = "/excelDownload/facility.selectFcltSList/action.do";
		const fileName = "facilityList";
		
		excelDownLoad($('#facilityListTable'), url, fileName, jsonObj);
	});
	
	$('#facilityListTable').datagrid({
	    url:'/selectList/facility.selectFcltSList/action.do',
	    pagination:true,
	    pageSize:15,
	    queryParams : {
			pageSize: 15,
			param : JSON.stringify(jsonObj)
	    },
	    columns:[[
	        {field: 'rnum', title: '순번', width:'5%', align:'center'},
			{field: 'mgmtNo', title: '관리번호', width:'20%', align:'center'},
			{field: 'fcltNm', title: 'CCTV명', width:'40%', align:'center'},
			{field: 'lat', title: '경도', width:'10%', align:'center'},
			{field: 'lon', title: '위도', width:'10%', align:'center'},
			{field: 'fcltPuposeNm', title: '용도', width:'15%', align:'center'}
	    ]],
	    onLoadSuccess:function(data){
	    	$('#loading').hide();
	    	if(data.rows=='sessionOut'){
				alert('로그인 시간이 만료되었습니다.');
				closeWindow();
			}
	    }
	});
}

function closePopup() {
	$('#mapPopup').dialog('close');
}
</script>

