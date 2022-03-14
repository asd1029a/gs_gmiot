package com.danusys.web.drone.service;


import com.danusys.web.drone.dto.request.DroneRequest;
import com.danusys.web.drone.dto.response.DroneMissionDetailsResponse;
import com.danusys.web.drone.dto.response.DroneResponse;
import com.danusys.web.drone.dto.response.MissionDetailResponse;
import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneDetails;
import com.danusys.web.drone.repository.DroneDetailsRepository;
import com.danusys.web.drone.repository.DroneInMissionRepository;
import com.danusys.web.drone.repository.DroneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class DroneService {

    private final DroneRepository droneRepository;
    private final DroneDetailsRepository droneDetailsRepository;
    private final DroneInMissionRepository droneInMissionRepository;

    @Transactional
    public String updateDrone(Drone drone) {

        Optional optionalDrone = droneRepository.findById(drone.getId());
        if (!optionalDrone.isPresent()) {
            return "fail";
        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Drone updateDrone = (Drone) optionalDrone.get();

        if (drone.getDroneDeviceName() != null)
            updateDrone.setDroneDeviceName(drone.getDroneDeviceName());
        updateDrone.setUserId(drone.getUserId());

        updateDrone.setUpdateDt(timestamp);

        return "success";

    }


    @Transactional
    public String saveDrone(Drone drone) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        drone.setUpdateDt(timestamp);
        droneRepository.save(drone);
        return "success";
    }

    @Transactional
    public String deleteDrone(Drone drone) {
        DroneDetails droneDetails = droneDetailsRepository.findByDrone(drone);
        if (droneDetails == null) {
            return "fail";
        }


        droneDetailsRepository.deleteByDrone(drone);
        droneRepository.deleteById(drone.getId());
        droneInMissionRepository.deleteDroneInMissionbySeq(drone.getId());

        return "success";
    }
    @Transactional
    public List<?> findDroneList(DroneRequest droneRequest) {
        log.info("droneRequest={}",droneRequest);
        List<Drone> droneList = null;
        Sort sort = sortByupdateDt();

        String droneStatus = null;
        if (droneRequest.getDroneStatus() != null) {
            if (droneRequest.getDroneStatus().equals("all")) {
                droneStatus = "대기중";
            } else if (droneRequest.getDroneStatus().equals("wait")) {
                droneStatus = "대기중";
            } else if (droneRequest.getDroneStatus().equals("run")) {
                droneStatus = "운영중";
            }
            if (droneRequest.getUserId() != null) {
                log.info("id로검색");

                droneList = droneRepository.findAllByUserIdLikeAndStatus("%" + droneRequest.getUserId() + "%", droneStatus, sort);

              droneList.forEach(r->{
                    log.info("droneId={},{},{},{}",r.getUserId(),r.getDroneDeviceName(),
                        r.getId(),   r.getStatus());

                });
            } else if (droneRequest.getDroneDeviceName() != null) {
                log.info("devicename으로검색");
                droneList = droneRepository.findAllByDroneDeviceNameLikeAndStatus("%" + droneRequest.getDroneDeviceName() + "%",
                        droneStatus, sort);
            }

        } else {
            if (droneRequest.getUserId() != null) {
                log.info("id로검색");
                droneList = droneRepository.findAllByUserIdLike("%" + droneRequest.getUserId() + "%", sort);

            } else if (droneRequest.getDroneDeviceName() != null) {
                log.info("devicename으로검색");
                droneList = droneRepository.findAllByDroneDeviceNameLike("%" + droneRequest.getDroneDeviceName() + "%", sort);
            }
        }


       // log.info("droneStatus={}", droneStatus);

        log.info("여긴오나?");
        return droneList.stream().map(DroneResponse::new).collect(Collectors.toList());
        //return droneList;
    }

    private Sort sortByupdateDt() {
        return Sort.by(Sort.Direction.DESC, "updateDt");
    }


    public Drone findDrone(Drone drone) {
        //log.info("droneName={}", drone.getDroneDeviceName());
        if (drone.getDroneDeviceName() != null) {
            return droneRepository.findByDroneDeviceName(drone.getDroneDeviceName());
        } else {
            return null;
        }
    }

    public DroneResponse findOneDrone(long droneId) {
        Optional<Drone> optionalDrone = droneRepository.findById(droneId);
        if (!optionalDrone.isPresent())
            return null;
        Drone drone =optionalDrone.get();
        DroneResponse droneResponse=new DroneResponse(drone);
        return droneResponse;
    }

    public List<Drone> findAllDrone() {

        return droneRepository.findAllByIdNot(0l);
    }


}
