<!-- 시설물현황조회 -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" type="text/css" href="/css/import/easyui_datagrid.css">
<div class="fclt_popup_area" style="height:100%; background-color: #2b2d30; color: #fff; z-index : 11">
	<div class=fclt_cnct_line_list>
		<dl class="add-fclt-line-tit">
			<dt>
				<span class="title">시설물 노드 선택</span>
			</dt>
		</dl>
		<ul class="add-fclt-line-form">
			<li>
				<dl>
					<dt>라인 이름</dt>
					<dd>
						<input type="text" id="fcltLineNm" style="text-align:center;">
					</dd>
				</dl>
			</li>
			<li>
				<dl>
					<dt>선 색</dt>
					<dd style="width:73.5%">
						<input type="text" id="fcltLineColor" style="width: calc(80% + -2px);text-align: center;" >
					</dd>
				</dl>
			</li>
			</ul>
	</div>
	<div class="btn_wrap btn_area_center">
		<a style="display:none;" class="btn btnSave" id="fcltListExcelBtn" onclick="javascript:fcltCnctLineIdChk();">저장</a>
		<a style="display:none;" class="btn btnUpt" id="fcltListExcelBtn" onclick="javascript:updateLayerCode();">저장</a>
		<a style="display:none;" class="btn btnDelete" id="fcltListExcelBtn" onclick="javascript:deleteLayerCode();">삭제</a>
		<a class="btn btnCancle" onclick="javascript:closePopup()">닫기</a>
	</div>
</div>

<script>
$(document).ready(function(){
	$('.context-fclt').hide();
	$('#fcltLineColor').minicolors({
		keyword : '22'
		
		  /* // animation speed
		  animationSpeed: 50,

		  // easing function
		  animationEasing: 'swing',

		  // defers the change event from firing while the user makes a selection
		  changeDelay: 0,

		  // hue, brightness, saturation, or wheel
		  control: 'hue',

		  // default color
		  defaultValue: '',

		  // hex or rgb
		  format: 'hex',

		  // show/hide speed
		  showSpeed: 100,
		  hideSpeed: 100,

		  // is inline mode?
		  inline: false,

		  // a comma-separated list of keywords that the control should accept (e.g. inherit, transparent, initial). 
		  keywords: '',

		  // uppercase or lowercase
		  letterCase: 'lowercase',

		  // enables opacity slider
		  opacity: false,

		  // custom position
		  position: 'bottom left',
		  
		  // additional theme class
		  theme: 'default',
		  // an array of colors that will show up under the main color <a href="https://www.jqueryscript.net/tags.php?/grid/">grid</a>
		  swatches: [],
		 // Fires when the value of the color picker changes
		  change:null,
		  // Fires when the color picker is hidden.
		  hide:null,
		  // Fires when the color picker is shown.
		  show:null */
	});
	var lineKind = $('#fcltCnctLinePopup').data('param');
	if(String(lineKind) == 'undefined') {
		$('.btnSave').css('display','inline-block');
	} else {
		var color = getLineColor(lineKind);
		$('#fcltLineNm').val(lineKind);
		$('#fcltLineColor').minicolors('value',color);
		$('#fcltLineNm').attr('disabled','disabled')
		$('.btnUpt').css('display','inline-block');
		$('.btnDelete').css('display','inline-block');
	}
});

function closePopup() {
	$('#fcltCnctLinePopup').dialog('close');
	$('#fcltLineNm').val("");
	$('#fcltLineColor').val("");
}

function fcltCnctLineIdChk() {
	const jsonObj= {};
	var layerName = $('#fcltLineNm').val();
	var layerColor = $('#fcltLineColor').val();
	
	if(!checkNull(layerName,'레이어 이름을 입력해주세요')) return false;
	if(!checkNull(layerColor,'레이어 색상을 입력해주세요')) return false;
	jsonObj.layerName = layerName;
	$.ajax({
		type		:"POST",
		url			:"/select/facility.chkLayerNm/action.do",
		dataType    : "json",
		data        : {"param" : JSON.stringify(jsonObj)},
		async       : false,
	}).done(function (result) {
		var row = result.rows[0]
		if(row != 0) {
			alert('중복된 이름입니다.');
			return;
		}else {
			jsonObj.layerColor = layerColor;
			insertLayerCode(jsonObj);
		}
	})
}


function updateLayerCode(lineKind) {
	const type = 'U'
	const jsonObj = {};
	var fcltLineNm = $('#fcltLineNm').val();
	var fcltLineColor = $('#fcltLineColor').val();
	jsonObj.layerName = fcltLineNm;
	jsonObj.layerColor = fcltLineColor;
	insertLayerCode(jsonObj , type);
}

/**
 * layer code 추가하는 함수
 */
function insertLayerCode(jsonObj,type) {
	$.ajax({
		type		:"POST",
		url			:"/ajax/insert/facility.insertLayerCode/action.do",
		dataType	:"json",
		data		: {"param" : JSON.stringify(jsonObj)},
		async		: false,
		success:function(data) {
			alert('저장이 완료 되었습니다.');
			layerCodeSelectBox();
			closePopup();
			if(type != '' && type != 'undefined') {
				updateNode(jsonObj);
			}
		},error:function(e) {
			alert('작성 실패');
		}
	}) 
}

function deleteLayerCode() {
	var fcltLineNm = $('#fcltLineNm').val();
	var fcltLineColor = $('#fcltLineColor').val();
	if(confirm('삭제하시겠습니까?')) {
		const jsonArray1 = [];
    	const jsonArray2 = [];
    	const jsonArray3 = [];
    	const jsonArray4 = [];
		const jsonObj = {};
		jsonObj.rowStatus = "D";
		jsonObj.layerTitle = fcltLineNm;
		jsonArray1[0] = jsonObj;
        jsonArray2[0] = jsonObj;
        
        $.ajax({
    		type		:"POST",
    		url        : "/multiTransaction/facility.deleteLayerCode/facility.deleteFacilityNode/{sqlid3}/{sqlid4}/action.do",
    		dataType	:"json",
    		data		: {
    			"param1" : JSON.stringify(jsonArray1),
                "param2" : JSON.stringify(jsonArray2),
                "param3" : JSON.stringify(jsonArray3),
                "param4" : JSON.stringify(jsonArray4)
    		},
    		async		: false,
    		success:function(data) {
    			alert('삭제가 완료 되었습니다.');
    			mapManager.removeVectorLayer(fcltLineNm);
    			deleteNode(fcltLineNm);
    			layerCodeSelectBox();
    			closePopup();
    		},error:function(e) {
    			alert('삭제 실패');
    		}
    	})
	}
}

//현재 사용안함 노드 추가함수
function createNode(data) {
	if(type = "C") {
		const nodeData = {};
		nodeData.id = data.layerName;
		nodeData.text = data.layerName;
		nodeData.color = data.layerColor;
		$('#lineLayerHidden').jstree().create_node('#', nodeData, 'last')	
	} 
}
function deleteNode(id) {
	$("#lineLayerHidden").jstree().delete_node(id);
}

function updateNode(data) {
	$("#lineLayerHidden").jstree(true).get_node(data.layerName).original.color = data.layerColor;
	$("#lineLayerHidden").jstree(true).uncheck_node(data.layerName);
	$("#lineLayerHidden").jstree(true).check_node(data.layerName);
}

</script>
