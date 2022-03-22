

/**
 * 레이어 조작
 * @param 좌표가 담긴 obj
*/
const layerControl = {
	//해당 레이어 보여지기
	on : layerName => {
		map.getLayers().forEach( layer => {
			if(layer.get('name')==layerName){
				layer.setVisible(true);
			}
		});
	}
	//해당 레이어 숨겨지기
	,off : layerName => {
		map.getLayers().forEach( layer => {
			if(layer.get('name')==layerName){
				layer.setVisible(false);
			}
		});
	}
	//해당 레이어 제거하기
	,remove : layerName => {
		map.getLayers().forEach( layer => {
			if(layer.get('name')==layerName){
				map.removeLayer(layer);
			}
		});
	}
	//해당단어가 포함된 레이어 제거하기
	,removeLike : word => {
		map.getLayers().forEach( layer => {
			if(layer.get('name').indexOf(word)>-1) {
				map.removeLayer(layer);
			}
		});
	}
	/**
	 * 레이어 존재 여부 판단
	 * @param 레이어 이름
	 * @return boolean(존재(true)/미존재(false))
	*/
	,exist :  layerName => {
		let flag = false;
		map.getLayers().forEach( layer => {
			if(layer.get('name')==layerName){
				flag = true;
			}
		});
		return flag;
	}
	/**
	 * 레이어 객체 찾기
	 * @param 레이어 이름
	 * @return 해당 레이어 객체
	*/
	,find :  layerName => {
		let foundLayer;
		map.getLayers().forEach( layer => {
			if(layer.get('name')==layerName){
				foundLayer =  layer;
			}
		});
		return foundLayer;
	}
};


/**
 * 그룹 레이어 조작 (middleLayer)
 */
const groupLayerControl = {
	//그룹안 모든 레이어 보여지기
	allOn : groupName => {
		const layerGroup = layerControl.find(groupName);
		layerGroup.getLayers().forEach( layer => layer.setVisible(true) );
		map.renderSync();
	}
	//그룹안 모든 레이어 숨겨지기
	,allOff : groupName => {
		const layerGroup = layerControl.find(groupName);
		layerGroup.getLayers().forEach(layer => layer.setVisible(false) );
		map.renderSync();
	}
	//그룹안 모든 레이어 제거하기
	,allRemove : groupName => {
		map.removeLayer(layerControl.find(groupName));
		map.renderSync();
	}
	/**
	 * 그룹안 해당 레이어 보여지기
	 * @param groupName 그룹레이어 이름
	 * @param layerName 보여질 그룹안 레이어 이름
	 */
	,on : (groupName,layerName) => {
		const layerGroup = layerControl.find(groupName);
		layerGroup.getLayers().forEach( layer => {
			if(layer.get('name')==layerName){
				layer.setVisible(true);
			}
		});
		map.renderSync();
	}
	/**
	 * 그룹안 해당 레이어 숨겨지기
	 * @param groupName 그룹레이어 이름
	 * @param layerName 숨겨질 그룹안 레이어 이름
	 */
	,off : (groupName,layerName) => {
		const layerGroup = layerControl.find(groupName);
		layerGroup.getLayers().forEach( layer => {
			if(layer.get('name')==layerName){
				layer.setVisible(false);
			}
		});
		map.renderSync();
	}
	/**
	 * 그룹레이어 안에 레이어 추가하기
	 * @param groupName 그룹레이어 이름
	 * @param layer 추가할 레이어 객체
	 */
	,add : (groupName,layer) => {
		const ary = layerControl.find(groupName).getLayers().getArray();
		ary.push(layer);
		map.renderSync();
	}
	/**
	 * 그룹레이어 안에 레이어 제거하기
	 * @param groupName 그룹레이어 이름
	 * @param layerName 제거할 레이어 이름
	 */
	,remove : (groupName,layerName) => {
		const ary = layerControl.find(groupName).getLayers().getArray();
		if(ary.length>0){
			for(let i=0; i<ary.length; i++){
				if(ary[i].get('name')==layerName){
					ary.splice(i,1);
				}
			}
		}
		map.renderSync();
	}
};

/**
 * 좌표계 설정
 * */
const olProjection = {
	addProjection : (epsg,param) => {
		proj4.defs(epsg,param);
		ol.proj.setProj4 = proj4;
	},
	createProjection : (code,extent) => {
		const makedProjection = new ol.proj.Projection({
			code : code,
			extent : extent,
			units : 'm'
		});
		return makedProjection;
	}
};

olProjection.addProjection('EPSG:5181',"+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=500000 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs");
olProjection.addProjection('EPSG:5174',"+proj=tmerc +lat_0=38 +lon_0=127.0028902777778 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +units=m +no_defs +towgs84=-126.80,477.99,665.11,1.16,-2.31,-1.63,6.43");


