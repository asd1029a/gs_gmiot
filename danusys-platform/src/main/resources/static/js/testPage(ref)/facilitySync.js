
function sendSync(){
	var jsonObj = {}; 
	var url = $('#url').val();
	var manageServerIp = $('#manageServerIp').val();
	jsonObj.code = "3200";
	jsonObj.svr_ip = manageServerIp;
	jsonObj.send_kind = "1";
	jsonObj.client = "0";
	
	if(!checkNull(url,"url 을 입력해 주세요.")) return;
	if(!checkNull(manageServerIp,"관리서버 IP 를 입력해주세요.")) return;
	$.ajax({
		type		: "POST",
		url			: url,
		dataType	: "json",
		data		: JSON.stringify(jsonObj),
		async		: false
	}).done(function(result){
		alert("성공");
	}).fail(function(xhr){
		alert("실패");
	});
}