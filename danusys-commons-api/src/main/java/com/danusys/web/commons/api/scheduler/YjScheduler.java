package com.danusys.web.commons.api.scheduler;

import com.danusys.web.commons.api.dto.FacilityDataRequestDTO;
import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.FacilityActiveLog;
import com.danusys.web.commons.api.model.FacilityOpt;
import com.danusys.web.commons.api.model.Station;
import com.danusys.web.commons.api.repository.FacilityActiveRepository;
import com.danusys.web.commons.api.repository.FacilityOptRepository;
import com.danusys.web.commons.api.repository.FacilityRepository;
import com.danusys.web.commons.api.scheduler.service.YjSchedulerService;
import com.danusys.web.commons.api.service.FacilityOptService;
import com.danusys.web.commons.api.service.FacilityService;
import com.danusys.web.commons.api.service.StationService;
import com.danusys.web.commons.api.util.ApiUtils;
import com.danusys.web.commons.api.util.IpCheckedUtil;
import com.danusys.web.commons.app.StrUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Profile(value = "yj")
@RequiredArgsConstructor
public class YjScheduler {
    private final ApiUtils apiUtils;
    private final ObjectMapper objectMapper;
    private final FacilityOptService facilityOptService;
    private final FacilityOptRepository facilityOptRepository;
    private final FacilityService facilityService;
    private final FacilityRepository facilityRepository;
    private final FacilityActiveRepository facilityActiveRepository;
    private final YjSchedulerService yjSchedulerService;

    /**
     * 정류장 유동인구 저장
     */
    @Scheduled(cron = "0 0 0/1 * * *")
    public void apiCallSchedule() throws Exception{
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        String formatNow = now.format(formatter);
        int iNow = Integer.parseInt(formatNow);
        List<Facility> facilityList = facilityService.findByFacilityKind(124L);
        facilityList.stream().forEach(f -> {
            Map<String,Object> param = new HashMap<>();
            String substring = f.getAdministZone().substring(0,5);
            if (f.getFacilityName().equals("유동인구") && substring.equals("47210")) {
                param.put("callUrl","/mjvt/smart-station/people-count");
                param.put("cameraId",f.getFacilityId());
                param.put("dateTime",iNow-1);
            }

            try {
                // event save;
                String json = objectMapper.writeValueAsString(apiUtils.getRestCallBody(param));
                log.info("event json =  {}",json);
                FacilityDataRequestDTO facilityDataRequestDTO = objectMapper.readValue(StrUtils.getStr(json), FacilityDataRequestDTO.class);
                facilityDataRequestDTO.setFacilityOptType(112);
                facilityDataRequestDTO.setFacilityOptName("floating_population");
                facilityOptService.save(facilityDataRequestDTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     *
     * 시설물 장애 이벤트 ping check
     */
    @Scheduled(cron = "0 0 0/1 * * *")
    public void ipPingCheck(){
        List<FacilityOpt> facilityOptList = new ArrayList<>();
        List<Facility> facilityList = facilityRepository.findAllByAdministZoneAndFacilityName("47210","유동인구");
        facilityList.stream().forEach(facility -> {
            FacilityOpt facilityOpt = facilityOptRepository.findByFacilitySeqAndFacilityOptName(facility.getFacilitySeq(), "ip");
            if(facilityOpt != null) {
                facilityOptList.add(facilityOpt);
            }
        });
        List<Map<String, Object>> ipLists = new ArrayList<>();
        List<FacilityActiveLog> facilityActiveLogList = new ArrayList<>();
        FacilityActiveLog facilityActiveLog = new FacilityActiveLog();
        facilityOptList.stream().filter(f -> f.getFacilityOptName().equals("ip"))
                .forEach(facilityOpt -> {
                    Map<String,Object> maps = new HashMap<>();
                    maps.put(facilityOpt.getFacilityOptName(),facilityOpt.getFacilityOptValue());
                    maps.put("facilitySeq",facilityOpt.getFacilitySeq());
                    ipLists.add(maps);
                });
        IpCheckedUtil.ipCheckedList(ipLists);

        ipLists.stream().forEach(f -> {
            FacilityActiveLog build = facilityActiveLog.builder().facilitySeq((Long) f.get("facilitySeq")).
                    facilityActiveCheck((boolean) f.get("active")).facilityActiveIp((String) f.get("ip")).build();
            facilityActiveLogList.add(build);

            Facility facility = facilityRepository.findByFacilitySeq((Long) f.get("facilitySeq"));
            if ((boolean) f.get("active")) {
                facility.setAliveCheck(1L);
            } else {
                facility.setAliveCheck(0L);
            }
            facilityService.save(facility);
        });
        facilityActiveRepository.saveAll(facilityActiveLogList);
    }

    @PostConstruct
    public void facilityScheduleInit() {
        yjSchedulerService.setScheduler();
    }
}
