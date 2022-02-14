//플랫폼 마다 만들고 자주쓴다면 공통으로.

/**
 * 플랫폼 당 스타일 지정
 * */
const layerStyle = {
    //개소
    station : () => {
        return new ol.style.Style({
            image: new ol.style.Circle({
                radius:13,
                stroke: new ol.style.Stroke({
                    color:'rgba(255,255,255,1)',
                    width: 2,
                }),
                fill: new ol.style.Fill({
                    color: 'red',
                })
            }),
            text: new ol.style.Text({
                text: '10',
                fill: new ol.style.Fill({
                    color:'white',
                    font: '13px'
                })
            })
        });
    }
    //이벤트
    , event : () => {
        return null;
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
            "<div class='popup_mini'>" +
                "<dl>" +
                    "<dt>전북 김제시 죽산면 석산길<i><img src='/images/default/close.svg'></i></dt>" +
                    "<dd>(지번)&nbsp;" + info + "</dd>" +
                    "<dd>(위도)&nbsp;" + data.coordinate[1] + "</dd>" +
                    "<dd>(경도)&nbsp;" + data.coordinate[0] + "</dd>" +
                "</dl>" +
            "</div>"
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