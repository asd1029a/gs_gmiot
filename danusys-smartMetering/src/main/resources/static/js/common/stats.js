const stats = {
	account : {
        init : () => {
            const $form = $("#searchForm");
            $form.find("#searchBtn").on("click", ()=> {
                stats.account.search();
            });

            // call
            stats.account.search();
        },
        search : () => {
            account.loadAccountDataMinMax();
            account.loadAccountDataSumAvg();
            account.loadAccountDataTop();
            account.loadAccountDataStatsChart();
        }
    },
    event : {
        init : () => {
            const $form = $("#searchForm");
            $form.find("#searchBtn").on("click", ()=> {
                stats.event.search();
            });

            // call
            stats.event.search();
        },
        search : () => {
            event.loadEventTotalCnt();
            event.loadTotalEventChartForStats();
            event.loadProcessEventChartForStats();
            event.loadEventStatsChartForStats();
        }
    }
}