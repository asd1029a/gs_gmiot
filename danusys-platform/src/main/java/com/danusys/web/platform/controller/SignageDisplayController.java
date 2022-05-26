package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.app.FileUtil;
import com.danusys.web.commons.app.JsonUtil;
import com.danusys.web.platform.service.facility.FacilityService;
import com.danusys.web.platform.service.station.StationService;
import com.danusys.web.commons.app.AirPollutionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName : com.danusys.web.platform.controller
 * fileName : SignageDisplayController
 * author : brighthoon94
 * date : 2022-05-13
 * description :
 * ===========================================================
 * DATE     AUTHOR      NOTE
 * -----------------------------------------------------------
 * 2022-05-13  brighthoon94      최초 생성
 */
@RestController
public class SignageDisplayController {

    public SignageDisplayController(FacilityService facilityService, StationService stationService) {
        this.facilityService = facilityService;
        this.stationService = stationService;
    }

    private final FacilityService facilityService;
    private final StationService stationService;

    @Value("${airkorea.service.key}")
    private String serviceKey;

    @GetMapping(value = "/pages/signageDisplay")
    public ModelAndView signagePage(@RequestParam(value = "id") int templateSeq, @RequestParam(value = "stationId") int stationSeq) throws Exception {
        ModelAndView mav = new ModelAndView("/view/pages/signage_display");
        EgovMap data = facilityService.getSignageLayout(templateSeq);
        EgovMap stationData = stationService.getOneStationForSignage(stationSeq);
        data.put("options", stationData);
        mav.addObject("data", data);
        return mav;
    }

    @PostMapping(value = "/signageDisplay/getListAirPollution")
    public Map<String, Object> getAirPollution(@RequestBody Map<String, Object> paramMap) throws Exception {
        /*TODO : 기상청 키 발급 properties 갱신 요망*/
        String measuringStationName = AirPollutionUtil.getNearByMeasuringStation(serviceKey, paramMap.get("x").toString(), paramMap.get("y").toString());

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + serviceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("returnType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*xml 또는 json*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("stationName","UTF-8") + "=" + URLEncoder.encode(measuringStationName, "UTF-8")); /*측정소 이름*/
        urlBuilder.append("&" + URLEncoder.encode("dataTerm","UTF-8") + "=" + URLEncoder.encode("DAILY", "UTF-8")); /*요청 데이터기간(1일: DAILY, 1개월: MONTH, 3개월: 3MONTH)*/
        urlBuilder.append("&" + URLEncoder.encode("ver","UTF-8") + "=" + URLEncoder.encode("1.3", "UTF-8")); /*버전별 상세 결과 참고*/

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        Map<String, Object> dataMap = JsonUtil.JsonToMap(sb.toString());
        Map<String, Object> valueMap = ((List<HashMap>)((HashMap)((HashMap) dataMap.get("response")).get("body")).get("items")).get(0);
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("measuringStationName", measuringStationName);
        valueMap.forEach((strKey, objValue) -> {
            Map<String, Object> result = null;
            Double result500 = null;
            try {
                if ("so2Value".equals(strKey) || "coValue".equals(strKey) || "o3Value".equals(strKey)
                        || "no2Value".equals(strKey) || "pm10Value24".equals(strKey) || "pm25Value24".equals(strKey)) {
                    result500 = AirPollutionUtil.convertAirPollutionDataToScore500(strKey, objValue);
                    result = AirPollutionUtil.convertAirPollutionDataToScoreMap(result500);
                    resultMap.put(strKey, result);
                } else if("khaiValue".equals(strKey)) {
                    resultMap.put(strKey, AirPollutionUtil.convertAirPollutionDataToScoreMap(Double.parseDouble(objValue.toString())));
                } else if("dataTime".equals(strKey)) {
                    resultMap.put(strKey, objValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return resultMap;
    }

    @RequestMapping(value="/signageDisplay/getImage")
    public void getImage(HttpServletResponse response, @RequestParam Map<String, Object> paramMap) throws Exception {
        String imageFileName = paramMap.get("imageFile").toString();
        FileUtil.getImage2("/pages/config/signage/", imageFileName, response);
    }

    @GetMapping(value="/signageDisplay/getVideo")
    public ResponseEntity<StreamingResponseBody> getVideo(@RequestParam String videoFile) throws Exception {
        File file = FileUtil.getVideoFile("/pages/config/signage/", videoFile);
        if(!file.isFile()) {
            return ResponseEntity.notFound().build();
        }
        StreamingResponseBody srb = outputStream -> FileCopyUtils.copy(new FileInputStream(file), outputStream);
        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "video/mp4");
        responseHeaders.add("Content-Length", Long.toString(file.length()));

        return ResponseEntity.ok().headers(responseHeaders).body(srb);
    }
}
