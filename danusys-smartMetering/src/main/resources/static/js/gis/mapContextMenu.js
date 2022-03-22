/**
 * 
 */


const thisCoord = e => {
	groupLayerControl.remove('middleLayer','addressMarker');
	
	if(popUp.exist('addressPopup')){
		popUp.remove('addressPopup');
	}
	
	const r = externalApi.getCoord2AddressData(e.coordinate);
	const clickPoint = e.coordinate;
	//console.log(clickPoint);
	map.getLayers().forEach( layer => {
		if(layer.get('name')=='addressMarker'){
			map.removeLayer(layer);
			$(".address-tooltip").parent().remove();
		}
	});
	
	const addressMarker = new ol.layer.Vector({
		name: 'addressMarker',
		source: new ol.source.Vector({
			features: [new ol.Feature({
				geometry: new ol.geom.Point(clickPoint),
				visible: true
			})]
		}),
		style : new ol.style.Style({
			image: new ol.style.Icon({
				anchor:[0.5,0.9],
				anchorXUnits:'fraction',
				anchorYUnits:'fraction',
				offset:[0,0],
				scale: 1.5,
				src: "/images/common/iconPin.svg"
			})
		})
	});

	const addressPopupElement = document.createElement('div');
	const clickLonLat = new ol.proj.transform(clickPoint,baseProjection,'EPSG:4326');
	addressPopupElement.id = 'addressPopup';
	addressPopupElement.className = 'address-tooltip';
	
	const addressPopup = new ol.Overlay({
		name: 'addressPopup',
		element : addressPopupElement,
		offset:[0,-50],
		positioning:'bottom-center'
	});
	
	let content = r.documents[0].address.address_name;
	if((r.documents[0].road_address)!=null){
		content += "<div class='address-tooltip-header'>[도로명]</div>"
			+ "<p>" + r.documents[0].road_address.address_name + "<p>"  ;
	}
	addressPopupElement.innerHTML = 
		"<a class='address-tooltip-closer icon_closed'></a></div>" 
		+ "<div class='address-tooltip-header'> [좌표] </div>"
		+ "<p>" + clickLonLat[1] +", "+ clickLonLat[0] +"</p>"
		+ "<div class='address-tooltip-header'> [지번] </div>"
		+ "<p>" + content + "</p>"  ;
	map.addOverlay(addressPopup);
	
	addressPopup.setPosition(clickPoint);
	
//	addressPopup.setPosition([clickPoint[0],Number(clickPoint[1])+250]);
	
//	console.log($('.address-tooltip').parent().css('bottom'));
//	var px = $('.address-tooltip').parent().css('bottom');
//	px = px.substring(0,px.length-2);
//	
//	$('.address-tooltip').parent().css('bottom',px+20+"");
//
//	map.getView().on('change:resolution',function() {
//		$('.address-tooltip').parent().css('bottom',px+20);	
//	});
	
	groupLayerControl.add('middleLayer',addressMarker);
	
	$(".address-tooltip-closer").bind("click", evt => {
		$('.address-tooltip').parent().remove();
		groupLayerControl.remove('middleLayer','addressMarker');
	});
	contextmenu.clear();
}

const mapAddFavor = clickObj => {
	const addressObj = externalApi.getCoord2AddressData(clickObj.coordinate);
	const putCoord = ol.proj.transform(clickObj.coordinate,baseProjection,"EPSG:4326");
	const obj = {data : {
						favorType : "A",
						lotNumAddress : addressObj.documents[0].address.address_name,
						addressName : addressObj.documents[0].address.address_name,
						longitude : putCoord[0], 
						latitude : putCoord[1]
				    }
				}
	if(addressObj.documents[0].road_address){
		obj.roadAddress = addressObj.documents[0].road_address.address_name
	}
	$('#favorite_modal .modal_top h5 .popup_tit').text(addressObj.documents[0].address.address_name);
    $('#favorite_modal #favorName').val(addressObj.documents[0].address.address_name);
    $('#favorite_modal').data(obj);
    $('#favorite_modal').modal(true);
    contextmenu.clear();
}

/*
const contextmenu_items = [
		{
			text:'투망감시',
			classname: 'some-style-class', // add some CSS rules
			//callback: thisCoord
			callback : e => {
				const coordinate = new ol.proj.transform(e.coordinate,'EPSG:5181','EPSG:4326');
				const pObj = {
					"longitude" 	: coordinate[0]
					, "latitude" 	: coordinate[1]
				}
				if(clientInfo.useCctvFlag == "Y"){
					cctv.totalView(pObj, "near");
				} else {
					return false;
				}
				contextmenu.clear();
			}
		},
		'-'
		,
		{
			text: '주소보기',
			classname: 'some-style-class', // add some CSS rules
			callback: thisCoord
		},
		{
			text:'장소 즐겨찾기',
			classname: 'some-style-class', // add some CSS rules
			callback: mapAddFavor
		}
		,
		{
			text:'클릭 지점 로드뷰보기',
			classname: 'some-style-class', // add some CSS rules
			callback: e => {
				const coordinate = new ol.proj.transform(e.coordinate,'EPSG:5181','EPSG:4326');
				window.open("/gis/roadView.do?lon="+coordinate[0]+"&lat="+coordinate[1],'road','');	
				contextmenu.clear();
			}
		}
];
*/

function getContextMenuItem() {
	const seperator = "-";

	const contextArr = [{
							text: '주소보기',
							classname: 'some-style-class', // add some CSS rules
							callback: thisCoord
						}
						
//						, {
//							text:'클릭 지점 로드뷰보기',
//							classname: 'some-style-class', // add some CSS rules
//							callback: e => {
//								const coordinate = new ol.proj.transform(e.coordinate,'EPSG:5181','EPSG:4326');
//								window.open("/gis/roadView.do?lon="+coordinate[0]+"&lat="+coordinate[1],'road','');	
//								contextmenu.clear();
//							}
//						}
					]
	
	return contextArr;
}

const contextmenu = new ContextMenu({
	width: 230,
	items: []
	,
	defaultItems: false
});

contextmenu.setProperties({'list':'exist'});


//컨텍스트 메뉴 리스트 없으면 보이지 않게 
function displayContextmenu(){
	map.getControls().getArray().forEach( e => {
		if(e.getProperties()){
			if(e.getProperties().list=="none"){
				$(e.element).css('visibility','hidden');
			} else { //exist
				$(e.element).css('visibility','visible');
			}
		}
	});
}

//우클릭 메뉴 호출 및 마커제거호출
/*contextmenu.on('open', function(evt) {
	var features = map.forEachFeatureAtPixel(evt.pixel, function(ft, l) {
		return ft.getProperties().features;
	});
	if (features && features[0].get('type') === 'removable') {
		contextmenu.clear();
		removeMarkerItem.data = {
			markers: features,
		};
		contextmenu.push(removeMarkerItem);
	} else {
		contextmenu.clear();
		contextmenu.extend(contextmenu_items);
	}
});*/