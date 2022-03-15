package com.danusys.web.drone.controller;


import com.danusys.web.drone.dto.request.DroneDetailRequest;
import com.danusys.web.drone.dto.request.DroneRequest;
import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneBase;
import com.danusys.web.drone.model.DroneDetails;
import com.danusys.web.drone.service.DroneDetailsService;
import com.danusys.web.drone.service.DroneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/drone/api")
@Slf4j
@RequiredArgsConstructor
public class  DroneCurdController {

    private final DroneService droneService;
    private final DroneDetailsService droneDetailsService;

    /*
        saveDrone
        parameter:
                    String droneId;
                    String droneDeviceName;
     */

    @PutMapping("/drone")
    public ResponseEntity<?> saveDrone(@RequestBody Drone drone) {
        String returnResult = null;
        DroneDetails droneDetails = new DroneDetails();
//        droneDetails.setStatus("임시저장");
        Drone findDrone = droneService.findDrone(drone);
        if (findDrone != null) {
            returnResult = "fail";
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(returnResult);
        }
        DroneBase droneBase=new DroneBase();
        droneBase.setId(1l);
        drone.setDroneBase(droneBase);
        drone.setStatus("임시저장");

        droneService.saveDrone(drone);
        DroneDetails saveDroneDetails = droneDetailsService.saveDroneDetails(droneDetails, drone.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saveDroneDetails);
    }


    @PatchMapping("/drone")
    public ResponseEntity<?> updateDrone(Drone drone) {




        return ResponseEntity
                .status(HttpStatus.OK)
                .body(droneService.updateDrone(drone));
    }

    @DeleteMapping("/drone")
    public ResponseEntity<?> deleteDrone(@RequestBody Drone drone) {


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(droneService.deleteDrone(drone));
    }
    /*
        드론 디테일 post

     */


    @PostMapping("/drone")
    public ResponseEntity<?> findAllDrone(@RequestBody DroneRequest droneRequest) {

        log.info("here");
        List<?> droneList=droneService.findDroneList(droneRequest);
                log.info("여기도오나요??");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(droneList);
    }

    @PatchMapping("/dronedetails")
    public ResponseEntity<?> updateDroneDetails(@RequestBody DroneDetailRequest droneDetailRequest){
        log.info("paramMap={},{}",droneDetailRequest,droneDetailRequest.getDroneDetails());
            long droneId = droneDetailRequest.getDroneId();
            DroneDetails droneDetails=droneDetailRequest.getDroneDetails();
          //  log.info("droneId={}",droneId);

            long droneBaseId = droneDetailRequest.getDroneBase();
         //   log.info("droneBaseId={}",droneBaseId);



            long droneMissionId =droneDetailRequest.getDroneMission();
            String droneStatus = droneDetailRequest.getDroneStatus();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(droneDetailsService.updateDroneDetails(droneDetails,droneId,droneBaseId,droneMissionId,droneStatus));

    }

    @GetMapping("/drone/{droneId}")
    public ResponseEntity<?> findOneDrone(@PathVariable long droneId){

        // input 이 drone_id라면
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(droneService.findOneDrone(droneId));
    }






}



