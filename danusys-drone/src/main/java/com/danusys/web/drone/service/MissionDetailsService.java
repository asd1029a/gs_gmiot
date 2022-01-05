package com.danusys.web.drone.service;


import com.danusys.web.drone.dto.response.MissionDetailResponse;
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
@Transactional
@Slf4j
public class MissionDetailsService {

    private final MissionDetailsRepository missionDetailsRepository;
    private final MissionRepository missionRepository;

    @Transactional
    public MissionDetails saveMisson(MissionDetails missonDetails,long mission_id){
        Optional<Mission> mission=missionRepository.findById(mission_id);

        missonDetails.setMission(mission.get());
        return missionDetailsRepository.save(missonDetails);}


    public List<MissionDetailResponse> findMissionDetails(String name) {
        List<MissionDetails> missonDetails= missionDetailsRepository.findAllByName(name);



            return missonDetails.stream().map(MissionDetailResponse::new).collect(Collectors.toList());

    }

    public List<MissionDetails> findAllMisson(){return (List<MissionDetails>) missionDetailsRepository.findAll();}

    public MissionDetails findByNameAndMission(String name,Mission mission){
            return missionDetailsRepository.findByNameAndMission(name,mission);
    }

}
