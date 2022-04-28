const chart = {
    /**
     * @param pOptions(차트 옵션)
     * @param pId(chart ID)
     * @returns void
     */
    createChart : (pOptions, pId) => {
        let charts;
        const options = $.extend(chart.getDefaultOption(), pOptions);

        if(typeof($("#"+pId).data("charts"))!="undefined") {
            charts = $("#"+pId).data("charts");
            charts.destroy();
        }
        charts = new ApexCharts(document.querySelector("#" + pId), options);
        charts.render();
        $("#"+pId).data("charts", charts);
    },
    /**
     * @returns chart 기본옵션 object
     */
    getDefaultOption : () => {
        const options = {
            zoom: {
                enabled: false,
            },
            colors: ['#31B4D6', '#63CFA1', '#D4DB6E', '#F2B36D', '#E868B0'],
            dataLabels: {
                enabled: true,
            },
            stroke: {
                curve: 'smooth'
            },
            grid: {
                borderColor: '#FFFFFF',
                row: {
                    colors: ['#424242', '#424242'], // takes an array which will be repeated on columns
                    opacity: 0.5
                },
                padding : {
                    left : 0,
                    right: 0
                }
            },
            markers: {
                size: 1
            },
            legend: {
                position: 'top',
                horizontalAlign: 'right',
                floating: true,
                offsetY: -25,
                offsetX: -5
            },
            chart: {
                height: 350,
                type: 'line',
                dropShadow: {
                    enabled: true,
                    color: '#000',
                    top: 18,
                    left: 7,
                    blur: 10,
                    opacity: 0.2
                },
                toolbar: {
                    show: true
                }
            },
            theme: {
                mode: 'dark',
                palette: 'palette1',
                monochrome: {
                    enabled: false,
                    color: '#255aee',
                    shadeTo: 'light',
                    shadeIntensity: 0.65
                },
            }
            /*
            tooltip: {
                enabled: true,
                marker: {
                    show: true
                },
                y: {
                    formatter: "sdfsdf",
                    title: {
                        formatter: "213",
                    },
                }
            }
            */
        };
        return options;
    }
}