package com.danusys.web.drone.service;

import com.danusys.web.drone.model.DroneLog;
import com.danusys.web.drone.repository.DroneLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class DroneLogService {

    private final DroneLogRepository droneLogRepository;

    public DroneLog saveDroneLog(DroneLog droneLog){


        return droneLogRepository.save(droneLog);
    }

    public DroneLog findById(long id){

        return droneLogRepository.findById(id).get();
    }

    public List<DroneLog> findAllDroneLog(Map<String,Object> paramMap){
        int start=0;
        int length=0;
        String deviceName=null;
        String missionName=null;
        if(paramMap.get("start")!=null)
            start=Integer.parseInt(paramMap.get("start").toString());
        if(paramMap.get("length")!=null)
            length=Integer.parseInt(paramMap.get("length").toString());
        PageRequest pageRequest = PageRequest.of(start / length, length);

        Page<DroneLog> droneLogPage =droneLogRepository.findByDroneDeviceNameLikeAndAndMissionNameLike(deviceName,missionName,pageRequest);
        List<DroneLog> droneLogList =droneLogPage.toList();
        return droneLogList;
    };
}
