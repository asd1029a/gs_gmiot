package com.danusys.web.commons.api.scheduler;

import com.danusys.web.commons.api.service.EventService;
import com.danusys.web.commons.api.service.FacilityOptService;
import com.danusys.web.commons.api.service.FacilityService;
import com.danusys.web.commons.api.util.ApiUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Profile(value = "dev")
@RequiredArgsConstructor
public class DevScheduler {
    private final ApiUtils apiUtils;
    private final ObjectMapper objectMapper;
    private final EventService eventService;
    private final FacilityService facilityService;
    private final FacilityOptService facilityOptService;
    private final RestTemplate restTemplate;

//    @Scheduled(cron = "0/30 * * * * *")
//    @Scheduled(fixedDelay = 60000)
    public void apiCallSchedule() throws Exception{
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
}
