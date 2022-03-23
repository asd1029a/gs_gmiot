/**
 * 대시보드 경계지도 
 */
let dashBoardOlMap;

const dashBoardMap = {
		init : () => {
			
			const dashBoardOlMapCenter =  [126.86114586689587, 37.4477115029528];//[127.12177507066025, 37.49847739784013];
			const daumProjection = olProjection.createProjection(daumEPSG,daumExtent);

			baseProjection = daumProjection;
			baseExtent = daumExtent;
			
			
			let boundType = 'B';
			let codeStr = '41210'; //',31042,31041';
			
			const baseEmdLayer = new ol.layer.Tile({
				name: 'emdBoundaryLayer',
				source: new ol.source.TileWMS({
					url: geoServerUrl,
					params: {
						'FORMAT': 'image/png',
						'VERSION': '1.1.0',
						'STYLES': 'smartMeterDashboard',
						tiled: true,
						'LAYERS': 'gm_basearea',//'EMD_'+ boundType +'_GROUP',
		                //'CQL_FILTER': "SGG_CD IN ("+ codeStr +")",
						'exceptions': 'application/vnd.ogc.se_inimage'
							,tilesOrigin:199950 + "," + 497291.11000000004
					},
					serverType: 'geoserver'
				}),
				visible: true
			});
			
			dashBoardOlMap = new ol.Map({
				target: 'mapArea'
				,renderer: 'canvas'
				,interactions: ol.interaction.defaults({
					shiftDragZoom: true
				}) 		   
				,layers: [ baseEmdLayer ]  	
				,view: new ol.View({
			        projection : baseProjection
			        ,extent: daumExtent
			        ,resolutions: resolutions
			        ,maxResolution: resolutions[0]
			        ,center : new ol.proj.transform(dashBoardOlMapCenter,'EPSG:4326',baseProjection)
			        ,zoom: 7.2
			        ,zoomFactor: 1
		            ,rotation: 0
			    })
				,interactions: ol.interaction.defaults({
					doubleClickZoom: false,
					dragAndDrop: false,
					dragPan: false,
					keyboardPan: false,
					keyboardZoom: false,
					mouseWheelZoom: false,
//					pointer: false,
//					select: false
				}),
				controls: ol.control.defaults({
					attribution: false,
					zoom: false,
				}),
			});
			
			comm.ajaxPost({
				type : 'post',
				url:'/account/getListAccountCntInBaseArea.ado',
				data: {},
				async: false,
			}, resultData =>{
				accountLayer = makeLayer.fromGeoJSON(resultData.data,'accountLayer', "", true);
				dashBoardOlMap.addLayer(accountLayer);
			});
			
			dashPopUp.create('boardAccountPopup');
			let target = dashBoardOlMap.getTarget();
			let jTarget = typeof target === "string" ? $("#" + target) : $(target);
			$(dashBoardOlMap.getViewport()).on('mousemove', e => {
				dashPopUp.hide('boardAccountPopup');
				const pixel = dashBoardOlMap.getEventPixel(e.originalEvent);
			    const hit = dashBoardOlMap.forEachFeatureAtPixel(pixel, (feature, layer) => true);
			    const cTarget = $(e.target);
			   
			    if (hit) {
			    	if(cTarget[0].tagName=="CANVAS"){
			        	jTarget.css("cursor", "pointer");
			        	//마우스 온 프리셋
			        	dashBoardOlMap.forEachFeatureAtPixel(pixel,(feature,layer) => {
				    		if(layer){
				    			if(layer.get("name")){
				    				if(layer.get("name")=="accountLayer"){	
				    					
				    					let prop = feature.getProperties();
				    					
				    					for (const [key, value] of Object.entries(prop)) {
				    						if(value==""){
				    							prop[key] = "정보없음";
				    						}
				    					};
				    					
				    					let content = "<div>" +
				    									"<li> 기초구역번호 : " + "(" + prop.sigKorNm + ") " + prop.basMgtSn + "</li>"+
				    									"<li> 수용가수 : " + prop.accountCnt + "</li>"+
				    									"<li> 수용가명" + "<div class='cutText'>" + prop.nameGroup + "</div>" + "</li>"+
//				    									"<li> 업체명 : " + prop.companyNm + "</li>"+
//				    									"<li> 계량기 시리얼 번호 : " + prop.meterSn + "</li>"+
//				    									"<li> 단말기 시리얼 번호 : " + prop.deviceSn + "</li>"+		    									
				    								  "</div>";
				    					
				    					
				    					dashPopUp.content('boardAccountPopup',content);
				    					dashPopUp.move('boardAccountPopup',feature.getGeometry().getCoordinates());
				    				} else {
				    					
				    				}
					    		}
				    		}
				         });
			        } 
			    	dashBoardOlMap.renderSync();
			    } else { 
			        jTarget.css("cursor", "all-scroll");
			    }
			});
			
			
		}
}

/**
 * 대시보드 팝업조작 (overlay)
 */
const dashPopUp = {
	//팝업생성
	create : id => {
		const commonPopUpElement = document.createElement('div');
		commonPopUpElement.id = id;
		commonPopUpElement.className = 'my-ol-popup';
		
		const commonPopUp = new ol.Overlay({
			element: commonPopUpElement,
			offset:[0,-15],
			positioning: 'bottom-center'
		});
		
		commonPopUpElement.innerHTML = "<a id='"+id+"Closer' class='my-ol-popup-closer'></a>"+
									   "<div id="+id+"Content></div>";
		
		dashBoardOlMap.addOverlay(commonPopUp);
		commonPopUp.setPosition(undefined);
		
		$(".my-ol-popup-closer").on('click', e => {
			dashPopUp.remove(id);			
			dashBoardOlMap.getInteractions().forEach( e => {} );
		});
		
	}
	//해당 팝업 제거하기
	,remove: id => { // dashPopUp 자체 삭제
		dashBoardOlMap.getOverlays().forEach( overlay => {
			const elementId = overlay.getElement().id;
			if(elementId){
				if(elementId==id){
					dashBoardOlMap.removeOverlay(overlay);
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
		dashBoardOlMap.getOverlays().forEach( overlay => {
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
		dashBoardOlMap.getOverlays().forEach( overlay => {
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
		dashBoardOlMap.getOverlays().forEach( overlay => {
			const elementId = overlay.getElement().id;
			if(elementId==id){
				flag = true;
			}
		});
		return flag;
	}
	//팝업 잠시 숨기기
	,hide:  id => { 
		dashBoardOlMap.getOverlays().forEach( overlay => {
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
