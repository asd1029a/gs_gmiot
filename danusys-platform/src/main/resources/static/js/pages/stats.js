const stats = {
    eventHandler: () => {
        $("#searchBtn").on('click', () => {
            stats.create();
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
    }
    , create: () => {
        const $target = $('#troubleEventTable');

        const optionObj = {
            dom: '<"table_body"rt><"table_bottom"p>',
            destroy: true,
            pageLength: 15,
            scrollY: "calc(100% - 40px)",
            pagingType: "full",
            ajax:
                {
                    'url': "/event",
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
            columns: [
                {data: "eventGradeName"},
                {data: "eventKindName"},
                {data: "eventKindName"},
                {data: "eventKindName"},
                // {data: "stationKind"},
                // {data: "facilityKind"},
                // {data: "dongShortNm"},
                {data: "eventStartDt"},
            ],
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
    }, createSumChart: (datas) => {
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
            colors: ['#f04242', '#f9a825', '#f04242', '#f9a825'],
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
                enabledOnSeries: [2, 3]
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
                y: [v.minCaution, v.maxCaution]
            });
            data[1].data.push({
                x: v.xAxis,
                y: [v.minUrgent, v.maxUrgent]
            });
        });

        let options = {
            chart: {
                type: 'rangeBar',
                height: "80%",
                background: "#00000000",
                zoom: {
                    enabled: false
                },
                toolbar: {
                    show: false
                },
            },
            theme: {
                mode: "dark"
            },
            colors: ['#f04242', '#f9a825'],
            fill: {
                opacity: 0.9
            },
            plotOptions: {
                bar: {
                    columnWidth: "45%"
                }
            },
            tooltip: {
                marker: true,
                custom: function({series, seriesIndex, dataPointIndex, w}) {
                    console.log({series, seriesIndex, dataPointIndex, w});
                    const g = w.globals;
                    const maxVal = g.seriesRangeEnd[seriesIndex][dataPointIndex];
                    const minVal = g.seriesRangeStart[seriesIndex][dataPointIndex];
                    const avgVal = (minVal + maxVal) / 2;

                    const text = `<div><span style="color: ${g.colors[seriesIndex]}">${g.seriesNames[seriesIndex]}: ${g.categoryLabels[dataPointIndex]}</span></div>
                            <div>
                                <div>최대: ${maxVal}</div>
                                <div>평균: ${avgVal}</div>
                                <div>최소: ${minVal}</div>
                            </div>`;

                    return text;
                }
            },
            xaxis: {
                tickPlacement: "category"
            },
            yaxis: {
                tickAmount: 10,
                axisTicks: {}
            },
            dataLabels: {
                enabled: true,
                formatter: function (val, {series, seriesIndex, dataPointIndex, w}) {
                    const minVal = w.globals.seriesRangeStart[seriesIndex][dataPointIndex];
                    const maxVal = w.globals.seriesRangeEnd[seriesIndex][dataPointIndex];
                    return (minVal + maxVal) / 2;
                },
            },
            series: data,
            noData: {
                text: "데이터가 없습니다.",
                align: 'center',
                verticalAlign: 'middle',
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
                        show: true
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
    getEventKind: (pCode,pCallback) => {
        $.ajax({
            url: "/config/commonCode/eventKind/" + pCode
            , type: "GET"
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