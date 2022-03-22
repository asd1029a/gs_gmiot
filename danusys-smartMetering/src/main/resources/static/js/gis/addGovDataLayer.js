/**
 * data : (JSON) 표출할 json
 * layerName : (String) 표출될 레이어명
 * iconSrc : (String) 아이콘 주소
 * prjFlag : (Boolean) 좌표변환 처리 유무  
 */
let cctvClusterStyle;
let presetStyle;
const clusterFunc = (feature, resolution) => {
	// 즐겨찾기인가 아닌가
//	let cctvLabel = feature.getProperties().features[0].getProperties().mgmtNo.replace(/[^0-9.]/gi,"");
	const reg = new RegExp(/([0-9]{3})/g);
	const mgmtNo = feature.getProperties().features[0].getProperties().mgmtNo;
	//let cctvLabel = reg.exec(mgmtNo)[0]; 
	let cctvLabel = mgmtNo;
	//줌별 카메라 연별 표시
	if(map.getView().getZoom()>10){
		//cctvLabel = cctvLabel.replace(/(^0+)/,""); 
	} else {
		cctvLabel = ""; 
	}
	const bookMarkFlag = feature.getProperties().features[0].getProperties().favorYn;
	let keys = "useCd"+feature.getProperties().features[0].getProperties().fcltUseCd+"_";
	
	keys = keys + bookMarkFlag+"_Sel";
	let iconScale = 0.7; 
	if(bookMarkFlag=="Y"){
		iconScale = 0.8;
	}
	
	if(!cctvImgObj[keys]){
		console.log('알수없는 CCTV 유형이 포함되어있습니다. 확인 바랍니다.');
		keys= "undefined";
		iconScale = 1;
	}
	
	const OneStyle = new ol.style.Style({
		image: new ol.style.Icon({
			anchor:[0.5,0.5],
			anchorXUnits: 'fraction',
			anchorYUnits: 'fraction',
			img: cctvImgObj[keys],
			imgSize:[50,50],
			scale: iconScale
		}),
		text: new ol.style.Text({
            scale: 1.8,
            text: cctvLabel,
            fill: new ol.style.Fill({ color: '#000', width: 5 }),
			font: 'Bold 10px Arial',
			stroke: new ol.style.Stroke({ color: 'white', width: 1 }),
            offsetY: 25
        })
	});
	return OneStyle;
};

const makeLayer = {	
	fromGeoJSON : (data,layerName,iconSrc,prjFlag) => {
		//geojson
		const apiSource = new ol.source.Vector();
		const apiFeatures = new ol.format.GeoJSON().readFeatures(data);
		
		if(prjFlag){
			apiFeatures.forEach( ee => {
				ee.getGeometry().transform('EPSG:4326',baseProjection);
			});
		}
		apiSource.addFeatures(apiFeatures);
		let size =0.8;
		const sizeCctv = 0.5
		
		let style;
		if((layerName=='accountLayer')){
//			style = feature => {
//				//새로운 이벤트여부 
//				const eventType = feature.getProperties().eventType; 
//				
//				const eventKind = feature.getProperties().eventKind;
//				let imageIconSrc = iconSrc;
//				if(eventKind=="비상벨") {
//					imageIconSrc = img_emer_old;
//					if(eventType) {
//						imageIconSrc = img_emer_new;
//					}
//				} else if(eventKind=="안전귀가"){
//					imageIconSrc = img_safety_old;
//					if(eventType) {
//						imageIconSrc = img_safety_new;
//					}
//				}
//				const st = new ol.style.Style({
//					image: new ol.style.Icon({
//						anchor:[0.5,0],
//						anchorXUnits: 'fraction',
//						anchorYUnits: 'fraction',
//						img: imageIconSrc,
//						imgSize:[50,50],
//						scale: 0.8
//					})
//				});
//				return st;
//			}
			
					
			style = new ol.style.Style({
				image: new ol.style.Circle({
					radius:7,
					stroke: new ol.style.Stroke({ 
						color:'rgba(255,255,255,1)',
						width: 1,
					}),
					fill: new ol.style.Fill({
						color: '#3579b1',
					})
				})
//				,text: new ol.style.Text({
//					text: '',
//					fill: new ol.style.Fill({
//						color:'white',
//						font: '13px'
//					})
//				})
			});
		} else if((layerName=='eventLayer')){
			style = new ol.style.Style({
				image: new ol.style.Icon({
					anchor:[0.19,0.55],
					anchorXUnits: 'fraction',
					anchorYUnits: 'fraction',
					img: img_event,
					imgSize:[50,50],
					scale: 1.5
				})
			});
		} else if((layerName=='accountCntLayer')){
			style = feature => {
				const accountCnt = feature.getProperties().accountCnt;
				
				const st = new ol.style.Style({
					image: new ol.style.Circle({
						radius:15,
						stroke: new ol.style.Stroke({ 
							color:'rgba(255,255,255,1)',
							width: 1.5,
						}),
						fill: new ol.style.Fill({
							color: '#3579b1',
						})
					})
					,text: new ol.style.Text({
						
						offsetX: -0.8,
						text: accountCnt,
						fill: new ol.style.Fill({
							color:'white'
						}),
						scale: 2
					})
				});
				
				return st;
			} 
				

		} else {
			style = new ol.style.Style({
				image: new ol.style.Icon(({
					anchor:[0,0],
					anchorXUnits: 'fraction',
					anchorYUnits: 'fraction',
					//rotation: Math.PI/1.3,
					src: iconSrc,
					scale:size
				})),
				stroke: new ol.style.Stroke({	
					color: 'red',
					width: 2
				}),
				fill: new ol.style.Fill({	
					color: 'red'
				})
			});
		}
		
		
		const pointsLayer = new ol.layer.Vector({ 
			source: apiSource,
			//'displayInLayerSwitcher': false,
			name : layerName,
			style: style 
		});
		//map.addLayer(pointsLayer);
		return pointsLayer;
	},
	toCluster : (data,layerName,iconSrc,prjFlag) => {
		let clusters;
		const wfsSource = new ol.source.Vector();
		let wfsFeatures;
		wfsFeatures = new ol.format.GeoJSON().readFeatures(data);
		
		//위경도 -> 지도 좌표 
		if(prjFlag){
			wfsFeatures.forEach( each => {
				each.getGeometry().transform('EPSG:4326',baseProjection);
			});		
		}
	
		wfsSource.addFeatures(wfsFeatures);
		
		const styleCacheOne = {};
		const styleCacheMul = {};
		if(layerName=='tempLayer'){
			const clusterSource = new ol.source.Cluster({
				distance: 0,
				source: wfsSource
			});
			clusters = new ol.layer.AnimatedCluster({
				// title: 'Cctv Clusters',
				animationDuration:0,
				name: layerName,
				source: clusterSource,
				//visible: true,
				style: (feature, resolution) => {
					const size = feature.get('features').length;
				
					mulStyle = styleCacheMul[size];
					if(!mulStyle){
						mulStyle = [new ol.style.Style({
							image: new ol.style.Circle({
								radius:13,
								stroke: new ol.style.Stroke({ 
									color:'rgba(255,255,255,1)',
									width: 2,
								}),
								fill: new ol.style.Fill({
									color: 'cornflowerblue',
								})
							}),
							text: new ol.style.Text({
								text: size.toString(),
								fill: new ol.style.Fill({
									color:'white',
									font: '13px'
								})
							})
						})];
						styleCacheMul[size] = mulStyle;
					}
					return mulStyle;
				}
			});
		} else {
			const clusterSource = new ol.source.Cluster({
				distance: 50,
				source: wfsSource
			});
			styleClusterStyle = (feature, resolution) => {
				//권한 없는 이미지 (추후)
				//console.log(feature.getProperties().features[0].getProperties().permit);
				const size = feature.get('features').length;
				
				if(size==1){
					// 즐겨찾기인가 아닌가
//					let cctvLabel = feature.getProperties().features[0].getProperties().mgmtNo.replace(/[^0-9.]/gi,""); 
					const reg = new RegExp(/([0-9]{3})/g);
					const mgmtNo = feature.getProperties().features[0].getProperties().mgmtNo;
					//let cctvLabel = reg.exec(mgmtNo)[0]; 
					let cctvLabel = mgmtNo;
					//줌별 카메라 연별 표시
					if(map.getView().getZoom()>10){
//						cctvLabel = cctvLabel.replace(/(^0+)/,""); 
					} else {
						cctvLabel = ""; 
					}
					
					const bookMarkFlag = feature.getProperties().features[0].getProperties().favorYn;
					let keys = "useCd"+feature.getProperties().features[0].getProperties().fcltUseCd+"_";
					
					keys = keys + bookMarkFlag;
					let OneStyle = styleCacheOne[keys];
					let iconScale = 0.7; 
					if(bookMarkFlag=="Y"){
						iconScale = 0.8;
					}
					
					if(!cctvImgObj[keys]){
						console.log('알수없는 CCTV 유형이 포함되어있습니다. 확인 바랍니다.');
						keys= "undefined";
						iconScale = 1;
					}
					if(!OneStyle){
						OneStyle = [new ol.style.Style({
							image: new ol.style.Icon({
								anchor:[0.5,0.5],
								anchorXUnits: 'fraction',
								anchorYUnits: 'fraction',
								img: cctvImgObj[keys],
								imgSize:[50,50],
								scale: iconScale
							}),
							text: new ol.style.Text({
					            scale: 1.8,
					            text: cctvLabel,
					            fill: new ol.style.Fill({ color: '#000', width: 5 }),
								font: 'Bold 10px Arial',
								stroke: new ol.style.Stroke({ color: 'white', width: 1 }),					            
								offsetY: 25
					        })
						})];
						
						styleCacheOne[cctvLabel] = OneStyle;
					}
					return OneStyle;
				}
				else {
					mulStyle = styleCacheMul[size];
					if(!mulStyle){
						mulStyle = [new ol.style.Style({
							image: new ol.style.Circle({
								radius:16.5,
								stroke: new ol.style.Stroke({ 
									color:'rgba(255,255,255,1)',
									width: 1,
								}),
								fill: new ol.style.Fill({ 
									color: '#888888',//'#3795FF',
								})
							}),
							text: new ol.style.Text({ 
								text: size.toString(),
								fill: new ol.style.Fill({
									color:'white',
								}),
								scale: 1.8
							})
						})];
						styleCacheMul[size] = mulStyle;
					}
					return mulStyle;
				}
			};
			clusters = new ol.layer.Vector({
				// title: 'Cctv Clusters',
				name: layerName,
				source: clusterSource,
				visible: true,
				style: styleClusterStyle
			});
		}
		return clusters;
	},
	fromRaw : (data,layerName,iconSrc,prjFlag) => {
		
		const featureCollection = new ol.Collection();
		
		let rnum = 0;
		
		data.data.forEach( each => {
			let coordinates = [Number(each.longitude),Number(each.latitude)];
			if(prjFlag){
				coordinates = ol.proj.transform(coordinates,'EPSG:4326',baseProjection);
			}
			
			if(layerName=='favorLayer'){
				const type = each.favorType;
				if(type!="C"){
					const featureOne = new ol.Feature({
						geometry: new ol.geom.Point(coordinates),
						properties : each
					});
					featureCollection.push(featureOne);
				}
			} else  {
				rnum++;
				each.order = rnum;
				const featureOne = new ol.Feature({
					geometry: new ol.geom.Point(coordinates),
					properties : each,
				});
				
				featureCollection.push(featureOne);
			} 
		});

		let style;
		if(layerName=='favorLayer'){
			style = (feature,layer) => {
				const res = new ol.style.Style({
					image: new ol.style.Icon({
						anchor:[0.5,0.5],
						anchorXUnits: 'fraction',
						anchorYUnits: 'fraction',
						img: img_favor_yellow,
					    imgSize:[50,50],
						scale: 0.5
					})
				});
				return res;
			}
		} else {
			style = new ol.style.Style({
				image: new ol.style.Icon(({
					anchor:[0.5,0.5],
					anchorXUnits: 'fraction',
					anchorYUnits: 'fraction',
					src: iconSrc
				})),
				stroke: new ol.style.Stroke({	
					color: 'red',
					width: 2
				}),
				fill: new ol.style.Fill({	
					color: 'red'
				})
			});
		}
		
		const pointsLayer = new ol.layer.Vector({ 
			source: new ol.source.Vector({features:featureCollection}),
			name : layerName,
			style:style
		});
		
		return pointsLayer;
	}
	,fromFeatures : (data,layerName,iconSrc,prjFlag) => {
		
		const apiSource = new ol.source.Vector();
		
		if(prjFlag){
			data.forEach( ee => {
				ee.getGeometry().transform('EPSG:4326',baseProjection);
			});
		}
		
		apiSource.addFeatures(data);
		
		const pointsLayer = new ol.layer.Vector({ 
			source: apiSource,
			name : layerName,
			style: new ol.style.Style({
				image: new ol.style.Icon({
					anchor:[0.5,0.5],
					anchorXUnits: 'fraction',
					anchorYUnits: 'fraction',
					img: img_park,
				    imgSize:[50,50],
					scale: 0.7
				})
			})
		});
		return pointsLayer;
		//map.addLayer(pointsLayer);
	}
}

 


