package com.danusys.web.platform.controller;

import com.danusys.web.commons.api.model.FacilityOpt;
import com.danusys.web.commons.api.service.ApiCallService;
import com.danusys.web.commons.api.service.FacilityOptService;
import com.danusys.web.commons.app.*;
import com.danusys.web.platform.dto.request.SignageRequestDto;
import com.danusys.web.platform.service.facility.FacilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequestMapping(value="/facility")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    private final ApiCallService apiCallService;

    private final FacilityOptService facilityOptService;

    @Value("${danusys.area.code.sig}")
    private String sigCode;

    /**
     * 시설물 : 시설물 목록 조회
     */
    @PostMapping
    public ResponseEntity<EgovMap> getList(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.getList(paramMap));
    }
    
    /**
     * 시설물 : 시설물 목록 조회 페이징
     */
    @PostMapping(value="/paging")
    public ResponseEntity<EgovMap> getListPaging(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.getListPaging(paramMap));
    }

    /**
     * 시설물 : 시설물 GEOJSON 목록 조회
     */
    @PostMapping(value="/geojson")
    public String getListFacilityGeoJson(@RequestBody Map<String, Object> paramMap) throws Exception {
        EgovMap resultEgov = facilityService.getList(paramMap);
        List<Map<String, Object>> list = (List<Map<String, Object>>) resultEgov.get("data");

        list.stream().peek(p -> {
            log.trace("facilitySeq > {}", p.get("facilitySeq"));
            p.put("facilityOpts", facilityOptService.findByFacilitySeqLast(Long.parseLong(String.valueOf(p.get("facilitySeq")))));
        }).collect(toList());

        return GisUtil.getGeoJson(list, "facility");
    }

    /**
     * CCTV : CCTV GEOJSON 목록 조회
     */
    @PostMapping(value="/cctv/geojson")
    public String getListCctvGeoJson(@RequestBody Map<String, Object> paramMap) throws Exception {
        EgovMap resultEgov = new EgovMap();

        paramMap.put("administZone", sigCode);

        String type = CommonUtil.validOneNull(paramMap, "type");

        System.out.println("&&&&&&&&&&&&&&&&&");
        System.out.println(paramMap);

        if(type.equals("head")) {
            //레이어 //투망감시
            resultEgov = facilityService.getListCctvHead(paramMap);
        } else {
            //개소감시
            resultEgov = facilityService.getListCctv(paramMap);
        }
        List<Map<String, Object>> list = (List<Map<String, Object>>) resultEgov.get("data");

        list.stream().peek(p -> {
            log.trace("facilitySeq > {}", p.get("facilitySeq"));
            p.put("facilityOpts", facilityOptService.findByFacilitySeqLast(Long.parseLong(String.valueOf(p.get("facilitySeq")))));
        }).collect(toList());

        return GisUtil.getGeoJson(list, "cctv");
    }


    /**
     * 시설물 : 시설물 단건 조회
     */
    @GetMapping(value="/{facilitySeq}")
    public ResponseEntity<EgovMap> getFacility(@PathVariable("facilitySeq") int facilitySeq) throws Exception {
        return ResponseEntity.ok().body(facilityService.getOne(facilitySeq));
    }

    /**
     * 시설물 : 시설물 등록
     */
    @PutMapping
    public ResponseEntity<?> add(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.add(paramMap));
    }

    /**
     * 시설물 : 시설물 기능 등록
     */
    @PutMapping(value="/opt")
    public ResponseEntity<?> addOpt(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.addOpt(paramMap));
    }

    /**
     * 시설물 : 시설물 수정
     */
    @PatchMapping
    public ResponseEntity<?> mod(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.mod(paramMap));
    }

    /**
     * 시설물 : 시설물 기능 수정
     */
    @PatchMapping(value="/opt")
    public ResponseEntity<?> modOpt(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.modOpt(paramMap));
    }

    /**
     * 시설물 : 시설물 삭제
     */
    @DeleteMapping(value="/{facilitySeq}")
    public ResponseEntity<?> del (@PathVariable("facilitySeq") int facilitySeq) throws Exception {
        facilityService.del(facilitySeq);
        return ResponseEntity.noContent().build();
    }

    /**
     * 시설물 : 시설물 삭제
     */
    @DeleteMapping(value="/opt")
    public ResponseEntity<?> del (@RequestBody Map<String, Object> paramMap) throws Exception {
        facilityService.delOpt(paramMap);
        return ResponseEntity.noContent().build();
    }

    /**
     * 시설물 : 개소 소속 시설물 조회
     */
    @PostMapping(value="/inStation")
    public ResponseEntity<EgovMap> getListFacilityInStation(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.getListFacilityInStation(paramMap));
    }

    /**
     * 시설물 : 디밍 그룹 조회
     */
    @PostMapping(value="/dimmingGroup")
    public ResponseEntity<EgovMap> getListDimmingGroup(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.getListDimmingGroup(paramMap));
    }

    /**
     * 시설물 : 디밍 그룹 조회
     */
    @GetMapping(value="/lastDimmingGroupSeq")
    public ResponseEntity<EgovMap> getLastDimmingGroupSeq() throws Exception {
        return ResponseEntity.ok().body(facilityService.getLastDimmingGroupSeq());
    }

    /**
     * 시설물 : 디밍 그룹 소속 시설물 조회
     */
    @PostMapping(value="/lampRoadInGroup")
    public ResponseEntity<EgovMap> getListLampRoadInDimmingGroup(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.getListLampRoadInDimmingGroup(paramMap));
    }

    /**
     * 시설물 : 사이니지 템플릿 목록 조회
     */
    @PostMapping(value="/signage/template")
    public ResponseEntity<EgovMap> getListSignageTemplate(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.getListSignageTemplate(paramMap));
    }

    /**
     * 시설물 : 사이니지 템플릿 등록
     */
    @PutMapping(value="/signage/template")
    public ResponseEntity<?> addSignageTemplate(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.addSignageTemplate(paramMap));
    }

    /**
     * 시설물 : 사이니지 템플릿 등록
     */
    @PatchMapping(value="/signage/template")
    public ResponseEntity<?> modSignageTemplate(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.modSignageTemplate(paramMap));
    }

    /**
     * 시설물 : 사이니지 레이아웃 등록
     */
    @PostMapping(value="/signage/layout", produces = "multipart/form-data")
    public ResponseEntity<?> modSignageLayout(MultipartFile[] imageFile, MultipartFile[] videoFile,
            HttpServletRequest request, SignageRequestDto signageRequestDto) throws Exception {
        facilityService.modSignageLayout(imageFile, videoFile, request, signageRequestDto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 시설물 : 사이니지 레이아웃 적용 (광명)
     */
    @PutMapping(value="/signage/layoutForGm")
    public ResponseEntity<?> modSignageLayoutForGm(@RequestBody Map<String, Object> paramMap) throws Exception {
        facilityService.modSignageLayoutForGm(paramMap);
        return ResponseEntity.noContent().build();
    }

    /**
     * 시설물 : 다중 이미지 업로드
     */
    @ResponseBody
    @PostMapping(value="/signage/uploadImageList")
    public ResponseEntity<EgovMap> uploadImageList(@RequestParam("files") List<MultipartFile> fileList, HttpServletRequest request) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("fileNames", FileUtil.uploadMulitAjaxPost(fileList, request));
        return ResponseEntity.ok().body(resultMap);
    }

    /**
     * 시설물 : 광명 사이니지 단건 조회(외부업체 호출용)
     */
    @GetMapping(value="/signage/getData")
    public ResponseEntity<String> getSignageData(HttpServletRequest request) throws Exception {
        String serverName = request.getServerName();
        String serverPort = StrUtils.getStr(request.getServerPort());
        return ResponseEntity.ok().body(facilityService.getOneSignageData(serverName, serverPort));
    }

    /**
     * 시설물 : 광명 사이니지 이미지 다운로드
     */
    @GetMapping(value="/signage/downloadImage/{imageFileName:.+}") //글 리스트 페이지
    public void fileDown(@PathVariable("imageFileName") String imageFileName, HttpServletResponse response) throws Exception {
        FileUtil.fileDownloadWithFilePath(response, imageFileName, "/pages/config/signage/");
    }

    /**
     * 시설물 : 사이니지 템플릿 삭제
     */
    @DeleteMapping(value="/signage/template")
    public ResponseEntity<?> delSignageTemplate(@RequestBody Map<String, Object> paramMap) throws Exception {
        facilityService.delSignageTemplate(paramMap);
        return ResponseEntity.noContent().build();
    }

    /**
     * 시설물 : 시설물 조회 엑셀 다운로드
     */
    @ResponseBody
    @PostMapping(value = "/excel/download")
    public void exportNotice(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
        EgovMap dataMap = facilityService.getList((Map<String, Object>) paramMap.get("search"));

        paramMap.put("dataMap", dataMap.get("data"));
        Workbook wb = FileUtil.excelDownload(paramMap);
        wb.write(response.getOutputStream());
        wb.close();
    }

    /**
     * 시설물 : 개별 제어
     * @param paramMap
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/control")
    public ResponseEntity control(@RequestBody Map<String, Object> paramMap) throws Exception {
        ResponseEntity result = apiCallService.call(paramMap);

        //시설물 상태 업데이트
        if( result.getStatusCode() == HttpStatus.OK ) {
            Map<String, Object> modMap = new HashMap<>();
            modMap.put("facilitySeq", paramMap.get("facilitySeq"));
            modMap.put("facilityStatus", String.valueOf(paramMap.get("settingValue")).equals("On") ? 1 : 0);
            facilityService.mod(modMap);

            FacilityOpt facilityOpt = facilityOptService.findByFacilitySeqAndFacilityOptName(Long.parseLong(String.valueOf(paramMap.get("facilitySeq"))), "power");
            facilityOpt.setFacilityOptValue(String.valueOf(paramMap.get("settingValue")).equals("On") ? "true" : "false");
            facilityOptService.save(facilityOpt);
        }

        return result;
    }
}
