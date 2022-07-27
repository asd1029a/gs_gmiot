package com.danusys.web.commons.api.scheduler.controller;

import com.danusys.web.commons.api.scheduler.service.SchedulerService;
import com.danusys.web.commons.api.scheduler.service.YjMqttFacility;
import com.danusys.web.commons.api.scheduler.service.YjMqttManager;
import com.danusys.web.commons.app.JsonUtil;
import com.danusys.web.commons.app.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/06/16
 * Time : 20:03
 */

@Profile(value = "yj")
@RequestMapping("/mqtt")
@Slf4j
@RestController
public class MqttController {
    private final YjMqttManager yjMqttManager;
    private final YjMqttFacility yjMqttFacility;
    private final SchedulerService schedulerService;

    public MqttController(YjMqttManager yjMqttManager, YjMqttFacility yjMqttFacility, SchedulerService schedulerService) {
        this.yjMqttManager = yjMqttManager;
        this.yjMqttFacility = yjMqttFacility;
        this.schedulerService = schedulerService;
    }

    @PostMapping(value = "/set")
    public void facilitySetPower(@RequestBody Map<String, Object> param) {
        try {
            if(!yjMqttManager.getMqttClient().isConnected()) {
                return;
            }

            String facilityInfo = StrUtils.getStr(param.get("pointPath"));
            String setValue = StrUtils.getStr(param.get("settingValue"));
            StringBuilder topicSb = new StringBuilder();

            Map<String,Object> message = new HashMap<>();

            String shelter = facilityInfo.split("/")[0];
            String facilityId = facilityInfo.split("/")[1];

            if("switch".equals(facilityId)) {
                if("On".equals(setValue)) {
                    message.put(facilityInfo.split("/")[2], true);
                } else {
                    message.put(facilityInfo.split("/")[2], false);
                }

            } else {
                if("On".equals(setValue)) {
                    message.put("power", true);
                } else {
                    message.put("power", false);
                }

            }

            topicSb.append(shelter);
            topicSb.append("/set/");
            topicSb.append(facilityId);

            String topic = new String(topicSb);
            String jsonMessage = JsonUtil.convertMapToJson(message).toString();

            yjMqttManager.sender(topic,jsonMessage);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@PostMapping(value= "/setScheduler")
    public void setScheduler(@RequestBody Map<String,Object> map) {
        String test = "test,test2";
        schedulerService.setScheduler();
    }*/
}
