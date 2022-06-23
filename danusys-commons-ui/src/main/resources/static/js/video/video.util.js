/**
 * 영상 관련 관리 기능
 * @author - 
 * @version 0.1.1
 * @class videoManager
 * @property {Map} playList - 재생중인 비디오 저장 Map
 * @property {Object} prop - 비디오 관련 설정 데이터
 * @property {String} prop.reqServerIp - 요청 서버 아이피
 * @property {String} prop.reqServerPort - 요청 서버 포트
 * @property {String} prop.clientIp - 사용자 아이피
 * @property {String} prop.sessionId - 사용자 세션 아이디
 * @property {String} prop.systemType - 플레이어를 제공하는 시스템 타입
 * @property {Class} player - 시스템 별 플레이어 동작 방식을 결정하는 Class 
 * */
var videoManager = {
	playList : new Map(),
	prop : {
		reqServerIp : '121.170.199.216',
		reqServerPort : '8000',
		midServerIp : '10.1.105.14',
		midServerPort : '10003',
		systemType : 'danu',
		mediaAuthority : 'N'
	},
	player : undefined,
	/**
	 * log 출력 함수.
	 * @function videoManager.log
	 * @param {string} msg - 로그 메세지
	 */
	log : function(msg) {
		console.log('videoManager : ' + msg);
	},
	init : function(option) {
		this.setProperty(option);
	},
	setProperty : function(option) {
		if(option.midServerIp !== undefined) this.setMidServerIp(option.midServerIp);
		if(option.mediaAuthority !== undefined) this.prop.mediaAuthority = option.mediaAuthority;
		if(option.systemType !== undefined) this.prop.systemType = option.systemType;
		if(option.clientIp !== undefined) this.prop.clientIp = option.clientIp;
	},
	setMidServerIp : function(midServerIp) {
	    const jsonObj = {};
	    jsonObj.searchIp = midServerIp.substring(0, midServerIp.lastIndexOf('.') - 1);
	    jsonObj.port = videoManager.prop.midServerPort;

	    $.ajax({
	      contentType : "application/json; charset=utf-8",
	      type        : 'POST',
	      url         : '/select/oprt.getNetMappingSubList/action',
	      dataType    : 'json',
	      data        : JSON.stringify(jsonObj),
	      async: true
	    }).done(function(result) {
	    	const rows = result.rows;

	    	if(rows.length === 0) return;
	    	
	    	const d = rows[0];
	    	
	    	videoManager.prop.midServerIp = d.ip; 
	    });
	},
	/**
	 * fcltId와 동일한 아이디로 재생중인 카메라 데이터를 return 하는 함수.
	 * @function videoManager.getVideoData
	 * @param {string} fcltId - 시설물 아이디
	 * @returns {object} - 카메라 데이터 
	 */
	getVideoData : function(fcltId) {
		return $('#' + this.playList.get(fcltId)).data('data');
	},
	/**
	 * fcltId와 동일한 아이디로 재생중인 영상이 있는지 확인하는 함수.
	 * @function videoManager.isPlaying
	 * @param {string} fcltId - 시설물 아이디
	 * @returns {boolean} - true : 재생 가능, false : 재생 불가
	 */
	isPlaying : function(fcltId) {
		if(this.playList.has(fcltId)) {
			commonLog.error('이미 재생중인 영상입니다.', {isAutoClose: true});
			return false;
		}
		return true;
	},
	/**
	 * 비디오 플레이어 생성 시 현재 페이지나 기능에 맞는 type을 읽어오는 함수.
	 * @function videoManager.getVideoType
	 * @param {boolean} flag - 영상 권한
	 * @returns {string} - 비디오타입(e : [미사용], x : 영상권한 없음, c : 순환감시(?)[미사용], s : 프리셋 세팅, p : [미사용])
	 */
	getVideoType : function(isEvent) {
		const param = getUrlParams();
		const type = isEvent === true ? 'e' : this.prop.mediaAuthority !== 'Y' ? 'x' : param.path.indexOf('set') > -1 ? $('#setCirclr').is(':visible') ? 'c' : 's' : 'p';
		return type;
	},
	/**
	 * 비디오 권한에 대한 체크를 한다.
	 * @function videoManager.checkPermission
	 * @param {boolean} isEvent - event에 대한 재생을 할 경우에는 영산 권한 체크를 제외한다.
	 * @returns {boolean} - true : 영상 재상, false : 영상 재생 불가
	 * */
	checkPermission : function(type) {
		if(type === 'x') {
			commonLog.error('영상 재생 권한이 없습니다. 관리자에게 권한 요청을 하십시오.', {isAutoClose: true});
			return false;
		}
		return true;
	},
	/**
	 * video에 Button UI를 생성하는 함수.
	 * @function videoManager.createButton
	 * @param {object} option - option 데이터
	 * @param {object} option.data - 카메라 데이터
	 * @param {element} option.parent - 부모 Element
	 * @param {boolean} option.isSite - 개소감시 여부
	 * @param {string} option.type - video 종류
	 */
	createButton : function(option) {
		const data = option.data;
		const parent = option.parent;
		const isSite = option.isSite;
		const type = option.type;
		
		let btnList = $('<div>').addClass('btn_wrap btn_area_center');
		const siteList = parent.data('siteList');
		
		if(!isSite) {
			let a = $('<a>').attr('href', '#').addClass('btn site_btn').html('개소감시').bind('click', function(e) {
				siteMntr(data);
			});
			
			btnList.append(a);
		}
		
		let c = $('<a>').attr('href', '#').addClass('btn');

		let bSpan = $('<span>').addClass('slider round');
		let bText = $('<span>').addClass('switch-text off').html('OFF');
		let bInput = $('<input>').attr('type', 'checkbox').bind({
			'click': function(e) {
				if($(this).hasClass('active') && $(c).hasClass('active')) {
					$(c).click();
				}
				if($(this).hasClass('active')) {
					bText.html('OFF');
					bText.removeClass('on').addClass('off');
					$(this).removeClass('active');
					$(parent).children('.video_wrap').children('.cctv-btn-wrap').removeClass('active');
					mapManager.map.removeLayer(data.directionLayer);
				} else {
					bText.html('ON');
					bText.removeClass('off').addClass('on');
					$(this).addClass('active');
					$(parent).children('.video_wrap').children('.cctv-btn-wrap').addClass('active');
				}
			},
		});
		
		let b = $('<label>').addClass('switch position_left').append(bInput, bSpan, bText);
		
		btnList.append(b);
		
		let d = $('<a>').attr('href', '#').addClass('btn').html('상세정보').bind('click', function(e) {
			if($(this).hasClass('active')) {
				$(this).removeClass('active');
				$(parent).children('.detail-wrap').slideUp();
			} else {
				$(this).addClass('active');
				$(parent).children('.detail-wrap').slideDown();
			}
		});
		
		btnList.append(d);

		if(type == 's' && data.cctvAgYn == '1') {
			option.overlayList = [];
			c.html('프리셋 설정');
			
			c.bind({
				'click': function(e) {
					if(!$(bInput).hasClass('active') && !$(this).hasClass('active')) {
						$(bInput).click();
					}
					
					if($(this).hasClass('active')) {
						removeBtns();
						$(this).removeClass('active');
						$(bInput).click();
					} else {
						mapManager.setCenter(ol.proj.transform([data.lon, data.lat], mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection));
						mapManager.setZoom(20);
						createPresetNumOverlays(option);
						$(this).addClass('active');
					}
				},
			})
			
			function removeBtns() {
				for(var i = 0, max = option.overlayList.length; i < max; i++) {
					mapManager.map.removeOverlay(option.overlayList[i]);
				}
			}
			
			option.removeBtns = removeBtns;
			
			btnList.append(c);
		} else if(type == 's' && data.cctvAgYn == '0') {
			var directionOverlay = undefined;
			c.html('방향 설정');
			
			const directionLayer = new ol.layer.Vector({
				source: new ol.source.Vector()
			});

			const directionFeature = new ol.Feature({});
			directionLayer.getSource().addFeature(directionFeature);
			
			c.bind({
				'click': function(e) {
					if($(this).hasClass('active')) {
						mapManager.map.removeLayer(directionLayer);
						mapManager.map.removeOverlay(directionOverlay);
						$(this).removeClass('active');
						// $(parent).children('.close').show();
					} else {
						mapManager.map.addLayer(directionLayer);
						mapManager.setCenter(ol.proj.transform([data.lon, data.lat], mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection));
						mapManager.setZoom(13);
						directionOverlay = createDirectionOverlay(data, directionFeature, this);
						data.directionOverlay = directionOverlay;
						$(this).addClass('active');
						// $(parent).children('.close').hide();
					}
				},
			});
			
			data.directionLayer = directionLayer;
			btnList.append(c);
		}
		
		$(parent).append(btnList);
	},
	/**
	 * 비디오 팝업에 이벤트 지정. context menu 이벤트, ptz 및 preset 버튼 이벤트 지정.
	 * @function videoManager.createPopupPlayerEvent
	 * @param {element} dialog - dialog element(conenctDialog에서 생성 됨)
	 * @param {object} data - 카메라 데이터
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
	},
	/**
	 * video element에 영상 상세정보 UI 생성
	 * @function videoManager.createDetailView
	 * @param {object} data - 카메라 데이터
	 * @param {element} dialog - dialog element(conenctDialog에서 생성 됨)
	 */
	createDetailView : function(data, dialog) {
		let wrap = $('<ul>').addClass('detail-wrap');
		
		let left1 = $('<span>').html('명칭');
		let right1 = $('<span>').html(data.fcltNm);
		let child1 = $('<li>').append(left1, right1);
		
		let left2 = $('<span>').html('노드 ID');
		let right2 = $('<span>').html(data.nodeId);
		let child2 = $('<li>').append(left2, right2);
		
		let left3 = $('<span>').html('용도');
		let right3 = $('<span>').html(data.fcltPuposeNm);
		let child3 = $('<li>').append(left3, right3);
		
		let left4 = $('<span>').html('SW/HW');
		let right4 = $('<span>').html(data.fcltSh);
		let child4 = $('<li>').append(left4, right4);
		
		wrap.append(child1, child2, child3, child4);
		
		$(dialog).append(wrap);
	},
	/**
	 * player element 생성 함수
	 * @function videoManager.createPlayer
	 * @param {object} option - 비디오 재생 관련 option
	 * @param {element} option.parent - 부모 element
	 * @param {object} option.data - 카메라 data
	 * @param {object} option.timestamp - 저장영상 시작 및 종료 시간 데이터
	 * @param {boolean} option.isEvent - event 관련해서 재생 되는 경우에는 권한 체크를 하지 않게 하는 값
	 * @param {boolean} option.isDetail - 상세보기를 생성할지 선택하는 값(default : true);
	 * @param {boolean} option.isButton - 버튼 영역을 생성할지 선택하는 값(default : true);
	 * @returns {boolean} - true : 영상 재생 성공, false : 영상 재생 실패
	 * */
	createPlayer : function(option) {
		const parent = option.parent;
		const data = option.data;
		// const isEvent = option.isEvent === undefined ? false : option.isEvent;
		// const isSite = option.isSite === undefined ? false : option.isSite;
		// const isDetail = option.isDetail === undefined ? true : option.isDetail;
		// const isButton = option.isButton === undefined ? true : option.isButton;
		// const type = this.getVideoType(isEvent);
		
		// option.type = type;
		
		// if(!this.checkPermission(type)) {
		// 	return false;
		// }
		
		const viewId = 'video' + data.facilitySeq;
		
		option.viewId = viewId.replace(/\(/g, '').replace(/\)/g, '');
		
		option.player = new videoManager.player(data.kind);
		
		option.player.createElements(option);
		// option.player.createPtzControls(option);
		option.player.play(option);
		
		// if(isDetail) {
		// 	videoManager.createDetailView(data, parent);
		// }
		
		// if(isButton) {
		// 	videoManager.createButton(option);
		// }
		
		// videoManager.insertCctvViewLog(option);
		
		videoManager.playList.set(data.facilitySeq, viewId);
		
		parent.bind({
			'remove' : function(e) {
				if(typeof option.removeBtns === 'function') option.removeBtns();
				videoManager.playList.delete(option.facilitySeq);
				option.player.stop(option);
			},
			'resize transitionend' : function(e) {
				option.player.onPopupVideoResize(parent);
			}
		});
		
		return true;
	},
	/**
	 * 영상 재생 로그 저장 함수
	 * @function videoManager.insertCctvViewLog
	 * @param {object} option - option 데이터
	 * @param {object} option.data - 카메라 데이터
	 * @param {string} option.type - 영상 타입 (실시간, 저장영상)
	 */
	insertCctvViewLog : function(option) {
		const data = option.data;
		const timestamp = option.timestamp;
		
		const jsonObj = {};
		jsonObj.fcltId = data.fcltId;
		jsonObj.fcltNm = data.fcltNm;
		jsonObj.userId = opener.document.getElementById('loginId').value;
		jsonObj.sessionId = this.prop.sessionId;
		jsonObj.type = timestamp ? 'S' : 'R';
		jsonObj.storageStartTime = timestamp ? timestamp.sTime : null;
		jsonObj.storageEndTime = timestamp ? timestamp.eTime : null;
		
		$.ajax({
		    contentType : "application/json; charset=utf-8",
			type        : "POST",
			url         : "/ajax/insert/facility.insertCctvViewLog/action",
			dataType    : "json",
			data        : JSON.stringify(jsonObj),
			async      : true,
			beforeSend : function(xhr) {
				// 전송 전 Code
			}
		}).done(function (row) {
	    });
	},
	/**
	 * 영상 control 로그 저장 함수
	 * @function videoManager.insertCctvCtrlLog
	 * @param {string} fcltId - 카메라 아이디
	 * @param {string} type - 영상 타입 (실시간, 저장영상)
	 * @param {string} kinds - PTZ 또는 preset 번호
	 */
	insertCctvCtrlLog : function(fcltId, type, kinds) {
		const jsonObj = {};
		jsonObj.type = type;
		jsonObj.kinds = kinds;
		jsonObj.fcltId = fcltId;
		jsonObj.userId = opener.document.getElementById('loginId').value;
		
		$.ajax({
		    contentType : "application/json; charset=utf-8",
			type        : "POST",
			url         : "/ajax/insert/facility.insertCctvCtrlLog/action",
			dataType    : "json",
			data        : JSON.stringify(jsonObj),
			async       : true,
			beforeSend  : function(xhr) {
				// 전송 전 Code
			}
		}).done(function (row) {
	    });
	},
	/**
	 * 이 function은 player.getPtzPosition가 실행되고 난 뒤 실행 될 function입니다.
	 * @function videoManager.saveSwPreset
	 * @param {object} data - 카메라 데이터
	 * @param {object} ptzData - 카메라 PTZ 데이터
	 * @param {object} point - 좌표 데이터
	 * @param {string} presetNo - PTZ 번호
	 * @see {@link player.getPtzPosition} - player.getPtzPosition
	 * */
	saveSwPreset : function(data, ptzData, point, presetNo) {
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
			async       : true
		}).done(function(result) {
			if(videoManager.prop.systemType === 'danu') {
				videoManager.deletePtzfTemp(data);
			} else {
				alert('프리셋 등록 완료');
				$('#loading').toggle();
			}
		});
	},
	/**
	 * 사내 VMS와 연동 시 임시로 DB에 저장하는 Preset 데이터를 지우는 함수.
	 * @function videoManager.deletePtzfTemp
	 * @param {object} data - 카메라 데이터
	 * @see {@link player.getPtzf} - player.getPtzf
	 * @deprecated 사내 VMS 연계시에만 사용
	 * */
	deletePtzfTemp : function(data) {
		const jsonObj = {};
		jsonObj.fcltId = data.fcltId;
		
		$.ajax({
		    contentType : "application/json; charset=utf-8",
			type        : "POST",
			url         : "/ajax/delete/agent.deletePtzData/action",
			dataType    : "json",
			data        : JSON.stringify(jsonObj),
			async      : true
		}).done(function(data) {
			alert('프리셋 등록 완료');
			$('#loading').toggle();
		});
	},
	/**
	 * Hardware 프리셋을 저장하는 함수.
	 * @function videoManager.saveHwPreset
	 * @param {object} data - 카메라 데이터
	 * @param {object} point - 좌표 데이터
	 * @param {string} presetNo - 프리셋 번호
	 * @see {@link preset.getPresetnumOverlayContent} - preset.getPresetnumOverlayContent
	 * */
	saveHwPreset : function(data, point, presetNo) {
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
			async       : true,
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
	},
	/**
	 * Hardware 카메라의 현재 방향을 카메라의 Preset 번호에 매칭하여 저장하는 함수
	 * @function videoManager.addHwPreset
	 * @param {object} data - 카메라 데이터
	 * @param {string} presetId - 프리셋 번호
	 * @param {string} presetNm - 프리셋 이름 
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
			url        : "http://"+videoManager.prop.midServerIp+':'+videoManager.prop.midServerPort+"/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : true
		}).done(function(data) {
		});
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
	 * Vurix 카메라 PTZ 제어 값 변환 함수
	 * @param {string} cmd - PTZ 방향
	 * @returns {number} - 10진수 형식
	 * @function 
	 */
	switchPtzValueForVurix : function (cmd) {
		var ptzCtrlMode;
		
		if(cmd=='stop') ptzCtrlMode = 14;
		else if(cmd=='left') ptzCtrlMode = 4;
		else if(cmd=='right') ptzCtrlMode = 6;
		else if(cmd=='up') ptzCtrlMode = 2;
		else if(cmd=='down') ptzCtrlMode = 8;
		else if(cmd=='zoomIn') ptzCtrlMode = 12;
		else if(cmd=='zoomOut') ptzCtrlMode = 13;
		else if(cmd=='focusNear') ptzCtrlMode = 10;
		else if(cmd=='focusFar') ptzCtrlMode = 11;
		
		return ptzCtrlMode;
	},
	/**
	 * 카메라 PTZ 이동 함수 (VMS 중계서버 연계)
	 * @param {object} data - 카메라 데이터
	 * @param {string} presetNo - 카메라 프리셋 번호
	 * @function 
	 */
	presetCtrl : function(data, presetNo) {
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
			// setMarkerImage(facilityMarkers[ch], imageSrc);
			
			videoManager.insertCctvCtrlLog(resultData.cctvId, '1', resultData.presetNo);

			if(resultData.cctvKnd == "SW") {
				videoManager.swPresetMove(data, resultData.pan,
						resultData.tilt, resultData.zoom, resultData.focus, '60');
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
			url        : "http://" + videoManager.prop.midServerIp + ":" + videoManager.prop.midServerPort + "/api/athena/midsvr",
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
			url        : "http://" + videoManager.prop.midServerIp + ":" + videoManager.prop.midServerPort + "/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(data) {
		});
	},
}

/**
 * Common Player
 * @author -
 * @version 0.0.1
 * @class commonPlayer
 * */
var commonPlayer = {
	/**
	 * 영상 재생 함수.
	 * @function danuPlayer.play
	 * @param {object} option - option 데이터
	 * */
	play : function(option) {
		const data = option.data;
		const viewId = option.viewId;

		// data.ip = videoManager.prop.midServerIp;
		// data.port = videoManager.prop.midServerPort;
		// data.clientSessionId = videoManager.prop.clientIp;
		//data.mediaServerWsUrl = 'ws://200.0.30.110:8888/kurento'

		// const apiOption = {
		// 	callUrl : "/cudo/media/play-broadcast",
		// 	videoId : option.videoId,
		// 	jobId : option.jobId
		// }
		//
		// $.ajax({
		// 	url: `${location.origin}/api/call`,
		// 	method: "POST",
		// 	contentType: "application/json",
		// 	data: JSON.stringify(apiOption)
		// }).done((d) => {
		// 	debugger;
		// 	//video.directVideoStart(data, viewId);
		// });

		video.directVideoStart(data, viewId);

		$('#' + viewId).data('data', data);
	},
	/**
	 * 영상 종료 함수.
	 * @function danuPlayer.stop
	 * @param {object} option - option 데이터
	 * */
	stop : function(option) {
		const viewId = option.viewId;

		video.directVideoStop(viewId);
		//webrtcserver.disconnect();
	},
	/**
	 * 영상 재생 영역 생성 함수.
	 * @function danuPlayer.createElements
	 * @param {object} option - option 데이터
	 * */
	createElements : function(option) {
		const data = option.data;
		const viewId = option.viewId;
		const parent = option.parent;

		const video = $('<video>').addClass('video_content').attr({'id': viewId, 'autoplay': true, 'muted': true});
		//const video = $('<video>').addClass('video_content').attr({'id': viewId, 'title': data.fcltId});

		const popupVideo = $('<div>').addClass('video_wrap').append(video);

		parent.append(popupVideo);
	}
}

/**
 * Danusys Player
 * @author - 
 * @version 0.0.1
 * @class danuPlayer
 * */
var danuPlayer = {
	/**
	 * 영상 재생 함수.
	 * @function danuPlayer.play
	 * @param {object} option - option 데이터
	 * */
	directPlay : function(option) {

	},
	play : function(option) {
		const data = option.data;
		const timestamp = option.timestamp;
		const viewId = option.viewId;
		const isDirect = option.isDirect;
		
		data.ip = videoManager.prop.midServerIp;
		data.port = videoManager.prop.midServerPort;
		data.clientSessionId = videoManager.prop.clientIp;
		//data.mediaServerWsUrl = 'ws://200.0.30.110:8888/kurento'
		
		/*const webrtcConfig = {
			defaultvideostream: "Bunny",
			layoutextraoptions: "&width=320&height=0",
			options: "rtptransport=tcp&timeout=60",
			url: "http://10.6.50.224:8000"
		}
		
		data.fcltId = '21473_1_1';
		
		const webrtcserver = new libwebrtc(viewId, webrtcConfig.url);
		let options = webrtcConfig.options;
		options += webrtcConfig.layoutextraoptions;
		
		webrtcserver.connect(data.fcltId, undefined, options);
		
		option.webrtcserver = webrtcserver;*/
		
		if (isDirect) {
			video.directVideoStart(data, viewId);
		} else if(timestamp) {
			data.sTime = moment(timestamp.sTime, 'YYYY.MM.DD.HH.mm.ss').format('YYYY.MM.DD.HH.mm.ss');
			data.eTime = moment(timestamp.eTime, 'YYYY.MM.DD.HH.mm.ss').format('YYYY.MM.DD.HH.mm.ss');
			video.singleSaveVideoStart(data, viewId);
		} else {
			video.singleVideoStart(data, viewId);
		}
		
		$('#' + viewId).data('data', data);
	},
	/**
	 * 영상 종료 함수.
	 * @function danuPlayer.stop
	 * @param {object} option - option 데이터
	 * */
	stop : function(option) {
		const viewId = option.viewId;
		
		video.singleVideoStop(viewId);
		//webrtcserver.disconnect();
	},
	/**
	 * 영상 재생 영역 생성 함수.
	 * @function danuPlayer.createElements
	 * @param {object} option - option 데이터
	 * */
	createElements : function(option) {
		const data = option.data;
		const viewId = option.viewId;
		const parent = option.parent;

		const video = $('<video>').addClass('video_content').attr({'id': viewId, 'autoplay': true, 'muted': true});
		//const video = $('<video>').addClass('video_content').attr({'id': viewId, 'title': data.fcltId});
		
		const popupVideo = $('<div>').addClass('video_wrap').append(video);
		
		parent.append(popupVideo);
	},
	/**
	 * 영상 컨트롤러 생성 함수.
	 * @function danuPlayer.createPtzControls
	 * @param {object} option - option 데이터
	 * */
	createPtzControls : function(option) {
		const parent = option.parent;
		const data = option.data;
		const type = option.type;
		const timestamp = option.timestamp;
		var width = parent.width();
		var height = parent.height();
		var hOffset = width / 2;
		var vOffset = height / 2;
		
		var left = 5;
		var center = hOffset - 13.5;
		var right = 5;
		
		var top = 5;
		var middle = vOffset - 13.5;
		var bottom = 5;
		
		let btnWrap = $('<div>').addClass('cctv-btn-wrap');
		if(!timestamp){
			if(data.cctvAgYn == "1"){
				let ptzL = $('<a>').addClass('left').css({left : (center-26) + 'px', top : (middle) + 'px'});
				let ptzR = $('<a>').addClass('right').css({left : (center+26) + 'px', top : (middle) + 'px'});
				let ptzU = $('<a>').addClass('up').css({left : (center) + 'px', top : (middle-26) + 'px'});
				let ptzD = $('<a>').addClass('down').css({left : (center) + 'px', top : (middle+26) + 'px'});
				let preset1 = $('<a>').addClass('preset_1').css({left : (left) + 'px', top : (top) + 'px'});
				let preset2 = $('<a>').addClass('preset_2').css({left : (center) + 'px', top : (top) + 'px'});
				let preset3 = $('<a>').addClass('preset_3').css({right : (right) + 'px', top : (top) + 'px'});
				let preset4 = $('<a>').addClass('preset_4').css({right : (right) + 'px', top : (middle) + 'px'});
				let preset5 = $('<a>').addClass('preset_5').css({right : (right) + 'px', bottom : (bottom) + 'px'});
				let preset6 = $('<a>').addClass('preset_6').css({left : (center) + 'px', bottom : (bottom) + 'px'});
				let preset7 = $('<a>').addClass('preset_7').css({left : (left) + 'px', bottom : (bottom) + 'px'});
				let preset8 = $('<a>').addClass('preset_8').css({left : (left) + 'px', top : (middle) + 'px'});
				
				btnWrap.append(ptzL, ptzR, ptzU, ptzD, 
						preset1, preset2, preset3, preset4, preset5, preset6, preset7, preset8);
			}
			let zoomIn = $('<a>').addClass('zoomIn').css({left : (center) + 'px', top : (middle) + 'px'});
			let zoomOut = $('<a>').addClass('zoomOut').css({left : (center) + 'px', top : (middle+13) + 'px'});
			
			btnWrap.append(zoomIn, zoomOut);
			
			btnWrap.children('a').bind({
				mousedown:function(e){
					var cmd = e.target.className;
					if (cmd.indexOf('preset') != -1) {
						var presetNo = cmd.split('_')[1];
						videoManager.presetCtrl(data, presetNo);
					} else {
						videoManager.insertCctvCtrlLog(data.fcltId, '0', cmd);
						try {
							console.log('===== ptz >>>> cmd : ', cmd);
							option.player.ptzCtrl(data,cmd,'60');
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
							console.log('===== ptz >>>> cmd : stop');
							option.player.ptzCtrl(data,'stop','60');
				    	} catch(e) {
				    		console.log('===== e : ', e);
				    	}
					}
					return false;
				}
			});
		}
		
		parent.children('.video_wrap').append(btnWrap);
	},
	/**
	 * 카메라의 현재 방향 PTZ 값을 가져오는 함수(Software 카메라 일 경우에만 동작)
	 * @function danuPlayer.getPtzPosition
	 * @param {object} data - 카메라 데이터
	 * @param {object} point - 좌표 데이터
	 * @param {number} no - 프리셋 번호
	 * @deprecated 사내 VMS 연계 시에만 사용
	 */
	getPtzPosition : function(data, no, point) {
		const jsonObj = {};
		jsonObj.code = "1300";
		jsonObj.node_id = data.nodeId;
		jsonObj.svr_ip = data.vmsSvrIp;
		jsonObj.kind = '0';
		
		$.ajax({
			type       : "POST",
			url        : "http://"+videoManager.prop.midServerIp+':'+videoManager.prop.midServerPort+"/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(x) {
			getPtzf();
		});
		
		function getPtzf() {
			const jsonObj2 = {};
			
			jsonObj2.cctvId = data.fcltId;
			
			$.ajax({
			    contentType : "application/json; charset=utf-8",
				type        : "POST",
				url         : "/select/facility.getFacilityPresetTemp/action",
				dataType    : "json",
				data        : JSON.stringify(jsonObj2),
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
				if(rows.length == 0) {
					setTimeout(function() {
						getPtzf();
					}, 2000);
				} else {
					videoManager.saveSwPreset(data, rows[0], point, no);
				}
		    }).fail(function() {
		    	getPtzf();
		    });
		}
	},
	/**
	 * 팝업의 크기를 조절 시 팝업 내부의 영상 및 컨트롤 UI의 크기를 Resize 시키는 함수
	 * @param {element} dialog - 영상 팝업 dialog element
	 * @function 
	 * @deprecated connectDialog로 이전 예정
	 */
	onPopupVideoResize : function(dialog) {
		var $btnArea = $(dialog).children('.video_wrap').children('.cctv-btn-wrap');
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

		$btnArea.children('.left').css({left: (center-26)+"px", top: (middle)+"px"});
		$btnArea.children('.right').css({left: (center+26)+"px", top: (middle)+"px"});
		$btnArea.children('.up').css({left: (center)+"px", top: (middle-26)+"px"});
		$btnArea.children('.down').css({left: (center)+"px", top: (middle+26)+"px"});
		$btnArea.children('.preset_1').css({left: (left)+"px", top: (top)+"px"});
		$btnArea.children('.preset_2').css({left: (center)+"px", top: (top)+"px"});
		$btnArea.children('.preset_3').css({right: (right)+"px", top: (top)+"px"});
		$btnArea.children('.preset_4').css({right: (right)+"px", top: (middle)+"px"});
		$btnArea.children('.preset_5').css({right: (right)+"px", bottom: (bottom)+"px"});
		$btnArea.children('.preset_6').css({left: (center)+"px", bottom: (bottom)+"px"});
		$btnArea.children('.preset_7').css({left: (left)+"px", bottom: (bottom)+"px"});
		$btnArea.children('.preset_8').css({left: (left)+"px", top: (middle)+"px"});
		$btnArea.children('.zoomIn').css({left: (center)+"px", top: (middle)+"px"});
		$btnArea.children('.zoomOut').css({left: (center)+"px", top: (middle+13)+"px"});
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
	ptzCtrl : function(data, cmd, speed) {
		const jsonObj = {};
		jsonObj.code = "1100";
		jsonObj.node_id = data.fcltId;
		jsonObj.ptz_mode = videoManager.switchPtzValue(cmd);
		jsonObj.speed = speed;
		
		$.ajax({
			type       : "POST",
			url        : "http://" + videoManager.prop.midServerIp + ":" + videoManager.prop.midServerPort + "/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(data) {
		});
	},
}

/**
 * Vurix Player
 * @author - 
 * @version 0.0.1
 * @class vurixPlayer
 * */
var vurixPlayer = {
	/**
	 * 영상 재생 함수.
	 * @function vurixPlayer.play
	 * @param {object} option - option 데이터
	 * */
	play : function(option) {
		const data = option.data;
		const timestamp = option.timestamp;
		const viewId = option.viewId;
		
		data.clientSessionId = videoManager.prop.clientIp;

		data.reqServerIp = videoManager.prop.reqServerIp;
		data.reqServerPort = videoManager.prop.reqServerPort;
		
		if(timestamp) {
			data.timestamp = timestamp.sTime;
			video.singleVideoStartForVurix(data, viewId);
		} else {
			video.singleVideoStartForVurix(data, viewId);
		}
		
		$('#' + viewId).data('data', data);
	},
	/**
	 * 영상 종료 함수.
	 * @function vurixPlayer.stop
	 * @param {object} option - option 데이터
	 * */
	stop : function(option) {
		const viewId = option.viewId;
		
		video.singleVideoStop(viewId);
		//webrtcserver.disconnect();
	},
	/**
	 * 영상 재생 영역 생성 함수.
	 * @function vurixPlayer.createElements
	 * @param {object} option - option 데이터
	 * */
	createElements : function(option) {
		const data = option.data;
		const viewId = option.viewId;
		const parent = option.parent;

		const video = $('<video>').addClass('video_content').attr({'id': viewId, 'autoplay': true, 'muted': true});
		//const video = $('<video>').addClass('video_content').attr({'id': viewId, 'title': data.fcltId});
		
		const popupVideo = $('<div>').addClass('video_wrap').append(video);
		
		parent.append(popupVideo);
	},
	/**
	 * 영상 컨트롤러 생성 함수.
	 * @function vurixPlayer.createPtzControls
	 * @param {object} option - option 데이터
	 * */
	createPtzControls : function(option) {
		const parent = option.parent;
		const data = option.data;
		const type = option.type;
		const timestamp = option.timestamp;
		var width = parent.width();
		var height = parent.height();
		var hOffset = width / 2;
		var vOffset = height / 2;
		
		var left = 5;
		var center = hOffset - 13.5;
		var right = 5;
		
		var top = 5;
		var middle = vOffset - 13.5;
		var bottom = 5;
		
		let btnWrap = $('<div>').addClass('cctv-btn-wrap');
		if(!timestamp){
			if(data.cctvAgYn == "1"){
				let ptzL = $('<a>').addClass('left').css({left : (center-26) + 'px', top : (middle) + 'px'});
				let ptzR = $('<a>').addClass('right').css({left : (center+26) + 'px', top : (middle) + 'px'});
				let ptzU = $('<a>').addClass('up').css({left : (center) + 'px', top : (middle-26) + 'px'});
				let ptzD = $('<a>').addClass('down').css({left : (center) + 'px', top : (middle+26) + 'px'});
//				let preset1 = $('<a>').addClass('preset_1').css({left : (left) + 'px', top : (top) + 'px'});
//				let preset2 = $('<a>').addClass('preset_2').css({left : (center) + 'px', top : (top) + 'px'});
//				let preset3 = $('<a>').addClass('preset_3').css({right : (right) + 'px', top : (top) + 'px'});
//				let preset4 = $('<a>').addClass('preset_4').css({right : (right) + 'px', top : (middle) + 'px'});
//				let preset5 = $('<a>').addClass('preset_5').css({right : (right) + 'px', bottom : (bottom) + 'px'});
//				let preset6 = $('<a>').addClass('preset_6').css({left : (center) + 'px', bottom : (bottom) + 'px'});
//				let preset7 = $('<a>').addClass('preset_7').css({left : (left) + 'px', bottom : (bottom) + 'px'});
//				let preset8 = $('<a>').addClass('preset_8').css({left : (left) + 'px', top : (middle) + 'px'});
				
//				btnWrap.append(ptzL, ptzR, ptzU, ptzD, 
//						preset1, preset2, preset3, preset4, preset5, preset6, preset7, preset8);
				btnWrap.append(ptzL, ptzR, ptzU, ptzD);
			}
			let zoomIn = $('<a>').addClass('zoomIn').css({left : (center) + 'px', top : (middle) + 'px'});
			let zoomOut = $('<a>').addClass('zoomOut').css({left : (center) + 'px', top : (middle+13) + 'px'});
			
			btnWrap.append(zoomIn, zoomOut);
			
			btnWrap.children('a').bind({
				mousedown:function(e){
					var cmd = e.target.className;
					if (cmd.indexOf('preset') != -1) {
						var presetNo = cmd.split('_')[1];
						videoManager.presetCtrl(data, presetNo);
					} else {
						videoManager.insertCctvCtrlLog(data.fcltId, '0', cmd);
						try {
							console.log('===== ptz >>>> cmd : ', cmd);
							option.player.ptzCtrl(data,cmd,'5');
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
							console.log('===== ptz >>>> cmd : stop');
							option.player.ptzCtrl(data,'stop','5');
				    	} catch(e) {
				    		console.log('===== e : ', e);
				    	}
					}
					return false;
				}
			});
		}
		
		parent.children('.video_wrap').append(btnWrap);
	},
	/**
	 * 카메라의 현재 방향 PTZ 값을 가져오는 함수(Software 카메라 일 경우에만 동작)
	 * @function vurixPlayer.getPtzPosition
	 * @param {object} data - 카메라 데이터
	 * @param {object} point - 좌표 데이터
	 * @param {number} no - 프리셋 번호
	 * @deprecated 사내 VMS 연계 시에만 사용
	 */
	getPtzPosition : function(data, no, point) {
		const jsonObj = {};
		jsonObj.code = "1300";
		jsonObj.node_id = data.nodeId;
		jsonObj.svr_ip = data.vmsSvrIp;
		jsonObj.kind = '0';
		
		$.ajax({
			type       : "POST",
			url        : "http://"+videoManager.prop.midServerIp+':'+videoManager.prop.midServerPort+"/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(x) {
			getPtzf();
		});
		
		function getPtzf() {
			const jsonObj2 = {};
			
			jsonObj2.cctvId = data.fcltId;
			
			$.ajax({
			    contentType : "application/json; charset=utf-8",
				type        : "POST",
				url         : "/select/facility.getFacilityPresetTemp/action",
				dataType    : "json",
				data        : JSON.stringify(jsonObj2),
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
				if(rows.length == 0) {
					getPtzf();
				} else {
					videoManager.saveSwPreset(data, rows[0], point, no);
				}
		    }).fail(function() {
		    	getPtzf();
		    });
		}
	},
	/**
	 * 팝업의 크기를 조절 시 팝업 내부의 영상 및 컨트롤 UI의 크기를 Resize 시키는 함수
	 * @param {element} dialog - 영상 팝업 dialog element
	 * @function 
	 * @deprecated connectDialog로 이전 예정
	 */
	onPopupVideoResize : function(dialog) {
		var $btnArea = $(dialog).children('.video_wrap').children('.cctv-btn-wrap');
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

		$btnArea.children('.left').css({left: (center-26)+"px", top: (middle)+"px"});
		$btnArea.children('.right').css({left: (center+26)+"px", top: (middle)+"px"});
		$btnArea.children('.up').css({left: (center)+"px", top: (middle-26)+"px"});
		$btnArea.children('.down').css({left: (center)+"px", top: (middle+26)+"px"});
		$btnArea.children('.preset_1').css({left: (left)+"px", top: (top)+"px"});
		$btnArea.children('.preset_2').css({left: (center)+"px", top: (top)+"px"});
		$btnArea.children('.preset_3').css({right: (right)+"px", top: (top)+"px"});
		$btnArea.children('.preset_4').css({right: (right)+"px", top: (middle)+"px"});
		$btnArea.children('.preset_5').css({right: (right)+"px", bottom: (bottom)+"px"});
		$btnArea.children('.preset_6').css({left: (center)+"px", bottom: (bottom)+"px"});
		$btnArea.children('.preset_7').css({left: (left)+"px", bottom: (bottom)+"px"});
		$btnArea.children('.preset_8').css({left: (left)+"px", top: (middle)+"px"});
		$btnArea.children('.zoomIn').css({left: (center)+"px", top: (middle)+"px"});
		$btnArea.children('.zoomOut').css({left: (center)+"px", top: (middle+13)+"px"});
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
	ptzCtrl : function(data, cmd, speed) {
		const jsonObj = {};
		jsonObj.code = "3320";
		jsonObj.node_id = data.nodeId;
		jsonObj.ptz_mode = videoManager.switchPtzValueForVurix(cmd);
		jsonObj.speed = speed;
		
		$.ajax({
			type       : "POST",
			url        : "http://" + videoManager.prop.midServerIp + ":" + videoManager.prop.midServerPort + "/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(data) {
		});
	},
}

/**
 * Hive Player
 * @author - 
 * @version 0.0.1
 * @class hivePlayer
 * */
var hivePlayer = {
	/**
	 * 영상 재생 함수.
	 * @function hivePlayer.play
	 * @param {object} option - option 데이터
	 * */
	play : function(option) {
		const data = option.data;
		const timestamp = option.timestamp;
		let hivePlayer;
		
		if(timestamp !== undefined) {
			hivePlayer = new HiVeWebRtcPlayer(
					viewId, videoManager.prop.reqServerIp, 
					videoManager.prop.reqServerPort, data.cctvUuid, 
					false, true, timeData.sTime, timeData.eTime, 1);
		} else {
			hivePlayer = new HiVeWebRtcPlayer(viewId, videoManager.prop.reqServerIp, videoManager.prop.reqServerPort, data.cctvUuid) ;
		}
		
		hivePlayer.Play(); 
		
		data.setSpeed = function(speed) {
			new HiVeWebRtcPlayer(videoDatas[ch].viewId, 
					videoManager.prop.reqServerIp, videoManager.prop.reqServerPort,
					videoDatas[ch].cctvUuid, false, flag, timeData.sTime, timeData.eTime, speed);
		};
		
		$('#' + viewId).data('data', data);
	},
	/**
	 * 영상 종료 함수.
	 * @function hivePlayer.stop
	 * @param {object} option - option 데이터
	 * */
	stop : function(option) {
		const viewId = option.viewId;
		const data = $('#' + viewId).data();
		const player = data.player;
		player.Stop();
	},
	/**
	 * 영상 재생 영역 생성 함수.
	 * @function hivePlayer.createElements
	 * @param {object} option - option 데이터
	 * */
	createElements : function(option) {
		const data = option.data;
		const viewId = option.viewId;
		const parent = option.parent;
        
		const video = $('<video>').addClass('video_content').attr({'id': viewId, 'autoplay' : true});
		
		const popupVideo = $('<div>').addClass('video_wrap').append(video);
		
		parent.append(popupVideo);
	},
	/**
	 * 영상 컨트롤러 생성 함수.
	 * @function hivePlayer.createPtzControls
	 * @param {object} option - option 데이터
	 * */
	createPtzControls : function(option) {
		const parent = option.parent;
		const data = option.data;
		const type = option.type;
		const timestamp = option.timestamp;
		var width = parent.width();
		var height = parent.height();
		var hOffset = width / 2;
		var vOffset = height / 2;
		
		var left = 5;
		var center = hOffset - 13.5;
		var right = 5;
		
		var top = 5;
		var middle = vOffset - 13.5;
		var bottom = 5;
		
		let btnWrap = $('<div>').addClass('cctv-btn-wrap');
		if(!timestamp){
			if(data.cctvAgYn == "1"){
				let ptzL = $('<a>').addClass('left').css({left : (center-26) + 'px', top : (middle) + 'px'});
				let ptzR = $('<a>').addClass('right').css({left : (center+26) + 'px', top : (middle) + 'px'});
				let ptzU = $('<a>').addClass('up').css({left : (center) + 'px', top : (middle-26) + 'px'});
				let ptzD = $('<a>').addClass('down').css({left : (center) + 'px', top : (middle+26) + 'px'});
				let preset1 = $('<a>').addClass('preset_1').css({left : (left) + 'px', top : (top) + 'px'});
				let preset2 = $('<a>').addClass('preset_2').css({left : (center) + 'px', top : (top) + 'px'});
				let preset3 = $('<a>').addClass('preset_3').css({right : (right) + 'px', top : (top) + 'px'});
				let preset4 = $('<a>').addClass('preset_4').css({right : (right) + 'px', top : (middle) + 'px'});
				let preset5 = $('<a>').addClass('preset_5').css({right : (right) + 'px', bottom : (bottom) + 'px'});
				let preset6 = $('<a>').addClass('preset_6').css({left : (center) + 'px', bottom : (bottom) + 'px'});
				let preset7 = $('<a>').addClass('preset_7').css({left : (left) + 'px', bottom : (bottom) + 'px'});
				let preset8 = $('<a>').addClass('preset_8').css({left : (left) + 'px', top : (middle) + 'px'});
				
				btnWrap.append(ptzL, ptzR, ptzU, ptzD, 
						preset1, preset2, preset3, preset4, preset5, preset6, preset7, preset8);
			}
			let zoomIn = $('<a>').addClass('zoomIn').css({left : (center) + 'px', top : (middle) + 'px'});
			let zoomOut = $('<a>').addClass('zoomOut').css({left : (center) + 'px', top : (middle+13) + 'px'});
			
			btnWrap.append(zoomIn, zoomOut);
			
			btnWrap.children('a').bind({
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
							option.player.ptzCtrl(data,cmd,actionCd);
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
							option.player.ptzCtrl(data,cmd,actionCd);
				    	} catch(e) {
				    		console.log('===== e : ', e);
				    	}
					}
					return false;
				}
			});
		}
		
		parent.children('.video_wrap').append(btnWrap);
	},
	/**
	 * 카메라의 현재 방향 PTZ 값을 가져오는 함수(Software 카메라 일 경우에만 동작)
	 * @function hivePlayer.getPtzPosition
	 * @param {object} data - 카메라 데이터
	 * @param {object} point - 좌표 데이터
	 * @param {number} no - 프리셋 번호
	 */
	getPtzPosition : function(data, point, no) {
		const jsonObj = {};
		jsonObj.code = "1300";
		jsonObj.node_id = data.nodeId;
		jsonObj.svr_ip = data.vmsSvrIp;
		jsonObj.kind = '0';
		
		$.ajax({
			type       : "POST",
			url        : "http://"+videoManager.prop.reqServerIp+':'+videoManager.prop.reqServerPort+"/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(x) {
			getPtzf();
		});
		
		function getPtzf() {
			$.ajax({
			    contentType : "application/json; charset=utf-8",
				type        : "GET",
				url         : "/select/facility.getFacilityPresetTemp/action",
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
				if(rows.length == 0) {
					getPtzf(data, presetNo, point);
				} else {
					videoManager.saveSwPreset(data, rows[0], point, presetNo);
				}
		    }).fail(function() {
		    	getPtzf(data, presetNo, point);
		    });
		}
	},
	/**
	 * 팝업의 크기를 조절 시 팝업 내부의 영상 및 컨트롤 UI의 크기를 Resize 시키는 함수
	 * @param {element} dialog - 영상 팝업 dialog element
	 * @function 
	 * @deprecated connectDialog로 이전 예정
	 */
	onPopupVideoResize : function(dialog) {
		var $btnArea = $(dialog).children('.video_wrap').children('.cctv-btn-wrap');
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

		$btnArea.children('.left').css({left: (center-26)+"px", top: (middle)+"px"});
		$btnArea.children('.right').css({left: (center+26)+"px", top: (middle)+"px"});
		$btnArea.children('.up').css({left: (center)+"px", top: (middle-26)+"px"});
		$btnArea.children('.down').css({left: (center)+"px", top: (middle+26)+"px"});
		$btnArea.children('.preset_1').css({left: (left)+"px", top: (top)+"px"});
		$btnArea.children('.preset_2').css({left: (center)+"px", top: (top)+"px"});
		$btnArea.children('.preset_3').css({right: (right)+"px", top: (top)+"px"});
		$btnArea.children('.preset_4').css({right: (right)+"px", top: (middle)+"px"});
		$btnArea.children('.preset_5').css({right: (right)+"px", bottom: (bottom)+"px"});
		$btnArea.children('.preset_6').css({left: (center)+"px", bottom: (bottom)+"px"});
		$btnArea.children('.preset_7').css({left: (left)+"px", bottom: (bottom)+"px"});
		$btnArea.children('.preset_8').css({left: (left)+"px", top: (middle)+"px"});
		$btnArea.children('.zoomIn').css({left: (center)+"px", top: (middle)+"px"});
		$btnArea.children('.zoomOut').css({left: (center)+"px", top: (middle+13)+"px"});
	},
}

/**
 * Xeus Player
 * @author - 
 * @version 0.0.1
 * @class xeusPlayer
 * */
var xeusPlayer = {
	/**
	 * 영상 재생 함수.
	 * @function xeusPlayer.play
	 * @param {object} option - option 데이터
	 * */
	play : function(option) {
		const data = option.data;
		const timestamp = option.timestamp;
		const viewId = option.viewId;

		var time = '10';
		var speed = '10';
		
		const playerOption = {
            playerId : viewId,
            url : 'ws://'+videoManager.prop.reqServerIp+':'+videoManager.prop.reqServerPort+'/xeus-gate/stream',
            cctvMgrNo : data.fcltId,  // 192: 마일스톤 194: 뷰릭스, 301:icon
            userId : 'tester1',
            evtType : '112',
            timestamp : timestamp !== undefined ? timestamp.sTime : '',// '20200219142030', 미구현
            speed : speed, // 미구현
            size : '',  // 1280x720 설정시cpu많이 먹음... default 권장,,
            codec : timestamp !== undefined ? 'mjpeg' : 'h264',
            rtspUrl : '', //
            debug : false
        }
		
		var player = new XeusGate.Player(playerOption);
		
		data.player = player;
		
		$('#' + viewId).data('data', data);
	},
	/**
	 * 영상 종료 함수.
	 * @function xeusPlayer.play
	 * @param {object} option - option 데이터
	 * */
	stop : function(videoId) {
		const data = $('#' + videoId).data();
		const player = data.player;
		if (player != null && player != 'undefined') {
			player.destroy();
			console.log("player.... destroy at unload....... OK", obj.option.data);
		}
	},
	/**
	 * 영상 재생 영역 생성 함수.
	 * @function xeusPlayer.createElements
	 * @param {object} option - option 데이터
	 * */
	createElements : function(option) {
		const data = option.data;
		const viewId = option.viewId;
		const parent = option.parent;
		
		//if(videoDatas.has(data.fcltId)) return;
		
		//const title = $('<div>').addClass('title').html(data.nodeId);
        
		const video = $('<div>').addClass('video_content').attr('id', viewId);
		
		const popupVideo = $('<div>').addClass('video_wrap').append(video);
		
		parent.append(popupVideo);
	},
	/**
	 * 영상 컨트롤러 생성 함수.
	 * @function xeusPlayer.createPtzControls
	 * @param {object} option - option 데이터
	 * */
	createPtzControls : function(option) {
		const parent = option.parent;
		const data = option.data;
		const type = option.type;
		const timestamp = option.timestamp;
		var width = parent.width();
		var height = parent.height();
		var hOffset = width / 2;
		var vOffset = height / 2;
		
		var left = 5;
		var center = hOffset - 13.5;
		var right = 5;
		
		var top = 5;
		var middle = vOffset - 13.5;
		var bottom = 5;
		
		let btnWrap = $('<div>').addClass('cctv-btn-wrap');
		if(!timestamp){
			if(data.cctvAgYn == "1"){
				let ptzL = $('<a>').addClass('Left').css({left : (center-26) + 'px', top : (middle) + 'px'});
				let ptzR = $('<a>').addClass('Right').css({left : (center+26) + 'px', top : (middle) + 'px'});
				let ptzU = $('<a>').addClass('Up').css({left : (center) + 'px', top : (middle-26) + 'px'});
				let ptzD = $('<a>').addClass('Down').css({left : (center) + 'px', top : (middle+26) + 'px'});
				let preset1 = $('<a>').addClass('preset_1').css({left : (left) + 'px', top : (top) + 'px'});
				let preset2 = $('<a>').addClass('preset_2').css({left : (center) + 'px', top : (top) + 'px'});
				let preset3 = $('<a>').addClass('preset_3').css({right : (right) + 'px', top : (top) + 'px'});
				let preset4 = $('<a>').addClass('preset_4').css({right : (right) + 'px', top : (middle) + 'px'});
				let preset5 = $('<a>').addClass('preset_5').css({right : (right) + 'px', bottom : (bottom) + 'px'});
				let preset6 = $('<a>').addClass('preset_6').css({left : (center) + 'px', bottom : (bottom) + 'px'});
				let preset7 = $('<a>').addClass('preset_7').css({left : (left) + 'px', bottom : (bottom) + 'px'});
				let preset8 = $('<a>').addClass('preset_8').css({left : (left) + 'px', top : (middle) + 'px'});
				btnWrap.append(ptzL, ptzR, ptzU, ptzD, 
						preset1, preset2, preset3, preset4, preset5, preset6, preset7, preset8);
			}
			let zoomIn = $('<a>').addClass('ZoomIn').css({left : (center) + 'px', top : (middle) + 'px'});
			let zoomOut = $('<a>').addClass('ZoomOut').css({left : (center) + 'px', top : (middle+13) + 'px'});
			btnWrap.append(zoomIn, zoomOut);
			
			btnWrap.children('a').bind({
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
							option.player.ptzCtrl(data,cmd,actionCd);
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
							option.player.ptzCtrl(data,'Stop',actionCd);
				    	} catch(e) {
				    		console.log('===== e : ', e);
				    	}
					}
					return false;
				}
			});
		}
		
		parent.children('.video_wrap').append(btnWrap);
	},
	/**
	 * 카메라의 현재 방향 PTZ 값을 가져오는 함수(Software 카메라 일 경우에만 동작)
	 * @function xuesPlayer.getPtzPosition
	 * @param {object} data - 카메라 데이터
	 * @param {object} point - 좌표 데이터
	 * @param {number} no - 프리셋 번호
	 * @deprecated 지오맥스 연계 시에만 사용
	 */
	getPtzPosition : function(data, presetNo, point) {
		$.ajax({
			type       : "GET",
			url        : "http://"+videoManager.prop.reqServerIp+':'+videoManager.prop.reqServerPort+"/xeus-gate/getPTZPosition.json?cctvMgrNo="+data.fcltId,
			dataType   : "text",
			async      : false
		}).done(function(result) {
			const ptzData = JSON.parse(result);
			videoManager.saveSwPreset(data, ptzData, point, presetNo);
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
	ptzCtrl : function(data, cmd, actionCd){
		const jsonObj = {};
		$.ajax({
			type       : "POST",
			url        : "http://"+videoManager.prop.reqServerIp+':'+videoManager.prop.reqServerPort+"/xeus-gate/setPTZ.json?cctvMgrNo="+data.fcltId+"&action="+actionCd+"&code="+cmd+"&spd=60",
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
	 * 팝업의 크기를 조절 시 팝업 내부의 영상 및 컨트롤 UI의 크기를 Resize 시키는 함수
	 * @param {element} dialog - 영상 팝업 dialog element
	 * @function 
	 * @deprecated connectDialog로 이전 예정
	 */
	onPopupVideoResize : function(dialog) {
		var $btnArea = $(dialog).children('.video_wrap').children('.cctv-btn-wrap');
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
}

/**
 * 연계 서비스 별 player 분기 calss
 * @author - 
 * @version 0.0.1
 * @class player
 * @see {@link videoManager}
 * @see {@link danuPlayer} - danusys player
 * @see {@link vurixPlayer} - vurix player
 * @see {@link xeusPlayer} - xeus player
 * @see {@link hivePlayer} - hive player
 * */
videoManager.player = function(kind) {
	/**
	 * player 생성자
	 * @function player.player
	 * */
	let player = function () {
		if (kind !== 'CCTV') {
			this.play = commonPlayer.play;
			this.stop = commonPlayer.stop;
			this.createElements = commonPlayer.createElements;
		} else if (videoManager.prop.systemType === 'danu') {
			this.play = danuPlayer.play;
			this.stop = danuPlayer.stop;
			this.createElements = danuPlayer.createElements;
			this.createPtzControls = danuPlayer.createPtzControls;
			this.getPtzPosition = danuPlayer.getPtzPosition;
			this.onPopupVideoResize = danuPlayer.onPopupVideoResize;
			this.ptzCtrl = danuPlayer.ptzCtrl;
		} else if (videoManager.prop.systemType === 'vurix') {
			this.play = vurixPlayer.play;
			this.stop = vurixPlayer.stop;
			this.createElements = vurixPlayer.createElements;
			this.createPtzControls = vurixPlayer.createPtzControls;
			this.getPtzPosition = vurixPlayer.getPtzPosition;
			this.onPopupVideoResize = xeusPlayer.onPopupVideoResize;
			this.ptzCtrl = vurixPlayer.ptzCtrl;
		}  else if (videoManager.prop.systemType === 'xeus') {
			this.play = xeusPlayer.play;
			this.stop = xeusPlayer.stop;
			this.createElements = xeusPlayer.createElements;
			this.createPtzControls = xeusPlayer.createPtzControls;
			this.getPtzPosition = xeusPlayer.getPtzPosition;
			this.onPopupVideoResize = xeusPlayer.onPopupVideoResize;
			this.ptzCtrl = xeusPlayer.ptzCtrl;
		} else if (videoManager.prop.systemType === 'hive') {
			this.play = hivePlayer.play;
			this.stop = hivePlayer.stop;
			this.createElements = hivePlayer.createElements;
			this.createPtzControls = hivePlayer.createPtzControls;
			this.getPtzPosition = hivePlayer.getPtzPosition;
			this.onPopupVideoResize = hivePlayer.onPopupVideoResize;
			this.ptzCtrl = hivePlayer.ptzCtrl;
		}
	}
	
	return player;
}();
