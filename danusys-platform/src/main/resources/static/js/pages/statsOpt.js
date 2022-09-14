const statsOpt = {
    optType: null,
    optName: {
        'floatingPopulationBus': {
            'korName': '스마트 정류장 이용자',
            'optName': 'floating_population',
            'stationKind': 'smart_station',
        },
        'floatingPopulationPole': {
            'korName': '스마트 폴 유동인구',
            'optName': 'floating_population',
            'stationKind': 'smart_pole',
        },
        'electricityBus': {
            'korName': '스마트 버스 전력량',
            'optName': 'wattage',
            'facilityKind': 'wattage',
        },
        "electricitySunlight": {
            'korName': "태양광 전력량",
            'optName': 'sunlight',
            'facilityKind': 'sunlight',
        },
        "electricityLampWalk": {
            'korName': "스마트 보안등 전력량",
            'optName': 'whUse',
            'facilityKind': 'lamp_walk',
        },
        "electricityBikeCharging": {
            'korName': "자전거 충전소 전력량",
            'optName': 'electricPower',
            'facilityKind': 'bike_charging',
        },
    },
    init: (url) => {
        statsOpt.optType = url.searchParams.get("optType");

        if ($("#" + statsOpt.optType).parents("li").hasClass("multi")) {
            $("#" + statsOpt.optType).parents("li").addClass("on");
            $("#" + statsOpt.optType).addClass("on");
        } else {
            $("h4 span").eq(1).remove();
            $("h4 i").eq(1).remove();

            $("#" + statsOpt.optType).addClass("on");
        }

        $(".event_type").text(statsOpt.optName[statsOpt.optType].korName);

        $("[data-mode=event]").hide();
        statsOpt.createTable();
        statsOpt.setChart();
    },
    eventHandler: () => {
        $("#searchBtn").on('click', (e) => {
            statsOpt.createTable();
            statsOpt.setChart();
        });
        // todo 검색 조건은 체크 되었지만 검색 버튼을 누르지 않았을 때 해당 차트만 데이터가 상이함
        // 전체 다 새로 고침을 하거나 이전 검색 데이터를 저장했다가 검색해야 될 듯
        $(".chartBtn span").on("click", (e) => {
            const $target = e.currentTarget;
            if (!$($target).hasClass("on")) {
                $($target).siblings().removeClass("on");
                $target.classList.add("on");
                const chartNm = $target.parentElement.id;

                statsOpt.setChart(chartNm);
            }
        });
    },
    createTable: () => {
        const $target = $('#troubleEventTable');

        const colunms = [
            {data: "stationKind", name: "개소 종류"},
            {data: "stationName", name: "개소 이름"},
            {data: "facilityKind", name: "시설물 종류"},
            {data: "administZoneName", name: "행정구역"},
            {data: "insertDt", name: "기록 일시"},
        ];

        for (col of colunms) {
            $("thead tr").append(`<th>${col.name}</th>`);
        }

        const optionObj = {
            dom: '<"table_body"rt><"table_bottom"p>',
            destroy: true,
            pageLength: 15,
            scrollY: "calc(100% - 40px)",
            pagingType: "full",
            ajax:
                {
                    'url': "/stats/listOpt",
                    'contentType': "application/json; charset=utf-8",
                    'type': "POST",
                    'data': function (d) {
                        const param = $.extend(statsOpt.optName[statsOpt.optType], d, $("#searchForm form").serializeJSON());
                        console.log(param);

                        return JSON.stringify(param);
                    },
                    'dataSrc': function (result) {
                        $('.title dd .count').text(result.recordsTotal);
                        return result.data;
                    }
                },
            select: {
                toggleable: false,
                style: "single"
            },
            columns: colunms,
        }

        comm.createTable($target, optionObj);
    },
    setChart: (chartNm) => {
        const param = $.extend(statsOpt.optName[statsOpt.optType], $("#searchForm form").serializeJSON());

        if (chartNm === "sumBtn") {
            statsOpt.getSumChartData(param, statsOpt.createSumChart);
        } else if (chartNm === "avgBtn") {
            statsOpt.getAvgChartData(param, statsOpt.createAvgChart);
        } else {
            statsOpt.getSumChartData(param, statsOpt.createSumChart);
            statsOpt.getAvgChartData(param, statsOpt.createAvgChart);
            statsOpt.getMapChartData(param, statsOpt.createMapChart);
        }
    },
    createSumChart: (datas) => {
        const data = stats.getColumnData(datas);

        const options = {
            chart: {
                height: "80%",
                type: 'line',
                stacked: false,
                toolbar: {
                    show: false
                },
                background: "#00000000",
                zoom: {
                    enabled: false
                }
            },
            theme: {
                mode: "dark"
            },
            colors: ['#f04242', '#f9a825'],
            fill: {
                opacity: 0.9
            },
            stroke: {
                width: [0, 3],
                dashArray: 10
            },
            plotOptions: {
                bar: {
                    columnWidth: '25%',
                }
            },
            xaxis: {
                categories: data.xAxis,
                tooltip: {
                    enabled: false
                }
            },
            yaxis: [{
                show: true,
                seriesName: "합계"
            }, {
                show: false,
                seriesName: "누적 합계"
            }],
            dataLabels: {
                enabled: true,
                enabledOnSeries: [1]
            },
            series: [
                {
                    name: '합계',
                    type: 'column',
                    data: data.value
                },
                {
                    name: '누적 합계',
                    type: 'line',
                    data: data.accValue
                }
            ],
            noData: {
                text: "데이터가 없습니다.",
                align: 'center',
                verticalAlign: 'middle',
            }
        }

        let charts = $("#sumChart").data("charts");
        if (charts != undefined) {
            charts.destroy();
        }
        charts = new ApexCharts(document.querySelector("#sumChart"), options);
        charts.render();
        $("#sumChart").data("charts", charts);
    },
    createAvgChart: (datas) => {

        const data = [{
            name: "평균 집계",
            data: new Array()
        }];

        datas.forEach(v => {
            data[0].data.push({
                x: v.xAxis,
                y: [v.minValue, v.minValue, v.maxValue, v.maxValue],
                avgVal: v.avgValue
            });
        });

        const options = {
            dataLabels: {
                enabled: true,
                offsetY: -10,
                background: {
                    opacity: 0.2
                },
                formatter: function (val, {series, seriesIndex, dataPointIndex, w}) {
                    const label = w.config.series[0].data[dataPointIndex].avgVal;
                    return label !== null ? label : "";
                },
            },
            tooltip: {
                enabled: true,
                marker: true,
                enabledOnSeries: [1],
                custom: function ({series, seriesIndex, dataPointIndex, w}) {
                    console.log({series, seriesIndex, dataPointIndex, w});
                    const g = w.globals;
                    const dataUrgent = w.config.series[0].data[dataPointIndex];

                    const text = `
                                <div>
                                    <div>
                                        <span style="color: ${g.colors[0]}">${g.seriesNames[0]} ${g.categoryLabels[dataPointIndex]}</span>
                                    </div>
                                    <div>
                                        <div>최대: ${dataUrgent.y[1]}</div>
                                        <div>평균: ${dataUrgent.avgVal}</div>
                                        <div>최소: ${dataUrgent.y[0]}</div>
                                    </div>
                                </div>`;

                    return text;
                }
            },
            series: data,
            noData: {
                text: "데이터가 없습니다.",
                align: 'center',
                verticalAlign: 'middle',
            },
            chart: {
                type: 'candlestick',
                height: "80%",
                toolbar: false,
                background: "#00000000",
                zoom: {
                    enabled: false
                },
                toolbar: {
                    show: false
                },
            },
            colors: ["#f04242"],
            theme: {
                mode: "dark"
            },
            fill: {
                opacity: 0.9
            },
            xaxis: {
                tickPlacement: "category"
            },
            yaxis: {
                tickAmount: 5,
                tooltip: {
                    enabled: false
                }
            },
            plotOptions: {
                candlestick: {
                    colors: {
                        upward: "#f04242"
                    },
                }
            }
        };

        let charts = $("#avgChart").data("charts");
        if (charts != undefined) {
            charts.destroy();
        }
        charts = new ApexCharts(document.querySelector("#avgChart"), options);
        charts.render();
        $("#avgChart").data("charts", charts);
    },
    createMapChart: (datas) => {
        const charts = echarts.init(document.querySelector("#mapChart"), "dark", {renderer: "svg"});

        charts.showLoading();
        stats.getMapGeoJson((geoJson) => {
            charts.hideLoading();
            echarts.registerMap('map', JSON.parse(geoJson));
        });

        let maxVal = 0;
        let minVal = 0;
        datas.forEach(v => {
            maxVal = maxVal < v.value ? v.value : maxVal;
            minVal = minVal > v.value ? v.value : minVal;
        })

        const option = {
            backgroundColor: "#00000000",
            tooltip: {
                trigger: 'item',
                showDelay: 0,
                transitionDuration: 0.2
            },
            visualMap: {
                left: 'right',
                min: minVal,
                max: maxVal,
                inRange: {
                    color: ['#313695', '#fee090', '#a50026']
                },
                text: ['High', 'Low'],
                calculable: true
            },
            series: [
                {
                    name: '이벤트 맵',
                    type: 'map',
                    roam: true,
                    map: 'map',
                    label: {
                        // show: true
                    },
                    emphasis: {
                        label: {
                            show: true
                        }
                    },
                    data: datas
                }
            ]
        };

        charts.setOption(option);
        $("#mapChart").data("charts", charts);
    },
    getSumChartData: (param, pCallback) => {
        param.unit = document.querySelector("#sumBtn span.on").classList[0];
        $.ajax({
            url: "/stats/sumChartOpt"
            , type: "POST"
            , data: JSON.stringify(param)
            , contentType: "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
        });
    },
    getAvgChartData: (param, pCallback) => {
        param.unit = document.querySelector("#avgBtn span.on").classList[0];
        $.ajax({
            url: "/stats/avgChartOpt"
            , type: "POST"
            , data: JSON.stringify(param)
            , contentType: "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
        });
    },
    getMapChartData: (param, pCallback) => {
        $.ajax({
            url: "/stats/mapChartOpt"
            , type: "POST"
            , data: JSON.stringify(param)
            , contentType: "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
        });
    },
}