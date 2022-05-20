/**
 * 공지사항
 */
const notice = {
    eventHandler : () => {
        $("#searchBtn").on('click', () => {
            comm.checkAuthority("/user/check/authority", "config", "rw")
                .then(
                    (result) => {
                        notice.create(result);
                    }
                );
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
    , create : (pPermit) => {
        const $target = $('#noticeTable');
        const optionObj = {
            dom: '<"table_body"rt><"table_bottom"p>',
            destroy: true,
            pageLength: 15,
            scrollY: "calc(100% - 40px)",
            security : true,
            autoWidth: true,
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
                toggleable: false,
                style: "single"
            },
            columns : [
                {data: "noticeTitle", className: "alignLeft"},
                {data: "noticeContent", className: "alignLeft"},
                {data: "insertUserSeq"},
                {data: "insertDt"},
                {data: "noticeFile"},
                {data: null}
            ],
            columnDefs: [{
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
                }
                , {
                    "targets": 5,
                    "data": null,
                    "defaultContent": '<span class="button detail">상세보기</span>'
                }
            ]
            , excelDownload : {
                url : "/notice/excel/download"
                , fileName : "공지사항 목록_"+ dateFunc.getCurrentDateYyyyMmDd(0, '') +".xlsx"
                , search : $("#searchForm form").serializeJSON()
                , headerList : ["고유번호|noticeSeq"
                    , "제목|noticeTitle"
                    , "내용|noticeContent"
                    , "파일이름|noticeFile"
                    , "작성자|insertUserSeq"
                    , "작성일|insertDt"
                    , "수정자|updateUserSeq"
                    , "수정일|updateDt"]
            }
        }
        if(pPermit !== "none") {
            optionObj.columnDefs.push({
                "targets": 6,
                "data": null,
                "defaultContent": '<span class="button mod">수정</span>'
            });
        }
        const evt = {
            click : function(e) {
                const $form = $('#noticeForm');
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                if($(e.target).hasClass('button')) {
                    const buttonType = $(e.target).attr("class").split(' ')[1];
                    notice.showPopup(buttonType);
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
        const fileHtml = '<label For="file">파일찾기</label>'
        + '<input type="file" name="file" id="file">';
        const downloadBtnHtml = '<span class="download_btn">내려받기</span>'

        comm.showModal($('#noticePopup'));
        $('#noticePopup').css("display", "flex");
        $('#noticeForm').initForm();
        $('#noticePopup [data-add], [data-mod], [data-detail]').hide();
        $('#noticePopup [data-'+type+'="true"]').show();
        $("#noticePopup #file, [for='file'], .download_btn").remove();
        if(type === "add") {
            $('#noticePopup .title dt').text("공지사항 등록");
            $('#noticePopup .fileBox').append(fileHtml);
        } else if(type === "mod") {
            $('#noticePopup .title dt').text('공지사항 수정');
            $('#noticePopup .fileBox').append(fileHtml);
        } else if(type === "detail") {
            $('#noticePopup .title dt').text('공지사항 상세');
            $('#noticePopup .fileBox').append(downloadBtnHtml);
            $("#noticePopup .download_btn").on('click', (e) => {
                const fileName = $("#noticeFile").val();
                if(fileName !== '') {
                    notice.fileDownLoad(fileName);
                } else {
                    comm.showAlert("업로드된 파일이 없습니다.");
                }
            });
        }
        $("#file").on('change', (e) => {
            const maxSize = 10 * 1024 * 1024 // 10MB
            const fileSize = e.currentTarget.files[0].size
            if( fileSize > maxSize){
                comm.showAlert("첨부파일의 사이즈는 10MB 이내로 등록 가능합니다.");
                e.currentTarget.value = null;
            } else {
                const fileName = $(e.currentTarget).val().split("\\")[$(e.currentTarget).val().split("\\").length-1];
                $("#noticeFile").val(fileName);
            }
        });
    },
    hidePopup : () => {
        comm.hideModal($('#noticePopup'));
        $('#noticePopup').hide();
    },
    fileDownLoad : (fileName) => {
        const encFileName = encodeURI(fileName);
        window.location = '/notice/download/'+encFileName;
    },
    addProc : () => {
        const formData = new FormData($("#noticeForm")[0]);

        if($('#noticeForm').doValidation()) {
            $.ajax({
                url : "/notice/add"
                , type: "POST"
                , enctype : "multipart/form-data"
                , processData: false
                , contentType: false
                , data : formData
            }).done(() => {
                comm.showAlert("공지사항이 등록되었습니다");
                notice.create($('#noticeTable'));
                notice.hidePopup();
            }).fail(() => {
                comm.showAlert("공지사항 등록에 실패했습니다.");
            });
        } else {
            return false;
        }
    },
    modProc : (pSeq) => {
        const formData = new FormData($("#noticeForm")[0]);

        if($('#noticeForm').doValidation()) {
            $.ajax({
                url : "/notice/mod/"+pSeq
                , type: "POST"
                , enctype : "multipart/form-data"
                , processData: false
                , contentType: false
                , data : formData
            }).done((result) => {
                comm.showAlert("공지사항이 수정되었습니다");
                notice.create($('#noticeTable'));
                notice.hidePopup();
            }).fail(() => {
                comm.showAlert("공지사항 수정에 실패했습니다.");
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