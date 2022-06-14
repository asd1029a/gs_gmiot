/**
 * 이벤트 관리
 */

const event = {
    init : (pTargetName) => {
        const nameObj = {
            "eventSmartPole" : {
                korName : "스마트 폴"
                , eventKind : "smart_pole_event"
            }
            , "eventSmartBusStop" : {
                korName : "스마트 정류장"
                , eventKind : "smart_busstop_event"
            }
            , "eventSmartCabinet" : {
                korName : "스마트 분전반"
                , eventKind : "EMS_EVENT"
            }
            , "eventSmartDrone" : {
                korName : "스마트 드론"
                , eventKind : "DRONE_EVENT"
            }
        }
        /* 공통 이벤트 페이지 세팅 */
        const $milestoneArr = $(".milestone span");
        $($milestoneArr[1]).text(nameObj[pTargetName].korName);
        $($milestoneArr[2]).text(nameObj[pTargetName].korName + " 이벤트");
        $(".search_list .article_title dt").text(nameObj[pTargetName].korName + " 이벤트 목록");
        $(".search_form h6").text(nameObj[pTargetName].korName + " 이벤트 검색");
        $(".search_list .search_table table").attr("id", pTargetName + "Table");
        $(".popup").attr("id", pTargetName + "Popup");
        $(".dropdown_checkbox").data("selectbox-sub-type", nameObj[pTargetName].eventKind);
        /* 멀티 셀렉트박스 제외할 종류 설정*/
        if(pTargetName === "eventSmartPole") {
            $(".dropdown_checkbox").data("selectbox-ignore-type", "dtctn_crmss");
        }
    }
    , eventHandler : ($target, pEventType) => {
        $("#searchBtn").on('click', () => {
            event.create($target, pEventType);
        });
        $("#eventPopup .title dd").on('click', () => {
            event.hidePopup();
        });
    }
    , create : ($target, pEventType) => {
        const optionObj = {
            dom: '<"table_body"rt><"table_bottom"p>',
            destroy: true,
            pageLength: 15,
            scrollY: "calc(100% - 40px)",
            ajax :
                {
                    'url' : "/event",
                    'contentType' : "application/json; charset=utf-8",
                    'type' : "POST",
                    'async' : false,
                    'data' : function ( d ) {
                        const param = $.extend({}, d, $("#searchForm form").serializeJSON());
                        //param.pEventType = pEventType;
                        return JSON.stringify( param );
                    },
                    'dataSrc' : function (result) {
                        $('.title dd .count').text(result.recordsTotal);
                        $('.status_label li .red').parent().html(`<span class="red"></span>미처리 : ${result.statusCount.red}`);
                        $('.status_label li .yellow').parent().html(`<span class="yellow"></span>조치 완료 : ${result.statusCount.yellow}`);
                        $('.status_label li .green').parent().html(`<span class="green"></span>이벤트 종료 : ${result.statusCount.green}`);
                        return result.data;
                    }
                },
            select: {
                toggleable: false,
                style: "single"
            },
            columns : event.getColumns(pEventType),
            columnDefs: [
                /*{
                "targets": -1,
                "data": null,
                "defaultContent": '<span class="button">상세보기</span>'
                }
                ,*/{
                    targets: 1,
                    render: $.fn.dataTable.render.ellipsis( 50, true )
                }]
            //, excelDownload : true
        }

        const evt = {
            click : function(e) {
                const $form = $('#eventPopup form');
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                if($(e.target).hasClass('button')) {
                    event.showPopup(rowData);
                    $('#eventForm').setItemValue(rowData);
                    event.get(rowData.eventSeq ,(result) => {
                        $.each($form, (idx, item) => {
                            $(item).data("eventSeq", rowData.eventSeq);
                            $(item).setItemValue(result);
                        })
                    });
                }
            }
            // 클릭시 디테일 select 추가해야함
        }
        comm.createTable($target ,optionObj, evt);
    },
    getList : (param, pCallback) => {
        $.ajax({
            url : "/event"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
        });
    },
    getListGeoJson : (param, pCallback) => {
        param.geojson = true;
        $.ajax({
            url : "/event/geojson"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
        });
    },
    get : (pSeq, pCallback) =>{
        $.ajax({
            url : "/event/" + pSeq
            , type : "GET"
        }).done((result) => {
            pCallback(result);
        });
    },
    getListEventKind : (parentCodeValue, pCallback) => {
        $.ajax({
            url : "/event/eventKindCodeList/" + parentCodeValue
            , type : "GET"
        }).done((result) => {
            pCallback(result);
        });
    },
    getColumns : (pEventType) => {
        let columns = [];
        columns.push({data: "eventSeq", className: "alignLeft"});   //이벤트 아이디
        columns.push({data: "eventKindName"});                          //이벤트 종류
        if(pEventType === 'trouble') {
            columns.push({data: "eventProcStat"});
            columns.push({data: "facilitySeq", className: "alignLeft"});
            columns.push({data: "facilityKind"});
            columns.push({data: "stationKind"});
            columns.push({data: "stationName"});
            columns.push({data: "dongShortNm"});
            columns.push({data: "address"});
            columns.push({data: "eventStartDt"});
            columns.push({data: "eventEndDt"});
        } else {
            columns.push({data: "eventGradeName"});
            columns.push({data: "eventProcStatName"});
            columns.push({data: "stationName"});
            columns.push({data: "facilityName"});
            columns.push({data: "administZoneName"});
            columns.push({data: "eventStartDt"});
            columns.push({data: "eventEndDt"});
        }

        return columns;
    },
    showPopup : (pData) => {
        comm.showModal($('#eventPopup'));
        $('#eventPopup').css("display", "flex");

        $.each($('#eventPopup form'), (idx, item)=> {
            $(item).initForm();
        });
        $('#eventPopup [data-mode]').hide();
        $('#eventPopup .title dt').text(pData.eventSeq + " " + pData.eventKind + " " + pData.stationName);
    },
    hidePopup : () => {
        comm.hideModal($('#eventPopup'));
        $('#eventPopup').hide();
    },
    addProc : ($target, pEventType) => {
        const formObj = $target.serializeJSON();

        if($target.doValidation()) {
            $.ajax({
                url : "/event"
                , type: "PUT"
                , contentType : "application/json; charset=utf-8"
                , data : JSON.stringify(formObj)
            }).done((result) => {
                comm.showAlert("등록되었습니다");
                event.create($target, pEventType);
                event.hidePopup();
            });
        } else {
            return false;
        }
    },
    modProc : ($target, pEventType) => {
        const formObj = $target.serializeJSON();

        comm.ajaxPost({
                url : "/event"
                , type : "PATCH"
                , data : formObj
            },
            (result) => {
                comm.showAlert("수정되었습니다");
                event.create($target, pEventType);
                event.hidePopup();
            });
    },
    delProc : ($target, pEventType, pSeq) => {
        comm.ajaxPost({
                url : "/event"
                , type : "DELETE"
                , data : {seq : pSeq}
            },
            (result) => {
                comm.showAlert("삭제되었습니다");
                event.create($target, pEventType);
                event.hidePopup();
            });
    }
}