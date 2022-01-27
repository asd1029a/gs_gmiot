package com.danusys.web.drone.service;

import com.danusys.web.drone.model.DroneLog;
import com.danusys.web.drone.model.DroneLogDetails;
import com.danusys.web.drone.repository.DroneLogDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DroneLogDetailsService {

    private final DroneLogDetailsRepository droneLogDetailsRepository;

    public DroneLogDetails saveDroneLogDetails(DroneLogDetails droneLogDetails){


        return droneLogDetailsRepository.save(droneLogDetails);
    }


}
