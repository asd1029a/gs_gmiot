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