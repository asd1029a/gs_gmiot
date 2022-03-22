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
        return feature => {
            let fillColor = selectFlag ? "white" : "red";
            let strokeColor =  selectFlag ? "red" : "white";

            const cnt = feature.getProperties().facilityCnt;
            const sty = new ol.style.Style({
                    image: new ol.style.Circle({
                        radius:13,
                        stroke: new ol.style.Stroke({
                            color: strokeColor,
                            width: 2,
                        }),
                        fill: new ol.style.Fill({
                            color: fillColor,
                        })
                    }),
                    text: new ol.style.Text({
                        scale: 2,
                        offsetY: -12,
                        offsetX: 14,
                        text: "+" + String(cnt),
                        fill: new ol.style.Fill({
                            color:'black',
                            font: '13px'
                        }),
                        stroke: new ol.style.Stroke({
                            color: 'white',
                            width: 1
                        })
                    })
                });
            return sty;
        }
    }
    //이벤트
    , event : () => {
        return null;
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
    //주소장소 팝업
    address : (data) => {
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
    //개소
    , station() {

    }
    //이벤트
    , event() {

    }
}

/**
 * 레이어별 클릭 이벤트
 * */
function clickIcon(layerType, layerObj) {
    $('.area_right').show(); //임시
    window.map.updateSize();
    switch (layerType)  {
        case "station":
            ///개소 클릭 이벤트
            console.log(layerObj);
            return false;
        case "facility" :
            //시설물 클릭 이벤트
            console.log(layerObj);
            return false;
        default :
            ////////////////////
    }


}


