
/**
 * @summary  점 데이터 obj로 layer 반환
 * */
class dataLayer {
    /**
     * @summary GeoJson 데이터로 벡터레이어 생성
     * @param data: 데이터 Obj, layerName : 레이어명, projFlag : 좌표변환여부, style : 레이어스타일
     * @return pointsLayer
     * */
    fromGeoJSon(data, layerName, projFlag, style) {
        const source = new ol.source.Vector();
        const features = new ol.format.GeoJSON().readFeatures(data);
    
        if(projFlag){
            features.forEach( feature => {
                e.getGeometry().transform('EPSG:4326', basePropjection);
            });
        }

        source.addFeatures(features);

        const pointsLayer = new ol.layer.Vector({
            source : source,
            name : layerName,
            style : style
        });

        return pointsLayer;
    }

    /**
     * @summary Json 데이터로 벡터 레이어 생성
     * @param data: 데이터 Obj, layerName : 레이어명, projFlag : 좌표변환여부, style : 레이어스타일
     * @return pointsLayer
     * */
    fromRaw(data, layerName, projFlag, style) {
        const featureCollection = new ol.Collection();

        let rnum  = 0;

        data.forEach( each => {
            let coordinates = [Number(each.longitude), Number(each.latitude)];
            if(projFlag){
                coordinates = ol.proj.transform(coordinates, 'EPSG:4326', baseProjection);
            }
            const featureOne = new ol.Feature({
                geometry: new ol.geom.Point(coordinates),
                properties : each
            });
            featureCollection.push(featureOne);

            const pointsLayer = new ol.layer.Vector({
                source : source,
                name : layerName,
                style : style
            });

            return pointsLayer;
        });
    }

    /**
     * @summary features로 데이터 벡터 레이어 생성
     * @param data: 데이터 Obj, layerName : 레이어명, projFla g : 좌표변환여부, style : 레이어스타일
     * @return pointsLayer
     * */
    fromFeatures(data, layerName, projFlag, style) {
        const source = new ol.source.Vector();

        if(projFlag){
            data.forEach( each => {
                each.getGeometry().transform('EPSG:4326',baseProjection);
            });
        }

        source.addFeatures(data);

        const pointsLayer = new ol.layer.Vector({
            source: source,
            name : layerName,
            style: style
        });

        return pointsLayer;
    }

    /**
     * @summary fromGeoJson toCluster
     * @param data: 데이터 Obj, layerName : 레이어명, projFlag : 좌표변환여부, style : 레이어스타일
     * @return clusterLayer
     * */
    toCluster(data, layerName, projFlag) {
        let clusters;
        const wfsSource = new ol.source.Vector();
        let wfsFeatures = new ol.format.GeoJSON().readFeatures(data);

        if(projFlag){
            wfsFeatures.forEach( each => {
                each.getGeometry().transform('EPSG:4326',baseProjection);
            });
        }

        wfsSource.addFeatures(wfsFeatures);

        //const styleCacheOne = style;
        //const styleMulty = style;
        const clusterSource = new ol.source.Cluster({
            distance : 0,
            source: wfsSource
        });

        clusters = new ol.layer.AnimatedCluster({
            // title: 'Cctv Clusters',
            animationDuration:0,
            name: layerName,
            source: clusterSource,
            //visible: true,
            style: style
        });

        clusters = new ol.layer.Vector({
            // title: 'Cctv Clusters',
            name: layerName,
            source: clusterSource,
            visible: true,
            style: styleClusterStyle
        });

        return clusters;
    }
    
}

/**
 * @summary 스타일 생성
 */
class layerStyle {
    cctvStyle() {
        const style = new ol.style.Style({
            image: new ol.style.Icon({
                nchor:[0.5,0.5],
                anchorXUnits: 'fraction',
                anchorYUnits: 'fraction',
                img: cctvImgObj[keys],
                imgSize:[50,50],
                scale: iconScale
            }),
            text: new ol.style.Text({
                fill: new ol.style.Fill({ color: '#000', width: 5 }),
                font: 'Bold 10px Arial',
                stroke: new ol.style.Stroke({ color: 'white', width: 1 }),
                offsetY: 25
            })
        })
        return style;
    }
    //clusterSty




   // function (feature, resolution) => {
   //      const size = feature.get('features').length;
   //
   //      mulStyle = styleCacheMul[size];
   //      if(!mulStyle){
   //      mulStyle = [new ol.style.Style({
   //          image: new ol.style.Circle({
   //              radius:13,
   //              stroke: new ol.style.Stroke({
   //                  color:'rgba(255,255,255,1)',
   //                  width: 2,
   //              }),
   //              fill: new ol.style.Fill({
   //                  color: 'cornflowerblue',
   //              })
   //          }),
   //          text: new ol.style.Text({
   //              text: size.toString(),
   //              fill: new ol.style.Fill({
   //                  color:'white',
   //                  font: '13px'
   //              })
   //          })
   //      })];
   //      styleCacheMul[size] = mulStyle;
   //  }
   //      return mulStyle;
   //  }
}





//레이어 만들기
// jsonObj => layer
// fromGeoJson
// fromRaw
// fromFeatures
// toCluster

//스타일
//function style
//text style
//Icon style
//Circle style
