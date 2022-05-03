/*
* 개소 관련 JS
*/

const station = {
    eventHandler: () => {
        $("#searchBtn").on('click', (e) => {
            station.create();
        });
        $("#stationPopup .title dd").on('click', () => {
            station.hidePopup();
        });
        $("#addStationBtn").on('click', () => {
            station.showPopup("add");
        });
        $("#addStationProcBtn").on('click', () => {
            if($("#stationForm").doValidation()) {
                station.addProc();
            }
        });
        $("#modStationProcBtn").on('click', () => {
            station.showPopup("add");
        });
        $("#delStationProcBtn").on('click', () => {
            station.showPopup("add");
        });
        $("#facilityKind").on('change', (e) => {
            const obj = {facilityKind : $(e.currentTarget).val()}
            station.createFacility(obj, "add");
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
                {data: "stationKindValue"},
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
                    , "주소|address"
                    , "행정구역|administZoneName"]
            }
        }

        const evt = {
            click : function(e) {
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                if($(e.target).hasClass('button')) {
                    station.showPopup('mod');
                    $('#stationForm').setItemValue(rowData);
                }
            }
        }
        comm.createTable($target ,optionObj, evt);
    },
    createFacility : (pObj, type) => {
        const $target = $('#facilityTable');
        const optionObj = {
            dom: '<"table_body"rt>',
            destroy: true,
            bPaginate: false,
            bServerSide: false,
            scrollY: "calc(100% - 50px)",
            ajax:
                {
                    'url': "/facility",
                    'contentType': "application/json; charset=utf-8",
                    'type': "POST",
                    'async': false,
                    'data' : function () {
                        return JSON.stringify( pObj );
                    },
                    'dataSrc' : function(result) {
                        if(type == "add") {
                            return result.data;
                        } else {

                        }
                    }
                }
            ,
            select: {
                toggleable: false
            }
            , columns: [
                {data: "facilityId"},
                {data: "facilityKind"},
                {data: "administZoneName"},
                {data: null}
            ]
            , columnDefs: [{
                "targets": -1,
                "data": null,
                "defaultContent": '<span><input type="checkbox"/><label><span></span></label></span>'
            }]
            , fnCreatedRow: (nRow, aaData, iDataIndex) => {
                const facilitySeq = aaData.facilitySeq;
                const facilitySeqList = $("#stationPopup").data("facilitySeqList");

                $(nRow).find('input').prop('id', "check" + facilitySeq);
                $(nRow).find('input').prop('value', facilitySeq);
                $(nRow).find('label').prop('for', "check" + facilitySeq);

                $.each(facilitySeqList, (idx, item) => {
                    if(item === facilitySeq) {
                        $(nRow).find('input').prop('checked', true);
                    }
                });
            }
            , excelDownload: false
        }

        const evt = {
            click: function (e) {
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                const facilitySeqList = $("#stationPopup").data("facilitySeqList");
                if($(e.target).prop("checked") === true) {
                    facilitySeqList.push(rowData.facilitySeq);
                } else if($(e.target).prop("checked") === false){
                    const index = facilitySeqList.indexOf(rowData.facilitySeq);
                    facilitySeqList.splice(index, 1);
                }
                console.log($("#stationPopup").data("facilitySeqList"));
            }
            , keyup : function() {
                $("#facilityKeyword").off("keyup");
                $("#facilityKeyword").on("keyup", function (input) {
                    const keyword = $(input.currentTarget).val();
                    if(keyword === "") {
                        $('#facilityTable tbody tr').show();
                    } else {
                        $('#facilityTable').DataTable().rows().data().each((data, idx)=> {
                            const rowHtml = $('#facilityTable').DataTable().row(idx).node();
                            if(data.facilityId.includes(keyword)) {
                                console.log("1" + data.facilityId);
                                $(rowHtml).show();
                            } else {
                                console.log("2" + data.facilityId);
                                $(rowHtml).hide();
                            }
                        });
                    }
                });
            }
        }
        comm.createTable($target, optionObj, evt);
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
    addProc : () => {
        const formObj = $('#stationForm').serializeJSON();

        $.ajax({
            url : "/station"
            , type: "PUT"
            , data : formObj
        }).done((result) => {
            comm.showAlert("개소가 등록되었습니다");
            station.create($('#stationTable'));
            station.hidePopup();
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
            station.create($('#stationTable'));
            station.hidePopup();
        });
    },
    delProc : (pSeq) => {
        $.ajax({
            url : "/station/"+pSeq
            , type: "DELETE"
        }).done((result) => {
            comm.showAlert("개소가 삭제되었습니다");
            station.create($('#stationTable'));
            station.hidePopup();
        });
    },
    createSelectOption : ($select, pObj) => {
        $.each(pObj.data, (idx, item)=> {
            if(idx === 0) {
                $select.append("<option value=''>선택</option>");
            } else {
                $select.append("<option value='"+item.codeValue+"'>"+item.codeName+"</option>");
            }
        })

    },
    showPopup : (type) => {
        comm.showModal($("#stationPopup"));
        $("#stationPopup").css('display', 'flex');
        $("#stationForm").initForm();
        $('#stationPopup [data-add], [data-mod]').hide();
        $('#stationPopup [data-'+type+'="true"]').show();
        $("#stationPopup").data("facilitySeqList", []);

        $("#stationForm select[data-type]").each((idx1, selectEle) => {
            const type = $(selectEle).data("type");
            commonCode.getList({type: type}, (result) => {
                station.createSelectOption($(selectEle), result);
            });
        });
        commonCode.getList({type: "facilityKind"}, (result) => {
            station.createSelectOption($("#facilityKind"), result);
        });

        if(type === "add") {
            $("#stationPopup .title dt").text("개소 추가");
        } else {
            $("#stationPopup .title dt").text("개소 수정");
        }
    },
    hidePopup : () => {
        comm.hideModal($("#stationPopup"));
        $("#stationPopup").hide();
    }
}