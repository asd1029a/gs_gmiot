/**
 * 순환감시 관련 함수 모음
 * @namespace circlr
 * */

/**
 * 순환감시 아이디
 * @memberof circlr
 * @property {string} circlr.circlrId
 * */
var circlrId;

/**
 * 순환김시 리스트 데이터를 불러오는 함수
 * @function circlr.selectCirclrGrpList 
 * */
function selectCirclrGrpList() {
	const jsonObj = {};
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url 		: "/select/facility.selectCirclrGrpList/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async       : false
	}).done(function(result) {
		const rows = result.rows;
		var itemsOnPage = 10;
		$("#circlrGrpListPagination").simplePagination({
			items: rows.length,
			
			itemsOnPage : itemsOnPage,
			displayedPages : 5,
			edges : 0,
			prevText : "<",
			nextText : ">",
			cssStyle: 'custom-theme',
			onInit : function (){
				grpPageClick(1, undefined, rows, itemsOnPage);
			},
			//클릭할때
			onPageClick :  function (pagenumber , event) {
				grpPageClick(pagenumber, event, rows, itemsOnPage);
			}
		});
		
		setCirclrTimeout(rows, 0, itemsOnPage, 1);
	});
}

function grpPageClick(pagenumber, event, data, itemsOnPage) {
	const length = data.length;
	var max = pagenumber * itemsOnPage;
	max = max <= length ? max : length;
	var cnt = (pagenumber - 1) * itemsOnPage;
	
	$('#circlrGrpList').html('');
	
	for(var i = cnt; i < max; i++) {
		const index = i;
		const temp = data[i];
		var inner = document.createElement('dl');
		var i1 = document.createElement('dt');
		var i1_1 = document.createElement('span');
		var i1_2 = document.createElement('span');
		var i2 = document.createElement('dd');
		var i3 = document.createElement('dd');
		var i4 = document.createElement('dd');
		
		i1_1.classList.add('count');
		i1_2.classList.add('event');
		i1_2.id = 'grpNo';
		
		i1.appendChild(i1_1);
		i1.appendChild(i1_2);
		
		i1_1.innerHTML = temp.rnum;
		i1_2.innerHTML = temp.no;
		i2.innerHTML = temp.information;
		i3.innerHTML = temp.manager;
		i4.innerHTML = temp.monitorArea;
		
		inner.appendChild(i1);
		inner.appendChild(i2);
		inner.appendChild(i3);
		inner.appendChild(i4);
		
		$(inner).bind('click', function() {
			setCirclrTimeout(data, index % itemsOnPage, itemsOnPage, pagenumber)
		});
		
		$(inner).data(temp);
		
		$('#circlrGrpList').append(inner);
	}
}

function setCirclrTimeout(datas, index, itemsOnPage, pagenumber) {
	const currentIndex = (itemsOnPage * (pagenumber - 1)) + index;
	
	// 재생중인 비디오 삭제
	$('.circle-video-area .cctv-view-layer .view').remove();
	
	$('.srch-list dl.active').removeClass('active');
	
	const isLastPage = Math.ceil(datas.length / itemsOnPage) === pagenumber 
		&& datas.length === currentIndex;
	
	// 마지믹 페이지의 마지막 row 일 경우
	if(isLastPage) {
		$("#circlrGrpListPagination").simplePagination('selectPage', 1);
		return setCirclrTimeout(datas, 0, itemsOnPage, 1);
	}
	
	// 현재 페이지의 마지막 row 일 경우
	if(index >= itemsOnPage) {
		$("#circlrGrpListPagination").simplePagination('nextPage');
		return setCirclrTimeout(datas, 0, itemsOnPage, ++pagenumber);
	}
	
	common.clearTimeout(circlrId);
	
	const currentItem = $('#circlrGrpList').children('dl')[index];
	
	const no = datas[currentIndex].no;
	let circlrTime = $('#circlrTime').val();
	
	circlrTime = convertTime(circlrTime);
	
	selectCirclrDtlList(no);
	
	$(currentItem).addClass('active');
	
	$('#circlrGrpList').animate({scrollTop : index * 100}, 300);
	
	circlrId = setTimeout(function() {
		setCirclrTimeout(datas, ++index, itemsOnPage, pagenumber)
	}, circlrTime);
}

/**
 * 순환김시 상세정보(CCTV정보) 리스트 데이터를 불러오는 함수
 * @function circlr.selectCirclrGrpList 
 * */
function selectCirclrDtlList(no) {
	const jsonObj = {};
	jsonObj.no = no;
	$.ajax({
	    contentType : "application/json; charset=utf-8",
		type        : "POST",
		url 		: "/select/facility.selectCirclrDtlCctv/action",
		dataType    : "json",
		data        : JSON.stringify(jsonObj),
		async      : false
	}).done(function(result) {
		const rows = result.rows;
		$('#circlrDtlList').html('');
		
		for(var i = 0; i < rows.length; i++) {
			const temp = rows[i];
			var inner = document.createElement('dl');
			var i1 = document.createElement('dt');
			var i1_1 = document.createElement('span');
			var i1_2 = document.createElement('span');
			var i2 = document.createElement('dd');
			var i3 = document.createElement('dd');
			
			i1_1.classList.add('count');
			i1_2.classList.add('event');
			i1_2.id = 'grpNo';
			
			i1.appendChild(i1_1);
			i1.appendChild(i1_2);
			
			i1_1.innerHTML = temp.rnum;
			i1_2.innerHTML = temp.mgmtNo;
			i2.innerHTML = temp.cctvName;
			i3.innerHTML = temp.fcltPuposeNm;
			
			inner.appendChild(i1);
			inner.appendChild(i2);
			inner.appendChild(i3);
			$(inner).bind('click', function() {
				//mapManager.setCenter(ol.proj.transform([temp.lon, temp.lat], mapManager.properties.pro4j[mapManager.properties.type],mapManager.properties.projection));
			});
			
			$('#circlrDtlList').append(inner);
			
			if(!videoManager.isPlaying(temp.fcltId)) {
				return;
			}
			
			const wrap = $('<div>').addClass('view');
			
			$($('.circle-video-area .cctv-view-layer')[i]).append(wrap);
			
			const option = {};
			option.data = temp;
			option.parent = wrap;
			option.isDetail = false;
			option.isButton = false;

			if(temp.fcltId !== null) {
				if(!videoManager.createPlayer(option)) {
					wrap.remove();
				};
			}
		}
	});
}

/**
 * 문자형식 시간값을 micro seconds로 변경 해주는 함수
 * @function circlr.converTime
 * @param {string} time - 문자형식 시간 값
 * @returns {number} - micro seconds
 * */
function convertTime(time) {
	var sec = 1000;
	var min = 0;
	switch (time) {
		case '1M':
			min = 60;
			break;
		case '10M':
			min = 600;
			break;
		case '30M':
			min = 3000;
			break;
		case '1H':
			min = 6000;
			break;
		default: 
			min = 1;
		break;
	}
	return sec * min;
}