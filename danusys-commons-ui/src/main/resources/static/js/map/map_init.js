
//EPSG:5181 좌표계 설정
proj4.defs("EPSG:5181","+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=500000 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs");
ol.proj.proj4.register(proj4);


let epsg5181 = new ol.proj.Projection({
	code : "EPSG:5181",
	extent : [-30000, -60000, 494288, 988576],
	units : 'm'
});

/**
 * 맵생성, 레이어생성 등등 기본적인 맵 셋팅을 해주는 class 이다.
 *
 * @class mapManager
 * @property {object} geocoder - 카카오 geocoder 담아주는 property
 * @property {object} ps - new window.kakao.maps.services.Places();
 * @property {object} map - map 담아주는 property
 * @property {object} overviewMap - 미니맵 정보 담아주는 property
 * @property {object} projection - 맵의 기본 projection 값 설정
 * @property {object} draw - draw layer 정보 설정
 * @property {object} sourceClear source 데이터 clear
 * @property {object} circleDraw circle draw 레이어 정보 설정
 * @property {object} drawInteraction - draw interaction 값을 저장해서 삭제함
 */
var mapManager = {
	geocoder : undefined,
	ps : undefined,
	map : null,
	overviewMap : null,
	drawInteraction : undefined,
	sourceClear : [],
	circleDraw : null,
	tiles : 'base',
	drawElement : {
		tooltip : [],
		sketch : '',
		measureTooltipElement : '',
		measureTooltip : ''
	},
	properties : {
		id : null,
		type : null,
		/*lat : 37.3616199494757,
		lon : 126.93514995791503,*/
		lat : 37.44457599567139,
		lon : 126.89482519279865,
			center : null,
		projection : null,
		resolutions : [
			[2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25], // daum,
			[], // naver,
			undefined, // vworld
		],
		extents : [
			[-30000, -60000, 494288, 988576], // daum
			[], // naver
			undefined // vworld
		],
		defaultZoom : [
			8, // daum
			undefined, // naver
			16, // vworld
		],
		maxZoom : [
			13, // daum
			undefined, //naver
			19, //vworld
		],
		minZoom : [
			0, // daum
			undefined, // naver
			7, // vworld
		],
		zoomFactor : [
			1, // daum
			undefined, // naver
			undefined// vworld
		],
		pro4j : [
			'EPSG:5181', // daum
			'EPSG:5179', // naver
			'EPSG:3857', // vworld
			'EPSG:4326' //위경도
		],
		urls : [
			[
				{
					name : 'base',
					prefix : '//map.daumcdn.net/map_2d_hd/1909dms/L',
					surffix : '.png'
				},
				{
					name : 'satellite',
					prefix : '//map.daumcdn.net/map_skyview/L',
					surffix : '.jpg'
				},
				{
					name : 'hybrid',
					prefix : '//map.daumcdn.net/map_hybrid/1909dms/L',
					surffix : '.png'
				},
				{
					name : 'roadView',
					prefix : '//map.daumcdn.net/map_roadviewline/7.00/L',
					surffix : '.png'
				},
				{
					name : 'traffic',
					prefix : '//r2.maps.daum-img.net/mapserver/file/realtimeroad/L',
					surffix : '.png'
				},
				{
					name : 'airPm10',
					prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_pm10/T/L',
					surffix : '.png'
				},
				{
					name : 'airKhai',
					prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_khai/T/L',
					surffix : '.png'
				},
				{
					name : 'airPm25',
					prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_pm25/T/L',
					surffix : '.png'
				},
				{
					name : 'airYsnd',
					prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_ysnd/T/L',
					surffix : '.png'
				},
				{
					name : 'airO3',
					prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_o3/T/L',
					surffix : '.png'
				},
				{
					name : 'airNo2',
					prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_no2/T/L',
					surffix : '.png'
				},
				{
					name : 'airCo',
					prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_co/T/L',
					surffix : '.png'
				},
				{
					name : 'airSo2',
					prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_so2/T/L',
					surffix : '.png'
				}
			],
			[
				{
					name : 'base',
					prefix : '//map.daumcdn.net/map_2d_hd/1909dms/L',
					surffix : '.png'
				},
				{
					name : 'satellite',
					prefix : '//map.daumcdn.net/map_skyview/L',
					surffix : '.jpg'
				},
				{
					name : 'hybrid',
					prefix : '//map.daumcdn.net/map_hybrid/1909dms/L',
					surffix : '.png'
				},
				{
					name : 'roadView',
					prefix : '//map.daumcdn.net/map_roadviewline/7.00/L',
					surffix : '.png'
				},
				{
					name : 'traffic',
					prefix : '//r2.maps.daum-img.net/mapserver/file/realtimeroad/L',
					surffix : '.png'
				},
				{
					name : 'airPm10',
					prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_pm10/T/L',
					surffix : '.png'
				},
				{
					name : 'airKhai',
					prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_khai/T/L',
					surffix : '.png'
				},
				{
					name : 'airPm25',
					prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_pm25/T/L',
					surffix : '.png'
				},
				{
					name : 'airYsnd',
					prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_ysnd/T/L',
					surffix : '.png'
				},
				{
					name : 'airO3',
					prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_o3/T/L',
					surffix : '.png'
				},
				{
					name : 'airNo2',
					prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_no2/T/L',
					surffix : '.png'
				},
				{
					name : 'airCo',
					prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_co/T/L',
					surffix : '.png'
				},
				{
					name : 'airSo2',
					prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_so2/T/L',
					surffix : '.png'
				}
			],
			[
				{
					name : 'base',
					prefix : '//api.vworld.kr/req/wmts/1.0.0/CEB52025-E065-364C-9DBA-44880E3B02B8/Base/',
					// prefix : '//xdworld.vworld.kr:8080/2d/Base/202002/',
					surffix : '.png'
				},
				{
					name : 'satellite',
					prefix : '//api.vworld.kr/req/wmts/1.0.0/CEB52025-E065-364C-9DBA-44880E3B02B8/Satellite/',
					// prefix : '//xdworld.vworld.kr:8080/2d/Satellite/202002/',
					surffix : '.png'
				},
				{
					name : 'hybrid',
					prefix : '//api.vworld.kr/req/wmts/1.0.0/CEB52025-E065-364C-9DBA-44880E3B02B8/Hybrid/',
					// prefix : '//xdworld.vworld.kr:8080/2d/Hybrid/202002/',
					surffix : '.png'
				}
			],
		]
	},

	/**
	 *  map 생성 기능
	 * @param {string} id - 맵생성 아이디
	 * @param {string} minimapId - 미니맵생성 아이디
	 * @param {string} type - (0 : daum, 1 : naver, 2 : openlayer)
	 * @function
	 */
	init : function(id, minimapId, type) {
		this.initKakao();
		this.properties.id = id;
		this.properties.type = type;
		this.createTileGrid();
		this.createProjection(type);
		this.createMap();
		// this.createOverviewMap(minimapId);
		this.createMapMoveEvent();
		// this.mapCenterAddress();
		const tileOptions = this.properties.urls[type];
		for (var i = 0, max = tileOptions.length; i < max; i++) {
			this.createTileLayer(tileOptions[i].name, tileOptions[i].prefix, tileOptions[i].surffix);
		}

		this.switchTileMap('btnRoadmap');
	},
	createTileGrid : function() {
		let tileGrid = this.properties.extents[this.properties.type] == undefined ? undefined :
			new ol.tilegrid.TileGrid({
				origin: [this.properties.extents[this.properties.type][0], this.properties.extents[this.properties.type][1]],
				resolutions: this.properties.resolutions[this.properties.type]
			});

		this.properties.tileGrid = tileGrid;
	},
	/**
	 * 맵 상단 주소검색 기능 위해 카카오 api 사용
	 * @function
	 */
	initKakao : function() {
		// 맵 상단 주소 표시 위해 카카모 api 사용
		try {
			mapManager.geocoder = new window.kakao.maps.services.Geocoder();
			mapManager.ps = new window.kakao.maps.services.Places();
		} catch(e) {
			console.log('주소정보 사용 불가');
		}
	},

	/**
	 * map projection 값 셋팅
	 * @param {string} type - (0 : daum, 1 : naver, 2 : openlayer)
	 * @function
	 */
	createProjection : function(type) {
		this.properties.projection = this.properties.pro4j[type]
		//this.properties.projection = 'EPSG:4326'
	},

	/**
	 * 타일생성
	 * @param {string} type - (0 : daum, 1 : naver, 2 : openlayer)
	 * @param {string} prefix 타일 주소
	 * @param {string} surffix 타일 주소
	 * @function
	 */
	createTileLayer : function(type, prefix, surffix) {
		try {
			var tileLayer = new ol.layer.Tile({
				title: type,
				name: type,
				visible: true,
				type: type,
				source: new ol.source.XYZ({

					/*url: `${prefix}{z}/{y}/{x}${surffix}`,*/
					tileSize: 256,
					tileGrid : this.properties.tileGrid,
					projection : ol.proj.get(this.properties.pro4j[this.properties.type]),
					maxZoom : this.properties.maxZoom[this.properties.type],
					minZoom : this.properties.minZoom[this.properties.type],
					// url: 'http://api.vworld.kr/req/wmts/1.0.0/CEB52025-E065-364C-9DBA-44880E3B02B8/Base/{z}/{y}/{x}.png'
					// projection: this.properties.projection,
					// tileSize: 512,
					// minZoom: 0,
					// tileGrid: new ol.tilegrid.TileGrid({
					//    origin: [this.properties.extents[this.properties.type][0], this.properties.extents[this.properties.type][1]],
					//    resolutions: this.properties.resolutions[this.properties.type]
					// }),
					tileUrlFunction: function (tileCoord, pixelRatio, projection) {
						let zType = mapManager.properties.type;
						let resolution = mapManager.properties.resolutions[zType];
						if (tileCoord == null) return undefined;
						var s = Math.floor(Math.random() * 4);
						var z = resolution ? resolution.length - tileCoord[0] : tileCoord[0];
						var x = tileCoord[1];
						var y = zType == 0 ? -(tileCoord[2]) : tileCoord[2];

						return prefix + z + '/' + y + '/' + x + surffix;
					},
				})
			});

			this.map.addLayer(tileLayer);
		} catch(e) {
			console.log('error');
		}
	},

	/**
	 * 맵생성
	 * @function
	 */
	createMap : function() {
		var map = new ol.Map({
			layers: [],
			target: this.properties.id,
			controls : [],
			logo: false,
			view : new ol.View({
				projection: ol.proj.get(this.properties.pro4j[this.properties.type]),
				extent: this.properties.extents[this.properties.type],
				resolutions: this.properties.resolutions[this.properties.type],
				center : new ol.proj.transform([this.properties.lon,this.properties.lat]
												, this.properties.pro4j[3]
												, this.properties.pro4j[this.properties.type]),
				zoom : this.properties.defaultZoom[this.properties.type],
				zoomFactor: this.properties.zoomFactor[this.properties.type],
				maxZoom : this.properties.maxZoom[this.properties.type],
				minZoom : this.properties.minZoom[this.properties.type],
			})
		});

		this.map = map;
	},

	/**
	 * 맵 우측클릭 메뉴 생성
	 * @function
	 */
	createContextMenu : function(menuObj){
		var contextmenu = new ContextMenu({
			width: 170,
			items: menuObj,
			defaultItems: false
		});
		this.map.addControl(contextmenu);
	},

	/**
	 * vector layer 생성
	 * @param {string} title - 레이어 타이틀 속성
	 * @param {object} style - 레이어 스타일
	 * @param {string} source - 레이어 소스
	 * @return {Object} layer - layer 리턴
	 * @function
	 */
	createVectorLayer: function(title,style,source){
		this.removeVectorLayer(title);

		var layer = new ol.layer.Vector({
			title: title
		});

		if(style != '' &&  style != null){
			layer.setStyle(style);
		}

		layer.setSource(source);
		this.map.addLayer(layer);

		return layer;
	},
	/**
	 * vector Layer 값 가져오는 기능
	 * @param {string} vector layer 의 title
	 * @function
	 */
	getVectorLayer: function(title) {
		const layers = this.map.getLayers().getArray();
		var tilesId = '';
		for (var i = 0, max = layers.length; i < max; i++) {
			const temp = layers[i].get('title');
			if(title == temp) return layers[i];
		}
	},
	removeVectorLayer: function(title) {
		const layer = this.getVectorLayer(title);
		mapManager.map.removeLayer(layer);
	},
	/**
	 * interaction 데이터 가져오기
	 * @param {string} title - interaction 에 설정된 type 이름
	 * @function
	 */
	getInteraction : function(title) {
		const interaction = mapManager.map.getInteractions().getArray();
		for(var i=0; i < interaction.length;i++){
			const temp = interaction[i].i
			if(title == temp) return interaction[i];
		}
	},

	/**
	 * 타일맵 변경 기능 기본,위성, 로드뷰 기능 사용
	 * @param {string} type - 기본,위성,로드뷰 구분 하기위한 param
	 * @function
	 */
	switchTileMap : function(type) {
		const layers = this.map.getLayers().getArray();
		var tilesId = '';
		if(type=='btnRoadmap'){
			tilesId = 'base';
			this.tiles = tilesId;
		} else if(type == 'btnSkyview'){
			tilesId = 'satellite,hybrid';
			this.tiles = tilesId;
		}
		for (var i = 0, max = layers.length; i < max; i++) {
			const title = layers[i].get('title');
			if($('#btnRoadview').hasClass('selected_btn') && title == 'roadView') {
				continue;
			}
			tilesId.indexOf(title) > -1 || layers[i] instanceof ol.layer.Vector ? layers[i].setVisible(true) : layers[i].setVisible(false);
		}
	},

	/**
	 * 로드뷰 기능
	 * @param {string} type
	 * @function
	 */
	btnRoadview : function(type){
		if(type.indexOf('active') != 0){
			mapManager.getVectorLayer('roadView').setVisible(false);
			mapManager.removeMapEventListener('click', roadViewClickEventListener);
		}else{
			tilesId = this.tiles + ',roadView';
			mapManager.setMapEventListener('click', roadViewClickEventListener);
			mapManager.getVectorLayer('roadView').setVisible(true);
		}
	},

	/**
	 * 사용하지 않음
	 */
	btnTraffic : function(type){
		if(type.indexOf('selected_btn') != -1){
			mapManager.getVectorLayer('traffic').setVisible(false);
		}else{
			tilesId = this.tiles + ',traffic';
			mapManager.getVectorLayer('traffic').setVisible(true);
		}
	},

	/**
	 * 사용하지 않음
	 */
	btnAir : function(type, airType){
		if(type.indexOf('selected_btn') != -1){
			mapManager.getVectorLayer('air_' + airType).setVisible(false);
		}else{
			tilesId = this.tiles + ',air_' + airType;
			mapManager.getVectorLayer('air_' + airType).setVisible(true);
		}
	},

	/**
	 * overvieMap 생성
	 * @function
	 */
	createOverviewMap : function(minimapId){
		this.overviewMap = new ol.control.OverviewMap({
			target : document.getElementById(minimapId),
			className : 'custom-overview-map',
			collapsible : false,
			view : new ol.View({
				zoom: 16,
				projection: ol.proj.get(this.properties.pro4j[this.properties.type]),
				extent: this.properties.extents[this.properties.type],
				resolutions: this.properties.resolutions[this.properties.type],
				rotation: 0,
				center : new ol.proj.transform([this.properties.lon,this.properties.lat], this.properties.projection, this.properties.pro4j[this.properties.type]),
			}),
		})
		this.map.addControl(this.overviewMap);
	},

	setMapEventListener : function(name, listener) {
		this.map.on(name, listener);
	},

	removeMapEventListener : function(name, listener) {
		this.map.un(name, listener);
	},

	setMapViewEventListener : function(name, listener) {
		this.map.getView().on(name, listener);
	},

	removeMapViewEventListener : function(name, listener) {
		this.map.getView().un(name, listener);
	},

	getOverlay : function(type) {
		return mapManager.map.getOverlayById(type);
	},

	setOverlay : function(option) {
		this.removeOverlayById(option.id);

		const overlay = new ol.Overlay(option);

		mapManager.map.addOverlay(overlay);

		return overlay;
	},
	removeOverlayById : function(id) {
		const overlay = mapManager.map.getOverlayById(id);
		if(overlay) mapManager.map.removeOverlay(overlay);
	},
	removeOverlay : function(overlay) {
		if(overlay) mapManager.map.removeOverlay(overlay);
	},
	removeOverlays : function() {
		mapManager.map.getOverlays().clear();
	},

	/**
	 * 주소 검색 기능
	 * @param {array} coords - 좌표
	 * @param {function} callback - 팝업생성 콜백함수
	 */
	searchDetailAddrFromCoords : function(coords, callback){
		try {
			transCoord =  new ol.proj.transform([coords[0], coords[1]], mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection);
			mapManager.geocoder.coord2Address(transCoord[0], transCoord[1], callback);
		} catch(e) {
			console.log('주소정보 사용 불가');
		}
	},

	/**
	 * 지도 센터 정보 가져온다
	 * @function
	 */
	displayCenterInfo : function(result, status){
		if (status === kakao.maps.services.Status.OK) {
			$('.map-address').html('');
			// 행정동의 region_type 값은 'H' 이므로
			for(var i = 0; i < result.length; i++) {
				$('.map-address').append(result[i].address.region_1depth_name + ">"+result[i].address.region_2depth_name+ ">" +result[i].address.region_3depth_name + result[i].address.main_address_no);
				break;
			}
		}
	},

	/**
	 * 맵 이동 이벤트
	 * @function
	 */
	createMapMoveEvent : function(){
		// mapManager.setMapViewEventListener('propertychange', mapManager.mapCenterAddress);
	},

	/**
	 * map center 주소 가져오기 기능
	 * @function
	 */
	mapCenterAddress : function(){
		mapManager.searchDetailAddrFromCoords(mapManager.map.getView().getCenter(), mapManager.displayCenterInfo);
	},
	setCenter : function(coord) {
		const center = mapManager.map.getView().getCenter();
		const resolution = mapManager.map.getView().getResolution() * 1;
		const a = ol.proj.transform(center, mapManager.properties.projection, mapManager.properties.pro4j[mapManager.properties.type]);
		const b = ol.proj.transform(coord, mapManager.properties.projection, mapManager.properties.pro4j[mapManager.properties.type]);

		//const distance = mapManager.wgs84Sphere.haversineDistance(a, b);
		const duration = 500;

		const pan = ol.animation.pan({
			source : center,
			duration : duration
		});

		mapManager.map.beforeRender(pan);
		mapManager.map.getView().setCenter(coord);
	},

	/**
	 * map 확대,축소 이벤트
	 * @param {string} level - 지도 줌 레벨
	 * @function
	 */
	setZoom : function(level) {
		const view = mapManager.map.getView();
		const resolution = view.getResolution();

		const zoom = ol.animation.zoom({
			resolution: resolution,
			duration: 500
		});

		mapManager.map.beforeRender(zoom);
		//view.setResolution(resolution * factor);
		mapManager.map.getView().setZoom(level);
	}

};// mapManager

/**
 * 맵 기능 버튼 관리
 * @class mapBtnFunc
 */
var mapBtnFunc = {
	properties : {
	},

	/**
	 * 맵 zoom in 기능
	 */
	zoomIn : function() {
		var nowZoom = mapManager.map.getView().getZoom();
		var zoomLevel = nowZoom + 1;
		//mapManager.map.getView().setZoom(zoomLevel);
		mapManager.setZoom(zoomLevel);
	},

	/**
	 * 맵 zoom out 기능
	 */
	zoomOut : function() {
		var nowZoom = mapManager.map.getView().getZoom();
		if(nowZoom < 6) return;
		var zoomLevel = nowZoom - 1;
		//mapManager.map.getView().setZoom(zoomLevel);
		mapManager.setZoom(zoomLevel);
	},
}//mapBtnFunc

/**
 * 지도 draw 지우는 기능
 * @function map.clearDraw
 */
function clearDraw() {
	mapManager.map.removeInteraction(mapManager.drawInteraction);
	$('.draw.active').removeClass("active");
	var mapClear = mapManager.sourceClear.length;
	for(var i=0;i<mapClear;i++){
		mapManager.sourceClear[i].clear();
	}
	$('.ol-overlay-container .ol-tooltip.ol-tooltip-static').parent().remove();
	var removeOverlayAll = mapManager.map.getOverlays().getArray().length;
	for(var i=0;i<removeOverlayAll;i++){
		mapManager.map.removeOverlay(mapManager.drawElement.tooltip[i]);
	}
	mapManager.drawElement.tooltip = [];
}

/**
 * @description 개소감시
 * @param {object} data - 카메라 데이터
 * @function map.siteMntr
 */
function siteMntr(data){
	const jsonObj = {};

	let purposeSpace = getLayerPurpose();

	jsonObj.sameLat = data.lat;
	jsonObj.sameLon = data.lon;
	jsonObj.purposeSpace = purposeSpace;

	$.ajax({
		contentType : "application/json; charset=utf-8",
		type      : "POST",
		url         : "/select/facility.selectSiteCctvList/action",
		dataType   : "json",
		data      : JSON.stringify(jsonObj),
		async      : true
	}).done(function(result){
		const datas = result.rows;
		if(datas=='sessionOut'){
			alert('로그인 시간이 만료되었습니다.');
			closeWindow();
		}

		dialogManager.closeAll();

		for(var i = 0, max = datas.length; i < max; i++) {

			const dialogOption = {
				draggable: true,
				clickable: true,
				data: datas[i],
				css: {
					width: '400px',
					height: '340px'
				}
			}

			const dialog = $.connectDialog(dialogOption);

			const videoOption = {};
			videoOption.data = datas[i];
			videoOption.parent = dialog;
			videoOption.btnFlag = true;
			videoOption.isSite = true;

			if(!videoManager.createPlayer(videoOption)) {
				dialogManager.close(dialog);
			}
		}

		dialogManager.sortDialog();
	})
}
mapManager.init('map', 'minimap', 2);
//mapManager.init('map', 'minimap', 0);