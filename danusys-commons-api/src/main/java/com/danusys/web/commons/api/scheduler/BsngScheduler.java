package com.danusys.web.commons.api.scheduler;

import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.FacilityOpt;
import com.danusys.web.commons.api.scheduler.dto.FloatingPopulationDTO;
import com.danusys.web.commons.api.service.EventService;
import com.danusys.web.commons.api.service.FacilityOptService;
import com.danusys.web.commons.api.service.FacilityService;
import com.danusys.web.commons.api.util.ApiUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile(value = "bsng")
@RequiredArgsConstructor
public class BsngScheduler {
    private final ApiUtils apiUtils;
    private final ObjectMapper objectMapper;
    private final EventService eventService;
    private final RestTemplate restTemplate;
    private final FacilityService facilityService;
    private final FacilityOptService facilityOptService;

//    @Scheduled(cron = "0/30 * * * * *")
    @Scheduled(fixedDelay = 60000)
    public void apiCallSchedule() throws Exception{
        //log.trace("---------------------bsng scheduler---------------------");
    }

    // 부산남구 ap 유동인구 연계
//    @Scheduled(fixedDelay = 60000)
    @Scheduled(cron = "0 0 0/1 * * *")
    public void floatingPopulationCnt() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBasicAuth("admin", "welcome123");
            HttpEntity<String> entity = new HttpEntity<String>("", headers);

            ResponseEntity<FloatingPopulationDTO.RequestDto> responseEntity = restTemplate.exchange("https://100.200.250.251/api/v1/presence", HttpMethod.GET, entity, new ParameterizedTypeReference<FloatingPopulationDTO.RequestDto>() {});
            responseEntity.getBody().getPresenceResultList().stream().collect(Collectors.groupingBy(arg -> arg.getMsg().getApName(), LinkedHashMap::new, Collectors.counting()) ).forEach((id, cnt) -> {
                Facility facility = facilityService.findByFacilityId(id);
                if(facility != null) {
                    facilityOptService.save(FacilityOpt.builder()
                            .facilitySeq(facility.getFacilitySeq())
                            .facilityOptName("floating_population")
                            .facilityOptValue(cnt.toString())
                            .facilityOptType(112)
                            .build());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
