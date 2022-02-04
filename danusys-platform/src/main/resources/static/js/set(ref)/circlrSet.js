/**
 * 저장할 카메라 배열 데이터
 * @memberof circlrSet
 * @property {Array} circlrSet.circlrDatas
 * */
var circlrDatas = [];


/**
 * 선택된 카메라 데이터 영상을 재생하는 함수
 * @function circlrSet.playCirclrTableData
 * @param {element} obj - 테이블 row element
 * @param {object} data - 카메라 데이터 
 * */
function playCirclrTableData(obj, data) {
	$('#circlrSetTable td').removeClass('active');
	$(obj).addClass('active');
	setCirclrSetVideo(data)
}

/**
 * 순환김시 리스트 데이터를 불러오는 함수
 * @function circlrSet.selectCirclrGrpList 
 * */
function selectCirclrGrpList() {
	
	const jsonObj = {};
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url 		: "/select/facility.selectCirclrGrpList/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async      : false
	}).done(function(result) {
		const rows = result.rows;
		var itemsOnPage = 10;
		$("#circlrGrpListPagination").simplePagination({
			items: rows.length,
			itemsOnPage : itemsOnPage,
			displayedPages : 5,
			edges : 0,
			prevText : "<",
			nextText : ">",
			cssStyle: 'custom-theme',
			onInit : function (){
				grpPageClick(1, undefined, rows, itemsOnPage);
			},
			//클릭할때
			onPageClick :  function (pagenumber , event) {
				grpPageClick(pagenumber, event, rows, itemsOnPage);
			}
		});
	});
}

function grpPageClick(pagenumber, event, data, itemsOnPage) {
	const length = data.length;
	var max = pagenumber * itemsOnPage;
	max = max <= length ? max : length;
	var cnt = (pagenumber - 1) * itemsOnPage;
	
	$('#circlrGrpList').html('');
	
	for(var i = cnt; i < max; i++) {
		const temp = data[i];
		var inner = document.createElement('dl');
		var i1 = document.createElement('dt');
		var i1_1 = document.createElement('span');
		var i1_2 = document.createElement('span');
		var i2 = document.createElement('dd');
		var i3 = document.createElement('dd');
		var i4 = document.createElement('dd');
		
		i1_1.classList.add('count');
		i1_2.classList.add('event');
		i1_2.id = 'grpNo';
		
		i1.appendChild(i1_1);
		i1.appendChild(i1_2);
		
		i1_1.innerHTML = temp.rnum;
		i1_2.innerHTML = temp.no;
		i2.innerHTML = temp.information;
		i3.innerHTML = temp.manager;
		i4.innerHTML = temp.monitorArea;
		
		inner.appendChild(i1);
		inner.appendChild(i2);
		inner.appendChild(i3);
		inner.appendChild(i4);
		
		$(inner).bind('click', function() {
			$('.srch-list dl.active').removeClass('active');
			$(this).addClass('active');
			selectCirclrDtlList($(this).find('span[id="grpNo"]').text());
			
			$('#circlrNo').val(temp.no);
			$('#circlrNm').val(temp.monitorArea);
			$('#circlrMngr').val(temp.manager);
			$('#circlrInfo').val(temp.information);
		});
		
		$('#circlrGrpList').append(inner);
	}
}

/**
 * 순환감시 상세정보(CCTV정보) 리스트 데이터를 불러오는 함수
 * @function circlrSet.selectCirclrGrpList 
 * */
function selectCirclrDtlList(no) {
	
	const jsonObj = {};
	jsonObj.no = no;
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url 		: "/select/facility.selectCirclrDtlCctv/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async      : false
	}).done(function(result) {
		const rows = result.rows;
		$('#circlrDtlList').html('');
		
		for(var i = 0; i < rows.length; i++) {
			const temp = rows[i];
			var inner = document.createElement('dl');
			var i1 = document.createElement('dt');
			var i1_1 = document.createElement('span');
			var i1_2 = document.createElement('span');
			var i2 = document.createElement('dd');
			var i3 = document.createElement('dd');
			
			i1_1.classList.add('count');
			i1_2.classList.add('event');
			i1_2.id = 'grpNo';
			
			i1.appendChild(i1_1);
			i1.appendChild(i1_2);
			
			i1_1.innerHTML = temp.rnum;
			i1_2.innerHTML = temp.mgmtNo;
			i2.innerHTML = temp.cctvName;
			i3.innerHTML = temp.fcltPuposeNm;
			
			inner.appendChild(i1);
			inner.appendChild(i2);
			inner.appendChild(i3);
			$(inner).bind('click', function() {
				mapManager.setCenter(ol.proj.transform([temp.lon, temp.lat], mapManager.properties.pro4j[mapManager.properties.type],mapManager.properties.projection));
			});
			
			$('#circlrDtlList').append(inner);
		}
		
		circlrDatas = rows;
	});
}


/**
 * 카메라(CCTV) 리스트 데이터를 불러오는 함수
 * @function circlrSet.selectCirclrGrpList 
 * */
function selectCirclrCctvList() {
	const jsonObj = {};
	jsonObj.pageKind = 'manage';
	jsonObj.menuNm = "facility"
	jsonObj.firstIndex = "";
	jsonObj.fcltAgCd = $('#agSelect').val();
	
	jsonObj.fcltSpace = areaCode;
	jsonObj.purposeSpace = useCode;
	jsonObj.userId = opener.document.getElementById('loginId').value;
	
	
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
		async      : false
	}).done(function(result) {
		const rows = result.rows;
		var itemsOnPage = 10;
		$("#circlrCctvListPagination").simplePagination({
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

function cctvPageClick(pagenumber, event, data, itemsOnPage) {
	const length = data.length;
	var max = pagenumber * itemsOnPage;
	max = max <= length ? max : length;
	var cnt = (pagenumber - 1) * itemsOnPage;
	
	$('#cctvListCount').text(length);

	$('#circlrCctvList').html('');
	
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
			setCirclrSetVideo(temp);

			$('#circlrSetTable td').removeClass('active');
		});
		
		$('#circlrCctvList').append(inner);
	}
}

function setCirclrSetVideo(data) {
	if(!videoManager.isPlaying(data.fcltId)) {
		return;
	}
	
	mapManager.removeOverlayById('circlrSetSelected');
	$('#setCirclr .cctv-layer-view .view').remove();
	
	const wrap = $('<div>').addClass('view');
	
	$('#setCirclr .cctv-layer-view').prepend(wrap);
	
	const position = ol.proj.transform([data.lon, data.lat], mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection);
	
	setCircleSetSelectedOverlay(position);
	
	const option = {};
	option.data = data;
	option.parent = wrap;
	option.isDetail = false;
	option.isButton = false;

	if(!videoManager.createPlayer(option)) {
		dialogManager.close(dialog);
	};
}

/**
 * 순환감시 설정 화면을 여는 함수
 * @function circlrSet.openCirClrGrp
 * @param {string} flag - 분류 값 (edit : 수정)
 * */
function openCirclrGrp(flag) {
	if(flag == "edit") {
		if($('.srch-list dl.active').length == 0) {
			alert('선택된 목록이 없습니다.');
			return false;
		}
		$('#setCirclr .btn-delete').show();
		setCirclrDatas();
	}
	else {
		$('#setCirclr .btn-delete').hide();
		clearCirclrSetting();
	}
	
	$('#cctvList').css('display','block');
	selectCirclrCctvList();
	
}

function closeCirclrCctv() {
	clearCirclrSetting();
	$('#cctvList').css('display','none');
}

/**
 * 순환감시 테이블에 현재 선택된 카메라 정보를 표시하는 함수
 * @function circlrSet.setCirclrData
 * @param {elemet} obj - row element
 * @param {object} data - 카메라 데이터
 * @param {index} index - row index
 * */
function setCirclrData(obj, data, index) {
	if (videoManager.playList.size == 0 || $('#circlrSetTable td.active').length == 1) {
		alert('선택된 카메라가 없습니다.');
		$(obj).text((index+1)).removeClass('selecte');
		return;
	}

	$('#circlrSetTable td').removeClass('active');
	circlrDatas[index] = common.clone(data);
	$(obj).text(circlrDatas[index].mgmtNo).addClass('selecte').addClass('active');
}

/**
 * 순환감시 설정된 데이터를 수정 할 때 표출되어 있는 정보를 clear 하는 함수
 * @function circlrSet.clearCirclrSetting
 * */
function clearCirclrSetting() {
	$('#circlrNo').val('');
	$('#circlrNm').val('');
	$('#circlrMngr').val('');
	$('#circlrInfo').val('');
	$('.srch-list dl.active').removeClass('active');
	$('#circlrDtlList').html('');
	
	const view = $('<div>').addClass('view');
	
	$('#setCirclr .cctv-layer-view .view').remove();
	$('#setCirclr .cctv-layer-view').append(view)
	
	$('#circlrSetTable td').removeClass('active').removeClass('selecte').html((i) => i + 1);
	
	mapManager.removeOverlayById('circlrSetSelected');

	circlrDatas = [];
}

/**
 * 순환감시 수정 시 해당 리스트에 담겨있는 데이터를 1~9번 영역에 표출해주는 함수
 * @function circlrSet.setCirclrDatas
 * @param {Array} datas - 카메라 정보 배열
 * */
function setCirclrDatas() {
	for(var i in circlrDatas) {
		if (circlrDatas[i].nodeId == null) {
			circlrDatas[i] = undefined; 
			continue;
		}
        
        $($('#circlrSetTable td')[i]).text(circlrDatas[i].mgmtNo).addClass('selecte');
	}
}

/**
 * 순환감시 정보를 삭제하는 함수
 * @function circlrSet.deleteCirclrGrp()
 * */
function deleteCirclrGrp() {
	if(confirm("삭제하시겠습니까?")) {
		const jsonObj = {};
		jsonObj.singleDeleteSid = "facility.deleteCircularGroup";
		jsonObj.no = $('#circlrNo').val();
		
	    $.ajax({
	            contentType : "application/json; charset=utf-8",
	            type        : "POST",
	            url         : "/multiAjax/action",
	            dataType    : "json",
	            data : {"param" : JSON.stringify(jsonObj)},
	            async      : false,
	            beforeSend : function(xhr) {
	                // 전송 전 Code
	            }
	        }).done(function (result) {
	    		console.log(result);
	        if (result == "SUCCESS") {
	            alert("삭제 완료");
	            selectCirclrGrpList();
	            closeCirclrCctv();
	        }
	        else {
	            alert("삭제 실패");
	        }
	    }).fail(function (xhr) {
	        alert("삭제 실패");
	    }).always(function() {

	    });
	}
}

/**
 * 순환감시 정보를 저장하는 함수
 * @function circlrSet.saveCirclrGrp
 * */
function saveCirclrGrp() {
	var circlrName = $('#circlrNm').val();
	
	if(!checkNull(circlrName, "감시지역을 입력해주십시오.")){
		return false;
	}
	
	const jsonObj = {};
	jsonObj.no = $('#circlrNo').val();
	jsonObj.monitorArea = circlrName;
	jsonObj.manager = $('#circlrMngr').val();
	jsonObj.information = $('#circlrInfo').val();
	
	for(var i = 0; i < circlrDatas.length; i++) {
		if (typeof circlrDatas[i] != 'undefined') {
			eval("jsonObj.cctvId"+(i+1)+"='"+circlrDatas[i].fcltId+"'");
			eval("jsonObj.cctvName"+(i+1)+"='"+circlrDatas[i].fcltNm+"'");
		} else {
			eval("jsonObj.cctvId"+(i+1)+"=''");
			eval("jsonObj.cctvName"+(i+1)+"=''");
		}
	}
	
	jsonObj.singleInsertSid = "facility.saveCircularGroup";
	
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
		console.log(result);
		if (result == "SUCCESS") {
			alert("등록 완료");
			selectCirclrGrpList();
			closeCirclrCctv()
		}
		else {
			alert("등록 실패");
		}
	}).fail(function (xhr) {
		alert("등록 실패");
	}).always(function() {
		
	});
}

function circlrSetMapPointermoveListener(evt) {
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
				if(!$('#setCirclr').is(':visible')) {
					commonLog.error('순환감시 추가, 수정 시에만 영상이 재생됩니다.', {isAutoClose: true});
					return;
				}
				
				setCirclrSetVideo(obj);

				$('#circlrSetTable td').removeClass('active');
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

function setCircleSetSelectedOverlay(position) {
	const content = createCircleSetSelectedOverlay();
	const option = {
		id : 'circlrSetSelected',
		position : position,
		element : content,
		offset : [ 0, 0 ],
		positioning : 'center-center',
		stopEvent : false,
		insertFirst : true
	}
	
	return mapManager.setOverlay(option);
}

function createCircleSetSelectedOverlay() {
	let wrap = document.createElement('div');
	
	wrap.classList.add('dialog-selected-overlay');
	
	let img = document.createElement('img');
	img.src = '/images/icons/dialog_selected.gif';
	
	wrap.appendChild(img);
	
	return wrap;
}