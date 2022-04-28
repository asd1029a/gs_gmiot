/*
* 시설물 관련 JS
*/

const facility = {
    eventHandler: () => {
        $("#searchBtn").on('click', (e) => {
            facility.create();
        });
    },
    create : () => {
        const $target = $('#facilityTable');

        const optionObj = {
            dom: '<"table_body"rt><"table_bottom"p>',
            destroy: true,
            pageLength: 15,
            scrollY: "calc(100% - 40px)",
            ajax :
                {
                    'url' : "/facility",
                    'contentType' : "application/json; charset=utf-8",
                    'type' : "POST",
                    'data' : function ( d ) {
                        const param = $.extend({}, d, $("#searchForm form").serializeJSON());
                        return JSON.stringify( param );
                    },
                    'dataSrc' : function (result) {
                        $('.title dd .count').text(result.recordsTotal);
                        $('.status_label li#notUseCount').html(`<span class="gray"></span>미사용 : ${result.statusCount.notUseCount}`);
                        $('.status_label li#normalCount').html(`<span class="green"></span>정상 : ${result.statusCount.normalCount}`);
                        $('.status_label li#errorCount').html(`<span class="red"></span>이상 : ${result.statusCount.errorCount}`);
                        return result.data;
                    }
                },
            select: {
                toggleable: false
            },
            columns : [
                {data: "facilitySeq", className: "alignLeft"},
                {data: "facilityKindName"},
                {data: "facilityStatus"},
                {data: "stationKindName"},
                {data: "stationName"},
                {data: "administZoneName"},
                {data: "address"},
                {data: null}
            ],
            columnDefs: [{
                "targets": -1,
                "data": null,
                "defaultContent": '<span class="button">상세보기</span>'
            }
            , {
                targets: 6,
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
                    event.get(rowData.facilitySeq, (result) => console.log(result));
                }
            }
        }
        comm.createTable($target ,optionObj, evt);
    }
    , getList : (param, pCallback) => {
        $.ajax({
            url : "/facility"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
        });
    }
    , getListGeoJson : (param, pCallback) => {
        $.ajax({
            url : "/facility/geojson"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
        });
    }
    , get : (pSeq, pCallback) => {
        $.ajax({
            url : "/facility/" + pSeq
            , type : "GET"
        }).done((result) => {
            pCallback(result);
        });
    }
    , addProc : () => {
        const formObj = $('#facilityForm').serializeJSON();

        $.ajax({
            url : "/facility"
            , type: "PUT"
            , data : formObj
        }).done((result) => {
            comm.showAlert("시설물이 등록되었습니다");
            facility.create($('#facilityTable'));
            facility.hidePopup();
        });
    }
    , addOptProc : (pObj, doneCallback, failCallback) => {
        $.ajax({
            url : "/facility/opt"
            , type: "PUT"
            , data :  JSON.stringify(pObj)
            , asnyc : false
            , contentType : "application/json; charset=utf-8",
        }).done((result) => {
            doneCallback(result);
        }).fail(() => {
            failCallback();
        });
    }
    , modProc : () => {
        const formObj = $('#facilityForm').serializeJSON();

        $.ajax({
            url : "/facility"
            , type: "PATCH"
            , data : formObj
            , contentType : "application/json; charset=utf-8",
        }).done((result) => {
            comm.showAlert("시설물이 수정되었습니다");
            facility.create($('#facilityTable'));
            facility.hidePopup();
        });
    }
    , modOptProc : (pObj, doneCallback, failCallback) => {
        $.ajax({
            url : "/facility/opt"
            , type: "PATCH"
            , data : JSON.stringify(pObj)
            , asnyc : false
            , contentType : "application/json; charset=utf-8",
        }).done((result) => {
            doneCallback(result);
        }).fail(() => {
            failCallback();
        });
    }
    , delProc : (pSeq) => {
        $.ajax({
            url : "/facility/"+pSeq
            , type: "DELETE"
            , contentType : "application/json; charset=utf-8",
        }).done((result) => {
            comm.showAlert("시설물이 삭제되었습니다");
            facility.create($('#facilityTable'));
            facility.hidePopup();
        });
    }
    , delOptProc : (pObj, doneCallback, failCallback) => {
        $.ajax({
            url : "/facility/opt"
            , type: "DELETE"
            , data : JSON.stringify(pObj)
            , contentType : "application/json; charset=utf-8",
        }).done((result) => {
            doneCallback(result);
        }).fail(() => {
            failCallback();
        });
    }
    , createFacilityOptList : (optType, facilitySeqList, defaultOptObj) => {
        const resultObj = {};
        const facilityOptList = [];
        $.each(facilitySeqList, (idx, facilitySeq)=> {
            $.each(defaultOptObj, (key, val) => {
                let facilityOptObj = {
                    "facility_seq": facilitySeq
                    , "facility_opt_name": key
                    , "facility_opt_value": val
                    , "facility_opt_type": optType
                }
                facilityOptList.push(facilityOptObj);
            });
        });
        resultObj.facilityOptList = facilityOptList
        return resultObj;
    }
}