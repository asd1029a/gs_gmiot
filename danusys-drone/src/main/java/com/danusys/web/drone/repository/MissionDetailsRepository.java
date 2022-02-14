package com.danusys.web.drone.repository;

import com.danusys.web.drone.model.Mission;
import com.danusys.web.drone.model.MissionDetails;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MissionDetailsRepository extends CrudRepository<MissionDetails,Long> {

    List<MissionDetails> findAllByName(String name);

    MissionDetails findByNameAndMission(String name, Mission mission);

    List<MissionDetails> findAllByMission(Mission mission, Sort sort);

    List<MissionDetails> findAllByMission(Mission mission);

    MissionDetails findByIndex(int index);

    MissionDetails findByMissionAndIndex(Mission mission,int index);

    Long deleteByMission(Mission mission);

}
