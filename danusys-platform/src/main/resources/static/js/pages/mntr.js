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
                    //팝업 붙이기
                    let popup = new mapPopup('map');
                    popup.create('addressPopup');
                    popup.move('addressPopup',e.coordinate);
                    popup.content('addressPopup',mapPopupContent.coord2address(coordinate));

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
        //맵 선 도구
        let lyConnect = new mapConnectLineCreater('map');
        window.lyConnect = lyConnect;

        //맵 이동 (move end) 이벤트
        map.setMapEventListener('moveend',e => {
            //동네 날씨 기능
            //centerVilageInfo(e);
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
                                        popup.create('mouseOverPopup');
                                        popup.content('mouseOverPopup', content);
                                        popup.move('mouseOverPopup', position);
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

        //개소 레이어
        station.getListGeoJson({} ,result => {
            //console.log(result);
           let stationLayer = new dataLayer('map')
                // .fromGeoJSon(result, 'stationLayer', true, layerStyle.station(false));
               .toCluster(result, 'stationLayer', true, layerStyle.station(false));
           map.addLayer(stationLayer);
           window.lyControl.find('stationLayer').set('selectable',true);

           lnbList.createStation(result);
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

        //이벤트 레이어
        event.getListGeoJson({
            "eventState": ["1", "2", "3"]
            }, result => {
            let eventLayer = new dataLayer('map')
                // .fromGeoJSon(result, 'stationLayer', true, layerStyle.station(false));
                .toCluster(result, 'eventLayer', true, layerStyle.event(false));
            map.addLayer(eventLayer);
            window.lyControl.find('eventLayer').set('selectable',true);

            lnbList.createEvent(result);
        });

        //과거 이벤트 레이어
        event.getListGeoJson({
            "eventState": ["9"]
            }, result => {
            let eventPastLayer = new dataLayer('map')
                .toCluster(result, 'eventPastLayer', true, layerStyle.event(false));
            map.addLayer(eventPastLayer);
            window.lyControl.find('eventPastLayer').set('selectable',true);
            window.lyControl.off('eventPastLayer');

            lnbList.createEventPast(result);
        });

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

                let target = $('.mntr_container section.select .tab li.active').attr('data-value');
                if(target=="station"){target = "event"}
                if(zoom > 10){ //13 ~ 9.xxx
                    window.lyControl.find("stationLayer").getSource().setDistance(0);
                    window.lyControl.find("eventLayer").getSource().setDistance(0);
                    window.lyControl.find("eventPastLayer").getSource().setDistance(0);

                    window.lyControl.on('stationLayer');
                    window.lyControl.on(target + "Layer");
                } else if((10 >= zoom) && (zoom >=5)) { //10 ~ 5
                    window.lyControl.find("stationLayer").getSource().setDistance(30);
                    window.lyControl.find("eventLayer").getSource().setDistance(30);
                    window.lyControl.find("eventPastLayer").getSource().setDistance(30);

                    window.lyControl.on('stationLayer');
                    window.lyControl.on(target + "Layer");
                } else { //4.xxx ~ 0
                    window.lyControl.off('stationLayer');
                    window.lyControl.off('eventLayer');
                    window.lyControl.off('eventPastLayer');
                }
            }


        });

        // facility.getListGeoJson({}, (result) => {
        //    // console.log(result);
        //     let result1 =
        //     {
        //         type: 'FeatureCollection',
        //         name: 'sample',
        //         crs: { type: 'name', properties: { name: 'urn:ogc:def:crs:OGC:1.3:CRS84' } },
        //         features: [
        //             { type: 'Feature', id: 'facility123', properties: { id: 123, nodeCnt: 1 }, geometry: { type: 'Point', coordinates: [ 126.727012512422448, 37.322852752634546 ] } },
        //             { type: 'Feature', id: 'facility234', properties: { id: 234, nodeCnt: 1 }, geometry: { type: 'Point', coordinates: [ 126.750776389512524, 37.309517452940021 ] } },
        //             { type: 'Feature', id: 'facility345', properties: { id: 345, nodeCnt: 2 }, geometry: { type: 'Point', coordinates: [ 126.70449023745131, 37.337370287491666 ] } },
        //             { type: 'Feature', id: 'facility456', properties: { id: 456, nodeCnt: 2 }, geometry: { type: 'Point', coordinates: [ 126.70449023745131, 37.337370287491666 ] } },
        //             { type: 'Feature', id: 'facility567', properties: { id: 567, nodeCnt: 1 }, geometry: { type: 'Point', coordinates: [ 126.744699931551466, 37.319431463919734 ] }},
        //             { type: 'Feature', id: 'facility678', properties: { id: 678, nodeCnt: 1 }, geometry: { type: 'Point', coordinates: [ 126.733088989968962, 37.313668244318841 ] } },
        //             { type: 'Feature', id: 'facility789', properties: { id: 789, nodeCnt: 1 }, geometry: { type: 'Point', coordinates: [ 126.740937197627019, 37.319332213043808 ] } }
        //         ]
        //     };
        //
        //     let facilityLayer = new dataLayer('map')
        //         .fromGeoJSon(result1,'facilityLayer', true, layerStyle.facility(false));
        //         //.toCluster(result1,'facilityLayer', true, layerStyle.station(false));
        //     map.addLayer(facilityLayer);
        //     window.lyControl.find('facilityLayer').set('selectable',true);
        // });

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
            window.lySelect.getFeatures().clear();
            const tab = $(e.currentTarget).attr('data-value');
            const rVisivle = $('.area_right[data-value=event]').is(':visible');
            switch(tab) {
                case "event" :
                    window.lyControl.off('eventPastLayer');
                    break;
                case "eventPast" :
                    window.lyControl.off("eventLayer");
                    break;
                case "station" :
                    window.lyControl.on('eventLayer');
                    window.lyControl.off('eventPastLayer');
                    break;
                default :
                    break;
            }
            if(rVisivle) { $('.area_right_closer').trigger("click")}
            window.lyControl.on(tab + "Layer");

            $(e.currentTarget).parents('section').find('.lnb_tab_section').removeClass("select");
            $(e.currentTarget).parents('section').find('div[data-value='+tab+']').addClass("select");
            //ACTIVE STYLE
            $(e.currentTarget).parent().children("li").removeClass("active");
            $(e.currentTarget).addClass("active");
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
            if(list.hasClass("select")){
                list.removeClass("select");
            } else {
                // $(e.currentTarget).parents('.search_fold').find('.dropdown_checkbox').removeClass("select");
                list.addClass("select");
            }
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
            $('.area_right_scroll').removeClass("select");
            $('.area_right_scroll[data-value='+ type +']').addClass("select");
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
        //TOP BUTTON (왼쪽창 리스트 맨위로)
        $(".search_list .button_top").on("click", e => {
            $(e.currentTarget).parent('div').scrollTop(0);
        });
        //검색 키워드 입력 이벤트 (임시공통)
        $(".search_form input[type=text]").on('keydown', key => {
            if(key.keyCode==13){
                const keyword = $(key.target).val();
                const section = $(key.currentTarget).parents('section').attr('id');
                searchList(section, keyword);
            }
        });
        //검색 키워드 아이콘 클릭 이벤트 (임시공통)
        $(".search_form input[type=text]").next('i').on('click', e => {
            const keyword = $(e.currentTarget).parent().find('input').val();
            const section = $(e.currentTarget).parents('section').attr('id');
            searchList(section, keyword);
        });
        //리스트 무한스크롤 이벤트
        $('.search_list').on("scroll", evt => {
            const elem = $(evt.currentTarget);
            const keyword = elem.data().keyword;
            if ( elem.scrollTop() + elem.innerHeight()  >= elem[0].scrollHeight ) {
                const id = elem.parents('.lnb_tab_section').attr('data-value');
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

            window.map.setCenter(coordinate);

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
            window.map.setZoom(11);
            window.map.setCenter(coordinate);
            window.map.map.renderSync();
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
            window.map.getZoom(11);
            window.map.setCenter(coordinate);
            window.map.map.renderSync();
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
     * */
    , createStation : obj => {
        let objAry = JSON.parse(obj);
        const $target = $('section.select .lnb_tab_section[data-value=station]');

        objAry.features.forEach(each => {
            let content = "";
            const prop = each.properties;

            let cnt = Number($target.find('.area_title .count').text());

            content = "<dl>" +
                "<dt>" + prop.stationName + "</dt>" +
                "<dd>구역 : " + prop.administZone + "</dd>" +
                "<dd>주소 : " + (prop.address ? prop.address : "-") + "</dd>" +
                "</dl>";

            $target.find('.search_list').append(content);
            $target.find('.search_list dl').last().data(each);

            $target.find('.area_title .count').text(cnt+1);
        });
        //개소 리스트 행 클릭 이벤트
        $target.find('.search_list dl').on("click", e => {
            const data = $(e.currentTarget).data().properties;
            const coordinate = ol.proj.transform([Number(data.longitude), Number(data.latitude)],'EPSG:4326', 'EPSG:5181');
            window.map.setZoom(11);
            window.map.setCenter(coordinate);
            window.map.map.renderSync();

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

        //prop 돌리면서 채워넣기
        const propList = ['stationSeq', 'administZone', 'address'];
        propList.map(propStr => {
            target.find('.area_right_text li input[data-value='+propStr+']').val(prop[propStr]);
        });
        //animation end
        window.map.removePulse();
        //다른 유형 중복선 제거
        window.lyConnect.remove('event');
        window.lyConnect.remove('station');
        const line = window.lyConnect.create(obj.getGeometry().getCoordinates(), '.area_right[data-value=station]', 'station' );
        window.map.addLayer(line);
        console.log(obj);
        window.map.setPulse(obj.getGeometry().getCoordinates());

    }
    , createEvent : obj => {
        /*TODO 데이터 오면 정보 채우기*/
        const target = $('.area_right[data-value=event]');
        const prop = obj.getProperties();

        target.data(obj);
        target.addClass('select');
        target.find('.eventTitle').text("[ " + prop.eventSeq + " ] "  + prop.eventKindName);

        //초기화
        target.find('span[data-value=eventGrade] input').prop('checked',false);
        target.find('span[data-value=eventGrade] input#lv'+prop.eventGrade.replace("0","")).prop('checked',true);

        //prop 돌리면서 채워넣기
        const propList = ['eventKindName', 'stationSeq', 'insertDt'];
        propList.map(propStr => {
            target.find('.area_right_text li input[data-value='+propStr+']').val(prop[propStr]);
        });

        window.map.removePulse();
        //다른 유형 중복선 제거
        window.lyConnect.remove('event');
        window.lyConnect.remove('station');
        const line = window.lyConnect.create(obj.getGeometry().getCoordinates(), '.area_right[data-value=event]', 'event');
        window.map.addLayer(line);
        console.log(obj);
        window.map.setPulse(obj.getGeometry().getCoordinates());
    }
}


/**
 * 리스트 검색
 * */
function searchList(section, keyword) {
    if(keyword!="" && keyword!=null){
        switch(section) {
            case "addressPlace" : //주소장소검색
                    const addressObj = kakaoApi.getAddress({query: keyword});
                    const placeObj = kakaoApi.getPlace({query: keyword});
                    lnbList.createAddressPlace('address', addressObj, keyword, true);
                    lnbList.createAddressPlace('place', placeObj, keyword, true);
                break;
            //case "" :
            default :
                break;
        }
    } else {
        alert("키워드를 입력하여 주십시오.");
    }
}
