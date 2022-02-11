package com.danusys.web.drone.service;


import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneDetails;
import com.danusys.web.drone.model.Mission;
import com.danusys.web.drone.repository.DroneDetailsRepository;
import com.danusys.web.drone.repository.DroneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DroneDetailsService {

    private final DroneDetailsRepository droneDetailsRepository;
    private final DroneRepository droneRepository;


    @Transactional
    public DroneDetails saveDroneDetails(DroneDetails droneDetails, Long droneId) {
        Optional<Drone> drone = droneRepository.findById(droneId);


        droneDetails.setDrone(drone.get());
        droneDetailsRepository.save(droneDetails);
        return droneDetails;
    }

    public DroneDetails findDroneDetails(Long droneId) {

        Optional<DroneDetails> droneDetailsOptional = droneDetailsRepository.findById(droneId);
        if(!droneDetailsOptional.isPresent())
            return null;
        log.info("log={}", droneDetailsOptional.get().getId());
        return (DroneDetails) droneDetailsOptional.get();
    }

    @Transactional
    public String updateDroneDetails(DroneDetails droneDetails, Long droneId) {

        DroneDetails updateDroneDetails = this.findDroneDetails(droneId);

        if(updateDroneDetails==null){
            return "fail";
        }
        if(droneDetails.getLocation()!=null){
            updateDroneDetails.setLocation(droneDetails.getLocation());
        }
        if(droneDetails.getStatus()!=null){
            updateDroneDetails.setStatus(droneDetails.getStatus());
        }
        if(droneDetails.getMasterManager()!=null){
            updateDroneDetails.setMasterManager(droneDetails.getMasterManager());
        }
        if(droneDetails.getSubManager()!=null){
            updateDroneDetails.setSubManager(droneDetails.getSubManager());
        }

            updateDroneDetails.setManufacturer(droneDetails.getManufacturer());
            updateDroneDetails.setType(droneDetails.getType());
            updateDroneDetails.setWeight(droneDetails.getWeight());
            updateDroneDetails.setMaximumOperatingDistance(droneDetails.getMaximumOperatingDistance());
            updateDroneDetails.setMaximumManagementAltitude(droneDetails.getMaximumManagementAltitude());
            updateDroneDetails.setMaximumOperatingSpeed(droneDetails.getMaximumOperatingSpeed());
            updateDroneDetails.setSimNumber(droneDetails.getSimNumber());
            updateDroneDetails.setMaximumSpeed(droneDetails.getMaximumSpeed());



        return "success";
    }


    @Transactional
    public String updateDroneDetails(Long droneId) {

        DroneDetails updateDroneDetails = this.findDroneDetails(droneId);
        Drone drone = new Drone();
        updateDroneDetails.setDrone(drone);

        return "success";
    }


}
