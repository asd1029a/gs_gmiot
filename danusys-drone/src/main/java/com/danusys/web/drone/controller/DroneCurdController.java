package com.danusys.web.drone.controller;


import com.danusys.web.drone.model.Drone;
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
public class DroneCurdController {

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
        DroneDetails droneDetails=new DroneDetails();
        droneDetails.setStatus("임시저장");
        Drone findDrone=droneService.findDrone(drone);
        if(findDrone!=null){
            returnResult = "fail";
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(returnResult);
        }


        String result1 = droneService.saveDrone(drone);

       // DroneDetails droneDetails = droneDetailsService.saveDroneDetails(droneDetails, drone.getId());


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(drone);

    }


    @PatchMapping("/drone")
    public ResponseEntity<?> updateDrone(Drone drone) {


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(droneService.updateDrone(drone));
    }

    @DeleteMapping("/drone")
    public ResponseEntity<?> deleteDrone(Drone drone) {


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(droneService.deleteDrone(drone));
    }
    /*
        드론 디테일 post

     */


    @PostMapping("/drone")
    public ResponseEntity findAllDrone(Drone drone) {


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(droneService.findDroneList(drone));
    }


}



