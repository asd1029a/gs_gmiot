package com.danusys.web.commons.api.scheduler;

import com.danusys.web.commons.api.dto.FacilityDataRequestDTO;
import com.danusys.web.commons.api.dto.LogicalfolderDTO;
import com.danusys.web.commons.api.model.*;
import com.danusys.web.commons.api.repository.FacilityActiveRepository;
import com.danusys.web.commons.api.repository.FacilityOptRepository;
import com.danusys.web.commons.api.repository.FacilityRepository;
import com.danusys.web.commons.api.scheduler.service.GmSchedulerService;
import com.danusys.web.commons.api.service.*;
import com.danusys.web.commons.api.service.executor.RestApiExecutor;
import com.danusys.web.commons.api.types.FacilityGroupType;
import com.danusys.web.commons.api.util.ApiUtils;
import com.danusys.web.commons.api.util.IpCheckedUtil;
import com.danusys.web.commons.api.util.XmlDataUtil;
import com.danusys.web.commons.app.RestUtil;
import com.danusys.web.commons.app.StrUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@Profile(value = {"gm", "local"})
@RequiredArgsConstructor
public class GmScheduler {
    private final ObjectMapper objectMapper;
    private final FacilityService facilityService;
    private final FacilityRepository facilityRepository;
    private final ApiUtils apiUtils;
    private final FacilityOptService facilityOptService;
    private final FacilityOptRepository facilityOptRepository;
    private final StationService stationService;
    private final CommonCodeService commonCodeService;
    private final FacilityActiveRepository facilityActiveRepository;
    private final GmSchedulerService gmSchedulerService;
    private final ApiExecutorFactoryService apiExecutorFactoryService;
    private final RestApiExecutor restApiExecutor;
    private final ApiCallService apiService;
    private final RestTemplate restTemplate;
    private static List<CommonCode> dataGroup;
    private static long SMART_STATION_NUM = 62L; //스마트 정류장
    private static long SMART_POLE_NUM = 4L; //스마트 폴
    private static int FACILITY_OPT_TYPE_ACCUMULATE_DATA = 112; //누적 데이터
    private static int FACILITY_OPT_TYPE_DATA = 53; // 수정 데이터
    private static List<Long> ACCUMULATE_DATA_GROUP = Arrays.asList(114L, 115L, 116L, 117L, 118L, 119L); //누적데이터 common code
    private static int FACILITY_OPT_TYPE_POWER = 175; // power : true, false
    private static long FACILITY_SEQ = 2219L; // 나중에 실제 ip넣을때 실제 facilitySeq으로 바꿔야함

    @Value("${server.port}")
    private String SERVICE_PORT;
    @Value("${service.ip}")
    private String SERVICE_IP;
    @Value("${facility.xml.path}")
    private String FACILITY_XML_PATH;

    @Value("#{${vms.server.map}}")
    private Map<String, String> vmsServerData;


    /**
     * 시설물 상대 동기화
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000)
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

        //스마트 정류장
        List<Station> stations = lists.stream().filter(f -> f.getStationKind() == SMART_STATION_NUM).collect(toList());

        //스마트 폴
        List<Station> poles = lists.stream().filter(f -> f.getStationKind() == SMART_POLE_NUM).collect(toList());

        //스마트 정류장
//        stations.stream().filter(f -> f.getStationName().contains("14117")).forEach(station -> { //로컬환경 개소 목록
        stations.stream().forEach(station -> { //개소 목록
            String stationName = StrUtils.getStr(station.getStationName());
            if (stationName.contains("_")) {
                String stationId = StrUtils.getStr(station.getStationName()).split("_")[1];
                List<Map<String, Object>> facilityDatas = this.findFacilityData(stationId);

                /**
                 * 시설물 목록
                 */
                for (Map<String, Object> fData : facilityDatas) {
                    String facilityKind = StrUtils.getStr(fData.get("facilityKind"));
                    if (!facilityKind.isEmpty()) {
                        FacilityGroupType facilityGroupType = this.facilityGroup(Long.parseLong(facilityKind));
                        log.trace("facilityGroupType : {} {}", facilityGroupType, Long.parseLong(facilityKind));
                        String facilityId = StrUtils.getStr(fData.get("pointPathOrg"));
                        Facility facility = facilityService.findByFacilityId(facilityId);

                        /**
                         * 제어, 데이터 가능한 시설물 모두 입력
                         */
                        String facilityData = "On".equals(StrUtils.getStr(fData.get("presentValue"))) ? "1" : "0";
//                        log.trace("facility {} {} {} {}", station.getStationName(), station.getStationSeq(), facilityKind, facilityData);

                        if (facility == null) { //입력
                            facility = Facility.builder()
                                    .stationSeq(station.getStationSeq())
                                    .facilityId(StrUtils.getStr(fData.get("pointPathOrg")))
                                    .facilityKind(Long.parseLong(facilityKind))
                                    .facilityName(StrUtils.getStr(fData.get("name")))
                                    .facilityStatus(Integer.valueOf(facilityData))
                                    .latitude(station.getLatitude())
                                    .longitude(station.getLongitude())
                                    .build();
                        } else { // 수정
                            facility.setFacilityStatus(Integer.valueOf(facilityData));
                        }
                        facility = facilityService.save(facility);

                        /**
                         * 광명 누적 데이터 적재
                         */
                        if (facilityGroupType == FacilityGroupType.DATA & facility != null) {
                            String facilityOptName = dataGroup.stream().filter(f -> f.getCodeSeq() == Long.parseLong(facilityKind)).collect(toList()).get(0).getCodeId();
                            String facilityOptValue = StrUtils.getStr(fData.get("presentValue"));

                            log.trace(stationId + "#### > opt data : {}, {}, {}, {}", facilityOptName, facility.getFacilitySeq(), facilityOptName, facilityOptValue);
                            FacilityOpt facilityOpt = facilityOptService.findByFacilitySeqAndFacilityOptName(facility.getFacilitySeq(), facilityOptName);
                            if (facilityOpt == null) {
                                facilityOptService.save(FacilityOpt.builder()
                                        .facilitySeq(facility.getFacilitySeq())
                                        .facilityOptName(facilityOptName)
                                        .facilityOptValue(facilityOptValue)
                                        .facilityOptType(FACILITY_OPT_TYPE_DATA)
                                        .build());
                            } else {
                                facilityOpt.setFacilityOptValue(facilityOptValue);
                                facilityOptService.save(facilityOpt);
                            }
                        } else {
                            if(facility != null) {
                                FacilityOpt facilityOpt = facilityOptService.findByFacilitySeqAndFacilityOptName(facility.getFacilitySeq(), "power");
                                String facilityOptValue = "On".equals(StrUtils.getStr(fData.get("presentValue"))) ? "true" : "false";
                                log.trace(stationId + "#### > opt data : {}, {}, {}", "power", facility.getFacilitySeq(), facilityOptValue);

                                if( facilityOpt == null ) {
                                    facilityOptService.save(FacilityOpt.builder()
                                            .facilitySeq(facility.getFacilitySeq())
                                            .facilityOptName("power")
                                            .facilityOptValue(facilityOptValue)
                                            .facilityOptType(FACILITY_OPT_TYPE_POWER)
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
     *
     * @param stationId
     * @return
     */
    private List findFacilityData(String stationId) {
        Map<String, Object> param = new HashMap<>();
        param.put("callUrl", "gmGetPointValues");
        param.put("pointPaths", FACILITY_XML_PATH + stationId + ".xml"); //TODO 서버 경로로 수정
        log.info("요청 데이터 : {}", param);

        /**
         * 광명 자자체용
         */
        ResponseEntity<Map> responseEntity = null;
        List<Map<String, Object>> result = null;
        try {
            //TODO 운영계는 서버 IP로 변경
            responseEntity = RestUtil.exchange("http://"+ SERVICE_IP +":"+ SERVICE_PORT +"/api/call", HttpMethod.POST, MediaType.APPLICATION_JSON, param, Map.class);
            result = (List) (new HashMap<>((Map) responseEntity.getBody().get("return"))).get("pointValues");
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 내부 개발용
         */
//        ResponseEntity<Map> responseEntity = null;
//        try {
//            responseEntity = RestUtil.exchange("http://localhost:8400/api/gmPointValues.json", HttpMethod.POST, MediaType.APPLICATION_JSON, param, Map.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//
//        List<Map<String, Object>> result = (List) (new HashMap<>((Map) responseEntity.getBody().get("return"))).get("pointValues");

        log.trace("result : {}", result.size());
        log.trace("result : {}", result);
        log.trace("result : {}", param.get("pointPaths"));

        final List<LogicalfolderDTO.Logicalpoints.Lpt> lpts = XmlDataUtil.getGmSoapPostList(String.valueOf(param.get("pointPaths")));

        List<Map<String, Object>> facilityData = result.stream().peek(f -> {
            LogicalfolderDTO.Logicalpoints.Lpt point = this.getXmlData(lpts, String.valueOf(f.get("pointPath")).replaceAll("point:", ""));
            f.put("name", point.getNm());
            f.put("pointPathOrg", point.getPth());
            f.put("facilityKind", point.getKind());
        }).collect(toList());

        log.trace("facilityData : {}", facilityData);
        return facilityData;
    }

    /**
     * 포인트 정보
     *
     * @param lpts
     * @param path
     * @return
     */
    private LogicalfolderDTO.Logicalpoints.Lpt getXmlData(List<LogicalfolderDTO.Logicalpoints.Lpt> lpts, String path) {
        AtomicReference<LogicalfolderDTO.Logicalpoints.Lpt> lpt = new AtomicReference<>(new LogicalfolderDTO.Logicalpoints.Lpt());
        lpts.stream().forEach(f -> {
            if (f.getPth().equals(path)) {
                lpt.set(f);
            }
        });

        return lpt.get();
    }

    /**
     * 시설물 그룹(누적 데이터)
     *
     * @param codeSeq
     * @return
     */
    private FacilityGroupType facilityGroup(Long codeSeq) {
        if(dataGroup == null)
            dataGroup = commonCodeService.findAllByCodeSeqIn(ACCUMULATE_DATA_GROUP);

        long dataGroupCount = dataGroup.stream().filter(f -> f.getCodeSeq() == codeSeq).count();

        log.trace("dataGroupCount {}", dataGroupCount);

        if (dataGroupCount > 0) {
            return FacilityGroupType.DATA;
        } else {
            return FacilityGroupType.CONTROL;
        }
    }
//    /**
//     * TODO facility 가져오는부분 변경 필요
//     * 시설물 장애 이벤트 ping check
//     */
//    @Scheduled(cron = "0 0 0/1 * * *")
//    public void ipPingCheck(){
//        List<FacilityOpt> facilityOptList = new ArrayList<>();
//        List<Facility> facilityList = facilityRepository.findAllByAdministZoneAndFacilityName("41210", "유동인구");
//        facilityList.stream().forEach(facility -> {
//            FacilityOpt facilityOpt = facilityOptRepository.findByFacilitySeqAndFacilityOptName(facility.getFacilitySeq(), "ip");
//            if(facilityOpt != null) {
//                facilityOptList.add(facilityOpt);
//            }
//        });
//        List<Map<String, Object>> ipLists = new ArrayList<>();
//        List<FacilityActiveLog> facilityActiveLogList = new ArrayList<>();
//        FacilityActiveLog facilityActiveLog = new FacilityActiveLog();
//        facilityOptList.stream().filter(f -> f.getFacilityOptName().equals("ip"))
//                .forEach(facilityOpt -> {
//                    Map<String,Object> maps = new HashMap<>();
//                    maps.put(facilityOpt.getFacilityOptName(),facilityOpt.getFacilityOptValue());
//                    maps.put("facilitySeq",facilityOpt.getFacilitySeq());
//                    ipLists.add(maps);
//                });
//        IpCheckedUtil.ipCheckedList(ipLists);
//
//        ipLists.stream().forEach(f -> {
//            FacilityActiveLog build = facilityActiveLog.builder().facilitySeq((Long) f.get("facilitySeq")).
//                    facilityActiveCheck((boolean) f.get("active")).facilityActiveIp((String) f.get("ip")).build();
//            facilityActiveLogList.add(build);
//
//            Facility facility = facilityRepository.findByFacilitySeq((Long) f.get("facilitySeq"));
//            if ((boolean) f.get("active")) {
//                facility.setFacilityPing(1L);
//            } else {
//                facility.setFacilityPing(0L);
//            }
//            facilityService.save(facility);
//        });
//        facilityActiveRepository.saveAll(facilityActiveLogList);
//    }

    /**
     *  TODO 광명에 맞게 변경 필요
     * 정류장 유동인구 저장
     */
//    @Scheduled(fixedDelay = 50000)
    @Scheduled(cron = "0 10 0/1 * * *")
    public void apiCallSchedule() throws Exception{
        Map<String,Object> param = new HashMap<>();
        param.put("callUrl","/mjvt/people_count");
        Map<String, Object> body = (Map<String, Object>) apiUtils.getRestCallBody(param);
        List<FacilityOpt> facilityOptList = new ArrayList<>();

        List<Map<String, Object>> data = null;

        try {
            data = objectMapper.readValue(objectMapper.writeValueAsString(body.get("data")), List.class);
            data.stream().forEach(f -> {
                String nodeId = StrUtils.getStr(f.get("nodeId"));
//                String channel = StrUtils.getStr(f.get("channel"));
                String count = StrUtils.getStr(f.get("count"));
                String facilityId = MessageFormat.format("{0}_1_1", nodeId);

                Facility facility = facilityService.findByFacilityId(facilityId);

                FacilityOpt facilityOpt = FacilityOpt.builder()
                        .facilityOptName("floating_population")
                        .facilityOptValue(count)
                        .facilityOptType(112)
                        .facilitySeq(facility.getFacilitySeq()).build();
                facilityOptList.add(facilityOpt);
            });
            facilityOptService.saveAll(facilityOptList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(cron = "0 0 0/1 * * *")
    public void findDeviceList() throws Exception {
        Map<String,Object> param = new HashMap<>();
        param.put("callUrl","/aepel/findDeviceList");
        Map<String, Object> body = (Map<String, Object>) apiUtils.getRestCallBody(param);
        List<Map<String,Object>> facilityList =(List<Map<String,Object>>) body.get("facility_list");
        facilityService.saveAllByList(facilityList);
    }

    @Scheduled(cron = "0 0 0/1 * * *")
    public void WattHourSchedule() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        String formatNow = now.format(formatter);
        int iNow = Integer.parseInt(formatNow);
        List<FacilityDataRequestDTO> list = new ArrayList<>();
        List<FacilityOpt> opts = new ArrayList<>();

        Map<String,Object> param = new HashMap<>();
        param.put("callUrl","/aepel/whInfoByDevice");
        param.put("searchTime",Integer.toString(iNow-1).substring(0,8));
        param.put("time",Integer.parseInt(formatNow.substring(formatNow.length() - 2))-1);
        Map<String, Object> body = (Map<String, Object>) apiUtils.getRestCallBody(param);
        List<Map<String,Object>> bodyList =(List<Map<String,Object>>) body.get("facility_list");
        bodyList.stream().forEach(b ->{
//            FacilityDataRequestDTO facilityDataRequestDTO = FacilityDataRequestDTO.builder()
//                                                            .facilityId(b.get("facility_id").toString())
//                                                            .facilityOptName(b.get("facility_kind").toString())
//                                                            .facilityOptValue(b.get("facility_opt_value").toString())
//                                                            .facilityOptType(112)
//                                                            .build();
//            list.add(facilityDataRequestDTO);
            String facilityId = StrUtils.getStr(b.get("facility_id"));
            Facility facility = facilityService.findByFacilityId(facilityId);
            String max = StrUtils.getStr(b.get("wh_max"));
            FacilityOpt maxOpt = FacilityOpt.builder()
                    .facilityOptName("wh_max")
                    .facilityOptType(112)
                    .facilityOptValue(max)
                    .facilitySeq(facility.getFacilitySeq()).build();
            opts.add(maxOpt);


            String reduce = StrUtils.getStr(b.get("wh_reduce"));
            FacilityOpt reduceOpt = FacilityOpt.builder()
                    .facilityOptName("wh_reduce")
                    .facilityOptType(112)
                    .facilityOptValue(reduce)
                    .facilitySeq(facility.getFacilitySeq()).build();
            opts.add(reduceOpt);

            String use = StrUtils.getStr(b.get("wh_use"));
            FacilityOpt useOpt = FacilityOpt.builder()
                    .facilityOptName("wh_use")
                    .facilityOptType(112)
                    .facilityOptValue(use)
                    .facilitySeq(facility.getFacilitySeq()).build();
            opts.add(useOpt);
        });
        facilityOptService.saveAll(opts);
//        facilityOptService.saveAllByFacilityDataRequestDTO(list);
    }

    @PostConstruct
    public void facilityScheduleInit() {
        gmSchedulerService.setScheduler();
    }
}
