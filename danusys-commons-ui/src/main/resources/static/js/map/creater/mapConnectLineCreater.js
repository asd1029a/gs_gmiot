/**
 * 맵상 연결선 생성
 * */
class mapConnectLineCreater {
    //target : 적용할 맵 이름
    constructor(target) {
        this.map = window[target].map;
    }
    /*
    * 맵과 div연결 생성
    * coord : 맵 좌표
    * popupname : 연결할 div선택자
    * */
    create(coord, popupname) {
        const container = $(popupname);
        const offset = container.offset();
        const width = container.width();
        const height = container.height();

        let pointIcon = coord;
        let pointVideo = this.map.getCoordinateFromPixel([offset.left+(width/2),offset.top+(height/2)]);

        const lineLayer = new ol.layer.Vector({
            name: 'LineLayer',
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
        return lineLayer;
    }

    /*
    * name : 레이어 명
    * */
    reload(name) {
        this.map.getLayers(name).remove
    }

}