var libwebrtc = (function() {

/** 
 * @constructor
 * @param {string} videoElement - id of the video element tag
 * @param {string} srvurl -  url of webrtc-streamer (default is current location)
*/
var libwebrtc = function libwebrtc (videoElement, srvurl) 
{
	if (typeof videoElement === "string") 
	{
		this.videoElement = document.getElementById(videoElement);
	} 
	else 
	{
		this.videoElement = videoElement;
	}
	this.srvurl           = srvurl;// || location.protocol+"//"+window.location.hostname+":"+window.location.port;
	this.pc               = null;    

	this.pcOptions        = { "optional": [{"DtlsSrtpKeyAgreement": true} ] };

	this.mediaConstraints = { offerToReceiveAudio: true, offerToReceiveVideo: true };

	this.iceServers = null;
	this.earlyCandidates = [];
}

/** 
 * Connect a WebRTC Stream to videoElement 
 * @param {string} videourl - id of WebRTC video stream
 * @param {string} audiourl - id of WebRTC audio stream
 * @param {string} options -  options of WebRTC call
 * @param {string} stream  -  local stream to send
*/
libwebrtc.prototype.connect = function(videourl, audiourl, options, localstream) 
{
	this.disconnect();
	
	// getIceServers is not already received
	if (!this.iceServers) 
	{
		console.log("Get IceServers");	
		var bind = this;
		request("GET" , this.srvurl + "/api/getIceServers")
			.done( function (response) { 
				if (response.statusCode === 200) 
				{
					bind.on_receive_get_ice_servers.call(bind,JSON.parse(response.body), videourl, audiourl, options, localstream);
				}
				else 
				{
					bind.onError("getIceServers "+response.statusCode);
				}
			}
		);		
	} else {
		this.on_receive_get_ice_servers(this.iceServers, videourl, audiourl, options, localstream);
	}
}

/** 
 * Disconnect a WebRTC Stream and clear videoElement source
*/
libwebrtc.prototype.disconnect = function() 
{		
	if (this.videoElement) 
	{
		this.videoElement.src = "";
	}
	if (this.pc) 
	{
		request("GET" , this.srvurl + "/api/hangup?peerid="+this.pc.peerid);
		
		try {
			this.pc.close();
		}
		catch (e) {
			console.log ("Failure close peer connection:" + e);
		}
		this.pc = null;
	}
}    

/*
* GetIceServers callback
*/
libwebrtc.prototype.on_receive_get_ice_servers = function(iceServers, videourl, audiourl, options, stream) 
{
	this.iceServers       = iceServers;
	this.pcConfig         = iceServers || {"iceServers": [] };
	try {            
		this.createPeerConnection();

		var callurl = this.srvurl + "/api/call?peerid="+ this.pc.peerid+"&url="+encodeURIComponent(videourl);
		if (audiourl) 
		{
			callurl += "&audiourl="+encodeURIComponent(audiourl);
		}
		if (options) 
		{
			callurl += "&options="+encodeURIComponent(options);
		}
		
		if (stream) {
			this.pc.addStream(stream);
		}

                // clear early candidates
		this.earlyCandidates.length = 0;
		
		// create Offer
		var bind = this;
		this.pc.createOffer(this.mediaConstraints).then(function(sessionDescription) 
		{
			console.log("Create offer:" + JSON.stringify(sessionDescription));	
			bind.pc.setLocalDescription(sessionDescription
				, function() {
					request("POST" , callurl, { body: JSON.stringify(sessionDescription) })
						.done( function (response) { 
							if (response.statusCode === 200) {
								bind.on_receive_call.call(bind,JSON.parse(response.body));
							}
							else {
								bind.onError("call " + response.statusCode);
							}
						}
					);					
				}
				, function(error) {
					console.log ("setLocalDescription error:" + JSON.stringify(error)); 
				} );
			
		}, function(error) { 
			alert("Create offer error:" + JSON.stringify(error));
		});

	} catch (e) {
		this.disconnect();
		alert("connect error: " + e);
	}	    
}


libwebrtc.prototype.get_ice_candidate = function() 
{
	var bind = this;
	request("GET" , this.srvurl + "/api/getIceCandidate?peerid=" + this.pc.peerid)
		.done( function (response) { 
			if (response.statusCode === 200) {
				bind.on_receive_candidate.call(bind,JSON.parse(response.body));
			}
			else {
				bind.onError("getIceCandidate" + response.statusCode);
			}
		}
	);
}
					
/*
* create RTCPeerConnection 
*/
libwebrtc.prototype.createPeerConnection = function() 
{
	console.log("createPeerConnection  config: " + JSON.stringify(this.pcConfig) + " option:"+  JSON.stringify(this.pcOptions));
	this.pc = new RTCPeerConnection(this.pcConfig, this.pcOptions);
	var pc = this.pc;
	pc.peerid = Math.random();		
	
	var bind = this;
	pc.onicecandidate = function(evt) { bind.on_ice_candidate.call(bind, evt); };
	pc.onaddstream    = function(evt) { bind.on_add_stream.call(bind,evt); };
	pc.iceTransportPolicy = "relay";
	pc.oniceconnectionstatechange = function(evt) 
	{
		console.log("oniceconnectionstatechange  state: " + pc.iceConnectionState);
		if (bind.videoElement) 
		{
			if (pc.iceConnectionState === "connected") 
			{
				bind.videoElement.style.opacity = "1.0";
			}			
			else if (pc.iceConnectionState === "disconnected") 
			{
				bind.videoElement.style.opacity = "0.25";
			}			
			else if ( (pc.iceConnectionState === "failed") || (pc.iceConnectionState === "closed") )  
			{
				bind.videoElement.style.opacity = "0.5";
			}
			else if (pc.iceConnectionState === "new") 
			{
				bind.get_ice_candidate.call(bind)
			}
		}
	}
	pc.ondatachannel = function(evt) 
	{  
		console.log("remote datachannel created:"+JSON.stringify(evt));
		
		evt.channel.onopen = function () 
		{
			console.log("remote datachannel open");
			this.send("remote channel openned");
		}
		evt.channel.onmessage = function (event) 
		{
			console.log("remote datachannel recv:"+JSON.stringify(event.data));
		}
	}

	try {
		var dataChannel = pc.createDataChannel("ClientDataChannel");
		dataChannel.onopen = function() 
		{
			console.log("local datachannel open");
			this.send("local channel openned");
		}
		dataChannel.onmessage = function(evt) 
		{
			console.log("local datachannel recv:"+JSON.stringify(evt.data));
		}
	} catch (e) {
		console.log("Cannor create datachannel error: " + e);
	}	
	
	console.log("Created RTCPeerConnnection with config: " + JSON.stringify(this.pcConfig) + "option:"+  JSON.stringify(this.pcOptions) );
	return pc;
}

libwebrtc.prototype.on_ice_candidate = function (event) 
{
	if (event.candidate) 
	{
		var tmp = event.candidate.candidate;
		if(tmp.indexOf("typ relay ")>=0) {
			if (this.pc.currentRemoteDescription)  
			{
				this.addIceCandidate(this.pc.peerid, event.candidate);					
			} else {
				this.earlyCandidates.push(event.candidate);
			}
		}
	} 
	else 
	{
		console.log("End of candidates.");
	}
}

libwebrtc.prototype.addIceCandidate = function(peerid, candidate) 
{
	var bind = this;
	request("POST" , this.srvurl + "/api/addIceCandidate?peerid="+peerid, { body: JSON.stringify(candidate) })
		.done( function (response) { 
			if (response.statusCode === 200) {
				console.log("addIceCandidate ok:" + response.body);
			}
			else {
				bind.onError("addIceCandidate " +response.statusCode);
			}
		}
	);
}
				
libwebrtc.prototype.on_add_stream = function(event) 
{
	console.log("Remote track added:" +  JSON.stringify(event));
	this.videoElement.srcObject = event.stream;
	var promise = this.videoElement.play();
	if (promise !== undefined) 
	{
		var bind = this;
		promise.catch(function(error) 
	 	{
			console.warn("error:"+error);
			bind.videoElement.setAttribute("controls", true);
	  	});
	}
}
		
libwebrtc.prototype.on_receive_call = function(dataJson) 
{
	var bind = this;
	console.log("offer: " + JSON.stringify(dataJson));
	var descr = new RTCSessionDescription(dataJson);
	this.pc.setRemoteDescription(descr
		, function() { 
			console.log ("setRemoteDescription ok");
			while (bind.earlyCandidates.length) {
				var candidate = bind.earlyCandidates.shift();
				bind.addIceCandidate.call(bind, bind.pc.peerid, candidate);				
			}
		
			bind.get_ice_candidate.call(bind)
		}
		, function(error) { 
			console.log ("setRemoteDescription error:" + JSON.stringify(error)); 
		});
}	

libwebrtc.prototype.on_receive_candidate = function(dataJson) 
{
	console.log("candidate: " + JSON.stringify(dataJson));
	if (dataJson) 
	{
		for (var i=0; i<dataJson.length; i++) 
		{
			var candidate = new RTCIceCandidate(dataJson[i]);	
			console.log("Adding ICE candidate :" + JSON.stringify(candidate) );
			this.pc.addIceCandidate(candidate
				, function()      { console.log ("addIceCandidate OK"); }
				, function(error) { console.log ("addIceCandidate error:" + JSON.stringify(error)); } );
		}
		this.pc.addIceCandidate();
	}
}

libwebrtc.prototype.onError = function(status) 
{
	console.log("onError:" + status);
}

return libwebrtc;
})();

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
	module.exports = libwebrtc;
else
	window.libwebrtc = libwebrtc;
