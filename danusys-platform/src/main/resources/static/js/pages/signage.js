/*
* 사이니지 관련 JS
*/

const signage = {
    eventHandler : () => {
        $("#addSignageTemplateBtn").on('click', () => {
            signage.showPopup();
        });
        $("#signagePopup .title dd, #signagePopup .bottom li:first-child").on('click', () => {
            signage.hidePopup();
        });
        $("select.contents_kind").on('change', (e) => {
            const targetValue = e.target.value;
            const targetNode = e.target.parentElement;

            var innerTag =
                {'fileSystem':'div',
                'urlLink':'input'};
            var innerHtml =
                {'fileSystem':'<div class="fileBox">\n' +
                        '<input class="uploadName" value="첨부파일" placeholder="첨부파일">\n' +
                        '<label for="file">파일찾기</label>\n' +
                        '<input type="file" id="file">\n' +
                        '</div>',
                'urlLink':'<input type="text">'};

            $(targetNode).children().not('p').not('select').remove();

            var tempTag = document.createElement(innerTag[targetValue]);
            tempTag.innerHTML = innerHtml[targetValue];
            targetNode.append(tempTag);
        });
    }
    , createList : () => {

    }
    , getList : () => {

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
    , showPopup : () => {
        comm.showModal($('#signagePopup'));
        $("#signagePopup").css('display', 'flex');
    }
    , hidePopup : () => {
        $("#signagePopup").hide();
        comm.hideModal($('#signagePopup'));
    }
}