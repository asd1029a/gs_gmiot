const a = {
    init : (function () {

    })
}

const b = a.init();

/**
 * 맵 위의 팝업
 * */
class mapPopup {
    /**
     * @param target : 레이어 적용할 맵명
     * */
    constructor(target){
        this.map = window[target].map;
    }

    //팝업 생성
    create(id) {
        if(this.find(id)){
            this.remove(id);
        }

        const popupElement = document.createElement('div');
        popupElement.id = id;
        popupElement.className = 'my-ol-popup';

        const popup = new ol.Overlay({
            id : id, //맵에서 찾기위해 필요
            element : popupElement,
            offset : [0,-15],
            positioning: 'bottom-center'
        });

        popupElement.innerHTML =
            "<i id="+ id +"Closer class='my-ol-popup-closer'>" +
                "<img src='/images/default/close.svg'>" +
            "</i>"
            + "<div id="+ id +"Content class='popup_mini'></div>"
        ;

        this.map.addOverlay(popup);
        popup.setPosition(undefined);

        //POPUP CLOSE BUTTON
        $(".my-ol-popup-closer").on('click', e => {
            const targetId = $(e.currentTarget).parents('.my-ol-popup').attr('id');
            this.remove(targetId);
            //this.map.getInteractions().forEach( e => {} );
        });
    }

    /**
     * 팝업 객체 찾기
     * @param id 찾을 팝업 아이디
     * @return 해당 아이디 팝업 객체
     */
    find(id) {
        return this.map.getOverlayById(id);
    }

    /**
     * 해당 팝업 좌표 이동
     * @param id 이동할 팝업객체 이름
     * @param position 이동할 좌표(베이스 지도좌표계 좌표)
     */
    move(id,position) {
        this.map.getOverlayById(id).setPosition(position);
    }

    //해당 팝업 제거하기
    remove(id) {
        if(this.exist(id)){
            this.map.removeOverlay(
                this.find(id)
            );
        }
    }

    /**
     * 팝업 존재여부 판단
     * @param id 판단할 팝업 아이디
     * @return boolean (존재(true)/미존재(false))
     */
    exist(id) {
        let	flag = false;
        if(this.map.getOverlayById(id) != null){
            flag = true;
        }
        return flag;
    }

    //팝업 숨기기
    hide(id) {
        this.find(id).setPosition(undefined);
    }

    /**
     * 팝업 내용 변경
     * @param id 변경할
     * @param content 변경할 팝업 내용(html)
     */
    content(id,content) {
        $("#"+id+"Content").html(content);
    }

}

