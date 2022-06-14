package com.danusys.web.commons.api.scheduler;

import com.danusys.web.commons.api.dto.EventReqeustDTO;
import com.danusys.web.commons.api.model.Event;
import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.FacilityOpt;
import com.danusys.web.commons.api.service.EventService;
import com.danusys.web.commons.api.service.FacilityOptService;
import com.danusys.web.commons.api.service.FacilityService;
import com.danusys.web.commons.api.service.StationService;
import com.danusys.web.commons.api.util.ApiUtils;
import com.danusys.web.commons.app.GisUtil;
import com.danusys.web.commons.app.StrUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile(value = "gj")
@RequiredArgsConstructor
public class GjScheduler {
    private final ApiUtils apiUtils;
    private final ObjectMapper objectMapper;
    private final EventService eventService;
    private final FacilityService facilityService;
    private final FacilityOptService facilityOptService;
    private final StationService stationService;
    private final RestTemplate restTemplate;

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final HttpSession session;

    private List<Map<String, Double>> testList = new ArrayList<>();
    private double[] latList = {35.80405637329925, 35.80366509913489, 35.803435578829465, 35.80313827937384, 35.80293993822085,
            35.8028508811051, 35.802506370345384, 35.80213066739625, 35.80180719609306, 35.801400379319034,
            35.801144868729445, 35.800811247012064, 35.80048812599378, 35.80030083424587, 35.80065038502661,
            35.80098418818261, 35.80186637016022, 35.802528837346514, 35.80288857312522, 35.80317511641252};

    private double[] lonList = {126.87960788436062, 126.87944203666537, 126.8793719675557, 126.8793148042305, 126.87913586481923,
            126.8787327114725, 126.87840036368404, 126.87816408657568, 126.87801094889892, 126.87794755802628,
            126.87796075139627, 126.87808288516642, 126.87827541520711, 126.87871098226493, 126.87887048154016,
            126.87892758606534, 126.87980962987741, 126.88000067320691, 126.8799169139106, 126.8796476227102};

    private int count = 0;

    // @Scheduled(cron = "0/30 * * * * *")
    // TODO : 드론 Dumy data
    @Scheduled(fixedDelay = 10000)
    public void droneCoordinatesTest() throws Exception{
        if (testList.size() == 0) {
            setTestList();
        }

        if (count >= 20) count = 0;
        Map<String, Double> param = testList.get(count);
        Facility facility = facilityService.findByFacilityId("DTEST00001");
        double latitude = Double.parseDouble(StrUtils.getStr(param.get("latitude")));
        double longitude = Double.parseDouble(StrUtils.getStr(param.get("longitude")));
        facility.setLatitude(latitude);
        facility.setLongitude(longitude);
        facility.setAdministZone(facilityService.getEmdCode(longitude, latitude));
        facilityService.update(facility);

        Map<String, Double> param2 = ((count + 1) == testList.size()) ? testList.get(0) : testList.get(count + 1);
        Facility facility2 = facilityService.findByFacilityId("DANUSYS_CCTV53");
        double latitude2 = Double.parseDouble(StrUtils.getStr(param2.get("latitude")));
        double longitude2 = Double.parseDouble(StrUtils.getStr(param2.get("longitude")));
        facility2.setLatitude(latitude2);
        facility2.setLongitude(longitude2);
        facility2.setAdministZone(facilityService.getEmdCode(longitude2, latitude2));
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

    // @Scheduled(fixedDelay = 999999999)
    // TODO : 스마트 분전반 이벤트 Dumy data
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

    // @Scheduled(fixedDelay = 999999999)
    // TODO : 스마트 분전반 목록 조회(cron 처리 추가 필요)
    public void getCenterEmsList() {
        try {
            Map<String,Object> param2 = new HashMap<>();
            param2.put("callUrl", "/centerEms/deivce/list");

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

    // @Scheduled(fixedDelay = 60000)
    // TODO : 스마트 분전반 상세 데이터 조회(cron 처리 추가 필요)
    public void getCenterEmsDetailList() throws Exception {
        List<Facility> facilityList = facilityService.findByFacilityKind(43L);

        facilityList.stream().forEach(f -> {

            Map<String,Object> param = new HashMap<>();
            param.put("callUrl", "/centerEms/device/list/detail");
            param.put("facility_id", f.getFacilityId());
//        Object result = apiUtils.getRestCallBody(param2);

            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    "http://localhost:8400/api/call",
                    HttpMethod.POST,
                    new HttpEntity<Map<String, Object>>(param),
                    String.class);

            Map<String, Object> detailData = null;
            try {
                detailData = objectMapper.readValue(responseEntity.getBody(), new TypeReference<Map<String, Object>>() {
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            if (detailData == null && detailData.isEmpty()) {
                return;
            }

            log.trace("data 2 : {}", detailData);

            List<FacilityOpt> facilityOptList = new ArrayList<>();
            Map<String, Object> data = (Map<String, Object>) detailData.get("facilityData");
            String facilityId = StrUtils.getStr(data.get("facility_id"));

            String[] temp = {"volt", "am", "igo", "igr", "igc", "mega",
                    "volt_r", "am_r", "volt_s", "am_s", "volt_t", "am_t", "volt_tot",
                    "ior", "igra", "igrb", "igrc", "wat_tot", "box_heat", "amn_status", "oam_status", "am_use"};
            List<String> addList = Arrays.asList(temp);

            data.entrySet().stream().filter(ff -> addList.contains(ff.getKey())).forEach(ff -> {
                FacilityOpt optOrigin = facilityOptService.findByFacilitySeqAndFacilityOptName(f.getFacilitySeq(), ff.getKey());
                if (optOrigin == null) {
                    FacilityOpt facilityOpt = FacilityOpt
                            .builder()
                            .facilitySeq(f.getFacilitySeq())
                            .facilityOptName(ff.getKey())
                            .facilityOptValue(StrUtils.getStr(ff.getValue()))
                            .facilityOptType(53)
                            .build();
                    facilityOptList.add(facilityOpt);
                } else {
                    optOrigin.setFacilityOptValue(StrUtils.getStr(ff.getValue()));
                    facilityOptList.add(optOrigin);
                }
            });

            facilityOptService.saveAll(facilityOptList);
        });
    }

    //    @Scheduled(cron = "0/30 * * * * *")
    // @Scheduled(fixedDelay = 60000)
    // TODO : 드론 목록 조회, 드론 디바이스 목록 조회(cron 처리 추가 필요)
    public void getDroneList() throws Exception{
        Map<String,Object> param = new HashMap<>();
        param.put("callUrl","/lg/drone/drones");

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:8400/api/call",
                HttpMethod.POST,
                new HttpEntity<Map<String, Object>>(param),
                String.class);

        List<Map<String, Object>> droneList = objectMapper.readValue(responseEntity.getBody(), new TypeReference<List<Map<String, Object>>>() {
        });

        log.trace("droneList 1 : {}", droneList);

        Map<String,Object> dronePram = new HashMap<>();
        dronePram.put("callUrl", "/cudo/video/device");

        ResponseEntity<String> responseEntity2 = restTemplate.exchange(
                "http://localhost:8400/api/call",
                HttpMethod.POST,
                new HttpEntity<Map<String, Object>>(dronePram),
                String.class);

        if (responseEntity2.getBody() == null) {
            return;
        }

        Map<String, Object> result = objectMapper.readValue(responseEntity2.getBody(), new TypeReference<Map<String, Object>>() {
        });

        List<Map<String, Object>> deviceList = (List<Map<String, Object>>) result.get("facilityList");

        List<FacilityOpt> facilityOptList = new ArrayList<>();
        List<Facility> l = new ArrayList<>();

        droneList.stream().forEach(f -> {
            String facilityId = StrUtils.getStr(f.get("facility_id"));
            String facilityName = StrUtils.getStr(f.get("facility_name"));
            Facility origin = facilityService.findByFacilityId(facilityId);
            Facility facility = origin == null ? Facility.builder().facilityId(facilityId).facilityName(facilityName).facilityKind(56L).build()
                    : origin;
            facilityService.save(facility);
            l.add(origin);
            String[] temp = {"serial_number", "model_name", "emergency_battery_level", "manufacturer",
                    "emergency_battery_rth", "emergency_battery_land", "battery_capacity", "battery_flight_time"};
            List<String> addList = Arrays.asList(temp);
            f.entrySet().stream().filter(ff -> addList.contains(ff.getKey())).forEach(ff -> {
                FacilityOpt optOrigin = facilityOptService.findByFacilitySeqAndFacilityOptName(facility.getFacilitySeq(), ff.getKey());
                if (optOrigin == null) {
                    FacilityOpt facilityOpt = FacilityOpt
                            .builder()
                            .facilitySeq(facility.getFacilitySeq())
                            .facilityOptName(ff.getKey())
                            .facilityOptValue(StrUtils.getStr(ff.getValue()))
                            .facilityOptType(53)
                            .build();
                    facilityOptList.add(facilityOpt);
                } else {
                    optOrigin.setFacilityOptValue(StrUtils.getStr(ff.getValue()));
                    facilityOptList.add(optOrigin);
                }
            });
        });
        try {
            facilityOptList.stream().filter(f -> f.getFacilityOptName() != null && f.getFacilityOptName().equals("serial_number")).forEach(f -> {
                deviceList.stream().filter(ff -> f.getFacilityOptValue() != null && StrUtils.getStr(ff.get("serial_number")).equals(f.getFacilityOptValue())).forEach(ff -> {
                    String[] temp = {"video_id"};
                    List<String> addList = Arrays.asList(temp);
                    ff.entrySet().stream().filter(fff -> addList.contains(fff.getKey())).forEach(fff -> {
                        FacilityOpt origin = facilityOptService.findByFacilitySeqAndFacilityOptName(f.getFacilitySeq(), fff.getKey());
                        if (origin == null) {
                            FacilityOpt facilityOpt = FacilityOpt
                                    .builder()
                                    .facilitySeq(f.getFacilitySeq())
                                    .facilityOptName(fff.getKey())
                                    .facilityOptValue(StrUtils.getStr(fff.getValue()))
                                    .facilityOptType(53)
                                    .build();
                            facilityOptList.add(facilityOpt);
                        } else {
                            origin.setFacilityOptValue(StrUtils.getStr(fff.getValue()));
                            facilityOptList.add(origin);
                        }
                    });
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        facilityOptService.saveAll(facilityOptList);
    }

    @Scheduled(cron = "0/30 * * * * *")
    // TODO : 드론 방송 목록 조회(cron 처리 추가 필요)
    public void getDroneBroadcast() throws Exception {
        Map<String,Object> param2 = new HashMap<>();
        param2.put("callUrl", "/cudo/video/broadcast/list");

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:8400/api/call",
                HttpMethod.POST,
                new HttpEntity<Map<String, Object>>(param2),
                String.class);

        if (responseEntity.getBody().equals("")) {
            return;
        }

        Map<String, Object> result = objectMapper.readValue(responseEntity.getBody(), new TypeReference<Map<String, Object>>() {
        });

        List<Map<String, Object>> deviceGroup = (List<Map<String, Object>>) result.get("device_groups");
        deviceGroup.stream().forEach(d -> {
            List<Map<String, Object>> deviceList = (List<Map<String, Object>>) d.get("devices");

            deviceList.stream().forEach(f -> {
                String videoIdStr = "video_id";
                String videoId = (String) f.get(videoIdStr);
                FacilityOpt videoOpt = facilityOptService.findByFacilityOptNameAndFacilityOptValue(videoIdStr, videoId);
                if (videoOpt == null) return;
                String jobIdStr = "job_id";
                FacilityOpt jobIdOpt = facilityOptService.findByFacilitySeqAndFacilityOptName(videoOpt.getFacilitySeq(), jobIdStr);
                Map<String, Object> broadcast = (Map<String, Object>) f.get("broadcast");
                String state = StrUtils.getStr(broadcast.get("state"));
                // R:방송대기, S:방송중, E:방송종료
                switch (state) {
                    case "R" :
                        break;
                    case "S" :
                        List<Map<String, Object>> streamingList = (List<Map<String, Object>>) broadcast.get("streamings");
                        if (streamingList.size() == 0) return;
                        Map<String, Object> stream = streamingList.get(0);
                        String jobId = StrUtils.getStr(stream.get("key"));
                        if (jobIdOpt == null) {
                            jobIdOpt = FacilityOpt.builder().facilitySeq(videoOpt.getFacilitySeq()).facilityOptName(jobIdStr).facilityOptType(53).facilityOptValue(jobId).build();
                        } else {
                            jobIdOpt.setFacilityOptValue(jobId);
                        }
                        facilityOptService.save(jobIdOpt);
                        break;
                    case "E" :
                        if (jobIdOpt == null) {
                            jobIdOpt = FacilityOpt.builder().facilitySeq(videoOpt.getFacilitySeq()).facilityOptName(jobIdStr).facilityOptType(53).facilityOptValue("").build();
                        } else {
                            jobIdOpt.setFacilityOptValue("");
                        }
                        facilityOptService.save(jobIdOpt);
                        break;
                }
            });
        });
    }

    //    @Scheduled(cron = "0/30 * * * * *")
     @Scheduled(fixedDelay = 1000)
    // TODO : 드론 좌표 및 실시간 데이터 업데이트 스케줄(cron 처리 추가 필요)
    public void getDroneCoordinatesList() throws Exception {
        Map<String,Object> param2 = new HashMap<>();
        param2.put("callUrl", "/lg/drone/drones/current_position");

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:8400/api/call",
                HttpMethod.POST,
                new HttpEntity<Map<String, Object>>(param2),
                String.class);

        if (responseEntity.getBody().equals("")) {
            return;
        }

        List<Map<String, Object>> list = objectMapper.readValue(responseEntity.getBody(), new TypeReference<List<Map<String, Object>>>() {
        });

        log.trace("list 2 : {}", list);

        List<FacilityOpt> facilityOptList = new ArrayList<>();

        list.stream().forEach(f -> {
            String facilityId = StrUtils.getStr(f.get("facility_id"));
            Map<String, Object> data = (Map<String, Object>) f.get("data");
            Map<String, Object> properties = (Map<String, Object>) data.get("properties");
            Map<String, Object> homePosition = (Map<String, Object>) data.get("home_position");
            double homeLatitude = Double.parseDouble(StrUtils.getStr(homePosition.get("latitude"))) / 10000000;
            double homeLongitude = Double.parseDouble(StrUtils.getStr(homePosition.get("longitude"))) / 10000000;
            double latitude = (double) properties.get("latitude");
            double longitude = (double) properties.get("longitude");
            double homeDist = 0;
            try {
                homeDist = GisUtil.getDistanceBetweenPoints(homeLatitude, homeLongitude, latitude, longitude, "km");
                properties.put("home_dist", homeDist);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Facility origin = facilityService.findByFacilityId(facilityId);
            origin.setLatitude(latitude);
            origin.setLongitude(longitude);
//            origin.setAdministZone(facilityService.getEmdCode(longitude, latitude));
            facilityService.save(origin);
            int flightState = Integer.parseInt(StrUtils.getStr(properties.get("flight_state")));
            properties.put("flight_state", this.convertFlightState(flightState));
            String[] temp = {"direction", "head", "alt", "run_dist", "battery", "speed", "home_dist", "flight_state"};
            List<String> addList = Arrays.asList(temp);
            properties.entrySet().stream().filter(ff -> addList.contains(ff.getKey())).forEach(ff -> {
                FacilityOpt optOrigin = facilityOptService.findByFacilitySeqAndFacilityOptName(origin.getFacilitySeq(), ff.getKey());
                if (optOrigin == null) {
                    FacilityOpt facilityOpt = FacilityOpt
                            .builder()
                            .facilitySeq(origin.getFacilitySeq())
                            .facilityOptName(ff.getKey())
                            .facilityOptValue(StrUtils.getStr(ff.getValue()))
                            .facilityOptType(53)
                            .build();
                    facilityOptList.add(facilityOpt);
                } else {
                    optOrigin.setFacilityOptValue(StrUtils.getStr(ff.getValue()));
                    facilityOptList.add(optOrigin);
                }
            });
        });

        facilityOptService.saveAll(facilityOptList);
    }

    // TODO : 드론 비행 상태값 변환 처리 (이후 DroneUtils에 포함)
    public String convertFlightState(int flightState) {
        String result = "";
        switch (flightState) {
            case 0 :
                result = "Onground";
                break;
            case 1 :
                result = "Idle";
                break;
            case 2:
                result = "이륙";
                break;
            case 3 :
                result = "미션비행";
                break;
            case 4 :
                result = "임무정지(호버링)";
                break;
            case 5 :
                result = "FTH";
                break;
            case 6 :
                result = "FTH 정지";
                break;
            case 7 :
                result = "선회비행";
                break;
            case 8 :
                result = "선회비행 정지";
                break;
            case 9 :
                result = "RTH";
                break;
            case 10 :
                result = "착륙";
                break;
            default :
                result = "";
                break;
        }
        return result;
    }

    // @Scheduled(cron = "0 0/2 * * * *")
//     @Scheduled(fixedDelay = 1000000)
    // TODO : 드론 격납고 리스트 조회(cron 설정 추가 필요)
    public void getStationList() throws Exception{
        Map<String,Object> param2 = new HashMap<>();
        param2.put("callUrl", "/lg/drone/stations");
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:8400/api/call",
                HttpMethod.POST,
                new HttpEntity<Map<String, Object>>(param2),
                String.class);

        List<Map<String, Object>> result = objectMapper.readValue(responseEntity.getBody(), new TypeReference<List<Map<String, Object>>>() {
        });

        if (result == null) {
            return;
        }

        this.stationService.saveAll(result, "DRONE_STATION");
    }
}
