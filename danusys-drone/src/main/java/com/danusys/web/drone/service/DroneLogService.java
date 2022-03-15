package com.danusys.web.drone.service;

import com.danusys.web.drone.dto.response.DroneLogResponse;
import com.danusys.web.drone.dto.response.DroneLogWithOutDetailsResponse;
import com.danusys.web.drone.model.DroneLog;
import com.danusys.web.drone.repository.DroneLogRepository;
import com.danusys.web.drone.utils.PagingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class DroneLogService {

    private final DroneLogRepository droneLogRepository;

    public DroneLog saveDroneLog(DroneLog droneLog) {

        return droneLogRepository.save(droneLog);
    }

    public DroneLog findById(long id) {

        return droneLogRepository.findById(id).get();
    }

    public Map<String, Object> findAllDroneLog(Map<String, Object> paramMap) {
        log.info("paramMap={}", paramMap);
        int start = 0;
        int length = 1;
        int count = 1;
        int pages = 0;
        int pageGroupCount = 3;
        int searchType = 0; //1 -> all(db 조회 or )  0 ->  db조회 (and)
        String deviceName = "";
        String missionName = "";
        Date beforeDate = Date.valueOf("2000-01-01");
        Date afterDate = Date.valueOf("9999-12-31");
        //Timestamp beforeDate = new Timestamp(Date.valueOf("2000-01-01").getTime());
        //Timestamp afterDate = new Timestamp(Date.valueOf("9999-12-31").getTime());
        boolean needAll = false;


        if (paramMap.get("start") != null) {
            start = Integer.parseInt(paramMap.get("start").toString());
            if (start == -1)
                start = 0;
        }

        if (paramMap.get("length") != null)
            length = Integer.parseInt(paramMap.get("length").toString());


        if (paramMap.get("deviceName") != null)
            deviceName = paramMap.get("deviceName").toString();
        if (paramMap.get("missionName") != null)
            missionName = paramMap.get("missionName").toString();

        if (paramMap.get("searchType") != null)
            searchType = Integer.parseInt(paramMap.get("searchType").toString());

        if (paramMap.get("beforeDate") != null)
            if (!paramMap.get("beforeDate").equals(""))
//                beforeDate = new Timestamp(Date.valueOf(paramMap.get("beforeDate").toString()).getTime());
                beforeDate = Date.valueOf(paramMap.get("beforeDate").toString());
        if (paramMap.get("afterDate") != null)
            if (!paramMap.get("afterDate").equals(""))
//                afterDate = new Timestamp(Date.valueOf(paramMap.get("afterDate").toString()).getTime());
                afterDate = Date.valueOf(paramMap.get("afterDate").toString());


        log.info("beforeDate=" + beforeDate + "afterDate= " + afterDate);
        log.info("searchType={}",searchType);
        Page<DroneLog> droneLogPage = null;
        List<DroneLog> droneLogList = null;
        List<DroneLogResponse> droneLogResponseList = null;
        List<DroneLogWithOutDetailsResponse> droneLogWithOutDetailsResponseList = null;

        if (length == 1) {
            if (searchType == 0) {
                droneLogList = droneLogRepository.findByInsertDtBetweenAndDroneDeviceNameIgnoreCaseLikeAndMissionNameIgnoreCaseLike(
                        beforeDate, afterDate, "%" + deviceName + "%", "%" + missionName + "%", Sort.by(Sort.Direction.DESC, "insertDt")
                );
            } else if (searchType == 1) {
                droneLogList = droneLogRepository.findByInsertDtBetweenAndDroneDeviceNameIgnoreCaseLikeOrInsertDtBetweenAndMissionNameIgnoreCaseLike(
                        beforeDate, afterDate, "%" + deviceName + "%", beforeDate, afterDate, "%" + deviceName + "%"
                        , Sort.by(Sort.Direction.DESC, "insertDt"));
            }
            droneLogWithOutDetailsResponseList =droneLogList.stream().map(DroneLogWithOutDetailsResponse::new).collect(Collectors.toList());
        } else {
            if (searchType == 0) {
                PageRequest pageRequest = PageRequest.of(start, length, Sort.by(Sort.Direction.DESC, "insertDt"));
                droneLogPage = droneLogRepository.findByInsertDtBetweenAndDroneDeviceNameIgnoreCaseLikeAndMissionNameIgnoreCaseLike(
                        beforeDate, afterDate, "%" + deviceName + "%", "%" + missionName + "%", pageRequest);
                count = (int) droneLogPage.getTotalElements();
                droneLogList = droneLogPage.toList();
            } else if (searchType == 1) {

                log.info("여기??1");
                PageRequest pageRequest = PageRequest.of(start, length, Sort.by(Sort.Direction.DESC, "insertDt"));
                droneLogPage = droneLogRepository.findByInsertDtBetweenAndDroneDeviceNameIgnoreCaseLikeOrInsertDtBetweenAndMissionNameIgnoreCaseLike(
                        beforeDate, afterDate, "%" + deviceName + "%", beforeDate, afterDate, "%" + deviceName + "%", pageRequest); //droneDevice로 or 검색함
                log.info("여기??2");
                count = (int) droneLogPage.getTotalElements();
                droneLogList = droneLogPage.toList();
            }
            // droneLogResponseList2=droneLogList.stream().map(DroneLogResponse::new).collect(Collectors.toList());
            droneLogResponseList = droneLogList.stream().map(DroneLogResponse::new).collect(Collectors.toList());
        }


        //  Map<String, Object> resultMap = new HashMap<String, Object>();
        Map<String, Object> pagingMap = new HashMap<>();
        try {

            //  int lastPage = count / length + 1;
            int lastPage = (int) Math.ceil(count * 1.0 / length);
            if (length == 1) {          //전체 데이터 엑셀 다운로드 용
                pagingMap.put("data", droneLogWithOutDetailsResponseList); // 페이징 + 검색조건 결과
            } else {
                pagingMap.put("data", droneLogResponseList); // 페이징 + 검색조건 결과
            }

            pagingMap.put("count", count); // 검색조건이 반영된 총 카운트
            pagingMap.put("pages", lastPage);
            pagingMap.put("pageGroupCount", pageGroupCount);
//            int lastPage =  (count / length / 3) + 1;

            int startPage = (start / pageGroupCount) * pageGroupCount + 1;
            int endPage = startPage + pageGroupCount - 1;
            //  pagingMap.put("lastPage", lastPage);
            if (endPage >= lastPage)
                endPage = lastPage;
            pagingMap.put("startPage", startPage);
            pagingMap.put("endPage", endPage);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return pagingMap;
    }




}
