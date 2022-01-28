package com.danusys.web.drone.repository;


import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneDetails;
import com.danusys.web.drone.model.DroneLogDetails;
import org.springframework.data.repository.CrudRepository;

public interface DroneLogDetailsRepository extends CrudRepository<DroneLogDetails,Long> {
        


}
