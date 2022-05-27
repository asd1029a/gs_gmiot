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
        } else if(chartNm === "avgBtn") {
            stats.getAvgChartData(param, stats.createAvgChart);
        } else {
            stats.getSumChartData(param, stats.createSumChart);
            stats.getAvgChartData(param, stats.createAvgChart);
            stats.createMapChart();
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
                categories: data.xAxis
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
            ]
        }

        let charts = $("#sumChart").data("charts");
        if (charts != undefined) {
            charts.destroy();
        }
        charts = new ApexCharts(document.querySelector("#sumChart"), options);
        charts.render();
        $("#sumChart").data("charts", charts);
    },
    createAvgChart: (data) => {
        let options = {
            chart: {
                type: 'rangeBar',
                height: "80%",
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
            plotOptions: {
                bar: {
                    columnWidth: "45%"
                }
            },
            dataLabels: {
                enabled: true,
                formatter: function (val, opts) {
                    const sIdx = opts.seriesIndex;
                    const dIdx = opts.dataPointIndex;
                    const minVal = opts.w.globals.seriesRangeStart[sIdx][dIdx];
                    const maxVal = opts.w.globals.seriesRangeEnd[sIdx][dIdx];
                    return (minVal + maxVal) / 2;
                },
            },
            series: [{
                name: "긴급",
                data: [{
                    x: 'Team A',
                    y: [1, 5]
                }, {
                    x: 'Team B',
                    y: [4, 6]
                }, {
                    x: 'Team C',
                    y: [5, 8]
                }, {
                    x: 'Team D',
                    y: [3, 11]
                }]
            }, {
                name: "주의",
                data: [{
                    x: 'Team A',
                    y: [2, 6]
                }, {
                    x: 'Team B',
                    y: [1, 6]
                }, {
                    x: 'Team C',
                    y: [2, 8]
                }, {
                    x: 'Team D',
                    y: [5, 9]
                }]
            }]
        };

        let charts = $("#avgChart").data("charts");
        if (charts != undefined) {
            charts.destroy();
        }
        charts = new ApexCharts(document.querySelector("#avgChart"), options);
        charts.render();
        $("#avgChart").data("charts", charts);
    },
    createMapChart: (data) => {
        const charts = echarts.init(document.querySelector("#mapChart"), "dark", {renderer: "svg"});

        charts.showLoading();
        stats.getMapGeoJson((geoJson) => {
            charts.hideLoading();
            echarts.registerMap('map', JSON.parse(geoJson));
        });
        const option = {
            backgroundColor: "#00000000",
            // tooltip: {
            //     trigger: 'item',
            // },
            visualMap: {
                min: 800,
                max: 50000,
                text: ['High', 'Low'],
                realtime: false,
                calculable: true,
                inRange: {
                    color: ['lightskyblue', 'yellow', 'orangered']
                }
            },
            series: [
                {
                    name: '이벤트 맵',
                    type: 'map',
                    map: 'map',
                    label: {
                        show: true
                    },
                    data: [
                        {name: 'Central and Western', value: 20057.34},
                        {name: 'Eastern', value: 15477.48},
                        {name: 'Islands', value: 31686.1},
                        {name: 'Kowloon City', value: 6992.6},
                        {name: 'Kwai Tsing', value: 44045.49},
                        {name: 'Kwun Tong', value: 40689.64},
                        {name: 'North', value: 37659.78},
                        {name: 'Sai Kung', value: 45180.97},
                        {name: 'Sha Tin', value: 55204.26},
                        {name: 'Sham Shui Po', value: 21900.9},
                        {name: 'Southern', value: 4918.26},
                        {name: 'Tai Po', value: 5881.84},
                        {name: 'Tsuen Wan', value: 4178.01},
                        {name: 'Tuen Mun', value: 2227.92},
                        {name: 'Wan Chai', value: 2180.98},
                        {name: 'Wong Tai Sin', value: 9172.94},
                        {name: 'Yau Tsim Mong', value: 3368},
                        {name: 'Yuen Long', value: 806.98}
                    ],
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