package com.danusys.web.drone.controller;


import com.danusys.web.drone.model.Mission;
import com.danusys.web.drone.model.MissionDetails;
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
public class MissionCurdController {


    private final MissionService missionService;
    private final MissionDetailsService missionDetailsService;


    @PutMapping("/missiondetails")
    public ResponseEntity<?> saveMissionDetails(@RequestBody Map<String, Object> missionList) {
        log.info("missionId={}", missionList);


        List<MissionDetails> missionDetailsList = new ArrayList<>();
        missionDetailsList = (List<MissionDetails>) missionList.get("missionList");

        int missionId = 0;
        missionId = (int) missionList.get("missionId");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(missionDetailsService.saveMission(missionDetailsList, missionId));

    }


    @PutMapping("/mission")
    public ResponseEntity<?> saveMission(@RequestBody Mission mission) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(missionService.saveMission(mission));

    }


    @GetMapping("/missiondetails/{name}")
    public ResponseEntity<?> findMissionDetails(@PathVariable final String name) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(missionDetailsService.findMissionDetails(name));
    }

//    @DeleteMapping("/missiondetails")
//    public ResponseEntity<?> updateMissionDetails(@RequestBody Map<String, Object> missionList){
//        List<MissionDetails> missionDetailsList = new ArrayList<>();
//        missionDetailsList = (List<MissionDetails>) missionList.get("missionList");
//
//        int missionId = 0;
//        missionId = (int) missionList.get("missionId");
//        log.info("missionId={}",missionId);
//        missionDetailsService.deleteMissionDetails(missionId);
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(null);
//    }


    @GetMapping("/findAllMission")
    public ResponseEntity findAllMission() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(missionDetailsService.findAllMisson());
    }

    @DeleteMapping("/mission")
    public ResponseEntity<?> deleteMission(Mission mission){


        return ResponseEntity.status(HttpStatus.OK)
                .body(missionService.deleteMission(mission));

    }


    @GetMapping("/mission")
    public ResponseEntity<?> findMission(String name,Long id) {

        if(name!=null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(missionService.missionResponseList(name));
        }else if(id!=null){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(missionService.missionResponseList(id));
        }

        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body("error");

    }





}



