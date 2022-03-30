package com.danusys.web.drone.repository;

import com.danusys.web.drone.model.DroneInMission;
import com.danusys.web.drone.model.DroneSocket;
import com.danusys.web.drone.model.Mission;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface DroneSocketRepository extends CrudRepository<DroneSocket, Integer> {

   DroneSocket findById(int id);

   List<DroneSocket> findAll();




}
