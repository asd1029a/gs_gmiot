package com.danusys.web.commons.api.scheduler.service;

import com.danusys.web.commons.api.scheduler.DynamicScheduler;
import com.danusys.web.commons.api.service.FacilitySettingService;
import com.danusys.web.commons.app.JsonUtil;
import com.danusys.web.commons.app.StrUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/08/01
 * Time : 17:04
 */
@Slf4j
@Component
public class YjSchedulerService {
    final private DynamicScheduler dynamicScheduler;
    final private FacilitySettingService facilitySettingService;

    public YjSchedulerService(DynamicScheduler dynamicScheduler, FacilitySettingService facilitySettingService ) {
        this.dynamicScheduler = dynamicScheduler;
        this.facilitySettingService = facilitySettingService;
    }

    public void setScheduler() {
        log.info("setting scheudler start !!");

        try {
            YjMqttManager yjMqttManager = new YjMqttManager();
            dynamicScheduler.stopScheduler();

            List<Map<String,Object>> settingList =  facilitySettingService.findBySetScheduler();
            settingList.stream().forEach(ff -> {
                if(!"47210390".equals(StrUtils.getStr(ff.get("administ_zone")))) {
                    return;
                }

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
                        log.info("===scheduler start===");
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
