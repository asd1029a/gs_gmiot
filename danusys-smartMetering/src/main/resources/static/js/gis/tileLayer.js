/**
 * 타일 레이어 관리 
 */

//지도 타일 생성
const customTile = {
	//다음 타일 생성
	createDaumTile: (mode, inputName, inputVisible) => {
		let makedLayer;
		
		makedLayer = new ol.layer.Tile({
			name: inputName,
			visible: inputVisible,
			type: 'base',
			source: new ol.source.XYZ({
		        projection: olProjection.createProjection(daumEPSG,daumExtent),
		        tileSize: 256,
		        minZoom: 0,
		        maxZoom: resolutions.length - 1,
		        tileGrid: new ol.tilegrid.TileGrid({
		            origin: [daumExtent[0], daumExtent[1]],
		            resolutions: resolutions
		        }),
		        tileUrlFunction: (tileCoord, pixelRatio, projection) => {
		            if (tileCoord == null) return undefined;
		            
		            //var s = Math.floor(Math.random() * 4);  
		            var z = resolutions.length - tileCoord[0];
		            
		            var x = tileCoord[1];
		            var y = tileCoord[2];
		            
		            if(mode=='imageTile'){
		            	return 'http://map.daumcdn.net/map_2d_hd/1912uow/L' + z + '/' + y + '/' + x + '.png';
		            } 
		            else if(mode=='sky0Tile'){
		            	return 'http://map.daumcdn.net/map_skyview_hd/L' + z + '/' + y + '/' + x + '.jpg';	            	
		            } 
		            else if(mode=='labelTile'){
		            	return 'https://map.daumcdn.net/map_hybrid_hd/1912uow/L' + z + '/' + y + '/' + x + '.png';	            	
		            } 
				// else if(mode=='trafficTile'){
				// return 'https://r' + s + '.maps.daum-img.net/mapserver/file/realtimeroad/L' +
				// z + '/' + y + '/' + x + '.png';
				// } else if(mode=='pm10Tile'){
				// return 'https://airinfo.map.kakao.com/mapserver/file/airinfo_pm10/T/L' + z +
				// '/' + y + '/' + x + '.png';
					else if(mode=='roadviewTile'){
						 return 'https://map.daumcdn.net/map_roadviewline/7.00/L' + z + '/' + y + '/' + x + '.png';
					}
		            else {
		            	console.log('DaumTile오류');
		            }
		        },
		        attributions: [
		            new ol.Attribution({ 
		                html: ['<a href="http://map.daum.net"><img src="http://i1.daumcdn.net/localimg/localimages/07/mapjsapi/m_bi.png"></a>']
		            })
		        ]
		    })
			
		});
		
		return makedLayer;
	},
	//지오서버 타일 생성
	/**layerName : 서버내 레이어 이름**/
	createTile: (layerName,inputName,inputVisible) =>  {
		let tile;
		//로컬에 캐시이미지가 있을시
		if((layerName=='topoMap')||(layerName=='gm_satellite2')){ 
			//resolutions 
			const size = ol.extent.getWidth(baseExtent) / 256;
			const matrixIds = ['Daum:EPSG:5181:'+0,'Daum:EPSG:5181:'+1,'Daum:EPSG:5181:'+2,'Daum:EPSG:5181:'+3,
				'Daum:EPSG:5181:'+4,'Daum:EPSG:5181:'+5,'Daum:EPSG:5181:'+6,'Daum:EPSG:5181:'+7,'Daum:EPSG:5181:'+8,
				'Daum:EPSG:5181:'+9,'Daum:EPSG:5181:'+10,'Daum:EPSG:5181:'+11,'Daum:EPSG:5181:'+12,'Daum:EPSG:5181:'+13];			
			
			const topoTile = new ol.layer.Tile({
				name: inputName,
				extent : baseExtent,
				source: new ol.source.WMTS({
					url:geoServerUrl+'/gwc/service/wmts',
					layer:'gmwork:'+layerName,
					matrixSet:'Daum:EPSG:5181',
					format:'image/png',
					tileSize: 256,
					projection: baseProjection,
					tileGrid: new ol.tilegrid.WMTS({
						//origin:ol.extent.getTopLeft(baseExtent),
						origin: [baseExtent[0], baseExtent[3]],
						resolutions: resolutions,
						matrixIds : matrixIds
					}),
					extent: baseExtent,
					style: ''
				}),
				visible: inputVisible
			});
			tile = topoTile;
			
		} else {
		//그냥 레이어 일때
			const basicTile = new ol.layer.Tile({
				name: inputName,
				source: new ol.source.TileWMS({
					url: geoServerUrl,
					params: {
						'FORMAT': 'image/png',
						'VERSION': '1.1.0',
						tiled: true,
						'LAYERS':layerName,
						'exceptions': 'application/vnd.ogc.se_inimage'
							,tilesOrigin:199950 + "," + 497291.11000000004
					},
					serverType: 'geoserver'
				}),
				visible: inputVisible
			});
			tile = basicTile;
		}
		
		return tile;
	},
	//지오서버 행정동 생성
	createBoundaryTile: () =>  {
		let tile;
		let obj = {
			"url" : "/sys/getListUseSigunCode.ado"
			, "type" : "POST"
			, "data" : {}
			, "async": false
		}
		comm.ajaxPost(obj, function(resultData) {
			
			let codeAry = [];
			let codeStr = "";
			let boundType = "";
			
			const data = resultData.data;
			if(data.length>0){
				boundType = data[0].boundaryType;
				
				data.forEach( each => {
					const code = each.sggCd;
					if(codeAry.indexOf(code) == -1){
						codeAry.push(code);
						codeStr += "'" + code + "',";
					}
				});

				codeStr = codeStr.substr(0, codeStr.length -1);
				
				const boundTile = new ol.layer.Tile({
					name: 'emdBoundaryLayer',
					source: new ol.source.TileWMS({
						url: geoServerUrl,
						params: {
							'FORMAT': 'image/png',
							'VERSION': '1.1.0',
							tiled: true,
							'LAYERS': 'EMD_'+ boundType +'_GROUP',
			                'CQL_FILTER': "SGG_CD IN ("+ codeStr +")",
							'exceptions': 'application/vnd.ogc.se_inimage'
								,tilesOrigin:199950 + "," + 497291.11000000004
						},
						serverType: 'geoserver'
					}),
					visible: true
				});
				tile = boundTile;
				
				baseSigCode = data[0].siCd;
				baseSigNm = data[0].searchKeyword;
				
			} else {
				const basicTile = 
				new ol.layer.Tile({
					name: 'emdBoundaryLayer',
					source: new ol.source.TileWMS({})
				});
				tile = basicTile;
			}
		});
		return tile;		
	} 
};