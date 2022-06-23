const dashboardGimje = {
    init: () => {
        dashboardGimje.create();

        dashboardGimje.interval(() => {
            dashboardGimje.getDroneCabinetStatus({}, (result) => {
                dashboardGimje.createDroneCabinetStatus(result);
            });

            dashboardGimje.getCabinetRank({}, (result) => {
                dashboardGimje.createCabinetRank(result);
            });
        }, 60000); //60000

        //대시보드 맵
        // $.each($('.dashboard_section .map_wrap'), (i,v) => {
        //    const id = $(v).attr('id');
        dashboardGimje.createMap('dashDroneStationMap');
        //});
    }
    , interval: (callback, interval) => {
        return this.setTimeoutId = setTimeout(function() {
            callback();
            dashboardGimje.interval(callback, interval);
        }, interval);
    }
    , create: () => {
        dashboardGimje.getDroneCabinetStatus({}, (result) => {
            dashboardGimje.createDroneCabinetStatus(result);
        });

        dashboardGimje.getCabinetRank({}, (result) => {
            dashboardGimje.createCabinetRank(result);
        });
    }
    , getDroneCabinetStatus: (param, pCallback) => {
        $.ajax({
            url : "/dashboard/getDroneCabinetStatus"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
            , async : false
        }).done((result) => {
            pCallback(result.data);
        }).fail((result)=> {

        });
    }
    , createDroneCabinetStatus: (pObj) => {
        $.each(pObj, (idx, obj) => {
            $('#dronStatus1 p.value').html(stringFunc.toString(obj.flyCnt)+'<span>개</span>');
            $('#dronStatus2 p.value').html(stringFunc.toString(obj.fireCnt)+'<span>건</span>');
            $('#cabinetStatus1 ul.count').html(
                '<li>'+stringFunc.toString(obj.oamDangerCnt)+'<span>누설전류</span></li>'
                +'<li>'+stringFunc.toString(obj.amnDangerCnt)+'<span>과전류</span></li>');
            $('#cabinetStatus2 ul.count').html(
                '<li>'+stringFunc.toString(obj.oamWarnCnt)+'<span>누설전류</span></li>'
                +'<li>'+stringFunc.toString(obj.amnWarnCnt)+'<span>과전류</span></li>');
        });
    }
    , getCabinetRank:(param, pCallback) => {
        $.ajax({
            url : "/dashboard/getCabinetRank"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
            , async : false
        }).done((result) => {
            pCallback(result.data);
        }).fail((result)=> {

        });
    }
    , createCabinetRank:(pObj) => {
        const $target = $("#cabinetRank");
        if(pObj.length===0) $target.html("<div class=pole_list'><p>정보없음</p></div>");
        else $target.html("");

        let targetHtml = "";
        $.each(pObj, (idx, obj) => {
            targetHtml +=
                '<div class="pole_list">' +
                '<p>'+(idx+1)+'. '+obj.facilityName+'</p>' +
                '<ul class="list_info">' +
                '<li><span>전력량</span>'+obj.watTot+'<span>kWh</span></li>' +
                '<li><span>누설전류</span>'+obj.amnStatus+'</li>' +
                '<li><span>과전류</span>'+obj.oamStatus+'</li>' +
                '<li><span>허용대비</span>'+obj.amUse+'<span>%</span></li>' +
                '</ul>' +
                '</div>';
        });
        $target.append(targetHtml);
    }
    , createMap : (type) => {
        let map = new mapCreater(type, 0, $("#sigunCode").val());
        //실제 드론 분전함 data
        let paramObj;
        $.ajax({
            url : "/config/mntrPageTypeData/drone"
            , type : "GET"
            , async : false
        }).done((result) => {
            paramObj = result['station'];
        });
        station.getListGeoJson( paramObj ,result => {
            let featureSource = new ol.source.Vector ({
                features: new ol.format.GeoJSON().readFeatures(result, {
                    dataProjection: "EPSG:4326"
                    , featureProjection: "EPSG:5181"
                })
            });

            let layer = new ol.layer.Vector({
                title : 'droneStationLayer',
                source: featureSource,
                style:  new ol.style.Style({
                    image: new ol.style.Circle({
                        radius:10,
                        stroke: new ol.style.Stroke({
                            color: 'white',
                            width: 2,
                        }),
                        fill: new ol.style.Fill({
                            color: '#8B62FF'
                        })
                    })
                })
            });

            map.addLayer(layer);
            const featureLen = featureSource.getFeatures().length;
            if(featureLen > 0) {
                const fitExtent = featureSource.getExtent();

                map.map.getView().fit(fitExtent, map.map.getSize());
            }
            map.map.getView().setZoom(map.map.getView().getZoom() - 0.5);

            window[type] = map;
        });
    }
}