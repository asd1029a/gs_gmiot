var daumExtent = [-30000, -60000, 494288, 988576];
var extent = daumExtent;


var eventVectorSource = new ol.source.Vector();
var eventVectorLayer = new ol.layer.Vector({ source: eventVectorSource });
var cctvVectorSource = new ol.source.Vector();
var cctvVectorLayer = new ol.layer.Vector({ source: cctvVectorSource });
var ptzVectorSource = new ol.source.Vector();
var ptzVectorLayer = new ol.layer.Vector({ source: ptzVectorSource });

function layerDraw(src, data, target) {
	
	removeFeatures(src);
	removeFeatures(ptzVectorSource);
	
	for(var i=0; i<data.length; i++) {
		//debugger;
		var startPoint = new ol.proj.transform([data[i].lon, data[i].lat],mapManager.properties.pro4j[mapManager.properties.type],mapManager.properties.projection);		
		feature = new ol.Feature({
			geometry: new ol.geom.Point(startPoint),
			target: target,
			population: 4000,
			rainfall: 500
		});
		
		var iconStyle = new ol.style.Style({
			image: new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
				anchor: [0.52, 46],
				anchorXUnits: 'fraction',
				anchorYUnits: 'pixels',
				scale: 0.5,
				src: '../images/icons/cctv.png'
			}))
		});
		
		feature.setStyle(iconStyle);
		src.addFeature(feature);
		
		//ptzLayerDraw(ptzVectorSource, startPoint);
	}
	//debugger;
	
	
	/*var startPoint = [219951.45445515434, 422395.3170886529];
	
	cctvFeature = new ol.Feature({
		geometry: new ol.geom.Point(startPoint),
		name: 'Null Island',
		population: 4000,
		rainfall: 500
	});
	
	var iconStyle = new ol.style.Style({
		image: new ol.style.Icon(*//** @type {olx.style.IconOptions} *//* ({
			anchor: [0.52, 46],
			anchorXUnits: 'fraction',
			anchorYUnits: 'pixels',
			scale: 0.5,
			src: '../images/icons/cctv.png'
		}))
	});
	
	cctvFeature.setStyle(iconStyle);
	src.addFeature(cctvFeature);
	
	var feature = new ol.Feature({
        geometry: new ol.geom.LineString([
        	[219951.45445515434, 422395.3170886529],[223916.22207068835, 425023.8341606102]
        ])
    });
	
	
	ptzFeature = new ol.Feature({
		geometry: new ol.geom.MultiLineString([
			[startPoint,[223916.22207068835, 425023.8341606102]],
        	[startPoint,[216080.58966034857, 422742.3170886532]],
        	[startPoint,[220047.04412530435, 422236.5385880986]],
        	[startPoint,[218749.93625597158, 420854.8034196643]]
		])
	});
	
	
	
	var style = [
		new ol.style.Style({
			stroke: new ol.style.Stroke({
				color: '#ffcc33',
				width: 1
			})
		})
	];
	
	var geometryLine = ptzFeature.getGeometry();
	
	geometryLine.getLineString().forEachSegment(function(start, end) {
		if(start[0]==startPoint[0]&&start[1]==startPoint[1]) {
			var dx = end[0] - start[0];
			var dy = end[1] - start[1];
			var rotation = Math.atan2(dy, dx);
			
			style.push(new ol.style.Style({
				geometry: new ol.geom.Point(start),
				image: new ol.style.Icon({
					src: '../img/arrow.png',
					anchor: [0.75, 0.5],
					rotateWithView: false,
					rotation: -rotation
				})
				image: new ol.style.Icon({
					src: '../svg/cone_pie.svg',
					//anchor: [0.75, 0.5],
					opacity: 0.75,
					anchor: [0, 0.42],
					rotateWithView: false,
					rotation: -rotation
				})
			}));
		}
	});*/
	
	/*geometryLine.forEachSegment(function(start, end) {
    	var dx = end[0] - start[0];
        var dy = end[1] - start[1];
        var rotation = Math.atan2(dy, dx);
        // arrows
        style.push(new ol.style.Style({
        	geometry: new ol.geom.Point(start),
            image: new ol.style.Icon({
              src: '../img/arrow.png',
              anchor: [0.75, 0.5],
              rotateWithView: false,
              rotation: -rotation
            })
        	image: new ol.style.Icon({
                src: '../svg/cone_pie.svg',
                anchor: [0.75, 0.5],
                rotateWithView: false,
                rotation: -rotation
              })
        }));
      });*/

	
	
	
}

function ptzLayerDraw(src, startPoint) {
	ptzFeature = new ol.Feature({
		geometry: new ol.geom.MultiLineString([
			[startPoint,[223916.22207068835, 425023.8341606102]],
        	[startPoint,[216080.58966034857, 422742.3170886532]],
        	[startPoint,[220047.04412530435, 422236.5385880986]],
        	[startPoint,[218749.93625597158, 420854.8034196643]]
		])
	});
	
	var style = [
		new ol.style.Style({
			stroke: new ol.style.Stroke({
				color: '#ffcc33',
				width: 1
			})
		})
	];
	
	var geometryLine = ptzFeature.getGeometry();
	
	geometryLine.getLineString().forEachSegment(function(start, end) {
		if(start[0]==startPoint[0]&&start[1]==startPoint[1]) {
			var dx = end[0] - start[0];
			var dy = end[1] - start[1];
			var rotation = Math.atan2(dy, dx);
			
			style.push(new ol.style.Style({
				geometry: new ol.geom.Point(start),
				/*image: new ol.style.Icon({
					src: '../img/arrow.png',
					anchor: [0.75, 0.5],
					rotateWithView: false,
					rotation: -rotation
				})*/
				image: new ol.style.Icon({
					src: '../svg/cone_pie.svg',
					//anchor: [0.75, 0.5],
					opacity: 0.75,
					anchor: [0, 0.42],
					rotateWithView: false,
					rotation: -rotation
				})
			}));
		}
	});
	ptzFeature.setStyle(style);
	ptzFeature.set('name', '폴리라인 Feature');
	
	src.addFeature(ptzFeature);
}

function removeFeatures(src) {
	var features = src.getFeatures();
	for(var i=0; i<features.length; i++) {
		src.removeFeature(features[i]);
	}
}

mapManager.map.getViewport().addEventListener("click", function(e) {
	mapManager.map.forEachFeatureAtPixel(mapManager.map.getEventPixel(e), function (feature, layer) {
    	const targetFeature = eval(feature.getProperties().target);
    	if(targetFeature == "ptzVectorSource") {
    		removeFeatures(targetFeature);
            const targetPoint = feature.getGeometry().getCoordinates();
            ptzLayerDraw(targetFeature,targetPoint);
    	}
    	//const oldTargetFeature = eval(feature.getProperties().oldTarget);
    	//if(typeof(oldTargetFeature) != "undefined")removeFeatures(oldTargetFeature);
    	
    });
});


var pointerVectorSource = new ol.source.Vector();
var pointerVectorLayer = new ol.layer.Vector({ source: pointerVectorSource });

function pointerLayerDraw(point) {
	
	removeFeatures(pointerVectorSource);
	
	var iconFeature = new ol.Feature({
		geometry: new ol.geom.Point(point),
		population: 4000,
		rainfall: 500
	});
	
	var iconStyle = new ol.style.Style({
		image: new ol.style.Icon({ scale: .3 , src: '../images/icons/cctv/selected.png' }),
	});
	
	iconFeature.setStyle(iconStyle);
	pointerVectorSource.addFeature(iconFeature);
	
	mapManager.map.addLayer(pointerVectorLayer);
}