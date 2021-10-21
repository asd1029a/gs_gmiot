var serverInfo = {
	midServerIp : '192.168.14.15',
	midServerPort : '10003'
};

var xeusInfo = {
	ip : '192.168.14.20',
	port : '8080'
}

var clientInfo = {
	clientIp : "",
	sessionId : ""
};


var layerInfo = {
	id : 'five_area'
}

var videoDatas = new Map();

/**
 * CCTV 영상 재생에 대한 전체 관리를 하는 class이다.
 * 영상 데이터를 관리하는 것 외에 팝업 UI 및 지도와 연계 되는 부분은 connectDialog.js에서 관리된다.
 * @author -
 * @version 0.0.1
 * @class videoManager
 * @property {string} mediaAuthority - 사용자 영상 권한
 * @property {string} userVmsSvrIp - 사용자에게 부여된 스트림 서버 아이피
 * @property {string} userGrpId - 사용자 그룹 아이디
 */
var videoManager = {
	mediaAuthority : undefined,
	userVmsSvrIp : '',
	userGrpId : '',
	/**
	 * 비디오의 형식을 가져온다
	 * @param {boolean} flag - 영상 권한
	 * @returns {string} - 비디오타입(e : [미사용], x : 영상권한 없음, c : 순환감시(?)[미사용], s : 프리셋 세팅, p : [미사용])
	 */
	getVideoType : function(flag) {
		const param = getUrlParams();
		const type = flag === true ? 'e' : this.mediaAuthority !== 'Y' ? 'x' : param.path.indexOf('set') > -1 ? $('#setCirclr').is(':visible') ? 'c' : 's' : 'p';
		return type;
	},
	/**
	 * 영상 재생 권한 체크
	 * @param {function} callback - 권한 체크후 실행 될 function. 
	 * videoManager의 createPopupVideo, eventPopup, storageCastPopup, sitePopup 등의 function이 넘어온다.  
	 * @param {object} option - callback function에서 사용 될 option.
	 * @param {object} option.data - 카메라 데이터(한 대의 카메라를 재생할 경우) [createPopupVideo]
	 * @param {Array} option.datas - 카메라 데이터 배열(여러 대의 카메라를 재생할 경우) [sitePopup, eventPopup, storageCastPopup]
	 * @param {object} option.feature - 지도 마커 데이터
	 * @param {object} option.evtData - 이벤트 데이터(이벤트 주위 투망을 실행할 경우) [eventPopup, storageCastPopup]
	 * @param {string} option.type - 영상 재생 종류(실시간, 저장영상). getVideoType에서 처리된 return 값이다.
	 * @param {boolean} option.btnFlag - createButton을 실행에 관여(true : 실행, false : 미실행) [createPopupVideo]
	 */
	checkPermission : function(callback, option) {
		const type = option.type;
		
		if(type === 'x') {
			commonLog.error('영상 재생 권한이 없습니다. 관리자에게 권한 요청을 하십시오.', {isAutoClose: true});
			return;
		}
		
		if(typeof callback == 'function') callback(option);
	},
	/**
	 * videoDatas의 모든 데이터 삭제
	 * @function
	 */
	removeDatas : function() {
		videoDatas.forEach(function(val, key, map) {
			videoDatas.delete(key);
		});
	},
	/**
	 * videoDatas에서 하나의 데이터 삭제
	 * @param {object} data - 카메라 데이터
	 * @function
	 */
	removeData : function(data) {
		videoDatas.forEach(function(val, key, map) {
			if(val.directionLayer !== undefined) mapManager.map.removeLayer(val.directionLayer);
			if(val.directionOverlay !== undefined) mapManager.map.removeOverlay(val.directionOverlay);
			if(val.presetList !== undefined) removePresetBtns(val.presetList);
			if(data.fcltId === val.fcltId) videoDatas.delete(key);
		});
	},
	/**
	 * 현재 재생중인 영상을 모두 종료하고
	 * @function
	 */
	clearVideoManager : function() {
		videoManager.setVideoDatas(null);
		videoManager.stopPlayer();
	},
	/**
	 * 카메라 Araay 데이터를 Map 형식으로 변환
	 * @param {Array} datas - 카메라 데이터 배열
	 * @returns {Map} - 카메라 데이터 맵
	 * @function
	 */
	convertArrayToMap : function(datas) {
		const temp = new Map();
		
		for(var i = 0, max = datas.length; i < max; i++) {
			const data = datas[i];
			data.ch = i;
			data.fcltId !== 'null' && typeof data.fcltId !== undefined ? temp.set(data.fcltId, data) : console.log('error');
		}
		
		return temp;
	},
	/**
	 * 배열 형식의 비디오 데이터를 Map으로 변환하여 videoDatas에 지정
	 * @param {Array} datas - 카메라 데이터 배열
	 * @function
	 */
	setVideoDatas : function(datas) {
		if (datas == null) {
			videoDatas = new Map();
			return;
		}
		
		const tempMap = videoManager.convertArrayToMap(datas);
		
		videoDatas = tempMap;
	},
	getDataChannel : function(data) {
		if (typeof data == 'undefined') {
			return -1;
		}
		
		if(videoDatas.has(data.fcltId)) {
			return videoDatas.get(data.fcltId).ch;
		}
		
		return -1;
	},
	/**
	 * videoManager init function
	 * @param {string} midServerIp - 중계 서버 아이피
	 * @param {string} midServerPort - 중계 서버 포트
	 * @function
	 */
	init : function(midServerIp, midServerPort) {
		serverInfo.mideServerIp = midServerIp ? midServerIp : serverInfo.midServerIp;
		serverInfo.midServerPort = midServerPort ? midServerPort : serverInfo.midServerPort;
	},
	/**
	 * 사용자 클라이언트의 아이피 및 세션 아이디 저장
	 * @param {string} ip - 사용자 클라이언트 아이피
	 * @param {string} sessionId - 사용자 세션 아이디
	 * @function
	 */
	setClientInfo : function(ip, sessionId) {
		clientInfo.clientIp = ip ? ip : clientInfo.clientIp;
		clientInfo.sessionId = sessionId ? sessionId : clientInfo.sessionId;
	},
	/**
	 * 순환감시 및 순환감시 설정에서 viewLayout element id를 설정하는 함수
	 * @param {string} id - layout으로 지정 된 Element id
	 * @function
	 */
	setViewLayout : function(id) {
		layerInfo.id = id;
		$('#'+layerInfo.id).show();
		$('#'+layerInfo.id+' .cctv_layer_area').show();
	},
	/**
	 * video에 Button UI를 생성하는 함수.
	 * @param {object} data - 카메라 데이터
	 * @param {element} parent - 부모 Element
	 * @param {object} feature - 지도 마커 데이터
	 * @param {string} type - video 종류
	 * @function
	 */
	createButton : function(data, parent, feature, type) {
		var btnList = document.createElement('div');
		btnList.classList.add('btn_wrap');
		btnList.classList.add('btn_area_center');
		
		let a = document.createElement('a');
		let b = document.createElement('label');
		let c = document.createElement('a');
		let d = document.createElement('a');
		
		a.setAttribute('href', '#');
		a.classList.add('btn');
		a.classList.add('site_btn');
		a.innerHTML = "개소감시";
			
		$(a).bind({
			'click': function(e) {
				const callback = function(feature, datas) {
					const option = {};
					option.datas = datas;
					option.feature = feature;
					videoManager.checkPermission(videoManager.sitePopup, option);
					// videoManager.sitePopup(datas, feature);
				}
				getSiteList(feature, callback)
			},
		});
		
		btnList.appendChild(a);
		
		d.setAttribute('href', '#');
		d.classList.add('btn');
		d.innerHTML = '상세정보';
		
		d.addEventListener('click', function() {
			if($(this).hasClass('active')) {
				$(this).removeClass('active');
				$(parent).children('.detail-wrap').slideUp();
			} else {
				$(this).addClass('active');
				$(parent).children('.detail-wrap').slideDown();
			}
		});
		
		btnList.appendChild(d);
		
		b.classList.add('switch');
		b.classList.add('position_left');
		
		const bInput = document.createElement('input');
		
		bInput.setAttribute('type', 'checkbox');
		
		const bSpan = document.createElement('span');
		
		bSpan.classList.add('slider');
		bSpan.classList.add('round');
		
		const bText = document.createElement('span');
		
		bText.innerHTML = 'OFF';
		bText.classList.add('switch-text');
		bText.classList.add('off');

		b.appendChild(bText);
		b.appendChild(bInput);
		b.appendChild(bSpan);
		
		$(bInput).bind({
			'click': function(e) {
				if($(this).hasClass('active') && $(c).hasClass('active')) {
					$(c).click();
					return;
				}
				if($(this).hasClass('active')) {
					bText.innerHTML = 'OFF';
					$(bText).removeClass('on').addClass('off');
					$(this).removeClass('active');
					$(parent).children('.video-wrap').children('.cctv-btn-wrap').removeClass('active');
					mapManager.map.removeLayer(data.directionLayer);
				} else {
					bText.innerHTML = 'ON';
					$(bText).removeClass('off').addClass('on');
					$(this).addClass('active');
					$(parent).children('.video-wrap').children('.cctv-btn-wrap').addClass('active');
				}
			},
		})
		btnList.appendChild(b);

		if(type == 's' && data.cctvAgYn == '1') {
			const popupList = [];
			c.setAttribute('href', '#');
			c.classList.add('btn');
			c.innerHTML = '프리셋 설정';
			
			$(c).bind({
				'click': function(e) {
					if(!$(bInput).hasClass('active') && !$(this).hasClass('active')) {
						$(bInput).click();
					}
					
					if($(this).hasClass('active')) {
						removeBtns(popupList);
						$(this).removeClass('active');
						$(bInput).click();
						// $(parent).children('.video-wrap').removeClass('active');
						// $(parent).children('.setting-wrap').removeClass('active');
						// $(parent).children('.close').show();
					} else {
						mapManager.setCenter(ol.proj.transform([data.lon, data.lat], 'EPSG:4326', mapManager.projection));
						mapManager.setZoom(20);
						createPresetNumOverlays(feature, data, popupList);
						$(this).addClass('active');
						// $(parent).children('.video-wrap').addClass('active');
						// $(parent).children('.setting-wrap').addClass('active');
						// $(parent).children('.close').hide();
					}
				},
			})
			btnList.appendChild(c);
			
			function removeBtns() {
				for(var i = 0, max = popupList.length; i < max; i++) {
					mapManager.map.removeOverlay(popupList[i]);
				}
			}
		} else if(type == 's' && data.cctvAgYn == '0') {
			var directionOverlay = undefined;
			c.setAttribute('href', '#');
			c.classList.add('btn');
			c.innerHTML = '방향 설정';
			
			const directionLayer = new ol.layer.Vector({
				source: new ol.source.Vector()
			});

			const directionFeature = new ol.Feature({});
			directionLayer.getSource().addFeature(directionFeature);
			
			$(c).bind({
				'click': function(e) {
					if($(this).hasClass('active')) {
						mapManager.map.removeLayer(directionLayer);
						mapManager.map.removeOverlay(directionOverlay);
						$(this).removeClass('active');
						// $(parent).children('.close').show();
					} else {
						mapManager.map.addLayer(directionLayer);
						mapManager.setCenter(ol.proj.transform([data.lon, data.lat], 'EPSG:4326', mapManager.projection));
						mapManager.setZoom(13);
						directionOverlay = createDirectionOverlay(feature, data, directionFeature, this);
						data.directionOverlay = directionOverlay;
						$(this).addClass('active');
						// $(parent).children('.close').hide();
					}
				},
			});
			
			data.directionLayer = directionLayer;
			
			btnList.appendChild(c);
		}
		
		parent.appendChild(btnList);

		videoManager.createPopupMouseOverEvent($(parent));
	},
	/**
	 * 비디오 팝업에 이벤트 지정. context menu 이벤트, ptz 및 preset 버튼 이벤트 지정.
	 * @param {element} dialog - dialog element(conenctDialog에서 생성 됨)
	 * @param {object} data - 카메라 데이터
	 * @function
	 */
	createPopupPlayerEvent : function(dialog, data) {
		document.addEventListener("fullscreenchange", function(e) {
			setTimeout(function() {
				videoManager.onVideoResize(data);
			}, 200);
		});
		
		$(dialog).bind({
			dblclick:function(e) {
				// videoManager.onVideoFullScreen(data);
			},
			contextmenu:function(e){
        		$(".rightclick_menu").remove();
				e.preventDefault();
				$("<ul class='rightclick_menu'></ul>")
					.appendTo("body")
					.append("<li class='rightclick_menu_title'>CCTV 메뉴</li>")
					.append(
						$("<li></li>").append(
							$("<a href='#'>전체화면</a>").on('click', function() {
								videoManager.onVideoFullScreen(data);
							})
						)
					)
					.css({top: e.pageY + "px", left: e.pageX + "px"});
			}
		});
		
		$(dialog).children('.video-wrap').children('.cctv-btn-wrap').children('a').bind({
			mousedown:function(e){
				var cmd = e.target.className;
				if (cmd.indexOf('preset') != -1) {
					var presetNo = cmd.split('_')[1];
					videoManager.popupPresetCtrl(data, presetNo);
				} else {
					videoManager.insertCctvCtrlLog(data.fcltId, '0', cmd);
					try {
						console.log('===== ptz >>>> cmd : ', cmd);
						const actionCd = 'Start';
						videoManager.xeusPtzCtrl(data,cmd,actionCd);
			    	} catch(e) {
			    		console.log('===== e : ', e);
			    	}
				}
		    	return false;
			},
			mouseup:function(e){
				var cmd = e.target.className;
				if (cmd.indexOf('preset') == -1) {
					videoManager.insertCctvCtrlLog(data.fcltId, '0', 'stop');
					try {
						const actionCd = 'Stop';
						videoManager.xeusPtzCtrl(data,cmd,actionCd);
			    	} catch(e) {
			    		console.log('===== e : ', e);
			    	}
				}
				return false;
			}
		});
	},
	/**
	 * video dialog에 프리셋 세팅용 UI 생성
	 * @param {element} dialog - dialog element(conenctDialog에서 생성 됨)
	 * @param {object} data - 카메라 데이터
	 * @function
	 */
	createPopupPresetSetting : function(dialog, data) {
		const wrap = document.createElement('div');
		wrap.classList.add('setting-wrap');
		wrap.classList.add('btn_area_center');
		
		const display = document.createElement('div');
		display.classList.add('display');
		
		const pWrap = document.createElement('div');
		pWrap.classList.add('pan_wrap');
		const pLabel = document.createElement('span');
		pLabel.innerHTML = 'Pan : ';
		const pValue = document.createElement('span');
		pValue.classList.add('set_value');
		pWrap.appendChild(pLabel);
		pWrap.appendChild(pValue);
		
		display.appendChild(pWrap);
		
		const tWrap = document.createElement('div');
		tWrap.classList.add('tilt_wrap');
		const tLabel = document.createElement('span');
		tLabel.innerHTML = 'Tilt : ';
		const tValue = document.createElement('span');
		tValue.classList.add('set_value');
		tWrap.appendChild(tLabel);
		tWrap.appendChild(tValue);
		
		display.appendChild(tWrap);
		
		const zWrap = document.createElement('div');
		zWrap.classList.add('zoom_wrap');
		const zLabel = document.createElement('span');
		zLabel.innerHTML = 'Zoom : ';
		const zValue = document.createElement('span');
		zValue.classList.add('set_value');
		zWrap.appendChild(zLabel);
		zWrap.appendChild(zValue);
		
		display.appendChild(zWrap);
		
		const fWrap = document.createElement('div');
		fWrap.classList.add('focus_wrap');
		const fLabel = document.createElement('span');
		fLabel.innerHTML = 'Focus : ';
		const fValue = document.createElement('span');
		fValue.classList.add('set_value');
		fWrap.appendChild(fLabel);
		fWrap.appendChild(fValue);
		
		display.appendChild(fWrap);
		
		wrap.appendChild(display);
		
		const btnWrap = document.createElement('div');
		btnWrap.classList.add('btn_wrap');
		btnWrap.classList.add('btn_area_center');
		
		const reqBtn = document.createElement('a');
		reqBtn.classList.add('btn');
		
		reqBtn.innerHTML = '가져오기';
		btnWrap.appendChild(reqBtn);
		
		const saveBtn = document.createElement('a');
		saveBtn.classList.add('btn');
		saveBtn.innerHTML = '저장';
		btnWrap.appendChild(saveBtn);
		
		wrap.appendChild(btnWrap);
		
		dialog.appendChild(wrap);
	},
	/**
	 * video dialog에 PTZ 컨르롤러 UI 생성
	 * @param {element} dialog - dialog element(conenctDialog에서 생성 됨)
	 * @param {object} data - 카메라 데이터
	 * @param {string} type - 영상 타입(R : 실시간, S : 저장)
	 * @function
	 */
	createPopupPtzControls : function(dialog, data, type) {
		var btnViewId = 'btn_view_ch';
		
		var width = $(dialog).width();
		var height = $(dialog).height();
		var hOffset = width / 2;
		var vOffset = height / 2;
		
		var left = 5;
		var center = hOffset - 13.5;
		var right = 5;
		
		var top = 5;
		var middle = vOffset - 13.5;
		var bottom = 5;
		
		var view = "";
		view += "<div class='cctv-btn-wrap' id='" + btnViewId + "'>";
		if(type == "R" || type == "SC"){
			if(data.cctvAgYn == "1"){
				view += "<a class='Left' style='left:"+(center-26)+"px;top:"+(middle)+"px'></a>";
				view += "<a class='Right' style='left:"+(center+26)+"px;top:"+(middle)+"px'></a>";
				view += "<a class='Up' style='left:"+(center)+"px;top:"+(middle-26)+"px'></a>";
				view += "<a class='Down' style='left:"+(center)+"px;top:"+(middle+26)+"px'></a>";
				view += "<a class='preset_1' style='left:"+left+"px;top:"+top+"px'></a>";
				view += "<a class='preset_2' style='left:"+center+"px;top:"+top+"px'></a>";
				view += "<a class='preset_3' style='right:"+right+"px;top:"+top+"px'></a>";
				view += "<a class='preset_4' style='right:"+right+"px;top:"+middle+"px'></a>";
				view += "<a class='preset_5' style='right:"+right+"px;bottom:"+bottom+"px'></a>";
				view += "<a class='preset_6' style='left:"+center+"px;bottom:"+bottom+"px'></a>";
				view += "<a class='preset_7' style='left:"+left+"px;bottom:"+bottom+"px'></a>";
				view += "<a class='preset_8' style='left:"+left+"px;top:"+middle+"px'></a>";
			}
			view += "<a class='ZoomIn' style='left:"+(center)+"px;top:"+(middle)+"px'></a>";
			view += "<a class='ZoomOut' style='left:"+(center)+"px;top:"+(middle+13)+"px'></a>";
		}else if(type == "S"){
			view += "<div class='storage_btn_area'>";
			view += "<a class='video_prev'></a>";
			view += "<a class='video_stop'></a>";
			view += "<a class='video_play'></a>";
			view += "<a class='video_next'></a>";
			// view += "<a class='video_speed'></a>";
		}
		view += "</div>";
		
		$(dialog).children('.video-wrap').append(view);
		
		data.btnViewId = btnViewId;
		
		videoManager.createPopupPlayerEvent(dialog, data);
	},
	/**
	 * video dialog에 영상 상세정보 UI 생성
	 * @param {object} data - 카메라 데이터
	 * @param {element} dialog - dialog element(conenctDialog에서 생성 됨)
	 * @function
	 */
	createDetailView : function(data, dialog) {
		const wrap = document.createElement('ul');
		wrap.classList.add('detail-wrap');
		
		const child1 = document.createElement('li');
		const left1 = document.createElement('span');
		left1.innerHTML = '명칭';
		const right1 = document.createElement('span');
		right1.innerHTML = data.fcltNm;
		child1.appendChild(left1);
		child1.appendChild(right1);
		
		wrap.appendChild(child1);
		
		const child2 = document.createElement('li');
		const left2 = document.createElement('span');
		left2.innerHTML = '노드 ID';
		const right2 = document.createElement('span');
		right2.innerHTML = data.nodeId;
		child2.appendChild(left2);
		child2.appendChild(right2);
		
		wrap.appendChild(child2);
		
		const child3 = document.createElement('li');
		const left3 = document.createElement('span');
		left3.innerHTML = '용도';
		const right3 = document.createElement('span');
		right3.innerHTML = data.fcltPuposeNm;
		child3.appendChild(left3);
		child3.appendChild(right3);
		
		wrap.appendChild(child3);
		
		const child4 = document.createElement('li');
		const left4 = document.createElement('span');
		left4.innerHTML = 'SW/HW';
		const right4 = document.createElement('span');
		right4.innerHTML = data.fcltSh;
		child4.appendChild(left4);
		child4.appendChild(right4);
		
		wrap.appendChild(child4);
		
		dialog.appendChild(wrap);
	},
	/**
	 * 개소 감시 팝업 생성
	 * @param {object} feature - 지도 마커 데이터
	 * @param {Array} datas - 카메라 데이터 배열
	 * @function
	 * @deprecated 미사용
	 */
	createSitePopupVideo : function(feature, datas) {
		if(videoDatas.size > 5) return;
		const siteWrap = document.createElement('div');
		siteWrap.classList.add('site-video-wrap');
		
		const max = datas.length > 6 ? 6 : datas.length;
		
		for(var i = 0; i < max; i++) {
			const data = datas[i];
			const videoWrap = document.createElement('div');
			videoWrap.classList.add('video-wrap');
			
			const viewId = 'cctv_' + i + '_' + new Date().getTime();
	        
			const video = document.createElement('div');
			video.classList.add('video-content');
			video.id = viewId;
			
			videoWrap.appendChild(video);
			
			siteWrap.appendChild(videoWrap);
			
			setTimeout(function() {
				videoManager.playXeus(data, viewId);
			}, 50);
			
			videoDatas.set(data.fcltId, data);
		}
		
		const width = 480;
		const height = 480 + (((datas.length % 2) + (datas.length / 2)) * 40);
		
		const option = {
			datas: datas,
			feature: feature,
			css: {
				width: width,
				height: height
			}
		}
		
		const dialog = $.connectSiteDialog(undefined, siteWrap, true, option);
	},
	/**
	 * video 팝업 생성 함수
	 * @param {object} option - video 관련 option
	 * @param {object} option.feature - 지도 마커 데이터
	 * @param {object} option.data - 카메라 데이터
	 * @param {boolean} option.btnFlag - 버튼 UI 생성 여부
	 * @param {string} option.type - 영상 타입 
	 * @param {boolean} option.isSaved - 저장영상 여부(true : 저장영상, false : 실시간}
	 * @param {date} option.timestamp - 저장영상 시작 시간
	 * @function
	 * @see {@link videoManager.getVideoType} - option.type
	 */
	createPopupVideo : function(option) {
		const feature = option.feature;
		const data = option.data;
		const btnFlag = option.btnFlag;
		const type = option.type;
		const isSaved = option.isSaved;
		const timestamp = option.timestamp;
		
		if(type === 'x') {
			alert('영상 권한이 없습니다.');
			return;
		}
		// if(videoDatas.has(data.fcltId) || videoDatas.size > 6) return;
		if(videoDatas.has(data.fcltId)) return;
		var popupVideo = document.createElement('div');
		
		popupVideo.classList.add('video-wrap');
		
		const title = document.createElement('div');
		
		title.classList.add('title');
		title.innerHTML = data.nodeId;
		
		popupVideo.appendChild(title);
		
		var viewId = 'cctv_' + new Date().getTime();
		
        
		const video = document.createElement('div');
		video.classList.add('video-content');
		video.id = viewId;
		
		$(popupVideo).append(video);
		
		const dialogOption = {
			data: data,
			feature: feature,
			css: {
				width: 400,
				height: 320
			}
		}
		
		const dialog = $.connectDialog(undefined, popupVideo, true, true, dialogOption);
		
		videoManager.createDetailView(data, dialog);
		
		if(btnFlag) videoManager.createButton(data, dialog, feature, type);
		
		data.dialog = dialog;
		
		if(timestamp !== undefined) {
			videoManager.playXeusStorage(data, viewId, timestamp);
		} else {
			videoManager.playXeus(data, viewId);
			videoManager.createPopupPtzControls(dialog, data, 'R');
		}
		
		videoManager.addCctvViewLog(data, isSaved ? 'S' : 'R');
		
		videoDatas.set(data.fcltId, data);
	},
	createPopupMouseOverEvent : function(popup){
		popup.mouseenter(function(){
			// $(this).children('.btn_wrap').stop().animate({opacity: 1}, 150);
		});
		popup.mouseleave(function(){
			// $(this).children('.btn_wrap').stop().css({opacity: 0.0})
		});
	},
	/**
	 * 지오맥스 영상 플레이(저상영상)
	 * @param {object} data - 카메라 데이터
	 * @param {string} viewId - video element 아이디
	 * @param {string} timestamp - 저장영상 시작 시간
	 * @function
	 */
	playXeusStorage : function(data, viewId, timestamp) {
		if(data === undefined) return;
		const player = new XeusGate.Player({
            playerId : viewId,
            url : 'ws://'+xeusInfo.ip+':'+xeusInfo.port+'/xeus-gate/stream',
            cctvMgrNo : data.fcltId,  // 192: 마일스톤 194: 뷰릭스, 301:icon
            userId : 'tester1',
            evtType : '112',
            timestamp : timestamp,// '20200219142030', 미구현
            speed : '10', // 미구현
            size : '',  // 1280x720 설정시cpu많이 먹음... default 권장,,
            codec : 'h264',
            rtspUrl : '', //
            debug : false
	    });
		
		data.player = player;
	},
	/**
	 * 지오맥스 영상 플레이(실시간)
	 * @param {object} data - 카메라 데이터
	 * @param {string} viewId - video element 아이디
	 * @function
	 */
	playXeus : function(data, viewId) {
		if(data === undefined) return;

		var time = '10';
		var speed = '10';
		
		var player = new XeusGate.Player({
            playerId : viewId,
            url : 'ws://'+xeusInfo.ip+':'+xeusInfo.port+'/xeus-gate/stream',
            cctvMgrNo : data.fcltId,  // 192: 마일스톤 194: 뷰릭스, 301:icon
            userId : 'tester1',
            evtType : '112',
            timestamp : '',// '20200219142030', 미구현
            speed : speed, // 미구현
            size : '',  // 1280x720 설정시cpu많이 먹음... default 권장,,
            codec : 'h264',
            // codec : 'mjpeg',
            rtspUrl : '', //
            debug : false
        });
		
		data.player = player;
	},
	/**
	 * 맵시 영상 플레이(저상영상)
	 * @param {string} ch - 카메라 채널
	 * @param {boolean} flag - 영상 종류(true : 저장영상, false : 실시간)
	 * @param {obejct} timeData - 저장영상 시작, 종료 시간 (sTime, eTime)
	 * @function
	 */
	playHive : function(ch, flag, timeData) { 
		let hivePlayer;
		!flag ? hivePlayer = new HiVeWebRtcPlayer(videoDatas[ch].viewId, '118.60.0.59', '8080',videoDatas[ch].cctvUuid) 
			: hivePlayer = new HiVeWebRtcPlayer(videoDatas[ch].viewId, '118.60.0.59', '8080',videoDatas[ch].cctvUuid, false, flag, timeData.sTime, timeData.eTime, 1); 
		hivePlayer.Play(); 
		videoDatas[ch].setSpeed = function(speed) {
			new HiVeWebRtcPlayer(videoDatas[ch].viewId, '118.60.0.59', '8080',videoDatas[ch].cctvUuid, false, flag, timeData.sTime, timeData.eTime, speed);
		}; 
	},
	/**
	 * 이 callback function은 stopPopup가 실행되고 난 뒤 실행 될 function입니다.
	 * @callback videoManager.callback
	 * @param {object} option - callback에서 쓰일 option.
	 * @param {Array} option.datas - 카메라 데이터 배열
	 * @param {object} option.evtData - 이벤트 데이터
	 * @param {object} option.data - 카메라 데이터
	 * @param {string} option.type - 영상 타입
	 * @see {@link videoManager.getVideoType} - option.type
	 * @see {@link videoManager.stopPopup} - stopPopup callback
	 * */
	/**
	 * 영상 팝업 종료 함수
	 * @param {object} option - 카메라 데이터
	 * @param {videoManager.callback} success - callback function
	 * @function
	 */
	stopPopup : function(option, success) {
		videoDatas.forEach(function(val, key, map) {
			const dialog = val.dialog;
			if(dialog !== undefined) {
				dialog.querySelector('.close').click();
			}
		});
		
		if (typeof success != 'undefined') success(option);
	},
	/**
	 * 이 callback function은 stopPlayer가 실행되고 난 뒤 실행 될 function입니다.
	 * @callback videoManager.callback
	 * @param {object} option - callback에서 쓰일 option.
	 * @param {Array} option.datas - 카메라 데이터 배열
	 * @see {@link videoManager.stopPlayer} - stopPlayer callback
	 * */
	/**
	 * 영상 종료 함수
	 * @param {object} option - 카메라 데이터
	 * @param {videoManager.callback} success - callback function
	 * @function
	 */
	stopPlayer : function(option, success) {
		videoDatas.forEach(function(val, key, map) {
			const player = val.player;
			if (player != null && player != 'undefined') {
				player.destroy();
				console.log("player.... destroy at unload....... OK", option.data);
			}
		});
		
		$('#'+layerInfo.id+' .cctv-layer-view .view').html('');
		if (typeof success == 'function') success(option);
	},
	/**
	 * viewLayout에 카메라 영상을 플레이하는 함수
	 * @param {string} type - 영상 종류(실시간 ,저장영상)
	 * @function
	 * @see {@link videoManager.circlr} - 순환감시
	 * @see {@link videoManager.settingCirclr} - 순환감시 세팅
	 */
	createPlayer : function(type) {
		videoDatas.forEach(function(val, key, map) {
			var viewId = 'cctv_' + val.ch + '_' + new Date().getTime();
			var viewEl = $('#'+layerInfo.id+' .view')[val.ch];
			
			var view = "";
			view += "<div id='" + viewId + "' class='video-content'></div>"; 
			
			$(viewEl).append(view);
			
			val.viewId = viewId;
			val.type = type;
			setTimeout(function() {
				videoManager.playXeus(val, viewId);
			}, 10);
		});
	},
	/**
	 * 개소 영상 팝업 생성 함수
	 * @param {object} option - 영상 재생 관련 option
	 * @param {Array} option.datas - 카메라 데이터 배열
	 * @param {object} option.feature - 지도 마커 데이터
	 * @function
	 */
	sitePopup : function(option) {
		let success = function(option) {
			const datas = option.datas;
			const layer = mapManager.getVectorLayer('cctv');
			const source = layer.getSource().getSource();
			const type = videoManager.getVideoType();

			for(var i = 0, max = datas.length; i < max; i++) {
				const data = datas[i];
				option.data = data;
				option.btnFlag = true;
				videoManager.createPopupVideo(option);
			}
			
			$('.site_btn').hide();
			dialogManager.sortDialog();
		}
		
		videoManager.stopPopup(option, success);
	},
	/**
	 * 저장영상 투망 팝업 함수
	 * @param {object} option - 카메라 및 이벤트 데이터 등의 option
	 * @param {object} option.evtData - 이벤트 데이터
	 * @param {object} option.data - 카메라 데이터
	 * @param {string} option.type - 영상 타입
	 * @function
	 * @see {@link videoManager.getVideoType} - option.type
	 */
	storageCastPopup : function(option) {
		let success = function(option) {
			const datas = option.datas;
			const layer = mapManager.getVectorLayer('cctv');
			const source = layer.getSource().getSource();
			const type = 'sp';
			const timestamp = moment(option.evtData.evtOcrYmdHms, 'YYYYMMDDHHmmss').format('YYYYMMDDHHmmss');
			/*
			 * const sTime = moment(option.evtData.evtOcrYmdHms,
			 * 'YYYYMMDDHHmmss').format('YYYY.MM.DD.HH.mm.ss'); const eTime =
			 * option.evtData.evtEndYmdHms != ' ' ?
			 * moment(option.evtData.evtEndYmdHms,
			 * 'YYYYMMDDHHmmss').format('YYYY.MM.DD.HH.mm.ss') :
			 * moment(option.evtData.evtOcrYmdHms, 'YYYYMMDDHHmmss').add('15',
			 * 'm').format('YYYY.MM.DD.HH.mm.ss');
			 */
			
			for(var i = 0, max = datas.length; i < max; i++) {
				const data = datas[i];
				// data.sTime = sTime;
				// data.eTime = eTime;
				data.direction = 0;
				const feature = source.getFeatureById(data.fcltId);
				option.data= data;
				option.feature = feature;
				option.btnFlag = true;
				option.isSaved = true;
				option.timestamp = timestamp;
				videoManager.createPopupVideo(option);
			}
			
			dialogManager.sortDialog();
		}
		
		videoManager.stopPopup(option, success);
	},
	/**
	 * 투망 팝업 함수
	 * @param {object} option - 카메라 재생 관련 option
	 * @param {object} option.data - 좌표 데이터(Point)
	 * @param {string} option.type - 영상 타입
	 * @function
	 * @see {@link videoManager.getVideoType} - option.type
	 */
	castPopup : function(option) {
		let success = function(option) {
			const datas = option.datas;
			const layer = mapManager.getVectorLayer('cctv');
			const source = layer.getSource().getSource();
			const type = videoManager.getVideoType();
			
			for(var i = 0, max = datas.length; i < max; i++) {
				const data = datas[i];
				const feature = source.getFeatureById(data.fcltId);
				option.data= data;
				option.feature = feature;
				option.btnFlag = true;
				
				videoManager.createPopupVideo(option);
			}
			
			dialogManager.sortDialog();
		}
		
		videoManager.stopPopup(option, success);
	},
	/**
	 * 순환감시 레이어 영상 재생 함수
	 * @param {Arrayt} datas - 카메라 데이터 배열
	 * @function
	 */
	circlr : function(datas) {
		videoManager.setViewLayout('nine_area');
		
		let success = function(option) {
			videoManager.setVideoDatas(option.datas);
			
			videoManager.createPlayer('R');
			// videoManager.getRtsp(undefined, data, true, type,
			// videoManager.createPlayer)
		}
		
		var option = {
			datas : datas
		}
		
		videoManager.stopPlayer(option, success);
	},
	/**
	 * 순환감시 세팅 레이어에 영상 재생 함수
	 * @param {object} data - 카메라 데이터
	 * @function
	 */
	settingCirclr : function(data) {
		videoManager.setViewLayout('setCirclr');
		
		let success = function(option) {
			videoManager.setVideoDatas(option.datas);
			
			videoManager.createPlayer('R');
		}
		
		var option = {
			datas : [data]
		}
		
		videoManager.stopPlayer(option, success);
	},
	/**
	 * 카메라 PTZ 제어 값 변환 함수
	 * @param {string} cmd - PTZ 방향
	 * @returns {number} - 10진수 or 16진수 형식
	 * @function 
	 */
	switchPtzValue : function (cmd) {
		var ptzCtrlMode;
		
		if(cmd=='stop') ptzCtrlMode = 9;
		else if(cmd=='left') ptzCtrlMode = 1;
		else if(cmd=='right') ptzCtrlMode = 2;
		else if(cmd=='up') ptzCtrlMode = 3;
		else if(cmd=='down') ptzCtrlMode = 4;
		else if(cmd=='zoomIn') ptzCtrlMode = 0x05;
		else if(cmd=='zoomOut') ptzCtrlMode = 0x06;
		else if(cmd=='focusNear') ptzCtrlMode = 0x07;
		else if(cmd=='focusFar') ptzCtrlMode = 0x08;
		
		return ptzCtrlMode;
	},
	/**
	 * 지도 팝업 카메라 PTZ 이동 함수 (VMS 중계서버 연계)
	 * @param {object} data - 카메라 데이터
	 * @param {string} cmd - PTZ 방향
	 * @param {string} speed - PTZ 이동 속도
	 * @function 
	 * @see {@link videoManager.switchPtzValue} - 카메라 PTZ 제어 값 변환 함수
	 * @deprecated 사내 VMS 연계시에만 사용
	 */
	popupPtzCtrl : function(data, cmd, speed) {
		const jsonObj = {};
		jsonObj.code = "1100";
		jsonObj.node_id = data.nodeId;
		jsonObj.ptz_mode = videoManager.switchPtzValue(cmd);
		jsonObj.speed = speed;
		jsonObj.svr_ip = data.vmsSvrIp;
		
		$.ajax({
			type       : "POST",
			url        : "http://" + serverInfo.midServerIp + ":" + serverInfo.midServerPort + "/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(data) {
		});
	},
	/**
	 * 카메라 PTZ 이동 함수 (VMS 중계서버 연계)
	 * @param {object} data - 카메라 데이터
	 * @param {string} presetNo - 카메라 프리셋 번호
	 * @function 
	 */
	popupPresetCtrl : function(data, presetNo) {
		const jsonObj = {};
		jsonObj.cctvId = data.fcltId;
		jsonObj.presetNo = presetNo;
		
		$.ajax({
		    contentType : "application/json; charset=utf-8",
			type        : "POST",
			url         : "/select/facility.getFacilityPreset/action",
			dataType    : "json",
			data        : JSON.stringify(jsonObj),
			async       : false,
			beforeSend  : function(xhr) {
				// 전송 전 Code
			}
		}).done(function (result) {
			const rows = result.rows;
	    	if(rows=='sessionOut'){
				alert('로그인 시간이 만료되었습니다.');
				closeWindow();
			}
			var resultData = rows[0];
			
			if (!resultData) {
				return;
			}
			var cctvAgYn = data.cctvAgYn;
			var fcltPurposeCd = 0;
			var imageSrc = '../../images/icons/cctv/cctv_'+cctvAgYn+'_'+ fcltPurposeCd+'_'+ presetNo+'.png';
			// setMarkerImage(facilityMarkers[ch], imageSrc);
			
			videoManager.insertCctvCtrlLog(resultData.cctvId, '1', resultData.presetNo);

			if(resultData.cctvKnd == "SW") {
				videoManager.setXeusPtzPosition(data, presetNo, resultData, '60');
				// videoManager.swPresetMove(data, resultData.pan,
				// resultData.tilt, resultData.zoom, resultData.focus, '60');
			}
			else {
				videoManager.hwPresetMove(data, resultData.presetNo, '60');
			}
	    });
	},
	/**
	 * 지도 팝업 카메라 PTZ 이동 함수 (VMS 중계서버 연계)
	 * @param {object} data - 카메라 데이터
	 * @param {string} ch - 카메라 채널 정보
	 * @param {string} cmd - PTZ 방향
	 * @param {string} speed - PTZ 이동 속도
	 * @function 
	 * @see {@link videoManager.switchPtzValue} - 카메라 PTZ 제어 값 변환 함수
	 * @deprecated 사내 VMS 연계시에만 사용
	 */
	ptzCtrl : function(data, ch, cmd, speed) {
		const jsonObj = {};
		jsonObj.code = "1100";
		jsonObj.node_id = data.nodeId;
		jsonObj.ptz_mode = videoManager.switchPtzValue(cmd);
		jsonObj.speed = speed;
		jsonObj.svr_ip = data.vmsSvrIp;
		
		$.ajax({
			type       : "POST",
			url        : "http://" + serverInfo.midServerIp + ":" + serverInfo.midServerPort + "/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(data) {
		});
	},
	/**
	 * 지오맥스 연계시 사용되는 PTZ Control 함수
	 * @param {object} data - 카메라 데이터
	 * @param {string} cmd - PTZ 방향
	 * @param {string} actionCd - Start Or Stop (Start : PTZ 버튼 mousedown, Stop : PTZ 버튼 mouseup) 
	 * @function 
	 * @deprecated 지오맥스 연계 시에만 사용
	 */
	xeusPtzCtrl : function(data, cmd, actionCd){
		const jsonObj = {};
		$.ajax({
			type       : "POST",
			url        : "http://"+xeusInfo.ip+":"+xeusInfo.port+"/xeus-gate/setPTZ.json?cctvMgrNo="+data.fcltId+"&action="+actionCd+"&code="+cmd+"&spd=60",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(data) {
		});
	},
	/**
	 * 카메라의 현재 방향 PTZ 값을 가져오는 함수
	 * @param {object} data - 카메라 데이터
	 * @param {string} presetNo - 카메라 프리셋 번호
	 * @param {object} point - 좌표 정보
	 * @param {float} point.lat - 위도
	 * @param {float} point.lon - 경도
	 * @param {videoManager.saveSwPreset} callback - 전달받은 PTZ 값을 저장하는 callback function
	 * @function 
	 * @deprecated 지오맥스 연계 시에만 사용
	 */
	getXeusPtzPosition : function(data, presetNo, point, callback) {
		$.ajax({
			type       : "GET",
			url        : "http://"+xeusInfo.ip+":"+xeusInfo.port+"/xeus-gate/getPTZPosition.json?cctvMgrNo="+data.fcltId,
			dataType   : "text",
			async      : false
		}).done(function(result) {
			const ptzData = JSON.parse(result);
			if(callback !== undefined) callback(data, ptzData, point, presetNo);
		});
	},
	/**
	 * 카메라의 PTZ 방향을 저장된 Preset 방향으로 이동시키는 함수
	 * @param {object} data - 카메라 데이터
	 * @param {string} presetNo - 카메라 프리셋 번호
	 * @param {object} ptzData - PTZ 데이터
	 * @param {string} speed - PTZ 이동 속도
	 * @function 
	 * @deprecated 지오맥스 연계 시에만 사용
	 */
	setXeusPtzPosition: function(data, presetNo, ptzData, speed) {
		mapManager.map.removeLayer(data.directionLayer);
		
		const pan = ptzData.pan;
		const tilt = ptzData.tilt;
		const zoom = ptzData.zoom;
		$.ajax({
			type       : "GET",
			url        : "http://"+xeusInfo.ip+":"+xeusInfo.port+"/xeus-gate/setPTZPosition.json?cctvMgrNo="+data.fcltId+"&pan="+pan+"&tilt="+tilt+"&zoom="+zoom,
			dataType   : "text",
			async      : false
		}).done(function(result) {
			mapManager.map.addLayer(data.directionLayer);
			getPresetPoint(data, presetNo, data.directionFeature, createPresetDirection);
		});
	},
	/**
	 * 카메라의 PTZ 방향을 사내 DB에서 읽어오고 software, hardware 별 카메라 preset control 방식에 따라 이동시키는 함수
	 * @param {object} data - 카메라 데이터
	 * @param {string} ch - 카메라 채널 번호
	 * @param {string} presetNo - 카메라 프리셋 번호
	 * @function 
	 * @deprecated 사내 VMS 연계시에만 사용
	 */
	presetCtrl : function(data, ch, presetNo) {
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
			var resultData = rows[0];
			
			if (!resultData) {
				return;
			}
			var cctvAgYn = data.cctvAgYn;
			var fcltPurposeCd = 0;
			var imageSrc = '../../images/icons/cctv/cctv_'+cctvAgYn+'_'+ fcltPurposeCd+'_'+ presetNo+'.png';
			setMarkerImage(facilityMarkers[ch], imageSrc);
			
			videoManager.insertCctvCtrlLog(resultData.cctvId, '1', resultData.presetNo);

			if(resultData.cctvKnd == "SW") {
				videoManager.swPresetMove(data, resultData.pan, resultData.tilt, resultData.zoom, resultData.focus, '60');
			}
			else {
				videoManager.hwPresetMove(data, resultData.presetNo, '60');
			}
	    });
	},
	/**
	 * Hardware 카메라의 PTZ 방향을 저장된 Preset 방향으로 이동시키는 함수
	 * @param {object} data - 카메라 데이터
	 * @param {string} ch - 카메라 채널 번호
	 * @param {string} presetNo - 카메라 프리셋 번호
	 * @function 
	 * @deprecated 사내 VMS 연동시 사용
	 */
	hwPresetMove : function(data, presetId, speed) {
		const jsonObj = {};
		jsonObj.code = "1200";
		jsonObj.node_id = data.nodeId;
		jsonObj.preset_id = presetId;
		jsonObj.speed = speed;
		jsonObj.svr_ip = data.vmsSvrIp;
		
		$.ajax({
			type       : "POST",
			url        : "http://" + serverInfo.midServerIp + ":" + serverInfo.midServerPort + "/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(data) {
		});
	},
	/**
	 * Software 카메라의 PTZ 방향을 저장된 Preset 방향으로 이동시키는 함수
	 * @param {object} data - 카메라 데이터
	 * @param {string} pan - pan 값
	 * @param {string} tilt - tile 값
	 * @param {string} zoom - zoom 값
	 * @param {string} focus - focus 값
	 * @param {string} speed - PTZ 이동 속도
	 * @function 
	 * @deprecated 사내 VMS 연동시 사용
	 */
	swPresetMove : function(data, pan, tilt, zoom, focus, speed) {
		const jsonObj = {};
		jsonObj.code = "1350";
		jsonObj.node_id = data.nodeId;
		jsonObj.pan = pan;
		jsonObj.tilt = tilt;
		jsonObj.zoom = zoom;
		jsonObj.focus = focus;
		jsonObj.speed = speed;
		jsonObj.svr_ip = data.vmsSvrIp;
		
		$.ajax({
			type       : "POST",
			url        : "http://" + serverInfo.midServerIp + ":" + serverInfo.midServerPort + "/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(data) {
		});
	},
	/**
	 * 카메라의 현재 방향 PTZ 값을 가져오는 함수(Software 카메라 일 경우에만 동작)
	 * @param {object} data - 카메라 데이터
	 * @function 
	 * @deprecated 사내 VMS 연계 시에만 사용
	 */
	getPtzDataReq : function(data) {
		const jsonObj = {};
		jsonObj.code = "1300";
		jsonObj.node_id = data.nodeId;
		jsonObj.svr_ip = data.vmsSvrIp;
		jsonObj.kind = '0';
		
		$.ajax({
			type       : "POST",
			url        : "http://" + serverInfo.midServerIp + ":" + serverInfo.midServerPort + "/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(data) {
		});
	},
	/**
	 * Hardware 카메라의 현재 방향을 카메라의 Preset 번호에 매칭하여 저장하는 함수
	 * @param {object} data - 카메라 데이터
	 * @param {string} presetId - 프리셋 번호
	 * @param {string} presetNm - 프리셋 이름 
	 * @function 
	 * @deprecated 사내 VMS 연계 시에만 사용
	 */
	addHwPreset : function(data, presetId, presetNm) {
		const jsonObj = {};
		jsonObj.code = "1700";
		jsonObj.node_id = data.nodeId;
		jsonObj.preset_id = presetId;
		jsonObj.preset_nm = presetNm;
		jsonObj.svr_ip = data.vmsSvrIp;
		
		$.ajax({
			type       : "POST",
			url        : "http://" + serverInfo.midServerIp + ":" + serverInfo.midServerPort + "/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(data) {
		});
	},
	/**
	 * Hardware 카메라의 현재 방향을 카메라의 Preset 번호에 매칭하여 삭제하는 함수
	 * @param {object} data - 카메라 데이터
	 * @param {string} presetId - 프리셋 번호
	 * @param {string} presetNm - 프리셋 이름 
	 * @function 
	 * @deprecated 사내 VMS 연계 시에만 사용
	 */
	removeHwPreset : function(data, presetId, presetNm) {
		const jsonObj = {};
		jsonObj.code = "1710";
		jsonObj.node_id = data.nodeId;
		jsonObj.preset_id = presetId;
		jsonObj.preset_nm = presetNm;
		jsonObj.svr_ip = data.vmsSvrIp;
		
		$.ajax({
			type       : "POST",
			url        : "http://" + serverInfo.midServerIp + ":" + serverInfo.midServerPort + "/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(data) {
		});
	},
	/**
	 * Hardware 카메라의 현재 방향을 카메라의 Preset 번호에 매칭하여 수정하는 함수
	 * @param {object} data - 카메라 데이터
	 * @param {string} presetId - 프리셋 번호
	 * @param {string} presetNm - 프리셋 이름 
	 * @function 
	 * @deprecated 사내 VMS 연계 시에만 사용
	 */
	updateHwPreset : function(data, presetId, presetNm) {
		const jsonObj = {};
		jsonObj.code = "1720";
		jsonObj.node_id = data.nodeId;
		jsonObj.preset_id = presetId;
		jsonObj.preset_nm = presetNm;
		jsonObj.svr_ip = data.vmsSvrIp;
		
		$.ajax({
			type       : "POST",
			url        : "http://" + serverInfo.midServerIp + ":" + serverInfo.midServerPort + "/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(data) {
		});
	},
	/**
	 * 사내 VMS에서 관리되는 카메라 리스트를 통합플랫폼 DB에 연동시키는 함수
	 * @function 
	 * @deprecated 사내 VMS 연계 시에만 사용
	 */
	getAllCenterList : function() {
		if(confirm("동기화 하시겠습니까?")) {
			$('#loading').toggle();
			const jsonObj = {};
			
			jsonObj.code = "3200";
			jsonObj.svr_ip = '172.20.20.100';
			jsonObj.send_kind = '1';
			jsonObj.client = '0';
			
			$.ajax({
				type       : "POST",
				url        : "http://" + serverInfo.midServerIp + ":" + serverInfo.midServerPort + "/api/athena/midsvr",
				dataType   : "text",
				data       : JSON.stringify(jsonObj),
				async      : false
			}).done(function(data) {
				setTimeout(function(){
					console.log('getAllCenterList Success!!');				
					alert("동기화가 성공하였습니다.");
					$('#loading').toggle();
				}, 3000);
			});
		}
	},
	/**
	 * 재생되고 있는 video element의 영상을 캡처하는 함수
	 * @param {object} data - 카메라 데이터
	 * @function 
	 * @deprecated 미사용
	 */
	videoCapture : function(data) {
		var video = document.getElementById(data.viewId);
		var canvas = document.createElement('canvas');
		canvas.width = 2560;
		canvas.height = 1440;
		var ctx = canvas.getContext('2d');
		
		ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
		
		var imgData = canvas.toDataURL('image/jpg');
		var blob = common.dataURItoBlob(imgData);
		var currentTime = moment().format('YYYYMMDDHHmmss');
		var fileName = currentTime + '_' + data.fcltId + '.jpg';
		var path = "D:/dscp_cctv/"
		
		common.canvasImgUpload(blob, fileName, path);
		return fileName;
	},
	/**
	 * 팝업의 크기를 조절 시 팝업 내부의 영상 및 컨트롤 UI의 크기를 Resize 시키는 함수
	 * @param {element} dialog - 영상 팝업 dialog element
	 * @function 
	 * @deprecated connectDialog로 이전 예정
	 */
	onPopupVideoResize : function(dialog) {
		var $btnArea = $(dialog).children('.video-wrap').children('.cctv-btn-wrap');
		// var $viewArea = $btnView.parent();
		
		// if ($viewArea.hasClass("fullscreen"))
		// $viewArea.removeClass("fullscreen");
		// else $viewArea.addClass("fullscreen");
		
		var width = $btnArea.width();
		var height = $btnArea.height();
		var hOffset = width / 2;
		var vOffset = height / 2;
		
		var left = 5;
		var center = hOffset - 13.5;
		var right = 5;
		
		var top = 5;
		var middle = vOffset - 13.5;
		var bottom = 5;

		$btnArea.children('.Left').css({left: (center-26)+"px", top: (middle)+"px"});
		$btnArea.children('.Right').css({left: (center+26)+"px", top: (middle)+"px"});
		$btnArea.children('.Up').css({left: (center)+"px", top: (middle-26)+"px"});
		$btnArea.children('.Down').css({left: (center)+"px", top: (middle+26)+"px"});
		$btnArea.children('.preset_1').css({left: (left)+"px", top: (top)+"px"});
		$btnArea.children('.preset_2').css({left: (center)+"px", top: (top)+"px"});
		$btnArea.children('.preset_3').css({right: (right)+"px", top: (top)+"px"});
		$btnArea.children('.preset_4').css({right: (right)+"px", top: (middle)+"px"});
		$btnArea.children('.preset_5').css({right: (right)+"px", bottom: (bottom)+"px"});
		$btnArea.children('.preset_6').css({left: (center)+"px", bottom: (bottom)+"px"});
		$btnArea.children('.preset_7').css({left: (left)+"px", bottom: (bottom)+"px"});
		$btnArea.children('.preset_8').css({left: (left)+"px", top: (middle)+"px"});
		$btnArea.children('.ZoomIn').css({left: (center)+"px", top: (middle)+"px"});
		$btnArea.children('.ZoomOut').css({left: (center)+"px", top: (middle+13)+"px"});
	},
	/**
	 * 영상의 크기를 조절 시 영상 관련 컨트롤 UI의 크기를 Resize 시키는 함수
	 * @param {object} data - 카메라 데이터
	 * @function 
	 * @deprecated 미사용
	 */
	onVideoResize : function(data) {
		var $btnView = $("#" + data.btnViewId);
		var $viewArea = $btnView.parent();
		
		if ($viewArea.hasClass("fullscreen")) $viewArea.removeClass("fullscreen");
		else $viewArea.addClass("fullscreen");
		
		var width = $btnView.width();
		var height = $btnView.height();
		var hOffset = width / 2;
		var vOffset = height / 2;
		
		var left = 5;
		var center = hOffset - 13.5;
		var right = 5;
		
		var top = 5;
		var middle = vOffset - 13.5;
		var bottom = 5;
		
		$btnArea = $btnView.children(".cctv_btn_area");

		$btnArea.children('.Left').css({left: (center-26)+"px", top: (middle)+"px"});
		$btnArea.children('.Right').css({left: (center+26)+"px", top: (middle)+"px"});
		$btnArea.children('.Up').css({left: (center)+"px", top: (middle-26)+"px"});
		$btnArea.children('.Down').css({left: (center)+"px", top: (middle+26)+"px"});
		$btnArea.children('.preset_1').css({left: (left)+"px", top: (top)+"px"});
		$btnArea.children('.preset_2').css({left: (center)+"px", top: (top)+"px"});
		$btnArea.children('.preset_3').css({right: (right)+"px", top: (top)+"px"});
		$btnArea.children('.preset_4').css({right: (right)+"px", top: (middle)+"px"});
		$btnArea.children('.preset_5').css({right: (right)+"px", bottom: (bottom)+"px"});
		$btnArea.children('.preset_6').css({left: (center)+"px", bottom: (bottom)+"px"});
		$btnArea.children('.preset_7').css({left: (left)+"px", bottom: (bottom)+"px"});
		$btnArea.children('.preset_8').css({left: (left)+"px", top: (middle)+"px"});
		$btnArea.children('.ZoomIn').css({left: (center)+"px", top: (middle)+"px"});
		$btnArea.children('.ZoomOut').css({left: (center)+"px", top: (middle+13)+"px"});
	},
	/**
	 * 영상의 크기를 fullscreen으로 변경하는 함수
	 * @param {object} data - 카메라 데이터
	 * @function 
	 * @deprecated 미사용
	 */
	onVideoFullScreen : function(data) {
		var elem = document.getElementById(data.viewId).parentElement;
		if (elem.requestFullscreen) {
			elem.requestFullscreen();
		} else if (elem.mozRequestFullScreen) { /* Firefox */
			elem.mozRequestFullScreen();
		} else if (elem.webkitRequestFullscreen) { /* Chrome, Safari and Opera */
			elem.webkitRequestFullscreen();
		} else if (elem.msRequestFullscreen) { /* IE/Edge */
			elem.msRequestFullscreen();
		}
	},
	/**
	 * 저장영상 재생 속도 변경 함수
	 * @param {object} data - 카메라 데이터
	 * @param {string} speed - 영상 속도
	 * @function 
	 * @deprecated 미사용 (사내 VMS 연동 시 사용)
	 */
	setStorageSpeed : function(data, speed) {
		const jsonObj = {};
		jsonObj.code = "2110";
		jsonObj.node_id = data.nodeId;
		jsonObj.speed = speed;
		jsonObj.rtsp_url = data.rtspUrl;
		jsonObj.svr_id = data.vmsSvrIp;
		
		$.ajax({
			type       : "POST",
			url        : "http://" + serverInfo.midServerIp + ":" + serverInfo.midServerPort + "/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(data) {
		});
	},
	/**
	 * 영상 재생 로그 저장 함수
	 * @param {object} data - 카메라 데이터
	 * @param {string} type - 영상 타입 (실시간, 저장영상)
	 * @function 
	 * @deprecated 미사용 (사내 VMS 연동 시 사용)
	 */
	addCctvViewLog : function(data, type) {
		const jsonObj = {};
		jsonObj.fcltId = data.fcltId;
		jsonObj.fcltNm = data.fcltNm;
		jsonObj.userId = opener.document.getElementById('loginId').value;
		jsonObj.sessionId = clientInfo.sessionId;
		jsonObj.type = type;
		jsonObj.storageStartTime = data.sTime !== undefined ? data.sTime : null;
		jsonObj.storageEndTime = data.eTime !== undefined ? data.eTime : null;
		
		$.ajax({
            contentType : "application/json; charset=utf-8",
			type        : "POST",
			url         : "/ajax/insert/facility.insertCctvViewLog/action",
			dataType    : "json",
			data        : JSON.stringify(jsonObj),
			async      : false,
			beforeSend : function(xhr) {
				// 전송 전 Code
			}
		}).done(function (row) {
	    });
	},
	/**
	 * 영상 control 로그 저장 함수
	 * @param {string} fcltId - 카메라 아이디
	 * @param {string} type - 영상 타입 (실시간, 저장영상)
	 * @param {string} kinds - PTZ 또는 preset 번호
	 * @function 
	 * @deprecated 미사용 (사내 VMS 연동 시 사용)
	 */
	insertCctvCtrlLog : function(fcltId, type, kinds) {
		const jsonObj = {};
		jsonObj.type = type;
		jsonObj.kinds = kinds;
		jsonObj.fcltId = fcltId;
		jsonObj.userId = opener.document.getElementById('loginId').value;
		
		$.ajax({
		    contentType : "application/json; charset=utf-8",
			type       : "POST",
			url        : "/ajax/insert/facility.insertCctvCtrlLog/action",
			dataType   : "json",
			data       : JSON.stringify(jsonObj),
			async      : false,
			beforeSend : function(xhr) {
				// 전송 전 Code
			}
		}).done(function (row) {
	    });
	}
};

/**
 * 영상 프리셋 및 방향에 대한 함수 모음.
 * @namespace preset
 * */


/**
 * 지정 된 카메라의 현재 PTZ 방향을 가져오는 함수.
 * @function preset.getPtzf
 * @param {object} data - 카메라 데이터
 * @param {string} presetNo - 프리셋 번호
 * @param {object} point - 좌표 데이터
 * @param {preset.saveSwPreset} callback - 전달받은 PTZ 값을 저장하는 callback function
 * @see {@link preset.saveSwPreset} - preset.saveSwPreset
 * */
function getPtzf(data, presetNo, point, callback) {
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url         : "/select/facility.getFacilityPresetTemp/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async       : false,
		beforeSend  : function(xhr) {
			// 전송 전 Code
		}
	}).done(function (result) {
		const rows = result.rows;
    	if(rows=='sessionOut'){
			alert('로그인 시간이 만료되었습니다.');
			closeWindow();
		}
		if(rows.length == 0) {
			getPtzf(data, presetNo, point, callback);
		} else {
			callback(data, rows[0], point, presetNo, deletePtzfTemp);
		}
    }).fail(function() {
    	getPtzf(data, presetNo, point, callback);
    });
	
	/*
	 * const jsonObj = {}; jsonObj.cctvId = data.fcltId;
	 * 
	 * setTimeout(function() { $.ajax({ type : "POST", url :
	 * "/select/facility.getFacilityPresetTemp/action.do", dataType : "json",
	 * data : { "param" : JSON.stringify(jsonObj) }, async : false, beforeSend :
	 * function(xhr) { // 전송 전 Code } }).done(function (row) { if(row.length ==
	 * 0) { getPtzf(data, presetNo, point, callback); } else { callback(data,
	 * row[0], point, presetNo, deletePtzfTemp); } }).fail(function() {
	 * getPtzf(data, presetNo, point, callback); }); }, 2000);
	 */
}

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
	
	const ePoint = ol.proj.transform([presetData.lon, presetData.lat],mapManager.properties.pro4j[mapManager.properties.type], mapManager.projection);
	const sPoint = ol.proj.transform([data.lon, data.lat],mapManager.properties.pro4j[mapManager.properties.type], mapManager.projection);
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
			const point = ol.proj.transform(temp,'EPSG:5181','EPSG:4326');
			saveDirection(data, point, btn);
		}
	});
	
	content.addEventListener('mousemove', function(e) {
		const x = e.target.parentElement.offsetLeft + e.offsetX;
		const y = e.target.parentElement.offsetTop + e.offsetY;
		const ePoint = mapManager.map.getCoordinateFromPixel([x, y]);
		const sPoint = ol.proj.transform([data.lon, data.lat],mapManager.properties.pro4j[mapManager.properties.type], mapManager.projection);
		setDirectionFeatureGeom(sPoint, ePoint, directionFeature);
	});
	
	return content;
}

/**
 * preset 방향에 맞춰 directionFeature를 그릴 overlay를 생성하는 함수.
 * @function preset.createDirectionOverlay
 * @param {object} feature - 지도 마커 데이터
 * @param {object} data - 카메라 데이터
 * @param {object} diectionFeature - 방향 표시 feature 데이터
 * @param {element} btn - 방향 선택 element
 * @see {@link videoManager.createButton} - videoManager.createButton
 * */
function createDirectionOverlay(feature, data, directionFeature, btn) {
	const lat = parseFloat(data.lat);
	const lon = parseFloat(data.lon);
	
	const content = createDirectionOverlayContent(data, directionFeature, btn);
	
	const overlay = new ol.Overlay({
		position : ol.proj.transform([lon,lat],'EPSG:4326','EPSG:5181'),
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
 * @param {string} no - 프리셋 번호
 * @param {object} feature - 지도 마커 데이터
 * @param {object} data - 카메라 데이터
 * @param {boolean} isSaved - 이미 저장된 번호 인지 확인
 * @see {@link preset.createPresetNumOverlays} - preset.createPresetNumOverlays
 * */
function getPresetnumOverlayContent(no, feature, data, isSaved) {
	const positionBtn = document.createElement('div');
	positionBtn.classList.add('get_position_btn');
	if(isSaved) {
		positionBtn.classList.add('selected');
	}
	const a = document.createElement('span');
	// a.setAttribute('href', '#');
	positionBtn.innerHTML = no;
	
	positionBtn.appendChild(a);
	positionBtn.addEventListener('click', function(e) {
		if(confirm(no+"번으로 등록하시겠습니까?")) {
			$('#loading').toggle();
			const x = e.target.parentElement.offsetLeft + e.offsetX;
			const y = e.target.parentElement.offsetTop + e.offsetY;
			const temp = mapManager.map.getCoordinateFromPixel([x, y]);
			const point = ol.proj.transform(temp,'EPSG:5181','EPSG:4326');
			
			if(data.fcltSh == "SW") {
				videoManager.getXeusPtzPosition(data, no, point, saveSwPreset);
				// videoManager.getPtzDataReq(data);
				// getPtzf(data, no, point, saveSwPreset);
			} else {
				saveHwPreset(data, point, no);
			}
			
			e.target.classList.add('selected');
		}
	});

	return positionBtn;
}

/**
 * 회전형 카메라의 preset 설정시 1~8번의 번호를 그리기 위해 DB에서 해당 카메라의 preset 설정 정보를 읽어오는 함수.
 * @function preset.createPresetNumOverlays
 * @param {object} feature - 지도 마커 데이터
 * @param {object} data - 카메라 데이터
 * @param {Array} list - overlay 데이터를 저장 할 배열 
 * @see {@link videoManager.createButton} - videoManager.createButton
 * */
function createPresetNumOverlays(feature, data, list) {
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
				const presetNo = presetData[j].presetNo;
				if (i == presetNo) {
					flag = true;
					break;
				}
			}
			
			const content = getPresetnumOverlayContent(i, feature, data, flag);
			
			const overlay = new ol.Overlay({
				position : ol.proj.transform([lon,lat],'EPSG:4326','EPSG:5181'),
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
 * 이 function은 videoManager.getXeusPtzPosition가 실행되고 난 뒤 실행 될 function입니다.
 * xeus에서 넘겨준 PTZ 데이터를 저장한다.
 * @callback preset.saveSwPreset
 * @param {object} data - 카메라 데이터
 * @param {object} ptzData - 카메라 PTZ 데이터
 * @param {object} point - 좌표 데이터
 * @param {string} presetNo - PTZ 번호
 * @see {@link videoManager.getXeusPtzPosition} - videoManager.getXeusPtzPosition
 * */
var saveSwPreset = function(data, ptzData, point, presetNo) {
	const jsonObj = {};
	
	jsonObj.cctvId = data.fcltId;
	jsonObj.cctvKnd = data.fcltSh;
	jsonObj.presetNo = presetNo;
	jsonObj.presetName = presetNo;
	jsonObj.pan = ptzData.pan;
	jsonObj.tilt = ptzData.tilt;
	jsonObj.zoom = ptzData.zoom;
	jsonObj.focus = '0';
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
		// if(callback !== undefined) callback(data);
		alert('프리셋 등록 완료');
		$('#loading').toggle();
	});
}

/**
 * 사내 VMS와 연동 시 임시로 DB에 저장하는 Preset 데이터를 지우는 함수.
 * @callback preset.deletePtzfTemp
 * @param {object} data - 카메라 데이터
 * @see {@link preset.getPtzf} - preset.getPtzf
 * @deprecated 사내 VMS 연계시에만 사용
 * */
var deletePtzfTemp = function(data) {
	const jsonObj = {};
	jsonObj.node_id = data.nodeId;
	jsonObj.vms_svr_ip = data.vmsSvrIp;
	
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url         : "/ajax/delete/agent.deletePtzData/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async      : false
	}).done(function(data) {
		alert('프리셋 등록 완료');
		$('#loading').toggle();
	});
}

/**
 * Hardware 프리셋을 저장하는 함수.
 * @callback preset.saveHwPreset
 * @param {object} data - 카메라 데이터
 * @param {object} point - 좌표 데이터
 * @param {string} presetNo - 프리셋 번호
 * @see {@link preset.getPresetnumOverlayContent} - preset.getPresetnumOverlayContent
 * */
var saveHwPreset = function(data, point, presetNo) {
	const jsonObj = {};
	jsonObj.cctvId = data.fcltId;
	jsonObj.presetNo = '2'+presetNo;	// 21~29
	jsonObj.presetName = jsonObj.presetNo;
	jsonObj.lat = point[1];
	jsonObj.lon = point[0];
	jsonObj.cctvKnd = data.cctvKnd;
	
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url         : "/ajax/insert/facility.saveFacilityPreset/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async       : false,
		beforeSend  : function(xhr) {
			// 전송 전 Code
		}
	}).done(function (result) {
		if (result == "SUCCESS") {
			alert('프리셋 등록 완료');
			if (data.cctvAgYn != '0') videoManager.addHwPreset(data, jsonObj.presetNo, jsonObj.presetNo);
		}
		else {
			alert("프리셋 등록 실패");
		}
		$('#loading').toggle();
	}).fail(function (xhr) {
		alert("프리셋 등록 실패");
	}).always(function() {
		
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

$(function() {
	$(window).unload(function(e){
		videoManager.stopPopup();
		videoManager.stopPlayer();
		return '';
	});

	videoManager.init();
});
