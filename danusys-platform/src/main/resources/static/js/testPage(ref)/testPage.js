
function sendTest(){
	const jsonObj = {};
	var evtOcrNo = $('#evtOcrNo').val();
	var evtId = $('#evtId').val();
	var evtNm = $('#evtNm').val();
	var evtGradCd = $('#evtGradCd').val();
	var evtPrgrsCd = $('#evtPrgrsCd').val();
	var evtPrgrsContent = $('#evtPrgrsContent').val();
	var userId = $('#userId').val();
	var evtPlace = $('#evtPlace').val();
	var evtDtl = $('#evtDtl').val();
	var evtOcrYmdHms = $('#evtOcrYmdHms').val();
	var lon = $('#lon').val();
	var lat = $('#lat').val();
	
	if(!checkNull(evtId, "이벤트 아이디를 입력해주세요.")) return;
	if(!checkNull(evtOcrNo,"이벤트 발생번호를 입력해주세요")) return;
	if(!checkNull(evtNm, "이벤트 명을 입력해주세요")) return;
	if(!checkNull(evtGradCd, "이벤트 등급코드를 입력해주세요.")) return;
	if(!checkNull(evtPrgrsCd, "이벤트 진행코드를 입력해주세요.")) return;
	if(!checkNull(evtPrgrsContent, "진행상태 별 내용을 입력해주세요.")) return;
	if(!checkNull(userId, "진행 상태 별 처리자를 입력해주세요.")) return;
	if(!checkNull(evtPlace, "이벤트 발생장소를 입력해주세요.")) return;
	if(!checkNull(evtDtl, "이벤트 상세내용을 입력해주세요.")) return;
	if(!checkNull(evtOcrYmdHms, "이벤트 발생일자 시각을 입력해주세요.")) return;
	if(!checkNull(lon, "좌표(경도)를 입력해 주세요.")) return;
	if(!checkNull(lat, "좌표(위도)를 입력해 주세요.")) return;
	jsonObj.evtOcrNo = evtOcrNo;
	jsonObj.evtId = evtId;
	jsonObj.evtNm = evtNm;
	jsonObj.evtGradCd = evtGradCd;
	jsonObj.evtPrgrsCd = evtPrgrsCd;
	jsonObj.evtPrgrsContent = evtPrgrsContent;
	jsonObj.userId = userId;
	jsonObj.evtPlace = evtPlace;
	jsonObj.evtDtl = evtDtl;
	jsonObj.evtOcrYmdHms = evtOcrYmdHms;
	jsonObj.lon = lon;
	jsonObj.lat = lat;
	
	$.ajax({
		type		: "POST",
		url			: "/test/sendEvent.do",
		dataType	: "json",
		data		: jsonObj,
		async 		: true
	}).done(function(result){
		if(result == "SUCCESS") {
			alert("전송완료");
			window.location.reload();
		}
	}).fail(function(xhr) {
		alert('전송실패');
	}).always(function() {
		
	})
}

