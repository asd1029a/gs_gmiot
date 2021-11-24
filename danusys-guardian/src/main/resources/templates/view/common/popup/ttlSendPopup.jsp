<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="popup">
	<dl class="popup-tit">
		<dt>TTL 메시지 전송</dt>
		<dd onClick="closePopup();"><img src="../images/icons/icon_closed.png"></dd>
	</dl>
	<div class="ttl-cont">
		<ul class="ttl-cont-area">
			<li class="item">
				<dl>
					<dt>방송 타입 종류</dt>
					<dd>
						<select id="ttlType">
							<option value="2">2</option>
							<option value="3">3</option>
							<option value="4">4</option>
							<option value="5">5</option>
							<option value="6">6</option>
							<option value="7">7</option>
							<option value="8">8</option>
							<option value="9">9</option>
							<option value="10">10</option>
						</select>
					</dd>
				</dl>
			</li>
			<li class="item">
				<dl>
					<dt>장소 선택</dt>
					<dd>
						<span class="search-area">
							<em>검색조건</em>
							<input type="text" id="searchNm" style="width:185px;" placeholder=""/>
							<a href="#" class="select-btn" onclick="searchTtlTree()">검색</a>
						</span>
						<span id="ttlTree" class="tree"></span>
					</dd>
				</dl>
			</li>
		</ul>
	</div>
	<ul class="center-btn" style="margin: 0 0 10px 0;">
		<li onClick="closePopup();">취소</li>
		<li onClick="ttlSend();">전송</li>
		<li onClick="ttlFacilitySync();">장비 동기화</li>
	</ul>
</div>
<script>
$(function() {
	createTtlTree();
	
	$('#searchNm').bind({
		'keyup': function(e) {
			searchTtlTree();
		}
	});
});

function searchTtlTree(){
	const searchKeyword = $("#searchNm").val();
	$('#ttlTree').jstree('search',searchKeyword);
}


function createTtlTree() {
	const jsonObj = {};
	
	$.ajax(
		{
			type       : "POST",
			url        : "/select/common.pcTtlFcltList/action.do",
			dataType   : "json",
			data       : {"param" : JSON.stringify(jsonObj)},
			async      : false,
			beforeSend : function(xhr) {
				
			}
		}).done(function(result) {
			const rows = result.rows;
	    	if(rows=='sessionOut'){
				alert('로그인 시간이 만료되었습니다.');
				closeWindow();
			}
	    	
	        $('#ttlTree').jstree({
	            'core': {
	                'data': rows
	            },
				'checkbox' : {
					"keep_selected_style" : false
					//"three_state" : false,
					//"cascade" : "down+undetermined",
				},
				'search' : {
					'case_insensitive': true,
			        'show_only_matches' : true,
			        'ajax' : true
				},
				'plugins' : [ 'checkbox', 'search' ]
	        });
		}).fail(function(xhr) {

		}).always(function() {

		});
	
		let limit = 20;

		$('#ttlTree').on("changed.jstree", function (e, data) {
			if(data.selected.length >= limit) {
				alert('최대 20 개소만 선택 할 수 있습니다.');
				$('#ttlTree').jstree('deselect_node', data.node.id);
			}
		});

}

function ttlSend() {
	const idList = $('#ttlTree').jstree('get_selected');
	
	const list = idList.map((d) => {return $('#ttlTree').jstree('get_node', d).text});
	const type = $('#ttlType').val();
	
	const jsonObj = {};
	jsonObj.type = type;
	jsonObj.list = list;
	
	
	$.ajax({
		type       : "POST",
		url        : "/ttlBroad.do",
		dataType   : "json",
		data       : {"param" : JSON.stringify(jsonObj)},
		async      : true,
	}).done(function(e) {
		alert('TTL 메세지 전송이 완료되었습니다.');
		closePopup();
	}).fail(function(e) {
		alert('TTL 메세지 전송이 완료되었습니다.');
		closePopup();
	});
}

function ttlFacilitySync() {
	const jsonObj = {};
	
	$.ajax({
		type       : "POST",
		url        : "/ttlFacilityLink.do",
		dataType   : "json",
		data       : {"param" : JSON.stringify(jsonObj)},
		async      : true,
	}).done(function(e) {
		alert('TTL 장비 동기화가 완료되었습니다.');
		$('#ttlTree').jstree(true).refresh();
	}).fail(function(e) {
		alert('TTL 장비 동기화가 완료되었습니다.');
		$('#ttlTree').jstree(true).refresh();
	});
}

function closePopup() {
	$('#ttlSendPopup').dialog('close');
}
</script>