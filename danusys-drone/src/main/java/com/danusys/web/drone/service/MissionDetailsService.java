package com.danusys.web.drone.service;


import com.danusys.web.drone.dto.response.MissionDetailResponse;
import com.danusys.web.drone.model.Mission;
import com.danusys.web.drone.model.MissionDetails;
import com.danusys.web.drone.repository.MissionDetailsRepository;
import com.danusys.web.drone.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
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
    public MissionDetails saveMission(MissionDetails missonDetails, long mission_id) {
        Optional<Mission> mission = missionRepository.findById(mission_id);

        missonDetails.setMission(mission.get());
        return missionDetailsRepository.save(missonDetails);
    }

    /*
        saveMission

        미션 번호로 미션 조회 불가 시: return "fail"
        미션 세부 사항 이 이미 있을 경우: return "fail"

     */
    @Transactional
    public String saveMission(List<MissionDetails> missionDetails, long mission_id) {
        log.info("mission_id={}", mission_id);
        Optional<Mission> mission = missionRepository.findById(mission_id);
        log.info("mission={}", mission);
        if (!mission.isPresent()) {
            return "fail";
        }
        ObjectMapper mapper = new ObjectMapper();
        List<MissionDetails> missionDetailsList = mapper.convertValue(missionDetails, new TypeReference<List<MissionDetails>>() {
        });

        log.info("missionDetails={}", missionDetails);

        Long deleteResult=missionDetailsRepository.deleteByMission(mission.get());

        missionDetailsList.forEach(r -> {
            r.setMission(mission.get());
        });


        List<MissionDetails> isExist = missionDetailsRepository.findAllByMission(mission.get());
        log.info("isExist={}", isExist);
        if (isExist.isEmpty()) {
            missionDetailsList.forEach(r -> {
                missionDetailsRepository.save(r);
            });
            return "success";
        }

        return "fail";
    }


    @Transactional
    public String updateMissionDetails(List<MissionDetails> missionDetails, long mission_id) {


        log.info("mission_id={}", mission_id);
        Optional<Mission> mission = missionRepository.findById(mission_id);
        log.info("mission={}", mission);
        ObjectMapper mapper = new ObjectMapper();
        List<MissionDetails> missionDetailsList = mapper.convertValue(missionDetails, new TypeReference<List<MissionDetails>>() {
        });

        log.info("missionDetails={}", missionDetails);


        if (!mission.isPresent()) {
            return "fail";
        }


        missionDetailsList.forEach(r -> {
            r.setMission(mission.get());
        });

            missionDetailsList.forEach(r -> {
                missionDetailsRepository.save(r);
            });
            return "success";





    }

    public void deleteMissionDetails(long mission_id){


        Optional<Mission> optionalMission = missionRepository.findById(mission_id);
        Mission mission =optionalMission.get();



    }


    public List<MissionDetailResponse> findMissionDetails(String name) {
        List<MissionDetails> missonDetails = missionDetailsRepository.findAllByName(name);


        return missonDetails.stream().map(MissionDetailResponse::new).collect(Collectors.toList());

    }

    public List<MissionDetailResponse> findMission(String name) {
        log.info("{}",name);
        Mission mission =missionRepository.findByName(name);
        List<MissionDetails> missonDetails = missionDetailsRepository.findAllByMission(mission);

        log.info("{}",missonDetails);
        return missonDetails.stream().map(MissionDetailResponse::new).collect(Collectors.toList());

    }

    public List<MissionDetails> findAllMission() {
        return (List<MissionDetails>) missionDetailsRepository.findAll();
    }

    public MissionDetails findByNameAndMission(String name, Mission mission) {
        return missionDetailsRepository.findByNameAndMission(name, mission);
    }

}
