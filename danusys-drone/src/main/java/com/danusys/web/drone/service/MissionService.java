package com.danusys.web.drone.service;


import com.danusys.web.drone.dto.response.MissionDetailResponse;
import com.danusys.web.drone.dto.response.MissionResponse;
import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneDetails;
import com.danusys.web.drone.model.DroneInMission;
import com.danusys.web.drone.model.Mission;
import com.danusys.web.drone.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionService {

    private final MissionRepository missionRepository;
    private final MissionDetailsRepository missionDetailsRepository;
    private final DroneDetailsRepository droneDetailsRepository;
    private final DroneRepository droneRepository;
    private final DroneInMissionRepository droneInMissionRepository;
    private String returnType;
    private List<Mission> missionList = null;

    public List<Mission> findAllMission() {
        return (List<Mission>) missionRepository.findAll();
    }

    public Mission missionResponseList2(Long id) {
        Optional<Mission> missionList = missionRepository.findById(id);
        if (!missionList.isPresent())
            return null;
        else
            return missionList.get();
    }

    public MissionResponse missionResponse(Long id) {

        Optional<Mission> optionalMission = missionRepository.findById(id);
        if (!optionalMission.isPresent()) {
            return null;
        }
        Mission mission = optionalMission.get();

//        MissionResponse missionResponse=new MissionResponse(mission.getId(),mission.getName(),
//                mission.getMissonDetails().stream().map(MissionDetailResponse::new).collect(Collectors.toList()));

        MissionResponse missionResponse = new MissionResponse(mission.getId(), mission.getName(), mission.getUserId(),
                mission.getUpdateDt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")),
                mission.getDroneId(),
                mission.getTotalDistance(), mission.getEstimatedTime(),mission.getMissionDetails().stream().map(MissionDetailResponse::new).collect(Collectors.toList()));

        return missionResponse;
    }


    /*
    
        missionList 조회
     
     */
    public List<?> missionResponseList(Map<String, Object> paramMap) {

        List<Mission> missionList = null;
        Sort sort = sortByupdateDt();
        Long id = null;



        String name = null;
        if (paramMap.get("missionName") != null) {
            name = paramMap.get("missionName").toString();
        }
        if (paramMap.get("name") != null) {
            name = paramMap.get("name").toString();
        }
        if (paramMap.get("droneId") != null && !paramMap.get("droneId").equals("")) {

            id = Long.parseLong(paramMap.get("droneId").toString());
        }



        if (name != null) {
            if (id == null) {

                missionList = missionRepository.findAllByNameLike("%" + name + "%", sort);
            } else {
                if (id == 0l) {
                    Drone drone = new Drone();
//                    drone.setId(id);
//                    DroneInMission droneInMission=new DroneInMission();
//                    droneInMission.setDrone(drone);
                    //  missionList = missionRepository.findAllByNameLikeAndDrone("%" + name + "%", drone, sort);
                    missionList = missionRepository.findAllByNameLikeAndDroneId("%" + name + "%", id.intValue(), sort);
                } else {
                    Drone drone = new Drone();
//                    drone.setId(0l);
//                    DroneInMission droneInMission=new DroneInMission();
//                    droneInMission.setDrone(drone);
                    //missionList = missionRepository.findAllByNameLikeAndDroneNot("%" + name + "%", drone, sort);
                    missionList = missionRepository.findAllByNameLikeAndDroneIdNot("%" + name + "%", 0, sort);
                }
            }
        }
        if (paramMap.get("adminUserId") != null) {
            String adminUserId = paramMap.get("adminUserId").toString();
            if (id == null) {
                missionList = missionRepository.findAllByUserIdLike("%" + adminUserId + "%", sort);
            } else {

                if (id == 0l) {
                    Drone drone = new Drone();
                    drone.setId(id);
                    DroneInMission droneInMission = new DroneInMission();
                    droneInMission.setDrone(drone);
                    //      missionList = missionRepository.findAllByUserIdLikeAndDrone("%" + adminUserId + "%", drone, sort);
                    missionList = missionRepository.findAllByUserIdLikeAndDroneInMission("%" + adminUserId + "%", droneInMission, sort);

                } else {
                    Drone drone = new Drone();
                    drone.setId(0l);
                    DroneInMission droneInMission = new DroneInMission();
                    droneInMission.setDrone(drone);
//                    missionList = missionRepository.findAllByUserIdLikeAndDroneNot("%" + adminUserId + "%", drone, sort);
                    missionList = missionRepository.findAllByUserIdLikeAndDroneInMissionNot("%" + adminUserId + "%", droneInMission, sort);
                }
            }
        }


        if (missionList == null) {
            return null;
        }


//        missionList.forEach(r -> {
//            r.getMissonDetails().forEach(rr -> {
//
//                log.info("rr.getName={}", rr.getName());
//                if (rr.getName().equals("takeoff")) {
//                    this.returnType = "takeoff";
//                }
//            });
//
//        });

        return missionList.stream().

                map(MissionResponse::new).

                collect(Collectors.toList());
    }

    private Sort sortByupdateDt() {
        return Sort.by(Sort.Direction.DESC, "updateDt");
    }


    public Long saveMission(Mission mission) {
        Mission findMission = missionRepository.findByName(mission.getName());
        Drone drone = new Drone();
        //drone.setId(0l);
        //mission.setDrone(drone);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        mission.setUpdateDt(timestamp);
        if (findMission == null) {
            missionRepository.save(mission);
            return mission.getId();
        }
        return 0l;

    }

    @Transactional
    public int updateMission(Mission mission) {

        Optional<Mission> optionalMission = missionRepository.findById(mission.getId());
        if (!optionalMission.isPresent())
            return 0;
        Mission updateMission = optionalMission.get();
        if (updateMission.getName() != null) {
            updateMission.setName(mission.getName());
            updateMission.setUserId(mission.getUserId());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            updateMission.setUpdateDt(timestamp);
        }

        return updateMission.getId().intValue();
    }

    @Transactional
    public String deleteMission(Mission mission) {
      //  log.info("{}", mission.getId());
        Optional<Mission> optionalMission = missionRepository.findById(mission.getId());

        if (!optionalMission.isPresent()) {
            return "fail";
        }

        Long result = missionDetailsRepository.deleteByMission(mission);
       // log.info("result={}", result);
        droneInMissionRepository.deleteDroneInMission(mission.getId());
        missionRepository.deleteById(mission.getId());

        //droneInMissionRepository.deleteByMission(mission);
        return "success";
    }

}
