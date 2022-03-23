const event = {
	init : () => {
		event.getListEvent($('#eventTable'));
		
		$("#initSearchForm").on('click', (e) => {
			$("#eventKeyword").val('');
		});

		$("#getListEventBtn").on('click', (e) => {
			event.getListEvent($('#eventTable'));
		});
		
		$("#eventListCntSel").on('change', (e) => {
			event.getListEvent($('#eventTable'));
		});
		
		$("#eventKeyword").on('keyup', (e)=> {
			if(e.keyCode == 13) {
				event.getListEvent($('#eventTable'));
			}
		});
		
		$("#initSearchForm").on('click', (e) => {
			$("#eventKeyword").val('');
		});
		
		$('.excelDownloadBtn').on('click', (e) => {
			let paramObj = {
				url : "/event/exportExcelEvent.do"
			}
			comm.downloadExcelFile(paramObj);
		});
		
		
	},
	/**
	 * 이벤트 리스트 조회
	 */
	getListEvent : ($target) => {
		const optionObj = {
			dom: '<"tableBody"rt><"tableBottom"p>',
			destroy: true,
			pageLength: $("#eventListCntSel").val(),
            scrollY: "calc(100% - 6px)",
            select : false,
            ajax : 
            	{
					'url' : "/event/getListEvent.ado",
					'contentType' : "application/json; charset=utf-8",
					'type' : "POST",
					'data' : function ( d ) {
						d.keyword = $("#eventKeyword").val();
						d.startDt = $("#eventStartDt").val();
						d.endDt = $("#eventEndDt").val();
						return JSON.stringify( d );
					},
					'dataSrc' : function (result) {
						$('.tableTitle h4 span').text(result.recordsTotal);
						return result.data;
					}
        	},
			columns : [
				{data: "eventName", className : "alignLeft"},
				{data: "meterDtm"},
				{data: "accountNo"},
				{data: "accountNm"},
				{data: "eventNo"},
				{data: "eventStartDt"},
				{data: "eventEndDt"},
				{data: "step"}
			],
			columnDefs : [
				{
				}
			],
			excelDownload : true
		}
		const evt = {
				click : function() {
					//console.log(this);
				},
				dblclick : function() {
					//console.log(this);
				}
		}
		comm.createTable($target ,optionObj, evt);
	},
	/**
	 * 관제 이벤트 리스트 조회
	 */
	getListEventGis : (obj) => {
		const paramObj = {
			url : "/event/getListEventGIS.ado",
			type : "post",
			data : obj,
			showLoading : false
		};
		comm.ajaxPost(paramObj,result => {
			const eventList = result.data; 
			controlList.createList(eventList,'event');
		});
	},
	/**
	 * 전체 이벤트 건수 백분율 (도넛 차트)
	 */
	loadTotalEventChart : () => {
		comm.ajaxPost({
			url : "/event/getListEventForTotalPerChart.ado",
			data : {},
			showLoading : false,
			hideLoading : false,
			//async : false
		}, resultData => {
			const data = resultData.data;
			const colorArr = ["#66DD95", "#4389F8", "#7A297B", "#138535", "#DF475F", "#FF7F27"];
			
			let dataArr = [];
			let labelArr = [];
			let html = "";
			
			$("#totalEventChart").next().find("ul").append("<table style='padding:20px; color:#FFFFFF;'></table>");
			
			$.each(data, (i, v) => {
				html = "";
				dataArr.push(Number(data[i].perEvent));
				
				html += "<tr>";
				html += "<td style='padding:10px;'><span style='border-radius:35%; font-size:9px; background:"+colorArr[i]+"'>　</span><span class='name' style='padding-left:7px;'>"+data[i].eventName+"</span></td>";
				html += "<td style='padding:10px;'><span class='count' style='color:yellow; font-weight:bold;'>"+data[i].eventCnt+"</span>건</td>";
				html += "<td style='padding:10px;'><span class='per' style='color:yellow; font-weight:bold;'>"+data[i].perEvent+"</span>%</td>";
				html += "</tr>";
				
				$("#totalEventChart").next().find("table").append(html);
				
				labelArr.push(data[i].eventName);
			});
			
			const options = {
				series : dataArr,
				labels : labelArr,
				stroke : {
					show: false,
				},
				chart: {
					background: '#30353f',
					type: 'donut',
					//offsetY: 12,
					//width: 300,
					height : 190,
					toolbar : {
						show : false
					},
					zoom: {
						enabled: false
					},
					selection: {
						enabled: true
					}
				},
				dataLabels: {
					enabled: true
	            },
				fill: {
					colors : colorArr
				},
				toolbar : {
					enable : false
				},
				tooltip : {
					 enable : true,
					 y: {
						formatter: function(value, { series, seriesIndex, dataPointIndex, w }) {
							return value+"%";
						}
					},
					x: {
						show : true,
						formatter: function(value, { series, seriesIndex, dataPointIndex, w }) {
							return value;
						}
					}
				},
				legend: {
					show: false,
				}
			};
			chart.createChart(options, "totalEventChart");
		});
	},
	/**
	 * 대시보드 > 장비 이상 이벤트 
	 */
	loadTodayDeviceChart : () => {
		comm.ajaxPost({
			url : "/account/getListAccountDataForDeviceEventChart.ado",
			data : {},
			showLoading : false,
			hideLoading : false,
		}, resultData => {
			const data = resultData.data;
			const codeArr = [
				{"key" : "mNotUse", "value" : "계량기장기미사용"}
				, {"key" : "mLowBatt", "value" : "계량기저전압"}
				, {"key" : "mLeak", "value" : "계량기누수"}
				, {"key" : "mOverload", "value" : "계량기과부하"}
				, {"key" : "mReverse", "value" : "계량기역류"}
				, {"key" : "leakState", "value" : "누수예상"}
			]
			
			let heatArr = [];
			let obj = {};
			let dataArr = [];
			
			for(let x=0; x<codeArr.length; x++) {
				$.each(data, (i, v) => {
					dataArr.push({
						"x" : data[i].time
						, "y" : data[i][codeArr[x].key]
					});
				});
				heatArr.push({
					"name" : codeArr[x].value // 축
					, "data" : dataArr
				});
				dataArr = [];
			}
			
			const options = {
				series: heatArr,
				chart: {
					width: "105%",
					height: 245,
					type: 'heatmap',
					zoom : {
						enabled : false
					},
					toolbar : {
						show : false
					},
					background: '#30353f',
				},
				grid: {
					padding: {
						left: 10,
						right: 10
					}
				},
				plotOptions: {
					heatmap: {
					  	reverseNegativeShade: true
					  	, radius : 4
					  	, distributed: true
					  	, colorScale: {
							ranges: [
								{
									from: 0,
									to: 0,
									color: '#676F78',
									name: 'medium',
								}
							]
						}
					}
				},
				dataLabels: {
					enabled: false
				},
				colors: ["#E26666"],
				tooltip: {
					followCursor: false,
					y: {
						formatter: function(value, { series, seriesIndex, dataPointIndex, w }) {
							let resultVal = "이상 없음";
							if(value=="1") {
								resultVal = "비정상"
							}
							return resultVal;
						}
					},
					x: {
						show : true,
						formatter: function(value, { series, seriesIndex, dataPointIndex, w }) {
							return value;
						}
					},
					marker: {
						show: true,
					}
				}
			};
			chart.createChart(options, "todayDeviceChart");
		});
	},
	/**
	 * 날씨
	 */
	loadWeather : () => {
		function getCode(pCode, pFcstValue) {
			const codeObj = {
				"POP" : {
					"name" : "강수확률"
					, "unit" : "%"
				},
				"PTY" : {
					"name" : "강수형태"
					, "unit" : ""
					, "code" : {
						"C0" : "없음"
						, "C1" : "비"
						, "C2" : "비/눈"
						, "C3" : "눈"
						, "C4" : "소나기"
						, "C5" : "빗방울"
						, "C6" : "빗방울/눈날림"
						, "C7" : "눈날림"
					}
				},
				"R06" : {
					"name" : "6시간 강수량"
					, "unit" : "mm"
				},
				"REH" : {
					"name" : "습도"
					, "unit" : "%"
				},
				"S06" : {
					"name" : "6시간 신적설"
					, "unit" : "cm"
				},
				"SKY" : {
					"name" : "하늘상태"
					, "unit" : ""
					, "code" : {
						"C1" : "맑음"
						, "C3" : "구름맑음"
						, "C4" : "흐림"
					}
				},
				"T1H" : {
					"name" : "1시간 기온"
					, "unit" : "℃"
				},
				"T3H" : {
					"name" : "3시간 기온"
					, "unit" : "℃"
				},
				"TMN" : {
					"name" : "아침 최저기온"
					, "unit" : "℃"
				},
				"TMX" : {
					"name" : "낮 최고기온"
					, "unit" : "℃"
				},
				"UUU" : {
					"name" : "풍속(동서성분)"
					, "unit" : "m/s"
				},
				"VVV" : {
					"name" : "풍속(남북성분)"
					, "unit" : "m/s"
				},
				"WAV" : {
					"name" : "파고"
					, "unit" : "M"
				},
				"VEC" : {
					"name" : "풍향"
					, "unit" : "deg"
				},
				"WSD" : {
					"name" : "풍속"
					, "unit" : "m/s"
				},
				"RN1" : {
					"name" : "1시간 강수량"
					, "unit" : "mm"
				},
				"DAT" : {
					"name" : ""
					, "unit" : ""
				}
			}
			const resultCodeObj = codeObj[pCode];
			
			if(resultCodeObj.unit=="") {
				resultCodeObj.fcstValue = resultCodeObj.code["C"+pFcstValue];
			} else {
				resultCodeObj.fcstValue = pFcstValue + resultCodeObj.unit;
			}
			return resultCodeObj;
		}
		
		function getBaseTime(pDate, pFcstValue) {
			const hour = pDate.getHours();
			const min = pDate.getMinutes();
			let baseTime = "";

			if(hour>=23 && min>=10) {
				baseTime = "2300";
			} else if(hour>=20 && min>=10) {
				baseTime = "2000";
			} else if(hour>=17 && min>=10) {
				baseTime = "1700";
			} else if(hour>=14 && min>=10) {
				baseTime = "1400";
			} else if(hour>=11 && min>=10) {
				baseTime = "1100";
			} else if(hour>=5 && min>=10) {
				baseTime = "0500";
			} else {
				baseTime = "0200";
			}
			return baseTime;
		}
		
		const apiProp = {
			"airApiKey" : "FvEFsYpik4RN1u1XFWddP0wKfyG1Qenr7REfFikbf29ZBh4oswiAjfc9Jhy5B1Bh%2FVPt2FhpMMW%2BnHusEvO%2FOQ%3D%3D"	
			, "nx" : "59"
			, "ny" : "122"
			, "areaNo" : "4141000000"
		}
		
		let date = new Date();
		const baseDate = date.getFullYear() + "" + (dateFunc.getZeroString(date.getMonth()+1)) + "" + dateFunc.getZeroString(date.getDate());

		const queryStr = $.param({
			"numOfRows" : "100"
			, "pageNo" : "1"
			, "base_date" : baseDate
			, "base_time" : getBaseTime(date)
			, "dataType" : "JSON"
			, "nx" : apiProp.nx
			, "ny" : apiProp.ny
		});
		
		comm.ajaxPost({
			url : "/common/getApiData.ado",
			data : {
				apiUrl : "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?serviceKey=" + apiProp.airApiKey + "&" + queryStr
			},
			showLoading : false,
			hideLoading : false,
			//async : false
		}, resultData => {
			if(resultData.data.response.header.resultCode=="00") {
				const itemArr = resultData.data.response.body.items.item;
				let todayFlag = true;
				let todayDate = itemArr[0].fcstDate + itemArr[0].fcstTime;
				let bottomCnt = 1;
				
				$.each(itemArr, (i, v) => {
					//오늘 기상 정보 
					const codeObj = getCode(v.category, v.fcstValue);
					if(todayFlag && v.fcstDate + v.fcstTime == todayDate) {
						if(v.category == "SKY") {
							$('.weatherArea .weatherTop .weather01 img').prop('src', "/images/common/SKY_C"+v.fcstValue+".svg");
						} else if(v.category == "T3H") {
							$('.weatherArea .weatherTop [data-category="'+v.category+'"]').text(codeObj.fcstValue);
						} else {
							$('.weatherArea .weatherTop [data-category="'+v.category+'"] span:nth-child(2)').text(codeObj.fcstValue);
						}
					//이후 기상정보
					} else {
						todayFlag = false;
						if(v.category == "SKY"){
							$('.weatherArea .weatherBottom dl:nth-child('+bottomCnt+') .iconWeather img').prop('src', "/images/common/SKY_C"+v.fcstValue+".svg");
						} else if(v.category == "T3H") {
							const dateChar = v.fcstDate.split('');
							const timeChar = v.fcstTime.split('');
							const dateText = dateChar[5] + '월 ' + dateChar[7] + '일 ' + timeChar[0] + timeChar[1] + '시'
							$('.weatherArea .weatherBottom dl:nth-child('+bottomCnt+') dt').text(dateText);
							$('.weatherArea .weatherBottom dl:nth-child('+bottomCnt+') [data-category="'+v.category+'"]').text(codeObj.fcstValue);
							bottomCnt++;
						}
					}
				});
			}
		});
	},
	/**
	 * 검침 통계 차트
	 */
	loadAccountDataChart : ()=> {
		
		// 1. 최근 하루 시간대별 검침 증가량
		comm.ajaxPost({
			url : "/account/getListAccountDataForTimediffChart.ado",
			data : {},
			showLoading : false,
			hideLoading : false,
			//async : false
		}, resultData => {
			const data = resultData.data;
			let xArr = [];
			let yArr = [];
			
			$.each(data, (i, v) => {
				xArr.push(data[i].time);
				yArr.push(data[i].avgDiffValue);
			});
			
			let options = {
				series: [
					{
						name : "평균 증가값",
						data : yArr
					}
				],
				xaxis: {
					categories : xArr
				},
				chart: {
					background: '#30353f',
					toolbar : {
						show : false
					},
					zoom: {
						enabled: false
					},
					width : 590,
					height : 210,
					type: 'line'
				},
				stroke: {
				  curve: 'smooth',
				},
				grid : {
					padding : {
						left : 30,
						right : 30
					}
				},
				colors : ["#4389F8"],
				yaxis: [
					{
						axisTicks: {
							show: true
						},
						axisBorder: {
							show: true,
							color: "#FF1654"
						},
						labels: {
							style: {
							colors: "#FFFFFF"
							}
						},
						title: {
							text: "",
							style: {
								color: "#FF1654"
							}
						}
					}
				],
				toolbar : {
					enable : false
				},
				dataLabels: {
	                enabled: false,
	            },
	            tooltip : {
	            	y :  
						{
							formatter: function(value, { series, seriesIndex, dataPointIndex, w }) {
								return value;
						    }
						},
					x :  
						{
							formatter: function(value, { series, seriesIndex, dataPointIndex, w }) {
								return w.globals.categoryLabels[dataPointIndex]+"시";
						    }
						},
	            }
			}
			chart.createChart(options, "accountDataTimediffChart");
		});
	
		// 2. 최근 일년 검침 증가량 시간대별 검침 증가량
		comm.ajaxPost({
			url : "/account/getListAccountDataForMonthdiffChart.ado",
			data : {},
			showLoading : false,
			hideLoading : false,
			//async : false
		}, resultData => {
			const data = resultData.data;
			let xArr = [];
			let yArr = [];
			
			$.each(data, (i, v) => {
				xArr.push(data[i].month);
				yArr.push(data[i].diffValue);
			});
			
			let options = {
				series: [
					{
						name : "증가값",
						data : yArr
					}
				],
				xaxis: {
					categories : xArr
				},
				chart: {
					background: '#30353f',
					toolbar : {
						show : false
					},
					zoom: {
						enabled: false
					},
					width : 590,
					height : 210,
					type: 'line'
				},
				stroke: {
				  curve: 'smooth',
				},
				grid : {
					padding : {
						left : 30,
						right : 30
					}
				},
				colors : ["#4389F8"],
				yaxis: [
					{
						axisTicks: {
							show: true
						},
						axisBorder: {
							show: true,
							color: "#FF1654"
						},
						labels: {
							style: {
							colors: "#FFFFFF"
							}
						},
						title: {
							text: "",
							style: {
								color: "#FF1654"
							}
						}
					}
				],
				toolbar : {
					enable : false
				},
				dataLabels: {
	                enabled: false,
	            },
	            tooltip : {
	            	x :  
					{
						formatter: function(value, { series, seriesIndex, dataPointIndex, w }) {
							let date = w.globals.categoryLabels[dataPointIndex];
							return date.split("/")[0] + "년 " + date.split("/")[1] + "일";
					    }
					},
			        y :  
						{
							formatter: function(value, { series, seriesIndex, dataPointIndex, w }) {
								return value;
						    }
						}
					,
					marker: {
						show: true
					}
	            }
			}
			chart.createChart(options, "accountDataMonthdiffChart");
		});
		
		// 3. 최근 일년 검침 증가량 일별 검침 증가량
		comm.ajaxPost({
			url : "/account/getListAccountDataForDaydiffChart.ado",
			data : {},
			showLoading : false,
			hideLoading : false,
			//async : false
		}, resultData => {
			const data = resultData.data;
			let xArr = [];
			let yArr = [];
			
			$.each(data, (i, v) => {
				xArr.push(data[i].day);
				yArr.push(data[i].diffValue);
			});
			
			let options = {
				series: [
					{
						name : "일 증가값",
						data : yArr
					}
				],
				xaxis: {
					categories : xArr
				},
				chart: {
					background: '#30353f',
					toolbar : {
						show : false
					},
					zoom: {
						enabled: false
					},
					width : 590,
					height : 210,
					type: 'line'
				},
				stroke: {
				  curve: 'smooth',
				},
				grid : {
					padding : {
						left : 30,
						right : 30
					}
				},
				colors : ["#4389F8"],
				yaxis: [
					{
						axisTicks: {
							show: true
						},
						axisBorder: {
							show: true,
							color: "#FF1654"
						},
						labels: {
							style: {
							colors: "#FFFFFF"
							}
						},
						title: {
							text: "",
							style: {
								color: "#FF1654"
							}
						}
					}
				],
				toolbar : {
					enable : false
				},
				dataLabels: {
	                enabled: false,
	            },
	            tooltip : {
	            	enabled : true,
					x :  
						{
							formatter: function(value, { series, seriesIndex, dataPointIndex, w }) {
								return w.globals.categoryLabels[dataPointIndex]+"일";
						    }
						}
					,
					y : {
						formatter: function(value, { series, seriesIndex, dataPointIndex, w }) {
					      return value;
					    }
					}
	            }
			}
			chart.createChart(options, "accountDataDaydiffChart");
		});
	},
	loadAccountDataChartForMain : ()=> {
		
		// 1. 최근 하루 시간대별 검침 증가량
		comm.ajaxPost({
			url : "/account/getListAccountDataForTimediffChart.ado",
			data : {},
			showLoading : false,
			hideLoading : false,
			//async : false
		}, resultData => {
			const data = resultData.data;
			let xArr = [];
			let yArr = [];
			
			$.each(data, (i, v) => {
				xArr.push(data[i].time);
				yArr.push(data[i].avgDiffValue);
			});
			
			let options = {
				series: [
					{
						name : "평균 증가값",
						data : yArr
					}
				],
				xaxis: {
					categories : xArr
				},
				chart: {
					background: '#30353f',
					toolbar : {
						show : false
					},
					zoom: {
						enabled: false
					},
					width : 1085,
					height : 250,
					type: 'line'
				},
				stroke: {
				  curve: 'smooth',
				},
				grid : {
					padding : {
						left : 30,
						right : 30
					}
				},
				colors : ["#4389F8"],
				yaxis: [
					{
						axisTicks: {
							show: true
						},
						axisBorder: {
							show: true,
							color: "#FF1654"
						},
						labels: {
							style: {
							colors: "#FFFFFF"
							}
						},
						title: {
							text: "",
							style: {
								color: "#FF1654"
							}
						}
					}
				],
				toolbar : {
					enable : false
				},
				dataLabels: {
	                enabled: false,
	            },
	            tooltip : {
	            	y :  
						{
							formatter: function(value, { series, seriesIndex, dataPointIndex, w }) {
								return value;
						    }
						},
					x :  
						{
							formatter: function(value, { series, seriesIndex, dataPointIndex, w }) {
								return w.globals.categoryLabels[dataPointIndex]+"시";
						    }
						},
	            }
			}
			chart.createChart(options, "accountDataChart");
		});
	},
	/**
	 *  유형별 이벤트 건수 (메인)
	 */
	loadEventTotalCnt : () => {
		comm.ajaxPost({
			url : "/event/getListEventTotalCnt.ado",
			data : $("#searchForm").serializeJSON(),
			showLoading : false,
			hideLoading : false,
			//async : false
		}, resultData => {
			const data = resultData.data;
			const eventTypeArr = ["leakState", "mLowBatt", "mLeak", "mOverload", "mReverse", "mNotUse"];
			let dataCnt = "";

			for(let i=0; i<eventTypeArr.length; i++) {
				if(typeof(data[eventTypeArr[i]+"Cnt"])!="undefined") {
					dataCnt = data[eventTypeArr[i]+"Cnt"];
				} else {
					dataCnt = "-";
				}
				$("#"+eventTypeArr[i]+"Cnt").text(dataCnt);
			}
		});
	},
	/**
	 *  날짜 조회 기간 이벤트 건수 도넛 차트 (메인)
	 */
	loadTotalEventChartForStats : () => {
		comm.ajaxPost({
			url : "/event/getListEventForTotalPerChart.ado",
			data : $("#searchForm").serializeJSON(),
			showLoading : false,
			hideLoading : false,
			//async : false
		}, resultData => {
			const data = resultData.data;
			const colorArr = ["#66DD95", "#4389F8", "#7A297B", "#138535", "#DF475F", "#FF7F27"];
			
			let dataArr = [];
			let labelArr = [];
			let html = "";

			$("#totalEventChart").next().empty();
			$("#totalEventChart2").next().empty();

			$("#totalEventChart").next().append("<table style='padding:20px; color:#FFFFFF;'></table>");
			$("#totalEventChart2").next().append("<table style='padding:20px; color:#FFFFFF;'></table>");
			
			$.each(data, (i, v) => {
				html = "";
				dataArr.push(Number(data[i].perEvent));
				
				html += "<tr>";
				html += "<td style='padding:10px;'><span style='border-radius:35%; font-size:9px; background:"+colorArr[i]+"'>　</span><span class='name' style='padding-left:7px;'>"+data[i].eventName+"</span></td>";
				html += "<td style='padding:10px;'><span class='count' style='color:yellow; font-weight:bold;'>"+data[i].eventCnt+"</span>건</td>";
				html += "<td style='padding:10px;'><span class='per' style='color:yellow; font-weight:bold;'>"+data[i].perEvent+"</span>%</td>";
				html += "</tr>";
				
				$("#totalEventChart").next().find("table").append(html);
				$("#totalEventChart2").next().find("table").append(html);
				
				labelArr.push(data[i].eventName);
			});
			
			const options = {
				series : dataArr,
				labels : labelArr,
				stroke : {
					show: false,
				},
				chart: {
					background: '#30353f',
					type: 'donut',
					//offsetY: 12,
					//width: 300,
					height : 255,
					toolbar : {
						show : false
					},
					zoom: {
						enabled: false
					},
					selection: {
						enabled: true
					}
				},
				dataLabels: {
					enabled: true
	            },
				fill: {
					colors : colorArr
				},
				toolbar : {
					enable : false
				},
				tooltip : {
					 enable : false
				},
				legend: {
					show: false,
				}
			};
			chart.createChart(options, "totalEventChart");
		});
	},
	loadProcessEventChartForStats : () => {
		comm.ajaxPost({
			url : "/event/getListEventForProcessPerChart.ado",
			data : $("#searchForm").serializeJSON(),
			showLoading : false,
			hideLoading : false,
			//async : false
		}, resultData => {
			const data = resultData.data;
			const colorArr = ["#DF475F", "#4389F8"];
			
			let dataArr = [];
			let labelArr = [];
			let html = "";

			$("#processEventChart").next().empty();
			$("#processEventChart").next().append("<table style='padding:20px; color:#FFFFFF;'></table>");
			
			$.each(data, (i, v) => {
				html = "";
				dataArr.push(Number(data[i].perStep));
				
				html += "<tr>";
				html += "<td style='padding:10px;'><span style='border-radius:35%; font-size:9px; background:"+colorArr[i]+"'>　</span><span class='name' style='padding-left:7px;'>"+data[i].stepNm+"</span></td>";
				//html += "<td style='padding:10px;'><span class='count' style='color:yellow; font-weight:bold;'>"+data[i].stepNm+"</span>건</td>";
				html += "<td style='padding:10px;'><span class='per' style='color:yellow; font-weight:bold;'>"+data[i].perStep+"</span>%</td>";
				html += "</tr>";
				
				$("#processEventChart").next().find("table").append(html);
				
				labelArr.push(data[i].stepNm);
			});
			
			const options = {
				series : dataArr,
				labels : labelArr,
				stroke : {
					show: false,
				},
				chart: {
					background: '#30353f',
					type: 'donut',
					//offsetY: 12,
					//width: 300,
					height : 255,
					toolbar : {
						show : false
					},
					zoom: {
						enabled: false
					},
					selection: {
						enabled: true
					}
				},
				dataLabels: {
					enabled: true
	            },
				fill: {
					colors : colorArr
				},
				toolbar : {
					enable : false
				},
				tooltip : {
					 enable : false
				},
				legend: {
					show: false,
				}
			};
			chart.createChart(options, "processEventChart");
		});
	},
	loadEventStatsChartForStats : ()=> {
		
		comm.ajaxPost({
			url : "/event/getListEventForStatsChart.ado",
			data : $("#searchForm").serializeJSON(),
			showLoading : false,
			hideLoading : false,
			//async : false
		}, resultData => {
			const data = resultData.data;
			let xArr = [];
			let yArr = [];
			
			$.each(data, (i, v) => {
				xArr.push(data[i].day);
				yArr.push(data[i].dayCnt);
			});
			
			let options = {
				series: [
					{
						name : "이벤트 발생",
						data : yArr
					}
				],
				xaxis: {
					categories : xArr
				},
				chart: {
					background: '#30353f',
					toolbar : {
						show : false
					},
					zoom: {
						enabled: false
					},
					width : 1085,
					height : 250,
					type: 'line'
				},
				stroke: {
				  curve: 'smooth',
				},
				grid : {
					padding : {
						left : 30,
						right : 30
					}
				},
				colors : ["#4389F8"],
				yaxis: [
					{
						axisTicks: {
							show: true
						},
						axisBorder: {
							show: true,
							color: "#FF1654"
						},
						labels: {
							style: {
							colors: "#FFFFFF"
							}
						},
						title: {
							text: "",
							style: {
								color: "#FF1654"
							}
						}
					}
				],
				toolbar : {
					enable : false
				},
				dataLabels: {
	                enabled: false,
	            },
	            tooltip : {
	            	y :  
						{
							formatter: function(value, { series, seriesIndex, dataPointIndex, w }) {
								return value + "건";
						    }
						},
					x :  
						{
							formatter: function(value, { series, seriesIndex, dataPointIndex, w }) {
								return w.globals.categoryLabels[dataPointIndex]+"일";
						    }
						},
	            }
			}
			chart.createChart(options, "eventStatsChart");
		});
	}
}