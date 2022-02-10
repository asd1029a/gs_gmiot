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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MissionDetailsService {

    private final MissionDetailsRepository missionDetailsRepository;
    private final MissionRepository missionRepository;
    private int estimatedTime = 0;
    private int timeCountNumber = 0;

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
    public String saveMission(List<MissionDetails> missionDetails, long mission_id, double totalDistance) {
        log.info("mission_id={}", mission_id);
        Optional<Mission> optionalMission = missionRepository.findById(mission_id);


        if (!optionalMission.isPresent()) {
            return "fail";
        }
        Mission mission = optionalMission.get();


        ObjectMapper mapper = new ObjectMapper();
        List<MissionDetails> missionDetailsList =
                mapper.convertValue(missionDetails, new TypeReference<List<MissionDetails>>() {
                });

        log.info("missionDetails={}", missionDetails);

        Long deleteResult = missionDetailsRepository.deleteByMission(mission);

        missionDetailsList.forEach(r -> {
            if (r.getName().equals("waypoint")) {
                estimatedTime += r.getSpeed();
                timeCountNumber++;
            }

            r.setMission(mission);
        });
        log.info("estimatedTime={},timeCountNumber={}", estimatedTime, timeCountNumber);
        mission.setTotalDistance(totalDistance);
        mission.setEstimatedTime((int) (totalDistance / estimatedTime * timeCountNumber / 60));
        missionRepository.save(mission);

        List<MissionDetails> isExist = missionDetailsRepository.findAllByMission(mission);
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

    public void deleteMissionDetails(long mission_id) {


        Optional<Mission> optionalMission = missionRepository.findById(mission_id);
        Mission mission = optionalMission.get();


    }


    public List<MissionDetailResponse> findMissionDetails(String name) {
        List<MissionDetails> missonDetails = missionDetailsRepository.findAllByName(name);


        return missonDetails.stream().map(MissionDetailResponse::new).collect(Collectors.toList());

    }

    public List<MissionDetailResponse> findMission(Map<String, Object> paramMap) {
        String name = null;
        Mission mission = null;
        Optional<Mission> optionalMission = null;
        Long id = null;
        int inputid = 0;
        if (paramMap.get("name") != null) {
            name = paramMap.get("name").toString();
            mission = missionRepository.findByName(name);
        }
        if (paramMap.get("id") != null) {
            inputid = Integer.parseInt(paramMap.get("id").toString());
            id = Long.valueOf(inputid);
            optionalMission = missionRepository.findById(id);
            if (!optionalMission.isPresent()) return null;
            mission = optionalMission.get();
        }

        if (name == null && id == 0) {
            return null;
        }


        List<MissionDetails> missionDetails = missionDetailsRepository.findAllByMission(mission);

        if (missionDetails.isEmpty()) {
            log.info("비엇음");
            return null;

        }
        return missionDetails.stream().map(MissionDetailResponse::new).collect(Collectors.toList());

    }

    public List<MissionDetails> findAllMission() {
        return (List<MissionDetails>) missionDetailsRepository.findAll();
    }

    public MissionDetails findByNameAndMission(String name, Mission mission) {
        return missionDetailsRepository.findByNameAndMission(name, mission);
    }

}
