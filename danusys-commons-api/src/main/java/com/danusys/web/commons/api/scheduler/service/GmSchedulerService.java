package com.danusys.web.commons.api.scheduler.service;

import com.danusys.web.commons.api.model.FacilitySetting;
import com.danusys.web.commons.api.scheduler.DynamicScheduler;
import com.danusys.web.commons.api.service.FacilitySettingService;
import com.danusys.web.commons.app.JsonUtil;
import com.danusys.web.commons.app.RestUtil;
import com.danusys.web.commons.app.StrUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/07/28
 * Time : 16:00
 */
@Slf4j
@Component
public class GmSchedulerService {
    /*@Value("${server.port}")*/
    private String SERVICE_IP = "127.0.0.1";
    /*@Value("${service.ip}")*/
    private String SERVICE_PORT = "8400";
    /*@Value("${facility.xml.path}")*/
    private String FACILITY_XML_PATH;
    private final FacilitySettingService facilitySettingService;
    private final DynamicScheduler dynamicScheduler;

    public GmSchedulerService(FacilitySettingService facilitySettingService, DynamicScheduler dynamicScheduler) {
        this.facilitySettingService = facilitySettingService;
        this.dynamicScheduler = dynamicScheduler;
    }


    public void setScheduler() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            List<FacilitySetting> settingList = facilitySettingService.findAll();

            settingList.stream().forEach(ff -> {
                if (StrUtils.getStr(ff.getAdministZone()).indexOf("41210") != 0) {
                    return;
                }
                String facilityId = StrUtils.getStr(ff.getFacilityId());
                String dayOfWeek = StrUtils.getStr(ff.getFacilitySettingDay());
                String settingTime = StrUtils.getStr(ff.getFacilitySettingTime());
                String facilitySettingValue = StrUtils.getStr(ff.getFacilitySettingValue());
                String facilitySeq = StrUtils.getStr(ff.getFacilitySeq());
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


                String cronWeek = "0,6";
                if("weekday".equals(dayOfWeek)) {
                    cronWeek = "1-5";
                }

                String cron = "0 " + min + " " + hours + " ?" +" * " + cronWeek;
                Map<String, Object> param = new HashMap<>();
                param.put("callUrl", "gmSetPointValues");
                param.put("pointValues", "");
                param.put("pointPath", facilityId);
                param.put("settingValue", facilitySettingValue);
                param.put("facilitySeq", facilitySeq);
                log.info("?????? ????????? : {}", param);

                Runnable runnable = new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        List<Map<String, Object>> result = null;
                        final HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        final String json = new ObjectMapper().writeValueAsString(param);
                        URI uri = new URI("http://"+ SERVICE_IP +":"+ SERVICE_PORT +"/api/call");
                        HttpEntity requestEntity = new HttpEntity(json, headers);
                        try {
                            //TODO ???????????? ?????? IP??? ??????
//                            responseEntity = RestUtil.exchange("http://"+ SERVICE_IP +":"+ SERVICE_PORT +"/api/call", HttpMethod.POST, MediaType.APPLICATION_JSON, param, Map.class);

                            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);
                            //result = (List) (new HashMap<>((Map) responseEntity.getBody().get("return"))).get("pointValues");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                dynamicScheduler.registerScheduler(cron, runnable);

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
