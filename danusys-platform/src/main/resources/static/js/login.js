function loginDisplaySet() {
	let param = {"id":"admin"};
	$.ajax({
	    contentType: "application/json; charset=utf-8",
		type       : "POST",
		url        : "/selectNoSession/admin.selectDisplaySet/action",
		data       : JSON.stringify(param),
		async      : false
	}).done(function(data) {
		var rowData = data.rows[0];

		$('#loginBgTitSub').text(rowData.loginBgTitSub);
		$('#loginBgTit').text(rowData.loginBgTit);
		
		var imgFile = "/file/getImage2?sPath=display&imageUrl="+rowData.loginBg;
		console.log(imgFile);
		$('#loginImg').css('background-image','url('+imgFile+')');
		
	});
}

//let page = "/pages/mntr";
let page = "/permitallpage";

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
		username: $("#id").val(),
		password: $("#pwd").val()
	}
	$.ajax({
		url: "/auth/generateToken",
		type: "POST",
		data: sendData,
		success: function (resultData) {
			console.log("성공",resultData);
			let keys = Object.keys(resultData);

			keys.forEach(key => {
				if (key === 'accessToken') {
					flag = true;
					document.cookie = key + '=' + resultData[key];

				}
			});
			//   document.location=page;

			if (flag === true) {
				document.location = page;
			} else {

				document.write(resultData);
			}

		}
	});

	return false;
});
