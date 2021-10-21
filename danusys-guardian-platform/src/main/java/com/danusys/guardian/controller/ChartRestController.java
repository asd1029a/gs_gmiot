package com.danusys.guardian.controller;

import com.danusys.guardian.common.util.CommonUtil;
import com.danusys.guardian.common.util.JsonUtil;
import com.danusys.guardian.service.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ChartRestController {
	
	private BaseService baseService;

	@Autowired
	public ChartRestController(BaseService baseService) {
		this.baseService = baseService;
	}

	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/getDashBoardData.do")
	public Object select(HttpServletRequest request) throws Exception {
		request.setCharacterEncoding("UTF-8");
		List<Map<String, Object>> reList = new ArrayList<Map<String, Object>>();
		String kind = request.getParameter("kind");
		// 전체를 보낼 배열
		JSONArray obj = new JSONArray();
		if ("p".equals(kind)) {
			reList = baseService.baseSelectList("chart.eventDataPie", null);
//			reList = chartService.getEventPie();
			for (Map<String, Object> map : reList) {
				JSONObject crimetime = new JSONObject();
				JSONArray value = new JSONArray();
				JSONObject type = new JSONObject();

				crimetime.put("crimetime", map.get("evtNm"));
				crimetime.put("value", value);
				type.put("type", "112긴급지원");
				type.put("crimecount", map.get("cnt"));
				value.add(type);
				obj.add(crimetime);
			}
			return obj;
		} else if ("t".equals(kind)) {
			reList = baseService.baseSelectList("chart.eventDataTime", null);

		} else if ("d".equals(kind)) {
			reList = baseService.baseSelectList("chart.eventDataDay", null);
//			reList = chartService.getEventData();
		}
		for (Map<String, Object> map : reList) {
			// 그안에 담긴 객체들
			JSONObject crimetime = new JSONObject();
			JSONArray value = new JSONArray();
			JSONObject type1 = new JSONObject();
			JSONObject type2 = new JSONObject();
			JSONObject type3 = new JSONObject();
			JSONObject type4 = new JSONObject();
			crimetime.put("crimetime", map.get("time"));
			crimetime.put("value", value);
			type1.put("type", "112긴급");
			type1.put("crimecount", map.get("event1"));
			type2.put("type", "119긴급");
			type2.put("crimecount", map.get("event2"));
			type3.put("type", "재난");
			type3.put("crimecount", map.get("event3"));
			type4.put("type", "기타");
			type4.put("crimecount", map.get("etc"));
			value.add(type1);
			value.add(type2);
			value.add(type3);
			value.add(type4);

			obj.add(crimetime);
		}
		return obj;
	}
	
//	@SuppressWarnings("unchecked")
//	@CrossOrigin
	@PostMapping("/selectBarChart/{sqlid}/action")
	public Object selectEvent(@PathVariable("sqlid") String sqlid, @RequestBody Map<String ,Object> param) throws Exception {
//		response.setCharacterEncoding("UTF-8");
//		response.setHeader("Access-Control-Allow-Origin", "*"); // 크로스도메인 허용
		log.trace("# param : {} ", param);
		List<Map<String, Object>> reList = new ArrayList<Map<String, Object>>();
//		Map<String, Object> param = null;
//		if (request.getParameter("param").trim().equals("") == true) {
//			param = new HashMap<String, Object>();
//		} else {`
//			param = JsonUtil.JsonToMap(request.getParameter("param"));
//		}
		reList = baseService.baseSelectList(sqlid,param);

		JSONArray obj = new JSONArray();
		for(Map<String, Object>map : reList) {
			JSONObject object = new JSONObject();
			object.put("xtarget", map.get("xtarget"));
			object.put("cnt", map.get("cnt"));
			obj.add(object);
		}
		return obj;
	}
	
//	@CrossOrigin
	@PostMapping("/selectHeatMap/{sqlid}/action")
	public Object selectHeatMap(@PathVariable("sqlid") String sqlid, @RequestBody Map<String, Object> param) throws Exception {
//		request.setCharacterEncoding("UTF-8");
		List<Map<String, Object>> reList = new ArrayList<Map<String, Object>>();
		
		List<Map<String, Object>> yList = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
//		Map<String, Object> param = null;
//		if (request.getParameter("param").trim().equals("") == true) {
//			param = new HashMap<String, Object>();
//		} else {
//			param = JsonUtil.JsonToMap(request.getParameter("param"));
//		}
				
		String[] time = {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
		String[] week = {"일","월","화","수","목","금","토"};
		String[] month = {"1","2","3","4","5","6","7","8","9","10","11","12"};
		if(param.get("heatmapKind").equals("e") && !param.get("sigCode").equals("all")) {
			reList = baseService.baseSelectList(sqlid, param);
			yList = baseService.baseSelectList("eventChart.selectDongList", param);
		} else if(param.get("heatmapKind").equals("e") && param.get("sigCode").equals("all")) {
			reList = baseService.baseSelectList(sqlid + "Si", param);
			yList = baseService.baseSelectList("eventChart.selectSiList", param);
		} else {
			reList = baseService.baseSelectList(sqlid, param);
			yList = baseService.baseSelectList("facilityChart.selectPurposeList", param);
		}
		String[] yNm = new String[yList.size()];
		for(int i=0;i<yList.size();i++) {
			yNm[i] = (String)yList.get(i).get("yList");
		}
		data.put("data", reList);
		if(param.get("kind").equals("t")) {
			data.put("x",time);
		}else if(param.get("kind").equals("d")){
			data.put("x",week);
		}else if(param.get("kind").equals("m")){
			data.put("x",month);
		}
		data.put("y",yNm);
		return data;
	}
	
//	@CrossOrigin
	@PostMapping("/getTotalData")
	public Object selectTotalData(@RequestBody Map<String, Object> param) throws Exception {
//		 request.setCharacterEncoding("UTF-8");
		 List<Map<String, Object>> reList = new ArrayList<Map<String, Object>>();
		 List<Map<String, Object>> reList2 = new ArrayList<Map<String, Object>>();
		 List<Map<String, Object>> reList3 = new ArrayList<Map<String, Object>>();
		 List<Map<String, Object>> reList4 = new ArrayList<Map<String, Object>>();
		 Map<String, Object> data = new HashMap<String, Object>();
		 
//			Map<String, Object> param = null;
//			if (request.getParameter("param").trim().equals("") == true) {
//				param = new HashMap<String, Object>();
//			} else {
//				param = JsonUtil.JsonToMap(request.getParameter("param"));
//			}
			
			log.trace("# param : {}", param.toString());

			if(param.get("changeTypeKind").equals("event")) {
				reList = baseService.baseSelectList("eventChart.selectTotalData", param);
				reList2 = baseService.baseSelectList("eventChart.selectTotalData2", param);
				reList3 = baseService.baseSelectList("eventChart.selectMaxMinEvent", param);
				reList4 = baseService.baseSelectList("eventChart.selectMaxMinPlace", param);
				data.put("totalData", reList);
				data.put("totalData2", reList2);
				data.put("maxMinEvent", reList3);
				data.put("maxMinPlace", reList4);
			} else if(param.get("changeTypeKind").equals("facility")) {
				reList = baseService.baseSelectList("facilityChart.selectTotalData", param);
				reList2 = baseService.baseSelectList("facilityChart.selectTotalData2", param);
				reList3 = baseService.baseSelectList("facilityChart.selectMaxMinEvent", param);
				reList4 = baseService.baseSelectList("facilityChart.selectMaxMinPlace", param);
				data.put("totalData", reList);
				data.put("totalData2", reList2);
				data.put("maxMinEvent", reList3);
				data.put("maxMinPlace", reList4);
			} else if(param.get("changeTypeKind").equals("api")) {
				reList = baseService.baseSelectList("api.selectTotalData", param);
				reList2 = baseService.baseSelectList("api.selectTotalData2", param);
				reList3 = baseService.baseSelectList("api.selectMaxMinAp", param);
				reList4 = baseService.baseSelectList("api.selectMaxMinGroup", param);
				data.put("totalData", reList);
				data.put("totalData2", reList2);
				data.put("maxMinEvent", reList3);
				data.put("maxMinPlace", reList4);
			}
		return data;
	}

	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/getDongEventData.do")
	public Object getDongEventData(HttpServletRequest request) throws Exception {
		request.setCharacterEncoding("UTF-8");

		Map<String, Object> param = new HashMap<String, Object>();
		String areaCd = request.getParameter("areaCd");
		List<String> dateList = CommonUtil.get30DaysBeforeList();
		
		JSONObject result = new JSONObject();
		
		param.put("areaCd", areaCd);
		param.put("dateList", dateList);
		
		List<Map<String, Object>> list = baseService.baseSelectList("chart.getDongEventData", param);
		
		List<String> itemList = baseService.baseSelectList("chart.getDongEventTotal", param);
		
		result.put("list", list);
		result.put("dateList", dateList);
		result.put("itemList", itemList);
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/getTimelineData.do")
	public Object getTimelineData(@RequestBody HashMap<String, Object> param, HttpServletRequest request) throws Exception {
		request.setCharacterEncoding("UTF-8");
		
		JSONObject result = new JSONObject();
		
		List<Map<String, Object>> list = baseService.baseSelectList("chart.getTimelineData", param);
		
		result.put("list", list);
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/getTimelineDataOne.do", produces = "application/json; charset=utf8")
	public Object getTimelineDataOne(@RequestBody HashMap<String, Object> param, HttpServletRequest request) throws Exception {
		request.setCharacterEncoding("UTF-8");
		
		JSONObject result = new JSONObject();
		
		List<Map<String, Object>> list = baseService.baseSelectList("chart.getTimelineDataOne", param);
		
		result.put("list", list);
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/getEventThirty.do", produces = "application/json; charset=utf8")
	public Object getEventTotal(@RequestBody HashMap<String, Object> param, HttpServletRequest request) throws Exception {
		request.setCharacterEncoding("UTF-8");
		
		JSONObject result = new JSONObject();
		
		List<Map<String, Object>> list = baseService.baseSelectList("chart.getEventThirty", param);
		
		result.put("list", list);
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/getEventDayFromPie.do")
	public Object getEventMonthFromPie(@RequestBody HashMap<String, Object> param, HttpServletRequest request) throws Exception {
		request.setCharacterEncoding("UTF-8");
		
		JSONObject result = new JSONObject();
		
		List<Map<String, Object>> list = baseService.baseSelectList("chart.getEventDayFromPie", param);
		
		result.put("list", list);
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/getEventThirtyFromPie.do")
	public Object getEventTotalFromPie(@RequestBody HashMap<String, Object> param, HttpServletRequest request) throws Exception {
		request.setCharacterEncoding("UTF-8");
		
		JSONObject result = new JSONObject();
		
		List<Map<String, Object>> list = baseService.baseSelectList("chart.getEventThirtyFromPie", param);
		
		result.put("list", list);
		
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/getLineChart.do")
	public Object getLineChart(@RequestBody HashMap<String, Object> param, HttpServletRequest request) throws Exception {
		request.setCharacterEncoding("UTF-8");
		JSONObject result = new JSONObject();
		List<Map<String, Object>> list = baseService.baseSelectList("chart.getLineChart", param);
		result.put("list", list);
		return result;
	}
	
	@CrossOrigin
	@RequestMapping(value = "/getAllData.do", method = RequestMethod.POST)
	public Object getAllData(HttpServletRequest request) throws Exception {
		 request.setCharacterEncoding("UTF-8");
		 List<Map<String, Object>> reList = new ArrayList<Map<String, Object>>();
		 Map<String, Object> data = new HashMap<String, Object>();
		 
			Map<String, Object> param = null;
			if (request.getParameter("param").trim().equals("") == true) {
				param = new HashMap<String, Object>();
			} else {
				param = JsonUtil.JsonToMap(request.getParameter("param"));
			}
			
			System.out.println(param);
			if(param.get("changeTypeKind").equals("event")) {
				reList = baseService.baseSelectList("chart.eventAllData", param);
				data.put("rows", reList);
			} else if(param.get("changeTypeKind").equals("facility")) {
				reList = baseService.baseSelectList("chart.facilityAllData", param);
				data.put("rows", reList);
			}
		return data;
	}
	
}
