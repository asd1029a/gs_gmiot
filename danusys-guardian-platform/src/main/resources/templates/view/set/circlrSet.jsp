<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div id="grpList" style="display: contents">
	<div class="aside-srch-list">
		<dl class="srch-list-tit">
			<dt>
				<span class="title">목록</span>
				<!-- <span class="count" id="grpListCount"></span> -->
				<span>
					<a href="#" class="btn-insert" onclick="openCirclrGrp();">추가</a>
					<a href="#" class="btn-modify" onclick="openCirclrGrp('edit');">수정</a>
				</span>
			</dt>
		</dl>
		<article class="srch-list" id="circlrGrpList"></article>
		<ul id="circlrGrpListPagination"></ul>
	</div>
	<div class="aside-srch-list" style="height: calc(100% - 155px);">
		<article class="srch-list" id="circlrDtlList" style="height: 100%;"><p>상단 목록 클릭 시 나타납니다.</p></article>
	</div>
</div>

<div id="cctvList" style="position: absolute;top: 75px;background: #2b2d30;display: none;height: calc(100% - 75px);">
	<div class="aside-srch-form">
		<ul>
			<li>
				<div id="spaceSelect" style="width:calc(25% - 21px);margin-right:46px;float:left;"></div>
				<div id="useSelect" style="width:29%;margin-right:10px;float:left;"></div>
				<select id="agSelect" style="width:23%;">
					<option value="1">회전형</option>
					<option value="0">고정형</option>
				</select>
			</li>
			<li>
				<input type="checkbox" id="mapFlag"><label for="mapFlag"></label>지도 내 검색
			</li>
			<li class="search-btn" onClick="javascript:selectCirclrCctvList()">조회</li>
		</ul>
	</div>
	<div class="aside-srch-list" style="">
		<dl class="srch-list-tit">
			<dt>
				<span class="title">목록</span>
				<span class="count" id="cctvListCount"></span>
			</dt>
		</dl>
		<article class="srch-list" id="circlrCctvList"></article>
		<ul id="circlrCctvListPagination"></ul>
	</div>
	
	<div class="set-circlr-panel-right" id="setCirclr">
		<div class="cctv-layer-view">
			<div class="view"></div>
		</div>
		<div class="data-area">
			<ul>
				<li>
					<em>감시지역 : </em>
					<input id="circlrNm" type="text" placeholder="감시지역 입력"/>
					<input id="circlrNo" type="hidden"/>
				</li>
				<li>
					<em>담당자 : </em>
					<input id="circlrMngr" type="text" placeholder="담당자 입력"/>
				</li>
				<li>
					<em>비고 : </em>
					<input id="circlrInfo" type="text" placeholder="비고 입력"/>
				</li>
			</ul>
			<table id="circlrSetTable">
				<tbody>
					<tr>
						<td>1</td><td>2</td><td>3</td>
					</tr>
					<tr>
						<td>4</td><td>5</td><td>6</td>
					</tr>
					<tr>
						<td>7</td><td>8</td><td>9</td>
					</tr>
				</tbody>
			</table>
			<a href="#" class="btn-insert" onclick="saveCirclrGrp();">저장</a>
			<a href="#" class="btn-delete" onclick="deleteCirclrGrp();">삭제</a>
			<a href="#" class="btn-cancle" onclick="closeCirclrCctv();">닫기</a>
		</div>
	</div>
</div>

<script type="text/javascript" src="/js/set/circlrSet.js"></script>
<script>
$(document).ready(function() {
	areaCode = [];
	useCode = [];
	
	$('.aside-tab-sub-btn ul li').click(function(){
		$(".aside-tab-sub-btn ul li").removeClass("active");
		$(this).addClass("active");
		
		selectCirclrGrpList();
	});
	
	comboArea('spaceSelect');
	comboFcltUse('useSelect','${admin.id}','FCLT_PURPOSE');
	
	//setBodyClass('circlr_container');
	selectCirclrGrpList();
	
	mapManager.setMapEventListener('pointermove', circlrSetMapPointermoveListener);

	$('#circlrSetTable td').each(function(index) {
		$(this).on('click', function() {
			const isSelected = circlrDatas.filter(data => {
				if(data) videoManager.playList.keys().next().value === data.fcltId;
			}).length !== 0;
			
			if(isSelected) {
				alert('선택 되어 있는 카메라입니다. 다른 카메라를 선택하십시오.');
				return;
			}
			
			typeof circlrDatas[index] == 'undefined' ? 
			setCirclrData(this, videoManager.getVideoData(videoManager.playList.keys().next().value), index) : 
			videoManager.playList.size != 0 && videoManager.playList.has(circlrDatas[index].fcltId) ? 
			playCirclrTableData(this, videoManager.getVideoData(videoManager.playList.keys().next().value)) : 
			videoManager.playList.size == 0 ? 
			playCirclrTableData(this, circlrDatas[index]) : 
			$('#circlrSetTable td.active').length == 1 && !videoManager.playList.has(circlrDatas[index].fcltId) ? 
			playCirclrTableData(this, circlrDatas[index]) : 
			$('#circlrSetTable td.active').length == 0 && !videoManager.playList.has(circlrDatas[index].fcltId) ? 
			(function(obj, data) {
				if (confirm('카메라를 변경하시겠습니까?') == true) {
					setCirclrData(obj, data, index);
				} else {
					return;
				}
			})(this, videoManager.getVideoData(videoManager.playList.keys().next().value)) : 
			playCirclrTableData(this, circlrDatas[index]);
		});
	});
});

</script>