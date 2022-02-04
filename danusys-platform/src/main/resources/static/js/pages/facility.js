/*
* 시설물 관련 JS
*/

const facility = {
    create : () => {

    }
    , getList : (param, pCallback) => {
        $.ajax({
            url : "/facility"
            , type : "POST"
            , data : param
        }).done((result) => {
            pCallback(result);
        });
    }
    , get : (pSeq, pCallback) => {
        $.ajax({
            url : "/facility/" + pSeq
            , type : "GET"
        }).done((result) => {
            pCallback(result);
        });
    }
    , addProc : () => {
        const formObj = $('#facilityForm').serializeJSON();

        $.ajax({
            url : "/facility"
            , type: "PUT"
            , data : formObj
        }).done((result) => {
            comm.showAlert("시설물이 등록되었습니다");
            facility.create($('#facilityTable'));
            facility.hidePopup();
        });
    }
    , modProc : () => {
        const formObj = $('#facilityForm').serializeJSON();

        $.ajax({
            url : "/facility"
            , type: "PATCH"
            , data : formObj
        }).done((result) => {
            comm.showAlert("시설물이 수정되었습니다");
            facility.create($('#facilityTable'));
            facility.hidePopup();
        });
    }
    , modOptProc : (pData, pCallback) => {
        $.ajax({
            url : "/facility/opt"
            , type: "PATCH"
            , data : pData
        }).done((result) => {
            pCallback(result);
        });
    }
    , delProc : (pSeq) => {
        $.ajax({
            url : "/facility/"+pSeq
            , type: "DELETE"
        }).done((result) => {
            comm.showAlert("시설물이 삭제되었습니다");
            facility.create($('#facilityTable'));
            facility.hidePopup();
        });
    }
}