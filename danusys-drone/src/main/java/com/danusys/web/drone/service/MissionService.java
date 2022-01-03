package com.danusys.web.drone.service;


import com.danusys.web.drone.dto.response.MissionResponse;
import com.danusys.web.drone.model.Mission;
import com.danusys.web.drone.model.MissionDetails;
import com.danusys.web.drone.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;


    public List<Mission> findAllMisson(){
        return (List<Mission>) missionRepository.findAll();
    }

    public Mission missionResponseList(Long id){
        Optional<Mission> missonList = missionRepository.findById(id);
        return missonList.get();
    }

    public List<MissionResponse> missionResponseList(String name){
        List <Mission> missonList = missionRepository.findAllByName(name);
        return missonList.stream().map(MissionResponse::new).collect(Collectors.toList());
    }

    @Transactional
    public Mission saveMission(Mission mission){

        return missionRepository.save(mission);}


}
