const stats = {
    eventHandler: ($target, pEventType) => {
        $("#searchBtn").on('click', () => {
            stats.create($target, pEventType);
            stats.setChart(pEventType);
        });
        $(".chartBtn span").on("click", (e) => {
            const $target = e.currentTarget;
            if ($target.class !== "on"){
                $($target).siblings().removeClass("on");
                $target.classList.add("on");

                const param = $("#searchForm form").serializeJSON();
                // param.eventKind = '';
                if($target.id.indexOf("sum") >= 0){
                    // const data = stats.getSumChartData(param);
                    stats.createSumChart();
                }else{
                    // const data = stats.getAvgChartData(param);
                    stats.createAvgChart();
                }
            }
        });
    }
    , create: ($target, pEventType) => {
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
                        param.pEventType = pEventType;
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
    setChart: (pEventType) => {
        const param = $("#searchForm form").serializeJSON();
        // param.eventKind = '';

        // const sumChartData = stats.getSumChartData(param);
        // const avgChartData = stats.getAvgChartData(param);
        // const mapChartData = stats.getMapChartData(param);
        // stats.createSumChart(sumChartData);
        // stats.createAvgChart(avgChartData);
        // stats.createMapChart(mapChartData);
        stats.createSumChart();
        stats.createAvgChart();
        stats.createMapChart();
    }, createSumChart: (data) => {
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
                categories: ['일', '월', '화', '수', '목', '금', '토']
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
                    data: [10, 10, 10, 10, 10, 10, 10]
                },
                {
                    name: '주의',
                    type: 'column',
                    data: [10, 10, 10, 10, 10, 10, 10]
                },
                {
                    name: '긴급 누적',
                    type: 'line',
                    data: [50, 60, 70, 80, 90, 100, 110]
                },
                {
                    name: '주의 누적',
                    type: 'line',
                    data: [160, 170, 180, 190, 200, 210, 220]
                }
            ]
        }

        let charts = $("#sumChart").data("charts");
        if(charts != undefined){
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
                opacity: 1
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
        if(charts != undefined){
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
        $.ajax({
            url: "/event/sumChart"
            , type: "POST"
            , data: JSON.stringify(param)
            , contentType: "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
        });
    },
    getAvgChartData: (param, pCallback) => {
        $.ajax({
            url: "/event/avgChart"
            , type: "POST"
            , data: JSON.stringify(param)
            , contentType: "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
        });
    },
    getMapChartData: (param, pCallback) => {
        $.ajax({
            url: "/event/mapChart"
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
    }
}