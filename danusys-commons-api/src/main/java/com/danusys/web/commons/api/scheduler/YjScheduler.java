package com.danusys.web.commons.api.scheduler;

import com.danusys.web.commons.api.dto.FacilityDataRequestDTO;
import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.FacilityActiveLog;
import com.danusys.web.commons.api.model.FacilityOpt;
import com.danusys.web.commons.api.repository.FacilityActiveRepository;
import com.danusys.web.commons.api.service.FacilityOptService;
import com.danusys.web.commons.api.service.FacilityService;
import com.danusys.web.commons.api.util.ApiUtils;
import com.danusys.web.commons.api.util.IpCheckedUtil;
import com.danusys.web.commons.app.StrUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    private final FacilityService facilityService;
    private final FacilityActiveRepository facilityActiveRepository;
    private static long FACILITY_SEQ = 9588L; //실서버 facility_seq
//    private static long FACILITY_SEQ = 9722L; // 로컬서버 facility_seq

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
            param.put("callUrl","/mjvt/smart-station/people-count");
            param.put("cameraId",f.getFacilityId());
            param.put("dateTime",iNow-1);
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
