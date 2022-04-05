let page = "/pages/mntr";


$(".login-btn").on("click", function () {
    let flag=false;
    if ($('#id').val() == "") {
        alert("아이디를 입력하세요");
        return false;
    } else if ($('#pwd').val() == "") {
        alert("비밀번호를 입력하세요");
        return false;
    }
    const sendData = {
        username: $("#id").val(),
        password: $("#pwd").val()
    }
    $.ajax({
        url: "/auth/generateToken",
        type: "POST",
        data: sendData,
        success: function (resultData) {
            log.info("성공",resultData);
            let keys = Object.keys(resultData);
            keys.forEach(key => {
                if (key === 'accessToken') {
                    document.cookie = key + '=' + resultData[key];
                    flag=true;
                }
            });


                document.location = page;

        }
    });

    return false;
});
