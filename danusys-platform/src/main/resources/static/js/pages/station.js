/*
* 개소 관련 JS
*/

const station = {
    create : () => {
        const $target = $('#stationTable');

        const optionObj = {
            dom: '<"table_body"rt><"table_bottom"p>',
            destroy: true,
            pageLength: 15,
            scrollY: "calc(100% - 45px)",
            ajax :
                {
                    'url' : "/station",
                    'contentType' : "application/json; charset=utf-8",
                    'type' : "POST",
                    'data' : function ( d ) {
                        console.log(d);
                        const param = $.extend({}, d, $("#searchForm form").serializeJSON());
                        return JSON.stringify( param );
                    },
                    'dataSrc' : function (result) {
                        console.log(result);
                        $('.title dd .count').text(result.recordsTotal);
                        return result.data;
                    }
                },
            select: {
                toggleable: false
            },
            columns : [
                {data: "stationSeq", className: "alignLeft"},
                {data: "stationKind"},
                {data: "stationName"},
                {data: "facilityStatus"},
                {data: "facilityKind"},
                {data: "administZone"},
                {data: "address"},
                {data: null}
            ],
            columnDefs: [{
                "targets": -1,
                "data": null,
                "defaultContent": '<span class="button">상세보기</span>'
            }
            , {
                targets: 1,
                createdCell: function (td, cellData) {
                    if ( cellData !== null ) {
                        $(td).text("");
                        $(td).append("<i><img src='/images/default/clipboard.svg'></i>");
                    } else {
                        $(td).text("없음");
                    }
                }
            }
            , {
                targets: 4,
                render: $.fn.dataTable.render.ellipsis( 50, true )
            }]
            , excelDownload : true
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