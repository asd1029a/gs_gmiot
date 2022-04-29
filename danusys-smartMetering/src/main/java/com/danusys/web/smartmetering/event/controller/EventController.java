package com.danusys.web.smartmetering.event.controller;

import com.danusys.web.smartmetering.common.annotation.JsonRequestMapping;
import com.danusys.web.smartmetering.common.util.DateUtil;
import com.danusys.web.smartmetering.common.util.ExcelUtil;
import com.danusys.web.smartmetering.event.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class EventController {
	
	@Autowired
	EventService eventService;

	@Autowired
	ExcelUtil excelUtil;
	
	/**
	 * 조회/관리 이벤트 페이지
	 */
	@RequestMapping(value="/search/event/eventList.do")
	public String loginForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "event/eventList";
	}
	
	/**
	 * 이벤트 : 이벤트 목록 조회
	 */
	@JsonRequestMapping(value = "/event/getListEvent.ado")
	public String getListEvent(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return eventService.selectListEvent(paramMap);
	}
	
	/**
	 * 이벤트 : 이벤트 종료
	 */
	@JsonRequestMapping(value = "/event/modEventStep.ado", method=RequestMethod.PATCH)
	public String modEventStep(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return eventService.updateEventStep(paramMap);
	}

	/**
	 * 이벤트 : 관제 이벤트 리스트 조회
	 */
	@JsonRequestMapping(value = "/event/getListEventGIS.ado")
	public String getListEventGIS(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return eventService.selectListEventGIS(paramMap);
	}

	/**
	 * 이벤트 : 수용가별 최근 이벤트 조회
	 */
	@JsonRequestMapping(value = "/event/getListLastestEventByAccount.ado")
	public String getListLastestEventByAccount(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return eventService.selectListLastestEventByAccount(paramMap);
	}

	/**
	 * 이벤트 : 한 수용가의 이벤트 사용량 통계 조회
	 */
	@JsonRequestMapping(value = "/event/getListEventDataStats.ado")
	public String getListEventDataStats(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return eventService.selectListEventDataStats(paramMap);
	}
	
	/**
	 * 이벤트 : 이벤트 GEOJSON 조회
	 */
	@JsonRequestMapping(value = "/event/getListEventGeojson.ado")
	public String getListEventGeojson(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return eventService.selectListEventGeojson(paramMap);
	}
	
	/**
	 * 이벤트 : 한 수용가의 이벤트 로그 조회
	 */
	@JsonRequestMapping(value = "/event/getListEventLog.ado")
	public String getListEventLog(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return eventService.selectListEventLog(paramMap);
	}
	
	/**
	 * 이벤트 : 전체 이벤트 발생 (도넛 차트)
	 */
	@JsonRequestMapping(value = "/event/getListEventForTotalPerChart.ado")
	public String getListEventForTotalPerChart(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return eventService.selectListEventForTotalPerChart(paramMap);
	}
	
	/**
	 * 이벤트 : 처리 현황 (도넛 차트)
	 */
	@JsonRequestMapping(value = "/event/getListEventForProcessPerChart.ado")
	public String getListEventForProcessPerChart(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return eventService.selectListEventForProcessPerChart(paramMap);
	}
	
	/**
	 * 이벤트 : 통계 (라인 차트)
	 */
	@JsonRequestMapping(value = "/event/getListEventForStatsChart.ado")
	public String getListEventForStatsChart(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return eventService.selectListEventForStatsChart(paramMap);
	}
	
	/**
	 * 이벤트 : 유형 별 발생 건수 (메인)
	 */
	@JsonRequestMapping(value = "/event/getListEventTotalCnt.ado")
	public String getListEventTotalCnt(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return eventService.selectDetailEventTotalCnt(paramMap);
	}
	
	/**
	 * 이벤트 : 이벤트 엑셀
	 */
	@RequestMapping(value = "/event/exportExcelEvent.do")
	public ModelAndView exportExcelEvent(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> paramMap) throws Exception {
		String fileName = "이벤트목록_"+DateUtil.getCurrentDate("yyyyMmddHHmmss");
		String columnArr = "eventNo|eventName|meterDtm|eventStartDt|eventEndDt|step|accountNo|accountNm";
		String columnNmArr = "이벤트 고유 번호|이벤트 이름|검침 일시|이벤트 발생 일시|이벤트 종료 일시|처리상태|수용가 번호|수용가 이름";
		String qId = "event.SELECT_LIST_EVENT_EXCEL";
		
		paramMap.put("columnArr", columnArr);
		paramMap.put("columnNmArr", columnNmArr);
		paramMap.put("qId", qId);
		paramMap.put("fileName", fileName+".xlsx");

		return excelUtil.exportExcel(paramMap);
	}
}