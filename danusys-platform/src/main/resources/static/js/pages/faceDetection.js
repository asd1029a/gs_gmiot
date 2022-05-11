const faceDetection = {
    eventHandler: () => {
        $("#searchBtn").on('click', () => {
            faceDetection.create();
        });
        $("#addFaceDetectionBtn").on('click', () => {
            faceDetection.showPopup("add");
        });
        $("#addUFaceDetectionProcBtn").on('click', () => {
            faceDetection.addProc();
        });
        $("#modFaceDetectionProcBtn").on('click', () => {
            faceDetection.modProc($("#faceDetectionForm").data("faceSeq"));
        });
        $("#delFaceDetectionProcBtn").on('click', () => {
            faceDetection.delProc($("#faceDetectionForm").data("faceSeq"));
        });
        $("#faceDetectionPopup .title dd").on('click', () => {
            faceDetection.hidePopup();
        });
    }
    , create: () => {
        const $target = $('#faceDetectionTable');

        const optionObj = {
            dom: '<"table_body"rt><"table_bottom"p>',
            destroy: true,
            pageLength: 15,
            scrollY: "calc(100% - 40px)",
            ajax:
                {
                    'url': "/faceDetection/paging",
                    'contentType': "application/json; charset=utf-8",
                    'type': "POST",
                    'data': function (d) {
                        const param = $.extend({}, d, $("#searchForm form").serializeJSON());
                        return JSON.stringify(param);
                    },
                    'dataSrc': function (result) {
                        $('.title dd .count').text(result.recordsTotal);
                        return result.data;
                    }
                },
            select: {
                toggleable: false,
                style: "single"
            },
            columns: [
                {data: "faceSeq", class: "alignLeft"},
                {data: "faceName"},
                {data: "faceAge"},
                {data: "faceGender"},
                {data: "faceKind"},
                {data: "faceImgPath"},
                {data: "faceSimilarity"},
                {data: "faceStatus"},
                {data: "faceUid"},
                {data: null}
            ],
            columnDefs: [{
                "targets": -1,
                "data": null,
                "defaultContent": '<span class="button">상세보기</span>'
            }, {
                "targets": 3,
                "data": null,
                "render": function (data, type, row) {
                    switch (row.faceGender) {
                        case "F" :
                            return `여성`;
                            break;
                        case "M" :
                            return `남성`;
                            break;
                        default:
                            "";
                    }
                }
            }, {
                "targets": 4,
                "data": null,
                "render": function (data, type, row) {
                    switch (row.faceKind) {
                        case "suspect" :
                            return `범죄 용의자`;
                            break;
                        case "missing" :
                            return `실종자`;
                            break;
                        default:
                            "";
                    }
                }
            }, {
                "targets": 7,
                "data": null,
                "render": function (data, type, row) {
                    switch (row.faceStatus) {
                        case 0 :
                            return `검출 전`;
                            break;
                        case 1 :
                            return `검출 완료`;
                            break;
                        default:
                            "";
                    }
                }
            }]
            , excelDownload: {
                url: "/faceDetection/excel/download"
                , fileName: "검출 대상 목록_" + dateFunc.getCurrentDateYyyyMmDd(0, '') + ".xlsx"
                , search: $("#searchForm form").serializeJSON()
                , headerList: [
                    "고유번호|faceDetectionSeq"
                    , "이름|faceDetectionName"
                    , "종류|faceDetectionKindName"
                    , "행정구역|administZoneName"
                ]
            }
        }

        const evt = {
            click : function(e) {
                const $form = $('#faceDetectionForm');
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                if($(e.target).hasClass('button')) {
                    const buttonType = $(e.target).attr("class").split(' ')[1];
                    faceDetection.showPopup(buttonType);
                    faceDetection.get(rowData.faceSeq ,(result) => {
                        $form.data("faceSeq", rowData.faceSeq);
                        $form.setItemValue(result);
                    });
                }
            }
        }
        comm.createTable($target, optionObj, evt);
    },
    getList: (pCallback) => {
        comm.ajaxPost({
            url: "/faceDetection"
            , data: {}
        }, (result) => {
            pCallback(result);
        });
    },
    get: (pSeq, pCallback) => {
        $.ajax({
            url: "/faceDetection/" + pSeq
            , type: "GET"
        }).done((result) => {
            pCallback(result);
        });
    },
    showPopup: (type) => {
        comm.showModal($("#faceDetectionPopup"));
        $("#faceDetectionPopup").css('display', 'flex');
        $("#faceDetectionForm").initForm();
        $('#faceDetectionPopup [data-add], [data-mod]').hide();
        $('#faceDetectionPopup [data-'+type+'="true"]').show();
        $("#faceDetectionPopup").data("facilitySeqList", []);

        if(type === "add") {
            $("#faceDetectionPopup .title dt").text("용의자 · 실종자 추가");
            $("#faceDetectionPopup").data("type", "add");
        } else if(type === "mod"){
            $("#faceDetectionPopup .title dt").text("용의자 · 실종자 수정");
            $("#faceDetectionPopup").data("type", "mod");
        }
    },
    hidePopup: () => {
        comm.hideModal($('#faceDetectionPopup'));
        $('#faceDetectionPopup').hide();
    },
    addProc: () => {
        const formObj = $('#faceDetectionForm').serializeJSON();

        $.ajax({
            url : "/faceDetection"
            , type: "PUT"
            , data : JSON.stringify(formObj)
            , contentType : "application/json; charset=utf-8"
        }).done((result) => {
            comm.showAlert("정보가 등록되었습니다");
            faceDetection.create();
            faceDetection.hidePopup();
        });
    },
    modProc: (pSeq) => {
        const formObj = $('#faceDetectionForm').serializeJSON();
        formObj.faceDetectionSeq = pSeq;

        $.ajax({
            url : "/faceDetection"
            , type: "PATCH"
            , data : JSON.stringify(formObj)
            , contentType : "application/json; charset=utf-8"
        }).done((result) => {
            comm.showAlert("정보가 수정되었습니다.");
            faceDetection.create();
            faceDetection.hidePopup();
        });
    },
    delProc: (pSeq) => {
        $.ajax({
            url: "/faceDetection/" + pSeq
            , type: "DELETE"
        }).done((result) => {
            comm.showAlert("정보가 삭제되었습니다");
            faceDetection.create();
            faceDetection.hidePopup();
        });
    }
}