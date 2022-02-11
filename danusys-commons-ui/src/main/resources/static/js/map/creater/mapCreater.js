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
        8, // daum
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
                prefix : '/map-daumcdn-net/map_2d_hd/1909dms/L',
                surffix : '.png'
            },
            {
                name : 'satellite',
                prefix : '/map-daumcdn-net/map_skyview/L',
                surffix : '.jpg'
            },
            {
                name : 'hybrid',
                prefix : '/map-daumcdn-net/map_hybrid/1909dms/L',
                surffix : '.png'
            },
            {
                name : 'roadView',
                prefix : '/map-daumcdn-net/map_roadviewline/7.00/L',
                surffix : '.png'
            },
            {
                name : 'traffic',
                prefix : '//r2.maps.daum-img.net/mapserver/file/realtimeroad/L',
                surffix : '.png'
            },
            {
                name : 'airPm10',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_pm10/T/L',
                surffix : '.png'
            },
            {
                name : 'airKhai',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_khai/T/L',
                surffix : '.png'
            },
            {
                name : 'airPm25',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_pm25/T/L',
                surffix : '.png'
            },
            {
                name : 'airYsnd',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_ysnd/T/L',
                surffix : '.png'
            },
            {
                name : 'airO3',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_o3/T/L',
                surffix : '.png'
            },
            {
                name : 'airNo2',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_no2/T/L',
                surffix : '.png'
            },
            {
                name : 'airCo',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_co/T/L',
                surffix : '.png'
            },
            {
                name : 'airSo2',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_so2/T/L',
                surffix : '.png'
            }
        ],
        [
            {
                name : 'base',
                prefix : '/map-daumcdn-net/map_2d_hd/1909dms/L',
                surffix : '.png'
            },
            {
                name : 'satellite',
                prefix : '/map-daumcdn-net/map_skyview/L',
                surffix : '.jpg'
            },
            {
                name : 'hybrid',
                prefix : '/map-daumcdn-net/map_hybrid/1909dms/L',
                surffix : '.png'
            },
            {
                name : 'roadView',
                prefix : '/map-daumcdn-net/map_roadviewline/7.00/L',
                surffix : '.png'
            },
            {
                name : 'traffic',
                prefix : '//r2.maps.daum-img.net/mapserver/file/realtimeroad/L',
                surffix : '.png'
            },
            {
                name : 'airPm10',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_pm10/T/L',
                surffix : '.png'
            },
            {
                name : 'airKhai',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_khai/T/L',
                surffix : '.png'
            },
            {
                name : 'airPm25',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_pm25/T/L',
                surffix : '.png'
            },
            {
                name : 'airYsnd',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_ysnd/T/L',
                surffix : '.png'
            },
            {
                name : 'airO3',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_o3/T/L',
                surffix : '.png'
            },
            {
                name : 'airNo2',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_no2/T/L',
                surffix : '.png'
            },
            {
                name : 'airCo',
                prefix : '//airinfo.map.kakao.com/mapserver/file/airinfo_co/T/L',
                surffix : '.png'
            },
            {
                name : 'airSo2',
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
        ],
    ];

    constructor(id, type) {
        //맵 객체명
        this.id = id;
        //{ 0 : daum, 1 : naver, 2 : vworld }
        this.type = type;
        //map의 기본 설정 좌표계
        this.projection = this.realProjection[this.type];
        //표출용 범용(위경도) 좌표계
        this.baseProjection = 'EPSG:4326';

        //기본 중심점
        this.lat = 37.44457599567139;
        this.lon = 126.89482519279865;
        this.center = [this.lon, this.lat];

        this.createTileGrid();

        this.map = this.createMap();
        this.view = this.map.getView();

        this.createTileLayers();

        this.switchTileMap('btnImgmap');
    }

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
            const layer = this.createTileLayer(urls[i].name, urls[i].prefix, urls[i].surffix);

            this.map.addLayer(layer);
        }
    }

    createTileLayer(type, prefix, surffix) {
        try {
            return new ol.layer.Tile({
                title: type,
                name: type,
                visible: true,
                type: type,
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
        for (let i = 0, max = layers.length; i < max; i++) {
            const title = layers[i].get('title');
            if($('#btnImgview').hasClass('selected_btn') && title == 'roadView') {
                continue;
            }
            tilesId.indexOf(title) > -1 || layers[i] instanceof ol.layer.Vector ? layers[i].setVisible(true) : layers[i].setVisible(false);
        }
    }

    setMapEventListener(name, listener) {
        this.map.on(name, listener);
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

        const zoom = ol.animation.zoom({
            resolution: resolution,
            duration: 500
        });

        this.map.beforeRender(zoom);
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




















