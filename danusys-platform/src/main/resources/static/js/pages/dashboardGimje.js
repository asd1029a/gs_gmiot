const dashboardGimje = {
    init: () => {
        dashboardGimje.create();

        dashboardGimje.interval(() => {
            dashboardGimje.getDronCabinetStatus({}, (result) => {
                dashboardGimje.createDronCabinetStatus(result);
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
    , isValidCnt: (value) => {
        return value === '' || value == null || value === 0 || value === undefined ? '-' : value;
    }
    , interval: (callback, interval) => {
        return this.setTimeoutId = setTimeout(function() {
            callback();
            dashboardGimje.interval(callback, interval);
        }, interval);
    }
    , create: () => {
        dashboardGimje.getDronCabinetStatus({}, (result) => {
            dashboardGimje.createDronCabinetStatus(result);
        });

        dashboardGimje.getCabinetRank({}, (result) => {
            dashboardGimje.createCabinetRank(result);
        });
    }
    , getDronCabinetStatus: (param, pCallback) => {
        $.ajax({
            url : "/dashboard/getDronCabinetStatus"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
            , async : false
        }).done((result) => {
            pCallback(result.data);
        }).fail((result)=> {

        });
    }
    , createDronCabinetStatus: (pObj) => {
        $.each(pObj, (idx, obj) => {
            $('#dronStatus1 p.value').html(dashboardGimje.isValidCnt(obj.flyCnt)+'<span>개</span>');
            $('#dronStatus2 p.value').html(dashboardGimje.isValidCnt(obj.fireCnt)+'<span>건</span>');
            $('#cabinetStatus1 ul.count').html(
                '<li>'+dashboardGimje.isValidCnt(obj.oamDangerCnt)+'<span>누설전류</span></li>'
                +'<li>'+dashboardGimje.isValidCnt(obj.amnDangerCnt)+'<span>과전류</span></li>');
            $('#cabinetStatus2 ul.count').html(
                '<li>'+dashboardGimje.isValidCnt(obj.oamWarnCnt)+'<span>누설전류</span></li>'
                +'<li>'+dashboardGimje.isValidCnt(obj.amnWarnCnt)+'<span>과전류</span></li>');
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
        $target.html("");
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

            const fitExtent = featureSource.getExtent();

            map.addLayer(layer);
            map.map.getView().fit(fitExtent, map.map.getSize());
            map.map.getView().setZoom(map.map.getView().getZoom() - 0.5);

            window[type] = map;
        });
    }
}