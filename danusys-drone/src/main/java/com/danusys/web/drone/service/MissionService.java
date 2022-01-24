package com.danusys.web.drone.service;


import com.danusys.web.drone.dto.response.MissionResponse;
import com.danusys.web.drone.model.Mission;
import com.danusys.web.drone.model.MissionDetails;
import com.danusys.web.drone.repository.MissionDetailsRepository;
import com.danusys.web.drone.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionService {

    private final MissionRepository missionRepository;
    private final MissionDetailsRepository missionDetailsRepository;


    public List<Mission> findAllMission(){
        return (List<Mission>) missionRepository.findAll();
    }

    public Mission missionResponseList2(Long id){
        Optional<Mission> missonList = missionRepository.findById(id);
        return missonList.get();
    }

    public List<MissionResponse> missionResponseList(Long id){
        List <Mission> missonList = missionRepository.findAllById(id);
        return missonList.stream().map(MissionResponse::new).collect(Collectors.toList());
    }

    public List<MissionResponse> missionResponseList(String name){
        List <Mission> missonList = missionRepository.findAllByName(name);
        return missonList.stream().map(MissionResponse::new).collect(Collectors.toList());
    }

    @Transactional
    public String saveMission(Mission mission){
        missionRepository.save(mission);
        return "success";
    }

    @Transactional
    public Mission updateMission(Mission mission){
        Optional<Mission> optionalMission =missionRepository.findById(mission.getId());
        Mission updateMission= optionalMission.get();
        if(updateMission.getName()!=null){
            updateMission.setName(mission.getName());
        }

        return missionRepository.save(mission);
    }
    @Transactional
    public String deleteMission(Mission mission){
       Long result= missionDetailsRepository.deleteByMission(mission);
       log.info("result={}",result);
        missionRepository.deleteById(mission.getId());
        return "success";
    }

}
