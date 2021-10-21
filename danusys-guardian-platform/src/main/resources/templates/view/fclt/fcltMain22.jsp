<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script>
$(document).ready(function(){
	setBodyClass('fclt_container');
	$('#slctStat').combobox({onChange: getFacility});
	
	$("ul.tabs li").click(function () {
		$("ul.tabs li").removeClass("active").css("color", "#333");
		$(this).addClass("active").css("color", "#6e87df");
		$(".tab_content").hide();
		var activeTab = $(this).attr("rel");
		$("#" + activeTab).fadeIn();

		getFacility();
    });
	
	setSearchCombo();
	
	$("ul.tabs li:first").click();
});
</script>

<div class="container">
	<div class="list_menu_title">시설물</div>
	<ul class="tabs">
		<li rel="site">현장 장비</li>
		<!-- <li rel="center">센터 장비</li> -->
	</ul>
	<div class="tab_container">
		<div class="search_content">
			<ul>
				<li>
					<a id="fcltStat" href="#" onclick="javascript:$('#slctStat').combobox('showPanel');"></a>
					<select id="slctStat" class="combo_select" style="width:75px; height: 30px;">
						<option value="">장애상태</option>
						<option value="0">연결</option>
						<option value="1">미연결</option>
					</select>
				</li>
				<li>
					<a href="javascript:$('#selectSpaceCombo').combobox('showPanel');"></a>
					<select id="selectSpaceCombo" class="easyui-combotree" style="width:70px; height:30px;"
				           		data-options="idField:'code',textField:'name', onChange: getFacility,
				           					value:['행정구역'],panelWidth: 150,labelPosition:'top',multiple:true"></select>
				</li>
				<li>
					<a href="javascript:$('#selecFcltGbnCombo').combobox('showPanel');"></a>
					<input id="selecFcltGbnCombo" class="easyui-combotree" style="width:80px; height:30px;"
							data-options="valueField: 'code',textField: 'name', onChange: getFacility,
										value:['시설물용도'],panelWidth: 150,labelPosition:'top',multiple:true">
				</li>
				<!-- <li>
					<a id="fcltGrp" href="#" onclick="javascript:$('#scltGrp').combobox('showPanel');"></a>
					<select id="scltGrp" class="easyui-combotree" style="width:90px; height:30px"
		             		data-options="idField:'code',textField:'name',labelPosition:'top',multiple:true,value:['시설물분류']"></select>
				</li> -->
			</ul>
			<ul>
				<li>
					<input type="checkbox" id="mapYn"/>
					<label for="mapYn">지도 내 검색</label>
				</li>
				<li>
					<a class="btn btnSelect" href="javascript:common.openDialogPop('mapPopup','시설물 조회','800','670',true,'/action/page.do',{path : '/fclt/popup/facilityListPopup'},'mapPopup')">시설물 자료</a>
				</li>
				<li style="float: right;">
					<a class="btn btnSelect" href="javascript:getFacility()">조회</a>
				</li>
		    </ul>
		</div>
		
		<div id="site" class="tab_content">
			<div id="siteTable">
			</div>
		</div>
		
		<!-- <div id="center" class="tab_content">
			<div id="centerTable">
			</div>
		</div> -->
	</div>
</div>
<script>

var grid;
var gridFlag = false;
function getFacility() {
	if(gridFlag) return;
	gridFlag = true;
	if(!$('#loading').is(':visible')) $('#loading').toggle();
	if(typeof(grid) != "undefined") grid.destroy();
	
	var spaceVal = [];
	if($('#selectSpaceCombo').combotree('getValues').length > 1) {
		spaceVal = $('#selectSpaceCombo').combotree('getValues');
		const rIndex = spaceVal.indexOf("행정구역");
		spaceVal.splice(rIndex,1);
	}
	
	var purposeSpace = [];
	if($('#selecFcltGbnCombo').combotree('getValues').length > 1) {
		purposeSpace = $('#selecFcltGbnCombo').combotree('getValues');
		const rIndex = purposeSpace.indexOf("시설물용도");
		purposeSpace.splice(rIndex,1);
	}

	const jsonObj = {};
	
	jsonObj.userId = '${admin.id}';
	jsonObj.pageKind = 'manage';
	jsonObj.menuNm = 'facility';
	jsonObj.state = $('#slctStat').combobox('getValue');
	jsonObj.fcltSpace = spaceVal;
	jsonObj.purposeSpace = purposeSpace;

	if($('#mapYn').prop('checked')) {
		var extent = mapManager.map.getView().calculateExtent(mapManager.map.getSize());

		var posSW = ol.proj.transform([extent[0], extent[1]], mapManager.properties.projection, 'EPSG:4326').toString().replace(',',' ');
		var posNW = ol.proj.transform([extent[0], extent[3]], mapManager.properties.projection, 'EPSG:4326').toString().replace(',',' ');
		var posSE = ol.proj.transform([extent[2], extent[1]], mapManager.properties.projection, 'EPSG:4326').toString().replace(',',' ');
		var posNE = ol.proj.transform([extent[2], extent[3]], mapManager.properties.projection, 'EPSG:4326').toString().replace(',',' ');
		
		var mapBound = posNW+','		//북서
					   +posNE+','	//북동
					   +posSE+','	//남동
					   +posSW+','	//남서
					   +posNW;

		jsonObj.mapBound = mapBound;
	}

	$.ajax({
		type       : "POST",
		url 		: "/select/facility.selectFcltSList/action.do",
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
		const flag = $("ul.tabs li.active").attr('rel');
		grid = new tui.Grid({
			el: document.getElementById(flag+'Table'),
			data: rows,
			scrollX: false,
			scrollY: true,
			bodyHeight: Number((innerHeight>988?988:innerHeight)-250),
			rowHeaders: ['rowNum'],
			rowHeight: 'auto',
			pageOptions: {
				useClient: true,
				perPage: 20
			},
			columns: [
				{
					header: '관리번호',
					name: 'mgmtNo',
					sortable: true
				},
				{
					header: '명칭',
					name: 'fcltNm',
					sortable: true,
					width: 95,
					filter: {
			            type: 'text',
			            operator: 'OR'
			          }
				},
				{
					header: '용도',
					name: 'fcltPuposeNm',
					sortable: true,
					//width: 80,
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
		
		$('#'+flag+'Table tbody').off('click');
		$('#'+flag+'Table tbody').on( 'mousedown', 'tr', function (e) {
			const currentPage = grid.getPagination().getCurrentPage() - 1;
			const itemsPerPage = grid.getPagination()._options.itemsPerPage;
			const rowIndex = this.rowIndex + (itemsPerPage * currentPage);
			var rows = grid.getRow(rowIndex);
			mapManager.setCenter(ol.proj.transform([rows.lon, rows.lat], 'EPSG:4326', mapManager.properties.projection));
			//videoManager.facilityPopup(rows);
			mapManager.map.getView().setZoom(13);
			mapManager.facilityOverlay = createFacilityPopupOverlay(rows);
			mapManager.map.addOverlay(mapManager.facilityOverlay);
		});
		
		gridFlag = false;
	});
}

function setSearchCombo() {
	$('#selectSpaceCombo').combotree({required: true});
	$('#selecFcltGbnCombo').combotree({required: true});
	
	const jsonObj = {};
	
	$.ajax({
		type      : "POST",
		url       : "/select/common.getAreaSiCode/action.do",
		dataType  : "json",
		data      : {"param": JSON.stringify(jsonObj)},
		async     : false
	}).done(function (result) {
		const rows = result.rows;
    	if(rows=='sessionOut'){
			alert('로그인 시간이 만료되었습니다.');
			closeWindow();
		}
		var areaData = [];
		var siTemp = {};
		$.each(rows, function (index) {
			siTemp.id = Number(rows[index].siCd);
			siTemp.text = rows[index].siNm;
			
			jsonObj.siCd = rows[index].siCd;
			$.ajax({
				type      : "POST",
				url       : "/select/common.getAreaDongCode/action.do",
				dataType  : "json",
				data      : {"param": JSON.stringify(jsonObj)},
				async     : false,
				beforeSend: function (xhr) {
					// 전송 전 Code
				},
			}).done(function (result2) {
				const rows2 = result2.rows;
		    	if(rows2=='sessionOut'){
					alert('로그인 시간이 만료되었습니다.');
					closeWindow();
				}
				var dongTemp = [];
				var dongTemp2 = [];
				$.each(rows2, function (index2) {
					dongTemp.id = Number(rows2[index2].dongCd);
					dongTemp.text = rows2[index2].dongNm;
					
					dongTemp2.push({id: dongTemp.id, text: dongTemp.text, checked : true});
				});
				siTemp.children = dongTemp2;
			});
			areaData.push({id: siTemp.id, text: siTemp.text, children: siTemp.children, checked : true});
		});
		$('#selectSpaceCombo').combotree('loadData',areaData);
 	}).fail(function (xhr) {
		console.log(JSON.stringify(xhr));
	}).always(function() {
		
	});
	
	jsonObj.userId = '${admin.id}';
	jsonObj.chkDeGrpCd = 'FCLT_PURPOSE';
	
	$.ajax({
		type      : "POST",
		url       : "/select/oprt.getCodeDetailList/action.do",
		dataType  : "json",
		data      : {"param": JSON.stringify(jsonObj)},
		async     : false,
		beforeSend: function (xhr) {
			// 전송 전 Code
		},
	}).done(function (result) {
		const rows = result.rows;
    	if(rows=='sessionOut'){
			alert('로그인 시간이 만료되었습니다.');
			closeWindow();
		}
		var fcltPurposeData = [];
		var purpose = {};
		$.each(rows, function (index) {
			purpose.id = Number(rows[index].deCd);
			purpose.text = rows[index].deCdNm;
			fcltPurposeData.push({id: purpose.id, text: purpose.text, checked : true});
		});
		$('#selecFcltGbnCombo').combotree('loadData',fcltPurposeData);
 	}).fail(function (xhr) {
		console.log(JSON.stringify(xhr));
	}).always(function() {
		
	});
}

</script>