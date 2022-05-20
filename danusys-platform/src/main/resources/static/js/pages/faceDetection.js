const faceDetection = {
    eventHandler: () => {
        $("#searchBtn").on('click', () => {
            faceDetection.create();
        });
        $("#addFaceDetectionBtn").on('click', () => {
            faceDetection.showPopup("add");
        });
        $("#addFaceDetectionProcBtn").on('click', () => {
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
        $("#file").on('change', (e) => {
            const maxSize = 10 * 1024 * 1024 // 10MB
            const file = e.currentTarget.files[0];
            const fileSize = file.size;
            const fileExt = file.type;
            if(fileExt !== "image/jpeg"){
                comm.showAlert("jpg 이미지만 첨부 가능합니다.");
                e.currentTarget.value = null;
            }else if( fileSize > maxSize){
                comm.showAlert("첨부파일의 사이즈는 10MB 이내로 등록 가능합니다.");
                e.currentTarget.value = null;
            } else {
                const fileName = $(e.currentTarget).val().split("\\")[$(e.currentTarget).val().split("\\").length-1];
                $("#faceFile").val(fileName);

                // 이미지 미리보기
                const reader = new FileReader()
                reader.onload = e => {
                    $("#preview").html(`<img src="${e.target.result}" alt="미리보기 이미지" id="previewImg">`);
                }
                reader.readAsDataURL(file)
            }
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
                {data: "faceFile"},
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
                        case 0 :
                            return `남성`;
                            break;
                        case 1 :
                            return `여성`;
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
                            return `검출 대상`;
                            break;
                        case 1 :
                            return `보류`;
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
                    "고유번호|faceSeq"
                    , "이름|faceName"
                    , "종류|faceKindName"
                    , "행정구역|administZoneName"
                ]
            }
        }

        const evt = {
            click: function (e) {
                const $form = $('#faceDetectionForm');
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                if ($(e.target).hasClass('button')) {
                    faceDetection.showPopup("mod");
                    faceDetection.get(rowData.faceSeq, (result) => {
                        $form.data("faceSeq", rowData.faceSeq);
                        $form.setItemValue(result);
                        $("#imgForm").setItemValue(result);
                        if (result.faceFile !== null && result.faceFile !== "" && result.faceFile !== undefined){
                            $("#preview").html(`<img src="/image/${result.faceFile}" alt="미리보기 이미지" id="previewImg">`);
                        }
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
        $("#imgForm").initForm();
        $('#faceDetectionPopup [data-mode]').hide();
        $('#faceDetectionPopup [data-mode="' + type + '"]').show();
        $("#preview").html("");

        if (type === "add") {
            $("#faceDetectionPopup .title dt").text("용의자 · 실종자 추가");
            $("#faceDetectionPopup").data("type", "add");
            $("#faceDetectionPopup [name=faceName]").attr("readonly", false);
        } else if (type === "mod") {
            $("#faceDetectionPopup .title dt").text("용의자 · 실종자 수정");
            $("#faceDetectionPopup").data("type", "mod");
            $("#faceDetectionPopup [name=faceName]").attr("readonly", true);
        }
    },
    hidePopup: () => {
        comm.hideModal($('#faceDetectionPopup'));
        $('#faceDetectionPopup').hide();
    },
    addProc: () => {
        const formData = new FormData();
        const infoForm = $("#faceDetectionForm").serializeJSON();
        const fileForm = new FormData($("#imgForm")[0]);
        Object.entries(infoForm).forEach(item => formData.append(item[0], item[1]));
        for (let key of fileForm.keys()) {
            formData.append(key, fileForm.get(key))
        }

        if ($('#faceDetectionForm').doValidation() && $('#imgForm').doValidation()) {
            $.ajax({
                url: "/faceDetection/checkName/" + infoForm.faceName
                , type: "GET"
            }).done((result) => {
                if(result.count > 0){
                    comm.showAlert("중복된 이름이 있습니다.");
                }else{
                    $.ajax({
                        url: "/faceDetection/add"
                        , type: "POST"
                        , enctype: "multipart/form-data"
                        , processData: false
                        , contentType: false
                        , data: formData
                    }).done((result) => {
                        comm.showAlert("정보가 등록되었습니다");
                        faceDetection.create();
                        faceDetection.hidePopup();
                    }).fail(function(jqXHR, textStatus, errorThrown) {
                        if(typeof(jqXHR.responseJSON) == "undefined") {
                            console.log(jqXHR);
                            comm.showAlert("저장 중 오류가 발생했습니다.");
                        } else {
                            var message = jqXHR.responseJSON.message;
                            comm.showAlert(message);
                            console.log(jqXHR.responseJSON.exception);
                        }
                    });
                }
            }).fail(function(jqXHR, textStatus, errorThrown) {
                if(typeof(jqXHR.responseJSON) == "undefined") {
                    console.log(jqXHR);
                    comm.showAlert("저장 중 오류가 발생했습니다.");
                } else {
                    var message = jqXHR.responseJSON.message;
                    comm.showAlert(message);
                    console.log(jqXHR.responseJSON.exception);
                }
            });
        }
    },
    modProc: (pSeq) => {
        const formData = new FormData();
        const infoForm = $("#faceDetectionForm").serializeJSON();
        const fileForm = new FormData($("#imgForm")[0]);
        infoForm.faceSeq = pSeq;
        Object.entries(infoForm).forEach(item => formData.append(item[0], item[1]));
        for (let key of fileForm.keys()) {
            formData.append(key, fileForm.get(key))
        }

        if ($('#faceDetectionForm').doValidation()) {
            $.ajax({
                url: "/faceDetection/mod/" + pSeq
                , type: "POST"
                , enctype: "multipart/form-data"
                , processData: false
                , contentType: false
                , data: formData
            }).done((result) => {
                comm.showAlert("정보가 수정되었습니다.");
                faceDetection.create();
                faceDetection.hidePopup();
            }).fail(function(jqXHR, textStatus, errorThrown) {
                if(typeof(jqXHR.responseJSON) == "undefined") {
                    console.log(jqXHR);
                    comm.showAlert("저장 중 오류가 발생했습니다.");
                } else {
                    var message = jqXHR.responseJSON.message;
                    comm.showAlert(message);
                    console.log(jqXHR.responseJSON.exception);
                }
            });
        }
    },
    delProc: (pSeq) => {
        $.ajax({
            url: "/faceDetection/" + pSeq
            , type: "DELETE"
        }).done((result) => {
            comm.showAlert("정보가 삭제되었습니다");
            faceDetection.create();
            faceDetection.hidePopup();
        }).fail(function(jqXHR, textStatus, errorThrown) {
            if(typeof(jqXHR.responseJSON) == "undefined") {
                console.log(jqXHR);
                comm.showAlert("저장 중 오류가 발생했습니다.");
            } else {
                var message = jqXHR.responseJSON.message;
                comm.showAlert(message);
                console.log(jqXHR.responseJSON.exception);
            }
        });
    }
}