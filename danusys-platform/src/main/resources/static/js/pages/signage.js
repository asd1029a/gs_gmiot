/*
* 사이니지 관련 JS
*/

const signage = {
    eventHandler : () => {
        /*$("#searchBtn").on('click', () => {
            comm.checkAuthority("/user/check/authority", "config", "rw")
                .then(
                    (result) => {
                        signage.createList(result);
                    }
                );
        });*/
        //signage.createList();
        $("#addSignageTemplateBtn").on('click', () => {
            signage.showPopup();
        });
        $("#signagePopup .title dd, #signagePopup .bottom li:first-child").on('click', () => {
            signage.hidePopup();
        });

        $("#addTemplateLayout").on('click', (e) => {
            const tit = $("#templateTitle").val();
            const cnt = Number($("#templateCnt").val());

            signage.hidePopup();

            const $target = $('#templateArea');
            $target.empty();

            for (let i = 0; i < cnt; i++){
                let content = signage.createTemplateHtml([],i);
                $target.append(content);
            }
            signage.selectHandler();
        });

        signage.getList({},signage.createTemplateList);

        $("#templateList dl").on('click', () => {
            signage.selectTemplate($('#templateList').find("dl").data()[0]);
        });

        $("#addSignage").on('click', () => {
            const tit = $("#templateTitle").val();
            const cnt = Number($("#templateCnt").val());
            signage.addProc({}
                , () => {
                    comm.showAlert("등록되었습니다.");
                    signage.getList({},signage.createTemplateList);
                }
                , () => {
                    comm.showAlert("등록에 실패했습니다.");
                });
        });
    }
    , create :() => {

    }
    , createList : (pPermit) => {

    }
    , getList : (param, pCallback) => {
        $.ajax({
            url : "/facility/signageTemplate"
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

            $target.find("dl:last-child").data([templateContent]);

        });
    }
    , selectTemplate : obj => {
        const $target = $('#templateArea');
        $target.empty();

        //html 생성
        obj.forEach((each,idx) => {
            let content = signage.createTemplateHtml(each[idx],idx);
            $target.append(content);
        });

        signage.selectHandler();

        //값 부여
        obj.forEach((each,idx) => {
            $('#height_'+idx).val(each.height);
            $('#kind_'+idx).val(each.kind).trigger('change');
            //todo. 파일일경우, url text일 경우
            //$('#val_'+idx).val(each.value);
        });
    }
    , selectHandler : () => {
        $("select.contents_kind").on('change', (e) => {
            const targetValue = e.target.value;
            const targetNode = e.target.parentElement;

            let innerTag =
                {
                    'urlLink': 'input',
                    'fileSystem': 'div'
                };
            let innerHtml =
                {
                    'urlLink': '<input type="text">',
                    'fileSystem': '<div class="fileBox">\n' +
                        '<input class="uploadName" value="첨부파일" placeholder="첨부파일">\n' +
                        '<label for="file">파일찾기</label>\n' +
                        '<input type="file" id="file">\n' +
                        '</div>'
                };

            $(targetNode).children().not('p').not('select').remove();

            let tempTag = document.createElement(innerTag[targetValue]);
            tempTag.innerHTML = innerHtml[targetValue];
            targetNode.append(tempTag);
        });
    }
    , createTemplateHtml : (each, idx) => {
        let content = "";
        content =
            "<li><p class='input_height'>높이<input type='text' id='height_"+idx+"'></p></li>" +
            "<li>" +
            "<p>종류<span>레이아웃에 들어갈 컨텐츠를 선택해주세요.</span></p>" +
            "<select class='contents_kind' id='kind_"+idx+"'>" +
            "<option value='urlLink'>URL 링크</option>" +
            "<option value='fileSystem'>이미지 / 영상 선택</option>" +
            "</select>" +
            "<input type='text'>" +
            "</li>";

        return content;
    }

    , createTemplateOpt : () => {

    }
    , get : () => {

    }
    , add : () => {

    }
    , mod : () => {

    }
    , del : () => {

    }
    , addProc : (pObj, doneCallback, failCallback) => {
        $.ajax({
            url : "/facility/signage"
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
    , showPopup : () => {
        comm.showModal($('#signagePopup'));
        $("#signagePopup").css('display', 'flex');
    }
    , hidePopup : () => {
        $("#signagePopup").hide();
        comm.hideModal($('#signagePopup'));
    }
}