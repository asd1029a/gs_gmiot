function regenerateToken(){
    $.ajax({
        url: "/auth/regenerateToken",
        type: "POST",
        success: function (resultData) {
            let keys = Object.keys(resultData);
            keys.forEach(key => {
                if (key === 'accessToken') {
                    document.cookie = key + '=' + resultData[key];
                }
            });
        }
    });
}


regenerateToken();
setInterval(regenerateToken
    , 1000 * 60 * 5);
