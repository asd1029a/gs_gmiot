//2020-0922-0000
var console = window.console || {
    log: function() {}
};
console.log("Xeus-Gate Player VER: 2.1.0 2020-0922-0000");

var XeusGate = {
    Player: null,
    Source: null,
    Renderer: {},
    debug: false,
    Now: function() {
        return window.performance ? window.performance.now() / 1e3 : Date.now() / 1e3
    },

    BrowserType: function() {
        var agent = navigator.userAgent;
        var browser = 'Unknown';
        if (agent.search('Edge') >= 0) { // Edge/18.18362이상
            browser = 'Edge';
        } else if (agent.search('Trident') >= 0) { // IE11
            browser = 'Trident';
        } else if (agent.search('Whale') >= 0 || agent.search('OPR') >= 0 ||
            agent.search('Chrome') >= 0) {
            browser = 'Chrome';
        } else if (agent.search('Firefox') >= 0) {
            browser = 'Firefox';
        }
        return browser;
    },

    ToHex: function(byteArray) {
        return Array.prototype.map.call(byteArray, function(byte) {
            return ('00' + (byte & 0xFF).toString(16)).slice(-2);
        }).join(' ');
    },

    Log: function(msg) {
        if (this.debug) {
            console.log(msg);
        }
    },

    Queue: function() {
        var queue = [];
        var offset = 0;
        var capability = 10;

        this.getLength = function() {
            return (queue.length - offset);
        }

        this.isEmpty = function() {
            return (queue.length == 0);
        }

        this.enqueue = function(item) {
            // console.log("enqueue call......." + this.getLength());
            if ((queue.length - offset) > this.capability) {
                // console.log("enqueue is full ......." + this.getLength());
                this.dequeue();
            }
            queue.push(item);
            // console.log("enqueue push......." + this.getLength());
        }

        this.dequeue = function() {
            // console.log("dequeue........" + this.getLength());
            // if the queue is empty, return immediately
            if (queue.length == 0) return undefined;
            // store the item at the front of the queue
            var item = queue[offset];
            // increment the offset and remove the free space if necessary
            if (++offset * 2 >= queue.length) {
                queue = queue.slice(offset);
                offset = 0;
            }
            // return the dequeued item
            return item;
        }

        this.peek = function() {
            return (queue.length > 0 ? queue[offset] : undefined);
        }
    }
};

// ///////////////////////////////////////////
// XeusGate.Player
// ///////////////////////////////////////////

XeusGate.Player = function() {
    "user strict";

    var setStyles = function(element, styles) {
        for (var name in styles) {
            element.style[name] = styles[name]
        }
    };

    var Player = function(options) {
        this.options = options || {};
        this.options.browser = XeusGate.BrowserType();
        if (this.options.debug) {
            XeusGate.debug = this.options.debug;
        }

        if (!this.options.codec || (this.options.codec == 'auto')) {
            this.options.codec = 'h264';
        }

        if (!this.options.reconnect) {
            this.options.reconnect = true;
        }

        if (!this.options.timeout) {
            this.options.timeout = 20; //대기 시간 20초
        }

        this.playerElement = document.getElementById(this.options.playerId);
        this.playerVisible = true;
        var _self = this;

        document.addEventListener("visibilitychange", function() {
            if (document.visibilityState === 'hidden') {
                _self.playerVisible = false;
                XeusGate.Log("Player visibilitychange  hidden...");
                if (_self.options.videoElement) {
                    if (_self.renderer) {
                        _self.renderer.pause();
                    }
                }
            } else {
                XeusGate.Log("Player visibilitychange  visible...");
                _self.playerVisible = true;
                if (_self.options.videoElement) {
                    if (_self.renderer) {
                        _self.renderer.play();
                    }
                }
            }
        });

        // /
        if (this.options.codec == 'h264') {
            this.createVideoElement('video');
            this.renderer = new XeusGate.Renderer.H264(this, this.options);
            XeusGate.Log("Player create renderer : XeusGate.Renderer.H264");
        } else if (this.options.codec == 'mjpeg') {
            this.createVideoElement('img');
            this.renderer = new XeusGate.Renderer.MJPEG(this, this.options);
            XeusGate.Log("Player create renderer : XeusGate.Renderer.MJPEG");
        } else {
            XeusGate.Log("unsupported codec name: " + this.options.codec);
        }

        this.createTextElement();
        this.source = new XeusGate.Source(this, this.renderer, this.options);
        this.start();
    };

    Player.prototype.start = function() {
        if (this.renderer && this.source) {
            this.renderer.start();
            this.source.start();
            XeusGate.Log("Player started...");
        } else {
            XeusGate.Log("Player start: renderer or source is null.");
        }
    };

    Player.prototype.isPlayerVisible = function() {
        return this.playerVisible;
    };

    Player.prototype.showText = function(txt) {
        this.options.spanElement.innerHTML = txt;
        this.visible(this.options.textElement, true);
    };

    Player.prototype.visible = function(element, visible) {
        element.style.visibility = (visible) ? 'visible' : 'hidden';
    };

    Player.prototype.createTextElement = function() {
        var wrap = document.createElement('div');
        setStyles(wrap, {
            'display': "block",
            'width': '100%',
            'height': '100%',
            'position': 'absolute',
            'left': '0px',
            'top': '0px',
            'text-align': 'center',
            'background-color': 'rgb(34, 34, 34)'
        });
        var div = document.createElement('div');
        setStyles(div, {
            'position': 'relative',
            'top': '50%',
            'margin-top': '-1em',
            'background-color': 'rgb(34, 34, 34)'
        });
        var span = document.createElement('span');
        setStyles(span, {
            'font-size': '13px',
            'display': 'block',
            'color': 'rgb(180, 180, 180)'
        });
        div.appendChild(span);
        wrap.appendChild(div);
        this.playerElement.appendChild(wrap);
        this.options.textElement = wrap;
        this.options.spanElement = span;
        this.showText("연결을 시도합니다.");
        XeusGate.Log("Player TextElement created...");
    };

    Player.prototype.createVideoElement = function(elementName) {
        var w = '100%';
        var h = '100%';
        if (this.options.width) {
            w = this.options.width;
        }
        if (this.options.heigh) {
            h = this.options.height;
        }

        var video = document.createElement(elementName);
        if (elementName == 'video') {
            video.autoplay = true;
            video.muted = true;
            video.controls = false;
            video.loop = false;
            video.setAttribute('oncontextmenu', 'return false;');
        }
        setStyles(video, {
            'display': "block",
            'width': w,
            'height': h,
            // 'position': 'absolute',
            'left': '0px',
            'top': '0px',
            'transform': 'translateZ(0)',
            'background-color': 'rgb(34, 34, 34)'
        });
        this.playerElement.appendChild(video);
        this.options.videoElement = video;
        XeusGate.Log("Player VideoElement created...");
    };

    Player.prototype.moveto = function(time) {
        if (this.source) {
            this.source.moveto(time);
        }
    };

    Player.prototype.speed = function(spd) {
        if (this.source) {
            this.source.speed(spd);
        }
    };

    Player.prototype.pause = function() {
        if (this.source) {
            this.source.pause();
        }
    };

    Player.prototype.resume = function() {
        if (this.source) {
            this.source.resume();
        }
    };

    Player.prototype.forward = function() {
        if (this.source) {
            this.source.forward();
        }
    };

    Player.prototype.backward = function() {
        if (this.source) {
            this.source.backward();
        }
    };

    Player.prototype.close = function() {
        if (this.source) {
            this.source.close();
        }
        if (this.renderer) {
            this.renderer.close();
        }
        XeusGate.Log("Player closed...");
    };

    Player.prototype.destroy = function() {
        this.close();

        if (this.options.videoElement) {
            this.playerElement.removeChild(this.options.videoElement);
            this.options.videoElement = undefined;
        }
        if (this.options.textElement) {
            this.playerElement.removeChild(this.options.textElement);
            this.options.textElement = undefined;
        }
        if (this.source) {
            this.source.destroy();
        }
        if (this.renderer) {
            this.renderer.destroy();
        }
        XeusGate.Log("Player destroyed...");
    };
    return Player;
}();


XeusGate.Source = function() {
    "use strict";

    var WsSource = function(player, renderer, options) {
        this.options = options || {};
        this.socket = null;
        this.player = player;
        this.renderer = renderer;

        // this.reconnectTimeoutId = 0;
        this.connectIntervalId = 0; // 연결시간 체크용..
        this.retryCount = 0; // 연결 시도수..초
        this.message = "연결이 종료 되었습니다.";
        this.playing = false;
    };

    WsSource.prototype.start = function() {
        this.playing = false;
        this.socket = new WebSocket(this.options.url);
        this.socket.binaryType = "arraybuffer";
        this.socket.onmessage = this.onMessage.bind(this);
        this.socket.onopen = this.onOpen.bind(this);
        this.socket.onerror = this.onClose.bind(this);
        this.socket.onclose = this.onClose.bind(this);
        XeusGate.Log("WsSource Started...");
    };

    WsSource.prototype.onOpen = function() {
        this.playing = false;
        var cmd = "{" +
            "\"cmd\":\"open\"," +
            "\"cctvMgrNo\":\"" + this.options.cctvMgrNo + "\"," +
            "\"size\":\"" + this.options.size + "\"," +
            "\"bitrate\":\"" + this.options.bitrate + "\"," +
            "\"framerate\":\"" + this.options.framerate + "\"," +
            "\"userId\":\"" + this.options.userId + "\", " +
            "\"evtType\":\"" + this.options.evtType + "\", " +
            "\"timestamp\":\"" + this.options.timestamp + "\", " +
            "\"speed\":\"" + this.options.speed + "\", " +
            "\"codec\":\"" + this.options.codec + "\", " +
            "\"src\":\"" + this.options.rtspUrl + "\" " +
            "}";

        XeusGate.Log(cmd);

        this.socket.send(cmd);
        this.retryCount = 0;
        //
        this.connectIntervalId = setInterval(function() {
            this.player.showText("데이터 수신 대기중... (" + this.retryCount + "초)");
            if (this.retryCount >= this.options.timeout) { // 20초
                this.onClose();
            }
            this.retryCount++;
        }.bind(this), 1000); // 초
    };

    WsSource.prototype.onClose = function() {
        XeusGate.Log("WsSource onClosing...");

        this.playing = false;
        //this.retryCount = 0;
        //
        clearInterval(this.connectIntervalId); // 연결시간 초과 알림
        if (this.player) {
            this.player.showText(this.message);
            this.player.close();
        }
        // /
        // if(this.options.reconnect){
        // if(this.options.renderer){
        // this.options.renderer.destroy();
        // this.options.renderer = new XeusGate.Renderer.MPEG4(this.player, this.options);
        // }
        //
        // clearTimeout(this.reconnectTimeoutId); // 연결재시도..
        // this.reconnectTimeoutId = setTimeout(function() {
        // this.start()
        // }.bind(this), 5000); // 5초 후 재 연결 시도.
        // }//
        //
    };

    WsSource.prototype.onMessage = function(ev) {

        if (this.playing == false) {
            clearInterval(this.connectIntervalId); // 연결시간 초과 알림
            // clearTimeout(this.reconnectTimeoutId); // 연결재시도..
            this.playing == true;
        }

        if (this.renderer) {
            var packet = new Uint8Array(ev.data);

            var code = packet[0];
            if (code == 110) { // error
                // var err = packet[1];
                switch (packet[1]) { // error code
                    case 11:
                        this.message = "카메라 정보 없음.";
                        break;
                    case 12:
                        this.message = "영상 수신대기 초과.";
                        break;
                    case 13:
                        this.message = "영상 연결요청 실패.";
                        break;
                    case 14:
                        this.message = "Bridge 요청 오류.";
                        break;
                    case 15:
                        this.message = "요청처리 실패.";
                        break;
                    case 90:
                        this.message = "요청처리 실패.";
                        break;
                    default:
                        this.message = "연결종료.";
                        break;
                }
                // console.log("에러............." + packet[1] );
            } else if (code == 112) { // video
                this.renderer.feed(packet.subarray(1));
            }
        }
    };

    WsSource.prototype.moveto = function(time) {
        var cmd = "{" +
            "\"cmd\":\"playback\"," +
            "\"cctvMgrNo\":\"" + this.options.cctvMgrNo + "\"," +
            "\"mode\":\"GoTo\"," +
            "\"timestamp\":\"" + time + "\"" +
            "}";
        if (this.socket.readyState === this.socket.OPEN) {
            this.socket.send(cmd);
        }
    };

    WsSource.prototype.speed = function(spd) {
        var cmd = "{" +
            "\"cmd\":\"playback\"," +
            "\"cctvMgrNo\":\"" + this.options.cctvMgrNo + "\"," +
            "\"mode\":\"Speed\"," +
            "\"speed\":\"" + spd + "\"" +
            "}";
        if (this.socket.readyState === this.socket.OPEN) {
            this.socket.send(cmd);
        }
    };

    WsSource.prototype.pause = function() {
        var cmd = "{" +
            "\"cmd\":\"playback\"," +
            "\"cctvMgrNo\":\"" + this.options.cctvMgrNo + "\"," +
            "\"mode\":\"Pause\"" +
            "}";
        if (this.socket.readyState === this.socket.OPEN) {
            this.socket.send(cmd);
        }
    };

    WsSource.prototype.resume = function() {
        var cmd = "{" +
            "\"cmd\":\"playback\"," +
            "\"cctvMgrNo\":\"" + this.options.cctvMgrNo + "\"," +
            "\"mode\":\"Resume\"" +
            "}";
        if (this.socket.readyState === this.socket.OPEN) {
            this.socket.send(cmd);
        }
    };

    WsSource.prototype.forward = function() {
        var cmd = "{" +
            "\"cmd\":\"playback\"," +
            "\"cctvMgrNo\":\"" + this.options.cctvMgrNo + "\"," +
            "\"mode\":\"Forward\"" +
            "}";
        if (this.socket.readyState === this.socket.OPEN) {
            this.socket.send(cmd);
        }
    };

    WsSource.prototype.backward = function() {
        var cmd = "{" +
            "\"cmd\":\"playback\"," +
            "\"cctvMgrNo\":\"" + this.options.cctvMgrNo + "\"," +
            "\"mode\":\"Backward\"" +
            "}";
        if (this.socket.readyState === this.socket.OPEN) {
            this.socket.send(cmd);
        }
    };

    WsSource.prototype.close = function() {
        var cmd = "{" +
            "\"cmd\":\"close\"," +
            "\"cctvMgrNo\":\"" + this.options.cctvMgrNo + "\"," +
            "\"size\":\"" + this.options.size + "\"," +
            "\"bitrate\":\"" + this.options.bitrate + "\"," +
            "\"framerate\":\"" + this.options.framerate + "\"," +
            "\"userId\":\"" + this.options.userId + "\", " +
            "\"evtType\":\"" + this.options.evtType + "\", " +
            "\"timestamp\":\"" + this.options.timestamp + "\", " +
            "\"speed\":\"" + this.options.speed + "\", " +
            "\"codec\":\"" + this.options.codec + "\", " +
            "\"src\":\"" + this.options.rtspUrl + "\" " +
            "}";
        if (this.socket.readyState === this.socket.OPEN) {
            this.socket.send(cmd);
        }
        // XeusGate.Log(cmd);
        clearInterval(this.connectIntervalId); // 연결시간 초과 알림
        // clearTimeout(this.reconnectTimeoutId); // 연결재시도..
        this.retryCount = 0;
        this.playing == false;
        this.socket.close();
        XeusGate.Log("WsSource closed...");
    };

    WsSource.prototype.destroy = function() {
        this.player = null;
        this.renderer = null;
        XeusGate.Log("WsSource destroyed...");
    };

    return WsSource;
}();



// ///////////////////////////////////////////
// XeusGate.Renderer
// ///////////////////////////////////////////

XeusGate.Renderer.H264 = function() {
    "user strict";

    var H264Renderer = function(player, options) {
        this.CLEAN_INTERVAL = 10000; // 10초
        this.options = options || {};
        this.player = player;
        this.mediaSource = null;
        this.sourceBuffer = null;

        this.mediaSourceReady = false;
        this.initSegmentReady = false;
        this.codecSupported = true;
        this.initialized = false;
        this.codec = null;

        this.queue = new Uint8Array();

        this.lastCleanUpTime = 0;
        this.lastCleanUpOffset = 0;
    };

    H264Renderer.prototype.appendByteArray = function(buffer1, buffer2) {
        var tmp = new Uint8Array((buffer1.byteLength | 0) + (buffer2.byteLength | 0));
        tmp.set(buffer1, 0);
        tmp.set(buffer2, buffer1.byteLength | 0);
        return tmp;
    };

    H264Renderer.prototype.start = function() {
        window.MediaSource = window.MediaSource || window.WebKitMediaSource;
        var isMSESupported = !!window.MediaSource;
        if (!isMSESupported) {
            XeusGate.Log('Oops! Browser does not support media source extension.');
            this.player.destroy();
            return;
        }

        this.mediaSource = new MediaSource();
        this.options.videoElement.setAttribute('src', URL.createObjectURL(this.mediaSource));

        this.mediaSource.addEventListener('sourceopen', this.onMSEOpen.bind(this));
        this.mediaSource.addEventListener('sourceclose', this.onMSEClose.bind(this));
        this.mediaSource.addEventListener('webkitsourceopen', this.onMSEOpen.bind(this));
        this.mediaSource.addEventListener('webkitsourceclose', this.onMSEClose.bind(this));
        XeusGate.Log("H264Renderer Started...");
    };

    H264Renderer.prototype.onMSEOpen = function() {
        this.mediaSourceReady = true;
    };

    H264Renderer.prototype.onMSEClose = function() {
        XeusGate.Log("H264Renderer onMSEClose...");
        this.player.close();
    };


    H264Renderer.prototype.cleanUpBuffer = function() {
        if (this.options.videoElement.paused) {
            XeusGate.Log("cleanUp video paused.. return");
            return;
        }
        if (this.sourceBuffer.buffered && this.sourceBuffer.buffered.length) {
            for (var i = 0; i < this.sourceBuffer.buffered.length; ++i) {
                var start = this.sourceBuffer.buffered.start(i);
                var end = this.sourceBuffer.buffered.end(i);
                var cur = this.options.videoElement.currentTime;

                var endOffset = end - 20; //10초전
                if ((start < endOffset) && !this.sourceBuffer.updating) {
                    this.sourceBuffer.remove(this.lastCleanUpOffset, endOffset);
                    //XeusGate.Log("cleanUp cur: " + cur + " off: " + this.lastCleanUpOffset + " end: " + end);
                    this.lastCleanUpOffset = start;
                }
                XeusGate.Log("cleanUp curTime: " + cur + " endTime: " + end + " offset:" + this.lastCleanUpOffset);
            }
        }
    };

    H264Renderer.prototype.feed = function(data) {
        if (data.byteLength == 0) {
            return;
        }
        if (!this.codecSupported) {
            XeusGate.Log("Video codec not supported. " + this.codec);
            return;
        }
        var packet = new Uint8Array(data);
        if (!this.initSegmentReady) {
            this.options.videoElement.pause();
            //console.log(XeusGate.ToHex(packet));
            //console.log(XeusGate.ToHex(packet.subarray(0, 6)));
            var str = String.fromCharCode.apply(null, packet.subarray(0, 6));
            this.codec = 'video/mp4; codecs="avc1.' + str + '"';
            XeusGate.Log("H264Renderer codec: " + this.codec);

            if (!MediaSource.isTypeSupported(this.codec)) {
                this.codecSupported = false;
                XeusGate.Log("Video codec not supported. " + this.codec);
                return;
            }
            //console.log(XeusGate.ToHex(packet.subarray(6)));            
            this.queue = this.appendByteArray(this.queue, packet.subarray(6));
            //console.log("init queue..... " + this.queue.length);
            this.initSegmentReady = true;
        }

        var _self = this;
        if (this.initSegmentReady && this.mediaSourceReady) {
            if (!this.initialized) {
                var sb = this.mediaSource.addSourceBuffer(this.codec);
                this.sourceBuffer = sb;
                this.sourceBuffer.mode = 'sequence';
                this.sourceBuffer.addEventListener('updateend', function() {
                    if (_self.lastCleanUpTime == 0) {
                        _self.lastCleanUpTime = Date.now();
                    } else {
                        var curTime = _self.options.videoElement.currentTime;
                        if (curTime > 20) {
                            if ((Date.now() - _self.lastCleanUpTime) > _self.CLEAN_INTERVAL) { // 10초마다
                                _self.cleanUpBuffer();
                                _self.lastCleanUpTime = Date.now();
                            }
                        }
                    }
                });
                this.initialized = true;
                console.log("H264Renderer Initialized............. ");
            } else {

                this.queue = this.appendByteArray(this.queue, packet);
            }
        }

        if (this.initialized) {
            if ((this.options.textElement.style.visibility == 'visible')) {
                this.player.visible(this.options.textElement, false);
                if (this.options.videoElement.paused) {
                    this.options.videoElement.play(); // play시작
                    XeusGate.Log("H264Renderer playing....");
                }
            }
            this.update();
        }
    };

    H264Renderer.prototype.update = function() {
        if (!this.queue.length) {
            //console.log("queue.... empty..................");
            return;
        }

        try {
            if (!this.sourceBuffer.updating) {
                this.sourceBuffer.appendBuffer(this.queue);
                this.queue = new Uint8Array();
            }
        } catch (e) {
            XeusGate.Log(e.name + " : sourceBuffer.appendBuffer error");
        }

        // //////////////////////////////////////////////////////////////////////////////
//        try {
//            var curTime = this.options.videoElement.currentTime;
//            var ended = this.options.videoElement.ended;
//            if (curTime > 10) {
//                var endtime = this.options.videoElement.buffered.end(0);
//                if ((parseFloat(endtime) - parseFloat(curTime)) < 0.03) {
//                    this.options.videoElement.pause();
//                    XeusGate.Log("> etime-curtime<=0.03, paused. ctime: " + curTime + " eTime: " + endtime);
//                    if (parseFloat(curTime) >= parseFloat(endtime)) {
//                        this.options.videoElement.currentTime = parseFloat(endtime) - 0.03;
//                        XeusGate.Log("> curtime>endtime, (endtime-0.03): " + (this.options.videoElement.currentTime));
//                    }
//                } else {
//                    if (this.options.videoElement.paused) {
//                        this.options.videoElement.play(); // play시작
//                        XeusGate.Log("> etime-curtime>0.03, replayed. ctime: " + curTime + " eTime: " + endtime);
//                    }
//                }
//            }
//        } catch (e) {
//            XeusGate.Log(e.name + " : videoElement timecheck error");
//        }
        // /////////////////////////////////////////////////////////
    };

    H264Renderer.prototype.pause = function() {
        this.options.videoElement.pause();
    };

    H264Renderer.prototype.play = function() {
        this.options.videoElement.play();
    };

    H264Renderer.prototype.close = function() {
        if (this.mediaSource) {
            try {
                this.mediaSource.removeSourceBuffer(this.sourceBuffer);
                this.mediaSource.endOfStream();
            } catch (e) {}
            this.mediaSource = null;
        }
        this.sourceBuffer = null;
        this.mseReady = false;
        this.initialized = false;
        this.lastCleanUpTime = 0;
        this.lastCleanUpOffset = 0;
        this.queue = null;
        this.mediaSourceReady = false;
        this.initSegmentReady = false;
        XeusGate.Log("H264Renderer closed...");
    };

    H264Renderer.prototype.destroy = function() {
        XeusGate.Log("H264Renderer destroyed...");
    };
    return H264Renderer;
}();


// ///////////////////////////////////////////////
// MJPEG
// ///////////////////////////////////////////////
XeusGate.Renderer.MJPEG = function() {
    "user strict";

    var DEFAULT_FPS = 20;
    var JPEG_MAGIG_NUMBER = [0xff, 0xd9];

    var MjpegRenderer = function(player, options) {
        this.options = options || {};
        this.imageElement = this.options.videoElement;
        this.player = player;
        this.isPlaying = false;
        this.isDrawing = false;
        this.jpegUrl = null;
        this.interval;
    };

    MjpegRenderer.prototype.start = function() {
        this.isPlaying = false;
        this.isDrawing = false;
        this.jpegUrl = null;
        this.queue = new XeusGate.Queue();
        this.play();
        XeusGate.Log("MjpegRenderer Started...");
    };

    MjpegRenderer.prototype.feed = function(data) {
        // console.log("feed called. create.... " + this.queue.getLength());
        if (!this.isPlaying) {
            return;
        }
        // if (!this.isDrawing) {
        if (this.options.textElement.style.visibility == 'visible') {
            this.player.visible(this.options.textElement, false);
        }
        this.queue.enqueue(new Uint8Array(data));
        // }
        // this.render(new Uint8Array(data));
        // }else{
        // console.log("drawin...... pass");
        // }
    };

    MjpegRenderer.prototype.render = function(frame) {
        if (!frame) return;
        var len = frame.length;
        var _self = this;
        if (frame[len - 2] === JPEG_MAGIG_NUMBER[0] && frame[len - 1] === JPEG_MAGIG_NUMBER[1]) {
            // function showFrame() {
            _self.isDrawing = true;
            if (_self.jpegUrl) {
                URL.revokeObjectURL(_self.jpegUrl); // 메모리 해제.
            }
            _self.jpegUrl = URL.createObjectURL(new Blob([frame], {
                type: "image/jpeg"
            }));
            _self.imageElement.onload = function() {
                _self.isDrawing = false;
            };
            _self.imageElement.setAttribute('src', _self.jpegUrl);
            // }
            // requestAnimationFrame(showFrame);
        };
    };

    MjpegRenderer.prototype.pause = function() {
        if (this.interval) {
            clearInterval(this.interval);
        }
        this.isPlaying = false;
    };

    MjpegRenderer.prototype.play = function() {
        var _self = this;
        this.interval = setInterval(function() {
            var data = _self.queue.dequeue();
            //
            _self.render(data);
            //
        }, 1000 / _self.DEFAULT_FPS); // 24
        this.isPlaying = true;
    };

    MjpegRenderer.prototype.close = function() {
        this.isPlaying = false;
        this.isDrawing = false;
        this.jpegUrl = null;
        clearInterval(this.interval);
        XeusGate.Log("MjpegRenderer closed...");
    };

    MjpegRenderer.prototype.destroy = function() {
        XeusGate.Log("MjpegRenderer destroyed...");
    };

    return MjpegRenderer;
}();