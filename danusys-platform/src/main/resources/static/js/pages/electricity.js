/*
* 유동인구 JS
*/

const electricity = {
    eventHandler: () => {
        $("#searchBtn").on('click', (e) => {
            electricity.create();
        });
    },
    create : () => {
        const $target = $('#electricityTable');

        const optionObj = {
            dom: '<"table_body"rt><"table_bottom"p>',
            destroy: true,
            pageLength: 15,
            scrollY: "calc(100% - 40px)",
            ajax :
                {
                    'url' : "/facilityOpt/paging",
                    'contentType' : "application/json; charset=utf-8",
                    'type' : "POST",
                    'data' : function ( d ) {
                        const param = $.extend({}, d, $("#searchForm form").serializeJSON());
                        return JSON.stringify( param );
                    },
                    'dataSrc' : function (result) {
                        $('.title dd .count').text(result.recordsTotal);
                        return result.data;
                    }
                },
            select: {
                toggleable: false,
                style: "single"
            },
            columns : [
                {data: "stationSeq", class: "alignLeft"},
                {data: "stationKindName"},
                {data: "stationName"},
                {data: "facilityName"},
                {data: "administZoneName"},
                {data: "address"},
                {data: "optValue"}
            ],
            columnDefs: [{

            }]
            , excelDownload : {
                url : "/facilityOpt/excel/download"
                , fileName : "전력량 목록_"+ dateFunc.getCurrentDateYyyyMmDd(0, '') +".xlsx"
                , search : $("#searchForm form").serializeJSON()
                , headerList : ["고유 번호|stationSeq"
                    , "개소 종류|stationKindName"
                    , "개소 이름|stationName"
                    , "시설물 이름|facilityName"
                    , "행정구역|administZoneName"
                    , "개소 주소|address"
                    , "전력량(W)|optValue"]
            }
        }

        const evt = {

        }
        comm.createTable($target ,optionObj, evt);
    }
}