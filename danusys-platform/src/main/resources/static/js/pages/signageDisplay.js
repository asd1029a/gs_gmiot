/*
* 사이니지 디스플레이 관련 JS
*/

const signageDisplay = {
    init : (pObj) => {
        const templateContent = JSON.parse(pObj.data.templateContent);
        const options = pObj.options;
        signageDisplay.create(templateContent, options);

        setInterval(function () {
            if(new Date().getMinutes() === 0) {
                signageDisplay.getListAirPollution(
                    {}
                    , (result) => {
                        signageDisplay.reloadAirPollutionChart(result);
                    }
                );
            }
        }, 60000); //3600000
    }
    , create : (pObj, options) => {
        const $wrap = $(".wrap_signage");
        const rtspUrl = options.rtspUrl === null || options.rtspUrl === "" ? "" : options.rtspUrl
        const latitude = options.latitude === null ? "" : options.latitude
        const longitude = options.longitude === null ? "" : options.longitude

        $.each(pObj, (idx, obj1) => {
            let signageEle = "";

            if(obj1.kind === "airPollution") {
                signageEle = signageDisplay.createAirPollutionEle();
            } else if(obj1.kind === "stationList") {
                signageEle = signageDisplay.createRtsp(obj1);
            } else if(obj1.kind === "imageFile") {
                signageEle = signageDisplay.createImageEle(obj1);
            } else if(obj1.kind === "videoFile") {
                signageEle = signageDisplay.createVideoEle(obj1);
            }
            $wrap.append(signageEle);

            if(obj1.kind === "imageFile") {
                $("#imageFile").prop("src", "/signageDisplay/getImage?imageFile=" + obj1.value);
            } else if(obj1.kind === "videoFile") {
                $("#videoFile").prop("src", "/signageDisplay/getVideo?videoFile=" + obj1.value);
            } else if(obj1.kind === "airPollution") {
                let transLonLat = transProj.lonLatToTM(longitude, latitude);

                signageDisplay.getListAirPollution(
                    {"x" : transLonLat.x, "y" : transLonLat.y}
                    , (result) => {
                        signageDisplay.createAirPollutionChart(result);
                    });

            } else if(obj1.kind === "stationList") {
                const videoObj = {
                    turnUrl : "172.20.14.49:3478?transport=tcp",
                    credential : "turnadm123",
                    username : "turnadm",
                    mediaServerWsUrl : "ws://172.20.14.49:8888/kurento",
                    rtspUrl : rtspUrl
                };
                video.directVideoStart(videoObj, "rtspVideo")
            }
        });
    }
    , createRtsp : () => {
        return '<div class="wrap_rtsp">'
            + '<video id="rtspVideo" width="100%" height="100%" autoplay="autoplay" muted="muted">'
            + '</div>';
    }
    , createAirPollutionEle : () => {
        return '<div class="wrap_atmosphere">'
            + '<div class="area_title">'
            + '<div>'
            + ' <h1>공기질</h1>'
            + ' <ul class="comment">'
            + ' <li id="measuringStationName">측정소 : </li>'
            + ' <li id="dataTime">측정일시 : </li>'
            + ' <li>데이터 출처 : 한국환경공단 Airkorea</li>'
            + ' </ul>'
            + ' </div>'
            + ' <ul class="stepper">'
            + ' <li class="remove">'
            + ' <span class="bar"><em>0</em></span>'
            + ' </li>'
            + ' <li>'
            + ' <span class="bar"><em>20</em></span>'
            + ' <span class="text">매우나쁨(위험)</span>'
            + ' </li>'
            + ' <li>'
            + ' <span class="bar"><em>40</em></span>'
            + ' <span class="text">매우나쁨</span>'
            + ' </li>'
            + ' <li>'
            + ' <span class="bar"><em>60</em></span>'
            + ' <span class="text">나쁨</span>'
            + ' </li>'
            + ' <li>'
            + ' <span class="bar"><em>80</em></span>'
            + ' <span class="text">보통</span>'
            + ' </li>'
            + ' <li>'
            + ' <span class="bar"><em>100</em></span>'
            + ' <span class="text">좋음</span>'
            + ' </li>'
            + ' </ul>'
            + ' </div>'
            + ' <div class="area_atmosphere">'
            + ' <section class="box_lt">'
            + ' <div class="box_value">'
            + ' <h6>통합대기환경</h6>'
            + ' <div class="area_graph" id="khaiValue"></div>'
            + ' </div>'
            + ' </section>'
            + ' <section class="box_rt">'
            + ' <div class="box_value">'
            + ' <h6>미세먼지 PM<sub>10</sub></h6>'
            + ' <div class="area_graph" id="pm10Value24"></div>'
            + ' </div>'
            + ' <div class="box_value">'
            + ' <h6>초미세먼지 PM<sub>2.5</sub></h6>'
            + ' <div class="area_graph" id="pm25Value24"></div>'
            + ' </div>'
            + ' <div class="box_value">'
            + ' <h6>오존 O<sub>3</sub></h6>'
            + ' <div class="area_graph" id="o3Value"></div>'
            + ' </div>'
            + ' <div class="box_value">'
            + ' <h6>일산화탄소 CO</h6>'
            + ' <div class="area_graph" id="coValue"></div>'
            + ' </div>'
            + ' <div class="box_value">'
            + ' <h6>이산화질소 NO<sub>2</sub></h6>'
            + ' <div class="area_graph" id="no2Value"></div>'
            + ' </div>'
            + ' <div class="box_value">'
            + ' <h6>아황산가스 SO<sub>2</sub></h6>'
            + ' <div class="area_graph" id="so2Value"></div>'
            + ' </div>'
            + ' </section>'
            + ' </div>'
            + '</div>';
    }
    , createImageEle : () => {
        return '<div class="wrap_image">'
            + '<img width="100%" height="100%" id="imageFile" alt="image">'
            + '</div>';
    }
    , createVideoEle : () => {
        return '<div class="wrap_video">'
            + '<video id="videoFile" width="100%" height="100%" autoplay="autoplay" loop="loop" muted="muted">'
            + '브라우저가 영상을 지원하지 않습니다.'
            + '</div>';
    }
    , createAirPollutionChart : (pObj) => {
        const options = {
            series: [],
            chart: {
                height: 0,
                type: 'radialBar',
                toolbar: {
                    show: false
                }
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
                            fontSize: '20px'
                        },
                        value: {
                            formatter: function(val) {
                                return val + "점";
                            },
                            color: '#fff',
                            fontSize: '22px',
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
            },
            labels: [],
        };

        const typeObj = {
            "good" : {"title" : "좋음", "color": "#2DCCFF"}
            , "normal" : {"title" : "보통", "color": "#56F000"}
            , "bad" : {"title" : "나쁨", "color": "#FFB302"}
            , "tooBad" : {"title" : "매우나쁨", "color": "#FF3838"}
            , "danger": {"title" : "매우나쁨(위험)", "color": "#8B62FF"}
        }
        $.each(pObj, (key, val) => {
            if(key.indexOf("Value") < 0) {
                $("#"+key).text($("#"+key).text() + val);
            } else {
                options.series = [];
                options.labels = [];
                options.fill.colors = [];
                options.series.push(val.score);
                options.labels.push(typeObj[val.type].title);
                options.fill.colors.push(typeObj[val.type].color);
                if(key === "khaiValue") {
                    options.chart.height = 400;
                    options.plotOptions.radialBar.dataLabels.name.fontSize = "40px";
                    options.plotOptions.radialBar.dataLabels.value.fontSize = "42px";
                } else {
                    options.chart.height = 200;
                    options.plotOptions.radialBar.dataLabels.name.fontSize = "20px";
                    options.plotOptions.radialBar.dataLabels.value.fontSize = "22px";
                }
                let chart = new ApexCharts(document.querySelector("#"+key), options);
                chart.render();
            }
        })
    }
    , reloadAirPollutionChart : (pObj) => {
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
                if(key === "dataTime") {
                    $("#"+key).text("측정일시 : " + val);
                }
            } else {
                newOptions.series = [];
                newOptions.labels = [];
                newOptions.fill = {colors : []};
                newOptions.series.push(val.score);
                newOptions.labels.push(typeObj[val.type].title);
                newOptions.fill.colors.push(typeObj[val.type].color);
                ApexCharts.exec(document.querySelector("#"+key), 'updateOptions', newOptions);
            }
        })
    }
    , getListAirPollution : (param, pCallback) => {
        $.ajax({
            url : "/signageDisplay/getListAirPollution"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
            , async : false
        }).done((result) => {
            pCallback(result);
        }).fail((result)=> {
            console.log("대기오염 정보수신에 실패했습니다." + result);
        });
    }
    , getImage : () => {

    }
    , getVideo : () => {

    }
}