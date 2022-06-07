let sliderId1, sliderId2, sliderId3, sliderId4;
const dashboard = {
    init: () => {

        dashboard.create();

        let map = new mapCreater('map',0, $("#sigunCode").val());//, siGunCode);

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

            dashboard.getListStation({}, (result) => {
                dashboard.reloadCharStation(result);
            });

            dashboard.getListAirPollution({}, (result) => {
                dashboard.createChartAirPollution(result);
            });

        }, 60000); //60000
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
        $statusCnt.html("");
        let htmlStatusCnt = "";
        $.each(pObj, (idx, obj) => {
            const tempTotalCnt = obj.totalCnt>0?' '+obj.totalCnt:'';
            htmlStatusCnt +=
                '<div>' +
                '<p class="title">'+obj.name+'<span>'+obj.subName+'</span></p>' +
                '<p class="value">'+obj.value+'<span>'+obj.unit+tempTotalCnt+'</span></p>' +
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
                slideWidth: '456.5' //css에도 max지정 해둔 상태
            });
        }
        return sliderId;
    }


    //유동인구 차트
    , getListFloatingPopulation : (param, pCallback) =>  {
        let result = JSON.parse('{' +
            '"xData":[{"name":"스마트 가로등 1","data":[4, 5, 3, 4, 3, 6, 1, 0, 6, 8, 11, 10]}' +
            ',{"name":"스마트 가로등 2","data":[5, 1, 2, 2, 13, 8, 2, 3, 6, 1, 3, 5]}' +
            ',{"name":"스마트 가로등 3","data":[8, 5, 7, 9, 7, 3, 6, 4, 8, 5, 4, 4]}' +
            ',{"name":"스마트 가로등 4","data":[1, 7, 4, 9, 5, 8, 2, 7, 1, 6, 5, 7]}' +
            ']}');
        pCallback(result);
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
            theme: {
                mode: "dark",
                palette : 'palette3'
            },
            dataLabels: {
                enabled: false
            },
            stroke: {
                curve: 'straight'
            },
            legend: {
                tooltipHoverFormatter: function(val, opts) {
                    return val + ' : <strong>' + opts.w.globals.series[opts.seriesIndex][opts.dataPointIndex] + '</strong> 명'
                }
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
        options.series = pObj.xData;

        //let chart = new ApexCharts(document.querySelector("#"+key), options);
        let chart = new ApexCharts(document.querySelector("#floatingPopulationChart"), options);
        chart.render();
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
            }
        };

        options.series = [];
        options.series = pObj;

        let chart = new ApexCharts(document.querySelector("#stationChart"), options);
        chart.render();
    }
    , reloadCharStation : (pObj) => {
        const newOptions = {};
        newOptions.series = [];
        newOptions.series = pObj;

        ApexCharts.exec(document.querySelector("#stationChart"), 'updateOptions', newOptions);
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
        }
        $.each(pObj, (key, val) => {
            if(key.indexOf("Value") < 0) { //일시
                $("#"+key).text(val);
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
        }

        $.each(pObj, (key, val) => {
            const newOptions = {};
            if(key.indexOf("Value") < 0) {
                $("#"+key).text(val);
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
}
