
function getMsrstnAcctoRltmMesureDnsty() {
	const jsonObj = {
		returnType : 'json',
		numOfRows : '1',
		pageNo : '1',
		stationName : '선단동',
		dataTerm : 'DAILY',
		ver : '1.0'
	}
	
	$.ajax({
		type : 'POST',
		url : "/api/getMsrstnAcctoRltmMesureDnsty",
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify(jsonObj)
	}).done((d) => {
		console.log(d);
	}).fail((e) => {
		console.log(e);
	})
}

function setLineLayerItem(id) {
	
}

function setSafeLayerItem(id) {
	const parent = $('#' + id + ' dd');
	var setData = [];
	var id = ['safemap1','safemap2','safemap3','safemap4','safemap5'];
	var value = ["A2SM_PHARMACY" ,"A2SM_AED" ,"A2SM_TFCACDSTATS_0" ,"A2SM_CRMNLSTATS" ,"A2SM_HEALTH_CENTER"];;
	var text = ["약국","AED","교통사고","치안","보건소"];
	for(var i=0;i<id.length;i++){
		const p = $('<span>').addClass('checkbox-wrap');
		const input = $('<input>').attr({
			type : 'checkbox',
			id : id[i],
			value : value[i],
		})
		const label = $('<label>').attr({
			for : id[i]
		}).text(text[i]);
		
		
		input.bind({
			'click' : function(e) {
				var targetVal = $(this).val();
				var targetId = $(this).attr('id');
				const flag = $(this).is(':checked');
				if(flag){
					eval(targetId+"= new ol.layer.Tile({"
							+ "source:  new ol.source.TileWMS({"
								+"url: 'http://www.safemap.go.kr/sm/apis.do?apikey=YW3D9Q4G-YW3D-YW3D-YW3D-YW3D9Q4G57',"
								+"params: {'layers': '"+targetVal+"', 'tiled': true,format: 'image/png',exceptions:'text/xml',transparent: true},"
								+"serverType: 'geoserver',"
								+"tileOptions: {crossOriginKeyword: 'anonymous'},"
								+"transitionEffect: null,"
								+"projection: ol.proj.get('EPSG:3857')"
								+"},{isBaseLayer: false})"
							+"});");
					mapManager.map.addLayer(eval(targetId));
				}
				else {
					mapManager.map.removeLayer(eval(targetId));
				}
			}
		})
		
		p.append(input, label);
		parent.append(p);
	}
}

function getCctvLayerData(id) {
	const jsonObj = {};
	jsonObj.chkDeGrpCd = 'FCLT_PURPOSE';
	jsonObj.userId = window.opener.document.getElementById('loginId').value
	var fcltPurposeData = [];
	var fcltData = [];
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url         : "/select/oprt.getCodeDetailList/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async       : false,
		beforeSend: function (xhr) {
			// 전송 전 Code
		},
	}).done(function (result) {
		const rows = result.rows;
    	if(rows=='sessionOut'){
			alert('로그인 시간이 만료되었습니다.');
			closeWindow();
		}
    	setCctvLayerItem(id, rows);
 	}).fail(function (xhr) {
		console.log(JSON.stringify(xhr));
	}).always(function() {
		
	});
}

function setCctvLayerItem(id, l) {
	const parent = $('#' + id + ' dd');
	var setData = [];
	for(var i=0;i<l.length;i++){
		const p = $('<span>').addClass('checkbox-wrap');
		const input = $('<input>').attr({
			type : 'checkbox',
			id : l[i].deCd,
			name : 'cctv',
			checked : true
		})
		const label = $('<label>').attr({
			for : l[i].deCd
		}).text(l[i].deCdNm);
		
		
		input.bind({
			'click' : function(e) {
				const list = $('#' + id + ' input[name="cctv"]:checked');
				const selected = [];
				list.map((a, b) => selected.push(b.id));
				const param = {};
				if(selected.length > 0 ){
					param.purposeSpace = selected;
				} else {
					param.purposeSpace = ['레이어'];
				}
				setCctvDraw(param,'');
			}
		})
		
		p.append(input, label);
		parent.append(p);
	}
}

var airLegend = {
	prop : {
		id : 'legendLayer',
		tabId : 'airTabs',
		densityId : 'airDensity'
	},
	density : {
		'khai' : ['좋음 0~50', '보통 51~100', '나쁨 101~250', '매우나쁨 251~', '정보없음'],
		'pm10' : ['좋음 0~30', '보통 31~80', '나쁨 81~150', '매우나쁨 151~', '정보없음'],
		'pm25' : ['좋음 0~15', '보통 16~35', '나쁨 36~75', '매우나쁨 76~', '정보없음'],
		'ysnd' : ['좋음 0~199', '보통 200~399', '나쁨 400~799', '매우나쁨 800~', '정보없음'],
		'o3' : ['좋음 0~0.03', '보통 0.031~0.09', '나쁨 0.091~0.15', '매우나쁨 0.151~', '정보없음'],
		'no2' : ['좋음 0~0.03', '보통 0.031~0.06', '나쁨 0.061~0.2', '매우나쁨 0.201~', '정보없음'],
		'co' : ['좋음 0~2', '보통 2.01~9', '9.01~15', '나쁨 15.01~', '정보없음'],
		'so2' : ['좋음 0~0.02', '보통 0.021~0.05', '나쁨 0.051~0.15', '매우나쁨 0.151~', '정보없음']
	},
	unit : {
		'khai' : '',
		'pm10' : '㎍/㎥',
		'pm25' : '㎍/㎥',
		'ysnd' : '㎍/㎥',
		'o3' : 'ppm',
		'no2' : 'ppm',
		'co' : 'ppm',
		'so2' : 'ppm'
	},
	setDatas: function(type) {
		const d = this.density[type];
		const u = this.unit[type];
		const l = $('#' + this.prop.densityId).children('li.item');
		
		l.each((index, el) => {
			$(el).children('.unit').text(d[index]);
		});
		
		$('#' + this.prop.tabId + ' li').removeClass('active');
		
		$('#' + type).parent().addClass('active');
		
		$('#' + this.prop.id).children('.desc').children('.unit').text('(단위:' + u + ')');
	},
	show: function() {
		$('#' + this.prop.id).show();
	},
	hide: function() {
		$('#' + this.prop.id).hide();
	}
}

