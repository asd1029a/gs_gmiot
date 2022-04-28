const init = {
	// uri : /dashboard/index.do
	dashboardIndex : () => {
		
		// 공지사항
		board.getListBoardForMain((resultData)=> {
			const $ul = $("#boardListUl");
			const data = resultData.data;
			
			$ul.empty();
			
			$.each(data, (i, v) => {
				$ul.append("<li><span class='txt'>" +data[i].title + "</span><span class='name'>"+data[i].userName+"</span><span class='date'>"+data[i].insertDt+"</span></li>");
			});
			$ul.on("click", ()=> {
				location.href = "/setting/board/boardList.do";
			});
		});
		
		//미처리 이벤트 
		const paramObj = {
			url : "/event/getListEventGIS.ado",
			type : "post",
			data : {step:0},
			showLoading : false
		};
		comm.ajaxPost(paramObj,result => {
			let eventList = result.data;  
			if(eventList.length>0){
				$('.settingMainBody div.right ul.eventList').empty();
				let content = "";
				eventList.forEach(function(e,i){
					let dayGap;
					if(e.dayGap=="0"){
						dayGap = "오늘";
					} else {
						dayGap = e.dayGap + "일 전";					
					}

					content += 
						"<li>" +
						"<dl>" +  
						"<dt>" + 
						"<span class='num'>"+ (i+1) +"</span>" +
						"<span class='tit title0"+e.eventGroupCode+"'>"+ e.eventGroupName +"</span>" +
						"<span class='state state01-03'>"+ e.eventName +"</span>" + 
						"</dt>" +
						"<dd>" + dayGap + "</dd>" + 
						"</dl>" + 
						"<p>" + e.fullAddr + "</p>" + 
						"<p>" + e.eventStartDt + "</p>" + 
						"</li>";
			 	});	
				$('.settingMainBody .right .eventList').append(content);

				$('.settingMainBody .right .eventList li').click(function(e){ location.href='/gis/main.do'; });
				
			}
		});
		
	},
	// uri : /stats/accountStats.do
	statsAccountStats : () => {
		event.loadAccountDataChartForMain();
	}
}





// ------------------------------------------------------------------------------------------------- //
$(document).ready(() => {
	comm.initModal();
	
	$('[id*=StartDt], [id*=EndDt]').datepicker({
		language: 'ko-KR',
		autoHide: true,
	    format: 'yyyy-mm-dd'
	});
	$('[id*=EndDt]').datepicker('setStartDate', $('[id*=StartDt]').datepicker('getDate'));
	
	$('[id*=StartTime], [id*=EndTime]').mask('00:00:00', {placeholder: "__:__:__"});

	$('[id*=StartDt]').on('change', () => {
		$('[id*=EndDt]').datepicker('setStartDate', $('[id*=StartDt]').datepicker('getDate'));
	});

	$('[id*=StartDt]').datepicker('setDate', dayjs().add(-1, 'month').$d);
	$('[id*=EndDt]').datepicker('setDate', dayjs().$d);
	
	const pathArr = window.location.pathname.split('/');
	const category1 = pathArr[1];
	const category2 = pathArr[2];
	const category3 = pathArr[3];
	
	// 환경설정
	if(category1 === "setting") {
		if (category2 === "admin") {
			admin.init();
			adminGroup.init();
		} else if(category2 === "account") {
			accountGroup.init();
		} else if (category2 === "board") {
			board.init();
		}
	
	// 조회
	} else if (category1 === "search") {
		if (category2 === "account") {
			account.init();
			accountData.init();
		} else if (category2 === "event") {
			event.init();
		}
	} else if(category1 === "dashboard") {
		event.loadTotalEventChart();
		event.loadTodayDeviceChart();
		event.loadWeather();
		event.loadAccountDataChart();
		dashBoardMap.init();
	}else if (category1 === "gis") {
		gis.init();
		gisMenu.init();
	// 통계
	} else if(category1 === "stats") {
		//  검침 통계
		if(category2.indexOf("accountStats")>-1) {
			stats.account.init();
		
		// 이벤트 통계
		} else if(category2.indexOf("eventStats")>-1) {
			stats.event.init();
		}
	}
});