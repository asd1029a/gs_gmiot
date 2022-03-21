function regenerateToken() {
    $.ajax({
        url: "/auth/regenerateToken",
        type: "POST",
        success: function (resultData) {
            let keys = Object.keys(resultData);
            keys.forEach(key => {
                if (key === 'accessToken') {

                    let date = new Date();
                    date.setDate(date.getDate() + 1000 * 60 * 15);
                    document.cookie = key + '=' + resultData[key] + ';' + `Expires=${date.toUTCString()}`;

                }
            });
        }
    });
};


regenerateToken();
setInterval(regenerateToken
    , 1000 * 60 * 13);
let idleTime = 0;
timeLogout();

function timeLogout() {
    let idleInterval = setInterval(timerIncrement, 60000); // 1 minute

    //일정시간 움직임이 있으면 초기화

    $(this).mousemove(function (e) {
        idleTime = 0;
    });

    $(this).keypress(function (e) {
        idleTime = 0;
    });
}


function timerIncrement() {

    idleTime = idleTime + 1;

    if (idleTime > 10) { // 20 minutes

        //새로고침 하거나 로그아웃 처리
        document.location.href = "/";
        document.cookie = 'accessToken' + '=; expires=Thu, 01 Jan 1999 00:00:10 GMT;';


    }
}