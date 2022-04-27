package com.danusys.web.drone.controller;


import com.danusys.web.drone.service.ConnectionService;
import com.danusys.web.drone.service.DroneSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/drone/api")
@Slf4j
@RequiredArgsConstructor
public class SocketDroneController {


    private final ConnectionService connectionService;
    private final DroneSocketService droneSocketService;
    int currentIndex = -1;

    @RequestMapping("/droneSocket")
    public ResponseEntity<?> getSocketList() {
        Map<Integer, Map<String, Object>> listMap = new HashMap<>();
        listMap = connectionService.getSocketList();
        log.info("listMap1={}",listMap);
        log.info("connectionMap={}",connectionService.getConnectionMap());
        return ResponseEntity.ok(listMap);
    }

    @PostMapping("/socket")
    public ResponseEntity<?> getAllSocketList(){

        return ResponseEntity.ok(droneSocketService.getList());
    }

    @PutMapping("/socket")
    public ResponseEntity<?> saveSocketList() {
        Map<Integer, Map<String, Object>> listMap = new HashMap<>();
        droneSocketService.delete();
        listMap=connectionService.getSocketList();

        log.info("listMap2={}",listMap);

        return null;

    }


}
