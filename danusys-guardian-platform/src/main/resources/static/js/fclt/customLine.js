/**
 * customLine class
 * @author - 
 * @version 0.0.1
 * @class customLine
 * @property {Object} start - line start point feature
 * @property {Object} end - line end point feature
 * @property {Object} lineFeature - line string feature
 * @property {Object} layer - custom line layer
 * @property {Object} style - custom line style
 * */
var customLine = {
	start : undefined,
	end : undefined,
	lineFeature : undefined,
	layer : undefined,
	style : new ol.style.Style({
		stroke: new ol.style.Stroke({
			color: '#ffcc33',
			width: 4,
			//lineDash : [ .1, 5 ]
		})
	}),
	/**
	 * 라인 수정 기능 없이 호출 할 경우 사용
	 * @function customLine.viewOnlyInit
	 */
	viewOnlyInit : function() {
		this.layer = !this.layer ? 
				mapManager.createVectorLayer('customLine', this.style, new ol.source.Vector()) : this.layer;
	},
	/**
	 * 라인 수정 기능을 사용하여 호출 할 경우 사용
	 * @function customLine.init
	 */
	init : function() {
		this.layer = !this.layer ? 
				mapManager.createVectorLayer('customLine', this.style, new ol.source.Vector()) : this.layer;
		lineModify.init();
		lineSnap.init();
	},
	/**
	 * customLine clear 함수
	 * @function customLine.clear
	 */
	clear : function() {
		mapManager.removeVectorLayer('customLine');
		lineSnap.clear();
		lineModify.clear();
		this.start = undefined;
		this.end = undefined;
		this.layer = undefined;
	},
	/**
	 * start Feature set 함수
	 * @function customLine.setStart
	 * @param {float} x - longitude(위도) 값 [미사용]
	 * @param {float} y - latitude(경도) 값 [미사용]
	 */
	setStart : function(x, y) {
		const source = mapManager.getVectorLayer('fclt').getSource();
		const mainFcltId = $('#mainFcltId').val();
		const mainFeature = source.getFeatureById(mainFcltId);
		
		this.start = mainFeature;
	},
	/**
	 * end Feature set 함수
	 * @function customLine.setEnd
	 * @param {float} x - longitude(위도) 값 [미사용]
	 * @param {float} y - latitude(경도) 값 [미사용]
	 */
	setEnd : function(x, y) {
		const source = mapManager.getVectorLayer('fclt').getSource();
		const subFcltId = $('#subFcltId').val();
		const subFeature = source.getFeatureById(subFcltId);
		
		this.end = subFeature;
	},
	/**
	 * LineString 데이터를 DB에 저장할 String 데이터로 변환하여 return
	 * @function customLine.getLineString
	 * @returns {String} - 좌표계 변환된 String 데이터 
	 */
	getLineString : function() {
		const t = this.lineFeature.getGeometry().getCoordinates();
		let result = '';
		
		for(var i = 0; i < t.length; i++) {
			const a = ol.proj.transform(t[i], mapManager.properties.projection, mapManager.properties.pro4j[mapManager.properties.type]);
			
			result += a.toString() + ' ';
		}
		
		return result.trim();
	},
	/**
	 * LineString 데이터를 DB에 저장할 String 데이터로 변환하여 return
	 * @function customLine.getSaveLineString
	 * @returns {String} - 좌표계 변환된 String 데이터 [start 및 end 좌표 미저장]
	 */
	getSaveLineString : function() {
		const t = this.lineFeature.getGeometry().getCoordinates();
		let result = '';
		
		for(var i = 0; i < t.length; i++) {
			if(i === 0 || i === (t.length - 1)) continue;
			const a = ol.proj.transform(t[i], mapManager.properties.projection, mapManager.properties.pro4j[mapManager.properties.type]);
			
			result += a.toString() + ' ';
		}
		
		return result.trim();
	},
	/**
	 * String으로 관리되는 LineString 데이터를 지도에 표출하기 위해 Array 데이터로 변환
	 * @function customLine.convertLineStringToArray
	 * @param {String} lineString - lineString 데이터
	 * @returns {Array} - lineString을 Array 데이터로 변환
	 */
	convertLineStringToArray : function(lineString) {
		let t = lineString.split(' ');
		
		return t.map(a => {
			return ol.proj.transform(a.split(','), mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection);
		})
		
	},
	/**
	 * 시설물 연결 라인 데이터를 그려주는 함수
	 * @function customLine.convertLineStringToArray
	 * @param {String} lineString - lineString 데이터
	 */
	viewOnlyDraw : function(lineString) {
		const l = this.convertLineStringToArray(lineString);
		
		const source = this.layer.getSource();
		source.clear();
		
		this.lineFeature = new ol.Feature({
	        geometry: new ol.geom.LineString(l)
	    });
		
		source.addFeature(this.lineFeature);
	},
	/**
	 * 시설물 연결 라인 데이터를 그려주는 함수
	 * @function customLine.convertLineStringToArray
	 * @param {String} lineString - lineString 데이터
	 */
	draw : function(lineString) {
		if(!this.start || !this.end) return;
		
		const source = this.layer.getSource();
		source.clear();
		
		const lineData = [];
		lineData.push(this.start.getGeometry().getCoordinates());
		lineData.push(this.end.getGeometry().getCoordinates());
		
		this.lineFeature = new ol.Feature({
	        geometry: new ol.geom.LineString(lineData)
	    });
		
		source.addFeature(this.lineFeature);
	}
}

var lineSnap = {
	init : function() {
		this.pointer = new ol.interaction.Pointer({
			handleDownEvent : function(e) {
				if(!lineSnap.check(e.coordinate)) throw 'not move point!';
			}
		});
		mapManager.map.addInteraction(this.pointer);
		
		
		this.snap = new ol.interaction.Snap({
			source : customLine.layer.getSource()
		});
		mapManager.map.addInteraction(this.snap);
	},
	check : function(coordinate) {
		if(!customLine.start || !customLine.end) return true;
		const a = customLine.start.getGeometry().getCoordinates();
		const b = customLine.end.getGeometry().getCoordinates();
		
		if(a.toString() == coordinate.toString() || b.toString() == coordinate.toString()) {
			return false;
		}

		return true;
	},
	clear : function() {
		mapManager.map.removeInteraction(this.snap);
		mapManager.map.removeInteraction(this.pointer);
	}
}

var lineModify = {
	init : function() {
		this.select = new ol.interaction.Select({
			layers : [customLine.layer]
		});
		
		mapManager.map.addInteraction(this.select);
		
		this.modify = new ol.interaction.Modify({
			features: this.select.getFeatures()
		});
		
		mapManager.map.addInteraction(this.modify);
		
		this.setEvents();
	},
	setEvents : function() {
		const selectedFeatures = this.select.getFeatures();
		
		this.select.on('change:active', function(e) {
			selectedFeatures.forEach(function(item) {
				selectedFeatures.remove(item);
			});
		});
	},
	setActive : function(active) {
		this.select.setActive(active);
		this.modify.setActive(active);
	},
	clear : function() {
		mapManager.map.removeInteraction(this.select);
		mapManager.map.removeInteraction(this.modify);
	}
}