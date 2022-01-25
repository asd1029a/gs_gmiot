/**
 * 지도 측정 Tool
 * */
class measureTool {

    /**
     * @param target : 레이어 적용할 맵명
     * */
    constructor(target){
        this.map = window[target].map;

        this.style = {
            /* 측정후 스타일 */
            measured : new ol.style.Style({
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
            /* 측정중 스타일 */
            , measuring : new ol.style.Style({
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
        };

        this.sourceProj = this.map.getView().getProjection();

        /* 측정 방식 */
        this.formatOutput = {
            /* 길이 구하기 */
            getLength : line => {
                let length=0;
                let coordinates = line.getCoordinates();

                for (let i = 0, ii = coordinates.length - 1; i < ii; ++i) {
                    let c1 = ol.proj.transform(coordinates[i], this.sourceProj,'EPSG:4326');
                    let c2 = ol.proj.transform(coordinates[i + 1], this.sourceProj,'EPSG:4326');
                    length += ol.sphere.getDistance(c1, c2, 6378137); //, 6378137
                }
                let output;
                if (length > 100) {
                    output = (Math.round(length / 1000 * 100) / 100) + ' ' + 'km';
                } else {
                    output = (Math.round(length * 100) / 100) + ' ' + 'm';
                }
                return output;
            },
            /* 면적 구하기 */
            getArea : polygon => {
                let area;
                let geom = polygon.clone().transform(this.sourceProj, 'EPSG:4326');

                let coordinates = geom.getLinearRing(0).getCoordinates();

                area = ol.sphere.getArea(polygon, 6378137);//ol.sphere.getArea(geom, 6378137);

                let output;
                if (area > 10000) {
                    output = (Math.round(area / 1000000 * 100) / 100) + ' '
                        + 'km<sup>2</sup>';
                } else {
                    output = (Math.round(area * 100) / 100) + ' ' + 'm<sup>2</sup>';
                }
                return output;
            },
            /* 반경 구하기 */
            getRadius : circle => {
                let length = circle.clone().getRadius();

                let output;
                if (length > 1000) {
                    output = (Math.round(length / 1000 * 100) / 100) + ' ' + 'km';
                } else {
                    output = (Math.round(length * 100) / 100) + ' ' + 'm';
                }
                return output;
            }
        };

        this.measureTooltipElement = null;
        this.measureTooltip = null;

    }

    createMeasureToolTip() {
        /* 툴팁 구성 */
        this.measureTooltipElement = document.createElement('div');
        this.measureTooltipElement.className = 'tooltip tooltip-measure';
        this.measureTooltipElement.style.color = 'black';
        this.measureTooltip = new ol.Overlay({
            element : this.measureTooltipElement,
            offset : [ 0, -15 ],
            positioning : 'bottom-center'
        });
        this.map.addOverlay(this.measureTooltip);
    }

    removeMeasureTool() {
        debugger;
        this.map.getLayers().forEach( layer => {
            if(layer.get('name')=='measureVector'){
                this.map.getInteractions().forEach( inter => {
                    if(inter instanceof ol.interaction.Draw){
                        inter.setActive(false);
                        this.map.removeInteraction(inter);
                    }
                });
                this.map.removeLayer(layer);
                $(".tooltip-static").parent().remove();
                $(".tooltip-measure").parent().remove();
            }
        });
    }

    /**
     * @param type : 폴리곤 타입  Polygon | LineString | Circle
     * */
    initDraw(type) {

        this.removeMeasureTool();
        /* 드로우 객체  */
        let source = new ol.source.Vector({wrapX: false});
        let vector = new ol.layer.Vector({
            title : 'measureVector',
            'displayInLayerSwitcher' : false,
            source : source,
            style : this.style.measured
        });
        this.map.addLayer(vector);

        let draw = new ol.interaction.Draw({
            title : 'measureDraw',
            source : source,
            type : type,
            style : this.style.measuring
        });
        this.map.addInteraction(draw);

        this.createMeasureToolTip();

        //////////////////////////////////
        let sketch;
        let listener;

        draw.on('drawstart', event => {
            sketch = event.feature;

            let tooltipCoord = event.coordinate;

            listener = sketch.getGeometry().on('change', evt => {
                let geom = evt.target;
                let output;
                if (geom instanceof ol.geom.Polygon) {
                    output = this.formatOutput.getArea(geom);
                    tooltipCoord = geom.getInteriorPoint().getCoordinates();

                } else if (geom instanceof ol.geom.LineString) {
                    output = this.formatOutput.getLength(geom);
                    tooltipCoord = geom.getLastCoordinate();

                } else if (geom instanceof ol.geom.Circle){
                    output = this.formatOutput.getRadius(geom);
                    tooltipCoord = geom.getLastCoordinate();
                } else {
                    alert("그리기 타입오류");
                }
                this.measureTooltipElement.innerHTML = output;
                this.measureTooltip.setPosition(tooltipCoord);

            });
        }, this);

        draw.on('drawend', event => {
            this.measureTooltipElement.className = 'tooltip tooltip-static';
            this.measureTooltip.setOffset([ 0, -7 ]);

            sketch = null;
            this.measureTooltipElement = null;

            this.createMeasureToolTip();
            ol.Observable.unByKey(listener);

            draw.setActive(false);
            this.map.removeInteraction(draw);
            //
            // $(".map_measure").children().map( (i,v) => {
            //     $(v).removeClass('on');
            // });

        }, this);


    }





}