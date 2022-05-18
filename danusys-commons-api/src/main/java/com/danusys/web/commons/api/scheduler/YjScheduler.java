package com.danusys.web.commons.api.scheduler;

import com.danusys.web.commons.api.dto.EventReqeustDTO;
import com.danusys.web.commons.api.service.EventService;
import com.danusys.web.commons.api.util.ApiUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Profile(value = "yj")
@RequiredArgsConstructor
public class YjScheduler {
    private final ApiUtils apiUtils;
    private final ObjectMapper objectMapper;
    private final EventService eventService;

//    @Scheduled(cron = "0/30 * * * * *")
    @Scheduled(fixedDelay = 60000)
    public void apiCallSchedule() throws Exception{
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        String formatNow = now.format(formatter);
        int iNow = Integer.parseInt(formatNow);
        Map<String,Object> param = new HashMap<>();
        param.put("callUrl","/mjvt/smart-station/people-count");
        param.put("cameraId","1");
        param.put("dateTime",iNow-1);

        log.info("보려는 시간 : {}",iNow-1);

        String body = (String) apiUtils.getRestCallBody(param);
        log.trace("scheduler people count : {}", body);
        // event save
        EventReqeustDTO eventReqeustDTO = objectMapper.readValue(body, new TypeReference<EventReqeustDTO>() {
        });
        eventService.saveByEventRequestDTO(eventReqeustDTO);
    }
}
