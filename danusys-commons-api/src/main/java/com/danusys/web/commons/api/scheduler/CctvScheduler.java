package com.danusys.web.commons.api.scheduler;

import com.danusys.web.commons.app.RestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Profile(value = {"local"})
@RequiredArgsConstructor
public class CctvScheduler {
    /**
     * 시설물 상대 동기화
     */
    @Scheduled(fixedDelay = 60 * 1000 * 5)
    public void cctvScheduler() throws InterruptedException {
        log.trace("---------------------cctv scheduler---------------------");
        ResponseEntity<Map> responseEntity = null;
        List<Map<String, Object>> result = null;
        Map<String, Object> param = new HashMap<>();
        param.put("svr_ip","172.20.20.100");
        param.put("code","3200");
        param.put("send_kind","1");
        param.put("client","1");

        try {

            responseEntity = RestUtil.exchange("http://172.20.14.45:10003/api/athena/midsvr", HttpMethod.POST, MediaType.APPLICATION_JSON, param, Map.class);
            log.trace("# status >>> {}", responseEntity.getHeaders());
            log.trace("# status >>> {}", responseEntity.getStatusCode());
            log.trace("# status >>> {}", responseEntity.getBody());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
