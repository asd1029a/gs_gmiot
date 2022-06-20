package com.danusys.web.commons.api.scheduler.service;

import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.FacilityOpt;
import com.danusys.web.commons.api.model.Station;
import com.danusys.web.commons.api.scheduler.types.FacilityKindType;
import com.danusys.web.commons.api.service.FacilityOptService;
import com.danusys.web.commons.api.service.FacilityService;
import com.danusys.web.commons.api.service.StationService;
import com.danusys.web.commons.app.JsonUtil;
import com.danusys.web.commons.app.StrUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
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
    private final ObjectMapper objectMapper;
    private final StationService stationService;

    @Autowired
    public YjMqttFacility(FacilityService facilityService, FacilityOptService facilityOptService, ObjectMapper objectMapper, StationService stationService) {
        this.facilityService = facilityService;
        this.facilityOptService = facilityOptService;
        this.objectMapper = objectMapper;
        this.stationService = stationService;
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
                        facilityOptService.save(FacilityOpt.builder()
                                .facilitySeq(finalFacility.getFacilitySeq())
                                .facilityOptName(receivedFaciliyOptName)
                                .facilityOptType(optType)
                                .facilityOptValue(receivedFacilityOptValue)
                                .build());
                    });
                    return;
                }

                // update
                Facility finalFacility1 = facility;
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
                                facilityOptService.save(optData);
                            }
                        });
                    }
                });

            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
