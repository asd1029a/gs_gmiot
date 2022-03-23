/**
 * 
 */

const measure = {
	drawFeature	: selectedType => {
		$('#measureErase').show();
		/*typeSelect.onchange = function(e) {
			  map.removeInteraction(draw);
			  addInteraction();
		};*/
		
		//draw//layer//tooltip
		map.getLayers().forEach( layer => {
			if(layer.get('name')=='measureVector'){
				map.getInteractions().forEach( inter => {
					if(inter instanceof ol.interaction.Draw){
						inter.setActive(false);
						map.removeInteraction(inter);
					}
				});
				map.removeLayer(layer);
				$(".tooltip-static").parent().remove();
				$(".tooltip-measure").parent().remove();
				
			}
		});
		
		let measureTooltipElement;
		let measureTooltip;
		let draw;

		let sketch;
		let listener;
		
		let source = new ol.source.Vector({wrapX: false});
		
		let vector = new ol.layer.Vector({
			name : 'measureVector',
			'displayInLayerSwitcher' : false,
			source : source,
			style : new ol.style.Style({
				fill : new ol.style.Fill({
					color : 'rgba(255, 0, 0, 0.2)'
				}),
				stroke : new ol.style.Stroke({
					color : 'red',
					width : 2
				}),
				image : new ol.style.Circle({
					radius : 7,
					fill : new ol.style.Fill({
						color : 'red'
					})
				})
			})
		});
		
		map.addLayer(vector);
		
		function addInteraction() {
			let type="";
			
			switch(selectedType){
				case "area":
					type = 'Polygon';
					break;
				case "length":        
					type = 'LineString';
					break;
				case "radius":
					type = 'Circle';
					break;
				default: 
					type='';
					break;
			}
		
			draw = new ol.interaction.Draw({
				name:'measureDraw',
				source: source,
				type:(type),
				style: new ol.style.Style({
					fill : new ol.style.Fill({
						color : 'rgba(0, 0, 255, 0.1)'
					}),
					stroke : new ol.style.Stroke({
						color : 'rgba(0, 0, 255, 0.5)',
						lineDash : [ 10, 10 ],
						width : 2
					}),
					image : new ol.style.Circle({
						radius : 5,
						stroke : new ol.style.Stroke({
							color : 'rgba(0, 0, 255, 0.7)'
						}),
						fill : new ol.style.Fill({
							color : 'rgba(255, 255, 255, 0.1)'
						})
					})
				}) 
			});

			map.addInteraction(draw);

			createMeasureTooltip();
			
			$(document).keydown( e => {
				if((e.keyCode==27)&&(draw.getActive())){
					map.getLayers().forEach( layer => {
						if(layer.get('name')=='measureVector'){
							draw.setActive(false);
							map.removeInteraction(draw);
							map.removeLayer(layer);
							$(".tooltip-measure").parent().remove();
							$(".tooltip-static").parent().remove();
							$("#measureErase").hide();
							$(".map_measure").children().map( (i,v) => {
								$(v).removeClass('on');
							});
						}
					});
				}
			});
			
			draw.on('drawstart', evt => {
				sketch = evt.feature;
				
				let tooltipCoord = evt.coordinate;
				
				lisener = sketch.getGeometry().on('change', evt => {
					let geom = evt.target;
					let output;
					if (geom instanceof ol.geom.Polygon) {
						output = formatOutput.getArea((geom));
						tooltipCoord = geom.getInteriorPoint().getCoordinates();
						
					} else if (geom instanceof ol.geom.LineString) {
						output = formatOutput.getLength((geom));
						tooltipCoord = geom.getLastCoordinate();
						
					} else if (geom instanceof ol.geom.Circle){
						output = formatOutput.getRadius((geom));
						tooltipCoord = geom.getLastCoordinate();
					} else {
						alert("그리기 타입오류");
					}
					measureTooltipElement.innerHTML = output;
					measureTooltip.setPosition(tooltipCoord);
					
				});
				
			},this);
			
			draw.on('drawend', evt => {
				measureTooltipElement.className = 'tooltip tooltip-static';
				measureTooltip.setOffset([ 0, -7 ]);

				sketch = null;
				measureTooltipElement = null;
				
				//createMeasureTooltip();
				ol.Observable.unByKey(listener);
				draw.setActive(false);
				map.removeInteraction(draw);
				
				$(".map_measure").children().map( (i,v) => { 
					$(v).removeClass('on');
				});
				
			}, this);
		
		}
		
		const wgs84Sphere = new ol.Sphere(6378137);
		const sourceProj = map.getView().getProjection();
		const formatOutput = {
			getLength : line => {
				let length=0;
				let coordinates = line.getCoordinates();

				for (let i = 0, ii = coordinates.length - 1; i < ii; ++i) {
					let c1 = ol.proj.transform(coordinates[i], sourceProj,'EPSG:4326');
					let c2 = ol.proj.transform(coordinates[i + 1], sourceProj,'EPSG:4326');
					length += wgs84Sphere.haversineDistance(c1, c2);
				}
				let output;
				if (length > 100) {
					output = (Math.round(length / 1000 * 100) / 100) + ' ' + 'km';
				} else {
					output = (Math.round(length * 100) / 100) + ' ' + 'm';
				}
				return output;
			},
			getArea : polygon => {
				let area;
				let geom = (polygon.clone().transform(sourceProj, 'EPSG:4326'));
				let coordinates = geom.getLinearRing(0).getCoordinates();
				
				area = Math.abs(wgs84Sphere.geodesicArea(coordinates));
				
				let output;
				if (area > 10000) {
					output = (Math.round(area / 1000000 * 100) / 100) + ' '
							+ 'km<sup>2</sup>';
				} else {
					output = (Math.round(area * 100) / 100) + ' ' + 'm<sup>2</sup>';
				}
				return output;
			},
			getRadius : circle => {
				let length = circle.clone().getRadius();
				
				if (length > 1000) {
					output = (Math.round(length / 1000 * 100) / 100) + ' ' + 'km';
				} else {
					output = (Math.round(length * 100) / 100) + ' ' + 'm';
				}
				return output;
			}
		};
		
		function createMeasureTooltip() {
			measureTooltipElement = document.createElement('div');
			measureTooltipElement.className = 'tooltip tooltip-measure';
			measureTooltip = new ol.Overlay({
				element : measureTooltipElement,
				offset : [ 0, -15 ],
				positioning : 'bottom-center'
			});
			map.addOverlay(measureTooltip);
		}
		addInteraction();
		
	},
	erase : () => {
		map.getLayers().forEach( layer => {
			if(layer.get('name')=='measureVector'){
				map.getInteractions().forEach( inter => {
					if(inter instanceof ol.interaction.Draw){
						inter.setActive(false);
						map.removeInteraction(inter);
					}
				});
				map.removeLayer(layer);
				$(".tooltip-static").parent().remove();
				$(".tooltip-measure").parent().remove();
			}
		});
		$('#measureErase').hide();
	}
	
}
