/**
 * 공지사항
 */

const notice = {
    eventHandler : () => {

    }
    , create : () => {
        const $target = $('#noticeTable');

        const optionObj = {
            dom: '<"tableBody"rt><"tableBottom"p>',
            destroy: true,
            pageLength: 15, //$("#noticeListCntSel").val(),
            scrollY: "calc(100% - 45px)",
            ajax :
                {
                    'url' : "/notice",
                    'contentType' : "application/json; charset=utf-8",
                    'type' : "POST",
                    'data' : function ( d ) {
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
                {data: "noticeTitle", className: "alignLeft"},
                {data: "noticeContent", className: "alignLeft"},
                {data: "insertUserSeq"},
                {data: "insertDt"},
                {data: "noticeFile"},
                {data: null}
            ],
            "columnDefs": [
                {
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
                }
                , {
                    targets: 4,
                    createdCell: function (td, cellData) {
                        if ( cellData !== null ) {
                            $(td).append("<i><img src='/images/default/clipboard.svg'></i>");
                        } else {
                            $(td).text("없음");
                        }
                    }
                }]
            , excelDownload : true
        }

        const evt = {
            click : function(e) {
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                if($(e.target).hasClass('button')) {
                    //notice.showPopup('mod');
                    //$('#noticeForm').setItemValue(rowData);
                    notice.get(rowData.noticeSeq ,(result) => console.log(result));
                }
            }
        }
        comm.createTable($target ,optionObj, evt);
    },
    getList : (param, pCallback) => {
        $.ajax({
            url : "/notice"
            , type : "POST"
            , data : param
        }).done((result) => {
            pCallback(result);
        });
    },
    get : (pSeq, pCallback) => {
        $.ajax({
            url : "/notice/" + pSeq
            , type : "GET"
        }).done((result) => {
            pCallback(result);
        });
    },
    showPopup : (type) => {
        $('#noticePopup .popupContents').scrollTop(0);
        comm.showModal($('#noticePopup'));
        $('#noticePopup').css("display", "flex");
        $('#noticeForm').initForm();
        $('#noticePopup [data-mode]').hide();
        if(type === "add") {
            $('#noticePopup .popupTitle h4').text("공지사항 게시글 등록");
            $('#noticePopup').css('height', '480px');
            $('#noticePopup [data-mode="'+type+'"]').show();
        } else if(type === "mod") {
            $('#noticePopup .popupTitle h4').text('공지사항 게시글 수정');
            $('#noticePopup').css('height', '780px');
            $('#noticePopup [data-mode="'+type+'"]').show();
        }
    },
    hidePopup : () => {
        $('#noticePopup .popupContents').scrollTop(0);
        comm.hideModal($('#noticePopup'));
        $('#noticePopup').hide();
    },
    addProc : (pSeq) => {
        const formObj = $('#noticeForm').serializeJSON();

        $.ajax({
            url : "/notice"
            , type: "PUT"
            , data : formObj
        }).done((result) => {
            comm.showAlert("공지사항이 등록되었습니다");
            notice.create($('#noticeTable'));
            notice.hidePopup();
        });
    },
    modProc : (pSeq) => {
        const formObj = $('#noticeForm').serializeJSON();

        $.ajax({
            url : "/notice"
            , type: "PATCH"
            , data : formObj
        }).done((result) => {
            comm.showAlert("공지사항이 수정되었습니다");
            notice.create($('#noticeTable'));
            notice.hidePopup();
        });
    },
    delProc : (pSeq) => {
        const formObj = $('#noticeForm').serializeJSON();

        $.ajax({
            url : "/notice/del/"+pSeq
            , type: "DELETE"
            , data : formObj
        }).done((result) => {
            comm.showAlert("공지사항이 삭제되었습니다");
            notice.create($('#noticeTable'));
            notice.hidePopup();
        });
    }
}