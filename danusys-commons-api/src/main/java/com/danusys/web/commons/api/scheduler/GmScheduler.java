package com.danusys.web.commons.api.scheduler;

import com.danusys.web.commons.api.dto.LogicalfolderDTO;
import com.danusys.web.commons.api.service.EventService;
import com.danusys.web.commons.api.util.ApiUtils;
import com.danusys.web.commons.api.util.SoapXmlDataUtil;
import com.danusys.web.commons.app.config.WebClientHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile(value = {"local", "gm"})
@RequiredArgsConstructor
public class GmScheduler {
    private final ApiUtils apiUtils;
    private final ObjectMapper objectMapper;
    private final EventService eventService;
//    private final HttpServletRequest request;
    private final WebClientHelper webClientHelper;

//    @Scheduled(cron = "0/30 * * * * *")
    @Scheduled(fixedDelay = 60000)
    public void apiCallSchedule() throws Exception{
        log.trace("---------------------gm scheduler---------------------");

        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("callUrl","gmPointList");
        param.add("pointPaths","data/gm_soap/OFa8.xml");

        log.info("요청 데이터 : {}", param);

        /**
         * 광명 자자체용
          */
//        ResponseEntity<Map> responseEntity = webClientHelper.exchange("http://localhost:8400/api/call", MediaType.APPLICATION_JSON, HttpMethod.POST, param, Map.class);
//        List<Map<String, Object>> result = (List) (new HashMap<>((Map) responseEntity.getBody().get("return"))).get("pointValues");
        /**
         * 내부 개발용
         */
        ResponseEntity<Map> responseEntity = webClientHelper.exchange("http://localhost:8400/api/gmPointValues.json", MediaType.APPLICATION_JSON, HttpMethod.POST, param, Map.class);
        List<Map<String, Object>> result = (List) (new HashMap<>((Map) responseEntity.getBody().get("return"))).get("pointValues");


        log.trace("result : {}", result.size());
        log.trace("result : {}", result);
        log.trace("result : {}", param.getFirst("pointPaths"));

        final List<LogicalfolderDTO.Logicalpoints.Lpt> lpts = SoapXmlDataUtil.getGmSoapPostList(String.valueOf(param.getFirst("pointPaths")));
//        final List<String> pointPaths = lpts.stream().map(m -> m.getPth()).collect(Collectors.toList());

        List facilityData = result.stream().peek(f -> {
            f.put("name", this.getXmlData(lpts, String.valueOf(f.get("pointPath")).replaceAll("point:","")).getNm());
        }).collect(Collectors.toList());

        log.trace("facilityData : {}", facilityData);
    }

    private LogicalfolderDTO.Logicalpoints.Lpt getXmlData(List<LogicalfolderDTO.Logicalpoints.Lpt> lpts, String path) {
        AtomicReference<LogicalfolderDTO.Logicalpoints.Lpt> lpt = new AtomicReference<>(new LogicalfolderDTO.Logicalpoints.Lpt());
        lpts.stream().forEach(f -> {
            if(f.getPth().equals(path)) {
                lpt.set(f);
            }
        });

        return lpt.get();
    }

}
