/**
 * 관제
 */
const mntr = {
    init : () => {
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
            },
        ];

        //지도 생성
        let map = new mapCreater('map',0, '119-26000');
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

        //축척별 레이어 반응
        map.setMapViewEventListener('propertychange' ,e => {
            //연결선 리로드
            window.map.map.getLayers().getArray().map(ly => {
                if(ly.get('title')){
                    if(ly.get('title').includes('lineLayer')){
                        window.lyConnect.reload(ly.getProperties().type);
                    }
                }
            });
        });


    }
    /**
     * 메뉴별 관제 초기 데이터 구분 파라미터 요청
     * param :
     * */
    , getInitParam : (pageType) => {
        let param = {};
        $.ajax({
            url : "/config/mntrPageTypeData/" + pageType
            , type : "GET"
            , async : false
        }).done((result) => {
            param = result;
        });
        return param;
    }
    , eventHandler : () => {
        //RNM CLOSER (오른쪽창 닫기)
        $('#navLClose, .panel_toggle').on("click", e => {
            const type = $(e.currentTarget).parents('.panel').attr('data-value');
            $('.panel').toggleClass("select");

            const panelToggle = $('.panel_toggle img').attr('src');
            if(panelToggle.indexOf('close') > -1)
                $('.panel_toggle img').attr('src', panelToggle.replace('close', 'open'));
            else
                $('.panel_toggle img').attr('src', panelToggle.replace('open', 'close'));

            //선택 해제
            window.map.updateSize();
        });

        //RNM TAB SWITCH (오른쪽 창 탭 변경)
        $('.sort_tab li').on("click", e => {
            const type = $(e.currentTarget).attr('data-value');
            $(e.currentTarget).parents('.panel_contents').find('.area_right_scroll').removeClass("select");
            $(e.currentTarget).parents('.panel_contents').find('.area_right_scroll[data-value='+ type +']').addClass("select");
            //ACTIVE STYLE
            $(e.currentTarget).parent().children("li").removeClass("active");
            $(e.currentTarget).addClass("active");
        });

        ////////////////////////////////////////////// 추후 공통소스로 구상
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

        $('#mapLayer').on("click", e => {
            $(".popup_layer").toggle();
        })

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
            + " " + result["tmp"] + "℃" ;
        $(".map_info #admWeather").empty();
        $(".map_info #admWeather").append(html);
    });
    // // 현재 위치 시-군-동 반환
    $(".map_info #admAreaName").text("알수 없음");
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
        $(".map_info #admAreaName").text( result["areaName"]);
    });
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
}