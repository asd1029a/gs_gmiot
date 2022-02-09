/**
 * 관제
 */

const mntr = {
    init : () => {
        $(document).contextmenu( e => {
            e.preventDefault();
//			if(e.target.className.indexOf('no_target')>-1){
//				return false;
//			}
        });

        //컨텍스트메뉴
        const menuObj = [
            {
                text: '클릭 지점 로드뷰보기',
                classname: 'context-style',
                callback: e => {
                    const coordinate = new ol.proj.transform(e.coordinate, window.map.realProjection[window.map.type] ,'EPSG:4326');
					window.open("/ui/roadView?lon="+coordinate[0]+"&lat="+coordinate[1],'road','');
					//contextmenu.clear();
                }
            },
            {
                text: '주소보기',
                classname: 'context-style',
                callback: e => {
                    const coordinate = new ol.proj.transform(e.coordinate, window.map.realProjection[window.map.type] ,'EPSG:4326');
                    console.log(coordinate);

                    //팝업 붙이기
                }
            }
        ];

        //지도 생성
        let map = new mapCreater('map',0);
        map.createMousePosition('mousePosition');
        map.scaleLine();
        map.createContextMenu(menuObj);
        window.map = map;
        //레이어 도구
        let lyControl = new layerControl('map', 'title');
        window.lc = lyControl;
        //측정 도구
        let measure = new measureTool('map');
        window.measure = measure;

        // station.getList({} ,(result) => {
        //     console.log(result.data);
        // });
        //개소 레이어
        station.getListGeoJson({} ,(result) => {
            let dataLy = new dataLayer('map');
            let stationLayer = dataLy.fromGeoJSon(result, 'stationLayer', true, layerStyle.station());
            //map.addLayer(stationLayer);

            //열지도 test
            const heat = new ol.layer.Heatmap({
                source: new ol.source.Vector({
                    features: new ol.format.GeoJSON().readFeatures(result, {
                        dataProjection: "EPSG:4326"
                        , featureProjection: "EPSG:5181"
                    })
                }),
                blur: 15,
                radius: 8,
                weight: function (feature) {
                    //var magnitude = parseFloat(feature.get('magnitude'));
                    //return magnitude - 5;
                    return 0.4;
                },
            });

            window.map.addLayer(heat);
        });




    },
    eventHandler : () => {
        //LNM FOLD
        $('.mntr_container .lnb_fold').on("click", function(e){
            $('.mntr_container .menu_fold').hide();
            window.map.updateSize();
        });
        //LNM SWITCH
        $('.mntr_container .lnb ul li').on("click", function(e){
            const theme = $(e.currentTarget).attr('data-value');
            $('.mntr_container .menu_fold').hide();
            $('.mntr_container .menu_fold#'+theme).show();
            window.map.updateSize();
        });
        //LNM TAB SWITCH
        $('.mntr_container .menu_fold .tab li').on("click", function(e){
            const tab = $(e.currentTarget).attr('data-value');
            $('.menu_fold .lnb_tab_section').hide();
            $('div#'+tab).show();
            //ACTIVE STYLE
            $(e.currentTarget).parent().children("li").removeClass("active");
            $(e.currentTarget).addClass("active");

        });
        //LNM TAB SEARCH DETAIL
        $('.detail_btn').on("click", function(e){
            const form = $(e.currentTarget).parents('.lnb_tab_section').find('.search_fold');
            if(form.is(':visible')) {
                form.hide();
            } else {
                form.show();
            }
        });
        //RNM CLOSER
        $('.rnm_closer').on("click", function(e){
            $('.area_info').hide();
            window.map.updateSize();
        });
        //MAP TOOL
        $('.map_options li').on("click", function(e){
            const type = $(e.currentTarget).attr('data-value');
            switch(type) {
                case "roadView" :
                    if(window.lc.find(type).getVisible()){
                        window.lc.off(type);
                    } else {
                        window.lc.on(type);
                    }
                    break;
                case "plus" : window.map.zoomInOut('plus'); break;
                case "minus" : window.map.zoomInOut('minus'); break;
                case "distance" : window.measure.initDraw('LineString'); break;
                case "measure" : window.measure.initDraw('Polygon'); break;
                case "radius" : window.measure.initDraw('Circle'); break;
                case "eraser" : window.measure.removeMeasureTool(); break;
                default:
            }
        });
        //MAP BASE SWITCH
        $('.map_type li').on("click", function(e){
            const type = $(e.currentTarget).attr('data-value');
            window.map.switchTileMap(type);
            //ACTIVE STYLE
            $(e.currentTarget).parent().children("li").removeClass("active");
            $(e.currentTarget).addClass("active");
        });
        //TOP BUTTON
        $(".search_list .button_top").on("click", function(e){
            $(e.currentTarget).parent('div').scrollTop(0);
        });



    }

}
