

/**
 * 맵 주소검색 기능
 * @function map.searchPlaces
 */

function searchPlaces(keyword){
	var keyword = $('#searchKeyword').val();
	$('.search-list').css('background','none').html('');
	
	if($('#mapSrchForm01').is(":checked") == true){
	    if (!keyword.replace(/^\s+|\s+$/g, '')) {
	        alert('검색어를 입력해주세요!');
	        return false;
	    }
	    searchAddress(keyword);
	} else {
		if (!keyword.replace(/^\s+|\s+$/g, '')) {
	        alert('검색어를 입력해주세요!');
	        $('#searchCctvPagination').html('').css('display', 'none');
	        return false;
	    }
		searchCctv(keyword);
	}
	$('.search-list').css('background','#2b2d30');
}

/**
 * 맵 cctv 검색기능
 * @param {string} keyword - 검색어 스트링 값 
 * @function map.searchCctv
 */
function searchCctv(keyword){
	const jsonObj = {};
    jsonObj.recordCountPerPage = '-1';
    jsonObj.featureKind = 'cctv';
    jsonObj.pageKind = 'manage';
    jsonObj.searchTxt = keyword;
    jsonObj.userId = opener.document.getElementById('loginId').value;
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type		: "POST",
		url			: "/select/facility.selectFcltSList/action",
		dataType	: "json",
		data		: JSON.stringify(jsonObj),
		async		: false
	}).done(function(result){
		const rows = result.rows;
    	if(rows=='sessionOut'){
			alert('로그인 시간이 만료되었습니다.');
			closeWindow();
		}
		if(rows.length == 0){
			var searchList = $('.search-list');
			searchList.html('<a href="javascript:onCheckBox();">닫기</a>');
			$('#searchCctvPagination').html('').css('display', 'none');
		    $('#searchCctvPagination').removeAttr('class')
			var content = $('<dd>').addClass('keyword-list-content');
			let nDd = $('<dd>')
			let nDl = $('<dl>');
			let nDt = $('<dt>');
			let nSpan = $('<span>');
			nSpan.text('검색결과가 없습니다.');
			nDt.append(nSpan);
			nDl.append(nDt);
			nDd.append(nDl);
			content.append(nDl);
			searchList.append(content);
			return;
		}
		
		var itemsOnPage = 15;
		$("#searchCctvPagination").simplePagination({
			items: rows.length,
			itemsOnPage : itemsOnPage,
			displayedPages : 5,
			edges : 0,
			prevText : "<",
			nextText : ">",
			cssStyle: 'custom-theme',
			onInit : function (){
				cctvListPageClick(1 , undefined, rows , itemsOnPage);
			},
			//클릭할때
			onPageClick :  function (pagenumber , event ) {
				cctvListPageClick(pagenumber , event, rows , itemsOnPage)
			}
		}).css('display', 'flex');
	})
}

/**
 * cctv 검색 페이지 생성 및 이벤트 추가 기능
 * @param {string} pagenumber - 현재 페이지 넘버
 * @param {object} event - 이벤트
 * @param {object} data - cctv 검색 결과 데이터
 * @param {string} itemsOnPage - 페이지 수
 * @function map.cctvListPageClick 
 */
function cctvListPageClick(pagenumber, event, data, itemsOnPage){
	
	// 한페이지에서 보여줄 개수
	var max = pagenumber * itemsOnPage;
	max = max <= data.length ? max : data.length;
	var cnt = (pagenumber -1 ) * itemsOnPage;
	
	var searchList = $('.search-list');
	searchList.html('<a href="javascript:onCheckBox();">닫기</a>');
	var cctvT = $('<dt>').addClass('search-list-title');
	var cctvC = $('<dd>').addClass('keyword-list-content');
	var span = $('<span>');
	span.text('CCTV');
	cctvT.append(span);
	searchList.append(cctvT);
	for (var i=cnt; i < max; i++){
		var dl = $('<dl>');
		var innerDt = $('<dt>');
		var innerLoc = $('<div>').css('display','none');
		var innerSpan = $('<span>');
		var innerDd = $('<dd>').addClass('between');
		
		var mgmtSpan = $('<span>');
		var typeSpan = $('<span>');
		
		
		var fcltNm;
		var mgmtNo;
		var type;
		var loc;
		
		fcltNm = data[i].fcltNm;
		type = data[i].fcltPuposeNm + (data[i].cctvAgYn == '0' ? '(고정)' : '(PTZ)');
		mgmtNo = '[관리번호] '+(data[i].mgmtNo);
		
		mgmtSpan.append(mgmtNo);
		typeSpan.append(type);
		
		
		loc = (data[i].lon + "/" + data[i].lat);
		innerLoc.append(loc)
		
		innerSpan.append(fcltNm);
		
		dl.append(innerLoc);
		innerDt.append(innerSpan);
		
		innerDd.append(mgmtSpan);
		innerDd.append(typeSpan);
		dl.append(innerDt)
		dl.append(innerDd);
		cctvC.append(dl);
	}
	searchList.append(cctvC);
	
	$('.keyword-list-content dl').click(function(){
		const location = $(this).find('div').text().split('/');
		const position = ol.proj.transform(location, mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection);
		setSearchCctvSelectedOverlay(position);
		mapManager.setCenter(position);
	})
}

/**
 * 시설물 검색 오버레이 생성 
 * @function map.setSearchCctvSelectedOverlay
 * @param {object} position -팝업에 loc, lat
 */
function setSearchCctvSelectedOverlay(position) {
	const content = createSearchCctvSelectedOverlay();
	const option = {
		id : 'searchCctvSelected',
		position : position,
		element : content,
		offset : [ 0, 0 ],
		positioning : 'center-center',
		stopEvent : false,
		insertFirst : true
	}
	
	mapManager.setOverlay(option);
}

/**
 * 시설물 검색 아이콘에 gif 삽입 기능
 * @function evtMain.createSearchCctvSelectedOverlay
 * @returns {element} 선택된 마커 이미지 overlay
 */
function createSearchCctvSelectedOverlay() {
	let wrap = document.createElement('div');
	
	wrap.classList.add('search-cctv-selected-overlay');
	
	let img = document.createElement('img');
	img.src = '/images/icons/selected.gif';
	
	wrap.appendChild(img);
	
	return wrap;
}


/**
 * 맵 주소 검색 기능 kakao api 를 사용
 * @param {string} keyword -검색어 스트링 값
 * @function map.searchAddress
 */
function searchAddress(keyword){
	$.ajax({
		url : 'https://dapi.kakao.com/v2/local/search/address.json',
		type	: 'GET',
		dataType : "json",
		data : {'query' : keyword },
		beforeSend : function(xhr) {
			//전송 전 setRequestHeader 설정
			xhr.setRequestHeader('Authorization' ,'KakaoAK cb428b7f662762683264ceb3a4e04f8d');
		}
	}).done(function (result){
		var data = result.documents;
		let searchList = $('.search-list');
		searchList.html('<a href="javascript:onCheckBox();">닫기</a>');
		let title = $('<dt>').addClass('search-list-title');
		let content = $('<dd>').addClass('search-list-content');
		let span = $('<span>');
		span.text('주소')
		title.append(span);
		searchList.append(title);
		
		if(data.length != 0){
			for(var i=0;i<data.length;i++){
				let searchDl = $('<dl>');
				let ddAddr = $('<dt>');
				let ddLoc = $('<div>').addClass('dd-loc').css('display','none');
				var address = data[i].address_name;
				var addressSpan = $('<span>');
				addressSpan.append(address);
				var lon =  data[i].x;
				var lat = data[i].y;
				ddLoc.append(lon + "/" + lat);
				ddAddr.append(addressSpan);
				searchDl.append(ddAddr);
				searchDl.append(ddLoc);
				content.append(searchDl);
			}
			searchList.append(content);
			searchKeyword(keyword);
		}
		else {
			let nDd = $('<dd>')
			let nDl = $('<dl>');
			let nDt = $('<dt>');
			let nSpan = $('<span>');
			nSpan.text('검색결과가 없습니다.');
			nDt.append(nSpan);
			nDl.append(nDt);
			nDd.append(nDl);
			content.append(nDl);
			searchList.append(content);
			return;
		}
		$('.search-list dl').click(function(){
			var location = $(this).find('div').text().split("/");
			mapManager.setCenter(ol.proj.transform([location[0], location[1]], mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection));
		})
	}).always(function(){
	})
}

/**
 * 맵 키워드 장소 검색
 * @param {string} keyword - 검색어 스트링 값
 * @function map.searchKeyword
 */
function searchKeyword(keyword){
	$.ajax({
		url : 'https://dapi.kakao.com/v2/local/search/keyword.json',
		type	: 'GET',
		dataType : "json",
		data : {
			'query' : keyword,
		},
		beforeSend : function(xhr) {
			//전송 전 setRequestHeader 설정
			xhr.setRequestHeader('Authorization' ,'KakaoAK cb428b7f662762683264ceb3a4e04f8d');
		},
	}).done(function (result){
		var data = result.documents
		let searchList = $('.search-list');
		let title = $('<dt>').addClass('search-list-title');
		let content = $('<dd>').addClass('keyword-list-content');
		let span = $('<span>');
		span.text('장소')
		title.append(span);
		searchList.append(title);
		let dl;
		if(data.length != 0){
			for(var i=0;i<data.length;i++){
				dl = $('<dl>');
				let loc = $('<div>');
				loc.css('display','none');
				let kTitle = $('<dt>');
				let span = $('<span>');
				let kList = $('<dd>');
				
				address = data[i].address_name;
				place = data[i].place_name
				loc.append(data[i].x + "/" + data[i].y);
				
				span.append(address);
				kTitle.append(span);
				kList.append(place);
				dl.append(loc);
				dl.append(kTitle);
				dl.append(kList);
				content.append(dl);
			}
			searchList.append(content);
		}
		else {
		}
		$('.keyword-list-content dl').click(function(){
			var location = $(this).find('div').text().split("/");
			mapManager.setCenter(ol.proj.transform([location[0], location[1]], mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection));
		})
		
	})
}