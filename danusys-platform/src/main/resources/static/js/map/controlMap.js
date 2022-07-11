//플랫폼 마다 만들고 자주쓴다면 공통으로

/**
 * 플랫폼 당 스타일 지정
 * */
const layerStyle = {
    /**
     * 개소의 스타일
     * @selectFlag 선택시 스타일
     * */
    station : (selectFlag) => {
        return (feature, resolution) => {
            //console.log(feature);
            let fillColor = selectFlag ? "white" : "#6c8ce6";
            let strokeColor =  selectFlag ? "#6c8ce6" : "white";

            const features = feature.get("features");
            const size = features.length;
            let textStyle = null;

             if(size > 1) {
                 textStyle =  new ol.style.Text({
                     scale: 2,
                     offsetY: -8,
                     offsetX: 10,
                     text: "+" + (size-1) ,
                     fill: new ol.style.Fill({
                         color:'black',
                         width: 3
                     }),
                     font: 'Bold 10px Arial',
                     stroke: new ol.style.Stroke({
                         color: 'white',
                         width: 2
                     })
                 });
             }

            return new ol.style.Style({
                image: new ol.style.Circle({
                    radius:10,
                    stroke: new ol.style.Stroke({
                        color: strokeColor,
                        width: 2,
                    }),
                    fill: new ol.style.Fill({
                        color: fillColor
                    })
                }),
                text: textStyle
            });
        }
    }
    //이벤트
    , event : (selectFlag) => {
        return (feature, resolution) => {
            let flag = selectFlag ? "_select" : "";

            const features = feature.get("features");
            //grade 중복제거
            let list = features.filter((item1, idx1) => {
                return features.findIndex((item2, idx2)=> {
                    return item1.getProperties().eventGrade == item2.getProperties().eventGrade
                }) == idx1;
            });
            //grade 오름차순 정렬
            list.sort((a,b) => {
                return a.getProperties().eventGrade - b.getProperties().eventGrade;
            });

            const firstInfo =  list[0].getProperties();
            let grade = firstInfo.eventGrade;
            if(firstInfo.eventProcStat == "9"){ //과거이력
                grade = "9";
            }
            //이벤트 등급 (긴급 10 주의 20 과거 9)
            const gradeName  = {"10": "danger" ,"20": "caution", "9" : "past"};

            const key = "event_" + gradeName[grade] + flag;
            const size = features.length;
            let textStyle = null;

            if(size > 1) {
                textStyle =  new ol.style.Text({
                    scale: 2,
                    offsetY: -50,
                    offsetX: 10,
                    text: "+" + (size-1) ,
                    fill: new ol.style.Fill({
                        color:'black',
                        width: 3
                    }),
                    font: 'Bold 10px Arial',
                    stroke: new ol.style.Stroke({
                        color: 'white',
                        width: 3
                    })
                });
            }

            return new ol.style.Style({
                image: new ol.style.Icon({
                    anchor : [20,60],
                    anchorXUnits : 'pixel',
                    anchorYUnits : 'pixel',
                    img : imgObj[key],
                    imgSize: [50,50],
                    scale : 1
                }),
                text: textStyle
            });
        }
    }
    //시설물
    , facility : (selectFlag) => {
        return feature => {
            const selected = feature.getProperties().selected;
            let imgNm = "";
            if(selected != undefined){
                selectFlag = selected;
            }
            imgNm = selectFlag ? "drone_select" : "drone" ;

            const prop = feature.getProperties();
            const style = new ol.style.Style({
                text : new ol.style.Text({
                    text: String(prop.facilityId),
                    offsetY: 20,
                    fill: new ol.style.Fill({
                        color:'black',
                        width: 3
                    }),
                    font: 'Bold 10px Arial',
                    stroke: new ol.style.Stroke({
                        color: 'white',
                        width: 2
                    })
                }),
                image: new ol.style.Icon({
                    anchor:[0.5,0.5],
                    anchorXUnits: 'fraction',
                    anchorYUnits: 'fraction',
                    img: imgObj[imgNm],
                    imgSize:[50,50],
                    scale: 1,
                    rotation: 30 + (Math.random()*20)// TODO prop.rotate
                })
            });
            return style;
        }
    }
    //route
    , route : () => {
        return feature => {
            let text = "";
            if(feature.getGeometry() instanceof ol.geom.Point){ //지점일시
                text = String(feature.getProperties().properties.properties.order);

                const style =
                    new ol.style.Style({
                        image: new ol.style.Circle({
                            radius:10,
                            stroke: new ol.style.Stroke({
                                color: 'white',
                                width: 2,
                            }),
                            fill: new ol.style.Fill({
                                color: 'green'
                            })
                        }),
                        text: new ol.style.Text({
                            offsetX:10,
                            offsetY:-16,
                            text: text,
                            fill: new ol.style.Fill({
                                color : 'white',
                            }),
                            stroke : new ol.style.Stroke({
                                color: 'green',//'#FF4747',//'#002060',//'#9857FF',
                                width: 3
                            }),
                            scale: 1.7
                        })
                    });
                return style;
            } else { //선일시
                const geometry = feature.getGeometry();

                const style = [new ol.style.Style({
                    stroke : new ol.style.Stroke({
                        color : 'green',//'#ff4242',
                        width: 4,
                        lineDash: [.1, 5]
                    })
                })];
                return style;
            }
        }
    }
    //cctv
    , cctv : (selectFlag) => {
        return feature => {
            const text = feature.getProperties().facilityName; //facilityId

            let keys = 'cctv_useCd1';
            keys = selectFlag ?  keys +'_select' : keys;

            const style = new ol.style.Style({
                image: new ol.style.Icon({
                    anchor:[0.5,0.5],
                    anchorXUnits: 'fraction',
                    anchorYUnits: 'fraction',
                    img: imgObj[keys],
                    imgSize:[50,50],
                    scale: 0.7
                }),
                text : new ol.style.Text({
                    text: String(text),
                    offsetY: 20,
                    fill: new ol.style.Fill({
                        color:'black',
                        width: 3
                    }),
                    font: 'Bold 10px Arial',
                    stroke: new ol.style.Stroke({
                        color: 'white',
                        width: 2
                    })
                })
            });
            return style;
        }
    }
    //(디밍 설정)시설물
    , dimming : (selectFlag) => {
       return feature => {
           const selected = feature.getProperties().properties.selected;
           const text = feature.getProperties().properties.facilityId;
           if(selected != undefined){
               selectFlag = selected;
           }
           let fillColor = selectFlag ? "white" : "blue";
           let strokeColor =  selectFlag ? "blue" : "white";

           const style = new ol.style.Style({
               image: new ol.style.Circle({
                   radius: 13,
                   stroke: new ol.style.Stroke({
                       color : strokeColor,
                       width: 2
                   }),
                   fill: new ol.style.Fill({
                       color: fillColor,
                   })
               }),
               text : new ol.style.Text({
                   text: text,
                   offsetY: 20,
                   fill: new ol.style.Fill({
                       color:'black',
                       width: 3
                   }),
                   font: 'Bold 10px Arial',
                   stroke: new ol.style.Stroke({
                       color: 'white',
                       width: 3
                   })
               })
           });
           return style;
       }
    }
}

/**
 * 플랫폼 당 팝업 내용 생성
 * */
const mapPopupContent = {
    //좌표 주소보기 팝업
    coord2address : data => {
        const resultObj = kakaoApi.getCoord2Address({ x:data[0], y:data[1] });

        let addressName = resultObj.documents[0].address.address_name;
        let content =
            "<dt>" + addressName + "</dt>" +
            "<dd>(지번)&nbsp;" + addressName + "</dd>";
        if((resultObj.documents[0].road_address)!=null){
            content += "<dd>(도로명)&nbsp" + resultObj.documents[0].road_address.address_name + "<dd>"  ;
        }
        content += "<dd>(위도)&nbsp;" + data[1] + "</dd>" +
                   "<dd>(경도)&nbsp;" + data[0] + "</dd>"
        ;
        content = "<dl>"+ content +"</dl>"

        return content;
    }
    //주소 장소 팝업
    , addressPlace : data => {
        let title = "";
        if(data.type=="address") {
            title = data.address_name;
        } else {
            title = data.place_name;
        }
        let content =
            "<dt>" + title + "</dt>" +
            "<dd>(지번)&nbsp;" + data.address_name + "</dd>";
        if((data.road_address_name!="")&&(data.road_address_name!=undefined)){
            content += "<dd>(도로명)&nbsp" + data.road_address_name + "<dd>"  ;
        }
        content += "<dd>(위도)&nbsp;" + data.y + "</dd>" +
            "<dd>(경도)&nbsp;" + data.x + "</dd>"
        ;
        content = "<dl>"+ content +"</dl>"

        return content;
    }
    //개소
    , station : (data, length) => {
        let content = "";
        if(length == 1){ //단일 팝업
            const info = data.getProperties().features[0].getProperties();
            content = "<dl>" + info.stationName + "</dl>";
        } else if(length > 1) { //다중 팝업
            content = "<li><span class='circle lv3'></span>" + data.getProperties().stationName + "</li>";
        }
        return content;
    }
    //이벤트 //이벤트 과거
    , event : (data, length) =>  {
        let content = "";
        if(length == 1){ //단일 팝업
            const info = data.getProperties().features[0].getProperties();
            content = "<dl><dt><span class='level lv"+ info.eventGrade + "'>" + info.eventGradeName + "</span> " + info.eventKindName + "</dt>" +
                "<dd>" + info.insertDt + "<span class='state'>" + info.eventProcStatName +"</span></dd></dl>";
        } else if(length > 1) { //다중 팝업
            let grade = data.getProperties().eventGrade;
            if(data.getProperties().eventProcStat == "9") {
                grade = "999";
            }
            content += "<li><span class='circle lv"+ grade +"'></span>" + data.getProperties().eventMessage + "</li>";
        }
        return content;
    }
    //디밍 시설물
    , dimming : (data) => {
        return "<div>" + data.facilityId + "</div>";
    }
}

/**
 * 레이어별 클릭 이벤트
 * */
function clickIcon(layerType, layerObj) {
    //리스트 클릭시
    let len = 1;
    let features = layerObj;
    let popup = new mapPopup('map');
    let position = null;
    //지도 클릭시
    if(layerObj instanceof ol.Feature) {
        if(layerObj.getProperties().features){
            len = layerObj.getProperties().features.length;
            features = layerObj.getProperties().features;
        }
        position = layerObj.getGeometry().getCoordinates();
    }
    //features[0].set('layerName', layerType) //레이어구분

    switch (layerType)  {
        case "station": ///개소 클릭 이벤트
            if(len == 1){
                $('.area_right').removeClass('select');
                rnbList.createStation(features[0]);
                window.map.updateSize();
            } else if(len > 1) {
                popup.create('mouseClickPopup');
                popup.content("mouseClickPopup"
                    , "<ul class='multiple_list'></ul>");

                features.forEach(obj => {
                    let content = mapPopupContent.station(obj, len);
                    $('#mouseClickPopup .multiple_list').append(content);
                    $('#mouseClickPopup .multiple_list li').last().data(obj);
                });
                popup.move('mouseClickPopup', position);
            }
            break;
        case "facility" : //시설물(드론) 클릭 이벤트
            //TODO 드론 오른쪽 패널
            $('.area_right').removeClass('select');
            //TODO video 재생
            //////////////////////////
            rnbList.createFacility(features);
            window.map.updateSize();
            break;
        case "event" : //이벤트 클릭 이벤트
        case "eventPast" : //이벤트이력 클릭 이벤트
            if(len == 1){
                $('.area_right').removeClass('select');
                rnbList.createEvent(features[0]);
                window.map.updateSize();
            } else if(len > 1) {
                popup.create('mouseClickPopup');
                popup.content("mouseClickPopup"
                    , "<ul class='multiple_list'></ul>");

                features.forEach(obj => {
                    let content = mapPopupContent.event(obj, len);
                    $('#mouseClickPopup .multiple_list').append(content);
                    $('#mouseClickPopup .multiple_list li').last().data(obj);
                });

                let point = window.map.map.getPixelFromCoordinate(position);
                position = window.map.map.getCoordinateFromPixel([point[0], point[1] - 50]);
                popup.move('mouseClickPopup', position);
            }
            break;
        case "cctv" : //cctv 클릭 이벤트
            //TODO 영상 재생 연결
            console.log(layerObj);
            console.log(layerObj.getProperties().facilitySeq);

            let prop = layerObj.getProperties();

            const rtspOpt = prop.facilityOpts.filter(opt => opt.facilityOptName === "rtsp_url");
            if(rtspOpt.length > 0){
                const rtspUrl = rtspOpt[0].facilityOptValue;
                const videoData = {
                    facilitySeq : prop.facilitySeq,
                    rtspUrl : rtspUrl,
                    facilityKind : prop.facilityKind,
                    lon : prop.longitude,
                    lat : prop.latitude
                }

                const dialogOption = {
                    draggable: true,
                    clickable: true,
                    data: videoData,
                    css: {
                        width: '400px',
                        height: '340px'
                    }
                }

                const dialog = $.connectDialog(dialogOption);

                const videoOption = {};
                videoOption.data = videoData;
                videoOption.parent = dialog;
                videoOption.btnFlag = false;
                videoOption.isSite = false;
                videoOption.site_video_wrap = true;

                if(!videoManager.createPlayer(videoOption)) {
                    dialogManager.close(dialog);
                };
            }

            break;
        default :
            break;
    } //switch end

    //클릭 팝업 삭제 -> 레이어 아이콘 선택해제
    $("#mouseClickPopupCloser").on("click", e => {
        let features = window.lySelect.getFeatures();
        features.clear();
    });

    //리스트 팝업 클릭 -> 오른쪽 패널
    $("#mouseClickPopup .multiple_list li").on("click", e => {
        let data = $(e.currentTarget).data();
        const layerNm = data.getId().replace(/[0-9]/gi,'');

        const coordinate = data.getGeometry().getCoordinates();
        window.map.setZoom(11);
        window.map.setCenter(coordinate);

        window.map.map.renderSync();

        const targetFeature = window.lyControl.find(layerNm + 'Layer').getSource().getClosestFeatureToCoordinate(coordinate);
        window.lySelect.getFeatures().clear();
        window.lySelect.getFeatures().push(targetFeature);

        $('.area_right').removeClass('select');
        if(layerNm == "station"){
            rnbList.createStation(data);
        } else if((layerNm == "event")||(layerNm == "eventPast")){
            rnbList.createEvent(data);
        }
        // else if(layerNm == "facility"){
        //     rnbList.createDrone(data);
        // }



    });

}

/**
 * 드론 레이어 polling
 * */
let droneTimer;
const dronePolling = {
    start : () => {
        droneTimer = setInterval(dronePolling.refresh, 5000);
    }
    , stop : () => {
        clearInterval(droneTimer);
    }
    , refresh : () => {
        facility.getListGeoJson({
            "facilityKind": ["DRONE"]
        },result => {
            reloadLayer(result, 'facilityLayer');
        });
    }
}

/** fcltid -> facilityid
 * 시설물 팝업생성
 * @function evtMain.createFcltSlideContent
 * @param {Array} data - 시설물 데이터 Array
 */
function createFcltSlideContent(data){
    let content = document.createElement('div');
    content.classList.add('slide-popup');
    content.style.width = data.length > 5 ? 50 + (5 * 25) + 'px' : 50 + (data.length * 25) + 'px';

    let leftBtn = document.createElement('a');

    leftBtn.setAttribute('href', '#');
    leftBtn.classList.add('img-box');
    leftBtn.classList.add('left-zero');

    let leftImg = document.createElement('img');
    leftImg.src = '/images/default/arrowPrev.svg';
    leftImg.style.width = '25px';

    leftBtn.appendChild(leftImg);

    let divWrap = document.createElement('div');

    divWrap.classList.add('menu-wrap');

    let ul = document.createElement('ul');

    ul.id = 'cctvSlide';

    for(let i = 0; i < data.length; i++) {
        let obj = data[i];

        let li = document.createElement('li');
        let a = document.createElement('a');
        let img = document.createElement('img');
        li.style.width = '25px';

        a.setAttribute('href', '#');
        a.classList.add('img-box');

        const directionLayer = new ol.layer.Vector({
            source: new ol.source.Vector()
        });

        const directionFeature = new ol.Feature({});
        obj.directionLayer = directionLayer;
        obj.directionFeature = directionFeature;
        directionLayer.getSource().addFeature(directionFeature);

        $(a).bind({
            'click': function(e) {
                if($('.circlr_container').length > 0 && !$('#setCirclr').is(':visible')) {
                    alert('순환감시 추가, 수정 시에만 영상이 재생됩니다.');
                    return;
                }

                if(!videoManager.isPlaying(obj.facilityId)) {
                    return;
                }

                const dialogOption = {
                    draggable: true,
                    clickable: true,
                    data: obj,
                    css: {
                        width: '400px',
                        height: '340px'
                    }
                }

                const dialog = $.connectDialog(dialogOption);
                dialog.data('siteList', data);

                const option = {};
                option.data = obj;
                option.parent = dialog;

                if(!videoManager.createPlayer(option)) {
                    dialogManager.close(dialog);
                };
            },
            'mouseenter': function(e) {
                window.map.map.addLayer(directionLayer);
                getPresetPoint(obj, obj.presetNo, directionFeature, createPresetDirection);
            },
            'mouseout': function(e) {
                window.map.map.removeLayer(directionLayer);

            }
        });

        img.src = getCctvMarkerImage(obj);

        a.appendChild(img);
        li.appendChild(a);
        ul.appendChild(li)
    }

    divWrap.appendChild(ul);

    let rightBtn = document.createElement('a');

    rightBtn.setAttribute('href', '#');
    rightBtn.classList.add('img-box');
    rightBtn.classList.add('right-zero');

    let rightImg = document.createElement('img');
    rightImg.src = '/images/default/arrowNext.svg';
    rightImg.style.width = '25px';

    rightBtn.appendChild(rightImg);

    content.appendChild(leftBtn);
    content.appendChild(divWrap);
    content.appendChild(rightBtn);

    let cctvSlide = slideMenu.init();
    cctvSlide.init(ul, data.length > 5 ? 5 : data.length, 'h');

    leftBtn.addEventListener('click', function() {
        cctvSlide.move.left();
    });

    rightBtn.addEventListener('click', function() {
        cctvSlide.move.right();
    });

    let overFlag = false;

    content.addEventListener('mouseover', function() {
        overFlag = true;
        clearTimeout(closeOverlay);
    });

    content.addEventListener('mouseleave', function() {
        overFlag = false;
        window.map.removeOverlayById('cctv');
    });

    let closeOverlay = setTimeout(function() {
        if(!overFlag) {
            window.map.removeOverlayById('cctv');
        }
    }, 500);

    $('.ol-overlaycontainer-stopevent').on('pointermove', function(e){
        e.stopPropagation();
    });

    return content;
}

/**
 * 시설물데이터 마커 삽입 기능
 * @param {object} data -마커데이터
 * @function map.getCctvMarkerImage
 */
function getCctvMarkerImage(data) {
    const fcltPurposeCd = 0;
    const cctvAgYn = data.cctvAgYn;
    const presetNo = data.presetNo;
    const stateCd = data.stateCd;
    let imageSrc = '/images/mapicon/cctv_crime.svg';

    // if(stateCd == '1') {
    //     imageSrc = '/images/icons/cctv/cctv_state_0.png'; // 마커이미지의 주소입니다
    // } else if(cctvAgYn == '0') {
    //     imageSrc = '/images/icons/cctv/cctv_'+cctvAgYn+'_'+fcltPurposeCd+'_0.png'; // 마커이미지의 주소입니다
    // } else {
    //     imageSrc = '/images/icons/cctv/cctv_'+cctvAgYn+'_'+fcltPurposeCd+'_0.png'; // 마커이미지의 주소입니다
    // }

    return imageSrc;
}

/**
 * 같은위치에 있는 cctv 리스트를 가져오는 기능    ????????????????
 * @param {object} feature - feature 데이터
 * @param {function} callback - callback function
 * @function map.getSiteList
 */
function getSiteList(feature, callback){
    const jsonObj = {};
    jsonObj.sameLat = feature.getProperties().lat;
    jsonObj.sameLon = feature.getProperties().lon;
    jsonObj.sameCctv = true;
    jsonObj.recordCountPerPage = '-1';
    jsonObj.pageKind = 'manage';
    $.ajax({
        type      : "POST",
        url         : "/select/facility.selectSiteCctvList/action.do",
        dataType   : "json",
        data      : {
            "param"   : JSON.stringify(jsonObj)
        },
        async      : false
    }).done(function(result){
        if(typeof callback == 'function') callback(feature, rows);
    })
}

