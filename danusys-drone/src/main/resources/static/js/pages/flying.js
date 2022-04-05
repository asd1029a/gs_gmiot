'use strict'
document.addEventListener("DOMContentLoaded",function () {
    common.getQs(".lnbToggle").addEventListener("click", function() {
        common.getQs(".lnb").classList.toggle("toggle");

    });
    pageFlying.init();
});

const pageFlying = {
    init: function() {
        pageFlying.lnbMenuInit();
        mapManager.createVectorLayer("droneLayer",null,new ol.source.Vector({}));
        setDrawDrone.init();
        droneSocket.init();
    },
    lnbMenuInit: async function() {
        const ul = common.getQs(".listScroll ul");
        ul.innerText = "";
        const listData = await axios.post("/drone/api/dronemissiondetails", {});
        const listCnt = common.getQs(".subTitle span");
        listCnt.innerText = listData.data.length;

        listData.data.map((data) => {
            //set drone feature
            setDrawDrone.createFeature(data);
            setDrawDrone.createMissionFeature(data);

            const li = common.crtEl("li");
            li.dataset.id = data.id;
            li.addEventListener("click", function(e) {
                const mapComponent = common.getQs(".mapComponent");
                setDrawDrone.resetValue();
                mapComponent.querySelector(".values h6").innerText = `${data.droneDeviceName + " Values"}`;
                mapComponent.dataset.id = data.id;
                let mission = data.mission;
                if(mission.missionDetails[0]) {
                    mapManager.map.getView().setCenter(ol.proj.transform([mission.missionDetails[0].gpsX, mission.missionDetails[0].gpsY],mapManager.baseProjection, mapManager.projection));
                }
                pageFlying.setMissionSummary(mission);
            });
            const dl = common.crtEl("dl");
            const dlDt = common.crtEl("dt");
            const dtSpan1 = common.crtEl("span");
            dtSpan1.className = "gray";
            dtSpan1.innerText = "대기 중";
            dlDt.append(dtSpan1, data.droneDeviceName);
            const dlDd = common.crtEl("dd");
            const ddI = common.crtEl("i");
            const ddImg = common.crtEl("img");
            ddImg.src = "images/default/listMore.svg";
            ddI.append(ddImg);
            dlDd.append(ddI);
            dl.append(dlDt, dlDd);

            if(!data.mission.id) {
                li.append(dl);
                ul.append(li);
                return false;
            }
            const article = common.crtEl("article");
            article.className = "flightContents";

            const articleDl = common.crtEl("dl");
            const missionName = common.crtEl("dt");
            const totalDistance = common.crtEl("dd");
            const time = common.crtEl("dd");
            missionName.innerText = `미션명 : ${data.mission.name}`;
            totalDistance.innerText = `총 거리 : ${data.mission.totalDistance}`;
            time.innerText = `예상시간 : ${data.mission.estimatedTime}분`;
            
            articleDl.append(missionName, totalDistance, time);

            const routeUl = common.crtEl("ul");
            routeUl.className = "route";
            routeUl.style.display = "block";

            let i = 0;
            data.mission.missionDetails.map((mission) => {
                i += 1;
                const routeLi = common.crtEl("li");
                const span1 = common.crtEl("span");
                span1.className = "circle";
                const span2 = common.crtEl("span");
                span2.className = "text";
                span2.innerText = `${mission.koName} ${i}`;
                routeLi.append(span1, span2);
                routeUl.append(routeLi);
            });
            article.append(articleDl, routeUl);
            li.append(dl, article);
            ul.append(li);
        });
    },
    setMissionSummary: function(data) {
        const missionSummary = common.getQs(".missionSummary");
        missionSummary.style.display = "block";
        const summaryName = common.getQs("#summaryName");
        const summaryDistance = common.getQs("#summaryDistance");
        const summaryTime = common.getQs("#summaryTime");
        const summaryUserId = common.getQs("#summaryUserId");
        const summaryUpdateDt = common.getQs("#summaryUpdateDt");
        if(!data.id) {
            missionSummary.style.display = "none";
            return false;
        }

        summaryName.innerText = data.name;
        summaryDistance.innerText = data.totalDistance;
        summaryTime.innerText = data.estimatedTime;
        summaryUserId.innerText = data.userId;
        summaryUpdateDt.innerText = `(${data.updateDt})`
        /*summaryName.append(data.name);
        summaryDistance.append(data.totalDistance);
        summaryTime.append(data.time);
        summaryUserId.append(data.userId);
        summaryUpdateDt.append(`(${data.updateDt})`);*/
    },
    setContextMenu() {
        const contextItems = [
            {
                text: 'GO TO',
                classname: '',
                callback : setDrawDrone.createGoToPopup,
            },
            {
                text: 'YAW 변경',
                classname: '',
                callback : droneSocket.changeYaw,
            },
            {
                text: ' 미션 이동',
                classname: '',
                callback : droneSocket.skipMission,
            },
        ]
        mapManager.createContextMenu(contextItems);
    }
    ,
    mapComponentDisplay() {
        const mapComponent = common.getQs(".mapComponent");

        if(mapComponent.style.display == "none") {
            mapComponent.style.display = "block";
        } else {
            mapComponent.style.display = "none";
        }

    }
}

const setDrawDrone = {
    layer: null,
    droneId : undefined,
    init: function() {
        this.layer = mapManager.getVectorLayer("droneLayer");
    },
    
    // li 클릭 시 실행
    createFeature: function(data) {
        let droneFeatureType = new ol.geom.Point({});
        let directionPieType = new ol.geom.Point({});
        let missionRouteType = new ol.geom.LineString([[0,0],[0,0]]);


        //let feature = new ol.Feature();
        let droneFeature = mission.setFeature(droneFeatureType, setDrawDrone.setDroneStyle(),setDrawDrone.layer);
        let directionPie = mission.setFeature(directionPieType, setDrawDrone.setPieStyle(),setDrawDrone.layer);
        let missionRoute = mission.setFeature(missionRouteType, setDrawDrone.setRouteStyle(),setDrawDrone.layer);
        droneFeature.setId(data.id);
        directionPie.setId(`pie-${data.id}`);
        missionRoute.setId(`line-${data.id}`);

    },
    createMissionFeature: function(data) {
        if(!data.mission.missionDetails) {
            return false;
        }
        let lineStringCoordinates = [];
        data.mission.missionDetails.map((missionDetails) => {
            if(!missionDetails.gpsX) {
                
                //return lineString 좌표 설정
                lineStringCoordinates.push(lineStringCoordinates[0]);
                return false;
            }
            let obj = {};
            obj.droneId = data.id
            obj.coordinate = ol.proj.transform([missionDetails.gpsX,missionDetails.gpsY],mapManager.baseProjection,mapManager.projection);
            obj.index = missionDetails.index;

            lineStringCoordinates.push(obj.coordinate);
            if(missionDetails.name == "waypoint" || missionDetails.name == "takeOff") {
                obj.style = mission.setIconStyle(missionDetails.index + 1);
            } else if(missionDetails.name == "loi") {
                obj.style = mission.setIconStyle("L");
            } else if(missionDetails.name == "roi") {
                obj.style = mission.setIconStyle("R");
            } else if(missionDetails.name == "return"){
                return false;
            }
            setDrawDrone.drawMissionFeature(obj);
        });
        let lineFeatureType = new ol.geom.LineString(lineStringCoordinates);
        let lineFeature = mission.setFeature(lineFeatureType, waypoint.lineStyle(), setDrawDrone.layer);

    },
    // 미션 재생 시 실행
    reloadFeature: function(obj) {

        // create right click menu
        pageFlying.setContextMenu();
        // set state popup
        let bodyData = JSON.parse(obj.body);
        const mapComponent = common.getQs(`.mapComponent`);
        const flyAlt = mapComponent.querySelector(`[data-id='${bodyData.droneId}'] #flyAlt`);
        const flySpeed= mapComponent.querySelector(`[data-id='${bodyData.droneId}'] #flySpeed`);
        const flyTime = mapComponent.querySelector(`[data-id='${bodyData.droneId}'] #flyTime`);
        const flyYaw = mapComponent.querySelector(`[data-id='${bodyData.droneId}'] #flyYaw`);

        if(flyAlt) {
            flyAlt.innerText = bodyData.currentHeight;
            flySpeed.innerText = bodyData.airSpeed;
            flyTime.innerText = `${bodyData.hour}:${bodyData.min}:${bodyData.sec}`
            flyYaw.innerText = bodyData.heading;
        }

        // coordinate transform
        let coordinate = ol.proj.transform([bodyData.gpsX,bodyData.gpsY],mapManager.baseProjection,mapManager.projection);

        // setting left menu
        let li = common.getQs(`.listScroll li[data-id='${bodyData.droneId}']`);
        const span = li.querySelector(`dt span`);
        const circle = common.arr.call(li.querySelectorAll(".route li .circle"));

        // set drone, pie, missionLine, progressiveMission
        let drone = mapManager.getVectorLayer("droneLayer").getSource().getFeatureById(bodyData.droneId);
        let pie = mapManager.getVectorLayer("droneLayer").getSource().getFeatureById(`pie-${bodyData.droneId}`);
        let missionLine = mapManager.getVectorLayer("droneLayer").getSource().getFeatureById(`line-${bodyData.droneId}`);
        let progressiveMission;


        //class circle on
        circle.map((data) => {
            data.classList.remove("on");
        });

        // drone parse
        if(!bodyData.status) {

            // end mission
            span.className = "gray";
            span.innerText = "대기 중";
        } else if(bodyData.status == "2") {

            // pause mission
            span.className = "yellow";
            span.innerText = "비행 중";
        } else {

            // play mission
            span.className = "green";
            span.innerText = "비행 중";
        }

        // end mission
        if(bodyData.missionType == "end") {

            // drone, pie, missionLine feature reset
            drone.getGeometry().setCoordinates([0,0]);
            pie.getGeometry().setCoordinates([0,0]);
            missionLine.getGeometry().setCoordinates([[0,0],[0,0]]);

            setDrawDrone.resetValue();
            return false;
        } else if(bodyData.missionType == "return") {

            //return mission
            bodyData.missionType = 0;
            circle[circle.length -1].classList.add("on");
        } else if(bodyData.missionType == "waypoint") {

        } else {

            // mission index
            circle[bodyData.missionType].classList.add("on");
        }

        // set coordinate of drone, pie, missionLine
        drone.getGeometry().setCoordinates(coordinate);
        pie.getGeometry().setCoordinates(coordinate);
        progressiveMission = mapManager.getVectorLayer("droneLayer").getSource().getFeatureById(`mission-${bodyData.droneId}-${bodyData.missionType}`);
        missionLine.getGeometry().setCoordinates([coordinate, progressiveMission.getGeometry().getCoordinates()]);
        setDrawDrone.setRotate(bodyData.heading,drone, pie);
    },

    setDroneStyle() {
        let style = new ol.style.Style({
            image: new ol.style.Icon({
                scale: 1,
                src: "/images/test/drone.png",
            }),
            zIndex: 101,
        });
        return style;
    },
    setPieStyle() {
        let style = new ol.style.Style({
            image: new ol.style.Icon({
                src: 'images/svg/cone_pie.svg',
                opacity: 0.8,
                scale: 4.5,
                anchor: [0.5, 0.5],
            }),
            zIndex: 102,
        });
    return style;
    },

    setRouteStyle() {
        let style= new ol.style.Style({
            fill: new ol.style.Fill({
                color: 'rgba(255, 255, 255, 0.2)',
            }),
            stroke: new ol.style.Stroke({
                color: 'red',
                width: 2,
                lineDash: [4,8]
            })
        });
        return style;
    },
    resetValue() {
        // set state popup
        common.getQs("#flyAlt").innerText = "0";
        common.getQs("#flySpeed").innerText = "0";
        common.getQs("#flyTime").innerText = "00:00:00";
        common.getQs("#flyYaw").innerText = "0";
    },
    drawMissionFeature(data) {
        let featureType = new ol.geom.Point({});
        featureType.setCoordinates(data.coordinate);
        let style = data.style;
        let feature = mission.setFeature(featureType, style, setDrawDrone.layer);
        /*let lineFeatureType = new ol.geom.LineString([[0,0],[0,0]]);
        lineFeatureType.setCoordinates([[],[]]);
        let lineFeature = mission.setFeature(lineFeatureType, waypoint.lineStyle(), setDrawDrone.layer);*/


        feature.setId(`mission-${data.droneId}-${data.index}`);
    },
    createGoToPopup(e) {
        let data = e.coordinate;
        let coordinate = ol.proj.transform(data,mapManager.projection, mapManager.baseProjection);

        const popup = common.getQs(`.popup`);
        popup.style.display = "block";
        const gotoX = common.getQs(`#goToGpsX`);
        const gotoY = common.getQs(`#goToGpsY`);
        const gotoAlt = common.getQs(`#goToAlt`);
        const gotoYaw = common.getQs(`#goToYaw`);

        gotoX.value = coordinate[0];
        gotoY.value = coordinate[1];
        gotoAlt.placeholder = "0.00";
        gotoYaw.placeholder = "0";
    },
    createGoToMission(data) {
        data.closest(".popup").style.display= "none";
        const obj = {};
        const gpsX = common.getQs(`#goToGpsX`).value;
        const gpsY = common.getQs(`#goToGpsY`).value;
        const alt = common.getQs(`#goToAlt`).value;
        const yaw = common.getQs(`#goToYaw`).value;
        obj.gpsX = gpsX;
        obj.gpsY = gpsY;
        obj.alt = alt;
        obj.yaw = Number.parseInt(yaw);

        if(mapManager.getVectorLayer("droneLayer").getSource().getFeatureById("flag")) {
            setDrawDrone.layer.getSource().removeFeature(mapManager.getVectorLayer("droneLayer").getSource().getFeatureById("flag"));
        }

        let featureType = new ol.geom.Point({});
        let coordinate = ol.proj.transform([obj.gpsX,obj.gpsY],mapManager.baseProjection,mapManager.projection);
        featureType.setCoordinates(coordinate);
        let feature = mission.setFeature(featureType, this.gotoMissionIconStyle(), this.layer);
        feature.setId("flag");

        droneSocket.gotoMission(obj);

    },
    gotoMissionIconStyle: function() {
        let style = new ol.style.Style({
            image: new ol.style.Icon({
                scale: 1,
                src: "/images/test/flag.png",
            }),
            zIndex: 99,
        });
        return style;
    },

    closePopup() {
        const popup = common.getQs(`.popup`);
        popup.style.display = "none";
    },
    reloadGotoFeature(data) {
        const flyAlt = common.getQs("#flyAlt");
        const flySpeed= common.getQs("#flySpeed");
        const flyTime = common.getQs("#flyTime");
        const flyYaw = common.getQs("#flyYaw");


        let bodyData = JSON.parse(data.body);
        let coordinate = ol.proj.transform([bodyData.gpsX,bodyData.gpsY],mapManager.baseProjection,mapManager.projection);

        let id = bodyData.droneId;
        let drone = mapManager.getVectorLayer("droneLayer").getSource().getFeatureById(id);
        let missionLine = mapManager.getVectorLayer("droneLayer").getSource().getFeatureById(`line-${id}`);
        let pie = mapManager.getVectorLayer("droneLayer").getSource().getFeatureById(`pie-${id}`);
        let flagFeature = mapManager.getVectorLayer("droneLayer").getSource().getFeatureById(`flag`);


        missionLine.getGeometry().setCoordinates([coordinate, flagFeature.getGeometry().getCoordinates()]);
        drone.getGeometry().setCoordinates(coordinate);
        pie.getGeometry().setCoordinates(coordinate);

        this.setRotate(bodyData.heading, drone, pie);
        flyAlt.innerText = bodyData.currentHeight;
        flySpeed.innerText = bodyData.airSpeed;
        flyTime.innerText = `${bodyData.hour}:${bodyData.min}:${bodyData.sec}`
        flyYaw.innerText = bodyData.heading;

        if(bodyData.missionType == "end") {
            droneSocket.stompClient.subscribe('/topic/log', function (drone) {
                flagFeature.getGeometry().setCoordinates([0,0]);
                setDrawDrone.reloadFeature(drone);
            }, {id: "missionLog"});
        };
    },
    setRotate(heading, drone, pie) {
        drone.getStyle().getImage().setRotation(heading * Math.PI/180 );
        pie.getStyle().getImage().setRotation((heading-90) * Math.PI/180 );
    },
}

const droneSocket = {
    missionStartState: 0,
    stompClient: null,
    socket: null,
    connect: function() {
        let self = this;
        this.socket = new SockJS("ws");
        this.stompClient = Stomp.over(this.socket);
        this.stompClient.debug = null;
        this.stompClient.connect({}, function(frame) {
            console.log('Connected: ' + frame);
            self.stompClient.subscribe('/topic/startmission', function (drone) {
            });
            self.stompClient.subscribe('/topic/log', function (drone) {
                console.log(drone + " : topic/log ");
                setDrawDrone.reloadFeature(drone);
            }, {id: "missionLog"});
            self.stompClient.subscribe('/topic/pause',function (drone){
                console.log(drone);
            });
            self.stompClient.subscribe('/topic/play',function (drone){
                console.log(drone);
            });
            self.stompClient.subscribe('/topic/waypoint',function (drone){
                console.log(drone + " : topic/waypoint");
                setDrawDrone.reloadGotoFeature(drone);
            }, {id: "goToMission"});
            self.stompClient.subscribe('/topic/arm',function (drone){
                console.log(drone);
            });
            self.stompClient.subscribe('/topic/disarm',function (drone){
                console.log(drone);
            });
        });
    },
    disConnect: function() {
        this.stompClient.disconnect();
    },
    startMission: function() {
        let droneId = Number.parseInt(common.getQs(".mapComponent").dataset.id);
        this.stompClient.send("/app/startmission", {}, JSON.stringify({'droneId': droneId}));
    },
    pauseMission: function() {
        let startAndStop = common.getQs("#startAndStop span");
        //default : 0
        if(droneSocket.missionStartState) {
            droneSocket.playMission();
            return false;
        }
        startAndStop.innerText = "재생"
        droneSocket.missionStartState = 1;
        let droneId = common.getQs(".mapComponent").dataset.id;
        //droneId = 40;
        this.stompClient.send("/app/pause", {}, JSON.stringify({'droneId': droneId}))
    },
    playMission: function() {
        let startAndStop = common.getQs("#startAndStop span");
        startAndStop.innerText = "일시정지";
        droneSocket.missionStartState = 0;
        let droneId = common.getQs(".mapComponent").dataset.id;
        //droneId = 40;
        this.stompClient.send("/app/play", {}, JSON.stringify({'droneId': droneId}));
    },
    returnMission: function() {
        let droneId = common.getQs(".mapComponent").dataset.id;
        this.stompClient.send("/app/return", {}, JSON.stringify({'droneId': droneId}));
    },
    changeYaw: function(obj= {}) {
        // {yaw: number}
        let droneId = Number.parseInt(common.getQs(".mapComponent").dataset.id);
        let yaw = prompt("변경할 YAW를 입력해 주세요.")
        obj.yaw = yaw;
        obj.droneId=droneId;
        droneSocket.stompClient.send("/app/changeyaw", {}, JSON.stringify(obj));
    },
    gotoMission: function(obj) {

        let droneId = Number.parseInt(common.getQs(".mapComponent").dataset.id);
        this.stompClient.unsubscribe("missionLog");
        obj.droneId=droneId;
        this.stompClient.send("/app/waypoint", {}, JSON.stringify(obj));
    },
    //obj.seq = (number)
    skipMission: function(obj = {}) {
        let droneId = Number.parseInt(common.getQs(".mapComponent").dataset.id);
        let missionNo = prompt("이동 할 미션 번호를 입력해 주세요.")
        obj.seq = Number.parseInt(missionNo) - 1;
        obj.droneId=droneId;
        droneSocket.stompClient.send("/app/setmissioncurrent", {}, JSON.stringify(obj));
    },
    init: function() {
        this.connect();
    },
};

const flyingVideo = {
    state: 0,
    videoPlay() {
        if(flyingVideo.state) {
            flyingVideo.videoStop();
            return false;
        }
        this.state = 1;
        const obj = {};
        obj.turnUrl = "172.20.14.49:3478";
        obj.credential = "turnadm123";
        obj.username = "turnadm";
        obj.mediaServerWsUrl = "ws://172.20.14.49:8888/kurento";
        obj.rtspUrl = "rtsp://admin:1234@172.20.19.4/video1";
        video.directVideoStart(obj,"videoPlayer");
    },
    videoStop() {
        this.state = 0;
        video.directVideoStop("videoPlayer");
    }
}