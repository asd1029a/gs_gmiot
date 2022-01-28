//png예외......

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
        const rootPath = '/images/'; //icon svg 경로
        //i(type) : 1~10
        //imgSrcObj[''] = rootPath + '' + '.svg';
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


//svgToImage.init();
//레이어 아이콘 생성
//for (const [k, v] of Object.entries(imgSrcObj)) {
//    imgObj[k] = svgToImage.create(k);
//}

