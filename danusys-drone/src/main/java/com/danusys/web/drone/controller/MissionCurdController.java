package com.danusys.web.drone.controller;



import com.danusys.web.drone.dto.response.DroneMissionDetailsResponse;
import com.danusys.web.drone.dto.response.MissionDetailsDto;
import com.danusys.web.drone.dto.response.MissionDto;
import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneDetails;
import com.danusys.web.drone.model.Mission;
import com.danusys.web.drone.model.MissionDetails;
import com.danusys.web.drone.service.DroneService;
import com.danusys.web.drone.service.MissionDetailsService;
import com.danusys.web.drone.service.MissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/drone/api")
@Slf4j
@RequiredArgsConstructor
public class MissionCurdController {


    private final MissionService missionService;
    private final MissionDetailsService missionDetailsService;
    private final DroneService droneSerivce;
    private List<MissionDetails> missionDetailsList = null;


    @PutMapping("/missiondetails")
    public ResponseEntity<?> saveMissionDetails(@RequestBody Map<String, Object> missionList) {
     //   log.info("missionId={}", missionList);
        List<MissionDetails> missionDetailsList = new ArrayList<>();
        missionDetailsList = (List<MissionDetails>) missionList.get("missionList");

        int missionId = 0;
        missionId = Integer.parseInt(missionList.get("missionId").toString());

        double totalDistance = 0;
        totalDistance = Double.parseDouble(missionList.get("totalDistance").toString());


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(missionDetailsService.saveMission(missionDetailsList, missionId, totalDistance));

    }


    @PutMapping("/mission")
    public ResponseEntity<?> saveMission(@RequestBody Mission mission) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(missionService.saveMission(mission));

    }

    @PatchMapping("/mission")
    public ResponseEntity<?> updateMission(@RequestBody Mission mission) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(missionService.updateMission(mission));

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

    @DeleteMapping("/mission")
    public ResponseEntity<?> deleteMission(@RequestBody Mission mission) {


        return ResponseEntity.status(HttpStatus.OK)
                .body(missionService.deleteMission(mission));

    }

    @GetMapping("/mission/{id}")
    public ResponseEntity<?> findMission(@PathVariable Long id) {
        if (id != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(missionService.missionResponse(id));
        }
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body("fail");
    }

    @PostMapping("/mission")
    public ResponseEntity<?> findMissionList(@RequestBody Map<String, Object> paramMap) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(missionService.missionResponseList(paramMap));

    }

    @PostMapping("/missiondetails")
    public ResponseEntity<?> findMissionDetailsList(@RequestBody Map<String, Object> paramMap) {


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(missionDetailsService.findMission(paramMap));

    }

    @GetMapping("/missioncount")
    public ResponseEntity<?> getMissionCount() {
        List<Mission> missionList = missionService.findAllMission();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(missionList.size());
    }


    @PostMapping("/dronemissiondetails")
    @Transactional
    public ResponseEntity<?> getListDroneMissionDetails() {
        List<Drone> droneList = droneSerivce.findAllDrone();


//                droneList.forEach(r->{
//                    missionDetailsList=r.getMission()
//                            .getMissionDetails().stream().sorted(Comparator.comparing((MissionDetails d) -> d.getIndex())).collect(Collectors.toList());
//                        });
//        List<MissionDetailsDto> missionDetailsDtoList=missionDetailsList.stream().map(MissionDetailsDto::new).collect(Collectors.toList());
//        DroneMissionDetailsResponse droneMissionDetailsResponse = new DroneMissionDetailsResponse();
//        MissionDto missionDto = new MissionDto();
//
//        missionDto.setMissionDetailsDto(missionDetailsDtoList);
//        droneMissionDetailsResponse.setMissionDto(missionDto);
        List<DroneMissionDetailsResponse> droneMissionDetailsResponses = droneList.stream().map(DroneMissionDetailsResponse::new).collect(Collectors.toList());


        droneMissionDetailsResponses.forEach(r -> {
                if(r.getMission()!=null)
            r.getMission().setMissionDetails(r.getMission().getMissionDetails().stream().sorted(Comparator.comparing((MissionDetailsDto d) -> d.getIndex())).collect(Collectors.toList())
            );
        });


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(droneMissionDetailsResponses);
//                .body(droneMissionDetailsResponse);
    }

}



