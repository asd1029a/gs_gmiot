//플랫폼 마다 만들고 자주쓴다면 공통으로

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
}