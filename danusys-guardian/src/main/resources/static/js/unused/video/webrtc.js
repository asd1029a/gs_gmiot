/* webrtc.js */

var webrtc = {
	load : function() {
		args = this.getOpts(location.search, {
			default: {
				ws_uri: mediaServerWsUrl,
				ice_servers: undefined
			}
		});

		if (args.ice_servers) {
			console.log("Use ICE servers: " + args.ice_servers);
			// kurentoUtils.WebRtcPeer.prototype.server.iceServers =
			// JSON.parse(args.ice_servers);
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
	stop : function(data) {
		if(data.peer) {
			data.peer.dispose();
			data.peer = null;
		}
		
		if(data.pipeline) {
			data.pipeline.release();
			data.pipeline = null;
		}

		$(data.spinner).hide();
		$(data.errorView).toggle();
	},
	start : function(data, el) {
		options = {
			remoteVideo : el,
			audio : false,
			video : {
				framerate : 30
				//width : 500
			}
		};
		// 우측1
		data.peer = kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(options,
			function(error) {
				if(error) {
					return console.error(error);
				}
				this.generateOffer(offer);
			}
		);
		
		function offer(error, sdpOffer) {
			if(error) return onError(error, data);

			kurentoClient(args.ws_uri, function(error, kurentoClient) {
				if(error) return onError(error, data);
				
				kurentoClient.create("MediaPipeline", function(error, p) {
					if(error) return onError(error, data);

					data.pipeline = p;

					data.pipeline.create("PlayerEndpoint", {uri: data.rtspUrl, useEncodedMedia: true}, function(error, player) {
						if(error) return onError(error, data);

						data.pipeline.create("WebRtcEndpoint", function(error, webRtcEndpoint){
							if(error) return onError(error, data);

							setIceCandidateCallbacks(webRtcEndpoint, data.peer, onError); 

							webRtcEndpoint.processOffer(sdpOffer, function(error, sdpAnswer){
								if(error) return onError(error, data);

								webRtcEndpoint.gatherCandidates(onError);
								data.peer.processAnswer(sdpAnswer);
							});

							player.connect(webRtcEndpoint, function(error){
								if(error) return onError(error, data);

								//console.log("PlayerEndpoint-->WebRtcEndpoint connection established");
								//console.log(player);	
								player.play(function(error){
									if(error) return onError(error, data);
	
									console.log("하단 투망 Play [1] ...");
									$(data.spinner).hide();
								});
							});
						});
					});
				});
			});
		}
	},
	getRtsp : function(data, el, callback) {
		const jsonObj = {};
		const ch = data.fcltId.split('_')[2];
		
		jsonObj.code = "4000";
		jsonObj.node_id = data.nodeId;
		jsonObj.ch = ch;
		jsonObj.client = clientInfo.sessionId;
		
		$.ajax({
			type       : "POST",
			url        : "http://" + serverInfo.midServerIp + ":" + serverInfo.midServerPort + "/api/athena/midsvr",
			dataType   : "text",
			data       : JSON.stringify(jsonObj),
			async      : false
		}).done(function(result) {
			const jsonData = JSON.parse(result);
			const rtspUrl = jsonData.rtsp_url;
			
			if(rtspUrl === '') {
				webrtc.stop(data);
				return;
			}
			
			data.rtspUrl = jsonData.rtsp_url;
			if(callback !== undefined) callback(data, el);
		});
	}
}

function setIceCandidateCallbacks(webRtcEndpoint, webRtcPeer, onError){
  	webRtcPeer.on('icecandidate', function(candidate) {
    	//console.log("Local icecandidate " + JSON.stringify(candidate));

    	candidate = kurentoClient.register.complexTypes.IceCandidate(candidate);
    	webRtcEndpoint.addIceCandidate(candidate, onError);
  });
  webRtcEndpoint.on('OnIceCandidate', function(event) {
    var candidate = event.candidate;
		//console.log("Remote icecandidate " + JSON.stringify(candidate));

		webRtcPeer.addIceCandidate(candidate, onError);
	});
}

function onError(error, data) {
  	if(error) {
    	console.error(error);
    	webrtc.stop(data);
  	}
}

webrtc.load();