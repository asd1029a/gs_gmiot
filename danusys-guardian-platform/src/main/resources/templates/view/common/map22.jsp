<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link href="/css/map/ol.css" rel="stylesheet">

<link rel="stylesheet" type="text/css" href="/css/video/connectDialog.css">
<link rel="stylesheet" type="text/css" href="/css/video/video.css">
<link rel="stylesheet" type="text/css" href="/css/simplePagination/simplePagination.css">
<div id="map">
	<div id="searchAddrWrap" class="search_addr_wrap">
		<div id="searchRadio" class="search_section">
			<input type="radio" name="search" id="addrRadio" class="search_radio" checked="checked" >
			<label for="addrRadio">주소 검색</label>
			<input type="radio" name="search" id="facilityRadio" class="searchRadio">
			<label for="facilityRadio">시설물 검색</label>
		</div>
		<div id="searchText" class="search_text">
			<input type="text" id=searchKeyword placeholder="주소/장소" onkeydown="if(event.keyCode==13){searchPlaces()}"/>
			<button type="button" id="searchAddrBtn" class="search_addr_btn" onclick="searchPlaces()"></button>
		</div>
		<!-- <div id="searchButton" class="search_section">
			<button type="button" id="searchAddrBtn" class="search_addr_btn" onclick="searchPlaces()"></button>
		</div> -->
		<div id="searchList" style="display:none;">
			<div id="addressList"></div>
			<div id="cctvList" style="display:none;">
				<div id="cctvTitle" class="title"><strong>CCTV</strong></div>
				<div class="table_wrap">
					<table id="cctvTable">
						<thead style="display: none;">
							<tr>
								<th width="13%">순번</th>
								<th width="30%">관리번호</th>
								<th width="40%">cctv명</th>
								<th width="17%">유형</th>
								<th style="display:none;"></th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
				<div id="cctvPagination" class="pagination"></div>
			</div>
			<div class="btn_area">
				<button type="button" id="clearBtn" class="clear_btn"></button>
			</div>
		</div>
	</div>
	<div id="mapCenterAddrWrap">
		<div id="mapCenterAddr" class="map_center_addr">
			<span id="province" class="area"></span>
			<img alt="" src="/images/icons/address_right_btn.png">
			<span id="county" class="area"></span>
			<img alt="" src="/images/icons/address_right_btn.png">
			<span id="town" class="area"></span>
		</div>
	</div>
	<div id="mapTypeControlHolder" class="map_type_control_holder">
		<ul>
			<li>
				<a href="#" id="btnRoadmap" onclick="mapManager.switchTileMap(this.id)">
					<img alt="" src="/images/icons/mapBtn/btn_map_base.png">
					<span>일반지도</span>
				</a>
			</li>
			<li>
				<a href="#" id="btnSkyview" onclick="mapManager.switchTileMap(this.id)">
					<img alt="" src="/images/icons/mapBtn/btn_map_satellite.png">
					<span>위성지도</span>
				</a>
			</li>
		</ul>
		<!-- <div id="mapTypeControl" class="map_type_control">
			<a href="#" id="btnRoadmap" class="tile" onclick="mapManager.switchTileMap(this.id)">일반</a>
			<a href="#" id="btnSkyview" class="tile" onclick="mapManager.switchTileMap(this.id)">위성</a>
			<a href="#" id="btnRoadview" class="tile" onclick="mapManager.switchTileMap(this.id)">로드뷰</a>
		</div> -->
	</div>
	
	<div id="mapControlView" class="map_control_view">
		<span>Map</span>
		<div style="margin-bottom: 10px;">
			<a href="#" id="btnRoadview" class="map_tools" title="로드뷰" onclick="mapManager.btnRoadview(this.className)"></a>
		</div>
		<!-- <div style="margin-bottom: 10px;">
			<a href="#" id="btnRoadview" class="map_tools" title="로드뷰" onclick="mapManager.switchTileMap(this.id)"></a>
		</div> -->
		
		<div id="zoomControl" class="zoom_control">
			<a href="#" id="mapZoomIn" class="map_tools" title="확대"></a>
			<a href="#" id="mapZoomOut" class="map_tools" title="축소"></a>
		</div>
		
		<div id="tools" class="tools">
			<a href="#" id="mapDistance" class="map_tools" title="거리재기"></a>
			<a href="#" id="mapArea" class="map_tools" title="면적재기"></a>
			<a href="#" id="mapRadius" class="map_tools" title="반경재기"></a>
			<a href="#" id="mapClear" class="map_tools" title="지우기"></a>
		</div>
	</div>
	
	<div class="cctv_control_view">
		<span>Cctv</span>
		<div id="videoControlBox">
			<a href="#" id="btnVideoSort" class="map_tools" title="영상 정렬" onclick="dialogManager.sortDialog()"></a>
			<a href="#" id="btnVideoTransparent" class="map_tools" title="영상 투명화"></a>
			<!-- <ul>
				<li>
					<a href="#" id="btnVideoSort" class="map_tools" title="영상 정렬" onclick="dialogManager.sortDialog()"></a>
				</li>
				<li>
					<a href="#" id="btnVideoTransparent" class="map_tools" title="영상 투명화"></a>
				</li>
			</ul> -->
		</div>
		<div id="layerControlBox">
			<a href="#" id="btnLayer" class="map_tools" title="레이어"></a>
			<select id="btnLayerHidden" class="easyui-combotree"></select>
			<!-- <ul>
				<li>
					<a href="#" id="btnLayer" class="map_tools" title="레이어"></a>
					<select id="btnLayerHidden" class="easyui-combotree"></select>
				</li>
			</ul> -->
		</div>
	</div>
</div>
<div id="measure-popup" class="my-ol-popup">
    <a href="#" id="measure-popup-closer" class="my-ol-popup-closer"></a>
    <div id="measure-popup-content"></div>
</div>

<div id="mapPopup" style="overflow:hidden;display:none;"></div>


<script>
$(document).ready(function(){
	videoManager.mediaAuthority = '${admin.mediaAuthority}';
	const jsonObj = {};
	jsonObj.pageKind = "cast";
	jsonObj.userId = '${admin.id}';
	jsonObj.menuNm = "facility";
	
	$('#mapZoomIn').click(function(){
		toolsChangeSelectedBtn($(this).context.id);
		mapBtnFunc.zoomIn();
	});
	$('#mapZoomOut').click(function(){
		toolsChangeSelectedBtn($(this).context.id);
		mapBtnFunc.zoomOut();
	});
	$('#mapDistance').click(function(){
		toolsChangeSelectedBtn($(this).context.id);
		addInteraction('LineString');
	});
	$('#mapArea').click(function(){
		toolsChangeSelectedBtn($(this).context.id);
		addInteraction('Polygon');
	});
	$('#mapRadius').click(function(){
		toolsChangeSelectedBtn($(this).context.id);
		measureCircle('Circle');
	});
	$('#mapClear').click(function(){
		toolsChangeSelectedBtn($(this).context.id);
		clearDraw();
	});
	
	$('#clearBtn').click(function(){
		$('#searchKeyword').val('');
		$('#clearBtn').css('display','none');
		$('#cctvPagination').html('');
		$('#searchList').css('display','none');
		if($("input:radio[id=addrRadio]").is(':checked') == true){
			$('#addressList').empty();
		}else {
			$('#cctvTable tbody').html('');
			$('#cctvList').css('display','none');
			$('#cctvTable thead').css('display','none');
		}
	})
	
	$("input:radio[name=search]").click(function(){
		$('#searchKeyword').val(""); 
		$('#clearBtn').css('display','none');
		$('#addressList').empty();
		
		$('#cctvTable tbody').html('');
		$('#cctvList').css('display','none');
		$('#cctvTable thead').css('display','none');
		$('#cctvPagination').html('');
		if($(this).attr('id').indexOf('facility') != 0){
			$('#searchKeyword').attr('placeholder', '주소/장소');			
		} else {
			$('#searchKeyword').attr('placeholder','관리번호/CCTV명');
			
		}
	})
	
	$('#btnVideoTransparent').bind('click', function(e) {
		$(this).toggleClass('selected_btn');
		$('#map .dialog').toggleClass('transparent');
		/* if($(this).hasClass('selected_btn')) {
			$(this).removeClass('selected_btn');
			$('#map .dialog').removeClass('transparent');
		} else {
			$(this).addClass('selected_btn');
			$('#map .dialog').addClass('transparent');
		} */
	});
	
	$('#btnLayer').bind('click', function(e) {
		$('#btnLayer').addClass('selected_btn');
		$('#btnLayerHidden').combotree('showPanel');
	});
	
	$('#btnRoadview').click(function(){
		$('#btnRoadview').toggleClass('selected_btn');
	});
	
	videoManager.setClientInfo('', '${sessionId}');
	clientSessionId = '${sessionId}';
	
	setCctvDraw({}, '');
	getLayerPurpose();
});

</script>

<script type="text/javascript" src="/js/moment.min.js"></script>
<script type="text/javascript" src="//dapi.kakao.com/v2/maps-tunneling/sdk.js?appkey=8a0ebda8876bb68f663ceb77b22a5a51&libraries=services,clusterer,drawing"></script>
<script type="text/javascript" src="/js/openlayers3/proj4js.js"></script>
<script type="text/javascript" src="/js/openlayers3/ol3.js"></script>
<script type="text/javascript" src="/js/slideMenu.js"></script>
<script type="text/javascript" src="/js/map/map_marker.js"></script>
<script type="text/javascript" src="/js/openlayers3/ol-contextmenu.js"></script>
<script type="text/javascript" src="/js/map/map_init.js"></script>
<script type="text/javascript" src="/js/map/map_draw.js"></script>
<script type="text/javascript" src="/js/map/map_popup.js"></script>
<script type="text/javascript" src="/js/map/map.js"></script>
<script type="text/javascript" src="/js/openlayers3/map_ol3.js"></script>
<script type="text/javascript" src="/js/common.js"></script>
<script src="/js/video/video.util.js"></script>
<script src="/js/video/connectDialog.js"></script>
<!-- <script src="/js/video/webrtc.js"></script> -->

<script src="/js/constant/constant-real.js"></script>

<script src="/js/kurento/midsvr.js"></script>
<script src="/js/kurento/video.js"></script>

<script src="/js/kurento/adapter.js"></script>
<script src="/js/kurento/kurento-client.js"></script>
<script src="/js/kurento/kurento-utils.js"></script>

<script src="/js/simplePagination/simplePagination.js"></script>

