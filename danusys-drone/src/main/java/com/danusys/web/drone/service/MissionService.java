package com.danusys.web.drone.service;


import com.danusys.web.drone.dto.response.MissionDetailResponse;
import com.danusys.web.drone.dto.response.MissionResponse;
import com.danusys.web.drone.model.Mission;
import com.danusys.web.drone.repository.MissionDetailsRepository;
import com.danusys.web.drone.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private String returnType;
    private List<Mission> missionList = null;

    public List<Mission> findAllMission() {
        return (List<Mission>) missionRepository.findAll();
    }

    public Mission missionResponseList2(Long id) {
        Optional<Mission> missonList = missionRepository.findById(id);
        return missonList.get();
    }

    public List<?> missionResponseList(Long id) {
        missionList = missionRepository.findAllById(id);

        List<MissionResponse> changeMissionList = missionList.stream().map(MissionResponse::new).collect(Collectors.toList());


        return missionList.stream().map(MissionResponse::new).collect(Collectors.toList());
    }

    public List<?> missionResponseList(String name) {
        List<Mission> missionList = missionRepository.findAllByName(name);
        AtomicReference<String> returnType = null;
        missionList.forEach(r -> {
            r.getMissonDetails().forEach(rr -> {

                log.info("rr.getName={}", rr.getName());
                if (rr.getName().equals("takeoff")) {
                    this.returnType = "takeoff";
                }
            });

        });

        return missionList.stream().map(MissionResponse::new).collect(Collectors.toList());
    }


    public Long saveMission(Mission mission) {
        Mission findMission=missionRepository.findByName(mission.getName());

        if(findMission==null){
            missionRepository.save(mission);
            return mission.getId();
        }
        return 0l;

    }

    @Transactional
    public Mission updateMission(Mission mission) {
        Optional<Mission> optionalMission = missionRepository.findById(mission.getId());
        Mission updateMission = optionalMission.get();
        if (updateMission.getName() != null) {
            updateMission.setName(mission.getName());
        }

        return missionRepository.save(mission);
    }

    @Transactional
    public String deleteMission(Mission mission) {

        Optional<Mission> optionalMission=missionRepository.findById(mission.getId());

        if (!optionalMission.isPresent()) {
            return "fail";
        }

        Long result = missionDetailsRepository.deleteByMission(mission);
        log.info("result={}", result);
        missionRepository.deleteById(mission.getId());
        return "success";
    }

}
