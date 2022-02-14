package com.danusys.web.drone.repository;


import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneDetails;
import org.springframework.data.repository.CrudRepository;

public interface DroneDetailsRepository extends CrudRepository<DroneDetails,Long> {
        


    DroneDetails findByDrone(Drone drone);

    void deleteByDrone(Drone drone);
}
