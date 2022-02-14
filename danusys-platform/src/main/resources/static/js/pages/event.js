/**
 * 이벤트 관리
 */

const event = {
    eventHandler : ($target, pEventType) => {
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
                    'data' : function ( d ) {
                        console.log(d);
                        const param = $.extend({}, d, $("#searchForm form").serializeJSON());
                        param.pEventType = pEventType;
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
            columns : event.getColumns(pEventType),
            columnDefs: [{
                "targets": -1,
                "data": null,
                "defaultContent": '<span class="button">상세보기</span>'
            }
                , {
                    targets: 0,
                    render: $.fn.dataTable.render.ellipsis( 30, true )
                }
                , {
                    targets: 1,
                    render: $.fn.dataTable.render.ellipsis( 50, true )
                }]
            , excelDownload : true
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
    getColumns : (pEventType) => {
        let columns = [];
        columns.push({data: "eventSeq", className: "alignLeft"});   //이벤트 아이디
        columns.push({data: "eventKind"});                          //이벤트 종류
        if(pEventType === 'city') {
            columns.push({data: "eventGrade"});
            columns.push({data: "eventProcStat"});
            columns.push({data: "stationSeq", className: "alignLeft"});
            columns.push({data: "stationName"})
            columns.push({data: "dongShortNm"});
            columns.push({data: "address"});
            columns.push({data: "eventStartDt"});
            columns.push({data: "eventEndDt"});
        }
        else if(pEventType === 'trouble') {
            columns.push({data: "eventProcStat"});
            columns.push({data: "facilitySeq", className: "alignLeft"});
            columns.push({data: "facilityKind"});
            columns.push({data: "stationKind"})
            columns.push({data: "stationName"})
            columns.push({data: "dongShortNm"});
            columns.push({data: "address"});
            columns.push({data: "eventStartDt"});
            columns.push({data: "eventEndDt"});
        }
        else if(pEventType === 'cabinet') {
            columns.push({data: "eventProcStat"});
            columns.push({data: "facilitySeq", className: "alignLeft"});
            columns.push({data: "facilityKind"});
            columns.push({data: "stationKind"})
            columns.push({data: "stationName"})
            columns.push({data: "dongShortNm"});
            columns.push({data: "address"});
            columns.push({data: "eventStartDt"});
            columns.push({data: "eventEndDt"});
        }
        else if(pEventType === 'dron') {
            columns.push({data: "eventProcStat"});
            columns.push({data: "facilitySeq", className: "alignLeft"});
            columns.push({data: "facilityKind"});
            columns.push({data: "stationKind"})
            columns.push({data: "stationName"})
            columns.push({data: "dongShortNm"});
            columns.push({data: "address"});
            columns.push({data: "eventStartDt"});
            columns.push({data: "eventEndDt"});
        }
        columns.push({data: null});             //상세보기

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
                notice.create($target, pEventType);
                notice.hidePopup();
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
                commonCode.create($target, pEventType);
                commonCode.hidePopup();
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
                commonCode.create($target, pEventType);
                commonCode.hidePopup();
            });
    }
}