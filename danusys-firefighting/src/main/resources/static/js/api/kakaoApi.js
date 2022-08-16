/*
* 카카오 api 호출
* */
const kakaoApi = {
    //kakao rest api ajax 요청
    getProc : (callUrl, mappingUrl, obj, callback) => {
        let param = {
            callUrl : callUrl,
        }
        param = $.extend(param, typeof(obj)=="undefined" ? {} : obj);

        $.ajax({
            contentType : "application/json; charset=utf-8",
            type : "POST",
            url : mappingUrl,
            dataType : "JSON",
            data : JSON.stringify(param),
            async : false
        }).done( result => {
            callback(result);
        });
    }
    //주소 반환
    , getAddress : obj => {
        let param = {
            query : "", //질의어
            size : 15, //한 페이지 사이즈
            analyze_type : "similar", //질의 정확성
            page : 1 //페이지
        }
        param = $.extend(param, typeof(obj)=="undefined" ? {} : obj);

        let resultMap = null;
        kakaoApi.getProc('/kakao/address'
              , '/api/getKaKao'
              , param
              , result => {
                resultMap = result;
              }
        );
        return resultMap;
    }
    //장소 반환
    , getPlace : obj => {
        let param = {
            query : "", //질의어
            //category_group_code : "", //카테고리 그룹 코드
            //x : "", //경도
            //y : "", //위도
            //redius : "", //반경
            //rect : "", //범위
            page : 1, //페이지
            size : 15, //한 페이지 사이즈
            //sort : "", //정렬
        }
        param = $.extend(param, typeof(obj)=="undefined" ? {} : obj);

        let resultMap = null;
        kakaoApi.getProc('/kakao/keyword'
            , '/api/getKaKao'
            , param
            , result => {
                resultMap = result
            }
        );
        return resultMap;
    }
    //좌표 -> 주소 반환
    , getCoord2Address : obj => {
        let param = {
            x : "", //경도
            y : "", //위도
            input_coord : 'WGS84' //좌표계
        }
        param = $.extend(param, typeof(obj)=="undefined" ? {} : obj);

        let resultMap = null;
        kakaoApi.getProc('/kakao/coord2address'
            , '/api/getKaKao'
            , param
            , result => {
                resultMap = result;
            }
        );
        return resultMap;
    }
}