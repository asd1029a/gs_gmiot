package com.danusys.web.drone.repository;

import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.Mission;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DroneRepository extends CrudRepository<Drone,Long> {



    void deleteById(Long id);


    List<Drone> findAllById(Long id);

    List<Drone> findAllByDroneDeviceNameLike(String droneDeviceName, Sort sort);

    Drone findByDroneDeviceName(String droneDeviceName);

    List<Drone> findAllByIdNot(long id);
}
