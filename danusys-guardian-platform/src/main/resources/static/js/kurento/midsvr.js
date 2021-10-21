var midsvr = {
	/**
	 * Http 전송 -> 중계서버
	 * @param : options
	 * @param : 결과값 return callback
	 * @param : 에러처리 callback
	*/
	sendData : function(options, callback, errCallback) {
		var defaults = {
			type       	: "POST",
			url        		: options.midSvrTargetUrl,
			dataType   	: "text",
			async      	: false, 
			data	   		: {}
		}
		defaults.data = JSON.stringify(options.data);
		//var exDefaults = $.extend(defaults, options);
		//exDefaults.data = JSON.stringify(options.data);
		
		//console.log("##sendData");
		//console.log(defaults);
		
		$.ajax(defaults)
			.done(function(result) {
				//console.log("##receiveData");
				//console.log(result);
				
				if(typeof(callback)=="function") {
					callback(result);
				}
			}).fail(function(e, textStatus, errorThrown) {
				console.log(e);

				if(typeof(errCallback)=="function") {
					errCallback(e);
				}
			});
	},
	/**
	 * code : 9999
	 * 로딩 인포
	 * @param : paramObj (node 정보를 담은 Object)
	 * @param : callback
	*/
	load : function(paramObj, callback) {
		var targetUrl = "http://"+paramObj.ip+":"+paramObj.port+"/api/athena/midsvr";
		
		var dataObj = {
			"data" : {
				"code" : "4100"
			}
		}
		dataObj.midSvrTargetUrl = targetUrl;
		
		midsvr.sendData(dataObj, 
			function(result) {
				var resultData = JSON.parse(result);
				var turnUrl;
				
				if(resultData.media_url) {
					const pattern = /((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})/g;
					turnUrl = resultData.media_url.match(pattern) + ':3478?transport=tcp';
				}
				
				paramObj.mediaServerWsUrl = resultData.media_url;
				paramObj.midSvrMngSvrIp = resultData.mgrsvr_ip;
				paramObj.midSvrTargetUrl = targetUrl;
				paramObj.turnUrl = turnUrl;
				paramObj.credential = 'turnadm123';
				paramObj.username = 'turnadm';
				
				callback(paramObj);
			}
		);
	},
	/**
	 * code : 1600
	 * 카메라 연결 해제
	 * @param : paramObj (node 정보를 담은 Object)
	 * @param : 결과값 return callback
	 * @param : 에러처리 callback
	*/
	clearRtspVideo : function(paramObj, callback, errCallback) {		
		var fcltId = paramObj.fcltId;
		var nodeId = fcltId.split("_")[0];
		var ch = fcltId.split("_")[2];
		
		var dataObj = {
			"data" : {
				"code" : "1600"
				, "node_id" : fcltId
				, "rtsp" : paramObj.rtspUrl
				, "client_ip" : paramObj.clientSessionId
			}
		}
		dataObj = $.extend(dataObj, paramObj);
		
		midsvr.sendData(dataObj
			, function(result) {
				var resultData = JSON.parse(result);

				if(typeof(callback)=="function") {
					callback(resultData);
				}
			}, function(e) {
				if(typeof(errCallback)=="function") {
					errCallback(e);
				}
			}
		);
	},
	/**
	 * code : 4000
	 * 카메라 연결 요청 (Rtsp Url 호출)
	 * @param : paramObj (nodeId, clienSessionId, nodePort(채널) 등)
	 * @param : callback 결과값 (rtspUrl) return callback
	 * @param : 에러처리 callback
	 */
	getRtspVideo : function(paramObj, callback, errCallback) {
		
		this.load(paramObj, function(infoObj) {
			var fcltId = paramObj.fcltId;
			var nodeId = fcltId.split("_")[0];
			var ch = fcltId.split("_")[2];
			
			var dataObj = {
				"data" : {
					"code" : "4000"
					, "node_id" : fcltId
					, "client" : paramObj.clientSessionId
					, "ch" : ch
				}
			}
			dataObj.midSvrTargetUrl = paramObj.midSvrTargetUrl;
			dataObj.midSvrMngSvrIp = paramObj.midSvrMngSvrIp;
			
			midsvr.sendData(dataObj
				, function(result) {
					var resultData = JSON.parse(result);
		
					rtspUrl = resultData.rtsp_url;

					if(typeof(callback)=="function") {
						callback(rtspUrl);
					}
				}, function(e) {
					if(typeof(errCallback)=="function") {
						errCallback(e);
					}
				}
			);
		});
	},
	/**
	 * code : 1100
	 * PTZ 컨트롤
	 * @param : paramObj (node 정보를 담은 Object)
	 * @param : 결과값 return callback
	 * @param : 에러처리 callback
	*/
	controlPtzRtspVideo : function(paramObj, targetId) {
		var videoInfoObj = video.getVideoInfo(targetId);
		var fcltId = videoInfoObj.fcltId;
		
		var dataObj = {
			"asnyc" : true
			, "data" : {
				"code" 			: "1100"
				, "node_id" 		: fcltId
				, "ch" 				: fcltId.split("_")[2]
				, "ptz_mode" 	: paramObj.ptzMode
				, "speed" 		: paramObj.speed
			}
		}
		dataObj = $.extend(dataObj, videoInfoObj);
		
		midsvr.sendData(dataObj
			, function(result) {
				var resultData = JSON.parse(result);

				if(typeof(callback)=="function") {
					callback(resultData);
				}
			}, function(e) {
				if(typeof(errCallback)=="function") {
					errCallback(e);
				}
			}
		);
		
		if(paramObj.ptzMode != 9) {
			setTimeout(function() {
				paramObj.ptzMode = 9;
				midsvr.controlPtzRtspVideo(paramObj, targetId);
			}, 1000);
		}
	},
	/**
	 * code : 2000
	 * 저장영상 Rtsp 요청
	 * @param : paramObj (node 정보를 담은 Object)
	 * node_id : 카메라 ID
	 * s_time : 2019.01.01.09.00.00 (YYYY.MM.DD.HH24.MI.SS)
	 * e_time : 2019.01.01.09.00.00 (YYYY.MM.DD.HH24.MI.SS)
	 * cam_nm : 카메라명
	 * client_ip : 클라이언트 세션 아이디
	 * mac_addr : 클라이언트 세션 아이디
	 * direction : (0 : 정방향 / 1 : 역방향)
	 * svr_ip : 관리서버IP
	 * @param : 결과값 return callback
	 * @param : 에러처리 callback
	*/
	getSaveRtspVideo : function(paramObj, callback, errCallback) {
		this.load(paramObj, function(infoObj) {
			var fcltId = paramObj.fcltId;
			var nodeId = fcltId.split("_")[0];
			var ch = fcltId.split("_")[2];
			
			var dataObj = {
				"data" : {
					"code" 		: "2000"
					, "node_id" 	: fcltId
					, "ch" 			: ch
					, "s_time"		: paramObj.sTime
					, "e_time"		: paramObj.eTime
					, "cam_nm"	: paramObj.fcltNm
					, "clientIp"	: paramObj.clientSessionId
					, "mac_addr"	: paramObj.clientSessionId
					, "direction"	: paramObj.direction
				}
			}
			dataObj.midSvrTargetUrl = infoObj.midSvrTargetUrl;
			dataObj.midSvrMngSvrIp = infoObj.midSvrMngSvrIp;
			
			midsvr.sendData(dataObj
				, function(result) {
					var resultData = JSON.parse(result);
		
					rtspUrl = resultData.rtsp_url;

					if(typeof(callback)=="function") {
						callback(rtspUrl);
					}
				}, function(e) {
					if(typeof(errCallback)=="function") {
						errCallback(e);
					}
				}
			);
		});
	},
	/**
	 * code : 2100
	 * 저장영상 Play 시간 변경
	 * @param : paramObj (node 정보를 담은 Object)
	 * time : 시간 YYYYMMDDHH24MISS 형식
	 * @param : targetId (플레이중인 비디오 ID)
	*/
	changeSaveRtspVideoPlayTime : function(paramObj, targetId) {
		var videoInfoObj = video.getVideoInfo(targetId);
		var fcltId = videoInfoObj.fcltId;
		
		var dataObj = {
			"data" : {
				"code" 		: "2100"
				, "node_id" 	: fcltId
				, "time" 		: paramObj.time
				, "client_ip"	: videoInfoObj.clientSessionId
				, "mac_addr"	: videoInfoObj.clientSessionId
				, "rtsp_url"	: videoInfoObj.rtspUrl
			}
		}
		dataObj.midSvrTargetUrl = videoInfoObj.midSvrTargetUrl;
		midsvr.sendData(dataObj);
	},
	/**
	 * code : 2110
	 * 저장영상 배속 설정
	 * @param : paramObj (node 정보를 담은 Object)
	 * @param : targetId (비디오 targetId)
	*/
	changeRtspVideoPlaySpeed : function(paramObj, targetId) {
		var videoInfoObj = video.getVideoInfo(targetId);
		var fcltId = videoInfoObj.fcltId;
		
		var dataObj = {
			"data" : {
				"code" 		: "2110"
				, "node_id" 	: fcltId
				, "speed" 	: paramObj.speed
				, "client_ip"	: videoInfoObj.clientSessionId
				, "mac_addr"	: videoInfoObj.clientSessionId
				, "rtsp_url"	: videoInfoObj.rtspUrl
			}
		}
		dataObj.midSvrTargetUrl = videoInfoObj.midSvrTargetUrl;
		midsvr.sendData(dataObj);
	},
	/**
	 * code : 3310
	 * Vurix 카메라 연결 요청 (Rtsp Url 호출)
	 * @param : paramObj (nodeId, clienSessionId, nodePort(채널) 등)
	 * @param : callback 결과값 (rtspUrl) return callback
	 * @param : 에러처리 callback
	 */
	getRtspVideoForVurix : function(paramObj, callback, errCallback) {
		
		this.load(paramObj, function(infoObj) {
			var fcltId = paramObj.fcltId;
			var nodeId = fcltId.split("_")[0];
			var ch = fcltId.split("_")[2];
			
			var dataObj = {
				"data" : {
					"code" : "3310"
					, "node_id" : nodeId
					, "ch" : ch
					, "stream_profile" : "0"
					, "timestamp" : paramObj.timestamp
				}
			}
			dataObj.midSvrTargetUrl = paramObj.midSvrTargetUrl;
			dataObj.midSvrMngSvrIp = paramObj.midSvrMngSvrIp;
			
			midsvr.sendData(dataObj
				, function(result) {
					var resultData = JSON.parse(result);
		
					rtspUrl = resultData.rtsp_url;

					if(typeof(callback)=="function") {
						callback(rtspUrl);
					}
				}, function(e) {
					if(typeof(errCallback)=="function") {
						errCallback(e);
					}
				}
			);
		});
	},
	/**
	 * code : 3320
	 * Vurix PTZ 컨트롤
	 * @param : paramObj (node 정보를 담은 Object)
	 * @param : 결과값 return callback
	 * @param : 에러처리 callback
	*/
	controlPtzRtspVideoForVurix : function(paramObj, targetId) {
		var videoInfoObj = video.getVideoInfo(targetId);
		var fcltId = videoInfoObj.fcltId;
		
		var dataObj = {
			"asnyc" : true
			, "data" : {
				"code" 			: "3320"
				, "node_id" 		: fcltId.split("_")[0]
				, "ch" 				: fcltId.split("_")[2]
				, "ptz_mode" 	: paramObj.ptzMode
				, "speed" 		: paramObj.speed
			}
		}
		dataObj = $.extend(dataObj, videoInfoObj);
		
		midsvr.sendData(dataObj
			, function(result) {
				var resultData = JSON.parse(result);

				if(typeof(callback)=="function") {
					callback(resultData);
				}
			}, function(e) {
				if(typeof(errCallback)=="function") {
					errCallback(e);
				}
			}
		);
		
		if(paramObj.ptzMode != 14) {
			setTimeout(function() {
				paramObj.ptzMode = 14;
				midsvr.controlPtzRtspVideoForVurix(paramObj, targetId);
			}, 1000);
		}
	},
}