package com.danusys.web.commons.api.scheduler.service;

import com.danusys.web.commons.api.model.FacilitySetting;
import com.danusys.web.commons.api.scheduler.DynamicScheduler;
import com.danusys.web.commons.api.service.FacilitySettingService;
import com.danusys.web.commons.app.JsonUtil;
import com.danusys.web.commons.app.RestUtil;
import com.danusys.web.commons.app.StrUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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
    private String SERVICE_PORT = "172.20.14.19";
    /*@Value("${service.ip}")*/
    private String SERVICE_IP = "8400";
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
            List<FacilitySetting> settingList = facilitySettingService.findAll();

            settingList.stream().forEach(ff -> {
                if (!"41210102".equals(StrUtils.getStr(ff.getAdministZone()))) {
                    return;
                }
                String facilityId = StrUtils.getStr(ff.getFacilityId());
                String dayOfWeek = StrUtils.getStr(ff.getFacilitySettingDay());
                String settingTime = StrUtils.getStr(ff.getFacilitySettingTime());
                String facilitySettingValue = StrUtils.getStr(ff.getFacilitySettingTime());
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
                param.put("pointPaths", facilityId);
                param.put("settingValue", facilitySettingValue);
                log.info("요청 데이터 : {}", param);

                Runnable runnable = new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        ResponseEntity<Map> responseEntity = null;
                        List<Map<String, Object>> result = null;
                        try {
                            //TODO 운영계는 서버 IP로 변경
                            responseEntity = RestUtil.exchange("http://"+ SERVICE_IP +":"+ SERVICE_PORT +"/api/call", HttpMethod.POST, MediaType.APPLICATION_JSON, param, Map.class);
                            result = (List) (new HashMap<>((Map) responseEntity.getBody().get("return"))).get("pointValues");
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
