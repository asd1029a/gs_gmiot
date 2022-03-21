const kakaoApi = {
    getAddress : (obj, callback, pageObj) => {
        // const defaultsObj = {
        //     async: false,
        //     data : {
        //         format : "JSON"
        //     },
        //     isExternalApi : true
        // }
        // if(pageObj) {
        //     obj.data.page = pageObj.selectedPage;
        // }
        // const paramObj = $.extend(defaultsObj, typeof(obj)=="undefined" ? {} : obj);
        // comm.ajaxPost(paramObj, (result) => {
        //     callback(result, pageObj);
        // })

        const param = {
            callUrl : '/kakao/address',
            query : "광명",
            size : 10,
            analyze_type : "similar",
            page : 1
        }
        $.ajax({
            contentType : "application/json; charset=utf-8",
            type : "POST",
            url : '/api/getAddress',
            dataType : "JSON",
            data : JSON.stringify(param),
            async : true
        }).done( result => {
            console.log(result);
        });



    }
}