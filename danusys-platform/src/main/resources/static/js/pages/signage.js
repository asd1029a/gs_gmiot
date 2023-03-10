/*
* 사이니지 관련 JS
*/

const signage = {
    /**
     * 사이니지 통합 이벤트 핸들러
     */
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
            signage.hideStationPopup();
        });

        signage.getList({}, signage.createTemplateList);
        
        // 템플릿 등록 버튼
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
        
        // 템플릿 수정 버튼
        $("#modSignageTemplateProcBtn").on('click', () => {
            const municipality = $(".signage_layout").data('municipality');

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

                let alertMsg = "";
                if(municipality === "gm") {
                    alertMsg = "사이니지 템플릿이 수정되었습니다.";
                } else {
                    alertMsg = "사이니지 템플릿이 수정되었습니다. <br/> 템플릿 레이아웃을 재설정해 주십시오.";
                }
                signage.modProc(templateObj
                    , () => {
                        comm.showAlert(alertMsg);
                        signage.getList({}, signage.createTemplateList);
                        signage.hidePopup();
                        signage.hideLayout();
                    }
                    , () => {
                        comm.showAlert("사이니지 템플릿 수정에 실패했습니다.");
                    });
            }
        });
        
        // 템플릿 삭제 버튼
        $("#delSignageTemplateProcBtn").on("click", () => {
            comm.confirm("해당 템플릿을 삭제하시겠습니까?"
                ,{}
            , () => signage.delProc(
                $('#templateList').find("input:checked").parents('dl').data()
                , () => {
                    comm.showAlert("사이니지 템플릿이 삭제되었습니다.");
                    signage.getList({}, signage.createTemplateList);
                    signage.hidePopup();
                    signage.hideLayout();
                }
                , () => {comm.showAlert("사이니지 템플릿 삭제에 실패했습니다.")})
            , () => {return false})
        })
        
        // 사이니지 레이아웃 적용 (광명)
        $("#applySignageLayoutProcBtn").on("click", () => {
            const templateSeq = $('#templateList').find("input:checked").parents('dl').data('templateSeq');
            let templateContent;
            signage.getTemplateContentJson(
                result => templateContent = result
            , msg => comm.showAlert(msg));
            const templateContentJson = templateContent.templateContentList;
            const notDeleteFileList = templateContent.notDeleteFileList;

            if(templateSeq === "" || typeof templateSeq === "undefined") {
                comm.showAlert("템플릿을 선택 후 적용해주세요.");
            } else {
                signage.addLayoutForGmProc({templateSeq : templateSeq
                        , templateContent : templateContentJson
                        , notDeleteFileList : notDeleteFileList
                        , useYn : "Y"}
                    , () => {
                        comm.showAlert("<br/> 사이니지 레이아웃이 적용되었습니다.");
                        signage.getList({}, signage.createTemplateList);
                        signage.hidePopup();
                        signage.hideLayout();
                    }
                    , () => {
                        comm.showAlert("사이니지 레이아웃 등록에 실패했습니다.");
                    });
            }
        });

        // 사이니지 레이아웃 등록 (영주)
        $("#addSignageLayoutProcBtn").on("click", () => {
            const templateSeq = $('#templateList').find("input:checked").parents('dl').data('templateSeq');
            const templateLayoutList = [];
            let alertMsg = "";
            $("#templateArea li select").each((idx, ele) => {
                const obj = {
                    "kind": $(ele).val()
                };
                if(obj.kind === "imageFile" || obj.kind === "videoFile") {
                    const fileName = $("input[name='" + $(ele).val() + "Name']").val();
                    if(fileName === "" || typeof fileName === "undefined") {
                        alertMsg = "이미지 또는 영상파일이 선택되지 않았습니다.";
                    } else {
                        obj.value = fileName;
                    }
                } else {
                    const value = $("input[name='" + $(ele).val() + "']").val();
                    if(obj.kind === "stationList") {
                        if(value === "" || typeof value === "undefined") {
                            alertMsg = "개소가 선택되지 않았습니다";
                        } else {
                            obj.value = value;
                        }
                    } else {
                        obj.value = value;
                    }
                }
                templateLayoutList.push(obj);
            });

            if(alertMsg === "") {
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
                comm.showAlert(alertMsg);
            }
        });

        // 사이니지 화면 표출 (영주)
        $("#displaySignageBtn").on("click", () => {
            const templateSeq = $('#templateList').find("input:checked").parents('dl').data('templateSeq');
            const templateContent = JSON.parse($('#templateList dl input:checked').parents('dl').data('templateContent'));
            let options = {};

            $.each(templateContent, (idx, obj) => {
                if(obj.kind === "stationList") {
                    if(obj.value !== "") {
                        const stationList = JSON.parse(obj.value);
                        $.each(stationList, (idx2, stationObj) => {
                            options[stationObj.stationSeq] = stringFunc.changeXSSOutputValue(stationObj.stationName);
                        });
                    }
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

        // 사이니지 레이아웃 취소 => 레이아웃 닫기
        $("#cancelSignageLayoutBtn").on("click", (e) => {
            signage.hideLayout();
        });

        // 사이니지 표출 시 개소 선택 (영주)
        $("#addStationBtn").on("click", () => {
            const stationList = $("#signageStationPopup").data("stationList");

            if(stationList.length > 0) {
                $("#templateLayoutForm input[name='stationList']").val(JSON.stringify(stationList));
                comm.showAlert("사이니지 시설물이 선택되었습니다.");
                signage.hideStationPopup();
            } else {
                comm.showAlert("사이니지 시설물을 선택해주십시오.");
            }
        });
    }
    /**
     * 사이니지 템플릿 리스트 조회
     * @param1 : 검색조건
     * @param2 : 조회후 실행 func
     * @result : 조회 데이터 리스트
     */
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
    /**
     * 사이니지 템플릿 리스트 조회
     * @param1 : 템플릿 리스트 데이터
     */
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

        $(".signage_template .article_title .count").text(result.length);

        $("#templateList dl").off('click');
        $("#templateList dl").on('click', (e) => {
            e.preventDefault();
            $(".signage_template .article_title ul li").hide();
            $("#templateList dl dt input:checked").prop("checked", false);
            $(e.currentTarget).find("input").prop("checked", true);

            if($(e.currentTarget).find("input").prop("checked")){
                $("#modSignageTemplateBtn").show();
                $(".signage_layout .article_title ul").css("display", "flex");
                const municipality = $(".signage_layout").data('municipality');
                signage.createTemplateLayout(JSON.parse($(e.currentTarget).data("templateContent")), municipality);
            } else {
                $("#addSignageTemplateBtn").show();
            }
        });
    }
    /**
     * 사이니지 템플릿 레이아웃 UI 생성
     * @param1 : 템플릿의 레이아웃 Obj
     * @param2 : 지자체 구분자 ex) gm, yj...
     */
    , createTemplateLayout: (pObj, pMunicipality) => {
        // 광명 레이아웃 html 생성
        if (pMunicipality === "gm") {
            $('.layout_image_field').empty();

            pObj.forEach((each, idx) => {
                let content = signage.createLayoutHtml(each[idx], idx, pMunicipality);
                $('.layout_image_field').append(content);
                const fileListName = Object.keys(each)[0];
                const fileList = each[fileListName]
                if(typeof fileList !== "undefined" && fileList.length > 0) {
                    const target = fileListName.substring(0, fileListName.indexOf("List"));
                    let fileNameList = [];
                    $.each(fileList, (idx2, obj) => {               // fileList for
                        $.each(Object.keys(obj), (idx3, key) => {   // fileObject key for
                            if(key === "imageFile" || key === "videoFile") {
                                if(fileList.length === (idx2+1)) {
                                    fileNameList.push(obj[key]);
                                    const fileNameListString = stringFunc.changeXSSOutputValue(fileNameList.join(", "));
                                    $("." + target + " .upload_name").val(fileNameListString);
                                } else {
                                    fileNameList.push(obj[key]);
                                }
                            } else {
                                $("." + target + " input[name="+ key +"]").val(obj[key]);
                            }
                        });
                    })
                }
            });
            $("input.input_date").attr("autocomplete", "off");
            $('input[name=startDt]').each((idx, item) => {
                const $startDt = $(item);
                const $endDt = $(item).siblings('[name=endDt]');
                dateFunc.datePickerSet($startDt, $endDt, true, {
                    "position" : "top right"
                });
            });
            signage.fileHandler();
        // 영주시 레이아웃 html 생성
        } else {
            $('#templateArea').empty();
            pObj.forEach((each, idx) => {
                let content = signage.createLayoutHtml(each[idx], idx, pMunicipality);
                $('#templateArea').append(content);
            });
            signage.selectOptionHandler();
            //값 부여
            pObj.forEach((each, idx) => {
                if (Object.keys(each).length === 0 && each.constructor === Object) {
                    return false;
                } else {
                    //$('#height_'+idx).val(each.height);
                    const $selectId = $('#kind_' + idx);
                    $selectId.val(each.kind).trigger('change');
                    if (each.value !== "" && typeof each.value !== "undefined") {
                        if (each.kind === "stationList") {
                            $selectId.siblings("div").find("input[type='hidden']").val(each.value);
                        } else if (each.kind === "imageFile") {
                            const $previewLi = $("#templateArea li:nth-child("+$selectId.parents("li").index()+")");
                            $selectId.siblings("div").find("input[type='text']").val(each.value);
                            $previewLi.css("background", "url(/signageDisplay/getImage?imageFile=" + each.value + ") no-repeat #1a1b1d");
                            $previewLi.css("background-size", "100% 100%");
                        } else {
                            $selectId.siblings("div").find("input[type='text']").val(each.value);
                        }
                    }
                }
            });
        }
    }
    /**
     * 광명시 전용 JSON 형식 생성
     * @param1 : 완료 후 func
     * @param2 : 실패 후 func
     * @result : 레이아웃 데이터 JSON (광명API 형식에 따름)
     */
    , getTemplateContentJson : (pDoneCallback, pFailCallback) => {
        const templateContentList = []; // 수정 JSON
        const notDeleteFileList = []; // 수정시 삭제하면 안되는 파일이름
        let completeFlag = true;
        let returnMsg = "";
        $(".layout_image_field > div:nth-child(odd)").each((idx1, divEle) => { // 좌측 element for
            const templateContent = {}
            const imageListName = $(divEle).prop("class");
            templateContent[imageListName] = [];
            const rowClassName = imageListName.substring(0, imageListName.indexOf("List"));
            $("li[class *="+ rowClassName + "] input[type='file']").each((idx2, inputEle) => { // div - file input for
                const sibling = $("."+ rowClassName);
                const startDt = sibling.find("input[name='startDt']").val();
                const endDt = sibling.find("input[name='endDt']").val();
                const delayTime = sibling.find("input[name='delayTime']").val();
                if(startDt === "" || endDt === "" || delayTime === "") {
                    returnMsg = "기간 또는 순환 시간이 설정되지 않았습니다.";
                    completeFlag = false;
                    return false;
                }
                if(inputEle.files.length === 0) { // input file empty
                    const fileObj = {};
                    if($(inputEle).siblings(".upload_name").val() !== "") {
                        const fileNameAry = $(inputEle).siblings(".upload_name").val().split(", ");
                        $.each(fileNameAry, (idx4, fileName) => { // fileName split list create JSON for
                            if(rowClassName === "topVideo") {
                                fileObj.videoFile = fileName;
                            } else {
                                fileObj.imageFile = fileName;
                            }
                            fileObj.startDt =  startDt;
                            fileObj.endDt =  endDt;
                            fileObj.delayTime =  delayTime;
                            templateContent[imageListName].push(fileObj);
                            notDeleteFileList.push(fileName);
                        });
                    } else {
                        completeFlag = false;
                        return false;
                    }
                } else if(inputEle.files.length > 0) { // input file not empty
                    const formData = new FormData();
                    let fileNameAry;
                    $.each(inputEle.files, (idx3, file) => { // file list append for
                        formData.append("files", file);
                    });
                    signage.uploadImageList(formData
                        , (result) => {
                            fileNameAry = result.fileNames.split(", ");
                        }
                        ,() => {
                            returnMsg = "사진/동영상 업로드에 실패했습니다.";
                            completeFlag = false;
                            return false;
                        });
                    $.each(fileNameAry, (idx4, fileName) => { // file list create JSON for
                        const newFileObj = {};
                        if(rowClassName === "topVideo") {
                            newFileObj.videoFile = fileName;
                        } else {
                            newFileObj.imageFile = fileName;
                        }
                        newFileObj.startDt =  startDt;
                        newFileObj.endDt =  endDt;
                        newFileObj.delayTime =  delayTime;
                        templateContent[imageListName].push(newFileObj);
                    });
                }
            });
            templateContentList.push(templateContent);
        });
        if(completeFlag) {
            pDoneCallback({"templateContentList" : JSON.stringify(templateContentList), "notDeleteFileList" : notDeleteFileList});
        } else {
            pFailCallback(returnMsg);
        }
    }
    /**
     * 레이아웃 input file handler (광명)
     */
    , fileHandler : () => {
        const $imageFileEle = $(".layout_image_field input[type='file']");

        $imageFileEle.off('change');
        $imageFileEle.on('change', (e) => {
            const maxSize = 1000 * 1024 * 1024 // 30MB
            const nameAry = [];

            $.each(e.currentTarget.files, (idx, file) => {
                const fileSize = e.currentTarget.files[idx].size
                if( fileSize > maxSize){
                    comm.showAlert("첨부파일의 사이즈는 1GB 이내로 등록 가능합니다.");
                } else {
                    const fileName = file.name;
                    nameAry.push(fileName);
                }
            });
            $(e.currentTarget).siblings(".upload_name").val(nameAry.join(", "));
        });
    }
    /**
     * 레이아웃 selectbox handler (영주)
     */
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
                        '<input type="text" class="upload_name" name="imageFileName" accept="image/*" placeholder="이미지 첨부파일" readonly>' +
                        '<label for="imageFile">파일찾기</label>' +
                        '<input type="file" name="imageFile" id="imageFile">' +
                        '</div>',
                    'videoFile': '<div class="file_box">' +
                        '<input type="text" class="upload_name" name="videoFileName" accept="video/*" placeholder="동영상 첨부파일" readonly>' +
                        '<label for="videoFile">파일찾기</label>' +
                        '<input type="file" name="videoFile" id="videoFile">' +
                        '</div>',
                    'airPollution' : '기상청 미세먼지 정보'
                };
            $(targetNode).children().not('p').not('select').remove();

            let tempTag;
            const $previewLi = $("#templateArea li:nth-child("+$(targetNode).index()+")");
            if(targetValue !== "" && typeof targetValue !== "undefined") {
                $previewLi.css("background", "#1a1b1d");
                tempTag = document.createElement(innerTag[targetValue]);
                tempTag.innerHTML = innerHtml[targetValue];
                if(targetValue === "stationList" || targetValue === "videoFile") {
                    $previewLi.css("background", "url(../../images/default/videoEx.png) no-repeat center #1a1b1d");
                } else if(targetValue === "airPollution") {
                    $previewLi.css("background", "url(../../images/default/airPollutionEx.png) no-repeat center #1a1b1d");
                }
                $previewLi.css("background-size", "100% 100%");
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
                    comm.showAlert("첨부파일의 사이즈는 30MB 이내로 등록 가능합니다.");
                } else {
                    const fileName = $(e.currentTarget).val().split("\\")[$(e.currentTarget).val().split("\\").length-1];
                    $(".upload_name").val(fileName);
                    const reader = new FileReader();
                    reader.onload = (r) => {
                        $previewLi.css("background", "url("+r.target.result+") no-repeat #1a1b1d");
                        $previewLi.css("background-size", "100% 100%");
                    }
                    reader.readAsDataURL(e.currentTarget.files[0]);
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
                signage.showStationPopup();
                /* 템플릿 개소 데이터 저장 */
                const templateContent = JSON.parse($("#templateList dl input:checked").parents("dl").data("templateContent"));
                let stationList = [];
                $.each(templateContent, (idx, obj)=> {
                    if(obj.kind === "stationList" && obj.value !== "" &&
                        typeof obj.value !== "undefined") {
                        stationList = JSON.parse(obj.value);
                    }
                });
                $("#signageStationPopup").data("stationList", stationList);
                signage.createStation();
            });
        });
    }
    /**
     * 사이니지 레이아웃 UI 생성
     * @param1 : 레이아웃 칸 별 데이터 
     * @param2 : 레이아웃 칸 별 인덱스
     * @param3 : 지자체 이니셜 ex) gm, yj...
     * @result : 레이아웃 html
     */
    , createLayoutHtml : (each, idx, pMunicipality) => {
        let content = "";
        if(pMunicipality === "gm") {
            const htmlObj = {
                "0" : {
                    "name" : "top"
                    , "value" : "상단"
                }
                , "1" : {
                    "name" : "bottom1"
                    , "value" : "하단1"
                }
                , "2" : {
                    "name" : "bottom2"
                    , "value" : "하단2"
                }
            }

            if(idx === 0) { // input video 타입
                content =
                    "<div class='"+ htmlObj[idx].name + "VideoList'>" +
                    "<h6>종류<span>" + htmlObj[idx].value + " 동영상를 설정해주세요. (영문명 파일만 가능)</span></h6>" +
                    "<ul>" +
                    "<li class='"+ htmlObj[idx].name + "Video'>" +
                    "<div class='file_box'>" +
                    "<input type='text' class='upload_name' name='topVideoFileName' placeholder='동영상 첨부파일' readonly>" +
                    "<label for='topVideoFile'>파일찾기</label>" +
                    "<input type='file' name='videoFile' accept='video/*' id='topVideoFile' multiple>" +
                    "</div>" +
                    "</li>" +
                    "</ul>" +
                    "</div>" +
                    "<div>" +
                    "<h6>기간<span>동영상 표출 기간을 설정해주세요.</span></h6>" +
                    "<ul class='date'>" +
                    "<li class='"+ htmlObj[idx].name + "Video'>" +
                    "<input type='text' placeholder='날짜를 입력하세요' name='startDt' class='input_date'>" +
                    "<input type='text' placeholder='날짜를 입력하세요' name='endDt' class='input_date'>" +
                    "</li>" +
                    "</div>";
            } else if (idx === 1) { // input image 타입
                content =
                    "<div class='"+ htmlObj[idx].name + "ImageList'>" +
                    "<h6>종류<span>" + htmlObj[idx].value + " 이미지를 설정해주세요. (영문명 파일만 가능)</span></h6>" +
                    "<ul>" +
                    "<li class='"+ htmlObj[idx].name + "Image'>" +
                    "<div class='file_box'>" +
                    "<input type='text' class='upload_name' name='"+ htmlObj[idx].name + "ImageFileName' placeholder='이미지 첨부파일' readonly>" +
                    "<label for='"+ htmlObj[idx].name + "ImageFile'>파일찾기</label>" +
                    "<input type='file' name='imageFile' accept='image/*, .pdf' id='"+ htmlObj[idx].name + "ImageFile' multiple>" +
                    "</div>" +
                    "</li>" +
                    "</ul>" +
                    "</div>" +
                    "<div>" +
                    "<h6>기간<span>이미지 표출 기간과 순환 시간을 설정해주세요.</span></h6>" +
                    "<ul class='date'>" +
                    "<li class='"+ htmlObj[idx].name + "Image'>" +
                    "<input type='text' placeholder='날짜를 입력하세요' name='startDt' class='input_date'>" +
                    "<input type='text' placeholder='날짜를 입력하세요' name='endDt' class='input_date'>" +
                    "<span>순환 시간</span>" +
                    "<input type='number' class='sec' name='delayTime' value='30' placeholder='30'><span>초</span>" +
                    "</li>" +
                    "</div>";
            } else {
                content =
                    "<div class='"+ htmlObj[idx].name + "ImageList'>" +
                    "<h6>종류<span>" + htmlObj[idx].value + " 이미지를 설정해주세요. (영문명 파일만 가능)</span></h6>" +
                    "<ul>" +
                    "<li class='"+ htmlObj[idx].name + "Image'>" +
                    "<div class='file_box'>" +
                    "<input type='text' class='upload_name' name='"+ htmlObj[idx].name + "ImageFileName' placeholder='이미지 첨부파일' readonly>" +
                    "<label for='"+ htmlObj[idx].name + "ImageFile'>파일찾기</label>" +
                    "<input type='file' name='imageFile' accept='image/*, .pdf, text/plain' id='"+ htmlObj[idx].name + "ImageFile' multiple>" +
                    "</div>" +
                    "</li>" +
                    "</ul>" +
                    "</div>" +
                    "<div>" +
                    "<h6>기간<span>이미지 표출 기간과 순환 시간을 설정해주세요.</span></h6>" +
                    "<ul class='date'>" +
                    "<li class='"+ htmlObj[idx].name + "Image'>" +
                    "<input type='text' placeholder='날짜를 입력하세요' name='startDt' class='input_date'>" +
                    "<input type='text' placeholder='날짜를 입력하세요' name='endDt' class='input_date'>" +
                    "<span>순환 시간</span>" +
                    "<input type='number' class='sec' name='delayTime' value='30' placeholder='30'><span>초</span>" +
                    "</li>" +
                    "</div>";
            }
        } else {
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
        }
        return content;
    }
    /**
     * @param1 : 템플릿 추가 데이터
     * @param2 : 추가 후 func
     * @param3 : 추가 실패 후 func
     */
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
    /**
     * @param1 : 템플릿 수정 데이터
     * @param2 : 수정 후 func
     * @param3 : 수정 실패 후 func
     */
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
    /**
     * @param1 : 레이아웃 추가 데이터
     * @param2 : 추가 후 func
     * @param3 : 추가 실패 후 func
     */
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
    /**
     * @param1 : 레이아웃 추가 데이터(광명)
     * @param2 : 추가 후 func
     * @param3 : 추가 실패 후 func
     */
    , addLayoutForGmProc : (pObj, doneCallback, failCallback) => {
        $.ajax({
            url : "/facility/signage/layoutForGm"
            , type: "PUT"
            , data : JSON.stringify(pObj)
            , async : false
            , contentType : "application/json; charset=utf-8",
        }).done((result) => {
            doneCallback(result);
        }).fail(() => {
            failCallback();
        });
    }
    /**
     * @param1 : FormData 형식 미디어 Obj
     * @param2 : 추가 후 func
     * @param3 : 추가 실패 후 func
     */
    , uploadImageList : (pFormData, doneCallback, failCallback) => {
        $.ajax({
            url : "/facility/signage/uploadImageList"
            , type: "POST"
            , enctype : "multipart/form-data"
            , data :  pFormData
            , async : false
            , processData: false
            , contentType: false
        }).done((result) => {
            doneCallback(result);
        }).fail(() => {
            failCallback();
        });
    }
    /**
     * @param1 : 템플릿 삭제 데이터
     * @param2 : 추가 후 func
     * @param3 : 추가 실패 후 func
     */
    , delProc : (pObj, doneCallback, failCallback) => {
        $.ajax({
            url : "/facility/signage/template"
            , type : "DELETE"
            , data : JSON.stringify(pObj)
            , async : false
            , contentType : "application/json; charset=utf-8",
        }).done((result) => {
            doneCallback(result);
        }).fail(() => {
            failCallback();
        });
    }
    /**
     * 개소 선택 팝업 내 개소 테이블 생성(영주)
     */
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
    /**
    * 템플릿 팝업 show
    * @param1 : 팝업 종류 ex) add, mod...
    */
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
    /**
     * 템플릿 팝업 hide
     */
    , hidePopup : () => {
        const $popup = $("#signagePopup");

        $popup.hide();
        comm.hideModal($popup);
    }
    /**
     * 개소 선택 팝업 show
     */
    , showStationPopup : () => {
        const $stationPopup = $('#signageStationPopup');

        comm.showModal($stationPopup);
        $stationPopup.css('display', 'flex');
    }
    /**
     * 개소 선택 팝업 hide
     */
    , hideStationPopup : () => {
        const $stationPopup = $('#signageStationPopup');

        comm.hideModal($stationPopup);
        $stationPopup.hide();
    }
    /**
     * 레이아웃 초기화
     */
    , hideLayout : () => {
        const municipality = $(".signage_layout").data("municipality");
        $(".signage_template .article_title ul li").hide();
        $("#addSignageTemplateBtn").show();
        $("#templateList dl dt input:checked").prop("checked", false);
        if(municipality === "gm") {
            $(".layout_image_field").empty();
        } else {
            $("#templateArea").empty();
        }
    }
}