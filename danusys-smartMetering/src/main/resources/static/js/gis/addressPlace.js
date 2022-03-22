/**
 * gis 관제 주소/장소 검색
 */

addressPlace = {
		
	/**
	 * 관제 주소 테이블  
	 */
	getListAddress : result =>  {
		const addressList = result.documents;
		if($('#addressList ul li').length < result.meta.pageable_count){
			addressList.forEach(function(e,i){
				
				let loadAd = "";
				
				if(e.road_address instanceof Object){ //로
					loadAd = e.road_address.road_name ? e.road_address.road_name : "정보없음";
				} else {
					loadAd = e.road_address ? e.road_address : "정보없음";
				}
				
				let content = "<li>" +
				"<p>주소명 : " + e.address_name + "</p>" + 
				"<p>도로명 : " + loadAd + "</p>" + 
				"</li>" ;
	
				$("#addressList ul").append(content);
				$('#addressList ul li').last().data(e);
			});
	
			$("#addressListWrap dl dt span").text($('#addressList ul li').length);
				
			addressPlace.moveForList('addressList');
		}
	},
	/**
	 * 관제 장소 테이블  
	 */
	getListPlace : result => {
		const placeList = result.documents;
		let index = $('#placeList ul li').length +1;
		
		if($('#placeList ul li').length < result.meta.pageable_count){
			placeList.forEach(function(e,i){
				let loadAd = e.road_address_name ? e.road_address_name : "정보없음";
				let phoneNb = e.phone ? e.phone : "정보없음";
				
				let content = "<li>" +
				"<dl>"+
				"<dd>"+ (parseInt(i)+ index) +"</dd>" + 
				"<dt>"+ e.place_name +"</dt>" + 
				"</dl>" +
				"<p>주소명 : " + e.address_name + "</p>" + 
				"<p>도로명 : " + loadAd + "</p>" + 
				"<p class='tel'>전화 : "+ phoneNb +"</p>" + 
				"</li>" ;
				
				$("#placeList ul").append(content);
				$("#placeList ul li").last().data(e);
			});
			
			$("#placeListWrap dl dt span").text($('#placeList ul li').length);
			
			addressPlace.moveForList('placeList');			
		}
	},
	/**
	 * 결과 테이블 클릭 이벤트(맵이동)   
	 */
	moveForList : listName => {
		$('#'+listName+' ul li').unbind("click");
		
		$('#'+listName+' ul li').on('click',function(e){
			const data =$(this).data();
			const coordinate = new ol.proj.transform([data.x, data.y],"EPSG:4326",baseProjection); 
			const moveFlag = ol.extent.containsCoordinate(baseExtent, coordinate);
			if(moveFlag){
				map.getView().setCenter(coordinate);
				map.getView().setZoom(12);
				pulse(coordinate);
			}
			
		});
	}
}