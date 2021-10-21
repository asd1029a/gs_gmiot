/**
 * 관제 페이지 관련 함수 모음
 * @namespace evtMain
 * */

/**
 * feature 그려주는 기능
 * @function evtMain.setEventDraw
 * @param {object} param -이벤트 
 * @param {string} type 
 * @param {function} callback
 * @param {object} callbackOption
 */
function setEventDraw(param, type, callback, callbackOption) {
	param.recordCountPerPage = '-1';
	param.featureKind = 'event';
	param.userId = opener.document.getElementById('loginId').value;
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type       : "POST",
		url        : "/getEventGeoFeature",
		dataType   : "json",
		data       : JSON.stringify(param),
		async      : false
	}).done(function(data) {
		var source = createEventGeoJsonSource(data);
		var style = eventLayerStyle;
		mapManager.createVectorLayer("event", style, source);
	});
}

/**
 * event Layer feature데이터 스타일 셋팅
 * @function evtMain.eventLayerStyle
 * @param {object} feature - feature 데이터
 */
function eventLayerStyle(feature) {
	var imgSrc = createEventMarker(feature.getProperties());
	//var imgSrc = '../images/icons/logout_over.png'; 
	var style = new ol.style.Style({
		image: new ol.style.Icon({ scale: 1.2 , src: imgSrc }),
		text: new ol.style.Text({
			offsetY: 25,
			fill: new ol.style.Fill({ color: '#111' }),
			stroke: new ol.style.Stroke({ color: '#eee', width: 2 })
		}),
		zIndex: 3
	});
	return style;
}

/**
 * 이벤트마커 이미지 생성 
 * @function evtMain.createEventMarker
 * @param {object} data -marker 데이터
 */
function createEventMarker(data){
	var imageSrc  = '../../images/icons/EVENT_TYPE_3_S.png'; // 마커이미지의 주소입니다
	if (data.iconUrl != null) {
		imageSrc = '/file/getImage2?sPath=event&imageUrl=' + data.iconUrl;
	}
	return imageSrc;
}


/**
 * car line 스타일 셋팅 기능
 * @function evtMain.carLineLayerStyle
 * @param {object} feature - feature 데이터 
 */
function carLineLayerStyle(feature) {
	var geometry = feature.getGeometry();
	var styles = [
		new ol.style.Style({
			stroke: new ol.style.Stroke({
				color: '#ffcc33',
				width: 4,
				lineDash : [ .1, 5 ]
			})
		})
	];
	geometry.forEachSegment(function (start, end) {
		var dx = end[0] - start[0];
		var dy = end[1] - start[1];
		var rotation = Math.atan2(dy, dx);
		
		var lineStr1 = new ol.geom.LineString([end, [end[0] - 40, end[1] + 40]]);
		lineStr1.rotate(rotation, end);
		var lineStr2 = new ol.geom.LineString([end, [end[0] - 40, end[1] - 40]]);
		lineStr2.rotate(rotation, end);
		
		var stroke = new ol.style.Stroke({
			color: '#ffcc33',
			width: 4
		});
		
		styles.push(new ol.style.Style({
			geometry: lineStr1,
			stroke: stroke
		}));
		
		styles.push(new ol.style.Style({
			geometry: lineStr2,
			stroke: stroke
		}));
	});
	return styles;
}

/**
 * EVENT source 생성
 * @function evtMain.createEventGeoJsonSource
 * @param {object} layer 
 * @param {object} data - event 데이터
 * @return {object} source - 소스
 */
function createEventGeoJsonSource(data) {
	var source = new ol.source.Vector();
	var features = new ol.format.GeoJSON().readFeatures(data);
	source.addFeatures(features);
	return source;
}

/**
 * event car 소스 생성
 * @function evtMain.createEventCarSource
 * @param {object} data - 데이터
 * @returns {object} source - 소스 
 */
function createEventCarSource(data){
	var carLineList = data.rows;
	const source = new ol.source.Vector();
	for(var i=0; i<carLineList.length; i++) {
		var lonlat = JSON.parse(carLineList[i].lonlat);
		if(lonlat.length > 1) {
			const lonlatTarget = [];
			for(var j=0; j<lonlat.length; j++) {
				lonlatTarget.push(new ol.proj.transform(lonlat[j],mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection));
			}
			var feature = new ol.Feature({
		        geometry: new ol.geom.LineString(lonlatTarget)
		    });
			source.addFeature(feature);
		}
	}
	return source;
}

/**
 * 페이지네이션 클릭시
 * @function evtMain.pageClick
 * @param {string} pagenumber - 현재 페이지 ㅈ번호
 * @param event
 * @param {object} data - 리스트 데이터
 * @param {string} itemsOnPage - 한페이지 보여줄 리스트 숫자
 */
function pageClick(pagenumber, event, data, itemsOnPage) {
	const length = data.length;
	var max = pagenumber * itemsOnPage;
	max = max <= length ? max : length;
	var cnt = (pagenumber - 1) * itemsOnPage;
	
	$('#list_count').text(length);

	$('#eventList').html('');
	
	for(var i = cnt; i < max; i++) {
		const temp = data[i];
		var inner = document.createElement('dl');
		var i1 = document.createElement('dt');
		var i1_1 = document.createElement('span');
		var i1_2 = document.createElement('span');
		var i2 = document.createElement('dd');
		var i3 = document.createElement('dd');
		
		i1_1.classList.add('count');
		i1_2.classList.add('event');
		
		i1.appendChild(i1_1);
		i1.appendChild(i1_2);
		
		i1_1.innerHTML = temp.rnum;
		i1_2.innerHTML = temp.evtNm;
		i2.innerHTML = temp.evtPlace;
		i3.innerHTML = temp.evtOcrYmdHms;
		
		inner.appendChild(i1);
		inner.appendChild(i2);
		inner.appendChild(i3);
		$(inner).bind('click', function() {
			const position = ol.proj.transform([temp.lon, temp.lat], mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection);
			const content = createEventOverlayContent(temp);
			
			const option = {
				id : 'event',
				position : position,
				element : content,
				offset : [ 0, -250 ],
				positioning : 'center-center',
				stopEvent : true,
				insertFirst : true
			}

			setEvtSelectedOverlay(position);
			mapManager.setCenter(position);
			mapManager.setOverlay(option);
			
			if(temp.evtId == "LPRUM101"){
				const carLayer = mapManager.createVectorLayer('eventCar');
				
				const param = {};
				
				param.evtTime = temp.evtOcrYmdHms.substr(0,10)
				param.evtDtl = temp.evtDtl;
				$.ajax({
					type       : "POST",
					url        : "/getCreateCarLineFeature",
					dataType   : "json",
					data       : {
						"param" : JSON.stringify(param)
					},
					async      : true
				}).done(function(data) {
					const eventCarSource = createEventCarSource(data);
					const carStyle = carLineLayerStyle;
					
					mapManager.createVectorLayer('eventCar', carStyle, eventCarSource );
					$('#loading').hide();
				})
				
			} else {
				mapManager.removeVectorLayer('eventCar');
			}
		});
		$('#eventList').append(inner);
	}
}

/**
 * 이벤트 리스트 표출
 * @function evtMain.selectEvent
 * @param {function} callback -callback function
 * @param {object} callbackOption
 */
function selectEvent() {
	if(mapManager.getVectorLayer('event') != null){
		mapManager.map.removeLayer(mapManager.getVectorLayer('event'));
	}
	
	if(mapManager.getVectorLayer('eventCar') != null){
		mapManager.map.removeLayer(mapManager.getVectorLayer('eventCar'));
	}
	
	const jsonObj = {};
	
	var gbnVal;
	if($('#eventSelect').val() != "null") {
		gbnVal = $('#eventSelect').val();
	}
	
	jsonObj.userId = opener.document.getElementById('loginId').value
	jsonObj.evtSpace = areaCode;	//행정구역
	jsonObj.evtId = gbnVal;		//이벤트구분
	jsonObj.evtDtl = $("#contSearch").val();
	
	var flag = $(".aside-tab-btn ul li.active").attr('value');
	jsonObj.pageKind = flag;
	
	if($('#mapFlag').prop('checked')) {
		var extent = mapManager.map.getView().calculateExtent(mapManager.map.getSize());

		var posSW = ol.proj.transform([extent[0], extent[1]], mapManager.properties.projection, mapManager.properties.pro4j[mapManager.properties.type]).toString().replace(',',' ');
		var posNW = ol.proj.transform([extent[0], extent[3]], mapManager.properties.projection, mapManager.properties.pro4j[mapManager.properties.type]).toString().replace(',',' ');
		var posSE = ol.proj.transform([extent[2], extent[1]], mapManager.properties.projection, mapManager.properties.pro4j[mapManager.properties.type]).toString().replace(',',' ');
		var posNE = ol.proj.transform([extent[2], extent[3]], mapManager.properties.projection, mapManager.properties.pro4j[mapManager.properties.type]).toString().replace(',',' ');
		
		var mapBound = posNW+','	//북서
					   +posNE+','	//북동
					   +posSE+','	//남동
					   +posSW+','	//남서
					   +posNW;

		jsonObj.mapBound = mapBound;
	}
	
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url 		: "/select/event.selectEventList/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async      : false
	}).done(function(result) {
		const rows = result.rows;
		var itemsOnPage = 10;
		$("#eventListPagination").simplePagination({
			items: rows.length,
			itemsOnPage : itemsOnPage,
			displayedPages : 5,
			edges : 0,
			prevText : "<",
			nextText : ">",
			cssStyle: 'custom-theme',
			onInit : function (){
				pageClick(1, undefined, rows, itemsOnPage);
			},
			//클릭할때
			onPageClick :  function (pagenumber , event) {
				pageClick(pagenumber, event, rows, itemsOnPage);
			}
		});
		setEventDraw(jsonObj, '');
	});
}

/**
 * 관제 페이지 맵 마우스 포인터 이동 이벤트 리스너
 * @function evtMain.evtMapPointermoveListener
 * @param {object} evt -event
 */
function evtMapPointermoveListener(evt) {
	if (!evt.dragging) {
		mapManager.map.getTargetElement().style.cursor = 
			mapManager.map.hasFeatureAtPixel(mapManager.map.getEventPixel(evt.originalEvent)) ? 'pointer' : '';
	}
	
	feature = undefined;
	
	mapManager.map.forEachFeatureAtPixel(evt.pixel, function(f) {
		var features = f.get('features');
		if(features != undefined){
			if(features.length == 1){
				feature = features[0];
				return true;
			}
		}
	});
	
	const overlay = mapManager.getOverlay('cctv');
	
	if(feature && overlay === null){
		var callback = function(feature, data) {
			const position = feature.getGeometry().getCoordinates();
			const content = createFcltSlideContent(data);
			
			const option = {
				id : 'cctv',
				position : position,
				element : content,
				offset : [ 0, 0 ],
				positioning : 'center-center',
				stopEvent : true,
				insertFirst : true
			}
			
			mapManager.setOverlay(option);
		}
		getSiteList(feature, callback);
	}
}

/**
 * 관제 페이지 맵 마우스 클릭 이동 이벤트 리스너
 * @function evtMain.evtMapClickListener
 * @param {object} e -event
 */
function evtMapClickListener(e){
	mapManager.map.forEachFeatureAtPixel(e.pixel, function(feature, layer) {
		var evtInfo = feature.getProperties()
		if(feature.getProperties().featureKind == 'event'){
			//mapManager.selectedFeatures(feature)
			var data = feature.getProperties();
			console.log(data);
			const position = ol.proj.transform([data.lon, data.lat], mapManager.properties.pro4j[mapManager.properties.type],mapManager.properties.projection);
			const content = createEventOverlayContent(data);
			
			const option = {
				id : 'event',
				position : position,
				element : content,
				offset : [ 0, -250 ],
				positioning : 'center-center',
				stopEvent : true,
				insertFirst : true
			}
			
			setEvtSelectedOverlay(position);
			mapManager.setCenter(position);
			mapManager.setOverlay(option);
			
			if(evtInfo.evtId == "LPRUM101"){
				const carLayer = mapManager.createVectorLayer('eventCar');
				
				const param = {};
				
				param.evtTime = evtInfo.evtOcrYmdHms.substr(0,10)
				param.evtDtl = evtInfo.evtDtl;
				$.ajax({
					type       : "POST",
					url        : "/getCreateCarLineFeature.do",
					dataType   : "json",
					data       : {
						"param" : JSON.stringify(param)
					},
					async      : true
				}).done(function(data) {
					const eventCarSource = createEventCarSource(data);
					const carStyle = carLineLayerStyle;
					
					mapManager.createVectorLayer('eventCar', carStyle, eventCarSource );
					$('#loading').hide();
				})
				
			} else {
				mapManager.removeVectorLayer('eventCar');
			}
		}//if
	});
}

/**
 * 시설물 팝업생성
 * @function evtMain.createFcltSlideContent
 * @param {Array} data - 시설물 데이터 Array
 */
function createFcltSlideContent(data){
	let content = document.createElement('div');
	content.classList.add('slide-popup');
	content.style.width = data.length > 5 ? 50 + (5 * 25) + 'px' : 50 + (data.length * 25) + 'px';
	
	let leftBtn = document.createElement('a');
	
	leftBtn.setAttribute('href', '#');
	leftBtn.classList.add('img-box');
	leftBtn.classList.add('left-zero');
	
	let leftImg = document.createElement('img');
	leftImg.src = '/images/icons/pagination_prev.png';
	leftImg.style.width = '25px';
	
	leftBtn.appendChild(leftImg);
	
	let divWrap = document.createElement('div');
	
	divWrap.classList.add('menu-wrap');
	
	let ul = document.createElement('ul');
	
	ul.id = 'cctvSlide';
	
	for(var i = 0; i < data.length; i++) {
		let obj = data[i];
		
		let li = document.createElement('li'); 
		let a = document.createElement('a');
		let img = document.createElement('img');
		li.style.width = '25px';
		
		a.setAttribute('href', '#');
		a.classList.add('img-box');
		
		const directionLayer = new ol.layer.Vector({
			source: new ol.source.Vector()
		});

		const directionFeature = new ol.Feature({});
		obj.directionLayer = directionLayer;
		obj.directionFeature = directionFeature;
		directionLayer.getSource().addFeature(directionFeature);
		
		$(a).bind({
			'click': function(e) {
				if($('.circlr_container').length > 0 && !$('#setCirclr').is(':visible')) {
					alert('순환감시 추가, 수정 시에만 영상이 재생됩니다.');
					return;
				}

				if(!videoManager.isPlaying(obj.fcltId)) {
					return;
				}
				
				const dialogOption = {
					draggable: true,
					clickable: true,
					data: obj,
					css: {
						width: '400px',
						height: '340px'
					}
				}
				
				const dialog = $.connectDialog(dialogOption);
				dialog.data('siteList', data);
				
				const option = {};
				option.data = obj;
				option.parent = dialog;
				
				if(!videoManager.createPlayer(option)) {
					dialogManager.close(dialog);
				};
			},
			'mouseenter': function(e) {
				if(obj.cctvAgYn === '1') return;
				mapManager.map.addLayer(directionLayer);
				getPresetPoint(obj, obj.presetNo, directionFeature, createPresetDirection);
			},
			'mouseout': function(e) {
				if(obj.cctvAgYn === '1') return;
				mapManager.map.removeLayer(directionLayer);
			}
		});
		
		img.src = getCctvMarkerImage(obj);
		
		a.appendChild(img);
		li.appendChild(a);
		ul.appendChild(li)
	}
	
	divWrap.appendChild(ul);
	
	let rightBtn = document.createElement('a');
	
	rightBtn.setAttribute('href', '#');
	rightBtn.classList.add('img-box');
	rightBtn.classList.add('right-zero');
	
	let rightImg = document.createElement('img');
	rightImg.src = '/images/icons/pagination_next.png';
	rightImg.style.width = '25px';
	
	rightBtn.appendChild(rightImg);
	
	content.appendChild(leftBtn);
	content.appendChild(divWrap);
	content.appendChild(rightBtn);
	
	let cctvSlide = slideMenu.init();
	cctvSlide.init(ul, data.length > 5 ? 5 : data.length, 'h');
	
	leftBtn.addEventListener('click', function() {
		cctvSlide.move.left();
	});
	
	rightBtn.addEventListener('click', function() {
		cctvSlide.move.right();
	});
	
	var overFlag = false;
	
	content.addEventListener('mouseover', function() {
		overFlag = true;
		clearTimeout(closeOverlay);
	});
	
	content.addEventListener('mouseleave', function() {
		overFlag = false;
		mapManager.removeOverlayById('cctv');
	});
	
	var closeOverlay = setTimeout(function() {
		if(!overFlag) {
			mapManager.removeOverlayById('cctv');
		}
	}, 500);
	
	$('.ol-overlaycontainer-stopevent').on('pointermove', function(e){
		e.stopPropagation();
	});
	
	return content;
}

/**
 * 이벤트 팝업 content 생성 함수.
 * @function evtMain.createEventPopupOverlay 
 * @param {object} data - 팝업 데이터
 */
function createEventOverlayContent(data){
	var hHtml = ["이벤트 유형","발생일자","발생주소","접수내용","접수자","담당자"];
	var bHtml = [data.evtNm,data.evtOcrYmdHms,data.evtPlace,data.evtDtl,"",data.evtMngrUser];
	
	let popup = document.createElement('div');
	popup.classList.add('popup');
	popup.classList.add('popup-event');
	
	//->타이틀시작
	let popupTitle = document.createElement('dl');
	popupTitle.classList.add('popup-tit');
	let poupTitle_1 = document.createElement('dt');
	poupTitle_1.innerHTML = Number(data.rnum)+"# 이벤트 상세정보";
	let poupTitle_2 = document.createElement('dd');
	let poupTitle_3 = document.createElement('dd');
	
	let playBtn = document.createElement('img');
	playBtn.src = '../images/icons/icon_eventPopupPlayer.png';
	let closeBtn = document.createElement('img');
	closeBtn.src = '../images/icons/icon_closed.png';
	
	poupTitle_2.appendChild(playBtn);
	poupTitle_3.appendChild(closeBtn);
	
	popupTitle.appendChild(poupTitle_1);
	popupTitle.appendChild(poupTitle_2);
	popupTitle.appendChild(poupTitle_3);
	
	popup.appendChild(popupTitle);
	//<-타이틀끝
	
	//->내용시작
	let cont = document.createElement('div');
	cont.classList.add('event-cont');
	let article = document.createElement('article');
	article.classList.add('event-cont-list');
	let ul = document.createElement('ul');
	
	cont.appendChild(article);
	article.appendChild(ul);
	
	for(var i=0;i<hHtml.length;i++){
		let li = document.createElement('li');
		let h = document.createElement('span');
		let b = document.createElement('span');
		//td.id = "eventTableTd";
		h.innerHTML = hHtml[i];
		b.innerHTML = bHtml[i];
		li.appendChild(h);
		li.appendChild(b);
		ul.appendChild(li);
		b.setAttribute('title',bHtml[i]);
	}
	popup.appendChild(cont);
	//<-내용끝

	var pageKind = $(".aside-tab-btn ul li.active").attr('value');
	
	poupTitle_2.addEventListener('click', function() {
		if(pageKind === 'eventE') {
			const timestamp = {};
			timestamp.sTime = moment(data.evtOcrYmdHms, 'YYYYMMDDHHmmss').format('YYYYMMDDHHmmss');
			timestamp.eTime = moment(data.evtEndYmdHms, 'YYYYMMDDHHmmss').format('YYYYMMDDHHmmss');
			storageCastMntr(data, timestamp);
		} else {
			evtCastMntr(data);
		}
	});
	
	//팝업 종료 버튼
	poupTitle_3.addEventListener('click', function() {
		mapManager.removeOverlayById('event');
		mapManager.removeOverlayById('evtSelected');
		mapManager.removeVectorLayer('eventCar');
	});
	
	if(data.evtId == "LPRUM101") {
		let eventCarImg = document.createElement('div');
		eventCarImg.classList.add('car-img');
		let carImg = document.createElement('img');
		carImg.title = '확대';
		carImg.src = data.imgUrl !== null ? '/file/getImage2.do?sPath=LPRUM101&imageUrl=' + data.imgUrl : '/images/intro/logo.png';
		
		eventCarImg.addEventListener('click', function(e) {
			if ($(this).hasClass('full')) {
				$(this).removeClass('full');
				carImg.title = '확대';
			} else {
				$(this).addClass('full');
				carImg.title = '축소';
			}
		});
		
		eventCarImg.appendChild(carImg);
		popup.appendChild(eventCarImg);
	}
	
	if(data.evtPrgrsCd == '10' || data.evtPrgrsCd == '30'){
		let eventPopupBtn = document.createElement('ul');
		eventPopupBtn.classList.add('right-btn');
		eventPopupBtn.classList.add('ol-btn');
		
		let eventBtn = document.createElement('li');
		eventBtn.innerHTML = "이벤트 종료";
		eventPopupBtn.append(eventBtn);
		popup.appendChild(eventPopupBtn);
		
		eventBtn.addEventListener('click', function() {
			if(confirm("이벤트를 종료 하시겠습니까?")) {
				endEvent(data, '91');
				mapManager.removeOverlayById('event');
				mapManager.removeOverlayById('evtSelected');
				mapManager.removeVectorLayer('eventCar');
			}
		})
	}
	return popup;
}

/**
 * 투망감시 - 가까운 5대의 영상 팝업 재생 함수.
 * @function evtMain.castMntr 
 * @param {object} obj
 * @description 투망감시
 */
function castMntr(obj){
	const coordi = new ol.geom.Point(new ol.proj.transform([obj.coordinate[0],obj.coordinate[1]],mapManager.properties.projection,mapManager.properties.pro4j[mapManager.properties.type]))
	let lon = coordi.getCoordinates()[0];
	let lat = coordi.getCoordinates()[1];
	
	const jsonObj = {};
	
	let purposeSpace = getLayerPurpose();
	
	jsonObj.lat = lat;
	jsonObj.lon = lon;
	jsonObj.rowNm = 5;
	jsonObj.purposeSpace = purposeSpace;
	jsonObj.userId = opener.document.getElementById('loginId').value;
	
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type		: "POST",
		url			: "/select/facility.castMntr/action",
		dataType	: "json",
		data		: JSON.stringify(jsonObj),
		async		: true
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
			videoOption.isSite = false;

			if(!videoManager.createPlayer(videoOption)) {
				dialogManager.close(dialog);
			};
		}
		
		dialogManager.sortDialog();
	})
}

/**
 * 이벤트 수동 발생 팝업 생성 함수.
 * @function evtMain.passive
 * @param {object} obj - 좌표값
 * @description 수동발생
 */
function passive(obj){
	var coordi = new ol.geom.Point(new ol.proj.transform([obj.coordinate[0],obj.coordinate[1]],mapManager.properties.projection,mapManager.properties.pro4j[mapManager.properties.type]));
	var page = '/common/popup/eventPassiveOcrPopup';
	common.openDialogPop('mapPopup','수동발생','680','600',true,'/action/page.do',{path : page},'mapPopup',function(){
		$('#passiveOcrLon').val(coordi.getCoordinates()[0]);
    	$('#passiveOcrLat').val(coordi.getCoordinates()[1]);
    	$('#passiveOcrTime').val(moment().format("YYYYMMDDHHmmss"));
    	mapManager.searchDetailAddrFromCoords(obj.coordinate, function(result, status) {
    		if(status !== 'ZERO_RESULT') {
        		$('#passiveOcrPlace').val(result[0].address.address_name);
    		} else {
        		$('#passiveOcrPlace').val('주소 검색 불가 지역');
    		}
    	});
	})
}

/**
 * 이벤트 수동 발생 팝업 생성 함수.
 * @function evtMain.passive
 * @param {object} obj - 좌표값
 * @description 수동발생
 */
function ttl(obj){
	var page = '/common/popup/ttlSendPopup';
	common.openDialogPop('ttlSendPopup','TTL 메시지 전송','400','500',true,'/action/page.do',{path : page},'ttlSendPopup',function(){
		
	})
}

/**
 * 이벤트 투망감시 - 가까운 5대의 영상 팝업 재생 함수.
 * @function evtMain.evtCastMntr 
 * @param {object} obj
 * @description 이벤트 투망감시
 */
function evtCastMntr(obj){
	let lon = obj.lon;
	let lat = obj.lat;
	
	const jsonObj = {};
	
	let purposeSpace = getLayerPurpose();
	
	jsonObj.lat = lat;
	jsonObj.lon = lon;
	jsonObj.rowNm = 5;
	jsonObj.purposeSpace = purposeSpace;
	
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type		: "POST",
		url			: "/select/facility.evtCastMntr/action",
		dataType	: "json",
		data		: JSON.stringify(jsonObj),
		async		: true
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
			videoOption.isSite = false;
			videoOption.isEvent = true;

			if(!videoManager.createPlayer(videoOption)) {
				dialogManager.close(dialog);
			};
		}
		
		dialogManager.sortDialog();
	})
}

/**
 * 이벤트 저장영상 투망감시 - 가까운 5대의 영상 팝업 재생 함수.
 * @function evtMain.storageCastMntr 
 * @param {object} obj
 * @description 이벤트 저장영상 투망감시
 */
function storageCastMntr(obj, timestamp){
	let lon = obj.lon;
	let lat = obj.lat;
	
	const jsonObj = {};
	
	let purposeSpace = getLayerPurpose();
	
	jsonObj.lat = lat;
	jsonObj.lon = lon;
	jsonObj.rowNm = 5;
	jsonObj.purposeSpace = purposeSpace;
	
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type		: "POST",
		url			: "/select/facility.evtCastMntr/action",
		dataType	: "json",
		data		: JSON.stringify(jsonObj),
		async		: true
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
			videoOption.isEvent = true;
			videoOption.isSite = false;
			videoOption.timestamp = timestamp;

			if(!videoManager.createPlayer(videoOption)) {
				dialogManager.close(dialog);
			};
		}
		
		dialogManager.sortDialog();
	})
}

/**
 * 이벤트 오버레이 생성 
 * @function evtMain.setEvtSelectedOverlay
 * @param {object} position -팝업에 loc, lat
 */
function setEvtSelectedOverlay(position) {
	const content = createEvtSelectedOverlay();
	const option = {
		id : 'evtSelected',
		position : position,
		element : content,
		offset : [ 0, 0 ],
		positioning : 'center-center',
		stopEvent : false,
		insertFirst : true
	}
	
	mapManager.setOverlay(option);
}

/**
 * 이벤트 아이콘에 gif 삽입 기능
 * @function evtMain.createEvtSelectedOverlay
 * @returns {element} 선택된 마커 이미지 overlay
 */
function createEvtSelectedOverlay() {
	let wrap = document.createElement('div');
	
	wrap.classList.add('evt-selected-overlay');
	
	let img = document.createElement('img');
	img.src = '/images/icons/evt_selected.gif';
	
	wrap.appendChild(img);
	
	return wrap;
}

