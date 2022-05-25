package com.danusys.web.commons.api.scheduler;

import com.danusys.web.commons.api.dto.EventReqeustDTO;
import com.danusys.web.commons.api.model.Event;
import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.service.EventService;
import com.danusys.web.commons.api.service.FacilityService;
import com.danusys.web.commons.api.util.ApiUtils;
import com.danusys.web.commons.app.StrUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Time;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
@Profile(value = "gj")
@RequiredArgsConstructor
public class GjScheduler {
    private final ApiUtils apiUtils;
    private final ObjectMapper objectMapper;
    private final EventService eventService;
    private final FacilityService facilityService;
    private final RestTemplate restTemplate;
    private List<Map<String, Double>> testList = new ArrayList<>();
    private double[] latList = {37.324622998538246, 37.325484200140124, 37.32598170009432, 37.32653137256927, 37.325884873654815,
            37.32544579913053, 37.324662395012396, 37.32396246561237, 37.322922254540615, 37.32208880447235,
            37.32177300719446, 37.321363837225526, 37.32068952399314, 37.32042472557308, 37.31977453592061,
            37.319406788675806, 37.319195584646444, 37.31942541274213, 37.32061720719184, 37.322125239335286};
    private double[] lonList = {126.71627654263953, 126.7173809723173, 126.71871483473845, 126.72011367903626, 126.7217775873515,
            126.72314750097082, 126.72191240691127, 126.72299005375115, 126.72318933606539, 126.72280146902158,
            126.72127151643195, 126.72430265925664, 126.72521726150553, 126.72323107242441, 126.72333118462878,
            126.72183404397262, 126.72053179241048, 126.71870669406218, 126.71661736536176, 126.71622077890437};

    private int count = 0;

//    @Scheduled(cron = "0/30 * * * * *")
    @Scheduled(fixedDelay = 10000)
    public void apiCallSchedule() throws Exception{
        if (testList.size() == 0) {
            setTestList();
        }

        if (count >= 20) count = 0;
        Map<String, Double> param = testList.get(count);
        Facility facility = facilityService.findByFacilityId("DTEST00001");
        facility.setLatitude(Double.parseDouble(StrUtils.getStr(param.get("latitude"))));
        facility.setLongitude(Double.parseDouble(StrUtils.getStr(param.get("longitude"))));
        facilityService.update(facility);

        Map<String, Double> param2 = ((count + 1) == testList.size()) ? testList.get(0) : testList.get(count + 1);
        Facility facility2 = facilityService.findByFacilityId("0034002734305108373739");
        facility2.setLatitude(Double.parseDouble(StrUtils.getStr(param2.get("latitude"))));
        facility2.setLongitude(Double.parseDouble(StrUtils.getStr(param2.get("longitude"))));
        facilityService.update(facility2);
        count++;
    }

    public void setTestList() {
        for (int i = 0; i < 20; i++) {
            Map<String, Double> map = new HashMap<>();
            map.put("longitude", lonList[i]);
            map.put("latitude", latList[i]);
            testList.add(map);
        }
    }

//    @Scheduled(fixedDelay = 999999999)
    public void setDroneDumyEvent() {
        List<Event> list = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            int ran = (int) (Math.random() * 30);
            int hour = (int) (Math.random() * 24);
            int min = (int) (Math.random() * 60);
            int sec = (int) (Math.random() * 60);

            Timestamp eventStartDt = Timestamp.valueOf(LocalDateTime.now().minusDays(ran).minusHours(hour).minusMinutes(min).minusSeconds(sec));

            Event e = Event.builder()
                    .eventKind(eventService.findEventKind("LKGE_ERCRT"))
                    .eventGrade(eventService.findEventGrade("10"))
                    .eventMessage("누설전류 경고")
                    .facilitySeq(2227L)
                    .eventStartDt(eventStartDt)
                    .build();
            list.add(e);
        }

        this.eventService.saveAll(list);
    }

//    @Scheduled(fixedDelay = 999999999)
    public void setEmsDumyEvent() {
        List<Event> list = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            int ran = (int) (Math.random() * 30);
            int hour = (int) (Math.random() * 24);
            int min = (int) (Math.random() * 60);
            int sec = (int) (Math.random() * 60);

            Timestamp eventStartDt = Timestamp.valueOf(LocalDateTime.now().minusDays(ran).minusHours(hour).minusMinutes(min).minusSeconds(sec));

            Event e = Event.builder()
                    .eventKind(eventService.findEventKind("LKGE_ERCRT"))
                    .eventGrade(eventService.findEventGrade("10"))
                    .eventMessage("누설전류 경고")
                    .facilitySeq(2227L)
                    .eventStartDt(eventStartDt)
                    .build();
            list.add(e);
        }

        this.eventService.saveAll(list);
    }

//    @Scheduled(fixedDelay = 999999999)
    public void getCenterEmsList() {
        try {
            Map<String,Object> param2 = new HashMap<>();
            param2.put("callUrl", "/centerEms/deivce/list");
//        Object result = apiUtils.getRestCallBody(param2);

            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    "http://localhost:8400/api/call",
                    HttpMethod.POST,
                    new HttpEntity<Map<String, Object>>(param2),
                    String.class);

            Map<String, Object> result = objectMapper.readValue(responseEntity.getBody(), new TypeReference<Map<String, Object>>() {
            });

            if (result == null) {
                return;
            }

            log.trace("scheduler 2 : {}", result);

            List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("facilityList");

            this.facilityService.saveAll(list, "EMS");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    @Scheduled(cron = "0/30 * * * * *")
    @Scheduled(fixedDelay = 60000)
    public void getDroneList() throws Exception{
//        Map<String,Object> param = new HashMap<>();
//        param.put("callUrl","/lg/drone/drones");
//
//        List<Map<String, Object>> body = (List<Map<String, Object>>) apiUtils.getRestCallBody(param);
//        log.trace("scheduler 1 : {}", body);

        Map<String,Object> param2 = new HashMap<>();
        param2.put("callUrl", "/cudo/video/device");
//        Object result = apiUtils.getRestCallBody(param2);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:8400/api/call",
                HttpMethod.POST,
                new HttpEntity<Map<String, Object>>(param2),
                String.class);

        Map<String, Object> result = objectMapper.readValue(responseEntity.getBody(), new TypeReference<Map<String, Object>>() {
        });

        if (result == null) {
            return;
        }

        log.trace("scheduler 2 : {}", result);

        List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("facilityList");

        this.facilityService.saveAll(list, "DRONE");
    }

    //    @Scheduled(cron = "0 0/2 * * * *")
    public void getStationList() throws Exception{
//        Map<String,Object> param = new HashMap<>();
//        param.put("callUrl","/lg/drone/drones");
//
//        List<Map<String, Object>> body = (List<Map<String, Object>>) apiUtils.getRestCallBody(param);
//        log.trace("scheduler 1 : {}", body);

        Map<String,Object> param2 = new HashMap<>();
        param2.put("callUrl", "/lg/drone/stations");
//        Object result = apiUtils.getRestCallBody(param2);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:8400/api/call",
                HttpMethod.POST,
                new HttpEntity<Map<String, Object>>(param2),
                String.class);

        Map<String, Object> result = objectMapper.readValue(responseEntity.getBody(), new TypeReference<Map<String, Object>>() {
        });

        if (result == null) {
            return;
        }

        log.trace("scheduler 2 : {}", result);

        List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("facilityList");

        this.facilityService.saveAll(list, "DRONE_STATION");
    }
}
