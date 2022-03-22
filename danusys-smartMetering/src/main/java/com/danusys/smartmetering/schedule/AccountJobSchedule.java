package com.danusys.smartmetering.schedule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.danusys.smartmetering.account.service.AccountService;
import com.danusys.smartmetering.common.util.DateUtil;
import com.danusys.smartmetering.common.util.StringUtil;
import com.danusys.smartmetering.event.service.EventService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@EnableScheduling
public class AccountJobSchedule {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	EventService eventService;
	
	@Value("${api.host}")
	String apiHost;
	
	@Value("${api.id}")
	String apiId;
	
	@Value("${api.pw}")
	String apiPw;
	
	@SuppressWarnings("unchecked")
	@Scheduled(cron="0 12 * * * *") 
	public void cronDeviceInfo() throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		paramMap.put("url", apiHost + "/app/deviceInfoApi.do?id="+apiId+"&pw="+apiPw);
		
		List<Map<String, Object>> resultList = (List<Map<String, Object>>) this.getApiData(paramMap);
		
		for(int i=0; i<resultList.size(); i++) {
			if(i==0) {
				resultList.get(i).put("gpsLatitude", "37.477610797605614");
				resultList.get(i).put("gpsLongitude", "126.86456335746894");
			} else if(i==1) {
				resultList.get(i).put("gpsLatitude", "37.47244672770059");
				resultList.get(i).put("gpsLongitude", "126.8690161815463");
			} else {
				resultList.get(i).put("gpsLatitude", "37.458403531613975");
				resultList.get(i).put("gpsLongitude", "126.88249406827788");
			}
			accountService.insertAccount(resultList.get(i));
		}
	}
	
	/**
	 * 검침 API 데이터
	 * 매시 9분
	 */
	@SuppressWarnings("unchecked")
	@Scheduled(cron="0 9 * * * *")
	public void cronMeterData() throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String now = DateUtil.getCurrentDate("yyyy-MM-dd");
		
		paramMap.put("url", apiHost + "/app/meterDataApi.do?id="+apiId+"&pw="+apiPw+"&startDate="+now+"&endDate="+now);
		
		List<Map<String, Object>> resultList = (List<Map<String, Object>>) this.getApiData(paramMap);
		
		for(int i=0; i<resultList.size(); i++) {
			accountService.insertAccountData(resultList.get(i));
		}
	}
	
	/**
	 * 이벤트 등록
	 * 매시 11분
	 */
	@Scheduled(cron="0 11 * * * *")
	public void cronEventData() throws Exception {
		eventService.insertEventLog(new HashMap<String, Object>());
	}
	
	private List<?> getApiData(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> resultList = null;
		
		try {
			URL url = new URL(paramMap.get("url").toString());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String inputLine;
			StringBuffer sbResult = new StringBuffer();
			 
			while ((inputLine = in.readLine()) != null) {
				sbResult.append(inputLine); 
			}
			in.close();
			
			//String resultStr = sbResult.toString();
			//resultObject = new ObjectMapper().readValue(resultStr, new TypeReference<List<Map<String, Object>>>(){});
			//resultObject = new ObjectMapper().readValue(resultStr, new TypeReference<Map<String, Object>>(){});
			Map<String, Object> resultMap = new ObjectMapper().readValue(sbResult.toString(), new TypeReference<Map<String, Object>>(){});
			//System.out.println(resultMap.get("data"));
			
			resultList = (List<Map<String, Object>>) resultMap.get("data");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	public static void main(String[] args) {
		System.out.println("EVT-"+DateUtil.getCurrentDate("yyyyMMddHHmmsss")+"-"+StringUtil.getRandomAlpha(5));
	}
}