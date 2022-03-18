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
        //동네 날씨 기능를 위해
        map.setMapEventListener('moveend',(e) => {
            let mapProj = e.map.getView().getProjection().getCode();
            let mapCenter = e.map.getView().getCenter();
            let mapLonLat = ol.proj.transform(mapCenter,mapProj,'EPSG:4326');
            // 날씨 데이터 반환
            const param = {
                callUrl : '/getWeatherData',
                numOfRows: '1000',
                pageNo: '1',
                dataType: "JSON",
                lon : mapLonLat[0],
                lat : mapLonLat[1]
            }
            $.ajax({
                contentType : "application/json; charset=utf-8",
                type : "POST",
                url : '/api/getCurSkyTmp',
                dataType : "json",
                data : JSON.stringify(param),
                async : true
            }).done( result => {
                if (!result) return;
                const html = "<i><img src='/images/default/icon_weather_"+ result["sky"] +".png'></i>"
                    + result["skyNm"] + " " + result["tmp"] + "℃" ;
                $(".map_location #admWeather").empty();
                $(".map_location #admWeather").append(html);
            });
            // // 현재 위치 시-군-동 반환
            // $(".map_location #admAreaName").text("알수 없음");
            // $.ajax({
            //     type : "POST",
            //     url : '/adm/lonLatToAdm',
            //     contentType : "application/json; charset=utf-8",
            //     dataType : "JSON",
            //     async : true,
            //     data : JSON.stringify({
            //         lon : mapLonLat[0],
            //         lat : mapLonLat[1]
            //     })
            // }).done( result => {
            //     if (!result) return;
            //     $(".map_location #admAreaName").text( result["areaName"]);
            // });

            /////////////////////////////////////////
            kakaoApi.getAddress();

        }); // 지도 move end 이벤트

        /**
         * 레이어 선택 조작
         */
        const layerSelect = new ol.interaction.Select({
            layers :
                layer => {
                    return layer.get('selectable') === true;
                }
            , style :
                feature => {
                    const id = feature.getId().replace(/[0-9]/gi, '');
                    const styleFunc = layerStyle[id](true);
                    return styleFunc(feature);
                }
        });
        window.map.map.addInteraction(layerSelect);

        layerSelect.on('select', function(evt){
            const target = evt.selected[0];
            if(target) {
                const targetType = target.getId().replace(/[0-9]/gi,'');
                clickIcon(targetType,target.getProperties());
            }
        });

        // station.getList({}, (result) => {
        //     console.log(result);
        // });
        //개소 레이어
        station.getListGeoJson({} ,(result) => {
            //console.log(result);
            let stationLayer = new dataLayer('map')
                .fromGeoJSon(result, 'stationLayer', true, layerStyle.station(false));
            map.addLayer(stationLayer);
            window.lyControl.find('stationLayer').set('selectable',true);
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
                    { type: 'Feature', id: 'facility123', properties: { id: 123 }, geometry: { type: 'Point', coordinates: [ 126.727012512422448, 37.322852752634546 ] } },
                    { type: 'Feature', id: 'facility234', properties: { id: 234 }, geometry: { type: 'Point', coordinates: [ 126.750776389512524, 37.309517452940021 ] } },
                    { type: 'Feature', id: 'facility345', properties: { id: 345 }, geometry: { type: 'Point', coordinates: [ 126.70449023745131, 37.337370287491666 ] } },
                    { type: 'Feature', id: 'facility456', properties: { id: 456 }, geometry: { type: 'Point', coordinates: [ 126.73797079693405, 37.3197810464005 ] } },
                    { type: 'Feature', id: 'facility567', properties: { id: 567 }, geometry: { type: 'Point', coordinates: [ 126.744699931551466, 37.319431463919734 ] }},
                    { type: 'Feature', id: 'facility678', properties: { id: 678 }, geometry: { type: 'Point', coordinates: [ 126.733088989968962, 37.313668244318841 ] } },
                    { type: 'Feature', id: 'facility789', properties: { id: 789 }, geometry: { type: 'Point', coordinates: [ 126.740937197627019, 37.319332213043808 ] } }
                ]
            };

            let facilityLayer = new dataLayer('map')
                .fromGeoJSon(result1,'facilityLayer', true, layerStyle.facility(false));
            map.addLayer(facilityLayer);
            window.lyControl.find('facilityLayer').set('selectable',true);
        });

        // event.getListGeoJson({}, (result) => {
        //    // console.log(result);
        // });
        //
        // const clickObjs = {
        //     'stationLayer':[]
        //     , 'facilityLayer':[]
        // };
        // window.map.clickLayer(clickObjs);
        //


    }
    , eventHandler : () => {
        //LNM FOLD (왼쪽창 끄고켜기)
        $('.mntr_container .lnb_fold').on("click", e => {
            $('.mntr_container .menu_fold').removeClass("select");
            $('.mntr_container .lnb ul li').removeClass("active");
            window.map.updateSize();
        });
        //LNM SWITCH (왼쪽창 변경)
        $('.mntr_container .lnb ul li').on("click", e => {
            const theme = $(e.currentTarget).attr('data-value');
            $('.mntr_container .menu_fold').removeClass("select");
            $('.mntr_container .menu_fold#'+theme).addClass("select");
            //ACTIVE STYLE
            $(e.currentTarget).parent().children("li").removeClass("active");
            $(e.currentTarget).addClass("active");
            window.map.updateSize();
        });
        //LNM TAB SWITCH (왼쪽창 탭별 변경)
        $('.mntr_container .menu_fold .tab li').on("click", e => {
            const tab = $(e.currentTarget).attr('data-value');
            $(e.currentTarget).parents('section').find('.lnb_tab_section').removeClass("select");
            $('div#'+tab).addClass("select");
            //ACTIVE STYLE
            $(e.currentTarget).parent().children("li").removeClass("active");
            $(e.currentTarget).addClass("active");

        });
        //LNM TAB SEARCH DETAIL (왼쪽창 검색 조건 더보기)
        $('.detail_btn').on("click", e => {
            const form = $(e.currentTarget).parents('.lnb_tab_section').find('.search_fold');
            if(form.hasClass("select")) {
                form.removeClass("select");
                //하위 체크 리스트
                form.find('.checkbox_list').removeClass("select");
            } else {
                form.addClass("select");
            }
        });
        //LNM TAB SEARCH DROPDOWN (왼쪽창 검색 조건 리스트 보기)
        $('.search_fold .checkbox_title').on("click", e => {
            const list = $(e.currentTarget).parent().find('.checkbox_list');
            if(list.hasClass("select")){
                list.removeClass("select");
            } else {
                // $(e.currentTarget).parents('.search_fold').find('.dropdown_checkbox').removeClass("select");
                list.addClass("select");
                // debugger;
            }
        });
        //RNM CLOSER (오른쪽창 닫기)
        $('.rnm_closer').on("click", e => {
            $('.area_right').hide();
            window.map.updateSize();
        });
        //LAYER ORDER LIST (레이어 순서 제어창)
        $("#layerViewer").hide();
        //MAP TOOL (맵 도구)
        $('.map_options li').on("click", e => {
            const type = $(e.currentTarget).attr('data-value');
            switch(type) {
                case "roadView" :
                    if(window.lyControl.find(type).getVisible()){
                        window.lyControl.off(type);
                    } else {
                        window.lyControl.on(type);
                    }
                    break;
                case "plus" : window.map.zoomInOut('plus'); break; //확대
                case "minus" : window.map.zoomInOut('minus'); break; //축소
                case "distance" : window.measure.initDraw('LineString'); break; //거리재기
                case "measure" : window.measure.initDraw('Polygon'); break; //면적재기
                case "radius" : window.measure.initDraw('Circle'); break; //반경재기
                case "eraser" : window.measure.removeMeasureTool(); break; //지우기
                case "layer" : //레이어 제어
                    const target = $("#layerViewer");
                    target.toggle('show');
                    $("#layerViewer ul").empty();

                    const ary = window.map.map.getLayers().getArray();
                    let layerAry = ary.filter(value => {
                        return value.getProperties().title.includes("Layer");
                    });
                    layerAry.sort((a,b)=> {
                        return a.getZIndex() - b.getZIndex();
                    });

                    for(let i in layerAry){
                        const layerNm = layerAry[i].getProperties().title;
                        const zIdx = window.lyControl.find(layerNm).getZIndex();

                        const li = "<li data-zindex='"+ zIdx +"'>" + layerNm + "</li>";
                        $("#layers").append(li);
                    }

                    $("#layers").sortable({
                        start: (e, ui) => {
                            $(this).attr("prev-index", ui.item.index());
                        }
                        , update: (e, ui) => {
                            // let newOrd = Number(ui.item.index());
                            // let oldOrd = Number($(this).attr("prev-index"));
                            // let targetLayer = ui.item.text();
                            //console.log(targetLayer + " : " + oldOrd + " => " + newOrd);
                           const totalLen = window.map.map.getLayers().getArray().length;
                           const liLen = $("#layers li").length;
                           const startIdx = totalLen - liLen;

                            //LAYER SHIFT
                            $.each($("#layers li"), (i,v)=> {
                                let origin = startIdx + i;
                                let zIdx = $(v).attr('data-zindex');

                                if(origin != zIdx){
                                    const layer = $(v).text();
                                    $("#layers li").eq(i).attr('data-zindex',origin);
                                    window.lyControl.find(layer).setZIndex(origin);
                                }
                            });
                        }
                    });
                    break;
                default:
            }
        });
        //MAP BASE SWITCH (지도 타입 변경)
        $('.map_type li').on("click", e => {
            const type = $(e.currentTarget).attr('data-value');
            window.map.switchTileMap(type);
            //ACTIVE STYLE
            $(e.currentTarget).parent().children("li").removeClass("active");
            $(e.currentTarget).addClass("active");
        });
        //TOP BUTTON (오른쪽창 리스트 맨위로)
        $(".search_list .button_top").on("click", e => {
            $(e.currentTarget).parent('div').scrollTop(0);
        });

    }
    , create : () => {
        /* 다중 셀렉트 박스 */
        // $.each($(".dropdown_checkbox"), (idx, item) => {
        //     comm.createMultiSelectBox(item);
        // });
        // const pObj = {
        //     draw : null
        //     , type: "stationKind"
        // }
        // commonCode.getList( pObj , (result) => {
        //     console.log(result);
        // });
        //
        // const pObj2 = {
        //     draw : null
        //     , type : "facilityKind"
        // }
        // commonCode.getList( pObj , (result) => {
        //     console.log(result);
        // });


    }

}

// select t3.area_name, t3.area_code from (
//     select
// t1.emd_cd
// from t_area_emd t1
// where st_contains(t1.geom, ST_GEOMFROMTEXT('POINT(126.88119336994183 37.44335040773767)',4326)) = true
// ) t2
// inner join t_area_code_name t3
// on t2.emd_cd||'00' = t3.area_code::varchar