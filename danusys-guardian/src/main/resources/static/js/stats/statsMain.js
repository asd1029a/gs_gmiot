/**
 * statsMain 관련 함수 모음
 * @namespace statsMain
 * */


/**
 * 검색 날짜 데이터
 * @memberof statsMain
 * @property {object} statsMain - 검색 날따 object
 * @property {date} statsMain.sDate - 검색 시작 날짜
 * @property {date} statsMain.eDate - 검색 마지막 날짜
 * */
var statsMain = {
	sDate : null,
	eDate : null
}

var serverUrl = "http://10.1.105.13:8050";

/**
 * 데이터 검색 함수
 * @memberof statsMain
 * @function loadChart
 * */
function loadChart() {
	var type = $('#selectType').val();
	var jsonObj = {};
	statsMain.sDate = $('#searchChartDateS').datebox('getValue');
	statsMain.eDate = $('#searchChartDateE').datebox('getValue');
	
	jsonObj.sDate = statsMain.sDate;
	jsonObj.eDate = statsMain.eDate;
	jsonObj.changeTypeKind = type;
	
	if(!dateboxValiCk(statsMain.sDate , statsMain.eDate)){
		return;
	}
	
	if(type == "event") {
		$("label[for = 'maxEvent' ]").text("최다이벤트");
		$("label[for = 'minEvent' ]").text("최소이벤트");
	    $(".stats-area.l").load("/action/page", { path : '/stats/eventMapChart' }, function() { mapChart(baseSigCode); });
	    $(".stats-area.c .ct").load("/action/page", { path : '/stats/eventBarChart' }, function() { barChart(); });
	    $(".stats-area.c .cb").load("/action/page", { path : '/stats/eventHeatmapChart' }, function() { blockChart(); });
	    $(".stats-area.r .rt").load("/action/page", { path : '/stats/eventBarChart2' }, function() { barChart2(); });
	    $(".stats-area.r .rb").load("/action/page", { path : '/stats/eventHeatmapChart2' }, function() { blockChart2(); });
	}
	else {
		$("label[for = 'maxEvent' ]").text("최다장애시설물(용도)");
		$("label[for = 'minEvent' ]").text("최소장애시설물(용도)");
		$(".stats-area.l").load("/action/page", { path : '/stats/facilityMapChart' }, function() { mapChart(baseSigCode); });
	    $(".stats-area.c .ct").load("/action/page", { path : '/stats/facilityBarChart' }, function() { barChart(); });
	    $(".stats-area.c .cb").load("/action/page", { path : '/stats/facilityHeatmapChart' }, function() { blockChart(); });
	    $(".stats-area.r .rt").load("/action/page", { path : '/stats/facilityBarChart2' }, function() { barChart2(); });
	    $(".stats-area.r .rb").load("/action/page", { path : '/stats/facilityHeatmapChart2' }, function() { blockChart2(); });
	}
	
	setMapTable();
	setTable('c');
	setTable('r');
	
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type	    : 'POST',
		url         : "/getTotalData",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
	}).done(function (result){
		$('#cntTotal1 em').empty().text(result.totalData2[0].TOTALCNT);
		$('#avgDay1 em').text(result.totalData2[0].DAYAVG);
		$('#cntTotal2 em').text(result.totalData[0].TOTALCNT);
		$('#avgDay2 em').text(result.totalData[0].DAYAVG);
		if(result.maxMinEvent.length == 0){
			$('#minEvent em').text("데이터 없음");
			$('#maxEvent em').text("데이터 없음");
		} else {
			if(result.maxMinEvent.length == 2){
				$('#minEvent em').text(result.maxMinEvent[1].EVTNM);
				$('#maxEvent em').text(result.maxMinEvent[0].EVTNM);
			} else {
				$('#minEvent em').text(result.maxMinEvent[0].EVTNM);
				$('#maxEvent em').text(result.maxMinEvent[0].EVTNM);
			}
		}
		if(result.maxMinPlace.length == 0){
			$('#minArea em').text("데이터 없음");
			$('#maxArea em').text("데이터 없음");
		} else {
			if(result.maxMinPlace.length == 2){
				$('#minArea em').text(result.maxMinPlace[1].PLACE);
				$('#maxArea em').text(result.maxMinPlace[0].PLACE);
			} else {
				$('#minArea em').text(result.maxMinPlace[0].PLACE);
				$('#maxArea em').text(result.maxMinPlace[0].PLACE);
			}
		}
	})
	
	/* mapChart(baseSigCode,clickCenter,clickMapScale);
	blockChart();
	barChart();
	barChart2();
	blockChart2(); */
}

/**
 * 왼쪽 차트 불러오기 함수
 * @memberof statsMain
 * @function cChartChange
 * */
function cChartChange() {
	barChart();
	blockChart();
	setTable('c');
}

/**
 * 오른쪽 차트 불러오기 함수
 * @memberof statsMain
 * @function rChartChange
 * */
function rChartChange() {
	barChart2();
	blockChart2();
	setTable('r');
}

var cQueryUrl;
var rQueryUrl;
function setTable(target) {
	var selectType = $('#selectType').val();
	
	var tableColumns;
	var dayColumns = [[
		{field: 'gbn', title: '구분'},
		{field: 'tm0', title: '0시'},
		{field: 'tm1', title: '1시'},
		{field: 'tm2', title: '2시'},
		{field: 'tm3', title: '3시'},
		{field: 'tm4', title: '4시'},
		{field: 'tm5', title: '5시'},
		{field: 'tm6', title: '6시'},
		{field: 'tm7', title: '7시'},
		{field: 'tm8', title: '8시'},
		{field: 'tm9', title: '9시'},
		{field: 'tm10', title: '10시'},
		{field: 'tm11', title: '11시'},
		{field: 'tm12', title: '12시'},
		{field: 'tm13', title: '13시'},
		{field: 'tm14', title: '14시'},
		{field: 'tm15', title: '15시'},
		{field: 'tm16', title: '16시'},
		{field: 'tm17', title: '17시'},
		{field: 'tm18', title: '18시'},
		{field: 'tm19', title: '19시'},
		{field: 'tm20', title: '20시'},
		{field: 'tm21', title: '21시'},
		{field: 'tm22', title: '22시'},
		{field: 'tm23', title: '23시'}
	]];
	var weekColumns = [[
		{field: 'gbn', title: '구분'},
		{field: 'mon', title: '월요일'},
		{field: 'tue', title: '화요일'},
		{field: 'wed', title: '수요일'},
		{field: 'thu', title: '목요일'},
		{field: 'fri', title: '금요일'},
		{field: 'sat', title: '토요일'},
		{field: 'sun', title: '일요일'}
	]];
	var monColumns = [[
		{field: 'gbn', title: '구분'},
		{field: 'jan', title: '1월'},
		{field: 'feb', title: '2월'},
		{field: 'mar', title: '3월'},
		{field: 'apr', title: '4월'},
		{field: 'may', title: '5월'},
		{field: 'jun', title: '6월'},
		{field: 'jul', title: '7월'},
		{field: 'aug', title: '8월'},
		{field: 'sep', title: '9월'},
		{field: 'oct', title: '10월'},
		{field: 'nov', title: '11월'},
		{field: 'dec', title: '12월'}
	]];
	
	var optVal;
	if(target == "c") optVal = $('#selectBarChartType').val();
	else if(target == "r") optVal = $('#selectBarChart2Type').val();
	
	if(optVal == "0") {	//일별
		if(selectType == "event") eval(target+'QueryUrl'+'= "selectEventByDay"');
		else if(selectType == "facility") eval(target+'QueryUrl'+'= "selectFacilityByDay"');
		
		tableColumns = dayColumns;
	}
	else if(optVal == "1") {	//요일별
		if(selectType == "event") eval(target+'QueryUrl'+'= "selectEventByWeek"');
		else if(selectType == "facility") eval(target+'QueryUrl'+'= "selectFacilityByWeek"');
		
		tableColumns = weekColumns;
	}
	else if(optVal == "2") {	//월별
		if(selectType == "event") eval(target+'QueryUrl'+'= "selectEventByMon"');
		else if(selectType == "facility") eval(target+'QueryUrl'+'= "selectFacilityByMon"');
		
		tableColumns = monColumns;
	}
	
	var jsonObj = {};
	jsonObj.sDate = statsMain.sDate;
	jsonObj.eDate = statsMain.eDate;
	jsonObj.sigCode = baseSigCode.toString();
	
	$('#'+target+'Table').datagrid({

//	    url:'/select/excel.'+eval(target+'QueryUrl')+'/action',
	    pagination:true,
	    pageSize:20,
//	    queryParams : {
//			pageSize: 20,
//			param : JSON.stringify(jsonObj),
//	    },

        loader: function(param, success, error){
	        $.ajax({
	            contentType : "application/json; charset=utf-8",
				url:'/select/excel.'+eval(target+'QueryUrl')+'/action',
                type: 'POST',
                data : JSON.stringify(jsonObj),
				success: function(data, textStatus, jqXHR){
					success(data);
				}
			});
	    },

	    columns:tableColumns,
	    onLoadSuccess:function(data){
	    	if(data.rows=='sessionOut'){
				alert('로그인 시간이 만료되었습니다.');
				closeWindow();
			}
	    }
	});
}

/**
 * html2canvas를 이용하여 div 이미지 캡쳐
 * @memberof statsMain
 * @function elementCapture
 * @param {String} target - element class 
 * */
function elementCapture(target) {
	var el = $('.stats-area.' + target)[0];
	html2canvas(el).then(canvas => {
		var ctx = canvas.getContext('2d');
		
		var img = canvas.toDataURL('image/jpg');
		excelChart(target, img);
	})
}
/**
 * html2canvas를 이용하여 div 이미지 캡쳐
 * @memberof statsMain
 * @function excelChart
 * @param {String} target - element class
 * @param {String} img - 이미지 바이너리 데이터
 * */
function excelChart(target, img) {
	var jsonObj = {};
	jsonObj.sDate = statsMain.sDate;
	jsonObj.eDate = statsMain.eDate;
	jsonObj.sigCode = baseSigCode.toString();
	jsonObj.img = img;

	var path = '/excelDownload/excel.'+eval(target+'QueryUrl')+'/action';
	excelDownLoad('#'+target+'Table', path, 'stats', jsonObj);
}

var lQueryUrl;
function setMapTable() {
	var selectType = $('#selectType').val();
	if(baseSigCode == "all") {
		if(selectType == "event") lQueryUrl = "selectEventMapBySi";
		else if(selectType == "facility") lQueryUrl = "selectFacilityMapBySi";
	}
	else {
		if(selectType == "event") lQueryUrl = "selectEventMapByDong";
		else if(selectType == "facility") lQueryUrl = "selectFacilityMapByDong";
	}
	
	var jsonObj = {};
	jsonObj.sDate = statsMain.sDate;
	jsonObj.eDate = statsMain.eDate;
	jsonObj.sigCode = baseSigCode.toString();
	
	$('#lTable').datagrid({
//	    ajaxGridOptions : {contentType : "application/json; charset=utf-8"},
//	    url:'/select/excel.'+lQueryUrl+'/action',
	    pagination:true,
	    pageSize:20,
        columns:[[
            {field: 'area', title: '지역'},
            {field: 'gbn', title: '구분'},
            {field: 'cnt', title: '합계'},
        ]],
        loader: function(param, success, error){
	        $.ajax({
	            contentType : "application/json; charset=utf-8",
				url: '/select/excel.'+lQueryUrl+'/action',
                type: 'POST',
                data : JSON.stringify(jsonObj),
				success: function(data, textStatus, jqXHR){
					success(data);

				}
			});
	    },
//	    queryParams : {
//			pageSize: 20,
//			param : JSON.stringify(jsonObj),
//	    },
	    onLoadSuccess: function(data){
	    	if(data.rows=='sessionOut'){
				alert('로그인 시간이 만료되었습니다.');
				closeWindow();
			}
	    }
	});
}
