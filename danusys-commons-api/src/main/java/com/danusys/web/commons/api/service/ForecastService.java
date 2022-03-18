package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.model.ForecastGridTransfer;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ForecastService {

    public ForecastService() {}

    /*
    * 초단기 예보에 필요한 param setting
    * */
    public Map<String, Object> setReqParam(Map<String, Object> param) throws Exception {
        //nx, ny 기상청 격자 param 처리
        Double lon = (Double) param.get("lon");
        Double lat = (Double) param.get("lat");
        ForecastGridTransfer fcgt = new ForecastGridTransfer(lat, lon,0);
        Map<String, Object> resultMap = fcgt.transfer();
        param.put("nx",resultMap.get("nx"));
        param.put("ny",resultMap.get("ny"));

        //base_time, base_date param 처리
        Date d = new Date();
        SimpleDateFormat f1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat f2 = new SimpleDateFormat("HH");
        String baseDate = f1.format(d);
        Integer dayInt =  Integer.parseInt(f2.format(d));
        String baseTime = Integer.parseInt(f2.format(d))-1+"30";
        if(Integer.parseInt(baseTime) < 1000) baseTime="0"+baseTime;

        param.put("base_date", baseDate);
        param.put("base_time", baseTime);

        return param;
    }

    /*
    * 현재 하늘, 온도 상태반환
    * */
    public Map<String, Object> getCurSkyTmp(Map<String, Object> resultBody) throws Exception {
        //추후 JSON 처리 예정
        Map<String, Object> response = (Map<String, Object>) resultBody.get("response");
        Map<String, Object> body = (Map<String, Object>) response.get("body");
        Map<String, Object> items = (Map<String, Object>) body.get("items");
        List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");

        Map<String, Object> resultMap = new HashMap<>();
        Integer gapMin = 1000;
        Iterator iterObj = itemList.iterator();

        while (iterObj.hasNext()) {
            Map<String, Object> obj = (Map<String, Object>) iterObj.next();

            String btime = obj.get("baseTime").toString();
            btime = btime.charAt(0) == 0 ? btime.substring(1) : btime;
            String fcstTime = obj.get("fcstTime").toString();
            fcstTime = fcstTime.charAt(0) == 0 ? fcstTime.substring(1) : fcstTime;
            Integer gap = Integer.valueOf(fcstTime) -  Integer.valueOf(btime);

            //가장 최근의 데이터들만 
            if(gap <= gapMin) {
                gapMin = gap;
                String category = obj.get("category").toString();
                String fcstDate = obj.get("fcstDate").toString();
                String fcstValue = obj.get("fcstValue").toString();
                //SimpleDateFormat f3 = new SimpleDateFormat("yyyyMMddHH");
                //Date valueDateInt = f3.parse(fcstDate + fcstTime);

                //하늘 상태
                if("SKY".equals(category) && !resultMap.containsKey("PTY")) {
                    if("1".equals(fcstValue)) {
                        resultMap.put("sky", "sunny");
                        resultMap.put("skyNm", "맑음");
                    } else if("2".equals(fcstValue) || "4".equals(fcstValue)|| "3".equals(fcstValue) ) {
                        resultMap.put("sky", "cloudy");
                        resultMap.put("skyNm", "구름많음");
                    }
                }
                //강수 상태
                if("PTY".equals(category)) {
                    if("1".equals(fcstValue)) {
                        resultMap.put("sky", "rain");
                        resultMap.put("skyNm", "비");
                        resultMap.put("PTY",fcstValue);
                    } else if("2".equals(fcstValue)) {
                        resultMap.put("sky", "sleet");
                        resultMap.put("skyNm", "진눈깨비");
                        resultMap.put("PTY",fcstValue);
                    } else if("3".equals(fcstValue)) {
                        resultMap.put("sky", "snow");
                        resultMap.put("skyNm", "눈");
                        resultMap.put("PTY",fcstValue);
                    } else if("4".equals(fcstValue)) {
                        resultMap.put("sky", "rain");
                        resultMap.put("skyNm", "비");
                        resultMap.put("PTY",fcstValue);
                    }
                }
                //현재 온도
                if("T1H".equals(category)) {
                    resultMap.put("tmp", obj.get("fcstValue"));
                }
            } //current if
        }; //while end
        return resultMap;
    }
}
