
let page = "/flying";

$(".login-btn").on("click", function () {
    let flag = false;
    if ($('#id').val() == "") {
        alert("아이디를 입력하세요");
        return false;
    } else if ($('#pwd').val() == "") {
        alert("비밀번호를 입력하세요");
        return false;
    }
    const sendData = {
        userId: $("#id").val(),
        password: $("#pwd").val()
    }
    $.ajax({
        url: "/auth/generateToken",
        type: "POST",
        data: sendData,
        success: function (resultData) {
           // console.log("성공",resultData);
            let keys = Object.keys(resultData);

            keys.forEach(key => {
                if (key === 'accessToken') {
                    flag = true;
                    let date=new Date();
                    date.setDate(date.getDate() + 1000* 60 * 60 *24 *30);
                    document.cookie = key + '=' + resultData[key]+';'+`Expires=${date.toUTCString()}`;

                }
            });
            //   document.location=page;

            if (flag === true) {
                document.location = page;
            } else {

                document.location.reload();
            }

        }
    });

    return false;
});
