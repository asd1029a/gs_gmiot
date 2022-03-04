package com.danusys.web.drone.repository;

import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneInMission;
import com.danusys.web.drone.model.Mission;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;

public interface DroneInMissionRepository extends CrudRepository<DroneInMission,Long> {

    long deleteByDrone(Drone drone);
    DroneInMission findByDrone(Drone drone);
}