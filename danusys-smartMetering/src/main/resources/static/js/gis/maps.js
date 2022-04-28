/**
 *
 */
let baseProjection;
let baseExtent;
const resolutions = [2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25];
const daumEPSG = 'EPSG:5181';
const daumExtent = [-30000, -60000, 494288, 988576];

let map;

function distinct(value,index,self){
	return self.indexOf(value) === index;
}

function nvl(str, defaultStr){
	if(typeof str == "undefined" || str == null || str == ""){
		str = defaultStr ;
	}
	return str ;
}

const gis = {
	init : () => {
		$(document).contextmenu( e => {
			e.preventDefault();

//			if(e.target.className.indexOf('no_target')>-1){
//				return false;
//			}
		});

		olProjection.addProjection('EPSG:5181',"+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=500000 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs");
		const daumProjection = olProjection.createProjection(daumEPSG,daumExtent);

		baseProjection = daumProjection;
		baseExtent = daumExtent;

		const daumTileLayer = customTile.createDaumTile('imageTile','Daum Street Map',true);
		const daumSKY0TileLayer = customTile.createDaumTile('sky0Tile','Daum Sky0 Map', false);
		const daumLabelTileLayer = customTile.createDaumTile('labelTile','Daum Label Map', false);
//		const gmTopoLayer = customTile.createTile('topoMap','GM Topology Map',false);
//		const emdBoundaryLayer = customTile.createBoundaryTile();
		const baseAreaLayer = customTile.createTile('gm_basearea','baseAreaLayer',true);

		//배경지도레이어
		const baseLayer = new ol.layer.Group({
			name : 'baseLayer',
			layers : [
				daumTileLayer,
				daumSKY0TileLayer,
				daumLabelTileLayer,
//				gmTopoLayer
				baseAreaLayer
			]
		});


		let middleLayer = new ol.layer.Group({
			name: 'middleLayer',
			layers: []
		});

		let searchCCTVObj = {
			keyword : '',
			dongCdList : [],
			fcltUseCdList : [],
			usage : 'normal'
		};

		//마우스 커서 좌표
		const mousePositionControl = new ol.control.MousePosition({
			coordinateFormat: ol.coordinate.createStringXY(6), // 좌표 표시 포맷
			projection: "EPSG:4326", // 표출 좌표계
			className : 'custom-mouse-position',
			target: document.getElementById("mousePosition"), // 표출할 영역
			undefinedHTML:''
		});

		//맵 객체생성
		map = new ol.Map({
			target: 'map'
			,renderer: 'canvas'
			,interactions: ol.interaction.defaults({
				shiftDragZoom: true
			})
			,layers: [ baseLayer, middleLayer ]
			,view: new ol.View({
				projection : baseProjection
				,extent: daumExtent
				,resolutions: resolutions
				,maxResolution: resolutions[0]
				,center : new ol.proj.transform(baseMapCenter,'EPSG:4326',baseProjection)
				,zoom: 7
				,zoomFactor: 1
				,rotation: 0
			})
			,controls: [
				contextmenu,
				mousePositionControl
			]
		});

		comm.ajaxPost({
			type : 'post',
			url:'/account/getListAccountGeojson.ado',
			data: {},
			async: false
		}, resultData =>{

			let accountLayer = makeLayer.fromGeoJSON(resultData.data,'accountLayer', "", true);
			map.addLayer(accountLayer);
			layerControl.off(accountLayer);
		});

		comm.ajaxPost({
			type : 'POST',
			url : '/event/getListEventGeojson.ado',
			data : {},
			async : false,
		}, resultData => {
//			console.log(resultData.data);

			let eventLayer = makeLayer.fromGeoJSON(resultData.data,'eventLayer', "", true);
			map.addLayer(eventLayer);

		});

		comm.ajaxPost({
			type : 'POST',
			url : '/account/getListAccountCntInBaseArea.ado',
			data : {},
			async : false
		}, resultData => {

			let accountCntLayer = makeLayer.fromGeoJSON(resultData.data,'accountCntLayer', "", true);
			map.addLayer(accountCntLayer);

		});

		//맵 확대 & 축소
		$(".controlsButton .mapIconButton li").click(function(e) {

			const className = $(this).children().attr('class');

			const view = map.getView();
			const zoom = view.getZoom();

			if(className.indexOf('mapIconPlus')>-1){
				view.setZoom(zoom +1);
			} else if(className.indexOf('mapIconMinus')>-1){
				view.setZoom(zoom -1);
			}

		});

		//권한없는 CCTV 툴팁
//		popUp.create('cctvNamePopup');
		let target = map.getTarget();
		let jTarget = typeof target === "string" ? $("#" + target) : $(target);
		$(map.getViewport()).on('mousemove', e => {
//	 		popUp.hide('cctvNamePopup');
			const pixel = map.getEventPixel(e.originalEvent);
			const hit = map.forEachFeatureAtPixel(pixel, (feature, layer) => true);
			const cTarget = $(e.target);

			if (hit) {
				if(cTarget[0].tagName=="CANVAS"){
					jTarget.css("cursor", "pointer");
					//마우스 온 프리셋
					map.forEachFeatureAtPixel(pixel,(feature,layer) => {
						if(layer){
							if(layer.get("name")){

							}
						}
					});
				}
				map.renderSync();
			} else {
				jTarget.css("cursor", "all-scroll");
			}
		});

		//레이어 이름 모음집
		const layerNameObj = {'accountCntLayer':'수용가','accountLayer':'수용가','eventLayer':'이벤트'};

		//선택이벤트
		$(map.getViewport()).on('click', e => {

			const pixel = map.getEventPixel(e.originalEvent);
			let clickLayers = new Array();
			const clickObjs = {'accountCntLayer':[],'accountLayer':[],'eventLayer':[]};
			let flag = true;

			map.forEachFeatureAtPixel(pixel,(feature,layer) => {
				if(layer){
					const selectLayer = layer.get('name');

					clickLayers.push(selectLayer);
					if(clickObjs[selectLayer]){
						clickObjs[selectLayer].push(feature);
					} else {
						flag = false;
					}
				}

			});
			clickLayers = clickLayers.filter(distinct);
			if(clickLayers.length==0){}
			else if(clickLayers.length>1){
				if(flag){
					const layersContext = document.createElement('div');
					layersContext.id='layersContext';

					const layerList  = new ol.Overlay({
						element : layersContext,
						offset:[0,0],
						positioning: 'top-left'
					});

					let list ="";
					clickLayers.forEach( l => {
						list+="<li id='"+l+"'>"+layerNameObj[l]+"</li>";
					});

					layersContext.innerHTML =
						"<div><ul>" +
						list +
						"</ul></div>";

					map.addOverlay(layerList);
					layerList.setPosition(map.getEventCoordinate(e));

					$('#layersContext ul li').on('click', e => {
						popUp.remove('layersContext');
						const clickId = $(e.currentTarget).attr('id');
						clickIcon(clickId,clickObjs[clickId]);
					});
				}
			} else { //레이어 중복 아닐때
				const clickId=clickLayers[0];
				clickIcon(clickId,clickObjs[clickId]);
			}
		});

		// 배경변경
		$(".btnBaseMap").on('click', e => {
			const selectedBase = $(e.currentTarget).attr('id');

			if(selectedBase=='imageTile'){
				groupLayerControl.allOff('baseLayer');
				groupLayerControl.on('baseLayer','Daum Street Map');
			} else if(selectedBase=='satelliteTile'){
				groupLayerControl.allOff('baseLayer');
				groupLayerControl.on('baseLayer','Daum Sky0 Map');
				groupLayerControl.on('baseLayer','GM Satillite Map');
				groupLayerControl.on('baseLayer','Daum Label Map');
			} else if(selectedBase=='topoTile'){
				groupLayerControl.allOff('baseLayer');
				groupLayerControl.on('baseLayer','GM Topology Map');
			} else {}
		})

		// 측정
		$(".btnMeasure").on('click', e => {

			const selectedType = $(e.currentTarget).attr('id').substring(7).toLowerCase();
			if(!(selectedType=='erase')){
				measure.drawFeature(selectedType);
			} else {
				measure.erase();
			}
		});

		//최대 줌일때 노드 표출
		map.on('moveend', evt => {

			if($('.mapLayerButton li[data-type=accountLayer]').hasClass('active')){
				if( evt.map.getView().getZoom() > 11 ){
					layerControl.on('accountLayer');
					layerControl.off('accountCntLayer');
				} else {
					layerControl.on('accountCntLayer');
					layerControl.off('accountLayer');
				}
			}

		});

		map.getView().on('propertychange', event =>  {
			const key = String(event.key);
			//축척바
			const lv = map.getView().getZoom();
			let px = "";
			let meter = "";
			if(lv >= 13) { px = 76;  meter = '20 m';}
			else if(lv >= 12) { px = 56;  meter = '30 m';}
			else if(lv >= 11) { px = 46;  meter = '50 m';}
			else if(lv >= 10) { px = 46;  meter = '100 m';}
			else if(lv >= 9) { px = 58;  meter = '250 m';}
			else if(lv >= 8) { px = 58;  meter = '500 m';}
			else if(lv >= 7) { px = 58;  meter = '1 km';}
			else if(lv >= 6) { px = 58;  meter = '2 km';}
			else if(lv >= 5) { px = 58;  meter = '4 km';}
			else if(lv >= 4) { px = 58;  meter = '8 km';}
			else if(lv >= 3) { px = 58;  meter = '16 km';}
			else if(lv >= 2) { px = 58;  meter = '32 km';}
			else if(lv >= 1) { px = 58;  meter = '64 km';}
			else if(lv >= 0) { px = 58; meter = '128 km';}

			$('div.scaleBar .bar').css('width', px + 'px');
			$('div.scaleBar .meter').text(meter);

			if((key=="center") || (key=="resolution")) {
				popUp.hide('cctvNamePopup');

				popUp.remove('layersContext');
				contextmenu.clear();
				contextmenu.close();

			}
			map.renderSync();

		});

		$(map.getViewport()).on('mousedown', e => {
			if (e.which == 3) {
				let target = $(e.target);
				target = target[0].tagName;
				if (target == "CANVAS") {
					contextmenu.clear();
					contextmenu.extend(getContextMenuItem());
					displayContextmenu();
				}
			}
		});

		//맵 배경
		$('.controlMapArea .mapTypeButton li').click(function(e){
			const tileName = $(this).attr('data-type');
			if(tileName=='imageTile'){
				groupLayerControl.allOff('baseLayer');
				groupLayerControl.on('baseLayer','Daum Street Map');
				groupLayerControl.on('baseLayer','baseAreaLayer');
			} else if(tileName=='satelliteTile'){
				groupLayerControl.allOff('baseLayer');
				groupLayerControl.on('baseLayer','Daum Sky0 Map');
				groupLayerControl.on('baseLayer','Daum Label Map');
				groupLayerControl.on('baseLayer','baseAreaLayer');
			} else {}
		});




	} //gis init
} //gis



/*
 * 지도위 이벤트팝업 가려짐 방지 (팝업이 다 보이도록 맵 이동)
 * targetId : 다 보이도록 해야하는 팝업 id
 */
function moveMapForPopup(targetId){
	const mapX = $('#map').width();
	const mapY = $('#map').height();
	let target = $('#'+targetId);

	//팝업의 LeftTop
	const targetXY = target.offset();

	//팝업의 중심
	const targetCenterX = targetXY.left + (target.width()/2);
	const targetCenterY = targetXY.top + (target.height()/2);

	const center = map.getCoordinateFromPixel([targetCenterX, targetCenterY]);
	map.getView().setCenter(center);
}

/**
 * 지도위 팝업 컨텐츠 생성
 * @param objType
 * @param objProp
 */
function makePopupContent(objType,objProp) {

	//이벤트
	if(objType=='event'){

		$('#eventDetailInfo').show();
		$('#eventContainList').hide();
		map.renderSync();

		$('#eventDetailInfo .popupTitle dt').text(objProp.eventNo);

		let endDt = objProp.eventEndDt? objProp.eventEndDt : '-' ;

		if(objProp.step=="0"){
			endDt = "종료 미처리 <a data-type='"+objProp.eventLogSeq+"' class='btnEventEnd'>이벤트 종료하기</a>";
		}

		let content = //"<dl><dd> [" + objProp.eventLogSeq + "]</dd></dl>" +
			"<dl><dt>수용가 번호 </dt><dd>"+ objProp.accountNo +"</dd></dl>" +
			"<dl><dt>이벤트 그룹 및 이벤트명</dt><dd>"+ objProp.eventGroupName + " > " + objProp.eventName +"</dd></dl>" +
			"<dl><dt>시작일시</dt><dd>"+ objProp.eventStartDt +"</dd></dl>"+
			"<dl><dt>종료일시</dt><dd class='eventEndDt'>"+ endDt +"</dd></dl>"+
			"<dl><dt>위경도</dt><dd>"+ objProp.longitude + " , " + objProp.latitude +"</dd></dl>"
		;

		$('#eventDetailInfo .popupContents').empty();
		$('#eventDetailInfo .popupContents').append(content);

		//이벤트 종료 처리버튼
		$('#eventPopup .btnEventEnd').click(function(e){
			let eventSeq = $(e.currentTarget).attr('data-type');

			comm.confirm("정말 이벤트를 종료하시겠습니까?"
				, {}
				, () => {
					comm.ajaxPost({
						type : 'PATCH',
						url : '/event/modEventStep.ado',
						data : {'eventLogSeq' : eventSeq},
						async : false,
					}, resultData => {
						const data = resultData.data[0];
						if(resultData.data.length>0){

							$('#eventPopup .popupContents dd.eventEndDt').text(data.eventEndDt);
							$('#eventPopup #eventContainList #eventList'+data.eventLogSeq).data(data);

							const eventStepLi = $('#eventPopup #eventList'+data.eventLogSeq);
							const eventStepDiv = eventStepLi.find('.eventStep');
							eventStepDiv.text(data.stepNm);
							eventStepDiv.attr('class','eventStep eventStep'+data.step);

							eventStepLi.find('.eventState').attr('class','eventState eventState'+data.step);
							eventStepLi.find('#evtEndDt').text(' 종료일시 : ' + data.eventEndDt);

						}
					});
				}
			);

		});

		//수용가
	} else if(objType=='account') {

		$('#accountDetailInfo').show();
		$('#accountContainList').hide();
		$('#accountDetailInfo .popupTitle dt').text("[수용가] " + objProp.accountNo);

		let state = objProp.eventLogSeq ? 'stateYellow' : 'stateBlue';


		let lonLat = [objProp.gpsLongitude, objProp.gpsLatitude];

		//좌표로 수용가 주소
		let adObj = externalApi.getCoord2AddressData(ol.proj.transform(lonLat,"EPSG:4326",baseProjection));
		let address = "-";
		if(adObj.documents[0].address.address_name){
			address = adObj.documents[0].address.address_name;
			objProp.addressNm = address;
		}

		let content = "<h4><span class='state "+ state +"'></span>" + objProp.accountNo + " | " + objProp.accountNm + "</h4>" +
			"<dl><dt>상세주소</dt><dd>"+ objProp.fullAddr +"</dd></dl>" +
			"<dl><dt>주소</dt><dd>"+ address +"</dd></dl>" +
			"<dl><dt>업체명(코드)</dt><dd>"+ objProp.companyNm +"("+objProp.companyCd+")" +"</dd></dl>"+
			"<dl><dt>위경도</dt><dd>"+ objProp.gpsLongitude + " , " + objProp.gpsLatitude +"</dd></dl>"
		;

		if(objProp.eventLogSeq){
			content += "<dl class='eventDl'><dt>최근 이벤트</dt><dd>"+ objProp.eventGroupName +" > "+objProp.eventName +"</dd></dl>" +
				"<dl class='eventDl'><dt>최근 이벤트 번호</dt><dd>"+ objProp.eventNo +"</dd></dl>" +
				"<dl class='eventDl'><dt>최근 이벤트 시작</dt><dd>"+ objProp.eventStartDt +"</dd></dl>"
			;
		}

		$('#accountDetailInfo .popupContents').empty();
		$('#accountDetailInfo .popupContents').append(content);

		$('#accountDetail').data(objProp);

		$('#accountDetail').click(function(e){
			e.preventDefault();
			const obj = $(this).data();
			controlRightMenu.show(obj);
			$('#basicInfo').trigger('click');
		});

	}
}

//아이콘 클릭시
function clickIcon(id,clickObj){

	if(id=='accountCntLayer'){
		popUp.createForAccountGroup(clickObj[0],'accountPopup');
		$('#eventPopup').hide();

//		const objProp = clickObj[0].getProperties();
//		const prevPop = $('.popup_layer');
//		if(prevPop){
//			popUp.remove(prevPop.attr('id'));
//		}

//		if( clickObj[0].getProperties().nodeGroup != "" ){
//		} else {
//			comm.showAlert("수용가가 존재하지 않습니다.");
//			
//		}

	} else if(id=="accountLayer") {
		popUp.createForAccountGroup(clickObj[0],'accountPopup');
		$('#eventPopup').hide();
	} else if(id=="eventLayer") {
		popUp.createForEventGroup(clickObj[0],'eventPopup');
		$('#accountPopup').hide();
		$('#rnmCloser').trigger('click');

	}

}


/**
 * 팝업조작 (overlay)
 */
const popUp = {
	//팝업생성
	create : id => {
		const commonPopupElement = document.createElement('div');
		commonPopupElement.id = id;
		commonPopupElement.className = 'my-ol-popup';

		const commonPopup = new ol.Overlay({
			element: commonPopupElement,
			offset:[0,-15],
			positioning: 'bottom-center'
		});

		commonPopupElement.innerHTML = "<a id='"+id+"Closer' class='my-ol-popup-closer'></a>"+
			"<div id="+id+"Content></div>";

		map.addOverlay(commonPopup);
		commonPopup.setPosition(undefined);

		$(".my-ol-popup-closer").on('click', e => {
			popUp.remove(id);
			map.getInteractions().forEach( e => {} );
		});

	}
	//이벤트 팝업용 
	, createForEventGroup : (featureData,id) => {
		$('#eventPopup').show();

		const prop = featureData.getProperties();
		const coordinates = featureData.getGeometry().getCoordinates();

		const eventNmAry = prop.eventNmList.split(',');
		const eventSeqAry = prop.eventSeqList.split(',');

		let eventObj;

		comm.ajaxPost({
			type : 'POST',
			url : '/event/getListEventGIS.ado',
			data : {'eventSeqAry': eventSeqAry},
			async : false,
			showLoading : false
		}, resultData => {

			eventObj = resultData.data;
		});

		const commonPopupElement = document.createElement('div');
		commonPopupElement.id = 'eventPopup';
		commonPopupElement.className = 'popup_wrap popup_layer popup_custom';

		const commonPopup = new ol.Overlay({
			element: document.querySelector("#eventPopup"),
			offset:[0,-15],
			positioning: 'bottom-center'
		});

		map.addOverlay(commonPopup);

		// 이벤트 하나 클릭 (이벤트정보팝업)
		if(eventObj.length==1){

			$('#eventDetailPopupPrev').hide();
			makePopupContent('event',eventObj[0]);


			// 이벤트 그룹 클릭 (리스트팝업 ->이벤트정보팝업)
		} else if(eventObj.length>1){

			$('#eventContainList').show();
			$('#eventContainList .popupTitle dt').text("수용가 " + prop.accountNo);


			$('#eventDetailPopupPrev').show();

			$('#eventContainList .popupContents ul').empty();
			eventObj.forEach(function(e){

				let content = "<li id='eventList"+ e.eventLogSeq +"'>" +
					"<div class='eventStep eventStep" + e.step + "'>" + e.stepNm + "</div>" +
					"<span class='spanDiv'><span class='eventState eventState"+e.step+"'></span>" + e.eventGroupName + " > " + e.eventName + "</span>" +
					"<span class='spanDiv spanDt'> 시작일시 : " + e.eventStartDt +"</span>" +
					"<span class='spanDiv spanDt' id='evtEndDt'> 종료일시 : " + e.eventEndDt +"</span>" +
					"</li>"
				;
				$('#eventContainList .popupContents ul').append(content);
				$('#eventContainList .popupContents ul li').last().data(e);
			});

			$('#eventContainList .popupContents ul li').click(function(e){
				e.preventDefault();
				makePopupContent('event', $(this).data());
			});

			$('#eventDetailInfo').hide();

		} else {}

		//팝업 디테일 접기
		$('#eventDetailPopupPrev').click(function(e){
			$('#eventContainList').show();
			$('#eventDetailInfo').hide();
		});

		//팝업 닫기
		$('#eventPopupCloser').click(function(e){
			popUp.hide('eventPopup');
			$('#eventDetailInfo').hide();
		});

		//팝업 디테일 닫기
		$('#eventDetailPopupCloser').click(function(e){
			$('#eventDetailInfo').hide();
		});

		commonPopup.setPosition(coordinates);
		map.renderSync();

		moveMapForPopup('eventPopup');

	}

	//수용가 팝업
	, createForAccountGroup : (featureData,id) => {

		$('#accountPopup').show();
		$('#accountDetailInfo').hide();
		$('#rnmCloser').trigger('click');

//		popUp.remove(id);
		const properties = featureData.getProperties();
		const coordinates = featureData.getGeometry().getCoordinates();

		const commonPopupElement = document.createElement('div');
		commonPopupElement.id = 'accountPopup';
		commonPopupElement.className = 'popup_wrap popup_layer popup_custom';

		const commonPopup = new ol.Overlay({
			element: document.querySelector("#accountPopup"),
			offset:[0,-15],
			positioning: 'bottom-center'
		});

		map.addOverlay(commonPopup);

		let nodeGroupAry;
		if(properties.nodeGroup){
			nodeGroupAry = properties.nodeGroup.split(',');
		} else {
			nodeGroupAry = [properties.accountNo];
		}

		let eventByAccount;

		comm.ajaxPost({
			type : 'POST',
			url : '/event/getListLastestEventByAccount.ado',
			data : {'nodeGroup': nodeGroupAry},
			async : false,
			showLoading : false
		}, resultData => {
			eventByAccount = resultData.data;
		});

		let clickLength = eventByAccount.length;

		//수용가 하나 클릭 (수용가정보팝업)
		if(clickLength==1){

			makePopupContent('account', eventByAccount[0]);
			$('#accountPopupPrev').hide();
			map.renderSync();

			//수용가 그룹 클릭 (리스트팝업 ->수용가정보팝업)
		} else if(clickLength>1) {
			$('#accountContainList').show();
			$('#accountPopupPrev').show();
			$('#accountContainList .popupContents ul').empty();
			$('#accountContainList .popupTitle dt').text(properties.sigKorNm + " - " + properties.basMgtSn);

			eventByAccount.forEach(function(e){
				let content = "";

				let color = 'Green';
				if(e.eventGroupCode=='2'){
					color = 'Blue';
				}

				if(e.eventLogSeq){
					content = "<li>"+
						"<span><span class='state stateYellow'></span>"+ e.accountNo +"</span>"+
						"<span class='category category"+color+"'>" + e.eventGroupName + "</span>"+
						"</li>";
				} else {
					content = "<li>"+
						"<span><span class='state stateBlue'></span>"+ e.accountNo +"</span>"+
						"</li>";
				}

				$('#accountContainList .popupContents ul').append(content);
				$('#accountContainList .popupContents ul li').last().data(e);
			});

			//팝업 닫기
			$('#accountPopupCloser').click(function(e){
				popUp.hide('accountPopup');
				$('#rnmCloser').trigger('click');
				$('#accountDetailInfo').hide();
			});

			//팝업 숨기기
			$('#accountPopupPrev').click(function(e){
				$('#accountDetailInfo').hide();
				$('#rnmCloser').trigger('click');
				$('#accountContainList').show();
				map.renderSync();
			});

			//팝업 수용가 클릭
			$('#accountContainList .popupContents ul li').click(function(e){
				e.preventDefault();
				$('#rnmCloser').trigger('click');

				makePopupContent('account', $(this).data());
				map.renderSync();

			});

		}

		$('#accountDetailPopupCloser').click(function(e){
			popUp.hide('accountPopup');
			$('#accountDetailInfo').hide();
			$('#rnmCloser').trigger('click');
		});


		commonPopup.setPosition(coordinates);
		map.renderSync();
		moveMapForPopup('accountPopup');



	}
	//해당 팝업 제거하기
	,remove: id => { // popup 자체 삭제
		map.getOverlays().forEach( overlay => {
			const elementId = overlay.getElement().id;
			if(elementId){
				if(id=="centerCctvPopup") {
					$("#"+elementId).hide();
					if($("#"+elementId).is(":visible")) {
						cctv.centerViewClose();
					}
				} else {
					if(elementId==id){
						map.removeOverlay(overlay);
					}
				}
			}
		});
	}
	/**
	 * 해당 팝업 좌표 이동
	 * @param id 이동할 팝업객체 이름
	 * @param position 이동할 좌표(베이스 지도좌표계 좌표)
	 */
	,move: (id,position) => {
		map.getOverlays().forEach( overlay => {
			const elementId = overlay.getElement().id;
			if(elementId==id){
				overlay.setPosition(position);
			}
		});
	}
	/**
	 * 팝업 객체 찾기
	 * @param id 찾을 팝업 아이디
	 * @return 해당 아이디 팝업 객체
	 */
	,find:  id => {
		let	foundOverlay;
		map.getOverlays().forEach( overlay => {
			const elementId = overlay.getElement().id;
			if(elementId==id){
				foundOverlay = overlay;
			}
		});
		return foundOverlay;
	}
	/**
	 * 팝업 존재여부 판단
	 * @param id 판단할 팝업 아이디
	 * @return boolean (존재(true)/미존재(false))
	 */
	,exist:  id => {
		let	flag = false;
		map.getOverlays().forEach( overlay => {
			const elementId = overlay.getElement().id;
			if(elementId==id){
				flag = true;
			}
		});
		return flag;
	}
	//팝업 잠시 숨기기
	,hide:  id => {
		map.getOverlays().forEach( overlay => {
			const elementId = overlay.getElement().id;
			if(elementId==id){
				overlay.setPosition(undefined);
			}
		});
	}
	/**
	 * 팝업 내용 변경
	 * @param id 변경할
	 * @param content 변경할 팝업 내용(html)
	 */
	,content : (id,content) => $("#"+id+"Content").html(content)

}

/**
 * map anumation
 * */
var bounce_amt = 5;
var a_amt = (2*bounce_amt+1) * Math.PI/2;
var b_amt = -0.01;
var c_amt = -Math.cos(a_amt) * Math.pow(2, b_amt);
ol.easing.bounce = function(t) {
	t = 1-Math.cos(t*Math.PI/2);
	return (1 + Math.abs( Math.cos(a_amt*t) ) * Math.pow(2, b_amt*t) + c_amt*t)/2;
}
function pulseFeature(coord){
	var f = new ol.Feature (new ol.geom.Point(coord));
	f.setStyle (new ol.style.Style({
		image: new ol.style["Circle"] ({
			radius: 30,
			points: 4,
			stroke: new ol.style.Stroke ({ color: "#ff0000", width:5 })
		})
	}));
	map.animateFeature (f, new ol.featureAnimation.Zoom({
		fade: ol.easing.easeOut,
		duration: 500,
		easing: ol.easing["easyOut"] //bounce 
	}));
}

function pulse(coord) {
	var nb = 3; //bounce==1;
	for (var i=0; i<nb; i++) {
		setTimeout (function() {
			pulseFeature(coord);
		}, i*500);
	};
}



/*
 * 관제 오른쪽 팝업
 * 
 * */
const controlRightMenu = {
	show : (obj) => {
		$('.controlRNM').css('display','flex');
		$('#rnmCloser').click(function(e){
			e.preventDefault();

			$('.controlRNM').css('display','none');
			$('.rnmTabButton li').removeClass('active');
			$('.rnmContents').css('display','none');

			$('#basicInfoRNM').css('display','flex');
			$('#basicInfo').addClass('active');
		});


		//관제 오른쪽 메뉴 탑메뉴
		$('.controlRNM .rnmTabButton li').click(function(e){

			e.preventDefault();

			const targetId = e.currentTarget.id;
			$('.rnmTabButton li').removeClass('active');
			$('.rnmContents').css('display','none');

			$('#'+targetId+"RNM").css('display','flex');
			$('#'+targetId).addClass('active');



			if(targetId=="eventLog"){
				$('#eventLogRNM .logListWrap').empty();

				comm.ajaxPost({
					type : 'POST',
					url : '/event/getListEventLog.ado',
					data : {accountNo : obj.accountNo},
					async : false,
					showLoading : false
				}, resultData => {
					let content="";

					resultData.data.forEach(function(e){

						let start = e.eventStartDt ? e.eventStartDt : '-';
						let end = e.eventEndDt ? e.eventEndDt : '-';

						let color = 'Green';
						if(e.eventGroupCode=='2'){
							color = 'Blue';
						}
						content += "<div class='logList'> "+
							"<p><span class='circle circle"+ color +"'></span></p>"+
							"<dl>"+
							"<dt><span class='subtitle subtitle"+ color +"'>"+e.eventGroupName+"</span><img src='/images/common/iconNext.svg'>"+e.eventName+"</dt>"+
							"<dd>이벤트 번호 : "+ e.eventNo +"</dd>" +
							"<dd>발생 일시 : "+ start +"</dd>" +
							"<dd>종료 일시 : "+ end +"</dd>" +
							"</dl>" +
							"</div>"
						;

					});

					if(resultData.data.length < 1){
						content += "<div class='logList'> "+
							"<p><span class='circle circleGray'></span></p>"+
							"<dl>"+
							"<dt><span class='subtitle subtitleGray'>이벤트 그룹</span><img src='/images/common/iconNext.svg'>이벤트 이름</dt>"+
							"<dd>이벤트 번호 : 정보없음</dd>" +
							"<dd>발생 일시 : 정보없음</dd>" +
							"<dd>종료 일시 : 정보없음</dd>" +
							"</dl>" +
							"</div>"
						;
					}

					$('#eventLogRNM .logListWrap').append(content);

				});
			} else if(targetId=="basicInfo"){


				let color ='Blue';
				if(obj.eventGroupCode){
					color = 'Yellow';
				}
				$('#basicInfoRNM .rnmLocation dt').empty();
				$('#basicInfoRNM #accountAddress').empty();

				$('#basicInfoRNM .rnmLocation dt').append("<span class='state state" + color +"'></span>" + obj.accountNo);
				$('#basicInfoRNM #accountAddress').append("<span><img src='/images/common/iconLocation.svg'></span>" + obj.addressNm);


				$('.rnmBox[id!=eventInfo] li').each(function(i,v){
					const span = $(this).find('span').not('.subtitle');
					span.text(obj[span.attr('id')]);
				});

				if(obj.eventLogSeq != null){
					///이벤트
					$('#eventInfo span[id]').each(function(i,v){
						$(this).text(obj[$(this).attr('id')]);
					});
					$('#eventInfo').css('display','block');
				} else {
					$('#eventInfo').css('display','none');
				}


			} else if(targetId=="deviceDetail"){

				$('#deviceDetailRNM .rnmBox li').each(function(i,v){
					const span = $(this).find('span').not('.subtitle');
					span.text(obj[span.attr('id')]);
				});

			} else if(targetId=="accountStats"){

				let color ='Blue';
				if(obj.eventGroupCode){
					color = 'Yellow';
				}
				$('#accountStatsRNM .rnmLocation dt').empty();
				$('#accountStatsRNM #accountAddress').empty();

				$('#accountStatsRNM .rnmLocation dt').append("<span class='state state" + color +"'></span>" + obj.accountNo);
				$('#accountStatsRNM #accountAddress').append("<span><img src='/images/common/iconLocation.svg'></span>" + obj.addressNm);

				// 이벤트 통계
				comm.ajaxPost({
					type : 'POST',
					url : '/event/getListEventDataStats.ado',
					data : {accountNo : obj.accountNo},
					async : false,
					showLoading : false
				}, resultData => {
					const statsInfo = resultData.data;

					$('#eventCntStats dd').text(0);

					statsInfo.forEach(function(e){
						$('#eventCntStats #group'+e.eventGroupCode).text(e.cnt);
					});


				});

				// 수용가 정보
				comm.ajaxPost({
					type : 'POST',
					url : '/account/getListAccountDataStats.ado',
					data : {accountNo : obj.accountNo},
					async : false,
					showLoading : false
				}, resultData => {
					const accountDataAry = resultData.data;
					const length = accountDataAry.length;

					$('#accountMinMax #maxUseVal').text('0');
					$('#accountMinMax #minUseVal').text('0');
					$('#accountStatsRNM table#datatablesUser tbody').empty();

					if(length>1){
						accountDataAry.forEach(function(v,i){
							if((i+1)==length){
								$('#accountMinMax #maxUseVal').text(v.meterDtm);
								$('#accountMinMax #minUseVal').text(v.dailyValue);
							} else {
								const content = "<tr>"+
									"<td>"+v.meterDtm+"</td>"+
									"<td>"+v.dailyValue+"</td>"+
									"<td>"+v.termBatt+"</td>"+
									"</tr>";
								$('#accountStatsRNM table#datatablesUser tbody').append(content);

							}

						}); //accountDataAry
					} //1개아닐시 

				}); //post


			}


		});





	}
}



