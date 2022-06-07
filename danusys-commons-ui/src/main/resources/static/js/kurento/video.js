var argsObj = {}; 
var video = {
	load : function(options) {
		argsObj = this.getOpts(location.search, {
			default: {
				ice_servers 			: undefined,
				ws_uri 				: ""
			}
		});

		if (args.ice_servers) {
			console.log("Use ICE servers: " + args.ice_servers);
			//kurentoUtils.WebRtcPeer.prototype.server.iceServers = JSON.parse(args.ice_servers);
		} else {
			console.log("Use freeice");
		}
	},
	getOpts : function(args, opts) {
		var result = opts.default || {};
		args.replace(
			new RegExp("([^?=&]+)(=([^&]*))?", "g"),
			function($0, $1, $2, $3) { 
				result[$1] = $3; 
			});
		return result;
	},
	getVideoInfo : function(targetId) {
		return $("#"+targetId).data("videoInfo");
	},
	sinlgeSaveVideoStart : function(paramObj, targetId) {
		midsvr.getSaveRtspVideo(paramObj, function(rtspUrl) {
			paramObj.rtspUrl = rtspUrl;
			
			// 비디오 재생
			video.showSpinner("#"+targetId);
			$("#"+targetId).data("videoInfo", paramObj);
			video.createWebRtcPeer(paramObj, targetId);
		});
	},
	singleSaveVideoStart : function(paramObj, targetId) {
		midsvr.getSaveRtspVideo(paramObj, function(rtspUrl) {
			paramObj.rtspUrl = rtspUrl;
			
			video.showSpinner("#"+targetId);
			$("#"+targetId).data("videoInfo", paramObj);
			$("#"+targetId).data("playCnt", 1);
			
			video.createWebRtcPeer(paramObj, targetId);
			video.reloadVideoStart(paramObj, targetId);
		});
	},
	singleVideoStart : function(paramObj, targetId) {
		this.singleVideoStop(targetId);
		
		midsvr.getRtspVideo(paramObj, function(rtspUrl) {
			paramObj.rtspUrl = rtspUrl;
			
			video.showSpinner("#"+targetId);
			$("#"+targetId).data("videoInfo", paramObj);
			$("#"+targetId).data("playCnt", 1);
			
			video.createWebRtcPeer(paramObj, targetId);
			video.reloadVideoStart(paramObj, targetId);
		});
	},
	singleVideoStop : function(targetId) {
		this.hideSpinner("#"+targetId);
		var videoInfoObj = this.getVideoInfo(targetId);
		
		if(typeof(videoInfoObj)!="undefined") {
			midsvr.clearRtspVideo(videoInfoObj, function() {
				var peerInfoObj = $("#"+targetId).data("peerInfo");
				
				if(peerInfoObj!=undefined) {
					if (peerInfoObj.webRtcPeer) {
						peerInfoObj.webRtcPeer.dispose(); 
						peerInfoObj.webRtcPeer = null; 
					}
			
					if(peerInfoObj.pipeline) { 
						peerInfoObj.pipeline.release(); 
						peerInfoObj.pipeline = null;
					}
					$("#"+targetId).removeData("videoInfo");
					$("#"+targetId).removeData("peerInfo");
					$("#"+targetId).data("playCnt", 0);
				}
			});
		}
	},
	singleVideoStartForVurix : function(paramObj, targetId) {
		this.singleVideoStop(targetId);
		
		midsvr.getRtspVideoForVurix(paramObj, function(rtspUrl) {
			paramObj.rtspUrl = rtspUrl;

			console.log(rtspUrl);
			
			video.showSpinner("#"+targetId);
			$("#"+targetId).data("videoInfo", paramObj);
			$("#"+targetId).data("playCnt", 1);
			
			video.createWebRtcPeer(paramObj, targetId);
			//video.reloadVideoStart(paramObj, targetId);
		});
	},
	directVideoStart : function(paramObj, targetId) {
		//this.singleVideoStop(targetId);
		video.showSpinner("#"+targetId);
		
		$("#"+targetId).data("videoInfo", paramObj);
		$("#"+targetId).data("playCnt", 1);
		
		video.createWebRtcPeer(paramObj, targetId);
		video.reloadVideoStart(paramObj, targetId);
	},
	directVideoStop : function(targetId) {
		this.hideSpinner("#"+targetId);
		var videoInfoObj = this.getVideoInfo(targetId);
		
		if(typeof(videoInfoObj)!="undefined") {
			var peerInfoObj = $("#"+targetId).data("peerInfo");
			
			if(peerInfoObj!=undefined) {
				if (peerInfoObj.webRtcPeer) {
					peerInfoObj.webRtcPeer.dispose(); 
					peerInfoObj.webRtcPeer = null; 
				}
		
				if(peerInfoObj.pipeline) { 
					peerInfoObj.pipeline.release(); 
					peerInfoObj.pipeline = null;
				}
				$("#"+targetId).removeData("videoInfo");
				$("#"+targetId).removeData("peerInfo");
				$("#"+targetId).data("playCnt", 0);
			}
		}
	},
	reloadVideoStart : function(videoInfoObj, targetId) {
		// 8초후 플레이가 되지 않으면 다시 시작. (5회까지만 재시도)
		setTimeout(function() {
			var playCnt = $("#"+targetId).data("playCnt");
			
			if(playCnt<6) {
				if(!$.isEmptyObject(videoInfoObj) && $("#"+targetId)[0].currentTime==0 && playCnt>0 && $("#"+targetId)[0].readyState!=4) {
					console.log("targetId : " + targetId + " 영상 재요청 " + playCnt + "회 시도");
					playCnt++;
					
					video.videoPipelineClear(targetId);
					video.createWebRtcPeer(videoInfoObj, targetId);
					video.reloadVideoStart(videoInfoObj, targetId);
					
					$("#"+targetId).data("playCnt", playCnt);
				} else {
					$("#"+targetId).data("playCnt", 0);
				}
			} else {
				console.log("targetId : " + targetId + " 영상 5회 이상 재요청 중지");
				video.singleVideoStop(targetId);
			}
		}, 8000);
	},
	videoPipelineClear : function(targetId) {
		// Pipeline만 초기화
		var peerInfoObj = $("#"+targetId).data("peerInfo");
		
		if(peerInfoObj!=undefined) {
			if (peerInfoObj.webRtcPeer) {
				peerInfoObj.webRtcPeer.dispose();
				peerInfoObj.webRtcPeer = null; 
			}
	
			if(peerInfoObj.pipeline) { 
				peerInfoObj.pipeline.release(); 
				peerInfoObj.pipeline = null; 
			}
			$("#"+targetId).removeData("peerInfo");
		}
	},
	createWebRtcPeer : function(infoObj, targetId) {
		var options = {
			remoteVideo : document.getElementById(targetId)
			, video : {
				framerate : 30
			}
			, audio : false
			, turnUrl : infoObj.turnUrl
			, credential : infoObj.credential
			, username : infoObj.username
		};

		var tempPipeline;
		var webRtcPeer = kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(options, 
			function(error) {
				if(error) {
					return console.error(error);
				}
				
				var tempOffer = function(error, sdpOffer) {
					if(error) return video.onError(error);

					kurentoClient(infoObj.mediaServerWsUrl, function(error, kurentoClient) {
						if(error) return video.onError(error);
			
						kurentoClient.create("MediaPipeline", function(error, p) {
							if(error) return video.onError(error);
			
							tempPipeline = p;

							tempPipeline.create("PlayerEndpoint", {uri : infoObj.rtspUrl, useEncodedMedia: true}, function(error, player) {
								if(error) return video.onError(error);
			
								tempPipeline.create("WebRtcEndpoint", function(error, webRtcEndpoint) {
									if(error) return video.onError(error);

									$("#"+targetId).data("peerInfo", {"webRtcPeer" : webRtcPeer, "pipeline" : tempPipeline});

									video.setIceCandidateCallbacks(webRtcEndpoint, webRtcPeer, video.onError);
			
									webRtcEndpoint.processOffer(sdpOffer, function(error, sdpAnswer){
										if(error) return video.onError(error);
			
										webRtcEndpoint.gatherCandidates(video.onError);
										webRtcPeer.processAnswer(sdpAnswer);
									});
			
									player.connect(webRtcEndpoint, function(error){
										if(error) return video.onError(error);
			
										//console.log("PlayerEndpoint-->WebRtcEndpoint connection established");
										//console.log(player);
										player.play(function(error){
										if(error) return video.onError(error);
			
										console.log("Player playing ...");
										});
									});
								});
							});
						});
					});
				}
				this.generateOffer(tempOffer);
			}
		);
	},
	showSpinner : function(t) {
		$(t).attr("poster", "/images/default/loading.gif");
	},
	hideSpinner : function(t) {
		$(t).attr("poster", "");
	},
	setIceCandidateCallbacks : function(webRtcEndpoint, webRtcPeer, onError) {
		webRtcPeer.on('icecandidate', function(candidate) {
			console.log("Local icecandidate " + JSON.stringify(candidate));
			candidate = kurentoClient.register.complexTypes.IceCandidate(candidate);
			webRtcEndpoint.addIceCandidate(candidate, onError);
	  	});
	  	webRtcEndpoint.on('OnIceCandidate', function(event) {
			var candidate = event.candidate;
			console.log("Remote icecandidate " + JSON.stringify(candidate));
			webRtcPeer.addIceCandidate(candidate, onError);
		});	
	},
	onError : function(error) {
		if(error) {
			console.error(error);
			//stop();
		}	
	}
}