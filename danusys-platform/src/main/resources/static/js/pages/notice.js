/**
 * 공지사항
 */
const notice = {
    eventHandler : () => {
        $("#searchBtn").on('click', () => {
            notice.create();
        });
        $("#addNoticeProcBtn").on('click', () => {
           notice.addProc();
        });
        $("#modNoticeProcBtn").on('click', () => {
            notice.modProc($("#noticeForm").data("noticeSeq"));
        });
        $("#delNoticeProcBtn").on('click', () => {
            notice.delProc($("#noticeForm").data("noticeSeq"));
        });
        $("#noticePopup .title dd").on('click', () => {
            notice.hidePopup();
        });
        $("#addNoticeBtn").on('click', () => {
            notice.showPopup("add");
        });
    }
    , create : () => {
        const $target = $('#noticeTable');

        const optionObj = {
            dom: '<"table_body"rt><"table_bottom"p>',
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
                            $(td).text("");
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
                const $form = $('#noticeForm');
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                if($(e.target).hasClass('button')) {
                    notice.showPopup('mod');
                    $('#noticeForm').setItemValue(rowData);
                    notice.get(rowData.noticeSeq ,(result) => {
                        $form.data("noticeSeq", rowData.noticeSeq);
                        $form.setItemValue(result);
                    });
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
        comm.showModal($('#noticePopup'));
        $('#noticePopup').css("display", "flex");
        $('#noticeForm').initForm();
        $('#noticePopup [data-mode]').hide();
        if(type === "add") {
            $('#noticePopup .title dt').text("공지사항 등록");
            $('#noticePopup [data-mode="'+type+'"]').show();
        } else if(type === "mod") {
            $('#noticePopup .title dt').text('공지사항 수정');
            $('#noticePopup [data-mode="'+type+'"]').show();
        }
    },
    hidePopup : () => {
        comm.hideModal($('#noticePopup'));
        $('#noticePopup').hide();
    },
    addProc : () => {
        const formObj = $('#noticeForm').serializeJSON();

        if($('#noticeForm').doValidation()) {
            $.ajax({
                url : "/notice"
                , type: "PUT"
                , contentType : "application/json; charset=utf-8"
                , data : JSON.stringify(formObj)
            }).done((result) => {
                comm.showAlert("공지사항이 등록되었습니다");
                notice.create($('#noticeTable'));
                notice.hidePopup();
            });
        } else {
            return false;
        }
    },
    modProc : (pSeq) => {
        const formObj = $('#noticeForm').serializeJSON();
        formObj.noticeSeq = pSeq;

        if($('#noticeForm').doValidation()) {
            $.ajax({
                url: "/notice"
                , type: "PATCH"
                , contentType: "application/json; charset=utf-8"
                , data: JSON.stringify(formObj)
            }).done((result) => {
                comm.showAlert("공지사항이 수정되었습니다");
                notice.create($('#noticeTable'));
                notice.hidePopup();
            });
        } else {
            return false;
        }
    },
    delProc : (pSeq) => {
        comm.confirm(
            "공지사항을 삭제하시겠습니까?"
        , {}
        , () => {
                $.ajax({
                    url : "/notice/"+pSeq
                    , type: "DELETE"
                }).done((result) => {
                    comm.showAlert("공지사항이 삭제되었습니다");
                    notice.create($('#noticeTable'));
                    notice.hidePopup();
                });
            }
        ,() => {
            return false;
        })
    }
}