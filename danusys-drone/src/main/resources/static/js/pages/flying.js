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
            const li = common.crtEl("li");
            li.dataset.id = data.id;
            li.addEventListener("click", function(e) {
                const mapComponent = common.getQs(".mapComponent");
                mapComponent.dataset.id = data.id;
                let check = setDrawDrone.createFeature(data.id);
                if(!data.mission) {
                    return false;
                }
                if(!check) {
                    return false;
                }
                pageFlying.lnbMenuClickEvent(data.mission);
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

            if(!data.mission) {
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
    lnbMenuClickEvent(data) {
        data.missionDetails.map((mission) => {
            let obj = {};
            obj.coordinate = ol.proj.transform([mission.gpsX,mission.gpsY],mapManager.baseProjection,mapManager.projection);
            obj.index = mission.index;
            if(mission.name == "waypoint" ) {
                obj.style = waypoint.waypointStyle(mission.index + 1);
            } else if(mission.name == "takeOff") {
                obj.style = takeOff.setStyle();
            } else if(mission.name == "loi") {
                obj.style = loi.loiStyle();
            } else if(mission.name == "roi") {
                obj.style = roi.roiStyle();
            }
            setDrawDrone.drawMissionFeature(obj);
        })
    }
}

const setDrawDrone = {
    layer: null,
    droneId : undefined,
    init: function() {
        this.layer = mapManager.getVectorLayer("droneLayer");
    },
    
    // li 클릭 시 실행
    createFeature: function(id) {
        if(this.layer.getSource().getFeatureById(id)) {
           return false;
        }
        let droneFeatureType = new ol.geom.Point({});
        let directionPieType = new ol.geom.Point({});
        let missionRouteType = new ol.geom.LineString([0,0]);


        //let feature = new ol.Feature();
        let droneFeature = mission.setFeature(droneFeatureType, this.setDroneStyle(),this.layer);
        let directionPie = mission.setFeature(directionPieType, this.setPieStyle(),this.layer);
        let missionRoute = mission.setFeature(missionRouteType, setDrawDrone.setRouteStyle(),this.layer);
        droneFeature.setId(id);
        directionPie.setId(`pie-${id}`);
        missionRoute.setId(`line-${id}`);

    },
    
    // 미션 재생 시 실행
    reloadFeature: function(obj) {
        // set state popup
        const flyAlt = common.getQs("#flyAlt");
        const flySpeed= common.getQs("#flySpeed");
        const flyTime = common.getQs("#flyTime");
        const flyYaw = common.getQs("#flyYaw");

        if(mapManager.getVectorLayer("droneLayer").getSource().getFeatures().length == 0) {
            return false;
        }
        let bodyData = JSON.parse(obj.body);
        let coordinate = ol.proj.transform([bodyData.gpsX,bodyData.gpsY],mapManager.baseProjection,mapManager.projection);
        //mapManager.getVectorLayer("droneLayer").getSource().getFeatureById(this.droneId).getGeometry().setCenterAndRadius(coordinate, 50);
        if(bodyData.droneId != common.getQs(".mapComponent").dataset.id) {
            //setDrawDrone.clearPopup();
            flyAlt.innerText = "0"
            flyTime.innerText = "00:00:00"
            flySpeed.innerText = "0"
            flyYaw.innerText = "0"

            return false;
        }

        if(bodyData.missionType == "end") {
            flyAlt.innerText = "0";
            return false;
        }

        const li = common.getQs(`.listScroll li[data-id='${bodyData.droneId}']`);
        common.arr.call(li.querySelectorAll(".route li .circle")).map((data) => {
            data.classList.remove("on");
        });

        if(bodyData.missionType == "return") {
            bodyData.missionType = 0;
        }

        let drone = mapManager.getVectorLayer("droneLayer").getSource().getFeatureById(bodyData.droneId);
        let pie = mapManager.getVectorLayer("droneLayer").getSource().getFeatureById(`pie-${bodyData.droneId}`);
        let missionLine = mapManager.getVectorLayer("droneLayer").getSource().getFeatureById(`line-${bodyData.droneId}`);
        let missionFeature = mapManager.getVectorLayer("droneLayer").getSource().getFeatureById(`mission-${bodyData.missionType}`);

        drone.getGeometry().setCoordinates(coordinate);
        pie.getGeometry().setCoordinates(coordinate);
        missionLine.getGeometry().setCoordinates([coordinate, missionFeature.getGeometry().getCenter()]);
        drone.getStyle().getImage().setRotation(bodyData.heading * Math.PI/180 );
        pie.getStyle().getImage().setRotation((bodyData.heading-90) * Math.PI/180 );

        flyAlt.innerText = bodyData.currentHeight;
        flySpeed.innerText = bodyData.airSpeed;
        flyTime.innerText = `${bodyData.hour}:${bodyData.min}:${bodyData.sec}`
        flyYaw.innerText = bodyData.heading;
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
            zIndex: 101,
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
    clearPopup() {
        // set state popup
        common.getQs("#flyAlt").innerText = "";
        common.getQs("#flySpeed").innerText = "";
        common.getQs("#flyTime").innerText = "";
        common.getQs("#flyYaw").innerText = "";
    },
    drawMissionFeature(data) {
        let featureType = new ol.geom.Circle({});
        featureType.setCenterAndRadius(data.coordinate, 100);
        let style = data.style;
        let feature = mission.setFeature(featureType, style, this.layer);
        feature.setId(`mission-${data.index}`);
    },
}

const droneSocket = {
    stompClient: null,
    socket: null,
    connect: function() {
        let self = this;
        this.socket = new SockJS("/ws");
        this.stompClient = Stomp.over(this.socket);
        this.stompClient.connect({}, function(frame) {
            console.log('Connected: ' + frame);
            self.stompClient.subscribe('/topic/startmission', function (drone) {
                console.log(drone);
                setDrawDrone.reloadFeature(drone);
            });
            self.stompClient.subscribe('/topic/log', function (drone) {
                setDrawDrone.reloadFeature(drone);
            });
            self.stompClient.subscribe('/topic/pause',function (drone){
                console.log(drone);
            });
            self.stompClient.subscribe('/topic/play',function (drone){
                console.log(drone);
            })
        });
    },
    disConnect: function() {
        this.stompClient.disconnect();
    },
    startMission: function(missionId) {
        this.stompClient.send("/app/startmission", {}, JSON.stringify({'id': missionId}));
    },
    pauseMission: function() {
        this.stompClient.send("/app/pause");
    },
    playMission: function() {
        this.stompClient.send("/app/play");
    },
    returnMission: function() {
        this.stompClient.send("/app/return", {}, JSON.stringify({}));
    },
    gotoMission: function(obj) {
        this.stompClient.send("/app/waypoint", {}, JSON.stringify(obj));
    },
    init: function() {
        this.connect();
    }
};

