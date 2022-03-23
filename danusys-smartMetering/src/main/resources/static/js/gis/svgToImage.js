/**
 * svg를 레이어로 사용하기 위한 기초작업 코드
 */

function makeImg(url,img){
	$.get(url, svg => { 
		img.src = 'data:image/svg+xml,' + escape(String(svg));
	}, 'text');
}

//이벤트 마커 test
const img_event = new Image(); //장소검색결과
makeImg('/images/common/iconEvent.svg',img_event);


/*
//제거예정
const img_searchSelect = new Image(); 
makeImg('/css/images/search_place_select.svg',img_searchSelect);

//검색 결과 마커
const img_searchPlace = new Image(); //장소검색결과
makeImg('/css/images/search_place.svg',img_searchPlace);
const img_searchAddress = new Image(); //주소검색결과
makeImg('/css/images/search_address.svg',img_searchAddress);
const img_bookmark_searchPlace = new Image(); //장소검색결과_즐겨찾기
makeImg('/css/images/search_bookmark_place.svg',img_bookmark_searchPlace);
const img_bookmark_searchAddress = new Image(); //주소검색결과_즐겨찾기
makeImg('/css/images/search_bookmark_address.svg',img_bookmark_searchAddress);

//cctv 이미지 일괄생성
const cctvImgObj = {};
const cctvImgColor = {};
//유형을 알수 없는 CCTV
const img_undefined = new Image();
makeImg('/css/images/cctv_undefined.svg',img_undefined);

cctvImgColor["undefined"] = "128,128,128";
cctvImgObj["undefined"] = img_undefined;
*/
/*
comm.ajaxPost(
		{
			"type":"POST"
			, "url" : "/cctv/getListCctvImageName.ado"
			, "data" : {}
			, "async" : false
		}
	, resultData => {
		const rData = resultData.data;
		$.each(rData, (i,v) => {
			cctvImgColor[v.type] = v.rgbCode;
			
			let path = '/css/images/cctv_'+v.imgName;
			//기본
			const baseImg = new Image(); 
			makeImg(path+'.svg',baseImg);
			cctvImgObj['useCd'+v.type+'_N'] = baseImg;
			//선택
			const selImg = new Image();
			makeImg(path+'_select.svg',selImg);
			cctvImgObj['useCd'+v.type+'_N_Sel'] = selImg;
			//즐겨찾기
			const bmImg = new Image();
			makeImg(path+'_bookmark.svg',bmImg);
			cctvImgObj['useCd'+v.type+'_Y'] = bmImg;
			//즐겨찾기+선택
			const bmSelImg = new Image(); 
			makeImg(path+'_bookmark_select.svg',bmSelImg);
			cctvImgObj['useCd'+v.type+'_Y_Sel'] = bmSelImg;
		});
});


*/