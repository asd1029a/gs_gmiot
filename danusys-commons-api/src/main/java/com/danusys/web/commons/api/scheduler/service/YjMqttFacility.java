package com.danusys.web.commons.api.scheduler.service;

import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.FacilityOpt;
import com.danusys.web.commons.api.model.Station;
import com.danusys.web.commons.api.scheduler.DynamicScheduler;
import com.danusys.web.commons.api.scheduler.types.FacilityKindType;
import com.danusys.web.commons.api.service.FacilityOptService;
import com.danusys.web.commons.api.service.FacilityService;
import com.danusys.web.commons.api.service.FacilitySettingService;
import com.danusys.web.commons.api.service.StationService;
import com.danusys.web.commons.app.JsonUtil;
import com.danusys.web.commons.app.StrUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/06/03
 * Time : 15:35
 */


@Profile(value ="yj")
@Slf4j
@Service
public class YjMqttFacility {
    private final FacilityService facilityService;
    private final FacilityOptService facilityOptService;
    private final FacilitySettingService facilitySettingService;
    private final StationService stationService;
    private final DynamicScheduler dynamicScheduler;

    @Autowired
    public YjMqttFacility(FacilityService facilityService, FacilityOptService facilityOptService, ObjectMapper objectMapper, StationService stationService, FacilitySettingService facilitySettingService, DynamicScheduler dynamicScheduler) {
        this.facilityService = facilityService;
        this.facilityOptService = facilityOptService;
        this.facilitySettingService = facilitySettingService;
        this.stationService = stationService;
        this.dynamicScheduler = dynamicScheduler;
    }

    // 동기화 t_facility 있으면 수정 없으면 입력
    @PostConstruct
    public void facilitySync() {

        try {
            YjMqttManager yjMqttManager = new YjMqttManager();
            MqttClient subscriber = yjMqttManager.getMqttClient();
            subscriber.subscribe("#", (topic, msg) -> {
                byte[] payload = msg.getPayload();
                log.info("topic : {}, message : {}", topic, new String(payload, StandardCharsets.UTF_8));

                // shetler1 .... 10
                String facilityId = topic;
                String shelterId = topic.split("/")[0];
                String facilityKindCode = topic.split("/")[1];
                FacilityKindType  facilityKindType = FacilityKindType.findFacilityKind(facilityKindCode);

                // mqtt get facility data
                Map<String,Object> mqttReceivedData = JsonUtil.JsonToMap(new String(payload, StandardCharsets.UTF_8));

                if("switch".equals(facilityKindCode)) {
                    mqttReceivedData.entrySet().forEach((entry) -> {
                        String facilityName = entry.getKey();
                        FacilityKindType facilityKindType2 = FacilityKindType.findFacilityKind(facilityName);
                        this.saveFacilitySync(shelterId+ "/" + facilityKindCode +"/"+ facilityName, shelterId, facilityKindType2, mqttReceivedData, facilityKindCode);
                    });
                } else {
                    this.saveFacilitySync(facilityId, shelterId, facilityKindType, mqttReceivedData, null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // save t_facility or t_facility_opt
    public void saveFacilitySync(String facilityId, String shelterId, FacilityKindType facilityKindType, Map<String,Object> mqttReceivedData, String deviceType) {
        try {
            //개소 정보 추출
            List<Station> stationList = stationService.findAll();
            stationList.stream().forEach(station -> {
                String stationName = StrUtils.getStr(station.getStationName());
                Facility facility = facilityService.findByFacilityId(facilityId);

                // facility info
                String stationSeq = StrUtils.getStr(station.getStationSeq());
                Long facilityKind = facilityKindType.getCommonCodeSeq();
                String facilityName = facilityKindType.getTitle();

                // 현재 시설물의 상위 개소가 저장되지 않은 상태 ||  shelter1 != shelter10
                if(!stationName.contains(shelterId) || !stationName.split("_")[1].equals(shelterId)) {
                    return;
                }

                // 시설물 연결 상태 변환
                Integer facilityStatus = "true".equals(StrUtils.getStr(mqttReceivedData.get("status"))) ? 1 : 0;

                // facility가 null 이면 새로 저장
                if(facility == null) {
                    if(facilityKind == -1) {
                        return;
                    }
                    if("자동문".equals(facilityName)) {
                        facilityName = facilityName + facilityId.charAt(facilityId.length() -1);
                    }

                    facility = Facility.builder()
                            .stationSeq(Long.parseLong(stationSeq))
                            .facilityId(facilityId)
                            .facilityKind(facilityKind)
                            .facilityName(facilityName)
                            .facilityStatus(facilityStatus)
                            .build();
                    facilityService.save(facility);

                } else {
                    // status 수정
                    facility.setFacilityStatus(facilityStatus);
                    facilityService.save(facility);
                }

                // 저장 되어 있다면!!
                // opt 동기화 시작
                List<FacilityOpt> facilityOptList = facilityOptService.findByFacilitySeq(facility.getFacilitySeq());

                // insert
                if(facilityOptList.isEmpty()) {
                    List<FacilityOpt> optList = new ArrayList<>();
                    Facility finalFacility = facility;
                    mqttReceivedData.entrySet().forEach((entry) -> {
                        String receivedFaciliyOptName = entry.getKey();
                        String receivedFacilityOptValue = entry.getValue().toString();
                        Integer optType = 112;
                        if(receivedFaciliyOptName.equals("status")) {
                            return;
                        } else if(receivedFaciliyOptName.equals("power")) {
                            optType = 175;
                        }
                        if(deviceType != null) {
                            String facilityName2 = facilityId.split("/")[2];
                            if(facilityName2.equals(receivedFaciliyOptName)) {
                                receivedFaciliyOptName = "power";
                                optType = 175;
                            } else {
                                return;
                            }
                        }

                        FacilityOpt optData = FacilityOpt.builder()
                                .facilitySeq(finalFacility.getFacilitySeq())
                                .facilityOptName(receivedFaciliyOptName)
                                .facilityOptType(optType)
                                .facilityOptValue(receivedFacilityOptValue)
                                .build();
                        optList.add(optData);
                    });
                    return;
                }

                // update
                Facility finalFacility1 = facility;
                List<FacilityOpt> optList = new ArrayList<>();
                facilityOptList.stream().forEach(optData -> {
                    if(optData.getFacilitySeq().equals(finalFacility1.getFacilitySeq())) {

                        // 현재 시설물에 received data
                        mqttReceivedData.entrySet().forEach(entry -> {
                            String receivedOptName = entry.getKey();
                            if(entry.getKey().equals("status")) {
                                return;
                            }
                            
                            // message 가 switch 일 경우 안에서 한번 더 파싱
                            if(deviceType != null) {
                                receivedOptName = "power";
                                if(!finalFacility1.getFacilityId().split("/")[2].equals(entry.getKey())) {
                                    return;
                                }
                            }
                             if(optData.getFacilityOptName().equals(receivedOptName)) {
                                optData.setFacilityOptValue(entry.getValue().toString());
//                                facilityOptService.save(optData);
                                 optList.add(optData);
                            }
                        });
                    }
                });
                facilityOptService.saveAll(optList);

            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setScheduler() {
        log.info("setting scheudler start !!");

        try {
            YjMqttManager yjMqttManager = new YjMqttManager();
            dynamicScheduler.stopScheduler();

            List<Map<String,Object>> settingList =  facilitySettingService.findBySetScheduler();
            settingList.stream().forEach(ff -> {
                Map<String,Object> message = new HashMap<>();
                String facilityId = StrUtils.getStr(ff.get("facility_id"));
                String dayOfWeek = StrUtils.getStr(ff.get("facility_setting_day"));
                String key = StrUtils.getStr(ff.get("optionKey"));
                String value = StrUtils.getStr(ff.get("optionValue"));
                String settingTime = StrUtils.getStr(ff.get("facility_setting_time"));
                String hours = "0";
                String min = "0";

                if(!"".equals(settingTime)) {
                    hours = settingTime.split(":")[0];
                    min = settingTime.split(":")[1];

                    if("00".equals(hours)) {
                        hours = "0";
                    }else {
                        hours = StringUtils.stripStart(settingTime.split(":")[0], "0");
                    }
                    if("00".equals(min)) {
                        min = "0";
                    }else {
                        min = StringUtils.stripStart(settingTime.split(":")[1], "0");
                    }
                }


                if(!"".equals(key)) {
                    String[] keyArray = key.split(",");
                    String[] valueArray = value.split(",");
                    for(int i=0;i<keyArray.length; i++) {
                        message.put(keyArray[i], valueArray[i]);
                    }
                }
                String cronWeek = "0,6";
                if("weekday".equals(dayOfWeek)) {
                    cronWeek = "1-5";
                }

                String cron = "0 " + min + " " + hours + " ?" +" * " + cronWeek;
                Runnable runnable = new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        String jsonMessage = JsonUtil.convertMapToJson(message).toString();
                        log.info("scheduler start : {} message {}, topic : {}", cron, jsonMessage, facilityId + "/set/");
                        yjMqttManager.sender(facilityId + "/set/", jsonMessage);
                    }
                };
                dynamicScheduler.registerScheduler(cron, runnable);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
