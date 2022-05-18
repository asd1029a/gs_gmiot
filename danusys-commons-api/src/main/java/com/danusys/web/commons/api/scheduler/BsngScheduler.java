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
@Profile(value = "bsng")
@RequiredArgsConstructor
public class BsngScheduler {
    private final ApiUtils apiUtils;
    private final ObjectMapper objectMapper;
    private final EventService eventService;

//    @Scheduled(cron = "0/30 * * * * *")
    @Scheduled(fixedDelay = 60000)
    public void apiCallSchedule() throws Exception{
        log.trace("---------------------bsng scheduler---------------------");
    }
}
