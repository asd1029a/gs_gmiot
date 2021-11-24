/**
 * socket 페이지 관련 모음
 * @namespace socketPage
 * */

/**
 * 이벤트 데이터를 불러오고 callback function을 실행하는 함수
 * @function socket.getEventInfo
 * @param {object} evt - 이벤트 데이터
 * @param {socket.openEventAlert} callback - callback function
 * */
function getEventInfo(evt, callback) {
	const param = {};
	param.evtOcrNo = evt.evtOcrNo;
	param.userId = '${admin.id}';
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type : "POST",
		url : "/select/event.selectEventList/action",
		dataType : "json",
		data : "param" : JSON.stringify(param),
		async : false,
		beforeSend : function(xhr) {
			// 전송 전 Code
		},
	}).done(function(result) {
		const rows = result.rows;
		if (rows == 'sessionOut') {
			alert('로그인 시간이 만료되었습니다.');
			closeWindow();
		}
		if (callback !== undefined)
			callback(rows[0]);
	}).fail(function(xhr) {
	}).always(function() {

	});
}

/**
 * 이벤트 정보로 로그 정보를 표출하는 함수.
 * @function socket.openEventAlert
 * @param {object} data - 이벤트 데이터
 * @deprecated - callback를 너무 많이 실행하여 이후에 변경해야 함.
 * */
function openEventAlert(evt) {
	const source = mapManager.getVectorLayer('event').getSource();
	const evtInfo = source.getFeatureById(evt.evtOcrNo).getProperties();
	var evtOcrYmdHms = moment(evtInfo.evtOcrYmdHms, 'YYYY년MM월DD일HH:mm:ss').format(
			'YYYY년MM월DD일HH:mm:ss');

	const msg = evtInfo.evtDtl + '관련 ' + evtInfo.evtNm + '\n요청이 발생하였습니다.\n'
			+ '발생시각 : ' + evtOcrYmdHms + '\n이동하시겠습니까?';

	const callback = function(data) {
		const success = function(option) {
			const data = option.data;
			const feature = mapManager.getVectorLayer('event').getSource()
					.getFeatureById(data.evtOcrNo);
			const position = ol.proj.transform([data.lon, data.lat], mapManager.properties.pro4j[mapManager.properties.type],mapManager.properties.projection);
			const content = createEventOverlayContent(data);
			
			mapManager.setCenter(position);
			
			const popupOption = {
				id : 'event',
				position : position,
				element : content,
				offset : [ 0, -250 ],
				positioning : 'center-center',
				stopEvent : true,
				insertFirst : true
			}
			
			mapManager.setOverlay(popupOption);
		}

		const option = {};
		option.data = data;

		success(option);
	}

	const option = {};

	option.callback = callback;
	option.callbackProp = evtInfo;

	commonLog.info(msg, option);
}