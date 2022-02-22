package com.danusys.web.drone.service;

import com.danusys.web.drone.model.DroneLog;
import com.danusys.web.drone.repository.DroneLogRepository;
import com.danusys.web.drone.utils.PagingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String deviceName = "";
        String missionName = "";
        if (paramMap.get("start") != null){
            start = Integer.parseInt(paramMap.get("start").toString());
            if(start ==-1)
                start=0;

        }

        if (paramMap.get("length") != null)
            length = Integer.parseInt(paramMap.get("length").toString());

        if (paramMap.get("deviceName") != null)
            deviceName = paramMap.get("deviceName").toString();
        if (paramMap.get("missionName") != null)
            missionName = paramMap.get("missionName").toString();

        PageRequest pageRequest = PageRequest.of(start , length);

        Page<DroneLog> droneLogPage = droneLogRepository.findByDroneDeviceNameIgnoreCaseLikeAndAndMissionNameIgnoreCaseLike(
                "%" + deviceName + "%", "%" + missionName + "%", pageRequest);
        count = (int) droneLogPage.getTotalElements();
        List<DroneLog> droneLogList = droneLogPage.toList();

        //  Map<String, Object> resultMap = new HashMap<String, Object>();
        Map<String, Object> pagingMap = new HashMap<>();
        try {


            pagingMap.put("data", droneLogList); // 페이징 + 검색조건 결과
            pagingMap.put("count", count); // 검색조건이 반영된 총 카운트
            pagingMap.put("pages", count / length + 1);
            pagingMap.put("pageGroupCount", pageGroupCount);
            int lastPage =  (count / 3) + 1;

            int startPage = (start/ pageGroupCount) * pageGroupCount + 1;
            int endPage = startPage + pageGroupCount - 1;
            pagingMap.put("lastPage", lastPage);
            pagingMap.put("startPage", startPage);
            pagingMap.put("endPage", endPage);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return pagingMap;
    }

    ;
}
