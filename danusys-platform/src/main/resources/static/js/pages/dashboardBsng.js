let sliderId1, sliderId2, sliderId3, sliderId4;
const dashboard = {
    init: () => {

        dashboard.create();

        /*dashboard.interval(() => {
            dashboard.getListStatusCnt1({}, (result) => {
                sliderId1 = dashboard.createChartStatusCnt(result,'statusCnt1', sliderId1);
            });
            dashboard.getListStatusCnt2({}, (result) => {
                sliderId2 = dashboard.createChartStatusCnt(result,'statusCnt2', sliderId2);
            });
            dashboard.getListStatusCnt3({}, (result) => {
                sliderId3 = dashboard.createChartStatusCnt(result,'statusCnt3', sliderId3);
            });
            dashboard.getListStatusCnt4({}, (result) => {
                sliderId4 = dashboard.createChartStatusCnt(result,'statusCnt4', sliderId4);
            });

            dashboard.getListFloatingPopulation({}, (result) => {
                dashboard.reloadCharFloatingPopulation(result);
            });

            dashboard.getListAirPollution({}, (result) => {
                dashboard.createChartAirPollution(result);
            });

        }, 60000); //60000*/

        let mapData = dashboard.mapData();
        //대시보드 맵
        $.each($('.dashboard_section .map_wrap'), (i,v) => {
            const id = $(v).attr('id');
            dashboard.createMap(id, mapData);
        });
    }
    , interval: (callback, interval) => {
        return this.setTimeoutId = setTimeout(function() {
            callback();
            dashboard.interval(callback, interval);
        }, interval);
    }
    , create: () => {
        dashboard.getListStatusCnt1({}, (result) => {
            sliderId1 = dashboard.createChartStatusCnt(result,'statusCnt1',sliderId1);
        });
        dashboard.getListStatusCnt2({}, (result) => {
            sliderId2 = dashboard.createChartStatusCnt(result,'statusCnt2',sliderId2)
        });
        dashboard.getListStatusCnt3({}, (result) => {
            sliderId3 = dashboard.createChartStatusCnt(result,'statusCnt3',sliderId3)
        });
        dashboard.getListStatusCnt4({}, (result) => {
            sliderId4 = dashboard.createChartStatusCnt(result,'statusCnt4',sliderId4)
        });

        dashboard.getListFloatingPopulation({}, (result) => {
            dashboard.createCharFloatingPopulation(result);
        });

        dashboard.getListAirPollution({}, (result) => {
            dashboard.createChartAirPollution(result);
        });
    }


    //상단 카운트1
    , getListStatusCnt1 : (param, pCallback) =>  {
        $.ajax({
            url : "/dashboard/getStatusCnt1"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
            , async : false
        }).done((result) => {
            pCallback(result.data);
        }).fail((result)=> {
            //console.log("대기오염 정보수신에 실패했습니다." + result);
        });
    }

    //상단 카운트2
    , getListStatusCnt2 : (param, pCallback) =>  {
        $.ajax({
            url : "/dashboard/getStatusCnt2"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
            , async : false
        }).done((result) => {
            pCallback(result.data);
        }).fail((result)=> {
            //console.log("대기오염 정보수신에 실패했습니다." + result);
        });
    }

    //상단 카운트3
    , getListStatusCnt3 : (param, pCallback) =>  {
        $.ajax({
            url : "/dashboard/getStatusCnt3"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
            , async : false
        }).done((result) => {
            pCallback(result.data);
        }).fail((result)=> {
            //console.log("대기오염 정보수신에 실패했습니다." + result);
        });
    }

    //상단 카운트4
    , getListStatusCnt4 : (param, pCallback) =>  {
        $.ajax({
            url : "/dashboard/getStatusCnt4"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
            , async : false
        }).done((result) => {
            pCallback(result.data);
        }).fail((result)=> {
            //console.log("대기오염 정보수신에 실패했습니다." + result);
        });
    }

    //상단 생성
    , createChartStatusCnt : (pObj, target, sliderId) => {
        const $statusCnt = $("#"+target);
        if(pObj.length===0) $statusCnt.html("<div><p class='title'>정보없음</p></div>");
        else $statusCnt.html("");

        let htmlStatusCnt = "";
        $.each(pObj, (idx, obj) => {
            const tempTotalCnt = obj.totalCnt>0?' '+obj.totalCnt:'';
            const unitHtml = obj.unit==null||obj.unit===""?'':'<span class="unit">'+obj.unit+'</span>';
            const totCntHtml = tempTotalCnt==null||tempTotalCnt===""?'':'<span>'+tempTotalCnt+'</span>';
            htmlStatusCnt +=
                '<div>' +
                '<p class="title">'+stringFunc.changeXSSOutputValue(obj.name)+'<span>'+obj.subName+'</span></p>' +
                '<p class="value">'+stringFunc.toString(obj.value) + unitHtml + totCntHtml +'</p>' +
                '</div>';
        });
        $statusCnt.append(htmlStatusCnt);

        if(!!sliderId && sliderId[0].id===target) {
            sliderId.stopAuto();
            sliderId.destroySlider();
        }
        if(pObj.length > 1) {
            sliderId = $statusCnt.bxSlider({
                auto: true,
                autoControls: true,
                stopAutoOnClick: true,
                pager: true,
                pause: 5000,
                slideWidth: '456.5' //css에도 max지정 해둔 상태
            });
        }
        return sliderId;
    }


    //유동인구 차트
    , getListFloatingPopulation : (param, pCallback) =>  {
        $.ajax({
            url : "/dashboard/getFloatingPopulation"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
            , async : false
        }).done((result) => {
            pCallback(result.data);
        }).fail((result)=> {
            //console.log("대기오염 정보수신에 실패했습니다." + result);
        });
        /*let result = JSON.parse('{' +
            '"xData":[{"name":"스마트 가로등 1","data":[4, 5, 3, 4, 3, 6, 1, 0, 6, 8, 11, 10]}' +
            ',{"name":"스마트 가로등 2","data":[5, 1, 2, 2, 13, 8, 2, 3, 6, 1, 3, 5]}' +
            ',{"name":"스마트 가로등 3","data":[8, 5, 7, 9, 7, 3, 6, 4, 8, 5, 4, 4]}' +
            ',{"name":"스마트 가로등 4","data":[1, 7, 4, 9, 5, 8, 2, 7, 1, 6, 5, 7]}' +
            ']}');
        pCallback(result);*/
    }
    , createCharFloatingPopulation : (pObj) => {
        const options = {
            series: [],
            xaxis: {
                categories: ["12시간 전", "11시간 전", "10시간 전", "9시간 전", "8시간 전", "7시간 전", "6시간 전", "5시간 전", "4시간 전", "3시간 전", "2시간 전", "1시간 전"],
                labels: {
                    formatter: function (value) {
                        return value + " (상위 10개소)";
                    }
                }
            },
            chart: {
                height: '80%',
                type: 'line',
                zoom: {
                    enabled: false
                },
                toolbar: {
                    show: false
                },
                background: "#1a1b1d"
            },
            colors: ['#00E396','#FEB019','#FF4560','#775DD0',
                '#3F51B5','#03A9F4','#4CAF50','#F9CE1D','#FF9800',
                '#33B2DF','#546E7A','#D4526E','#13D8AA','#A5978B',
                '#4ECDC4','#C7F464','#81D4FA','#546E7A','#FD6A6A',
                '#2B908F','#F9A3A4','#90EE7E','#FA4443','#69D2E7',
                '#449DD1','#F86624','#EA3546','#662E9B','#C5D86D',
                '#D7263D','#1B998B','#2E294E','#F46036','#E2C044',
                '#662E9B','#F86624','#F9C80E','#EA3546','#43BCCD',
                '#5C4742','#A5978B','#8D5B4C','#5A2A27','#C4BBAF',
                '#A300D6','#7D02EB','#5653FE','#2983FF','#00B1F2'],
            theme: {
                mode: "dark"
                //,palette : 'palette3'
            },
            dataLabels: {
                enabled: false
            },
            stroke: {
                curve: 'straight'
            },
            legend: {
                /*formatter: function(seriesName, opts) {
                    return [stringFunc.getCutByteLength(seriesName, 10)]
                }*/
                //showForZeroSeries: false
                // ,tooltipHoverFormatter: function(val, opts) {
                //     debugger;
                //     return opts.w.globals.seriesNames[opts.seriesIndex] + ' : <strong>' + opts.w.globals.series[opts.seriesIndex][opts.dataPointIndex] + '</strong> 명'
                // }
            },
            markers: {
                size: 0,
                hover: {
                    sizeOffset: 6
                }
            },
            tooltip: {
                fillSeriesColor: false,
                y: {
                    formatter: function (val, s) {
                        let tempValue = [];

                        for(let t=0; t<s.series.length; t++) {
                            let value = s.series[t][s.dataPointIndex];
                            tempValue.push(value);
                        }
                        tempValue = tempValue.sort((a,b) => b-a).slice(0,10);

                        for(let tt=0; tt<tempValue.length; tt++) {
                            if(tempValue[tt] === val) return val + " 명"
                        }
                        //return top5 + " 명"
                    }
                }
            },
            grid: {
                borderColor: '#f1f1f1',
            }
        };

        options.series = [];
        options.series = pObj;
        options.chart.id = 'floatingPopulationChart';

        let chart = new ApexCharts(document.querySelector("#floatingPopulationChart"), options);
        chart.render();
    }
    , reloadCharFloatingPopulation : (pObj) => {
        const newOptions = {};
        newOptions.series = [];
        newOptions.series = pObj;

        ApexCharts.exec("floatingPopulationChart", 'updateOptions', newOptions);
    }
    , reloadCharStation : (pObj) => {
        const newOptions = {};
        newOptions.series = [];
        newOptions.series = pObj;

        ApexCharts.exec("stationChart", 'updateOptions', newOptions);
    }

    //환경 센서
    , getListAirPollution : (param, pCallback) =>  {
        $.ajax({
            url : "/dashboard/getAirPollution"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
            , async : false
        }).done((result) => {
            pCallback(result.data[0]);
            let resultLen = result.data.length-1;
            let idx = 1;
            if(!!this.intervalID) return;
            this.intervalID = dashboard.interval(() => {
                dashboard.reloadChartAirPollution(result.data[idx]);
                if(resultLen === idx) idx = 0;
                else idx++;
            }, 10000);
        }).fail((result)=> {
            console.log("대기오염 정보수신에 실패했습니다." + result);
        });
    }
    , createChartAirPollution : (pObj) => {
        const options = {
            series: [],
            chart: {
                height: '70%',
                type: 'radialBar',
                toolbar: {
                    show: false
                },
                background: "#1a1b1d"
            },
            theme: {
                mode: "dark"
            },
            plotOptions: {
                radialBar: {
                    startAngle: -135,
                    endAngle: 225,
                    hollow: {
                        margin: 0,
                        size: '60%',
                        background: '#202124',
                        image: undefined,
                        imageOffsetX: 0,
                        imageOffsetY: 0,
                        position: 'front',
                        dropShadow: {
                            enabled: true,
                            top: 3,
                            left: 0,
                            blur: 4,
                            opacity: 0.24
                        }
                    },
                    track: {
                        background: '#fff',
                        strokeWidth: '67%',
                        margin: 0, // margin is in pixels
                        dropShadow: {
                            enabled: true,
                            top: -3,
                            left: 0,
                            blur: 4,
                            opacity: 0.35
                        }
                    },
                    dataLabels: {
                        show: true,
                        name: {
                            offsetY: -10,
                            show: true,
                            color: '#fff',
                            fontSize: '120%'
                        },
                        value: {
                            formatter: function(val) {
                                return val + "점";
                            },
                            offsetY: 10,
                            color: '#fff',
                            fontSize: '80%',
                            show: true,
                        }
                    }
                }
            },
            fill: {
                colors : []
            },
            stroke: {
                lineCap: 'round'
            }
        };

        const typeObj = {
            "good" : {"title" : "좋음", "color": "#2DCCFF"}
            , "normal" : {"title" : "보통", "color": "#56F000"}
            , "bad" : {"title" : "나쁨", "color": "#FFB302"}
            , "tooBad" : {"title" : "매우나쁨", "color": "#FF3838"}
            , "danger": {"title" : "매우나쁨(위험)", "color": "#8B62FF"}
            , "noData": {"title" : "정보없음", "color": "#FFFFFF"}
        }
        $.each(pObj, (key, val) => {
            if(key.indexOf("Value") < 0) { //일시
                $("#"+key).text(stringFunc.changeXSSOutputValue(val));
            } else {
                options.series = [];
                options.labels = [];
                options.fill.colors = [];
                options.series.push(val.score);
                options.labels.push(typeObj[val.type].title);
                options.fill.colors.push(typeObj[val.type].color);
                options.chart.id = key;

                let chart = new ApexCharts(document.querySelector("#"+key), options);
                chart.render();
            }
        })
    }
    , reloadChartAirPollution : (pObj) => {
        const typeObj = {
            "good" : {"title" : "좋음", "color": "#2DCCFF"}
            , "normal" : {"title" : "보통", "color": "#56F000"}
            , "bad" : {"title" : "나쁨", "color": "#FFB302"}
            , "tooBad" : {"title" : "매우나쁨", "color": "#FF3838"}
            , "danger": {"title" : "매우나쁨(위험)", "color": "#8B62FF"}
            , "noData": {"title" : "정보없음", "color": "#FFFFFF"}
        }

        $.each(pObj, (key, val) => {
            const newOptions = {};
            if(key.indexOf("Value") < 0) {
                $("#"+key).text(stringFunc.changeXSSOutputValue(val));
            } else {
                newOptions.series = [];
                newOptions.labels = [];
                newOptions.fill = {colors : []};
                newOptions.series.push(val.score);
                newOptions.labels.push(typeObj[val.type].title);
                newOptions.fill.colors.push(typeObj[val.type].color);

                ApexCharts.exec(key, 'updateOptions', newOptions);
                //ApexCharts.getChartByID(key).updateOptions(newOptions);
            }
        })
    }
    /**
     * 대시보드 맵 생성
     * param type : dashFacilityStationMap(위치지도), dashFacilityHeatMap(분포도)
     * */
    , createMap : (type, data) => {
        const map = new mapCreater(type, 0, $("#sigunCode").val());

        let dataJson = JSON.parse(data);
        if(type=="dashFacilityHeatMap"){
            let dataFeatures = dataJson.features.filter(f => {
                if(f.properties.fcltPopCount){
                    return true;
                }
                return false;
            });
            dataJson.features = dataFeatures;
        }

        const featureSource = new ol.source.Vector ({
            features: new ol.format.GeoJSON().readFeatures(JSON.stringify(dataJson), {
                dataProjection: "EPSG:4326"
                , featureProjection: "EPSG:5181"
            })
        });

        let layer;

        if(type == "dashFacilityHeatMap"){
            layer = new ol.layer.Heatmap({
                source: featureSource,
                blur: 15,
                radius: 8,
                weight: function (feature) {
                    let wei = 0.4; //0;
                    // const popCnt = Number(feature.getProperties().fcltPopCount);
                    // const sumCnt = Number(feature.getProperties().fcltPopCountSum);
                    // if(!isNaN(popCnt)){
                    //     wei = (popCnt/sumCnt); //* 100;
                    // }
                    return wei;
                },
            });
        } else if(type=="dashFacilityStationMap") {
            layer = new ol.layer.Vector({
                title : 'positionLayer',
                source: featureSource,
                style:  new ol.style.Style({
                    image: new ol.style.Circle({
                        radius:10,
                        stroke: new ol.style.Stroke({
                            color: 'white',
                            width: 2,
                        }),
                        fill: new ol.style.Fill({
                            color: '#CC6565'
                        })
                    })
                })
            });
        }
        map.addLayer(layer);
        const featureLen = featureSource.getFeatures().length;
        if(featureLen > 0) {
            const fitExtent = featureSource.getExtent();

            map.map.getView().fit(fitExtent, map.map.getSize());
        }
        map.map.getView().setZoom(map.map.getView().getZoom() - 0.5);
        window[type] = map;
    }
    , mapData : () => {
        const municipality = $("#dashFacilityStationMap").parent().data('municipality');
        let pageType = "";
        if(municipality === 47210000) {
            pageType = "smartBusStop"
        } else {
            pageType = "smartPole"
        }
        let param = mntr.getInitParam(pageType)['station'];
        param['option'] = 'heatMap';

        let data;
        station.getListGeoJson( param ,result => {
            data = result;
        });
        return data;
    }
}
