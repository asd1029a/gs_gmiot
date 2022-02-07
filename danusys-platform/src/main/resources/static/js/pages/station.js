/*
* 개소 관련 JS
*/

const station = {
    getList : (param, pCallback) => {
        $.ajax({
            url : "/station"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
        });
    }
    , getListGeoJson : (param, pCallback) => {
        $.ajax({
            url : "/station/geojson"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
        });
    }
    , get : () => {

    }
    , add : () => {

    }
    , mod : () => {

    }
    , del : () => {

    }
}