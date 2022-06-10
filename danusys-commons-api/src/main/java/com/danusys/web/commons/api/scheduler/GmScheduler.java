package com.danusys.web.commons.api.scheduler;

import com.danusys.web.commons.api.dto.LogicalfolderDTO;
import com.danusys.web.commons.api.model.CommonCode;
import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.FacilityOpt;
import com.danusys.web.commons.api.model.Station;
import com.danusys.web.commons.api.service.CommonCodeService;
import com.danusys.web.commons.api.service.FacilityOptService;
import com.danusys.web.commons.api.service.FacilityService;
import com.danusys.web.commons.api.service.StationService;
import com.danusys.web.commons.api.types.FacilityGroupType;
import com.danusys.web.commons.api.util.SoapXmlDataUtil;
import com.danusys.web.commons.app.RestUtil;
import com.danusys.web.commons.app.StrUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@Profile(value = {"local", "gm"})
@RequiredArgsConstructor
public class GmScheduler {
    private final FacilityService facilityService;
    private final FacilityOptService facilityOptService;
    private final StationService stationService;
    private final CommonCodeService commonCodeService;

    private static List<CommonCode> dataGroup;
    private static List<CommonCode> onOfGroup;
    long SMART_STATION_NUM = 62L; //스마트 정류장
    long SMART_POLE_NUM = 4L; //스마트 폴

    /**
     * 시설물 상대 동기화
     */
    @Scheduled(fixedDelay = 30 * 1000)
    public void facilityStatusSync() {
        log.trace("---------------------gm scheduler---------------------");
        this.facilitySync();

    }

    /**
     * TODO 서비스로 등록해서 UI에서 호출 가능하도록 변경 필요함
     * 시설물 동기화
     * 1. 광명 개소정보 조회
     * 2.1 스마트 정류장 정보 필터
     * 2.2 스마트 폴 정보 필터
     * 3. 개소의 이름( ex) 광명경찰서_14117)의 "_" 뒷 부분을 잘라서 xml의 장비 정보를 가져옮
     * 4. xml 정보중 facilityKind 를 가져외서 있는 건들만 pointPathOrg의 정보로 findByFacilityId를 조회해 오고 없으면 신규 생성
     *   > FacilityId가 조회 되면 업데이트가 됨
     * 5. 생성된 Facility를 리스트에 넣고
     * 6. 전체 리스트로 facilityService.saveAll를 호출 함.
     */
    public void facilitySync() {
        List<Station> lists = stationService.findAll(); //전체 개소 가져오기

//        List<CommonCode> dataGroup = commonCodeService.findByParentCodeSeq(113L);
//        List<CommonCode> onOfGroup = commonCodeService.findByParentCodeSeq(16L);

        //스마트 정류장
        List<Station> stations = lists.stream().filter(f -> f.getStationKind() == SMART_STATION_NUM).collect(toList());

        //스마트 폴
        List<Station> poles = lists.stream().filter(f -> f.getStationKind() == SMART_POLE_NUM).collect(toList());

        //스마트 정류장
        stations.stream().forEach(station -> { //개소 목록
            String stationName = StrUtils.getStr(station.getStationName());
            if(stationName.contains("_")) {
//                log.trace("stationName : {}", stationName);
                List<Facility> facilities = new ArrayList<>();
                String stationId = StrUtils.getStr(station.getStationName()).split("_")[1];

                if ("14117".equals(stationId)) {
                    List<Map<String, Object>> facilityDatas = this.findFacilityData(stationId);
//                    log.trace("facilityDatas : {}", facilityDatas.size());

                    /**
                     * 시설물 목록
                     */
                    for (Map<String, Object> fData : facilityDatas) {
                        String facilityKind = StrUtils.getStr(fData.get("facilityKind"));
                        if (!facilityKind.isEmpty()) {
                            FacilityGroupType facilityGroupType = this.facilityGroup(Long.parseLong(facilityKind));
//                            log.trace("facilityGroupType : {}", facilityGroupType);
                            String facilityId = StrUtils.getStr(fData.get("pointPathOrg"));
                            Facility facility = facilityService.findByFacilityId(facilityId);


                            /**
                             * 제어 가능한 시설물
                             */
                            if(facilityGroupType == FacilityGroupType.CONTROL) {
                                String facilityData = "On".equals(StrUtils.getStr(fData.get("presentValue"))) ? "1" : "0";
//                                log.trace("facility {} {} {} {}", station.getStationName(), station.getStationSeq(), facilityKind, facilityData);

                                if (facility == null) { //입력
//                                    facilities.add(
                                    facility = Facility.builder()
                                            .stationSeq(station.getStationSeq())
                                            .facilityId(StrUtils.getStr(fData.get("pointPathOrg")))
                                            .facilityKind(Long.parseLong(facilityKind))
                                            .facilityName(StrUtils.getStr(fData.get("name")))
                                            .facilityStatus(Integer.valueOf(facilityData))
                                            .latitude(station.getLatitude())
                                            .longitude(station.getLongitude())
                                            .build();
//                                );
                                } else { // 수정
//                                    log.trace("facilityOrg : {}", facility);
                                    facility.setFacilityStatus(Integer.valueOf(facilityData));
//                                    facilities.add(facilityOrg);
                                }
                                facility = facilityService.save(facility);
                            }

                            /**
                             * 데이터 적재
                             */
                            if(facilityGroupType == FacilityGroupType.DATA && facility != null) {

                                String facilityOptName = dataGroup.stream().filter(f -> f.getCodeSeq() == Long.parseLong(facilityKind)).collect(toList()).get(0).getCodeId();
                                String facilityOptValue = StrUtils.getStr(fData.get("presentValue"));

//                                log.trace(stationId + " > opt data : {}, {}, {}, {}", facilityOptName, facility.getFacilitySeq(), "stationInfo_" + facilityOptName, facilityOptValue);

                                FacilityOpt facilityOpt = facilityOptService.findByFacilitySeqAndFacilityOptName(facility.getFacilitySeq(), "stationInfo_" + facilityOptName);
                                if(facilityOpt == null) {
                                facilityOptService.save(FacilityOpt.builder()
                                        .facilitySeq(facility.getFacilitySeq())
                                        .facilityOptName("stationInfo_" + facilityOptName)
                                        .facilityOptValue(facilityOptValue)
                                        .facilityOptType(112)
                                        .build());
                                } else {
                                    facilityOpt.setFacilityOptValue(facilityOptValue);
                                    facilityOptService.save(facilityOpt);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 스마트 정류장 시설물 가져오기
     * @param stationId
     * @return
     */
    private List findFacilityData(String stationId) {
        Map<String, Object> param = new HashMap<>();
        param.put("callUrl","gmDataPointList");
        param.put("pointPaths","data/gm_soap/" + stationId + ".xml");
        log.info("요청 데이터 : {}", param);

        /**
         * 광명 자자체용
         */
//        ResponseEntity<Map> responseEntity = null;
//        try {
//            responseEntity = RestUtil.exchange("http://localhost:8400/api/call", HttpMethod.POST, MediaType.APPLICATION_JSON, param, Map.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//        List<Map<String, Object>> result = (List) (new HashMap<>((Map) responseEntity.getBody().get("return"))).get("pointValues");

        /**
         * 내부 개발용
         */
        ResponseEntity<Map> responseEntity = null;
        try {
            responseEntity = RestUtil.exchange("http://localhost:8400/api/gmPointValues.json", HttpMethod.POST, MediaType.APPLICATION_JSON, param, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        List<Map<String, Object>> result = (List) (new HashMap<>((Map) responseEntity.getBody().get("return"))).get("pointValues");

        log.trace("result : {}", result.size());
        log.trace("result : {}", result);
        log.trace("result : {}", param.get("pointPaths"));

        final List<LogicalfolderDTO.Logicalpoints.Lpt> lpts = SoapXmlDataUtil.getGmSoapPostList(String.valueOf(param.get("pointPaths")));
//        final List<String> pointPaths = lpts.stream().map(m -> m.getPth()).collect(Collectors.toList());

        List<Map<String, Object>> facilityData = result.stream().peek(f -> {
            LogicalfolderDTO.Logicalpoints.Lpt point = this.getXmlData(lpts, String.valueOf(f.get("pointPath")).replaceAll("point:",""));
            f.put("name", point.getNm());
            f.put("pointPathOrg", point.getPth());
            f.put("facilityKind", point.getKind());
        }).collect(toList());

        log.trace("facilityData : {}", facilityData);
        return facilityData;
    }

    /**
     * 포인트 정보
     * @param lpts
     * @param path
     * @return
     */
    private LogicalfolderDTO.Logicalpoints.Lpt getXmlData(List<LogicalfolderDTO.Logicalpoints.Lpt> lpts, String path) {
        AtomicReference<LogicalfolderDTO.Logicalpoints.Lpt> lpt = new AtomicReference<>(new LogicalfolderDTO.Logicalpoints.Lpt());
        lpts.stream().forEach(f -> {
            if(f.getPth().equals(path)) {
                lpt.set(f);
            }
        });

        return lpt.get();
    }

    /**
     * 시설물 그룹(데이터, 제어)
     * @param codeSeq
     * @return
     */
    private FacilityGroupType facilityGroup(Long codeSeq) {
        dataGroup = commonCodeService.findByParentCodeSeq(113L);
//        onOfGroup = commonCodeService.findByParentCodeSeq(16L);

        long dataGroupCount = dataGroup.stream().filter(f -> f.getCodeSeq() == codeSeq).count();
        if(dataGroupCount > 0) {
            return FacilityGroupType.DATA;
        } else {
            return FacilityGroupType.CONTROL;
        }
    }
}
