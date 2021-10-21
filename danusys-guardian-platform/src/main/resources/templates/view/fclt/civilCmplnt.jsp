<!-- 민원관리 -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" type="text/css" href="/css/civilCmplnt.css">
<div class="list_menu_title">민원관리</div>
<div id="civil_cmplnt">
	<div class="search_content">
		<div class="search_list">
			<div class="left">
				<em>상태</em>
			</div>
			<div class="right">
				<select id="searchFlag" class="combo_select" onchange="getCivilCmplnt();" style="width:123px;">
					<option value="">전체</option>
					<option value="R">접수</option>
					<option value="E">처리완료</option>
				</select>
			</div>
		</div>
		<div class="search_list">
			<div class="left">
				<em>민원인</em>
			</div>
			<div class="right">
				<input type="text" id="searchRecvNm" style="width:120px;" onkeypress="if(event.keyCode==13){getCivilCmplnt();}"/>
			</div>
		</div>
		<div class="search_list">
			<div class="left">
				<em>민원인연락처</em>
			</div>
			<div class="right">
				<input type="text" id="searchRecvTel" style="width:120px;" onkeypress="if(event.keyCode==13){getCivilCmplnt();}"/>
			</div>
		</div>
		<div class="search_list">
			<div class="left">
				<em>시작일</em>
			</div>
			<div class="right">
				<input id="searchRecvTimeS" data-options="formatter:myformatter,parser:myparser,prompt:'시작일 입력'" style="width:123px;margin: 0;">
			</div>
		</div>
		<div class="search_list">
			<div class="left">
				<em>종료일</em>
			</div>
			<div class="right">
				<input id="searchRecvTimeE" data-options="formatter:myformatter,parser:myparser,prompt:'종료일 입력'" style="width:123px;margin: 0;">
			</div>
		</div>
		<div class="search_list">
			<div>
				<input type="checkbox" id="mapYn"/>
				<label for="mapYn">지도 내 검색</label>
			</div>
			
			<!-- <input type="checkbox" id="map_marker_yn" checked>
			<label for="map_marker_yn">지도마커표시</label> -->
			
			<a class="btn btnDelete disable" id="dltCivil" href="#">삭제</a>
			<a class="btn btnSelect" href="javascript:getCivilCmplnt()">조회</a>
		</div>
	</div>
	
	<div id="civilCmplntTable">
	</div>
	
	<div class="btn_area_center">
		<!-- <a href="#" class="btnModify" onclick="modifyCivilCmplnt()">상세/수정</a> -->
	</div>
</div>

<script type="text/javascript">
var selectedData = [];
$(document).ready(function(){
	setBodyClass('civil_container');
	//setMapCivilCmplntMarker(null);
	$('#searchRecvTimeS').datebox({requeired:true});
	$('#searchRecvTimeE').datebox({requeired:true});
	
	$("#map_marker_yn").click(function(){
        if($(this).is(":checked")) {
        	mapManager.getVectorLayer('civil').setVisible(true);
        } else {
        	mapManager.getVectorLayer('civil').setVisible(false);
        }
    });
	
	$('#dltCivil').bind('click', function() {
		if($(this).hasClass('disable')) return;
		
		if(confirm("민원을 삭제 하시겠습니까?")) {
			deleteCivilCmplnt();
			$(this).addClass('disable');
		}
	});
	
	getCivilCmplnt();
});


var grid;
function getCivilCmplnt() {
	if(!$('#loading').is(':visible')) $('#loading').toggle();
	if(typeof(grid) != "undefined") grid.destroy();

	const jsonObj = {};
	
	jsonObj.userId = '${admin.id}';
	jsonObj.civilCmplntFlag = $('#searchFlag').val();
	jsonObj.civilCmplntRecvNm = $('#searchRecvNm').val();
	jsonObj.civilCmplntRecvTel = $('#searchRecvTel').val();
	jsonObj.civilCmplntRecvDeS = $('#searchRecvTimeS').datebox('getValue').replace(/-/g,'');
	jsonObj.civilCmplntRecvDeE = $('#searchRecvTimeE').datebox('getValue').replace(/-/g,'');
	//jsonObj.pageKind = 'manage';
	//jsonObj.menuNm = 'facility';

	if($('#mapYn').prop('checked')) {
		var extent = mapManager.map.getView().calculateExtent(mapManager.map.getSize());

		var posSW = ol.proj.transform([extent[0], extent[1]], mapManager.properties.projection, mapManager.properties.pro4j[mapManager.properties.type]).toString().replace(',',' ');
		var posNW = ol.proj.transform([extent[0], extent[3]], mapManager.properties.projection, mapManager.properties.pro4j[mapManager.properties.type]).toString().replace(',',' ');
		var posSE = ol.proj.transform([extent[2], extent[1]], mapManager.properties.projection, mapManager.properties.pro4j[mapManager.properties.type]).toString().replace(',',' ');
		var posNE = ol.proj.transform([extent[2], extent[3]], mapManager.properties.projection, mapManager.properties.pro4j[mapManager.properties.type]).toString().replace(',',' ');
		
		var mapBound = posNW+','		//북서
					   +posNE+','	//북동
					   +posSE+','	//남동
					   +posSW+','	//남서
					   +posNW;

		jsonObj.mapBound = mapBound;
	}
	
	$.ajax({
		type       : "POST",
		url 		: "/select/common.getCivilCmplntList/action.do",
		dataType   : "json",
		data       : {
			"param" : JSON.stringify(jsonObj)
		},
		async      : true
	}).done(function(result) {
		const rows = result.rows;
    	if(rows=='sessionOut'){
			alert('로그인 시간이 만료되었습니다.');
			closeWindow();
		}
		grid = new tui.Grid({
			el: document.getElementById('civilCmplntTable'),
			data: rows,
			scrollX: false,
			scrollY: true,
			bodyHeight: Number((innerHeight>988?988:innerHeight)-280),
			//bodyHeight: 'auto',
			rowHeaders: ['rowNum'],
			rowHeight: 'auto',
			pageOptions: {
				useClient: true,
				perPage: 20
			},
			columns: [
				{
					header: '민원인',
					name: 'civilCmplntRecvNm',
					sortable: true,
					width: 80,
					filter: {
						type: 'text',
						operator: 'OR'
					}
				},
				{
					header: '연락처',
					name: 'civilCmplntRecvTel',
					sortable: true,
					width: 80,
					filter: {
						type: 'text',
						operator: 'OR'
					}
				},
				{
					header: '접수일시',
					name: 'civilCmplntRecvDeCustom',
					sortable: true,
					width: 90,
					filter: {
						type: 'text',
						operator: 'OR'
					}
				}
			],
			onGridMounted: function(e) {
				$('#loading').hide();
			}
		});
		
		grid.on('click', function (ev) {
			if(typeof(ev.rowKey) == "undefined") return;
				var record = {
					start: [ev.rowKey, 0],
					end: [ev.rowKey, grid.getColumns().length]
				}
			grid.setSelectionRange(record);
		});
		
		$('#civilCmplntTable tbody').off('click');
		$('#civilCmplntTable tbody').on( 'mousedown', 'tr', function (e) {
			const currentPage = grid.getPagination().getCurrentPage() - 1;
			const itemsPerPage = grid.getPagination()._options.itemsPerPage;
			const rowIndex = this.rowIndex + (itemsPerPage * currentPage);
			var rows = grid.getRow(rowIndex);
			mapManager.setCenter(ol.proj.transform([rows.lon, rows.lat], mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection));
			$('#dltCivil').removeClass('disable');
		});
		
		//setCivilCmplntDraw({}, '');
	});
}

function modifyCivilCmplnt(data) {
	var row;
	if(typeof(data) != "undefined") row = data;
	else row = $('#civilCmplntTable').datagrid('getSelected');
	
	if(row == null) {
		alert('선택 된 정보가 없습니다.');
		return;
	}
	
	var page = '/main/popup/civil_cmplnt_popup'; 
    $("#menu_container_popup").html("");
    $("#menu_container_popup").load("/action/page.do", { path : page }, function() {
    	$("#civil_cmplnt_hndl_area").show();
    });
    
	const jsonObj = {};
	jsonObj.seqNo = row.seqNo;

    $.ajax({
    	type       : "POST",
    	url        : "/select/common.getCivilCmplntList/action.do",
    	dataType   : "json",
    	data       : {"param" : JSON.stringify(jsonObj)},
    	async      : false,
    	beforeSend : function(xhr) {}
    }).done(function (result) {
		const rows = result.rows;
    	if(rows=='sessionOut'){
			alert('로그인 시간이 만료되었습니다.');
			closeWindow();
		}
    	selectedData = rows;
    	$('#menu_container_popup').dialog({
    		title: '민원관리',
    		width: 600,
    		height: 560,
    		left: 610,
    		top: 150,
    		closed: false,
    		cache: false,
    		modal: true,
    		onClose: function() {
    			$("#civil_cmplnt_hndl_area").hide();
    			selectedData = '';
    			$('#civilCmplntTable').datagrid('reload');
    		}
    	});
    	$('#menu_container_popup').dialog('open');
    }).fail(function (xhr) {
        
    }).always(function() {

    });
}

function deleteCivilCmplnt() {
	const row = grid.getRowAt(grid.getFocusedCell().rowKey);
	const jsonObj = {};
	jsonObj.seqNo = row.seqNo;
	
    $.ajax({
    	type       : "POST",
        url        : "/ajax/delete/common.deleteCivilCmplnt/action.do",
        dataType   : "json",
        data : {"param" : JSON.stringify(jsonObj)},
        async      : false,
        beforeSend : function(xhr) {}
    }).done(function (result) {
        if (result == "SUCCESS") {
        	getCivilCmplnt();
            alert("삭제 완료");
        }
        else {
            alert("삭제 실패");
        }
    }).fail(function (xhr) {
        alert("삭제 실패");
    }).always(function() {

    });
	/* var chkRows = grid.getCheckedRows();
	if(chkRows.length > 0) {
		if(confirm(chkRows.length+"개의 데이터를 삭제하시겠습니까?")) {
			var deleteRowId = [];
			for(var i=0; i<chkRows.length; i++) {
				deleteRowId.push(chkRows[i].seqNo);
			}
			
			const jsonObj = {};
			jsonObj.singleDeleteSid = "common.deleteCivilCmplntList";
			jsonObj.deleteRowId = deleteRowId;
		    $.ajax({
		    	type       : "POST",
	            url        : "/multiAjax/action.do",
	            dataType   : "json",
	            data : {"param" : JSON.stringify(jsonObj)},
	            async      : false,
	            beforeSend : function(xhr) {}
	        }).done(function (result) {
		        if (result == "SUCCESS") {
		        	getCivilCmplnt();
		            alert("삭제 완료");
		        }
		        else {
		            alert("삭제 실패");
		        }
		    }).fail(function (xhr) {
		        alert("삭제 실패");
		    }).always(function() {

		    });
		}
	}
	else {
		alert('선택 된 정보가 없습니다.');
		return;
	} */
}


</script>

