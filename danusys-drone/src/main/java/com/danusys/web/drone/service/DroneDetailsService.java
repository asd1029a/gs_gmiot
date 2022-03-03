package com.danusys.web.drone.service;


import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneBase;
import com.danusys.web.drone.model.DroneDetails;
import com.danusys.web.drone.model.Mission;
import com.danusys.web.drone.repository.DroneBaseRepository;
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
    private final DroneBaseRepository droneBaseRepository;

    @Transactional
    public DroneDetails saveDroneDetails(DroneDetails droneDetails, Long droneId) {
        Optional<Drone> drone = droneRepository.findById(droneId);

        droneDetails.setDrone(drone.get());
        droneDetailsRepository.save(droneDetails);
        return droneDetails;
    }

    public DroneDetails findDroneDetails(Long droneId) {


        Drone setDrone = new Drone();
        setDrone.setId(droneId);
        DroneDetails droneDetails = droneDetailsRepository.findByDrone(setDrone);

        // log.info("droneDetailsId={}", droneDetails.getId());
        return droneDetails;
    }

    @Transactional
    public String updateDroneDetails(DroneDetails droneDetails, Long droneId) {

        Optional<Drone> optionalDrone = droneRepository.findById(droneId);
        Drone updateDrone = null;
        if (!optionalDrone.isPresent())
            return null;
        else
            updateDrone = optionalDrone.get();
//        DroneBase droneBase=new DroneBase();
//        updateDrone.setDroneBase(droneBase);
        DroneDetails updateDroneDetails = this.findDroneDetails(droneId);

        if (updateDroneDetails == null) {
            return "fail";
        }
        if (droneDetails.getLocation() != null) {
            updateDroneDetails.setLocation(droneDetails.getLocation());
        }
        if (droneDetails.getStatus() != null) {
            updateDroneDetails.setStatus(droneDetails.getStatus());
        }
        if (droneDetails.getMasterManager() != null) {
            updateDroneDetails.setMasterManager(droneDetails.getMasterManager());
        }
        if (droneDetails.getSubManager() != null) {
            updateDroneDetails.setSubManager(droneDetails.getSubManager());
        }
        if (droneDetails.getMasterManager() != null) {
            updateDroneDetails.setManufacturer(droneDetails.getManufacturer());
        }
        if (droneDetails.getType() != null) {
            updateDroneDetails.setType(droneDetails.getType());
        }
        if (droneDetails.getWeight() != 0) {
            updateDroneDetails.setWeight(droneDetails.getWeight());
        }
        if (droneDetails.getMaximumOperatingDistance() != 0) {
            updateDroneDetails.setMaximumOperatingDistance(droneDetails.getMaximumOperatingDistance());
        }
        if (droneDetails.getMaximumManagementAltitude() != 0) {
            updateDroneDetails.setMaximumManagementAltitude(droneDetails.getMaximumManagementAltitude());
        }
        if (droneDetails.getMaximumOperatingSpeed() != 0) {
            updateDroneDetails.setMaximumOperatingSpeed(droneDetails.getMaximumOperatingSpeed());
        }
        if (droneDetails.getSimNumber() != null) {
            updateDroneDetails.setSimNumber(droneDetails.getSimNumber());
        }
        if (droneDetails.getMaximumSpeed() != 0) {
            updateDroneDetails.setMaximumSpeed(droneDetails.getMaximumSpeed());
        }
        if (droneDetails.getManufacturer() != null) {
            updateDroneDetails.setManufacturer(droneDetails.getManufacturer());
        }
        if (droneDetails.getThumbnailImg() != null) {
            updateDroneDetails.setThumbnailImg(droneDetails.getThumbnailImg());
        }


        return "success";
    }

    @Transactional
    public String updateDroneDetails(DroneDetails droneDetails, Long droneId,Long droneBaseId) {

        Optional<Drone> optionalDrone = droneRepository.findById(droneId);
        Drone updateDrone = null;
        if (!optionalDrone.isPresent())
            return null;
        else
            updateDrone = optionalDrone.get();
        Optional<DroneBase> optionalDroneBase=droneBaseRepository.findById(droneBaseId);
        DroneBase droneBase=null;
        if(!optionalDroneBase.isPresent())
            return null;
        else
            droneBase=optionalDroneBase.get();
        updateDrone.setDroneBase(droneBase);
        DroneDetails updateDroneDetails = this.findDroneDetails(droneId);

        if (updateDroneDetails == null) {
            return "fail";
        }
        if (droneDetails.getLocation() != null) {
            updateDroneDetails.setLocation(droneDetails.getLocation());
        }
        if (droneDetails.getStatus() != null) {
            updateDroneDetails.setStatus(droneDetails.getStatus());
        }
        if (droneDetails.getMasterManager() != null) {
            updateDroneDetails.setMasterManager(droneDetails.getMasterManager());
        }
        if (droneDetails.getSubManager() != null) {
            updateDroneDetails.setSubManager(droneDetails.getSubManager());
        }
        if (droneDetails.getMasterManager() != null) {
            updateDroneDetails.setManufacturer(droneDetails.getManufacturer());
        }
        if (droneDetails.getType() != null) {
            updateDroneDetails.setType(droneDetails.getType());
        }
        if (droneDetails.getWeight() != 0) {
            updateDroneDetails.setWeight(droneDetails.getWeight());
        }
        if (droneDetails.getMaximumOperatingDistance() != 0) {
            updateDroneDetails.setMaximumOperatingDistance(droneDetails.getMaximumOperatingDistance());
        }
        if (droneDetails.getMaximumManagementAltitude() != 0) {
            updateDroneDetails.setMaximumManagementAltitude(droneDetails.getMaximumManagementAltitude());
        }
        if (droneDetails.getMaximumOperatingSpeed() != 0) {
            updateDroneDetails.setMaximumOperatingSpeed(droneDetails.getMaximumOperatingSpeed());
        }
        if (droneDetails.getSimNumber() != null) {
            updateDroneDetails.setSimNumber(droneDetails.getSimNumber());
        }
        if (droneDetails.getMaximumSpeed() != 0) {
            updateDroneDetails.setMaximumSpeed(droneDetails.getMaximumSpeed());
        }
        if (droneDetails.getManufacturer() != null) {
            updateDroneDetails.setManufacturer(droneDetails.getManufacturer());
        }
        if (droneDetails.getThumbnailImg() != null) {
            updateDroneDetails.setThumbnailImg(droneDetails.getThumbnailImg());
        }


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
