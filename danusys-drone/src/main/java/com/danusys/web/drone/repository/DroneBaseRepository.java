package com.danusys.web.drone.repository;


import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneBase;
import com.danusys.web.drone.model.DroneDetails;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DroneBaseRepository extends CrudRepository<DroneBase,Long> {
        

    List<DroneBase> findAll();

}
