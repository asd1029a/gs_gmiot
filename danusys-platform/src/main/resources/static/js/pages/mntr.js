/**
 * 관제
 */
const mntr = {
    init : () => {
        //TODO sse이벤트 수신 test
        const eventSource = new EventSource(`/sse/112`);

        eventSource.onopen = (e) => {
            console.log(e);
        };
        eventSource.onerror = (e) => {
            console.log("=====수신실패=====")
            console.log(e);
        };
        eventSource.onmessage = (e) => {
            const objJson = JSON.parse(e.data);
            const objList = objJson['event_list'];

            const eventSeqs = [];

            //긴급 배너 띄우기
            objList.forEach((e,i)=> {
                if(e.event_grade == 20){
                    eventSeqs.push(e.event_seq);
                    const mainObj = {
                        type : "error",
                        title : e.device_id + " 디바이스 이벤트 발생",
                        content : e.event_message
                    };
                    comm.toastOpen(mainObj, () => {}, {});
                }
            });
            // const options = {
            //     timeOut : "1500",
            //     positionClass : "toast-top-full-width",
            //     progressBar: false,
            //     preventDuplicates : false
            // }
            $('.toast-top-full-width').css({
                'left': $('.lnb').width() + $('.menu_fold').width() + $('.map_location').width() + 7,
                    //$('#map').offset().left,
                'top': $('#map').offset().top + 10,
                    //$('#map').offset().top + $('.map_location').height() + 20 ,
                'width': '40%', /*'height' : '320px'*/
            }); //TODO 스크롤

            const $targetMenu = $('.mntr_container .lnb ul li.active').attr('data-value');
            const $targetTab = $('.mntr_container section.menu_fold.select .lnb_tab_section.select').attr('data-value');

            if($targetTab == "event"){
                let newAry;
                event.getListGeoJson({
                    "eventState": ["1","2","3"]
                }, result => {
                    //리스트 추가하기 (TODO 리스트도 refresh 해야할까)
                    const data = JSON.parse(result);
                    const ary = [];
                    data.features.forEach(t => {
                        const seq = t.properties.eventSeq;
                        if(eventSeqs.includes(seq)){
                            ary.push(t);
                        }
                    });
                    data.features = ary;
                    lnbList.createEvent(JSON.stringify(data));
                    //레이어 refresh
                    reloadLayer(JSON.parse(result), 'eventLayer');
                });
            }
        };

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
                    //팝업 붙이기
                    let popup = new mapPopup('map');
                    popup.create('addressPopup');
                    popup.move('addressPopup',e.coordinate);
                    popup.content('addressPopup',mapPopupContent.coord2address(coordinate));

                    window.popup = popup;
                }
            }
        ];

        let siGunCode;
        //현재 지자체명 반환
        $.ajax({
            url : "/getSiGunCode"
            , type : "POST"
            , data : JSON.stringify({})
            , contentType : "application/json; charset=utf-8"
            , async : false
        }).done((result) => {
            siGunCode = result;
            window.siGunCode = result;
        });

        //지도 생성
        let map = new mapCreater('map',0, siGunCode);
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
        //맵 선 도구
        let lyConnect = new mapConnectLineCreater('map');
        window.lyConnect = lyConnect;

        //맵 이동 (move end) 이벤트
        map.setMapEventListener('moveend',e => {
            //동네 날씨 기능
            centerVilageInfo(e);
        });

        //레이어 마우스오버 이벤트
        let target = map.map.getTarget();
        let jTarget = typeof target === "string" ? $("#" + target) : $(target);
        $(map.map.getViewport()).on('mousemove', e => {

            const pixel = map.map.getEventPixel(e.originalEvent);
            const hit = map.map.forEachFeatureAtPixel(pixel, (feature, layer) => true);
            const cTarget = $(e.target);
            let popup = new mapPopup('map');

            if (hit) {
                if(cTarget[0].tagName=="CANVAS"){
                    jTarget.css("cursor", "pointer");
                    //마우스 온 프리셋
                    map.map.forEachFeatureAtPixel(pixel,(feature,layer) => {
                        if(layer){
                            if(layer.get("title")){
                                const layerName = layer.get("title");
                                //클러스터 인가
                                if(feature.getProperties().features){
                                    const len = feature.getProperties().features.length;
                                    if(len == 1){
                                        let position = feature.getGeometry().getCoordinates();
                                        let content = "";
                                        //개소
                                        if(layerName == "stationLayer"){
                                            content = mapPopupContent.station(feature, len);
                                        //이벤트
                                        } else if((layerName == "eventLayer")||(layerName == "eventPastLayer")){
                                            content = mapPopupContent.event(feature, len);
                                            let point = window.map.map.getPixelFromCoordinate(position);
                                            position = window.map.map.getCoordinateFromPixel([point[0], point[1] - 50]); //아이콘 높이만큼
                                        }
                                        if(content!=""){
                                            popup.create('mouseOverPopup');
                                            popup.content('mouseOverPopup', content);
                                            popup.move('mouseOverPopup', position);
                                        }
                                    }
                                }// end cluster?
                            }// end layer title
                        } // end layer
                    }); // end mouseon
                } // end hit canvas
                map.map.renderSync();
            } else {
                jTarget.css("cursor", "all-scroll");
                popup.remove('mouseOverPopup');
            }
        });

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
                    let id = "";
                    //클러스터 일때
                    if(feature.getProperties().features != undefined) {
                        id = feature.getProperties().features[0].getId().replace(/[0-9]/gi,'');
                    } else { //클러스터 아닐때
                        id = feature.getId().replace(/[0-9]/gi,'');
                    }
                    const styleFunc = layerStyle[id](true);
                    return styleFunc(feature);
                }
        });
        window.map.map.addInteraction(layerSelect);

        layerSelect.on('select', evt => {
            if(evt.deselected.length > 0){
                //선택해체
                let popup = new mapPopup('map');
                popup.remove('mouseClickPopup');
            }
            //선택
            const target = evt.selected[0];
            if(target) {
                let targetType = "";
                if(target.getProperties().features != undefined) {
                    //클러스터 일때
                    targetType = target.getProperties().features[0].getId().replace(/[0-9]/gi,'');
                } else {
                    //클러스터 아닐때
                    targetType = target.getId().replace(/[0-9]/gi,'');
                }
                //*** eventPast 내부 id => event
                clickIcon(targetType, target);
            }
        });
        window.lySelect = layerSelect;

        //기본 geojson 초기 form
        const geoJsonStr = {
            "type": "FeatureCollection",
            "features": []
        };
        //기본 레이어 초기 설정
        let eventLayer = new dataLayer('map') //이벤트 실시간
            .toCluster(geoJsonStr, 'eventLayer', true, layerStyle.event(false));
        let eventPastLayer = new dataLayer('map') //이벤트 과거이력
            .toCluster(geoJsonStr, 'eventPastLayer', true, layerStyle.event(false));
        let stationLayer = new dataLayer('map') //개소
            .toCluster(geoJsonStr, 'stationLayer', true, layerStyle.station(false));
        let routeLayer = new dataLayer('map') //경로
            .fromGeoJsonToRoute(geoJsonStr, 'routeLayer', true, layerStyle.route(false));
        let facilityLayer = new dataLayer('map') //시설물
            .fromGeoJSon(geoJsonStr, 'facilityLayer', true, layerStyle.facility());
        let cctvLayer = new dataLayer('map') //씨씨티비
            .fromGeoJSon(geoJsonStr,'cctvLayer', true, layerStyle.cctv(false));

        [eventLayer, eventPastLayer, facilityLayer, stationLayer, cctvLayer].forEach(ly => {
           window.map.addLayer(ly);
           ly.set('selectable', true);
        });
        //     // //열지도 test
        //     // const heat = new ol.layer.Heatmap({
        //     //     source: new ol.source.Vector({
        //     //         features: new ol.format.GeoJSON().readFeatures(result, {
        //     //             dataProjection: "EPSG:4326"
        //     //             , featureProjection: "EPSG:5181"
        //     //         })
        //     //     }),
        //     //     blur: 15,
        //     //     radius: 8,
        //     //     weight: function (feature) {
        //     //         //var magnitude = parseFloat(feature.get('magnitude'));
        //     //         //return magnitude - 5;
        //     //         return 0.4;
        //     //     },
        //     // });
        //     //
        //     // window.map.addLayer(heat);

        //축척별 레이어 반응
        map.setMapViewEventListener('propertychange' ,e => {
            //연결선 리로드
            window.map.map.getLayers().getArray().map(ly => {
                if(ly.get('title').includes('lineLayer')){
                    window.lyConnect.reload(ly.getProperties().type);
                }
            });

            if(String(e.key)=="resolution"){
                const zoom = map.map.getView().getZoom();
                let popup = new mapPopup('map');
                popup.remove('mouseClickPopup');
                let theme = $('.mntr_container .lnb ul li.active').attr('data-value');
                let target = $('.mntr_container section.select .tab li.active').attr('data-value');

                if(target=="station"){target = "event"}

                if(zoom > 10){ //13 ~ 9.xxx
                    if((theme != "drone")&&(theme != "addressPlace")&&(theme != "smart")) {
                        window.lyControl.onList(['station', target]);
                    }
                    if(theme == "drone"){window.lyControl.onList(['facility', target]);}
                    window.lyControl.setDistances(['station', 'event', 'eventPast'],0);
                } else if((10 >= zoom) && (zoom >=5)) { //10 ~ 5
                    if((theme != "drone")&&(theme != "addressPlace")&&(theme != "smart")) {
                        window.lyControl.onList(['station', target]);
                    }
                    if(theme == "drone"){window.lyControl.onList(['facility', target]);}
                    window.lyControl.setDistances(['station', 'event', 'eventPast'],30);
                } else { //4.xxx ~ 0
                    window.lyControl.offList(['station', 'event', 'eventPast', 'facility', 'route']);
                }
            }


        });


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
            let target = $('.mntr_container section.menu_fold.select .lnb_tab_section.select').attr('data-value');

            if(target == "station") {target = "event"}

            //ACTIVE STYLE
            $(e.currentTarget).parent().children("li").removeClass("active");
            $(e.currentTarget).addClass("active");

            /// TODO 데이터 확인 후 정리해서 코드 따로 빼기
            let eventParam = {"eventState": ["1", "2", "3"]};
            let eventPastParam = {"eventState": ["9"]};
            let stationParam = {};

            let tablType = 'station';
            //기체 폴링 멈춤
            dronePolling.stop();

            switch (theme) {
                case "smartPole" : //스마트폴
                    //실시간
                    eventParam = {"eventState": ["1", "2", "3"], "eventKind": ["gateway_trans", "device_trans", "dtctn_crmss"]};
                    //과거이력
                    eventPastParam = {"eventState": ["9"], "eventKind": ["gateway_trans", "device_trans", "dtctn_crmss"]};
                    //개소
                    stationParam = {"station": ["lamp_road"]};
                    window.lyControl.offList(['facility']);
                    window.lyControl.onList(['station', target]);
                    break;
                case "smartBusStop" : //스마트 정류장
                    //실시간
                    eventParam = {"eventState": ["1", "2", "3"], "eventKind": ["BUSSTOP_FALL_DOWN", "BUSSTOP_FIRE"]};
                    //과거이력
                    eventPastParam = {"eventState": ["9"], "eventKind": ["BUSSTOP_FALL_DOWN", "BUSSTOP_FIRE"]};
                    //개소
                    stationParam = {"station": ["smart_station"]};
                    window.lyControl.offList(['facility']);
                    window.lyControl.onList(['station', target]);
                    break;
                case "smartPower": //스마트 분전함
                    //실시간
                    eventParam = {"eventState": ["1", "2", "3"], "eventKind": ["LKGE_ERCRT", "OVER_ERCRT"]};
                    //과거이력
                    eventPastParam = {"eventState": ["9"], "eventKind": ["LKGE_ERCRT", "OVER_ERCRT"]};
                    //개소
                    window.lyControl.offList(['facility','eventPast']);
                    window.lyControl.onList(['station', target]);
                    break;
                case "drone" : //드론
                    tablType = 'facility';
                    //실시간
                    eventParam = {"eventState": ["1", "2", "3"], "eventKind": ["drone_fire_detection", "drone_object_tracking"]};
                    //과거이력
                    eventPastParam = {"eventState": ["9"], "eventKind": ["drone_fire_detection", "drone_object_tracking"]};
                    //개소
                    /*stationParam = {"facilityKind": ["57"]}*/
                    //기체
                    facility.getListGeoJson({
                        "facilityKind" : ["DRONE"],
                        "sigCode" : window.siGunCode
                    },result => {
                        reloadLayer(result, 'facilityLayer');
                        lnbList.createFacility(result);
                    });
                    //기제 폴링 시작
                    dronePolling.start();

                    window.lyControl.offList(['station', 'event', 'eventPast']);
                    window.lyControl.onList(['facility', 'station' , target]);
                    break;
                case "smart" : //스마트OO
                    window.lyControl.offList(['facility', 'station', 'event', 'eventPast', 'route']);
                    break;
                default :
                    if(theme != "addressPlace"){
                        window.lyControl.offList(['facility', 'route','event', 'eventPast']);
                        window.lyControl.onList(['station', target]);
                    }
                    break;
            }

            stationParam["sigCode"] = window.siGunCode//"41390"; //TODO 시흥 -> window.siGunCode
            eventParam["sigCode"] = window.siGunCode;
            eventPastParam["sigCode"] = window.siGunCode;

            //실시간 이벤트
            event.getListGeoJson(eventParam, result => {
                reloadLayer(result, 'eventLayer');
                lnbList.createEvent(result);
            });
            //개소
            station.getListGeoJson(stationParam ,result => {
                reloadLayer(result, 'stationLayer');
                lnbList.createStation(result, tablType);
            });
            //이벤트 과거이력
            event.getListGeoJson(eventPastParam, result => {
                reloadLayer(result, 'eventPastLayer');
                lnbList.createEventPast(result);
            });
            window.map.map.render();
            window.map.updateSize();

            const rVisivle = $('.area_right').is(':visible');
            if(rVisivle) { $('.area_right_closer').trigger("click")}
        });

        //LNM TAB SWITCH (왼쪽창 탭별 변경)
        $('.mntr_container .menu_fold .tab li').on("click", e => {
            window.lySelect.getFeatures().clear();
            let tab = $(e.currentTarget).attr('data-value');
            const theme = $('.mntr_container .lnb ul li.active').attr('data-value');

            $(e.currentTarget).parents('section').find('.lnb_tab_section').removeClass("select");
            $(e.currentTarget).parents('section').find('div[data-value='+tab+']').addClass("select");
            //ACTIVE STYLE
            $(e.currentTarget).parent().children("li").removeClass("active");
            $(e.currentTarget).addClass("active");

            if((theme == "drone")&&(tab == "station")){ tab = "facility" }
            switch(tab) {
                case "event" :
                    window.lyControl.off('eventPastLayer');
                    break;
                case "eventPast" :
                    window.lyControl.off("eventLayer");
                    break;
                case "station" :
                case "facility" :
                    window.lyControl.on('eventLayer');
                    window.lyControl.off('eventPastLayer');
                    break;
                default :
                    break;
            }
            window.lyControl.on(tab + "Layer");

            const rVisivle = $('.area_right').is(':visible');
            if(rVisivle) { $('.area_right_closer').trigger("click")}
        });

        //LNM TAB SEARCH DETAIL (왼쪽창 검색 조건 더보기)
        $('.detail_btn').on("click", e => {
            const form = $(e.currentTarget).parents('.lnb_tab_section').find('.search_fold');
            const btnArrow = $(e.currentTarget).find('img');
            if(form.hasClass("select")) {
                form.removeClass("select");
                //하위 체크 리스트
                form.find('.checkbox_list').removeClass("select");
                btnArrow.css("transform", "rotate(0deg)");
            } else {
                btnArrow.css("transform", "rotate(180deg)");
                form.addClass("select");
            }
        });

        //LNM TAB SEARCH DROPDOWN (왼쪽창 검색 조건 리스트 보기)
        $('.search_fold .checkbox_title').on("click", e => {
            const list = $(e.currentTarget).parent().find('.checkbox_list');
            list.toggleClass('select')
        });

        //RNM CLOSER (오른쪽창 닫기)
        $('.area_right_closer').on("click", e => {
            const type = $(e.currentTarget).parents('.area_right').attr('data-value');
            $('.area_right').removeClass("select");
            //선택 해제
            window.map.updateSize();
            window.lySelect.getFeatures().clear();
            //팝업 목록 삭제
            let popup = new mapPopup('map');
            popup.remove('mouseClickPopup');
            //펄스 제거
            window.map.removePulse();
            //연결선 제거
            window.lyConnect.remove(type);
        });

        //RNM TAB SWITCH (오른쪽 창 탭 변경)
        $('.area_right .tab li').on("click", e => {
            const type = $(e.currentTarget).attr('data-value');
            $(e.currentTarget).parents('section').find('.area_right_scroll').removeClass("select");
            $(e.currentTarget).parents('section').find('.area_right_scroll[data-value='+ type +']').addClass("select");
            //ACTIVE STYLE
            $(e.currentTarget).parent().children("li").removeClass("active");
            $(e.currentTarget).addClass("active");
        });

        ////////////////////////////////////////////// 추후 공통소스로 구상
        //대기 타일(맵 도구 기본)
        const airTileAry = window.map.map.getLayers().getArray().slice(5,13);
        for(let i in airTileAry){
            const layerNm = airTileAry[i].getProperties().prop.nameKo;
            const li = "<li>" + layerNm + "</li>";
            $('#airTiles').append(li);
            $('#airTiles li').last().data(airTileAry[i].getProperties());
        }
        $('#airTiles li').on('click', e => {
            //ACTIVE STYLE
            $(e.currentTarget).toggleClass('active');
            const layerNm = $(e.currentTarget).data().name;
            window.lyControl.toggle(layerNm);
            window.map.map.render();
            //범례조작
            if($('#airTiles li.active').length > 0 ){
                $('#legendLayer').show();
                $('#airTabs li a[data-id='+layerNm+']').trigger("click");
            } else {
                $('#legendLayer').hide();
            }
        });
        //대기 타일 범례 탭 조작
        $('#airTabs li a').on("click", e => {
            const id = $(e.currentTarget).attr('data-id');
            const legendAry = window.map.airLegends[id];
            $.each($('#airDensity .item'), function(idx, val) {
                $(val).find('span.unit').text(legendAry[idx]);
            });
            //ACTIVE STYLE
            $(e.currentTarget).parents('#airTabs').find('li').removeClass('active');
            $(e.currentTarget).parent('li').addClass("active");
        });
        //////////////////////////////////////////////

        //MAP TOOL (맵 도구)
        $('.map_options li').on("click", e => {
            const $target = $(e.currentTarget);
            const type = $target.attr('data-value');
            const toggleFlag = $target.hasClass('toggle');
            //ACTIVE STYLE
            if(toggleFlag){
                $target.toggleClass('active');
            }

            switch(type) {
                case "roadView" : window.lyControl.toggle(type); break; //로드뷰
                case "cctv" : window.lyControl.toggle('cctvLayer'); break; //cctv 레이어 on/off
                case "plus" : window.map.zoomInOut('plus'); break; //확대
                case "minus" : window.map.zoomInOut('minus'); break; //축소
                case "distance" : window.measure.initDraw('LineString'); break; //거리재기
                case "measure" : window.measure.initDraw('Polygon'); break; //면적재기
                case "radius" : window.measure.initDraw('Circle'); break; //반경재기
                case "eraser" : window.measure.removeMeasureTool(); break; //지우기
                case "airTile" : //대기정보
                    const $viewer = $("#tileViewer");
                    $viewer.toggle('show');
                    break;
                case "layer" : //레이어 제어
                    const $target = $("#layerViewer");
                    $target.toggle('show');
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

                        let addFlag = true;
                        if(layerNm == "cctvLayer"){
                            if((window.siGunCode != "26290") && (window.siGunCode != "41210")){
                                addFlag = false;
                            }
                        }
                        if(addFlag){
                            const li = "<li data-zindex='"+ zIdx +"'>" + layerNm + "</li>";
                            $("#layers").append(li);
                        }
                    }
                    //레이어 순서 제어
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

        //TOP BUTTON (왼쪽창 리스트 맨위로)
        $(".search_list .button_top").on("click", e => {
            $(e.currentTarget).parent('div').scrollTop(0);
        });

        //검색 키워드 입력 이벤트 (임시공통)
        $(".search_form dt input[type=text]").on('keydown', key => {
            if(key.keyCode==13){
                const keyword = $(key.target).val();
                const section = $(key.currentTarget).parents('section').attr('id');
                searchList(section, keyword);
            }
        });

        //검색 키워드 아이콘 클릭 이벤트 (임시공통)
        $(".search_form dt input[type=text] + i, .search_fold .button").on('click', e => {
            const keyword = $(e.currentTarget).parents('.search_form').find('input').val();
            const section = $(e.currentTarget).parents('section').attr('id');
            searchList(section, keyword);
        });

        //리스트 무한스크롤 이벤트
        $('.search_list').on("scroll", evt => {
            const elem = $(evt.currentTarget);
            const keyword = elem.data().keyword;
            if ( elem.scrollTop() + elem.innerHeight()  >= elem[0].scrollHeight ) {
                const id = elem.parents('.lnb_tab_section').attr('data-value');
                console.log(id);
                switch(id) {
                    case "addressPlaceTab" : //주소장소 탭
                        const target = elem.attr('data-value');
                        if(!(elem.data().meta.is_end)){
                            const page = (Number(elem.data().listCnt) / 15) + 1;
                            const obj = { query : keyword, page : page };
                            if(target=="address") {
                                lnbList.createAddressPlace('address',kakaoApi.getAddress(obj), keyword,false);
                            } else {
                                lnbList.createAddressPlace('place',kakaoApi.getPlace(obj), keyword,false);
                            }
                        }
                        break;
                    default:
                        break;
                } // switch
            } // srcoll 끝
        });

        //관제 검색 조건 더보기 check
        $('.lnb_tab_section .search_fold form input').on('change', e => {
            const $form = $(e.currentTarget).parents('.search_form');
            const checkLen = $form.find('input[type="checkbox"]:checked').length;
            const $target = $form.find('.detail_btn');

            let dateFlag = false;
            $form.find('.date_set input').each((idx, item) => {
                const text = $(item).val();
                if(text != ""){
                    dateFlag = true;
                }
            });
            if(checkLen > 0){
                $target.addClass("active");
            } else {
                if(dateFlag){
                    $target.addClass("active");
                } else {
                    $target.removeClass("active");
                }
            }
        });

        //관제 검색 조건 더보기 text
        $('.lnb_tab_section .search_fold form .date_set input').datepicker({
            onSelect: (text, inst, elem) => {
                const $form = elem.$el.parents('.search_form');

                const $target = $form.find('.detail_btn');
                const checkLen = $form.find('input[type="checkbox"]:checked').length;

                let dateFlag = false;
                $form.find('.date_set input').each((idx, item) => {
                    const text = $(item).val();
                    if(text != ""){
                        dateFlag = true;
                    }
                });
                if(dateFlag){
                    $target.addClass("active");
                } else {
                    if(checkLen > 0){
                        $target.addClass("active");
                    } else {
                        $target.removeClass("active");
                    }
                }
            }
        })
        //관제 검색 조건 초기화
        $('.lnb_tab_section .button.refresh').on("click", e => {
            const section = $(e.currentTarget).parents('section').attr('id');
            const tab = $('#'+section +" .tab li.active").attr('data-value');
            //keyword 처리
            const $target = $('section.select div[data-value='+tab+'].select');
            const $form = $target.find('.search_form');
            $form.find('input').val("");
            //check 풀기
            $form.find('input[type=checkbox]:checked').prop('checked',false);
            $target.find('.detail_btn').removeClass('active');
            //타이틀 초기화
            $form.find('.checkbox_title').each((idx, item) => {
                const text = $(item).data('placeholder');
                $(item).text(text);
            });
            //list reload
            searchList(section, "");
        });

        //관제 이벤트 종료 팝업
        $('section[data-value=event] .occur_process ul li').on("click", e => {
           const type = $(e.currentTarget).attr('data-value');
           if(type == "eventEnd"){
               $('#popupEventEnd').css('display','flex');
           }
        });

        //팝업 closer
        $('.popup_detection_closer, .popup_detection li[data-value=cancel]').on("click", e => {
            const $target = $(e.currentTarget).parents('.popup_detection');
            $target.css('display', 'none');
        });

    }
}

/**
 * 지도중심 읍면동 > 날씨 반환
 * */
function centerVilageInfo(evt) {
    let mapProj = evt.map.getView().getProjection().getCode();
    let mapCenter = evt.map.getView().getCenter();
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
    $(".map_location #admAreaName").text("알수 없음");
    $.ajax({
        type : "POST",
        url : '/adm/lonLatToAdm',
        contentType : "application/json; charset=utf-8",
        dataType : "JSON",
        async : true,
        data : JSON.stringify({
            lon : mapLonLat[0],
            lat : mapLonLat[1]
        })
    }).done( result => {
        if (!result) return;
        $(".map_location #admAreaName").text( result["areaName"]);
    });
}

/**
 * 관제 왼쪽창 리스트 생성
 * TODO 데이터 적용 후 중복코드 정리
 * */
const lnbList = {
    /**
     * 주소장소 리스트 생성
     * type : address, place
     * obj : 카카오 api 반환결과
     * keyword : 검색 키워드 (저장)
     * flag : 검색 초기인가 아닌가
     * */
    createAddressPlace : (type, obj, keyword, flag) => {
        let objCnt = obj.documents.length;
        const target = $('.search_list[data-value='+type+']');

        if(flag){
            target.empty();
            target.scrollTop(0);
        } else {
            const prevCnt = Number($('.area_title .count[data-value='+type+']').text());
            objCnt += prevCnt;
        }
        obj.listCnt = objCnt;
        obj.keyword = keyword;
        target.data(obj);
        $('.area_title .count[data-value='+type+']').text(objCnt);

        obj.documents.forEach(row => {
            let content = "";
            let roadName = row.road_address_name == undefined ? "-" : row.road_address_name;
            if(type == "address"){
                content = "<dl>" +
                    "<dt>" + row["address_name"] + "</dt>" +
                    "<dd>도로명 : " + roadName + "</dd>" +
                    "</dl>"
                ;
            } else if(type == "place") {
                content = "<dl>" +
                    "<dt>" + row["place_name"] + "</dt>" +
                    "<dd>도로명칭</dd>" +
                    "<dd>"+ roadName +"</dd>" +
                    "</dl>"
                ;
            }
            target.append(content);
            target.find('dl').last().data(row);
        });
        //주소장소 리스트 행 클릭 이벤트
        target.find('dl').on("click", e => {
            const data = $(e.currentTarget).data();
            const type = $(e.currentTarget).parents('.search_list').attr('data-value');
            data.type = type;
            // 팝업
            const coordinate = ol.proj.transform([Number(data.x), Number(data.y) ],'EPSG:4326', 'EPSG:5181');
            if(ol.extent.containsCoordinate(window.map.extent, coordinate)){
                window.map.setCenter(coordinate);
            }

            let popup = new mapPopup('map');
            popup.create('addressPopup');
            popup.move('addressPopup', coordinate);
            popup.content('addressPopup',mapPopupContent.addressPlace(data));

        });
    } //createAddressPlace end
    /**
     * 실시간 이벤트 리스트 생성
     * obj : ajax 반환값
     * */
    , createEvent : obj => {
        let objAry = JSON.parse(obj);
        const $target = $('section.select .lnb_tab_section[data-value=event]');

        objAry.features.forEach(each => {
            let content = "";
            const prop = each.properties;
            let level = prop.eventGrade.replace("0",""); //////?
            let cnt = Number($target.find('.area_title[data-value=lv' + level + '] .count').text());

            content = "<dl>" +
                "<dt>" + prop.eventSeq + "<span class='state'>" + prop.eventProcStatName + "</span></dt>" +
                "<dd class='event_level'><span class='level lv" + level + "'>" + prop.eventGradeName + "</span>" + prop.eventKindName + "</dd>" +
                "<dd>" + (prop.address ? prop.address : "-") + "</dd>" +
                "<dd>" + prop.insertDt + "<span class='ago'>" + dateFunc.getDateText(prop.insertDt) + "</span></dd>" +
                "</dl>";

            $target.find('.search_list[data-value=lv' + level + ']').append(content);
            $target.find('.search_list[data-value=lv' + level + '] dl').last().data(each);

            $target.find('.area_title[data-value=lv' + level + '] .count').text(cnt+1);
        });
        //실시간 리스트 행 클릭 이벤트
        $target.find('.search_list dl').on("click", e => {
            const data = $(e.currentTarget).data().properties;
            const coordinate = ol.proj.transform([Number(data.longitude), Number(data.latitude) ],'EPSG:4326', 'EPSG:5181');
            if(ol.extent.containsCoordinate(window.map.extent, coordinate)){
                window.map.setZoom(11);
                window.map.setCenter(coordinate);
                window.map.map.renderSync();
            }
            //아이콘 선택
            const targetFeature = window.lyControl.find('eventLayer').getSource().getClosestFeatureToCoordinate(coordinate);
            window.lySelect.getFeatures().clear();
            window.lySelect.getFeatures().push(targetFeature);

            let tempFeature = targetFeature.getProperties().features;
            tempFeature = tempFeature.filter(ele => {
                return ele.getProperties().eventSeq == data.eventSeq;
            });
            //오른쪽 패널
            clickIcon("event", tempFeature);
        });
    }
    /**
     * 과거이력 이벤트 리스트 생성
     * obj : ajax 반환값
     * */
    , createEventPast : (obj) => {
        let objAry = JSON.parse(obj);
        const $target = $('section.select .lnb_tab_section[data-value=eventPast]');

        objAry.features.forEach(each => {
            let content = "";
            const prop = each.properties;

            let level = prop.eventGrade.replace("0",""); //////?
            let cnt = Number($target.find('.area_title .count').text());

            content = "<dl>" +
                "<dt>" + prop.eventSeq + "<span class='state'>" + prop.eventProcStatName + "</span></dt>" +
                "<dd class='event_level'><span class='level lv" + level + "'>" + prop.eventGradeName + "</span>" + prop.eventKindName + "</dd>" +
                "<dd>" + (prop.address ? prop.address : "-") + "</dd>" +
                "<dd>" + prop.insertDt + "<span class='ago'>" + dateFunc.getDateText(prop.insertDt) + "</span></dd>" +
                "</dl>";

            $target.find('.search_list').append(content);
            $target.find('.search_list dl').last().data(each);

            $target.find('.area_title .count').text(cnt+1);
        });
        //과거이력 리스트 행 클릭 이벤트
        $target.find('.search_list dl').on("click", e => {
            const data = $(e.currentTarget).data().properties;
            const coordinate = ol.proj.transform([Number(data.longitude), Number(data.latitude) ],'EPSG:4326', 'EPSG:5181');
            if(ol.extent.containsCoordinate(window.map.extent, coordinate)){
                window.map.setZoom(11);
                window.map.setCenter(coordinate);
                window.map.map.renderSync();
            }
            // 아이콘 선택
            const targetFeature = window.lyControl.find('eventPastLayer').getSource().getClosestFeatureToCoordinate(coordinate);
            window.lySelect.getFeatures().clear();
            window.lySelect.getFeatures().push(targetFeature);

            let tempFeature = targetFeature.getProperties().features;
            tempFeature = tempFeature.filter(ele => {
                return ele.getProperties().eventSeq == data.eventSeq;
            });
            //오른쪽 패널
            clickIcon("eventPast", tempFeature);
        });
    }
    /**
     * 개소 리스트 생성
     * obj : ajax 반환값
     * type : facility, station (for같은탭에 존재할때)
     * */
    , createStation : (obj, type) => {
        let objAry = JSON.parse(obj);
        const $target = $('section.select .lnb_tab_section[data-value='+ type +']');

        $target.find('.search_list[data-value=station]').html("");
        const cnt = objAry.features.length;

        objAry.features.forEach(each => {
            let content = "";
            const prop = each.properties;

            // let cnt = Number($target.find('.area_title[data-value=station] .count').text());

            content = "<dl>" +
                "<dt>" + prop.stationName + "</dt>" +
                "<dd>구역 : " + prop.administZone + "</dd>" +
                "<dd>주소 : " + (prop.address ? prop.address : "-") + "</dd>" +
                "</dl>";

            $target.find('.search_list[data-value=station]').append(content);
            $target.find('.search_list[data-value=station] dl').last().data(each);
            // $target.find('.area_title[data-value=station] .count').text(cnt+1);
            $target.find('.area_title[data-value=station] .count').text(cnt);
        });
        //개소 리스트 행 클릭 이벤트
        $target.find('.search_list[data-value=station] dl').on("click", e => {
            const data = $(e.currentTarget).data().properties;
            const coordinate = ol.proj.transform([Number(data.longitude), Number(data.latitude)],'EPSG:4326', 'EPSG:5181');
            if(ol.extent.containsCoordinate(window.map.extent, coordinate)){
                window.map.setZoom(11);
                window.map.setCenter(coordinate);
                window.map.map.renderSync();
            }

            // 아이콘 선택
            const targetFeature = window.lyControl.find('stationLayer').getSource().getClosestFeatureToCoordinate(coordinate);
            window.lySelect.getFeatures().clear();
            window.lySelect.getFeatures().push(targetFeature);

            let tempFeature = targetFeature.getProperties().features;
            tempFeature = tempFeature.filter(ele => {
                return ele.getProperties().stationSeq == data.stationSeq;
            });
            //오른쪽 패널
            clickIcon("station", tempFeature);
        });
    }
    /**
     * 시설물 리스트 생성
     * obj : ajax 반환값
     * */
    , createFacility : (obj) => {
        let type = 'facility';
        let objAry = JSON.parse(obj);
        const $target = $('section.select .lnb_tab_section[data-value='+ type +']');

        objAry.features.forEach(each => {
            let content = "";
            const prop = each.properties;
            let cnt = Number($target.find('.area_title[data-value=facility] .count').text());

             /*<dl>
                <dt>DRONE ID<span class="state">비행 중</span></dt>
                <dd>LTE 신호세기 : 양호</dd>
                <dd>잔여비행가능시간 : 5분 (20%)</dd>
                <dd>매주 화 14:00, 매주 목 16:00</dd>
            </dl>*/
            content = "<dl>" +
                "<dt>" + prop.facilityId + " " + prop.facilityStatus + "</dt>" +
                "<dd>LTE 신호세기  : " + prop.facilityKindName + "</dd>" +
                "<dd>잔여비행가능시간 : " + prop.facilityKindName + "</dd>" +
                "<dd>" + "매주 화 14:00, 매주 목 16:00" + "</dd>" +
                "</dl>";

            $target.find('.search_list[data-value=facility]').append(content);
            $target.find('.search_list[data-value=facility] dl').last().data(each);
            $target.find('.area_title[data-value=facility] .count').text(cnt+1);

        });
        //시설물 리스트 행 클릭 이벤트
        $target.find('.search_list[data-value=facility] dl').on("click", e => {
            const data = $(e.currentTarget).data().properties;
            const coordinate = ol.proj.transform([Number(data.longitude), Number(data.latitude)],'EPSG:4326', 'EPSG:5181');
            if(ol.extent.containsCoordinate(window.map.extent, coordinate)){
                window.map.setZoom(11);
                window.map.setCenter(coordinate);
                window.map.map.renderSync();
            }
            // 아이콘 선택
            const targetFeature = window.lyControl.find('facilityLayer').getSource().getClosestFeatureToCoordinate(coordinate);
            window.lySelect.getFeatures().clear();
            window.lySelect.getFeatures().push(targetFeature);

            let facilityAry = window.lyControl.find('facilityLayer').getSource().getFeatures();
            let tempFeature;
            facilityAry.map(ele => {
                if(ele.getProperties().facilitySeq == data.facilitySeq){
                    tempFeature = ele;
                }
            });
            //오른쪽 패널
            clickIcon("facility", tempFeature);
        });
    }
    /**
     * 해당 리스트 초기화
     * type : data-value 값
     * */
    , removeAllList(type) {
        const $target = $('section.select .lnb_tab_section[data-value=' + type + ']');
        $target.find('.area_title .count').text(0);
        $target.find('.search_list dl').remove();

    }
}

/**
 * 관제 오른쪽창 정보 생성
 * TODO 데이터 적용 후 반복코드 정리
 * */
const rnbList = {
    /**
     * 개소 정보 생성
     * obj : ajax 반환값
     * */
    createStation : obj => {
        /*TODO 데이터 오면 정보 채우기*/
        const target = $('.area_right[data-value=station]');
        const prop = obj.getProperties();

        target.data(obj);
        target.addClass('select');
        target.find('.stationTitle').text("[ " + prop.stationSeq + " ] "  + prop.stationName);

        //TODO 대메뉴 타입 가져와서 패널 UI(공통)에서 제거 + 추가


        //prop 돌리면서 채워넣기
        const propList = Object.keys(prop);
        propList.map(propStr => {
            const textArea = target.find('.area_right_text li input[data-value='+propStr+']');
            if(textArea.length > 0){
                textArea.val(prop[propStr]);
            }
        });
        //animation end
        window.map.removePulse();
        //다른 유형 중복선 제거
        window.lyConnect.remove('event');
        window.lyConnect.remove('station');
        const line = window.lyConnect.create(obj.getGeometry().getCoordinates(), '.area_right[data-value=station]', 'station' );
        window.map.addLayer(line);
        window.map.setPulse(obj.getGeometry().getCoordinates());

    }
    , createEvent : obj => {
        /*TODO 데이터 오면 정보 채우기*/

        //이벤트 타겟 레이어 찾기
        let eventTarget = 'station';
        if(window.lyControl.find('facilityLayer').getVisible()){
            eventTarget = 'facility';
        }
        const target = $('.area_right[data-value=event][data-target=' + eventTarget + ']');
        const prop = obj.getProperties();

        target.data(obj);
        target.addClass('select');
        target.find('.eventTitle').text("[ " + prop.eventSeq + " ] "  + prop.eventKindName);

        //TODO 대메뉴 타입 가져와서 패널 UI(공통)에서 제거 + 추가

        //초기화
        target.find('span[data-value=eventGrade] input').prop('checked',false);
        target.find('span[data-value=eventGrade] input#lv'+prop.eventGrade.replace("0","")).prop('checked',true);

        //prop 돌리면서 채워넣기
        const propList = Object.keys(prop);
        propList.map(propStr => {
            const textArea = target.find('.area_right_text li input[data-value='+propStr+']');
            if(textArea.length > 0){
                textArea.val(prop[propStr]);
            }
        });

        window.map.removePulse();
        //다른 유형 중복선 제거
        window.lyConnect.remove('event');
        window.lyConnect.remove('station');
        const line = window.lyConnect.create(obj.getGeometry().getCoordinates(), '.area_right[data-value=event][data-target=' + eventTarget + ']', 'event');
        window.map.addLayer(line);
        window.map.setPulse(obj.getGeometry().getCoordinates());
    }
    , createFacility : obj => {
        const $target = $('.area_right[data-value=facility]');
        const prop = obj.getProperties();
        //TODO 정보 채워두기
        $target.data(obj);
        $target.addClass('select');
        $target.find('.facilityTitle').text("[ " + prop.facilitySeq + " ] "  + prop.facilityKindName);
        $target.find('.facilitySubTitle').eq(0).text("[ "+ prop.facilityId +" ] 기체 사진");
        $target.find('.facilitySubTitle').eq(1).text("[ "+ prop.facilityId +" ] 기체 상세 정보");
        $target.find('.facilitySubTitle').eq(2).text("[ "+ prop.facilityId +" ] 기체 현황");

        //prop 돌리면서 채워넣기
        const propList = Object.keys(prop);
        propList.map(propStr => {
            const textArea = $target.find('.area_right_text li input[data-value='+propStr+']');
            if(textArea.length > 0){
                textArea.val(prop[propStr]);
            }
        });
        //////////
        // video는 냅두고

        // //animation end
        //다른 유형 중복선 제거
        window.lyConnect.remove('event');
        window.lyConnect.remove('station');

    }
}


/**
 * 리스트 검색
 * section : rnm 대메뉴 분류
 * keyword : 검색 키워드
 * */
function searchList(section, keyword) {
    // if(keyword!="" && keyword!=null){
    switch(section) {
        case "smartPole" : //스마트폴검색
            const tab = $('#'+section +" .tab li.active").attr('data-value');

            let objJSON = {};
            if(keyword!="" && keyword!=null){
                objJSON = $('#'+section +'.select .lnb_tab_section.select').find('.search_form .search_fold form').serializeJSON();
                objJSON.keyword = keyword;
           }
            if(tab == "station") {
                //리스트 ajax
                station.getListGeoJson({
                    /// objJSON
                }, result => {
                    lnbList.removeAllList(tab);
                    lnbList.createStation(result);
                });
            } else if(tab == "eventPast") {
                //TODO 조건 form serialize
                //리스트 ajax
                event.getListGeoJson({
                    "eventState": ["9"]
                    , "eventGrade": [20]
                    ////////// objJSON
                }, result => {
                    // 리스트 초기화
                    lnbList.removeAllList(tab);
                    lnbList.createEventPast(result);
                    // 과거 이벤트 레이어 reload
                    reloadLayer(result, 'eventPastLayer');
                    //패널 제어
                    const rVisivle = $('.area_right[data-value=event]').is(':visible');
                    if(rVisivle) { $('.area_right_closer').trigger("click")}
                });
            }
            break;
        case "addressPlace" : //주소장소검색
                const addressObj = kakaoApi.getAddress({query: keyword});
                const placeObj = kakaoApi.getPlace({query: keyword});
                lnbList.createAddressPlace('address', addressObj, keyword, true);
                lnbList.createAddressPlace('place', placeObj, keyword, true);
            break;
        default :
            break;
    }
    // } else {
    //     //TODO 전체검색 || 초기조건
    //     //alert("키워드를 입력하여 주십시오.");
    // }
}

/**
 * 클러스터 레이어 reload
 * result : 변경할 데이터
 * layer : 적용할 레이어명
 * */
function reloadLayer(result, layer) {
    let type;
    const layerNm = layer.replace("Layer", "");
    if(["station", "event", "eventPast"].includes(layerNm)){
        type = "cluster";
    } else if(["facility"].includes(layerNm)){
        type = "vector";
    } else {}

    const newFeatures = new ol.format.GeoJSON().readFeatures(result);
    newFeatures.forEach( each => {
        each.getGeometry().transform('EPSG:4326','EPSG:5181');
    });

    let newSource = new ol.source.Vector();
    newSource.addFeatures(newFeatures);

    switch(type) {
        case "cluster" :
            newSource = new ol.source.Cluster({
                distance: 30, source : newSource
            });
            break;
        case "vector" :
            break;
        default :
            break;
    }

    window.lyControl.find(layer).setSource(newSource);
    window.lyControl.find(layer).changed();
    window.map.map.render();

    //패널 띄워져잇으면
    if($('.area_right').is(":visible")){
        const type = $('.area_right.select').attr('data-value');
        const beforeProp = $('.area_right.select').data();
        // 패널 새로고침
        switch (type) {
            case "facility": //시설물일때
                const seq = beforeProp.getProperties().facilitySeq;
                newFeatures.forEach(f => {
                    if(f.getProperties().facilitySeq == seq){
                        //clickIcon('facility',f);
                        rnbList.createFacility(f);
                        //선택 되어잇으면 선택
                        window.lySelect.getFeatures().push(f);
                        window.map.map.renderSync();
                    }
                });
                break;
            // case "station" :
            //     rnbList.createStation();
            //     break;
            // case "event" :
            //     rnbList.createEvent();
            //     break;
            ///////
            default :
                break;
        }
    }

}