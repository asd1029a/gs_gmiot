/*
* 사이니지 관련 JS
*/

const signage = {
    eventHandler : () => {
        $("#addSignageTemplateBtn").on('click', () => {
            signage.showPopup("add");
        });
        $("#modSignageTemplateBtn").on('click', () => {
            signage.showPopup("mod");
        });
        $("#signagePopup .title dd, #signagePopup .bottom li:first-child").on('click', () => {
            signage.hidePopup();
        });
        $("#signageStationPopup .title dd").on('click', () => {
            signage.hideFacilityPopup();
        });

        signage.getList({}, signage.createTemplateList);

        $("#addSignageTemplateProcBtn").on('click', () => {
            if($("#signageTemplateForm").doValidation()) {
                const templateObj = $("#signageTemplateForm").serializeJSON();
                templateObj.templateContent = [];
                for (let i = 0; i < Number(templateObj.templateRowCnt); i++) {
                    templateObj.templateContent.push({});
                }
                templateObj.templateContent = JSON.stringify(templateObj.templateContent);
                delete templateObj.templateRowCnt;
                signage.addProc(templateObj
                    , () => {
                        comm.showAlert("사이니지 템플릿이 등록되었습니다. <br/> 템플릿 레이아웃을 설정해 주십시오.");
                        signage.getList({}, signage.createTemplateList);
                        signage.hidePopup();
                        signage.hideLayout();
                    }
                    , () => {
                        comm.showAlert("사이니지 템플릿 등록에 실패했습니다.");
                    });
            }
        });

        $("#modSignageTemplateProcBtn").on('click', () => {
            if($("#signageTemplateForm").doValidation()) {
                const templateObj = $("#signageTemplateForm").serializeJSON();
                const oriTemplateContent = JSON.parse($('#templateList').find("input:checked").parents('dl').data("templateContent"));
                templateObj.templateContent = [];

                for (let i = 0; i < Number(templateObj.templateRowCnt); i++) {
                    if (oriTemplateContent.length - 1 < i) {
                        templateObj.templateContent.push({});
                    } else {
                        templateObj.templateContent.push(oriTemplateContent[i]);
                    }
                }
                templateObj.templateContent = JSON.stringify(templateObj.templateContent);
                delete templateObj.templateRowCnt;
                templateObj.templateSeq = $('#templateList').find("input:checked").parents('dl').data('templateSeq');

                signage.modProc(templateObj
                    , () => {
                        comm.showAlert("사이니지 템플릿이 수정되었습니다. <br/> 템플릿 레이아웃을 재설정해 주십시오.");
                        signage.getList({}, signage.createTemplateList);
                        signage.hidePopup();
                        signage.hideLayout();
                    }
                    , () => {
                        comm.showAlert("사이니지 템플릿 수정에 실패했습니다.");
                    });
            }
        });

        $("#delSignageTemplateProcBtn").on("click", () => {
            comm.confirm("해당 템플릿을 삭제하시겠습니까?"
                ,{}
            , () => signage.delProc(
                $('#templateList').find("input:checked").parents('dl').data('templateSeq')
                , () => {
                    comm.showAlert("사이니지 템플릿이 삭제되었습니다.");
                    signage.getList({}, signage.createTemplateList);
                    signage.hidePopup();
                    signage.hideLayout();
                }
                , () => {comm.showAlert("사이니지 템플릿 삭제에 실패했습니다.")})
            , () => {return false})
        })

        $("#addSignageLayoutProcBtn").on("click", () => {
            const templateSeq = $('#templateList').find("input:checked").parents('dl').data('templateSeq');
            const templateLayoutList = [];
            let selectNullFlag = false;
            $("#templateArea li select").each((idx, ele) => {
                if($(ele).val() === null) {
                    selectNullFlag = true;
                }
                const obj = {
                    "kind" : $(ele).val()
                    , "value" : $("input[name='"+$(ele).val()+"']").val()
                };
                if(typeof obj.value === "undefined") {
                    obj.value = '';
                }
                templateLayoutList.push(obj);
            });

            if(!selectNullFlag) {
                signage.addLayoutProc({templateSeq : templateSeq, templateContent : JSON.stringify(templateLayoutList)}
                    , () => {
                        comm.showAlert("<br/> 사이니지 레이아웃이 등록되었습니다.");
                        signage.getList({}, signage.createTemplateList);
                        signage.hidePopup();
                        signage.hideLayout();
                    }
                    , () => {
                        comm.showAlert("사이니지 레이아웃 등록에 실패했습니다.");
                    });
            } else {
                comm.showAlert("템플릿 레이아웃 종류를 선택해주십시오.");
            }
        });

        /* 화면 표출 */
        $("#displaySignageBtn").on("click", () => {
            const templateSeq = $('#templateList').find("input:checked").parents('dl').data('templateSeq');
            const templateContent = JSON.parse($('#templateList dl input:checked').parents('dl').data('templateContent'));
            let options = {};

            $.each(templateContent, (idx, obj) => {
                if(obj.kind === "stationList") {
                    const stationList = JSON.parse(obj.value);
                    $.each(stationList, (idx2, stationObj) => {
                        const stationName = stationObj.stationName.replaceAll('&amp;', '&').replace("&#40;", '(').replace("&#41;", ')').replace("\t", '');
                        options[stationObj.stationSeq] = stationName;
                    });
                }
            });

            swal.fire(
                {
                    title: "사이니지 표출 개소를 선택해 주십시오.",
                    input: 'select',
                    inputOptions : options,
                    inputPlaceholder: '개소 선택',
                    showCancelButton: true,
                    confirmButtonColor: '#3a66e5',
                    cancelButtonColor: '#d33',
                    confirmButtonText: '화면 표출',
                    cancelButtonText: '취소',
                    padding : '30px',
                    heightAuto: false,
                    inputValidator: (value) => {
                        if (typeof value !== "undefined" && value !== "") {
                            window.open("/pages/signageDisplay?id=" + templateSeq + "&stationId=" + value, '_blank').focus();
                        }
                    }
                });
        });

        /* 취소 */
        $("#cancelSignageLayoutBtn").on("click", () => {
            signage.hideLayout();
        });

        /* rtsp 영상 개소 선택 */
        $("#addStationBtn").on("click", () => {
            const stationList = $("#signageStationPopup").data("stationList");

            if(stationList.length > 0) {
                $("#templateLayoutForm input[name='stationList']").val(JSON.stringify(stationList));
                comm.showAlert("사이니지 시설물이 선택되었습니다.");
                signage.hideFacilityPopup();
            } else {
                comm.showAlert("사이니지 시설물을 선택해주십시오.");
            }
        });
    }
    , getList : (param, pCallback) => {
        $.ajax({
            url : "/facility/signage/template"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
            , async : false
        }).done((result) => {
            pCallback(result.data);
        });
    }
    , createTemplateList : (result) => {
        const $target = $('#templateList');
        $target.empty();

        result.forEach(each => {
            let content = "";
            let templateContent = JSON.parse(each.templateContent);
            content = "<dl>" +
                "<dt>" +
                "<input type='checkbox' id='template_"+each.templateSeq+"' name=''>" +
                "<label for='template_"+each.templateSeq+"'><span></span>"+each.templateName+"</label>" +
                "</dt>" +
                "<dd>"+each.templateExplain+"<span>("+templateContent.length+"칸)</span></dd>" +
                "</dl>"
            $target.append(content);

            $target.find("dl:last-child").data(each);
        });

        $("#templateList dl").off('click');
        $("#templateList dl").on('click', (e) => {
            e.preventDefault();
            $(".signage_template .article_title ul li").hide();
            $("#templateList dl dt input:checked").prop("checked", false);
            $(e.currentTarget).find("input").prop("checked", true);

            if($(e.currentTarget).find("input").prop("checked")){
                $("#modSignageTemplateBtn").show();
                $(".signage_layout .article_title ul").css("display", "flex");
                signage.createTemplateLayout(JSON.parse($(e.currentTarget).data("templateContent")));
            } else {
                $("#addSignageTemplateBtn").show();
            }
        });
    }
    , createTemplateLayout : obj => {
        const $target = $('#templateArea');
        $target.empty();

        //html 생성
        obj.forEach((each,idx) => {
            let content = signage.createTemplateHtml(each[idx], idx);
            $target.append(content);
        });

        signage.selectOptionHandler();

        //값 부여
        obj.forEach((each,idx) => {
            if(Object.keys(each).length === 0 && each.constructor === Object) {
                return false;
            } else {
                //$('#height_'+idx).val(each.height);
                $('#kind_'+idx).val(each.kind).trigger('change');
                if(each.value !== "" && typeof each.value !== "undefined") {
                    $('#kind_'+idx).siblings("div").find("input[type='text']").val(each.value);
                }
            }
        });
    }
    , selectOptionHandler : () => {
        $("select.contents_kind").on('change', (e) => {
            const targetValue = e.target.value;
            const targetNode = e.target.parentElement;

            let innerTag =
                {
                    'stationList': 'div',
                    'imageFile': 'div',
                    'videoFile': 'div',
                    'airPollution' : 'div'
                };
            let innerHtml =
                {
                    'stationList': '<div class="rtsp_box">' +
                        '<input type="hidden" name="stationList">' +
                        '<a href="#" id="getStationBtn">개소 선택</a>',
                    'imageFile': '<div class="file_box">' +
                        '<input type="text" class="upload_name" name="imageFileName" placeholder="이미지 첨부파일" readonly>' +
                        '<label for="imageFile">파일찾기</label>' +
                        '<input type="file" name="imageFile" id="imageFile">' +
                        '</div>',
                    'videoFile': '<div class="file_box">' +
                        '<input type="text" class="upload_name" name="videoFileName" placeholder="동영상 첨부파일" readonly>' +
                        '<label for="videoFile">파일찾기</label>' +
                        '<input type="file" name="videoFile" id="videoFile">' +
                        '</div>',
                    'airPollution' : '기상청 미세먼지 정보'
                };
            $(targetNode).children().not('p').not('select').remove();

            let tempTag;
            if(targetValue !== "" && typeof targetValue !== "undefined") {
                tempTag = document.createElement(innerTag[targetValue]);
                tempTag.innerHTML = innerHtml[targetValue];
            } else {
                tempTag = '<div>종류를 선택해주십시오.</div>';
            }

            $(targetNode).append(tempTag);

            const $imageFileEle = $("#imageFile");
            const $videoFileEle = $("#videoFile");
            const $stationBtn = $("#getStationBtn");

            $imageFileEle.off('change');
            $imageFileEle.on('change', (e) => {
                const maxSize = 30 * 1024 * 1024 // 30MB
                const fileSize = e.currentTarget.files[0].size
                if( fileSize > maxSize){
                    comm.showAlert("첨부파일의 사이즈는 10MB 이내로 등록 가능합니다.");
                } else {
                    const fileName = $(e.currentTarget).val().split("\\")[$(e.currentTarget).val().split("\\").length-1];
                    $(".upload_name").val(fileName);
                }
            });

            $videoFileEle.off('change');
            $videoFileEle.on('change', (e) => {
                const maxSize = 2000 * 1024 * 1024 // 2GB
                const fileSize = e.currentTarget.files[0].size
                if( fileSize > maxSize){
                    comm.showAlert("첨부파일의 사이즈는 2GB 이내로 등록 가능합니다.");
                } else {
                    const fileName = $(e.currentTarget).val().split("\\")[$(e.currentTarget).val().split("\\").length-1];
                    $(".upload_name").val(fileName);
                }
            });

            $stationBtn.off('click');
            $stationBtn.on('click', () => {
                signage.showFacilityPopup();
                /* 템플릿 개소 데이터 저장 */
                const templateContent = JSON.parse($("#templateList dl input:checked").parents("dl").data("templateContent"));
                let stationList = [];
                $.each(templateContent, (idx, obj)=> {
                    if(obj.kind === "stationList") {
                        stationList = JSON.parse(obj.value);
                    }
                });
                $("#signageStationPopup").data("stationList", stationList);
                signage.createStation();
            });
        });
    }
    , createTemplateHtml : (each, idx) => {
        let content = "";
        content =
            // "<li><p class='input_height'>높이<input type='text' id='height_"+idx+"'></p></li>" +
            "<li></li>" +
            "<li>" +
            "<p>종류<span>레이아웃에 들어갈 컨텐츠를 선택해주세요.</span></p>" +
            "<select class='contents_kind' id='kind_"+idx+"'>" +
            "<option value=''>선택</option>" +
            "<option value='stationList'>rtsp 영상</option>" +
            "<option value='imageFile'>이미지 선택</option>" +
            "<option value='videoFile'>영상 선택</option>" +
            "<option value='airPollution'>미세먼지 정보</option>" +
            "</select>" +
            "<div>종류를 선택해주십시오</div>" +
            "</li>";

        return content;
    }
    , addProc : (pObj, doneCallback, failCallback) => {
        $.ajax({
            url : "/facility/signage/template"
            , type: "PUT"
            , data :  JSON.stringify(pObj)
            , async : false
            , contentType : "application/json; charset=utf-8",
        }).done((result) => {
            doneCallback(result);
        }).fail(() => {
            failCallback();
        });
    }
    , modProc : (pObj, doneCallback, failCallback) => {
        $.ajax({
            url : "/facility/signage/template"
            , type: "PATCH"
            , data :  JSON.stringify(pObj)
            , async : false
            , contentType : "application/json; charset=utf-8",
        }).done((result) => {
            doneCallback(result);
        }).fail(() => {
            failCallback();
        });
    }
    , addLayoutProc : (pObj, doneCallback, failCallback) => {
        const formData = new FormData($("#templateLayoutForm")[0]);
        formData.append("templateSeq", pObj.templateSeq);
        formData.append("templateContent", pObj.templateContent);

        $.ajax({
            url : "/facility/signage/layout"
            , type: "POST"
            , enctype : "multipart/form-data"
            , data :  formData
            , async : false
            , processData: false
            , contentType: false
        }).done((result) => {
            doneCallback(result);
        }).fail(() => {
            failCallback();
        });
    }
    , delProc : (pSeq, doneCallback, failCallback) => {
        $.ajax({
            url : "/facility/signage/template/"+pSeq
            , type: "DELETE"
            , async : false
            , contentType : "application/json; charset=utf-8",
        }).done((result) => {
            doneCallback(result);
        }).fail(() => {
            failCallback();
        });
    }
    , createStation : () => {
        const $target = $('#signageStationTable');
        const set = new Set();

        $("#templateList dl input:not(:checked)").each((idx, ele) => {
            const templateContent = JSON.parse($(ele).parents('dl').data('templateContent'));
            $.each(templateContent, (idx2, obj) => {
                if(obj.kind === "stationList") {
                    const stationList = JSON.parse(obj.value);
                    $.each(stationList, (idx3, stationObj) => {
                        set.add(stationObj.stationSeq);
                    })
                }
            });
        });
        const optionObj = {
            dom: '<"table_body"rt>',
            destroy: true,
            bPaginate: false,
            bServerSide: false,
            scrollY: "calc(100% - 150px)",
            ajax:
                {
                    'url': "/station/signage",
                    'contentType': "application/json; charset=utf-8",
                    'type': "POST",
                    'async': false,
                    'data' : function () {
                        return JSON.stringify({"stationSeqList" : [...set]});
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
                {data: "stationName"},
                {data: "administZoneName"},
                {data: null}
            ]
            , columnDefs: [{
                "targets": -1,
                "data": null,
                "defaultContent": '<span><input type="checkbox"/><label><span></span></label></span>'
            }]
            , fnCreatedRow: (nRow, aaData, iDataIndex) => {
                const stationSeq = aaData.stationSeq;
                const stationList = $("#signageStationPopup").data("stationList");

                $(nRow).find('input').prop('id', "check" + stationSeq);
                $(nRow).find('input').prop('value', stationSeq);
                $(nRow).find('label').prop('for', "check" + stationSeq);

                $.each(stationList, (idx, item) => {
                    if(item.stationSeq === stationSeq) {
                        $(nRow).find('input').prop('checked', true);
                    }
                });
            }
            , excelDownload: false
        }

        const evt = {
            click: function (e) {
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                const stationList = $("#signageStationPopup").data("stationList");
                const stationObj = {stationSeq : rowData.stationSeq , stationName : rowData.stationName};

                if($(e.target).prop("checked") === true) {
                    stationList.push(stationObj);
                } else if($(e.target).prop("checked") === false){
                    let index;
                    $.each(stationList, (idx, obj) => {
                        if(obj.stationSeq === rowData.stationSeq) {
                            index = idx;
                        }
                    })
                    stationList.splice(index, 1);
                }
            }
            , keyup : function() {
                $("#stationKeyword").off("keyup");
                $("#stationKeyword").on("keyup", function (input) {
                    const keyword = $(input.currentTarget).val();
                    if(keyword === "") {
                        $('#signageStationTable tbody tr').show();
                    } else {
                        $target.DataTable().rows().data().each((data, idx)=> {
                            const rowHtml = $target.DataTable().row(idx).node();
                            if(data.stationName.includes(keyword)) {
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
    }
    , showPopup : (type) => {
        const $popup = $('#signagePopup');
        const $form = $("#signageTemplateForm");

        comm.showModal($popup);
        $popup.css('display', 'flex');
        $("#signagePopup .bottom li").hide();
        $("#signagePopup [data-"+type+"]").show();
        $form.initForm();

        if(type === "add") {
            $("#signagePopup .title dt").text("사이니지 템플릿 추가");
        } else {
            $("#signagePopup .title dt").text("사이니지 템플릿 수정");
            const templateData = $("#templateList dl input:checked").parents("dl").data();
            templateData.templateRowCnt = JSON.parse(templateData.templateContent).length;
            $form.setItemValue(templateData);
        }
    }
    , hidePopup : () => {
        const $popup = $("#signagePopup");

        $popup.hide();
        comm.hideModal($popup);
    }
    , showFacilityPopup : () => {
        const $stationPopup = $('#signageStationPopup');

        comm.showModal($stationPopup);
        $stationPopup.css('display', 'flex');
    }
    , hideFacilityPopup : () => {
        const $stationPopup = $('#signageStationPopup');

        comm.hideModal($stationPopup);
        $stationPopup.hide();
    }
    , hideLayout : () => {
        $("#templateArea").empty();
        $(".signage_template .article_title ul li").hide();
        $("#addSignageTemplateBtn").show();
        $("#templateList dl dt input:checked").prop("checked", false);
    }
}