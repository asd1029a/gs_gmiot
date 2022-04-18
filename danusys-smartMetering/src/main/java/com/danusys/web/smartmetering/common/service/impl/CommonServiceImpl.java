package com.danusys.web.smartmetering.common.service.impl;

import com.danusys.web.commons.auth.session.util.NetworkUtil;
import com.danusys.web.smartmetering.common.dao.CommonDao;
import com.danusys.web.smartmetering.common.service.CommonService;
import com.danusys.web.smartmetering.common.util.ExcelUtil;
import com.danusys.web.smartmetering.common.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CommonServiceImpl implements CommonService {
	private final Logger errorLogger = LoggerFactory.getLogger("XERR." + this.getClass());
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CommonDao commonDao;

	@Autowired
	ExcelUtil excelUtil;
	
	@Override
	public String getApiData(Map<String, Object> paramMap) throws Exception {
		URL url = new URL(paramMap.get("apiUrl").toString());
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		Object resultObject = null;
		con.setRequestMethod("GET");
		//con.setDoOutput(true); 
		//conn.setRequestProperty("Content-type", "application/json");
		//DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        //BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
        
		//wr.flush();
		//wr.close();
		try {
			logger.info("apiUrl : " + paramMap.get("apiUrl").toString());
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String inputLine;
			StringBuffer sbResult = new StringBuffer();
			 
			while ((inputLine = in.readLine()) != null) {
				sbResult.append(inputLine); 
			}
			in.close();
			
			String resultStr = sbResult.toString();
			
			if("xml".equals(paramMap.get("type"))) {
				JSONObject jsonObject = XML.toJSONObject(sbResult.toString());
				resultStr = jsonObject.toString();
			}
			
			if("Y".equals(paramMap.get("listFlag"))) {
				resultObject = new ObjectMapper().readValue(resultStr, new TypeReference<List<Map<String, Object>>>(){});
			} else {
				resultObject = new ObjectMapper().readValue(resultStr, new TypeReference<Map<String, Object>>(){});
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return JsonUtil.getJsonString(resultObject);
	}
	
	/**
	 * @param HttpServletRequest
	 * @return 클라이언트 정보 (JSON)
	 */
	public String getRemoteInfo(HttpServletRequest request) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String requestIp = NetworkUtil.getLocalReqIp(request);
		String requestType = ""; 
		String propRequestIp = "";
		
		/*
		InputStream is = getClass().getResourceAsStream("/message/global-remoteip.properties");
        Properties props = new Properties();
        props.load(is);
       
    	for(int i=1; i<6; i++) {
    		propRequestIp = (String) props.get("request.ip." + systemGov +i);
    		
    		if(propRequestIp!=null) {
    			if(requestIp.indexOf(propRequestIp)>-1) {
        			requestType = systemGov+i;
        			break;
        		}
    		} else {
    			requestType = "DEV";
    			break;
    		}
       	}
    	resultMap.put("systemGov", systemGov);
		resultMap.put("requestType", requestType);
		*/
		resultMap.put("requestIp", requestIp);
		
		logger.info("Request Ip : " + requestIp);
		
		return JsonUtil.getJsonString(resultMap);
	}
	
	/**
	 * @param HttpServletRequest
	 * @return isAjax에 따라 ModelAndView, JSON String 리턴
	*/
	public Object exceptionProc(HttpServletRequest request) throws Exception {

		ModelAndView mav = new ModelAndView();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Object resultObj = new Object();
		
		String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
		Integer code = (Integer) request.getAttribute("javax.servlet.error.status_code");
		
		HttpStatus codeText = null;
		String message = "";
		boolean isAjax = false;
		
		if(requestUri.indexOf(".ado")>-1) {
			isAjax = true;
		}
		
		switch (code) {
			case 400:
				codeText = HttpStatus.BAD_REQUEST;
				message = "BAD_REQUEST 잘못된 요청입니다.";
				break;
			case 401:
				codeText = HttpStatus.UNAUTHORIZED;
				message = "로그인을 해야 이용 가능합니다.";
				break;
			case 402:
				codeText = HttpStatus.UNAUTHORIZED;
				message = "권한 없는 URL을 요청하였습니다.";
				break;
			case 403 :
				codeText = HttpStatus.FORBIDDEN;
				message = "요청 권한이 없습니다.";
				break;
			case 404:
				codeText = HttpStatus.NOT_FOUND;
				message = "요청하신 페이지를 찾을 수 없습니다.";
				break;
			case 405:
				codeText = HttpStatus.METHOD_NOT_ALLOWED;
				message = "요청권한이 없습니다.";
				break;
			case 500:
				codeText = HttpStatus.INTERNAL_SERVER_ERROR;
				message = "처리중 오류가 발생하였습니다.";
				break;
		}

		String exceptionStr = "";
		if(request.getAttribute("javax.servlet.error.exception")!=null) {
			exceptionStr = request.getAttribute("javax.servlet.error.exception").toString();
		} else {
			exceptionStr = message;
		}
		
		// 1.  Exception 로그 등록
		resultMap.put("code", code);
		resultMap.put("statusCode", code);
		resultMap.put("message", message);
		resultMap.put("requestUri", requestUri + (request.getQueryString() == null ? "" : new StringBuilder("?").append(request.getQueryString()).toString()));
		resultMap.put("exceptionContent", exceptionStr);
		resultMap.put("requestIp", NetworkUtil.getLocalReqIp());
		resultMap.put("systemType", "METERING");
		
		// 2. Ajax Return 값 설정
		jsonMap.put("resultCode", code);
		jsonMap.put("message", message);
		jsonMap.put("exception", exceptionStr);
		
		if(isAjax) {
			HttpHeaders responseHeaders = new HttpHeaders();
		    responseHeaders.add("Content-Type", "application/json; charset=UTF-8");
		    resultObj = new ResponseEntity<String>(JsonUtil.getJsonString(jsonMap), responseHeaders, codeText);
		} else {
			resultMap.put("code", code);
			resultMap.put("message", message);
			resultMap.put("requestHost", request.getRemoteHost());
			resultMap.put("requestUri", requestUri);
			resultMap.put("exception", exceptionStr);
			
			mav.addObject("resultMap", resultMap);
			mav.setViewName("fragments/error");

			System.out.println("commonserviceimple에 mav정보 : " + mav);

			resultObj = mav;
		}
		
		if(code!=404) {
			// 3. EXCEPTION 등록
			Pattern wordPattern = Pattern.compile("###[A-Za-z아-헿0-9\\s:.-]+");
			Matcher wordMatcher = wordPattern.matcher(exceptionStr);
			StringBuffer exceptionSb = new StringBuffer();
			
			while (wordMatcher.find()) {
				wordMatcher.appendReplacement(exceptionSb, "<b>" + wordMatcher.group()+"</b>");
			}
			resultMap.put("exceptionContent", exceptionSb.toString());
			
			commonDao.insert("sys.INSERT_EXCEPTION", resultMap);
			errorLogger.error("Code : " + code + " / RequestUri : " + requestUri + " / Exception : " + exceptionSb.toString());
		}
		return resultObj;
	}
	
	/**
	 * geoJson 생성
	 * @param geoList
	 * @return geoJson
	 * @throws Exception
	 */
	public Map<String,Object> getGeoJson(List<Map<String,Object>> geoList, String id) throws Exception {
		
		Map<String,Object> geoObj = new HashMap<String,Object>();
		geoObj.put("type","FeatureCollection");
		//features
		ArrayList<Map<String, Object>> ary = new ArrayList<Map<String, Object>>();
		Integer i = 1;
		for(Map<String,Object> map : geoList) {
			Map<String,Object> each = new HashMap<String,Object>();
			
			each.put("type", "Feature");
			
			each.put("id", id+i);
			i++;
			
			Map<String,Object> geom = new HashMap<String,Object>();
			geom.put("type", "Point");
			
			ArrayList<Double> coordinates = new ArrayList<Double>();
			coordinates.add(Double.parseDouble(map.get("longitude").toString()));
			coordinates.add(Double.parseDouble(map.get("latitude").toString()));
			
			geom.put("coordinates",coordinates);
			each.put("geometry",geom);
			
			each.put("geometry_name","geom");
			
			Map<String,Object> prop = new HashMap<String,Object>();
			for(String key : map.keySet()) {
				prop.put(key,map.get(key));
				if((key.equals("longitude"))||(key.equals("latitude"))){
					prop.put(key, Double.parseDouble(map.get(key).toString()));
				}
			}
			each.put("properties",prop);
			
			ary.add(each);
		} //for cctvList
		geoObj.put("features",ary);
		return geoObj;
	}
}