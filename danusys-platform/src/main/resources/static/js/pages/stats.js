const stats = {
    isOpt: false,
    type: "",
    init: () => {
        const url = new URL(location.href);
        stats.type = url.searchParams.get("type");
        const typeCamel = stringFunc.camelize(stats.type);

        if($("#" + typeCamel).parents("li").hasClass("multi")){
            $("#" + typeCamel).parents("li").addClass("on");
            $("#" + typeCamel).addClass("on");
        }else {
            $("h4 span").eq(1).remove();
            $("h4 i").eq(1).remove();

            $("#" + typeCamel).addClass("on");
        }

        stats.getEventKind(stats.type, (data) => {
            $(".event_type").text(data.codeName);
        });

        stats.isOpt = stats.type.indexOf("event") + 1 ? false : true;
        if(stats.isOpt){
            $("[data-mode=event]").hide();
            stats.createOptTable();
            stats.setOptChart();
        }else{
            $("[data-mode=opt]").hide();
            stats.createTable();
            stats.setChart();
        }
    },
    eventHandler: () => {
        $("#searchBtn").on('click', (e) => {
            if(stats.isOpt){
                stats.createOptTable();
                stats.setOptChart();
            }else{
                stats.createTable();
                stats.setChart();
            }
        });
        // todo 검색 조건은 체크 되었지만 검색 버튼을 누르지 않았을 때 해당 차트만 데이터가 상이함
        // 전체 다 새로 고침을 하거나 이전 검색 데이터를 저장했다가 검색해야 될 듯
        $(".chartBtn span").on("click", (e) => {
            const $target = e.currentTarget;
            if (!$($target).hasClass("on")) {
                $($target).siblings().removeClass("on");
                $target.classList.add("on");
                const chartNm = $target.parentElement.id;

                if(stats.isOpt){
                    stats.setOptChart(chartNm);
                }else{
                    stats.setChart(chartNm);
                }
            }
        });
    }
    , createTable: () => {
        const $target = $('#troubleEventTable');

        const colunms = [
            {data: "eventGradeName", name: "이벤트 등급"},
            {data: "eventKindName", name: "이벤트 종류"},
            {data: "facilityKind", name: "시설물 종류"},
            {data: "administZoneName", name: "행정구역"},
            {data: "eventStartDt", name: "이벤트 발생일시"},
        ]

        for(col of colunms){
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
    }
    , createOptTable: () => {
        const $target = $('#troubleEventTable');

        const colunms = [
            {data: "stationKind", name: "개소 종류"},
            {data: "stationName", name: "개소 이름"},
            {data: "facilityKind", name: "시설물 종류"},
            {data: "administZoneName", name: "행정구역"},
            {data: "insertDt", name: "기록 일시"},
        ];

        for(col of colunms){
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
    }
    , setChart: (chartNm) => {
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
    }
    , createSumChart: (datas) => {
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
                y: [v.minUrgent, v.maxUrgent],
                avg: v.avgUrgent
            });
            data[1].data.push({
                x: v.xAxis,
                y: [v.minCaution, v.maxCaution],
                avg: v.avgCaution
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
                custom: function ({series, seriesIndex, dataPointIndex, w}) {
                    // console.log({series, seriesIndex, dataPointIndex, w});
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
                                    <div>평균: ${dataUrgent.avg}</div>
                                    <div>최소: ${dataUrgent.y[0]}</div>
                                </div>
                            </div>
                            <div>
                                <div>
                                    <span style="color: ${g.colors[1]}">${g.seriesNames[1]} ${g.categoryLabels[dataPointIndex]}</span>
                                </div>
                                <div>
                                    <div>최대: ${dataCaution.y[1]}</div>
                                    <div>평균: ${dataCaution.avg}</div>
                                    <div>최소: ${dataCaution.y[0]}</div>
                                </div>
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
                    const label = w.config.series[seriesIndex].data[dataPointIndex].avg;
                    return label !== null ? label : "";
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
    getSumOptChartData: (param, pCallback) => {
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
    setOptChart: (chartNm) => {
        const param = $("#searchForm form").serializeJSON();
        param.optName = stats.type;
        if (chartNm === "sumBtn") {
            stats.getSumOptChartData(param, stats.createSumOptChart);
        } else if (chartNm === "avgBtn") {
            stats.getAvgOptChartData(param, stats.createAvgOptChart);
        } else {
            stats.getSumOptChartData(param, stats.createSumOptChart);
            stats.getAvgOptChartData(param, stats.createAvgOptChart);
            stats.getMapOptChartData(param, stats.createMapOptChart);
        }
    }
    , createSumOptChart: (datas) => {
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
    createAvgOptChart: (datas) => {

        const data = [{
            name: "평균 집계",
            data: new Array()
        }];

        datas.forEach(v => {
            data[0].data.push({
                x: v.xAxis,
                y: [v.minValue, v.maxValue],
                avg: v.avgValue
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
            colors: ['#f04242'],
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
                custom: function ({series, seriesIndex, dataPointIndex, w}) {
                    // console.log({series, seriesIndex, dataPointIndex, w});
                    const g = w.globals;
                    const dataUrgent = w.config.series[0].data[dataPointIndex];

                    const text = `
                                <div>
                                    <div>
                                        <span style="color: ${g.colors[0]}">${g.seriesNames[0]} ${g.categoryLabels[dataPointIndex]}</span>
                                    </div>
                                    <div>
                                        <div>최대: ${dataUrgent.y[1]}</div>
                                        <div>평균: ${dataUrgent.avg}</div>
                                        <div>최소: ${dataUrgent.y[0]}</div>
                                    </div>
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
                    const label = w.config.series[seriesIndex].data[dataPointIndex].avg;
                    return label !== null ? label : "";
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
    createMapOptChart: (datas) => {
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
    getAvgOptChartData: (param, pCallback) => {
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
    getMapOptChartData: (param, pCallback) => {
        $.ajax({
            url: "/stats/mapChartOpt"
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
    getEventKind: (pCode, pCallback) => {
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