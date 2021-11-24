/**
 * 시설물 관리 메인 관련
 * @namespace fcltMain
 * */

/**
 * 시설물 데이터를 가져오는 함수
 * @function fcltMain.selecCctv
 * */
function selectCctv() {
	const jsonObj = {};
	
	jsonObj.userId = opener.document.getElementById('loginId').value
	jsonObj.state = $('#statSelect').val();
	jsonObj.fcltSpace = areaCode;
	jsonObj.purposeSpace = useCode;

	if($('#mapFlag').prop('checked')) {
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
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url 		: "/select/facility.selectFcltSList/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async      : false
	}).done(function(result) {
		const rows = result.rows;
		var itemsOnPage = 10;
		$("#fcltListPagination").simplePagination({
			items: rows.length,
			itemsOnPage : itemsOnPage,
			displayedPages : 5,
			edges : 0,
			prevText : "<",
			nextText : ">",
			cssStyle: 'custom-theme',
			onInit : function (){
				cctvPageClick(1, undefined, rows, itemsOnPage);
			},
			//클릭할때
			onPageClick :  function (pagenumber , event) {
				cctvPageClick(pagenumber, event, rows, itemsOnPage);
			}
		});
	});
}

/**
 * 시설물 관리의 좌측 그리드 페이지 이동 함수
 * @function fcltMain.cctvPageClick
 * @param {number} pagenumber - 페이지 번호
 * @param {event} event - 페이지 변경 이벤트
 * @param {Array} data - 시설물 데이터 배열
 * @param {number} itemsOnPage - 페이지에 표시할 row 개수
 * */
function cctvPageClick(pagenumber, event, data, itemsOnPage) {
	const length = data.length;
	var max = pagenumber * itemsOnPage;
	max = max <= length ? max : length;
	var cnt = (pagenumber - 1) * itemsOnPage;
	
	$('#list_count').text(length);

	$('#fcltList').html('');
	
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
		i1_2.innerHTML = temp.mgmtNo;
		i2.innerHTML = temp.fcltNm;
		i3.innerHTML = temp.fcltId;
		
		inner.appendChild(i1);
		inner.appendChild(i2);
		inner.appendChild(i3);
		$(inner).bind('click', function() {
			var kinds = $('.aside-tab-btn ul').children('.active').attr('value');
			if(kinds == 'selectCctv'){
				setFcltPopup(temp);
				dialogManager.sortDialog();
			} else if(kinds == 'selectFclt'){
				var feature = mapManager.getVectorLayer('fclt').getSource().getFeatureById(temp.fcltId).getProperties();
				fcltOverlayAddMap(feature);
			}
			
		});
		
		$('#fcltList').append(inner);
	}
}

/**
 * 시설물 데이터 리스트 만들어주는 함수
 * @function fcltMain.selecFclt
 */
function selectFclt(){
	const jsonObj = {};
	jsonObj.menuNm = 'facility';
	jsonObj.state = $('#statSelect').val();
	jsonObj.fcltSpace = areaCode;
	jsonObj.fcltKnd = useCode;
	if($('#mapFlag').prop('checked')) {
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
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url 		: "/select/facility.selectFcltEList/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async      : false
	}).done(function(result) {
		const rows = result.rows;
		var itemsOnPage = 10;
		$("#fcltListPagination").simplePagination({
			items: rows.length,
			itemsOnPage : itemsOnPage,
			displayedPages : 5,
			edges : 0,
			prevText : "<",
			nextText : ">",
			cssStyle: 'custom-theme',
			onInit : function (){
				fcltPageClick(1, undefined, rows, itemsOnPage);
			},
			//클릭할때
			onPageClick :  function (pagenumber , event) {
				fcltPageClick(pagenumber, event, rows, itemsOnPage);
			}
		});
		setFcltDraw(jsonObj, '');
	});
}

/**
 * 시설물 관리의 좌측 그리드 페이지 이동 함수
 * @function fcltMain.fcltPageClick
 * @param {number} pagenumber - 페이지 번호
 * @param {event} event - 페이지 변경 이벤트
 * @param {Array} data - 시설물 데이터 배열
 * @param {number} itemsOnPage - 페이지에 표시할 row 개수
 * */
function fcltPageClick(pagenumber, event, data, itemsOnPage) {
	const length = data.length;
	var max = pagenumber * itemsOnPage;
	max = max <= length ? max : length;
	var cnt = (pagenumber - 1) * itemsOnPage;
	
	$('#list_count').text(length);

	$('#fcltList').html('');
	
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
		i1_2.innerHTML = temp.fcltId;
		i2.innerHTML = temp.fcltNm;
		i3.innerHTML = temp.purposeNm;
		
		inner.appendChild(i1);
		inner.appendChild(i2);
		inner.appendChild(i3);
		$(inner).bind('click', function() {
			var kinds = $('.aside-tab-btn ul').children('.active').attr('value');
			if(kinds == 'selectCctv'){
				setFcltPopup(temp);
				dialogManager.sortDialog();
			} else if(kinds == 'selectFclt'){
				var feature = mapManager.getVectorLayer('fclt').getSource().getFeatureById(temp.fcltId).getProperties();
				fcltOverlayAddMap(feature);
			}
			
		});
		
		$('#fcltList').append(inner);
	}
}

/**
 * 시설물 feature 생성
 * @function fcltMain.setFcltDraw
 * @param {object} param - fclt 파라미터
 * @param {string} type
 */
function setFcltDraw(param, type){
	param.featureKind = 'fclt';
	param.userId = opener.document.getElementById('loginId').value;
	$.ajax({
		type		: "POST",
		url			: "/getFcltGeoFeature.do",
		dataType	: "json",
		data		: {
			"param" : JSON.stringify(param)
		},
		async		: false
	}).done(function(data){
		var source = createFcltGeoJsonSource(data);
		var style = fcltLayerStyle;
		mapManager.createVectorLayer("fclt",style,source);
	});//done
}

/**
 * @function fcltMain.createFcltGeoJsonSource
 * 시설물데이터 vector source 생성
 * @param {object} data
 */
function createFcltGeoJsonSource(data){
	var source = new ol.source.Vector();
	var features = new ol.format.GeoJSON().readFeatures(data);
	source.addFeatures(features);
	return source;
}

/**
 * 시설물 데이터 이미지 스타일
 * @function fcltMain.fcltLayerStyle
 * @param features
 */
function fcltLayerStyle(feature){
	const imgSrc = createFcltMarker(feature.getProperties());
	const style = new ol.style.Style({
		image: new ol.style.Icon({ scale: 1, src: imgSrc }),
		zIndex: 0
	});
	return style;
}

function createFcltMarker(data){
	let imageSrc = '';
	imageSrc = '/file/getImage2.do?sPath=fclt&imageUrl=' + data.iconUrl;
	
	return imageSrc;
}

/**
 * 시설물 관리 좌측 row 선택시 지도 위에 영상 팝업을 그리는 함수
 * @function fcltMain.setFcltPopup
 * @param {object} data - 카메라 데이터
 * */
function setFcltPopup(data) {
	if(!videoManager.isPlaying(data.fcltId)) {
		return;
	}
	
	const dialogOption = {
		draggable: true,
		clickable: true,
		data: data,
		css: {
			width: '400px',
			height: '340px'
		}
	}
	
	const dialog = $.connectDialog(dialogOption);
	
	dialog.click();
	
	const option = {};
	option.data = data;
	option.parent = dialog;

	if(!videoManager.createPlayer(option)) {
		dialogManager.close(dialog);
	};
}

function fcltMapPointermoveListener(evt) {
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
		const callback = function(feature, data) {
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
 * CCTV 팝업생성
 * @param {object} feature - feature 데이터 
 * @param {object} data 팝업 데이터 삽입 
 * @param type
 * @function map.createFcltSlideContent
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
 * 관제 페이지 맵 마우스 클릭 이동 이벤트 리스너
 * @function fcltMain.fcltMapClickListener
 * @param {object} e -event
 */
function fcltMapClickListener(e){
	mapManager.map.forEachFeatureAtPixel(e.pixel, function(feature, layer) {
		var fcltInfo = feature.getProperties();
		if(fcltInfo.featureKind == 'fclt'){
			fcltOverlayAddMap(fcltInfo);
		}
	});
}

function fcltOverlayAddMap(fcltInfo){
	const position = ol.proj.transform([fcltInfo.lon, fcltInfo.lat], mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection);
	const content = createFcltOverlayContent(fcltInfo);
	const option = {
		id : 'fclt',
		position : position,
		element : content,
		offset : [ 0, -230 ],
		positioning : 'center-center',
		stopEvent : true,
		insertFirst : true
	}
	
	setFcltSelectedOverlay(position);
	mapManager.setCenter(position);
	mapManager.setOverlay(option);
}

/**
 * 시설물 선택 오버레이 추가 함수
 * @function fclt.setFcltSelectedOverlay
 * @param {object} position - 좌표 데이터
 */
function setFcltSelectedOverlay(position) {
	const content = createFcltSelectedOverlay();
	const option = {
		id : 'fcltSelected',
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
 * 시설물 선택 오버레이 contents 생성 함수
 * @function fclt.createFcltSelectedOverlay
 * @returns {element} - 선택된 마커 이미지 overlay
 */
function createFcltSelectedOverlay() {
	let wrap = document.createElement('div');
	
	wrap.classList.add('fclt-selected-overlay');
	
	let img = document.createElement('img');
	img.src = '/images/icons/evt_selected.gif';
	
	wrap.appendChild(img);
	
	return wrap;
}

/**
 * 센서 팝업 content 생성 함수.
 * @function fcltMain.createFcltPopupOverlay 
 * @param {object} data - 팝업 데이터
 */
function createFcltOverlayContent(data){
	var hHtml = ["시설물 종류","시설물 아이디","시설물 명칭","상태"];
	var bHtml = [data.purposeNm,data.fcltId,data.fcltNm,data.stateCase];
	var popupData = data
	let popup = document.createElement('div');
	popup.classList.add('popup-fclt');
	popup.classList.add('popup');
	
	//->타이틀시작
	let popupTitle = document.createElement('dl');
	popupTitle.classList.add('popup-tit');
	let poupTitle_1 = document.createElement('dt');
	poupTitle_1.innerHTML = Number(data.rnum)+"# 시설물 정보";
	let poupTitle_2 = document.createElement('dd');
	let poupTitle_3 = document.createElement('dd');
	let popupBtnArea = document.createElement('ul');
	popupBtnArea.classList.add('right-btn');
	popupBtnArea.classList.add('ol-btn');
	let popupBtnDd_1 = document.createElement('li');
	popupBtnDd_1.classList.add('btn_insert');
	let popupBtnDd_2 = document.createElement('li');
	popupBtnDd_2.classList.add('btn-delete');
	//let playBtn = document.createElement('img');
	//playBtn.src = '../images/icons/icon_eventPopupPlayer.png';
	let closeBtn = document.createElement('img');
	closeBtn.src = '../images/icons/icon_closed.png';
	
	//poupTitle_2.appendChild(playBtn);
	poupTitle_3.appendChild(closeBtn);
	
	popupTitle.appendChild(poupTitle_1);
	popupTitle.appendChild(poupTitle_2);
	popupTitle.appendChild(poupTitle_3);
	
	popup.appendChild(popupTitle);
	//<-타이틀끝
	
	//->내용시작
	let cont = document.createElement('div');
	cont.classList.add('fclt-cont');
	let article = document.createElement('article');
	article.classList.add('fclt-cont-list');
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
	
	//-> 버튼 시작
	popupBtnDd_1.innerHTML = ('수정');
	popupBtnDd_2.innerHTML = ('삭제');
	//popupBtnDd_1.appendChild(popupUpdate);
	//popupBtnDd_2.appendChild(popupDelete);
	popupBtnArea.appendChild(popupBtnDd_1);
	popupBtnArea.appendChild(popupBtnDd_2);
	popup.appendChild(popupBtnArea);
	//<- 버튼 끝
	
	poupTitle_2.addEventListener('click', function() {
		evtCastMntr(data);
	});
	
	//팝업 종료 버튼
	poupTitle_3.addEventListener('click', function() {
		mapManager.removeOverlayById('fclt');
		mapManager.removeOverlayById('fcltSelected');
		customLine.clear();
	});
	
	popupBtnDd_1.addEventListener('click', function() {
		const obj = {};
		var coordinate = [data.lon, data.lat];
		obj.coordinate = new ol.proj.transform([coordinate[0],coordinate[1]],mapManager.properties.pro4j[mapManager.properties.type],mapManager.properties.projection)
		obj.state = "U";
		obj.data = popupData;
		addFclt(obj);
	});
	popupBtnDd_2.addEventListener('click', function() {
		deleteFclt(data.fcltId);
	});
	return popup;
}

function addDragFeature(data) {
	const feature = mapManager.getVectorLayer('fclt').getSource().getFeatureById(data.fcltId);
	const imgSrc = createFcltMarker(feature.getProperties());
	
	const translate = new ol.interaction.Translate({
		features: new ol.Collection([feature]),
		style: new ol.style.Style({
			image: new ol.style.Icon({ scale: .9, src: imgSrc }),
			zIndex: 0
		})
	});
	
	mapManager.map.addInteraction(translate);

	console.log(feature.getGeometry().getCoordinates());
	translate.on('translateend', function(e) {
		console.log();
		if(confirm('현재 좌표로 저장하시겠습니까?')) {
			$('#fcltPopup').show();
			mapManager.map.removeInteraction(translate);
			const coordinate = new ol.proj.transform(feature.getGeometry().getCoordinates(),mapManager.properties.projection,mapManager.properties.pro4j[mapManager.properties.type]);
			$('#fcltLon').val(coordinate[0]);
			$('#fcltLat').val(coordinate[1]);
		}
	});
}

function addFclt(obj){
	var coordi = new ol.geom.Point(new ol.proj.transform([obj.coordinate[0],obj.coordinate[1]],mapManager.properties.projection,mapManager.properties.pro4j[mapManager.properties.type]));
	var page = "/common/popup/fcltAddPopup";
	common.openDialogPop('fcltPopup','시설물 추가팝업','800','350',true,'/action/page.do',{path : page},'fcltPopup',function(){
		if(obj.state != '' && obj.state != undefined){
			$('#fcltId').prop('disabled', true);
			$('#insertFclt').hide();
			$('#updateFclt').show();
			$('#moveCoord').show();
			$('#fcltId').val(obj.data.fcltId);
			$('#fcltNm').val(obj.data.fcltNm);
			$('#fcltLon').val(obj.data.lon);
			$('#fcltLat').val(obj.data.lat);
			$('#fcltPlace').val(obj.data.fcltPlace);
			$('#fcltKind option[value='+obj.data.kinds+']').attr('selected','selected');
			// fcltPurpose('base');
			$('#fcltPurpose option[value='+obj.data.purpose+']').attr('selected','selected');
			$('#fcltMgmtNo').val(obj.data.mgmtNo);
			//$('#fcltAreaCd option[value='+obj.data.areaCd+']').attr('selected','selected');
			mapManager.removeOverlayById('fclt');
			mapManager.removeOverlayById('fcltSelected');
			setMooveCoordEvent(obj.data);
		} else {
			$('#insertFclt').show();
			$('#updateFclt').hide();
			$('#moveCoord').hide();
			$('#fcltLon').val(coordi.getCoordinates()[0]);
			$('#fcltLat').val(coordi.getCoordinates()[1]);
			mapManager.searchDetailAddrFromCoords(obj.coordinate, function(result,status){
				if(status !== 'ZERO_RESULT') {
	        		$('#fcltPlace').val(result[0].address.address_name);
	    		} else {
	        		$('#fcltPlace').val('주소 검색 불가 지역');
	    		}
			})
		}
	})
}

function deleteFclt(data) {
	var deleteAlert = confirm('삭제 하시겠습니까?');
	if(deleteAlert){
		const jsonObj = {};
		jsonObj.fcltId = data;
		$.ajax({
		    contentType : "application/json; charset=utf-8",
			type		: "POST",
			url			: "/ajax/delete/facility.deleteFclt/action",
			data 		: JSON.stringify(jsonObj),
			dataType	: "json",
			async		: false
		}).done(function(result) {
			alert('삭제 완료');
			mapManager.removeOverlayById('fclt');
			mapManager.removeOverlayById('fcltSelected');
			selectFclt();
		}).fail(function (xhr){
			alert('삭제 실패');
		}).always(function() {
			
		});
	}
}

function openFcltConnectLine() {
	$('.connect-node-form').slideDown();
	$('#mainFcltId').focus().click();
	mapManager.removeMapEventListener('click', fcltMapClickListener);
}

function closeFcltConnectLine() {
	$('#mainFcltId').val('');
	$('#subFcltId').val('');
	$('.connect-node-form').slideUp();
	mapManager.removeMapEventListener('click', fcltConnectLineMain);
	mapManager.removeMapEventListener('click', fcltConnectLineSub);
	mapManager.setMapEventListener('click', fcltMapClickListener);
}

/**
 * 시설물 연결의 메인 시설물 선택용 이벤트 리스너
 * @function fcltMain.fcltConnectLineMain
 * @param {object} e -event
 */
function fcltConnectLineMain(e){
	mapManager.map.forEachFeatureAtPixel(e.pixel, function(feature, layer) {
		var fcltInfo = feature.getProperties();
		if(fcltInfo.featureKind != 'fclt') return;
		
		const subFcltId = $('#subFcltId').val();
		
		if(fcltInfo.fcltId === subFcltId) {
			alert('동일한 시설물은 선택 할 수 없습니다.');
			return;
		}
		
		$('#mainFcltId').val(fcltInfo.fcltId);
		customLine.setStart();
		customLine.draw();
	});
}

/**
 * 시설물 연결의 서브 시설물 선택용 이벤트 리스너
 * @function fcltMain.fcltConnectLineSub
 * @param {object} e -event
 */
function fcltConnectLineSub(e){
	mapManager.map.forEachFeatureAtPixel(e.pixel, function(feature, layer) {
		var fcltInfo = feature.getProperties();
		if(fcltInfo.featureKind != 'fclt') return;
		
		const mainFcltId = $('#mainFcltId').val();
		
		if(fcltInfo.fcltId === mainFcltId) {
			alert('동일한 시설물은 선택 할 수 없습니다.');
			return;
		}
		
		$('#subFcltId').val(fcltInfo.fcltId);
		customLine.setEnd();
		customLine.draw();
	});
}

function saveFcltConnectLine() {
	if(!checkNullFocus('mainFcltId',"메일 시설물을 선택해 주세요")) return;
	if(!checkNull('subFcltId',"메일 시설물을 선택해 주세요")) return;
	
	const mainFcltId = $('#mainFcltId').val();
	const subFcltId = $('#subFcltId').val();
	const lineString = customLine.getSaveLineString();
	
	/*const checkFcltId = (function() {
		let rtnVal;
		const jsonObj = {};
		jsonObj.fcltId = fcltId;
		$.ajax({
			type		: "POST",
			url			: "/select/facility.getCheckFcltId/action.do",
			dataType	: "json",
			data		: {"param" : JSON.stringify(jsonObj)},
			async		: false
		}).done(function(result) {
			const rows = result.rows;
			rtnVal = rows[0];
			
		}).fail(function (xhr) {
			
		}).always(function() {
			
		});
		return rtnVal;
	}());*/
	
	const jsonObj = {};
	jsonObj.mainFcltId = mainFcltId;
	jsonObj.subFcltId = subFcltId;
	jsonObj.lineString = lineString;
	
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type		: "POST",
		url			: "/ajax/insert/facility.saveFcltConnectLine/action",
		dataType	: "json",
		data 		: JSON.stringify(jsonObj),
		async 		: false,
	}).done(function() {
		alert('저장 완료');
		selectFclt();
		closeFcltConnectLine();
	}).fail(function(xhr) {
		alert('저장 실패');
	}).always(function() {
		
	});
}

/**
 * 라인 클릭 시 수정 기능
 * @param {object} - e 
 */
function fcltCnctLineClickListener(e) {
	mapManager.map.forEachFeatureAtPixel(e.pixel, function(feature, layer) {
		var layerTitle = feature.getProperties().layerTitle;
		if(layerTitle != undefined && layerTitle != '') {
			const coordinates = feature.getGeometry().getCoordinates();
			customLine2.layer = layer;
			
			if(true == e.originalEvent.ctrlKey) {
				if(confirm("라인을 삭제하시겠습니까?")) 
				{
					deleteLine(feature);
					$('#lineLayerHidden').jstree('deselect_node',layerTitle);
					$('#lineLayerHidden').jstree('select_node', layerTitle);
				}
			}
			else if(true  == e.originalEvent.altKey) {
				lineModify2.init();
			}
		}
	});
}


/**
 * 라인 추가 팝업 
 */
function addFcltCnctLinePopup() {
	const path = "fclt/popup/fcltCnctLinePopup";
	common.openDialogPopCenter("fcltCnctLinePopup", "선 종류 추가", "350", "auto", "true", "/action/page.do", {path: path}, path.split("/")[2]);
}

/**
 * 라인 수정 팝업 
 */
function updateFcltCnctLinePopup() {
	var data = $('#fcltCnctLineKind').val();
	if(data == '' || data == undefined) {alert("선 종류를 선택해주세요"); return false;}
	const path = "fclt/popup/fcltCnctLinePopup";
	common.openDialogPopCenter("fcltCnctLinePopup", "선 종류 수정", "350", "auto", "true", "/action/page.do", {path: path}, path.split("/")[2],'',data);
}

/**
 *  라인그리기 버튼 버큰클릭 후, 지도 클릭 시 라인 그려짐
 */
function addFcltCnctLine() {
	var layerTitle = $('#fcltCnctLineKind').val();
	if(layerTitle == '' || layerTitle == undefined ){ alert('선 종류를 선택해 주세요.'); return;}
	var layer = mapManager.getVectorLayer(layerTitle);
	var lineColor = getLineColor(layerTitle);
	fcltCnctLine(layerTitle, layer, lineColor);
}

/**
 * 라인컬러를 가져온다
 * @param {String} title - 컬러를 가져올 layer 타이틀 값
 * @returns
 */
function getLineColor(title) {
	const jsonObj = {};
	jsonObj.title = title;
	var lineColor;
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		url			: "/select/facility.getLayerCode/action",
		type		: "POST",
		dataType	: "json",
		data		: JSON.stringify(jsonObj),
		async		: false
	}).done(function (result){
		lineColor = result.rows[0].layerColor
	})
	return lineColor;
}

/**
 * 라인삭제기능
 * @param {object} feature -피쳐데이터
 */
function deleteLine(feature) {
	const jsonObj = {};
	var seqNo = feature.getProperties().no;
	jsonObj.seqNo = seqNo;
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		url			: "/ajax/delete/facility.deleteLine/action",
		type		: "POST",
		dataType	: "json",
		data		: {JSON.stringify(jsonObj),
		async		: false,
		success		: function(result) {
			alert('삭제가 완료되었습니다');
		},
		error		: function(error) {
			alert("삭제 실패")
		}
	})
}


