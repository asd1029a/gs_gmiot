package com.danusys.web.commons.api.scheduler;

import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.FacilityActiveLog;
import com.danusys.web.commons.api.model.FacilityOpt;
import com.danusys.web.commons.api.repository.FacilityActiveRepository;
import com.danusys.web.commons.api.scheduler.dto.FloatingPopulationDTO;
import com.danusys.web.commons.api.service.EventService;
import com.danusys.web.commons.api.service.FacilityOptService;
import com.danusys.web.commons.api.service.FacilityService;
import com.danusys.web.commons.api.util.ApiUtils;
import com.danusys.web.commons.api.util.IpCheckedUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
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
    private final FacilityActiveRepository facilityActiveRepository;
    private static long FACILITY_SEQ = 8244L; // 실서버 facility_seq
//    private static long FACILITY_SEQ = 9721L; // 로컬서버 facility_seq

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

    /**
     *
     * 시설물 장애 이벤트 ping check
     */
    @Scheduled(cron = "0 0 0/1 * * *")
    public void ipPingCheck(){
        Long faSeq = FACILITY_SEQ;
        List<FacilityOpt> facilityOptList = facilityOptService.findByFacilitySeq(faSeq);
        List<Map<String, Object>> ipLists = new ArrayList<>();
        List<FacilityActiveLog> facilityActiveLogList = new ArrayList<>();
        FacilityActiveLog facilityActiveLog = new FacilityActiveLog();
        facilityOptList.stream().filter(f -> f.getFacilityOptName().equals("ip"))
                .forEach(facilityOpt -> {
                    Map<String,Object> maps = new HashMap<>();
                    maps.put(facilityOpt.getFacilityOptName(),facilityOpt.getFacilityOptValue());
                    ipLists.add(maps);
                });
        IpCheckedUtil.ipCheckedList(ipLists);

        ipLists.stream().forEach(f -> {
            FacilityActiveLog build = facilityActiveLog.builder().facilitySeq(faSeq).
                    facilityActiveCheck((boolean) f.get("active")).facilityActiveIp((String) f.get("ip")).build();
            facilityActiveLogList.add(build);
        });
        facilityActiveRepository.saveAll(facilityActiveLogList);
    }

}
