package com.danusys.web.drone.service;

import com.danusys.web.drone.model.DroneLog;
import com.danusys.web.drone.repository.DroneLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
