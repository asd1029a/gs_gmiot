
var customLine2 = {
	notUpdateLineString : undefined,
	layer : undefined,
	modifyDraw : undefined,
	modifySelect : undefined,
	draw : undefined,
	lineFeature : undefined,
}

function fcltCnctLine(layerTitle, layer, lineColor) {
	const jsonObj={};
	var style = fcltLineColor(lineColor);
	var source; 
	if(layer != undefined && layer != '') {
		source = layer.getSource();
	} else {
		source = fcltLineSource();
		mapManager.createVectorLayer(layerTitle, style, source);
		customLine2.layer = layer;
	}
	var draw = new ol.interaction.Draw({
		source : source,
		type : 'LineString',
		style : style,
	});
	if(customLine2.draw == undefined) {
		customLine2.draw = draw;
	} else {
		alert('이미 실행 중입니다.');
		return;
	}
	mapManager.map.addInteraction(draw);
	draw.on('drawstart',function(evt) {
	});
	draw.on('drawend',function(evt) {
			customLine2.lineFeature = evt.feature;
		if(confirm('저장하시겠습니까?')) {
			//evt.feature.setProperties({"layerTitle":layerTitle})
			var coordinates = evt.feature.getGeometry().getCoordinates();
			var lineString = getLineString(coordinates);
			jsonObj.lineString = lineString;
			jsonObj.layerTitle = layerTitle;
			addFcltCnctLineAjax(jsonObj);
			clickLineLayer('lineLayerHidden','c');
			$('#lineLayerHidden').jstree('deselect_node',layerTitle);
			$('#lineLayerHidden').jstree('select_node', layerTitle);
			draw.setActive(false);
			customLine2.draw = undefined;
		} else {
			draw.setActive(false);
			customLine2.draw = undefined;
			mapManager.getVectorLayer(layerTitle).getSource().removeFeature(customLine2.lineFeature);
			return;
		}
	});
}

function addFcltCnctLineAjax(jsonObj) {
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		url			: "/ajax/insert/facility.savefcltCnctLine/action",
		type		: "POST",
		dateType	: "json",
		data		: JSON.stringify(jsonObj),
		async		: false
	}).done(function(result) {
		alert('저장완료');
		lineModify2.clear();
		customLine2.modifyDraw = undefined;
		return;
	})
}

function fcltLineSource() {
	const source = new ol.source.Vector({
		wrapX : false
	});
	return source;
}

function getLineString(coordinates) {
	const t = coordinates;
	let result = '';
	
	for(var i = 0; i < t.length; i++) {
		const a = ol.proj.transform(t[i], mapManager.properties.projection, mapManager.properties.pro4j[mapManager.properties.type]);
		
		result += a.toString() + ' ';
	}
	
	return result.trim();
}

var lineModify2 = {
		select : undefined,
		modify : undefined,
		init : function() {
			lineModify2.select = new ol.interaction.Select({
				layers : [customLine2.layer],
			});
			//여기는 클릭만되면 무조건온다.
			this.select.on('select',function(e) {
				// 선택 종료 시
				if(e.deselected.length != 0){
					if(confirm("수정중인 라인을 저장하시겠습니까?")) {
						const jsonObj = {};
						const coordinates = e.deselected[0].getGeometry().getCoordinates();
						const seqNo = e.deselected[0].getProperties().no;
						var uptLineString = getLineString(coordinates);
						jsonObj.lineString = uptLineString;
						jsonObj.seqNo = seqNo;
						addFcltCnctLineAjax(jsonObj);
					} else {
						const layerTitle = e.deselected[0].getProperties().layerTitle;
						const exstLineString = getFcltNode(e.deselected[0].getProperties().no);
						e.deselected[0].setGeometry(new ol.geom.LineString(exstLineString));
						lineModify2.clear();
					}
					mapManager.map.removeInteraction(this.select);
					mapManager.map.removeInteraction(this.modify);
				} 
			});
			mapManager.map.addInteraction(this.select);
			this.modify = new ol.interaction.Modify({
				features: this.select.getFeatures(),
			});
			const modify = this.modify;
			
			mapManager.map.addInteraction(this.modify);
			customLine2.modifyDraw = this.modify;
			customLine2.modifySelect = this.select;
		},
		setActive : function(active) {
			this.select.setActive(active);
			this.modify.setActive(active);
		},
		clear : function() {
			mapManager.map.removeInteraction(this.select);
			mapManager.map.removeInteraction(this.modify);
		},
	}


function getFcltNode(seqNo) {
	'use strict';
	const jsonObj = {};
	var exstLineString;
	jsonObj.seqNo = seqNo;
	$.ajax({
		url			: "/getLineStringData.do",
		type		: "POST",
		dataType	: "json",
		data		: {"param" : JSON.stringify(jsonObj)},
		async		: false
	}).done(function(result) {
		exstLineString = result.features[0].geometry.coordinates;
	})
	return exstLineString;
}

function checkNode(id) {
	$("#lineLayerHidden").jstree(true).check_node(data.layerName);
}
