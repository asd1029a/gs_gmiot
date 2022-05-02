/*
* 개소 관련 JS
*/

const stations = {
    eventHandler: () => {
        $("#searchBtn").on('click', (e) => {
            stations.create();
        });
    },
    create : () => {
        const $target = $('#stationTable');

        const optionObj = {
            dom: '<"table_body"rt><"table_bottom"p>',
            destroy: true,
            pageLength: 15,
            scrollY: "calc(100% - 40px)",
            ajax :
                {
                    'url' : "/station/paging",
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
                // {data: "facilityStatus"},
                {data: "inFacilityKind"},
                {data: "administZoneName"},
                {data: null}
            ],
            columnDefs: [{
                "targets": -1,
                "data": null,
                "defaultContent": '<span class="button">수정</span>'
            }
            , {
                "targets": 1,
                "data": null,
                "render": function ( data, type, row ) {
                    switch (row.stationKindValue){
                        case "lamp_road" : return `<span class="type pole"><i><img src="/images/default/icon_pole.svg"></i></span>`; break;
                        case "bus" : return `<span class="type bus"><i><img src="/images/default/icon_bus.svg"></i></span>`; break;
                        default: "";
                    }
                }
            }]
            , excelDownload : {
                url : "/station/excel/download"
                , fileName : "개소 목록_"+ dateFunc.getCurrentDateYyyyMmDd(0, '') +".xlsx"
                , search : $("#searchForm form").serializeJSON()
                , headerList : ["고유번호|stationSeq"
                    , "이름|stationName"
                    , "종류|stationKindName"
                    , "행정구역|administZoneName"]
            }
        }

        const evt = {
            click : function(e) {
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                if($(e.target).hasClass('button')) {
                    //commonCode.showPopup('mod');
                    //$('#commonCodeForm').setItemValue(rowData);
                    event.get(rowData.stationSeq, (result) => console.log(result));
                }
            }
            // 클릭시 디테일 select 추가해야함
        }
        comm.createTable($target ,optionObj, evt);
    },
    getList : (param, pCallback) => {
        $.ajax({
            url : "/station"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
        });
    },
    get : (pSeq, pCallback) => {
        $.ajax({
            url : "/station/" + pSeq
            , type : "GET"
        }).done((result) => {
            pCallback(result);
        });
    },
    getListGeoJson : (param, pCallback) => {
        $.ajax({
            url : "/station/geojson"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
            , async : false
        }).done((result) => {
            pCallback(result);
        });
    },
    showPopup : (type) => {

    },
    hidePopup : () => {

    },
    addProc : (pSeq) => {
        const formObj = $('#stationForm').serializeJSON();

        $.ajax({
            url : "/station"
            , type: "PUT"
            , data : formObj
        }).done((result) => {
            comm.showAlert("개소가 등록되었습니다");
            notice.create($('#stationTable'));
            notice.hidePopup();
        });
    },
    modProc : (pSeq) => {
        const formObj = $('#stationForm').serializeJSON();

        $.ajax({
            url : "/station"
            , type: "PATCH"
            , data : formObj
        }).done((result) => {
            comm.showAlert("개소가 수정되었습니다");
            notice.create($('#stationTable'));
            notice.hidePopup();
        });
    },
    delProc : (pSeq) => {
        $.ajax({
            url : "/station/"+pSeq
            , type: "DELETE"
        }).done((result) => {
            comm.showAlert("개소가 삭제되었습니다");
            notice.create($('#stationTable'));
            notice.hidePopup();
        });
    }
}