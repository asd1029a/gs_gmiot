/**
 * ptzfSet 관련 모음
 * @namespace ptzfSet
 * */

/**
 * 카메라(CCTV) 데이터를 불러오는 함수
 * @function ptzfSet.selectCctvFclt
 * */
function selectCctvFclt() {
	if(typeof(grid) != "undefined") grid.destroy();
	
	const jsonObj = {};
	//onObj.fcltAgCd = $('#selectFcltAgCombo').combobox('getValue');
	jsonObj.fcltAgCd = $('#agSelect').val();
	jsonObj.fcltSpace = areaCode;
	jsonObj.purposeSpace = useCode;
	jsonObj.userId = opener.document.getElementById('loginId').value;
	jsonObj.pageKind = 'manage';
	
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
		url 		: "/select/facility.selectFcltSList/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async       : false
	}).done(function(result) {
		const rows = result.rows;
		var itemsOnPage = 10;
		$("#ptzCctvListPagination").simplePagination({
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
	});
}

function pageClick(pagenumber, event, data, itemsOnPage) {
	const length = data.length;
	var max = pagenumber * itemsOnPage;
	max = max <= length ? max : length;
	var cnt = (pagenumber - 1) * itemsOnPage;
	
	$('#listCount').text(length);

	$('#ptzCctvList').html('');
	
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
			mapManager.setCenter(ol.proj.transform([temp.lon, temp.lat], mapManager.properties.pro4j[mapManager.properties.type],mapManager.properties.projection));
			setFcltPopup(temp);
			dialogManager.sortDialog();
		});
		
		$('#ptzCctvList').append(inner);
	}
}

/**
 * 프리셋 설정 좌측 row 선택시 지도 위에 영상 팝업을 그리는 함수
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

function ptzfSetMapPointermoveListener(evt) {
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
 * 시설물 팝업생성
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
	ul.classList.add('flex');
	ul.classList.add('jc_between');
	
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