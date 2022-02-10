package com.danusys.web.drone.service;


import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneDetails;
import com.danusys.web.drone.repository.DroneDetailsRepository;
import com.danusys.web.drone.repository.DroneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class DroneService {

    private final DroneRepository droneRepository;
    private final DroneDetailsRepository droneDetailsRepository;


    @Transactional
    public String updateDrone(Drone drone) {

        Optional optionalDrone = droneRepository.findById(drone.getId());
        if(!optionalDrone.isPresent()){
            return "fail";
        }
        Date date = new Date();
        Drone updateDrone = (Drone) optionalDrone.get();

        updateDrone.setDroneDeviceName(drone.getDroneDeviceName());
        updateDrone.setUserId(drone.getUserId());

        updateDrone.setUpdateDt(date);

        return "success";

    }




    @Transactional
    public String saveDrone(Drone drone) {
        droneRepository.save(drone);
        return "success";
    }


    public String deleteDrone(Drone drone) {
        DroneDetails droneDetails = droneDetailsRepository.findByDrone(drone);
        if(droneDetails==null) {
            return "fail";
        }
        droneDetailsRepository.deleteByDrone(drone);
        droneRepository.deleteById(drone.getId());

        return "success";
    }

    public List<Drone> findDroneList(Drone drone) {

        List<Drone> droneList = null;
        if (drone.getId() != null) {
            log.info("id로검색");
            droneList = droneRepository.findAllById(drone.getId());

        } else if (drone.getDroneDeviceName()!=null) {
            log.info("devicename으로검색");
            droneList = droneRepository.findAllByDroneDeviceNameLike("%"+drone.getDroneDeviceName()+"%");
        }

        return droneList;
    }


    public Drone findDrone(Drone drone) {
        log.info("droneName={}",drone.getDroneDeviceName());
        if(drone.getDroneDeviceName()!=null){
            return droneRepository.findByDroneDeviceName(drone.getDroneDeviceName());
        }else{
            return null;
        }


    }
}
