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

            const grade = list[0].getProperties().eventGrade;
            //이벤트 등급 (긴급 10 주의 20 과거 ?)
            const gradeName  = {"10": "danger" ,"20": "caution"};

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
            if( firstInfo.eventProcStat == "9"){ //과거이력
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
            content = "<dl><dt>"+ info.eventGradeName + " " + info.eventKindName + "</dt>" +
                "<dd>" + info.insertDt + "<span class='state state"+ info.eventProcStat +"'>" + info.eventProcStatName +"</span></dd></dl>";
        } else if(length > 1) { //다중 팝업
            content += "<li><span class='circle lv"+ data.getProperties().eventProcStat +"'></span>" + data.getProperties().eventMessage + "</li>";
        }
        return content;
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
        len = layerObj.getProperties().features.length;
        features = layerObj.getProperties().features;
        position = layerObj.getGeometry().getCoordinates();
    }

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

        case "facility" : //시설물 클릭 이벤트
            console.log(layerObj);
            break;

        case "event" : //이벤트 클릭 이벤트
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

        $('.area_right').removeClass('select');
        if(layerNm == "station"){
            rnbList.createStation(data);
        } else if((layerNm == "event")||(layerNm == "eventPast")){
            rnbList.createEvent(data);
        }
    });

}


