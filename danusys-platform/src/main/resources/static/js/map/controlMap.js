//플랫폼 마다 만들고 자주쓴다면 공통으로.

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

        //api coord to address
        /////////////////////////

        // let content = r.documents[0].address.address_name;
        // if((r.documents[0].road_address)!=null){
        //     content += "<div class='address-tooltip-header'>[도로명]</div>"
        //         + "<p>" + r.documents[0].road_address.address_name + "<p>"  ;
        // }
        //////////////////////////////////////////////////////////////////////
        let info = "infotest";
        let content =
            "<dl>" +
                "<dt>전북 김제시 죽산면 석산길</dt>" +
                "<dd>(지번)&nbsp;" + info + "</dd>" +
                "<dd>(위도)&nbsp;" + data[1] + "</dd>" +
                "<dd>(경도)&nbsp;" + data[0] + "</dd>" +
            "</dl>"
        ;
            // "<a class='address-tooltip-closer icon_closed'><img src='/images/common/iconClosed.svg' /></a></div>"
            // + "<div class='address-tooltip-header'> [좌표] </div>"
            // //+ "<p>" + clickLonLat[1] +", "+ clickLonLat[0] +"</p>"
            // + "<p>" + data.coordinate[1] +", "+ data.coordinate[0] +"</p>"
            // + "<div class='address-tooltip-header'> [지번] </div>"
            // + "<p>" + info + "</p>"

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
 * 레이어 선택 조작
 */
const layerSelect = {
    add(layer) {
        const select =
            new ol.interaction.Select({
                layers : [layer]
                // layer => {
                //     return layer.get('selectable') === true;
                // }
                , style : feature => {
                    let layerName = feature.getId().replace(/[0-9]/g,"");
                    console.log(layerStyle[layerName](true));
                    return eval(layerStyle[layerName](true));
                }
            });
        return select;
    }
}