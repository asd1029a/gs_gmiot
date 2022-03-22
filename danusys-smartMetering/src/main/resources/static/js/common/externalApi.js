const externalApi = {
	/**	
	 * kakaoApi 호출
	 */
	getKakaoApiProc : (obj, callback, pageObj) => {
		const defaultsObj = {
				headers : {"Authorization":kakaoKey},
				async: false,
				data : {
					format : "JSON"
				},
				isExternalApi : true
			}
		const paramObj = $.extend(defaultsObj, typeof(obj)=="undefined" ? {} : obj);
		comm.ajaxPost(paramObj, (result) => {
			callback(result, pageObj);
		});
	},
	/**
	 * 위경도로 주소찾기
	 */
	getCoord2AddressData : (coord) => {
		let obj = {};
		const putCoord = ol.proj.transform(coord,baseProjection,"EPSG:4326");
		const paramObj = {
			url : "https://dapi.kakao.com/v2/local/geo/coord2address.json",
			type : "GET",
			headers : {"Authorization":kakaoKey},
			async:false, 
			data : {
				x: putCoord[0],
				y: putCoord[1],
				input_coord: 'WGS84'
			},
			showLoading : false,
			isExternalApi : true
		}
		comm.ajaxPost(paramObj, r => {
			obj = r;
		});
		return obj;
	},
	/**
	 * 주소로 위경도 찾기
	 */
	getAddress2CoordData : (page) => {
		const addressWord = $("#addressInput").val();
		addressWord = addressWord.replace(/ /gi, ""); 
		const paramObj = {
			url : "https://dapi.kakao.com/v2/local/search/address.json",
			type : "GET",
			headers : {"Authorization":kakaoKey},
			data : {
				query : addressWord,
				size : 15,
				page : page,
				format : "JSON"
			},
			isExternalApi : true
		}
		comm.ajaxPost(paramObj, r => {
			
		})
	}
}
