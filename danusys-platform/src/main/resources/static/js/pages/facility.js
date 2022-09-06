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
                    'url' : "/facility/paging",
                    'contentType' : "application/json; charset=utf-8",
                    'type' : "POST",
                    'data' : function ( d ) {
                        const param = $.extend({}, d, $("#searchForm form").serializeJSON());
                        return JSON.stringify( param );
                    },
                    'dataSrc' : function (result) {
                        $('.title dd .count').text(result.recordsTotal);
                        //$('.status_label li#notUseCount').html(`<span class="gray"></span>미사용 : ${result.statusCount.notUseCount}`);
                        $('.status_label li#normalCount').html(`<span class="green"></span>정상 : ${result.statusCount.normalCount}`);
                        $('.status_label li#errorCount').html(`<span class="red"></span>이상 : ${result.statusCount.errorCount}`);
                        return result.data;
                    }
                },
            select: {
                toggleable: false,
                style: "single"
            },
            columns : [
                {data: "facilitySeq", className: "alignLeft"},
                {data: "facilityId"},
                {data: "facilityKindName"},
                {data: "facilityStatusName"},
                {data: "stationKindName"},
                {data: "stationName"},
                {data: "administZoneName"},
                /*{data: null}*/
            ],
            columnDefs: [
            /*{
                "targets": -1,
                "data": null,
                "defaultContent": '<span class="button">상세보기</span>'
            }
            , */{
                "targets": 2,
                "data": null,
                "render": function ( data, type, row ) {
                    switch (row.facilityStatus){
                        case 0 : return `<span class="status red"></span>`; break;
                        case 1 : return `<span class="status green"></span>`; break;
                        case 2 : return `<span class="status gary"></span>`; break;
                        default: "";
                    }
                }
            }
            ,{
                "target" : 4,
                "data" : null,
                "render": function (data, type, row) {
                    const stationName = row.stationName;
                    if(stationName == null || stationName === "") {
                        return "개소 없음"
                    }
                }
            }]
            , excelDownload : {
                url : "/facility/excel/download"
                , fileName : "시설물 목록_"+ dateFunc.getCurrentDateYyyyMmDd(0, '') +".xlsx"
                , search : $("#searchForm form").serializeJSON()
                , headerList : ["고유 번호|facilitySeq"
                    , "시설물 관리 ID|facilityId"
                    , "시설물 종류|facilityKindName"
                    , "시설물 상태|facilityStatus"
                    , "개소 종류|stationKindName"
                    , "개소 이름|stationName"
                    , "행정구역|administZoneName"]
            }
        }

        const evt = {
            click : function(e) {
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                if($(e.target).hasClass('button')) {
                    //commonCode.showPopup('mod');
                    //$('#commonCodeForm').setItemValue(rowData);
                    facility.get(rowData.facilitySeq, (result) => console.log(result));
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
            pCallback(stringFunc.changeXSSOutputValue(result));
        });
    }
    , getListCctvGeoJson : (param, pCallback) => {
        $.ajax({
            url : "/facility/cctv/geojson"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(stringFunc.changeXSSOutputValue(result));
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
    , facilityControl : (param, pCallback) => {
        // comm.showLoading();
        let callUrl = "";
        if(siGunCode === "47210") { //영주
            callUrl = "/mqtt/set";
        } else if(siGunCode === "41210") { //광명
            callUrl = "/facility/control";
        }

        $.ajax({
            url : callUrl
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
            // comm.hideLoading();
        }).fail((result)=> {
            console.log(result)
            // comm.hideLoading();
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