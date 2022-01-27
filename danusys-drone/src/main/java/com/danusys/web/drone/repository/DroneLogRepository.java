package com.danusys.web.drone.repository;

import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneLog;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DroneLogRepository extends CrudRepository<DroneLog,Long> {



    DroneLog findById(String missionName);


}
