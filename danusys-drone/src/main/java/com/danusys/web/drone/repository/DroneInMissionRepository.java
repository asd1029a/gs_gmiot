package com.danusys.web.drone.repository;

import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneInMission;
import com.danusys.web.drone.model.Mission;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface DroneInMissionRepository extends CrudRepository<DroneInMission,Long> {

    long deleteByDrone(Drone drone);
    DroneInMission findByDrone(Drone drone);

    @Modifying
    @Query(value="delete from drone_in_mission  where mission_seq=:case_1", nativeQuery = true)
            void deleteDroneInMission(@Param("case_1") long mission_seq);
}