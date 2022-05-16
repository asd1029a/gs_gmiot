//package com.danusys.web.commons.api.scheduler;
//
//import com.danusys.web.commons.api.controller.ApiCallRestController;
//import com.danusys.web.commons.api.util.ApiUtils;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class Scheduler {
//    private final ApiUtils apiUtils;
//
////    @Scheduled(cron = "0/30 * * * * *")
////    @Scheduled(fixedDelay = 60000)
//    public void apiCallSchedule() throws Exception{
//        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
//        String formatNow = now.format(formatter);
//        int iNow = Integer.parseInt(formatNow);
//        Map<String,Object> param = new HashMap<>();
//        param.put("callUrl","/mjvt/smart-station/people-count");
//        param.put("cameraId","1");
//        param.put("dateTime",iNow-1);
//
//        log.info("현재 시각 : {}",iNow-1);
//
//        String body = apiUtils.getRestCallBody(param);
//        log.trace("scheduler people count : {}", body);
//    }
//}
