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
                    let popup = new mapPopup('map');

                    popup.create('testpopup');
                    popup.move('testpopup',e.coordinate);
                    popup.content('testpopup',mapPopupContent.address(coordinate));

                    window.popup = popup;
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
        window.lyControl = lyControl;
        //측정 도구
        let measure = new measureTool('map');
        window.measure = measure;

        // station.getList({}, (result) => {
        //     console.log(result);
        // });
        //개소 레이어
        station.getListGeoJson({} ,(result) => {
            console.log(result);
            let stationLayer = new dataLayer('map')
                .fromGeoJSon(result, 'stationLayer', true, layerStyle.station(false));
            map.addLayer(stationLayer);

            //select
           //window.lyControl.find('stationLayer').set('selectable', true);

            // //열지도 test
            // const heat = new ol.layer.Heatmap({
            //     source: new ol.source.Vector({
            //         features: new ol.format.GeoJSON().readFeatures(result, {
            //             dataProjection: "EPSG:4326"
            //             , featureProjection: "EPSG:5181"
            //         })
            //     }),
            //     blur: 15,
            //     radius: 8,
            //     weight: function (feature) {
            //         //var magnitude = parseFloat(feature.get('magnitude'));
            //         //return magnitude - 5;
            //         return 0.4;
            //     },
            // });
            //
            // window.map.addLayer(heat);
        });

        facility.getListGeoJson({}, (result) => {
           // console.log(result);
            let result1 =
            {
                type: 'FeatureCollection',
                name: 'sample',
                crs: { type: 'name', properties: { name: 'urn:ogc:def:crs:OGC:1.3:CRS84' } },
                features: [
                    { type: 'Feature', properties: { id: 123 }, geometry: { type: 'Point', coordinates: [ 126.727012512422448, 37.322852752634546 ] } },
                    { type: 'Feature', properties: { id: 234 }, geometry: { type: 'Point', coordinates: [ 126.750776389512524, 37.309517452940021 ] } },
                    { type: 'Feature', properties: { id: 345 }, geometry: { type: 'Point', coordinates: [ 126.70449023745131, 37.337370287491666 ] } },
                    { type: 'Feature', properties: { id: 567 }, geometry: { type: 'Point', coordinates: [ 126.73797079693405, 37.3197810464005 ] } },
                    { type: 'Feature', properties: { id: 456 }, geometry: { type: 'Point', coordinates: [ 126.744699931551466, 37.319431463919734 ] }},
                    { type: 'Feature', properties: { id: 678 }, geometry: { type: 'Point', coordinates: [ 126.733088989968962, 37.313668244318841 ] } },
                    { type: 'Feature', properties: { id: 789 }, geometry: { type: 'Point', coordinates: [ 126.740937197627019, 37.319332213043808 ] } }
                ]
            };

            let facilityLayer = new dataLayer('map')
                .fromGeoJSon(result1,'facilityLayer', true, layerStyle.facility(false));
            map.addLayer(facilityLayer);

        });

        // event.getListGeoJson({}, (result) => {
        //    // console.log(result);
        // });

        const select =
            new ol.interaction.Select({
                layers : //[window.lyControl.find('stationLayer')]
                    layer => {
                        return layer.get('selectable') === true;
                    }
                , style :
                    feature => {
                        //console.log(feature.getProperties());
                    }

                }
            );
        window.map.map.addInteraction(select);
        // window.map.map.addInteraction(
        //     select
        //     //layerSelect.add(window.lyControl.find('stationLayer'))
        // );


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
                    if(window.lyControl.find(type).getVisible()){
                        window.lyControl.off(type);
                    } else {
                        window.lyControl.on(type);
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
