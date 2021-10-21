/**
 * 영상 프리셋 및 방향에 대한 함수 모음.
 * @namespace preset
 * */


/**
 * 지도 위 프리셋 번호 Element의 x, y 위치 값을 잡아주는 함수.
 * @function preset.getPresetNumOverlayAnchor
 * @param {number} index - 방향 index
 * @see {@link videoManager.createButton} - videoManager.createButton
 * */
function getPresetNumOverlayAnchor(index) {
	switch (index) {
		case 1: 
			return {x: 0.000085, y: -0.0001}
		case 2: 
			return {x: 0.000135, y: 0}
		case 3: 
			return {x: 0.000085, y: 0.0001}
		case 4: 
			return {x: 0, y: 0.00015}
		case 5: 
			return {x: -0.000085, y: 0.0001}
		case 6: 
			return {x: -0.000135, y: 0}
		case 7: 
			return {x: -0.000085, y: -0.0001}
		case 8: 
			return {x: 0, y: -0.00015}
	}
}


/**
 * 지정 된 시작, 종료 위치에 맞춰 방향을 표시 할 데이터를 그려주는 함수.
 * @function preset.setDirectionFeatureGeom
 * @param {point} sPoint - 시작 위치
 * @param {point} ePoint - 종료 위치
 * @param {feature} directionFeature - 방향 표시 feature 데이터
 * @see {@link preset.createPresetDirection} - preset.createPresetDirection
 * */
function setDirectionFeatureGeom(sPoint, ePoint, directionFeature) {
	var geometry = new ol.geom.MultiLineString([
		[sPoint, ePoint]
	]);
	
	const style = [
		new ol.style.Style({
			stroke: new ol.style.Stroke({
				color: '#ffcc33',
				width: 0
			})
		})
	];
	
	geometry.getLineString().forEachSegment(function(start, end) {
		if(start[0]==sPoint[0]&&start[1]==sPoint[1]) {
			var dx = end[0] - start[0];
			var dy = end[1] - start[1];
			var rotation = Math.atan2(dy, dx);
			
			style.push(new ol.style.Style({
				geometry: new ol.geom.Point(start),
				image: new ol.style.Icon({
					src: '/svg/cone_pie.svg',
					opacity: 0.8,
					scale: 4.5,
					anchor: [0.5, 0.5],
					rotateWithView: false,
					rotation: -rotation
				})
			}));
		}
	});


	directionFeature.setGeometry(geometry);
	directionFeature.setStyle(style);
}

/**
 * 넘어온 presetNo와 카메라 데이터를 매칭하여 DB에 저장된 preset 정보를 읽어온다.
 * @function preset.getPresetPoint
 * @param {object} data - 카메라 데이터
 * @param {string} presetNo - 프리셋 번호
 * @param {object} diectionFeature - 방향 표시 feature 데이터
 * @param {preset.createPresetDirection} callback - callback function
 * @see {@link videoManager.setXeusPtzPosition} - videoManager.setXeusPtzPosition
 * */
function getPresetPoint(data, presetNo, directionFeature, callback) {
	const jsonObj = {};
	
	jsonObj.cctvId = data.fcltId;
	jsonObj.presetNo = presetNo;
	
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url         : "/select/facility.getFacilityPreset/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async      : false,
		beforeSend : function(xhr) {
			// 전송 전 Code
		}
	}).done(function (result) {
		const rows = result.rows;
    	if(rows=='sessionOut'){
			alert('로그인 시간이 만료되었습니다.');
			closeWindow();
		}
		if(rows.length == 0) return;
		const presetData = rows[0];
		if(callback != undefined) callback(data, presetData, directionFeature);
    }).fail(function() {
    });
}

/**
 * preset 방향에 맞춰 directionFeature를 그리는 함수.
 * @function preset.createPresetDirection
 * @param {object} data - 카메라 데이터
 * @param {object} presetData - 프리셋 데이터
 * @param {object} diectionFeature - 방향 표시 feature 데이터
 * @see {@link preset.getPresetPoint} - preset.getPresetPoint
 * */
var createPresetDirection = function (data, presetData, directionFeature) {
	if(typeof presetData == 'undefined') return;
	
	const ePoint = ol.proj.transform([presetData.lon, presetData.lat],mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection);
	const sPoint = ol.proj.transform([data.lon, data.lat],mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection);
	setDirectionFeatureGeom(sPoint, ePoint, directionFeature);
}

/**
 * preset 방향에 맞춰 directionFeature를 그릴 overlay 내부의 content를 생성하는 함수.
 * @function preset.createDirectionOverlayContent
 * @param {object} data - 카메라 데이터
 * @param {object} diectionFeature - 방향 표시 feature 데이터
 * @param {element} btn - 방향 선택 element
 * @see {@link preset.createDirectionOverlay} - preset.createDirectionOverlay
 * */
function createDirectionOverlayContent(data, directionFeature, btn) {
	const content = document.createElement('div');
	content.classList.add('direction_area');
	
	content.addEventListener('click', function(e) {
		if(confirm("현재 위치로 방향을 등록하시겠습니까?")) {
			const x = e.target.parentElement.offsetLeft + e.offsetX;
			const y = e.target.parentElement.offsetTop + e.offsetY;
			const temp = mapManager.map.getCoordinateFromPixel([x, y]);
			const point = ol.proj.transform(temp,mapManager.properties.projection,mapManager.properties.pro4j[mapManager.properties.type]);
			saveDirection(data, point, btn);
		}
	});
	
	content.addEventListener('mousemove', function(e) {
		const x = e.target.parentElement.offsetLeft + e.offsetX;
		const y = e.target.parentElement.offsetTop + e.offsetY;
		const ePoint = mapManager.map.getCoordinateFromPixel([x, y]);
		const sPoint = ol.proj.transform([data.lon, data.lat],mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection);
		setDirectionFeatureGeom(sPoint, ePoint, directionFeature);
	});
	
	return content;
}

/**
 * preset 방향에 맞춰 directionFeature를 그릴 overlay를 생성하는 함수.
 * @function preset.createDirectionOverlay
 * @param {object} data - 카메라 데이터
 * @param {object} diectionFeature - 방향 표시 feature 데이터
 * @param {element} btn - 방향 선택 element
 * @see {@link videoManager.createButton} - videoManager.createButton
 * */
function createDirectionOverlay(data, directionFeature, btn) {
	const lat = parseFloat(data.lat);
	const lon = parseFloat(data.lon);
	
	const content = createDirectionOverlayContent(data, directionFeature, btn);
	
	const overlay = new ol.Overlay({
		position : ol.proj.transform([lon,lat],mapManager.properties.pro4j[mapManager.properties.type],mapManager.properties.projection),
		element : content,
		positioning : 'center-center',
		stopEvent : true,
		insertFirst : true
	});
	
	mapManager.map.addOverlay(overlay);
	
	return overlay;
}

/**
 * 회전형 카메라의 preset 설정시 1~8번의 번호를 지도위에 그려주는 함수.
 * 이미 설정된 데이터가 있을 경우에는 isSaved값이 true, 아닐 경우 false이다.
 * @function preset.getPresetnumOverlayContent
 * @param {object} option - option 데이터
 * @param {string} option.no - 프리셋 번호
 * @param {object} option.data - 카메라 데이터
 * @param {boolean} option.flag - 이미 저장된 번호 인지 확인
 * @see {@link preset.createPresetNumOverlays} - preset.createPresetNumOverlays
 * */
function getPresetnumOverlayContent(option) {
	const no = option.index;
	const data = option.data;
	const isSaved = option.flag;
	
	const positionBtn = document.createElement('div');
	positionBtn.classList.add('get-preset-btn');
	
	if(isSaved) {
		positionBtn.classList.add('selected');
	}
	
	const span = document.createElement('span');
	span.innerHTML = no;
	
	positionBtn.appendChild(span);
	
	positionBtn.addEventListener('click', function(e) {
		if(confirm(no+"번으로 등록하시겠습니까?")) {
			$('#loading').toggle();
			const x = e.target.parentElement.offsetLeft + e.offsetX;
			const y = e.target.parentElement.offsetTop + e.offsetY;
			const temp = mapManager.map.getCoordinateFromPixel([x, y]);
			const point = ol.proj.transform(temp,mapManager.properties.projection,mapManager.properties.pro4j[mapManager.properties.type]);
			
			if(data.fcltSh == "SW") {
				data.player.getPtzPosition(data, no, point);
			} else {
				videoManager.saveHwPreset(data, point, no);
			}
			
			e.target.parentElement.classList.add('selected');
		}
	});

	return positionBtn;
}

/**
 * 회전형 카메라의 preset 설정시 1~8번의 번호를 그리기 위해 DB에서 해당 카메라의 preset 설정 정보를 읽어오는 함수.
 * @function preset.createPresetNumOverlays
 * @param {obejct} option - option 데이터
 * @param {object} option.data - 카메라 데이터
 * @param {Array} option.overlayList - overlay 데이터를 저장 할 배열 
 * @see {@link videoManager.createButton} - videoManager.createButton
 * */
function createPresetNumOverlays(option) {
	const data = option.data;
	const list = option.overlayList;
	
	const jsonObj = {};
	
	jsonObj.cctvId = data.fcltId;
	
	$.ajax({
	    contentType : "application/json; charset=utf-8",
    	type        : "POST",
		url         : "/select/facility.getFacilityPreset/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async      : false,
		beforeSend : function(xhr) {
		    // 전송 전 Code
		}
	}).done(function (result) {
		const rows = result.rows;
    	if(rows=='sessionOut'){
			alert('로그인 시간이 만료되었습니다.');
			closeWindow();
		}
		const presetData = rows;
		const temp = '';
		
		for (var i = 1; i <= 8; i++) {
			var flag = false;
			
			const anchor = getPresetNumOverlayAnchor(i);
			const lat = parseFloat(data.lat) + anchor.x;
			const lon = parseFloat(data.lon) + anchor.y;
			
			for (var j = 0, max = presetData.length; j < max; j++) {
				const presetNo = presetData[j].presetNo > 20 ? presetData[j].presetNo % 20 : presetData[j].presetNo;
				if (i == presetNo) {
					flag = true;
					break;
				}
			}
			
			data.player = option.player;
			
			const overlayOption = {
				index: i,
				flag: flag,
				data: data
			};
			
			const content = getPresetnumOverlayContent(overlayOption);
			
			const overlay = new ol.Overlay({
				position : ol.proj.transform([lon,lat],mapManager.properties.pro4j[mapManager.properties.type],mapManager.properties.projection),
				element : content,
				positioning : 'center-center',
				stopEvent : true,
				insertFirst : true
			});
			
			list.push(overlay);
			
			mapManager.map.addOverlay(overlay);
		}
		data.presetList = list;
		
    }).fail(function (xhr) {
        
    }).always(function() {

    });
	
}

/**
 * 고정형 카메라 방향 저장 함수.
 * @function preset.saveDirection
 * @param {object} data - 카메라 데이터
 * @param {object} point - 좌표 데이터
 * @param {element} btn - 방향 선택 element 
 * @see {@link videoManager.createDirectionOverlayContent} - 
 * */
var saveDirection = function(data, point, btn) {
	const jsonObj = {};
	
	jsonObj.cctvId = data.fcltId;
	jsonObj.cctvKnd = data.cctvKnd;
	jsonObj.presetNo = '1';
	jsonObj.presetName = '1';
	jsonObj.lat = point[1];
	jsonObj.lon = point[0];
	
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url         : "/ajax/insert/facility.saveFacilityPreset/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async      : false
	}).done(function(result) {
		alert('방향 등록 완료');
		$(btn).click();
	});
}

/**
 * 프리셋 설정 시 지도위에 그려진 1~8번의 번호 overlay를 지우는 함수.
 * @callback preset.removePresetBtns
 * @param {Array} list - preset overlay 배열 데이터
 * @see {@link videoManager.removeData} - videoManager.removeData
 * */
function removePresetBtns(list) {
	for(var i = 0, max = list.length; i < max; i++) {
		mapManager.map.removeOverlay(list[i]);
	}
}