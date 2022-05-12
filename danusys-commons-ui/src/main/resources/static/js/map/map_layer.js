/**
 * @summary  점 데이터 obj로 layer 반환
 * */
class dataLayer {
    constructor(target){
        this.map = window[target];
    }
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
                feature.getGeometry().transform('EPSG:4326', this.map.projection);
            });
        }
        source.addFeatures(features);

        const pointsLayer = new ol.layer.Vector({
            source : source,
            title : layerName,
            //name : layerName,
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

        if(data.length > 0){
            data.forEach( each => {
                let coordinates = [Number(each.longitude), Number(each.latitude)];
                if(projFlag){
                    coordinates = ol.proj.transform(coordinates, 'EPSG:4326', this.map.projection);
                }
                const featureOne = new ol.Feature({
                    geometry: new ol.geom.Point(coordinates),
                    properties : each
                });
                featureCollection.push(featureOne);

            });
        }
        const pointsLayer = new ol.layer.Vector({
            source : new ol.source.Vector({features: featureCollection}),
            title : layerName,
            style : style
        });

        return pointsLayer;
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
                each.getGeometry().transform('EPSG:4326',this.map.projection);
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
    toCluster(data, layerName, projFlag, style) {
        let clusters;
        const wfsSource = new ol.source.Vector();
        let wfsFeatures = new ol.format.GeoJSON().readFeatures(data);

        if(projFlag){
            wfsFeatures.forEach( each => {
                each.getGeometry().transform('EPSG:4326',this.map.projection);
            });
        }

        wfsSource.addFeatures(wfsFeatures);

        const clusterSource = new ol.source.Cluster({
            distance : 30,
            source: wfsSource,
            // createCluster: (p,l) => {
            //     console.log(p,l);
            //     return new ol.Feature({
            //         geometry: p,
            //         features: l
            //     });
            // }
        });

        clusters = new ol.layer.Vector({
            // title: 'Cctv Clusters',
            title: layerName,
            source: clusterSource,
            visible: true,
            style: style
        });

        return clusters;
    }

    /**
     * @summary fromGeoJson toRoute
     * @param data: 데이터 Obj, layerName : 레이어명, projFlag : 좌표변환여부, style : 레이어스타일
     * @return multiLayer
     * */
    fromGeoJsonToRoute(data, layerName, projFlag, style) {
        const featureCollection = new ol.Collection();

        //point
        data.features.forEach(each => {
            let coordinates = ol.proj.transform(each.geometry.coordinates,'EPSG:4326',this.map.projection);
            const featureOne = new ol.Feature({
                geometry: new ol.geom.Point(coordinates),
                properties : each,
            });
            featureCollection.push(featureOne);
        });//point end

        //line
        for(let i=0; i<data.features.length;i++){
            const features = data.features;
            //동선이 하나여서 점일때
            if(features.length==1){
                const point = new ol.proj.transform(data.features[0].geometry.coordinates,"EPSG:4326",this.map.projection);
            //vertex가 두 개 이상일때
            } else {
                if(i>0){
                    const from = new ol.proj.transform(data.features[i-1].geometry.coordinates,"EPSG:4326",this.map.projection);
                    const to = new ol.proj.transform(data.features[i].geometry.coordinates,"EPSG:4326",this.map.projection);
                    const featureLine = new ol.Feature({
                        geometry: new ol.geom.LineString([from,to])
                    });
                    featureCollection.push(featureLine);
                }
            }
        }// line end

        const routeLayer = new ol.layer.Vector({
            source : new ol.source.Vector({features: featureCollection}),
            title: layerName,
            visible: true,
            style: style
        });

        return routeLayer;

    }
}
