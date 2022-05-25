const imgSrcObj = {};
const imgObj = {};
/**
 *  svg를 레이어로 사용하기 위한 기초작업
 */
const svgToImage = {
    /**
     * 이미지 경로 설정
     * */
    init() {
        const rootPath = '/images/mapIcon/'; //icon svg 경로
        const fileType = '.svg';

        //관제 이벤트 icon
        imgSrcObj['event_caution'] = rootPath + 'event_caution' + fileType;
        imgSrcObj['event_caution_select'] = rootPath + 'event_caution_select' + fileType;
        imgSrcObj['event_danger'] = rootPath + 'event_danger' + fileType;
        imgSrcObj['event_danger_select'] = rootPath + 'event_danger_select' + fileType;
        imgSrcObj['event_past'] = rootPath + 'event_past' + fileType;
        imgSrcObj['event_past_select'] = rootPath + 'event_past_select' + fileType;
        //관제 cctv icon
        imgSrcObj['cctv_useCd1'] = rootPath + 'cctv_crime' + fileType;
        imgSrcObj['cctv_useCd1_select'] = rootPath + 'cctv_crime_select' + fileType;
        //관제 드론 icon
        imgSrcObj['drone'] = rootPath + 'drone_pointer' + fileType;
        imgSrcObj['drone_select'] = rootPath + 'drone_pointer_select' + fileType;

        for (const [k, v] of Object.entries(imgSrcObj)) {
            imgObj[k] = svgToImage.create(k);
        }
    },
    /**
     * @summary img 객체 반환
     * @param img객체화 시키고자하는 imgSrcObj index
     * @return 변환된 img 객체
     * */
    create(index){
        let img = null;
        const url = imgSrcObj[index];

        img = new Image();
        $.get(url, svg => {

            let svgStr = String(svg);

            const strIdx = svg.indexOf('<svg');
            const endIdx =  svg.indexOf('>',strIdx);

            const svgTagStr = svg.substring(strIdx, endIdx + 1);

            let size = "0 0 50 50";
            let wh = "50px";

            const newTagStr =
                '<svg version="1.1" '+
                'id="레이어_1" '+
                'xmlns="http://www.w3.org/2000/svg" '+
                'xmlns:xlink="http://www.w3.org/1999/xlink" '+
                'viewBox="' + size + '" '+
                'style="enable-background:new ' + size + '; transform: translate(0.5px, 0.5px);" '+
                'width="'+ wh +'" '+
                'height="'+ wh +'" '+
                '>';
            svg = svgStr.replace(svgTagStr,newTagStr);

            img.src = 'data:image/svg+xml,' + escape(String(svg));
        }, 'text');

        return img;
    }

}
