/**
 * 관제 페이지 메뉴 init 
 */

const gisMenu = {
	init : () => {
		//gis왼쪽 메뉴
		$(".controlAside .inquireForm .tabButton li").click(function(e){
			$(this).siblings().each(function(index,item){
				$(item).removeClass('active');
			});
			
			$(this).addClass('active');
			
			let type = $(this).attr('data-type');

			$('.inquireOptions').css('display','none');
			$('.controlAside [class$=Wrap]').css('display','none');
			
			$('[data-type='+type+']').each(function(index,item){
				if((item.tagName=="DIV") && (type!="address") ){
					$(item).css('display','flex');
				} else {
					$(item).css('display','block');				
				}		
			});
			
		});
		
		//검색창에서 엔터시 페이지 이동 방지
		$('dl.inquireOptions input[type="text"]').keydown(function(event) { 
			if (event.keyCode === 13) {
				event.preventDefault();
			};
		});

		//맵 타일 타입 버튼
		$('.controlMapArea .mapTypeButton li').click(function(e){
			let type = $(this).attr('data-type');
			$(this).siblings().each(function(index,item){	
				$(item).removeClass('active');
			});
			$(this).addClass('active');		
		});	

		//맵 레이어 on/off 버튼
		$('.controlMapArea .mapLayerButton li').click(function(e){
			let type = $(this).attr('data-type');
			let flag = $(this).attr('class').indexOf('active') > -1 ? true : false;
			
			if(type=="accountLayer"){
				let zoom = map.getView().getZoom();
				if(zoom < 12){
					type = "accountCntLayer";
				}
			}
			
			//해당 레이어 팝업 끄기 
			let typeNm = type.replace('Layer','');
			if(typeNm=="accountCnt"){ typeNm = "account"; }
			$('#'+ typeNm +'Popup').hide();
			
			if(flag){
				
				layerControl.off(type);
				$(this).removeClass('active');
			} else {
				layerControl.on(type);
				$(this).addClass('active');			
			}
		});

		//왼쪽 메뉴 show-hide
		$(".asideNav").click(() => {		
			$(".asideNav").toggleClass("asideNavHide");
			$(".controlAside").toggleClass("controlAsideHide");
		});

		//주소장소 검색 조회 버튼클릭
		$("#addressPlaceSearchArea dd").click(function(e){
			e.preventDefault();

			let keyword = $(this).parent().find('input').val();
			
			const paramObj= {
				url : "",
				type : "GET",
				data : {
					query : keyword,
					x: baseMapCenter[0],
					y: baseMapCenter[1],
					radius:"10000",
					sort:"accuracy"
				},
				showLoading : false
			};

			$('.addressPlaceList ul').empty();
			$('.addressPlaceList').scrollTop(0);
			$(".addressPlaceListWrap dl dt span").text(0);
			
			if(keyword != null && keyword != "") {
				
				paramObj.url = "https://dapi.kakao.com/v2/local/search/address.json";
				externalApi.getKakaoApiProc(paramObj, addressPlace.getListAddress);
				
				paramObj.url = "https://dapi.kakao.com/v2/local/search/keyword.json";
				externalApi.getKakaoApiProc(paramObj, addressPlace.getListPlace);
				
			} else {
				comm.showAlert("검색어를 입력해주세요.", {icon:"warning"});
			}
			
		});

		//주소장소 검색 키보드 엔터
		$('#addressPlaceSearchArea input').on('keydown', key => {
			if(key.keyCode == 13){
				let keyword = key.currentTarget.value;
				
				$('.addressPlaceList ul').empty();
				$('.addressPlaceList').scrollTop(0);
				$(".addressPlaceListWrap dl dt span").text(0);

				if(keyword != null && keyword != ""){
					const paramObj= {
						url : "",
						type : "GET",
						data : {
							query : keyword,
							x: baseMapCenter[0],
							y: baseMapCenter[1],
							sort:"accuracy",
							radius:"10000"
						},
						showLoading : false
					};
					
					paramObj.url = "https://dapi.kakao.com/v2/local/search/address.json";
					externalApi.getKakaoApiProc(paramObj, addressPlace.getListAddress);
					
					paramObj.url = "https://dapi.kakao.com/v2/local/search/keyword.json";
					externalApi.getKakaoApiProc(paramObj, addressPlace.getListPlace);
					
				} else {
					setTimeout(() => {
						comm.showAlert("검색어를 입력해주세요.", {icon:"warning"});
					}, 1);
				}
				
			}
		});

		//이벤트 종류 select list 드롭
		$('.selectCheckTit').click(function(e){

			const ul = $(this).siblings('.selectCheckUL');
			let flag = ul.css('display');
			
			if(flag=="none"){
				ul.css('display','block');
			} else {
				ul.css('display','none');		
			}
		});

		//수용가 및 이벤트 조회
		$('.inquireOptions .searchBtn').click(function(e){
			 let tab = $(this).parent().attr('data-type');
			 let obj = $('#'+tab+'Form').serializeJSON();
			 
			 obj.orderType = $('.controlListWrap[data-type='+tab+'] .orderType').val();
			 
			 if(tab=="account"){
//				 if(obj.searchText != null && obj.searchText != ""){
				 account.getListAccountGis(obj);
//				 } else {
//					 setTimeout(() => {
//						 comm.showAlert("검색어를 입력해주세요.", {icon:"warning"});
//					 }, 1);
//				 }
			 } else if(tab=="event"){
				 
				 let eventCodeStr = "";
				 
				 for (const [key, value] of Object.entries(obj)) {
					 
					 if(key.startsWith("eventCode")){
						 eventCodeStr += '\'' +value + '\',';
						 delete obj[key];
					 }
					 if((key=="step")&&(value=="")){
						 delete obj[key];
					 }
				 }
				 
				 obj.eventCode = eventCodeStr.slice(0,-1);
				 event.getListEventGis(obj);
			 }
		});

		//검색 리스트 정렬
		$('.orderType').on('change',function(e){
			$('.inquireOptions .searchBtn').trigger('click');
		});

		$('#eventStartDt').datepicker({
			language: 'ko-KR',
			autoHide: true,
		    format: 'yyyy-mm-dd'
		});
		$('#eventEndDt').datepicker({
			language: 'ko-KR',
			autoHide: true,
		    format: 'yyyy-mm-dd',
		    startDate: $('#eventStartDt').datepicker('getDate')
		});

		$('#eventStartTime').mask('00:00:00', {placeholder: "__:__:__"});
		$('#eventEndTime').mask('00:00:00', {placeholder: "__:__:__"});

		$('#eventStartDt').on('change', () => {
			$('#eventEndDt').datepicker('setStartDate', $('#eventStartDt').datepicker('getDate'));
		});


		//스크롤 마무리
		$('.addressPlaceList').scroll(function(){
			if((this.scrollTop+this.clientHeight) == this.scrollHeight){
				let lastNum = parseInt($(this).find('ul li').last().find('dl dd').text());
				 
				if(lastNum >= 15){
					let page = (lastNum / 15) + 1 ;
					
					let keyword = $('#addressPlaceSearchArea input').val();
					if(keyword != null && keyword != ""){
						const paramObj= {
								url : "",
								type : "GET",
								data : {
									query : keyword,
									x: baseMapCenter[0],
									y: baseMapCenter[1],
									sort:"accuracy",
									size : 15,
									page : page,
									radius:"10000"
								},
								showLoading : false
						};
						
						if(this.id=="addressList"){
							paramObj.url = "https://dapi.kakao.com/v2/local/search/address.json";
							externalApi.getKakaoApiProc(paramObj, addressPlace.getListAddress);
						} else {					
							paramObj.url = "https://dapi.kakao.com/v2/local/search/keyword.json";
							externalApi.getKakaoApiProc(paramObj, addressPlace.getListPlace);
						}
					}	
				}
					
			}
			
		});
	
		//관제 초기 리스트 검색
		$('.inquireOptions .searchBtn').trigger('click');

	} // init()
	
}

const controlList = {
	/**
	 * 관제 검색 결과 리스트 추가
	 */
	createList : (list, tab) => {
		$('.controlListWrap[data-type='+tab+'] .controlList').scrollTop(0);
		
		$('.controlListWrap[data-type='+tab+'] .controlList ul').empty();
	 	$('.controlListWrap[data-type='+tab+'] dl dt span').text(list.length);
	 	
	 	let content = "";
	 	
	 	list.forEach(function(e,i){
	 		
			if(tab=="account"){
				content = 
					"<li>" +
					"<dl>" +  
					"<dt>" + 
					"<span class='num'>"+ (i+1) +"</span>" +
					"<span class='tit title01'>"+ e.accountNo +"</span>" +
					"</dt>" + 
					"</dl>" + 
					"<p> 주소 :  " + e.fullAddr + "</p>" + 
					"<p> 등록일 :  " + e.insertDt + "</p>" + 
					"</li>" ;				
			} else if(tab=="event"){
		        
				let dayGap;
				if(e.dayGap=="0"){
					dayGap = "오늘";
				} else {
					dayGap = e.dayGap + "일 전";					
				}
				
				let addText = "";
				if(e.step=="0"){
					addText = "<span class='noStep'>미처리</span>";
				}
				
				content = 
					"<li>" +
					"<dl>" +  
					"<dt>" + 
					"<span class='num'>"+ (i+1) +"</span>" +
					"<span class='tit title0"+e.eventGroupCode+"'>"+ e.eventGroupName +"</span>" +
					"<span class='state state01-03'>"+ e.eventName +"</span>" +
					addText + 
					"</dt>" +
					"<dd>" + dayGap + "</dd>" + 
					"</dl>" + 
					"<p> 이벤트 번호 :  " + e.eventNo + "</p>" + 
					"<p> 수용가 번호 :  " + e.accountNo + "</p>" + 
					"</li>"; 
			}

			$('.controlListWrap[data-type='+tab+'] .controlList ul').append(content);
			$('.controlListWrap[data-type='+tab+'] .controlList ul li').last().data(e);
		});

		 $('.controlListWrap[data-type='+tab+'] .controlList ul li').unbind("click");
			
		 $('.controlListWrap[data-type='+tab+'] .controlList ul li').on('click', function(e){
			 const data =$(this).data();
			 const coordinate = new ol.proj.transform([data.longitude, data.latitude],"EPSG:4326",baseProjection); 
			 const moveFlag = ol.extent.containsCoordinate(baseExtent, coordinate);
			 if(moveFlag){
				 map.getView().setCenter(coordinate);
				 map.getView().setZoom(12);
				 pulse(coordinate);
			 }
		 });
		 
		 $('.controlListWrap[data-type='+tab+'] .controlList').data(list);
	}
}

//json 리스트 정렬 ?
let by = function(name) {
    return function(o, p) {
        var a, b;
        if (typeof o === 'object' && typeof p === 'object' && o && p) {
            a = o[name];
            b = p[name];
            
            //문자 -> 숫자
            const regex = /^[0-9]/gi;  
            const regex2 = /^[0-9]/gi; 
            const flagA = regex.test(a); 
            const flagB = regex2.test(b);
            if(flagA && flagB) {
            	a = parseInt(a);
            	b = parseInt(b);
            }

            if (a === b) {
                return 0;
            }
            if (typeof a === typeof b) {
                return a < b ? -1 : 1;
            }
            return typeof a < typeof b ? -1 : 1;
        } else {
            throw {
                name : 'Error',
                message : 'Expected an object when sorting by ' + name
            };
        }
    };
};