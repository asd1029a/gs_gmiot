function regenerateToken(){
    $.ajax({
        url: "/auth/regenerateToken",
        type: "POST",
        success: function (resultData) {
            let keys = Object.keys(resultData);
            keys.forEach(key => {
                if (key === 'accessToken') {

                    let date=new Date();
                    date.setDate(date.getDate() + 1000* 60 * 15);
                    document.cookie = key + '=' + resultData[key]+';'+`Expires=${date.toUTCString()}`;

                }
            });
        }
    });
};


regenerateToken();
setInterval(regenerateToken
    , 1000 * 60 * 5);
