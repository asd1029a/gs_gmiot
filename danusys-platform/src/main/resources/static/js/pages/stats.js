const stats = {
    korName: {
        "smart_pole_event": "스마트 폴 이벤트",
        "smart_busstop_event": "스마트 정류장 이벤트",
        "EMS_EVENT": "스마트분전함 이벤트",
        "DRONE_EVENT": "드론 관제 이벤트",
    },
    init: (url) => {
        const eventType = url.searchParams.get("eventType");
        const eventTypeCamel = stringFunc.camelize(eventType);

        if ($("#" + eventTypeCamel).parents("li").hasClass("multi")) {
            $("#" + eventTypeCamel).parents("li").addClass("on");
            $("#" + eventTypeCamel).addClass("on");
        } else {
            $("h4 span").eq(1).remove();
            $("h4 i").eq(1).remove();

            $("#" + eventTypeCamel).addClass("on");
        }

        $(".event_type").text(stats.korName[eventType]);

        stats.createTable();
        stats.setChart();
    },
    eventHandler: () => {
        $("#searchBtn").on('click', (e) => {
            stats.createTable();
            stats.setChart();
        });
        // todo 검색 조건은 체크 되었지만 검색 버튼을 누르지 않았을 때 해당 차트만 데이터가 상이함
        // 전체 다 새로 고침을 하거나 이전 검색 데이터를 저장했다가 검색해야 될 듯
        $(".chartBtn span").on("click", (e) => {
            const $target = e.currentTarget;
            if (!$($target).hasClass("on")) {
                $($target).siblings().removeClass("on");
                $target.classList.add("on");
                const chartNm = $target.parentElement.id;

                stats.setChart(chartNm);
            }
        });
    },
    createTable: () => {
        const $target = $('#troubleEventTable');

        const colunms = [
            {data: "eventGradeName", name: "이벤트 등급"},
            {data: "eventKindName", name: "이벤트 종류"},
            {data: "facilityKind", name: "시설물 종류"},
            {data: "administZoneName", name: "행정구역"},
            {data: "eventStartDt", name: "이벤트 발생일시"},
        ]

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
                    'url': "/stats/list",
                    'contentType': "application/json; charset=utf-8",
                    'type': "POST",
                    'data': function (d) {
                        const param = $.extend({}, d, $("#searchForm form").serializeJSON());
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
        const param = $("#searchForm form").serializeJSON();

        if (chartNm === "sumBtn") {
            stats.getSumChartData(param, stats.createSumChart);
        } else if (chartNm === "avgBtn") {
            stats.getAvgChartData(param, stats.createAvgChart);
        } else {
            stats.getSumChartData(param, stats.createSumChart);
            stats.getAvgChartData(param, stats.createAvgChart);
            stats.getMapChartData(param, stats.createMapChart);
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
                width: [0, 0, 3, 3],
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
                seriesName: "주의"
            }, {
                show: false,
                seriesName: "주의"
            }, {
                show: true,
                seriesName: "주의 누적",
                opposite: true
            }, {
                show: false,
                seriesName: "주의 누적",
            }],
            dataLabels: {
                enabled: true,
                // enabledOnSeries: [2, 3]
            },
            series: [
                {
                    name: '긴급',
                    type: 'column',
                    data: data.urgent
                },
                {
                    name: '주의',
                    type: 'column',
                    data: data.caution
                },
                {
                    name: '긴급 누적',
                    type: 'line',
                    data: data.accUrgent
                },
                {
                    name: '주의 누적',
                    type: 'line',
                    data: data.accCaution
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
            name: "긴급",
            data: []
        }, {
            name: "주의",
            data: []
        }];

        datas.forEach(v => {
            data[0].data.push({
                x: v.xAxis,
                y: [v.minUrgent, v.minUrgent, v.maxUrgent, v.maxUrgent],
                avgVal: v.avgUrgent
            });
            data[1].data.push({
                x: v.xAxis,
                y: [v.minCaution, v.minCaution, v.maxCaution, v.maxCaution],
                avgVal: v.avgCaution
            });
        });

        const options = {
            tooltip: {
                enabled: true,
                custom: function ({series, seriesIndex, dataPointIndex, w}) {
                    const g = w.globals;
                    const dataUrgent = w.config.series[0].data[dataPointIndex];
                    const dataCaution = w.config.series[1].data[dataPointIndex];

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
                            </div>
                            <div>
                                <div>
                                    <span style="color: ${g.colors[1]}">${g.seriesNames[1]} ${g.categoryLabels[dataPointIndex]}</span>
                                </div>
                                <div>
                                    <div>최대: ${dataCaution.y[1]}</div>
                                    <div>평균: ${dataCaution.avgVal}</div>
                                    <div>최소: ${dataCaution.y[0]}</div>
                                </div>
                            </div>`;

                    return text;
                }
            },
            dataLabels: {
                enabled: true,
                offsetY: -10,
                background: {
                    opacity: 0.2
                },
                formatter: function (val, {series, seriesIndex, dataPointIndex, w}) {
                    const label = w.config.series[seriesIndex].data[dataPointIndex].avgVal;
                    return label !== null ? label : "";
                },
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
            colors: ['#f04242', '#f9a825'],
            stroke: {
                colors: ['#f04242', '#f9a825']
            },
            theme: {
                mode: "dark"
            },
            fill: {
                opacity: 0.9
            },
            xaxis: {
                tickPlacement: "category",
                tooltip: {
                    enabled: false
                }
            },
            yaxis: {
                tickAmount: 5,
                tooltip: {
                    enabled: false
                }
            },
            plotOptions: {
                candlestick: {
                    colors: [{
                        upward: "#f04242"
                    }, {
                        upward: "#f9a825"
                    }],
                },
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
        const option = {
            backgroundColor: "#00000000",
            tooltip: {
                trigger: 'item',
                showDelay: 0,
                transitionDuration: 0.2
            },
            visualMap: {
                left: 'right',
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
            url: "/stats/sumChart"
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
            url: "/stats/avgChart"
            , type: "POST"
            , data: JSON.stringify(param)
            , contentType: "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
        });
    },
    getMapChartData: (param, pCallback) => {
        $.ajax({
            url: "/stats/mapChart"
            , type: "POST"
            , data: JSON.stringify(param)
            , contentType: "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
        });
    },
    getMapGeoJson: (pCallback) => {
        $.ajax({
            url: "/stats/geoJson"
            , type: "POST"
            , contentType: "application/json; charset=utf-8"
            , async: false
        }).done((result) => {
            pCallback(result);
        });
    },
    getColumnData: (data) => {
        let result = {};
        data.forEach(v => {
            for (let k in v) {
                if (Array.isArray(result[k]))
                    result[k].push(v[k]);
                else
                    result[k] = Array.of(v[k]);
            }
        });
        return result;
    },
}