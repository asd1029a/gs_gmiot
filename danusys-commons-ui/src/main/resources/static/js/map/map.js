/**
 * 맵관련기능
 * @namespace map
 */

/**
 * 지도 circle 그리기 기능 
 * @param {string} type - draw 타입 스트링값 
 * @function map.measureCircle
 */
function measureCircle(type){
	var source;
	//var interaction = mapManager.getInteraction('Circle');
	var layer = mapManager.getVectorLayer(type);
	mapManager.map.removeInteraction(mapManager.drawInteraction);
	if(layer == '' || layer == undefined) {
		source = setDrawVectorSource();
		mapManager.createVectorLayer(type,null,source)
	} else {
		source = mapManager.getVectorLayer('Circle').getSource();
	}
	
	var draw = new ol.interaction.Draw({
		source: source,
		type: type,
	});
	
	mapManager.map.addInteraction(draw);
	mapManager.drawInteraction = draw;
	var listener;
	draw.on('drawstart', function(evt) {
		createMeasureTooltip();
		mapManager.drawElement.sketch = evt.feature;
		var tooltipCoord = evt.coordinate;
		listener = evt.feature.getGeometry().on('change', function(e) {
			var geom = e.target;
			var coordi = evt.feature.getGeometry().B;
			var output;
			if (geom instanceof ol.geom.Circle){
				tooltipCoord = geom.getCenter();
			}
			var length = evt.feature.getGeometry().getRadius();
			length = Math.round(length * 100) / 100;
			if (length > 1000) {
				output = (Math.round(length / 1000 * 100) / 100) + ' ' + 'km';
			} else {
				output = (Math.round(length * 100) / 100) + ' ' + 'm';
			}
			mapManager.drawElement.measureTooltipElement.innerHTML = output+' ';
			mapManager.drawElement.measureTooltip.setPosition(tooltipCoord);
		});
	}); //drawStart end
	draw.on('drawend', function (e) {
		var coordi = e.feature.getGeometry().B;
		var length = e.feature.getGeometry().getRadius();
		length = Math.round(length * 100) / 100;
		var output; 
		if (length > 1000) {
			output = (Math.round(length / 1000 * 100) / 100) + ' ' + 'km';
		} else {
			output = (Math.round(length * 100) / 100) + ' ' + 'm';
		}
		mapManager.drawElement.measureTooltipElement.className = 'ol-tooltip ol-tooltip-static';
		mapManager.drawElement.measureTooltip.setOffset([0, -7]);
		mapManager.drawElement.measureTooltipElement = null;
		ol.Observable.unByKey(listener);
		draw.setActive(false);
		$('.draw.active').removeClass("active");
	});
}

/**
 * draw 기능 길이 foramt 기능
 * @param {object} line - 크기
 * @function map.formatLenght 
 */
function formatLength(line) {
   var length = line.getLength();
   var output;
   if (length > 100) {
      output = (Math.round(length / 1000 * 100) / 100) + ' ' + 'km';
   } else {
      output = (Math.round(length * 100) / 100) + ' ' + 'm';
   }
   return output;
};

/**
 * 면적 그리기 계산식
 * @param {object} polygon -polygon 데이터
 * @function map.foramtArea
 */
//면적 그리기 계산식
function formatArea(polygon) {
   var area = polygon.getArea();
   var output;
   if (area > 10000) {
      output = (Math.round(area / 1000000 * 100) / 100) + ' ' + 'km<sup>2</sup>';
   } else {
      output = (Math.round(area * 100) / 100) + ' ' + 'm<sup>2</sup>';
   }
   return output;
};

/**
 * interaction 추가 기능
 * @param {string} type - draw 종류 스트링 값
 * @function map.addInteraction 
 */
function addInteraction(type , layer) {
	var source;
	var interaction = mapManager.drawInteraction;
	mapManager.map.removeInteraction(interaction);
	
	var drawLayer = false;
	mapManager.map.getLayers().forEach(function(layer){
		var layerTarget = layer.get('target');
		if(layerTarget=='LineString' || layerTarget == 'Polygon'){
			drawLayer = true;
		}
	});
	
	if(!drawLayer) {
		source = setDrawVectorSource();
		var layer = mapManager.createVectorLayer(layer,'', source);
		layer.set('target',type);
	} else {
		source = mapManager.getVectorLayer('draw').getSource();
	}
	
	var draw = new ol.interaction.Draw({
		source : source,
		type : type
	});
	
	mapManager.drawInteraction = draw;
	mapManager.map.addInteraction(draw);
	
	var listener;
	draw.on('drawstart',function(evt) {
		createMeasureTooltip();
		mapManager.drawElement.sketch = evt.feature;
		var tooltipCoord = evt.coordinate;
		listener = mapManager.drawElement.sketch.getGeometry().on('change', function(evt) {
			var geom = evt.target;
			var output;
			if (geom instanceof ol.geom.Polygon) {
				output = formatArea(geom);
				tooltipCoord = geom.getInteriorPoint().getCoordinates();
			}else if (geom instanceof ol.geom.LineString) {
				output = formatLength(geom);
				tooltipCoord = geom.getLastCoordinate();
			}
			mapManager.drawElement.measureTooltipElement.innerHTML = output+' ';
			mapManager.drawElement.measureTooltip.setPosition(tooltipCoord);
		});
	});
	draw.on('drawend',function(e) {
		mapManager.drawElement.measureTooltipElement.className = 'ol-tooltip ol-tooltip-static';
		mapManager.drawElement.measureTooltip.setOffset([0, -7]);
		mapManager.drawElement.sketch = null;
		mapManager.drawElement.measureTooltipElement = null;
		ol.Observable.unByKey(listener);
		draw.setActive(false);
		$('.draw.active').removeClass("active");
	});
}

/**
 * draw 길이 표시 툴팁 생성기능 
 * @function map.createMeasureTooltip
 */
function createMeasureTooltip() {
	if (mapManager.drawElement.measureTooltipElement) {
		mapManager.drawElement.measureTooltipElement.parentNode.removeChild(mapManager.drawElement.measureTooltipElement);
	}
	mapManager.drawElement.measureTooltipElement = document.createElement('div');
	mapManager.drawElement.measureTooltipElement.className = 'ol-tooltip ol-tooltip-measure';
	mapManager.drawElement.measureTooltip = new ol.Overlay({
		element: mapManager.drawElement.measureTooltipElement,
		offset: [0, -15],
		positioning: 'bottom-center'
	});
	mapManager.map.addOverlay(mapManager.drawElement.measureTooltip);
	mapManager.drawElement.tooltip.push(mapManager.drawElement.measureTooltip);
}

/**
 * 지도그리기 vectorSource 삽입 기능
 * @param {object} layer
 * @param {object} data
 * @param {string} target
 * @function 
 */
function setDrawVectorSource(){
	//var attribute = [type];
	const source = new ol.source.Vector({
		wrapX: false,
		//attributions : attribute
	});
	mapManager.sourceClear.push(source);
	return source;
}

/**
 * 레이어 purpose 값 가져오기 값 셋팅 기능
 * @function map.getLayerPurpose
 */
function getLayerPurpose(id) {
	const jsonObj = {};
	jsonObj.chkDeGrpCd = 'FCLT_PURPOSE';
	jsonObj.userId = window.opener.document.getElementById('loginId').value
	var fcltPurposeData = [];
	var fcltData = [];
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type      : "POST",
		url       : "/select/oprt.getCodeDetailList/action",
		dataType  : "json",
		data      : JSON.stringify(jsonObj),
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
		var purpose = {};
		$.each(rows, function (index) {
			purpose.id = Number(rows[index].deCd);
			purpose.text = rows[index].deCdNm;
			fcltPurposeData.push({id: purpose.id, text: purpose.text, 'state' : {"selected" : "true"} });
			fcltData.push(rows[index].deCd)
		});
		if(id != undefined && id != ''){
			createPurposeJSTree(id,fcltPurposeData);
		}
 	}).fail(function (xhr) {
		console.log(JSON.stringify(xhr));
	}).always(function() {
		
	});
	return fcltData;
}

/**
 * safeMap 값 셋팅
 */
function getSafeMap(treeId){
	var setData = [];
	var id = ['safemap1','safemap2','safemap3','safemap4','safemap5'];
	var value = ["A2SM_PHARMACY" ,"A2SM_AED" ,"A2SM_TFCACDSTATS_0" ,"A2SM_CRMNLSTATS" ,"A2SM_HEALTH_CENTER"];;
	var text = ["약국","AED","교통사고","치안","보건소"];
	for(var i=0;i<id.length;i++){
		var setObj = {};
		setObj.id = id[i];
		setObj.value = value[i];
		setObj.text = text[i];
		setData.push(setObj);
	}
	createSafeMap(treeId,setData);
	return setData;
}

/**
 * 레이어 트리 생성 
 * @param {string} id -트리아이디
 * @param {object} jsondata -레이어 트리 데이터
 */
function createPurposeJSTree(id,jsondata) {
	var layerPopup =$('<div>').addClass('layer-popup').attr({'id':id });
	$('.map-layer').append(layerPopup).css('display','none');
	$('#'+id).jstree({
		"core": {
			"data" : jsondata,
			 "themes":{
		            "icons":false
		        }
		},
		"checkbox" : {
			"keep_selected_style" : false
		},
		"plugins" : [ "checkbox" ]
	});
}

function createSafeMap(id,jsondata){
	var layerPopup = $('<div>').addClass('safe-popup').attr({'id':id});
	$('.map-safe').append(layerPopup).css('display','none');
	$('#'+id).jstree({
		"core": {
			"data" : jsondata,
			 "themes":{
		            "icons":false
		        }
		},
		"checkbox" : {
			"keep_selected_style" : false
		},
		"plugins" : [ "checkbox" ]
	});
}

/**
 * 레이어 버튼 클릭 시 이벤트
 * @param {string} id - 트리 아이디
 * @returns
 */
function clickBtnLayer(id){
	if($('#btnLayer').attr('class').indexOf('active') == -1){
		$('.map-layer').css('display','none');
		return;
	}
	$('.map-layer').css('display','block');
	$('#'+id).on("changed.jstree", function(e, data) {
		const param = {};
		if(data.selected.length > 0 ){
			param.purposeSpace = data.selected;
		} else {
			param.purposeSpace = ['레이어'];
		}
		setCctvDraw(param,'');
	});
}



/**
 * 레이어 버튼 클릭 시 이벤트
 * @param {string} id - 트리 아이디
 * @returns
 */
function clickBtnSafe(id){
	if($('#btnSafe').attr('class').indexOf('active') == -1){
		$('.map-safe').css('display','none');
		return;
	}
	$('.map-safe').css('display','block');
	$('#'+id).on("changed.jstree", function(e, data) {
		var targetVal = data.node.original.value;
		var targetId = data.node.original.id;
		if(data.action=="select_node"){
			eval(targetId+"= new ol.layer.Tile({"
					+ "source:  new ol.source.TileWMS({"
						+"url: 'http://www.safemap.go.kr/sm/apis.do?apikey=YW3D9Q4G-YW3D-YW3D-YW3D-YW3D9Q4G57',"
						+"params: {'layers': '"+targetVal+"', 'tiled': true,format: 'image/png',exceptions:'text/xml',transparent: true},"
						+"serverType: 'geoserver',"
						+"tileOptions: {crossOriginKeyword: 'anonymous'},"
						+"transitionEffect: null,"
						+"projection: ol.proj.get('EPSG:3857')"
						+"},{isBaseLayer: false})"
					+"});");
			mapManager.map.addLayer(eval(targetId));
		}
		else {
			mapManager.map.removeLayer(eval(targetId));
		}
	});
}


/**
 * cctv 그려주는 기능 
 * @param {object} param 쿼리 실행 parameter
 * @param type
 * @function map.setCctvDraw
 */

function setCctvDraw(param, type){
	if(mapManager.getVectorLayer('cctv') != null){
		mapManager.map.removeLayer(mapManager.getVectorLayer('cctv'));
	}
	param.recordCountPerPage = '-1';
	param.featureKind = 'cctv';
	param.userId = opener.document.getElementById('loginId').value;
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url         : "/getCctvGeoFeature",
		dataType    : "json",
		data        : JSON.stringify(param),
		async       : false
	}).done(function(data) {
		var style = cctvLayerStyle;
		var source = createCctvGeoJsonSource(data);
		var layer = mapManager.createVectorLayer('cctv',style, source);
		$('#loading').hide();
	});
}

/**
 * CCTV source 생성
 * @param {object} layer 
 * @param {object} data - 시설물 데이터
 * @function
 */
function createCctvGeoJsonSource(data) {
	var source = new ol.source.Vector();
	var features = new ol.format.GeoJSON().readFeatures(data);
	source.addFeatures(features);
	var cluster = new ol.source.Cluster({
		distance: 30,
		source: source
	});
	return cluster
}


/**
 * 시설물 데이터 feature 스타일
 * @param {object} feature - feature 데이터
 * @function  
 */
function cctvLayerStyle(feature) {
	var styleCache = {};
	var features = feature.get('features');
	var size = features.length;
	if(size == 1){
		var imgSrc = getCctvMarkerImage(features[0].getProperties());
		var strokeColor = features[0].getProperties().stateCd === '0' ? '#ff0000' : '#0000ff';
		var style = new ol.style.Style({
			image: new ol.style.Icon({ scale: .9, src: imgSrc }),
			zIndex: 0
		});
		return style;
	} else {
		var style = styleCache[size];
		if (!style) {
			style = new ol.style.Style({
				image : new ol.style.Circle({
					radius: 10,
					stroke: new ol.style.Stroke({
                	color: '#fff'
					}),
					fill: new ol.style.Fill({
						color: '#3399CC'
					})
				}),
				text: new ol.style.Text({
					text: size.toString(),
					fill: new ol.style.Fill({
						color: '#fff'
					})
				}),
				zIndex: 0
			});
			styleCache[size] = style;
		}
		return style;
	}
}

/**
 * 시설물데이터 마커 삽입 기능
 * @param {object} data -마커데이터 
 * @function map.getCctvMarkerImage
 */
// function getCctvMarkerImage(data) {
// 	const fcltPurposeCd = 0;
// 	const cctvAgYn = data.cctvAgYn;
// 	const presetNo = data.presetNo;
// 	const stateCd = data.stateCd;
// 	var imageSrc = '';
//
// 	if(stateCd == '1') {
// 		imageSrc = '../../images/icons/cctv/cctv_state_0.png'; // 마커이미지의 주소입니다
// 	} else if(cctvAgYn == '0') {
// 		imageSrc = '../../images/icons/cctv/cctv_'+cctvAgYn+'_'+fcltPurposeCd+'_0.png'; // 마커이미지의 주소입니다
// 	} else {
// 		imageSrc = '../../images/icons/cctv/cctv_'+cctvAgYn+'_'+fcltPurposeCd+'_0.png'; // 마커이미지의 주소입니다
// 	}
//
// 	return imageSrc;
// }

/**
 * event 종료
 * @param {object} data - 종료시킬 이벤트 data
 * @param {string} evtPrgrsCd - 이벤트 진행 코드
 * @function map.endEvent
 */
function endEvent(data, evtPrgrsCd) {
	const jsonObj = {};
	jsonObj.evtOcrNo = data.evtOcrNo;
	jsonObj.evtDtl = data.evtDtl; 
	jsonObj.evtEndYmdHms = moment().format('YYYYMMDDHHmmss');
	jsonObj.evtPrgrsCd = evtPrgrsCd;
	jsonObj.rceptUserId = opener.document.getElementById('loginId').value
	jsonObj.rceptConts = data.rceptConts;
	jsonObj.evtPlace = data.evtPlace;
	jsonObj.lon = data.lon;
	jsonObj.lat = data.lat;
	
	jsonObj.singleInsertSid = "event.saveEvent";
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url         : "/multiAjax/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async       : false,
		beforeSend  : function(xhr) {
			// 전송 전 Code
		}
	}).done(function (result) {
		if (result == "SUCCESS") {
			eventTouringSend(data, true);
			alert("종료 완료");
			mapManager.removeOverlayById('event');
			selectEvent();
		}
		else {
			alert("종료 실패");
		}
	}).fail(function (xhr) {
		alert("종료 실패");
	}).always(function() {
		
	});
}

/**
 * vms 투어링 on , off 기능
 * @param {object} data - 이벤트 데이터
 * @param {boolean} isEnd - flag 값 
 */
function eventTouringSend(data, isEnd) {
	//if (data.evtPrgrsCd == '30' || data.evtPrgrsCd == '25') return;
	const jsonObj = {};
	
	if(isEnd) {
		jsonObj.evtPrgrsCd = '91';
	} else {
		jsonObj.evtPrgrsCd = '30';
	}
	
	jsonObj.lat = data.lat;
	jsonObj.lon = data.lon;
	jsonObj.evtOcrNo = data.evtOcrNo;
	jsonObj.rowNm = 5;
	jsonObj.recordCountPerPage = -1;
	
	$.ajax({
		type       : "POST",
		url        : "/eventTouringSend.do",
		dataType   : "json",
		data : {"param" : JSON.stringify(jsonObj)},
		async      : true,
		beforeSend : function(xhr) {
			// 전송 전 Code
		}
	}).done(function (result) {
		
	});
}

/**
 * cctv 마커 이동 시 사용 기능(현재 사용안함 드론사용시 필요 기능) 
 * @param {object} data - 시설물 데이터
 * @function map.moveCctvMarker
 */
function moveCctvMarker(data) {
	const source = mapManager.getVectorLayer('cctv').getSource().getSource();
	const feature = source.getFeatureById(data.fcltId);
	
	const coord = ol.proj.transform([data.lon, data.lat], mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection);
	feature.getGeometry().setCoordinates(coord);
	feature.set('lat', data.lat);
	feature.set('lon', data.lon);
	
	dialogManager.mapMoveEvent();
}

/**
 * 같은위치에 있는 cctv 리스트를 가져오는 기능
 * @param {object} feature - feature 데이터 
 * @param {function} callback - callback function
 * @function map.getSiteList
 */
function getSiteList(feature, callback){
	const jsonObj = {};
	jsonObj.sameLat = feature.getProperties().lat;
    jsonObj.sameLon = feature.getProperties().lon;
    jsonObj.sameCctv = true;
    jsonObj.recordCountPerPage = '-1';
    jsonObj.pageKind = 'manage';
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type		: "POST",
		url			: "/select/facility.selectSiteCctvList/action",
		dataType	: "json",
		data		: JSON.stringify(jsonObj),
		async		: false
	}).done(function(result){
		const rows = result.rows;
    	if(rows=='sessionOut'){
			alert('로그인 시간이 만료되었습니다.');
			closeWindow();
		}
		if(typeof callback == 'function') callback(feature, rows);
	})
}

function roadViewClickEventListener(e) {
	var path = "common/popup/roadview";
	var coordnate = mapManager.map.getCoordinateFromPixel(e.pixel);
	var trans =  ol.proj.transform([coordnate[0], coordnate[1]], mapManager.properties.projection, mapManager.properties.pro4j[mapManager.properties.type]);
	var url = encodeURI('/action/page.do?path=' + path + '&lon='+trans[0]+'&lat='+trans[1]+"&type='"+mapManager.tiles+"'");
	window.open(url,'roadviewPopup','height=' + screen.height + ',width=' + screen.width ,'channelmode=yes,left=0,top=0');
	//window.open("/openEventText.do?nodeId="+nodeId+"&vmsSvrIp="+vmsIp+"&imgUrl="+imgUrl,"text_popup","width=600 height=800");
}


//2020-11-06 
function clickLineLayer(id,type){
	if(type == 'c') {
		$('#'+id).on("changed.jstree", function(e, data) {
			var layerTitle = data.node.original.id;
			var layerColor = data.node.original.color;
			if(data.action == 'select_node') {
				getFcltCnctLine(layerTitle,layerColor);
			} else {
				mapManager.removeVectorLayer(layerTitle);
			}
		})
		return;
	} else if($('#lineLayer').attr('class').indexOf('active') == -1){
		$('.map-line-layer').css('display','none');
		return;
	}
	$('.map-line-layer').css('display','block');
	$('#'+id).on("changed.jstree", function(e, data) {
		var layerTitle = data.node.original.id;
		var layerColor = data.node.original.color;
		if(data.action == 'select_node') {
			getFcltCnctLine(layerTitle,layerColor);
		} else {
			mapManager.removeVectorLayer(layerTitle);
		}
	});
}

/**
 * 
 * @param {string} id - js tree 아이디 값
 * @param jsondata
 * @returns
 */
function createfcltCnctLineJSTree(id,jsondata) {
	var layerPopup =$('<div>').addClass('layer-popup').attr({'id':id });
	$('.map-line-layer').html('');
	$('.map-line-layer').append(layerPopup).css('display','none');
	$('#'+id).jstree({
		"core": {
			"data" : jsondata,
			 "themes":{
		            "icons":false
			 },
			 "check_callback" : true
		},
		"checkbox" : {
			"keep_selected_style" : false
		},
		"plugins" : [ "checkbox" ]
	})
}

function getLayerCode(id) {
	const jsonObj = {};
	jsonObj.chkDeGrpCd = 'FACILITY_NODE';
	jsonObj.userId = window.opener.document.getElementById('loginId').value
	var fcltLineData = [];
	var lineData = [];
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url         : "/select/facility.getLayerCode/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async       : false,
		beforeSend  : function (xhr) {
			// 전송 전 Code
		},
	}).done(function (result) {
		const rows = result.rows;
    	if(rows=='sessionOut'){
			alert('로그인 시간이 만료되었습니다.');
			closeWindow();
		}
		var purpose = {};
		$.each(rows, function (index) {
			purpose.id = rows[index].layerName;
			purpose.text = rows[index].layerName;
			purpose.color = rows[index].layerColor;
			fcltLineData.push({id: purpose.id, text: purpose.text, color:purpose.color });
			lineData.push(rows[index].layerName)
		});
		if(id != undefined && id != ''){
			createfcltCnctLineJSTree(id,fcltLineData);
		}
 	}).fail(function (xhr) {
		console.log(JSON.stringify(xhr));
	}).always(function() {
		
	});
	return lineData;
}

function getFcltCnctLine(layerTitle,layerColor) {
	var jsonObj = {};
	jsonObj.layerTitle = layerTitle;
	$.ajax({
		type		: "POST",
		url			: "/getLineStringData.do",
		dataType	: "json",
		data		: {"param" : JSON.stringify(jsonObj)},
		async		: false
	}).done(function(result) {
		var source = getFcltCnctLineSource(result);
		var layer = mapManager.createVectorLayer(layerTitle, fcltLineColor(layerColor), source);
		layer.set('layerColor',layerColor);
	})
}

function getFcltCnctLineSource(data) {
	var source = new ol.source.Vector();
	var features = new ol.format.GeoJSON().readFeatures(data);
	source.addFeatures(features);
	return source;
}

function fcltLineColor(color) {
	var style = new ol.style.Style({
		stroke: new ol.style.Stroke({
			color: color,
			width: 4,
		}),
	});
	return style;
}
