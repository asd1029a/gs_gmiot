package com.danusys.web.drone.repository;

import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneInMission;
import com.danusys.web.drone.model.Mission;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;


public interface MissionRepository extends CrudRepository<Mission, Long> {

    // List<Mission> findAllByNameLikeAndDrone(String name, Drone drone, Sort sort);

    //List<Mission> findAllByNameLikeAndDroneNot(String name, Drone drone, Sort sort);


    List<Mission> findAllByNameLikeAndDroneId(String name, int droneId, Sort sort);

    List<Mission> findAllByNameLikeAndDroneIdNot(String name, int droneId, Sort sort);

    List<Mission> findAllByNameLike(String name, Sort sort);

    List<Mission> findAllById(Long id);

    Mission findByName(String name);

    //  List<Mission> findAllByUserIdLikeAndDrone(String adminUserId, Drone drone, Sort sort);

    //  List<Mission> findAllByUserIdLikeAndDroneNot(String adminUserId, Drone drone, Sort sort);


    List<Mission> findAllByUserIdLikeAndDroneInMission(String adminUserId, DroneInMission drone, Sort sort);

    List<Mission> findAllByUserIdLikeAndDroneInMissionNot(String adminUserId, DroneInMission drone, Sort sort);


    List<Mission> findAllByUserIdLike(String adminUserId, Sort sort);

//    List<Mission> findAllByDrone(Drone drone);

//    List<Mission> findByDroneNot(Drone drone);

    List<Mission> findAll(Sort sort);



}
