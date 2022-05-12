package com.danusys.web.drone.controller;


import com.danusys.web.commons.api.service.SseService;
import com.danusys.web.drone.service.DroneBaseService;
import com.danusys.web.drone.service.DroneLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/drone/api")
@Slf4j
@RequiredArgsConstructor
public class DroneBaseController {


    private final DroneBaseService droneBaseService;
    private final SseService sseService;

    @PostMapping("/dronebase")
    public ResponseEntity<?> findAllDroneBase(@RequestBody Map<String, Object> paramMap) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(droneBaseService.findAllDroneBase());
    }

    @GetMapping("/test")
    public void test() {
        Map<String, Object> map = new HashMap<>();
        map.put("testKey","testValue");
        sseService.send(map);
    }

}



