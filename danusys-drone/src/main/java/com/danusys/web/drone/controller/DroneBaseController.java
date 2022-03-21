package com.danusys.web.drone.controller;


import com.danusys.web.drone.service.DroneBaseService;
import com.danusys.web.drone.service.DroneLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/drone/api")
@Slf4j
@RequiredArgsConstructor
public class DroneBaseController {


    private final DroneBaseService droneBaseService;



    @PostMapping("/dronebase")
    public ResponseEntity<?> findAllDroneBase(@RequestBody Map<String,Object> paramMap){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(droneBaseService.findAllDroneBase());
    }



}



