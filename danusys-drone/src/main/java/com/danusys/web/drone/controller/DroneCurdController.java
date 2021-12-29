package com.danusys.web.drone.controller;


import com.danusys.web.drone.model.Mission;
import com.danusys.web.drone.model.MissionDetails;
import com.danusys.web.drone.service.MissionDetailsService;
import com.danusys.web.drone.service.MissionService;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.CommandLong;
import io.dronefleet.mavlink.common.MavCmd;
import io.dronefleet.mavlink.common.MavFrame;
import io.dronefleet.mavlink.common.MissionItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/drone/api")
@Slf4j
@RequiredArgsConstructor
public class DroneCurdController {


        private final MissionService missionService;
        private final MissionDetailsService missionDetailsService;





    @PostMapping("/missiondetails")
    public ResponseEntity <?> saveMissionDetails(MissionDetails missionDetails,long missionId){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(missionDetailsService.saveMisson(missionDetails,missionId));

    }



    @PostMapping("/mission")
    public ResponseEntity <?> saveMission(Mission mission){

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(missionService.saveMission(mission));

    }





@GetMapping("/missiondetails")
public ResponseEntity<?> findMissionDetails(@RequestParam String name) {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(missionDetailsService.findMissionDetails(name));
}


    @GetMapping("/findAllMission")
    public ResponseEntity  findAllMisson() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(missionDetailsService.findAllMisson());
    }

    @GetMapping("/mission")
    public ResponseEntity<?> findMisson(String name){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(missionService.missionResponseList(name));
    }




}



