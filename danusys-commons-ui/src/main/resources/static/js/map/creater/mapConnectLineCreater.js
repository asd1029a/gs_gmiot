/**
 * 맵상 연결선 생성
 * */
class mapConnectLineCreater {
    //target : 적용할 맵 이름
    constructor(target, checkProp) {
        this.map = window[target].map;
        this.rawMap = window[target];
    }
    /*
    * 맵과 div연결 생성
    * coord : 맵 좌표
    * popupname : 연결할 div선택자
    * type
    * */
    create(coord, popupName, type) {
        const container = $(popupName);
        const offset = container.offset();
        const width = container.width();
        const height = container.height();

        let pointIcon = coord;
        let pointVideo = this.map.getCoordinateFromPixel([offset.left+(width/2),offset.top+(height/2)]);

        const lineLayer = new ol.layer.Vector({
            title: type + 'lineLayer',
            source: new ol.source.Vector({
                features: [new ol.Feature({
                    geometry: new ol.geom.LineString([pointVideo,pointIcon]),
                    visible: true
                })]
            })
        });
        const style = new ol.style.Style({
            stroke: new ol.style.Stroke({
                color: '#a9a9a9',
                lineDash : [ 10, 5 ],
                width: 3
            })
        });
        lineLayer.setStyle(style);
        const data = {
            "coord" : coord,
            "popupName" : popupName,
            "type" : type
        }
        lineLayer.setProperties(data);
        return lineLayer;
    }
    /*
    * 연결선 reload
    * name : 레이어 명
    * type
    * */
    reload(type) {
        let data = null;
        this.map.getLayers().forEach( layer => {
            if(layer.get('title')== type + "lineLayer"){
                data = layer.getProperties();
                this.map.removeLayer(layer);
            }
        });
        //레이어 순서(가 필요할때는 creater에 직접 접근해서 addLayer)
        this.rawMap.addLayer(this.create(data.coord, data.popupName, data.type));
        this.map.render();
    }
    /**
     * 연결선 제거
     * */
    remove(type) {
        this.map.getLayers().forEach( layer => {
            const title = layer.get('title');
            if(!type) { // 전체삭제
                if(title.includes("lineLayer")){
                    this.map.removeLayer(layer);
                }
            } else {
                if(title == type + "lineLayer") {
                    this.map.removeLayer(layer);
                }
            }
        });
        this.map.render();
    }

}