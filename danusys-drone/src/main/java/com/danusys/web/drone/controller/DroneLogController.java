package com.danusys.web.drone.controller;


import com.danusys.web.drone.model.DroneLog;
import com.danusys.web.drone.model.DroneLogDetails;
import com.danusys.web.drone.model.Mission;
import com.danusys.web.drone.model.MissionDetails;
import com.danusys.web.drone.service.DroneLogDetailsService;
import com.danusys.web.drone.service.DroneLogService;
import com.danusys.web.drone.service.MissionDetailsService;
import com.danusys.web.drone.service.MissionService;
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
public class DroneLogController {


    private final DroneLogService droneLogService;
    private final DroneLogDetailsService droneLogDetailsService;


    @PostMapping("/logtest")
    public ResponseEntity<?> saveMissionDetails(DroneLog droneLog) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(droneLogService.saveDroneLog(droneLog));

    }

    @PostMapping("/dronelog")
    public ResponseEntity<?> findAllDroneLog(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(droneLogService.findAllDroneLog());
    }


//
//    @PostMapping("/logtest2")
//    public ResponseEntity<?> logtest2() {
//
//        DroneLogDetails
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(droneLogDetailsService.saveDroneLogDetails(droneLog));
//
//    }

}



