package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.app.JsonUtil;
import com.danusys.web.platform.service.base.BaseService;
import com.danusys.web.platform.util.GisUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Slf4j
@RestController
public class GisRestController {

	public static String gisProjection = "EPSG:5181";

	private BaseService baseService;
	private GisUtil gisUtil;

	@Autowired
	public GisRestController(BaseService baseService, GisUtil gisUtil) {
		this.baseService = baseService;
		this.gisUtil = gisUtil;
	}

	@RequestMapping(value = "/searchGis.do")
	public Object selectSi(HttpServletRequest request) throws Exception {
		
		request.setCharacterEncoding("UTF-8");
		
		String ac = request.getParameter("sig_cd");
		List<Map<String, Object>> reList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> eventList = new ArrayList<Map<String, Object>>();
		//int eventCnt;
		reList = baseService.baseSelectList("gis.gisSiData", null);
		JSONObject obj = new JSONObject();
		JSONArray objArrGeo = new JSONArray();
		JSONArray objArrPie = new JSONArray();
		String coords;
		String[] latlon;
		
		for (Map<String, Object> map : reList) {
			Map<String, Object> param = new HashMap<>();
			param.put("areaCd",map.get("siCd"));
			eventList = baseService.baseSelectList("gis.gisEventCnt", param);
			
			JSONArray coordinate = new JSONArray();
			JSONArray eventData = new JSONArray();
			JSONObject eventInfo = null;
			JSONObject info = new JSONObject();
			JSONObject properties = new JSONObject();
			JSONObject geometry = new JSONObject();
			JSONArray arr = new JSONArray();
			
			for(int a=0;a<eventList.size();a++) {
				eventInfo = new JSONObject();
				eventInfo.put("region_kor_nm",map.get("siNm"));
				eventInfo.put("region_cd",map.get("siCd"));
				eventInfo.put("type",eventList.get(a).get("evtNm"));
				eventInfo.put("value",eventList.get(a).get("cnt"));
				eventData.add(eventInfo);
			}
			
			coords = CommonUtil.clobToString((java.sql.Clob)map.get("coord"));
			System.out.println(coords);
			coords = coords.replace("POLYGON((","");
			coords = coords.replace("))","");
			latlon = coords.split(",");
			
			for (int j = 0; j < latlon.length; j++) {
				//double [] latlonNums = Arrays.asList(latlon[j].split("\\s")).stream().mapToDouble(Double ::parseDouble).toArray();
				//coordinate.add(latlonNums);
			}
			arr.add(coordinate);

			properties.put("region_cd", map.get("siCd"));
			properties.put("region_kor_nm", map.get("siNm"));
			properties.put("count", map.get("cnt"));
			geometry.put("type", "Polygon");
			geometry.put("coordinates", arr);

			info.put("type", "Feature");
			info.put("geometry_name", "geom");
			info.put("properties", properties);
			info.put("geometry", geometry);
			info.put("id", "CM_AREA_SI_CD_" + map.get("id"));
			
			objArrGeo.add(info);
			objArrPie.add(eventData);
		}
		
		obj.put("features", objArrGeo);
		obj.put("pieData", objArrPie);
		return obj;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/searchDongGis.do")
	public Object selectDong(HttpServletRequest request) throws Exception {
		List<Map<String, Object>> reList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> eventList = new ArrayList<Map<String, Object>>();
		//int eventCnt;
		request.setCharacterEncoding("UTF-8");
		
		Map<String, Object> gisParam = null;
		if (request.getParameter("gisParam").trim().equals("") == true) {
			gisParam = new HashMap<String, Object>();
		} else {
			gisParam = JsonUtil.JsonToMap(request.getParameter("gisParam"));
		}
		reList = baseService.baseSelectList("gis.gisDongData", null);
		JSONObject obj = new JSONObject();
		JSONArray objArrGeo = new JSONArray();
		JSONArray objArrPie = new JSONArray();
		String coords;
		String[] latlon;
		
		for (Map<String, Object> map : reList) {
			Map<String, Object> param = new HashMap<>();
			param.put("areaCd",map.get("siCd"));
			eventList = baseService.baseSelectList("gis.gisEventCnt", param);
			
			JSONArray coordinate = new JSONArray();
			JSONArray eventData = new JSONArray();
			JSONObject eventInfo = null;
			JSONObject info = new JSONObject();
			JSONObject properties = new JSONObject();
			JSONObject geometry = new JSONObject();
			JSONArray arr = new JSONArray();
			
			for(int a=0;a<eventList.size();a++) {
				eventInfo = new JSONObject();
				eventInfo.put("region_kor_nm",map.get("dongNm"));
				eventInfo.put("region_cd",map.get("dongCd"));
				eventInfo.put("type",eventList.get(a).get("evtNm"));
				eventInfo.put("value",eventList.get(a).get("cnt"));
				eventData.add(eventInfo);
			}
			
			coords = map.get("coord").toString();
			coords = coords.replace("POLYGON((","");
			latlon = coords.split(",");
			
			for (int j = 0; j < latlon.length; j++) {
//				double [] latlonNums = Arrays.asList(latlon[j].split("\\s")).stream().mapToDouble(Double ::parseDouble).toArray();
//				coordinate.add(latlonNums);
			}
			arr.add(coordinate);

			properties.put("region_cd", map.get("dongCd"));
			properties.put("region_kor_nm", map.get("dongNm"));
			properties.put("count", map.get("cnt"));
			geometry.put("type", "Polygon");
			geometry.put("coordinates", arr);

			info.put("type", "Feature");
			info.put("geometry_name", "geom");
			info.put("properties", properties);
			info.put("geometry", geometry);
			info.put("id", "CM_AREA_DONG_CD_" + map.get("id"));
			
			objArrGeo.add(info);
			objArrPie.add(eventData);
		}
		
		obj.put("features", objArrGeo);
		obj.put("pieData", objArrPie);
		return obj;
	}

//	@SuppressWarnings("unchecked")
//	@CrossOrigin
	@GetMapping("/searchDongGis2")
	public Object searchDongGis2(HttpServletRequest request) throws Exception {
		List<Map<String, Object>> reList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> facilityList = new ArrayList<Map<String, Object>>();
		String sigCode = null;
		String timeS = null;
		String timeE = null;
		request.setCharacterEncoding("UTF-8");

		Map<String, Object> gisParam = null;
		sigCode = request.getParameter("sigCode");
		timeS = request.getParameter("timeS");
		timeE = request.getParameter("timeE");

		if (request.getParameter("sigCode").trim().equals("") == true) {
			gisParam = new HashMap<String, Object>();
		} else {
			gisParam = new HashMap<String, Object>();
			gisParam.put("sigCode", sigCode);
			gisParam.put("timeS", timeS);
			gisParam.put("timeE", timeE);
		}

		if ("all".equals(sigCode)) {
			reList = baseService.baseSelectList("gis.gisSiDataByFacility", gisParam);
		} else {
			reList = baseService.baseSelectList("gis.gisDongDataByFacility", gisParam);
		}

		JSONObject obj = new JSONObject();
		JSONArray objArrGeo = new JSONArray();
		JSONArray objArrPie = new JSONArray();
		String coords;

		for (Map<String, Object> map : reList) {
			Map<String, Object> param = new HashMap<>();
			param.put("areaCd", map.get("regionCd"));
			param.put("timeS", timeS);
			param.put("timeE", timeE);
			facilityList = baseService.baseSelectList("gis.gisFacilityCnt", param);

			JSONArray coordinate = null;
			JSONArray facilityData = new JSONArray();
			JSONObject facilityInfo = null;
			JSONObject info = new JSONObject();
			JSONObject properties = new JSONObject();
			JSONObject geometry = new JSONObject();
			JSONArray arr = new JSONArray();

			for (int a = 0; a < facilityList.size(); a++) {
				facilityInfo = new JSONObject();
				facilityInfo.put("region_kor_nm", map.get("regionNm"));
				facilityInfo.put("region_cd", map.get("regionCd"));
				facilityInfo.put("type", facilityList.get(a).get("nm"));
				facilityInfo.put("value", facilityList.get(a).get("cnt"));
				facilityData.add(facilityInfo);
			}

			//mariadb
//			coords = map.get("coord").toString();
			//tibero
//			coords = CommonUtil.clobToString((java.sql.Clob) map.get("coord"));
			//postgresql
			coords = map.get("coord").toString();

			coords = coords.replace("MULTIPOLYGON(((", "");
			coords = coords.replace(")))", "");

			String[] testArray = coords.split("\\)\\),\\(\\(");
			for (int abc = 0; abc < testArray.length; abc++) {
				String[] latlon = testArray[abc].split(",");
				coordinate = new JSONArray();
				for (int j = 0; j < latlon.length; j++) {
					String temp = latlon[j].replace("(", "").replace(")", "");
					double[] latlonNums = Arrays.asList(temp.split("\\s")).stream()
							.mapToDouble(Double::parseDouble).toArray();
					coordinate.add(latlonNums);
				}
				arr.add(coordinate);
			}

			properties.put("region_cd", map.get("regionCd"));
			properties.put("region_kor_nm", map.get("regionNm"));
			properties.put("count", map.get("cnt"));
			properties.put("cityNm", map.get("cityNm"));
			properties.put("cityLon", map.get("cityLon"));
			properties.put("cityLat", map.get("cityLat"));
			properties.put("cityScale", map.get("cityScale"));
			geometry.put("type", "Polygon");
			geometry.put("coordinates", arr);

			info.put("type", "Feature");
			info.put("geometry_name", "geom");
			info.put("properties", properties);
			info.put("geometry", geometry);
			info.put("id", "CM_AREA_DONG_CD_" + map.get("id"));

			objArrGeo.add(info);
			objArrPie.add(facilityData);
		}

		obj.put("features", objArrGeo);
		obj.put("pieData", objArrPie);
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/searchGis1.do")
	public Object selectSSibal(HttpServletRequest request) throws Exception {
		
		request.setCharacterEncoding("UTF-8");
		
		List<Map<String, Object>> reList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> eventList = new ArrayList<Map<String, Object>>();
		//int eventCnt;
		reList = baseService.baseSelectList("gis.gisSiData", null);
		JSONObject obj = new JSONObject();
		JSONArray objArrGeo = new JSONArray();
		JSONArray objArrPie = new JSONArray();
		String coords;
		
		
		for (Map<String, Object> map : reList) {
			Map<String, Object> param = new HashMap<>();
			param.put("areaCd",map.get("siCd"));
			eventList = baseService.baseSelectList("gis.gisEventCnt", param);
			
			JSONArray coordinate = null;
			JSONArray eventData = new JSONArray();
			JSONObject eventInfo = null;
			JSONObject info = new JSONObject();
			JSONObject properties = new JSONObject();
			JSONObject geometry = new JSONObject();
			JSONArray arr = new JSONArray();
			
			for(int a=0;a<eventList.size();a++) {
				eventInfo = new JSONObject();
				eventInfo.put("region_kor_nm",map.get("siNm"));
				eventInfo.put("region_cd",map.get("siCd"));
				eventInfo.put("type",eventList.get(a).get("evtNm"));
				eventInfo.put("value",eventList.get(a).get("cnt"));
				eventData.add(eventInfo);
			}
			
			coords = CommonUtil.clobToString((java.sql.Clob)map.get("coord"));
			
			coords = coords.replace("MULTIPOLYGON(((","");
			coords = coords.replace(")))","");
			
			String[] testArray = coords.split("\\)\\),\\(\\(");
			for(int abc = 0; abc < testArray.length ; abc++) {
				String[] latlon = testArray[abc].split(",");
				coordinate = new JSONArray();
				for (int j = 0; j < latlon.length; j++) {
//					double [] latlonNums = Arrays.asList(latlon[j].split("\\s")).stream().mapToDouble(Double ::parseDouble).toArray();
//					coordinate.add(latlonNums);
				}
				arr.add(coordinate);
			}
			

			properties.put("region_cd", map.get("siCd"));
			properties.put("region_kor_nm", map.get("siNm"));
			properties.put("count", map.get("cnt"));
			geometry.put("type", "Polygon");
			geometry.put("coordinates", arr);

			info.put("type", "Feature");
			info.put("geometry_name", "geom");
			info.put("properties", properties);
			info.put("geometry", geometry);
			info.put("id", "CM_AREA_SI_CD_" + map.get("id"));
			
			objArrGeo.add(info);
			objArrPie.add(eventData);
		}
		
		obj.put("features", objArrGeo);
		obj.put("pieData", objArrPie);
		return obj;
	}


//	@SuppressWarnings("unchecked")
//	@CrossOrigin
	@GetMapping(value = "/searchDongGis1")
	public Object selectDongSSibal(HttpServletRequest request) throws Exception {
		List<Map<String, Object>> reList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> eventList = new ArrayList<Map<String, Object>>();
		String sigCode = null;
		String timeS = null;
		String timeE = null;
		request.setCharacterEncoding("UTF-8");

		Map<String, Object> gisParam = null;
		sigCode = request.getParameter("sigCode");
		timeS = request.getParameter("timeS");
		timeE = request.getParameter("timeE");

		if (request.getParameter("sigCode").trim().equals("") == true) {
			gisParam = new HashMap<String, Object>();
		} else {
			gisParam = new HashMap<String, Object>();
			gisParam.put("sigCode", sigCode);
			gisParam.put("timeS", timeS);
			gisParam.put("timeE", timeE);
		}

		if ("all".equals(sigCode)) {
			reList = baseService.baseSelectList("gis.gisSiData", gisParam);
		} else {
			reList = baseService.baseSelectList("gis.gisDongData", gisParam);
		}

		JSONObject obj = new JSONObject();
		JSONArray objArrGeo = new JSONArray();
		JSONArray objArrPie = new JSONArray();
		String coords;

		for (Map<String, Object> map : reList) {
			Map<String, Object> param = new HashMap<>();
			param.put("areaCd", map.get("regionCd"));
			param.put("timeS", timeS);
			param.put("timeE", timeE);
			eventList = baseService.baseSelectList("gis.gisEventCnt", param);

			JSONArray coordinate = null;
			JSONArray eventData = new JSONArray();
			JSONObject eventInfo = null;
			JSONObject info = new JSONObject();
			JSONObject properties = new JSONObject();
			JSONObject geometry = new JSONObject();
			JSONArray arr = new JSONArray();

			for (int a = 0; a < eventList.size(); a++) {
				eventInfo = new JSONObject();
				eventInfo.put("region_kor_nm", map.get("regionNm"));
				eventInfo.put("region_cd", map.get("regionCd"));
				eventInfo.put("type", eventList.get(a).get("evtNm"));
				eventInfo.put("value", eventList.get(a).get("cnt"));
				eventData.add(eventInfo);
			}

			//mariadb
//			coords = map.get("coord").toString();

			//tibero
//			coords = CommonUtil.clobToString((java.sql.Clob) map.get("coord"));

			//postgresql
			coords = map.get("coord").toString();

			coords = coords.replace("MULTIPOLYGON(((", "");
			coords = coords.replace(")))", "");

			String[] testArray = coords.split("\\)\\),\\(\\(");

			for (int abc = 0; abc < testArray.length; abc++) {
				String[] latlon = testArray[abc].split(",");
				coordinate = new JSONArray();
				for (int j = 0; j < latlon.length; j++) {
					String temp = latlon[j].replace("(", "").replace(")", "");
					double[] latlonNums = Arrays.asList(temp.split("\\s")).stream()
							.mapToDouble(Double::parseDouble).toArray();
					coordinate.add(latlonNums);
				}
				arr.add(coordinate);
			}

			properties.put("region_cd", map.get("regionCd"));
			properties.put("region_kor_nm", map.get("regionNm"));
			properties.put("count", map.get("cnt"));
			properties.put("cityNm", map.get("cityNm"));
			properties.put("cityLon", map.get("cityLon"));
			properties.put("cityLat", map.get("cityLat"));
			properties.put("cityScale", map.get("cityScale"));
			geometry.put("type", "Polygon");
			geometry.put("coordinates", arr);

			info.put("type", "Feature");
			info.put("geometry_name", "geom");
			info.put("properties", properties);
			info.put("geometry", geometry);
			info.put("id", "CM_AREA_DONG_CD_" + map.get("id"));

			objArrGeo.add(info);
			objArrPie.add(eventData);
		}

		obj.put("features", objArrGeo);
		obj.put("pieData", objArrPie);
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/getCctvGeoFeature")
	public Map<String, Object> getCctvGeoFeature(HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> param = null;
		HttpSession session = request.getSession(false);
//		if (request.getParameter("param").trim().equals("") == true) {
//			param = new HashMap<String, Object>();
//		} else {
//			param = JsonUtil.JsonToMap(request.getParameter("param"));
//		}
		String featureKind = param.get("featureKind").toString();
		
		List<EgovMap> list = baseService.baseSelectList("facility.selectFcltSList", param);
		
	    return this.gisUtil.createGeoJson(list, "lon", "lat", featureKind);
	}
	
	@CrossOrigin
	@RequestMapping(value = "/getFcltGeoFeature.do")
	public Map<String, Object> getFcltGeoFeature(HttpServletRequest request) throws Exception {
		Map<String, Object> param =null;
		if(request.getParameter("param").trim().equals("") == true) {
			param = new HashMap<String, Object>();
		} else {
			param = JsonUtil.JsonToMap(request.getParameter("param"));
		}
		String featureKind = param.get("featureKind").toString();
		List<EgovMap> list = this.baseService.baseSelectList("facility.selectFcltEList", param);
		return this.gisUtil.createGeoJson(list, "lon", "lat", featureKind);
	}
	
	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/getEventGeoFeature")
	public Map<String, Object> getEventGeoFeature(HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> param = null;
		HttpSession session = request.getSession(false);
//		if (request.getParameter("param").trim().equals("") == true) {
//			param = new HashMap<String, Object>();
//		} else {
//			param = JsonUtil.JsonToMap(request.getParameter("param"));
//		}
		String featureKind = param.get("featureKind").toString();
		
		List<EgovMap> list = this.baseService.baseSelectList("event.selectEventList", param);
		
	    return this.gisUtil.createGeoJson(list, "lon", "lat", featureKind);
	}
	
	@RequestMapping(value = "/getLineStringData.do")
	public Map<String, Object> getLineStringData(HttpServletRequest request) throws Exception {
		Map<String, Object> param = null;
		if (request.getParameter("param").trim().equals("") == true) {
			param = new HashMap<String, Object>();
		} else {
			param = JsonUtil.JsonToMap(request.getParameter("param"));
		}
		List<EgovMap> list = this.baseService.baseSelectList("facility.selectFcltNodeList", param);
		return this.gisUtil.createGeoJson2(list,"lineString");
	}
	
	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/getCivilGeoFeature.do")
	public Map<String, Object> getCivilGeoFeature(HttpServletRequest request) throws Exception {
		Map<String, Object> param = null;
		HttpSession session = request.getSession(false);
		if (request.getParameter("param").trim().equals("") == true) {
			param = new HashMap<String, Object>();
		} else {
			param = JsonUtil.JsonToMap(request.getParameter("param"));
		}
		String featureKind = param.get("featureKind").toString();
		
		List<EgovMap> list = this.baseService.baseSelectList("common.getCivilCmplntList", param);
		
	    return this.gisUtil.createGeoJson(list, "lon", "lat", featureKind);
	}
	
	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/getCreateCarLineFeature")
	public Map<String, Object> getCreateCarLineFeature(HttpServletRequest request) throws Exception {
		Map<String, Object> param = null;
		HttpSession session = request.getSession(false);
		if (request.getParameter("param").trim().equals("") == true) {
			param = new HashMap<String, Object>();
		} else {
			param = JsonUtil.JsonToMap(request.getParameter("param"));
		}
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
		resList = this.baseService.baseSelectList("event.selectEventCarList", param);
		map.put("rows", resList);
	    return map;
	}
	
	
	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/getHeatMapData.do")
	public Map<String, Object> getHeatMapData(HttpServletRequest request) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> evtReList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> dongList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> dongEvtReList = new ArrayList<Map<String, Object>>();
		
		dongList = baseService.baseSelectList("common.dongData", null);
		
		System.out.println(dongList.size());
		for(int i = 0;i<dongList.size();i++) {
			System.out.println(dongList.get(i).get("dongNm"));
			String dongNm = (String) dongList.get(i).get("dongNm");
			System.out.println(dongNm);
			param.put("dongCd", dongList.get(i).get("dongCd"));
			dongEvtReList = baseService.baseSelectList("common.dongEvtData", param);
			for(int a = 1 ; a <= 24 ;a++) {
				resultData.put("ytarget", dongNm);
				resultData.put("value", dongEvtReList.get(i).get("evtCnt"));
				resultData.put("xtarget", a);
				System.out.println(resultData);
				evtReList.add(resultData);
			}
			System.out.println(evtReList);
			
		}
		
		return resultData;
	}
}
