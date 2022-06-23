let sliderId1, sliderId2, sliderId3, sliderId4;
const dashboard = {
    init: () => {

        dashboard.create();

        dashboard.interval(() => {
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

            dashboard.getListStation({}, (result) => {
                dashboard.reloadCharStation(result);
            });

            dashboard.getListAirPollution({}, (result) => {
                dashboard.createChartAirPollution(result);
            });

        }, 60000); //60000

        //대시보드 맵
        $.each($('.dashboard_section .map_wrap'), (i,v) => {
            const id = $(v).attr('id');
            dashboard.createMap(id);
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

        dashboard.getListStation({}, (result) => {
            dashboard.createCharStation(result);
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
                categories: ["12시간 전", "11시간 전", "10시간 전", "9시간 전", "8시간 전", "7시간 전", "6시간 전", "5시간 전", "4시간 전", "3시간 전", "2시간 전", "1시간 전"]
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
                    formatter: function (val) {
                        return val + " 명"
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

    //장소별 설치 현황 차트
    , getListStation : (param, pCallback) =>  {
        $.ajax({
            url : "/dashboard/getStation"
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
    , createCharStation : (pObj) => {
        const options = {
            series: [],
            chart: {
                type: 'bar',
                height: '80%',
                toolbar: {
                    show: false
                },
                background: "#1a1b1d"
            },
            theme: {
                mode: "dark"
            },
            plotOptions: {
                bar: {
                    horizontal: false,
                    columnWidth: '55%',
                    endingShape: 'rounded'
                },
            },
            dataLabels: {
                enabled: false
            },
            stroke: {
                show: true,
                width: 2,
                colors: ['transparent']
            },
            fill: {
                opacity: 1
            },
            tooltip: {
                y: {
                    formatter: function (val) {
                        return val + " 개소"
                    }
                }
            },
            legend: {
                markers: {
                    radius: 12
                }
            }
        };

        options.series = [];
        options.series = pObj;
        options.chart.id = 'stationChart';

        let chart = new ApexCharts(document.querySelector("#stationChart"), options);
        chart.render();
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
                height: '100%',
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
     * param type : viewMap(위치지도), heatMap(분포도)
     * */
    , createMap : (type) => {

        let map = new mapCreater(type, 0);//, $("#sigunCode").val());

        //TODO 유동인구 log 실 data
        let result = {"type":"FeatureCollection","features":[{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event1","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-23 13:47:20.115872","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":58,"eventStartDt":"2022-03-23 13:47:20.115872","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event2","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-23 13:46:45.321347","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":56,"eventStartDt":"2022-03-23 13:46:45.321347","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event3","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-23 13:42:35.75623","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":50,"eventStartDt":"2022-03-23 13:42:35.75623","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event4","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-23 12:05:09.441115","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":37,"eventStartDt":"2022-03-23 12:05:09.441115","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event5","geometry_name":"geom","type":"Feature","properties":{"insertDt":"2022-03-23 09:07:53.241537","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":29,"eventStartDt":"2022-03-23 09:07:53.241537","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event6","geometry_name":"geom","type":"Feature","properties":{"insertDt":"2022-03-22 16:49:08.871186","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":27,"eventStartDt":"2022-03-22 16:49:08.871186","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event7","geometry_name":"geom","type":"Feature","properties":{"insertDt":"2022-03-22 16:34:51.446981","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":25,"eventStartDt":"2022-03-22 16:34:51.446981","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event8","geometry_name":"geom","type":"Feature","properties":{"insertDt":"2022-03-22 15:51:32.84241","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":13,"eventStartDt":"2022-03-22 15:51:32.84241","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event9","geometry_name":"geom","type":"Feature","properties":{"insertDt":"2022-03-22 15:44:55.096856","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":11,"eventStartDt":"2022-03-22 15:44:55.096856","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event10","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-29 15:43:35.759816","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":84,"eventStartDt":"2022-03-29 15:43:35.759816","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event11","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-29 15:09:17.897052","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":64,"eventStartDt":"2022-03-29 15:09:17.897052","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event12","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-29 15:22:49.434325","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":70,"eventStartDt":"2022-03-29 15:22:49.434325","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event13","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-29 15:36:46.02353","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":76,"eventStartDt":"2022-03-29 15:36:46.02353","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event14","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-29 15:10:09.635201","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":66,"eventStartDt":"2022-03-29 15:10:09.635201","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event15","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-29 15:12:09.051309","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":68,"eventStartDt":"2022-03-29 15:12:09.051309","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event16","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-29 15:28:54.050451","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":72,"eventStartDt":"2022-03-29 15:28:54.050451","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event17","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-29 15:38:30.936353","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":78,"eventStartDt":"2022-03-29 15:38:30.936353","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event18","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-29 15:29:01.046222","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":74,"eventStartDt":"2022-03-29 15:29:01.046222","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event19","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-29 15:38:51.413218","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":80,"eventStartDt":"2022-03-29 15:38:51.413218","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event20","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-29 15:39:39.343616","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":82,"eventStartDt":"2022-03-29 15:39:39.343616","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.70033715752935,37.33738166839028]},"id":"event21","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-04-06 08:47:20.0","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":37.33738166839028,"stationSeq":888,"eventProcStat":"1","administZone":"41390132","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":57,"eventStartDt":"2022-04-06 08:47:20.0","eventKindName":"누설 전류","longitude":126.70033715752935}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event22","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-04-06 09:36:20.0","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":62,"eventStartDt":"2022-04-06 09:36:20.0","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event23","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"누설전류  경고","insertDt":"2022-03-23 13:53:10.838751","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":61,"eventStartDt":"2022-03-23 13:53:10.838751","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event24","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-23 13:40:35.010896","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":48,"eventStartDt":"2022-03-23 13:40:35.010896","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event25","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-23 13:38:51.959306","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":46,"eventStartDt":"2022-03-23 13:38:51.959306","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event26","geometry_name":"geom","type":"Feature","properties":{"insertDt":"2022-03-23 12:08:23.620164","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":44,"eventStartDt":"2022-03-23 12:08:23.620164","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event27","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-23 12:08:08.561399","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":42,"eventStartDt":"2022-03-23 12:08:08.561399","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event28","geometry_name":"geom","type":"Feature","properties":{"insertDt":"2022-03-23 12:05:17.200125","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":40,"eventStartDt":"2022-03-23 12:05:17.200125","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event29","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-23 10:04:41.741758","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":36,"eventStartDt":"2022-03-23 10:04:41.741758","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event30","geometry_name":"geom","type":"Feature","properties":{"insertDt":"2022-03-23 09:30:39.561412","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":32,"eventStartDt":"2022-03-23 09:30:39.561412","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event31","geometry_name":"geom","type":"Feature","properties":{"insertDt":"2022-03-22 16:49:08.871186","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":28,"eventStartDt":"2022-03-22 16:49:08.871186","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event32","geometry_name":"geom","type":"Feature","properties":{"insertDt":"2022-03-22 16:34:51.446981","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":26,"eventStartDt":"2022-03-22 16:34:51.446981","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event33","geometry_name":"geom","type":"Feature","properties":{"insertDt":"2022-03-22 15:51:32.84241","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":14,"eventStartDt":"2022-03-22 15:51:32.84241","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event34","geometry_name":"geom","type":"Feature","properties":{"insertDt":"2022-03-22 15:44:55.096856","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":12,"eventStartDt":"2022-03-22 15:44:55.096856","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event35","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-25 09:36:20.185617","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":63,"eventStartDt":"2022-03-25 09:36:20.185617","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event36","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-29 15:22:49.434325","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":71,"eventStartDt":"2022-03-29 15:22:49.434325","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event37","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-29 15:12:09.051309","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":69,"eventStartDt":"2022-03-29 15:12:09.051309","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event38","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-29 15:10:09.635201","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":67,"eventStartDt":"2022-03-29 15:10:09.635201","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event39","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-29 15:09:17.897052","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":65,"eventStartDt":"2022-03-29 15:09:17.897052","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event40","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-29 15:38:30.936353","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":79,"eventStartDt":"2022-03-29 15:38:30.936353","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event41","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-29 15:36:46.02353","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":77,"eventStartDt":"2022-03-29 15:36:46.02353","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event42","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-29 15:29:01.046222","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":75,"eventStartDt":"2022-03-29 15:29:01.046222","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event43","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-29 15:28:54.050451","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":73,"eventStartDt":"2022-03-29 15:28:54.050451","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event44","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-29 15:43:35.759816","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":85,"eventStartDt":"2022-03-29 15:43:35.759816","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event45","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-29 15:39:39.343616","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":83,"eventStartDt":"2022-03-29 15:39:39.343616","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event46","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-29 15:38:51.413218","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":81,"eventStartDt":"2022-03-29 15:38:51.413218","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event47","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-23 13:46:35.000011","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"조치중","latitude":35.80437063,"stationSeq":893,"eventProcStat":"2","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":54,"eventStartDt":"2022-03-23 13:46:35.000011","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event48","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"초과","insertDt":"2022-03-23 12:05:09.441115","administZoneName":"서암동","eventGrade":"20","eventProcStatName":"조치 완료","latitude":35.80437063,"stationSeq":893,"eventProcStat":"3","administZone":"45210107","facilitySeq":2226,"eventKind":"OVER_ERCRT","eventGradeName":"주의","eventSeq":38,"eventStartDt":"2022-03-23 12:05:09.441115","eventKindName":"과전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event49","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"누설전류  경고","insertDt":"2022-03-23 13:49:43.320139","administZoneName":"서암동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2226,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":60,"eventStartDt":"2022-03-23 13:49:43.320139","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event50","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"누설전류  경고","insertDt":"2022-03-23 13:48:45.50031","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":59,"eventStartDt":"2022-03-23 13:48:45.50031","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event51","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-23 13:46:45.321347","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":55,"eventStartDt":"2022-03-23 13:46:45.321347","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event52","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-23 13:46:35.000011","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":53,"eventStartDt":"2022-03-23 13:46:35.000011","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event53","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-23 13:42:59.620712","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":51,"eventStartDt":"2022-03-23 13:42:59.620712","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event54","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-23 13:42:35.75623","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":49,"eventStartDt":"2022-03-23 13:42:35.75623","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event55","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-23 13:38:51.959306","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":45,"eventStartDt":"2022-03-23 13:38:51.959306","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event56","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-23 12:08:23.620164","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":43,"eventStartDt":"2022-03-23 12:08:23.620164","eventKindName":"누설 전류","longitude":126.88041901}},{"geometry":{"type":"Point","coordinates":[126.88041901,35.80437063]},"id":"event57","geometry_name":"geom","type":"Feature","properties":{"eventMessage":"30mA 초과","insertDt":"2022-03-23 12:08:08.561399","administZoneName":"교동","eventGrade":"10","eventProcStatName":"미처리","latitude":35.80437063,"stationSeq":893,"eventProcStat":"1","administZone":"45210107","facilitySeq":2227,"eventKind":"LKGE_ERCRT","eventGradeName":"긴급","eventSeq":41,"eventStartDt":"2022-03-23 12:08:08.561399","eventKindName":"누설 전류","longitude":126.88041901}}]};
        //if(type=""){result = data}

        let featureSource = new ol.source.Vector ({
            features: new ol.format.GeoJSON().readFeatures(result, {
                dataProjection: "EPSG:4326"
                , featureProjection: "EPSG:5181"
            })
        });

        let layer;
        if(type=="dashFacilityHeatMap") { //분포도 레이어
            layer = new ol.layer.Heatmap({
                source: featureSource,
                blur: 15,
                radius: 8,
                weight: function (feature) {
                    return 0.4;
                },
            });
        } else if(type=="dashFacilityStationMap") { //위치 레이어
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

        const fitExtent = featureSource.getExtent();

        map.addLayer(layer);
        map.map.getView().fit(fitExtent, map.map.getSize());
        map.map.getView().setZoom(map.map.getView().getZoom() - 0.5);

        window[type] = map;
    }
}
