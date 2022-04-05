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