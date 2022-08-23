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
                if($("#stationPopup").data("facilitySeqList").length > 0) {
                    station.addProc();
                } else {
                    comm.showAlert("시설물이 선택되지 않았습니다.");
                }
            }
        });

        $("#modStationProcBtn").on('click', () => {
            if($("#stationForm").doValidation()) {
                if($("#stationPopup").data("facilitySeqList").length > 0) {
                    station.modProc($("#stationPopup").data("stationSeq"));
                } else {
                    comm.showAlert("시설물이 선택되지 않았습니다.");
                }
            }
        });

        $("#delStationProcBtn").on('click', () => {
            comm.confirm("해당 개소를 삭제하시겠습니까?",
                {},
                () => {
                    const stationSeq = $("#stationPopup").data("stationSeq");
                    station.delProc(stationSeq);
                },
                () => {return false;});
        });

        $("#facilityKind").on('change', (e) => {
            const facilityKind = $(e.currentTarget).val();
            if(facilityKind === null || facilityKind === "") {
                $("#facilityTable").DataTable().clear();
                $("#facilityTable").DataTable().destroy();
            } else {
                const obj = {
                    "popupType" : "station"
                    , "facilityKind" : facilityKind
                    , "type" : $("#stationPopup").data("type")
                }
                station.createFacility(obj);
            }
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
                {data: "inFacilityKind"},
                {data: "administZoneName"},
                {data: null}
            ],
            columnDefs: [{
                "targets": -1,
                "data": null,
                "defaultContent": '<span class="button">수정</span>'
            }]
            , excelDownload : {
                url : "/station/excel/download"
                , fileName : "개소 목록_"+ dateFunc.getCurrentDateYyyyMmDd(0, '') +".xlsx"
                , search : $("#searchForm form").serializeJSON()
                , headerList : ["고유 번호|stationSeq"
                    , "개소 종류|stationKindName"
                    , "개소 이름|stationName"
                    , "시설물 종류|inFacilityKind"
                    , "행정구역|administZoneName"]
            }
        }

        const evt = {
            click : function(e) {
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                if($(e.target).hasClass('button')) {
                    station.showPopup('mod');
                    $('#stationForm').setItemValue(rowData);
                    $('#stationKind').val(rowData.stationKindValue);
                    $('#administZone').val(rowData.administZone);
                    $('#stationPopup').data("stationSeq", rowData.stationSeq);
                    station.getListFacilityInStation(
                        {"stationSeq" : rowData.stationSeq},
                        (result) => {
                            const facilitySeqList = [];
                            $.each(result.data, (i, e) => {
                                facilitySeqList.push(e.facilitySeq);
                            });
                            $("#stationPopup").data("facilitySeqList", facilitySeqList);
                    });
                }
            }
        }
        comm.createTable($target ,optionObj, evt);
    },
    createFacility : (pObj) => {
        const $target = $('#facilityTable');
        if(pObj.type === "mod") {
            pObj.stationSeq = $("#stationPopup").data("stationSeq");
        }

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
                        return result.data;
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
                                $(rowHtml).show();
                            } else {
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
    getListFacilityInStation : (param, pCallback) => {
        $.ajax({
            url : "/facility/inStation"
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
            pCallback(stringFunc.changeXSSOutputValue(result));
        });
    },
    addProc : () => {
        const formObj = {
            "stationInfo" : $('#stationForm').serializeJSON()
            , "facilitySeqList" : $('#stationPopup').data("facilitySeqList")
        };

        $.ajax({
            url : "/station"
            , type: "PUT"
            , data : JSON.stringify(formObj)
            , contentType : "application/json; charset=utf-8"
        }).done((result) => {
            comm.showAlert("개소가 등록되었습니다");
            station.create($('#stationTable'));
            station.hidePopup();
        });
    },
    modProc : (pSeq) => {
        const formObj = {
            "stationSeq" : pSeq
            , "stationInfo" : $('#stationForm').serializeJSON()
            , "facilitySeqList" : $('#stationPopup').data("facilitySeqList")
        };

        $.ajax({
            url : "/station"
            , type: "PATCH"
            , data : JSON.stringify(formObj)
            , contentType : "application/json; charset=utf-8"
        }).done((result) => {
            comm.showAlert("개소가 수정되었습니다");
            station.create($('#stationTable'));
            station.hidePopup();
        });
    },
    modUnFormProc : (pSeq, obj) => {
        $.ajax({
            url : "/station"
            , type: "PATCH"
            , data : JSON.stringify(
                Object.assign(obj, {"stationSeq" : pSeq})
            )
            , contentType : "application/json; charset=utf-8"
        }).done((result) => {
            comm.showAlert("개소가 수정되었습니다");
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
        $select.find("option").remove();
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
            const obj = {
                type : $(selectEle).data("type")
            }
            const subType = $(selectEle).data("subType");
            if(typeof subType !== "undefined") {
                obj.subType = subType;
            }
            commonCode.getList(obj, (result) => {
                station.createSelectOption($(selectEle), result);
            });
        });
        commonCode.getList({type: "facilityKind"}, (result) => {
            station.createSelectOption($("#facilityKind"), result);
        });

        if(type === "add") {
            $("#stationPopup .title dt").text("개소 추가");
            $("#stationPopup").data("type", "add");
        } else if(type === "mod"){
            $("#stationPopup .title dt").text("개소 수정");
            $("#stationPopup").data("type", "mod");
        }
    },
    hidePopup : () => {
        comm.hideModal($("#stationPopup"));
        $("#stationPopup").hide();
        $("#facilityKind").val("");
        $("#facilityTable").DataTable().clear();
        $("#facilityTable").DataTable().destroy();
    }
}