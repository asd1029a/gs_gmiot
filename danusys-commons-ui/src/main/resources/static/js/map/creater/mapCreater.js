proj4.defs("EPSG:5181","+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=500000 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs");
proj4.defs("EPSG:5179","+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs");
ol.proj.proj4.register(proj4);

/**
 * 0: daum, 1: naver, 2:vworld
 * */
class mapCreater {
    'use strict'
    // default values
    resolutions = [
        [2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25], // daum
        [2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25], // naver,
        undefined, // vworld
    ];
    extents = [
        [-30000, -60000, 494288, 988576], // daum
        [90112, 1192896, 2187264, 2765760], // naver
        undefined // vworld
    ];
    defaultZoom = [
        9.5, // daum
        undefined, // naver
        15, // vworld
    ];
    maxZoom = [
        13, // daum
        undefined, //naver
        19, //vworld
    ];
    minZoom = [
        0, // daum
        undefined, // naver
        7, // vworld
    ];
    zoomFactor = [
        1, // daum
        undefined, // naver
        undefined// vworld
    ];
    realProjection = [
        'EPSG:5181', // daum
        'EPSG:5179', // naver
        'EPSG:3857', // vworld
    ];
    urls = [
        [
            {
                name : 'base',
                nameKo : '이미지',
                prefix : 'http://map.daumcdn.net/map_2d_hd/1909dms/L',
                surffix : '.png'
            },
            {
                name : 'satellite',
                nameKo : '위성',
                prefix : 'http://map.daumcdn.net/map_skyview/L',
                surffix : '.jpg'
            },
            {
                name : 'hybrid',
                nameKo : '위성라벨',
                prefix : 'http://map.daumcdn.net/map_hybrid/1909dms/L',
                surffix : '.png'
            },
            {
                name : 'roadView',
                nameKo : '로드뷰',
                prefix : 'http://map.daumcdn.net/map_roadviewline/7.00/L',
                surffix : '.png'
            },
            {
                name : 'traffic',
                nameKo : '교통상황',
                prefix : '//r2.maps.daum-img.net/mapserver/file/realtimeroad/L',
                surffix : '.png'
            },
            {
                name : 'airPm10',
                nameKo : '미세먼지',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_pm10/T/L',
                surffix : '.png'
            },
            {
                name : 'airKhai',
                nameKo : '통합대기',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_khai/T/L',
                surffix : '.png'
            },
            {
                name : 'airPm25',
                nameKo : '초미세먼지',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_pm25/T/L',
                surffix : '.png'
            },
            {
                name : 'airYsnd',
                nameKo : '황사',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_ysnd/T/L',
                surffix : '.png'
            },
            {
                name : 'airO3',
                nameKo : '오존',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_o3/T/L',
                surffix : '.png'
            },
            {
                name : 'airNo2',
                nameKo : '이산화질소',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_no2/T/L',
                surffix : '.png'
            },
            {
                name : 'airCo',
                nameKo : '일산화탄소',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_co/T/L',
                surffix : '.png'
            },
            {
                name : 'airSo2',
                nameKo : '아황산가스',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_so2/T/L',
                surffix : '.png'
            }
        ],
        [
            {
                name : 'base',
                nameKo : '이미지',
                prefix : 'http://map.daumcdn.net/map_2d_hd/1909dms/L',
                surffix : '.png'
            },
            {
                name : 'satellite',
                nameKo : '위성',
                prefix : 'http://map.daumcdn.net/map_skyview/L',
                surffix : '.jpg'
            },
            {
                name : 'hybrid',
                nameKo : '위성라벨',
                prefix : 'http://map.daumcdn.net/map_hybrid/1909dms/L',
                surffix : '.png'
            },
            {
                name : 'roadView',
                nameKo : '로드뷰',
                prefix : 'http://map.daumcdn.net/map_roadviewline/7.00/L',
                surffix : '.png'
            },
            {
                name : 'traffic',
                nameKo : '교통상황',
                prefix : '//r2.maps.daum-img.net/mapserver/file/realtimeroad/L',
                surffix : '.png'
            },
            {
                name : 'airPm10',
                nameKo : '미세먼지',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_pm10/T/L',
                surffix : '.png'
            },
            {
                name : 'airKhai',
                nameKo : '통합대기',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_khai/T/L',
                surffix : '.png'
            },
            {
                name : 'airPm25',
                nameKo : '초미세먼지',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_pm25/T/L',
                surffix : '.png'
            },
            {
                name : 'airYsnd',
                nameKo : '황사',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_ysnd/T/L',
                surffix : '.png'
            },
            {
                name : 'airO3',
                nameKo : '오존',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_o3/T/L',
                surffix : '.png'
            },
            {
                name : 'airNo2',
                nameKo : '이산화질소',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_no2/T/L',
                surffix : '.png'
            },
            {
                name : 'airCo',
                nameKo : '일산화탄소',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_co/T/L',
                surffix : '.png'
            },
            {
                name : 'airSo2',
                nameKo : '아황산가스',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_so2/T/L',
                surffix : '.png'
            }
        ],
        [
            {
                name : 'base',
                prefix : '//api.vworld.kr/req/wmts/1.0.0/CEB52025-E065-364C-9DBA-44880E3B02B8/Base/',
                // prefix : '//xdworld.vworld.kr:8080/2d/Base/202002/',
                surffix : '.png'
            },
            {
                name : 'satellite',
                prefix : '//api.vworld.kr/req/wmts/1.0.0/CEB52025-E065-364C-9DBA-44880E3B02B8/Satellite/',
                // prefix : '//xdworld.vworld.kr:8080/2d/Satellite/202002/',
                surffix : '.png'
            },
            {
                name : 'hybrid',
                prefix : '//api.vworld.kr/req/wmts/1.0.0/CEB52025-E065-364C-9DBA-44880E3B02B8/Hybrid/',
                // prefix : '//xdworld.vworld.kr:8080/2d/Hybrid/202002/',
                surffix : '.png'
            }
        ]
    ];

    airLegends = {
        'airKhai': ['좋음 0~50', '보통 51~100', '나쁨 101~250', '매우나쁨 251~', '정보없음'] //통합대기지수
        , 'airPm10': ['좋음 0~30', '보통 31~80', '나쁨 81~150', '매우나쁨 151~', '정보없음'] //미세먼지
        , 'airPm25': ['좋음 0~15', '보통 16~35', '나쁨 36~75', '매우나쁨 76~', '정보없음'] //초미세먼지
        , 'airYsnd': ['좋음 0~199', '보통 200~399', '나쁨 400~799', '매우나쁨 800~', '정보없음'] //황사
        , 'airO3': ['좋음 0~0.03', '보통 0.031~0.09', '나쁨 0.091~0.15', '매우나쁨 0.151~', '정보없음'] //오존
        , 'airNo2': ['좋음 0~0.03', '보통 0.031~0.06', '나쁨 0.061~0.2', '매우나쁨 0.201~', '정보없음'] //이산화질소
        , 'airCo': ['좋음 0~2', '보통 2.01~9', '나쁨 9.01~15', '매우나쁨 15.01~', '정보없음'] //일산화탄소
        , 'airSo2': ['좋음 0~0.02', '보통 0.021~0.05', '나쁨 0.051~0.15', '매우나쁨 0.151~', '정보없음'] //아황산가스;
    }

    centers = {
        '41210': [126.86795945284835, 37.47588847099903], //광명
        '45210': [126.880651, 35.803587], //김제
        '26290': [129.084238267311, 35.1365226602861], //부산남구
        '47210': [128.62393703546, 36.8056192756897], //영주
    }

    constructor(id, type, sigun) {

        console.log("sigun : " + sigun);

        //맵 객체명
        this.id = id;
        //{ 0 : daum, 1 : naver, 2 : vworld }
        this.type = type;
        //map의 기본 설정 좌표계
        this.projection = this.realProjection[this.type];
        //표출용 범용(위경도) 좌표계
        this.baseProjection = 'EPSG:4326';

        if(sigun != undefined){
            const cen = this.centers[sigun];
            this.lon = cen[0];
            this.lat = cen[1];
        } else {
            //기본 중심점
            this.lat = 37.323351;
            this.lon = 126.726054;
        }

        this.extent = this.extents[type];
        this.center = [this.lon, this.lat];

        this.createTileGrid();

        this.map = this.createMap();
        this.view = this.map.getView();

        this.pulseInterval = null;
        this.pulseStop = false;

        this.cursorStyle();

        this.createTileLayers();

        this.switchTileMap('btnImgmap');

        this.map.getLayers().getArray().forEach((layer, idx) => {
            layer.setZIndex(idx);
        });

    }

    /**
     * 맵 레이어 커서 지정
     * * */
    cursorStyle() {
        let target = this.map.getTarget();
        let jTarget = typeof target === "string" ? $("#" + target) : $(target);

        this.map.on("pointermove", e => {
            const pixel = this.map.getEventPixel(e.originalEvent);
            let hit = this.map.forEachFeatureAtPixel(pixel, (feature,layer) => true);
            const cTarget = $(e.target);

            if(hit) {
                jTarget.css("cursor", "pointer");
            } else {
                jTarget.css("cursor", "");
            }
        });
    }

    //제거 예정
    // clickLayer(layers) {
    //     //LAYER SELECT EVENT
    //     window.map.map.on('click', evt => {
    //         const pixel = window.map.map.getEventPixel(evt.originalEvent);
    //         let clickLayer = new Array();
    //         let flag = true;
    //
    //         map.forEachFeatureAtPixel(pixel,(feature,layer) => {
    //             // if(layer){
    //             //     const selectLayer = layer.get('name');
    //             //     clickLayers.push(selectLayer);
    //             //     if(clickObjs[selectLayer]){
    //             //         clickObjs[selectLayer].push(feature);
    //             //     } else {
    //             //         flag = false;
    //             //     }
    //             // }
    //
    //         });
    //
    //     });
    // }

    createTileGrid() {
        let tileGrid = this.extents[this.type] == undefined ? undefined :
            new ol.tilegrid.TileGrid({
                origin: [this.extents[this.type][0], this.extents[this.type][1]],
                resolutions: this.resolutions[this.type]
            });

        this.tileGrid = tileGrid;
    }

    createTileLayers() {
        const urls = this.urls[this.type];
        for (let i = 0, max = urls.length; i < max; i++) {
            const layer = this.createTileLayer(urls[i].name, urls[i].prefix, urls[i].surffix, urls[i]);

            this.map.addLayer(layer);
        }
    }

    createTileLayer(type, prefix, surffix, prop) {
        try {
            return new ol.layer.Tile({
                title: type,
                name: type,
                visible: true,
                type: type,
                prop: prop,
                source: new ol.source.XYZ({
                    tileSize: 256,
                    tileGrid: this.tileGrid,
                    projection: ol.proj.get(this.projection),
                    maxZoom: this.maxZoom[this.type],
                    minZoom: this.minZoom[this.type],
                    this: this,
                    tileUrlFunction: (tileCoord, pixelRatio, projection) => {
                        let zType = this.type;
                        let resolution = this.resolutions[zType];
                        if (tileCoord == null) return undefined;
                        let s = Math.floor(Math.random() * 4);
                        let z = resolution ? resolution.length - tileCoord[0] : tileCoord[0];
                        let x = tileCoord[1];
                        let y = zType == 0 ? -(tileCoord[2]) - 1 : tileCoord[2];

                        return prefix + z + '/' + y + '/' + x + surffix;
                    },
                })
            });
        } catch (e) {
            console.log('error');
        }
    }

    createMap() {
        return new ol.Map({
            layers: [],
            target: this.id,
            controls: [],
            logo: false,
            view: new ol.View({
                projection: ol.proj.get(this.projection),
                extent: this.extents[this.type],
                resolutions: this.resolutions[this.type],
                // maxResolution : this.resolutions ? this.resolutions[this.type][0] : undefined,
                center: new ol.proj.transform([this.lon, this.lat]
                    , this.baseProjection
                    , this.projection),
                zoom: this.defaultZoom[this.type],
                zoomFactor: this.zoomFactor[this.type],
                maxZoom: this.maxZoom[this.type],
                minZoom: this.minZoom[this.type]
            })
        });
    }

    createContextMenu(menuObj) {
        let contextmenu = new ContextMenu({
            width: 170,
            items: menuObj,
            defaultItems: false
        });
        this.map.addControl(contextmenu);
    }

    createMousePosition(textArea){
        let mousePosition = new ol.control.MousePosition({
            coordinateFormat: ol.coordinate.createStringXY(6), // 좌표 표시 포맷
            projection: this.baseProjection, // 표출 좌표계
            className : 'custom-mouse-position',
            target: document.getElementById(textArea), // 표출할 영역 (id값)
            undefinedHTML:''
        });
        this.map.addControl(mousePosition);
    }

    createVectorLayer(title, style, source) {
        this.removeVectorLayer(title);

        let layer = new ol.layer.Vector({
            title: title
        });

        if (style != '' && style != null) {
            layer.setStyle(style);
        }

        layer.setSource(source);
        this.map.addLayer(layer);

        return layer;
    }

    getVectorLayer(title) {
        const layers = this.map.getLayers().getArray();
        let tilesId = '';
        for (let i = 0, max = layers.length; i < max; i++) {
            const temp = layers[i].get('title');
            if(title == temp) return layers[i];
        }
    }

    removeVectorLayer(title) {
        const layer = this.getVectorLayer(title);
        this.map.removeLayer(layer);
    }

    addLayer(layer) {
        //order 전처리
        const lastIdx = this.map.getLayers().getArray().length;
        layer.setZIndex(lastIdx);
        this.map.addLayer(layer);
    }

    getInteraction(title) {
        const interaction = this.map.getInteractions().getArray();
        for(let i=0; i < interaction.length;i++){
            const temp = interaction[i].i
            if(title == temp) return interaction[i];
        }
    }

    switchTileMap(type) {
        const layers = this.map.getLayers().getArray();
        let tilesId = '';
        if(type=='btnImgmap'){
            tilesId = 'base';
            this.tiles = tilesId;
        } else if(type == 'btnSkyview'){
            tilesId = 'satellite,hybrid';
            this.tiles = tilesId;
        }

        for (let i = 0, max = 13; i < max; i++) {
            const title = layers[i].get('title');
        //     if($('#btnImgview').hasClass('selected_btn') && title == 'roadView') {
        //         continue;
        //     }
            tilesId.indexOf(title) > -1 || layers[i] instanceof ol.layer.Vector ? layers[i].setVisible(true) : layers[i].setVisible(false);
        }
    }

    setMapEventListener(name, listener) {
        this.map.on(name, listener);
    }

    setMapViewEventListener(name, listener) {
        this.map.getView().on(name, listener);
    }

    removeMapEventListener(name, listener) {
        this.map.un(name, listener);
    }

    getCenter() {
        return this.view.getCenter();
    }

    setCenter(coord) {
        const center = this.map.getView().getCenter();
        const resolution = this.map.getView().getResolution() * 1;
        const a = ol.proj.transform(center
            , this.projection
            , this.baseProjection);
        const b = ol.proj.transform(coord
            , this.projection
            , this.baseProjection);

        //const distance = this.wgs84Sphere.haversineDistance(a, b);
        const duration = 500;

        this.view.animate({
            center : center,
            duration : duration
        });

        this.map.getView().setCenter(coord);
    }

    getZoom() {
        this.view.getZoom();
    }

    setZoom(level) {
        const view = this.map.getView();
        const resolution = view.getResolution();

        const zoom = new ol.control.Zoom({
            resolution: resolution,
            duration: 500
        });

        //this.map.beforeRender(zoom);
        //view.setResolution(resolution * factor);
        this.map.getView().setZoom(level);
    }

    updateSize() {
        this.map.updateSize();
    }

    /////
    zoomInOut(type){
        const view = this.map.getView();
        const zoom = view.getZoom();

        if(type == 'plus'){
            view.setZoom(zoom +1);
        } else if(type == 'minus'){
            view.setZoom(zoom -1);
        }
    }

    scaleLine() {
        const scaleLine = new ol.control.ScaleLine({
            units: 'metric'
            //target :
        });
        this.map.addControl(scaleLine);
    }

    setPulseFeature(coord) {
        let f = new ol.Feature (new ol.geom.Point(coord));
        f.setStyle (new ol.style.Style({
            image: new ol.style["Circle"] ({
                radius: 30,
                points: 4,
                stroke: new ol.style.Stroke ({ color: "#ff0000", width:5 })
            })
        }));
        this.map.animateFeature (f, new ol.featureAnimation.Zoom({
            fade: ol.easing.easeOut,
            duration: 500,
            easing: ol.easing["easyOut"] //bounce
        }));
        this.map.renderSync();
    }

    setPulse(coord) {
        const fn = () => {
            if(this.pulseStop) {
                this.removePulse();
            } else {
                this.setPulse(coord);
            }
        }
        this.pulseStop = false;
        this.setPulseFeature(coord);

        this.pulseInterval = setTimeout(fn , 500);

    }

    removePulse() {
        clearTimeout(this.pulseInterval);
    }



}


// const olProjection = {
//     addProjection : (epsg,param) => {
//         proj4.defs(epsg,param);
//         ol.proj.proj4.register(proj4);
//     },
//     createProjection : (code,extent) => {
//         const makedProjection = new ol.proj.Projection({
//             code : code,
//             extent : extent,
//             units : 'm'
//         });
//         return makedProjection;
//     }
// };




















